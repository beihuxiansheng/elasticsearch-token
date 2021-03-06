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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|index
operator|.
name|IndexReaderContext
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
name|index
operator|.
name|ReaderUtil
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
name|IndexSearcher
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
operator|.
name|BitSetProducer
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
name|Accountable
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
name|BitSet
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
name|common
operator|.
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|RemovalListener
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
name|cache
operator|.
name|RemovalNotification
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
name|Property
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
name|IndexWarmer
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
name|IndexWarmer
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
name|index
operator|.
name|engine
operator|.
name|Engine
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
name|Objects
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
comment|/**  * This is a cache for {@link BitDocIdSet} based filters and is unbounded by size or time.  *<p>  * Use this cache with care, only components that require that a filter is to be materialized as a {@link BitDocIdSet}  * and require that it should always be around should use this cache, otherwise the  * {@link org.elasticsearch.index.cache.query.QueryCache} should be used instead.  */
end_comment

begin_class
DECL|class|BitsetFilterCache
specifier|public
specifier|final
class|class
name|BitsetFilterCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexReader
operator|.
name|ClosedListener
implements|,
name|RemovalListener
argument_list|<
name|IndexReader
operator|.
name|CacheKey
argument_list|,
name|Cache
argument_list|<
name|Query
argument_list|,
name|BitsetFilterCache
operator|.
name|Value
argument_list|>
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|INDEX_LOAD_RANDOM_ACCESS_FILTERS_EAGERLY_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|INDEX_LOAD_RANDOM_ACCESS_FILTERS_EAGERLY_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.load_fixed_bitset_filters_eagerly"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
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
name|IndexReader
operator|.
name|CacheKey
argument_list|,
name|Cache
argument_list|<
name|Query
argument_list|,
name|Value
argument_list|>
argument_list|>
name|loadedFilters
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|Listener
name|listener
decl_stmt|;
DECL|method|BitsetFilterCache
specifier|public
name|BitsetFilterCache
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|loadRandomAccessFiltersEagerly
operator|=
name|this
operator|.
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_LOAD_RANDOM_ACCESS_FILTERS_EAGERLY_SETTING
argument_list|)
expr_stmt|;
name|this
operator|.
name|loadedFilters
operator|=
name|CacheBuilder
operator|.
expr|<
name|IndexReader
operator|.
name|CacheKey
operator|,
name|Cache
argument_list|<
name|Query
argument_list|,
name|Value
argument_list|>
operator|>
name|builder
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
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|createListener
specifier|public
name|IndexWarmer
operator|.
name|Listener
name|createListener
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
return|return
operator|new
name|BitSetProducerWarmer
argument_list|(
name|threadPool
argument_list|)
return|;
block|}
DECL|method|getBitSetProducer
specifier|public
name|BitSetProducer
name|getBitSetProducer
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
return|return
operator|new
name|QueryWrapperBitSetProducer
argument_list|(
name|query
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
name|IndexReader
operator|.
name|CacheKey
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
name|BitSet
name|getAndLoadIfNotPresent
parameter_list|(
specifier|final
name|Query
name|query
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
name|IndexReader
operator|.
name|CacheHelper
name|cacheHelper
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheHelper
argument_list|()
decl_stmt|;
if|if
condition|(
name|cacheHelper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Reader "
operator|+
name|context
operator|.
name|reader
argument_list|()
operator|+
literal|" does not support caching"
argument_list|)
throw|;
block|}
specifier|final
name|IndexReader
operator|.
name|CacheKey
name|coreCacheReader
init|=
name|cacheHelper
operator|.
name|getKey
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
if|if
condition|(
name|indexSettings
operator|.
name|getIndex
argument_list|()
operator|.
name|equals
argument_list|(
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// insanity
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to load bit set for index "
operator|+
name|shardId
operator|.
name|getIndex
argument_list|()
operator|+
literal|" with cache of index "
operator|+
name|indexSettings
operator|.
name|getIndex
argument_list|()
argument_list|)
throw|;
block|}
name|Cache
argument_list|<
name|Query
argument_list|,
name|Value
argument_list|>
name|filterToFbs
init|=
name|loadedFilters
operator|.
name|computeIfAbsent
argument_list|(
name|coreCacheReader
argument_list|,
name|key
lambda|->
block|{
name|cacheHelper
operator|.
name|addClosedListener
argument_list|(
name|BitsetFilterCache
operator|.
name|this
argument_list|)
expr_stmt|;
return|return
name|CacheBuilder
operator|.
expr|<
name|Query
operator|,
name|Value
operator|>
name|builder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
argument_list|)
decl_stmt|;
return|return
name|filterToFbs
operator|.
name|computeIfAbsent
argument_list|(
name|query
argument_list|,
name|key
lambda|->
block|{
specifier|final
name|IndexReaderContext
name|topLevelContext
init|=
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|topLevelContext
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Scorer
name|s
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|bitSet
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|bitSet
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|bitSet
operator|=
name|BitSet
operator|.
name|of
argument_list|(
name|s
operator|.
name|iterator
argument_list|()
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
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
name|listener
operator|.
name|onCache
argument_list|(
name|shardId
argument_list|,
name|value
operator|.
name|bitset
argument_list|)
expr_stmt|;
return|return
name|value
return|;
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
name|IndexReader
operator|.
name|CacheKey
argument_list|,
name|Cache
argument_list|<
name|Query
argument_list|,
name|Value
argument_list|>
argument_list|>
name|notification
parameter_list|)
block|{
if|if
condition|(
name|notification
operator|.
name|getKey
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Cache
argument_list|<
name|Query
argument_list|,
name|Value
argument_list|>
name|valueCache
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|valueCache
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Value
name|value
range|:
name|valueCache
operator|.
name|values
argument_list|()
control|)
block|{
name|listener
operator|.
name|onRemoval
argument_list|(
name|value
operator|.
name|shardId
argument_list|,
name|value
operator|.
name|bitset
argument_list|)
expr_stmt|;
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
name|BitSet
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
name|BitSet
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
DECL|class|QueryWrapperBitSetProducer
specifier|final
class|class
name|QueryWrapperBitSetProducer
implements|implements
name|BitSetProducer
block|{
DECL|field|query
specifier|final
name|Query
name|query
decl_stmt|;
DECL|method|QueryWrapperBitSetProducer
name|QueryWrapperBitSetProducer
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBitSet
specifier|public
name|BitSet
name|getBitSet
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
name|query
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
parameter_list|()
block|{
return|return
literal|"random_access("
operator|+
name|query
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
name|QueryWrapperBitSetProducer
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|QueryWrapperBitSetProducer
operator|)
name|o
operator|)
operator|.
name|query
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
literal|31
operator|*
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|class|BitSetProducerWarmer
specifier|final
class|class
name|BitSetProducerWarmer
implements|implements
name|IndexWarmer
operator|.
name|Listener
block|{
DECL|field|executor
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
DECL|method|BitSetProducerWarmer
name|BitSetProducerWarmer
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|threadPool
operator|.
name|executor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|WARMER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|warmReader
specifier|public
name|IndexWarmer
operator|.
name|TerminationHandle
name|warmReader
parameter_list|(
specifier|final
name|IndexShard
name|indexShard
parameter_list|,
specifier|final
name|Engine
operator|.
name|Searcher
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|indexSettings
operator|.
name|getIndex
argument_list|()
operator|.
name|equals
argument_list|(
name|indexShard
operator|.
name|indexSettings
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// this is from a different index
return|return
name|TerminationHandle
operator|.
name|NO_WAIT
return|;
block|}
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
name|Query
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
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|searcher
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
name|searcher
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
name|Query
name|filterToWarm
range|:
name|warmUp
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
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
name|Exception
name|e
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
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"failed to load bitset for [{}]"
argument_list|,
name|filterToWarm
argument_list|)
argument_list|,
name|e
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
argument_list|)
expr_stmt|;
block|}
block|}
return|return
parameter_list|()
lambda|->
name|latch
operator|.
name|await
argument_list|()
return|;
block|}
block|}
DECL|method|getLoadedFilters
name|Cache
argument_list|<
name|IndexReader
operator|.
name|CacheKey
argument_list|,
name|Cache
argument_list|<
name|Query
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
comment|/**      *  A listener interface that is executed for each onCache / onRemoval event      */
DECL|interface|Listener
specifier|public
interface|interface
name|Listener
block|{
comment|/**          * Called for each cached bitset on the cache event.          * @param shardId the shard id the bitset was cached for. This can be<code>null</code>          * @param accountable the bitsets ram representation          */
DECL|method|onCache
name|void
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
function_decl|;
comment|/**          * Called for each cached bitset on the removal event.          * @param shardId the shard id the bitset was cached for. This can be<code>null</code>          * @param accountable the bitsets ram representation          */
DECL|method|onRemoval
name|void
name|onRemoval
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

