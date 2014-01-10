begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|test
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
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|spec
operator|.
name|RestApi
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
name|spec
operator|.
name|RestApiParser
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
name|equalTo
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
name|notNullValue
import|;
end_import

begin_class
DECL|class|RestApiParserTests
specifier|public
class|class
name|RestApiParserTests
extends|extends
name|AbstractParserTests
block|{
annotation|@
name|Test
DECL|method|testParseRestSpecIndexApi
specifier|public
name|void
name|testParseRestSpecIndexApi
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|REST_SPEC_INDEX_API
argument_list|)
expr_stmt|;
name|RestApi
name|restApi
init|=
operator|new
name|RestApiParser
argument_list|()
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|restApi
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"POST"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"PUT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"/{index}/{type}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"/{index}/{type}/{id}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseRestSpecGetTemplateApi
specifier|public
name|void
name|testParseRestSpecGetTemplateApi
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|REST_SPEC_GET_TEMPLATE_API
argument_list|)
expr_stmt|;
name|RestApi
name|restApi
init|=
operator|new
name|RestApiParser
argument_list|()
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|restApi
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"indices.get_template"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"GET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"/_template"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"/_template/{name}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|REST_SPEC_GET_TEMPLATE_API
specifier|private
specifier|static
specifier|final
name|String
name|REST_SPEC_GET_TEMPLATE_API
init|=
literal|"{\n"
operator|+
literal|"  \"indices.get_template\": {\n"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/reference/api/admin-indices-templates/\",\n"
operator|+
literal|"    \"methods\": [\"GET\"],\n"
operator|+
literal|"    \"url\": {\n"
operator|+
literal|"      \"path\": \"/_template/{name}\",\n"
operator|+
literal|"      \"paths\": [\"/_template\", \"/_template/{name}\"],\n"
operator|+
literal|"      \"parts\": {\n"
operator|+
literal|"        \"name\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"required\" : false,\n"
operator|+
literal|"          \"description\" : \"The name of the template\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"      },\n"
operator|+
literal|"      \"params\": {\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"body\": null\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
DECL|field|REST_SPEC_INDEX_API
specifier|private
specifier|static
specifier|final
name|String
name|REST_SPEC_INDEX_API
init|=
literal|"{\n"
operator|+
literal|"  \"index\": {\n"
operator|+
literal|"    \"documentation\": \"http://elasticsearch.org/guide/reference/api/index_/\",\n"
operator|+
literal|"    \"methods\": [\"POST\", \"PUT\"],\n"
operator|+
literal|"    \"url\": {\n"
operator|+
literal|"      \"path\": \"/{index}/{type}\",\n"
operator|+
literal|"      \"paths\": [\"/{index}/{type}\", \"/{index}/{type}/{id}\"],\n"
operator|+
literal|"      \"parts\": {\n"
operator|+
literal|"        \"id\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"Document ID\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"index\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"required\" : true,\n"
operator|+
literal|"          \"description\" : \"The name of the index\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"type\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"required\" : true,\n"
operator|+
literal|"          \"description\" : \"The type of the document\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }   ,\n"
operator|+
literal|"      \"params\": {\n"
operator|+
literal|"        \"consistency\": {\n"
operator|+
literal|"          \"type\" : \"enum\",\n"
operator|+
literal|"          \"options\" : [\"one\", \"quorum\", \"all\"],\n"
operator|+
literal|"          \"description\" : \"Explicit write consistency setting for the operation\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"op_type\": {\n"
operator|+
literal|"          \"type\" : \"enum\",\n"
operator|+
literal|"          \"options\" : [\"index\", \"create\"],\n"
operator|+
literal|"          \"default\" : \"index\",\n"
operator|+
literal|"          \"description\" : \"Explicit operation type\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"parent\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"ID of the parent document\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"percolate\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"Percolator queries to execute while indexing the document\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"refresh\": {\n"
operator|+
literal|"          \"type\" : \"boolean\",\n"
operator|+
literal|"          \"description\" : \"Refresh the index after performing the operation\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"replication\": {\n"
operator|+
literal|"          \"type\" : \"enum\",\n"
operator|+
literal|"          \"options\" : [\"sync\",\"async\"],\n"
operator|+
literal|"          \"default\" : \"sync\",\n"
operator|+
literal|"          \"description\" : \"Specific replication type\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"routing\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"Specific routing value\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"timeout\": {\n"
operator|+
literal|"          \"type\" : \"time\",\n"
operator|+
literal|"          \"description\" : \"Explicit operation timeout\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"timestamp\": {\n"
operator|+
literal|"          \"type\" : \"time\",\n"
operator|+
literal|"          \"description\" : \"Explicit timestamp for the document\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"ttl\": {\n"
operator|+
literal|"          \"type\" : \"duration\",\n"
operator|+
literal|"          \"description\" : \"Expiration time for the document\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"version\" : {\n"
operator|+
literal|"          \"type\" : \"number\",\n"
operator|+
literal|"          \"description\" : \"Explicit version number for concurrency control\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"version_type\": {\n"
operator|+
literal|"          \"type\" : \"enum\",\n"
operator|+
literal|"          \"options\" : [\"internal\",\"external\"],\n"
operator|+
literal|"          \"description\" : \"Specific version type\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"body\": {\n"
operator|+
literal|"      \"description\" : \"The document\",\n"
operator|+
literal|"      \"required\" : true\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
decl_stmt|;
block|}
end_class

end_unit

