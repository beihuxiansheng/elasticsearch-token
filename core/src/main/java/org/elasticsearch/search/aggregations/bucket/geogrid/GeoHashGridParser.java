begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.geogrid
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
name|geogrid
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
name|LeafReaderContext
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|GeoHashUtils
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
name|ParseFieldMatcher
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
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
name|index
operator|.
name|fielddata
operator|.
name|SortingNumericDocValues
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
name|query
operator|.
name|GeoBoundingBoxQueryBuilder
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
name|NonCollectingAggregator
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
name|BucketUtils
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
name|AbstractValuesSourceParser
operator|.
name|GeoPointValuesSourceParser
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
name|ValueType
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
name|ValuesSourceType
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Aggregates Geo information into cells determined by geohashes of a given precision.  * WARNING - for high-precision geohashes it may prove necessary to use a {@link GeoBoundingBoxQueryBuilder}  * aggregation to focus in on a smaller area to avoid generating too many buckets and using too much RAM  */
end_comment

begin_class
DECL|class|GeoHashGridParser
specifier|public
class|class
name|GeoHashGridParser
extends|extends
name|GeoPointValuesSourceParser
block|{
DECL|field|DEFAULT_PRECISION
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PRECISION
init|=
literal|5
decl_stmt|;
DECL|field|DEFAULT_MAX_NUM_CELLS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_NUM_CELLS
init|=
literal|10000
decl_stmt|;
DECL|method|GeoHashGridParser
specifier|public
name|GeoHashGridParser
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|InternalGeoHashGrid
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFactoryPrototypes
specifier|public
name|AggregatorFactory
index|[]
name|getFactoryPrototypes
parameter_list|()
block|{
return|return
operator|new
name|AggregatorFactory
index|[]
block|{
operator|new
name|GeoGridFactory
argument_list|(
literal|null
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createFactory
specifier|protected
name|GeoGridFactory
name|createFactory
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|ValuesSourceType
name|valuesSourceType
parameter_list|,
name|ValueType
name|targetValueType
parameter_list|,
name|Map
argument_list|<
name|ParseField
argument_list|,
name|Object
argument_list|>
name|otherOptions
parameter_list|)
block|{
name|GeoGridFactory
name|factory
init|=
operator|new
name|GeoGridFactory
argument_list|(
name|aggregationName
argument_list|)
decl_stmt|;
name|Integer
name|precision
init|=
operator|(
name|Integer
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_PRECISION
argument_list|)
decl_stmt|;
if|if
condition|(
name|precision
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
expr_stmt|;
block|}
name|Integer
name|size
init|=
operator|(
name|Integer
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|size
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|Integer
name|shardSize
init|=
operator|(
name|Integer
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_SHARD_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardSize
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|shardSize
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
annotation|@
name|Override
DECL|method|token
specifier|protected
name|boolean
name|token
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|String
name|currentFieldName
parameter_list|,
name|Token
name|token
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|Map
argument_list|<
name|ParseField
argument_list|,
name|Object
argument_list|>
name|otherOptions
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
operator|||
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
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GeoHashGridParams
operator|.
name|FIELD_PRECISION
argument_list|)
condition|)
block|{
name|otherOptions
operator|.
name|put
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_PRECISION
argument_list|,
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GeoHashGridParams
operator|.
name|FIELD_SIZE
argument_list|)
condition|)
block|{
name|otherOptions
operator|.
name|put
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_SIZE
argument_list|,
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GeoHashGridParams
operator|.
name|FIELD_SHARD_SIZE
argument_list|)
condition|)
block|{
name|otherOptions
operator|.
name|put
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_SHARD_SIZE
argument_list|,
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|class|GeoGridFactory
specifier|public
specifier|static
class|class
name|GeoGridFactory
extends|extends
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|,
name|GeoGridFactory
argument_list|>
block|{
DECL|field|precision
specifier|private
name|int
name|precision
init|=
name|DEFAULT_PRECISION
decl_stmt|;
DECL|field|requiredSize
specifier|private
name|int
name|requiredSize
init|=
name|DEFAULT_MAX_NUM_CELLS
decl_stmt|;
DECL|field|shardSize
specifier|private
name|int
name|shardSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|GeoGridFactory
specifier|public
name|GeoGridFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalGeoHashGrid
operator|.
name|TYPE
argument_list|,
name|ValuesSourceType
operator|.
name|GEOPOINT
argument_list|,
name|ValueType
operator|.
name|GEOPOINT
argument_list|)
expr_stmt|;
block|}
DECL|method|precision
specifier|public
name|GeoGridFactory
name|precision
parameter_list|(
name|int
name|precision
parameter_list|)
block|{
name|this
operator|.
name|precision
operator|=
name|GeoHashGridParams
operator|.
name|checkPrecision
argument_list|(
name|precision
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|precision
specifier|public
name|int
name|precision
parameter_list|()
block|{
return|return
name|precision
return|;
block|}
DECL|method|size
specifier|public
name|GeoGridFactory
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|requiredSize
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|requiredSize
return|;
block|}
DECL|method|shardSize
specifier|public
name|GeoGridFactory
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|shardSize
specifier|public
name|int
name|shardSize
parameter_list|()
block|{
return|return
name|shardSize
return|;
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
throws|throws
name|IOException
block|{
specifier|final
name|InternalAggregation
name|aggregation
init|=
operator|new
name|InternalGeoHashGrid
argument_list|(
name|name
argument_list|,
name|requiredSize
argument_list|,
name|Collections
operator|.
expr|<
name|InternalGeoHashGrid
operator|.
name|Bucket
operator|>
name|emptyList
argument_list|()
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
decl_stmt|;
return|return
operator|new
name|NonCollectingAggregator
argument_list|(
name|name
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|buildEmptyAggregation
parameter_list|()
block|{
return|return
name|aggregation
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|doCreateInternal
specifier|protected
name|Aggregator
name|doCreateInternal
parameter_list|(
specifier|final
name|ValuesSource
operator|.
name|GeoPoint
name|valuesSource
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|boolean
name|collectsFromSingleBucket
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|shardSize
operator|==
literal|0
condition|)
block|{
name|shardSize
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|requiredSize
operator|==
literal|0
condition|)
block|{
name|requiredSize
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|shardSize
operator|<
literal|0
condition|)
block|{
comment|// Use default heuristic to avoid any wrong-ranking caused by
comment|// distributed counting
name|shardSize
operator|=
name|BucketUtils
operator|.
name|suggestShardSideQueueSize
argument_list|(
name|requiredSize
argument_list|,
name|aggregationContext
operator|.
name|searchContext
argument_list|()
operator|.
name|numberOfShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardSize
operator|<
name|requiredSize
condition|)
block|{
name|shardSize
operator|=
name|requiredSize
expr_stmt|;
block|}
if|if
condition|(
name|collectsFromSingleBucket
operator|==
literal|false
condition|)
block|{
return|return
name|asMultiBucketAggregator
argument_list|(
name|this
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
name|CellIdSource
name|cellIdSource
init|=
operator|new
name|CellIdSource
argument_list|(
name|valuesSource
argument_list|,
name|precision
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeoHashGridAggregator
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|cellIdSource
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerReadFrom
specifier|protected
name|GeoGridFactory
name|innerReadFrom
parameter_list|(
name|String
name|name
parameter_list|,
name|ValuesSourceType
name|valuesSourceType
parameter_list|,
name|ValueType
name|targetValueType
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|GeoGridFactory
name|factory
init|=
operator|new
name|GeoGridFactory
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|factory
operator|.
name|precision
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|factory
operator|.
name|requiredSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|factory
operator|.
name|shardSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
return|return
name|factory
return|;
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
name|writeVInt
argument_list|(
name|precision
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|requiredSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
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
name|GeoHashGridParams
operator|.
name|FIELD_PRECISION
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|precision
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_SIZE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|requiredSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoHashGridParams
operator|.
name|FIELD_SHARD_SIZE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|shardSize
argument_list|)
expr_stmt|;
return|return
name|builder
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
name|GeoGridFactory
name|other
init|=
operator|(
name|GeoGridFactory
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|precision
operator|!=
name|other
operator|.
name|precision
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|requiredSize
operator|!=
name|other
operator|.
name|requiredSize
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|shardSize
operator|!=
name|other
operator|.
name|shardSize
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
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
name|precision
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|)
return|;
block|}
DECL|class|CellValues
specifier|private
specifier|static
class|class
name|CellValues
extends|extends
name|SortingNumericDocValues
block|{
DECL|field|geoValues
specifier|private
name|MultiGeoPointValues
name|geoValues
decl_stmt|;
DECL|field|precision
specifier|private
name|int
name|precision
decl_stmt|;
DECL|method|CellValues
specifier|protected
name|CellValues
parameter_list|(
name|MultiGeoPointValues
name|geoValues
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|this
operator|.
name|geoValues
operator|=
name|geoValues
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|geoValues
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|resize
argument_list|(
name|geoValues
operator|.
name|count
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
name|count
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|GeoPoint
name|target
init|=
name|geoValues
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|GeoHashUtils
operator|.
name|longEncode
argument_list|(
name|target
operator|.
name|getLon
argument_list|()
argument_list|,
name|target
operator|.
name|getLat
argument_list|()
argument_list|,
name|precision
argument_list|)
expr_stmt|;
block|}
name|sort
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|CellIdSource
specifier|static
class|class
name|CellIdSource
extends|extends
name|ValuesSource
operator|.
name|Numeric
block|{
DECL|field|valuesSource
specifier|private
specifier|final
name|ValuesSource
operator|.
name|GeoPoint
name|valuesSource
decl_stmt|;
DECL|field|precision
specifier|private
specifier|final
name|int
name|precision
decl_stmt|;
DECL|method|CellIdSource
specifier|public
name|CellIdSource
parameter_list|(
name|ValuesSource
operator|.
name|GeoPoint
name|valuesSource
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|this
operator|.
name|valuesSource
operator|=
name|valuesSource
expr_stmt|;
comment|//different GeoPoints could map to the same or different geohash cells.
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
block|}
DECL|method|precision
specifier|public
name|int
name|precision
parameter_list|()
block|{
return|return
name|precision
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
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|longValues
specifier|public
name|SortedNumericDocValues
name|longValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
block|{
return|return
operator|new
name|CellValues
argument_list|(
name|valuesSource
operator|.
name|geoPointValues
argument_list|(
name|ctx
argument_list|)
argument_list|,
name|precision
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doubleValues
specifier|public
name|SortedNumericDoubleValues
name|doubleValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|bytesValues
specifier|public
name|SortedBinaryDocValues
name|bytesValues
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
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

