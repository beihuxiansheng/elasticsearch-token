begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|AtomicReaderContext
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
name|lucene
operator|.
name|SegmentReaderUtils
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
name|FieldMapper
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
name|service
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
name|index
operator|.
name|shard
operator|.
name|service
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
name|indices
operator|.
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCacheListener
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_comment
comment|/**  * A simple field data cache abstraction on the *index* level.  */
end_comment

begin_interface
DECL|interface|IndexFieldDataCache
specifier|public
interface|interface
name|IndexFieldDataCache
block|{
DECL|method|load
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
name|AtomicReaderContext
name|context
parameter_list|,
name|IFD
name|indexFieldData
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Clears all the field data stored cached in on this index.      */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**      * Clears all the field data stored cached in on this index for the specified field name.      */
DECL|method|clear
name|void
name|clear
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
DECL|method|clear
name|void
name|clear
parameter_list|(
name|Object
name|coreCacheKey
parameter_list|)
function_decl|;
DECL|interface|Listener
interface|interface
name|Listener
block|{
DECL|method|onLoad
name|void
name|onLoad
parameter_list|(
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|AtomicFieldData
name|fieldData
parameter_list|)
function_decl|;
DECL|method|onUnload
name|void
name|onUnload
parameter_list|(
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|boolean
name|wasEvicted
parameter_list|,
name|long
name|sizeInBytes
parameter_list|,
annotation|@
name|Nullable
name|AtomicFieldData
name|fieldData
parameter_list|)
function_decl|;
block|}
comment|/**      * The resident field data cache is a *per field* cache that keeps all the values in memory.      */
DECL|class|FieldBased
specifier|static
specifier|abstract
class|class
name|FieldBased
implements|implements
name|IndexFieldDataCache
implements|,
name|SegmentReader
operator|.
name|CoreClosedListener
implements|,
name|RemovalListener
argument_list|<
name|FieldBased
operator|.
name|Key
argument_list|,
name|AtomicFieldData
argument_list|>
block|{
annotation|@
name|Nullable
DECL|field|indexService
specifier|private
specifier|final
name|IndexService
name|indexService
decl_stmt|;
DECL|field|fieldNames
specifier|private
specifier|final
name|FieldMapper
operator|.
name|Names
name|fieldNames
decl_stmt|;
DECL|field|fieldDataType
specifier|private
specifier|final
name|FieldDataType
name|fieldDataType
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|Key
argument_list|,
name|AtomicFieldData
argument_list|>
name|cache
decl_stmt|;
DECL|field|indicesFieldDataCacheListener
specifier|private
specifier|final
name|IndicesFieldDataCacheListener
name|indicesFieldDataCacheListener
decl_stmt|;
DECL|method|FieldBased
specifier|protected
name|FieldBased
parameter_list|(
annotation|@
name|Nullable
name|IndexService
name|indexService
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|CacheBuilder
name|cache
parameter_list|,
name|IndicesFieldDataCacheListener
name|indicesFieldDataCacheListener
parameter_list|)
block|{
name|this
operator|.
name|indexService
operator|=
name|indexService
expr_stmt|;
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
name|this
operator|.
name|fieldDataType
operator|=
name|fieldDataType
expr_stmt|;
name|this
operator|.
name|indicesFieldDataCacheListener
operator|=
name|indicesFieldDataCacheListener
expr_stmt|;
name|cache
operator|.
name|removalListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
name|this
operator|.
name|cache
operator|=
name|cache
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|AtomicFieldData
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
name|AtomicFieldData
name|value
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|long
name|sizeInBytes
init|=
name|key
operator|.
name|sizeInBytes
decl_stmt|;
if|if
condition|(
name|sizeInBytes
operator|==
operator|-
literal|1
operator|&&
name|value
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|=
name|value
operator|.
name|getMemorySizeInBytes
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Listener
name|listener
range|:
name|key
operator|.
name|listeners
control|)
block|{
name|listener
operator|.
name|onUnload
argument_list|(
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|notification
operator|.
name|wasEvicted
argument_list|()
argument_list|,
name|sizeInBytes
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
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
name|AtomicReaderContext
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
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
return|return
operator|(
name|FD
operator|)
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
operator|new
name|Callable
argument_list|<
name|AtomicFieldData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AtomicFieldData
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|SegmentReaderUtils
operator|.
name|registerCoreListener
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|FieldBased
operator|.
name|this
argument_list|)
expr_stmt|;
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
name|key
operator|.
name|sizeInBytes
operator|=
name|fieldData
operator|.
name|getMemorySizeInBytes
argument_list|()
expr_stmt|;
name|key
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|indicesFieldDataCacheListener
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexService
operator|!=
literal|null
condition|)
block|{
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
name|key
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|shard
operator|.
name|fieldData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Listener
name|listener
range|:
name|key
operator|.
name|listeners
control|)
block|{
name|listener
operator|.
name|onLoad
argument_list|(
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|fieldData
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldData
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|invalidateAll
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
name|cache
operator|.
name|invalidateAll
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
name|Object
name|coreCacheKey
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
operator|new
name|Key
argument_list|(
name|coreCacheKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|Object
name|coreCacheKey
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
operator|new
name|Key
argument_list|(
name|coreCacheKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|Key
specifier|static
class|class
name|Key
block|{
DECL|field|readerKey
specifier|final
name|Object
name|readerKey
decl_stmt|;
DECL|field|listeners
specifier|final
name|List
argument_list|<
name|Listener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// optional stats listener
DECL|field|sizeInBytes
name|long
name|sizeInBytes
init|=
operator|-
literal|1
decl_stmt|;
comment|// optional size in bytes (we keep it here in case the values are soft references)
DECL|method|Key
name|Key
parameter_list|(
name|Object
name|readerKey
parameter_list|)
block|{
name|this
operator|.
name|readerKey
operator|=
name|readerKey
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
return|return
name|readerKey
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
DECL|class|Resident
specifier|static
class|class
name|Resident
extends|extends
name|FieldBased
block|{
DECL|method|Resident
specifier|public
name|Resident
parameter_list|(
annotation|@
name|Nullable
name|IndexService
name|indexService
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndicesFieldDataCacheListener
name|indicesFieldDataCacheListener
parameter_list|)
block|{
name|super
argument_list|(
name|indexService
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
argument_list|,
name|indicesFieldDataCacheListener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Soft
specifier|static
class|class
name|Soft
extends|extends
name|FieldBased
block|{
DECL|method|Soft
specifier|public
name|Soft
parameter_list|(
annotation|@
name|Nullable
name|IndexService
name|indexService
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndicesFieldDataCacheListener
name|indicesFieldDataCacheListener
parameter_list|)
block|{
name|super
argument_list|(
name|indexService
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|softValues
argument_list|()
argument_list|,
name|indicesFieldDataCacheListener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

