begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
package|;
end_package

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

begin_class
DECL|class|SizeValueTests
specifier|public
class|class
name|SizeValueTests
extends|extends
name|ESTestCase
block|{
DECL|method|testThatConversionWorks
specifier|public
name|void
name|testThatConversionWorks
parameter_list|()
block|{
name|SizeValue
name|sizeValue
init|=
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|kilo
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1k"
argument_list|)
argument_list|)
expr_stmt|;
name|sizeValue
operator|=
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|,
name|SizeUnit
operator|.
name|KILO
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|singles
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1000000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1m"
argument_list|)
argument_list|)
expr_stmt|;
name|sizeValue
operator|=
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|,
name|SizeUnit
operator|.
name|MEGA
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|singles
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1000000000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1g"
argument_list|)
argument_list|)
expr_stmt|;
name|sizeValue
operator|=
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|,
name|SizeUnit
operator|.
name|GIGA
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|singles
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1000000000000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1t"
argument_list|)
argument_list|)
expr_stmt|;
name|sizeValue
operator|=
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|,
name|SizeUnit
operator|.
name|TERA
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|singles
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1000000000000000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1p"
argument_list|)
argument_list|)
expr_stmt|;
name|sizeValue
operator|=
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|,
name|SizeUnit
operator|.
name|PETA
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|singles
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1000000000000000000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sizeValue
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1000p"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatParsingWorks
specifier|public
name|void
name|testThatParsingWorks
parameter_list|()
block|{
name|assertThat
argument_list|(
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"1k"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
operator|new
name|SizeValue
argument_list|(
literal|1000
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"1p"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
operator|new
name|SizeValue
argument_list|(
literal|1
argument_list|,
name|SizeUnit
operator|.
name|PETA
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"1G"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
operator|new
name|SizeValue
argument_list|(
literal|1
argument_list|,
name|SizeUnit
operator|.
name|GIGA
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatNegativeValuesThrowException
specifier|public
name|void
name|testThatNegativeValuesThrowException
parameter_list|()
block|{
try|try
block|{
operator|new
name|SizeValue
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|containsString
argument_list|(
literal|"may not be negative"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCompareEquality
specifier|public
name|void
name|testCompareEquality
parameter_list|()
block|{
name|long
name|randomValue
init|=
name|randomNonNegativeLong
argument_list|()
decl_stmt|;
name|SizeUnit
name|randomUnit
init|=
name|randomFrom
argument_list|(
name|SizeUnit
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|SizeValue
name|firstValue
init|=
operator|new
name|SizeValue
argument_list|(
name|randomValue
argument_list|,
name|randomUnit
argument_list|)
decl_stmt|;
name|SizeValue
name|secondValue
init|=
operator|new
name|SizeValue
argument_list|(
name|randomValue
argument_list|,
name|randomUnit
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|firstValue
operator|.
name|compareTo
argument_list|(
name|secondValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompareValue
specifier|public
name|void
name|testCompareValue
parameter_list|()
block|{
name|long
name|firstRandom
init|=
name|randomNonNegativeLong
argument_list|()
decl_stmt|;
name|long
name|secondRandom
init|=
name|randomValueOtherThan
argument_list|(
name|firstRandom
argument_list|,
name|ESTestCase
operator|::
name|randomNonNegativeLong
argument_list|)
decl_stmt|;
name|SizeUnit
name|unit
init|=
name|randomFrom
argument_list|(
name|SizeUnit
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|SizeValue
name|firstSizeValue
init|=
operator|new
name|SizeValue
argument_list|(
name|firstRandom
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|SizeValue
name|secondSizeValue
init|=
operator|new
name|SizeValue
argument_list|(
name|secondRandom
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|firstRandom
operator|>
name|secondRandom
argument_list|,
name|firstSizeValue
operator|.
name|compareTo
argument_list|(
name|secondSizeValue
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|secondRandom
operator|>
name|firstRandom
argument_list|,
name|secondSizeValue
operator|.
name|compareTo
argument_list|(
name|firstSizeValue
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompareUnits
specifier|public
name|void
name|testCompareUnits
parameter_list|()
block|{
name|long
name|number
init|=
name|randomNonNegativeLong
argument_list|()
decl_stmt|;
name|SizeUnit
name|randomUnit
init|=
name|randomValueOtherThan
argument_list|(
name|SizeUnit
operator|.
name|PETA
argument_list|,
parameter_list|()
lambda|->
name|randomFrom
argument_list|(
name|SizeUnit
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SizeValue
name|firstValue
init|=
operator|new
name|SizeValue
argument_list|(
name|number
argument_list|,
name|randomUnit
argument_list|)
decl_stmt|;
name|SizeValue
name|secondValue
init|=
operator|new
name|SizeValue
argument_list|(
name|number
argument_list|,
name|SizeUnit
operator|.
name|PETA
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|firstValue
operator|.
name|compareTo
argument_list|(
name|secondValue
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondValue
operator|.
name|compareTo
argument_list|(
name|firstValue
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testConversionHashCode
specifier|public
name|void
name|testConversionHashCode
parameter_list|()
block|{
name|SizeValue
name|firstValue
init|=
operator|new
name|SizeValue
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|SizeUnit
operator|.
name|GIGA
argument_list|)
decl_stmt|;
name|SizeValue
name|secondValue
init|=
operator|new
name|SizeValue
argument_list|(
name|firstValue
operator|.
name|getSingles
argument_list|()
argument_list|,
name|SizeUnit
operator|.
name|SINGLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|firstValue
operator|.
name|hashCode
argument_list|()
argument_list|,
name|secondValue
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

