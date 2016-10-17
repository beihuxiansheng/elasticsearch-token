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
name|XContentParser
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
name|ESTestCase
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
name|restspec
operator|.
name|ClientYamlSuiteRestApiParser
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
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_comment
comment|/**  * These tests are not part of {@link ClientYamlSuiteRestApiParserTests} because the tested failures don't allow to consume the whole yaml  * stream  */
end_comment

begin_class
DECL|class|ClientYamlSuiteRestApiParserFailingTests
specifier|public
class|class
name|ClientYamlSuiteRestApiParserFailingTests
extends|extends
name|ESTestCase
block|{
DECL|method|testDuplicateMethods
specifier|public
name|void
name|testDuplicateMethods
parameter_list|()
throws|throws
name|Exception
block|{
name|parseAndExpectFailure
argument_list|(
literal|"{\n"
operator|+
literal|"  \"ping\": {"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/\","
operator|+
literal|"    \"methods\": [\"PUT\", \"PUT\"],"
operator|+
literal|"    \"url\": {"
operator|+
literal|"      \"path\": \"/\","
operator|+
literal|"      \"paths\": [\"/\"],"
operator|+
literal|"      \"parts\": {"
operator|+
literal|"      },"
operator|+
literal|"      \"params\": {"
operator|+
literal|"        \"type\" : \"boolean\",\n"
operator|+
literal|"        \"description\" : \"Whether specified concrete indices should be ignored when unavailable (missing or closed)\""
operator|+
literal|"      }"
operator|+
literal|"    },"
operator|+
literal|"    \"body\": null"
operator|+
literal|"  }"
operator|+
literal|"}"
argument_list|,
literal|"Found duplicate method [PUT]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicatePaths
specifier|public
name|void
name|testDuplicatePaths
parameter_list|()
throws|throws
name|Exception
block|{
name|parseAndExpectFailure
argument_list|(
literal|"{\n"
operator|+
literal|"  \"ping\": {"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/\","
operator|+
literal|"    \"methods\": [\"PUT\"],"
operator|+
literal|"    \"url\": {"
operator|+
literal|"      \"path\": \"/pingone\","
operator|+
literal|"      \"paths\": [\"/pingone\", \"/pingtwo\", \"/pingtwo\"],"
operator|+
literal|"      \"parts\": {"
operator|+
literal|"      },"
operator|+
literal|"      \"params\": {"
operator|+
literal|"        \"type\" : \"boolean\",\n"
operator|+
literal|"        \"description\" : \"Whether specified concrete indices should be ignored when unavailable (missing or closed)\""
operator|+
literal|"      }"
operator|+
literal|"    },"
operator|+
literal|"    \"body\": null"
operator|+
literal|"  }"
operator|+
literal|"}"
argument_list|,
literal|"Found duplicate path [/pingtwo]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicateParts
specifier|public
name|void
name|testDuplicateParts
parameter_list|()
throws|throws
name|Exception
block|{
name|parseAndExpectFailure
argument_list|(
literal|"{\n"
operator|+
literal|"  \"ping\": {"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/\","
operator|+
literal|"    \"methods\": [\"PUT\"],"
operator|+
literal|"    \"url\": {"
operator|+
literal|"      \"path\": \"/\","
operator|+
literal|"      \"paths\": [\"/\"],"
operator|+
literal|"      \"parts\": {"
operator|+
literal|"        \"index\": {"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"index part\"\n"
operator|+
literal|"        },"
operator|+
literal|"        \"type\": {"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"type part\"\n"
operator|+
literal|"        },"
operator|+
literal|"        \"index\": {"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"index parameter part\"\n"
operator|+
literal|"        }"
operator|+
literal|"      },"
operator|+
literal|"      \"params\": {"
operator|+
literal|"        \"type\" : \"boolean\",\n"
operator|+
literal|"        \"description\" : \"Whether specified concrete indices should be ignored when unavailable (missing or closed)\""
operator|+
literal|"      }"
operator|+
literal|"    },"
operator|+
literal|"    \"body\": null"
operator|+
literal|"  }"
operator|+
literal|"}"
argument_list|,
literal|"Found duplicate part [index]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicateParams
specifier|public
name|void
name|testDuplicateParams
parameter_list|()
throws|throws
name|Exception
block|{
name|parseAndExpectFailure
argument_list|(
literal|"{\n"
operator|+
literal|"  \"ping\": {"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/\","
operator|+
literal|"    \"methods\": [\"PUT\"],"
operator|+
literal|"    \"url\": {"
operator|+
literal|"      \"path\": \"/\","
operator|+
literal|"      \"paths\": [\"/\"],"
operator|+
literal|"      \"parts\": {"
operator|+
literal|"      },"
operator|+
literal|"      \"params\": {"
operator|+
literal|"        \"timeout\": {"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"timeout parameter\"\n"
operator|+
literal|"        },"
operator|+
literal|"        \"refresh\": {"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"refresh parameter\"\n"
operator|+
literal|"        },"
operator|+
literal|"        \"timeout\": {"
operator|+
literal|"          \"type\" : \"string\",\n"
operator|+
literal|"          \"description\" : \"timeout parameter again\"\n"
operator|+
literal|"        }"
operator|+
literal|"      }"
operator|+
literal|"    },"
operator|+
literal|"    \"body\": null"
operator|+
literal|"  }"
operator|+
literal|"}"
argument_list|,
literal|"Found duplicate param [timeout]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBrokenSpecShouldThrowUsefulExceptionWhenParsingFailsOnParams
specifier|public
name|void
name|testBrokenSpecShouldThrowUsefulExceptionWhenParsingFailsOnParams
parameter_list|()
throws|throws
name|Exception
block|{
name|parseAndExpectFailure
argument_list|(
name|BROKEN_SPEC_PARAMS
argument_list|,
literal|"Expected params field in rest api definition to contain an object"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBrokenSpecShouldThrowUsefulExceptionWhenParsingFailsOnParts
specifier|public
name|void
name|testBrokenSpecShouldThrowUsefulExceptionWhenParsingFailsOnParts
parameter_list|()
throws|throws
name|Exception
block|{
name|parseAndExpectFailure
argument_list|(
name|BROKEN_SPEC_PARTS
argument_list|,
literal|"Expected parts field in rest api definition to contain an object"
argument_list|)
expr_stmt|;
block|}
DECL|method|parseAndExpectFailure
specifier|private
name|void
name|parseAndExpectFailure
parameter_list|(
name|String
name|brokenJson
parameter_list|,
name|String
name|expectedErrorMessage
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|brokenJson
argument_list|)
decl_stmt|;
name|ClientYamlSuiteRestApiParser
name|restApiParser
init|=
operator|new
name|ClientYamlSuiteRestApiParser
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|restApiParser
operator|.
name|parse
argument_list|(
literal|"location"
argument_list|,
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|expectedErrorMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// see params section is broken, an inside param is missing
DECL|field|BROKEN_SPEC_PARAMS
specifier|private
specifier|static
specifier|final
name|String
name|BROKEN_SPEC_PARAMS
init|=
literal|"{\n"
operator|+
literal|"  \"ping\": {"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/\","
operator|+
literal|"    \"methods\": [\"HEAD\"],"
operator|+
literal|"    \"url\": {"
operator|+
literal|"      \"path\": \"/\","
operator|+
literal|"      \"paths\": [\"/\"],"
operator|+
literal|"      \"parts\": {"
operator|+
literal|"      },"
operator|+
literal|"      \"params\": {"
operator|+
literal|"        \"type\" : \"boolean\",\n"
operator|+
literal|"        \"description\" : \"Whether specified concrete indices should be ignored when unavailable (missing or closed)\"\n"
operator|+
literal|"      }"
operator|+
literal|"    },"
operator|+
literal|"    \"body\": null"
operator|+
literal|"  }"
operator|+
literal|"}"
decl_stmt|;
comment|// see parts section is broken, an inside param is missing
DECL|field|BROKEN_SPEC_PARTS
specifier|private
specifier|static
specifier|final
name|String
name|BROKEN_SPEC_PARTS
init|=
literal|"{\n"
operator|+
literal|"  \"ping\": {"
operator|+
literal|"    \"documentation\": \"http://www.elasticsearch.org/guide/\","
operator|+
literal|"    \"methods\": [\"HEAD\"],"
operator|+
literal|"    \"url\": {"
operator|+
literal|"      \"path\": \"/\","
operator|+
literal|"      \"paths\": [\"/\"],"
operator|+
literal|"      \"parts\": {"
operator|+
literal|"          \"type\" : \"boolean\",\n"
operator|+
literal|"      },"
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
literal|"    },"
operator|+
literal|"    \"body\": null"
operator|+
literal|"  }"
operator|+
literal|"}"
decl_stmt|;
block|}
end_class

end_unit

