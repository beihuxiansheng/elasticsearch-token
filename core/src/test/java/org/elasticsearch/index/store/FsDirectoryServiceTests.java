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
name|SleepingLockWrapper
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
name|Arrays
import|;
end_import

begin_class
DECL|class|FsDirectoryServiceTests
specifier|public
class|class
name|FsDirectoryServiceTests
extends|extends
name|ESTestCase
block|{
DECL|method|testPreload
specifier|public
name|void
name|testPreload
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestPreload
argument_list|()
expr_stmt|;
name|doTestPreload
argument_list|(
literal|"nvd"
argument_list|,
literal|"dvd"
argument_list|,
literal|"tim"
argument_list|)
expr_stmt|;
name|doTestPreload
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestPreload
specifier|private
name|void
name|doTestPreload
parameter_list|(
name|String
modifier|...
name|preload
parameter_list|)
throws|throws
name|IOException
block|{
name|Settings
name|build
init|=
name|Settings
operator|.
name|builder
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
literal|"mmapfs"
argument_list|)
operator|.
name|putArray
argument_list|(
name|IndexModule
operator|.
name|INDEX_STORE_PRE_LOAD_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|preload
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|settings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"foo"
argument_list|,
name|build
argument_list|)
decl_stmt|;
name|IndexStore
name|store
init|=
operator|new
name|IndexStore
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|settings
operator|.
name|getUUID
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|ShardPath
name|path
init|=
operator|new
name|ShardPath
argument_list|(
literal|false
argument_list|,
name|tempDir
argument_list|,
name|tempDir
argument_list|,
operator|new
name|ShardId
argument_list|(
name|settings
operator|.
name|getIndex
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|FsDirectoryService
name|fsDirectoryService
init|=
operator|new
name|FsDirectoryService
argument_list|(
name|settings
argument_list|,
name|store
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
name|fsDirectoryService
operator|.
name|newDirectory
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|directory
operator|instanceof
name|SleepingLockWrapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|preload
operator|.
name|length
operator|==
literal|0
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
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|MMapDirectory
operator|)
name|directory
operator|)
operator|.
name|getPreload
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|preload
argument_list|)
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
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
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|MMapDirectory
operator|)
name|directory
operator|)
operator|.
name|getPreload
argument_list|()
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
name|FileSwitchDirectory
argument_list|)
expr_stmt|;
name|FileSwitchDirectory
name|fsd
init|=
operator|(
name|FileSwitchDirectory
operator|)
name|directory
decl_stmt|;
name|assertTrue
argument_list|(
name|fsd
operator|.
name|getPrimaryDir
argument_list|()
operator|instanceof
name|MMapDirectory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|MMapDirectory
operator|)
name|fsd
operator|.
name|getPrimaryDir
argument_list|()
operator|)
operator|.
name|getPreload
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fsd
operator|.
name|getSecondaryDir
argument_list|()
operator|instanceof
name|MMapDirectory
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|MMapDirectory
operator|)
name|fsd
operator|.
name|getSecondaryDir
argument_list|()
operator|)
operator|.
name|getPreload
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

