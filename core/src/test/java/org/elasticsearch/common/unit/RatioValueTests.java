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
name|ElasticsearchParseException
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

begin_comment
comment|/**  * Tests for the {@link RatioValue} class  */
end_comment

begin_class
DECL|class|RatioValueTests
specifier|public
class|class
name|RatioValueTests
extends|extends
name|ESTestCase
block|{
DECL|method|testParsing
specifier|public
name|void
name|testParsing
parameter_list|()
block|{
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"100%"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"100.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"0%"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"-0%"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"15.1%"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"15.1%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"0.1%"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.1%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"1.0"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"100.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"0"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"-0"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"0.0"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"-0.0"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"0.151"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"15.1%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
literal|"0.001"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.1%"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegativeCase
specifier|public
name|void
name|testNegativeCase
parameter_list|()
block|{
name|testInvalidRatio
argument_list|(
literal|"100.0001%"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"-0.1%"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"1a0%"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"-0.01"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"0.1.0"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"five"
argument_list|)
expr_stmt|;
name|testInvalidRatio
argument_list|(
literal|"1/2"
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidRatio
specifier|public
name|void
name|testInvalidRatio
parameter_list|(
name|String
name|r
parameter_list|)
block|{
try|try
block|{
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Value: ["
operator|+
name|r
operator|+
literal|"] should be an invalid ratio"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
end_class

end_unit

