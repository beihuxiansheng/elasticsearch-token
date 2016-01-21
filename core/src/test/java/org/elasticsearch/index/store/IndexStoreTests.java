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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
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
name|FileSwitchDirectory
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
name|MMapDirectory
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
name|NIOFSDirectory
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
name|NoLockFactory
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
name|store
operator|.
name|StoreRateLimiting
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
name|Version
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
name|IndexModule
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
name|shard
operator|.
name|ShardPath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|IndexSettingsModule
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
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndexStoreTests
specifier|public
class|class
name|IndexStoreTests
extends|extends
name|ESTestCase
block|{
DECL|method|testStoreDirectory
specifier|public
name|void
name|testStoreDirectory
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
specifier|final
name|IndexModule
operator|.
name|Type
index|[]
name|values
init|=
name|IndexModule
operator|.
name|Type
operator|.
name|values
argument_list|()
decl_stmt|;
specifier|final
name|IndexModule
operator|.
name|Type
name|type
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|values
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexModule
operator|.
name|INDEX_STORE_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|type
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|FsDirectoryService
name|service
init|=
operator|new
name|FsDirectoryService
argument_list|(
name|indexSettings
argument_list|,
literal|null
argument_list|,
operator|new
name|ShardPath
argument_list|(
literal|false
argument_list|,
name|tempDir
argument_list|,
name|tempDir
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Directory
name|directory
init|=
name|service
operator|.
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
init|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NIOFS
case|:
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|NIOFSDirectory
argument_list|)
expr_stmt|;
break|break;
case|case
name|MMAPFS
case|:
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|MMapDirectory
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLEFS
case|:
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|SimpleFSDirectory
argument_list|)
expr_stmt|;
break|break;
case|case
name|FS
case|:
case|case
name|DEFAULT
case|:
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
operator|&&
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
block|{
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|MMapDirectory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|SimpleFSDirectory
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
operator|&&
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
block|{
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|FileSwitchDirectory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|NIOFSDirectory
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
DECL|method|testStoreDirectoryDefault
specifier|public
name|void
name|testStoreDirectoryDefault
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
name|FsDirectoryService
name|service
init|=
operator|new
name|FsDirectoryService
argument_list|(
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
operator|new
name|ShardPath
argument_list|(
literal|false
argument_list|,
name|tempDir
argument_list|,
name|tempDir
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Directory
name|directory
init|=
name|service
operator|.
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
init|)
block|{
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|assertTrue
argument_list|(
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|MMapDirectory
operator|||
name|directory
operator|instanceof
name|SimpleFSDirectory
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
condition|)
block|{
name|assertTrue
argument_list|(
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|FileSwitchDirectory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|directory
operator|instanceof
name|NIOFSDirectory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testUpdateThrottleType
specifier|public
name|void
name|testUpdateThrottleType
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexStoreConfig
operator|.
name|INDICES_STORE_THROTTLE_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"all"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|IndexStoreConfig
name|indexStoreConfig
init|=
operator|new
name|IndexStoreConfig
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|IndexStore
name|store
init|=
operator|new
name|IndexStore
argument_list|(
name|indexSettings
argument_list|,
name|indexStoreConfig
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|NONE
argument_list|,
name|store
operator|.
name|rateLimiting
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|ALL
argument_list|,
name|indexStoreConfig
operator|.
name|getNodeRateLimiter
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|indexStoreConfig
operator|.
name|getNodeRateLimiter
argument_list|()
argument_list|,
name|store
operator|.
name|rateLimiting
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|setType
argument_list|(
name|IndexStore
operator|.
name|IndexRateLimitingType
operator|.
name|fromString
argument_list|(
literal|"NODE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|ALL
argument_list|,
name|store
operator|.
name|rateLimiting
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|indexStoreConfig
operator|.
name|getNodeRateLimiter
argument_list|()
argument_list|,
name|store
operator|.
name|rateLimiting
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|setType
argument_list|(
name|IndexStore
operator|.
name|IndexRateLimitingType
operator|.
name|fromString
argument_list|(
literal|"merge"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|MERGE
argument_list|,
name|store
operator|.
name|rateLimiting
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|indexStoreConfig
operator|.
name|getNodeRateLimiter
argument_list|()
argument_list|,
name|store
operator|.
name|rateLimiting
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|ALL
argument_list|,
name|indexStoreConfig
operator|.
name|getNodeRateLimiter
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

