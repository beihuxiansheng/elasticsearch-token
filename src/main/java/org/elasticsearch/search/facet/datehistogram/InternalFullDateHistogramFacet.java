begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.datehistogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|datehistogram
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
name|util
operator|.
name|CollectionUtil
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
name|Strings
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
name|bytes
operator|.
name|BytesReference
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
name|bytes
operator|.
name|HashedBytesArray
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
name|trove
operator|.
name|ExtTLongObjectHashMap
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalFullDateHistogramFacet
specifier|public
class|class
name|InternalFullDateHistogramFacet
extends|extends
name|InternalDateHistogramFacet
block|{
DECL|field|STREAM_TYPE
specifier|private
specifier|static
specifier|final
name|BytesReference
name|STREAM_TYPE
init|=
operator|new
name|HashedBytesArray
argument_list|(
name|Strings
operator|.
name|toUTF8Bytes
argument_list|(
literal|"fdHistogram"
argument_list|)
argument_list|)
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
name|BytesReference
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
DECL|field|time
specifier|private
specifier|final
name|long
name|time
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
name|time
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
name|time
operator|=
name|time
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
DECL|method|getTime
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|time
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
if|if
condition|(
name|totalCount
operator|==
literal|0
condition|)
block|{
return|return
name|totalCount
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
DECL|field|comparatorType
specifier|private
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|tEntries
name|ExtTLongObjectHashMap
argument_list|<
name|FullEntry
argument_list|>
name|tEntries
decl_stmt|;
DECL|field|cachedEntries
name|boolean
name|cachedEntries
decl_stmt|;
DECL|field|entries
name|Collection
argument_list|<
name|FullEntry
argument_list|>
name|entries
decl_stmt|;
DECL|method|InternalFullDateHistogramFacet
name|InternalFullDateHistogramFacet
parameter_list|()
block|{     }
DECL|method|InternalFullDateHistogramFacet
name|InternalFullDateHistogramFacet
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|InternalFullDateHistogramFacet
specifier|public
name|InternalFullDateHistogramFacet
parameter_list|(
name|String
name|name
parameter_list|,
name|ComparatorType
name|comparatorType
parameter_list|,
name|ExtTLongObjectHashMap
argument_list|<
name|InternalFullDateHistogramFacet
operator|.
name|FullEntry
argument_list|>
name|entries
parameter_list|,
name|boolean
name|cachedEntries
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|this
operator|.
name|tEntries
operator|=
name|entries
expr_stmt|;
name|this
operator|.
name|cachedEntries
operator|=
name|cachedEntries
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
operator|.
name|valueCollection
argument_list|()
expr_stmt|;
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
if|if
condition|(
operator|!
operator|(
name|entries
operator|instanceof
name|List
operator|)
condition|)
block|{
name|entries
operator|=
operator|new
name|ArrayList
argument_list|<
name|FullEntry
argument_list|>
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|List
argument_list|<
name|FullEntry
argument_list|>
operator|)
name|entries
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
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|releaseCache
name|void
name|releaseCache
parameter_list|()
block|{
if|if
condition|(
name|cachedEntries
condition|)
block|{
name|CacheRecycler
operator|.
name|pushLongObjectMap
argument_list|(
name|tEntries
argument_list|)
expr_stmt|;
name|cachedEntries
operator|=
literal|false
expr_stmt|;
name|tEntries
operator|=
literal|null
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
name|InternalFullDateHistogramFacet
name|internalFacet
init|=
operator|(
name|InternalFullDateHistogramFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FullEntry
argument_list|>
name|entries
init|=
name|internalFacet
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|CollectionUtil
operator|.
name|timSort
argument_list|(
name|entries
argument_list|,
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
name|internalFacet
operator|.
name|releaseCache
argument_list|()
expr_stmt|;
return|return
name|internalFacet
return|;
block|}
name|ExtTLongObjectHashMap
argument_list|<
name|FullEntry
argument_list|>
name|map
init|=
name|CacheRecycler
operator|.
name|popLongObjectMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Facet
name|facet
range|:
name|facets
control|)
block|{
name|InternalFullDateHistogramFacet
name|histoFacet
init|=
operator|(
name|InternalFullDateHistogramFacet
operator|)
name|facet
decl_stmt|;
for|for
control|(
name|FullEntry
name|fullEntry
range|:
name|histoFacet
operator|.
name|entries
control|)
block|{
name|FullEntry
name|current
init|=
name|map
operator|.
name|get
argument_list|(
name|fullEntry
operator|.
name|time
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|current
operator|.
name|count
operator|+=
name|fullEntry
operator|.
name|count
expr_stmt|;
name|current
operator|.
name|total
operator|+=
name|fullEntry
operator|.
name|total
expr_stmt|;
name|current
operator|.
name|totalCount
operator|+=
name|fullEntry
operator|.
name|totalCount
expr_stmt|;
if|if
condition|(
name|fullEntry
operator|.
name|min
operator|<
name|current
operator|.
name|min
condition|)
block|{
name|current
operator|.
name|min
operator|=
name|fullEntry
operator|.
name|min
expr_stmt|;
block|}
if|if
condition|(
name|fullEntry
operator|.
name|max
operator|>
name|current
operator|.
name|max
condition|)
block|{
name|current
operator|.
name|max
operator|=
name|fullEntry
operator|.
name|max
expr_stmt|;
block|}
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|fullEntry
operator|.
name|time
argument_list|,
name|fullEntry
argument_list|)
expr_stmt|;
block|}
block|}
name|histoFacet
operator|.
name|releaseCache
argument_list|()
expr_stmt|;
block|}
comment|// sort
name|Object
index|[]
name|values
init|=
name|map
operator|.
name|internalValues
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
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
name|List
argument_list|<
name|FullEntry
argument_list|>
name|ordered
init|=
operator|new
name|ArrayList
argument_list|<
name|FullEntry
argument_list|>
argument_list|(
name|map
operator|.
name|size
argument_list|()
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
name|map
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FullEntry
name|value
init|=
operator|(
name|FullEntry
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|ordered
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|CacheRecycler
operator|.
name|pushLongObjectMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
comment|// just initialize it as already ordered facet
name|InternalFullDateHistogramFacet
name|ret
init|=
operator|new
name|InternalFullDateHistogramFacet
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ret
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|ret
operator|.
name|entries
operator|=
name|ordered
expr_stmt|;
return|return
name|ret
return|;
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
DECL|field|TIME
specifier|static
specifier|final
name|XContentBuilderString
name|TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"time"
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
name|getName
argument_list|()
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
name|Entry
name|entry
range|:
name|getEntries
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME
argument_list|,
name|entry
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
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
name|getCount
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
name|getMin
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
name|getMax
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
name|getTotal
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
name|MEAN
argument_list|,
name|entry
operator|.
name|getMean
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
name|builder
return|;
block|}
DECL|method|readHistogramFacet
specifier|public
specifier|static
name|InternalFullDateHistogramFacet
name|readHistogramFacet
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalFullDateHistogramFacet
name|facet
init|=
operator|new
name|InternalFullDateHistogramFacet
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
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
name|cachedEntries
operator|=
literal|false
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|entries
operator|=
operator|new
name|ArrayList
argument_list|<
name|FullEntry
argument_list|>
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
name|entries
operator|.
name|add
argument_list|(
operator|new
name|FullEntry
argument_list|(
name|in
operator|.
name|readLong
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
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
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
name|writeVInt
argument_list|(
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FullEntry
name|entry
range|:
name|entries
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|entry
operator|.
name|time
argument_list|)
expr_stmt|;
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
name|releaseCache
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

