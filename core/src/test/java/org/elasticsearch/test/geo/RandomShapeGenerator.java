begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
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
name|algorithm
operator|.
name|ConvexHull
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
name|Geometry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|CoordinateCollection
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
name|CoordinatesBuilder
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
name|GeometryCollectionBuilder
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
name|LineStringBuilder
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
name|MultiLineStringBuilder
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
name|MultiPointBuilder
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
name|PointBuilder
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
name|search
operator|.
name|geo
operator|.
name|GeoShapeQueryTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|jts
operator|.
name|JtsSpatialContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|distance
operator|.
name|DistanceUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|exception
operator|.
name|InvalidShapeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|impl
operator|.
name|Range
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|CONTAINS
import|;
end_import

begin_comment
comment|/**  * Random geoshape generation utilities for randomized {@code geo_shape} type testing  * depends on jts and spatial4j  */
end_comment

begin_class
DECL|class|RandomShapeGenerator
specifier|public
class|class
name|RandomShapeGenerator
extends|extends
name|RandomGeoGenerator
block|{
DECL|field|ctx
specifier|protected
specifier|static
name|JtsSpatialContext
name|ctx
init|=
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
decl_stmt|;
DECL|field|xDIVISIBLE
specifier|protected
specifier|static
specifier|final
name|double
name|xDIVISIBLE
init|=
literal|2
decl_stmt|;
DECL|field|ST_VALIDATE
specifier|protected
specifier|static
name|boolean
name|ST_VALIDATE
init|=
literal|true
decl_stmt|;
DECL|enum|ShapeType
specifier|public
specifier|static
enum|enum
name|ShapeType
block|{
DECL|enum constant|POINT
DECL|enum constant|MULTIPOINT
DECL|enum constant|LINESTRING
DECL|enum constant|MULTILINESTRING
DECL|enum constant|POLYGON
name|POINT
block|,
name|MULTIPOINT
block|,
name|LINESTRING
block|,
name|MULTILINESTRING
block|,
name|POLYGON
block|;
DECL|field|types
specifier|private
specifier|static
specifier|final
name|ShapeType
index|[]
name|types
init|=
name|values
argument_list|()
decl_stmt|;
DECL|method|randomType
specifier|public
specifier|static
name|ShapeType
name|randomType
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
return|return
name|types
index|[
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
name|types
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
return|;
block|}
block|}
DECL|method|createShape
specifier|public
specifier|static
name|ShapeBuilder
name|createShape
parameter_list|(
name|Random
name|r
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createShapeNear
argument_list|(
name|r
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createShape
specifier|public
specifier|static
name|ShapeBuilder
name|createShape
parameter_list|(
name|Random
name|r
parameter_list|,
name|ShapeType
name|st
parameter_list|)
block|{
return|return
name|createShapeNear
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
name|st
argument_list|)
return|;
block|}
DECL|method|createShapeNear
specifier|public
specifier|static
name|ShapeBuilder
name|createShapeNear
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createShape
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createShapeNear
specifier|public
specifier|static
name|ShapeBuilder
name|createShapeNear
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|,
name|ShapeType
name|st
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createShape
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
literal|null
argument_list|,
name|st
argument_list|)
return|;
block|}
DECL|method|createShapeWithin
specifier|public
specifier|static
name|ShapeBuilder
name|createShapeWithin
parameter_list|(
name|Random
name|r
parameter_list|,
name|Rectangle
name|bbox
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createShape
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
name|bbox
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createShapeWithin
specifier|public
specifier|static
name|ShapeBuilder
name|createShapeWithin
parameter_list|(
name|Random
name|r
parameter_list|,
name|Rectangle
name|bbox
parameter_list|,
name|ShapeType
name|st
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createShape
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
name|bbox
argument_list|,
name|st
argument_list|)
return|;
block|}
DECL|method|createGeometryCollection
specifier|public
specifier|static
name|GeometryCollectionBuilder
name|createGeometryCollection
parameter_list|(
name|Random
name|r
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createGeometryCollection
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|createGeometryCollectionNear
specifier|public
specifier|static
name|GeometryCollectionBuilder
name|createGeometryCollectionNear
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createGeometryCollection
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|createGeometryCollectionNear
specifier|public
specifier|static
name|GeometryCollectionBuilder
name|createGeometryCollectionNear
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createGeometryCollection
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
literal|null
argument_list|,
name|size
argument_list|)
return|;
block|}
DECL|method|createGeometryCollectionWithin
specifier|public
specifier|static
name|GeometryCollectionBuilder
name|createGeometryCollectionWithin
parameter_list|(
name|Random
name|r
parameter_list|,
name|Rectangle
name|within
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createGeometryCollection
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
name|within
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|createGeometryCollectionWithin
specifier|public
specifier|static
name|GeometryCollectionBuilder
name|createGeometryCollectionWithin
parameter_list|(
name|Random
name|r
parameter_list|,
name|Rectangle
name|within
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
return|return
name|createGeometryCollection
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
name|within
argument_list|,
name|size
argument_list|)
return|;
block|}
DECL|method|createGeometryCollection
specifier|protected
specifier|static
name|GeometryCollectionBuilder
name|createGeometryCollection
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|,
name|Rectangle
name|bounds
parameter_list|,
name|int
name|numGeometries
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
if|if
condition|(
name|numGeometries
operator|<=
literal|0
condition|)
block|{
comment|// cap geometry collection at 4 shapes (to save test time)
name|numGeometries
operator|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nearPoint
operator|==
literal|null
condition|)
block|{
name|nearPoint
operator|=
name|xRandomPoint
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|==
literal|null
condition|)
block|{
name|bounds
operator|=
name|xRandomRectangle
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|)
expr_stmt|;
block|}
name|GeometryCollectionBuilder
name|gcb
init|=
operator|new
name|GeometryCollectionBuilder
argument_list|()
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
name|numGeometries
condition|;
control|)
block|{
name|ShapeBuilder
name|builder
init|=
name|createShapeWithin
argument_list|(
name|r
argument_list|,
name|bounds
argument_list|)
decl_stmt|;
comment|// due to world wrapping, and the possibility for ambiguous polygons, the random shape generation could bail with
comment|// a null shape. We catch that situation here, and only increment the counter when a valid shape is returned.
comment|// Not the most efficient but its the lesser of the evil alternatives
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
name|gcb
operator|.
name|shape
argument_list|(
name|builder
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
block|}
return|return
name|gcb
return|;
block|}
DECL|method|createShape
specifier|private
specifier|static
name|ShapeBuilder
name|createShape
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|,
name|Rectangle
name|within
parameter_list|,
name|ShapeType
name|st
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
name|ShapeBuilder
name|shape
decl_stmt|;
name|short
name|i
init|=
literal|0
decl_stmt|;
do|do
block|{
name|shape
operator|=
name|createShape
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
name|within
argument_list|,
name|st
argument_list|,
name|ST_VALIDATE
argument_list|)
expr_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
return|return
name|shape
return|;
block|}
block|}
do|while
condition|(
operator|++
name|i
operator|!=
literal|100
condition|)
do|;
throw|throw
operator|new
name|InvalidShapeException
argument_list|(
literal|"Unable to create a valid random shape with provided seed"
argument_list|)
throw|;
block|}
comment|/**      * Creates a random shape useful for randomized testing, NOTE: exercise caution when using this to build random GeometryCollections      * as creating a large random number of random shapes can result in massive resource consumption      * see: {@link GeoShapeQueryTests#testShapeFilterWithRandomGeoCollection}      *      * The following options are included      * @param nearPoint Create a shape near a provided point      * @param within Create a shape within the provided rectangle (note: if not null this will override the provided point)      * @param st Create a random shape of the provided type      * @return the ShapeBuilder for a random shape      */
DECL|method|createShape
specifier|private
specifier|static
name|ShapeBuilder
name|createShape
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearPoint
parameter_list|,
name|Rectangle
name|within
parameter_list|,
name|ShapeType
name|st
parameter_list|,
name|boolean
name|validate
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
if|if
condition|(
name|st
operator|==
literal|null
condition|)
block|{
name|st
operator|=
name|ShapeType
operator|.
name|randomType
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|within
operator|==
literal|null
condition|)
block|{
name|within
operator|=
name|xRandomRectangle
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: multipolygon not yet supported. Overlapping polygons are invalid so randomization
comment|// requires an approach to avoid overlaps. This could be approached by creating polygons
comment|// inside non overlapping bounding rectangles
switch|switch
condition|(
name|st
condition|)
block|{
case|case
name|POINT
case|:
name|Point
name|p
init|=
name|xRandomPointIn
argument_list|(
name|r
argument_list|,
name|within
argument_list|)
decl_stmt|;
name|PointBuilder
name|pb
init|=
operator|new
name|PointBuilder
argument_list|()
operator|.
name|coordinate
argument_list|(
operator|new
name|Coordinate
argument_list|(
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|pb
return|;
case|case
name|MULTIPOINT
case|:
case|case
name|LINESTRING
case|:
comment|// for random testing having a maximum number of 10 points for a line string is more than sufficient
comment|// if this number gets out of hand, the number of self intersections for a linestring can become
comment|// (n^2-n)/2 and computing the relation intersection matrix will become NP-Hard
name|int
name|numPoints
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|r
argument_list|,
literal|3
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|CoordinatesBuilder
name|coordinatesBuilder
init|=
operator|new
name|CoordinatesBuilder
argument_list|()
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
name|numPoints
condition|;
operator|++
name|i
control|)
block|{
name|p
operator|=
name|xRandomPointIn
argument_list|(
name|r
argument_list|,
name|within
argument_list|)
expr_stmt|;
name|coordinatesBuilder
operator|.
name|coordinate
argument_list|(
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CoordinateCollection
name|pcb
init|=
operator|(
name|st
operator|==
name|ShapeType
operator|.
name|MULTIPOINT
operator|)
condition|?
operator|new
name|MultiPointBuilder
argument_list|(
name|coordinatesBuilder
operator|.
name|build
argument_list|()
argument_list|)
else|:
operator|new
name|LineStringBuilder
argument_list|(
name|coordinatesBuilder
argument_list|)
decl_stmt|;
return|return
name|pcb
return|;
case|case
name|MULTILINESTRING
case|:
name|MultiLineStringBuilder
name|mlsb
init|=
operator|new
name|MultiLineStringBuilder
argument_list|()
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
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
condition|;
operator|++
name|i
control|)
block|{
name|mlsb
operator|.
name|linestring
argument_list|(
operator|(
name|LineStringBuilder
operator|)
name|createShape
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
name|within
argument_list|,
name|ShapeType
operator|.
name|LINESTRING
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|mlsb
return|;
case|case
name|POLYGON
case|:
name|numPoints
operator|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|r
argument_list|,
literal|5
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|Coordinate
index|[]
name|coordinates
init|=
operator|new
name|Coordinate
index|[
name|numPoints
index|]
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
name|numPoints
condition|;
operator|++
name|i
control|)
block|{
name|p
operator|=
operator|(
name|Point
operator|)
name|createShape
argument_list|(
name|r
argument_list|,
name|nearPoint
argument_list|,
name|within
argument_list|,
name|ShapeType
operator|.
name|POINT
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|coordinates
index|[
name|i
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// random point order or random linestrings can lead to invalid self-crossing polygons,
comment|// compute the convex hull for a set of points to ensure polygon does not self cross
name|Geometry
name|shell
init|=
operator|new
name|ConvexHull
argument_list|(
name|coordinates
argument_list|,
name|ctx
operator|.
name|getGeometryFactory
argument_list|()
argument_list|)
operator|.
name|getConvexHull
argument_list|()
decl_stmt|;
name|Coordinate
index|[]
name|shellCoords
init|=
name|shell
operator|.
name|getCoordinates
argument_list|()
decl_stmt|;
comment|// if points are in a line the convex hull will be 2 points which will also lead to an invalid polygon
comment|// when all else fails, use the bounding box as the polygon
if|if
condition|(
name|shellCoords
operator|.
name|length
operator|<
literal|3
condition|)
block|{
name|shellCoords
operator|=
operator|new
name|Coordinate
index|[
literal|4
index|]
expr_stmt|;
name|shellCoords
index|[
literal|0
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|within
operator|.
name|getMinX
argument_list|()
argument_list|,
name|within
operator|.
name|getMinY
argument_list|()
argument_list|)
expr_stmt|;
name|shellCoords
index|[
literal|1
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|within
operator|.
name|getMinX
argument_list|()
argument_list|,
name|within
operator|.
name|getMaxY
argument_list|()
argument_list|)
expr_stmt|;
name|shellCoords
index|[
literal|2
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|within
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|within
operator|.
name|getMaxY
argument_list|()
argument_list|)
expr_stmt|;
name|shellCoords
index|[
literal|3
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|within
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|within
operator|.
name|getMinY
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PolygonBuilder
name|pgb
init|=
operator|new
name|PolygonBuilder
argument_list|(
operator|new
name|CoordinatesBuilder
argument_list|()
operator|.
name|coordinates
argument_list|(
name|shellCoords
argument_list|)
operator|.
name|close
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|validate
condition|)
block|{
comment|// This test framework builds semi-random geometry (in the sense that points are not truly random due to spatial
comment|// auto-correlation) As a result of the semi-random nature of the geometry, one can not predict the orientation
comment|// intent for ambiguous polygons. Therefore, an invalid oriented dateline crossing polygon could be built.
comment|// The validate flag will check for these possibilities and bail if an incorrect geometry is created
try|try
block|{
name|pgb
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
decl||
name|InvalidShapeException
name|e
parameter_list|)
block|{
comment|// jts bug may occasionally misinterpret coordinate order causing an unhelpful ('geom' assertion)
comment|// or InvalidShapeException
return|return
literal|null
return|;
block|}
block|}
return|return
name|pgb
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unable to create shape of type ["
operator|+
name|st
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|xRandomPoint
specifier|public
specifier|static
name|Point
name|xRandomPoint
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
return|return
name|xRandomPointIn
argument_list|(
name|r
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
return|;
block|}
DECL|method|xRandomPointIn
specifier|protected
specifier|static
name|Point
name|xRandomPointIn
parameter_list|(
name|Random
name|rand
parameter_list|,
name|Rectangle
name|r
parameter_list|)
block|{
name|double
index|[]
name|pt
init|=
operator|new
name|double
index|[
literal|2
index|]
decl_stmt|;
name|randomPointIn
argument_list|(
name|rand
argument_list|,
name|r
operator|.
name|getMinX
argument_list|()
argument_list|,
name|r
operator|.
name|getMinY
argument_list|()
argument_list|,
name|r
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|r
operator|.
name|getMaxY
argument_list|()
argument_list|,
name|pt
argument_list|)
expr_stmt|;
name|Point
name|p
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
name|pt
index|[
literal|0
index|]
argument_list|,
name|pt
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|r
operator|.
name|relate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|xRandomRectangle
specifier|private
specifier|static
name|Rectangle
name|xRandomRectangle
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearP
parameter_list|,
name|Rectangle
name|bounds
parameter_list|,
name|boolean
name|small
parameter_list|)
block|{
if|if
condition|(
name|nearP
operator|==
literal|null
condition|)
name|nearP
operator|=
name|xRandomPointIn
argument_list|(
name|r
argument_list|,
name|bounds
argument_list|)
expr_stmt|;
if|if
condition|(
name|small
condition|)
block|{
comment|// between 3 and 6 degrees
specifier|final
name|double
name|latRange
init|=
literal|3
operator|*
name|r
operator|.
name|nextDouble
argument_list|()
operator|+
literal|3
decl_stmt|;
specifier|final
name|double
name|lonRange
init|=
literal|3
operator|*
name|r
operator|.
name|nextDouble
argument_list|()
operator|+
literal|3
decl_stmt|;
name|double
name|minX
init|=
name|nearP
operator|.
name|getX
argument_list|()
decl_stmt|;
name|double
name|maxX
init|=
name|minX
operator|+
name|lonRange
decl_stmt|;
if|if
condition|(
name|maxX
operator|>
literal|180
condition|)
block|{
name|maxX
operator|=
name|minX
expr_stmt|;
name|minX
operator|-=
name|lonRange
expr_stmt|;
block|}
name|double
name|minY
init|=
name|nearP
operator|.
name|getY
argument_list|()
decl_stmt|;
name|double
name|maxY
init|=
name|nearP
operator|.
name|getY
argument_list|()
operator|+
name|latRange
decl_stmt|;
if|if
condition|(
name|maxY
operator|>
literal|90
condition|)
block|{
name|maxY
operator|=
name|minY
expr_stmt|;
name|minY
operator|-=
name|latRange
expr_stmt|;
block|}
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|minX
argument_list|,
name|maxX
argument_list|,
name|minY
argument_list|,
name|maxY
argument_list|)
return|;
block|}
name|Range
name|xRange
init|=
name|xRandomRange
argument_list|(
name|r
argument_list|,
name|rarely
argument_list|(
name|r
argument_list|)
condition|?
literal|0
else|:
name|nearP
operator|.
name|getX
argument_list|()
argument_list|,
name|Range
operator|.
name|xRange
argument_list|(
name|bounds
argument_list|,
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
name|Range
name|yRange
init|=
name|xRandomRange
argument_list|(
name|r
argument_list|,
name|rarely
argument_list|(
name|r
argument_list|)
condition|?
literal|0
else|:
name|nearP
operator|.
name|getY
argument_list|()
argument_list|,
name|Range
operator|.
name|yRange
argument_list|(
name|bounds
argument_list|,
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|xMakeNormRect
argument_list|(
name|xDivisible
argument_list|(
name|xRange
operator|.
name|getMin
argument_list|()
operator|*
literal|10e3
argument_list|)
operator|/
literal|10e3
argument_list|,
name|xDivisible
argument_list|(
name|xRange
operator|.
name|getMax
argument_list|()
operator|*
literal|10e3
argument_list|)
operator|/
literal|10e3
argument_list|,
name|xDivisible
argument_list|(
name|yRange
operator|.
name|getMin
argument_list|()
operator|*
literal|10e3
argument_list|)
operator|/
literal|10e3
argument_list|,
name|xDivisible
argument_list|(
name|yRange
operator|.
name|getMax
argument_list|()
operator|*
literal|10e3
argument_list|)
operator|/
literal|10e3
argument_list|)
return|;
block|}
comment|/** creates a small random rectangle by default to keep shape test performance at bay */
DECL|method|xRandomRectangle
specifier|public
specifier|static
name|Rectangle
name|xRandomRectangle
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearP
parameter_list|)
block|{
return|return
name|xRandomRectangle
argument_list|(
name|r
argument_list|,
name|nearP
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|xRandomRectangle
specifier|public
specifier|static
name|Rectangle
name|xRandomRectangle
parameter_list|(
name|Random
name|r
parameter_list|,
name|Point
name|nearP
parameter_list|,
name|boolean
name|small
parameter_list|)
block|{
return|return
name|xRandomRectangle
argument_list|(
name|r
argument_list|,
name|nearP
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|,
name|small
argument_list|)
return|;
block|}
DECL|method|rarely
specifier|private
specifier|static
name|boolean
name|rarely
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
return|return
name|RandomInts
operator|.
name|randomInt
argument_list|(
name|r
argument_list|,
literal|100
argument_list|)
operator|>=
literal|90
return|;
block|}
DECL|method|xRandomRange
specifier|private
specifier|static
name|Range
name|xRandomRange
parameter_list|(
name|Random
name|r
parameter_list|,
name|double
name|near
parameter_list|,
name|Range
name|bounds
parameter_list|)
block|{
name|double
name|mid
init|=
name|near
operator|+
name|r
operator|.
name|nextGaussian
argument_list|()
operator|*
name|bounds
operator|.
name|getWidth
argument_list|()
operator|/
literal|6
decl_stmt|;
name|double
name|width
init|=
name|Math
operator|.
name|abs
argument_list|(
name|r
operator|.
name|nextGaussian
argument_list|()
argument_list|)
operator|*
name|bounds
operator|.
name|getWidth
argument_list|()
operator|/
literal|6
decl_stmt|;
comment|//1/3rd
return|return
operator|new
name|Range
argument_list|(
name|mid
operator|-
name|width
operator|/
literal|2
argument_list|,
name|mid
operator|+
name|width
operator|/
literal|2
argument_list|)
return|;
block|}
DECL|method|xDivisible
specifier|private
specifier|static
name|double
name|xDivisible
parameter_list|(
name|double
name|v
parameter_list|,
name|double
name|divisible
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|v
operator|/
name|divisible
argument_list|)
operator|*
name|divisible
argument_list|)
return|;
block|}
DECL|method|xDivisible
specifier|private
specifier|static
name|double
name|xDivisible
parameter_list|(
name|double
name|v
parameter_list|)
block|{
return|return
name|xDivisible
argument_list|(
name|v
argument_list|,
name|xDIVISIBLE
argument_list|)
return|;
block|}
DECL|method|xMakeNormRect
specifier|protected
specifier|static
name|Rectangle
name|xMakeNormRect
parameter_list|(
name|double
name|minX
parameter_list|,
name|double
name|maxX
parameter_list|,
name|double
name|minY
parameter_list|,
name|double
name|maxY
parameter_list|)
block|{
name|minX
operator|=
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|minX
argument_list|)
expr_stmt|;
name|maxX
operator|=
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|maxX
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxX
operator|<
name|minX
condition|)
block|{
name|double
name|t
init|=
name|minX
decl_stmt|;
name|minX
operator|=
name|maxX
expr_stmt|;
name|maxX
operator|=
name|t
expr_stmt|;
block|}
name|double
name|minWorldY
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getMinY
argument_list|()
decl_stmt|;
name|double
name|maxWorldY
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getMaxY
argument_list|()
decl_stmt|;
if|if
condition|(
name|minY
argument_list|<
name|minWorldY
operator|||
name|minY
argument_list|>
name|maxWorldY
condition|)
block|{
name|minY
operator|=
name|DistanceUtils
operator|.
name|normLatDEG
argument_list|(
name|minY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxY
argument_list|<
name|minWorldY
operator|||
name|maxY
argument_list|>
name|maxWorldY
condition|)
block|{
name|maxY
operator|=
name|DistanceUtils
operator|.
name|normLatDEG
argument_list|(
name|maxY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxY
operator|<
name|minY
condition|)
block|{
name|double
name|t
init|=
name|minY
decl_stmt|;
name|minY
operator|=
name|maxY
expr_stmt|;
name|maxY
operator|=
name|t
expr_stmt|;
block|}
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|minX
argument_list|,
name|maxX
argument_list|,
name|minY
argument_list|,
name|maxY
argument_list|)
return|;
block|}
block|}
end_class

end_unit

