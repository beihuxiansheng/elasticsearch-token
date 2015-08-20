begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
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
name|common
operator|.
name|geo
operator|.
name|GeoDistance
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
name|geo
operator|.
name|GeoPoint
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
name|unit
operator|.
name|DistanceUnit
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
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|GeoDistanceTests
specifier|public
class|class
name|GeoDistanceTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testDistanceCheck
specifier|public
name|void
name|testDistanceCheck
parameter_list|()
block|{
comment|// Note, is within is an approximation, so, even though 0.52 is outside 50mi, we still get "true"
name|GeoDistance
operator|.
name|DistanceBoundingCheck
name|check
init|=
name|GeoDistance
operator|.
name|distanceBoundingCheck
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|50
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|isWithin
argument_list|(
literal|0.5
argument_list|,
literal|0.5
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|isWithin
argument_list|(
literal|0.52
argument_list|,
literal|0.52
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|isWithin
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|check
operator|=
name|GeoDistance
operator|.
name|distanceBoundingCheck
argument_list|(
literal|0
argument_list|,
literal|179
argument_list|,
literal|200
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|isWithin
argument_list|(
literal|0
argument_list|,
operator|-
literal|179
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|isWithin
argument_list|(
literal|0
argument_list|,
operator|-
literal|178
argument_list|)
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
DECL|method|testArcDistanceVsPlaneInEllipsis
specifier|public
name|void
name|testArcDistanceVsPlaneInEllipsis
parameter_list|()
block|{
name|GeoPoint
name|centre
init|=
operator|new
name|GeoPoint
argument_list|(
literal|48.8534100
argument_list|,
literal|2.3488000
argument_list|)
decl_stmt|;
name|GeoPoint
name|northernPoint
init|=
operator|new
name|GeoPoint
argument_list|(
literal|48.8801108681
argument_list|,
literal|2.35152032666
argument_list|)
decl_stmt|;
name|GeoPoint
name|westernPoint
init|=
operator|new
name|GeoPoint
argument_list|(
literal|48.85265
argument_list|,
literal|2.308896
argument_list|)
decl_stmt|;
comment|// With GeoDistance.ARC both the northern and western points are within the 4km range
name|assertThat
argument_list|(
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|centre
operator|.
name|lat
argument_list|()
argument_list|,
name|centre
operator|.
name|lon
argument_list|()
argument_list|,
name|northernPoint
operator|.
name|lat
argument_list|()
argument_list|,
name|northernPoint
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|lessThan
argument_list|(
literal|4D
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|centre
operator|.
name|lat
argument_list|()
argument_list|,
name|centre
operator|.
name|lon
argument_list|()
argument_list|,
name|westernPoint
operator|.
name|lat
argument_list|()
argument_list|,
name|westernPoint
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|lessThan
argument_list|(
literal|4D
argument_list|)
argument_list|)
expr_stmt|;
comment|// With GeoDistance.PLANE, only the northern point is within the 4km range,
comment|// the western point is outside of the range due to the simple math it employs,
comment|// meaning results will appear elliptical
name|assertThat
argument_list|(
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|centre
operator|.
name|lat
argument_list|()
argument_list|,
name|centre
operator|.
name|lon
argument_list|()
argument_list|,
name|northernPoint
operator|.
name|lat
argument_list|()
argument_list|,
name|northernPoint
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|lessThan
argument_list|(
literal|4D
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|centre
operator|.
name|lat
argument_list|()
argument_list|,
name|centre
operator|.
name|lon
argument_list|()
argument_list|,
name|westernPoint
operator|.
name|lat
argument_list|()
argument_list|,
name|westernPoint
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|4D
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
