begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.delete
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|delete
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
name|TransportActions
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
name|block
operator|.
name|ClusterBlockLevel
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
name|MetaDataService
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
comment|/**  * Delete index action.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportDeleteIndexAction
specifier|public
class|class
name|TransportDeleteIndexAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|DeleteIndexRequest
argument_list|,
name|DeleteIndexResponse
argument_list|>
block|{
DECL|field|metaDataService
specifier|private
specifier|final
name|MetaDataService
name|metaDataService
decl_stmt|;
DECL|method|TransportDeleteIndexAction
annotation|@
name|Inject
specifier|public
name|TransportDeleteIndexAction
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
name|MetaDataService
name|metaDataService
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
name|metaDataService
operator|=
name|metaDataService
expr_stmt|;
block|}
DECL|method|transportAction
annotation|@
name|Override
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|TransportActions
operator|.
name|Admin
operator|.
name|Indices
operator|.
name|DELETE
return|;
block|}
DECL|method|newRequest
annotation|@
name|Override
specifier|protected
name|DeleteIndexRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|DeleteIndexRequest
argument_list|()
return|;
block|}
DECL|method|newResponse
annotation|@
name|Override
specifier|protected
name|DeleteIndexResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|DeleteIndexResponse
argument_list|()
return|;
block|}
DECL|method|checkBlock
annotation|@
name|Override
specifier|protected
name|void
name|checkBlock
parameter_list|(
name|DeleteIndexRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedRaiseException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|masterOperation
annotation|@
name|Override
specifier|protected
name|DeleteIndexResponse
name|masterOperation
parameter_list|(
name|DeleteIndexRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|MetaDataService
operator|.
name|DeleteIndexResult
name|deleteIndexResult
init|=
name|metaDataService
operator|.
name|deleteIndex
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|DeleteIndexResponse
argument_list|(
name|deleteIndexResult
operator|.
name|acknowledged
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

