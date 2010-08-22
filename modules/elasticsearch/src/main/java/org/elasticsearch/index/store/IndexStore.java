begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
package|;
end_package

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
name|common
operator|.
name|unit
operator|.
name|ByteSizeValue
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
name|IndexComponent
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
name|Iterator
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
comment|/**  * Index store is an index level information of the {@link Store} each shard will use.  *  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|IndexStore
specifier|public
interface|interface
name|IndexStore
extends|extends
name|IndexComponent
block|{
comment|/**      * Is the store a persistent store that can survive full restarts.      */
DECL|method|persistent
name|boolean
name|persistent
parameter_list|()
function_decl|;
comment|/**      * The shard store class that should be used for each shard.      */
DECL|method|shardStoreClass
name|Class
argument_list|<
name|?
extends|extends
name|Store
argument_list|>
name|shardStoreClass
parameter_list|()
function_decl|;
comment|/**      * Returns the backing store total space. Return<tt>-1</tt> if not available.      */
DECL|method|backingStoreTotalSpace
name|ByteSizeValue
name|backingStoreTotalSpace
parameter_list|()
function_decl|;
comment|/**      * Returns the backing store free space. Return<tt>-1</tt> if not available.      */
DECL|method|backingStoreFreeSpace
name|ByteSizeValue
name|backingStoreFreeSpace
parameter_list|()
function_decl|;
DECL|method|deleteUnallocated
name|void
name|deleteUnallocated
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Lists the store files metadata for a shard. Note, this should be able to list also      * metadata for shards that are no allocated as well.      */
DECL|method|listStoreMetaData
name|StoreFilesMetaData
name|listStoreMetaData
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|class|StoreFilesMetaData
specifier|static
class|class
name|StoreFilesMetaData
implements|implements
name|Iterable
argument_list|<
name|StoreFileMetaData
argument_list|>
implements|,
name|Streamable
block|{
DECL|field|allocated
specifier|private
name|boolean
name|allocated
decl_stmt|;
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
DECL|field|files
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|files
decl_stmt|;
DECL|method|StoreFilesMetaData
name|StoreFilesMetaData
parameter_list|()
block|{         }
DECL|method|StoreFilesMetaData
specifier|public
name|StoreFilesMetaData
parameter_list|(
name|boolean
name|allocated
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|files
parameter_list|)
block|{
name|this
operator|.
name|allocated
operator|=
name|allocated
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
block|}
DECL|method|allocated
specifier|public
name|boolean
name|allocated
parameter_list|()
block|{
return|return
name|allocated
return|;
block|}
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
DECL|method|totalSizeInBytes
specifier|public
name|long
name|totalSizeInBytes
parameter_list|()
block|{
name|long
name|totalSizeInBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StoreFileMetaData
name|file
range|:
name|this
control|)
block|{
name|totalSizeInBytes
operator|+=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
return|return
name|totalSizeInBytes
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StoreFileMetaData
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|files
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|files
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|file
specifier|public
name|StoreFileMetaData
name|file
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|readStoreFilesMetaData
specifier|public
specifier|static
name|StoreFilesMetaData
name|readStoreFilesMetaData
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|StoreFilesMetaData
name|md
init|=
operator|new
name|StoreFilesMetaData
argument_list|()
decl_stmt|;
name|md
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|md
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
name|allocated
operator|=
name|in
operator|.
name|readBoolean
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|files
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
name|files
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|allocated
argument_list|)
expr_stmt|;
name|shardId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|files
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
name|files
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
block|}
end_interface

end_unit

