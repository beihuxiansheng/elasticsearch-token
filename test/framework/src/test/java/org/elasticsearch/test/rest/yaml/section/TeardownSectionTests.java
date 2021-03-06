begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.yaml.section
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
name|section
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

begin_comment
comment|/**  * Unit tests for the teardown section.  */
end_comment

begin_class
DECL|class|TeardownSectionTests
specifier|public
class|class
name|TeardownSectionTests
extends|extends
name|AbstractClientYamlTestFragmentParserTestCase
block|{
DECL|method|testParseTeardownSection
specifier|public
name|void
name|testParseTeardownSection
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
literal|"  - do:\n"
operator|+
literal|"      delete:\n"
operator|+
literal|"        index: foo\n"
operator|+
literal|"        type: doc\n"
operator|+
literal|"        id: 1\n"
operator|+
literal|"        ignore: 404\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      delete2:\n"
operator|+
literal|"        index: foo\n"
operator|+
literal|"        type: doc\n"
operator|+
literal|"        id: 1\n"
operator|+
literal|"        ignore: 404"
argument_list|)
expr_stmt|;
name|TeardownSection
name|section
init|=
name|TeardownSection
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|section
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
operator|.
name|getSkipSection
argument_list|()
operator|.
name|isEmpty
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
name|section
operator|.
name|getDoSections
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
name|section
operator|.
name|getDoSections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"delete"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
operator|.
name|getDoSections
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"delete2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseWithSkip
specifier|public
name|void
name|testParseWithSkip
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
literal|"  - skip:\n"
operator|+
literal|"      version:  \"5.0.0 - 5.3.0\"\n"
operator|+
literal|"      reason:   \"there is a reason\"\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      delete:\n"
operator|+
literal|"        index: foo\n"
operator|+
literal|"        type: doc\n"
operator|+
literal|"        id: 1\n"
operator|+
literal|"        ignore: 404\n"
operator|+
literal|"  - do:\n"
operator|+
literal|"      delete2:\n"
operator|+
literal|"        index: foo\n"
operator|+
literal|"        type: doc\n"
operator|+
literal|"        id: 1\n"
operator|+
literal|"        ignore: 404"
argument_list|)
expr_stmt|;
name|TeardownSection
name|section
init|=
name|TeardownSection
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|section
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
operator|.
name|getSkipSection
argument_list|()
operator|.
name|isEmpty
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
name|section
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
name|V_5_0_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
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
name|V_5_3_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
operator|.
name|getSkipSection
argument_list|()
operator|.
name|getReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"there is a reason"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
operator|.
name|getDoSections
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
name|section
operator|.
name|getDoSections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"delete"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|section
operator|.
name|getDoSections
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getApiCallSection
argument_list|()
operator|.
name|getApi
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"delete2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

