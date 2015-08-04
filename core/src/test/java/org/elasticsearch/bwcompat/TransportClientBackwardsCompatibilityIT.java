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
name|IndexRequestBuilder
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
name|transport
operator|.
name|TransportClient
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
name|ClusterName
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
name|transport
operator|.
name|TransportAddress
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
name|test
operator|.
name|CompositeTestCluster
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
name|ESBackcompatTestCase
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
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|assertSearchHits
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|TransportClientBackwardsCompatibilityIT
specifier|public
class|class
name|TransportClientBackwardsCompatibilityIT
extends|extends
name|ESBackcompatTestCase
block|{
annotation|@
name|Test
DECL|method|testSniffMode
specifier|public
name|void
name|testSniffMode
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|requiredSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"client.transport.nodes_sampler_interval"
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"transport_client_sniff_mode"
argument_list|)
operator|.
name|put
argument_list|(
name|ClusterName
operator|.
name|SETTING
argument_list|,
name|cluster
argument_list|()
operator|.
name|getClusterName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"client.transport.sniff"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CompositeTestCluster
name|compositeTestCluster
init|=
name|backwardsCluster
argument_list|()
decl_stmt|;
name|TransportAddress
name|transportAddress
init|=
name|compositeTestCluster
operator|.
name|externalTransportAddress
argument_list|()
decl_stmt|;
try|try
init|(
name|TransportClient
name|client
init|=
name|TransportClient
operator|.
name|builder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|client
operator|.
name|addTransportAddress
argument_list|(
name|transportAddress
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|iterations
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|IndexRequestBuilder
index|[]
name|indexRequestBuilders
init|=
operator|new
name|IndexRequestBuilder
index|[
name|numDocs
index|]
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
literal|"id"
operator|+
name|i
decl_stmt|;
name|indexRequestBuilders
index|[
name|i
index|]
operator|=
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|id
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|false
argument_list|,
name|indexRequestBuilders
argument_list|)
expr_stmt|;
name|String
name|randomId
init|=
literal|"id"
operator|+
name|randomInt
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|)
decl_stmt|;
name|GetResponse
name|getResponse
init|=
name|client
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|randomId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|randomDocId
init|=
name|randomInt
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
name|fieldValue
init|=
literal|"value"
operator|+
name|randomDocId
decl_stmt|;
name|String
name|id
init|=
literal|"id"
operator|+
name|randomDocId
decl_stmt|;
name|searchResponse
operator|=
name|client
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
name|termQuery
argument_list|(
literal|"field"
argument_list|,
name|fieldValue
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

