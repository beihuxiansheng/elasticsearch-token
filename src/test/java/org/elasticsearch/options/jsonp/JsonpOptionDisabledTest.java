begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.options.jsonp
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|options
operator|.
name|jsonp
package|;
end_package

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
name|http
operator|.
name|HttpServerTransport
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
name|RestController
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
name|helper
operator|.
name|HttpClient
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
name|helper
operator|.
name|HttpClientResponse
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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

begin_comment
comment|// Test to make sure that our JSONp response is disabled
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
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|JsonpOptionDisabledTest
specifier|public
class|class
name|JsonpOptionDisabledTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|// Build our cluster settings
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
comment|// false is the default!
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"using default jsonp settings (should be false)"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
return|;
block|}
return|return
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|RestController
operator|.
name|HTTP_JSON_ENABLE
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|// Make sure our response has both the callback as well as our "JSONP is disabled" message.
annotation|@
name|Test
DECL|method|testThatJSONPisDisabled
specifier|public
name|void
name|testThatJSONPisDisabled
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make the HTTP request
name|HttpServerTransport
name|httpServerTransport
init|=
name|internalCluster
argument_list|()
operator|.
name|getDataNodeInstance
argument_list|(
name|HttpServerTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|(
name|httpServerTransport
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|)
decl_stmt|;
name|HttpClientResponse
name|response
init|=
name|httpClient
operator|.
name|request
argument_list|(
literal|"/?callback=DisabledJSONPCallback"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"application/javascript"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|response
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"DisabledJSONPCallback("
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|response
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"JSONP is disabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

