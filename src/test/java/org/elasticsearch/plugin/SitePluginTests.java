begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
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
name|RestStatus
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|ImmutableSettings
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
name|test
operator|.
name|ElasticsearchIntegrationTest
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
name|equalTo
import|;
end_import

begin_comment
comment|/**  * We want to test site plugins  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|SUITE
argument_list|,
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|SitePluginTests
specifier|public
class|class
name|SitePluginTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
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
try|try
block|{
name|File
name|pluginDir
init|=
operator|new
name|File
argument_list|(
name|SitePluginTests
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/org/elasticsearch/plugin"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|settingsBuilder
argument_list|()
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
name|put
argument_list|(
literal|"path.plugins"
argument_list|,
name|pluginDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"force.http.enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|httpClient
specifier|public
name|HttpClient
name|httpClient
parameter_list|()
block|{
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
return|return
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
return|;
block|}
annotation|@
name|Test
DECL|method|testRedirectSitePlugin
specifier|public
name|void
name|testRedirectSitePlugin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We use an HTTP Client to test redirection
name|HttpClientResponse
name|response
init|=
name|httpClient
argument_list|()
operator|.
name|request
argument_list|(
literal|"/_plugin/dummy"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|errorCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|MOVED_PERMANENTLY
operator|.
name|getStatus
argument_list|()
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
literal|"/_plugin/dummy/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// We test the real URL
name|response
operator|=
name|httpClient
argument_list|()
operator|.
name|request
argument_list|(
literal|"/_plugin/dummy/"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|errorCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|OK
operator|.
name|getStatus
argument_list|()
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
literal|"<title>Dummy Site Plugin</title>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test direct access to an existing file (index.html)      */
annotation|@
name|Test
DECL|method|testAnyPage
specifier|public
name|void
name|testAnyPage
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpClientResponse
name|response
init|=
name|httpClient
argument_list|()
operator|.
name|request
argument_list|(
literal|"/_plugin/dummy/index.html"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|errorCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|OK
operator|.
name|getStatus
argument_list|()
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
literal|"<title>Dummy Site Plugin</title>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for #4845: https://github.com/elasticsearch/elasticsearch/issues/4845      * Serving _site plugins do not pick up on index.html for sub directories      */
annotation|@
name|Test
DECL|method|testWelcomePageInSubDirs
specifier|public
name|void
name|testWelcomePageInSubDirs
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpClientResponse
name|response
init|=
name|httpClient
argument_list|()
operator|.
name|request
argument_list|(
literal|"/_plugin/subdir/dir/"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|errorCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|OK
operator|.
name|getStatus
argument_list|()
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
literal|"<title>Dummy Site Plugin (subdir)</title>"
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|httpClient
argument_list|()
operator|.
name|request
argument_list|(
literal|"/_plugin/subdir/dir_without_index/"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|errorCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|FORBIDDEN
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|httpClient
argument_list|()
operator|.
name|request
argument_list|(
literal|"/_plugin/subdir/dir_without_index/page.html"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|errorCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|OK
operator|.
name|getStatus
argument_list|()
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
literal|"<title>Dummy Site Plugin (page)</title>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

