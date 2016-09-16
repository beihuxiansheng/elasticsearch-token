begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.fielddata.cache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|cache
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
name|Logger
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
name|DirectoryReader
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
name|SegmentReader
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
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
name|lease
operator|.
name|Releasable
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
name|index
operator|.
name|ElasticsearchDirectoryReader
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
name|fielddata
operator|.
name|AtomicFieldData
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
name|IndexFieldData
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
name|IndexFieldDataCache
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
name|function
operator|.
name|ToLongBiFunction
import|;
end_import

begin_class
DECL|class|IndicesFieldDataCache
specifier|public
class|class
name|IndicesFieldDataCache
extends|extends
name|AbstractComponent
implements|implements
name|RemovalListener
argument_list|<
name|IndicesFieldDataCache
operator|.
name|Key
argument_list|,
name|Accountable
argument_list|>
implements|,
name|Releasable
block|{
DECL|field|INDICES_FIELDDATA_CACHE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|INDICES_FIELDDATA_CACHE_SIZE_KEY
init|=
name|Setting
operator|.
name|memorySizeSetting
argument_list|(
literal|"indices.fielddata.cache.size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|indicesFieldDataCacheListener
specifier|private
specifier|final
name|IndexFieldDataCache
operator|.
name|Listener
name|indicesFieldDataCacheListener
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|Key
argument_list|,
name|Accountable
argument_list|>
name|cache
decl_stmt|;
DECL|method|IndicesFieldDataCache
specifier|public
name|IndicesFieldDataCache
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndexFieldDataCache
operator|.
name|Listener
name|indicesFieldDataCacheListener
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesFieldDataCacheListener
operator|=
name|indicesFieldDataCacheListener
expr_stmt|;
specifier|final
name|long
name|sizeInBytes
init|=
name|INDICES_FIELDDATA_CACHE_SIZE_KEY
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|CacheBuilder
argument_list|<
name|Key
argument_list|,
name|Accountable
argument_list|>
name|cacheBuilder
init|=
name|CacheBuilder
operator|.
expr|<
name|Key
decl_stmt|,
name|Accountable
decl|>
name|builder
argument_list|()
decl|.
name|removalListener
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|sizeInBytes
operator|>
literal|0
condition|)
block|{
name|cacheBuilder
operator|.
name|setMaximumWeight
argument_list|(
name|sizeInBytes
argument_list|)
operator|.
name|weigher
argument_list|(
operator|new
name|FieldDataWeigher
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cache
operator|=
name|cacheBuilder
operator|.
name|build
argument_list|()
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
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
DECL|method|buildIndexFieldDataCache
specifier|public
name|IndexFieldDataCache
name|buildIndexFieldDataCache
parameter_list|(
name|IndexFieldDataCache
operator|.
name|Listener
name|listener
parameter_list|,
name|Index
name|index
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|IndexFieldCache
argument_list|(
name|logger
argument_list|,
name|cache
argument_list|,
name|index
argument_list|,
name|fieldName
argument_list|,
name|indicesFieldDataCacheListener
argument_list|,
name|listener
argument_list|)
return|;
block|}
DECL|method|getCache
specifier|public
name|Cache
argument_list|<
name|Key
argument_list|,
name|Accountable
argument_list|>
name|getCache
parameter_list|()
block|{
return|return
name|cache
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
name|Key
argument_list|,
name|Accountable
argument_list|>
name|notification
parameter_list|)
block|{
name|Key
name|key
init|=
name|notification
operator|.
name|getKey
argument_list|()
decl_stmt|;
assert|assert
name|key
operator|!=
literal|null
operator|&&
name|key
operator|.
name|listeners
operator|!=
literal|null
assert|;
name|IndexFieldCache
name|indexCache
init|=
name|key
operator|.
name|indexCache
decl_stmt|;
specifier|final
name|Accountable
name|value
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexFieldDataCache
operator|.
name|Listener
name|listener
range|:
name|key
operator|.
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onRemoval
argument_list|(
name|key
operator|.
name|shardId
argument_list|,
name|indexCache
operator|.
name|fieldName
argument_list|,
name|notification
operator|.
name|getRemovalReason
argument_list|()
operator|==
name|RemovalNotification
operator|.
name|RemovalReason
operator|.
name|EVICTED
argument_list|,
name|value
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// load anyway since listeners should not throw exceptions
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to call listener on field data cache unloading"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|FieldDataWeigher
specifier|public
specifier|static
class|class
name|FieldDataWeigher
implements|implements
name|ToLongBiFunction
argument_list|<
name|Key
argument_list|,
name|Accountable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|applyAsLong
specifier|public
name|long
name|applyAsLong
parameter_list|(
name|Key
name|key
parameter_list|,
name|Accountable
name|ramUsage
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
name|ramUsage
operator|.
name|ramBytesUsed
argument_list|()
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
comment|/**      * A specific cache instance for the relevant parameters of it (index, fieldNames, fieldType).      */
DECL|class|IndexFieldCache
specifier|static
class|class
name|IndexFieldCache
implements|implements
name|IndexFieldDataCache
implements|,
name|SegmentReader
operator|.
name|CoreClosedListener
implements|,
name|IndexReader
operator|.
name|ReaderClosedListener
block|{
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|index
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|fieldName
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|Key
argument_list|,
name|Accountable
argument_list|>
name|cache
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|Listener
index|[]
name|listeners
decl_stmt|;
DECL|method|IndexFieldCache
name|IndexFieldCache
parameter_list|(
name|Logger
name|logger
parameter_list|,
specifier|final
name|Cache
argument_list|<
name|Key
argument_list|,
name|Accountable
argument_list|>
name|cache
parameter_list|,
name|Index
name|index
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Listener
modifier|...
name|listeners
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|listeners
operator|=
name|listeners
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
parameter_list|<
name|FD
extends|extends
name|AtomicFieldData
parameter_list|,
name|IFD
extends|extends
name|IndexFieldData
argument_list|<
name|FD
argument_list|>
parameter_list|>
name|FD
name|load
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|IFD
name|indexFieldData
parameter_list|)
throws|throws
name|Exception
block|{
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
specifier|final
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|this
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
specifier|final
name|Accountable
name|accountable
init|=
name|cache
operator|.
name|computeIfAbsent
argument_list|(
name|key
argument_list|,
name|k
lambda|->
block|{
name|context
operator|.
name|reader
argument_list|()
operator|.
name|addCoreClosedListener
argument_list|(
name|IndexFieldCache
operator|.
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|Listener
name|listener
range|:
name|this
operator|.
name|listeners
control|)
block|{
name|k
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicFieldData
name|fieldData
init|=
name|indexFieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|Listener
name|listener
range|:
name|k
operator|.
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onCache
argument_list|(
name|shardId
argument_list|,
name|fieldName
argument_list|,
name|fieldData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// load anyway since listeners should not throw exceptions
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to call listener on atomic field data loading"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fieldData
return|;
block|}
argument_list|)
decl_stmt|;
return|return
operator|(
name|FD
operator|)
name|accountable
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
parameter_list|<
name|FD
extends|extends
name|AtomicFieldData
parameter_list|,
name|IFD
extends|extends
name|IndexFieldData
operator|.
name|Global
argument_list|<
name|FD
argument_list|>
parameter_list|>
name|IFD
name|load
parameter_list|(
specifier|final
name|DirectoryReader
name|indexReader
parameter_list|,
specifier|final
name|IFD
name|indexFieldData
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ShardId
name|shardId
init|=
name|ShardUtils
operator|.
name|extractShardId
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|this
argument_list|,
name|indexReader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
specifier|final
name|Accountable
name|accountable
init|=
name|cache
operator|.
name|computeIfAbsent
argument_list|(
name|key
argument_list|,
name|k
lambda|->
block|{
name|ElasticsearchDirectoryReader
operator|.
name|addReaderCloseListener
argument_list|(
name|indexReader
argument_list|,
name|IndexFieldCache
operator|.
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|Listener
name|listener
range|:
name|this
operator|.
name|listeners
control|)
block|{
name|k
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Accountable
name|ifd
init|=
operator|(
name|Accountable
operator|)
name|indexFieldData
operator|.
name|localGlobalDirect
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
for|for
control|(
name|Listener
name|listener
range|:
name|k
operator|.
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onCache
argument_list|(
name|shardId
argument_list|,
name|fieldName
argument_list|,
name|ifd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// load anyway since listeners should not throw exceptions
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to call listener on global ordinals loading"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ifd
return|;
block|}
argument_list|)
decl_stmt|;
return|return
operator|(
name|IFD
operator|)
name|accountable
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
name|coreKey
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
operator|new
name|Key
argument_list|(
name|this
argument_list|,
name|coreKey
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't call cache.cleanUp here as it would have bad performance implications
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
operator|new
name|Key
argument_list|(
name|this
argument_list|,
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't call cache.cleanUp here as it would have bad performance implications
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|Key
name|key
range|:
name|cache
operator|.
name|keys
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|indexCache
operator|.
name|index
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|// force eviction
name|cache
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
for|for
control|(
name|Key
name|key
range|:
name|cache
operator|.
name|keys
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|indexCache
operator|.
name|index
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
if|if
condition|(
name|key
operator|.
name|indexCache
operator|.
name|fieldName
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// we call refresh because this is a manual operation, should happen
comment|// rarely and probably means the user wants to see memory returned as
comment|// soon as possible
name|cache
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
block|{
DECL|field|indexCache
specifier|public
specifier|final
name|IndexFieldCache
name|indexCache
decl_stmt|;
DECL|field|readerKey
specifier|public
specifier|final
name|Object
name|readerKey
decl_stmt|;
DECL|field|shardId
specifier|public
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|listeners
specifier|public
specifier|final
name|List
argument_list|<
name|IndexFieldDataCache
operator|.
name|Listener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Key
name|Key
parameter_list|(
name|IndexFieldCache
name|indexCache
parameter_list|,
name|Object
name|readerKey
parameter_list|,
annotation|@
name|Nullable
name|ShardId
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|indexCache
operator|=
name|indexCache
expr_stmt|;
name|this
operator|.
name|readerKey
operator|=
name|readerKey
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
name|Key
name|key
init|=
operator|(
name|Key
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|indexCache
operator|.
name|equals
argument_list|(
name|key
operator|.
name|indexCache
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|readerKey
operator|.
name|equals
argument_list|(
name|key
operator|.
name|readerKey
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|int
name|result
init|=
name|indexCache
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|readerKey
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

