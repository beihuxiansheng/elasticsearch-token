begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|common
operator|.
name|collect
operator|.
name|Tuple
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
name|logging
operator|.
name|ESLogger
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
name|env
operator|.
name|NodeEnvironment
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
name|nio
operator|.
name|file
operator|.
name|FileStore
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
name|Files
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
DECL|class|ShardPath
specifier|public
specifier|final
class|class
name|ShardPath
block|{
DECL|field|INDEX_FOLDER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_FOLDER_NAME
init|=
literal|"index"
decl_stmt|;
DECL|field|TRANSLOG_FOLDER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TRANSLOG_FOLDER_NAME
init|=
literal|"translog"
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|indexUUID
specifier|private
specifier|final
name|String
name|indexUUID
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|shardStatePath
specifier|private
specifier|final
name|Path
name|shardStatePath
decl_stmt|;
DECL|method|ShardPath
specifier|public
name|ShardPath
parameter_list|(
name|Path
name|path
parameter_list|,
name|Path
name|shardStatePath
parameter_list|,
name|String
name|indexUUID
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|indexUUID
operator|=
name|indexUUID
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|shardStatePath
operator|=
name|shardStatePath
expr_stmt|;
block|}
DECL|method|resolveTranslog
specifier|public
name|Path
name|resolveTranslog
parameter_list|()
block|{
return|return
name|path
operator|.
name|resolve
argument_list|(
name|TRANSLOG_FOLDER_NAME
argument_list|)
return|;
block|}
DECL|method|resolveIndex
specifier|public
name|Path
name|resolveIndex
parameter_list|()
block|{
return|return
name|path
operator|.
name|resolve
argument_list|(
name|INDEX_FOLDER_NAME
argument_list|)
return|;
block|}
DECL|method|getDataPath
specifier|public
name|Path
name|getDataPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|getIndexUUID
specifier|public
name|String
name|getIndexUUID
parameter_list|()
block|{
return|return
name|indexUUID
return|;
block|}
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
DECL|method|getShardStatePath
specifier|public
name|Path
name|getShardStatePath
parameter_list|()
block|{
return|return
name|shardStatePath
return|;
block|}
comment|/**      * This method walks through the nodes shard paths to find the data and state path for the given shard. If multiple      * directories with a valid shard state exist the one with the highest version will be used.      *<b>Note:</b> this method resolves custom data locations for the shard.      */
DECL|method|loadShardPath
specifier|public
specifier|static
name|ShardPath
name|loadShardPath
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|NodeEnvironment
name|env
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|indexUUID
init|=
name|indexSettings
operator|.
name|get
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_UUID
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|Path
index|[]
name|paths
init|=
name|env
operator|.
name|availableShardPaths
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|Path
name|loadedPath
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|ShardStateMetaData
name|load
init|=
name|ShardStateMetaData
operator|.
name|FORMAT
operator|.
name|loadLatestState
argument_list|(
name|logger
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|load
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
name|load
operator|.
name|indexUUID
operator|.
name|equals
argument_list|(
name|indexUUID
argument_list|)
operator|||
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
operator|.
name|equals
argument_list|(
name|load
operator|.
name|indexUUID
argument_list|)
operator|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|shardId
operator|+
literal|" index UUID in shard state was: "
operator|+
name|load
operator|.
name|indexUUID
operator|+
literal|" excepted: "
operator|+
name|indexUUID
operator|+
literal|" on shard path: "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
name|loadedPath
operator|==
literal|null
condition|)
block|{
name|loadedPath
operator|=
name|path
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|shardId
operator|+
literal|" more than one shard state found"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|loadedPath
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|Path
name|dataPath
decl_stmt|;
specifier|final
name|Path
name|statePath
init|=
name|loadedPath
decl_stmt|;
if|if
condition|(
name|NodeEnvironment
operator|.
name|hasCustomDataPath
argument_list|(
name|indexSettings
argument_list|)
condition|)
block|{
name|dataPath
operator|=
name|env
operator|.
name|resolveCustomLocation
argument_list|(
name|indexSettings
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataPath
operator|=
name|statePath
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"{} loaded data path [{}], state path [{}]"
argument_list|,
name|shardId
argument_list|,
name|dataPath
argument_list|,
name|statePath
argument_list|)
expr_stmt|;
return|return
operator|new
name|ShardPath
argument_list|(
name|dataPath
argument_list|,
name|statePath
argument_list|,
name|indexUUID
argument_list|,
name|shardId
argument_list|)
return|;
block|}
block|}
comment|/** Maps each path.data path to a "guess" of how many bytes the shards allocated to that path might additionally use over their      *  lifetime; we do this so a bunch of newly allocated shards won't just all go the path with the most free space at this moment. */
DECL|method|getEstimatedReservedBytes
specifier|private
specifier|static
name|Map
argument_list|<
name|Path
argument_list|,
name|Long
argument_list|>
name|getEstimatedReservedBytes
parameter_list|(
name|NodeEnvironment
name|env
parameter_list|,
name|long
name|avgShardSizeInBytes
parameter_list|,
name|Iterable
argument_list|<
name|IndexShard
argument_list|>
name|shards
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|totFreeSpace
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeEnvironment
operator|.
name|NodePath
name|nodePath
range|:
name|env
operator|.
name|nodePaths
argument_list|()
control|)
block|{
name|totFreeSpace
operator|+=
name|nodePath
operator|.
name|fileStore
operator|.
name|getUsableSpace
argument_list|()
expr_stmt|;
block|}
comment|// Very rough heurisic of how much disk space we expect the shard will use over its lifetime, the max of current average
comment|// shard size across the cluster and 5% of the total available free space on this node:
name|long
name|estShardSizeInBytes
init|=
name|Math
operator|.
name|max
argument_list|(
name|avgShardSizeInBytes
argument_list|,
call|(
name|long
call|)
argument_list|(
name|totFreeSpace
operator|/
literal|20.0
argument_list|)
argument_list|)
decl_stmt|;
comment|// Collate predicted (guessed!) disk usage on each path.data:
name|Map
argument_list|<
name|Path
argument_list|,
name|Long
argument_list|>
name|reservedBytes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexShard
name|shard
range|:
name|shards
control|)
block|{
name|Path
name|dataPath
init|=
name|NodeEnvironment
operator|.
name|shardStatePathToDataPath
argument_list|(
name|shard
operator|.
name|shardPath
argument_list|()
operator|.
name|getShardStatePath
argument_list|()
argument_list|)
decl_stmt|;
comment|// Remove indices/<index>/<shardID> subdirs from the statePath to get back to the path.data/<lockID>:
name|Long
name|curBytes
init|=
name|reservedBytes
operator|.
name|get
argument_list|(
name|dataPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|curBytes
operator|==
literal|null
condition|)
block|{
name|curBytes
operator|=
literal|0L
expr_stmt|;
block|}
name|reservedBytes
operator|.
name|put
argument_list|(
name|dataPath
argument_list|,
name|curBytes
operator|+
name|estShardSizeInBytes
argument_list|)
expr_stmt|;
block|}
return|return
name|reservedBytes
return|;
block|}
DECL|method|selectNewPathForShard
specifier|public
specifier|static
name|ShardPath
name|selectNewPathForShard
parameter_list|(
name|NodeEnvironment
name|env
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|long
name|avgShardSizeInBytes
parameter_list|,
name|Iterable
argument_list|<
name|IndexShard
argument_list|>
name|shards
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|dataPath
decl_stmt|;
specifier|final
name|Path
name|statePath
decl_stmt|;
specifier|final
name|String
name|indexUUID
init|=
name|indexSettings
operator|.
name|get
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_UUID
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|NodeEnvironment
operator|.
name|hasCustomDataPath
argument_list|(
name|indexSettings
argument_list|)
condition|)
block|{
name|dataPath
operator|=
name|env
operator|.
name|resolveCustomLocation
argument_list|(
name|indexSettings
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|statePath
operator|=
name|env
operator|.
name|nodePaths
argument_list|()
index|[
literal|0
index|]
operator|.
name|resolve
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|Path
argument_list|,
name|Long
argument_list|>
name|estReservedBytes
init|=
name|getEstimatedReservedBytes
argument_list|(
name|env
argument_list|,
name|avgShardSizeInBytes
argument_list|,
name|shards
argument_list|)
decl_stmt|;
comment|// TODO - do we need something more extensible? Yet, this does the job for now...
specifier|final
name|NodeEnvironment
operator|.
name|NodePath
index|[]
name|paths
init|=
name|env
operator|.
name|nodePaths
argument_list|()
decl_stmt|;
name|NodeEnvironment
operator|.
name|NodePath
name|bestPath
init|=
literal|null
decl_stmt|;
name|long
name|maxUsableBytes
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|NodeEnvironment
operator|.
name|NodePath
name|nodePath
range|:
name|paths
control|)
block|{
name|FileStore
name|fileStore
init|=
name|nodePath
operator|.
name|fileStore
decl_stmt|;
name|long
name|usableBytes
init|=
name|fileStore
operator|.
name|getUsableSpace
argument_list|()
decl_stmt|;
name|Long
name|reservedBytes
init|=
name|estReservedBytes
operator|.
name|get
argument_list|(
name|nodePath
operator|.
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|reservedBytes
operator|!=
literal|null
condition|)
block|{
comment|// Deduct estimated reserved bytes from usable space:
name|usableBytes
operator|-=
name|reservedBytes
expr_stmt|;
block|}
if|if
condition|(
name|usableBytes
operator|>
name|maxUsableBytes
condition|)
block|{
name|maxUsableBytes
operator|=
name|usableBytes
expr_stmt|;
name|bestPath
operator|=
name|nodePath
expr_stmt|;
block|}
block|}
name|statePath
operator|=
name|bestPath
operator|.
name|resolve
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|dataPath
operator|=
name|statePath
expr_stmt|;
block|}
return|return
operator|new
name|ShardPath
argument_list|(
name|dataPath
argument_list|,
name|statePath
argument_list|,
name|indexUUID
argument_list|,
name|shardId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|ShardPath
name|shardPath
init|=
operator|(
name|ShardPath
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|shardId
operator|!=
literal|null
condition|?
operator|!
name|shardId
operator|.
name|equals
argument_list|(
name|shardPath
operator|.
name|shardId
argument_list|)
else|:
name|shardPath
operator|.
name|shardId
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|indexUUID
operator|!=
literal|null
condition|?
operator|!
name|indexUUID
operator|.
name|equals
argument_list|(
name|shardPath
operator|.
name|indexUUID
argument_list|)
else|:
name|shardPath
operator|.
name|indexUUID
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|path
operator|!=
literal|null
condition|?
operator|!
name|path
operator|.
name|equals
argument_list|(
name|shardPath
operator|.
name|path
argument_list|)
else|:
name|shardPath
operator|.
name|path
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|path
operator|!=
literal|null
condition|?
name|path
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|indexUUID
operator|!=
literal|null
condition|?
name|indexUUID
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|shardId
operator|!=
literal|null
condition|?
name|shardId
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
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
literal|"ShardPath{"
operator|+
literal|"path="
operator|+
name|path
operator|+
literal|", indexUUID='"
operator|+
name|indexUUID
operator|+
literal|'\''
operator|+
literal|", shard="
operator|+
name|shardId
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

