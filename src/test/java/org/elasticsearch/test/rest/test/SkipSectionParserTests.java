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
name|VersionUtils
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
name|RestTestParseException
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
name|parser
operator|.
name|SkipSectionParser
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
name|SkipSection
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
name|*
import|;
end_import

begin_class
DECL|class|SkipSectionParserTests
specifier|public
class|class
name|SkipSectionParserTests
extends|extends
name|AbstractParserTests
block|{
annotation|@
name|Test
DECL|method|testParseSkipSectionVersionNoFeature
specifier|public
name|void
name|testParseSkipSectionVersionNoFeature
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
literal|"version:     \"0 - 0.90.2\"\n"
operator|+
literal|"reason:      Delete ignores the parent param"
argument_list|)
expr_stmt|;
name|SkipSectionParser
name|skipSectionParser
init|=
operator|new
name|SkipSectionParser
argument_list|()
decl_stmt|;
name|SkipSection
name|skipSection
init|=
name|skipSectionParser
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
name|skipSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getLowerVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getUpperVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_0_90_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getFeatures
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
name|skipSection
operator|.
name|getReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Delete ignores the parent param"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseSkipSectionFeatureNoVersion
specifier|public
name|void
name|testParseSkipSectionFeatureNoVersion
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
literal|"features:     regex"
argument_list|)
expr_stmt|;
name|SkipSectionParser
name|skipSectionParser
init|=
operator|new
name|SkipSectionParser
argument_list|()
decl_stmt|;
name|SkipSection
name|skipSection
init|=
name|skipSectionParser
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
name|skipSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|isVersionCheck
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
name|skipSection
operator|.
name|getFeatures
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
name|skipSection
operator|.
name|getFeatures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"regex"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getReason
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseSkipSectionFeaturesNoVersion
specifier|public
name|void
name|testParseSkipSectionFeaturesNoVersion
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
literal|"features:     [regex1,regex2,regex3]"
argument_list|)
expr_stmt|;
name|SkipSectionParser
name|skipSectionParser
init|=
operator|new
name|SkipSectionParser
argument_list|()
decl_stmt|;
name|SkipSection
name|skipSection
init|=
name|skipSectionParser
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
name|skipSection
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|isVersionCheck
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
name|skipSection
operator|.
name|getFeatures
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
name|skipSection
operator|.
name|getFeatures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"regex1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getFeatures
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"regex2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getFeatures
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"regex3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|skipSection
operator|.
name|getReason
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RestTestParseException
operator|.
name|class
argument_list|)
DECL|method|testParseSkipSectionBothFeatureAndVersion
specifier|public
name|void
name|testParseSkipSectionBothFeatureAndVersion
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
literal|"version:     \"0 - 0.90.2\"\n"
operator|+
literal|"features:     regex\n"
operator|+
literal|"reason:      Delete ignores the parent param"
argument_list|)
expr_stmt|;
name|SkipSectionParser
name|skipSectionParser
init|=
operator|new
name|SkipSectionParser
argument_list|()
decl_stmt|;
name|skipSectionParser
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
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RestTestParseException
operator|.
name|class
argument_list|)
DECL|method|testParseSkipSectionNoReason
specifier|public
name|void
name|testParseSkipSectionNoReason
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
literal|"version:     \"0 - 0.90.2\"\n"
argument_list|)
expr_stmt|;
name|SkipSectionParser
name|skipSectionParser
init|=
operator|new
name|SkipSectionParser
argument_list|()
decl_stmt|;
name|skipSectionParser
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
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RestTestParseException
operator|.
name|class
argument_list|)
DECL|method|testParseSkipSectionNoVersionNorFeature
specifier|public
name|void
name|testParseSkipSectionNoVersionNorFeature
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
literal|"reason:      Delete ignores the parent param\n"
argument_list|)
expr_stmt|;
name|SkipSectionParser
name|skipSectionParser
init|=
operator|new
name|SkipSectionParser
argument_list|()
decl_stmt|;
name|skipSectionParser
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
expr_stmt|;
block|}
block|}
end_class

end_unit

