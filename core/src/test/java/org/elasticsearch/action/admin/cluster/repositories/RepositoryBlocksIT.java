begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.repositories
package|package
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
name|get
operator|.
name|GetRepositoriesResponse
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
name|repositories
operator|.
name|verify
operator|.
name|VerifyRepositoryResponse
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
name|MetaData
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertBlocked
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
name|hasSize
import|;
end_import

begin_comment
comment|/**  * This class tests that repository operations (Put, Delete, Verify) are blocked when the cluster is read-only.  *  * The @NodeScope TEST is needed because this class updates the cluster setting "cluster.blocks.read_only".  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|TEST
argument_list|)
DECL|class|RepositoryBlocksIT
specifier|public
class|class
name|RepositoryBlocksIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|testPutRepositoryWithBlocks
specifier|public
name|void
name|testPutRepositoryWithBlocks
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"-->  registering a repository is blocked when the cluster is read only"
argument_list|)
expr_stmt|;
try|try
block|{
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertBlocked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo-blocks"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"fs"
argument_list|)
operator|.
name|setVerify
argument_list|(
literal|false
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|randomRepoPath
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|MetaData
operator|.
name|CLUSTER_READ_ONLY_BLOCK
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setClusterReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"-->  registering a repository is allowed when the cluster is not read only"
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo-blocks"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"fs"
argument_list|)
operator|.
name|setVerify
argument_list|(
literal|false
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|randomRepoPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testVerifyRepositoryWithBlocks
specifier|public
name|void
name|testVerifyRepositoryWithBlocks
parameter_list|()
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo-blocks"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"fs"
argument_list|)
operator|.
name|setVerify
argument_list|(
literal|false
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|randomRepoPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// This test checks that the Get Repository operation is never blocked, even if the cluster is read only.
try|try
block|{
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|VerifyRepositoryResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareVerifyRepository
argument_list|(
literal|"test-repo-blocks"
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
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|cluster
argument_list|()
operator|.
name|numDataAndMasterNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setClusterReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeleteRepositoryWithBlocks
specifier|public
name|void
name|testDeleteRepositoryWithBlocks
parameter_list|()
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo-blocks"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"fs"
argument_list|)
operator|.
name|setVerify
argument_list|(
literal|false
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|randomRepoPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-->  deleting a repository is blocked when the cluster is read only"
argument_list|)
expr_stmt|;
try|try
block|{
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertBlocked
argument_list|(
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
literal|"test-repo-blocks"
argument_list|)
argument_list|,
name|MetaData
operator|.
name|CLUSTER_READ_ONLY_BLOCK
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setClusterReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"-->  deleting a repository is allowed when the cluster is not read only"
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
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
literal|"test-repo-blocks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetRepositoryWithBlocks
specifier|public
name|void
name|testGetRepositoryWithBlocks
parameter_list|()
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo-blocks"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"fs"
argument_list|)
operator|.
name|setVerify
argument_list|(
literal|false
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|randomRepoPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// This test checks that the Get Repository operation is never blocked, even if the cluster is read only.
try|try
block|{
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|GetRepositoriesResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareGetRepositories
argument_list|(
literal|"test-repo-blocks"
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
name|response
operator|.
name|repositories
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setClusterReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

