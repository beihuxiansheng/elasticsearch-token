begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|master
operator|.
name|TransportMasterNodeOperationAction
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
name|IndexTemplateMetaData
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
operator|.
name|newClusterStateBuilder
import|;
end_import

begin_import
import|import static
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
name|newMetaDataBuilder
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
name|TransportMasterNodeOperationAction
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
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
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
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|ClusterStateAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|ClusterStateRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterStateRequest
argument_list|()
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
DECL|method|localExecute
specifier|protected
name|boolean
name|localExecute
parameter_list|(
name|ClusterStateRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|local
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
throws|throws
name|ElasticSearchException
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
name|debug
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
name|newClusterStateBuilder
argument_list|()
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
if|if
condition|(
operator|!
name|request
operator|.
name|filterNodes
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
operator|!
name|request
operator|.
name|filterRoutingTable
argument_list|()
condition|)
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
name|builder
operator|.
name|allocationExplanation
argument_list|(
name|currentState
operator|.
name|allocationExplanation
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|request
operator|.
name|filterBlocks
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
operator|!
name|request
operator|.
name|filterMetaData
argument_list|()
condition|)
block|{
name|MetaData
operator|.
name|Builder
name|mdBuilder
init|=
name|newMetaDataBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|filteredIndices
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|&&
name|request
operator|.
name|filteredIndexTemplates
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|mdBuilder
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|filteredIndices
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|request
operator|.
name|filteredIndices
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|ClusterStateRequest
operator|.
name|NONE
operator|.
name|equals
argument_list|(
name|request
operator|.
name|filteredIndices
argument_list|()
index|[
literal|0
index|]
argument_list|)
operator|)
condition|)
block|{
name|String
index|[]
name|indices
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndicesIgnoreMissing
argument_list|(
name|request
operator|.
name|filteredIndices
argument_list|()
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
block|}
if|if
condition|(
name|request
operator|.
name|filteredIndexTemplates
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|templateName
range|:
name|request
operator|.
name|filteredIndexTemplates
argument_list|()
control|)
block|{
name|IndexTemplateMetaData
name|indexTemplateMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|templates
argument_list|()
operator|.
name|get
argument_list|(
name|templateName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexTemplateMetaData
operator|!=
literal|null
condition|)
block|{
name|mdBuilder
operator|.
name|put
argument_list|(
name|indexTemplateMetaData
argument_list|)
expr_stmt|;
block|}
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

