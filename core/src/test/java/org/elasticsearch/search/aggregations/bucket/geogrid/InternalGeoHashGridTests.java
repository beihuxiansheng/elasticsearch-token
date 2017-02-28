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
name|IndexWriter
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|InternalAggregationTestCase
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
name|InternalAggregations
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
name|HashMap
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
DECL|class|InternalGeoHashGridTests
specifier|public
class|class
name|InternalGeoHashGridTests
extends|extends
name|InternalAggregationTestCase
argument_list|<
name|InternalGeoHashGrid
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|InternalGeoHashGrid
name|createTestInstance
parameter_list|(
name|String
name|name
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
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
argument_list|)
decl_stmt|;
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
name|long
name|geoHashAsLong
init|=
name|GeoHashUtils
operator|.
name|longEncode
argument_list|(
name|randomInt
argument_list|(
literal|90
argument_list|)
argument_list|,
name|randomInt
argument_list|(
literal|90
argument_list|)
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|buckets
operator|.
name|add
argument_list|(
operator|new
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|(
name|geoHashAsLong
argument_list|,
name|randomInt
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|)
argument_list|,
name|InternalAggregations
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalGeoHashGrid
argument_list|(
name|name
argument_list|,
name|size
argument_list|,
name|buckets
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Writeable
operator|.
name|Reader
argument_list|<
name|InternalGeoHashGrid
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalGeoHashGrid
operator|::
operator|new
return|;
block|}
annotation|@
name|Override
DECL|method|assertReduced
specifier|protected
name|void
name|assertReduced
parameter_list|(
name|InternalGeoHashGrid
name|reduced
parameter_list|,
name|List
argument_list|<
name|InternalGeoHashGrid
argument_list|>
name|inputs
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalGeoHashGrid
name|input
range|:
name|inputs
control|)
block|{
for|for
control|(
name|GeoHashGrid
operator|.
name|Bucket
name|bucket
range|:
name|input
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|InternalGeoHashGrid
operator|.
name|Bucket
name|internalBucket
init|=
operator|(
name|InternalGeoHashGrid
operator|.
name|Bucket
operator|)
name|bucket
decl_stmt|;
name|List
argument_list|<
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
name|map
operator|.
name|get
argument_list|(
name|internalBucket
operator|.
name|geohashAsLong
argument_list|)
decl_stmt|;
if|if
condition|(
name|buckets
operator|==
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|internalBucket
operator|.
name|geohashAsLong
argument_list|,
name|buckets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buckets
operator|.
name|add
argument_list|(
name|internalBucket
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|>
name|expectedBuckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|>
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|docCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InternalGeoHashGrid
operator|.
name|Bucket
name|bucket
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|docCount
operator|+=
name|bucket
operator|.
name|docCount
expr_stmt|;
block|}
name|expectedBuckets
operator|.
name|add
argument_list|(
operator|new
name|InternalGeoHashGrid
operator|.
name|Bucket
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|docCount
argument_list|,
name|InternalAggregations
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expectedBuckets
operator|.
name|sort
argument_list|(
parameter_list|(
name|first
parameter_list|,
name|second
parameter_list|)
lambda|->
block|{
name|int
name|cmp
init|=
name|Long
operator|.
name|compare
argument_list|(
name|second
operator|.
name|docCount
argument_list|,
name|first
operator|.
name|docCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
return|return
name|second
operator|.
name|compareTo
argument_list|(
name|first
argument_list|)
return|;
block|}
return|return
name|cmp
return|;
block|}
argument_list|)
expr_stmt|;
name|int
name|requestedSize
init|=
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getRequiredSize
argument_list|()
decl_stmt|;
name|expectedBuckets
operator|=
name|expectedBuckets
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|requestedSize
argument_list|,
name|expectedBuckets
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedBuckets
operator|.
name|size
argument_list|()
argument_list|,
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
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
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|GeoHashGrid
operator|.
name|Bucket
name|expected
init|=
name|expectedBuckets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|GeoHashGrid
operator|.
name|Bucket
name|actual
init|=
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|actual
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getKey
argument_list|()
argument_list|,
name|actual
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

