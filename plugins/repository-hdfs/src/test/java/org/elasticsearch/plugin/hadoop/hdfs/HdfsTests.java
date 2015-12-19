begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.hadoop.hdfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|repositories
operator|.
name|put
operator|.
name|PutRepositoryResponse
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|create
operator|.
name|CreateSnapshotResponse
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|restore
operator|.
name|RestoreSnapshotResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoryMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|hdfs
operator|.
name|TestingFs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|SnapshotState
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
name|ESIntegTestCase
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
name|ESIntegTestCase
operator|.
name|ClusterScope
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
name|ESIntegTestCase
operator|.
name|Scope
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
name|store
operator|.
name|MockFSDirectoryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_comment
comment|/**  * You must specify {@code -Dtests.thirdparty=true}  */
end_comment

begin_comment
comment|// Make sure to start MiniHDFS cluster before
end_comment

begin_comment
comment|// otherwise, one will get some wierd PrivateCredentialPermission exception
end_comment

begin_comment
comment|// caused by the HDFS fallback code (which doesn't do much anyway)
end_comment

begin_comment
comment|// @ThirdParty
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|SUITE
argument_list|,
name|numDataNodes
operator|=
literal|1
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|HdfsTests
specifier|public
class|class
name|HdfsTests
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|indexSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|RANDOM_PREVENT_DOUBLE_WRITE
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|RANDOM_NO_DELETE_OPEN_FILE
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|Settings
operator|.
name|Builder
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|ordinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.repo"
argument_list|,
literal|""
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|RANDOM_PREVENT_DOUBLE_WRITE
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|RANDOM_NO_DELETE_OPEN_FILE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|settings
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|HdfsTestPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
annotation|@
name|Before
DECL|method|wipeBefore
specifier|public
specifier|final
name|void
name|wipeBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|wipeRepositories
argument_list|()
expr_stmt|;
comment|//port = MiniHDFS.getPort();
comment|//path = "build/data/repo-" + randomInt();
block|}
annotation|@
name|After
DECL|method|wipeAfter
specifier|public
specifier|final
name|void
name|wipeAfter
parameter_list|()
throws|throws
name|Exception
block|{
name|wipeRepositories
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimpleWorkflow
specifier|public
name|void
name|testSimpleWorkflow
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-->  creating hdfs repository with path [{}]"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|PutRepositoryResponse
name|putRepositoryResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"hdfs"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"uri"
argument_list|,
literal|"hdfs://127.0.0.1:"
operator|+
name|port
argument_list|)
operator|.
name|put
argument_list|(
literal|"conf.fs.es-hdfs.impl"
argument_list|,
name|TestingFs
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
comment|// .put("uri", "es-hdfs:///")
operator|.
name|put
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
operator|.
name|put
argument_list|(
literal|"conf"
argument_list|,
literal|"additional-cfg.xml, conf-2.xml"
argument_list|)
operator|.
name|put
argument_list|(
literal|"chunk_size"
argument_list|,
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
operator|+
literal|"k"
argument_list|)
operator|.
name|put
argument_list|(
literal|"compress"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|putRepositoryResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"test-idx-1"
argument_list|,
literal|"test-idx-2"
argument_list|,
literal|"test-idx-3"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing some data"
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|index
argument_list|(
literal|"test-idx-1"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
operator|+
name|i
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test-idx-2"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
operator|+
name|i
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test-idx-3"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> snapshot"
argument_list|)
expr_stmt|;
name|CreateSnapshotResponse
name|createSnapshotResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareCreateSnapshot
argument_list|(
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|)
operator|.
name|setWaitForCompletion
argument_list|(
literal|true
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test-idx-*"
argument_list|,
literal|"-test-idx-3"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|createSnapshotResponse
operator|.
name|getSnapshotInfo
argument_list|()
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|createSnapshotResponse
operator|.
name|getSnapshotInfo
argument_list|()
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|createSnapshotResponse
operator|.
name|getSnapshotInfo
argument_list|()
operator|.
name|totalShards
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareGetSnapshots
argument_list|(
literal|"test-repo"
argument_list|)
operator|.
name|setSnapshots
argument_list|(
literal|"test-snap"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getSnapshots
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SnapshotState
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> delete some data"
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|client
operator|.
name|prepareDelete
argument_list|(
literal|"test-idx-1"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|50
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|client
operator|.
name|prepareDelete
argument_list|(
literal|"test-idx-2"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|client
operator|.
name|prepareDelete
argument_list|(
literal|"test-idx-3"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|50L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|50L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|50L
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> close indices"
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test-idx-1"
argument_list|,
literal|"test-idx-2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> restore all indices from the snapshot"
argument_list|)
expr_stmt|;
name|RestoreSnapshotResponse
name|restoreSnapshotResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareRestoreSnapshot
argument_list|(
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|)
operator|.
name|setWaitForCompletion
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|restoreSnapshotResponse
operator|.
name|getRestoreInfo
argument_list|()
operator|.
name|totalShards
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|50L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test restore after index deletion
name|logger
operator|.
name|info
argument_list|(
literal|"--> delete indices"
argument_list|)
expr_stmt|;
name|wipeIndices
argument_list|(
literal|"test-idx-1"
argument_list|,
literal|"test-idx-2"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> restore one index after deletion"
argument_list|)
expr_stmt|;
name|restoreSnapshotResponse
operator|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareRestoreSnapshot
argument_list|(
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|)
operator|.
name|setWaitForCompletion
argument_list|(
literal|true
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test-idx-*"
argument_list|,
literal|"-test-idx-2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|restoreSnapshotResponse
operator|.
name|getRestoreInfo
argument_list|()
operator|.
name|totalShards
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|(
name|client
argument_list|,
literal|"test-idx-1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|getMetaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test-idx-1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|getMetaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test-idx-2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|wipeIndices
specifier|private
name|void
name|wipeIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|cluster
argument_list|()
operator|.
name|wipeIndices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
comment|// RepositoryVerificationException.class
DECL|method|testWrongPath
specifier|public
name|void
name|testWrongPath
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-->  creating hdfs repository with path [{}]"
argument_list|,
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|PutRepositoryResponse
name|putRepositoryResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"hdfs"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"uri"
argument_list|,
literal|"hdfs://127.0.0.1:"
operator|+
name|port
argument_list|)
comment|// .put("uri", "es-hdfs:///")
operator|.
name|put
argument_list|(
literal|"conf.fs.es-hdfs.impl"
argument_list|,
name|TestingFs
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"path"
argument_list|,
name|path
operator|+
literal|"a@b$c#11:22"
argument_list|)
operator|.
name|put
argument_list|(
literal|"chunk_size"
argument_list|,
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
operator|+
literal|"k"
argument_list|)
operator|.
name|put
argument_list|(
literal|"compress"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|putRepositoryResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"test-idx-1"
argument_list|,
literal|"test-idx-2"
argument_list|,
literal|"test-idx-3"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Path name is invalid"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|re
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|/**      * Deletes repositories, supports wildcard notation.      */
DECL|method|wipeRepositories
specifier|public
specifier|static
name|void
name|wipeRepositories
parameter_list|(
name|String
modifier|...
name|repositories
parameter_list|)
block|{
comment|// if nothing is provided, delete all
if|if
condition|(
name|repositories
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|repositories
operator|=
operator|new
name|String
index|[]
block|{
literal|"*"
block|}
expr_stmt|;
block|}
for|for
control|(
name|String
name|repository
range|:
name|repositories
control|)
block|{
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareDeleteRepository
argument_list|(
name|repository
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryMissingException
name|ex
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
DECL|method|count
specifier|private
name|long
name|count
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|index
parameter_list|)
block|{
return|return
name|client
operator|.
name|prepareSearch
argument_list|(
name|index
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
return|;
block|}
block|}
end_class

end_unit

