begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.yaml.parser
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
name|parser
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
name|section
operator|.
name|SetSection
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
name|notNullValue
import|;
end_import

begin_class
DECL|class|SetSectionParserTests
specifier|public
class|class
name|SetSectionParserTests
extends|extends
name|AbstractClientYamlTestFragmentParserTestCase
block|{
DECL|method|testParseSetSectionSingleValue
specifier|public
name|void
name|testParseSetSectionSingleValue
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
literal|"{ _id: id }"
argument_list|)
expr_stmt|;
name|SetSectionParser
name|setSectionParser
init|=
operator|new
name|SetSectionParser
argument_list|()
decl_stmt|;
name|SetSection
name|setSection
init|=
name|setSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|ClientYamlTestSuiteParseContext
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
name|setSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|setSection
operator|.
name|getStash
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"_id"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseSetSectionMultipleValues
specifier|public
name|void
name|testParseSetSectionMultipleValues
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
literal|"{ _id: id, _type: type, _index: index }"
argument_list|)
expr_stmt|;
name|SetSectionParser
name|setSectionParser
init|=
operator|new
name|SetSectionParser
argument_list|()
decl_stmt|;
name|SetSection
name|setSection
init|=
name|setSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|ClientYamlTestSuiteParseContext
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
name|setSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|setSection
operator|.
name|getStash
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
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
literal|3
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
literal|"_id"
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
name|setSection
operator|.
name|getStash
argument_list|()
operator|.
name|get
argument_list|(
literal|"_type"
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
name|setSection
operator|.
name|getStash
argument_list|()
operator|.
name|get
argument_list|(
literal|"_index"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseSetSectionNoValues
specifier|public
name|void
name|testParseSetSectionNoValues
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
literal|"{ }"
argument_list|)
expr_stmt|;
name|SetSectionParser
name|setSectionParser
init|=
operator|new
name|SetSectionParser
argument_list|()
decl_stmt|;
try|try
block|{
name|setSectionParser
operator|.
name|parse
argument_list|(
operator|new
name|ClientYamlTestSuiteParseContext
argument_list|(
literal|"api"
argument_list|,
literal|"suite"
argument_list|,
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RestTestParseException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClientYamlTestParseException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"set section must set at least a value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

