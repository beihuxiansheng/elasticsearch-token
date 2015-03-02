begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.count
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|count
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
name|support
operator|.
name|QuerySourceBuilder
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentHelper
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
name|XContentType
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
name|ElasticsearchTestCase
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|CountRequestBuilderTests
specifier|public
class|class
name|CountRequestBuilderTests
extends|extends
name|ElasticsearchTestCase
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
name|client
operator|=
operator|new
name|TransportClient
argument_list|()
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
annotation|@
name|Test
DECL|method|testEmptySourceToString
specifier|public
name|void
name|testEmptySourceToString
parameter_list|()
block|{
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|QuerySourceBuilder
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryBuilderQueryToString
specifier|public
name|void
name|testQueryBuilderQueryToString
parameter_list|()
block|{
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|countRequestBuilder
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
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|QuerySourceBuilder
argument_list|()
operator|.
name|setQuery
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
annotation|@
name|Test
DECL|method|testStringQueryToString
specifier|public
name|void
name|testStringQueryToString
parameter_list|()
block|{
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"{ \"match_all\" : {} }"
decl_stmt|;
name|countRequestBuilder
operator|.
name|setQuery
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\n  \"query\":{ \"match_all\" : {} }\n}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testXContentBuilderQueryToString
specifier|public
name|void
name|testXContentBuilderQueryToString
parameter_list|()
throws|throws
name|IOException
block|{
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|XContentBuilder
name|xContentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"match_all"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|countRequestBuilder
operator|.
name|setQuery
argument_list|(
name|xContentBuilder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|QuerySourceBuilder
argument_list|()
operator|.
name|setQuery
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStringSourceToString
specifier|public
name|void
name|testStringSourceToString
parameter_list|()
block|{
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"{ \"query\": { \"match_all\" : {} } }"
decl_stmt|;
name|countRequestBuilder
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{ \"query\": { \"match_all\" : {} } }"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testXContentBuilderSourceToString
specifier|public
name|void
name|testXContentBuilderSourceToString
parameter_list|()
throws|throws
name|IOException
block|{
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|XContentBuilder
name|xContentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"match_all"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|countRequestBuilder
operator|.
name|setSource
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatToStringDoesntWipeSource
specifier|public
name|void
name|testThatToStringDoesntWipeSource
parameter_list|()
block|{
name|String
name|source
init|=
literal|"{\n"
operator|+
literal|"            \"query\" : {\n"
operator|+
literal|"            \"match\" : {\n"
operator|+
literal|"                \"field\" : {\n"
operator|+
literal|"                    \"query\" : \"value\""
operator|+
literal|"                }\n"
operator|+
literal|"            }\n"
operator|+
literal|"        }\n"
operator|+
literal|"        }"
decl_stmt|;
name|CountRequestBuilder
name|countRequestBuilder
init|=
operator|new
name|CountRequestBuilder
argument_list|(
name|client
argument_list|)
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|source
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|preToString
init|=
name|countRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|countRequestBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|postToString
init|=
name|countRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|toUtf8
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

