begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.jvm
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|HotThreads
specifier|public
class|class
name|HotThreads
block|{
DECL|field|mutex
specifier|private
specifier|static
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|busiestThreads
specifier|private
name|int
name|busiestThreads
init|=
literal|3
decl_stmt|;
DECL|field|interval
specifier|private
name|TimeValue
name|interval
init|=
operator|new
name|TimeValue
argument_list|(
literal|500
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
DECL|field|threadElementsSnapshotDelay
specifier|private
name|TimeValue
name|threadElementsSnapshotDelay
init|=
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|threadElementsSnapshotCount
specifier|private
name|int
name|threadElementsSnapshotCount
init|=
literal|10
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
init|=
literal|"cpu"
decl_stmt|;
DECL|method|interval
specifier|public
name|HotThreads
name|interval
parameter_list|(
name|TimeValue
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|busiestThreads
specifier|public
name|HotThreads
name|busiestThreads
parameter_list|(
name|int
name|busiestThreads
parameter_list|)
block|{
name|this
operator|.
name|busiestThreads
operator|=
name|busiestThreads
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|threadElementsSnapshotDelay
specifier|public
name|HotThreads
name|threadElementsSnapshotDelay
parameter_list|(
name|TimeValue
name|threadElementsSnapshotDelay
parameter_list|)
block|{
name|this
operator|.
name|threadElementsSnapshotDelay
operator|=
name|threadElementsSnapshotDelay
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|threadElementsSnapshotCount
specifier|public
name|HotThreads
name|threadElementsSnapshotCount
parameter_list|(
name|int
name|threadElementsSnapshotCount
parameter_list|)
block|{
name|this
operator|.
name|threadElementsSnapshotCount
operator|=
name|threadElementsSnapshotCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|type
specifier|public
name|HotThreads
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"cpu"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"wait"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"block"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"type not supported ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
DECL|method|detect
specifier|public
name|String
name|detect
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|innerDetect
argument_list|()
return|;
block|}
block|}
DECL|method|innerDetect
specifier|private
name|String
name|innerDetect
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
name|boolean
name|enabledCpu
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|threadBean
operator|.
name|isThreadCpuTimeSupported
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|threadBean
operator|.
name|isThreadCpuTimeEnabled
argument_list|()
condition|)
block|{
name|enabledCpu
operator|=
literal|true
expr_stmt|;
name|threadBean
operator|.
name|setThreadCpuTimeEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"MBean doesn't support thread CPU Time"
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|Long
argument_list|,
name|MyThreadInfo
argument_list|>
name|threadInfos
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|MyThreadInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|threadId
range|:
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
control|)
block|{
comment|// ignore our own thread...
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
name|threadId
condition|)
block|{
continue|continue;
block|}
name|long
name|cpu
init|=
name|threadBean
operator|.
name|getThreadCpuTime
argument_list|(
name|threadId
argument_list|)
decl_stmt|;
name|ThreadInfo
name|info
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|threadId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|threadInfos
operator|.
name|put
argument_list|(
name|threadId
argument_list|,
operator|new
name|MyThreadInfo
argument_list|(
name|cpu
argument_list|,
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|interval
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|threadId
range|:
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
control|)
block|{
comment|// ignore our own thread...
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
name|threadId
condition|)
block|{
continue|continue;
block|}
name|long
name|cpu
init|=
name|threadBean
operator|.
name|getThreadCpuTime
argument_list|(
name|threadId
argument_list|)
decl_stmt|;
name|ThreadInfo
name|info
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|threadId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|MyThreadInfo
name|data
init|=
name|threadInfos
operator|.
name|get
argument_list|(
name|threadId
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|data
operator|.
name|setDelta
argument_list|(
name|cpu
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sort by delta CPU time on thread.
name|List
argument_list|<
name|MyThreadInfo
argument_list|>
name|hotties
init|=
operator|new
name|ArrayList
argument_list|<
name|MyThreadInfo
argument_list|>
argument_list|(
name|threadInfos
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
comment|// skip that for now
name|Collections
operator|.
name|sort
argument_list|(
name|hotties
argument_list|,
operator|new
name|Comparator
argument_list|<
name|MyThreadInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|MyThreadInfo
name|o1
parameter_list|,
name|MyThreadInfo
name|o2
parameter_list|)
block|{
if|if
condition|(
literal|"cpu"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|cpuTime
operator|-
name|o1
operator|.
name|cpuTime
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"wait"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|waitedTime
operator|-
name|o1
operator|.
name|waitedTime
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"block"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|blockedTime
operator|-
name|o1
operator|.
name|blockedTime
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//        for(MyThreadInfo inf : hotties) {
comment|//            if(inf.deltaDone) {
comment|//                System.out.format("%5.2f %d/%d %d/%d %s%n",
comment|//                    inf.cpuTime/1E7,
comment|//                    inf.blockedCount,
comment|//                    inf.blockedTime,
comment|//                    inf.waitedCount,
comment|//                    inf.waitedtime,
comment|//                    inf.info.getThreadName()
comment|//                    );
comment|//            }
comment|//        }
comment|// analyse N stack traces for M busiest threads
name|long
index|[]
name|ids
init|=
operator|new
name|long
index|[
name|busiestThreads
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
name|busiestThreads
condition|;
name|i
operator|++
control|)
block|{
name|MyThreadInfo
name|info
init|=
name|hotties
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ids
index|[
name|i
index|]
operator|=
name|info
operator|.
name|info
operator|.
name|getThreadId
argument_list|()
expr_stmt|;
block|}
name|ThreadInfo
index|[]
index|[]
name|allInfos
init|=
operator|new
name|ThreadInfo
index|[
name|threadElementsSnapshotCount
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|threadElementsSnapshotCount
condition|;
name|j
operator|++
control|)
block|{
name|allInfos
index|[
name|j
index|]
operator|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|ids
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|threadElementsSnapshotDelay
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|busiestThreads
condition|;
name|t
operator|++
control|)
block|{
name|double
name|value
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
literal|"cpu"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|value
operator|=
name|hotties
operator|.
name|get
argument_list|(
name|t
argument_list|)
operator|.
name|cpuTime
operator|/
literal|1E7
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"wait"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|value
operator|=
name|hotties
operator|.
name|get
argument_list|(
name|t
argument_list|)
operator|.
name|waitedTime
operator|/
literal|1E7
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"block"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|value
operator|=
name|hotties
operator|.
name|get
argument_list|(
name|t
argument_list|)
operator|.
name|blockedTime
operator|/
literal|1E7
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%n%4.1f%% %s usage by thread '%s'%n"
argument_list|,
name|value
argument_list|,
name|type
argument_list|,
name|allInfos
index|[
literal|0
index|]
index|[
name|t
index|]
operator|.
name|getThreadName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// for each snapshot (2nd array index) find later snapshot for same thread with max number of
comment|// identical StackTraceElements (starting from end of each)
name|boolean
index|[]
name|done
init|=
operator|new
name|boolean
index|[
name|threadElementsSnapshotCount
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
name|threadElementsSnapshotCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|done
index|[
name|i
index|]
condition|)
continue|continue;
name|int
name|maxSim
init|=
literal|1
decl_stmt|;
name|boolean
index|[]
name|similars
init|=
operator|new
name|boolean
index|[
name|threadElementsSnapshotCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|threadElementsSnapshotCount
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|done
index|[
name|j
index|]
condition|)
continue|continue;
name|int
name|similarity
init|=
name|similarity
argument_list|(
name|allInfos
index|[
name|i
index|]
index|[
name|t
index|]
argument_list|,
name|allInfos
index|[
name|j
index|]
index|[
name|t
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|similarity
operator|>
name|maxSim
condition|)
block|{
name|maxSim
operator|=
name|similarity
expr_stmt|;
name|similars
operator|=
operator|new
name|boolean
index|[
name|threadElementsSnapshotCount
index|]
expr_stmt|;
block|}
if|if
condition|(
name|similarity
operator|==
name|maxSim
condition|)
name|similars
index|[
name|j
index|]
operator|=
literal|true
expr_stmt|;
block|}
comment|// print out trace maxSim levels of i, and mark similar ones as done
name|int
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|threadElementsSnapshotCount
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|similars
index|[
name|j
index|]
condition|)
block|{
name|done
index|[
name|j
index|]
operator|=
literal|true
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
name|StackTraceElement
index|[]
name|show
init|=
name|allInfos
index|[
name|i
index|]
index|[
name|t
index|]
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"  unique snapshot%n"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|show
operator|.
name|length
condition|;
name|l
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"    %s%n"
argument_list|,
name|show
index|[
name|l
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"  %d/%d snapshots sharing following %d elements%n"
argument_list|,
name|count
argument_list|,
name|threadElementsSnapshotCount
argument_list|,
name|maxSim
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|l
init|=
name|show
operator|.
name|length
operator|-
name|maxSim
init|;
name|l
operator|<
name|show
operator|.
name|length
condition|;
name|l
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"    %s%n"
argument_list|,
name|show
index|[
name|l
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|enabledCpu
condition|)
block|{
name|threadBean
operator|.
name|setThreadCpuTimeEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|similarity
specifier|private
name|int
name|similarity
parameter_list|(
name|ThreadInfo
name|threadInfo
parameter_list|,
name|ThreadInfo
name|threadInfo0
parameter_list|)
block|{
name|StackTraceElement
index|[]
name|s1
init|=
name|threadInfo
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|StackTraceElement
index|[]
name|s2
init|=
name|threadInfo0
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|s1
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|j
init|=
name|s2
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|rslt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|>=
literal|0
operator|&&
name|j
operator|>=
literal|0
operator|&&
name|s1
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|s2
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|rslt
operator|++
expr_stmt|;
name|i
operator|--
expr_stmt|;
name|j
operator|--
expr_stmt|;
block|}
return|return
name|rslt
return|;
block|}
DECL|class|MyThreadInfo
class|class
name|MyThreadInfo
block|{
DECL|field|cpuTime
name|long
name|cpuTime
decl_stmt|;
DECL|field|blockedCount
name|long
name|blockedCount
decl_stmt|;
DECL|field|blockedTime
name|long
name|blockedTime
decl_stmt|;
DECL|field|waitedCount
name|long
name|waitedCount
decl_stmt|;
DECL|field|waitedTime
name|long
name|waitedTime
decl_stmt|;
DECL|field|deltaDone
name|boolean
name|deltaDone
decl_stmt|;
DECL|field|info
name|ThreadInfo
name|info
decl_stmt|;
DECL|method|MyThreadInfo
name|MyThreadInfo
parameter_list|(
name|long
name|cpuTime
parameter_list|,
name|ThreadInfo
name|info
parameter_list|)
block|{
name|blockedCount
operator|=
name|info
operator|.
name|getBlockedCount
argument_list|()
expr_stmt|;
name|blockedTime
operator|=
name|info
operator|.
name|getBlockedTime
argument_list|()
expr_stmt|;
name|waitedCount
operator|=
name|info
operator|.
name|getWaitedCount
argument_list|()
expr_stmt|;
name|waitedTime
operator|=
name|info
operator|.
name|getWaitedTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|cpuTime
operator|=
name|cpuTime
expr_stmt|;
block|}
DECL|method|setDelta
name|void
name|setDelta
parameter_list|(
name|long
name|cpuTime
parameter_list|,
name|ThreadInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|deltaDone
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"setDelta already called once"
argument_list|)
throw|;
name|blockedCount
operator|=
name|info
operator|.
name|getBlockedCount
argument_list|()
operator|-
name|blockedCount
expr_stmt|;
name|blockedTime
operator|=
name|info
operator|.
name|getBlockedTime
argument_list|()
operator|-
name|blockedTime
expr_stmt|;
name|waitedCount
operator|=
name|info
operator|.
name|getWaitedCount
argument_list|()
operator|-
name|waitedCount
expr_stmt|;
name|waitedTime
operator|=
name|info
operator|.
name|getWaitedTime
argument_list|()
operator|-
name|waitedTime
expr_stmt|;
name|this
operator|.
name|cpuTime
operator|=
name|cpuTime
operator|-
name|this
operator|.
name|cpuTime
expr_stmt|;
name|deltaDone
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

