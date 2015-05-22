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
name|search
operator|.
name|aggregations
operator|.
name|AggregationStreams
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
name|InternalAggregation
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
name|metrics
operator|.
name|InternalMetricsAggregation
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
name|pipeline
operator|.
name|PipelineAggregator
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|InternalGeoBounds
specifier|public
class|class
name|InternalGeoBounds
extends|extends
name|InternalMetricsAggregation
implements|implements
name|GeoBounds
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"geo_bounds"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|final
specifier|static
name|AggregationStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|AggregationStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalGeoBounds
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalGeoBounds
name|result
init|=
operator|new
name|InternalGeoBounds
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
DECL|field|top
specifier|private
name|double
name|top
decl_stmt|;
DECL|field|bottom
specifier|private
name|double
name|bottom
decl_stmt|;
DECL|field|posLeft
specifier|private
name|double
name|posLeft
decl_stmt|;
DECL|field|posRight
specifier|private
name|double
name|posRight
decl_stmt|;
DECL|field|negLeft
specifier|private
name|double
name|negLeft
decl_stmt|;
DECL|field|negRight
specifier|private
name|double
name|negRight
decl_stmt|;
DECL|field|wrapLongitude
specifier|private
name|boolean
name|wrapLongitude
decl_stmt|;
DECL|method|InternalGeoBounds
name|InternalGeoBounds
parameter_list|()
block|{     }
DECL|method|InternalGeoBounds
name|InternalGeoBounds
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|top
parameter_list|,
name|double
name|bottom
parameter_list|,
name|double
name|posLeft
parameter_list|,
name|double
name|posRight
parameter_list|,
name|double
name|negLeft
parameter_list|,
name|double
name|negRight
parameter_list|,
name|boolean
name|wrapLongitude
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|top
operator|=
name|top
expr_stmt|;
name|this
operator|.
name|bottom
operator|=
name|bottom
expr_stmt|;
name|this
operator|.
name|posLeft
operator|=
name|posLeft
expr_stmt|;
name|this
operator|.
name|posRight
operator|=
name|posRight
expr_stmt|;
name|this
operator|.
name|negLeft
operator|=
name|negLeft
expr_stmt|;
name|this
operator|.
name|negRight
operator|=
name|negRight
expr_stmt|;
name|this
operator|.
name|wrapLongitude
operator|=
name|wrapLongitude
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
name|InternalAggregation
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|double
name|top
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|bottom
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|posLeft
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|posRight
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|negLeft
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|negRight
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalGeoBounds
name|bounds
init|=
operator|(
name|InternalGeoBounds
operator|)
name|aggregation
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|top
operator|>
name|top
condition|)
block|{
name|top
operator|=
name|bounds
operator|.
name|top
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|bottom
operator|<
name|bottom
condition|)
block|{
name|bottom
operator|=
name|bounds
operator|.
name|bottom
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|posLeft
operator|<
name|posLeft
condition|)
block|{
name|posLeft
operator|=
name|bounds
operator|.
name|posLeft
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|posRight
operator|>
name|posRight
condition|)
block|{
name|posRight
operator|=
name|bounds
operator|.
name|posRight
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|negLeft
operator|<
name|negLeft
condition|)
block|{
name|negLeft
operator|=
name|bounds
operator|.
name|negLeft
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|negRight
operator|>
name|negRight
condition|)
block|{
name|negRight
operator|=
name|bounds
operator|.
name|negRight
expr_stmt|;
block|}
block|}
return|return
operator|new
name|InternalGeoBounds
argument_list|(
name|name
argument_list|,
name|top
argument_list|,
name|bottom
argument_list|,
name|posLeft
argument_list|,
name|posRight
argument_list|,
name|negLeft
argument_list|,
name|negRight
argument_list|,
name|wrapLongitude
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|BoundingBox
name|boundingBox
init|=
name|resolveBoundingBox
argument_list|()
decl_stmt|;
name|String
name|bBoxSide
init|=
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|bBoxSide
condition|)
block|{
case|case
literal|"top"
case|:
return|return
name|boundingBox
operator|.
name|topLeft
operator|.
name|lat
argument_list|()
return|;
case|case
literal|"left"
case|:
return|return
name|boundingBox
operator|.
name|topLeft
operator|.
name|lon
argument_list|()
return|;
case|case
literal|"bottom"
case|:
return|return
name|boundingBox
operator|.
name|bottomRight
operator|.
name|lat
argument_list|()
return|;
case|case
literal|"right"
case|:
return|return
name|boundingBox
operator|.
name|bottomRight
operator|.
name|lon
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Found unknown path element ["
operator|+
name|bBoxSide
operator|+
literal|"] in ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|BoundingBox
name|boundingBox
init|=
name|resolveBoundingBox
argument_list|()
decl_stmt|;
name|GeoPoint
name|cornerPoint
init|=
literal|null
decl_stmt|;
name|String
name|cornerString
init|=
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|cornerString
condition|)
block|{
case|case
literal|"top_left"
case|:
name|cornerPoint
operator|=
name|boundingBox
operator|.
name|topLeft
expr_stmt|;
break|break;
case|case
literal|"bottom_right"
case|:
name|cornerPoint
operator|=
name|boundingBox
operator|.
name|bottomRight
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Found unknown path element ["
operator|+
name|cornerString
operator|+
literal|"] in ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|String
name|latLonString
init|=
name|path
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|latLonString
condition|)
block|{
case|case
literal|"lat"
case|:
return|return
name|cornerPoint
operator|.
name|lat
argument_list|()
return|;
case|case
literal|"lon"
case|:
return|return
name|cornerPoint
operator|.
name|lon
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Found unknown path element ["
operator|+
name|latLonString
operator|+
literal|"] in ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path not supported for ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|path
argument_list|)
throw|;
block|}
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
name|GeoPoint
name|topLeft
init|=
name|topLeft
argument_list|()
decl_stmt|;
name|GeoPoint
name|bottomRight
init|=
name|bottomRight
argument_list|()
decl_stmt|;
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
literal|"bounds"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"top_left"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
name|topLeft
operator|.
name|lat
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
name|topLeft
operator|.
name|lon
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
literal|"bottom_right"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
name|bottomRight
operator|.
name|lat
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
name|bottomRight
operator|.
name|lon
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
DECL|method|doReadFrom
specifier|protected
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|top
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|bottom
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|posLeft
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|posRight
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|negLeft
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|negRight
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|wrapLongitude
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|top
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|bottom
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|posLeft
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|posRight
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|negLeft
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|negRight
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|wrapLongitude
argument_list|)
expr_stmt|;
block|}
DECL|method|registerStream
specifier|public
specifier|static
name|void
name|registerStream
parameter_list|()
block|{
name|AggregationStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|BoundingBox
specifier|private
specifier|static
class|class
name|BoundingBox
block|{
DECL|field|topLeft
specifier|private
specifier|final
name|GeoPoint
name|topLeft
decl_stmt|;
DECL|field|bottomRight
specifier|private
specifier|final
name|GeoPoint
name|bottomRight
decl_stmt|;
DECL|method|BoundingBox
specifier|public
name|BoundingBox
parameter_list|(
name|GeoPoint
name|topLeft
parameter_list|,
name|GeoPoint
name|bottomRight
parameter_list|)
block|{
name|this
operator|.
name|topLeft
operator|=
name|topLeft
expr_stmt|;
name|this
operator|.
name|bottomRight
operator|=
name|bottomRight
expr_stmt|;
block|}
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
block|}
DECL|method|resolveBoundingBox
specifier|private
name|BoundingBox
name|resolveBoundingBox
parameter_list|()
block|{
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|top
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|posLeft
argument_list|)
condition|)
block|{
return|return
operator|new
name|BoundingBox
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|top
argument_list|,
name|negLeft
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|bottom
argument_list|,
name|negRight
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|negLeft
argument_list|)
condition|)
block|{
return|return
operator|new
name|BoundingBox
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|top
argument_list|,
name|posLeft
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|bottom
argument_list|,
name|posRight
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|wrapLongitude
condition|)
block|{
name|double
name|unwrappedWidth
init|=
name|posRight
operator|-
name|negLeft
decl_stmt|;
name|double
name|wrappedWidth
init|=
operator|(
literal|180
operator|-
name|posLeft
operator|)
operator|-
operator|(
operator|-
literal|180
operator|-
name|negRight
operator|)
decl_stmt|;
if|if
condition|(
name|unwrappedWidth
operator|<=
name|wrappedWidth
condition|)
block|{
return|return
operator|new
name|BoundingBox
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|top
argument_list|,
name|negLeft
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|bottom
argument_list|,
name|posRight
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BoundingBox
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|top
argument_list|,
name|posLeft
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|bottom
argument_list|,
name|negRight
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|BoundingBox
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|top
argument_list|,
name|negLeft
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|bottom
argument_list|,
name|posRight
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|topLeft
specifier|public
name|GeoPoint
name|topLeft
parameter_list|()
block|{
name|BoundingBox
name|boundingBox
init|=
name|resolveBoundingBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|boundingBox
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|boundingBox
operator|.
name|topLeft
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|bottomRight
specifier|public
name|GeoPoint
name|bottomRight
parameter_list|()
block|{
name|BoundingBox
name|boundingBox
init|=
name|resolveBoundingBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|boundingBox
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|boundingBox
operator|.
name|bottomRight
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

