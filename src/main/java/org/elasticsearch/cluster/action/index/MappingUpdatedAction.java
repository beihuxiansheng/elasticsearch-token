begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|action
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|ActionRequestValidationException
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
name|support
operator|.
name|master
operator|.
name|MasterNodeOperationRequest
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
name|ack
operator|.
name|ClusterStateUpdateListener
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
name|ack
operator|.
name|ClusterStateUpdateResponse
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
name|compress
operator|.
name|CompressedString
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Called by shards in the cluster when their mapping was dynamically updated and it needs to be updated  * in the cluster state meta data (and broadcast to all members).  */
end_comment

begin_class
DECL|class|MappingUpdatedAction
specifier|public
class|class
name|MappingUpdatedAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|MappingUpdatedAction
operator|.
name|MappingUpdatedRequest
argument_list|,
name|MappingUpdatedAction
operator|.
name|MappingUpdatedResponse
argument_list|>
block|{
DECL|field|mappingUpdateOrderGen
specifier|private
specifier|final
name|AtomicLong
name|mappingUpdateOrderGen
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|metaDataMappingService
specifier|private
specifier|final
name|MetaDataMappingService
name|metaDataMappingService
decl_stmt|;
annotation|@
name|Inject
DECL|method|MappingUpdatedAction
specifier|public
name|MappingUpdatedAction
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
block|}
DECL|method|generateNextMappingUpdateOrder
specifier|public
name|long
name|generateNextMappingUpdateOrder
parameter_list|()
block|{
return|return
name|mappingUpdateOrderGen
operator|.
name|incrementAndGet
argument_list|()
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
literal|"cluster/mappingUpdated"
return|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
comment|// we go async right away
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
DECL|method|newRequest
specifier|protected
name|MappingUpdatedRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|MappingUpdatedRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|MappingUpdatedResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|MappingUpdatedResponse
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
name|MappingUpdatedRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|MappingUpdatedResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|metaDataMappingService
operator|.
name|updateMapping
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|indexUUID
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|mappingSource
argument_list|()
argument_list|,
name|request
operator|.
name|order
argument_list|,
name|request
operator|.
name|nodeId
argument_list|,
operator|new
name|ClusterStateUpdateListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClusterStateUpdateResponse
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|MappingUpdatedResponse
argument_list|()
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
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] update-mapping [{}] failed to dynamically update the mapping in cluster_state from shard"
argument_list|,
name|t
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|MappingUpdatedResponse
specifier|public
specifier|static
class|class
name|MappingUpdatedResponse
extends|extends
name|ActionResponse
block|{
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
block|}
block|}
DECL|class|MappingUpdatedRequest
specifier|public
specifier|static
class|class
name|MappingUpdatedRequest
extends|extends
name|MasterNodeOperationRequest
argument_list|<
name|MappingUpdatedRequest
argument_list|>
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|indexUUID
specifier|private
name|String
name|indexUUID
init|=
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|mappingSource
specifier|private
name|CompressedString
name|mappingSource
decl_stmt|;
DECL|field|order
specifier|private
name|long
name|order
init|=
operator|-
literal|1
decl_stmt|;
comment|// -1 means not set...
DECL|field|nodeId
specifier|private
name|String
name|nodeId
init|=
literal|null
decl_stmt|;
comment|// null means not set
DECL|method|MappingUpdatedRequest
name|MappingUpdatedRequest
parameter_list|()
block|{         }
DECL|method|MappingUpdatedRequest
specifier|public
name|MappingUpdatedRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|indexUUID
parameter_list|,
name|String
name|type
parameter_list|,
name|CompressedString
name|mappingSource
parameter_list|,
name|long
name|order
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexUUID
operator|=
name|indexUUID
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|mappingSource
operator|=
name|mappingSource
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|indexUUID
specifier|public
name|String
name|indexUUID
parameter_list|()
block|{
return|return
name|indexUUID
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|mappingSource
specifier|public
name|CompressedString
name|mappingSource
parameter_list|()
block|{
return|return
name|mappingSource
return|;
block|}
comment|/**          * Returns -1 if not set...          */
DECL|method|order
specifier|public
name|long
name|order
parameter_list|()
block|{
return|return
name|this
operator|.
name|order
return|;
block|}
comment|/**          * Returns null for not set.          */
DECL|method|nodeId
specifier|public
name|String
name|nodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
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
name|index
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|type
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|mappingSource
operator|=
name|CompressedString
operator|.
name|readCompressedString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|indexUUID
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|order
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|nodeId
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
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
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|mappingSource
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|indexUUID
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|order
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"index ["
operator|+
name|index
operator|+
literal|"], indexUUID ["
operator|+
name|indexUUID
operator|+
literal|"], type ["
operator|+
name|type
operator|+
literal|"] and source ["
operator|+
name|mappingSource
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

