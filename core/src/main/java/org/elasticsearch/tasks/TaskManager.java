begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
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
name|component
operator|.
name|AbstractComponent
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
name|settings
operator|.
name|Settings
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMapLong
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Task Manager service for keeping track of currently running tasks on the nodes  */
end_comment

begin_class
DECL|class|TaskManager
specifier|public
class|class
name|TaskManager
extends|extends
name|AbstractComponent
block|{
DECL|field|tasks
specifier|private
specifier|final
name|ConcurrentMapLong
argument_list|<
name|Task
argument_list|>
name|tasks
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapLongWithAggressiveConcurrency
argument_list|()
decl_stmt|;
DECL|field|taskIdGenerator
specifier|private
specifier|final
name|AtomicLong
name|taskIdGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|TaskManager
specifier|public
name|TaskManager
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Registers a task without parent task      */
DECL|method|register
specifier|public
name|Task
name|register
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|action
parameter_list|,
name|TransportRequest
name|request
parameter_list|)
block|{
name|Task
name|task
init|=
name|request
operator|.
name|createTask
argument_list|(
name|taskIdGenerator
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|type
argument_list|,
name|action
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"register {} [{}] [{}] [{}]"
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|,
name|type
argument_list|,
name|action
argument_list|,
name|task
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Task
name|previousTask
init|=
name|tasks
operator|.
name|put
argument_list|(
name|task
operator|.
name|getId
argument_list|()
argument_list|,
name|task
argument_list|)
decl_stmt|;
assert|assert
name|previousTask
operator|==
literal|null
assert|;
block|}
return|return
name|task
return|;
block|}
comment|/**      * Unregister the task      */
DECL|method|unregister
specifier|public
name|void
name|unregister
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"unregister task for id: {}"
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|remove
argument_list|(
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the list of currently running tasks on the node      */
DECL|method|getTasks
specifier|public
name|Map
argument_list|<
name|Long
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|tasks
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

