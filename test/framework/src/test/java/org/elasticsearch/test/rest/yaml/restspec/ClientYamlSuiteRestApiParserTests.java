begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.yaml.restspec
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|yaml
operator|.
name|restspec
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
name|yaml
operator|.
name|YamlXContent
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
name|yaml
operator|.
name|parser
operator|.
name|AbstractClientYamlTestFragmentParserTestCase
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
name|contains
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
DECL|class|ClientYamlSuiteRestApiParserTests
specifier|public
class|class
name|ClientYamlSuiteRestApiParserTests
extends|extends
name|AbstractClientYamlTestFragmentParserTestCase
block|{
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
name|createParser
argument_list|(
name|YamlXContent
operator|.
name|yamlXContent
argument_list|,
name|REST_SPEC_INDEX_API
argument_list|)
expr_stmt|;
name|ClientYamlSuiteRestApi
name|restApi
init|=
operator|new
name|ClientYamlSuiteRestApiParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"location"
argument_list|,
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
name|assertThat
argument_list|(
name|restApi
operator|.
name|getParams
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getParams
argument_list|()
argument_list|,
name|contains
argument_list|(
literal|"wait_for_active_shards"
argument_list|,
literal|"op_type"
argument_list|,
literal|"parent"
argument_list|,
literal|"refresh"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|isBodySupported
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|isBodyRequired
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|createParser
argument_list|(
name|YamlXContent
operator|.
name|yamlXContent
argument_list|,
name|REST_SPEC_GET_TEMPLATE_API
argument_list|)
expr_stmt|;
name|ClientYamlSuiteRestApi
name|restApi
init|=
operator|new
name|ClientYamlSuiteRestApiParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"location"
argument_list|,
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
name|assertThat
argument_list|(
name|restApi
operator|.
name|getParams
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|isBodySupported
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|isBodyRequired
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseRestSpecCountApi
specifier|public
name|void
name|testParseRestSpecCountApi
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|createParser
argument_list|(
name|YamlXContent
operator|.
name|yamlXContent
argument_list|,
name|REST_SPEC_COUNT_API
argument_list|)
expr_stmt|;
name|ClientYamlSuiteRestApi
name|restApi
init|=
operator|new
name|ClientYamlSuiteRestApiParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"location"
argument_list|,
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
literal|"count"
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
literal|3
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
literal|"/_count"
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
literal|"/{index}/_count"
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
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"/{index}/{type}/_count"
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
literal|2
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
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|getParams
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
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"ignore_unavailable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|isBodySupported
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|restApi
operator|.
name|isBodyRequired
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|REST_SPEC_COUNT_API
specifier|private
specifier|static
specifier|final
name|String
name|REST_SPEC_COUNT_API
init|=
literal|"{\n"
operator|+
literal|"  \"count\": {\n"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-count.html\",\n"
operator|+
literal|"    \"methods\": [\"POST\", \"GET\"],\n"
operator|+
literal|"    \"url\": {\n"
operator|+
literal|"      \"path\": \"/_count\",\n"
operator|+
literal|"      \"paths\": [\"/_count\", \"/{index}/_count\", \"/{index}/{type}/_count\"],\n"
operator|+
literal|"      \"parts\": {\n"
operator|+
literal|"        \"index\": {\n"
operator|+
literal|"          \"type\" : \"list\",\n"
operator|+
literal|"          \"description\" : \"A comma-separated list of indices to restrict the results\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"type\": {\n"
operator|+
literal|"          \"type\" : \"list\",\n"
operator|+
literal|"          \"description\" : \"A comma-separated list of types to restrict the results\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"      },\n"
operator|+
literal|"      \"params\": {\n"
operator|+
literal|"        \"ignore_unavailable\": {\n"
operator|+
literal|"          \"type\" : \"boolean\",\n"
operator|+
literal|"          \"description\" : \"Whether specified concrete indices should be ignored when unavailable (missing or closed)\"\n"
operator|+
literal|"        } \n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"body\": {\n"
operator|+
literal|"      \"description\" : \"A query to restrict the results specified with the Query DSL (optional)\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
decl_stmt|;
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
literal|"        \"wait_for_active_shards\": {\n"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"The number of active shard copies required to perform the operation\"\n"
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
literal|"        \"refresh\": {\n"
operator|+
literal|"          \"type\" : \"boolean\",\n"
operator|+
literal|"          \"description\" : \"Refresh the index after performing the operation\"\n"
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

