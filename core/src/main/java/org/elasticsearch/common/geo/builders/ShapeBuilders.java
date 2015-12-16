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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A collection of static methods for creating ShapeBuilders.  */
end_comment

begin_class
DECL|class|ShapeBuilders
specifier|public
class|class
name|ShapeBuilders
block|{
comment|/**      * Create a new point      *      * @param longitude longitude of the point      * @param latitude latitude of the point      * @return a new {@link PointBuilder}      */
DECL|method|newPoint
specifier|public
specifier|static
name|PointBuilder
name|newPoint
parameter_list|(
name|double
name|longitude
parameter_list|,
name|double
name|latitude
parameter_list|)
block|{
return|return
name|ShapeBuilders
operator|.
name|newPoint
argument_list|(
operator|new
name|Coordinate
argument_list|(
name|longitude
argument_list|,
name|latitude
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link PointBuilder} from a {@link Coordinate}      * @param coordinate coordinate defining the position of the point      * @return a new {@link PointBuilder}      */
DECL|method|newPoint
specifier|public
specifier|static
name|PointBuilder
name|newPoint
parameter_list|(
name|Coordinate
name|coordinate
parameter_list|)
block|{
return|return
operator|new
name|PointBuilder
argument_list|()
operator|.
name|coordinate
argument_list|(
name|coordinate
argument_list|)
return|;
block|}
comment|/**      * Create a new set of points      * @return new {@link MultiPointBuilder}      */
DECL|method|newMultiPoint
specifier|public
specifier|static
name|MultiPointBuilder
name|newMultiPoint
parameter_list|(
name|List
argument_list|<
name|Coordinate
argument_list|>
name|points
parameter_list|)
block|{
return|return
operator|new
name|MultiPointBuilder
argument_list|(
name|points
argument_list|)
return|;
block|}
comment|/**      * Create a new lineString      * @return a new {@link LineStringBuilder}      */
DECL|method|newLineString
specifier|public
specifier|static
name|LineStringBuilder
name|newLineString
parameter_list|(
name|List
argument_list|<
name|Coordinate
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|LineStringBuilder
argument_list|(
name|list
argument_list|)
return|;
block|}
comment|/**      * Create a new Collection of lineStrings      * @return a new {@link MultiLineStringBuilder}      */
DECL|method|newMultiLinestring
specifier|public
specifier|static
name|MultiLineStringBuilder
name|newMultiLinestring
parameter_list|()
block|{
return|return
operator|new
name|MultiLineStringBuilder
argument_list|()
return|;
block|}
comment|/**      * Create a new Polygon      * @return a new {@link PointBuilder}      */
DECL|method|newPolygon
specifier|public
specifier|static
name|PolygonBuilder
name|newPolygon
parameter_list|(
name|List
argument_list|<
name|Coordinate
argument_list|>
name|shell
parameter_list|)
block|{
return|return
operator|new
name|PolygonBuilder
argument_list|(
name|shell
argument_list|)
return|;
block|}
comment|/**      * Create a new Collection of polygons      * @return a new {@link MultiPolygonBuilder}      */
DECL|method|newMultiPolygon
specifier|public
specifier|static
name|MultiPolygonBuilder
name|newMultiPolygon
parameter_list|()
block|{
return|return
operator|new
name|MultiPolygonBuilder
argument_list|()
return|;
block|}
comment|/**      * Create a new Collection of polygons      * @return a new {@link MultiPolygonBuilder}      */
DECL|method|newMultiPolygon
specifier|public
specifier|static
name|MultiPolygonBuilder
name|newMultiPolygon
parameter_list|(
name|ShapeBuilder
operator|.
name|Orientation
name|orientation
parameter_list|)
block|{
return|return
operator|new
name|MultiPolygonBuilder
argument_list|(
name|orientation
argument_list|)
return|;
block|}
comment|/**      * Create a new GeometryCollection      * @return a new {@link GeometryCollectionBuilder}      */
DECL|method|newGeometryCollection
specifier|public
specifier|static
name|GeometryCollectionBuilder
name|newGeometryCollection
parameter_list|()
block|{
return|return
operator|new
name|GeometryCollectionBuilder
argument_list|()
return|;
block|}
comment|/**      * create a new Circle      *      * @return a new {@link CircleBuilder}      */
DECL|method|newCircleBuilder
specifier|public
specifier|static
name|CircleBuilder
name|newCircleBuilder
parameter_list|()
block|{
return|return
operator|new
name|CircleBuilder
argument_list|()
return|;
block|}
comment|/**      * create a new rectangle      *      * @return a new {@link EnvelopeBuilder}      */
DECL|method|newEnvelope
specifier|public
specifier|static
name|EnvelopeBuilder
name|newEnvelope
parameter_list|(
name|Coordinate
name|topLeft
parameter_list|,
name|Coordinate
name|bottomRight
parameter_list|)
block|{
return|return
operator|new
name|EnvelopeBuilder
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|)
return|;
block|}
block|}
end_class

end_unit

