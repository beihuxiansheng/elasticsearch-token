begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * This class manages locks. Locks can be accessed with an identifier and are  * created the first time they are acquired and removed if no thread hold the  * lock. The latter is important to assure that the list of locks does not grow  * infinitely.  *   * A Thread can acquire a lock only once.  *   * */
end_comment

begin_class
DECL|class|KeyedLock
specifier|public
class|class
name|KeyedLock
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|map
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|T
argument_list|,
name|KeyLock
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|T
argument_list|,
name|KeyLock
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|threadLocal
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|KeyLock
argument_list|>
name|threadLocal
init|=
operator|new
name|ThreadLocal
argument_list|<
name|KeyedLock
operator|.
name|KeyLock
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|acquire
specifier|public
name|void
name|acquire
parameter_list|(
name|T
name|key
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|threadLocal
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// if we are here, the thread already has the lock
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Lock already accquired in Thread"
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" for key "
operator|+
name|key
argument_list|)
throw|;
block|}
name|KeyLock
name|perNodeLock
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|perNodeLock
operator|==
literal|null
condition|)
block|{
name|KeyLock
name|newLock
init|=
operator|new
name|KeyLock
argument_list|()
decl_stmt|;
name|perNodeLock
operator|=
name|map
operator|.
name|putIfAbsent
argument_list|(
name|key
argument_list|,
name|newLock
argument_list|)
expr_stmt|;
if|if
condition|(
name|perNodeLock
operator|==
literal|null
condition|)
block|{
name|newLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|threadLocal
operator|.
name|set
argument_list|(
name|newLock
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
assert|assert
name|perNodeLock
operator|!=
literal|null
assert|;
name|int
name|i
init|=
name|perNodeLock
operator|.
name|count
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|perNodeLock
operator|.
name|count
operator|.
name|compareAndSet
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
condition|)
block|{
name|perNodeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|threadLocal
operator|.
name|set
argument_list|(
name|perNodeLock
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|T
name|key
parameter_list|)
block|{
name|KeyLock
name|lock
init|=
name|threadLocal
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Lock not accquired"
argument_list|)
throw|;
block|}
assert|assert
name|lock
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
assert|assert
name|lock
operator|==
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
assert|;
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|threadLocal
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|int
name|decrementAndGet
init|=
name|lock
operator|.
name|count
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|decrementAndGet
operator|==
literal|0
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|,
name|lock
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|KeyLock
specifier|private
specifier|final
specifier|static
class|class
name|KeyLock
extends|extends
name|ReentrantLock
block|{
DECL|field|count
specifier|private
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
block|}
DECL|method|hasLockedKeys
specifier|public
name|boolean
name|hasLockedKeys
parameter_list|()
block|{
return|return
operator|!
name|map
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

end_unit

