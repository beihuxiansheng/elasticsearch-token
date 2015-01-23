begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bwcompat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bwcompat
package|;
end_package

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
name|search
operator|.
name|SearchRequestBuilder
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
name|search
operator|.
name|SearchResponse
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
name|unit
operator|.
name|TimeValue
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
name|query
operator|.
name|QueryBuilders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|internal
operator|.
name|InternalNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|upgrade
operator|.
name|UpgradeTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchHit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|SortOrder
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
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
name|rest
operator|.
name|client
operator|.
name|http
operator|.
name|HttpRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_class
DECL|class|OldIndexBackwardsCompatibilityTests
specifier|public
class|class
name|OldIndexBackwardsCompatibilityTests
extends|extends
name|StaticIndexBackwardCompatibilityTest
block|{
comment|// TODO: test for proper exception on unsupported indexes (maybe via separate test?)
comment|// We have a 0.20.6.zip etc for this.
DECL|field|indexes
name|List
argument_list|<
name|String
argument_list|>
name|indexes
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"index-0.90.0.Beta1.zip"
argument_list|,
literal|"index-0.90.0.RC1.zip"
argument_list|,
literal|"index-0.90.0.RC2.zip"
argument_list|,
literal|"index-0.90.0.zip"
argument_list|,
literal|"index-0.90.1.zip"
argument_list|,
literal|"index-0.90.2.zip"
argument_list|,
literal|"index-0.90.3.zip"
argument_list|,
literal|"index-0.90.4.zip"
argument_list|,
literal|"index-0.90.5.zip"
argument_list|,
literal|"index-0.90.6.zip"
argument_list|,
literal|"index-0.90.7.zip"
argument_list|,
literal|"index-0.90.8.zip"
argument_list|,
literal|"index-0.90.9.zip"
argument_list|,
literal|"index-0.90.10.zip"
argument_list|,
literal|"index-0.90.11.zip"
argument_list|,
literal|"index-0.90.12.zip"
argument_list|,
literal|"index-0.90.13.zip"
argument_list|,
literal|"index-1.0.0.Beta1.zip"
argument_list|,
literal|"index-1.0.0.Beta2.zip"
argument_list|,
literal|"index-1.0.0.RC1.zip"
argument_list|,
literal|"index-1.0.0.RC2.zip"
argument_list|,
literal|"index-1.0.0.zip"
argument_list|,
literal|"index-1.0.1.zip"
argument_list|,
literal|"index-1.0.2.zip"
argument_list|,
literal|"index-1.0.3.zip"
argument_list|,
literal|"index-1.1.0.zip"
argument_list|,
literal|"index-1.1.1.zip"
argument_list|,
literal|"index-1.1.2.zip"
argument_list|,
literal|"index-1.2.0.zip"
argument_list|,
literal|"index-1.2.1.zip"
argument_list|,
literal|"index-1.2.2.zip"
argument_list|,
literal|"index-1.2.3.zip"
argument_list|,
literal|"index-1.2.4.zip"
argument_list|,
literal|"index-1.3.0.zip"
argument_list|,
literal|"index-1.3.1.zip"
argument_list|,
literal|"index-1.3.2.zip"
argument_list|,
literal|"index-1.3.3.zip"
argument_list|,
literal|"index-1.3.4.zip"
argument_list|,
literal|"index-1.3.5.zip"
argument_list|,
literal|"index-1.3.6.zip"
argument_list|,
literal|"index-1.3.7.zip"
argument_list|,
literal|"index-1.4.0.Beta1.zip"
argument_list|,
literal|"index-1.4.0.zip"
argument_list|,
literal|"index-1.4.1.zip"
argument_list|,
literal|"index-1.4.2.zip"
argument_list|)
decl_stmt|;
DECL|method|testAllVersionsTested
specifier|public
name|void
name|testAllVersionsTested
parameter_list|()
throws|throws
name|Exception
block|{
name|SortedSet
argument_list|<
name|String
argument_list|>
name|expectedVersions
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
range|:
name|Version
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|getType
argument_list|()
operator|==
name|Version
operator|.
name|class
condition|)
block|{
name|Version
name|v
init|=
operator|(
name|Version
operator|)
name|field
operator|.
name|get
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|snapshot
argument_list|()
condition|)
continue|continue;
if|if
condition|(
name|v
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_0_20_6
argument_list|)
condition|)
continue|continue;
name|expectedVersions
operator|.
name|add
argument_list|(
literal|"index-"
operator|+
name|v
operator|.
name|toString
argument_list|()
operator|+
literal|".zip"
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|index
range|:
name|indexes
control|)
block|{
if|if
condition|(
name|expectedVersions
operator|.
name|remove
argument_list|(
name|index
argument_list|)
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Old indexes tests contain extra index: "
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|expectedVersions
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Old index tests are missing indexes:"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|expected
range|:
name|expectedVersions
control|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"\n"
operator|+
name|expected
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOldIndexes
specifier|public
name|void
name|testOldIndexes
parameter_list|()
throws|throws
name|Exception
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|indexes
argument_list|,
name|getRandom
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indexes
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Testing old index "
operator|+
name|index
argument_list|)
expr_stmt|;
name|assertOldIndexWorks
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertOldIndexWorks
name|void
name|assertOldIndexWorks
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|InternalNode
operator|.
name|HTTP_ENABLED
argument_list|,
literal|true
argument_list|)
comment|// for _upgrade
operator|.
name|build
argument_list|()
decl_stmt|;
name|loadIndex
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|logMemoryStats
argument_list|()
expr_stmt|;
name|assertBasicSearchWorks
argument_list|()
expr_stmt|;
name|assertRealtimeGetWorks
argument_list|()
expr_stmt|;
name|assertNewReplicasWork
argument_list|()
expr_stmt|;
name|assertUpgradeWorks
argument_list|(
name|isLatestLuceneVersion
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|unloadIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|extractVersion
name|Version
name|extractVersion
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|Version
operator|.
name|fromString
argument_list|(
name|index
operator|.
name|substring
argument_list|(
name|index
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|,
name|index
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isLatestLuceneVersion
name|boolean
name|isLatestLuceneVersion
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|Version
name|version
init|=
name|extractVersion
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|version
operator|.
name|luceneVersion
operator|.
name|major
operator|==
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
operator|.
name|major
operator|&&
name|version
operator|.
name|luceneVersion
operator|.
name|minor
operator|==
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
operator|.
name|minor
return|;
block|}
DECL|method|assertBasicSearchWorks
name|void
name|assertBasicSearchWorks
parameter_list|()
block|{
name|SearchRequestBuilder
name|searchReq
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
decl_stmt|;
name|SearchResponse
name|searchRsp
init|=
name|searchReq
operator|.
name|get
argument_list|()
decl_stmt|;
name|ElasticsearchAssertions
operator|.
name|assertNoFailures
argument_list|(
name|searchRsp
argument_list|)
expr_stmt|;
name|long
name|numDocs
init|=
name|searchRsp
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Found "
operator|+
name|numDocs
operator|+
literal|" in old index"
argument_list|)
expr_stmt|;
name|searchReq
operator|.
name|addSort
argument_list|(
literal|"long_sort"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
expr_stmt|;
name|ElasticsearchAssertions
operator|.
name|assertNoFailures
argument_list|(
name|searchReq
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRealtimeGetWorks
name|void
name|assertRealtimeGetWorks
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
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SearchRequestBuilder
name|searchReq
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
decl_stmt|;
name|SearchHit
name|hit
init|=
name|searchReq
operator|.
name|get
argument_list|()
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|docId
init|=
name|hit
operator|.
name|getId
argument_list|()
decl_stmt|;
comment|// foo is new, it is not a field in the generated index
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
name|docId
argument_list|)
operator|.
name|setDoc
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetResponse
name|getRsp
init|=
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
name|docId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
name|getRsp
operator|.
name|getSourceAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|source
argument_list|,
name|Matchers
operator|.
name|hasKey
argument_list|(
literal|"foo"
argument_list|)
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
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"refresh_interval"
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNewReplicasWork
name|void
name|assertNewReplicasWork
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numReplicas
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|3
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
name|numReplicas
condition|;
operator|++
name|i
control|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Creating another node for replica "
operator|+
name|i
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"data.node"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"master.node"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|InternalNode
operator|.
name|HTTP_ENABLED
argument_list|,
literal|true
argument_list|)
comment|// for _upgrade
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|""
operator|+
operator|(
name|numReplicas
operator|+
literal|1
operator|)
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
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
name|numReplicas
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
argument_list|)
expr_stmt|;
comment|// This can take a while when the number of replicas is greater than cluster.routing.allocation.node_concurrent_recoveries
comment|// (which defaults to 2).  We could override that setting, but running this test on a busy box could
comment|// still result in taking a long time to finish starting replicas, so instead we have an increased timeout
name|ensureGreen
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"test"
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
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
argument_list|)
expr_stmt|;
name|waitNoPendingTasksOnAll
argument_list|()
expr_stmt|;
comment|// make sure the replicas are removed before going on
block|}
DECL|method|assertUpgradeWorks
name|void
name|assertUpgradeWorks
parameter_list|(
name|boolean
name|alreadyLatest
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpRequestBuilder
name|httpClient
init|=
name|httpClient
argument_list|()
decl_stmt|;
if|if
condition|(
name|alreadyLatest
operator|==
literal|false
condition|)
block|{
name|UpgradeTest
operator|.
name|assertNotUpgraded
argument_list|(
name|httpClient
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|UpgradeTest
operator|.
name|runUpgrade
argument_list|(
name|httpClient
argument_list|,
literal|"test"
argument_list|,
literal|"wait_for_completion"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|UpgradeTest
operator|.
name|assertUpgraded
argument_list|(
name|httpClient
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

