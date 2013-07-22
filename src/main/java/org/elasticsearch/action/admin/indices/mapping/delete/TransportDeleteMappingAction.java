begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.delete
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
name|deletebyquery
operator|.
name|DeleteByQueryResponse
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
name|deletebyquery
operator|.
name|TransportDeleteByQueryAction
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
name|client
operator|.
name|Requests
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
name|MetaDataMappingService
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
name|index
operator|.
name|query
operator|.
name|FilterBuilders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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

begin_comment
comment|/**  * Delete mapping action.  */
end_comment

begin_class
DECL|class|TransportDeleteMappingAction
specifier|public
class|class
name|TransportDeleteMappingAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|DeleteMappingRequest
argument_list|,
name|DeleteMappingResponse
argument_list|>
block|{
DECL|field|metaDataMappingService
specifier|private
specifier|final
name|MetaDataMappingService
name|metaDataMappingService
decl_stmt|;
DECL|field|flushAction
specifier|private
specifier|final
name|TransportFlushAction
name|flushAction
decl_stmt|;
DECL|field|deleteByQueryAction
specifier|private
specifier|final
name|TransportDeleteByQueryAction
name|deleteByQueryAction
decl_stmt|;
DECL|field|refreshAction
specifier|private
specifier|final
name|TransportRefreshAction
name|refreshAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportDeleteMappingAction
specifier|public
name|TransportDeleteMappingAction
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
name|MetaDataMappingService
name|metaDataMappingService
parameter_list|,
name|TransportDeleteByQueryAction
name|deleteByQueryAction
parameter_list|,
name|TransportRefreshAction
name|refreshAction
parameter_list|,
name|TransportFlushAction
name|flushAction
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
name|metaDataMappingService
operator|=
name|metaDataMappingService
expr_stmt|;
name|this
operator|.
name|deleteByQueryAction
operator|=
name|deleteByQueryAction
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
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
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
name|DeleteMappingAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|DeleteMappingRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|DeleteMappingRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|DeleteMappingResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|DeleteMappingResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
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
comment|// update to concrete indices
name|request
operator|.
name|indices
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
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
name|super
operator|.
name|doExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|DeleteMappingRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indicesBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|DeleteMappingResponse
name|masterOperation
parameter_list|(
specifier|final
name|DeleteMappingRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|failureRef
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|flushAction
operator|.
name|execute
argument_list|(
name|Requests
operator|.
name|flushRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|FlushResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|FlushResponse
name|flushResponse
parameter_list|)
block|{
name|deleteByQueryAction
operator|.
name|execute
argument_list|(
name|Requests
operator|.
name|deleteByQueryRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|filteredQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|,
name|FilterBuilders
operator|.
name|typeFilter
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|DeleteByQueryResponse
name|deleteByQueryResponse
parameter_list|)
block|{
name|refreshAction
operator|.
name|execute
argument_list|(
name|Requests
operator|.
name|refreshRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|RefreshResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|RefreshResponse
name|refreshResponse
parameter_list|)
block|{
name|metaDataMappingService
operator|.
name|removeMapping
argument_list|(
operator|new
name|MetaDataMappingService
operator|.
name|RemoveRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|masterTimeout
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|,
operator|new
name|MetaDataMappingService
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MetaDataMappingService
operator|.
name|Response
name|response
parameter_list|)
block|{
name|latch
operator|.
name|countDown
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
name|t
parameter_list|)
block|{
name|failureRef
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
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
name|e
parameter_list|)
block|{
name|metaDataMappingService
operator|.
name|removeMapping
argument_list|(
operator|new
name|MetaDataMappingService
operator|.
name|RemoveRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|masterTimeout
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|,
operator|new
name|MetaDataMappingService
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MetaDataMappingService
operator|.
name|Response
name|response
parameter_list|)
block|{
name|latch
operator|.
name|countDown
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
name|t
parameter_list|)
block|{
name|failureRef
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
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
name|e
parameter_list|)
block|{
name|failureRef
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
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
name|e
parameter_list|)
block|{
name|failureRef
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|failureRef
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failureRef
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|failureRef
operator|.
name|get
argument_list|()
operator|instanceof
name|ElasticSearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticSearchException
operator|)
name|failureRef
operator|.
name|get
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
name|failureRef
operator|.
name|get
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|failureRef
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|DeleteMappingResponse
argument_list|()
return|;
block|}
block|}
end_class

end_unit

