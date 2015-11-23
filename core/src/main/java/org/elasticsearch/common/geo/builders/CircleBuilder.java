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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|common
operator|.
name|unit
operator|.
name|DistanceUnit
operator|.
name|Distance
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
name|Objects
import|;
end_import

begin_class
DECL|class|CircleBuilder
specifier|public
class|class
name|CircleBuilder
extends|extends
name|ShapeBuilder
block|{
DECL|field|FIELD_RADIUS
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_RADIUS
init|=
literal|"radius"
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|GeoShapeType
name|TYPE
init|=
name|GeoShapeType
operator|.
name|CIRCLE
decl_stmt|;
DECL|field|PROTOTYPE
specifier|public
specifier|static
specifier|final
name|CircleBuilder
name|PROTOTYPE
init|=
operator|new
name|CircleBuilder
argument_list|()
decl_stmt|;
DECL|field|unit
specifier|private
name|DistanceUnit
name|unit
decl_stmt|;
DECL|field|radius
specifier|private
name|double
name|radius
decl_stmt|;
DECL|field|center
specifier|private
name|Coordinate
name|center
decl_stmt|;
comment|/**      * Set the center of the circle      *      * @param center coordinate of the circles center      * @return this      */
DECL|method|center
specifier|public
name|CircleBuilder
name|center
parameter_list|(
name|Coordinate
name|center
parameter_list|)
block|{
name|this
operator|.
name|center
operator|=
name|center
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * set the center of the circle      * @param lon longitude of the center      * @param lat latitude of the center      * @return this      */
DECL|method|center
specifier|public
name|CircleBuilder
name|center
parameter_list|(
name|double
name|lon
parameter_list|,
name|double
name|lat
parameter_list|)
block|{
return|return
name|center
argument_list|(
operator|new
name|Coordinate
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Get the center of the circle      */
DECL|method|center
specifier|public
name|Coordinate
name|center
parameter_list|()
block|{
return|return
name|center
return|;
block|}
comment|/**      * Set the radius of the circle. The String value will be parsed by {@link DistanceUnit}      * @param radius Value and unit of the circle combined in a string      * @return this      */
DECL|method|radius
specifier|public
name|CircleBuilder
name|radius
parameter_list|(
name|String
name|radius
parameter_list|)
block|{
return|return
name|radius
argument_list|(
name|DistanceUnit
operator|.
name|Distance
operator|.
name|parseDistance
argument_list|(
name|radius
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Set the radius of the circle      * @param radius radius of the circle (see {@link org.elasticsearch.common.unit.DistanceUnit.Distance})      * @return this      */
DECL|method|radius
specifier|public
name|CircleBuilder
name|radius
parameter_list|(
name|Distance
name|radius
parameter_list|)
block|{
return|return
name|radius
argument_list|(
name|radius
operator|.
name|value
argument_list|,
name|radius
operator|.
name|unit
argument_list|)
return|;
block|}
comment|/**      * Set the radius of the circle      * @param radius value of the circles radius      * @param unit unit name of the radius value (see {@link DistanceUnit})      * @return this      */
DECL|method|radius
specifier|public
name|CircleBuilder
name|radius
parameter_list|(
name|double
name|radius
parameter_list|,
name|String
name|unit
parameter_list|)
block|{
return|return
name|radius
argument_list|(
name|radius
argument_list|,
name|DistanceUnit
operator|.
name|fromString
argument_list|(
name|unit
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Set the radius of the circle      * @param radius value of the circles radius      * @param unit unit of the radius value (see {@link DistanceUnit})      * @return this      */
DECL|method|radius
specifier|public
name|CircleBuilder
name|radius
parameter_list|(
name|double
name|radius
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|radius
operator|=
name|radius
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the radius of the circle without unit      */
DECL|method|radius
specifier|public
name|double
name|radius
parameter_list|()
block|{
return|return
name|this
operator|.
name|radius
return|;
block|}
comment|/**      * Get the radius unit of the circle      */
DECL|method|unit
specifier|public
name|DistanceUnit
name|unit
parameter_list|()
block|{
return|return
name|this
operator|.
name|unit
return|;
block|}
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
name|FIELD_RADIUS
argument_list|,
name|unit
operator|.
name|toString
argument_list|(
name|radius
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FIELD_COORDINATES
argument_list|)
expr_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|center
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Circle
name|build
parameter_list|()
block|{
return|return
name|SPATIAL_CONTEXT
operator|.
name|makeCircle
argument_list|(
name|center
operator|.
name|x
argument_list|,
name|center
operator|.
name|y
argument_list|,
literal|360
operator|*
name|radius
operator|/
name|unit
operator|.
name|getEarthCircumference
argument_list|()
argument_list|)
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
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|center
argument_list|,
name|radius
argument_list|,
name|unit
operator|.
name|ordinal
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CircleBuilder
name|other
init|=
operator|(
name|CircleBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|center
argument_list|,
name|other
operator|.
name|center
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|radius
argument_list|,
name|other
operator|.
name|radius
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|unit
operator|.
name|ordinal
argument_list|()
argument_list|,
name|other
operator|.
name|unit
operator|.
name|ordinal
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|writeCoordinateTo
argument_list|(
name|center
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|radius
argument_list|)
expr_stmt|;
name|DistanceUnit
operator|.
name|writeDistanceUnit
argument_list|(
name|out
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|CircleBuilder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CircleBuilder
argument_list|()
operator|.
name|center
argument_list|(
name|readCoordinateFrom
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|radius
argument_list|(
name|in
operator|.
name|readDouble
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|readDistanceUnit
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

