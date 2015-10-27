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
name|admin
operator|.
name|indices
operator|.
name|template
operator|.
name|get
operator|.
name|GetIndexTemplatesResponse
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexTemplateMetaData
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
name|index
operator|.
name|IndexNotFoundException
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
name|io
operator|.
name|Closeable
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * Base test cluster that exposes the basis to run tests against any elasticsearch cluster, whose layout  * (e.g. number of nodes) is predefined and cannot be changed during the tests execution  */
end_comment

begin_class
DECL|class|TestCluster
specifier|public
specifier|abstract
class|class
name|TestCluster
implements|implements
name|Iterable
argument_list|<
name|Client
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|logger
specifier|protected
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
DECL|field|seed
specifier|private
specifier|final
name|long
name|seed
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
DECL|method|TestCluster
specifier|public
name|TestCluster
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
block|}
DECL|method|seed
specifier|public
name|long
name|seed
parameter_list|()
block|{
return|return
name|seed
return|;
block|}
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
throws|throws
name|IOException
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
comment|/**      * Wipes any data that a test can leave behind: indices, templates (except exclude templates) and repositories      */
DECL|method|wipe
specifier|public
name|void
name|wipe
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|excludeTemplates
parameter_list|)
block|{
name|wipeIndices
argument_list|(
literal|"_all"
argument_list|)
expr_stmt|;
name|wipeAllTemplates
argument_list|(
name|excludeTemplates
argument_list|)
expr_stmt|;
name|wipeRepositories
argument_list|()
expr_stmt|;
block|}
comment|/**      * Assertions that should run before the cluster is wiped should be called in this method      */
DECL|method|beforeIndexDeletion
specifier|public
name|void
name|beforeIndexDeletion
parameter_list|()
block|{     }
comment|/**      * This method checks all the things that need to be checked after each test      */
DECL|method|assertAfterTest
specifier|public
name|void
name|assertAfterTest
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureEstimatedStats
argument_list|()
expr_stmt|;
block|}
comment|/**      * This method should be executed during tear down, after each test (but after assertAfterTest)      */
DECL|method|afterTest
specifier|public
specifier|abstract
name|void
name|afterTest
parameter_list|()
throws|throws
name|IOException
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
comment|/**      * Returns the number of data and master eligible nodes in the cluster.      */
DECL|method|numDataAndMasterNodes
specifier|public
specifier|abstract
name|int
name|numDataAndMasterNodes
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
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
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
name|IndexNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
comment|/**      * Removes all templates, except the templates defined in the exclude      */
DECL|method|wipeAllTemplates
specifier|public
name|void
name|wipeAllTemplates
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|exclude
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
name|GetIndexTemplatesResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetTemplates
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexTemplateMetaData
name|indexTemplate
range|:
name|response
operator|.
name|getIndexTemplates
argument_list|()
control|)
block|{
if|if
condition|(
name|exclude
operator|.
name|contains
argument_list|(
name|indexTemplate
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
name|indexTemplate
operator|.
name|getName
argument_list|()
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
comment|/**      * Ensures that any breaker statistics are reset to 0.      *      * The implementation is specific to the test cluster, because the act of      * checking some breaker stats can increase them.      */
DECL|method|ensureEstimatedStats
specifier|public
specifier|abstract
name|void
name|ensureEstimatedStats
parameter_list|()
function_decl|;
comment|/**      * Returns the cluster name      */
DECL|method|getClusterName
specifier|public
specifier|abstract
name|String
name|getClusterName
parameter_list|()
function_decl|;
block|}
end_class

end_unit

