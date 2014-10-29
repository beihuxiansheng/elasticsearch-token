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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SortedNumericDocValues
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
name|GeoDistance
operator|.
name|FixedSourceDistance
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
name|lucene
operator|.
name|ReaderContextAware
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
name|XContentParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|MultiGeoPointValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|SortedBinaryDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|SortedNumericDoubleValues
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
name|SearchParseException
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
name|Aggregator
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
name|RangeAggregator
operator|.
name|Unmapped
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
name|*
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
name|internal
operator|.
name|SearchContext
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
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GeoDistanceParser
specifier|public
class|class
name|GeoDistanceParser
implements|implements
name|Aggregator
operator|.
name|Parser
block|{
DECL|field|ORIGIN_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|ORIGIN_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"origin"
argument_list|,
literal|"center"
argument_list|,
literal|"point"
argument_list|,
literal|"por"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|InternalGeoDistance
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|key
specifier|private
specifier|static
name|String
name|key
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
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
return|return
name|key
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|from
operator|==
literal|0
condition|?
literal|"*"
else|:
name|from
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|to
argument_list|)
condition|?
literal|"*"
else|:
name|to
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|AggregatorFactory
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ValuesSourceParser
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|>
name|vsParser
init|=
name|ValuesSourceParser
operator|.
name|geoPoint
argument_list|(
name|aggregationName
argument_list|,
name|InternalGeoDistance
operator|.
name|TYPE
argument_list|,
name|context
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GeoPointParser
name|geoPointParser
init|=
operator|new
name|GeoPointParser
argument_list|(
name|aggregationName
argument_list|,
name|InternalGeoDistance
operator|.
name|TYPE
argument_list|,
name|context
argument_list|,
name|ORIGIN_FIELD
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RangeAggregator
operator|.
name|Range
argument_list|>
name|ranges
init|=
literal|null
decl_stmt|;
name|DistanceUnit
name|unit
init|=
name|DistanceUnit
operator|.
name|DEFAULT
decl_stmt|;
name|GeoDistance
name|distanceType
init|=
name|GeoDistance
operator|.
name|DEFAULT
decl_stmt|;
name|boolean
name|keyed
init|=
literal|false
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|vsParser
operator|.
name|token
argument_list|(
name|currentFieldName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|geoPointParser
operator|.
name|token
argument_list|(
name|currentFieldName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
literal|"unit"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|unit
operator|=
name|DistanceUnit
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"distance_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"distanceType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|distanceType
operator|=
name|GeoDistance
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
if|if
condition|(
literal|"keyed"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|keyed
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"ranges"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ranges
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|String
name|fromAsStr
init|=
literal|null
decl_stmt|;
name|String
name|toAsStr
init|=
literal|null
decl_stmt|;
name|double
name|from
init|=
literal|0.0
decl_stmt|;
name|double
name|to
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|String
name|key
init|=
literal|null
decl_stmt|;
name|String
name|toOrFromOrKey
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|toOrFromOrKey
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
if|if
condition|(
literal|"from"
operator|.
name|equals
argument_list|(
name|toOrFromOrKey
argument_list|)
condition|)
block|{
name|from
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"to"
operator|.
name|equals
argument_list|(
name|toOrFromOrKey
argument_list|)
condition|)
block|{
name|to
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
literal|"key"
operator|.
name|equals
argument_list|(
name|toOrFromOrKey
argument_list|)
condition|)
block|{
name|key
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"from"
operator|.
name|equals
argument_list|(
name|toOrFromOrKey
argument_list|)
condition|)
block|{
name|fromAsStr
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"to"
operator|.
name|equals
argument_list|(
name|toOrFromOrKey
argument_list|)
condition|)
block|{
name|toAsStr
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|RangeAggregator
operator|.
name|Range
argument_list|(
name|key
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
argument_list|,
name|from
argument_list|,
name|fromAsStr
argument_list|,
name|to
argument_list|,
name|toAsStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Missing [ranges] in geo_distance aggregator ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|GeoPoint
name|origin
init|=
name|geoPointParser
operator|.
name|geoPoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|origin
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Missing [origin] in geo_distance aggregator ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
operator|new
name|GeoDistanceFactory
argument_list|(
name|aggregationName
argument_list|,
name|vsParser
operator|.
name|config
argument_list|()
argument_list|,
name|InternalGeoDistance
operator|.
name|FACTORY
argument_list|,
name|origin
argument_list|,
name|unit
argument_list|,
name|distanceType
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|)
return|;
block|}
DECL|class|GeoDistanceFactory
specifier|private
specifier|static
class|class
name|GeoDistanceFactory
extends|extends
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
block|{
DECL|field|origin
specifier|private
specifier|final
name|GeoPoint
name|origin
decl_stmt|;
DECL|field|unit
specifier|private
specifier|final
name|DistanceUnit
name|unit
decl_stmt|;
DECL|field|distanceType
specifier|private
specifier|final
name|GeoDistance
name|distanceType
decl_stmt|;
DECL|field|rangeFactory
specifier|private
specifier|final
name|InternalRange
operator|.
name|Factory
name|rangeFactory
decl_stmt|;
DECL|field|ranges
specifier|private
specifier|final
name|List
argument_list|<
name|RangeAggregator
operator|.
name|Range
argument_list|>
name|ranges
decl_stmt|;
DECL|field|keyed
specifier|private
specifier|final
name|boolean
name|keyed
decl_stmt|;
DECL|method|GeoDistanceFactory
specifier|public
name|GeoDistanceFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|>
name|valueSourceConfig
parameter_list|,
name|InternalRange
operator|.
name|Factory
name|rangeFactory
parameter_list|,
name|GeoPoint
name|origin
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|,
name|GeoDistance
name|distanceType
parameter_list|,
name|List
argument_list|<
name|RangeAggregator
operator|.
name|Range
argument_list|>
name|ranges
parameter_list|,
name|boolean
name|keyed
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
name|valueSourceConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|distanceType
operator|=
name|distanceType
expr_stmt|;
name|this
operator|.
name|rangeFactory
operator|=
name|rangeFactory
expr_stmt|;
name|this
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createUnmapped
specifier|protected
name|Aggregator
name|createUnmapped
parameter_list|(
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
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
return|return
operator|new
name|Unmapped
argument_list|(
name|name
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|,
literal|null
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|rangeFactory
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Aggregator
name|create
parameter_list|(
specifier|final
name|ValuesSource
operator|.
name|GeoPoint
name|valuesSource
parameter_list|,
name|long
name|expectedBucketsCount
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
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
name|DistanceSource
name|distanceSource
init|=
operator|new
name|DistanceSource
argument_list|(
name|valuesSource
argument_list|,
name|distanceType
argument_list|,
name|origin
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|aggregationContext
operator|.
name|registerReaderContextAware
argument_list|(
name|distanceSource
argument_list|)
expr_stmt|;
return|return
operator|new
name|RangeAggregator
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|distanceSource
argument_list|,
literal|null
argument_list|,
name|rangeFactory
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|metaData
argument_list|)
return|;
block|}
DECL|class|DistanceSource
specifier|private
specifier|static
class|class
name|DistanceSource
extends|extends
name|ValuesSource
operator|.
name|Numeric
implements|implements
name|ReaderContextAware
block|{
DECL|field|source
specifier|private
specifier|final
name|ValuesSource
operator|.
name|GeoPoint
name|source
decl_stmt|;
DECL|field|distanceType
specifier|private
specifier|final
name|GeoDistance
name|distanceType
decl_stmt|;
DECL|field|unit
specifier|private
specifier|final
name|DistanceUnit
name|unit
decl_stmt|;
DECL|field|origin
specifier|private
specifier|final
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoPoint
name|origin
decl_stmt|;
DECL|field|metaData
specifier|private
specifier|final
name|MetaData
name|metaData
decl_stmt|;
DECL|field|distanceValues
specifier|private
name|SortedNumericDoubleValues
name|distanceValues
decl_stmt|;
DECL|method|DistanceSource
specifier|public
name|DistanceSource
parameter_list|(
name|ValuesSource
operator|.
name|GeoPoint
name|source
parameter_list|,
name|GeoDistance
name|distanceType
parameter_list|,
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoPoint
name|origin
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
comment|// even if the geo points are unique, there's no guarantee the distances are
name|this
operator|.
name|metaData
operator|=
name|MetaData
operator|.
name|builder
argument_list|(
name|source
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|uniqueness
argument_list|(
name|MetaData
operator|.
name|Uniqueness
operator|.
name|UNKNOWN
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|distanceType
operator|=
name|distanceType
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|reader
parameter_list|)
block|{
specifier|final
name|MultiGeoPointValues
name|geoValues
init|=
name|source
operator|.
name|geoPointValues
argument_list|()
decl_stmt|;
specifier|final
name|FixedSourceDistance
name|distance
init|=
name|distanceType
operator|.
name|fixedSourceDistance
argument_list|(
name|origin
operator|.
name|getLat
argument_list|()
argument_list|,
name|origin
operator|.
name|getLon
argument_list|()
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|distanceValues
operator|=
name|GeoDistance
operator|.
name|distanceValues
argument_list|(
name|geoValues
argument_list|,
name|distance
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|metaData
specifier|public
name|MetaData
name|metaData
parameter_list|()
block|{
return|return
name|metaData
return|;
block|}
annotation|@
name|Override
DECL|method|isFloatingPoint
specifier|public
name|boolean
name|isFloatingPoint
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|longValues
specifier|public
name|SortedNumericDocValues
name|longValues
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|doubleValues
specifier|public
name|SortedNumericDoubleValues
name|doubleValues
parameter_list|()
block|{
return|return
name|distanceValues
return|;
block|}
annotation|@
name|Override
DECL|method|bytesValues
specifier|public
name|SortedBinaryDocValues
name|bytesValues
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

