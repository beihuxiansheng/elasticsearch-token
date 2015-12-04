begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo.builders
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|builders
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|GeometryFactory
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

begin_class
DECL|class|LineStringBuilder
specifier|public
class|class
name|LineStringBuilder
extends|extends
name|PointCollection
argument_list|<
name|LineStringBuilder
argument_list|>
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|GeoShapeType
name|TYPE
init|=
name|GeoShapeType
operator|.
name|LINESTRING
decl_stmt|;
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FIELD_TYPE
argument_list|,
name|TYPE
operator|.
name|shapeName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FIELD_COORDINATES
argument_list|)
expr_stmt|;
name|coordinatesToXcontent
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * Closes the current lineString by adding the starting point as the end point      */
DECL|method|close
specifier|public
name|LineStringBuilder
name|close
parameter_list|()
block|{
name|Coordinate
name|start
init|=
name|points
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Coordinate
name|end
init|=
name|points
operator|.
name|get
argument_list|(
name|points
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|.
name|x
operator|!=
name|end
operator|.
name|x
operator|||
name|start
operator|.
name|y
operator|!=
name|end
operator|.
name|y
condition|)
block|{
name|points
operator|.
name|add
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|GeoShapeType
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Shape
name|build
parameter_list|()
block|{
name|Coordinate
index|[]
name|coordinates
init|=
name|points
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|points
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Geometry
name|geometry
decl_stmt|;
if|if
condition|(
name|wrapdateline
condition|)
block|{
name|ArrayList
argument_list|<
name|LineString
argument_list|>
name|strings
init|=
name|decompose
argument_list|(
name|FACTORY
argument_list|,
name|coordinates
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|LineString
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|geometry
operator|=
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LineString
index|[]
name|linestrings
init|=
name|strings
operator|.
name|toArray
argument_list|(
operator|new
name|LineString
index|[
name|strings
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|geometry
operator|=
name|FACTORY
operator|.
name|createMultiLineString
argument_list|(
name|linestrings
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|geometry
operator|=
name|FACTORY
operator|.
name|createLineString
argument_list|(
name|coordinates
argument_list|)
expr_stmt|;
block|}
return|return
name|jtsGeometry
argument_list|(
name|geometry
argument_list|)
return|;
block|}
DECL|method|decompose
specifier|static
name|ArrayList
argument_list|<
name|LineString
argument_list|>
name|decompose
parameter_list|(
name|GeometryFactory
name|factory
parameter_list|,
name|Coordinate
index|[]
name|coordinates
parameter_list|,
name|ArrayList
argument_list|<
name|LineString
argument_list|>
name|strings
parameter_list|)
block|{
for|for
control|(
name|Coordinate
index|[]
name|part
range|:
name|decompose
argument_list|(
operator|+
name|DATELINE
argument_list|,
name|coordinates
argument_list|)
control|)
block|{
for|for
control|(
name|Coordinate
index|[]
name|line
range|:
name|decompose
argument_list|(
operator|-
name|DATELINE
argument_list|,
name|part
argument_list|)
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|factory
operator|.
name|createLineString
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|strings
return|;
block|}
comment|/**      * Decompose a linestring given as array of coordinates at a vertical line.      *      * @param dateline x-axis intercept of the vertical line      * @param coordinates coordinates forming the linestring      * @return array of linestrings given as coordinate arrays      */
DECL|method|decompose
specifier|private
specifier|static
name|Coordinate
index|[]
index|[]
name|decompose
parameter_list|(
name|double
name|dateline
parameter_list|,
name|Coordinate
index|[]
name|coordinates
parameter_list|)
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|ArrayList
argument_list|<
name|Coordinate
index|[]
argument_list|>
name|parts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|double
name|shift
init|=
name|coordinates
index|[
literal|0
index|]
operator|.
name|x
operator|>
name|DATELINE
condition|?
name|DATELINE
else|:
operator|(
name|coordinates
index|[
literal|0
index|]
operator|.
name|x
operator|<
operator|-
name|DATELINE
condition|?
operator|-
name|DATELINE
else|:
literal|0
operator|)
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
name|coordinates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|t
init|=
name|intersection
argument_list|(
name|coordinates
index|[
name|i
operator|-
literal|1
index|]
argument_list|,
name|coordinates
index|[
name|i
index|]
argument_list|,
name|dateline
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|Coordinate
index|[]
name|part
decl_stmt|;
if|if
condition|(
name|t
operator|<
literal|1
condition|)
block|{
name|part
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|coordinates
argument_list|,
name|offset
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|part
index|[
name|part
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|Edge
operator|.
name|position
argument_list|(
name|coordinates
index|[
name|i
operator|-
literal|1
index|]
argument_list|,
name|coordinates
index|[
name|i
index|]
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|coordinates
index|[
name|offset
operator|+
name|i
operator|-
literal|1
index|]
operator|=
name|Edge
operator|.
name|position
argument_list|(
name|coordinates
index|[
name|i
operator|-
literal|1
index|]
argument_list|,
name|coordinates
index|[
name|i
index|]
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|shift
argument_list|(
name|shift
argument_list|,
name|part
argument_list|)
expr_stmt|;
name|offset
operator|=
name|i
operator|-
literal|1
expr_stmt|;
name|shift
operator|=
name|coordinates
index|[
name|i
index|]
operator|.
name|x
operator|>
name|DATELINE
condition|?
name|DATELINE
else|:
operator|(
name|coordinates
index|[
name|i
index|]
operator|.
name|x
operator|<
operator|-
name|DATELINE
condition|?
operator|-
name|DATELINE
else|:
literal|0
operator|)
expr_stmt|;
block|}
else|else
block|{
name|part
operator|=
name|shift
argument_list|(
name|shift
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|coordinates
argument_list|,
name|offset
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|offset
operator|=
name|i
expr_stmt|;
block|}
name|parts
operator|.
name|add
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|shift
argument_list|(
name|shift
argument_list|,
name|coordinates
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|offset
operator|<
name|coordinates
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|Coordinate
index|[]
name|part
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|coordinates
argument_list|,
name|offset
argument_list|,
name|coordinates
operator|.
name|length
argument_list|)
decl_stmt|;
name|parts
operator|.
name|add
argument_list|(
name|shift
argument_list|(
name|shift
argument_list|,
name|part
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|parts
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|parts
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
return|;
block|}
DECL|method|shift
specifier|private
specifier|static
name|Coordinate
index|[]
name|shift
parameter_list|(
name|double
name|shift
parameter_list|,
name|Coordinate
modifier|...
name|coordinates
parameter_list|)
block|{
if|if
condition|(
name|shift
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|coordinates
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|coordinates
index|[
name|j
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|coordinates
index|[
name|j
index|]
operator|.
name|x
operator|-
literal|2
operator|*
name|shift
argument_list|,
name|coordinates
index|[
name|j
index|]
operator|.
name|y
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|coordinates
return|;
block|}
block|}
end_class

end_unit

