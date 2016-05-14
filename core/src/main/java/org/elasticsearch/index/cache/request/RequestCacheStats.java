begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.request
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|request
package|;
end_package

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
DECL|class|RequestCacheStats
specifier|public
class|class
name|RequestCacheStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|memorySize
name|long
name|memorySize
decl_stmt|;
DECL|field|evictions
name|long
name|evictions
decl_stmt|;
DECL|field|hitCount
name|long
name|hitCount
decl_stmt|;
DECL|field|missCount
name|long
name|missCount
decl_stmt|;
DECL|method|RequestCacheStats
specifier|public
name|RequestCacheStats
parameter_list|()
block|{     }
DECL|method|RequestCacheStats
specifier|public
name|RequestCacheStats
parameter_list|(
name|long
name|memorySize
parameter_list|,
name|long
name|evictions
parameter_list|,
name|long
name|hitCount
parameter_list|,
name|long
name|missCount
parameter_list|)
block|{
name|this
operator|.
name|memorySize
operator|=
name|memorySize
expr_stmt|;
name|this
operator|.
name|evictions
operator|=
name|evictions
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
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|RequestCacheStats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|memorySize
operator|+=
name|stats
operator|.
name|memorySize
expr_stmt|;
name|this
operator|.
name|evictions
operator|+=
name|stats
operator|.
name|evictions
expr_stmt|;
name|this
operator|.
name|hitCount
operator|+=
name|stats
operator|.
name|hitCount
expr_stmt|;
name|this
operator|.
name|missCount
operator|+=
name|stats
operator|.
name|missCount
expr_stmt|;
block|}
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|memorySize
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
name|memorySize
argument_list|)
return|;
block|}
DECL|method|getEvictions
specifier|public
name|long
name|getEvictions
parameter_list|()
block|{
return|return
name|this
operator|.
name|evictions
return|;
block|}
DECL|method|getHitCount
specifier|public
name|long
name|getHitCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|hitCount
return|;
block|}
DECL|method|getMissCount
specifier|public
name|long
name|getMissCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|missCount
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
name|memorySize
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|evictions
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|hitCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|missCount
operator|=
name|in
operator|.
name|readVLong
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
name|writeVLong
argument_list|(
name|memorySize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|evictions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|hitCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|missCount
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
name|REQUEST_CACHE_STATS
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
name|memorySize
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
DECL|field|REQUEST_CACHE_STATS
specifier|static
specifier|final
name|String
name|REQUEST_CACHE_STATS
init|=
literal|"request_cache"
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
DECL|field|EVICTIONS
specifier|static
specifier|final
name|String
name|EVICTIONS
init|=
literal|"evictions"
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
block|}
block|}
end_class

end_unit

