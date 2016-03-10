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
name|spatial
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
name|Objects
import|;
end_import

begin_class
DECL|class|GeoGridAggregatorBuilder
specifier|public
class|class
name|GeoGridAggregatorBuilder
extends|extends
name|ValuesSourceAggregatorBuilder
argument_list|<
name|ValuesSource
operator|.
name|GeoPoint
argument_list|,
name|GeoGridAggregatorBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|GeoGridAggregatorBuilder
name|PROTOTYPE
init|=
operator|new
name|GeoGridAggregatorBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|precision
specifier|private
name|int
name|precision
init|=
name|GeoHashGridParser
operator|.
name|DEFAULT_PRECISION
decl_stmt|;
DECL|field|requiredSize
specifier|private
name|int
name|requiredSize
init|=
name|GeoHashGridParser
operator|.
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
DECL|method|GeoGridAggregatorBuilder
specifier|public
name|GeoGridAggregatorBuilder
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
name|GeoGridAggregatorBuilder
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
name|GeoGridAggregatorBuilder
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[size] must be greater than or equal to 0. Found ["
operator|+
name|shardSize
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
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
name|GeoGridAggregatorBuilder
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
if|if
condition|(
name|shardSize
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[shardSize] must be greater than or equal to 0. Found ["
operator|+
name|shardSize
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
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
name|int
name|shardSize
init|=
name|this
operator|.
name|shardSize
decl_stmt|;
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
name|int
name|requiredSize
init|=
name|this
operator|.
name|requiredSize
decl_stmt|;
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
comment|// Use default heuristic to avoid any wrong-ranking caused by distributed counting
name|shardSize
operator|=
name|BucketUtils
operator|.
name|suggestShardSideQueueSize
argument_list|(
name|requiredSize
argument_list|,
name|context
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
return|return
operator|new
name|GeoHashGridAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|precision
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
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
DECL|method|innerReadFrom
specifier|protected
name|GeoGridAggregatorBuilder
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
name|GeoGridAggregatorBuilder
name|factory
init|=
operator|new
name|GeoGridAggregatorBuilder
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
name|GeoGridAggregatorBuilder
name|other
init|=
operator|(
name|GeoGridAggregatorBuilder
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
end_class

end_unit
