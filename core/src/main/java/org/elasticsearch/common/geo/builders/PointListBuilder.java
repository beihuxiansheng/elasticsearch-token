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
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  * A builder for a list of points (of {@link Coordinate} type).  * Enables chaining of individual points either as long/lat pairs  * or as {@link Coordinate} elements, arrays or collections.  */
end_comment

begin_class
DECL|class|PointListBuilder
specifier|public
class|class
name|PointListBuilder
block|{
DECL|field|points
specifier|private
specifier|final
name|List
argument_list|<
name|Coordinate
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Add a new point to the collection      * @param longitude longitude of the coordinate      * @param latitude latitude of the coordinate      * @return this      */
DECL|method|point
specifier|public
name|PointListBuilder
name|point
parameter_list|(
name|double
name|longitude
parameter_list|,
name|double
name|latitude
parameter_list|)
block|{
return|return
name|this
operator|.
name|point
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
comment|/**      * Add a new point to the collection      * @param coordinate coordinate of the point      * @return this      */
DECL|method|point
specifier|public
name|PointListBuilder
name|point
parameter_list|(
name|Coordinate
name|coordinate
parameter_list|)
block|{
name|this
operator|.
name|points
operator|.
name|add
argument_list|(
name|coordinate
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a array of points to the collection      *      * @param coordinates array of {@link Coordinate}s to add      * @return this      */
DECL|method|points
specifier|public
name|PointListBuilder
name|points
parameter_list|(
name|Coordinate
modifier|...
name|coordinates
parameter_list|)
block|{
return|return
name|this
operator|.
name|points
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|coordinates
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Add a collection of points to the collection      *      * @param coordinates array of {@link Coordinate}s to add      * @return this      */
DECL|method|points
specifier|public
name|PointListBuilder
name|points
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Coordinate
argument_list|>
name|coordinates
parameter_list|)
block|{
name|this
operator|.
name|points
operator|.
name|addAll
argument_list|(
name|coordinates
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Closes the current list of points by adding the starting point as the end point      * if they are not already the same      */
DECL|method|close
specifier|public
name|PointListBuilder
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
comment|/**      * @return the current list of points      */
DECL|method|list
specifier|public
name|List
argument_list|<
name|Coordinate
argument_list|>
name|list
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|points
argument_list|)
return|;
block|}
block|}
end_class

end_unit

