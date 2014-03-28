begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectIntMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectIntOpenHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectIntCursor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|IndexRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchResponse
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
name|query
operator|.
name|GeoBoundingBoxFilterBuilder
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
name|AggregationBuilders
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
name|filter
operator|.
name|Filter
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
name|geogrid
operator|.
name|GeoHashGrid
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Random
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
name|XContentFactory
operator|.
name|jsonBuilder
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
name|AggregationBuilders
operator|.
name|geohashGrid
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThanOrEqualTo
import|;
end_import

begin_class
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|SuiteScopeTest
DECL|class|GeoHashGridTests
specifier|public
class|class
name|GeoHashGridTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|method|indexCity
specifier|private
name|IndexRequestBuilder
name|indexCity
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|latLon
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentBuilder
name|source
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"city"
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|latLon
operator|!=
literal|null
condition|)
block|{
name|source
operator|=
name|source
operator|.
name|field
argument_list|(
literal|"location"
argument_list|,
name|latLon
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
name|source
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
return|;
block|}
DECL|field|expectedDocCountsForGeoHash
specifier|static
name|ObjectIntMap
argument_list|<
name|String
argument_list|>
name|expectedDocCountsForGeoHash
init|=
literal|null
decl_stmt|;
DECL|field|highestPrecisionGeohash
specifier|static
name|int
name|highestPrecisionGeohash
init|=
literal|12
decl_stmt|;
DECL|field|numRandomPoints
specifier|static
name|int
name|numRandomPoints
init|=
literal|100
decl_stmt|;
DECL|field|smallestGeoHash
specifier|static
name|String
name|smallestGeoHash
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|setupSuiteScopeCluster
specifier|public
name|void
name|setupSuiteScopeCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"location"
argument_list|,
literal|"type=geo_point"
argument_list|,
literal|"city"
argument_list|,
literal|"type=string,index=not_analyzed"
argument_list|)
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"idx_unmapped"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|cities
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
name|getRandom
argument_list|()
decl_stmt|;
name|expectedDocCountsForGeoHash
operator|=
operator|new
name|ObjectIntOpenHashMap
argument_list|<>
argument_list|(
name|numRandomPoints
operator|*
literal|2
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
name|numRandomPoints
condition|;
name|i
operator|++
control|)
block|{
comment|//generate random point
name|double
name|lat
init|=
operator|(
literal|180d
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
operator|)
operator|-
literal|90d
decl_stmt|;
name|double
name|lng
init|=
operator|(
literal|360d
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
operator|)
operator|-
literal|180d
decl_stmt|;
name|String
name|randomGeoHash
init|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|highestPrecisionGeohash
argument_list|)
decl_stmt|;
comment|//Index at the highest resolution
name|cities
operator|.
name|add
argument_list|(
name|indexCity
argument_list|(
name|randomGeoHash
argument_list|,
name|lat
operator|+
literal|", "
operator|+
name|lng
argument_list|)
argument_list|)
expr_stmt|;
name|expectedDocCountsForGeoHash
operator|.
name|put
argument_list|(
name|randomGeoHash
argument_list|,
name|expectedDocCountsForGeoHash
operator|.
name|getOrDefault
argument_list|(
name|randomGeoHash
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//Update expected doc counts for all resolutions..
for|for
control|(
name|int
name|precision
init|=
name|highestPrecisionGeohash
operator|-
literal|1
init|;
name|precision
operator|>
literal|0
condition|;
name|precision
operator|--
control|)
block|{
name|String
name|hash
init|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|precision
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|smallestGeoHash
operator|==
literal|null
operator|)
operator|||
operator|(
name|hash
operator|.
name|length
argument_list|()
operator|<
name|smallestGeoHash
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
name|smallestGeoHash
operator|=
name|hash
expr_stmt|;
block|}
name|expectedDocCountsForGeoHash
operator|.
name|put
argument_list|(
name|hash
argument_list|,
name|expectedDocCountsForGeoHash
operator|.
name|getOrDefault
argument_list|(
name|hash
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|cities
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simple
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|precision
init|=
literal|1
init|;
name|precision
operator|<=
name|highestPrecisionGeohash
condition|;
name|precision
operator|++
control|)
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"geohashgrid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|GeoHashGrid
name|geoGrid
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"geohashgrid"
argument_list|)
decl_stmt|;
for|for
control|(
name|GeoHashGrid
operator|.
name|Bucket
name|cell
range|:
name|geoGrid
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|String
name|geohash
init|=
name|cell
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|bucketCount
init|=
name|cell
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|int
name|expectedBucketCount
init|=
name|expectedDocCountsForGeoHash
operator|.
name|get
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|bucketCount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Geohash "
operator|+
name|geohash
operator|+
literal|" has wrong doc count "
argument_list|,
name|expectedBucketCount
argument_list|,
name|bucketCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|filtered
specifier|public
name|void
name|filtered
parameter_list|()
throws|throws
name|Exception
block|{
name|GeoBoundingBoxFilterBuilder
name|bbox
init|=
operator|new
name|GeoBoundingBoxFilterBuilder
argument_list|(
literal|"location"
argument_list|)
decl_stmt|;
name|bbox
operator|.
name|topLeft
argument_list|(
name|smallestGeoHash
argument_list|)
operator|.
name|bottomRight
argument_list|(
name|smallestGeoHash
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"bbox"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|precision
init|=
literal|1
init|;
name|precision
operator|<=
name|highestPrecisionGeohash
condition|;
name|precision
operator|++
control|)
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|filter
argument_list|(
literal|"filtered"
argument_list|)
operator|.
name|filter
argument_list|(
name|bbox
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"geohashgrid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Filter
name|filter
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"filtered"
argument_list|)
decl_stmt|;
name|GeoHashGrid
name|geoGrid
init|=
name|filter
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"geohashgrid"
argument_list|)
decl_stmt|;
for|for
control|(
name|GeoHashGrid
operator|.
name|Bucket
name|cell
range|:
name|geoGrid
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|String
name|geohash
init|=
name|cell
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|bucketCount
init|=
name|cell
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|int
name|expectedBucketCount
init|=
name|expectedDocCountsForGeoHash
operator|.
name|get
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|bucketCount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Buckets must be filtered"
argument_list|,
name|geohash
operator|.
name|startsWith
argument_list|(
name|smallestGeoHash
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Geohash "
operator|+
name|geohash
operator|+
literal|" has wrong doc count "
argument_list|,
name|expectedBucketCount
argument_list|,
name|bucketCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|unmapped
specifier|public
name|void
name|unmapped
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|precision
init|=
literal|1
init|;
name|precision
operator|<=
name|highestPrecisionGeohash
condition|;
name|precision
operator|++
control|)
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx_unmapped"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"geohashgrid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|GeoHashGrid
name|geoGrid
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"geohashgrid"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|geoGrid
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|partiallyUnmapped
specifier|public
name|void
name|partiallyUnmapped
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|precision
init|=
literal|1
init|;
name|precision
operator|<=
name|highestPrecisionGeohash
condition|;
name|precision
operator|++
control|)
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|,
literal|"idx_unmapped"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"geohashgrid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|GeoHashGrid
name|geoGrid
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"geohashgrid"
argument_list|)
decl_stmt|;
for|for
control|(
name|GeoHashGrid
operator|.
name|Bucket
name|cell
range|:
name|geoGrid
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|String
name|geohash
init|=
name|cell
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|bucketCount
init|=
name|cell
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|int
name|expectedBucketCount
init|=
name|expectedDocCountsForGeoHash
operator|.
name|get
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|bucketCount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Geohash "
operator|+
name|geohash
operator|+
literal|" has wrong doc count "
argument_list|,
name|expectedBucketCount
argument_list|,
name|bucketCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testTopMatch
specifier|public
name|void
name|testTopMatch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|precision
init|=
literal|1
init|;
name|precision
operator|<=
name|highestPrecisionGeohash
condition|;
name|precision
operator|++
control|)
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"geohashgrid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|size
argument_list|(
literal|1
argument_list|)
operator|.
name|shardSize
argument_list|(
literal|100
argument_list|)
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|GeoHashGrid
name|geoGrid
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"geohashgrid"
argument_list|)
decl_stmt|;
comment|//Check we only have one bucket with the best match for that resolution
name|assertThat
argument_list|(
name|geoGrid
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|GeoHashGrid
operator|.
name|Bucket
name|cell
range|:
name|geoGrid
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|String
name|geohash
init|=
name|cell
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|bucketCount
init|=
name|cell
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|int
name|expectedBucketCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ObjectIntCursor
argument_list|<
name|String
argument_list|>
name|cursor
range|:
name|expectedDocCountsForGeoHash
control|)
block|{
if|if
condition|(
name|cursor
operator|.
name|key
operator|.
name|length
argument_list|()
operator|==
name|precision
condition|)
block|{
name|expectedBucketCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|expectedBucketCount
argument_list|,
name|cursor
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotSame
argument_list|(
name|bucketCount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Geohash "
operator|+
name|geohash
operator|+
literal|" has wrong doc count "
argument_list|,
name|expectedBucketCount
argument_list|,
name|bucketCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
comment|// making sure this doesn't runs into an OOME
DECL|method|sizeIsZero
specifier|public
name|void
name|sizeIsZero
parameter_list|()
block|{
for|for
control|(
name|int
name|precision
init|=
literal|1
init|;
name|precision
operator|<=
name|highestPrecisionGeohash
condition|;
name|precision
operator|++
control|)
block|{
specifier|final
name|int
name|size
init|=
name|randomBoolean
argument_list|()
condition|?
literal|0
else|:
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|int
name|shardSize
init|=
name|randomBoolean
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|0
decl_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"geohashgrid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|size
argument_list|(
name|size
argument_list|)
operator|.
name|shardSize
argument_list|(
name|shardSize
argument_list|)
operator|.
name|precision
argument_list|(
name|precision
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|GeoHashGrid
name|geoGrid
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"geohashgrid"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|geoGrid
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

