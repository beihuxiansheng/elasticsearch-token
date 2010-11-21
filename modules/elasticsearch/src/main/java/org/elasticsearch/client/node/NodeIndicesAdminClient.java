begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ActionFuture
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesRequest
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesResponse
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|TransportIndicesAliasesAction
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeRequest
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeResponse
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|TransportAnalyzeAction
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
name|admin
operator|.
name|indices
operator|.
name|cache
operator|.
name|clear
operator|.
name|ClearIndicesCacheRequest
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
name|admin
operator|.
name|indices
operator|.
name|cache
operator|.
name|clear
operator|.
name|ClearIndicesCacheResponse
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
name|admin
operator|.
name|indices
operator|.
name|cache
operator|.
name|clear
operator|.
name|TransportClearIndicesCacheAction
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
name|admin
operator|.
name|indices
operator|.
name|close
operator|.
name|CloseIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|close
operator|.
name|CloseIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|close
operator|.
name|TransportCloseIndexAction
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|TransportCreateIndexAction
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|TransportDeleteIndexAction
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|FlushRequest
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|FlushResponse
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|TransportFlushAction
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
name|admin
operator|.
name|indices
operator|.
name|gateway
operator|.
name|snapshot
operator|.
name|GatewaySnapshotRequest
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
name|admin
operator|.
name|indices
operator|.
name|gateway
operator|.
name|snapshot
operator|.
name|GatewaySnapshotResponse
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
name|admin
operator|.
name|indices
operator|.
name|gateway
operator|.
name|snapshot
operator|.
name|TransportGatewaySnapshotAction
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|delete
operator|.
name|DeleteMappingRequest
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|delete
operator|.
name|DeleteMappingResponse
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|delete
operator|.
name|TransportDeleteMappingAction
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingResponse
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|TransportPutMappingAction
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
name|admin
operator|.
name|indices
operator|.
name|open
operator|.
name|OpenIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|open
operator|.
name|OpenIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|open
operator|.
name|TransportOpenIndexAction
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
name|admin
operator|.
name|indices
operator|.
name|optimize
operator|.
name|OptimizeRequest
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
name|admin
operator|.
name|indices
operator|.
name|optimize
operator|.
name|OptimizeResponse
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
name|admin
operator|.
name|indices
operator|.
name|optimize
operator|.
name|TransportOptimizeAction
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|RefreshRequest
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|RefreshResponse
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|TransportRefreshAction
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|TransportUpdateSettingsAction
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|UpdateSettingsRequest
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|UpdateSettingsResponse
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
name|admin
operator|.
name|indices
operator|.
name|status
operator|.
name|IndicesStatusRequest
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
name|admin
operator|.
name|indices
operator|.
name|status
operator|.
name|IndicesStatusResponse
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
name|admin
operator|.
name|indices
operator|.
name|status
operator|.
name|TransportIndicesStatusAction
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
name|IndicesAdminClient
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
name|AbstractIndicesAdminClient
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NodeIndicesAdminClient
specifier|public
class|class
name|NodeIndicesAdminClient
extends|extends
name|AbstractIndicesAdminClient
implements|implements
name|IndicesAdminClient
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|indicesStatusAction
specifier|private
specifier|final
name|TransportIndicesStatusAction
name|indicesStatusAction
decl_stmt|;
DECL|field|createIndexAction
specifier|private
specifier|final
name|TransportCreateIndexAction
name|createIndexAction
decl_stmt|;
DECL|field|deleteIndexAction
specifier|private
specifier|final
name|TransportDeleteIndexAction
name|deleteIndexAction
decl_stmt|;
DECL|field|closeIndexAction
specifier|private
specifier|final
name|TransportCloseIndexAction
name|closeIndexAction
decl_stmt|;
DECL|field|openIndexAction
specifier|private
specifier|final
name|TransportOpenIndexAction
name|openIndexAction
decl_stmt|;
DECL|field|refreshAction
specifier|private
specifier|final
name|TransportRefreshAction
name|refreshAction
decl_stmt|;
DECL|field|flushAction
specifier|private
specifier|final
name|TransportFlushAction
name|flushAction
decl_stmt|;
DECL|field|optimizeAction
specifier|private
specifier|final
name|TransportOptimizeAction
name|optimizeAction
decl_stmt|;
DECL|field|putMappingAction
specifier|private
specifier|final
name|TransportPutMappingAction
name|putMappingAction
decl_stmt|;
DECL|field|deleteMappingAction
specifier|private
specifier|final
name|TransportDeleteMappingAction
name|deleteMappingAction
decl_stmt|;
DECL|field|gatewaySnapshotAction
specifier|private
specifier|final
name|TransportGatewaySnapshotAction
name|gatewaySnapshotAction
decl_stmt|;
DECL|field|indicesAliasesAction
specifier|private
specifier|final
name|TransportIndicesAliasesAction
name|indicesAliasesAction
decl_stmt|;
DECL|field|clearIndicesCacheAction
specifier|private
specifier|final
name|TransportClearIndicesCacheAction
name|clearIndicesCacheAction
decl_stmt|;
DECL|field|updateSettingsAction
specifier|private
specifier|final
name|TransportUpdateSettingsAction
name|updateSettingsAction
decl_stmt|;
DECL|field|analyzeAction
specifier|private
specifier|final
name|TransportAnalyzeAction
name|analyzeAction
decl_stmt|;
DECL|method|NodeIndicesAdminClient
annotation|@
name|Inject
specifier|public
name|NodeIndicesAdminClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportIndicesStatusAction
name|indicesStatusAction
parameter_list|,
name|TransportCreateIndexAction
name|createIndexAction
parameter_list|,
name|TransportDeleteIndexAction
name|deleteIndexAction
parameter_list|,
name|TransportCloseIndexAction
name|closeIndexAction
parameter_list|,
name|TransportOpenIndexAction
name|openIndexAction
parameter_list|,
name|TransportRefreshAction
name|refreshAction
parameter_list|,
name|TransportFlushAction
name|flushAction
parameter_list|,
name|TransportOptimizeAction
name|optimizeAction
parameter_list|,
name|TransportPutMappingAction
name|putMappingAction
parameter_list|,
name|TransportDeleteMappingAction
name|deleteMappingAction
parameter_list|,
name|TransportGatewaySnapshotAction
name|gatewaySnapshotAction
parameter_list|,
name|TransportIndicesAliasesAction
name|indicesAliasesAction
parameter_list|,
name|TransportClearIndicesCacheAction
name|clearIndicesCacheAction
parameter_list|,
name|TransportUpdateSettingsAction
name|updateSettingsAction
parameter_list|,
name|TransportAnalyzeAction
name|analyzeAction
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|indicesStatusAction
operator|=
name|indicesStatusAction
expr_stmt|;
name|this
operator|.
name|createIndexAction
operator|=
name|createIndexAction
expr_stmt|;
name|this
operator|.
name|deleteIndexAction
operator|=
name|deleteIndexAction
expr_stmt|;
name|this
operator|.
name|closeIndexAction
operator|=
name|closeIndexAction
expr_stmt|;
name|this
operator|.
name|openIndexAction
operator|=
name|openIndexAction
expr_stmt|;
name|this
operator|.
name|refreshAction
operator|=
name|refreshAction
expr_stmt|;
name|this
operator|.
name|flushAction
operator|=
name|flushAction
expr_stmt|;
name|this
operator|.
name|optimizeAction
operator|=
name|optimizeAction
expr_stmt|;
name|this
operator|.
name|deleteMappingAction
operator|=
name|deleteMappingAction
expr_stmt|;
name|this
operator|.
name|putMappingAction
operator|=
name|putMappingAction
expr_stmt|;
name|this
operator|.
name|gatewaySnapshotAction
operator|=
name|gatewaySnapshotAction
expr_stmt|;
name|this
operator|.
name|indicesAliasesAction
operator|=
name|indicesAliasesAction
expr_stmt|;
name|this
operator|.
name|clearIndicesCacheAction
operator|=
name|clearIndicesCacheAction
expr_stmt|;
name|this
operator|.
name|updateSettingsAction
operator|=
name|updateSettingsAction
expr_stmt|;
name|this
operator|.
name|analyzeAction
operator|=
name|analyzeAction
expr_stmt|;
block|}
DECL|method|threadPool
annotation|@
name|Override
specifier|public
name|ThreadPool
name|threadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
DECL|method|status
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|IndicesStatusResponse
argument_list|>
name|status
parameter_list|(
name|IndicesStatusRequest
name|request
parameter_list|)
block|{
return|return
name|indicesStatusAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|status
annotation|@
name|Override
specifier|public
name|void
name|status
parameter_list|(
name|IndicesStatusRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|IndicesStatusResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|indicesStatusAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|create
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|CreateIndexResponse
argument_list|>
name|create
parameter_list|(
name|CreateIndexRequest
name|request
parameter_list|)
block|{
return|return
name|createIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|create
annotation|@
name|Override
specifier|public
name|void
name|create
parameter_list|(
name|CreateIndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|createIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|delete
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteIndexResponse
argument_list|>
name|delete
parameter_list|(
name|DeleteIndexRequest
name|request
parameter_list|)
block|{
return|return
name|deleteIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|delete
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteIndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|deleteIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|CloseIndexResponse
argument_list|>
name|close
parameter_list|(
name|CloseIndexRequest
name|request
parameter_list|)
block|{
return|return
name|closeIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|CloseIndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|CloseIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|closeIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|open
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|OpenIndexResponse
argument_list|>
name|open
parameter_list|(
name|OpenIndexRequest
name|request
parameter_list|)
block|{
return|return
name|openIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|open
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|(
name|OpenIndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|OpenIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|openIndexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|refresh
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|RefreshResponse
argument_list|>
name|refresh
parameter_list|(
name|RefreshRequest
name|request
parameter_list|)
block|{
return|return
name|refreshAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|refresh
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|(
name|RefreshRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|RefreshResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|refreshAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|FlushResponse
argument_list|>
name|flush
parameter_list|(
name|FlushRequest
name|request
parameter_list|)
block|{
return|return
name|flushAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|flush
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|(
name|FlushRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|FlushResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|flushAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|optimize
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|OptimizeResponse
argument_list|>
name|optimize
parameter_list|(
name|OptimizeRequest
name|request
parameter_list|)
block|{
return|return
name|optimizeAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|optimize
annotation|@
name|Override
specifier|public
name|void
name|optimize
parameter_list|(
name|OptimizeRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|OptimizeResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|optimizeAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|putMapping
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|PutMappingResponse
argument_list|>
name|putMapping
parameter_list|(
name|PutMappingRequest
name|request
parameter_list|)
block|{
return|return
name|putMappingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|putMapping
annotation|@
name|Override
specifier|public
name|void
name|putMapping
parameter_list|(
name|PutMappingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PutMappingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|putMappingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteMapping
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteMappingResponse
argument_list|>
name|deleteMapping
parameter_list|(
name|DeleteMappingRequest
name|request
parameter_list|)
block|{
return|return
name|deleteMappingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|deleteMapping
annotation|@
name|Override
specifier|public
name|void
name|deleteMapping
parameter_list|(
name|DeleteMappingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteMappingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|deleteMappingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|gatewaySnapshot
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|GatewaySnapshotResponse
argument_list|>
name|gatewaySnapshot
parameter_list|(
name|GatewaySnapshotRequest
name|request
parameter_list|)
block|{
return|return
name|gatewaySnapshotAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|gatewaySnapshot
annotation|@
name|Override
specifier|public
name|void
name|gatewaySnapshot
parameter_list|(
name|GatewaySnapshotRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GatewaySnapshotResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|gatewaySnapshotAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|aliases
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|IndicesAliasesResponse
argument_list|>
name|aliases
parameter_list|(
name|IndicesAliasesRequest
name|request
parameter_list|)
block|{
return|return
name|indicesAliasesAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|aliases
annotation|@
name|Override
specifier|public
name|void
name|aliases
parameter_list|(
name|IndicesAliasesRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|IndicesAliasesResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|indicesAliasesAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|clearCache
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ClearIndicesCacheResponse
argument_list|>
name|clearCache
parameter_list|(
name|ClearIndicesCacheRequest
name|request
parameter_list|)
block|{
return|return
name|clearIndicesCacheAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|clearCache
annotation|@
name|Override
specifier|public
name|void
name|clearCache
parameter_list|(
name|ClearIndicesCacheRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClearIndicesCacheResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|clearIndicesCacheAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|updateSettings
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|UpdateSettingsResponse
argument_list|>
name|updateSettings
parameter_list|(
name|UpdateSettingsRequest
name|request
parameter_list|)
block|{
return|return
name|updateSettingsAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|updateSettings
annotation|@
name|Override
specifier|public
name|void
name|updateSettings
parameter_list|(
name|UpdateSettingsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|UpdateSettingsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|updateSettingsAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|analyze
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|AnalyzeResponse
argument_list|>
name|analyze
parameter_list|(
name|AnalyzeRequest
name|request
parameter_list|)
block|{
return|return
name|analyzeAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|analyze
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|AnalyzeResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|analyzeAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

