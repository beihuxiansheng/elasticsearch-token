begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|collect
operator|.
name|Tuple
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
name|inject
operator|.
name|Inject
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
name|CountDown
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|action
operator|.
name|SearchServiceTransportAction
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
name|BaseTransportRequestHandler
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
name|TransportChannel
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|AtomicReference
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|type
operator|.
name|TransportSearchHelper
operator|.
name|parseScrollId
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportClearScrollAction
specifier|public
class|class
name|TransportClearScrollAction
extends|extends
name|TransportAction
argument_list|<
name|ClearScrollRequest
argument_list|,
name|ClearScrollResponse
argument_list|>
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|searchServiceTransportAction
specifier|private
specifier|final
name|SearchServiceTransportAction
name|searchServiceTransportAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClearScrollAction
specifier|public
name|TransportClearScrollAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|SearchServiceTransportAction
name|searchServiceTransportAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|searchServiceTransportAction
operator|=
name|searchServiceTransportAction
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|ClearScrollAction
operator|.
name|NAME
argument_list|,
operator|new
name|TransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ClearScrollRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClearScrollResponse
argument_list|>
name|listener
parameter_list|)
block|{
operator|new
name|Async
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
DECL|class|Async
specifier|private
class|class
name|Async
block|{
DECL|field|nodes
specifier|final
name|DiscoveryNodes
name|nodes
decl_stmt|;
DECL|field|expectedOps
specifier|final
name|CountDown
name|expectedOps
decl_stmt|;
DECL|field|request
specifier|final
name|ClearScrollRequest
name|request
decl_stmt|;
DECL|field|contexts
specifier|final
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
argument_list|>
name|contexts
init|=
operator|new
name|ArrayList
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|expHolder
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|expHolder
decl_stmt|;
DECL|field|listener
specifier|final
name|ActionListener
argument_list|<
name|ClearScrollResponse
argument_list|>
name|listener
decl_stmt|;
DECL|method|Async
specifier|private
name|Async
parameter_list|(
name|ClearScrollRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClearScrollResponse
argument_list|>
name|listener
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|int
name|expectedOps
init|=
literal|0
decl_stmt|;
name|this
operator|.
name|nodes
operator|=
name|clusterState
operator|.
name|nodes
argument_list|()
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getScrollIds
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
literal|"_all"
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getScrollIds
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|expectedOps
operator|=
name|nodes
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|parsedScrollId
range|:
name|request
operator|.
name|getScrollIds
argument_list|()
control|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
name|context
init|=
name|parseScrollId
argument_list|(
name|parsedScrollId
argument_list|)
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|expectedOps
operator|+=
name|context
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|contexts
operator|.
name|add
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|expHolder
operator|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|expectedOps
operator|=
operator|new
name|CountDown
argument_list|(
name|expectedOps
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|expectedOps
operator|.
name|isCountedDown
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClearScrollResponse
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|contexts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
name|searchServiceTransportAction
operator|.
name|sendClearAllScrollContexts
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Boolean
name|success
parameter_list|)
block|{
name|onFreedContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onFailedFreedContext
argument_list|(
name|e
argument_list|,
name|node
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
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
name|context
range|:
name|contexts
control|)
block|{
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|target
range|:
name|context
control|)
block|{
specifier|final
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|target
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|onFreedContext
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|searchServiceTransportAction
operator|.
name|sendFreeContext
argument_list|(
name|node
argument_list|,
name|target
operator|.
name|v2
argument_list|()
argument_list|,
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Boolean
name|success
parameter_list|)
block|{
name|onFreedContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onFailedFreedContext
argument_list|(
name|e
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|onFreedContext
name|void
name|onFreedContext
parameter_list|()
block|{
if|if
condition|(
name|expectedOps
operator|.
name|countDown
argument_list|()
condition|)
block|{
name|boolean
name|succeeded
init|=
name|expHolder
operator|.
name|get
argument_list|()
operator|==
literal|null
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClearScrollResponse
argument_list|(
name|succeeded
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onFailedFreedContext
name|void
name|onFailedFreedContext
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Clear SC failed on node[{}]"
argument_list|,
name|e
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedOps
operator|.
name|countDown
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClearScrollResponse
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expHolder
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TransportHandler
class|class
name|TransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|ClearScrollRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|ClearScrollRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ClearScrollRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|ClearScrollRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
comment|// no need to use threaded listener, since we just send a response
name|request
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ClearScrollResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClearScrollResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send error response for action [clear_sc] and request ["
operator|+
name|request
operator|+
literal|"]"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
block|}
block|}
end_class

end_unit

