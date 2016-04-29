begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|query
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
name|search
operator|.
name|DocIdSet
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|xcontent
operator|.
name|ToXContent
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|QueryCacheStats
specifier|public
class|class
name|QueryCacheStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|ramBytesUsed
name|long
name|ramBytesUsed
decl_stmt|;
DECL|field|hitCount
name|long
name|hitCount
decl_stmt|;
DECL|field|missCount
name|long
name|missCount
decl_stmt|;
DECL|field|cacheCount
name|long
name|cacheCount
decl_stmt|;
DECL|field|cacheSize
name|long
name|cacheSize
decl_stmt|;
DECL|method|QueryCacheStats
specifier|public
name|QueryCacheStats
parameter_list|()
block|{     }
DECL|method|QueryCacheStats
specifier|public
name|QueryCacheStats
parameter_list|(
name|long
name|ramBytesUsed
parameter_list|,
name|long
name|hitCount
parameter_list|,
name|long
name|missCount
parameter_list|,
name|long
name|cacheCount
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|ramBytesUsed
operator|=
name|ramBytesUsed
expr_stmt|;
name|this
operator|.
name|hitCount
operator|=
name|hitCount
expr_stmt|;
name|this
operator|.
name|missCount
operator|=
name|missCount
expr_stmt|;
name|this
operator|.
name|cacheCount
operator|=
name|cacheCount
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|QueryCacheStats
name|stats
parameter_list|)
block|{
name|ramBytesUsed
operator|+=
name|stats
operator|.
name|ramBytesUsed
expr_stmt|;
name|hitCount
operator|+=
name|stats
operator|.
name|hitCount
expr_stmt|;
name|missCount
operator|+=
name|stats
operator|.
name|missCount
expr_stmt|;
name|cacheCount
operator|+=
name|stats
operator|.
name|cacheCount
expr_stmt|;
name|cacheSize
operator|+=
name|stats
operator|.
name|cacheSize
expr_stmt|;
block|}
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
DECL|method|getMemorySize
specifier|public
name|ByteSizeValue
name|getMemorySize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|ramBytesUsed
argument_list|)
return|;
block|}
comment|/**      * The total number of lookups in the cache.      */
DECL|method|getTotalCount
specifier|public
name|long
name|getTotalCount
parameter_list|()
block|{
return|return
name|hitCount
operator|+
name|missCount
return|;
block|}
comment|/**      * The number of successful lookups in the cache.      */
DECL|method|getHitCount
specifier|public
name|long
name|getHitCount
parameter_list|()
block|{
return|return
name|hitCount
return|;
block|}
comment|/**      * The number of lookups in the cache that failed to retrieve a {@link DocIdSet}.      */
DECL|method|getMissCount
specifier|public
name|long
name|getMissCount
parameter_list|()
block|{
return|return
name|missCount
return|;
block|}
comment|/**      * The number of {@link DocIdSet}s that have been cached.      */
DECL|method|getCacheCount
specifier|public
name|long
name|getCacheCount
parameter_list|()
block|{
return|return
name|cacheCount
return|;
block|}
comment|/**      * The number of {@link DocIdSet}s that are in the cache.      */
DECL|method|getCacheSize
specifier|public
name|long
name|getCacheSize
parameter_list|()
block|{
return|return
name|cacheSize
return|;
block|}
comment|/**      * The number of {@link DocIdSet}s that have been evicted from the cache.      */
DECL|method|getEvictions
specifier|public
name|long
name|getEvictions
parameter_list|()
block|{
return|return
name|cacheCount
operator|-
name|cacheSize
return|;
block|}
DECL|method|readQueryCacheStats
specifier|public
specifier|static
name|QueryCacheStats
name|readQueryCacheStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryCacheStats
name|stats
init|=
operator|new
name|QueryCacheStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ramBytesUsed
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|hitCount
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|missCount
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|cacheCount
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|cacheSize
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|ramBytesUsed
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|hitCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|missCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|cacheCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|QUERY_CACHE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|MEMORY_SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|MEMORY_SIZE
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL_COUNT
argument_list|,
name|getTotalCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|HIT_COUNT
argument_list|,
name|getHitCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MISS_COUNT
argument_list|,
name|getMissCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CACHE_SIZE
argument_list|,
name|getCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CACHE_COUNT
argument_list|,
name|getCacheCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|EVICTIONS
argument_list|,
name|getEvictions
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|QUERY_CACHE
specifier|static
specifier|final
name|String
name|QUERY_CACHE
init|=
literal|"query_cache"
decl_stmt|;
DECL|field|MEMORY_SIZE
specifier|static
specifier|final
name|String
name|MEMORY_SIZE
init|=
literal|"memory_size"
decl_stmt|;
DECL|field|MEMORY_SIZE_IN_BYTES
specifier|static
specifier|final
name|String
name|MEMORY_SIZE_IN_BYTES
init|=
literal|"memory_size_in_bytes"
decl_stmt|;
DECL|field|TOTAL_COUNT
specifier|static
specifier|final
name|String
name|TOTAL_COUNT
init|=
literal|"total_count"
decl_stmt|;
DECL|field|HIT_COUNT
specifier|static
specifier|final
name|String
name|HIT_COUNT
init|=
literal|"hit_count"
decl_stmt|;
DECL|field|MISS_COUNT
specifier|static
specifier|final
name|String
name|MISS_COUNT
init|=
literal|"miss_count"
decl_stmt|;
DECL|field|CACHE_SIZE
specifier|static
specifier|final
name|String
name|CACHE_SIZE
init|=
literal|"cache_size"
decl_stmt|;
DECL|field|CACHE_COUNT
specifier|static
specifier|final
name|String
name|CACHE_COUNT
init|=
literal|"cache_count"
decl_stmt|;
DECL|field|EVICTIONS
specifier|static
specifier|final
name|String
name|EVICTIONS
init|=
literal|"evictions"
decl_stmt|;
block|}
block|}
end_class

end_unit

