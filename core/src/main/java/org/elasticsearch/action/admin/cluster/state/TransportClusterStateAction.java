begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.state
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|state
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectObjectCursor
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
name|master
operator|.
name|TransportMasterNodeReadAction
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
name|ClusterName
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
name|IndexMetaData
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
name|metadata
operator|.
name|MetaData
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
name|MetaData
operator|.
name|Custom
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
name|routing
operator|.
name|RoutingTable
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
name|service
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
name|TransportService
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportClusterStateAction
specifier|public
class|class
name|TransportClusterStateAction
extends|extends
name|TransportMasterNodeReadAction
argument_list|<
name|ClusterStateRequest
argument_list|,
name|ClusterStateResponse
argument_list|>
block|{
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClusterStateAction
specifier|public
name|TransportClusterStateAction
parameter_list|(
name|Settings
name|settings
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
name|ClusterName
name|clusterName
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ClusterStateAction
operator|.
name|NAME
argument_list|,
literal|false
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|ClusterStateRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
comment|// very lightweight operation in memory, no need to fork to a thread
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|ClusterStateRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
comment|// cluster state calls are done also on a fully blocked cluster to figure out what is going
comment|// on in the cluster. For example, which nodes have joined yet the recovery has not yet kicked
comment|// in, we need to make sure we allow those calls
comment|// return state.blocks().globalBlockedException(ClusterBlockLevel.METADATA);
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClusterStateResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|ClusterStateResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|ClusterStateRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|ClusterState
name|currentState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Serving cluster state request using version {}"
argument_list|,
name|currentState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
name|builder
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
operator|.
name|getClusterName
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|version
argument_list|(
name|currentState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|stateUUID
argument_list|(
name|currentState
operator|.
name|stateUUID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|nodes
argument_list|()
condition|)
block|{
name|builder
operator|.
name|nodes
argument_list|(
name|currentState
operator|.
name|nodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|routingTable
argument_list|()
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|filteredIndex
range|:
name|request
operator|.
name|indices
argument_list|()
control|)
block|{
if|if
condition|(
name|currentState
operator|.
name|routingTable
argument_list|()
operator|.
name|getIndicesRouting
argument_list|()
operator|.
name|containsKey
argument_list|(
name|filteredIndex
argument_list|)
condition|)
block|{
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|currentState
operator|.
name|routingTable
argument_list|()
operator|.
name|getIndicesRouting
argument_list|()
operator|.
name|get
argument_list|(
name|filteredIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|routingTable
argument_list|(
name|currentState
operator|.
name|routingTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|request
operator|.
name|blocks
argument_list|()
condition|)
block|{
name|builder
operator|.
name|blocks
argument_list|(
name|currentState
operator|.
name|blocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|metaData
argument_list|()
condition|)
block|{
name|MetaData
operator|.
name|Builder
name|mdBuilder
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|mdBuilder
operator|=
name|MetaData
operator|.
name|builder
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mdBuilder
operator|=
name|MetaData
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|indices
init|=
name|indexNameExpressionResolver
operator|.
name|concreteIndexNames
argument_list|(
name|currentState
argument_list|,
name|request
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|filteredIndex
range|:
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|filteredIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|!=
literal|null
condition|)
block|{
name|mdBuilder
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Filter our metadata that shouldn't be returned by API
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Custom
argument_list|>
name|custom
range|:
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|customs
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|custom
operator|.
name|value
operator|.
name|context
argument_list|()
operator|.
name|contains
argument_list|(
name|MetaData
operator|.
name|XContentContext
operator|.
name|API
argument_list|)
condition|)
block|{
name|mdBuilder
operator|.
name|removeCustom
argument_list|(
name|custom
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|metaData
argument_list|(
name|mdBuilder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|customs
argument_list|()
condition|)
block|{
name|builder
operator|.
name|customs
argument_list|(
name|currentState
operator|.
name|customs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClusterStateResponse
argument_list|(
name|clusterName
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

