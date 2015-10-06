begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
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
name|Nullable
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|util
operator|.
name|BigArrays
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
name|settings
operator|.
name|IndexSettings
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
name|translog
operator|.
name|Translog
operator|.
name|TranslogGeneration
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/*  * Holds all the configuration that is used to create a {@link Translog}.  * Once {@link Translog} has been created with this object, changes to this  * object will affect the {@link Translog} instance.  */
end_comment

begin_class
DECL|class|TranslogConfig
specifier|public
specifier|final
class|class
name|TranslogConfig
block|{
DECL|field|INDEX_TRANSLOG_DURABILITY
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_TRANSLOG_DURABILITY
init|=
literal|"index.translog.durability"
decl_stmt|;
DECL|field|INDEX_TRANSLOG_FS_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_TRANSLOG_FS_TYPE
init|=
literal|"index.translog.fs.type"
decl_stmt|;
DECL|field|INDEX_TRANSLOG_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_TRANSLOG_BUFFER_SIZE
init|=
literal|"index.translog.fs.buffer_size"
decl_stmt|;
DECL|field|INDEX_TRANSLOG_SYNC_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_TRANSLOG_SYNC_INTERVAL
init|=
literal|"index.translog.sync_interval"
decl_stmt|;
DECL|field|DEFAULT_SHARD_TRANSLOG_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|ByteSizeValue
name|DEFAULT_SHARD_TRANSLOG_BUFFER_SIZE
init|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"64k"
argument_list|,
name|INDEX_TRANSLOG_BUFFER_SIZE
argument_list|)
decl_stmt|;
DECL|field|syncInterval
specifier|private
specifier|final
name|TimeValue
name|syncInterval
decl_stmt|;
DECL|field|bigArrays
specifier|private
specifier|final
name|BigArrays
name|bigArrays
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|syncOnEachOperation
specifier|private
specifier|final
name|boolean
name|syncOnEachOperation
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|volatile
name|int
name|bufferSize
decl_stmt|;
DECL|field|translogGeneration
specifier|private
specifier|volatile
name|TranslogGeneration
name|translogGeneration
decl_stmt|;
DECL|field|durabilty
specifier|private
specifier|volatile
name|Translog
operator|.
name|Durabilty
name|durabilty
init|=
name|Translog
operator|.
name|Durabilty
operator|.
name|REQUEST
decl_stmt|;
DECL|field|type
specifier|private
specifier|volatile
name|TranslogWriter
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|translogPath
specifier|private
specifier|final
name|Path
name|translogPath
decl_stmt|;
comment|/**      * Creates a new TranslogConfig instance      * @param shardId the shard ID this translog belongs to      * @param translogPath the path to use for the transaction log files      * @param indexSettings the index settings used to set internal variables      * @param durabilty the default durability setting for the translog      * @param bigArrays a bigArrays instance used for temporarily allocating write operations      * @param threadPool a {@link ThreadPool} to schedule async sync durability      */
DECL|method|TranslogConfig
specifier|public
name|TranslogConfig
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Path
name|translogPath
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Translog
operator|.
name|Durabilty
name|durabilty
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
annotation|@
name|Nullable
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|translogPath
operator|=
name|translogPath
expr_stmt|;
name|this
operator|.
name|durabilty
operator|=
name|durabilty
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|bigArrays
operator|=
name|bigArrays
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|TranslogWriter
operator|.
name|Type
operator|.
name|fromString
argument_list|(
name|indexSettings
operator|.
name|get
argument_list|(
name|INDEX_TRANSLOG_FS_TYPE
argument_list|,
name|TranslogWriter
operator|.
name|Type
operator|.
name|BUFFERED
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
operator|(
name|int
operator|)
name|indexSettings
operator|.
name|getAsBytesSize
argument_list|(
name|INDEX_TRANSLOG_BUFFER_SIZE
argument_list|,
name|DEFAULT_SHARD_TRANSLOG_BUFFER_SIZE
argument_list|)
operator|.
name|bytes
argument_list|()
expr_stmt|;
comment|// Not really interesting, updated by IndexingMemoryController...
name|syncInterval
operator|=
name|indexSettings
operator|.
name|getAsTime
argument_list|(
name|INDEX_TRANSLOG_SYNC_INTERVAL
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|syncInterval
operator|.
name|millis
argument_list|()
operator|>
literal|0
operator|&&
name|threadPool
operator|!=
literal|null
condition|)
block|{
name|syncOnEachOperation
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|syncInterval
operator|.
name|millis
argument_list|()
operator|==
literal|0
condition|)
block|{
name|syncOnEachOperation
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|syncOnEachOperation
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      * Returns a {@link ThreadPool} to schedule async durability operations      */
DECL|method|getThreadPool
specifier|public
name|ThreadPool
name|getThreadPool
parameter_list|()
block|{
return|return
name|threadPool
return|;
block|}
comment|/**      * Returns the current durability mode of this translog.      */
DECL|method|getDurabilty
specifier|public
name|Translog
operator|.
name|Durabilty
name|getDurabilty
parameter_list|()
block|{
return|return
name|durabilty
return|;
block|}
comment|/**      * Sets the current durability mode for the translog.      */
DECL|method|setDurabilty
specifier|public
name|void
name|setDurabilty
parameter_list|(
name|Translog
operator|.
name|Durabilty
name|durabilty
parameter_list|)
block|{
name|this
operator|.
name|durabilty
operator|=
name|durabilty
expr_stmt|;
block|}
comment|/**      * Returns the translog type      */
DECL|method|getType
specifier|public
name|TranslogWriter
operator|.
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Sets the TranslogType for this Translog. The change will affect all subsequent translog files.      */
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|TranslogWriter
operator|.
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Returns<code>true</code> iff each low level operation shoudl be fsynced      */
DECL|method|isSyncOnEachOperation
specifier|public
name|boolean
name|isSyncOnEachOperation
parameter_list|()
block|{
return|return
name|syncOnEachOperation
return|;
block|}
comment|/**      * Retruns the current translog buffer size.      */
DECL|method|getBufferSize
specifier|public
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|bufferSize
return|;
block|}
comment|/**      * Sets the current buffer size - for setting a live setting use {@link Translog#updateBuffer(ByteSizeValue)}      */
DECL|method|setBufferSize
specifier|public
name|void
name|setBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
block|}
comment|/**      * Returns the current async fsync interval      */
DECL|method|getSyncInterval
specifier|public
name|TimeValue
name|getSyncInterval
parameter_list|()
block|{
return|return
name|syncInterval
return|;
block|}
comment|/**      * Returns the current index settings      */
DECL|method|getIndexSettings
specifier|public
name|Settings
name|getIndexSettings
parameter_list|()
block|{
return|return
name|indexSettings
return|;
block|}
comment|/**      * Returns the shard ID this config is created for      */
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
comment|/**      * Returns a BigArrays instance for this engine      */
DECL|method|getBigArrays
specifier|public
name|BigArrays
name|getBigArrays
parameter_list|()
block|{
return|return
name|bigArrays
return|;
block|}
comment|/**      * Returns the translog path for this engine      */
DECL|method|getTranslogPath
specifier|public
name|Path
name|getTranslogPath
parameter_list|()
block|{
return|return
name|translogPath
return|;
block|}
comment|/**      * Returns the translog generation to open. If this is<code>null</code> a new translog is created. If non-null      * the translog tries to open the given translog generation. The generation is treated as the last generation referenced      * form already committed data. This means all operations that have not yet been committed should be in the translog      * file referenced by this generation. The translog creation will fail if this generation can't be opened.      */
DECL|method|getTranslogGeneration
specifier|public
name|TranslogGeneration
name|getTranslogGeneration
parameter_list|()
block|{
return|return
name|translogGeneration
return|;
block|}
comment|/**      * Set the generation to be opened. Use<code>null</code> to start with a fresh translog.      * @see #getTranslogGeneration()      */
DECL|method|setTranslogGeneration
specifier|public
name|void
name|setTranslogGeneration
parameter_list|(
name|TranslogGeneration
name|translogGeneration
parameter_list|)
block|{
name|this
operator|.
name|translogGeneration
operator|=
name|translogGeneration
expr_stmt|;
block|}
block|}
end_class

end_unit

