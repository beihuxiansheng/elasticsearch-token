begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.geobounds
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
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
name|collect
operator|.
name|Tuple
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
name|xcontent
operator|.
name|ConstructingObjectParser
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
name|ObjectParser
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|aggregations
operator|.
name|ParsedAggregation
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ConstructingObjectParser
operator|.
name|constructorArg
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
operator|.
name|InternalGeoBounds
operator|.
name|BOTTOM_RIGHT_FIELD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
operator|.
name|InternalGeoBounds
operator|.
name|BOUNDS_FIELD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
operator|.
name|InternalGeoBounds
operator|.
name|LAT_FIELD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
operator|.
name|InternalGeoBounds
operator|.
name|LON_FIELD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
operator|.
name|InternalGeoBounds
operator|.
name|TOP_LEFT_FIELD
import|;
end_import

begin_class
DECL|class|ParsedGeoBounds
specifier|public
class|class
name|ParsedGeoBounds
extends|extends
name|ParsedAggregation
implements|implements
name|GeoBounds
block|{
DECL|field|topLeft
specifier|private
name|GeoPoint
name|topLeft
decl_stmt|;
DECL|field|bottomRight
specifier|private
name|GeoPoint
name|bottomRight
decl_stmt|;
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|GeoBoundsAggregationBuilder
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
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
if|if
condition|(
name|topLeft
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|BOUNDS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|TOP_LEFT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|LAT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|topLeft
operator|.
name|getLat
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|LON_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|topLeft
operator|.
name|getLon
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|BOTTOM_RIGHT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|LAT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|bottomRight
operator|.
name|getLat
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|LON_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|bottomRight
operator|.
name|getLon
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|topLeft
specifier|public
name|GeoPoint
name|topLeft
parameter_list|()
block|{
return|return
name|topLeft
return|;
block|}
annotation|@
name|Override
DECL|method|bottomRight
specifier|public
name|GeoPoint
name|bottomRight
parameter_list|()
block|{
return|return
name|bottomRight
return|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|ParsedGeoBounds
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|ParsedGeoBounds
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|true
argument_list|,
name|ParsedGeoBounds
operator|::
operator|new
argument_list|)
decl_stmt|;
DECL|field|BOUNDS_PARSER
specifier|private
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|Tuple
argument_list|<
name|GeoPoint
argument_list|,
name|GeoPoint
argument_list|>
argument_list|,
name|Void
argument_list|>
name|BOUNDS_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
name|ParsedGeoBounds
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_BOUNDS"
argument_list|,
literal|true
argument_list|,
name|args
lambda|->
operator|new
name|Tuple
argument_list|<>
argument_list|(
operator|(
name|GeoPoint
operator|)
name|args
index|[
literal|0
index|]
argument_list|,
operator|(
name|GeoPoint
operator|)
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|GEO_POINT_PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|GeoPoint
argument_list|,
name|Void
argument_list|>
name|GEO_POINT_PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|ParsedGeoBounds
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_POINT"
argument_list|,
literal|true
argument_list|,
name|GeoPoint
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|declareAggregationFields
argument_list|(
name|PARSER
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareObject
argument_list|(
parameter_list|(
name|agg
parameter_list|,
name|bbox
parameter_list|)
lambda|->
block|{
name|agg
operator|.
name|topLeft
operator|=
name|bbox
operator|.
name|v1
argument_list|()
expr_stmt|;
name|agg
operator|.
name|bottomRight
operator|=
name|bbox
operator|.
name|v2
argument_list|()
expr_stmt|;
block|}
argument_list|,
name|BOUNDS_PARSER
argument_list|,
name|BOUNDS_FIELD
argument_list|)
expr_stmt|;
name|BOUNDS_PARSER
operator|.
name|declareObject
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
name|GEO_POINT_PARSER
argument_list|,
name|TOP_LEFT_FIELD
argument_list|)
expr_stmt|;
name|BOUNDS_PARSER
operator|.
name|declareObject
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
name|GEO_POINT_PARSER
argument_list|,
name|BOTTOM_RIGHT_FIELD
argument_list|)
expr_stmt|;
name|GEO_POINT_PARSER
operator|.
name|declareDouble
argument_list|(
name|GeoPoint
operator|::
name|resetLat
argument_list|,
name|LAT_FIELD
argument_list|)
expr_stmt|;
name|GEO_POINT_PARSER
operator|.
name|declareDouble
argument_list|(
name|GeoPoint
operator|::
name|resetLon
argument_list|,
name|LON_FIELD
argument_list|)
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|ParsedGeoBounds
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|ParsedGeoBounds
name|geoBounds
init|=
name|PARSER
operator|.
name|apply
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|geoBounds
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|geoBounds
return|;
block|}
block|}
end_class

end_unit

