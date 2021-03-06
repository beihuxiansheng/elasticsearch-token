begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

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
name|env
operator|.
name|Environment
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
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
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
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|MockTransportClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|SearchRequestBuilderTests
specifier|public
class|class
name|SearchRequestBuilderTests
extends|extends
name|ESTestCase
block|{
DECL|field|client
specifier|private
specifier|static
name|Client
name|client
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|initClient
specifier|public
specifier|static
name|void
name|initClient
parameter_list|()
block|{
comment|//this client will not be hit by any request, but it needs to be a non null proper client
comment|//that is why we create it but we don't add any transport address to it
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
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|=
operator|new
name|MockTransportClient
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|closeClient
specifier|public
specifier|static
name|void
name|closeClient
parameter_list|()
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|client
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testEmptySourceToString
specifier|public
name|void
name|testEmptySourceToString
parameter_list|()
block|{
name|SearchRequestBuilder
name|searchRequestBuilder
init|=
name|client
operator|.
name|prepareSearch
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|searchRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueryBuilderQueryToString
specifier|public
name|void
name|testQueryBuilderQueryToString
parameter_list|()
block|{
name|SearchRequestBuilder
name|searchRequestBuilder
init|=
name|client
operator|.
name|prepareSearch
argument_list|()
decl_stmt|;
name|searchRequestBuilder
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSearchSourceBuilderToString
specifier|public
name|void
name|testSearchSourceBuilderToString
parameter_list|()
block|{
name|SearchRequestBuilder
name|searchRequestBuilder
init|=
name|client
operator|.
name|prepareSearch
argument_list|()
decl_stmt|;
name|searchRequestBuilder
operator|.
name|setSource
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatToStringDoesntWipeRequestSource
specifier|public
name|void
name|testThatToStringDoesntWipeRequestSource
parameter_list|()
block|{
name|SearchRequestBuilder
name|searchRequestBuilder
init|=
name|client
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setSource
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|preToString
init|=
name|searchRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|searchRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|postToString
init|=
name|searchRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|preToString
argument_list|,
name|equalTo
argument_list|(
name|postToString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

