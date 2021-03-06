begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|Action
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|GenericAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|TransportAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|support
operator|.
name|AbstractClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|tasks
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Client that executes actions on the local node.  */
end_comment

begin_class
DECL|class|NodeClient
specifier|public
class|class
name|NodeClient
extends|extends
name|AbstractClient
block|{
DECL|field|actions
specifier|private
name|Map
argument_list|<
name|GenericAction
argument_list|,
name|TransportAction
argument_list|>
name|actions
decl_stmt|;
comment|/**      * The id of the local {@link DiscoveryNode}. Useful for generating task ids from tasks returned by      * {@link #executeLocally(GenericAction, ActionRequest, TaskListener)}.      */
DECL|field|localNodeId
specifier|private
name|Supplier
argument_list|<
name|String
argument_list|>
name|localNodeId
decl_stmt|;
DECL|method|NodeClient
specifier|public
name|NodeClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|Map
argument_list|<
name|GenericAction
argument_list|,
name|TransportAction
argument_list|>
name|actions
parameter_list|,
name|Supplier
argument_list|<
name|String
argument_list|>
name|localNodeId
parameter_list|)
block|{
name|this
operator|.
name|actions
operator|=
name|actions
expr_stmt|;
name|this
operator|.
name|localNodeId
operator|=
name|localNodeId
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
comment|// nothing really to do
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
DECL|method|doExecute
parameter_list|>
name|void
name|doExecute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
comment|// Discard the task because the Client interface doesn't use it.
name|executeLocally
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Execute an {@link Action} locally, returning that {@link Task} used to track it, and linking an {@link ActionListener}. Prefer this      * method if you don't need access to the task when listening for the response. This is the method used to implement the {@link Client}      * interface.      */
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
DECL|method|executeLocally
parameter_list|>
name|Task
name|executeLocally
parameter_list|(
name|GenericAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
return|return
name|transportAction
argument_list|(
name|action
argument_list|)
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
return|;
block|}
comment|/**      * Execute an {@link Action} locally, returning that {@link Task} used to track it, and linking an {@link TaskListener}. Prefer this      * method if you need access to the task when listening for the response.      */
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
DECL|method|executeLocally
parameter_list|>
name|Task
name|executeLocally
parameter_list|(
name|GenericAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|TaskListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
return|return
name|transportAction
argument_list|(
name|action
argument_list|)
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
return|;
block|}
comment|/**      * The id of the local {@link DiscoveryNode}. Useful for generating task ids from tasks returned by      * {@link #executeLocally(GenericAction, ActionRequest, TaskListener)}.      */
DECL|method|getLocalNodeId
specifier|public
name|String
name|getLocalNodeId
parameter_list|()
block|{
return|return
name|localNodeId
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Get the {@link TransportAction} for an {@link Action}, throwing exceptions if the action isn't available.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
DECL|method|transportAction
parameter_list|>
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|transportAction
parameter_list|(
name|GenericAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
parameter_list|)
block|{
if|if
condition|(
name|actions
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NodeClient has not been initialized"
argument_list|)
throw|;
block|}
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|transportAction
init|=
name|actions
operator|.
name|get
argument_list|(
name|action
argument_list|)
decl_stmt|;
if|if
condition|(
name|transportAction
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to find action ["
operator|+
name|action
operator|+
literal|"] to execute"
argument_list|)
throw|;
block|}
return|return
name|transportAction
return|;
block|}
block|}
end_class

end_unit

