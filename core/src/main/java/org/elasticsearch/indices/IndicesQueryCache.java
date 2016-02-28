begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|Term
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
name|search
operator|.
name|BulkScorer
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
name|search
operator|.
name|Explanation
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
name|search
operator|.
name|LRUQueryCache
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|QueryCache
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
name|search
operator|.
name|QueryCachingPolicy
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|Weight
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
name|component
operator|.
name|AbstractComponent
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
name|ShardCoreKeyMap
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
name|Setting
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
name|Setting
operator|.
name|SettingsProperty
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
name|Settings
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
name|index
operator|.
name|cache
operator|.
name|query
operator|.
name|QueryCacheStats
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
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
DECL|class|IndicesQueryCache
specifier|public
class|class
name|IndicesQueryCache
extends|extends
name|AbstractComponent
implements|implements
name|QueryCache
implements|,
name|Closeable
block|{
DECL|field|INDICES_CACHE_QUERY_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|INDICES_CACHE_QUERY_SIZE_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"indices.queries.cache.size"
argument_list|,
literal|"10%"
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|INDICES_CACHE_QUERY_COUNT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|INDICES_CACHE_QUERY_COUNT_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"indices.queries.cache.count"
argument_list|,
literal|10000
argument_list|,
literal|1
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|LRUQueryCache
name|cache
decl_stmt|;
DECL|field|shardKeyMap
specifier|private
specifier|final
name|ShardCoreKeyMap
name|shardKeyMap
init|=
operator|new
name|ShardCoreKeyMap
argument_list|()
decl_stmt|;
DECL|field|shardStats
specifier|private
specifier|final
name|Map
argument_list|<
name|ShardId
argument_list|,
name|Stats
argument_list|>
name|shardStats
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sharedRamBytesUsed
specifier|private
specifier|volatile
name|long
name|sharedRamBytesUsed
decl_stmt|;
comment|// This is a hack for the fact that the close listener for the
comment|// ShardCoreKeyMap will be called before onDocIdSetEviction
comment|// See onDocIdSetEviction for more info
DECL|field|stats2
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|StatsAndCount
argument_list|>
name|stats2
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|IndicesQueryCache
specifier|public
name|IndicesQueryCache
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
specifier|final
name|ByteSizeValue
name|size
init|=
name|INDICES_CACHE_QUERY_SIZE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|INDICES_CACHE_QUERY_COUNT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using [node] query cache with size [{}] max filter count [{}]"
argument_list|,
name|size
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|cache
operator|=
operator|new
name|LRUQueryCache
argument_list|(
name|count
argument_list|,
name|size
operator|.
name|bytes
argument_list|()
argument_list|)
block|{
specifier|private
name|Stats
name|getStats
parameter_list|(
name|Object
name|coreKey
parameter_list|)
block|{
specifier|final
name|ShardId
name|shardId
init|=
name|shardKeyMap
operator|.
name|getShardId
argument_list|(
name|coreKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|shardStats
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
return|;
block|}
specifier|private
name|Stats
name|getOrCreateStats
parameter_list|(
name|Object
name|coreKey
parameter_list|)
block|{
specifier|final
name|ShardId
name|shardId
init|=
name|shardKeyMap
operator|.
name|getShardId
argument_list|(
name|coreKey
argument_list|)
decl_stmt|;
name|Stats
name|stats
init|=
name|shardStats
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
operator|new
name|Stats
argument_list|()
expr_stmt|;
name|shardStats
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
comment|// It's ok to not protect these callbacks by a lock since it is
comment|// done in LRUQueryCache
annotation|@
name|Override
specifier|protected
name|void
name|onClear
parameter_list|()
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onClear
argument_list|()
expr_stmt|;
for|for
control|(
name|Stats
name|stats
range|:
name|shardStats
operator|.
name|values
argument_list|()
control|)
block|{
comment|// don't throw away hit/miss
name|stats
operator|.
name|cacheSize
operator|=
literal|0
expr_stmt|;
name|stats
operator|.
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
name|sharedRamBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onQueryCache
parameter_list|(
name|Query
name|filter
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onQueryCache
argument_list|(
name|filter
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
name|sharedRamBytesUsed
operator|+=
name|ramBytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onQueryEviction
parameter_list|(
name|Query
name|filter
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onQueryEviction
argument_list|(
name|filter
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
name|sharedRamBytesUsed
operator|-=
name|ramBytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onDocIdSetCache
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onDocIdSetCache
argument_list|(
name|readerCoreKey
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
specifier|final
name|Stats
name|shardStats
init|=
name|getOrCreateStats
argument_list|(
name|readerCoreKey
argument_list|)
decl_stmt|;
name|shardStats
operator|.
name|cacheSize
operator|+=
literal|1
expr_stmt|;
name|shardStats
operator|.
name|cacheCount
operator|+=
literal|1
expr_stmt|;
name|shardStats
operator|.
name|ramBytesUsed
operator|+=
name|ramBytesUsed
expr_stmt|;
name|StatsAndCount
name|statsAndCount
init|=
name|stats2
operator|.
name|get
argument_list|(
name|readerCoreKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsAndCount
operator|==
literal|null
condition|)
block|{
name|statsAndCount
operator|=
operator|new
name|StatsAndCount
argument_list|(
name|shardStats
argument_list|)
expr_stmt|;
name|stats2
operator|.
name|put
argument_list|(
name|readerCoreKey
argument_list|,
name|statsAndCount
argument_list|)
expr_stmt|;
block|}
name|statsAndCount
operator|.
name|count
operator|+=
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onDocIdSetEviction
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|int
name|numEntries
parameter_list|,
name|long
name|sumRamBytesUsed
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onDocIdSetEviction
argument_list|(
name|readerCoreKey
argument_list|,
name|numEntries
argument_list|,
name|sumRamBytesUsed
argument_list|)
expr_stmt|;
comment|// onDocIdSetEviction might sometimes be called with a number
comment|// of entries equal to zero if the cache for the given segment
comment|// was already empty when the close listener was called
if|if
condition|(
name|numEntries
operator|>
literal|0
condition|)
block|{
comment|// We can't use ShardCoreKeyMap here because its core closed
comment|// listener is called before the listener of the cache which
comment|// triggers this eviction. So instead we use use stats2 that
comment|// we only evict when nothing is cached anymore on the segment
comment|// instead of relying on close listeners
specifier|final
name|StatsAndCount
name|statsAndCount
init|=
name|stats2
operator|.
name|get
argument_list|(
name|readerCoreKey
argument_list|)
decl_stmt|;
specifier|final
name|Stats
name|shardStats
init|=
name|statsAndCount
operator|.
name|stats
decl_stmt|;
name|shardStats
operator|.
name|cacheSize
operator|-=
name|numEntries
expr_stmt|;
name|shardStats
operator|.
name|ramBytesUsed
operator|-=
name|sumRamBytesUsed
expr_stmt|;
name|statsAndCount
operator|.
name|count
operator|-=
name|numEntries
expr_stmt|;
if|if
condition|(
name|statsAndCount
operator|.
name|count
operator|==
literal|0
condition|)
block|{
name|stats2
operator|.
name|remove
argument_list|(
name|readerCoreKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onHit
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|Query
name|filter
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onHit
argument_list|(
name|readerCoreKey
argument_list|,
name|filter
argument_list|)
expr_stmt|;
specifier|final
name|Stats
name|shardStats
init|=
name|getStats
argument_list|(
name|readerCoreKey
argument_list|)
decl_stmt|;
name|shardStats
operator|.
name|hitCount
operator|+=
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onMiss
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|Query
name|filter
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|super
operator|.
name|onMiss
argument_list|(
name|readerCoreKey
argument_list|,
name|filter
argument_list|)
expr_stmt|;
specifier|final
name|Stats
name|shardStats
init|=
name|getOrCreateStats
argument_list|(
name|readerCoreKey
argument_list|)
decl_stmt|;
name|shardStats
operator|.
name|missCount
operator|+=
literal|1
expr_stmt|;
block|}
block|}
expr_stmt|;
name|sharedRamBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Get usage statistics for the given shard. */
DECL|method|getStats
specifier|public
name|QueryCacheStats
name|getStats
parameter_list|(
name|ShardId
name|shard
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|ShardId
argument_list|,
name|QueryCacheStats
argument_list|>
name|stats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|Stats
argument_list|>
name|entry
range|:
name|shardStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|stats
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toQueryCacheStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|QueryCacheStats
name|shardStats
init|=
operator|new
name|QueryCacheStats
argument_list|()
decl_stmt|;
name|QueryCacheStats
name|info
init|=
name|stats
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|QueryCacheStats
argument_list|()
expr_stmt|;
block|}
name|shardStats
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// We also have some shared ram usage that we try to distribute to
comment|// proportionally to their number of cache entries of each shard
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QueryCacheStats
name|s
range|:
name|stats
operator|.
name|values
argument_list|()
control|)
block|{
name|totalSize
operator|+=
name|s
operator|.
name|getCacheSize
argument_list|()
expr_stmt|;
block|}
specifier|final
name|double
name|weight
init|=
name|totalSize
operator|==
literal|0
condition|?
literal|1d
operator|/
name|stats
operator|.
name|size
argument_list|()
else|:
name|shardStats
operator|.
name|getCacheSize
argument_list|()
operator|/
name|totalSize
decl_stmt|;
specifier|final
name|long
name|additionalRamBytesUsed
init|=
name|Math
operator|.
name|round
argument_list|(
name|weight
operator|*
name|sharedRamBytesUsed
argument_list|)
decl_stmt|;
name|shardStats
operator|.
name|add
argument_list|(
operator|new
name|QueryCacheStats
argument_list|(
name|additionalRamBytesUsed
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|shardStats
return|;
block|}
annotation|@
name|Override
DECL|method|doCache
specifier|public
name|Weight
name|doCache
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|QueryCachingPolicy
name|policy
parameter_list|)
block|{
while|while
condition|(
name|weight
operator|instanceof
name|CachingWeightWrapper
condition|)
block|{
name|weight
operator|=
operator|(
operator|(
name|CachingWeightWrapper
operator|)
name|weight
operator|)
operator|.
name|in
expr_stmt|;
block|}
specifier|final
name|Weight
name|in
init|=
name|cache
operator|.
name|doCache
argument_list|(
name|weight
argument_list|,
name|policy
argument_list|)
decl_stmt|;
comment|// We wrap the weight to track the readers it sees and map them with
comment|// the shards they belong to
return|return
operator|new
name|CachingWeightWrapper
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|class|CachingWeightWrapper
specifier|private
class|class
name|CachingWeightWrapper
extends|extends
name|Weight
block|{
DECL|field|in
specifier|private
specifier|final
name|Weight
name|in
decl_stmt|;
DECL|method|CachingWeightWrapper
specifier|protected
name|CachingWeightWrapper
parameter_list|(
name|Weight
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|in
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|shardKeyMap
operator|.
name|add
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|in
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|shardKeyMap
operator|.
name|add
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|shardKeyMap
operator|.
name|add
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
comment|/** Clear all entries that belong to the given index. */
DECL|method|clearIndex
specifier|public
name|void
name|clearIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Object
argument_list|>
name|coreCacheKeys
init|=
name|shardKeyMap
operator|.
name|getCoreKeysForIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|coreKey
range|:
name|coreCacheKeys
control|)
block|{
name|cache
operator|.
name|clearCoreCacheKey
argument_list|(
name|coreKey
argument_list|)
expr_stmt|;
block|}
comment|// This cache stores two things: filters, and doc id sets. Calling
comment|// clear only removes the doc id sets, but if we reach the situation
comment|// that the cache does not contain any DocIdSet anymore, then it
comment|// probably means that the user wanted to remove everything.
if|if
condition|(
name|cache
operator|.
name|getCacheSize
argument_list|()
operator|==
literal|0
condition|)
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
assert|assert
name|shardKeyMap
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|:
name|shardKeyMap
operator|.
name|size
argument_list|()
assert|;
assert|assert
name|shardStats
operator|.
name|isEmpty
argument_list|()
operator|:
name|shardStats
operator|.
name|keySet
argument_list|()
assert|;
assert|assert
name|stats2
operator|.
name|isEmpty
argument_list|()
operator|:
name|stats2
assert|;
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|class|Stats
specifier|private
specifier|static
class|class
name|Stats
implements|implements
name|Cloneable
block|{
DECL|field|ramBytesUsed
specifier|volatile
name|long
name|ramBytesUsed
decl_stmt|;
DECL|field|hitCount
specifier|volatile
name|long
name|hitCount
decl_stmt|;
DECL|field|missCount
specifier|volatile
name|long
name|missCount
decl_stmt|;
DECL|field|cacheCount
specifier|volatile
name|long
name|cacheCount
decl_stmt|;
DECL|field|cacheSize
specifier|volatile
name|long
name|cacheSize
decl_stmt|;
DECL|method|toQueryCacheStats
name|QueryCacheStats
name|toQueryCacheStats
parameter_list|()
block|{
return|return
operator|new
name|QueryCacheStats
argument_list|(
name|ramBytesUsed
argument_list|,
name|hitCount
argument_list|,
name|missCount
argument_list|,
name|cacheCount
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
block|}
DECL|class|StatsAndCount
specifier|private
specifier|static
class|class
name|StatsAndCount
block|{
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|stats
specifier|final
name|Stats
name|stats
decl_stmt|;
DECL|method|StatsAndCount
name|StatsAndCount
parameter_list|(
name|Stats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|empty
specifier|private
name|boolean
name|empty
parameter_list|(
name|Stats
name|stats
parameter_list|)
block|{
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|stats
operator|.
name|cacheSize
operator|==
literal|0
operator|&&
name|stats
operator|.
name|ramBytesUsed
operator|==
literal|0
return|;
block|}
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
assert|assert
name|empty
argument_list|(
name|shardStats
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
argument_list|)
assert|;
name|shardStats
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

