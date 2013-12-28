begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine.robin
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
operator|.
name|robin
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|LuceneTestCase
operator|.
name|Slow
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodeInfo
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodesInfoResponse
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
name|admin
operator|.
name|indices
operator|.
name|segments
operator|.
name|IndexSegments
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
name|admin
operator|.
name|indices
operator|.
name|segments
operator|.
name|IndexShardSegments
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
name|admin
operator|.
name|indices
operator|.
name|segments
operator|.
name|IndicesSegmentResponse
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
name|admin
operator|.
name|indices
operator|.
name|segments
operator|.
name|ShardSegments
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
name|admin
operator|.
name|indices
operator|.
name|stats
operator|.
name|IndicesStatsResponse
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
name|settings
operator|.
name|ImmutableSettings
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
name|ByteSizeUnit
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
name|ByteSizeValue
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
name|util
operator|.
name|BloomFilter
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
name|codec
operator|.
name|CodecService
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
name|engine
operator|.
name|Segment
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
name|QueryBuilders
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
name|hamcrest
operator|.
name|Matchers
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
import|;
end_import

begin_class
DECL|class|RobinEngineIntegrationTest
specifier|public
class|class
name|RobinEngineIntegrationTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
annotation|@
name|Slow
DECL|method|testSettingLoadBloomFilterDefaultTrue
specifier|public
name|void
name|testSettingLoadBloomFilterDefaultTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|allowRamBytesUsed
init|=
name|RobinEngine
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"allowRamBytesUsed"
argument_list|)
decl_stmt|;
name|allowRamBytesUsed
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|allowRamBytesUsed
operator|.
name|set
argument_list|(
name|RobinEngine
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|IndicesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|long
name|segmentsMemoryWithBloom
init|=
name|stats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemoryInBytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"segments with bloom: {}"
argument_list|,
name|segmentsMemoryWithBloom
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"updating the setting to unload bloom filters"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CodecService
operator|.
name|INDEX_CODEC_BLOOM_LOAD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for memory to match without blooms"
argument_list|)
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndicesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|segmentsMemoryWithoutBloom
init|=
name|stats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemoryInBytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"trying segments without bloom: {}"
argument_list|,
name|segmentsMemoryWithoutBloom
argument_list|)
expr_stmt|;
return|return
name|segmentsMemoryWithoutBloom
operator|==
operator|(
name|segmentsMemoryWithBloom
operator|-
name|BloomFilter
operator|.
name|Factory
operator|.
name|DEFAULT
operator|.
name|createFilter
argument_list|(
literal|1
argument_list|)
operator|.
name|getSizeInBytes
argument_list|()
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"updating the setting to load bloom filters"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CodecService
operator|.
name|INDEX_CODEC_BLOOM_LOAD
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for memory to match with blooms"
argument_list|)
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndicesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|newSegmentsMemoryWithBloom
init|=
name|stats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemoryInBytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"trying segments with bloom: {}"
argument_list|,
name|newSegmentsMemoryWithBloom
argument_list|)
expr_stmt|;
return|return
name|newSegmentsMemoryWithBloom
operator|==
name|segmentsMemoryWithBloom
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|allowRamBytesUsed
operator|.
name|set
argument_list|(
name|RobinEngine
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Slow
DECL|method|testSettingLoadBloomFilterDefaultFalse
specifier|public
name|void
name|testSettingLoadBloomFilterDefaultFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|allowRamBytesUsed
init|=
name|RobinEngine
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"allowRamBytesUsed"
argument_list|)
decl_stmt|;
name|allowRamBytesUsed
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|allowRamBytesUsed
operator|.
name|set
argument_list|(
name|RobinEngine
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|CodecService
operator|.
name|INDEX_CODEC_BLOOM_LOAD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|IndicesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|long
name|segmentsMemoryWithoutBloom
init|=
name|stats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemoryInBytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"segments without bloom: {}"
argument_list|,
name|segmentsMemoryWithoutBloom
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"updating the setting to load bloom filters"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CodecService
operator|.
name|INDEX_CODEC_BLOOM_LOAD
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for memory to match with blooms"
argument_list|)
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndicesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|segmentsMemoryWithBloom
init|=
name|stats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemoryInBytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"trying segments with bloom: {}"
argument_list|,
name|segmentsMemoryWithoutBloom
argument_list|)
expr_stmt|;
return|return
name|segmentsMemoryWithoutBloom
operator|==
operator|(
name|segmentsMemoryWithBloom
operator|-
name|BloomFilter
operator|.
name|Factory
operator|.
name|DEFAULT
operator|.
name|createFilter
argument_list|(
literal|1
argument_list|)
operator|.
name|getSizeInBytes
argument_list|()
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"updating the setting to unload bloom filters"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CodecService
operator|.
name|INDEX_CODEC_BLOOM_LOAD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for memory to match without blooms"
argument_list|)
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndicesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|newSegmentsMemoryWithoutBloom
init|=
name|stats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemoryInBytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"trying segments without bloom: {}"
argument_list|,
name|newSegmentsMemoryWithoutBloom
argument_list|)
expr_stmt|;
return|return
name|newSegmentsMemoryWithoutBloom
operator|==
name|segmentsMemoryWithoutBloom
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|allowRamBytesUsed
operator|.
name|set
argument_list|(
name|RobinEngine
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSetIndexCompoundOnFlush
specifier|public
name|void
name|testSetIndexCompoundOnFlush
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|assertTotalCompoundSegments
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|RobinEngine
operator|.
name|INDEX_COMPOUND_ON_FLUSH
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|assertTotalCompoundSegments
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|RobinEngine
operator|.
name|INDEX_COMPOUND_ON_FLUSH
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|assertTotalCompoundSegments
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTotalCompoundSegments
specifier|private
name|void
name|assertTotalCompoundSegments
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|t
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|IndicesSegmentResponse
name|indicesSegmentResponse
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSegments
argument_list|(
name|index
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|IndexSegments
name|indexSegments
init|=
name|indicesSegmentResponse
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|IndexShardSegments
argument_list|>
name|values
init|=
name|indexSegments
operator|.
name|getShards
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|int
name|compounds
init|=
literal|0
decl_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexShardSegments
name|indexShardSegments
range|:
name|values
control|)
block|{
for|for
control|(
name|ShardSegments
name|s
range|:
name|indexShardSegments
control|)
block|{
for|for
control|(
name|Segment
name|segment
range|:
name|s
control|)
block|{
if|if
condition|(
name|segment
operator|.
name|isSearch
argument_list|()
operator|&&
name|segment
operator|.
name|getNumDocs
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|segment
operator|.
name|isCompound
argument_list|()
condition|)
block|{
name|compounds
operator|++
expr_stmt|;
block|}
name|total
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
name|assertThat
argument_list|(
name|compounds
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|total
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test4093
specifier|public
name|void
name|test4093
parameter_list|()
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.store.type"
argument_list|,
literal|"memory"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cache.memory.large_cache_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|1
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
comment|// no need to cache a lot
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|"0"
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|put
argument_list|(
name|RobinEngine
operator|.
name|INDEX_COMPOUND_ON_FLUSH
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.warmer.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|NodesInfoResponse
name|nodeInfos
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|setJvm
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|NodeInfo
index|[]
name|nodes
init|=
name|nodeInfos
operator|.
name|getNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeInfo
name|info
range|:
name|nodes
control|)
block|{
name|ByteSizeValue
name|directMemoryMax
init|=
name|info
operator|.
name|getJvm
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getDirectMemoryMax
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"  --> JVM max direct memory for node [{}] is set to [{}]"
argument_list|,
name|info
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|directMemoryMax
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numDocs
init|=
name|between
argument_list|(
literal|30
argument_list|,
literal|100
argument_list|)
decl_stmt|;
comment|// 30 docs are enough to fail without the fix for #4093
name|logger
operator|.
name|debug
argument_list|(
literal|"  --> Indexing [{}] documents"
argument_list|,
name|numDocs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"  --> Indexed [{}] documents"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"a"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"  --> Done indexing [{}] documents"
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

