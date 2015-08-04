begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|number
operator|.
name|IsCloseTo
operator|.
name|closeTo
import|;
end_import

begin_class
DECL|class|SloppyMathTests
specifier|public
class|class
name|SloppyMathTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testAccuracy
specifier|public
name|void
name|testAccuracy
parameter_list|()
block|{
for|for
control|(
name|double
name|lat1
init|=
operator|-
literal|89
init|;
name|lat1
operator|<=
literal|89
condition|;
name|lat1
operator|+=
literal|1
control|)
block|{
specifier|final
name|double
name|lon1
init|=
name|randomLongitude
argument_list|()
decl_stmt|;
for|for
control|(
name|double
name|i
init|=
operator|-
literal|180
init|;
name|i
operator|<=
literal|180
condition|;
name|i
operator|+=
literal|1
control|)
block|{
specifier|final
name|double
name|lon2
init|=
name|i
decl_stmt|;
specifier|final
name|double
name|lat2
init|=
name|randomLatitude
argument_list|()
decl_stmt|;
name|assertAccurate
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSloppyMath
specifier|public
name|void
name|testSloppyMath
parameter_list|()
block|{
name|testSloppyMath
argument_list|(
name|DistanceUnit
operator|.
name|METERS
argument_list|,
literal|0.01
argument_list|,
literal|5
argument_list|,
literal|45
argument_list|,
literal|90
argument_list|)
expr_stmt|;
name|testSloppyMath
argument_list|(
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|,
literal|0.01
argument_list|,
literal|5
argument_list|,
literal|45
argument_list|,
literal|90
argument_list|)
expr_stmt|;
name|testSloppyMath
argument_list|(
name|DistanceUnit
operator|.
name|INCH
argument_list|,
literal|0.01
argument_list|,
literal|5
argument_list|,
literal|45
argument_list|,
literal|90
argument_list|)
expr_stmt|;
name|testSloppyMath
argument_list|(
name|DistanceUnit
operator|.
name|MILES
argument_list|,
literal|0.01
argument_list|,
literal|5
argument_list|,
literal|45
argument_list|,
literal|90
argument_list|)
expr_stmt|;
block|}
DECL|method|maxError
specifier|private
specifier|static
name|double
name|maxError
parameter_list|(
name|double
name|distance
parameter_list|)
block|{
return|return
name|distance
operator|/
literal|1000.0
return|;
block|}
DECL|method|testSloppyMath
specifier|private
name|void
name|testSloppyMath
parameter_list|(
name|DistanceUnit
name|unit
parameter_list|,
name|double
modifier|...
name|deltaDeg
parameter_list|)
block|{
specifier|final
name|double
name|lat1
init|=
name|randomLatitude
argument_list|()
decl_stmt|;
specifier|final
name|double
name|lon1
init|=
name|randomLongitude
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"testing SloppyMath with {} at \"{}, {}\""
argument_list|,
name|unit
argument_list|,
name|lat1
argument_list|,
name|lon1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|test
init|=
literal|0
init|;
name|test
operator|<
name|deltaDeg
operator|.
name|length
condition|;
name|test
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// crop pole areas, sine we now there the function
comment|// is not accurate around lat(89Â°, 90Â°) and lat(-90Â°, -89Â°)
specifier|final
name|double
name|lat2
init|=
name|Math
operator|.
name|max
argument_list|(
operator|-
literal|89.0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
operator|+
literal|89.0
argument_list|,
name|lat1
operator|+
operator|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|-
literal|0.5
operator|)
operator|*
literal|2
operator|*
name|deltaDeg
index|[
name|test
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lon2
init|=
name|lon1
operator|+
operator|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|-
literal|0.5
operator|)
operator|*
literal|2
operator|*
name|deltaDeg
index|[
name|test
index|]
decl_stmt|;
specifier|final
name|double
name|accurate
init|=
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|,
name|unit
argument_list|)
decl_stmt|;
specifier|final
name|double
name|dist
init|=
name|GeoDistance
operator|.
name|SLOPPY_ARC
operator|.
name|calculate
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"distance between("
operator|+
name|lat1
operator|+
literal|", "
operator|+
name|lon1
operator|+
literal|") and ("
operator|+
name|lat2
operator|+
literal|", "
operator|+
name|lon2
operator|+
literal|"))"
argument_list|,
name|dist
argument_list|,
name|closeTo
argument_list|(
name|accurate
argument_list|,
name|maxError
argument_list|(
name|accurate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertAccurate
specifier|private
specifier|static
name|void
name|assertAccurate
parameter_list|(
name|double
name|lat1
parameter_list|,
name|double
name|lon1
parameter_list|,
name|double
name|lat2
parameter_list|,
name|double
name|lon2
parameter_list|)
block|{
name|double
name|accurate
init|=
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
decl_stmt|;
name|double
name|sloppy
init|=
name|GeoDistance
operator|.
name|SLOPPY_ARC
operator|.
name|calculate
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"distance between("
operator|+
name|lat1
operator|+
literal|", "
operator|+
name|lon1
operator|+
literal|") and ("
operator|+
name|lat2
operator|+
literal|", "
operator|+
name|lon2
operator|+
literal|"))"
argument_list|,
name|sloppy
argument_list|,
name|closeTo
argument_list|(
name|accurate
argument_list|,
name|maxError
argument_list|(
name|accurate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|randomLatitude
specifier|private
specifier|static
specifier|final
name|double
name|randomLatitude
parameter_list|()
block|{
comment|// crop pole areas, sine we now there the function
comment|// is not accurate around lat(89Â°, 90Â°) and lat(-90Â°, -89Â°)
return|return
operator|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|-
literal|0.5
operator|)
operator|*
literal|178.0
return|;
block|}
DECL|method|randomLongitude
specifier|private
specifier|static
specifier|final
name|double
name|randomLongitude
parameter_list|()
block|{
return|return
operator|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|-
literal|0.5
operator|)
operator|*
literal|360.0
return|;
block|}
block|}
end_class

end_unit

