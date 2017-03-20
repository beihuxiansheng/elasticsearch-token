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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
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
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * Extends the {@code SizeBlockingQueue} to add the {@code adjustCapacity} method, which will adjust  * the capacity by a certain amount towards a maximum or minimum.  */
end_comment

begin_class
DECL|class|ResizableBlockingQueue
specifier|final
class|class
name|ResizableBlockingQueue
parameter_list|<
name|E
parameter_list|>
extends|extends
name|SizeBlockingQueue
argument_list|<
name|E
argument_list|>
block|{
DECL|field|capacity
specifier|private
specifier|volatile
name|int
name|capacity
decl_stmt|;
DECL|method|ResizableBlockingQueue
name|ResizableBlockingQueue
parameter_list|(
name|BlockingQueue
argument_list|<
name|E
argument_list|>
name|queue
parameter_list|,
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|queue
argument_list|,
name|initialCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|initialCapacity
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"optimalCapacity is non-negative, therefore the difference cannot be< -Integer.MAX_VALUE"
argument_list|)
DECL|method|getChangeAmount
specifier|private
name|int
name|getChangeAmount
parameter_list|(
name|int
name|optimalCapacity
parameter_list|)
block|{
assert|assert
name|optimalCapacity
operator|>=
literal|0
operator|:
literal|"optimal capacity should always be positive, got: "
operator|+
name|optimalCapacity
assert|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|optimalCapacity
operator|-
name|this
operator|.
name|capacity
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|capacity
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|capacity
return|;
block|}
annotation|@
name|Override
DECL|method|remainingCapacity
specifier|public
name|int
name|remainingCapacity
parameter_list|()
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|this
operator|.
name|capacity
argument_list|()
argument_list|)
return|;
block|}
comment|/** Resize the limit for the queue, returning the new size limit */
DECL|method|adjustCapacity
specifier|public
specifier|synchronized
name|int
name|adjustCapacity
parameter_list|(
name|int
name|optimalCapacity
parameter_list|,
name|int
name|adjustmentAmount
parameter_list|,
name|int
name|minCapacity
parameter_list|,
name|int
name|maxCapacity
parameter_list|)
block|{
assert|assert
name|adjustmentAmount
operator|>
literal|0
operator|:
literal|"adjustment amount should be a positive value"
assert|;
assert|assert
name|optimalCapacity
operator|>=
literal|0
operator|:
literal|"desired capacity cannot be negative"
assert|;
assert|assert
name|minCapacity
operator|>=
literal|0
operator|:
literal|"cannot have min capacity smaller than 0"
assert|;
assert|assert
name|maxCapacity
operator|>=
name|minCapacity
operator|:
literal|"cannot have max capacity smaller than min capacity"
assert|;
if|if
condition|(
name|optimalCapacity
operator|==
name|capacity
condition|)
block|{
comment|// Yahtzee!
return|return
name|this
operator|.
name|capacity
return|;
block|}
if|if
condition|(
name|optimalCapacity
operator|>
name|capacity
operator|+
name|adjustmentAmount
condition|)
block|{
comment|// adjust up
specifier|final
name|int
name|newCapacity
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxCapacity
argument_list|,
name|capacity
operator|+
name|adjustmentAmount
argument_list|)
decl_stmt|;
name|this
operator|.
name|capacity
operator|=
name|newCapacity
expr_stmt|;
return|return
name|newCapacity
return|;
block|}
elseif|else
if|if
condition|(
name|optimalCapacity
operator|<
name|capacity
operator|-
name|adjustmentAmount
condition|)
block|{
comment|// adjust down
specifier|final
name|int
name|newCapacity
init|=
name|Math
operator|.
name|max
argument_list|(
name|minCapacity
argument_list|,
name|capacity
operator|-
name|adjustmentAmount
argument_list|)
decl_stmt|;
name|this
operator|.
name|capacity
operator|=
name|newCapacity
expr_stmt|;
return|return
name|newCapacity
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|capacity
return|;
block|}
block|}
block|}
end_class

end_unit

