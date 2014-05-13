begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|state
operator|.
name|ClusterStateResponse
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
name|bench
operator|.
name|BenchmarkNodeMissingException
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
name|bench
operator|.
name|BenchmarkStatusResponse
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
name|indices
operator|.
name|IndexMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndexTemplateMissingException
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
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|*
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
name|empty
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
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Base test cluster that exposes the basis to run tests against any elasticsearch cluster, whose layout  * (e.g. number of nodes) is predefined and cannot be changed during the tests execution  */
end_comment

begin_class
DECL|class|ImmutableTestCluster
specifier|public
specifier|abstract
class|class
name|ImmutableTestCluster
implements|implements
name|Iterable
argument_list|<
name|Client
argument_list|>
block|{
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
DECL|field|random
specifier|protected
name|Random
name|random
decl_stmt|;
DECL|field|transportClientRatio
specifier|protected
name|double
name|transportClientRatio
init|=
literal|0.0
decl_stmt|;
comment|/**      * This method should be executed before each test to reset the cluster to its initial state.      */
DECL|method|beforeTest
specifier|public
name|void
name|beforeTest
parameter_list|(
name|Random
name|random
parameter_list|,
name|double
name|transportClientRatio
parameter_list|)
block|{
assert|assert
name|transportClientRatio
operator|>=
literal|0.0
operator|&&
name|transportClientRatio
operator|<=
literal|1.0
assert|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Reset test cluster with transport client ratio: [{}]"
argument_list|,
name|transportClientRatio
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportClientRatio
operator|=
name|transportClientRatio
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wipes any data that a test can leave behind: indices, templates and repositories      */
DECL|method|wipe
specifier|public
name|void
name|wipe
parameter_list|()
block|{
name|wipeIndices
argument_list|(
literal|"_all"
argument_list|)
expr_stmt|;
name|wipeTemplates
argument_list|()
expr_stmt|;
name|wipeRepositories
argument_list|()
expr_stmt|;
block|}
comment|/**      * This method checks all the things that need to be checked after each test      */
DECL|method|assertAfterTest
specifier|public
name|void
name|assertAfterTest
parameter_list|()
block|{
name|assertAllSearchersClosed
argument_list|()
expr_stmt|;
name|assertAllFilesClosed
argument_list|()
expr_stmt|;
name|ensureEstimatedStats
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|BenchmarkStatusResponse
name|statusResponse
init|=
name|client
argument_list|()
operator|.
name|prepareBenchStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|statusResponse
operator|.
name|benchmarkResponses
argument_list|()
argument_list|,
name|is
argument_list|(
name|empty
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BenchmarkNodeMissingException
name|ex
parameter_list|)
block|{
comment|// that's fine
block|}
block|}
comment|/**      * This method should be executed during tear down, after each test (but after assertAfterTest)      */
DECL|method|afterTest
specifier|public
specifier|abstract
name|void
name|afterTest
parameter_list|()
function_decl|;
comment|/**      * Returns a client connected to any node in the cluster      */
DECL|method|client
specifier|public
specifier|abstract
name|Client
name|client
parameter_list|()
function_decl|;
comment|/**      * Returns the number of nodes in the cluster.      */
DECL|method|size
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * Returns the number of data nodes in the cluster.      */
DECL|method|numDataNodes
specifier|public
specifier|abstract
name|int
name|numDataNodes
parameter_list|()
function_decl|;
comment|/**      * Returns the number of bench nodes in the cluster.      */
DECL|method|numBenchNodes
specifier|public
specifier|abstract
name|int
name|numBenchNodes
parameter_list|()
function_decl|;
comment|/**      * Returns the http addresses of the nodes within the cluster.      * Can be used to run REST tests against the test cluster.      */
DECL|method|httpAddresses
specifier|public
specifier|abstract
name|InetSocketAddress
index|[]
name|httpAddresses
parameter_list|()
function_decl|;
comment|/**      * Closes the current cluster      */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * Deletes the given indices from the tests cluster. If no index name is passed to this method      * all indices are removed.      */
DECL|method|wipeIndices
specifier|public
name|void
name|wipeIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
assert|assert
name|indices
operator|!=
literal|null
operator|&&
name|indices
operator|.
name|length
operator|>
literal|0
assert|;
if|if
condition|(
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
name|indices
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexMissingException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Happens if `action.destructive_requires_name` is set to true
comment|// which is the case in the CloseIndexDisableCloseAllTests
if|if
condition|(
literal|"_all"
operator|.
name|equals
argument_list|(
name|indices
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|ClusterStateResponse
name|clusterStateResponse
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
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|ObjectArrayList
argument_list|<
name|String
argument_list|>
name|concreteIndices
init|=
operator|new
name|ObjectArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|clusterStateResponse
operator|.
name|getState
argument_list|()
operator|.
name|metaData
argument_list|()
control|)
block|{
name|concreteIndices
operator|.
name|add
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|concreteIndices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
name|concreteIndices
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Deletes index templates, support wildcard notation.      * If no template name is passed to this method all templates are removed.      */
DECL|method|wipeTemplates
specifier|public
name|void
name|wipeTemplates
parameter_list|(
name|String
modifier|...
name|templates
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// if nothing is provided, delete all
if|if
condition|(
name|templates
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|templates
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
name|template
range|:
name|templates
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
name|indices
argument_list|()
operator|.
name|prepareDeleteTemplate
argument_list|(
name|template
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
name|IndexTemplateMissingException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
comment|/**      * Deletes repositories, supports wildcard notation.      */
DECL|method|wipeRepositories
specifier|public
name|void
name|wipeRepositories
parameter_list|(
name|String
modifier|...
name|repositories
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|>
literal|0
condition|)
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
block|}
comment|/**      * Ensures that the breaker statistics are reset to 0 since we wiped all indices and that      * means all stats should be set to 0 otherwise something is wrong with the field data      * calculation.      */
DECL|method|ensureEstimatedStats
specifier|public
name|void
name|ensureEstimatedStats
parameter_list|()
block|{
if|if
condition|(
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|NodesStatsResponse
name|nodeStats
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
name|prepareNodesStats
argument_list|()
operator|.
name|clear
argument_list|()
operator|.
name|setBreaker
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
for|for
control|(
name|NodeStats
name|stats
range|:
name|nodeStats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
literal|"Breaker not reset to 0 on node: "
operator|+
name|stats
operator|.
name|getNode
argument_list|()
argument_list|,
name|stats
operator|.
name|getBreaker
argument_list|()
operator|.
name|getEstimated
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

