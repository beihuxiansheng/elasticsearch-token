begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.recycler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|recycler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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

begin_comment
comment|/**  * A {@link Recycler} implementation based on a concurrent {@link Deque}. This implementation is thread-safe.  */
end_comment

begin_class
DECL|class|ConcurrentDequeRecycler
specifier|public
class|class
name|ConcurrentDequeRecycler
parameter_list|<
name|T
parameter_list|>
extends|extends
name|DequeRecycler
argument_list|<
name|T
argument_list|>
block|{
comment|// we maintain size separately because concurrent deque implementations typically have linear-time size() impls
DECL|field|size
specifier|final
name|AtomicInteger
name|size
decl_stmt|;
DECL|method|ConcurrentDequeRecycler
specifier|public
name|ConcurrentDequeRecycler
parameter_list|(
name|C
argument_list|<
name|T
argument_list|>
name|c
parameter_list|,
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|c
argument_list|,
name|ConcurrentCollections
operator|.
expr|<
name|T
operator|>
name|newDeque
argument_list|()
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
assert|assert
name|deque
operator|.
name|size
argument_list|()
operator|==
name|size
operator|.
name|get
argument_list|()
assert|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|size
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|obtain
specifier|public
name|V
argument_list|<
name|T
argument_list|>
name|obtain
parameter_list|(
name|int
name|sizing
parameter_list|)
block|{
specifier|final
name|V
argument_list|<
name|T
argument_list|>
name|v
init|=
name|super
operator|.
name|obtain
argument_list|(
name|sizing
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|isRecycled
argument_list|()
condition|)
block|{
name|size
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|beforeRelease
specifier|protected
name|boolean
name|beforeRelease
parameter_list|()
block|{
return|return
name|size
operator|.
name|incrementAndGet
argument_list|()
operator|<=
name|maxSize
return|;
block|}
annotation|@
name|Override
DECL|method|afterRelease
specifier|protected
name|void
name|afterRelease
parameter_list|(
name|boolean
name|recycled
parameter_list|)
block|{
if|if
condition|(
operator|!
name|recycled
condition|)
block|{
name|size
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
