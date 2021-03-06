begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
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
name|collect
operator|.
name|Tuple
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|greaterThanOrEqualTo
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
name|lessThanOrEqualTo
import|;
end_import

begin_class
DECL|class|VersionUtilsTests
specifier|public
class|class
name|VersionUtilsTests
extends|extends
name|ESTestCase
block|{
DECL|method|testAllVersionsSorted
specifier|public
name|void
name|testAllVersionsSorted
parameter_list|()
block|{
name|List
argument_list|<
name|Version
argument_list|>
name|allVersions
init|=
name|VersionUtils
operator|.
name|allReleasedVersions
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|1
init|;
name|j
operator|<
name|allVersions
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
operator|,
operator|++
name|j
control|)
block|{
name|assertTrue
argument_list|(
name|allVersions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|before
argument_list|(
name|allVersions
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandomVersionBetween
specifier|public
name|void
name|testRandomVersionBetween
parameter_list|()
block|{
comment|// full range
name|Version
name|got
init|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
comment|// sub range
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_5_0_0
argument_list|,
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_0_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
argument_list|)
expr_stmt|;
comment|// unbounded lower
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|null
argument_list|,
name|VersionUtils
operator|.
name|allReleasedVersions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|VersionUtils
operator|.
name|allReleasedVersions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// unbounded upper
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_5_0_0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_0_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getPreviousVersion
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrAfter
argument_list|(
name|VersionUtils
operator|.
name|getPreviousVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
comment|// range of one
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|got
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|got
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|,
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|got
argument_list|,
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
expr_stmt|;
comment|// implicit range of one
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|null
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|got
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|)
expr_stmt|;
name|got
operator|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|got
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
comment|// max or min can be an unreleased version
name|Version
name|unreleased
init|=
name|randomFrom
argument_list|(
name|VersionUtils
operator|.
name|allUnreleasedVersions
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|null
argument_list|,
name|unreleased
argument_list|)
argument_list|,
name|lessThanOrEqualTo
argument_list|(
name|unreleased
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|unreleased
argument_list|,
literal|null
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
name|unreleased
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unreleased
argument_list|,
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|unreleased
argument_list|,
name|unreleased
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestReleaseBranch
specifier|static
class|class
name|TestReleaseBranch
block|{
DECL|field|V_5_3_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.0"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_1
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.1"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_2
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.2"
argument_list|)
decl_stmt|;
DECL|field|V_5_4_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_4_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.4.0"
argument_list|)
decl_stmt|;
DECL|field|V_5_4_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_4_1
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.4.1"
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|CURRENT
init|=
name|V_5_4_1
decl_stmt|;
block|}
DECL|method|testResolveReleasedVersionsForReleaseBranch
specifier|public
name|void
name|testResolveReleasedVersionsForReleaseBranch
parameter_list|()
block|{
name|Tuple
argument_list|<
name|List
argument_list|<
name|Version
argument_list|>
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|t
init|=
name|VersionUtils
operator|.
name|resolveReleasedVersions
argument_list|(
name|TestReleaseBranch
operator|.
name|CURRENT
argument_list|,
name|TestReleaseBranch
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|released
init|=
name|t
operator|.
name|v1
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|unreleased
init|=
name|t
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TestReleaseBranch
operator|.
name|V_5_3_0
argument_list|,
name|TestReleaseBranch
operator|.
name|V_5_3_1
argument_list|,
name|TestReleaseBranch
operator|.
name|V_5_3_2
argument_list|,
name|TestReleaseBranch
operator|.
name|V_5_4_0
argument_list|,
name|TestReleaseBranch
operator|.
name|V_5_4_1
argument_list|)
argument_list|,
name|released
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|unreleased
argument_list|)
expr_stmt|;
block|}
DECL|class|TestStableBranch
specifier|static
class|class
name|TestStableBranch
block|{
DECL|field|V_5_3_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.0"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_1
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.1"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_2
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.2"
argument_list|)
decl_stmt|;
DECL|field|V_5_4_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_4_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.4.0"
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|CURRENT
init|=
name|V_5_4_0
decl_stmt|;
block|}
DECL|method|testResolveReleasedVersionsForUnreleasedStableBranch
specifier|public
name|void
name|testResolveReleasedVersionsForUnreleasedStableBranch
parameter_list|()
block|{
name|Tuple
argument_list|<
name|List
argument_list|<
name|Version
argument_list|>
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|t
init|=
name|VersionUtils
operator|.
name|resolveReleasedVersions
argument_list|(
name|TestStableBranch
operator|.
name|CURRENT
argument_list|,
name|TestStableBranch
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|released
init|=
name|t
operator|.
name|v1
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|unreleased
init|=
name|t
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TestStableBranch
operator|.
name|V_5_3_0
argument_list|,
name|TestStableBranch
operator|.
name|V_5_3_1
argument_list|,
name|TestStableBranch
operator|.
name|V_5_4_0
argument_list|)
argument_list|,
name|released
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|singletonList
argument_list|(
name|TestStableBranch
operator|.
name|V_5_3_2
argument_list|)
argument_list|,
name|unreleased
argument_list|)
expr_stmt|;
block|}
DECL|class|TestStableBranchBehindStableBranch
specifier|static
class|class
name|TestStableBranchBehindStableBranch
block|{
DECL|field|V_5_3_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.0"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_1
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.1"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_2
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.2"
argument_list|)
decl_stmt|;
DECL|field|V_5_4_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_4_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.4.0"
argument_list|)
decl_stmt|;
DECL|field|V_5_5_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_5_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.5.0"
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|CURRENT
init|=
name|V_5_5_0
decl_stmt|;
block|}
DECL|method|testResolveReleasedVersionsForStableBtranchBehindStableBranch
specifier|public
name|void
name|testResolveReleasedVersionsForStableBtranchBehindStableBranch
parameter_list|()
block|{
name|Tuple
argument_list|<
name|List
argument_list|<
name|Version
argument_list|>
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|t
init|=
name|VersionUtils
operator|.
name|resolveReleasedVersions
argument_list|(
name|TestStableBranchBehindStableBranch
operator|.
name|CURRENT
argument_list|,
name|TestStableBranchBehindStableBranch
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|released
init|=
name|t
operator|.
name|v1
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|unreleased
init|=
name|t
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TestStableBranchBehindStableBranch
operator|.
name|V_5_3_0
argument_list|,
name|TestStableBranchBehindStableBranch
operator|.
name|V_5_3_1
argument_list|,
name|TestStableBranchBehindStableBranch
operator|.
name|V_5_5_0
argument_list|)
argument_list|,
name|released
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TestStableBranchBehindStableBranch
operator|.
name|V_5_3_2
argument_list|,
name|Version
operator|.
name|V_5_4_0
argument_list|)
argument_list|,
name|unreleased
argument_list|)
expr_stmt|;
block|}
DECL|class|TestUnstableBranch
specifier|static
class|class
name|TestUnstableBranch
block|{
DECL|field|V_5_3_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.0"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_1
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.1"
argument_list|)
decl_stmt|;
DECL|field|V_5_3_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_3_2
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.3.2"
argument_list|)
decl_stmt|;
DECL|field|V_5_4_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_4_0
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.4.0"
argument_list|)
decl_stmt|;
DECL|field|V_6_0_0_alpha1
specifier|public
specifier|static
specifier|final
name|Version
name|V_6_0_0_alpha1
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"6.0.0-alpha1"
argument_list|)
decl_stmt|;
DECL|field|V_6_0_0_alpha2
specifier|public
specifier|static
specifier|final
name|Version
name|V_6_0_0_alpha2
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"6.0.0-alpha2"
argument_list|)
decl_stmt|;
DECL|field|V_6_0_0_alpha3
specifier|public
specifier|static
specifier|final
name|Version
name|V_6_0_0_alpha3
init|=
name|Version
operator|.
name|fromString
argument_list|(
literal|"6.0.0-alpha3"
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|CURRENT
init|=
name|V_6_0_0_alpha3
decl_stmt|;
block|}
DECL|method|testResolveReleasedVersionsForUnstableBranch
specifier|public
name|void
name|testResolveReleasedVersionsForUnstableBranch
parameter_list|()
block|{
name|Tuple
argument_list|<
name|List
argument_list|<
name|Version
argument_list|>
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|t
init|=
name|VersionUtils
operator|.
name|resolveReleasedVersions
argument_list|(
name|TestUnstableBranch
operator|.
name|CURRENT
argument_list|,
name|TestUnstableBranch
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|released
init|=
name|t
operator|.
name|v1
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|unreleased
init|=
name|t
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TestUnstableBranch
operator|.
name|V_5_3_0
argument_list|,
name|TestUnstableBranch
operator|.
name|V_5_3_1
argument_list|,
name|TestUnstableBranch
operator|.
name|V_6_0_0_alpha1
argument_list|,
name|TestUnstableBranch
operator|.
name|V_6_0_0_alpha2
argument_list|,
name|TestUnstableBranch
operator|.
name|V_6_0_0_alpha3
argument_list|)
argument_list|,
name|released
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TestUnstableBranch
operator|.
name|V_5_3_2
argument_list|,
name|TestUnstableBranch
operator|.
name|V_5_4_0
argument_list|)
argument_list|,
name|unreleased
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that {@link Version#minimumCompatibilityVersion()} and {@link VersionUtils#allReleasedVersions()}      * agree with the list of wire and index compatible versions we build in gradle.      */
DECL|method|testGradleVersionsMatchVersionUtils
specifier|public
name|void
name|testGradleVersionsMatchVersionUtils
parameter_list|()
block|{
comment|// First check the index compatible versions
name|VersionsFromProperty
name|indexCompatible
init|=
operator|new
name|VersionsFromProperty
argument_list|(
literal|"tests.gradle_index_compat_versions"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|released
init|=
name|VersionUtils
operator|.
name|allReleasedVersions
argument_list|()
operator|.
name|stream
argument_list|()
comment|/* We skip alphas, betas, and the like in gradle because they don't have                  * backwards compatibility guarantees even though they are technically                  * released. */
operator|.
name|filter
argument_list|(
name|Version
operator|::
name|isRelease
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|releasedIndexCompatible
init|=
name|released
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|releasedIndexCompatible
argument_list|,
name|indexCompatible
operator|.
name|released
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|unreleasedIndexCompatible
init|=
name|VersionUtils
operator|.
name|allUnreleasedVersions
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|unreleasedIndexCompatible
argument_list|,
name|indexCompatible
operator|.
name|unreleased
argument_list|)
expr_stmt|;
comment|// Now the wire compatible versions
name|VersionsFromProperty
name|wireCompatible
init|=
operator|new
name|VersionsFromProperty
argument_list|(
literal|"tests.gradle_wire_compat_versions"
argument_list|)
decl_stmt|;
comment|// Big horrible hack:
comment|// This *should* be:
comment|//         Version minimumCompatibleVersion = Version.CURRENT.minimumCompatibilityVersion();
comment|// But instead it is:
name|Version
name|minimumCompatibleVersion
init|=
name|Version
operator|.
name|V_5_6_0
decl_stmt|;
comment|// Because things blow up all over the place if the minimum compatible version isn't released.
comment|// We'll fix this very, very soon. But for now, this hack.
comment|// end big horrible hack
name|List
argument_list|<
name|String
argument_list|>
name|releasedWireCompatible
init|=
name|released
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|.
name|onOrAfter
argument_list|(
name|minimumCompatibleVersion
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|releasedWireCompatible
argument_list|,
name|wireCompatible
operator|.
name|released
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|unreleasedWireCompatible
init|=
name|VersionUtils
operator|.
name|allUnreleasedVersions
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|v
lambda|->
name|v
operator|.
name|onOrAfter
argument_list|(
name|minimumCompatibleVersion
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|unreleasedWireCompatible
argument_list|,
name|wireCompatible
operator|.
name|unreleased
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read a versions system property as set by gradle into a tuple of {@code (releasedVersion, unreleasedVersion)}.      */
DECL|class|VersionsFromProperty
specifier|private
class|class
name|VersionsFromProperty
block|{
DECL|field|released
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|released
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|unreleased
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|unreleased
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|VersionsFromProperty
specifier|private
name|VersionsFromProperty
parameter_list|(
name|String
name|property
parameter_list|)
block|{
name|String
name|versions
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Couldn't find ["
operator|+
name|property
operator|+
literal|"]. Gradle should set these before running the tests."
argument_list|,
name|versions
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Looked up versions [{}={}]"
argument_list|,
name|property
argument_list|,
name|versions
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|version
range|:
name|versions
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
if|if
condition|(
name|version
operator|.
name|endsWith
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
condition|)
block|{
name|unreleased
operator|.
name|add
argument_list|(
name|version
operator|.
name|replace
argument_list|(
literal|"-SNAPSHOT"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|released
operator|.
name|add
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

