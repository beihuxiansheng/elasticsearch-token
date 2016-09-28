begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.smoketest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|smoketest
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
name|Constants
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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

begin_class
DECL|class|SmokeTestClientIT
specifier|public
class|class
name|SmokeTestClientIT
extends|extends
name|ESSmokeClientTestCase
block|{
comment|// needed to avoid the test suite from failing for having no tests
comment|// TODO: remove when Netty 4.1.5 is upgraded to Netty 4.1.6 including https://github.com/netty/netty/pull/5778
DECL|method|testSoThatTestsDoNotFail
specifier|public
name|void
name|testSoThatTestsDoNotFail
parameter_list|()
block|{      }
comment|/**      * Check that we are connected to a cluster named "elasticsearch".      */
DECL|method|testSimpleClient
specifier|public
name|void
name|testSimpleClient
parameter_list|()
block|{
comment|// TODO: remove when Netty 4.1.5 is upgraded to Netty 4.1.6 including https://github.com/netty/netty/pull/5778
name|assumeFalse
argument_list|(
literal|"JDK is JDK 9"
argument_list|,
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA9
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|getClient
argument_list|()
decl_stmt|;
comment|// START SNIPPET: java-doc-admin-cluster-health
name|ClusterHealthResponse
name|health
init|=
name|client
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
name|setWaitForYellowStatus
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|clusterName
init|=
name|health
operator|.
name|getClusterName
argument_list|()
decl_stmt|;
name|int
name|numberOfNodes
init|=
name|health
operator|.
name|getNumberOfNodes
argument_list|()
decl_stmt|;
comment|// END SNIPPET: java-doc-admin-cluster-health
name|assertThat
argument_list|(
literal|"cluster ["
operator|+
name|clusterName
operator|+
literal|"] should have at least 1 node"
argument_list|,
name|numberOfNodes
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create an index and index some docs      */
DECL|method|testPutDocument
specifier|public
name|void
name|testPutDocument
parameter_list|()
block|{
comment|// TODO: remove when Netty 4.1.5 is upgraded to Netty 4.1.6 including https://github.com/netty/netty/pull/5778
name|assumeFalse
argument_list|(
literal|"JDK is JDK 9"
argument_list|,
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA9
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|getClient
argument_list|()
decl_stmt|;
comment|// START SNIPPET: java-doc-index-doc-simple
name|client
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
comment|// Index, Type, Id
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
comment|// Simple document: { "foo" : "bar" }
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Execute and wait for the result
comment|// END SNIPPET: java-doc-index-doc-simple
comment|// START SNIPPET: java-doc-admin-indices-refresh
comment|// Prepare a refresh action on a given index, execute and wait for the result
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
name|index
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// END SNIPPET: java-doc-admin-indices-refresh
comment|// START SNIPPET: java-doc-search-simple
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|index
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
name|getTotalHits
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// END SNIPPET: java-doc-search-simple
block|}
block|}
end_class

end_unit

