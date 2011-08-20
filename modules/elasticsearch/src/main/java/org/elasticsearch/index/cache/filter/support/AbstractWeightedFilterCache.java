begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.filter.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|filter
operator|.
name|support
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
name|IndexReader
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
name|DocIdSet
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
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|RamUsage
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
name|concurrentlinkedhashmap
operator|.
name|EvictionListener
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
name|concurrentlinkedhashmap
operator|.
name|Weigher
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
name|lab
operator|.
name|LongsLAB
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
name|docset
operator|.
name|DocSet
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
name|search
operator|.
name|NoCacheFilter
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
name|metrics
operator|.
name|CounterMetric
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
name|metrics
operator|.
name|MeanMetric
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
name|concurrent
operator|.
name|ConcurrentCollections
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
name|AbstractIndexComponent
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
name|Index
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
name|filter
operator|.
name|FilterCache
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
name|settings
operator|.
name|IndexSettings
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_class
DECL|class|AbstractWeightedFilterCache
specifier|public
specifier|abstract
class|class
name|AbstractWeightedFilterCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|FilterCache
implements|,
name|IndexReader
operator|.
name|ReaderFinishedListener
implements|,
name|EvictionListener
argument_list|<
name|AbstractWeightedFilterCache
operator|.
name|FilterCacheKey
argument_list|,
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|>
block|{
DECL|field|seenReaders
specifier|final
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|Boolean
argument_list|>
name|seenReaders
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|seenReadersCount
specifier|final
name|CounterMetric
name|seenReadersCount
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|labEnabled
specifier|final
name|boolean
name|labEnabled
decl_stmt|;
DECL|field|labMaxAlloc
specifier|final
name|ByteSizeValue
name|labMaxAlloc
decl_stmt|;
DECL|field|labChunkSize
specifier|final
name|ByteSizeValue
name|labChunkSize
decl_stmt|;
DECL|field|labMaxAllocBytes
specifier|final
name|int
name|labMaxAllocBytes
decl_stmt|;
DECL|field|labChunkSizeBytes
specifier|final
name|int
name|labChunkSizeBytes
decl_stmt|;
DECL|field|evictionsMetric
specifier|final
name|CounterMetric
name|evictionsMetric
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|totalMetric
specifier|final
name|MeanMetric
name|totalMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|method|AbstractWeightedFilterCache
specifier|protected
name|AbstractWeightedFilterCache
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
comment|// The LAB is stored per reader, so whole chunks will be cleared once reader is discarded.
comment|// This means that with filter entry specific based eviction, like access time
comment|// we might get into cases where the LAB is held by a puny filter and other filters have been released.
comment|// This usually will not be that bad, compared to the GC benefit of using a LAB, but, that is why
comment|// the soft filter cache is recommended.
name|this
operator|.
name|labEnabled
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"lab"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// These values should not be too high, basically we want to cached the small readers and use the LAB for
comment|// them, 1M docs on OpenBitSet is around 110kb.
name|this
operator|.
name|labMaxAlloc
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"lab.max_alloc"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|128
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|labChunkSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"lab.chunk_size"
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
expr_stmt|;
name|this
operator|.
name|labMaxAllocBytes
operator|=
call|(
name|int
call|)
argument_list|(
name|labMaxAlloc
operator|.
name|bytes
argument_list|()
operator|/
name|RamUsage
operator|.
name|NUM_BYTES_LONG
argument_list|)
expr_stmt|;
name|this
operator|.
name|labChunkSizeBytes
operator|=
call|(
name|int
call|)
argument_list|(
name|labChunkSize
operator|.
name|bytes
argument_list|()
operator|/
name|RamUsage
operator|.
name|NUM_BYTES_LONG
argument_list|)
expr_stmt|;
block|}
DECL|method|cache
specifier|protected
specifier|abstract
name|ConcurrentMap
argument_list|<
name|FilterCacheKey
argument_list|,
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|>
name|cache
parameter_list|()
function_decl|;
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|clear
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|Object
name|readerKey
range|:
name|seenReaders
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Boolean
name|removed
init|=
name|seenReaders
operator|.
name|remove
argument_list|(
name|readerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|seenReadersCount
operator|.
name|dec
argument_list|()
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|FilterCacheKey
argument_list|,
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|>
name|cache
init|=
name|cache
argument_list|()
decl_stmt|;
for|for
control|(
name|FilterCacheKey
name|key
range|:
name|cache
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|readerKey
argument_list|()
operator|==
name|readerKey
condition|)
block|{
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
name|removed2
init|=
name|cache
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed2
operator|!=
literal|null
condition|)
block|{
name|totalMetric
operator|.
name|dec
argument_list|(
name|removed2
operator|.
name|value
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|finished
annotation|@
name|Override
specifier|public
name|void
name|finished
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|clear
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
comment|// we add the seen reader before we add the first cache entry for this reader
comment|// so, if we don't see it here, its won't be in the cache
name|Boolean
name|removed
init|=
name|seenReaders
operator|.
name|remove
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|seenReadersCount
operator|.
name|dec
argument_list|()
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|FilterCacheKey
argument_list|,
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|>
name|cache
init|=
name|cache
argument_list|()
decl_stmt|;
for|for
control|(
name|FilterCacheKey
name|key
range|:
name|cache
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|readerKey
argument_list|()
operator|==
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
condition|)
block|{
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
name|removed2
init|=
name|cache
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed2
operator|!=
literal|null
condition|)
block|{
name|totalMetric
operator|.
name|dec
argument_list|(
name|removed2
operator|.
name|value
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|entriesStats
annotation|@
name|Override
specifier|public
name|EntriesStats
name|entriesStats
parameter_list|()
block|{
name|long
name|seenReadersCount
init|=
name|this
operator|.
name|seenReadersCount
operator|.
name|count
argument_list|()
decl_stmt|;
return|return
operator|new
name|EntriesStats
argument_list|(
name|totalMetric
operator|.
name|sum
argument_list|()
argument_list|,
name|seenReadersCount
operator|==
literal|0
condition|?
literal|0
else|:
name|totalMetric
operator|.
name|count
argument_list|()
operator|/
name|seenReadersCount
argument_list|)
return|;
block|}
DECL|method|evictions
annotation|@
name|Override
specifier|public
name|long
name|evictions
parameter_list|()
block|{
return|return
name|evictionsMetric
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|cache
annotation|@
name|Override
specifier|public
name|Filter
name|cache
parameter_list|(
name|Filter
name|filterToCache
parameter_list|)
block|{
if|if
condition|(
name|filterToCache
operator|instanceof
name|NoCacheFilter
condition|)
block|{
return|return
name|filterToCache
return|;
block|}
if|if
condition|(
name|isCached
argument_list|(
name|filterToCache
argument_list|)
condition|)
block|{
return|return
name|filterToCache
return|;
block|}
return|return
operator|new
name|FilterCacheFilterWrapper
argument_list|(
name|filterToCache
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|isCached
annotation|@
name|Override
specifier|public
name|boolean
name|isCached
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|filter
operator|instanceof
name|FilterCacheFilterWrapper
return|;
block|}
DECL|class|FilterCacheFilterWrapper
specifier|static
class|class
name|FilterCacheFilterWrapper
extends|extends
name|Filter
block|{
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|AbstractWeightedFilterCache
name|cache
decl_stmt|;
DECL|method|FilterCacheFilterWrapper
name|FilterCacheFilterWrapper
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|AbstractWeightedFilterCache
name|cache
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
DECL|method|getDocIdSet
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|filterKey
init|=
name|filter
decl_stmt|;
if|if
condition|(
name|filter
operator|instanceof
name|CacheKeyFilter
condition|)
block|{
name|filterKey
operator|=
operator|(
operator|(
name|CacheKeyFilter
operator|)
name|filter
operator|)
operator|.
name|cacheKey
argument_list|()
expr_stmt|;
block|}
name|FilterCacheKey
name|cacheKey
init|=
operator|new
name|FilterCacheKey
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|filterKey
argument_list|)
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|FilterCacheKey
argument_list|,
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|>
name|innerCache
init|=
name|cache
operator|.
name|cache
argument_list|()
decl_stmt|;
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
name|cacheValue
init|=
name|innerCache
operator|.
name|get
argument_list|(
name|cacheKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|cache
operator|.
name|seenReaders
operator|.
name|containsKey
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
condition|)
block|{
name|Boolean
name|previous
init|=
name|cache
operator|.
name|seenReaders
operator|.
name|putIfAbsent
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|reader
operator|.
name|addReaderFinishedListener
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|cache
operator|.
name|seenReadersCount
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
block|}
name|LongsLAB
name|longsLAB
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cache
operator|.
name|labEnabled
condition|)
block|{
name|longsLAB
operator|=
operator|new
name|LongsLAB
argument_list|(
name|cache
operator|.
name|labChunkSizeBytes
argument_list|,
name|cache
operator|.
name|labMaxAllocBytes
argument_list|)
expr_stmt|;
block|}
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|DocSet
name|docSet
init|=
name|FilterCacheValue
operator|.
name|cacheable
argument_list|(
name|reader
argument_list|,
name|longsLAB
argument_list|,
name|docIdSet
argument_list|)
decl_stmt|;
name|cacheValue
operator|=
operator|new
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|(
name|docSet
argument_list|,
name|longsLAB
argument_list|)
expr_stmt|;
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
name|previous
init|=
name|innerCache
operator|.
name|putIfAbsent
argument_list|(
name|cacheKey
argument_list|,
name|cacheValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|cache
operator|.
name|totalMetric
operator|.
name|inc
argument_list|(
name|cacheValue
operator|.
name|value
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|cacheValue
operator|.
name|value
argument_list|()
operator|==
name|DocSet
operator|.
name|EMPTY_DOC_SET
condition|?
literal|null
else|:
name|cacheValue
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FilterCacheFilterWrapper("
operator|+
name|filter
operator|+
literal|")"
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FilterCacheFilterWrapper
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|FilterCacheFilterWrapper
operator|)
name|o
operator|)
operator|.
name|filter
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|filter
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x1117BF25
return|;
block|}
block|}
comment|// factored by 10
DECL|class|FilterCacheValueWeigher
specifier|public
specifier|static
class|class
name|FilterCacheValueWeigher
implements|implements
name|Weigher
argument_list|<
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
argument_list|>
block|{
DECL|field|FACTOR
specifier|public
specifier|static
specifier|final
name|long
name|FACTOR
init|=
literal|10l
decl_stmt|;
DECL|method|weightOf
annotation|@
name|Override
specifier|public
name|int
name|weightOf
parameter_list|(
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
name|value
parameter_list|)
block|{
name|int
name|weight
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|value
operator|.
name|value
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
operator|/
literal|10
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
return|return
name|weight
operator|==
literal|0
condition|?
literal|1
else|:
name|weight
return|;
block|}
block|}
DECL|method|onEviction
annotation|@
name|Override
specifier|public
name|void
name|onEviction
parameter_list|(
name|FilterCacheKey
name|filterCacheKey
parameter_list|,
name|FilterCacheValue
argument_list|<
name|DocSet
argument_list|>
name|docSetFilterCacheValue
parameter_list|)
block|{
if|if
condition|(
name|filterCacheKey
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|seenReaders
operator|.
name|containsKey
argument_list|(
name|filterCacheKey
operator|.
name|readerKey
argument_list|()
argument_list|)
condition|)
block|{
name|evictionsMetric
operator|.
name|inc
argument_list|()
expr_stmt|;
if|if
condition|(
name|docSetFilterCacheValue
operator|!=
literal|null
condition|)
block|{
name|totalMetric
operator|.
name|dec
argument_list|(
name|docSetFilterCacheValue
operator|.
name|value
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|FilterCacheKey
specifier|public
specifier|static
class|class
name|FilterCacheKey
block|{
DECL|field|readerKey
specifier|private
specifier|final
name|Object
name|readerKey
decl_stmt|;
DECL|field|filterKey
specifier|private
specifier|final
name|Object
name|filterKey
decl_stmt|;
DECL|method|FilterCacheKey
specifier|public
name|FilterCacheKey
parameter_list|(
name|Object
name|readerKey
parameter_list|,
name|Object
name|filterKey
parameter_list|)
block|{
name|this
operator|.
name|readerKey
operator|=
name|readerKey
expr_stmt|;
name|this
operator|.
name|filterKey
operator|=
name|filterKey
expr_stmt|;
block|}
DECL|method|readerKey
specifier|public
name|Object
name|readerKey
parameter_list|()
block|{
return|return
name|readerKey
return|;
block|}
DECL|method|filterKey
specifier|public
name|Object
name|filterKey
parameter_list|()
block|{
return|return
name|filterKey
return|;
block|}
DECL|method|equals
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
comment|//            if (o == null || getClass() != o.getClass()) return false;
name|FilterCacheKey
name|that
init|=
operator|(
name|FilterCacheKey
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|readerKey
operator|==
name|that
operator|.
name|readerKey
operator|&&
name|filterKey
operator|.
name|equals
argument_list|(
name|that
operator|.
name|filterKey
argument_list|)
operator|)
return|;
block|}
DECL|method|hashCode
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|readerKey
operator|.
name|hashCode
argument_list|()
operator|+
literal|31
operator|*
name|filterKey
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

