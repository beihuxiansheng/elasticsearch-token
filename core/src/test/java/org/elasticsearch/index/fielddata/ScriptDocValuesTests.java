begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|ScriptDocValuesTests
specifier|public
class|class
name|ScriptDocValuesTests
extends|extends
name|ESTestCase
block|{
DECL|method|wrap
specifier|private
specifier|static
name|MultiGeoPointValues
name|wrap
parameter_list|(
specifier|final
name|GeoPoint
modifier|...
name|points
parameter_list|)
block|{
return|return
operator|new
name|MultiGeoPointValues
argument_list|()
block|{
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|GeoPoint
name|valueAt
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|!=
literal|0
condition|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
return|return
name|points
index|[
name|i
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docID
operator|=
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
if|if
condition|(
name|docID
operator|!=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|points
operator|.
name|length
return|;
block|}
block|}
return|;
block|}
DECL|method|randomLat
specifier|private
specifier|static
name|double
name|randomLat
parameter_list|()
block|{
return|return
name|randomDouble
argument_list|()
operator|*
literal|180
operator|-
literal|90
return|;
block|}
DECL|method|randomLon
specifier|private
specifier|static
name|double
name|randomLon
parameter_list|()
block|{
return|return
name|randomDouble
argument_list|()
operator|*
literal|360
operator|-
literal|180
return|;
block|}
DECL|method|testGeoGetLatLon
specifier|public
name|void
name|testGeoGetLatLon
parameter_list|()
block|{
specifier|final
name|double
name|lat1
init|=
name|randomLat
argument_list|()
decl_stmt|;
specifier|final
name|double
name|lat2
init|=
name|randomLat
argument_list|()
decl_stmt|;
specifier|final
name|double
name|lon1
init|=
name|randomLon
argument_list|()
decl_stmt|;
specifier|final
name|double
name|lon2
init|=
name|randomLon
argument_list|()
decl_stmt|;
specifier|final
name|MultiGeoPointValues
name|values
init|=
name|wrap
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ScriptDocValues
operator|.
name|GeoPoints
name|script
init|=
operator|new
name|ScriptDocValues
operator|.
name|GeoPoints
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|script
operator|.
name|setNextDocId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|script
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|script
operator|.
name|setNextDocId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|script
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|,
name|script
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|)
argument_list|,
name|script
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lat1
argument_list|,
name|script
operator|.
name|getLat
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lon1
argument_list|,
name|script
operator|.
name|getLon
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
operator|new
name|double
index|[]
block|{
name|lat1
block|,
name|lat2
block|}
argument_list|,
name|script
operator|.
name|getLats
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
operator|new
name|double
index|[]
block|{
name|lon1
block|,
name|lon2
block|}
argument_list|,
name|script
operator|.
name|getLons
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoDistance
specifier|public
name|void
name|testGeoDistance
parameter_list|()
block|{
specifier|final
name|double
name|lat
init|=
name|randomLat
argument_list|()
decl_stmt|;
specifier|final
name|double
name|lon
init|=
name|randomLon
argument_list|()
decl_stmt|;
specifier|final
name|MultiGeoPointValues
name|values
init|=
name|wrap
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ScriptDocValues
operator|.
name|GeoPoints
name|script
init|=
operator|new
name|ScriptDocValues
operator|.
name|GeoPoints
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|script
operator|.
name|setNextDocId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|ScriptDocValues
operator|.
name|GeoPoints
name|emptyScript
init|=
operator|new
name|ScriptDocValues
operator|.
name|GeoPoints
argument_list|(
name|wrap
argument_list|()
argument_list|)
decl_stmt|;
name|emptyScript
operator|.
name|setNextDocId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|double
name|otherLat
init|=
name|randomLat
argument_list|()
decl_stmt|;
specifier|final
name|double
name|otherLon
init|=
name|randomLon
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|otherLat
argument_list|,
name|otherLon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|script
operator|.
name|arcDistanceInKm
argument_list|(
name|otherLat
argument_list|,
name|otherLon
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|otherLat
argument_list|,
name|otherLon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|script
operator|.
name|arcDistanceInKmWithDefault
argument_list|(
name|otherLat
argument_list|,
name|otherLon
argument_list|,
literal|42
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|emptyScript
operator|.
name|arcDistanceInKmWithDefault
argument_list|(
name|otherLat
argument_list|,
name|otherLon
argument_list|,
literal|42
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|otherLat
argument_list|,
name|otherLon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|script
operator|.
name|distanceInKm
argument_list|(
name|otherLat
argument_list|,
name|otherLon
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|otherLat
argument_list|,
name|otherLon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
name|script
operator|.
name|distanceInKmWithDefault
argument_list|(
name|otherLat
argument_list|,
name|otherLon
argument_list|,
literal|42
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|emptyScript
operator|.
name|distanceInKmWithDefault
argument_list|(
name|otherLat
argument_list|,
name|otherLon
argument_list|,
literal|42
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

