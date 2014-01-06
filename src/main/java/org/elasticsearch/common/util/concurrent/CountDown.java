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
name|ElasticsearchIllegalArgumentException
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
name|CountDownLatch
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
comment|/**  * A simple thread safe count-down class that in contrast to a {@link CountDownLatch}  * never blocks. This class is useful if a certain action has to wait for N concurrent   * tasks to return or a timeout to occur in order to proceed.  */
end_comment

begin_class
DECL|class|CountDown
specifier|public
specifier|final
class|class
name|CountDown
block|{
DECL|field|countDown
specifier|private
specifier|final
name|AtomicInteger
name|countDown
decl_stmt|;
DECL|field|originalCount
specifier|private
specifier|final
name|int
name|originalCount
decl_stmt|;
DECL|method|CountDown
specifier|public
name|CountDown
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"count must be greater or equal to 0 but was: "
operator|+
name|count
argument_list|)
throw|;
block|}
name|this
operator|.
name|originalCount
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|countDown
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**      * Decrements the count-down and returns<code>true</code> iff this call      * reached zero otherwise<code>false</code>      */
DECL|method|countDown
specifier|public
name|boolean
name|countDown
parameter_list|()
block|{
assert|assert
name|originalCount
operator|>
literal|0
assert|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|current
init|=
name|countDown
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
name|current
operator|>=
literal|0
assert|;
if|if
condition|(
name|current
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|countDown
operator|.
name|compareAndSet
argument_list|(
name|current
argument_list|,
name|current
operator|-
literal|1
argument_list|)
condition|)
block|{
return|return
name|current
operator|==
literal|1
return|;
block|}
block|}
block|}
comment|/**      * Fast forwards the count-down to zero and returns<code>true</code> iff      * the count down reached zero with this fast forward call otherwise      *<code>false</code>      */
DECL|method|fastForward
specifier|public
name|boolean
name|fastForward
parameter_list|()
block|{
assert|assert
name|originalCount
operator|>
literal|0
assert|;
assert|assert
name|countDown
operator|.
name|get
argument_list|()
operator|>=
literal|0
assert|;
return|return
name|countDown
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
operator|>
literal|0
return|;
block|}
comment|/**      * Returns<code>true</code> iff the count-down has reached zero. Otherwise<code>false</code>      */
DECL|method|isCountedDown
specifier|public
name|boolean
name|isCountedDown
parameter_list|()
block|{
assert|assert
name|countDown
operator|.
name|get
argument_list|()
operator|>=
literal|0
assert|;
return|return
name|countDown
operator|.
name|get
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
end_class

end_unit

