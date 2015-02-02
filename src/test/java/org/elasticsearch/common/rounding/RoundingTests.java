begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.rounding
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|rounding
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
name|ElasticsearchTestCase
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
name|greaterThan
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
DECL|class|RoundingTests
specifier|public
class|class
name|RoundingTests
extends|extends
name|ElasticsearchTestCase
block|{
comment|/**      * simple testcase to ilustrate how Rounding.Interval works on readable input      */
annotation|@
name|Test
DECL|method|testInterval
specifier|public
name|void
name|testInterval
parameter_list|()
block|{
name|int
name|interval
init|=
literal|10
decl_stmt|;
name|Rounding
operator|.
name|Interval
name|rounding
init|=
operator|new
name|Rounding
operator|.
name|Interval
argument_list|(
name|interval
argument_list|)
decl_stmt|;
name|int
name|value
init|=
literal|24
decl_stmt|;
specifier|final
name|long
name|key
init|=
name|rounding
operator|.
name|roundKey
argument_list|(
literal|24
argument_list|)
decl_stmt|;
specifier|final
name|long
name|r
init|=
name|rounding
operator|.
name|round
argument_list|(
literal|24
argument_list|)
decl_stmt|;
name|String
name|message
init|=
literal|"round("
operator|+
name|value
operator|+
literal|", interval="
operator|+
name|interval
operator|+
literal|") = "
operator|+
name|r
decl_stmt|;
name|assertEquals
argument_list|(
name|value
operator|/
name|interval
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
operator|/
name|interval
operator|*
name|interval
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
literal|0
argument_list|,
name|r
operator|%
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntervalRandom
specifier|public
name|void
name|testIntervalRandom
parameter_list|()
block|{
specifier|final
name|long
name|interval
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Rounding
operator|.
name|Interval
name|rounding
init|=
operator|new
name|Rounding
operator|.
name|Interval
argument_list|(
name|interval
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|long
name|l
init|=
name|Math
operator|.
name|max
argument_list|(
name|randomLong
argument_list|()
argument_list|,
name|Long
operator|.
name|MIN_VALUE
operator|+
name|interval
argument_list|)
decl_stmt|;
specifier|final
name|long
name|key
init|=
name|rounding
operator|.
name|roundKey
argument_list|(
name|l
argument_list|)
decl_stmt|;
specifier|final
name|long
name|r
init|=
name|rounding
operator|.
name|round
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|String
name|message
init|=
literal|"round("
operator|+
name|l
operator|+
literal|", interval="
operator|+
name|interval
operator|+
literal|") = "
operator|+
name|r
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
literal|0
argument_list|,
name|r
operator|%
name|interval
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
argument_list|,
name|r
argument_list|,
name|lessThanOrEqualTo
argument_list|(
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
argument_list|,
name|r
operator|+
name|interval
argument_list|,
name|greaterThan
argument_list|(
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|r
argument_list|,
name|key
operator|*
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Simple testcase to ilustrate how Rounding.Pre works on readable input.      * preOffset shifts input value before rounding (so here 24 -> 31)      * postOffset shifts rounded Value after rounding (here 30 -> 35)      */
annotation|@
name|Test
DECL|method|testPrePostRounding
specifier|public
name|void
name|testPrePostRounding
parameter_list|()
block|{
name|int
name|interval
init|=
literal|10
decl_stmt|;
name|int
name|value
init|=
literal|24
decl_stmt|;
name|int
name|preOffset
init|=
literal|7
decl_stmt|;
name|int
name|postOffset
init|=
literal|5
decl_stmt|;
name|Rounding
operator|.
name|PrePostRounding
name|rounding
init|=
operator|new
name|Rounding
operator|.
name|PrePostRounding
argument_list|(
operator|new
name|Rounding
operator|.
name|Interval
argument_list|(
name|interval
argument_list|)
argument_list|,
name|preOffset
argument_list|,
name|postOffset
argument_list|)
decl_stmt|;
specifier|final
name|long
name|key
init|=
name|rounding
operator|.
name|roundKey
argument_list|(
literal|24
argument_list|)
decl_stmt|;
specifier|final
name|long
name|roundedValue
init|=
name|rounding
operator|.
name|round
argument_list|(
literal|24
argument_list|)
decl_stmt|;
name|String
name|message
init|=
literal|"round("
operator|+
name|value
operator|+
literal|", interval="
operator|+
name|interval
operator|+
literal|") = "
operator|+
name|roundedValue
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|35
argument_list|,
name|roundedValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|postOffset
argument_list|,
name|roundedValue
operator|%
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrePostRoundingRandom
specifier|public
name|void
name|testPrePostRoundingRandom
parameter_list|()
block|{
specifier|final
name|long
name|interval
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Rounding
operator|.
name|Interval
name|internalRounding
init|=
operator|new
name|Rounding
operator|.
name|Interval
argument_list|(
name|interval
argument_list|)
decl_stmt|;
specifier|final
name|long
name|preRounding
init|=
name|randomIntBetween
argument_list|(
operator|-
literal|100
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|long
name|postRounding
init|=
name|randomIntBetween
argument_list|(
operator|-
literal|100
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Rounding
operator|.
name|PrePostRounding
name|prePost
init|=
operator|new
name|Rounding
operator|.
name|PrePostRounding
argument_list|(
operator|new
name|Rounding
operator|.
name|Interval
argument_list|(
name|interval
argument_list|)
argument_list|,
name|preRounding
argument_list|,
name|postRounding
argument_list|)
decl_stmt|;
name|long
name|safetyMargin
init|=
name|Math
operator|.
name|abs
argument_list|(
name|interval
argument_list|)
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|preRounding
argument_list|)
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|postRounding
argument_list|)
decl_stmt|;
comment|// to prevent range overflow / underflow
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|long
name|l
init|=
name|Math
operator|.
name|max
argument_list|(
name|randomLong
argument_list|()
operator|-
name|safetyMargin
argument_list|,
name|Long
operator|.
name|MIN_VALUE
operator|+
name|safetyMargin
argument_list|)
decl_stmt|;
specifier|final
name|long
name|key
init|=
name|prePost
operator|.
name|roundKey
argument_list|(
name|l
argument_list|)
decl_stmt|;
specifier|final
name|long
name|r
init|=
name|prePost
operator|.
name|round
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|String
name|message
init|=
literal|"round("
operator|+
name|l
operator|+
literal|", interval="
operator|+
name|interval
operator|+
literal|") = "
operator|+
name|r
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|internalRounding
operator|.
name|round
argument_list|(
name|l
operator|+
name|preRounding
argument_list|)
argument_list|,
name|r
operator|-
name|postRounding
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
argument_list|,
name|r
operator|-
name|postRounding
argument_list|,
name|lessThanOrEqualTo
argument_list|(
name|l
operator|+
name|preRounding
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
argument_list|,
name|r
operator|+
name|interval
operator|-
name|postRounding
argument_list|,
name|greaterThan
argument_list|(
name|l
operator|+
name|preRounding
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|r
argument_list|,
name|key
operator|*
name|interval
operator|+
name|postRounding
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

