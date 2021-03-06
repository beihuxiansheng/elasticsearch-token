begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.usage
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
name|node
operator|.
name|usage
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
name|FailedNodeException
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
name|nodes
operator|.
name|BaseNodeRequest
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
name|nodes
operator|.
name|TransportNodesAction
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|usage
operator|.
name|UsageService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_class
DECL|class|TransportNodesUsageAction
specifier|public
class|class
name|TransportNodesUsageAction
extends|extends
name|TransportNodesAction
argument_list|<
name|NodesUsageRequest
argument_list|,
name|NodesUsageResponse
argument_list|,
name|TransportNodesUsageAction
operator|.
name|NodeUsageRequest
argument_list|,
name|NodeUsage
argument_list|>
block|{
DECL|field|usageService
specifier|private
name|UsageService
name|usageService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportNodesUsageAction
specifier|public
name|TransportNodesUsageAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|UsageService
name|usageService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|NodesUsageAction
operator|.
name|NAME
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|NodesUsageRequest
operator|::
operator|new
argument_list|,
name|NodeUsageRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
argument_list|,
name|NodeUsage
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|usageService
operator|=
name|usageService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|NodesUsageResponse
name|newResponse
parameter_list|(
name|NodesUsageRequest
name|request
parameter_list|,
name|List
argument_list|<
name|NodeUsage
argument_list|>
name|responses
parameter_list|,
name|List
argument_list|<
name|FailedNodeException
argument_list|>
name|failures
parameter_list|)
block|{
return|return
operator|new
name|NodesUsageResponse
argument_list|(
name|clusterService
operator|.
name|getClusterName
argument_list|()
argument_list|,
name|responses
argument_list|,
name|failures
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeRequest
specifier|protected
name|NodeUsageRequest
name|newNodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|NodesUsageRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|NodeUsageRequest
argument_list|(
name|nodeId
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeResponse
specifier|protected
name|NodeUsage
name|newNodeResponse
parameter_list|()
block|{
return|return
operator|new
name|NodeUsage
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodeOperation
specifier|protected
name|NodeUsage
name|nodeOperation
parameter_list|(
name|NodeUsageRequest
name|nodeUsageRequest
parameter_list|)
block|{
name|NodesUsageRequest
name|request
init|=
name|nodeUsageRequest
operator|.
name|request
decl_stmt|;
return|return
name|usageService
operator|.
name|getUsageStats
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|,
name|request
operator|.
name|restActions
argument_list|()
argument_list|)
return|;
block|}
DECL|class|NodeUsageRequest
specifier|public
specifier|static
class|class
name|NodeUsageRequest
extends|extends
name|BaseNodeRequest
block|{
DECL|field|request
name|NodesUsageRequest
name|request
decl_stmt|;
DECL|method|NodeUsageRequest
specifier|public
name|NodeUsageRequest
parameter_list|()
block|{         }
DECL|method|NodeUsageRequest
name|NodeUsageRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|NodesUsageRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|NodesUsageRequest
argument_list|()
expr_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

