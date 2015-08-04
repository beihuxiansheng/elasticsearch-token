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
name|Version
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
name|parser
operator|.
name|RestTestSectionParser
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
name|parser
operator|.
name|RestTestSuiteParseContext
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
name|section
operator|.
name|*
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
name|Map
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
name|*
import|;
end_import

begin_class
DECL|class|TestSectionParserTests
specifier|public
class|class
name|TestSectionParserTests
extends|extends
name|AbstractParserTestCase
block|{
annotation|@
name|Test
DECL|method|testParseTestSectionWithDoSection
specifier|public
name|void
name|testParseTestSectionWithDoSection
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|YamlXContent
operator|.
name|yamlXContent
operator|.
name|createParser
argument_list|(
literal|"\"First test section\": \n"
operator|+
literal|" - do :\n"
operator|+
literal|"     catch: missing\n"
operator|+
literal|"     indices.get_warmer:\n"
operator|+
literal|"         index: test_index\n"
operator|+
literal|"         name: test_warmer"
argument_list|)
expr_stmt|;
name|RestTestSectionParser
name|testSectionParser
init|=
operator|new
name|RestTestSectionParser
argument_list|()
decl_stmt|;
name|TestSection
name|testSection
init|=
name|testSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|RestTestSuiteParseContext
argument_list|(
literal|"api"
argument_list|,
literal|"suite"
argument_list|,
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"First test section"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SkipSection
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getExecutableSections
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
name|DoSection
name|doSection
init|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"missing"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"indices.get_warmer"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseTestSectionWithDoSetAndSkipSectionsNoSkip
specifier|public
name|void
name|testParseTestSectionWithDoSetAndSkipSectionsNoSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|yaml
init|=
literal|"\"First test section\": \n"
operator|+
literal|"  - skip:\n"
operator|+
literal|"      version:  \"0.90.0 - 0.90.7\"\n"
operator|+
literal|"      reason:   \"Update doesn't return metadata fields, waiting for #3259\"\n"
operator|+
literal|"  - do :\n"
operator|+
literal|"      catch: missing\n"
operator|+
literal|"      indices.get_warmer:\n"
operator|+
literal|"          index: test_index\n"
operator|+
literal|"          name: test_warmer\n"
operator|+
literal|"  - set: {_scroll_id: scroll_id}"
decl_stmt|;
name|RestTestSectionParser
name|testSectionParser
init|=
operator|new
name|RestTestSectionParser
argument_list|()
decl_stmt|;
name|parser
operator|=
name|YamlXContent
operator|.
name|yamlXContent
operator|.
name|createParser
argument_list|(
name|yaml
argument_list|)
expr_stmt|;
name|TestSection
name|testSection
init|=
name|testSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|RestTestSuiteParseContext
argument_list|(
literal|"api"
argument_list|,
literal|"suite"
argument_list|,
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"First test section"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
operator|.
name|getLowerVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_0_90_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
operator|.
name|getUpperVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_0_90_7
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
operator|.
name|getReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Update doesn't return metadata fields, waiting for #3259"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getExecutableSections
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
name|DoSection
name|doSection
init|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"missing"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"indices.get_warmer"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|SetSection
name|setSection
init|=
operator|(
name|SetSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|setSection
operator|.
name|getStash
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
name|setSection
operator|.
name|getStash
argument_list|()
operator|.
name|get
argument_list|(
literal|"_scroll_id"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"scroll_id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseTestSectionWithMultipleDoSections
specifier|public
name|void
name|testParseTestSectionWithMultipleDoSections
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|YamlXContent
operator|.
name|yamlXContent
operator|.
name|createParser
argument_list|(
literal|"\"Basic\":\n"
operator|+
literal|"\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      index:\n"
operator|+
literal|"        index: test_1\n"
operator|+
literal|"        type:  test\n"
operator|+
literal|"        id:    ä¸­æ\n"
operator|+
literal|"        body:  { \"foo\": \"Hello: ä¸­æ\" }\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      get:\n"
operator|+
literal|"        index: test_1\n"
operator|+
literal|"        type:  test\n"
operator|+
literal|"        id:    ä¸­æ"
argument_list|)
expr_stmt|;
name|RestTestSectionParser
name|testSectionParser
init|=
operator|new
name|RestTestSectionParser
argument_list|()
decl_stmt|;
name|TestSection
name|testSection
init|=
name|testSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|RestTestSuiteParseContext
argument_list|(
literal|"api"
argument_list|,
literal|"suite"
argument_list|,
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Basic"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SkipSection
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getExecutableSections
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
name|DoSection
name|doSection
init|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|doSection
operator|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"get"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseTestSectionWithDoSectionsAndAssertions
specifier|public
name|void
name|testParseTestSectionWithDoSectionsAndAssertions
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|YamlXContent
operator|.
name|yamlXContent
operator|.
name|createParser
argument_list|(
literal|"\"Basic\":\n"
operator|+
literal|"\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      index:\n"
operator|+
literal|"        index: test_1\n"
operator|+
literal|"        type:  test\n"
operator|+
literal|"        id:    ä¸­æ\n"
operator|+
literal|"        body:  { \"foo\": \"Hello: ä¸­æ\" }\n"
operator|+
literal|"\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      get:\n"
operator|+
literal|"        index: test_1\n"
operator|+
literal|"        type:  test\n"
operator|+
literal|"        id:    ä¸­æ\n"
operator|+
literal|"\n"
operator|+
literal|"  - match: { _index:   test_1 }\n"
operator|+
literal|"  - is_true: _source\n"
operator|+
literal|"  - match: { _source:  { foo: \"Hello: ä¸­æ\" } }\n"
operator|+
literal|"\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      get:\n"
operator|+
literal|"        index: test_1\n"
operator|+
literal|"        id:    ä¸­æ\n"
operator|+
literal|"\n"
operator|+
literal|"  - length: { _index:   6 }\n"
operator|+
literal|"  - is_false: whatever\n"
operator|+
literal|"  - gt: { size: 5      }\n"
operator|+
literal|"  - lt: { size: 10      }"
argument_list|)
expr_stmt|;
name|RestTestSectionParser
name|testSectionParser
init|=
operator|new
name|RestTestSectionParser
argument_list|()
decl_stmt|;
name|TestSection
name|testSection
init|=
name|testSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|RestTestSuiteParseContext
argument_list|(
literal|"api"
argument_list|,
literal|"suite"
argument_list|,
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Basic"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getSkipSection
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SkipSection
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|DoSection
name|doSection
init|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|doSection
operator|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"get"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|MatchAssertion
name|matchAssertion
init|=
operator|(
name|MatchAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|matchAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matchAssertion
operator|.
name|getExpectedValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test_1"
argument_list|)
argument_list|)
expr_stmt|;
name|IsTrueAssertion
name|trueAssertion
init|=
operator|(
name|IsTrueAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|trueAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_source"
argument_list|)
argument_list|)
expr_stmt|;
name|matchAssertion
operator|=
operator|(
name|MatchAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matchAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_source"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matchAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|matchAssertion
operator|.
name|getExpectedValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|map
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
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Hello: ä¸­æ"
argument_list|)
argument_list|)
expr_stmt|;
name|doSection
operator|=
operator|(
name|DoSection
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getCatch
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"get"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getParams
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
name|doSection
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|hasBody
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|LengthAssertion
name|lengthAssertion
init|=
operator|(
name|LengthAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|lengthAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lengthAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|lengthAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|IsFalseAssertion
name|falseAssertion
init|=
operator|(
name|IsFalseAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|falseAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"whatever"
argument_list|)
argument_list|)
expr_stmt|;
name|GreaterThanAssertion
name|greaterThanAssertion
init|=
operator|(
name|GreaterThanAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|greaterThanAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"size"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|greaterThanAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|greaterThanAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|LessThanAssertion
name|lessThanAssertion
init|=
operator|(
name|LessThanAssertion
operator|)
name|testSection
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|get
argument_list|(
literal|9
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|lessThanAssertion
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"size"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lessThanAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|lessThanAssertion
operator|.
name|getExpectedValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSmallSection
specifier|public
name|void
name|testSmallSection
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
name|YamlXContent
operator|.
name|yamlXContent
operator|.
name|createParser
argument_list|(
literal|"\"node_info test\":\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      cluster.node_info: {}\n"
operator|+
literal|"  \n"
operator|+
literal|"  - is_true: nodes\n"
operator|+
literal|"  - is_true: cluster_name\n"
argument_list|)
expr_stmt|;
name|RestTestSectionParser
name|testSectionParser
init|=
operator|new
name|RestTestSectionParser
argument_list|()
decl_stmt|;
name|TestSection
name|testSection
init|=
name|testSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|RestTestSuiteParseContext
argument_list|(
literal|"api"
argument_list|,
literal|"suite"
argument_list|,
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node_info test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testSection
operator|.
name|getExecutableSections
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
block|}
block|}
end_class

end_unit

