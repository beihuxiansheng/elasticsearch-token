begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.put
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
name|mapping
operator|.
name|put
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
comment|/**  * Put mapping action.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportPutMappingAction
specifier|public
class|class
name|TransportPutMappingAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|PutMappingRequest
argument_list|,
name|PutMappingResponse
argument_list|>
block|{
DECL|field|metaDataService
specifier|private
specifier|final
name|MetaDataService
name|metaDataService
decl_stmt|;
DECL|method|TransportPutMappingAction
annotation|@
name|Inject
specifier|public
name|TransportPutMappingAction
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
name|Mapping
operator|.
name|PUT
return|;
block|}
DECL|method|newRequest
annotation|@
name|Override
specifier|protected
name|PutMappingRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|PutMappingRequest
argument_list|()
return|;
block|}
DECL|method|newResponse
annotation|@
name|Override
specifier|protected
name|PutMappingResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|PutMappingResponse
argument_list|()
return|;
block|}
DECL|method|masterOperation
annotation|@
name|Override
specifier|protected
name|PutMappingResponse
name|masterOperation
parameter_list|(
name|PutMappingRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
comment|// update to concrete indices
name|request
operator|.
name|indices
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|indices
init|=
name|request
operator|.
name|indices
argument_list|()
decl_stmt|;
name|MetaDataService
operator|.
name|PutMappingResult
name|result
init|=
name|metaDataService
operator|.
name|putMapping
argument_list|(
name|indices
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|source
argument_list|()
argument_list|,
name|request
operator|.
name|ignoreConflicts
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
name|PutMappingResponse
argument_list|(
name|result
operator|.
name|acknowledged
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

