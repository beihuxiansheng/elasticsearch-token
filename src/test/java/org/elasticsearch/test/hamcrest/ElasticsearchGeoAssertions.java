begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.hamcrest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
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
name|RandomizedTest
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
name|ShapeCollection
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
name|jts
operator|.
name|JtsGeometry
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
name|jts
operator|.
name|JtsPoint
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
name|*
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
name|hamcrest
operator|.
name|Matcher
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
name|Collections
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
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|ElasticsearchGeoAssertions
specifier|public
class|class
name|ElasticsearchGeoAssertions
block|{
DECL|method|top
specifier|private
specifier|static
name|int
name|top
parameter_list|(
name|Coordinate
modifier|...
name|points
parameter_list|)
block|{
name|int
name|top
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|points
index|[
name|i
index|]
operator|.
name|y
operator|<
name|points
index|[
name|top
index|]
operator|.
name|y
condition|)
block|{
name|top
operator|=
name|i
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|points
index|[
name|i
index|]
operator|.
name|y
operator|==
name|points
index|[
name|top
index|]
operator|.
name|y
condition|)
block|{
if|if
condition|(
name|points
index|[
name|i
index|]
operator|.
name|x
operator|<=
name|points
index|[
name|top
index|]
operator|.
name|x
condition|)
block|{
name|top
operator|=
name|i
expr_stmt|;
block|}
block|}
block|}
return|return
name|top
return|;
block|}
DECL|method|prev
specifier|private
specifier|static
name|int
name|prev
parameter_list|(
name|int
name|top
parameter_list|,
name|Coordinate
modifier|...
name|points
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|p
init|=
operator|(
name|top
operator|+
name|points
operator|.
name|length
operator|-
name|i
operator|)
operator|%
name|points
operator|.
name|length
decl_stmt|;
if|if
condition|(
operator|(
name|points
index|[
name|p
index|]
operator|.
name|x
operator|!=
name|points
index|[
name|top
index|]
operator|.
name|x
operator|)
operator|||
operator|(
name|points
index|[
name|p
index|]
operator|.
name|y
operator|!=
name|points
index|[
name|top
index|]
operator|.
name|y
operator|)
condition|)
block|{
return|return
name|p
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|next
specifier|private
specifier|static
name|int
name|next
parameter_list|(
name|int
name|top
parameter_list|,
name|Coordinate
modifier|...
name|points
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|n
init|=
operator|(
name|top
operator|+
name|i
operator|)
operator|%
name|points
operator|.
name|length
decl_stmt|;
if|if
condition|(
operator|(
name|points
index|[
name|n
index|]
operator|.
name|x
operator|!=
name|points
index|[
name|top
index|]
operator|.
name|x
operator|)
operator|||
operator|(
name|points
index|[
name|n
index|]
operator|.
name|y
operator|!=
name|points
index|[
name|top
index|]
operator|.
name|y
operator|)
condition|)
block|{
return|return
name|n
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|fixedOrderedRing
specifier|private
specifier|static
name|Coordinate
index|[]
name|fixedOrderedRing
parameter_list|(
name|List
argument_list|<
name|Coordinate
argument_list|>
name|coordinates
parameter_list|,
name|boolean
name|direction
parameter_list|)
block|{
return|return
name|fixedOrderedRing
argument_list|(
name|coordinates
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|coordinates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|direction
argument_list|)
return|;
block|}
DECL|method|fixedOrderedRing
specifier|private
specifier|static
name|Coordinate
index|[]
name|fixedOrderedRing
parameter_list|(
name|Coordinate
index|[]
name|points
parameter_list|,
name|boolean
name|direction
parameter_list|)
block|{
specifier|final
name|int
name|top
init|=
name|top
argument_list|(
name|points
argument_list|)
decl_stmt|;
specifier|final
name|int
name|next
init|=
name|next
argument_list|(
name|top
argument_list|,
name|points
argument_list|)
decl_stmt|;
specifier|final
name|int
name|prev
init|=
name|prev
argument_list|(
name|top
argument_list|,
name|points
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|orientation
init|=
name|points
index|[
name|next
index|]
operator|.
name|x
operator|<
name|points
index|[
name|prev
index|]
operator|.
name|x
decl_stmt|;
if|if
condition|(
name|orientation
operator|!=
name|direction
condition|)
block|{
name|List
argument_list|<
name|Coordinate
argument_list|>
name|asList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|points
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|asList
argument_list|)
expr_stmt|;
return|return
name|fixedOrderedRing
argument_list|(
name|asList
argument_list|,
name|direction
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|top
operator|>
literal|0
condition|)
block|{
name|Coordinate
index|[]
name|aligned
init|=
operator|new
name|Coordinate
index|[
name|points
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|points
argument_list|,
name|top
argument_list|,
name|aligned
argument_list|,
literal|0
argument_list|,
name|points
operator|.
name|length
operator|-
name|top
operator|-
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|points
argument_list|,
literal|0
argument_list|,
name|aligned
argument_list|,
name|points
operator|.
name|length
operator|-
name|top
operator|-
literal|1
argument_list|,
name|top
argument_list|)
expr_stmt|;
name|aligned
index|[
name|aligned
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|aligned
index|[
literal|0
index|]
expr_stmt|;
return|return
name|aligned
return|;
block|}
else|else
block|{
return|return
name|points
return|;
block|}
block|}
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|Coordinate
name|c1
parameter_list|,
name|Coordinate
name|c2
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"expected coordinate "
operator|+
name|c1
operator|+
literal|" but found "
operator|+
name|c2
argument_list|,
name|c1
operator|.
name|x
operator|==
name|c2
operator|.
name|x
operator|&&
name|c1
operator|.
name|y
operator|==
name|c2
operator|.
name|y
argument_list|)
expr_stmt|;
block|}
DECL|method|isRing
specifier|private
specifier|static
name|boolean
name|isRing
parameter_list|(
name|Coordinate
index|[]
name|c
parameter_list|)
block|{
return|return
operator|(
name|c
index|[
literal|0
index|]
operator|.
name|x
operator|==
name|c
index|[
name|c
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|x
operator|)
operator|&&
operator|(
name|c
index|[
literal|0
index|]
operator|.
name|y
operator|==
name|c
index|[
name|c
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|y
operator|)
return|;
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|Coordinate
index|[]
name|c1
parameter_list|,
name|Coordinate
index|[]
name|c2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|c1
operator|.
name|length
argument_list|,
name|c2
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRing
argument_list|(
name|c1
argument_list|)
operator|&&
name|isRing
argument_list|(
name|c2
argument_list|)
condition|)
block|{
name|c1
operator|=
name|fixedOrderedRing
argument_list|(
name|c1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|c2
operator|=
name|fixedOrderedRing
argument_list|(
name|c2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|c2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|c1
index|[
name|i
index|]
argument_list|,
name|c2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|LineString
name|l1
parameter_list|,
name|LineString
name|l2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|l1
operator|.
name|getCoordinates
argument_list|()
argument_list|,
name|l2
operator|.
name|getCoordinates
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|Polygon
name|p1
parameter_list|,
name|Polygon
name|p2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|p1
operator|.
name|getNumInteriorRing
argument_list|()
argument_list|,
name|p2
operator|.
name|getNumInteriorRing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p1
operator|.
name|getExteriorRing
argument_list|()
argument_list|,
name|p2
operator|.
name|getExteriorRing
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: This test do not check all permutations of linestrings. So the test
comment|// fails if the holes of the polygons are not ordered the same way
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p1
operator|.
name|getNumInteriorRing
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|p1
operator|.
name|getInteriorRingN
argument_list|(
name|i
argument_list|)
argument_list|,
name|p2
operator|.
name|getInteriorRingN
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|MultiPolygon
name|p1
parameter_list|,
name|MultiPolygon
name|p2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|p1
operator|.
name|getNumGeometries
argument_list|()
argument_list|,
name|p2
operator|.
name|getNumGeometries
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: This test do not check all permutations. So the Test fails
comment|// if the inner polygons are not ordered the same way in both Multipolygons
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p1
operator|.
name|getNumGeometries
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Geometry
name|a
init|=
name|p1
operator|.
name|getGeometryN
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Geometry
name|b
init|=
name|p2
operator|.
name|getGeometryN
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|Geometry
name|s1
parameter_list|,
name|Geometry
name|s2
parameter_list|)
block|{
if|if
condition|(
name|s1
operator|instanceof
name|LineString
operator|&&
name|s2
operator|instanceof
name|LineString
condition|)
block|{
name|assertEquals
argument_list|(
operator|(
name|LineString
operator|)
name|s1
argument_list|,
operator|(
name|LineString
operator|)
name|s2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s1
operator|instanceof
name|Polygon
operator|&&
name|s2
operator|instanceof
name|Polygon
condition|)
block|{
name|assertEquals
argument_list|(
operator|(
name|Polygon
operator|)
name|s1
argument_list|,
operator|(
name|Polygon
operator|)
name|s2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s1
operator|instanceof
name|MultiPoint
operator|&&
name|s2
operator|instanceof
name|MultiPoint
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s1
operator|instanceof
name|MultiPolygon
operator|&&
name|s2
operator|instanceof
name|MultiPolygon
condition|)
block|{
name|assertEquals
argument_list|(
operator|(
name|MultiPolygon
operator|)
name|s1
argument_list|,
operator|(
name|MultiPolygon
operator|)
name|s2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"equality of shape types not supported ["
operator|+
name|s1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" and "
operator|+
name|s2
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|JtsGeometry
name|g1
parameter_list|,
name|JtsGeometry
name|g2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|g1
operator|.
name|getGeom
argument_list|()
argument_list|,
name|g2
operator|.
name|getGeom
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|ShapeCollection
name|s1
parameter_list|,
name|ShapeCollection
name|s2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
operator|.
name|size
argument_list|()
argument_list|,
name|s2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s1
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|s1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|s2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEquals
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|Shape
name|s1
parameter_list|,
name|Shape
name|s2
parameter_list|)
block|{
if|if
condition|(
name|s1
operator|instanceof
name|JtsGeometry
operator|&&
name|s2
operator|instanceof
name|JtsGeometry
condition|)
block|{
name|assertEquals
argument_list|(
operator|(
name|JtsGeometry
operator|)
name|s1
argument_list|,
operator|(
name|JtsGeometry
operator|)
name|s2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s1
operator|instanceof
name|JtsPoint
operator|&&
name|s2
operator|instanceof
name|JtsPoint
condition|)
block|{
name|JtsPoint
name|p1
init|=
operator|(
name|JtsPoint
operator|)
name|s1
decl_stmt|;
name|JtsPoint
name|p2
init|=
operator|(
name|JtsPoint
operator|)
name|s2
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s1
operator|instanceof
name|ShapeCollection
operator|&&
name|s2
operator|instanceof
name|ShapeCollection
condition|)
block|{
name|assertEquals
argument_list|(
operator|(
name|ShapeCollection
operator|)
name|s1
argument_list|,
operator|(
name|ShapeCollection
operator|)
name|s2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//We want to know the type of the shape because we test shape equality in a special way...
comment|//... in particular we test that one ring is equivalent to another ring even if the points are rotated or reversed.
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"equality of shape types not supported ["
operator|+
name|s1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" and "
operator|+
name|s2
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|unwrap
specifier|private
specifier|static
name|Geometry
name|unwrap
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
name|assertThat
argument_list|(
name|shape
argument_list|,
name|instanceOf
argument_list|(
name|JtsGeometry
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|JtsGeometry
operator|)
name|shape
operator|)
operator|.
name|getGeom
argument_list|()
return|;
block|}
DECL|method|assertMultiPolygon
specifier|public
specifier|static
name|void
name|assertMultiPolygon
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
assert|assert
operator|(
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|instanceof
name|MultiPolygon
operator|)
operator|:
literal|"expected MultiPolygon but found "
operator|+
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
block|}
DECL|method|assertPolygon
specifier|public
specifier|static
name|void
name|assertPolygon
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
assert|assert
operator|(
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|instanceof
name|Polygon
operator|)
operator|:
literal|"expected Polygon but found "
operator|+
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
block|}
DECL|method|assertLineString
specifier|public
specifier|static
name|void
name|assertLineString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
assert|assert
operator|(
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|instanceof
name|LineString
operator|)
operator|:
literal|"expected LineString but found "
operator|+
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
block|}
DECL|method|assertMultiLineString
specifier|public
specifier|static
name|void
name|assertMultiLineString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
assert|assert
operator|(
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|instanceof
name|MultiLineString
operator|)
operator|:
literal|"expected MultiLineString but found "
operator|+
name|unwrap
argument_list|(
name|shape
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
block|}
DECL|method|assertDistance
specifier|public
specifier|static
name|void
name|assertDistance
parameter_list|(
name|String
name|geohash1
parameter_list|,
name|String
name|geohash2
parameter_list|,
name|Matcher
argument_list|<
name|Double
argument_list|>
name|match
parameter_list|)
block|{
name|GeoPoint
name|p1
init|=
operator|new
name|GeoPoint
argument_list|(
name|geohash1
argument_list|)
decl_stmt|;
name|GeoPoint
name|p2
init|=
operator|new
name|GeoPoint
argument_list|(
name|geohash2
argument_list|)
decl_stmt|;
name|assertDistance
argument_list|(
name|p1
operator|.
name|lat
argument_list|()
argument_list|,
name|p1
operator|.
name|lon
argument_list|()
argument_list|,
name|p2
operator|.
name|lat
argument_list|()
argument_list|,
name|p2
operator|.
name|lon
argument_list|()
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDistance
specifier|public
specifier|static
name|void
name|assertDistance
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
parameter_list|,
name|Matcher
argument_list|<
name|Double
argument_list|>
name|match
parameter_list|)
block|{
name|assertThat
argument_list|(
name|distance
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
DECL|method|distance
specifier|private
specifier|static
name|double
name|distance
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
return|return
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
name|DEFAULT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

