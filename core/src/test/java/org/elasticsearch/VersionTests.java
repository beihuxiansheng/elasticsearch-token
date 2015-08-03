begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|lucene
operator|.
name|Lucene
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
name|VersionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|elasticsearch
operator|.
name|Version
operator|.
name|V_0_20_0
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|Version
operator|.
name|V_0_90_0
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
name|VersionUtils
operator|.
name|randomVersion
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
name|sameInstance
import|;
end_import

begin_class
DECL|class|VersionTests
specifier|public
class|class
name|VersionTests
extends|extends
name|ESTestCase
block|{
DECL|method|testMavenVersion
specifier|public
name|void
name|testMavenVersion
parameter_list|()
block|{
comment|// maven sets this property to ensure that the latest version
comment|// we use here is the version that is actually set to the project.version
comment|// in maven
name|String
name|property
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.version"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assumeTrue
argument_list|(
literal|"tests.version is set"
argument_list|,
name|property
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|property
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testVersionComparison
specifier|public
name|void
name|testVersionComparison
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|before
argument_list|(
name|V_0_90_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|before
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_90_0
operator|.
name|before
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|onOrBefore
argument_list|(
name|V_0_90_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|onOrBefore
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_90_0
operator|.
name|onOrBefore
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|after
argument_list|(
name|V_0_90_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|after
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_90_0
operator|.
name|after
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|onOrAfter
argument_list|(
name|V_0_90_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_20_0
operator|.
name|onOrAfter
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|V_0_90_0
operator|.
name|onOrAfter
argument_list|(
name|V_0_20_0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testVersionConstantPresent
specifier|public
name|void
name|testVersionConstantPresent
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|,
name|sameInstance
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
argument_list|,
name|equalTo
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LATEST
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|20
argument_list|,
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Version
name|version
init|=
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|version
argument_list|,
name|sameInstance
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
name|version
operator|.
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|version
operator|.
name|luceneVersion
argument_list|,
name|sameInstance
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
name|version
operator|.
name|id
argument_list|)
operator|.
name|luceneVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCURRENTIsLatest
specifier|public
name|void
name|testCURRENTIsLatest
parameter_list|()
block|{
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Version
name|version
init|=
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|Version
operator|.
name|CURRENT
condition|)
block|{
name|assertThat
argument_list|(
literal|"Version: "
operator|+
name|version
operator|+
literal|" should be before: "
operator|+
name|Version
operator|.
name|CURRENT
operator|+
literal|" but wasn't"
argument_list|,
name|version
operator|.
name|before
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testVersionFromString
specifier|public
name|void
name|testVersionFromString
parameter_list|()
block|{
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Version
name|version
init|=
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|snapshot
argument_list|()
condition|)
block|{
comment|// number doesn't include SNAPSHOT but the parser checks for that
name|assertEquals
argument_list|(
name|Version
operator|.
name|fromString
argument_list|(
name|version
operator|.
name|number
argument_list|()
argument_list|)
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|Version
operator|.
name|fromString
argument_list|(
name|version
operator|.
name|number
argument_list|()
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|Version
operator|.
name|fromString
argument_list|(
name|version
operator|.
name|number
argument_list|()
argument_list|)
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testTooLongVersionFromString
specifier|public
name|void
name|testTooLongVersionFromString
parameter_list|()
block|{
name|Version
operator|.
name|fromString
argument_list|(
literal|"1.0.0.1.3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testTooShortVersionFromString
specifier|public
name|void
name|testTooShortVersionFromString
parameter_list|()
block|{
name|Version
operator|.
name|fromString
argument_list|(
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testWrongVersionFromString
specifier|public
name|void
name|testWrongVersionFromString
parameter_list|()
block|{
name|Version
operator|.
name|fromString
argument_list|(
literal|"WRONG.VERSION"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testVersionNoPresentInSettings
specifier|public
name|void
name|testVersionNoPresentInSettings
parameter_list|()
block|{
name|Version
operator|.
name|indexCreated
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexCreatedVersion
specifier|public
name|void
name|testIndexCreatedVersion
parameter_list|()
block|{
comment|// an actual index has a IndexMetaData.SETTING_INDEX_UUID
specifier|final
name|Version
name|version
init|=
name|randomFrom
argument_list|(
name|Version
operator|.
name|V_0_18_0
argument_list|,
name|Version
operator|.
name|V_0_90_13
argument_list|,
name|Version
operator|.
name|V_1_3_0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|version
argument_list|,
name|Version
operator|.
name|indexCreated
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|version
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinCompatVersion
specifier|public
name|void
name|testMinCompatVersion
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Version
operator|.
name|V_1_3_0
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_1_0_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Version
operator|.
name|V_1_2_0
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_1_0_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Version
operator|.
name|V_1_2_3
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_1_0_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Version
operator|.
name|V_1_0_0_RC2
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|V_1_0_0_RC2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
comment|// with 2.0.beta we lowercase
name|assertEquals
argument_list|(
literal|"2.0.0-beta1"
argument_list|,
name|Version
operator|.
name|V_2_0_0_beta1
operator|.
name|number
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.4.0.Beta1"
argument_list|,
name|Version
operator|.
name|V_1_4_0_Beta1
operator|.
name|number
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.4.0"
argument_list|,
name|Version
operator|.
name|V_1_4_0
operator|.
name|number
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsBeta
specifier|public
name|void
name|testIsBeta
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
operator|.
name|isBeta
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Version
operator|.
name|V_1_4_0_Beta1
operator|.
name|isBeta
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Version
operator|.
name|V_1_4_0
operator|.
name|isBeta
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseVersion
specifier|public
name|void
name|testParseVersion
parameter_list|()
block|{
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Version
name|version
init|=
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|snapshot
argument_list|()
operator|==
literal|false
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|version
operator|=
operator|new
name|Version
argument_list|(
name|version
operator|.
name|id
argument_list|,
literal|true
argument_list|,
name|version
operator|.
name|luceneVersion
argument_list|)
expr_stmt|;
block|}
name|Version
name|parsedVersion
init|=
name|Version
operator|.
name|fromString
argument_list|(
name|version
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|version
argument_list|,
name|parsedVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|version
operator|.
name|snapshot
argument_list|()
argument_list|,
name|parsedVersion
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseLenient
specifier|public
name|void
name|testParseLenient
parameter_list|()
block|{
comment|// note this is just a silly sanity check, we test it in lucene
for|for
control|(
name|Version
name|version
range|:
name|VersionUtils
operator|.
name|allVersions
argument_list|()
control|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|luceneVersion
init|=
name|version
operator|.
name|luceneVersion
decl_stmt|;
name|String
name|string
init|=
name|luceneVersion
operator|.
name|toString
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"^LUCENE_(\\d+)_(\\d+)$"
argument_list|,
literal|"$1.$2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|luceneVersion
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|Lucene
operator|.
name|parseVersionLenient
argument_list|(
name|string
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAllVersionsMatchId
specifier|public
name|void
name|testAllVersionsMatchId
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Version
argument_list|>
name|maxBranchVersions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
range|:
name|Version
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"_ID"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" should be static"
argument_list|,
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" should be final"
argument_list|,
name|Modifier
operator|.
name|isFinal
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|versionId
init|=
operator|(
name|Integer
operator|)
name|field
operator|.
name|get
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|constantName
init|=
name|field
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|field
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|)
decl_stmt|;
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|versionConstant
init|=
name|Version
operator|.
name|class
operator|.
name|getField
argument_list|(
name|constantName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|constantName
operator|+
literal|" should be static"
argument_list|,
name|Modifier
operator|.
name|isStatic
argument_list|(
name|versionConstant
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|constantName
operator|+
literal|" should be final"
argument_list|,
name|Modifier
operator|.
name|isFinal
argument_list|(
name|versionConstant
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Version
name|v
init|=
operator|(
name|Version
operator|)
name|versionConstant
operator|.
name|get
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Checking "
operator|+
name|v
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Version id "
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" does not point to "
operator|+
name|constantName
argument_list|,
name|v
argument_list|,
name|Version
operator|.
name|fromId
argument_list|(
name|versionId
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Version "
operator|+
name|constantName
operator|+
literal|" does not have correct id"
argument_list|,
name|versionId
argument_list|,
name|v
operator|.
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|major
operator|>=
literal|2
condition|)
block|{
name|String
name|number
init|=
name|v
operator|.
name|number
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|isBeta
argument_list|()
condition|)
block|{
name|number
operator|=
name|number
operator|.
name|replace
argument_list|(
literal|"-beta"
argument_list|,
literal|"_beta"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|isRC
argument_list|()
condition|)
block|{
name|number
operator|=
name|number
operator|.
name|replace
argument_list|(
literal|"-rc"
argument_list|,
literal|"_rc"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"V_"
operator|+
name|number
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'_'
argument_list|)
argument_list|,
name|constantName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"V_"
operator|+
name|v
operator|.
name|number
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'_'
argument_list|)
argument_list|,
name|constantName
argument_list|)
expr_stmt|;
block|}
comment|// only the latest version for a branch should be a snapshot (ie unreleased)
name|String
name|branchName
init|=
literal|""
operator|+
name|v
operator|.
name|major
operator|+
literal|"."
operator|+
name|v
operator|.
name|minor
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Remove this once beta1 is released"
argument_list|,
name|v
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
comment|// this is just a temporary fix until we have a snapshot for the beta since we now have 2 unreleased version of the same major.minor group
block|}
name|Version
name|maxBranchVersion
init|=
name|maxBranchVersions
operator|.
name|get
argument_list|(
name|branchName
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxBranchVersion
operator|==
literal|null
condition|)
block|{
name|maxBranchVersions
operator|.
name|put
argument_list|(
name|branchName
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|after
argument_list|(
name|maxBranchVersion
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Version "
operator|+
name|maxBranchVersion
operator|+
literal|" cannot be a snapshot because version "
operator|+
name|v
operator|+
literal|" exists"
argument_list|,
name|maxBranchVersion
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
name|maxBranchVersions
operator|.
name|put
argument_list|(
name|branchName
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

