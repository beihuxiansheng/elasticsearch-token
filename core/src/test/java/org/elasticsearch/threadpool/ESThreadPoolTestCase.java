begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_class
DECL|class|ESThreadPoolTestCase
specifier|public
specifier|abstract
class|class
name|ESThreadPoolTestCase
extends|extends
name|ESTestCase
block|{
DECL|method|info
specifier|protected
specifier|final
name|ThreadPool
operator|.
name|Info
name|info
parameter_list|(
specifier|final
name|ThreadPool
name|threadPool
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ThreadPool
operator|.
name|Info
name|info
range|:
name|threadPool
operator|.
name|info
argument_list|()
control|)
block|{
if|if
condition|(
name|info
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|info
return|;
block|}
block|}
assert|assert
literal|"same"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
assert|;
return|return
literal|null
return|;
block|}
DECL|method|stats
specifier|protected
specifier|final
name|ThreadPoolStats
operator|.
name|Stats
name|stats
parameter_list|(
specifier|final
name|ThreadPool
name|threadPool
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ThreadPoolStats
operator|.
name|Stats
name|stats
range|:
name|threadPool
operator|.
name|stats
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|stats
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|stats
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|name
argument_list|)
throw|;
block|}
DECL|method|terminateThreadPoolIfNeeded
specifier|protected
specifier|final
name|void
name|terminateThreadPoolIfNeeded
parameter_list|(
specifier|final
name|ThreadPool
name|threadPool
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|threadPool
operator|!=
literal|null
condition|)
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomThreadPool
specifier|static
name|String
name|randomThreadPool
parameter_list|(
specifier|final
name|ThreadPool
operator|.
name|ThreadPoolType
name|type
parameter_list|)
block|{
return|return
name|randomFrom
argument_list|(
name|ThreadPool
operator|.
name|THREAD_POOL_TYPES
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|t
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|type
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

