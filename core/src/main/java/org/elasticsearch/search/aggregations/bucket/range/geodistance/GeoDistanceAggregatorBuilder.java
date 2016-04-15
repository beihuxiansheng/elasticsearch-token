begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.geodistance
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
operator|.
name|geodistance
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
name|ParseField
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
name|AggregatorFactories
operator|.
name|Builder
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
name|AggregatorFactory
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
name|bucket
operator|.
name|range
operator|.
name|InternalRange
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
name|bucket
operator|.
name|range
operator|.
name|RangeAggregator
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
name|bucket
operator|.
name|range
operator|.
name|geodistance
operator|.
name|GeoDistanceParser
operator|.
name|Range
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
name|support
operator|.
name|AggregationContext
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
name|support
operator|.
name|ValuesSource
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
name|support
operator|.
name|ValuesSourceAggregatorBuilder
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
name|support
operator|.
name|ValuesSourceAggregatorFactory
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
name|support
operator|.
name|ValuesSourceConfig
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
name|ArrayList
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
name|Objects
import|;
end_import

begin_class
DECL|class|GeoDistanceAggregatorBuilder
specifier|public
class|class
name|GeoDistanceAggregatorBuilder
extends|extends
name|ValuesSourceAggregatorBuilder
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|,
name|GeoDistanceAggregatorBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|InternalGeoDistance
operator|.
name|TYPE
operator|.
name|name
argument_list|()
decl_stmt|;
DECL|field|AGGREGATION_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|AGGREGATION_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|origin
specifier|private
specifier|final
name|GeoPoint
name|origin
decl_stmt|;
DECL|field|ranges
specifier|private
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|unit
specifier|private
name|DistanceUnit
name|unit
init|=
name|DistanceUnit
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|distanceType
specifier|private
name|GeoDistance
name|distanceType
init|=
name|GeoDistance
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|keyed
specifier|private
name|boolean
name|keyed
init|=
literal|false
decl_stmt|;
DECL|method|GeoDistanceAggregatorBuilder
specifier|public
name|GeoDistanceAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|GeoPoint
name|origin
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|origin
argument_list|,
name|InternalGeoDistance
operator|.
name|FACTORY
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoDistanceAggregatorBuilder
specifier|private
name|GeoDistanceAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|GeoPoint
name|origin
parameter_list|,
name|InternalRange
operator|.
name|Factory
argument_list|<
name|InternalGeoDistance
operator|.
name|Bucket
argument_list|,
name|InternalGeoDistance
argument_list|>
name|rangeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|rangeFactory
operator|.
name|type
argument_list|()
argument_list|,
name|rangeFactory
operator|.
name|getValueSourceType
argument_list|()
argument_list|,
name|rangeFactory
operator|.
name|getValueType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|origin
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[origin] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|GeoDistanceAggregatorBuilder
specifier|public
name|GeoDistanceAggregatorBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|InternalGeoDistance
operator|.
name|FACTORY
operator|.
name|type
argument_list|()
argument_list|,
name|InternalGeoDistance
operator|.
name|FACTORY
operator|.
name|getValueSourceType
argument_list|()
argument_list|,
name|InternalGeoDistance
operator|.
name|FACTORY
operator|.
name|getValueType
argument_list|()
argument_list|)
expr_stmt|;
name|origin
operator|=
operator|new
name|GeoPoint
argument_list|(
name|in
operator|.
name|readDouble
argument_list|()
argument_list|,
name|in
operator|.
name|readDouble
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ranges
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ranges
operator|.
name|add
argument_list|(
name|Range
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|keyed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|distanceType
operator|=
name|GeoDistance
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|unit
operator|=
name|DistanceUnit
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|innerWriteTo
specifier|protected
name|void
name|innerWriteTo
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
name|origin
operator|.
name|lat
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|origin
operator|.
name|lon
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Range
name|range
range|:
name|ranges
control|)
block|{
name|range
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|keyed
argument_list|)
expr_stmt|;
name|distanceType
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|unit
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|usesNewStyleSerialization
specifier|protected
name|boolean
name|usesNewStyleSerialization
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|addRange
specifier|public
name|GeoDistanceAggregatorBuilder
name|addRange
parameter_list|(
name|Range
name|range
parameter_list|)
block|{
if|if
condition|(
name|range
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[range] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|ranges
operator|.
name|add
argument_list|(
name|range
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a new range to this aggregation.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the distances, inclusive      * @param to      *            the upper bound on the distances, exclusive      */
DECL|method|addRange
specifier|public
name|GeoDistanceAggregatorBuilder
name|addRange
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|)
block|{
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addRange(String, double, double)} but the key will be      * automatically generated based on<code>from</code> and      *<code>to</code>.      */
DECL|method|addRange
specifier|public
name|GeoDistanceAggregatorBuilder
name|addRange
parameter_list|(
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
literal|null
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no lower bound.      *      * @param key      *            the key to use for this range in the response      * @param to      *            the upper bound on the distances, exclusive      */
DECL|method|addUnboundedTo
specifier|public
name|GeoDistanceAggregatorBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|to
parameter_list|)
block|{
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedTo(String, double)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedTo
specifier|public
name|GeoDistanceAggregatorBuilder
name|addUnboundedTo
parameter_list|(
name|double
name|to
parameter_list|)
block|{
return|return
name|addUnboundedTo
argument_list|(
literal|null
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no upper bound.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the distances, inclusive      */
DECL|method|addUnboundedFrom
specifier|public
name|GeoDistanceAggregatorBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedFrom(String, double)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedFrom
specifier|public
name|GeoDistanceAggregatorBuilder
name|addUnboundedFrom
parameter_list|(
name|double
name|from
parameter_list|)
block|{
return|return
name|addUnboundedFrom
argument_list|(
literal|null
argument_list|,
name|from
argument_list|)
return|;
block|}
DECL|method|range
specifier|public
name|List
argument_list|<
name|Range
argument_list|>
name|range
parameter_list|()
block|{
return|return
name|ranges
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
DECL|method|unit
specifier|public
name|GeoDistanceAggregatorBuilder
name|unit
parameter_list|(
name|DistanceUnit
name|unit
parameter_list|)
block|{
if|if
condition|(
name|unit
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[unit] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|unit
specifier|public
name|DistanceUnit
name|unit
parameter_list|()
block|{
return|return
name|unit
return|;
block|}
DECL|method|distanceType
specifier|public
name|GeoDistanceAggregatorBuilder
name|distanceType
parameter_list|(
name|GeoDistance
name|distanceType
parameter_list|)
block|{
if|if
condition|(
name|distanceType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[distanceType] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|distanceType
operator|=
name|distanceType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|distanceType
specifier|public
name|GeoDistance
name|distanceType
parameter_list|()
block|{
return|return
name|distanceType
return|;
block|}
DECL|method|keyed
specifier|public
name|GeoDistanceAggregatorBuilder
name|keyed
parameter_list|(
name|boolean
name|keyed
parameter_list|)
block|{
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|keyed
specifier|public
name|boolean
name|keyed
parameter_list|()
block|{
return|return
name|keyed
return|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|,
name|?
argument_list|>
name|innerBuild
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|>
name|config
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|Builder
name|subFactoriesBuilder
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|GeoDistanceRangeAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|origin
argument_list|,
name|ranges
argument_list|,
name|unit
argument_list|,
name|distanceType
argument_list|,
name|keyed
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|subFactoriesBuilder
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
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
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceParser
operator|.
name|ORIGIN_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|origin
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|RangeAggregator
operator|.
name|RANGES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|RangeAggregator
operator|.
name|KEYED_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|keyed
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceParser
operator|.
name|UNIT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|unit
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceParser
operator|.
name|DISTANCE_TYPE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|distanceType
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|innerHashCode
specifier|protected
name|int
name|innerHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|origin
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|,
name|distanceType
argument_list|,
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerEquals
specifier|protected
name|boolean
name|innerEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|GeoDistanceAggregatorBuilder
name|other
init|=
operator|(
name|GeoDistanceAggregatorBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|origin
argument_list|,
name|other
operator|.
name|origin
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|ranges
argument_list|,
name|other
operator|.
name|ranges
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|keyed
argument_list|,
name|other
operator|.
name|keyed
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|distanceType
argument_list|,
name|other
operator|.
name|distanceType
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|unit
argument_list|,
name|other
operator|.
name|unit
argument_list|)
return|;
block|}
block|}
end_class

end_unit

