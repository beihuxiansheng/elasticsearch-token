begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Circle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|impl
operator|.
name|PointImpl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|Coordinate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|LineString
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|Polygon
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
name|builders
operator|.
name|PolygonBuilder
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
name|builders
operator|.
name|ShapeBuilder
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchGeoAssertions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests for {@link ShapeBuilder}  */
end_comment

begin_class
DECL|class|ShapeBuilderTests
specifier|public
class|class
name|ShapeBuilderTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testNewPoint
specifier|public
name|void
name|testNewPoint
parameter_list|()
block|{
name|Point
name|point
init|=
name|ShapeBuilder
operator|.
name|newPoint
argument_list|(
operator|-
literal|100
argument_list|,
literal|45
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|100D
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|45D
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNewRectangle
specifier|public
name|void
name|testNewRectangle
parameter_list|()
block|{
name|Rectangle
name|rectangle
init|=
name|ShapeBuilder
operator|.
name|newEnvelope
argument_list|()
operator|.
name|topLeft
argument_list|(
operator|-
literal|45
argument_list|,
literal|30
argument_list|)
operator|.
name|bottomRight
argument_list|(
literal|45
argument_list|,
operator|-
literal|30
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|45D
argument_list|,
name|rectangle
operator|.
name|getMinX
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|30D
argument_list|,
name|rectangle
operator|.
name|getMinY
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|45D
argument_list|,
name|rectangle
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30D
argument_list|,
name|rectangle
operator|.
name|getMaxY
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNewPolygon
specifier|public
name|void
name|testNewPolygon
parameter_list|()
block|{
name|Polygon
name|polygon
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|45
argument_list|,
literal|30
argument_list|)
operator|.
name|point
argument_list|(
literal|45
argument_list|,
literal|30
argument_list|)
operator|.
name|point
argument_list|(
literal|45
argument_list|,
operator|-
literal|30
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|45
argument_list|,
operator|-
literal|30
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|45
argument_list|,
literal|30
argument_list|)
operator|.
name|toPolygon
argument_list|()
decl_stmt|;
name|LineString
name|exterior
init|=
name|polygon
operator|.
name|getExteriorRing
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|exterior
operator|.
name|getCoordinateN
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Coordinate
argument_list|(
operator|-
literal|45
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exterior
operator|.
name|getCoordinateN
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|Coordinate
argument_list|(
literal|45
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exterior
operator|.
name|getCoordinateN
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|Coordinate
argument_list|(
literal|45
argument_list|,
operator|-
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exterior
operator|.
name|getCoordinateN
argument_list|(
literal|3
argument_list|)
argument_list|,
operator|new
name|Coordinate
argument_list|(
operator|-
literal|45
argument_list|,
operator|-
literal|30
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLineStringBuilder
specifier|public
name|void
name|testLineStringBuilder
parameter_list|()
block|{
comment|// Building a simple LineString
name|ShapeBuilder
operator|.
name|newLineString
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|130.0
argument_list|,
literal|55.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|130.0
argument_list|,
operator|-
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|15.0
argument_list|,
operator|-
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|20.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|45.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|45.0
argument_list|,
operator|-
literal|15.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|110.0
argument_list|,
operator|-
literal|15.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|110.0
argument_list|,
literal|55.0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Building a linestring that needs to be wrapped
name|ShapeBuilder
operator|.
name|newLineString
argument_list|()
operator|.
name|point
argument_list|(
literal|100.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
literal|110.0
argument_list|,
operator|-
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
literal|240.0
argument_list|,
operator|-
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
literal|230.0
argument_list|,
literal|60.0
argument_list|)
operator|.
name|point
argument_list|(
literal|200.0
argument_list|,
literal|60.0
argument_list|)
operator|.
name|point
argument_list|(
literal|200.0
argument_list|,
operator|-
literal|30.0
argument_list|)
operator|.
name|point
argument_list|(
literal|130.0
argument_list|,
operator|-
literal|30.0
argument_list|)
operator|.
name|point
argument_list|(
literal|130.0
argument_list|,
literal|60.0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Building a lineString on the dateline
name|ShapeBuilder
operator|.
name|newLineString
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|180.0
argument_list|,
literal|80.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|180.0
argument_list|,
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|180.0
argument_list|,
operator|-
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|180.0
argument_list|,
operator|-
literal|80.0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Building a lineString on the dateline
name|ShapeBuilder
operator|.
name|newLineString
argument_list|()
operator|.
name|point
argument_list|(
literal|180.0
argument_list|,
literal|80.0
argument_list|)
operator|.
name|point
argument_list|(
literal|180.0
argument_list|,
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
literal|180.0
argument_list|,
operator|-
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
literal|180.0
argument_list|,
operator|-
literal|80.0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiLineString
specifier|public
name|void
name|testMultiLineString
parameter_list|()
block|{
name|ShapeBuilder
operator|.
name|newMultiLinestring
argument_list|()
operator|.
name|linestring
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|100.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
literal|50.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
literal|50.0
argument_list|,
literal|20.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|100.0
argument_list|,
literal|20.0
argument_list|)
operator|.
name|end
argument_list|()
operator|.
name|linestring
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|100.0
argument_list|,
literal|20.0
argument_list|)
operator|.
name|point
argument_list|(
literal|50.0
argument_list|,
literal|20.0
argument_list|)
operator|.
name|point
argument_list|(
literal|50.0
argument_list|,
literal|0.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|100.0
argument_list|,
literal|0.0
argument_list|)
operator|.
name|end
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// LineString that needs to be wrappped
name|ShapeBuilder
operator|.
name|newMultiLinestring
argument_list|()
operator|.
name|linestring
argument_list|()
operator|.
name|point
argument_list|(
literal|150.0
argument_list|,
literal|60.0
argument_list|)
operator|.
name|point
argument_list|(
literal|200.0
argument_list|,
literal|60.0
argument_list|)
operator|.
name|point
argument_list|(
literal|200.0
argument_list|,
literal|40.0
argument_list|)
operator|.
name|point
argument_list|(
literal|150.0
argument_list|,
literal|40.0
argument_list|)
operator|.
name|end
argument_list|()
operator|.
name|linestring
argument_list|()
operator|.
name|point
argument_list|(
literal|150.0
argument_list|,
literal|20.0
argument_list|)
operator|.
name|point
argument_list|(
literal|200.0
argument_list|,
literal|20.0
argument_list|)
operator|.
name|point
argument_list|(
literal|200.0
argument_list|,
literal|0.0
argument_list|)
operator|.
name|point
argument_list|(
literal|150.0
argument_list|,
literal|0.0
argument_list|)
operator|.
name|end
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPolygonSelfIntersection
specifier|public
name|void
name|testPolygonSelfIntersection
parameter_list|()
block|{
try|try
block|{
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|40.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
literal|40.0
argument_list|,
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|40.0
argument_list|,
operator|-
literal|50.0
argument_list|)
operator|.
name|point
argument_list|(
literal|40.0
argument_list|,
operator|-
literal|50.0
argument_list|)
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Polygon self-intersection"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{}
block|}
annotation|@
name|Test
DECL|method|testGeoCircle
specifier|public
name|void
name|testGeoCircle
parameter_list|()
block|{
name|double
name|earthCircumference
init|=
literal|40075016.69
decl_stmt|;
name|Circle
name|circle
init|=
name|ShapeBuilder
operator|.
name|newCircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|radius
argument_list|(
literal|"100m"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|360
operator|*
literal|100
operator|)
operator|/
name|earthCircumference
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Point
operator|)
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|)
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|circle
operator|=
name|ShapeBuilder
operator|.
name|newCircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
operator|+
literal|180
argument_list|,
literal|0
argument_list|)
operator|.
name|radius
argument_list|(
literal|"100m"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|360
operator|*
literal|100
operator|)
operator|/
name|earthCircumference
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Point
operator|)
operator|new
name|PointImpl
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|,
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|)
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|circle
operator|=
name|ShapeBuilder
operator|.
name|newCircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
operator|-
literal|180
argument_list|,
literal|0
argument_list|)
operator|.
name|radius
argument_list|(
literal|"100m"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|360
operator|*
literal|100
operator|)
operator|/
name|earthCircumference
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Point
operator|)
operator|new
name|PointImpl
argument_list|(
operator|-
literal|180
argument_list|,
literal|0
argument_list|,
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|)
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|circle
operator|=
name|ShapeBuilder
operator|.
name|newCircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|)
operator|.
name|radius
argument_list|(
literal|"100m"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|360
operator|*
literal|100
operator|)
operator|/
name|earthCircumference
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Point
operator|)
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|,
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|)
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|circle
operator|=
name|ShapeBuilder
operator|.
name|newCircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
literal|0
argument_list|,
operator|-
literal|90
argument_list|)
operator|.
name|radius
argument_list|(
literal|"100m"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|360
operator|*
literal|100
operator|)
operator|/
name|earthCircumference
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Point
operator|)
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
operator|-
literal|90
argument_list|,
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|)
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|randomLat
init|=
operator|(
name|randomDouble
argument_list|()
operator|*
literal|180
operator|)
operator|-
literal|90
decl_stmt|;
name|double
name|randomLon
init|=
operator|(
name|randomDouble
argument_list|()
operator|*
literal|360
operator|)
operator|-
literal|180
decl_stmt|;
name|double
name|randomRadius
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|earthCircumference
operator|/
literal|4
argument_list|)
decl_stmt|;
name|circle
operator|=
name|ShapeBuilder
operator|.
name|newCircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
name|randomLon
argument_list|,
name|randomLat
argument_list|)
operator|.
name|radius
argument_list|(
name|randomRadius
operator|+
literal|"m"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|360
operator|*
name|randomRadius
operator|)
operator|/
name|earthCircumference
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Point
operator|)
operator|new
name|PointImpl
argument_list|(
name|randomLon
argument_list|,
name|randomLat
argument_list|,
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|)
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPolygonWrapping
specifier|public
name|void
name|testPolygonWrapping
parameter_list|()
block|{
name|Shape
name|shape
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|150.0
argument_list|,
literal|65.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|250.0
argument_list|,
literal|65.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|250.0
argument_list|,
operator|-
literal|65.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|150.0
argument_list|,
operator|-
literal|65.0
argument_list|)
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertMultiPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLineStringWrapping
specifier|public
name|void
name|testLineStringWrapping
parameter_list|()
block|{
name|Shape
name|shape
init|=
name|ShapeBuilder
operator|.
name|newLineString
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|150.0
argument_list|,
literal|65.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|250.0
argument_list|,
literal|65.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|250.0
argument_list|,
operator|-
literal|65.0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|150.0
argument_list|,
operator|-
literal|65.0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertMultiLineString
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDateline
specifier|public
name|void
name|testDateline
parameter_list|()
block|{
comment|// view shape at https://gist.github.com/anonymous/7f1bb6d7e9cd72f5977c
comment|// expect 3 polygons, 1 with a hole
comment|// a giant c shape
name|PolygonBuilder
name|builder
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|186
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|176
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|176
argument_list|,
literal|3
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|183
argument_list|,
literal|3
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|183
argument_list|,
literal|5
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|176
argument_list|,
literal|5
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|176
argument_list|,
literal|8
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|186
argument_list|,
literal|8
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|186
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// 3/4 of an embedded 'c', crossing dateline once
name|builder
operator|.
name|hole
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|185
argument_list|,
literal|1
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|181
argument_list|,
literal|1
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|181
argument_list|,
literal|2
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|184
argument_list|,
literal|2
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|184
argument_list|,
literal|6
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|178
argument_list|,
literal|6
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|178
argument_list|,
literal|7
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|185
argument_list|,
literal|7
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|185
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// embedded hole right of the dateline
name|builder
operator|.
name|hole
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|179
argument_list|,
literal|1
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|177
argument_list|,
literal|1
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|177
argument_list|,
literal|2
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|179
argument_list|,
literal|2
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|179
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Shape
name|shape
init|=
name|builder
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertMultiPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComplexShapeWithHole
specifier|public
name|void
name|testComplexShapeWithHole
parameter_list|()
block|{
name|PolygonBuilder
name|builder
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|85.0018514
argument_list|,
literal|37.1311314
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0016645
argument_list|,
literal|37.1315293
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0016246
argument_list|,
literal|37.1317069
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0016526
argument_list|,
literal|37.1318183
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0017119
argument_list|,
literal|37.1319196
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0019371
argument_list|,
literal|37.1321182
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0019972
argument_list|,
literal|37.1322115
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0019942
argument_list|,
literal|37.1323234
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0019543
argument_list|,
literal|37.1324336
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.001906
argument_list|,
literal|37.1324985
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.001834
argument_list|,
literal|37.1325497
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0016965
argument_list|,
literal|37.1325907
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0016011
argument_list|,
literal|37.1325873
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0014816
argument_list|,
literal|37.1325353
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0011755
argument_list|,
literal|37.1323509
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.000955
argument_list|,
literal|37.1322802
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0006241
argument_list|,
literal|37.1322529
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0000002
argument_list|,
literal|37.1322307
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9994
argument_list|,
literal|37.1323001
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.999109
argument_list|,
literal|37.1322864
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.998934
argument_list|,
literal|37.1322415
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9988639
argument_list|,
literal|37.1321888
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9987841
argument_list|,
literal|37.1320944
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9987208
argument_list|,
literal|37.131954
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.998736
argument_list|,
literal|37.1316611
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9988091
argument_list|,
literal|37.131334
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9989283
argument_list|,
literal|37.1311337
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9991943
argument_list|,
literal|37.1309198
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9993573
argument_list|,
literal|37.1308459
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9995888
argument_list|,
literal|37.1307924
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9998746
argument_list|,
literal|37.130806
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0000002
argument_list|,
literal|37.1308358
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0004984
argument_list|,
literal|37.1310658
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0008008
argument_list|,
literal|37.1311625
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0009461
argument_list|,
literal|37.1311684
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0011373
argument_list|,
literal|37.1311515
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0016455
argument_list|,
literal|37.1310491
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0018514
argument_list|,
literal|37.1311314
argument_list|)
decl_stmt|;
name|builder
operator|.
name|hole
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|85.0000002
argument_list|,
literal|37.1317672
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0001983
argument_list|,
literal|37.1317538
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0003378
argument_list|,
literal|37.1317582
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0004697
argument_list|,
literal|37.131792
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0008048
argument_list|,
literal|37.1319439
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0009342
argument_list|,
literal|37.1319838
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0010184
argument_list|,
literal|37.1319463
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0010618
argument_list|,
literal|37.13184
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0010057
argument_list|,
literal|37.1315102
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.000977
argument_list|,
literal|37.1314403
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0009182
argument_list|,
literal|37.1313793
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0005366
argument_list|,
literal|37.1312209
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.000224
argument_list|,
literal|37.1311466
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.000087
argument_list|,
literal|37.1311356
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0000002
argument_list|,
literal|37.1311433
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9995021
argument_list|,
literal|37.1312336
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9993308
argument_list|,
literal|37.1312859
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9992567
argument_list|,
literal|37.1313252
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9991868
argument_list|,
literal|37.1314277
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9991593
argument_list|,
literal|37.1315381
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9991841
argument_list|,
literal|37.1316527
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9992329
argument_list|,
literal|37.1317117
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9993527
argument_list|,
literal|37.1317788
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9994931
argument_list|,
literal|37.1318061
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|84.9996815
argument_list|,
literal|37.1317979
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|85.0000002
argument_list|,
literal|37.1317672
argument_list|)
expr_stmt|;
name|Shape
name|shape
init|=
name|builder
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShapeWithHoleAtEdgeEndPoints
specifier|public
name|void
name|testShapeWithHoleAtEdgeEndPoints
parameter_list|()
block|{
name|PolygonBuilder
name|builder
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
operator|-
literal|4
argument_list|,
literal|2
argument_list|)
operator|.
name|point
argument_list|(
literal|4
argument_list|,
literal|2
argument_list|)
operator|.
name|point
argument_list|(
literal|6
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
literal|4
argument_list|,
operator|-
literal|2
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|4
argument_list|,
operator|-
literal|2
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|6
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|4
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|builder
operator|.
name|hole
argument_list|()
operator|.
name|point
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|)
operator|.
name|point
argument_list|(
literal|4
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|4
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|4
argument_list|,
literal|1
argument_list|)
operator|.
name|point
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Shape
name|shape
init|=
name|builder
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShapeWithPointOnDateline
specifier|public
name|void
name|testShapeWithPointOnDateline
parameter_list|()
block|{
name|PolygonBuilder
name|builder
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
literal|176
argument_list|,
literal|4
argument_list|)
operator|.
name|point
argument_list|(
literal|176
argument_list|,
operator|-
literal|4
argument_list|)
operator|.
name|point
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Shape
name|shape
init|=
name|builder
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShapeWithEdgeAlongDateline
specifier|public
name|void
name|testShapeWithEdgeAlongDateline
parameter_list|()
block|{
name|PolygonBuilder
name|builder
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
literal|176
argument_list|,
literal|4
argument_list|)
operator|.
name|point
argument_list|(
literal|180
argument_list|,
operator|-
literal|4
argument_list|)
operator|.
name|point
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Shape
name|shape
init|=
name|builder
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShapeWithEdgeAcrossDateline
specifier|public
name|void
name|testShapeWithEdgeAcrossDateline
parameter_list|()
block|{
name|PolygonBuilder
name|builder
init|=
name|ShapeBuilder
operator|.
name|newPolygon
argument_list|()
operator|.
name|point
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|)
operator|.
name|point
argument_list|(
literal|176
argument_list|,
literal|4
argument_list|)
operator|.
name|point
argument_list|(
operator|-
literal|176
argument_list|,
literal|4
argument_list|)
operator|.
name|point
argument_list|(
literal|180
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Shape
name|shape
init|=
name|builder
operator|.
name|close
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertPolygon
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

