begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
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
name|Joiner
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
name|Iterators
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
name|ActionRequestBuilder
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
name|ActionResponse
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
name|health
operator|.
name|ClusterHealthRequest
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
name|health
operator|.
name|ClusterHealthResponse
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
name|health
operator|.
name|ClusterHealthStatus
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
name|create
operator|.
name|CreateIndexRequestBuilder
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
name|exists
operator|.
name|indices
operator|.
name|IndicesExistsResponse
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
name|flush
operator|.
name|FlushResponse
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
name|optimize
operator|.
name|OptimizeResponse
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
name|refresh
operator|.
name|RefreshResponse
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
name|get
operator|.
name|GetResponse
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
name|index
operator|.
name|IndexResponse
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
name|support
operator|.
name|broadcast
operator|.
name|BroadcastOperationRequestBuilder
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
name|support
operator|.
name|broadcast
operator|.
name|BroadcastOperationResponse
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
name|AdminClient
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
name|client
operator|.
name|Requests
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
name|ClusterService
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
name|cluster
operator|.
name|routing
operator|.
name|GroupShardsIterator
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
name|routing
operator|.
name|ShardIterator
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
name|routing
operator|.
name|ShardRouting
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
name|Priority
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
name|ImmutableSettings
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
name|xcontent
operator|.
name|XContentBuilder
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
name|IndexAlreadyExistsException
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
name|junit
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
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|assertNoFailures
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

begin_comment
comment|/**  * This abstract base testcase reuses a cluster instance internally and might  * start an abitrary number of nodes in the background. This class might in the  * future add random configureation options to created indices etc. unless  * unless they are explicitly defined by the test.  *<p/>  *<p>  * This test wipes all indices before a testcase is executed and uses  * elasticsearch features like allocation filters to ensure an index is  * allocated only on a certain number of nodes. The test doesn't expose explicit  * information about the client or which client is returned, clients might be  * node clients or transport clients and the returned client might be rotated.  *</p>  *<p/>  * Tests that need more explict control over the cluster or that need to change  * the cluster state aside of per-index settings should not use this class as a  * baseclass. If your test modifies the cluster state with persistent or  * transient settings the baseclass will raise and error.  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|AbstractSharedClusterTest
specifier|public
specifier|abstract
class|class
name|AbstractSharedClusterTest
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|cluster
specifier|private
specifier|static
name|TestCluster
name|cluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before
specifier|public
specifier|final
name|void
name|before
parameter_list|()
block|{
name|cluster
operator|.
name|ensureAtLeastNumNodes
argument_list|(
name|numberOfNodes
argument_list|()
argument_list|)
expr_stmt|;
name|wipeIndices
argument_list|()
expr_stmt|;
name|wipeTemplates
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{
name|MetaData
name|metaData
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"test leaves persistent cluster metadata behind: "
operator|+
name|metaData
operator|.
name|persistentSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
argument_list|,
name|metaData
operator|.
name|persistentSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"test leaves transient cluster metadata behind: "
operator|+
name|metaData
operator|.
name|transientSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
argument_list|,
name|metaData
operator|.
name|persistentSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|cluster
specifier|public
specifier|static
name|TestCluster
name|cluster
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
block|{
name|cluster
operator|=
name|ClusterManager
operator|.
name|accquireCluster
argument_list|(
name|getRandom
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
DECL|method|clusterService
specifier|public
name|ClusterService
name|clusterService
parameter_list|()
block|{
return|return
name|cluster
argument_list|()
operator|.
name|clusterService
argument_list|()
return|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|cluster
operator|=
literal|null
expr_stmt|;
name|ClusterManager
operator|.
name|releaseCluster
argument_list|()
expr_stmt|;
block|}
DECL|method|client
specifier|public
specifier|static
name|Client
name|client
parameter_list|()
block|{
return|return
name|cluster
argument_list|()
operator|.
name|client
argument_list|()
return|;
block|}
DECL|method|randomSettingsBuilder
specifier|public
name|ImmutableSettings
operator|.
name|Builder
name|randomSettingsBuilder
parameter_list|()
block|{
comment|// TODO RANDOMIZE
return|return
name|ImmutableSettings
operator|.
name|builder
argument_list|()
return|;
block|}
comment|// TODO Randomize MergePolicyProviderBase.INDEX_COMPOUND_FORMAT [true|false|"true"|"false"|[0..1]| toString([0..1])]
DECL|method|getSettings
specifier|public
name|Settings
name|getSettings
parameter_list|()
block|{
return|return
name|randomSettingsBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|wipeIndices
specifier|public
specifier|static
name|void
name|wipeIndices
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
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
name|prepareDelete
argument_list|(
name|names
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
name|IndexMissingException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
DECL|method|wipeIndex
specifier|public
specifier|static
name|void
name|wipeIndex
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|wipeIndices
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Deletes index templates, support wildcard notation.      */
DECL|method|wipeTemplates
specifier|public
specifier|static
name|void
name|wipeTemplates
parameter_list|(
name|String
modifier|...
name|templates
parameter_list|)
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
DECL|method|createIndex
specifier|public
name|void
name|createIndex
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
try|try
block|{
name|prepareCreate
argument_list|(
name|name
argument_list|)
operator|.
name|setSettings
argument_list|(
name|getSettings
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|IndexAlreadyExistsException
name|ex
parameter_list|)
block|{
name|wipeIndex
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|prepareCreate
argument_list|(
name|name
argument_list|)
operator|.
name|setSettings
argument_list|(
name|getSettings
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
block|}
DECL|method|createIndexMapped
specifier|public
name|void
name|createIndexMapped
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|String
modifier|...
name|simpleMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|type
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
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
name|simpleMapping
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|simpleMapping
index|[
name|i
operator|++
index|]
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|simpleMapping
index|[
name|i
index|]
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
try|try
block|{
name|prepareCreate
argument_list|(
name|name
argument_list|)
operator|.
name|setSettings
argument_list|(
name|getSettings
argument_list|()
argument_list|)
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
name|builder
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IndexAlreadyExistsException
name|ex
parameter_list|)
block|{
name|wipeIndex
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|prepareCreate
argument_list|(
name|name
argument_list|)
operator|.
name|setSettings
argument_list|(
name|getSettings
argument_list|()
argument_list|)
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
name|builder
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|prepareCreate
specifier|public
name|CreateIndexRequestBuilder
name|prepareCreate
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|numNodes
parameter_list|)
block|{
return|return
name|prepareCreate
argument_list|(
name|index
argument_list|,
name|numNodes
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
argument_list|)
return|;
block|}
DECL|method|prepareCreate
specifier|public
name|CreateIndexRequestBuilder
name|prepareCreate
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|numNodes
parameter_list|,
name|ImmutableSettings
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|cluster
argument_list|()
operator|.
name|ensureAtLeastNumNodes
argument_list|(
name|numNodes
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|getSettings
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|settings
argument_list|)
expr_stmt|;
if|if
condition|(
name|numNodes
operator|>
literal|0
condition|)
block|{
name|getExcludeSettings
argument_list|(
name|index
argument_list|,
name|numNodes
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addMapping
specifier|public
name|CreateIndexRequestBuilder
name|addMapping
parameter_list|(
name|CreateIndexRequestBuilder
name|builder
parameter_list|,
name|String
name|type
parameter_list|,
name|Object
index|[]
modifier|...
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|mappingBuilder
init|=
name|jsonBuilder
argument_list|()
decl_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|type
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
index|[]
name|objects
range|:
name|mapping
control|)
block|{
name|mappingBuilder
operator|.
name|startObject
argument_list|(
name|objects
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|objects
index|[
name|i
operator|++
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|objects
index|[
name|i
index|]
decl_stmt|;
name|mappingBuilder
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|mappingBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|mappingBuilder
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
name|mappingBuilder
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|getExcludeSettings
specifier|private
name|ImmutableSettings
operator|.
name|Builder
name|getExcludeSettings
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|num
parameter_list|,
name|ImmutableSettings
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|String
name|exclude
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|cluster
argument_list|()
operator|.
name|allButN
argument_list|(
name|num
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.exclude._name"
argument_list|,
name|exclude
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|getExcludeNodes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getExcludeNodes
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeExclude
init|=
name|cluster
argument_list|()
operator|.
name|nodeExclude
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodesInclude
init|=
name|cluster
argument_list|()
operator|.
name|nodesInclude
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodesInclude
operator|.
name|size
argument_list|()
operator|<
name|num
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|limit
init|=
name|Iterators
operator|.
name|limit
argument_list|(
name|nodeExclude
operator|.
name|iterator
argument_list|()
argument_list|,
name|num
operator|-
name|nodesInclude
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|limit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|limit
operator|.
name|next
argument_list|()
expr_stmt|;
name|limit
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|limit
init|=
name|Iterators
operator|.
name|limit
argument_list|(
name|nodesInclude
operator|.
name|iterator
argument_list|()
argument_list|,
name|nodesInclude
operator|.
name|size
argument_list|()
operator|-
name|num
argument_list|)
decl_stmt|;
while|while
condition|(
name|limit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|nodeExclude
operator|.
name|add
argument_list|(
name|limit
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|limit
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|nodeExclude
return|;
block|}
DECL|method|allowNodes
specifier|public
name|void
name|allowNodes
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|numNodes
parameter_list|)
block|{
name|cluster
argument_list|()
operator|.
name|ensureAtLeastNumNodes
argument_list|(
name|numNodes
argument_list|)
expr_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|builder
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|numNodes
operator|>
literal|0
condition|)
block|{
name|getExcludeSettings
argument_list|(
name|index
argument_list|,
name|numNodes
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|Settings
name|build
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|build
operator|.
name|getAsMap
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
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
name|prepareUpdateSettings
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|build
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|prepareCreate
specifier|public
name|CreateIndexRequestBuilder
name|prepareCreate
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|getSettings
argument_list|()
argument_list|)
return|;
block|}
DECL|method|updateClusterSettings
specifier|public
name|void
name|updateClusterSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
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
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|ensureGreen
specifier|public
name|ClusterHealthStatus
name|ensureGreen
parameter_list|()
block|{
name|ClusterHealthResponse
name|actionGet
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
name|health
argument_list|(
name|Requests
operator|.
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
operator|.
name|waitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|waitForRelocatingShards
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actionGet
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actionGet
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|actionGet
operator|.
name|getStatus
argument_list|()
return|;
block|}
DECL|method|waitForRelocation
specifier|public
name|ClusterHealthStatus
name|waitForRelocation
parameter_list|()
block|{
return|return
name|waitForRelocation
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|waitForRelocation
specifier|public
name|ClusterHealthStatus
name|waitForRelocation
parameter_list|(
name|ClusterHealthStatus
name|status
parameter_list|)
block|{
name|ClusterHealthRequest
name|request
init|=
name|Requests
operator|.
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForRelocatingShards
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|waitForStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
name|ClusterHealthResponse
name|actionGet
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
name|health
argument_list|(
name|request
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actionGet
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|actionGet
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|actionGet
operator|.
name|getStatus
argument_list|()
return|;
block|}
DECL|method|ensureYellow
specifier|public
name|ClusterHealthStatus
name|ensureYellow
parameter_list|()
block|{
name|ClusterHealthResponse
name|actionGet
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
name|health
argument_list|(
name|Requests
operator|.
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|waitForYellowStatus
argument_list|()
operator|.
name|waitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actionGet
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|actionGet
operator|.
name|getStatus
argument_list|()
return|;
block|}
DECL|method|commaString
specifier|public
specifier|static
name|String
name|commaString
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|strings
argument_list|)
return|;
block|}
DECL|method|numberOfNodes
specifier|protected
name|int
name|numberOfNodes
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
comment|// utils
DECL|method|index
specifier|protected
name|IndexResponse
name|index
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|XContentBuilder
name|source
parameter_list|)
block|{
return|return
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
return|;
block|}
DECL|method|get
specifier|protected
name|GetResponse
name|get
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
return|;
block|}
DECL|method|index
specifier|protected
name|IndexResponse
name|index
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|field
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|setSource
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
return|;
block|}
DECL|method|refresh
specifier|protected
name|RefreshResponse
name|refresh
parameter_list|()
block|{
name|waitForRelocation
argument_list|()
expr_stmt|;
comment|// TODO RANDOMIZE with flush?
name|RefreshResponse
name|actionGet
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
name|prepareRefresh
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|actionGet
argument_list|)
expr_stmt|;
return|return
name|actionGet
return|;
block|}
DECL|method|flush
specifier|protected
name|FlushResponse
name|flush
parameter_list|()
block|{
name|waitForRelocation
argument_list|()
expr_stmt|;
name|FlushResponse
name|actionGet
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
name|prepareFlush
argument_list|()
operator|.
name|setRefresh
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
name|assertNoFailures
argument_list|(
name|actionGet
argument_list|)
expr_stmt|;
return|return
name|actionGet
return|;
block|}
DECL|method|optimize
specifier|protected
name|OptimizeResponse
name|optimize
parameter_list|()
block|{
name|waitForRelocation
argument_list|()
expr_stmt|;
name|OptimizeResponse
name|actionGet
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
name|prepareOptimize
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|actionGet
argument_list|)
expr_stmt|;
return|return
name|actionGet
return|;
block|}
DECL|method|nodeIdsWithIndex
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|nodeIdsWithIndex
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|ClusterState
name|state
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
operator|.
name|getState
argument_list|()
decl_stmt|;
name|GroupShardsIterator
name|allAssignedShardsGrouped
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|allAssignedShardsGrouped
argument_list|(
name|indices
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardIterator
name|shardIterator
range|:
name|allAssignedShardsGrouped
control|)
block|{
for|for
control|(
name|ShardRouting
name|routing
range|:
name|shardIterator
operator|.
name|asUnordered
argument_list|()
control|)
block|{
if|if
condition|(
name|routing
operator|.
name|active
argument_list|()
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|routing
operator|.
name|currentNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|nodes
return|;
block|}
DECL|method|numAssignedShards
specifier|protected
name|int
name|numAssignedShards
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|ClusterState
name|state
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
operator|.
name|getState
argument_list|()
decl_stmt|;
name|GroupShardsIterator
name|allAssignedShardsGrouped
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|allAssignedShardsGrouped
argument_list|(
name|indices
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|allAssignedShardsGrouped
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|indexExists
specifier|protected
name|boolean
name|indexExists
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|IndicesExistsResponse
name|actionGet
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
name|prepareExists
argument_list|(
name|index
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
return|return
name|actionGet
operator|.
name|isExists
argument_list|()
return|;
block|}
DECL|method|admin
specifier|protected
name|AdminClient
name|admin
parameter_list|()
block|{
return|return
name|client
argument_list|()
operator|.
name|admin
argument_list|()
return|;
block|}
DECL|method|run
specifier|protected
parameter_list|<
name|Res
extends|extends
name|ActionResponse
parameter_list|>
name|Res
name|run
parameter_list|(
name|ActionRequestBuilder
argument_list|<
name|?
argument_list|,
name|Res
argument_list|,
name|?
argument_list|>
name|builder
parameter_list|)
block|{
name|Res
name|actionGet
init|=
name|builder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
return|return
name|actionGet
return|;
block|}
DECL|method|run
specifier|protected
parameter_list|<
name|Res
extends|extends
name|BroadcastOperationResponse
parameter_list|>
name|Res
name|run
parameter_list|(
name|BroadcastOperationRequestBuilder
argument_list|<
name|?
argument_list|,
name|Res
argument_list|,
name|?
argument_list|>
name|builder
parameter_list|)
block|{
name|Res
name|actionGet
init|=
name|builder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|actionGet
argument_list|)
expr_stmt|;
return|return
name|actionGet
return|;
block|}
block|}
end_class

end_unit

