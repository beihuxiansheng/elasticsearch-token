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
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClients
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
name|util
operator|.
name|LuceneTestCase
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
name|info
operator|.
name|NodeInfo
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
name|info
operator|.
name|NodesInfoResponse
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
name|get
operator|.
name|GetIndexResponse
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
name|InetSocketTransportAddress
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
name|java
operator|.
name|io
operator|.
name|File
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|greaterThanOrEqualTo
import|;
end_import

begin_comment
comment|/**  * These tests are against static indexes, built from versions of ES that cannot be upgraded without  * a full cluster restart (ie no wire format compatibility).  */
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"MockFixedIntBlock"
block|,
literal|"MockVariableIntBlock"
block|,
literal|"MockSep"
block|,
literal|"MockRandom"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Appending"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|,
literal|"Lucene46"
block|,
literal|"Lucene49"
block|}
argument_list|)
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|,
name|minNumDataNodes
operator|=
literal|0
argument_list|,
name|maxNumDataNodes
operator|=
literal|0
argument_list|)
DECL|class|StaticIndexBackwardCompatibilityTest
specifier|public
class|class
name|StaticIndexBackwardCompatibilityTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|method|loadIndex
specifier|public
name|void
name|loadIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|Object
modifier|...
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Checking static index "
operator|+
name|index
argument_list|)
expr_stmt|;
name|Settings
name|nodeSettings
init|=
name|prepareBackwardsDataDir
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|index
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertIndexSanity
argument_list|()
expr_stmt|;
block|}
DECL|method|unloadIndex
specifier|public
name|void
name|unloadIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|ElasticsearchAssertions
operator|.
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
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
condition|)
block|{}
comment|// stop all data nodes
block|}
DECL|method|assertIndexSanity
name|void
name|assertIndexSanity
parameter_list|()
block|{
name|GetIndexResponse
name|getIndexResponse
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
name|prepareGetIndex
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getIndexResponse
operator|.
name|indices
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|getIndexResponse
operator|.
name|indices
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|SearchResponse
name|test
init|=
name|client
argument_list|()
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
name|test
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|httpClient
specifier|protected
specifier|static
name|HttpRequestBuilder
name|httpClient
parameter_list|()
block|{
name|NodeInfo
name|info
init|=
name|nodeInfo
argument_list|(
name|client
argument_list|()
argument_list|)
decl_stmt|;
name|info
operator|.
name|getHttp
argument_list|()
operator|.
name|address
argument_list|()
operator|.
name|boundAddress
argument_list|()
expr_stmt|;
name|TransportAddress
name|publishAddress
init|=
name|info
operator|.
name|getHttp
argument_list|()
operator|.
name|address
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|publishAddress
operator|.
name|uniqueAddressTypeId
argument_list|()
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|address
init|=
operator|(
operator|(
name|InetSocketTransportAddress
operator|)
name|publishAddress
operator|)
operator|.
name|address
argument_list|()
decl_stmt|;
return|return
operator|new
name|HttpRequestBuilder
argument_list|(
name|HttpClients
operator|.
name|createDefault
argument_list|()
argument_list|)
operator|.
name|host
argument_list|(
name|address
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|port
argument_list|(
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeInfo
specifier|static
name|NodeInfo
name|nodeInfo
parameter_list|(
specifier|final
name|Client
name|client
parameter_list|)
block|{
specifier|final
name|NodesInfoResponse
name|nodeInfos
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|NodeInfo
index|[]
name|nodes
init|=
name|nodeInfos
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|nodes
index|[
literal|0
index|]
return|;
block|}
block|}
end_class

end_unit

