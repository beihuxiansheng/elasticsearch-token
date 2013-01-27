begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.histogram.bounded
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|histogram
operator|.
name|bounded
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
name|CacheRecycler
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|Facet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|histogram
operator|.
name|HistogramFacet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|histogram
operator|.
name|InternalHistogramFacet
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalBoundedFullHistogramFacet
specifier|public
class|class
name|InternalBoundedFullHistogramFacet
extends|extends
name|InternalHistogramFacet
block|{
DECL|field|STREAM_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|STREAM_TYPE
init|=
literal|"fBdHistogram"
decl_stmt|;
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
parameter_list|()
block|{
name|Streams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|STREAM_TYPE
argument_list|)
expr_stmt|;
block|}
DECL|field|STREAM
specifier|static
name|Stream
name|STREAM
init|=
operator|new
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Facet
name|readFacet
parameter_list|(
name|String
name|type
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readHistogramFacet
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|streamType
specifier|public
name|String
name|streamType
parameter_list|()
block|{
return|return
name|STREAM_TYPE
return|;
block|}
comment|/**      * A histogram entry representing a single entry within the result of a histogram facet.      */
DECL|class|FullEntry
specifier|public
specifier|static
class|class
name|FullEntry
implements|implements
name|Entry
block|{
DECL|field|key
name|long
name|key
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|totalCount
name|long
name|totalCount
decl_stmt|;
DECL|field|total
name|double
name|total
decl_stmt|;
DECL|field|min
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|method|FullEntry
specifier|public
name|FullEntry
parameter_list|(
name|long
name|key
parameter_list|,
name|long
name|count
parameter_list|,
name|double
name|min
parameter_list|,
name|double
name|max
parameter_list|,
name|long
name|totalCount
parameter_list|,
name|double
name|total
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|totalCount
operator|=
name|totalCount
expr_stmt|;
name|this
operator|.
name|total
operator|=
name|total
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|key
specifier|public
name|long
name|key
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|long
name|getKey
parameter_list|()
block|{
return|return
name|key
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|long
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|total
specifier|public
name|double
name|total
parameter_list|()
block|{
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getTotal
specifier|public
name|double
name|getTotal
parameter_list|()
block|{
return|return
name|total
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalCount
specifier|public
name|long
name|totalCount
parameter_list|()
block|{
return|return
name|totalCount
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalCount
specifier|public
name|long
name|getTotalCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalCount
return|;
block|}
annotation|@
name|Override
DECL|method|mean
specifier|public
name|double
name|mean
parameter_list|()
block|{
if|if
condition|(
name|totalCount
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|total
operator|/
name|totalCount
return|;
block|}
annotation|@
name|Override
DECL|method|getMean
specifier|public
name|double
name|getMean
parameter_list|()
block|{
return|return
name|total
operator|/
name|totalCount
return|;
block|}
annotation|@
name|Override
DECL|method|min
specifier|public
name|double
name|min
parameter_list|()
block|{
return|return
name|this
operator|.
name|min
return|;
block|}
annotation|@
name|Override
DECL|method|getMin
specifier|public
name|double
name|getMin
parameter_list|()
block|{
return|return
name|this
operator|.
name|min
return|;
block|}
annotation|@
name|Override
DECL|method|max
specifier|public
name|double
name|max
parameter_list|()
block|{
return|return
name|this
operator|.
name|max
return|;
block|}
annotation|@
name|Override
DECL|method|getMax
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|this
operator|.
name|max
return|;
block|}
block|}
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|comparatorType
specifier|private
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|entries
name|Object
index|[]
name|entries
decl_stmt|;
DECL|field|entriesList
name|List
argument_list|<
name|Object
argument_list|>
name|entriesList
decl_stmt|;
DECL|field|cachedEntries
name|boolean
name|cachedEntries
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
DECL|field|interval
name|long
name|interval
decl_stmt|;
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|normalized
name|boolean
name|normalized
decl_stmt|;
DECL|method|InternalBoundedFullHistogramFacet
specifier|private
name|InternalBoundedFullHistogramFacet
parameter_list|()
block|{     }
DECL|method|InternalBoundedFullHistogramFacet
specifier|public
name|InternalBoundedFullHistogramFacet
parameter_list|(
name|String
name|name
parameter_list|,
name|ComparatorType
name|comparatorType
parameter_list|,
name|long
name|interval
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|size
parameter_list|,
name|Object
index|[]
name|entries
parameter_list|,
name|boolean
name|cachedEntries
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
name|this
operator|.
name|cachedEntries
operator|=
name|cachedEntries
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|entries
specifier|public
name|List
argument_list|<
name|FullEntry
argument_list|>
name|entries
parameter_list|()
block|{
name|normalize
argument_list|()
expr_stmt|;
if|if
condition|(
name|entriesList
operator|==
literal|null
condition|)
block|{
name|Object
index|[]
name|newEntries
init|=
operator|new
name|Object
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|entries
argument_list|,
literal|0
argument_list|,
name|newEntries
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|entriesList
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|newEntries
argument_list|)
expr_stmt|;
block|}
name|releaseCache
argument_list|()
expr_stmt|;
return|return
operator|(
name|List
operator|)
name|entriesList
return|;
block|}
annotation|@
name|Override
DECL|method|getEntries
specifier|public
name|List
argument_list|<
name|FullEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|entries
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|(
name|Iterator
operator|)
name|entries
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|releaseCache
specifier|private
name|void
name|releaseCache
parameter_list|()
block|{
if|if
condition|(
name|cachedEntries
condition|)
block|{
name|cachedEntries
operator|=
literal|false
expr_stmt|;
name|CacheRecycler
operator|.
name|pushObjectArray
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|Facet
name|reduce
parameter_list|(
name|List
argument_list|<
name|Facet
argument_list|>
name|facets
parameter_list|)
block|{
if|if
condition|(
name|facets
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// we need to sort it
name|InternalBoundedFullHistogramFacet
name|internalFacet
init|=
operator|(
name|InternalBoundedFullHistogramFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparatorType
operator|!=
name|ComparatorType
operator|.
name|KEY
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|internalFacet
operator|.
name|entries
argument_list|,
operator|(
name|Comparator
operator|)
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|internalFacet
return|;
block|}
name|InternalBoundedFullHistogramFacet
name|first
init|=
operator|(
name|InternalBoundedFullHistogramFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|f
init|=
literal|1
init|;
name|f
operator|<
name|facets
operator|.
name|size
argument_list|()
condition|;
name|f
operator|++
control|)
block|{
name|InternalBoundedFullHistogramFacet
name|internalFacet
init|=
operator|(
name|InternalBoundedFullHistogramFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
name|f
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|FullEntry
name|aggEntry
init|=
operator|(
name|FullEntry
operator|)
name|first
operator|.
name|entries
index|[
name|i
index|]
decl_stmt|;
name|FullEntry
name|entry
init|=
operator|(
name|FullEntry
operator|)
name|internalFacet
operator|.
name|entries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|aggEntry
operator|==
literal|null
condition|)
block|{
name|first
operator|.
name|entries
index|[
name|i
index|]
operator|=
name|entry
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|aggEntry
operator|.
name|count
operator|+=
name|entry
operator|.
name|count
expr_stmt|;
name|aggEntry
operator|.
name|totalCount
operator|+=
name|entry
operator|.
name|totalCount
expr_stmt|;
name|aggEntry
operator|.
name|total
operator|+=
name|entry
operator|.
name|total
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|min
operator|<
name|aggEntry
operator|.
name|min
condition|)
block|{
name|aggEntry
operator|.
name|min
operator|=
name|entry
operator|.
name|min
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|max
operator|>
name|aggEntry
operator|.
name|max
condition|)
block|{
name|aggEntry
operator|.
name|max
operator|=
name|entry
operator|.
name|max
expr_stmt|;
block|}
block|}
block|}
name|internalFacet
operator|.
name|releaseCache
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|comparatorType
operator|!=
name|ComparatorType
operator|.
name|KEY
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|first
operator|.
name|entries
argument_list|,
operator|(
name|Comparator
operator|)
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|first
return|;
block|}
DECL|method|normalize
specifier|private
name|void
name|normalize
parameter_list|()
block|{
if|if
condition|(
name|normalized
condition|)
block|{
return|return;
block|}
name|normalized
operator|=
literal|true
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|FullEntry
name|entry
init|=
operator|(
name|FullEntry
operator|)
name|entries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entries
index|[
name|i
index|]
operator|=
operator|new
name|FullEntry
argument_list|(
operator|(
name|i
operator|*
name|interval
operator|)
operator|+
name|offset
argument_list|,
literal|0
argument_list|,
name|Double
operator|.
name|NaN
argument_list|,
name|Double
operator|.
name|NaN
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|key
operator|=
operator|(
name|i
operator|*
name|interval
operator|)
operator|+
name|offset
expr_stmt|;
block|}
block|}
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|_TYPE
specifier|static
specifier|final
name|XContentBuilderString
name|_TYPE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_type"
argument_list|)
decl_stmt|;
DECL|field|ENTRIES
specifier|static
specifier|final
name|XContentBuilderString
name|ENTRIES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"entries"
argument_list|)
decl_stmt|;
DECL|field|KEY
specifier|static
specifier|final
name|XContentBuilderString
name|KEY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
DECL|field|COUNT
specifier|static
specifier|final
name|XContentBuilderString
name|COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"count"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|TOTAL_COUNT
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL_COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total_count"
argument_list|)
decl_stmt|;
DECL|field|MEAN
specifier|static
specifier|final
name|XContentBuilderString
name|MEAN
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"mean"
argument_list|)
decl_stmt|;
DECL|field|MIN
specifier|static
specifier|final
name|XContentBuilderString
name|MIN
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"min"
argument_list|)
decl_stmt|;
DECL|field|MAX
specifier|static
specifier|final
name|XContentBuilderString
name|MAX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max"
argument_list|)
decl_stmt|;
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
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_TYPE
argument_list|,
name|HistogramFacet
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|ENTRIES
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|FullEntry
name|entry
init|=
operator|(
name|FullEntry
operator|)
name|entries
index|[
name|i
index|]
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|normalized
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|KEY
argument_list|,
name|entry
operator|.
name|key
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|KEY
argument_list|,
operator|(
name|i
operator|*
name|interval
operator|)
operator|+
name|offset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|COUNT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
literal|0
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
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|COUNT
argument_list|,
name|entry
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MIN
argument_list|,
name|entry
operator|.
name|min
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX
argument_list|,
name|entry
operator|.
name|max
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|entry
operator|.
name|total
argument_list|()
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
name|entry
operator|.
name|totalCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MEAN
argument_list|,
name|entry
operator|.
name|mean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|releaseCache
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readHistogramFacet
specifier|public
specifier|static
name|InternalBoundedFullHistogramFacet
name|readHistogramFacet
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalBoundedFullHistogramFacet
name|facet
init|=
operator|new
name|InternalBoundedFullHistogramFacet
argument_list|()
decl_stmt|;
name|facet
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|facet
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
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|comparatorType
operator|=
name|ComparatorType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|offset
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|interval
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|size
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entries
operator|=
name|CacheRecycler
operator|.
name|popObjectArray
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|cachedEntries
operator|=
literal|true
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
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|entries
index|[
name|i
index|]
operator|=
operator|new
name|FullEntry
argument_list|(
name|i
argument_list|,
name|in
operator|.
name|readVLong
argument_list|()
argument_list|,
name|in
operator|.
name|readDouble
argument_list|()
argument_list|,
name|in
operator|.
name|readDouble
argument_list|()
argument_list|,
name|in
operator|.
name|readVLong
argument_list|()
argument_list|,
name|in
operator|.
name|readDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|comparatorType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|FullEntry
name|entry
init|=
operator|(
name|FullEntry
operator|)
name|entries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//                out.writeLong(entry.key);
name|out
operator|.
name|writeVLong
argument_list|(
name|entry
operator|.
name|count
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|entry
operator|.
name|min
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|entry
operator|.
name|max
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|entry
operator|.
name|totalCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|entry
operator|.
name|total
argument_list|)
expr_stmt|;
block|}
block|}
name|releaseCache
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

