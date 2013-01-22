begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
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
comment|/**  *  */
end_comment

begin_class
DECL|class|CacheStats
specifier|public
class|class
name|CacheStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|filterEvictions
name|long
name|filterEvictions
decl_stmt|;
DECL|field|filterCount
name|long
name|filterCount
decl_stmt|;
DECL|field|filterSize
name|long
name|filterSize
decl_stmt|;
DECL|field|idCacheSize
name|long
name|idCacheSize
decl_stmt|;
DECL|method|CacheStats
specifier|public
name|CacheStats
parameter_list|()
block|{     }
DECL|method|CacheStats
specifier|public
name|CacheStats
parameter_list|(
name|long
name|filterEvictions
parameter_list|,
name|long
name|filterSize
parameter_list|,
name|long
name|filterCount
parameter_list|,
name|long
name|idCacheSize
parameter_list|)
block|{
name|this
operator|.
name|filterEvictions
operator|=
name|filterEvictions
expr_stmt|;
name|this
operator|.
name|filterSize
operator|=
name|filterSize
expr_stmt|;
name|this
operator|.
name|filterCount
operator|=
name|filterCount
expr_stmt|;
name|this
operator|.
name|idCacheSize
operator|=
name|idCacheSize
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|CacheStats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|filterEvictions
operator|+=
name|stats
operator|.
name|filterEvictions
expr_stmt|;
name|this
operator|.
name|filterSize
operator|+=
name|stats
operator|.
name|filterSize
expr_stmt|;
name|this
operator|.
name|filterCount
operator|+=
name|stats
operator|.
name|filterCount
expr_stmt|;
name|this
operator|.
name|idCacheSize
operator|+=
name|stats
operator|.
name|idCacheSize
expr_stmt|;
block|}
DECL|method|filterEvictions
specifier|public
name|long
name|filterEvictions
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterEvictions
return|;
block|}
DECL|method|getFilterEvictions
specifier|public
name|long
name|getFilterEvictions
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterEvictions
return|;
block|}
DECL|method|filterMemEvictions
specifier|public
name|long
name|filterMemEvictions
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterEvictions
return|;
block|}
DECL|method|getFilterMemEvictions
specifier|public
name|long
name|getFilterMemEvictions
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterEvictions
return|;
block|}
DECL|method|filterCount
specifier|public
name|long
name|filterCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterCount
return|;
block|}
DECL|method|getFilterCount
specifier|public
name|long
name|getFilterCount
parameter_list|()
block|{
return|return
name|filterCount
return|;
block|}
DECL|method|filterSizeInBytes
specifier|public
name|long
name|filterSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterSize
return|;
block|}
DECL|method|getFilterSizeInBytes
specifier|public
name|long
name|getFilterSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterSizeInBytes
argument_list|()
return|;
block|}
DECL|method|filterSize
specifier|public
name|ByteSizeValue
name|filterSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|filterSize
argument_list|)
return|;
block|}
DECL|method|getFilterSize
specifier|public
name|ByteSizeValue
name|getFilterSize
parameter_list|()
block|{
return|return
name|filterSize
argument_list|()
return|;
block|}
DECL|method|idCacheSizeInBytes
specifier|public
name|long
name|idCacheSizeInBytes
parameter_list|()
block|{
return|return
name|idCacheSize
return|;
block|}
DECL|method|getIdCacheSizeInBytes
specifier|public
name|long
name|getIdCacheSizeInBytes
parameter_list|()
block|{
return|return
name|idCacheSizeInBytes
argument_list|()
return|;
block|}
DECL|method|idCacheSize
specifier|public
name|ByteSizeValue
name|idCacheSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|idCacheSize
argument_list|)
return|;
block|}
DECL|method|getIdCacheSize
specifier|public
name|ByteSizeValue
name|getIdCacheSize
parameter_list|()
block|{
return|return
name|idCacheSize
argument_list|()
return|;
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
name|CACHE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FILTER_COUNT
argument_list|,
name|filterCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FILTER_EVICTIONS
argument_list|,
name|filterEvictions
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FILTER_SIZE
argument_list|,
name|filterSize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FILTER_SIZE_IN_BYTES
argument_list|,
name|filterSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ID_CACHE_SIZE
argument_list|,
name|idCacheSize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ID_CACHE_SIZE_IN_BYTES
argument_list|,
name|idCacheSize
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
DECL|field|CACHE
specifier|static
specifier|final
name|XContentBuilderString
name|CACHE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"cache"
argument_list|)
decl_stmt|;
DECL|field|FILTER_EVICTIONS
specifier|static
specifier|final
name|XContentBuilderString
name|FILTER_EVICTIONS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"filter_evictions"
argument_list|)
decl_stmt|;
DECL|field|FILTER_COUNT
specifier|static
specifier|final
name|XContentBuilderString
name|FILTER_COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"filter_count"
argument_list|)
decl_stmt|;
DECL|field|FILTER_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|FILTER_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"filter_size"
argument_list|)
decl_stmt|;
DECL|field|FILTER_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|FILTER_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"filter_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|ID_CACHE_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|ID_CACHE_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"id_cache_size"
argument_list|)
decl_stmt|;
DECL|field|ID_CACHE_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|ID_CACHE_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"id_cache_size_in_bytes"
argument_list|)
decl_stmt|;
block|}
DECL|method|readCacheStats
specifier|public
specifier|static
name|CacheStats
name|readCacheStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CacheStats
name|stats
init|=
operator|new
name|CacheStats
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
name|filterEvictions
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|filterSize
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|filterCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|idCacheSize
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
name|filterEvictions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|filterSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|filterCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|idCacheSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

