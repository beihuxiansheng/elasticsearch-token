begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|*
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
name|Constants
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
name|metrics
operator|.
name|CounterMetric
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
name|util
operator|.
name|set
operator|.
name|Sets
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
name|ShardPath
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FsDirectoryService
specifier|public
class|class
name|FsDirectoryService
extends|extends
name|DirectoryService
implements|implements
name|StoreRateLimiting
operator|.
name|Listener
implements|,
name|StoreRateLimiting
operator|.
name|Provider
block|{
DECL|field|indexStore
specifier|protected
specifier|final
name|IndexStore
name|indexStore
decl_stmt|;
DECL|field|rateLimitingTimeInNanos
specifier|private
specifier|final
name|CounterMetric
name|rateLimitingTimeInNanos
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|ShardPath
name|path
decl_stmt|;
annotation|@
name|Inject
DECL|method|FsDirectoryService
specifier|public
name|FsDirectoryService
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|IndexStore
name|indexStore
parameter_list|,
name|ShardPath
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
operator|.
name|getShardId
argument_list|()
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|indexStore
operator|=
name|indexStore
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|throttleTimeInNanos
specifier|public
name|long
name|throttleTimeInNanos
parameter_list|()
block|{
return|return
name|rateLimitingTimeInNanos
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rateLimiting
specifier|public
name|StoreRateLimiting
name|rateLimiting
parameter_list|()
block|{
return|return
name|indexStore
operator|.
name|rateLimiting
argument_list|()
return|;
block|}
DECL|method|buildLockFactory
specifier|public
specifier|static
name|LockFactory
name|buildLockFactory
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
name|String
name|fsLock
init|=
name|indexSettings
operator|.
name|get
argument_list|(
literal|"index.store.fs.lock"
argument_list|,
name|indexSettings
operator|.
name|get
argument_list|(
literal|"index.store.fs.fs_lock"
argument_list|,
literal|"native"
argument_list|)
argument_list|)
decl_stmt|;
name|LockFactory
name|lockFactory
decl_stmt|;
if|if
condition|(
name|fsLock
operator|.
name|equals
argument_list|(
literal|"native"
argument_list|)
condition|)
block|{
name|lockFactory
operator|=
name|NativeFSLockFactory
operator|.
name|INSTANCE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fsLock
operator|.
name|equals
argument_list|(
literal|"simple"
argument_list|)
condition|)
block|{
name|lockFactory
operator|=
name|SimpleFSLockFactory
operator|.
name|INSTANCE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unrecognized fs_lock \""
operator|+
name|fsLock
operator|+
literal|"\": must be native or simple"
argument_list|)
throw|;
block|}
return|return
name|lockFactory
return|;
block|}
DECL|method|buildLockFactory
specifier|protected
specifier|final
name|LockFactory
name|buildLockFactory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|buildLockFactory
argument_list|(
name|indexSettings
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newDirectory
specifier|public
name|Directory
name|newDirectory
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|location
init|=
name|path
operator|.
name|resolveIndex
argument_list|()
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|Directory
name|wrapped
init|=
name|newFSDirectory
argument_list|(
name|location
argument_list|,
name|buildLockFactory
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RateLimitedFSDirectory
argument_list|(
name|wrapped
argument_list|,
name|this
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onPause
specifier|public
name|void
name|onPause
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|rateLimitingTimeInNanos
operator|.
name|inc
argument_list|(
name|nanos
argument_list|)
expr_stmt|;
block|}
comment|/*     * We are mmapping norms, docvalues as well as term dictionaries, all other files are served through NIOFS     * this provides good random access performance while not creating unnecessary mmaps for files like stored     * fields etc.     */
DECL|field|PRIMARY_EXTENSIONS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PRIMARY_EXTENSIONS
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"nvd"
argument_list|,
literal|"dvd"
argument_list|,
literal|"tim"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|newFSDirectory
specifier|protected
name|Directory
name|newFSDirectory
parameter_list|(
name|Path
name|location
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|storeType
init|=
name|indexSettings
operator|.
name|get
argument_list|(
name|IndexStoreModule
operator|.
name|STORE_TYPE
argument_list|,
name|IndexStoreModule
operator|.
name|Type
operator|.
name|DEFAULT
operator|.
name|getSettingsKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|IndexStoreModule
operator|.
name|Type
operator|.
name|FS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
operator|||
name|IndexStoreModule
operator|.
name|Type
operator|.
name|DEFAULT
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
specifier|final
name|FSDirectory
name|open
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
decl_stmt|;
comment|// use lucene defaults
if|if
condition|(
name|open
operator|instanceof
name|MMapDirectory
operator|&&
name|Constants
operator|.
name|WINDOWS
operator|==
literal|false
condition|)
block|{
return|return
name|newDefaultDir
argument_list|(
name|location
argument_list|,
operator|(
name|MMapDirectory
operator|)
name|open
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
return|return
name|open
return|;
block|}
elseif|else
if|if
condition|(
name|IndexStoreModule
operator|.
name|Type
operator|.
name|SIMPLEFS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
return|return
operator|new
name|SimpleFSDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|IndexStoreModule
operator|.
name|Type
operator|.
name|NIOFS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
return|return
operator|new
name|NIOFSDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|IndexStoreModule
operator|.
name|Type
operator|.
name|MMAPFS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
return|return
operator|new
name|MMapDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No directory found for type ["
operator|+
name|storeType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|newDefaultDir
specifier|private
name|Directory
name|newDefaultDir
parameter_list|(
name|Path
name|location
parameter_list|,
specifier|final
name|MMapDirectory
name|mmapDir
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileSwitchDirectory
argument_list|(
name|PRIMARY_EXTENSIONS
argument_list|,
name|mmapDir
argument_list|,
operator|new
name|NIOFSDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Avoid doing listAll twice:
return|return
name|mmapDir
operator|.
name|listAll
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

