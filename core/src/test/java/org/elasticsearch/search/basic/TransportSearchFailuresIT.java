begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.basic
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|basic
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|WriteConsistencyLevel
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
name|search
operator|.
name|SearchPhaseExecutionException
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
name|bytes
operator|.
name|BytesArray
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
name|test
operator|.
name|ESIntegTestCase
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|*
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
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
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
name|assertAcked
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
name|*
import|;
end_import

begin_class
DECL|class|TransportSearchFailuresIT
specifier|public
class|class
name|TransportSearchFailuresIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|maximumNumberOfReplicas
specifier|protected
name|int
name|maximumNumberOfReplicas
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|// NORELEASE this needs to be done in a unit test
comment|//    @Test
comment|//    public void testFailedSearchWithWrongQuery() throws Exception {
comment|//        logger.info("Start Testing failed search with wrong query");
comment|//        assertAcked(prepareCreate("test", 1, settingsBuilder().put("routing.hash.type", "simple")));
comment|//        ensureYellow();
comment|//
comment|//        NumShards test = getNumShards("test");
comment|//
comment|//        for (int i = 0; i< 100; i++) {
comment|//            index(client(), Integer.toString(i), "test", i);
comment|//        }
comment|//        RefreshResponse refreshResponse = client().admin().indices().refresh(refreshRequest("test")).actionGet();
comment|//        assertThat(refreshResponse.getTotalShards(), equalTo(test.totalNumShards));
comment|//        assertThat(refreshResponse.getSuccessfulShards(), equalTo(test.numPrimaries));
comment|//        assertThat(refreshResponse.getFailedShards(), equalTo(0));
comment|//        for (int i = 0; i< 5; i++) {
comment|//            try {
comment|//                SearchResponse searchResponse = client().search(searchRequest("test").source(new BytesArray("{ xxx }"))).actionGet();
comment|//                assertThat(searchResponse.getTotalShards(), equalTo(test.numPrimaries));
comment|//                assertThat(searchResponse.getSuccessfulShards(), equalTo(0));
comment|//                assertThat(searchResponse.getFailedShards(), equalTo(test.numPrimaries));
comment|//                fail("search should fail");
comment|//            } catch (ElasticsearchException e) {
comment|//                assertThat(e.unwrapCause(), instanceOf(SearchPhaseExecutionException.class));
comment|//                // all is well
comment|//            }
comment|//        }
comment|//
comment|//        allowNodes("test", 2);
comment|//        assertThat(client().admin().cluster().prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForNodes(">=2").execute().actionGet().isTimedOut(), equalTo(false));
comment|//
comment|//        logger.info("Running Cluster Health");
comment|//        ClusterHealthResponse clusterHealth = client().admin().cluster().health(clusterHealthRequest("test")
comment|//                .waitForYellowStatus().waitForRelocatingShards(0).waitForActiveShards(test.totalNumShards)).actionGet();
comment|//        logger.info("Done Cluster Health, status " + clusterHealth.getStatus());
comment|//        assertThat(clusterHealth.isTimedOut(), equalTo(false));
comment|//        assertThat(clusterHealth.getStatus(), anyOf(equalTo(ClusterHealthStatus.YELLOW), equalTo(ClusterHealthStatus.GREEN)));
comment|//        assertThat(clusterHealth.getActiveShards(), equalTo(test.totalNumShards));
comment|//
comment|//        refreshResponse = client().admin().indices().refresh(refreshRequest("test")).actionGet();
comment|//        assertThat(refreshResponse.getTotalShards(), equalTo(test.totalNumShards));
comment|//        assertThat(refreshResponse.getSuccessfulShards(), equalTo(test.totalNumShards));
comment|//        assertThat(refreshResponse.getFailedShards(), equalTo(0));
comment|//
comment|//        for (int i = 0; i< 5; i++) {
comment|//            try {
comment|//                SearchResponse searchResponse = client().search(searchRequest("test").source(new BytesArray("{ xxx }"))).actionGet();
comment|//                assertThat(searchResponse.getTotalShards(), equalTo(test.numPrimaries));
comment|//                assertThat(searchResponse.getSuccessfulShards(), equalTo(0));
comment|//                assertThat(searchResponse.getFailedShards(), equalTo(test.numPrimaries));
comment|//                fail("search should fail");
comment|//            } catch (ElasticsearchException e) {
comment|//                assertThat(e.unwrapCause(), instanceOf(SearchPhaseExecutionException.class));
comment|//                // all is well
comment|//            }
comment|//        }
comment|//
comment|//        logger.info("Done Testing failed search");
comment|//    }
DECL|method|index
specifier|private
name|void
name|index
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|,
name|int
name|age
parameter_list|)
throws|throws
name|IOException
block|{
name|client
operator|.
name|index
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
name|id
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|(
name|id
argument_list|,
name|nameValue
argument_list|,
name|age
argument_list|)
argument_list|)
operator|.
name|consistencyLevel
argument_list|(
name|WriteConsistencyLevel
operator|.
name|ONE
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|source
specifier|private
name|XContentBuilder
name|source
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|,
name|int
name|age
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|multi
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|nameValue
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
name|age
condition|;
name|i
operator|++
control|)
block|{
name|multi
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|nameValue
argument_list|)
expr_stmt|;
block|}
return|return
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|nameValue
operator|+
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"age"
argument_list|,
name|age
argument_list|)
operator|.
name|field
argument_list|(
literal|"multi"
argument_list|,
name|multi
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
block|}
end_class

end_unit

