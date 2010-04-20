begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.timer
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|timer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|MapBackedSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|ReusableIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|ThreadRenamingRunnable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentIdentityHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * A {@link Timer} optimized for approximated I/O timeout scheduling.  *  *<h3>Tick Duration</h3>  *  * As described with 'approximated', this timer does not execute the scheduled  * {@link TimerTask} on time.  {@link HashedWheelTimer}, on every tick, will  * check if there are any {@link TimerTask}s behind the schedule and execute  * them.  *<p>  * You can increase or decrease the accuracy of the execution timing by  * specifying smaller or larger tick duration in the constructor.  In most  * network applications, I/O timeout does not need to be accurate.  Therefore,  * the default tick duration is 100 milliseconds and you will not need to try  * different configurations in most cases.  *  *<h3>Ticks per Wheel (Wheel Size)</h3>  *  * {@link HashedWheelTimer} maintains a data structure called 'wheel'.  * To put simply, a wheel is a hash table of {@link TimerTask}s whose hash  * function is 'dead line of the task'.  The default number of ticks per wheel  * (i.e. the size of the wheel) is 512.  You could specify a larger value  * if you are going to schedule a lot of timeouts.  *  *<h3>Implementation Details</h3>  *  * {@link HashedWheelTimer} is based on  *<a href="http://cseweb.ucsd.edu/users/varghese/>George Varghese</a> and  * Tony Lauck's paper,  *<a href="http://www-cse.ucsd.edu/users/varghese/PAPERS/twheel.ps.Z">'Hashed  * and Hierarchical Timing Wheels: data structures to efficiently implement a  * timer facility'</a>.  More comprehensive slides are located  *<a href="http://www.cse.wustl.edu/~cdgill/courses/cs6874/TimingWheels.ppt">here</a>.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HashedWheelTimer
specifier|public
class|class
name|HashedWheelTimer
implements|implements
name|Timer
block|{
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|id
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|id
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|// I'd say 64 active timer threads are obvious misuse.
DECL|field|MISUSE_WARNING_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|MISUSE_WARNING_THRESHOLD
init|=
literal|64
decl_stmt|;
DECL|field|activeInstances
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|activeInstances
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|loggedMisuseWarning
specifier|private
specifier|static
specifier|final
name|AtomicBoolean
name|loggedMisuseWarning
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|worker
specifier|private
specifier|final
name|Worker
name|worker
init|=
operator|new
name|Worker
argument_list|()
decl_stmt|;
DECL|field|workerThread
specifier|final
name|Thread
name|workerThread
decl_stmt|;
DECL|field|shutdown
specifier|final
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|roundDuration
specifier|private
specifier|final
name|long
name|roundDuration
decl_stmt|;
DECL|field|tickDuration
specifier|final
name|long
name|tickDuration
decl_stmt|;
DECL|field|wheel
specifier|final
name|Set
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|wheel
decl_stmt|;
DECL|field|iterators
specifier|final
name|ReusableIterator
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|iterators
decl_stmt|;
DECL|field|mask
specifier|final
name|int
name|mask
decl_stmt|;
DECL|field|lock
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|wheelCursor
specifier|volatile
name|int
name|wheelCursor
decl_stmt|;
comment|/**      * Creates a new timer with the default number of ticks per wheel.      *      * @param threadFactory a {@link ThreadFactory} that creates a      *                      background {@link Thread} which is dedicated to      *                      {@link TimerTask} execution.      * @param tickDuration  the duration between tick      * @param unit          the time unit of the {@code tickDuration}      */
DECL|method|HashedWheelTimer
specifier|public
name|HashedWheelTimer
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|long
name|tickDuration
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|this
argument_list|(
name|logger
argument_list|,
name|threadFactory
argument_list|,
name|tickDuration
argument_list|,
name|unit
argument_list|,
literal|512
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new timer.      *      * @param threadFactory a {@link ThreadFactory} that creates a      *                      background {@link Thread} which is dedicated to      *                      {@link TimerTask} execution.      * @param tickDuration  the duration between tick      * @param unit          the time unit of the {@code tickDuration}      * @param ticksPerWheel the size of the wheel      */
DECL|method|HashedWheelTimer
specifier|public
name|HashedWheelTimer
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|long
name|tickDuration
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|int
name|ticksPerWheel
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
if|if
condition|(
name|threadFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"threadFactory"
argument_list|)
throw|;
block|}
if|if
condition|(
name|unit
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"unit"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tickDuration
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"tickDuration must be greater than 0: "
operator|+
name|tickDuration
argument_list|)
throw|;
block|}
if|if
condition|(
name|ticksPerWheel
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ticksPerWheel must be greater than 0: "
operator|+
name|ticksPerWheel
argument_list|)
throw|;
block|}
comment|// Normalize ticksPerWheel to power of two and initialize the wheel.
name|wheel
operator|=
name|createWheel
argument_list|(
name|ticksPerWheel
argument_list|)
expr_stmt|;
name|iterators
operator|=
name|createIterators
argument_list|(
name|wheel
argument_list|)
expr_stmt|;
name|mask
operator|=
name|wheel
operator|.
name|length
operator|-
literal|1
expr_stmt|;
comment|// Convert tickDuration to milliseconds.
name|this
operator|.
name|tickDuration
operator|=
name|tickDuration
operator|=
name|unit
operator|.
name|toMillis
argument_list|(
name|tickDuration
argument_list|)
expr_stmt|;
comment|// Prevent overflow.
if|if
condition|(
name|tickDuration
operator|==
name|Long
operator|.
name|MAX_VALUE
operator|||
name|tickDuration
operator|>=
name|Long
operator|.
name|MAX_VALUE
operator|/
name|wheel
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"tickDuration is too long: "
operator|+
name|tickDuration
operator|+
literal|' '
operator|+
name|unit
argument_list|)
throw|;
block|}
name|roundDuration
operator|=
name|tickDuration
operator|*
name|wheel
operator|.
name|length
expr_stmt|;
name|workerThread
operator|=
name|threadFactory
operator|.
name|newThread
argument_list|(
operator|new
name|ThreadRenamingRunnable
argument_list|(
name|worker
argument_list|,
literal|"Hashed wheel timer #"
operator|+
name|id
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Misuse check
name|int
name|activeInstances
init|=
name|HashedWheelTimer
operator|.
name|activeInstances
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|activeInstances
operator|>=
name|MISUSE_WARNING_THRESHOLD
operator|&&
name|loggedMisuseWarning
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"There are too many active "
operator|+
name|HashedWheelTimer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" instances ("
operator|+
name|activeInstances
operator|+
literal|") - you should share the small number "
operator|+
literal|"of instances to avoid excessive resource consumption."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createWheel
specifier|private
specifier|static
name|Set
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|createWheel
parameter_list|(
name|int
name|ticksPerWheel
parameter_list|)
block|{
if|if
condition|(
name|ticksPerWheel
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ticksPerWheel must be greater than 0: "
operator|+
name|ticksPerWheel
argument_list|)
throw|;
block|}
if|if
condition|(
name|ticksPerWheel
operator|>
literal|1073741824
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ticksPerWheel may not be greater than 2^30: "
operator|+
name|ticksPerWheel
argument_list|)
throw|;
block|}
name|ticksPerWheel
operator|=
name|normalizeTicksPerWheel
argument_list|(
name|ticksPerWheel
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|wheel
init|=
operator|new
name|Set
index|[
name|ticksPerWheel
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wheel
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|wheel
index|[
name|i
index|]
operator|=
operator|new
name|MapBackedSet
argument_list|<
name|HashedWheelTimeout
argument_list|>
argument_list|(
operator|new
name|ConcurrentIdentityHashMap
argument_list|<
name|HashedWheelTimeout
argument_list|,
name|Boolean
argument_list|>
argument_list|(
literal|16
argument_list|,
literal|0.95f
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wheel
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createIterators
specifier|private
specifier|static
name|ReusableIterator
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|createIterators
parameter_list|(
name|Set
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|wheel
parameter_list|)
block|{
name|ReusableIterator
argument_list|<
name|HashedWheelTimeout
argument_list|>
index|[]
name|iterators
init|=
operator|new
name|ReusableIterator
index|[
name|wheel
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wheel
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|iterators
index|[
name|i
index|]
operator|=
operator|(
name|ReusableIterator
argument_list|<
name|HashedWheelTimeout
argument_list|>
operator|)
name|wheel
index|[
name|i
index|]
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|iterators
return|;
block|}
DECL|method|normalizeTicksPerWheel
specifier|private
specifier|static
name|int
name|normalizeTicksPerWheel
parameter_list|(
name|int
name|ticksPerWheel
parameter_list|)
block|{
name|int
name|normalizedTicksPerWheel
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|normalizedTicksPerWheel
operator|<
name|ticksPerWheel
condition|)
block|{
name|normalizedTicksPerWheel
operator|<<=
literal|1
expr_stmt|;
block|}
return|return
name|normalizedTicksPerWheel
return|;
block|}
comment|/**      * Starts the background thread explicitly.  The background thread will      * start automatically on demand even if you did not call this method.      *      * @throws IllegalStateException if this timer has been      *                               {@linkplain #stop() stopped} already      */
DECL|method|start
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot be started once stopped"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|workerThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|workerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|stop
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|Timeout
argument_list|>
name|stop
parameter_list|()
block|{
if|if
condition|(
operator|!
name|shutdown
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
name|boolean
name|interrupted
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|workerThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|workerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|workerThread
operator|.
name|join
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|interrupted
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|interrupted
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|activeInstances
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|Timeout
argument_list|>
name|unprocessedTimeouts
init|=
operator|new
name|HashSet
argument_list|<
name|Timeout
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Set
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|bucket
range|:
name|wheel
control|)
block|{
name|unprocessedTimeouts
operator|.
name|addAll
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|unprocessedTimeouts
argument_list|)
return|;
block|}
DECL|method|newTimeout
specifier|public
name|Timeout
name|newTimeout
parameter_list|(
name|TimerTask
name|task
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
specifier|final
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|task
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"task"
argument_list|)
throw|;
block|}
if|if
condition|(
name|unit
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"unit"
argument_list|)
throw|;
block|}
name|delay
operator|=
name|unit
operator|.
name|toMillis
argument_list|(
name|delay
argument_list|)
expr_stmt|;
if|if
condition|(
name|delay
operator|<
name|tickDuration
condition|)
block|{
name|delay
operator|=
name|tickDuration
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|workerThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Prepare the required parameters to create the timeout object.
name|HashedWheelTimeout
name|timeout
decl_stmt|;
specifier|final
name|long
name|lastRoundDelay
init|=
name|delay
operator|%
name|roundDuration
decl_stmt|;
specifier|final
name|long
name|lastTickDelay
init|=
name|delay
operator|%
name|tickDuration
decl_stmt|;
specifier|final
name|long
name|relativeIndex
init|=
name|lastRoundDelay
operator|/
name|tickDuration
operator|+
operator|(
name|lastTickDelay
operator|!=
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
specifier|final
name|long
name|deadline
init|=
name|currentTime
operator|+
name|delay
decl_stmt|;
specifier|final
name|long
name|remainingRounds
init|=
name|delay
operator|/
name|roundDuration
operator|-
operator|(
name|delay
operator|%
name|roundDuration
operator|==
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
comment|// Add the timeout to the wheel.
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|timeout
operator|=
operator|new
name|HashedWheelTimeout
argument_list|(
name|task
argument_list|,
name|deadline
argument_list|,
call|(
name|int
call|)
argument_list|(
name|wheelCursor
operator|+
name|relativeIndex
operator|&
name|mask
argument_list|)
argument_list|,
name|remainingRounds
argument_list|)
expr_stmt|;
name|wheel
index|[
name|timeout
operator|.
name|stopIndex
index|]
operator|.
name|add
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|timeout
return|;
block|}
DECL|class|Worker
specifier|private
specifier|final
class|class
name|Worker
implements|implements
name|Runnable
block|{
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|tick
specifier|private
name|long
name|tick
decl_stmt|;
DECL|method|Worker
name|Worker
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|List
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|expiredTimeouts
init|=
operator|new
name|ArrayList
argument_list|<
name|HashedWheelTimeout
argument_list|>
argument_list|()
decl_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|tick
operator|=
literal|1
expr_stmt|;
while|while
condition|(
operator|!
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
name|waitForNextTick
argument_list|()
expr_stmt|;
name|fetchExpiredTimeouts
argument_list|(
name|expiredTimeouts
argument_list|)
expr_stmt|;
name|notifyExpiredTimeouts
argument_list|(
name|expiredTimeouts
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fetchExpiredTimeouts
specifier|private
name|void
name|fetchExpiredTimeouts
parameter_list|(
name|List
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|expiredTimeouts
parameter_list|)
block|{
comment|// Find the expired timeouts and decrease the round counter
comment|// if necessary.  Note that we don't send the notification
comment|// immediately to make sure the listeners are called without
comment|// an exclusive lock.
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|oldBucketHead
init|=
name|wheelCursor
decl_stmt|;
name|int
name|newBucketHead
init|=
name|oldBucketHead
operator|+
literal|1
operator|&
name|mask
decl_stmt|;
name|wheelCursor
operator|=
name|newBucketHead
expr_stmt|;
name|ReusableIterator
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|i
init|=
name|iterators
index|[
name|oldBucketHead
index|]
decl_stmt|;
name|fetchExpiredTimeouts
argument_list|(
name|expiredTimeouts
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|fetchExpiredTimeouts
specifier|private
name|void
name|fetchExpiredTimeouts
parameter_list|(
name|List
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|expiredTimeouts
parameter_list|,
name|ReusableIterator
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|i
parameter_list|)
block|{
name|long
name|currentDeadline
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|tickDuration
decl_stmt|;
name|i
operator|.
name|rewind
argument_list|()
expr_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|HashedWheelTimeout
name|timeout
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeout
operator|.
name|remainingRounds
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|timeout
operator|.
name|deadline
operator|<
name|currentDeadline
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
name|expiredTimeouts
operator|.
name|add
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// A rare case where a timeout is put for the next
comment|// round: just wait for the next round.
block|}
block|}
else|else
block|{
name|timeout
operator|.
name|remainingRounds
operator|--
expr_stmt|;
block|}
block|}
block|}
DECL|method|notifyExpiredTimeouts
specifier|private
name|void
name|notifyExpiredTimeouts
parameter_list|(
name|List
argument_list|<
name|HashedWheelTimeout
argument_list|>
name|expiredTimeouts
parameter_list|)
block|{
comment|// Notify the expired timeouts.
for|for
control|(
name|int
name|i
init|=
name|expiredTimeouts
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|expiredTimeouts
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|expire
argument_list|()
expr_stmt|;
block|}
comment|// Clean up the temporary list.
name|expiredTimeouts
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|waitForNextTick
specifier|private
name|void
name|waitForNextTick
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|long
name|sleepTime
init|=
name|tickDuration
operator|*
name|tick
operator|-
operator|(
name|currentTime
operator|-
name|startTime
operator|)
decl_stmt|;
if|if
condition|(
name|sleepTime
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
block|}
comment|// Reset the tick if overflow is expected.
if|if
condition|(
name|tickDuration
operator|*
name|tick
operator|>
name|Long
operator|.
name|MAX_VALUE
operator|-
name|tickDuration
condition|)
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|tick
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// Increase the tick if overflow is not likely to happen.
name|tick
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|class|HashedWheelTimeout
specifier|private
specifier|final
class|class
name|HashedWheelTimeout
implements|implements
name|Timeout
block|{
DECL|field|task
specifier|private
specifier|final
name|TimerTask
name|task
decl_stmt|;
DECL|field|stopIndex
specifier|final
name|int
name|stopIndex
decl_stmt|;
DECL|field|deadline
specifier|final
name|long
name|deadline
decl_stmt|;
DECL|field|remainingRounds
specifier|volatile
name|long
name|remainingRounds
decl_stmt|;
DECL|field|cancelled
specifier|private
specifier|volatile
name|boolean
name|cancelled
decl_stmt|;
DECL|method|HashedWheelTimeout
name|HashedWheelTimeout
parameter_list|(
name|TimerTask
name|task
parameter_list|,
name|long
name|deadline
parameter_list|,
name|int
name|stopIndex
parameter_list|,
name|long
name|remainingRounds
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|deadline
operator|=
name|deadline
expr_stmt|;
name|this
operator|.
name|stopIndex
operator|=
name|stopIndex
expr_stmt|;
name|this
operator|.
name|remainingRounds
operator|=
name|remainingRounds
expr_stmt|;
block|}
DECL|method|getTimer
specifier|public
name|Timer
name|getTimer
parameter_list|()
block|{
return|return
name|HashedWheelTimer
operator|.
name|this
return|;
block|}
DECL|method|getTask
specifier|public
name|TimerTask
name|getTask
parameter_list|()
block|{
return|return
name|task
return|;
block|}
DECL|method|cancel
specifier|public
name|void
name|cancel
parameter_list|()
block|{
if|if
condition|(
name|isExpired
argument_list|()
condition|)
block|{
return|return;
block|}
name|cancelled
operator|=
literal|true
expr_stmt|;
comment|// Might be called more than once, but doesn't matter.
name|wheel
index|[
name|stopIndex
index|]
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|isCancelled
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|cancelled
return|;
block|}
DECL|method|isExpired
specifier|public
name|boolean
name|isExpired
parameter_list|()
block|{
return|return
name|cancelled
operator|||
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|deadline
return|;
block|}
DECL|method|expire
specifier|public
name|void
name|expire
parameter_list|()
block|{
if|if
condition|(
name|cancelled
condition|)
block|{
return|return;
block|}
try|try
block|{
name|task
operator|.
name|run
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"An exception was thrown by "
operator|+
name|TimerTask
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|remaining
init|=
name|deadline
operator|-
name|currentTime
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|192
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"deadline: "
argument_list|)
expr_stmt|;
if|if
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" ms later, "
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|<
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|-
name|remaining
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" ms ago, "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"now, "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isCancelled
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", cancelled"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

