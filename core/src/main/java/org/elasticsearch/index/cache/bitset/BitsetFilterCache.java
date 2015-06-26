begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.bitset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|bitset
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
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalNotification
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
name|LeafReader
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
name|DocIdSetIterator
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
operator|.
name|BitDocIdSetFilter
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
name|BitDocIdSet
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
name|SparseFixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|inject
operator|.
name|Inject
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
name|Queries
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
name|TimeValue
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
name|IndexService
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
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|MapperService
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|IndexShard
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesWarmer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesWarmer
operator|.
name|TerminationHandle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|HashSet
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
name|Callable
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
name|CountDownLatch
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
name|ExecutionException
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
name|Executor
import|;
end_import

begin_comment
comment|/**  * This is a cache for {@link BitDocIdSet} based filters and is unbounded by size or time.  *<p/>  * Use this cache with care, only components that require that a filter is to be materialized as a {@link BitDocIdSet}  * and require that it should always be around should use this cache, otherwise the  * {@link org.elasticsearch.index.cache.query.QueryCache} should be used instead.  */
end_comment

begin_class
DECL|class|BitsetFilterCache
specifier|public
class|class
name|BitsetFilterCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|LeafReader
operator|.
name|CoreClosedListener
implements|,
name|RemovalListener
argument_list|<
name|Object
argument_list|,
name|Cache
argument_list|<
name|Filter
argument_list|,
name|BitsetFilterCache
operator|.
name|Value
argument_list|>
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|LOAD_RANDOM_ACCESS_FILTERS_EAGERLY
specifier|public
specifier|static
specifier|final
name|String
name|LOAD_RANDOM_ACCESS_FILTERS_EAGERLY
init|=
literal|"index.load_fixed_bitset_filters_eagerly"
decl_stmt|;
DECL|field|loadRandomAccessFiltersEagerly
specifier|private
specifier|final
name|boolean
name|loadRandomAccessFiltersEagerly
decl_stmt|;
DECL|field|loadedFilters
specifier|private
specifier|final
name|Cache
argument_list|<
name|Object
argument_list|,
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
argument_list|>
name|loadedFilters
decl_stmt|;
DECL|field|warmer
specifier|private
specifier|final
name|BitDocIdSetFilterWarmer
name|warmer
decl_stmt|;
DECL|field|indexService
specifier|private
name|IndexService
name|indexService
decl_stmt|;
DECL|field|indicesWarmer
specifier|private
name|IndicesWarmer
name|indicesWarmer
decl_stmt|;
annotation|@
name|Inject
DECL|method|BitsetFilterCache
specifier|public
name|BitsetFilterCache
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
name|this
operator|.
name|loadRandomAccessFiltersEagerly
operator|=
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
name|LOAD_RANDOM_ACCESS_FILTERS_EAGERLY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|loadedFilters
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|removalListener
argument_list|(
name|this
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|warmer
operator|=
operator|new
name|BitDocIdSetFilterWarmer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Inject
argument_list|(
name|optional
operator|=
literal|true
argument_list|)
DECL|method|setIndicesWarmer
specifier|public
name|void
name|setIndicesWarmer
parameter_list|(
name|IndicesWarmer
name|indicesWarmer
parameter_list|)
block|{
name|this
operator|.
name|indicesWarmer
operator|=
name|indicesWarmer
expr_stmt|;
block|}
DECL|method|setIndexService
specifier|public
name|void
name|setIndexService
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{
name|this
operator|.
name|indexService
operator|=
name|indexService
expr_stmt|;
comment|// First the indicesWarmer is set and then the indexService is set, because of this there is a small window of
comment|// time where indexService is null. This is why the warmer should only registered after indexService has been set.
comment|// Otherwise there is a small chance of the warmer running into a NPE, since it uses the indexService
name|indicesWarmer
operator|.
name|addListener
argument_list|(
name|warmer
argument_list|)
expr_stmt|;
block|}
DECL|method|getBitDocIdSetFilter
specifier|public
name|BitDocIdSetFilter
name|getBitDocIdSetFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
assert|assert
name|filter
operator|!=
literal|null
assert|;
return|return
operator|new
name|BitDocIdSetFilterWrapper
argument_list|(
name|filter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|Object
name|ownerCoreCacheKey
parameter_list|)
block|{
name|loadedFilters
operator|.
name|invalidate
argument_list|(
name|ownerCoreCacheKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|indicesWarmer
operator|.
name|removeListener
argument_list|(
name|warmer
argument_list|)
expr_stmt|;
name|clear
argument_list|(
literal|"close"
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"clearing all bitsets because [{}]"
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|loadedFilters
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getAndLoadIfNotPresent
specifier|private
name|BitDocIdSet
name|getAndLoadIfNotPresent
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExecutionException
block|{
specifier|final
name|Object
name|coreCacheReader
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|ShardUtils
operator|.
name|extractShardId
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
name|filterToFbs
init|=
name|loadedFilters
operator|.
name|get
argument_list|(
name|coreCacheReader
argument_list|,
operator|new
name|Callable
argument_list|<
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|reader
argument_list|()
operator|.
name|addCoreClosedListener
argument_list|(
name|BitsetFilterCache
operator|.
name|this
argument_list|)
expr_stmt|;
return|return
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|filterToFbs
operator|.
name|get
argument_list|(
name|filter
argument_list|,
operator|new
name|Callable
argument_list|<
name|Value
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Value
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|BitDocIdSet
name|bitSet
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|instanceof
name|BitDocIdSet
condition|)
block|{
name|bitSet
operator|=
operator|(
name|BitDocIdSet
operator|)
name|docIdSet
expr_stmt|;
block|}
else|else
block|{
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|!=
literal|null
operator|&&
name|docIdSet
operator|!=
name|DocIdSet
operator|.
name|EMPTY
condition|)
block|{
name|DocIdSetIterator
name|iterator
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// some filters (QueryWrapperFilter) return not null or DocIdSet.EMPTY if there no matching docs
if|if
condition|(
name|iterator
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|or
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
block|}
name|BitDocIdSet
name|bits
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// code expects this to be non-null
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
name|bits
operator|=
operator|new
name|BitDocIdSet
argument_list|(
operator|new
name|SparseFixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|bitSet
operator|=
name|bits
expr_stmt|;
block|}
name|Value
name|value
init|=
operator|new
name|Value
argument_list|(
name|bitSet
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardId
operator|!=
literal|null
condition|)
block|{
name|IndexShard
name|shard
init|=
name|indexService
operator|.
name|shard
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|shard
operator|.
name|shardBitsetFilterCache
argument_list|()
operator|.
name|onCached
argument_list|(
name|value
operator|.
name|bitset
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|value
return|;
block|}
block|}
argument_list|)
operator|.
name|bitset
return|;
block|}
annotation|@
name|Override
DECL|method|onRemoval
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|Object
argument_list|,
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
argument_list|>
name|notification
parameter_list|)
block|{
name|Object
name|key
init|=
name|notification
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
name|value
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
name|entry
range|:
name|value
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|shardId
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|IndexShard
name|shard
init|=
name|indexService
operator|.
name|shard
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|ShardBitsetFilterCache
name|shardBitsetFilterCache
init|=
name|shard
operator|.
name|shardBitsetFilterCache
argument_list|()
decl_stmt|;
name|shardBitsetFilterCache
operator|.
name|onRemoval
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|bitset
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// if null then this means the shard has already been removed and the stats are 0 anyway for the shard this key belongs to
block|}
block|}
DECL|class|Value
specifier|public
specifier|static
specifier|final
class|class
name|Value
block|{
DECL|field|bitset
specifier|final
name|BitDocIdSet
name|bitset
decl_stmt|;
DECL|field|shardId
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|method|Value
specifier|public
name|Value
parameter_list|(
name|BitDocIdSet
name|bitset
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|bitset
operator|=
name|bitset
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
block|}
DECL|class|BitDocIdSetFilterWrapper
specifier|final
class|class
name|BitDocIdSetFilterWrapper
extends|extends
name|BitDocIdSetFilter
block|{
DECL|field|filter
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|method|BitDocIdSetFilterWrapper
name|BitDocIdSetFilterWrapper
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|BitDocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|getAndLoadIfNotPresent
argument_list|(
name|filter
argument_list|,
name|context
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"random_access("
operator|+
name|filter
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
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
name|BitDocIdSetFilterWrapper
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
name|BitDocIdSetFilterWrapper
operator|)
name|o
operator|)
operator|.
name|filter
argument_list|)
return|;
block|}
annotation|@
name|Override
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
literal|0x1117BF26
return|;
block|}
block|}
DECL|class|BitDocIdSetFilterWarmer
specifier|final
class|class
name|BitDocIdSetFilterWarmer
extends|extends
name|IndicesWarmer
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|warmNewReaders
specifier|public
name|IndicesWarmer
operator|.
name|TerminationHandle
name|warmNewReaders
parameter_list|(
specifier|final
name|IndexShard
name|indexShard
parameter_list|,
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|IndicesWarmer
operator|.
name|WarmerContext
name|context
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loadRandomAccessFiltersEagerly
condition|)
block|{
return|return
name|TerminationHandle
operator|.
name|NO_WAIT
return|;
block|}
name|boolean
name|hasNested
init|=
literal|false
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Filter
argument_list|>
name|warmUp
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|MapperService
name|mapperService
init|=
name|indexShard
operator|.
name|mapperService
argument_list|()
decl_stmt|;
for|for
control|(
name|DocumentMapper
name|docMapper
range|:
name|mapperService
operator|.
name|docMappers
argument_list|(
literal|false
argument_list|)
control|)
block|{
if|if
condition|(
name|docMapper
operator|.
name|hasNestedObjects
argument_list|()
condition|)
block|{
name|hasNested
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|ObjectMapper
name|objectMapper
range|:
name|docMapper
operator|.
name|objectMappers
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
name|ObjectMapper
name|parentObjectMapper
init|=
name|docMapper
operator|.
name|findParentObjectMapper
argument_list|(
name|objectMapper
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentObjectMapper
operator|!=
literal|null
operator|&&
name|parentObjectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
name|warmUp
operator|.
name|add
argument_list|(
name|parentObjectMapper
operator|.
name|nestedTypeFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|hasNested
condition|)
block|{
name|warmUp
operator|.
name|add
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Executor
name|executor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|reader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|*
name|warmUp
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|LeafReaderContext
name|ctx
range|:
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|reader
argument_list|()
operator|.
name|leaves
argument_list|()
control|)
block|{
for|for
control|(
specifier|final
name|Filter
name|filterToWarm
range|:
name|warmUp
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|getAndLoadIfNotPresent
argument_list|(
name|filterToWarm
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexShard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|indexShard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|trace
argument_list|(
literal|"warmed bitset for [{}], took [{}]"
argument_list|,
name|filterToWarm
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|indexShard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|warn
argument_list|(
literal|"failed to load bitset for [{}]"
argument_list|,
name|t
argument_list|,
name|filterToWarm
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|TerminationHandle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|awaitTermination
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|warmTopReader
specifier|public
name|TerminationHandle
name|warmTopReader
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|,
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|IndicesWarmer
operator|.
name|WarmerContext
name|context
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
return|return
name|TerminationHandle
operator|.
name|NO_WAIT
return|;
block|}
block|}
DECL|method|getLoadedFilters
name|Cache
argument_list|<
name|Object
argument_list|,
name|Cache
argument_list|<
name|Filter
argument_list|,
name|Value
argument_list|>
argument_list|>
name|getLoadedFilters
parameter_list|()
block|{
return|return
name|loadedFilters
return|;
block|}
block|}
end_class

end_unit

