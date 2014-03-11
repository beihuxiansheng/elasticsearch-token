begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|recovery
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|ClusterHealthRequestBuilder
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
name|collect
operator|.
name|MapBuilder
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
name|ElasticsearchIntegrationTest
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|ElasticsearchIntegrationTest
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
name|ElasticsearchIntegrationTest
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
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
name|assertHitCount
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|FullRollingRestartTests
specifier|public
class|class
name|FullRollingRestartTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|method|assertTimeout
specifier|protected
name|void
name|assertTimeout
parameter_list|(
name|ClusterHealthRequestBuilder
name|requestBuilder
parameter_list|)
block|{
name|ClusterHealthResponse
name|clusterHealth
init|=
name|requestBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterHealth
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"cluster health request timed out:\n{}"
argument_list|,
name|clusterHealth
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cluster health request timed out"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|numberOfReplicas
specifier|protected
name|int
name|numberOfReplicas
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Test
annotation|@
name|Slow
annotation|@
name|TestLogging
argument_list|(
literal|"indices.cluster:TRACE,cluster.service:TRACE"
argument_list|)
DECL|method|testFullRollingRestart
specifier|public
name|void
name|testFullRollingRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
name|createIndex
argument_list|(
literal|"test"
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|MapBuilder
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|map
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
name|flush
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1000
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|MapBuilder
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|map
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
comment|// now start adding nodes
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now start adding nodes
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|2000l
argument_list|)
expr_stmt|;
block|}
comment|// now start shutting nodes down
name|cluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|2000l
argument_list|)
expr_stmt|;
block|}
name|cluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|setWaitForYellowStatus
argument_list|()
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|2000l
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

