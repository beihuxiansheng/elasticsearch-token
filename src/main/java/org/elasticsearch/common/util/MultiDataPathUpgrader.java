begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CheckIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|SimpleFSDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|common
operator|.
name|io
operator|.
name|FileSystemUtils
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
name|BytesStreamOutput
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
name|logging
operator|.
name|Loggers
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
name|env
operator|.
name|ShardLock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|MetaDataStateFormat
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
name|Index
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
name|*
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
name|io
operator|.
name|PrintStream
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
name|*
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
name|attribute
operator|.
name|BasicFileAttributes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|MultiDataPathUpgrader
specifier|public
class|class
name|MultiDataPathUpgrader
block|{
DECL|field|nodeEnvironment
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnvironment
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Creates a new upgrader instance      * @param nodeEnvironment the node env to operate on.      *      */
DECL|method|MultiDataPathUpgrader
specifier|public
name|MultiDataPathUpgrader
parameter_list|(
name|NodeEnvironment
name|nodeEnvironment
parameter_list|)
block|{
name|this
operator|.
name|nodeEnvironment
operator|=
name|nodeEnvironment
expr_stmt|;
block|}
comment|/**      * Upgrades the given shard Id from multiple shard paths into the given target path.      *      * @see #pickShardPath(org.elasticsearch.index.shard.ShardId)      */
DECL|method|upgrade
specifier|public
name|void
name|upgrade
parameter_list|(
name|ShardId
name|shard
parameter_list|,
name|ShardPath
name|targetPath
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
index|[]
name|paths
init|=
name|nodeEnvironment
operator|.
name|availableShardPaths
argument_list|(
name|shard
argument_list|)
decl_stmt|;
comment|// custom data path doesn't need upgrading
if|if
condition|(
name|isTargetPathConfigured
argument_list|(
name|paths
argument_list|,
name|targetPath
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shard path must be one of the shards data paths"
argument_list|)
throw|;
block|}
assert|assert
name|needsUpgrading
argument_list|(
name|shard
argument_list|)
operator|:
literal|"Should not upgrade a path that needs no upgrading"
assert|;
name|logger
operator|.
name|info
argument_list|(
literal|"{} upgrading multi data dir to {}"
argument_list|,
name|shard
argument_list|,
name|targetPath
operator|.
name|getDataPath
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ShardStateMetaData
name|loaded
init|=
name|ShardStateMetaData
operator|.
name|FORMAT
operator|.
name|loadLatestState
argument_list|(
name|logger
argument_list|,
name|paths
argument_list|)
decl_stmt|;
if|if
condition|(
name|loaded
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|shard
operator|+
literal|" no shard state found in any of: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|paths
argument_list|)
operator|+
literal|" please check and remove them if possible"
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"{} loaded shard state {}"
argument_list|,
name|shard
argument_list|,
name|loaded
argument_list|)
expr_stmt|;
name|ShardStateMetaData
operator|.
name|FORMAT
operator|.
name|write
argument_list|(
name|loaded
argument_list|,
name|loaded
operator|.
name|version
argument_list|,
name|targetPath
operator|.
name|getShardStatePath
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|targetPath
operator|.
name|resolveIndex
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|SimpleFSDirectory
name|directory
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|targetPath
operator|.
name|resolveIndex
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
specifier|final
name|Lock
name|lock
init|=
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
init|)
block|{
if|if
condition|(
name|lock
operator|.
name|obtain
argument_list|(
literal|5000
argument_list|)
condition|)
block|{
name|upgradeFiles
argument_list|(
name|shard
argument_list|,
name|targetPath
argument_list|,
name|targetPath
operator|.
name|resolveIndex
argument_list|()
argument_list|,
name|ShardPath
operator|.
name|INDEX_FOLDER_NAME
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't obtain lock on "
operator|+
name|targetPath
operator|.
name|resolveIndex
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
name|upgradeFiles
argument_list|(
name|shard
argument_list|,
name|targetPath
argument_list|,
name|targetPath
operator|.
name|resolveTranslog
argument_list|()
argument_list|,
name|ShardPath
operator|.
name|TRANSLOG_FOLDER_NAME
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{} wipe upgraded directories"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|targetPath
operator|.
name|getShardStatePath
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{} wipe shard directories: [{}]"
argument_list|,
name|shard
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|FileSystemUtils
operator|.
name|files
argument_list|(
name|targetPath
operator|.
name|resolveIndex
argument_list|()
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"index folder ["
operator|+
name|targetPath
operator|.
name|resolveIndex
argument_list|()
operator|+
literal|"] is empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|FileSystemUtils
operator|.
name|files
argument_list|(
name|targetPath
operator|.
name|resolveTranslog
argument_list|()
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"translog folder ["
operator|+
name|targetPath
operator|.
name|resolveTranslog
argument_list|()
operator|+
literal|"] is empty"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Runs check-index on the target shard and throws an exception if it failed      */
DECL|method|checkIndex
specifier|public
name|void
name|checkIndex
parameter_list|(
name|ShardPath
name|targetPath
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesStreamOutput
name|os
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|os
argument_list|,
literal|false
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Directory
name|directory
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|targetPath
operator|.
name|resolveIndex
argument_list|()
argument_list|)
init|;
name|final
name|CheckIndex
name|checkIndex
operator|=
operator|new
name|CheckIndex
argument_list|(
name|directory
argument_list|)
init|)
block|{
name|checkIndex
operator|.
name|setInfoStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|CheckIndex
operator|.
name|Status
name|status
init|=
name|checkIndex
operator|.
name|checkIndex
argument_list|()
decl_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|status
operator|.
name|clean
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"check index [failure]\n{}"
argument_list|,
operator|new
name|String
argument_list|(
name|os
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"index check failure"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Returns true iff the given shard needs upgrading.      */
DECL|method|needsUpgrading
specifier|public
name|boolean
name|needsUpgrading
parameter_list|(
name|ShardId
name|shard
parameter_list|)
block|{
specifier|final
name|Path
index|[]
name|paths
init|=
name|nodeEnvironment
operator|.
name|availableShardPaths
argument_list|(
name|shard
argument_list|)
decl_stmt|;
comment|// custom data path doesn't need upgrading neither single path envs
if|if
condition|(
name|paths
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|int
name|numPathsExist
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
name|MetaDataStateFormat
operator|.
name|STATE_DIR_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|numPathsExist
operator|++
expr_stmt|;
if|if
condition|(
name|numPathsExist
operator|>
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Picks a target ShardPath to allocate and upgrade the given shard to. It picks the target based on a simple      * heuristic:      *<ul>      *<li>if the smallest datapath has 2x more space available that the shards total size the datapath with the most bytes for that shard is picked to minimize the amount of bytes to copy</li>      *<li>otherwise the largest available datapath is used as the target no matter how big of a slice of the shard it already holds.</li>      *</ul>      */
DECL|method|pickShardPath
specifier|public
name|ShardPath
name|pickShardPath
parameter_list|(
name|ShardId
name|shard
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|needsUpgrading
argument_list|(
name|shard
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Shard doesn't need upgrading"
argument_list|)
throw|;
block|}
specifier|final
name|NodeEnvironment
operator|.
name|NodePath
index|[]
name|paths
init|=
name|nodeEnvironment
operator|.
name|nodePaths
argument_list|()
decl_stmt|;
comment|// if we need upgrading make sure we have all paths.
for|for
control|(
name|NodeEnvironment
operator|.
name|NodePath
name|path
range|:
name|paths
control|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ShardFileInfo
index|[]
name|shardFileInfo
init|=
name|getShardFileInfo
argument_list|(
name|shard
argument_list|,
name|paths
argument_list|)
decl_stmt|;
name|long
name|totalBytesUsedByShard
init|=
literal|0
decl_stmt|;
name|long
name|leastUsableSpace
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|mostUsableSpace
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
assert|assert
name|shardFileInfo
operator|.
name|length
operator|==
name|nodeEnvironment
operator|.
name|availableShardPaths
argument_list|(
name|shard
argument_list|)
operator|.
name|length
assert|;
for|for
control|(
name|ShardFileInfo
name|info
range|:
name|shardFileInfo
control|)
block|{
name|totalBytesUsedByShard
operator|+=
name|info
operator|.
name|spaceUsedByShard
expr_stmt|;
name|leastUsableSpace
operator|=
name|Math
operator|.
name|min
argument_list|(
name|leastUsableSpace
argument_list|,
name|info
operator|.
name|usableSpace
operator|+
name|info
operator|.
name|spaceUsedByShard
argument_list|)
expr_stmt|;
name|mostUsableSpace
operator|=
name|Math
operator|.
name|max
argument_list|(
name|mostUsableSpace
argument_list|,
name|info
operator|.
name|usableSpace
operator|+
name|info
operator|.
name|spaceUsedByShard
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mostUsableSpace
operator|<
name|totalBytesUsedByShard
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't upgrade path available space: "
operator|+
operator|new
name|ByteSizeValue
argument_list|(
name|mostUsableSpace
argument_list|)
operator|+
literal|" required space: "
operator|+
operator|new
name|ByteSizeValue
argument_list|(
name|totalBytesUsedByShard
argument_list|)
argument_list|)
throw|;
block|}
name|ShardFileInfo
name|target
init|=
name|shardFileInfo
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|leastUsableSpace
operator|>=
operator|(
literal|2
operator|*
name|totalBytesUsedByShard
operator|)
condition|)
block|{
for|for
control|(
name|ShardFileInfo
name|info
range|:
name|shardFileInfo
control|)
block|{
if|if
condition|(
name|info
operator|.
name|spaceUsedByShard
operator|>
name|target
operator|.
name|spaceUsedByShard
condition|)
block|{
name|target
operator|=
name|info
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|ShardFileInfo
name|info
range|:
name|shardFileInfo
control|)
block|{
if|if
condition|(
name|info
operator|.
name|usableSpace
operator|>
name|target
operator|.
name|usableSpace
condition|)
block|{
name|target
operator|=
name|info
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|ShardPath
argument_list|(
name|target
operator|.
name|path
argument_list|,
name|target
operator|.
name|path
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
comment|/* we don't know */
argument_list|,
name|shard
argument_list|)
return|;
block|}
DECL|method|getShardFileInfo
specifier|private
name|ShardFileInfo
index|[]
name|getShardFileInfo
parameter_list|(
name|ShardId
name|shard
parameter_list|,
name|NodeEnvironment
operator|.
name|NodePath
index|[]
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ShardFileInfo
index|[]
name|info
init|=
operator|new
name|ShardFileInfo
index|[
name|paths
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|info
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|path
init|=
name|paths
index|[
name|i
index|]
operator|.
name|resolve
argument_list|(
name|shard
argument_list|)
decl_stmt|;
specifier|final
name|long
name|usabelSpace
init|=
name|getUsabelSpace
argument_list|(
name|paths
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|info
index|[
name|i
index|]
operator|=
operator|new
name|ShardFileInfo
argument_list|(
name|path
argument_list|,
name|usabelSpace
argument_list|,
name|getSpaceUsedByShard
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
DECL|method|getSpaceUsedByShard
specifier|protected
name|long
name|getSpaceUsedByShard
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
index|[]
name|spaceUsedByShard
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|}
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|path
argument_list|,
operator|new
name|FileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|preVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|attrs
operator|.
name|isRegularFile
argument_list|()
condition|)
block|{
name|spaceUsedByShard
index|[
literal|0
index|]
operator|+=
name|attrs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFileFailed
parameter_list|(
name|Path
name|file
parameter_list|,
name|IOException
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|postVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|IOException
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|spaceUsedByShard
index|[
literal|0
index|]
return|;
block|}
DECL|method|getUsabelSpace
specifier|protected
name|long
name|getUsabelSpace
parameter_list|(
name|NodeEnvironment
operator|.
name|NodePath
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStore
name|fileStore
init|=
name|path
operator|.
name|fileStore
decl_stmt|;
return|return
name|fileStore
operator|.
name|getUsableSpace
argument_list|()
return|;
block|}
DECL|class|ShardFileInfo
specifier|static
class|class
name|ShardFileInfo
block|{
DECL|field|path
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|usableSpace
specifier|final
name|long
name|usableSpace
decl_stmt|;
DECL|field|spaceUsedByShard
specifier|final
name|long
name|spaceUsedByShard
decl_stmt|;
DECL|method|ShardFileInfo
name|ShardFileInfo
parameter_list|(
name|Path
name|path
parameter_list|,
name|long
name|usableSpace
parameter_list|,
name|long
name|spaceUsedByShard
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
name|usableSpace
operator|=
name|usableSpace
expr_stmt|;
name|this
operator|.
name|spaceUsedByShard
operator|=
name|spaceUsedByShard
expr_stmt|;
block|}
block|}
DECL|method|upgradeFiles
specifier|private
name|void
name|upgradeFiles
parameter_list|(
name|ShardId
name|shard
parameter_list|,
name|ShardPath
name|targetPath
parameter_list|,
specifier|final
name|Path
name|targetDir
parameter_list|,
name|String
name|folderName
parameter_list|,
name|Path
index|[]
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|movedFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|targetPath
operator|.
name|getDataPath
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
specifier|final
name|Path
name|sourceDir
init|=
name|path
operator|.
name|resolve
argument_list|(
name|folderName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|sourceDir
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{} upgrading [{}] from [{}] to [{}]"
argument_list|,
name|shard
argument_list|,
name|folderName
argument_list|,
name|sourceDir
argument_list|,
name|targetDir
argument_list|)
expr_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|sourceDir
argument_list|)
init|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|targetDir
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|file
range|:
name|stream
control|)
block|{
if|if
condition|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
operator|.
name|equals
argument_list|(
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|||
name|Files
operator|.
name|isDirectory
argument_list|(
name|file
argument_list|)
condition|)
block|{
continue|continue;
comment|// skip write.lock
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"{} move file [{}] size: [{}]"
argument_list|,
name|shard
argument_list|,
name|file
operator|.
name|getFileName
argument_list|()
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|targetFile
init|=
name|targetDir
operator|.
name|resolve
argument_list|(
name|file
operator|.
name|getFileName
argument_list|()
argument_list|)
decl_stmt|;
comment|/* We are pessimistic and do a copy first to the other path and then and atomic move to rename it such that                                in the worst case the file exists twice but is never lost or half written.*/
specifier|final
name|Path
name|targetTempFile
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|targetDir
argument_list|,
literal|"upgrade_"
argument_list|,
literal|"_"
operator|+
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|file
argument_list|,
name|targetTempFile
argument_list|,
name|StandardCopyOption
operator|.
name|COPY_ATTRIBUTES
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|Files
operator|.
name|move
argument_list|(
name|targetTempFile
argument_list|,
name|targetFile
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
comment|// we are on the same FS - this must work otherwise all bets are off
name|Files
operator|.
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|movedFiles
operator|.
name|add
argument_list|(
name|targetFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|movedFiles
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// fsync later it might be on disk already
name|logger
operator|.
name|info
argument_list|(
literal|"{} fsync files"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|moved
range|:
name|movedFiles
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{} syncing [{}]"
argument_list|,
name|shard
argument_list|,
name|moved
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|fsync
argument_list|(
name|moved
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"{} syncing directory [{}]"
argument_list|,
name|shard
argument_list|,
name|targetDir
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|fsync
argument_list|(
name|targetDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns<code>true</code> iff the target path is one of the given paths.      */
DECL|method|isTargetPathConfigured
specifier|private
name|boolean
name|isTargetPathConfigured
parameter_list|(
specifier|final
name|Path
index|[]
name|paths
parameter_list|,
name|ShardPath
name|targetPath
parameter_list|)
block|{
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|targetPath
operator|.
name|getDataPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Runs an upgrade on all shards located under the given node environment if there is more than 1 data.path configured      * otherwise this method will return immediately.      */
DECL|method|upgradeMultiDataPath
specifier|public
specifier|static
name|void
name|upgradeMultiDataPath
parameter_list|(
name|NodeEnvironment
name|nodeEnv
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nodeEnv
operator|.
name|nodeDataPaths
argument_list|()
operator|.
name|length
operator|>
literal|1
condition|)
block|{
specifier|final
name|MultiDataPathUpgrader
name|upgrader
init|=
operator|new
name|MultiDataPathUpgrader
argument_list|(
name|nodeEnv
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allIndices
init|=
name|nodeEnv
operator|.
name|findAllIndices
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|allIndices
control|)
block|{
for|for
control|(
name|ShardId
name|shardId
range|:
name|findAllShardIds
argument_list|(
name|nodeEnv
operator|.
name|indexPaths
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
control|)
block|{
try|try
init|(
name|ShardLock
name|lock
init|=
name|nodeEnv
operator|.
name|shardLock
argument_list|(
name|shardId
argument_list|,
literal|0
argument_list|)
init|)
block|{
if|if
condition|(
name|upgrader
operator|.
name|needsUpgrading
argument_list|(
name|shardId
argument_list|)
condition|)
block|{
specifier|final
name|ShardPath
name|shardPath
init|=
name|upgrader
operator|.
name|pickShardPath
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|upgrader
operator|.
name|upgrade
argument_list|(
name|shardId
argument_list|,
name|shardPath
argument_list|)
expr_stmt|;
comment|// we have to check if the index path exists since we might
comment|// have only upgraded the shard state that is written under /indexname/shardid/_state
comment|// in the case we upgraded a dedicated index directory index
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|shardPath
operator|.
name|resolveIndex
argument_list|()
argument_list|)
condition|)
block|{
name|upgrader
operator|.
name|checkIndex
argument_list|(
name|shardPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"{} no upgrade needed - already upgraded"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|findAllShardIds
specifier|private
specifier|static
name|Set
argument_list|<
name|ShardId
argument_list|>
name|findAllShardIds
parameter_list|(
name|Path
modifier|...
name|locations
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Set
argument_list|<
name|ShardId
argument_list|>
name|shardIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|location
range|:
name|locations
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|location
argument_list|)
condition|)
block|{
name|shardIds
operator|.
name|addAll
argument_list|(
name|findAllShardsForIndex
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|shardIds
return|;
block|}
DECL|method|findAllShardsForIndex
specifier|private
specifier|static
name|Set
argument_list|<
name|ShardId
argument_list|>
name|findAllShardsForIndex
parameter_list|(
name|Path
name|indexPath
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|ShardId
argument_list|>
name|shardIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|indexPath
argument_list|)
condition|)
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|indexPath
argument_list|)
init|)
block|{
name|String
name|currentIndex
init|=
name|indexPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|shardPath
range|:
name|stream
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|shardPath
argument_list|)
condition|)
block|{
name|Integer
name|shardId
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|shardPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardId
operator|!=
literal|null
condition|)
block|{
name|ShardId
name|id
init|=
operator|new
name|ShardId
argument_list|(
name|currentIndex
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|shardIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|shardIds
return|;
block|}
block|}
end_class

end_unit

