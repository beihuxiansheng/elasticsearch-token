begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
package|;
end_package

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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|store
operator|.
name|Store
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
name|TransportRequest
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StartRecoveryRequest
specifier|public
class|class
name|StartRecoveryRequest
extends|extends
name|TransportRequest
block|{
DECL|field|recoveryId
specifier|private
name|long
name|recoveryId
decl_stmt|;
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
DECL|field|sourceNode
specifier|private
name|DiscoveryNode
name|sourceNode
decl_stmt|;
DECL|field|targetNode
specifier|private
name|DiscoveryNode
name|targetNode
decl_stmt|;
DECL|field|metadataSnapshot
specifier|private
name|Store
operator|.
name|MetadataSnapshot
name|metadataSnapshot
decl_stmt|;
DECL|field|recoveryType
specifier|private
name|RecoveryState
operator|.
name|Type
name|recoveryType
decl_stmt|;
DECL|method|StartRecoveryRequest
specifier|public
name|StartRecoveryRequest
parameter_list|()
block|{     }
comment|/**      * Start recovery request.      *      * @param sourceNode       The node to recover from      * @param targetNode       The node to recover to      */
DECL|method|StartRecoveryRequest
specifier|public
name|StartRecoveryRequest
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|DiscoveryNode
name|sourceNode
parameter_list|,
name|DiscoveryNode
name|targetNode
parameter_list|,
name|Store
operator|.
name|MetadataSnapshot
name|metadataSnapshot
parameter_list|,
name|RecoveryState
operator|.
name|Type
name|recoveryType
parameter_list|,
name|long
name|recoveryId
parameter_list|)
block|{
name|this
operator|.
name|recoveryId
operator|=
name|recoveryId
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|sourceNode
operator|=
name|sourceNode
expr_stmt|;
name|this
operator|.
name|targetNode
operator|=
name|targetNode
expr_stmt|;
name|this
operator|.
name|recoveryType
operator|=
name|recoveryType
expr_stmt|;
name|this
operator|.
name|metadataSnapshot
operator|=
name|metadataSnapshot
expr_stmt|;
block|}
DECL|method|recoveryId
specifier|public
name|long
name|recoveryId
parameter_list|()
block|{
return|return
name|this
operator|.
name|recoveryId
return|;
block|}
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|sourceNode
specifier|public
name|DiscoveryNode
name|sourceNode
parameter_list|()
block|{
return|return
name|sourceNode
return|;
block|}
DECL|method|targetNode
specifier|public
name|DiscoveryNode
name|targetNode
parameter_list|()
block|{
return|return
name|targetNode
return|;
block|}
DECL|method|recoveryType
specifier|public
name|RecoveryState
operator|.
name|Type
name|recoveryType
parameter_list|()
block|{
return|return
name|recoveryType
return|;
block|}
DECL|method|metadataSnapshot
specifier|public
name|Store
operator|.
name|MetadataSnapshot
name|metadataSnapshot
parameter_list|()
block|{
return|return
name|metadataSnapshot
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
name|recoveryId
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|shardId
operator|=
name|ShardId
operator|.
name|readShardId
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|sourceNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|targetNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|metadataSnapshot
operator|=
operator|new
name|Store
operator|.
name|MetadataSnapshot
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|recoveryType
operator|=
name|RecoveryState
operator|.
name|Type
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
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
name|out
operator|.
name|writeLong
argument_list|(
name|recoveryId
argument_list|)
expr_stmt|;
name|shardId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|sourceNode
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|targetNode
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|metadataSnapshot
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|recoveryType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

