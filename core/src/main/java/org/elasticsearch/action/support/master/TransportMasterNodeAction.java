begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.master
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
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
name|ActionListenerResponseHandler
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
name|ActionRunnable
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
name|ActionFilters
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
name|HandledTransportAction
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
name|ThreadedActionListener
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
name|ClusterService
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
name|ClusterState
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
name|ClusterStateObserver
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
name|MasterNodeChangePredicate
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
name|NotMasterException
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
name|block
operator|.
name|ClusterBlockException
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
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|DiscoveryNodes
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|Discovery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|MasterNotDiscoveredException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeClosedException
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
name|threadpool
operator|.
name|ThreadPool
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
name|ConnectTransportException
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
name|TransportException
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
name|TransportService
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
comment|/**  * A base class for operations that needs to be performed on the master node.  */
end_comment

begin_class
DECL|class|TransportMasterNodeAction
specifier|public
specifier|abstract
class|class
name|TransportMasterNodeAction
parameter_list|<
name|Request
extends|extends
name|MasterNodeRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
extends|extends
name|HandledTransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
block|{
DECL|field|transportService
specifier|protected
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|protected
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|executor
specifier|final
name|String
name|executor
decl_stmt|;
DECL|method|TransportMasterNodeAction
specifier|protected
name|TransportMasterNodeAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|actionName
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|Supplier
argument_list|<
name|Request
argument_list|>
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|actionName
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
argument_list|()
expr_stmt|;
block|}
DECL|method|executor
specifier|protected
specifier|abstract
name|String
name|executor
parameter_list|()
function_decl|;
DECL|method|newResponse
specifier|protected
specifier|abstract
name|Response
name|newResponse
parameter_list|()
function_decl|;
DECL|method|masterOperation
specifier|protected
specifier|abstract
name|void
name|masterOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Override this operation if access to the task parameter is needed      */
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
name|Task
name|task
parameter_list|,
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|masterOperation
argument_list|(
name|request
argument_list|,
name|state
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|localExecute
specifier|protected
name|boolean
name|localExecute
parameter_list|(
name|Request
name|request
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|checkBlock
specifier|protected
specifier|abstract
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
specifier|final
name|void
name|doExecute
parameter_list|(
specifier|final
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
name|logger
operator|.
name|warn
argument_list|(
literal|"attempt to execute a master node operation without task"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"task parameter is required for this operation"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|Task
name|task
parameter_list|,
specifier|final
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
operator|new
name|AsyncSingleAction
argument_list|(
name|task
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|class|AsyncSingleAction
class|class
name|AsyncSingleAction
block|{
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|observer
specifier|private
specifier|volatile
name|ClusterStateObserver
name|observer
decl_stmt|;
DECL|field|task
specifier|private
specifier|final
name|Task
name|task
decl_stmt|;
DECL|field|retryableOrNoBlockPredicate
specifier|private
specifier|final
name|ClusterStateObserver
operator|.
name|ChangePredicate
name|retryableOrNoBlockPredicate
init|=
operator|new
name|ClusterStateObserver
operator|.
name|ValidationPredicate
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|validate
parameter_list|(
name|ClusterState
name|newState
parameter_list|)
block|{
name|ClusterBlockException
name|blockException
init|=
name|checkBlock
argument_list|(
name|request
argument_list|,
name|newState
argument_list|)
decl_stmt|;
return|return
operator|(
name|blockException
operator|==
literal|null
operator|||
operator|!
name|blockException
operator|.
name|retryable
argument_list|()
operator|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|AsyncSingleAction
name|AsyncSingleAction
parameter_list|(
name|Task
name|task
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
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setParentTask
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO do we really need to wrap it in a listener? the handlers should be cheap
if|if
condition|(
operator|(
name|listener
operator|instanceof
name|ThreadedActionListener
operator|)
operator|==
literal|false
condition|)
block|{
name|listener
operator|=
operator|new
name|ThreadedActionListener
argument_list|<>
argument_list|(
name|logger
argument_list|,
name|threadPool
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|LISTENER
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|this
operator|.
name|observer
operator|=
operator|new
name|ClusterStateObserver
argument_list|(
name|clusterService
argument_list|,
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|,
name|logger
argument_list|,
name|threadPool
operator|.
name|getThreadContext
argument_list|()
argument_list|)
expr_stmt|;
name|doStart
argument_list|()
expr_stmt|;
block|}
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
specifier|final
name|ClusterState
name|clusterState
init|=
name|observer
operator|.
name|observedState
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNodes
name|nodes
init|=
name|clusterState
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|localNodeMaster
argument_list|()
operator|||
name|localExecute
argument_list|(
name|request
argument_list|)
condition|)
block|{
comment|// check for block, if blocked, retry, else, execute locally
specifier|final
name|ClusterBlockException
name|blockException
init|=
name|checkBlock
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockException
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|blockException
operator|.
name|retryable
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|blockException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"can't execute due to a cluster block, retrying"
argument_list|,
name|blockException
argument_list|)
expr_stmt|;
name|retry
argument_list|(
name|blockException
argument_list|,
name|retryableOrNoBlockPredicate
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|delegate
init|=
operator|new
name|ActionListener
argument_list|<
name|Response
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|Discovery
operator|.
name|FailedToCommitClusterStateException
operator|||
operator|(
name|t
operator|instanceof
name|NotMasterException
operator|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"master could not publish cluster state or stepped down before publishing action [{}], scheduling a retry"
argument_list|,
name|t
argument_list|,
name|actionName
argument_list|)
expr_stmt|;
name|retry
argument_list|(
name|t
argument_list|,
name|MasterNodeChangePredicate
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|ActionRunnable
argument_list|(
name|delegate
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|masterOperation
argument_list|(
name|task
argument_list|,
name|request
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|nodes
operator|.
name|masterNode
argument_list|()
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"no known master node, scheduling a retry"
argument_list|)
expr_stmt|;
name|retry
argument_list|(
literal|null
argument_list|,
name|MasterNodeChangePredicate
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|nodes
operator|.
name|masterNode
argument_list|()
argument_list|,
name|actionName
argument_list|,
name|request
argument_list|,
operator|new
name|ActionListenerResponseHandler
argument_list|<
name|Response
argument_list|>
argument_list|(
name|listener
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Response
name|newInstance
parameter_list|()
block|{
return|return
name|newResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
specifier|final
name|TransportException
name|exp
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|exp
operator|.
name|unwrapCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|ConnectTransportException
condition|)
block|{
comment|// we want to retry here a bit to see if a new master is elected
name|logger
operator|.
name|debug
argument_list|(
literal|"connection exception while trying to forward request with action name [{}] to master node [{}], scheduling a retry. Error: [{}]"
argument_list|,
name|actionName
argument_list|,
name|nodes
operator|.
name|masterNode
argument_list|()
argument_list|,
name|exp
operator|.
name|getDetailedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|retry
argument_list|(
name|cause
argument_list|,
name|MasterNodeChangePredicate
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|retry
specifier|private
name|void
name|retry
parameter_list|(
specifier|final
name|Throwable
name|failure
parameter_list|,
specifier|final
name|ClusterStateObserver
operator|.
name|ChangePredicate
name|changePredicate
parameter_list|)
block|{
name|observer
operator|.
name|waitForNextChange
argument_list|(
operator|new
name|ClusterStateObserver
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNewClusterState
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
name|doStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onClusterServiceClose
parameter_list|()
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|NodeClosedException
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"timed out while retrying [{}] after failure (timeout [{}])"
argument_list|,
name|failure
argument_list|,
name|actionName
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|MasterNotDiscoveredException
argument_list|(
name|failure
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|changePredicate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

