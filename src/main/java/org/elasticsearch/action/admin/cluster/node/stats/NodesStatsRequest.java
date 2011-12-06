begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.stats
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
name|stats
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
name|support
operator|.
name|nodes
operator|.
name|NodesOperationRequest
import|;
end_import

begin_comment
comment|/**  * A request to get node (cluster) level stats.  *  *  */
end_comment

begin_class
DECL|class|NodesStatsRequest
specifier|public
class|class
name|NodesStatsRequest
extends|extends
name|NodesOperationRequest
block|{
DECL|method|NodesStatsRequest
specifier|protected
name|NodesStatsRequest
parameter_list|()
block|{     }
comment|/**      * Get stats from nodes based on the nodes ids specified. If none are passed, stats      * for all nodes will be returned.      */
DECL|method|NodesStatsRequest
specifier|public
name|NodesStatsRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
name|super
argument_list|(
name|nodesIds
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

