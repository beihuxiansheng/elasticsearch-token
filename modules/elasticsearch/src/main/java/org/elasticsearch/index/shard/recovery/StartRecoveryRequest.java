begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|collect
operator|.
name|Maps
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|StoreFileMetaData
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
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|StartRecoveryRequest
specifier|public
class|class
name|StartRecoveryRequest
implements|implements
name|Streamable
block|{
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
DECL|field|markAsRelocated
specifier|private
name|boolean
name|markAsRelocated
decl_stmt|;
DECL|field|existingFiles
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|existingFiles
decl_stmt|;
DECL|method|StartRecoveryRequest
name|StartRecoveryRequest
parameter_list|()
block|{     }
comment|/**      * Start recovery request.      *      * @param shardId      * @param sourceNode      The node to recover from      * @param targetNode      Teh node to recover to      * @param markAsRelocated      * @param existingFiles      */
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
name|boolean
name|markAsRelocated
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|existingFiles
parameter_list|)
block|{
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
name|markAsRelocated
operator|=
name|markAsRelocated
expr_stmt|;
name|this
operator|.
name|existingFiles
operator|=
name|existingFiles
expr_stmt|;
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
DECL|method|markAsRelocated
specifier|public
name|boolean
name|markAsRelocated
parameter_list|()
block|{
return|return
name|markAsRelocated
return|;
block|}
DECL|method|existingFiles
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|existingFiles
parameter_list|()
block|{
return|return
name|existingFiles
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|DiscoveryNode
operator|.
name|readNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|targetNode
operator|=
name|DiscoveryNode
operator|.
name|readNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|markAsRelocated
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|existingFiles
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|StoreFileMetaData
name|md
init|=
name|StoreFileMetaData
operator|.
name|readStoreFileMetaData
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|existingFiles
operator|.
name|put
argument_list|(
name|md
operator|.
name|name
argument_list|()
argument_list|,
name|md
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|markAsRelocated
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|existingFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|StoreFileMetaData
name|md
range|:
name|existingFiles
operator|.
name|values
argument_list|()
control|)
block|{
name|md
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

