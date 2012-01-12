begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|search
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
operator|.
name|GeoUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
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
name|MatcherAssert
operator|.
name|assertThat
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
name|not
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|GeoUtilsTests
specifier|public
class|class
name|GeoUtilsTests
block|{
comment|/**      * Test special values like inf, NaN and -0.0.      */
annotation|@
name|Test
DECL|method|testSpecials
specifier|public
name|void
name|testSpecials
parameter_list|()
block|{
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|0.0
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|0.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|0.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|0.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
literal|0.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
literal|0.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test bounding values.      */
annotation|@
name|Test
DECL|method|testBounds
specifier|public
name|void
name|testBounds
parameter_list|()
block|{
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|360.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
literal|360.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
literal|180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
comment|// and halves
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|90.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
literal|180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
literal|90.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test normal values.      */
annotation|@
name|Test
DECL|method|testNormal
specifier|public
name|void
name|testNormal
parameter_list|()
block|{
comment|// Near bounds
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|360.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|180.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
literal|360.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
literal|180.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
comment|// and near halves
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|180.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|179.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|90.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|89.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
literal|180.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|179.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
literal|90.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|89.5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Every 10-units, multiple full turns
for|for
control|(
name|int
name|shift
init|=
operator|-
literal|20
init|;
name|shift
operator|<=
literal|20
condition|;
operator|++
name|shift
control|)
block|{
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|0.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|10.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|10.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|20.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|20.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|30.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|30.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|40.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|50.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|60.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|60.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|70.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|70.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|80.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|80.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|90.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|100.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|110.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|110.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|120.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|120.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|130.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|130.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|140.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|140.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|150.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|150.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|160.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|160.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|170.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|170.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|190.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|170.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|200.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|160.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|210.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|150.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|220.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|140.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|230.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|130.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|240.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|120.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|250.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|110.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|260.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|100.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|270.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|280.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|80.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|290.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|70.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|300.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|60.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|310.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|320.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|330.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|30.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|340.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|20.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|350.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|10.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|shift
operator|*
literal|360.0
operator|+
literal|360.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|shift
init|=
operator|-
literal|20
init|;
name|shift
operator|<=
literal|20
condition|;
operator|++
name|shift
control|)
block|{
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|0.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|10.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|10.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|20.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|20.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|30.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|30.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|40.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|50.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|60.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|60.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|70.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|70.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|80.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|80.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|90.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|100.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|80.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|110.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|70.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|120.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|60.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|130.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|140.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|150.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|30.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|160.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|20.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|170.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|10.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|shift
operator|*
literal|180.0
operator|+
literal|180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test huge values.      */
annotation|@
name|Test
DECL|method|testHuge
specifier|public
name|void
name|testHuge
parameter_list|()
block|{
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|36000000000181.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|181.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|36000000000180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|36000000000179.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|179.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|36000000000178.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|178.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|36000000000001.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|-
literal|001.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000000.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|000.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000001.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|001.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000002.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|002.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000178.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|178.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000179.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|179.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000180.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|180.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|36000000000181.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
operator|+
literal|181.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|18000000000091.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|091.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|18000000000090.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|090.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|18000000000089.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|089.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|18000000000088.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|088.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|18000000000001.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|-
literal|001.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000000.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|000.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000001.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|001.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000002.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|002.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000088.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|088.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000089.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|089.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000090.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|090.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|18000000000091.0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
operator|+
literal|091.0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

