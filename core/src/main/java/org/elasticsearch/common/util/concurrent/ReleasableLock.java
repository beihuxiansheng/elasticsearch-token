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
name|Assertions
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
name|lease
operator|.
name|Releasable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
operator|.
name|EngineException
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
name|Lock
import|;
end_import

begin_comment
comment|/**  * Releasable lock used inside of Engine implementations  */
end_comment

begin_class
DECL|class|ReleasableLock
specifier|public
class|class
name|ReleasableLock
implements|implements
name|Releasable
block|{
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
comment|/* a per thread boolean indicating the lock is held by it. only works when assertions are enabled */
DECL|field|holdingThreads
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
name|holdingThreads
decl_stmt|;
DECL|method|ReleasableLock
specifier|public
name|ReleasableLock
parameter_list|(
name|Lock
name|lock
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
if|if
condition|(
name|Assertions
operator|.
name|ENABLED
condition|)
block|{
name|holdingThreads
operator|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|holdingThreads
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
assert|assert
name|removeCurrentThread
argument_list|()
assert|;
block|}
DECL|method|acquire
specifier|public
name|ReleasableLock
name|acquire
parameter_list|()
throws|throws
name|EngineException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
assert|assert
name|addCurrentThread
argument_list|()
assert|;
return|return
name|this
return|;
block|}
DECL|method|addCurrentThread
specifier|private
name|boolean
name|addCurrentThread
parameter_list|()
block|{
name|holdingThreads
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|removeCurrentThread
specifier|private
name|boolean
name|removeCurrentThread
parameter_list|()
block|{
name|holdingThreads
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|isHeldByCurrentThread
specifier|public
name|Boolean
name|isHeldByCurrentThread
parameter_list|()
block|{
if|if
condition|(
name|holdingThreads
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"asserts must be enabled"
argument_list|)
throw|;
block|}
name|Boolean
name|b
init|=
name|holdingThreads
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|b
operator|!=
literal|null
operator|&&
name|b
operator|.
name|booleanValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

