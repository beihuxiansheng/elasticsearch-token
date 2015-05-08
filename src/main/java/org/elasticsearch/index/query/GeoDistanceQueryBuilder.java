begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|Locale
import|;
end_import

begin_class
DECL|class|GeoDistanceQueryBuilder
specifier|public
class|class
name|GeoDistanceQueryBuilder
extends|extends
name|QueryBuilder
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|distance
specifier|private
name|String
name|distance
decl_stmt|;
DECL|field|lat
specifier|private
name|double
name|lat
decl_stmt|;
DECL|field|lon
specifier|private
name|double
name|lon
decl_stmt|;
DECL|field|geohash
specifier|private
name|String
name|geohash
decl_stmt|;
DECL|field|geoDistance
specifier|private
name|GeoDistance
name|geoDistance
decl_stmt|;
DECL|field|optimizeBbox
specifier|private
name|String
name|optimizeBbox
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|method|GeoDistanceQueryBuilder
specifier|public
name|GeoDistanceQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|point
specifier|public
name|GeoDistanceQueryBuilder
name|point
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|lat
specifier|public
name|GeoDistanceQueryBuilder
name|lat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|lon
specifier|public
name|GeoDistanceQueryBuilder
name|lon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|distance
specifier|public
name|GeoDistanceQueryBuilder
name|distance
parameter_list|(
name|String
name|distance
parameter_list|)
block|{
name|this
operator|.
name|distance
operator|=
name|distance
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|distance
specifier|public
name|GeoDistanceQueryBuilder
name|distance
parameter_list|(
name|double
name|distance
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|distance
operator|=
name|unit
operator|.
name|toString
argument_list|(
name|distance
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|geohash
specifier|public
name|GeoDistanceQueryBuilder
name|geohash
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|this
operator|.
name|geohash
operator|=
name|geohash
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|geoDistance
specifier|public
name|GeoDistanceQueryBuilder
name|geoDistance
parameter_list|(
name|GeoDistance
name|geoDistance
parameter_list|)
block|{
name|this
operator|.
name|geoDistance
operator|=
name|geoDistance
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|optimizeBbox
specifier|public
name|GeoDistanceQueryBuilder
name|optimizeBbox
parameter_list|(
name|String
name|optimizeBbox
parameter_list|)
block|{
name|this
operator|.
name|optimizeBbox
operator|=
name|optimizeBbox
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the filter name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|GeoDistanceQueryBuilder
name|queryName
parameter_list|(
name|String
name|queryName
parameter_list|)
block|{
name|this
operator|.
name|queryName
operator|=
name|queryName
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
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
argument_list|(
name|GeoDistanceQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|geohash
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|geohash
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|name
argument_list|)
operator|.
name|value
argument_list|(
name|lon
argument_list|)
operator|.
name|value
argument_list|(
name|lat
argument_list|)
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"distance"
argument_list|,
name|distance
argument_list|)
expr_stmt|;
if|if
condition|(
name|geoDistance
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"distance_type"
argument_list|,
name|geoDistance
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|optimizeBbox
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"optimize_bbox"
argument_list|,
name|optimizeBbox
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|queryName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

