begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|InternalBoundedCountHistogramFacet
specifier|public
class|class
name|InternalBoundedCountHistogramFacet
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
literal|"cBdHistogram"
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
DECL|method|streamType
annotation|@
name|Override
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
DECL|class|CountEntry
specifier|public
specifier|static
class|class
name|CountEntry
implements|implements
name|Entry
block|{
DECL|field|key
specifier|private
specifier|final
name|long
name|key
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|long
name|count
decl_stmt|;
DECL|method|CountEntry
specifier|public
name|CountEntry
parameter_list|(
name|long
name|key
parameter_list|,
name|long
name|count
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
block|}
DECL|method|key
annotation|@
name|Override
specifier|public
name|long
name|key
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getKey
annotation|@
name|Override
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
DECL|method|count
annotation|@
name|Override
specifier|public
name|long
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getCount
annotation|@
name|Override
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
DECL|method|total
annotation|@
name|Override
specifier|public
name|double
name|total
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
DECL|method|getTotal
annotation|@
name|Override
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
DECL|method|totalCount
annotation|@
name|Override
specifier|public
name|long
name|totalCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getTotalCount
annotation|@
name|Override
specifier|public
name|long
name|getTotalCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|mean
annotation|@
name|Override
specifier|public
name|double
name|mean
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
DECL|method|getMean
annotation|@
name|Override
specifier|public
name|double
name|getMean
parameter_list|()
block|{
return|return
name|mean
argument_list|()
return|;
block|}
DECL|method|min
annotation|@
name|Override
specifier|public
name|double
name|min
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
DECL|method|getMin
annotation|@
name|Override
specifier|public
name|double
name|getMin
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
DECL|method|max
annotation|@
name|Override
specifier|public
name|double
name|max
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
DECL|method|getMax
annotation|@
name|Override
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
block|}
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|comparatorType
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|counts
name|int
index|[]
name|counts
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
DECL|field|entries
name|CountEntry
index|[]
name|entries
init|=
literal|null
decl_stmt|;
DECL|method|InternalBoundedCountHistogramFacet
specifier|private
name|InternalBoundedCountHistogramFacet
parameter_list|()
block|{     }
DECL|method|InternalBoundedCountHistogramFacet
specifier|public
name|InternalBoundedCountHistogramFacet
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
name|int
index|[]
name|counts
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
name|counts
operator|=
name|counts
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
DECL|method|name
annotation|@
name|Override
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
DECL|method|getName
annotation|@
name|Override
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
DECL|method|type
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|method|getType
annotation|@
name|Override
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
DECL|method|entries
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CountEntry
argument_list|>
name|entries
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|computeEntries
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getEntries
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CountEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|entries
argument_list|()
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
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
DECL|method|computeEntries
specifier|private
name|CountEntry
index|[]
name|computeEntries
parameter_list|()
block|{
if|if
condition|(
name|entries
operator|!=
literal|null
condition|)
block|{
return|return
name|entries
return|;
block|}
name|entries
operator|=
operator|new
name|CountEntry
index|[
name|size
index|]
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
index|[
name|i
index|]
operator|=
operator|new
name|CountEntry
argument_list|(
operator|(
name|i
operator|*
name|interval
operator|)
operator|+
name|offset
argument_list|,
name|counts
index|[
name|i
index|]
argument_list|)
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
name|entries
argument_list|,
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
DECL|method|reduce
annotation|@
name|Override
specifier|public
name|Facet
name|reduce
parameter_list|(
name|String
name|name
parameter_list|,
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
return|return
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|InternalBoundedCountHistogramFacet
name|firstHistoFacet
init|=
operator|(
name|InternalBoundedCountHistogramFacet
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
name|i
init|=
literal|1
init|;
name|i
operator|<
name|facets
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|InternalBoundedCountHistogramFacet
name|histoFacet
init|=
operator|(
name|InternalBoundedCountHistogramFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|firstHistoFacet
operator|.
name|size
condition|;
name|j
operator|++
control|)
block|{
name|firstHistoFacet
operator|.
name|counts
index|[
name|j
index|]
operator|+=
name|histoFacet
operator|.
name|counts
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
return|return
name|firstHistoFacet
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
block|}
DECL|method|toXContent
annotation|@
name|Override
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
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|COUNT
argument_list|,
name|counts
index|[
name|i
index|]
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
name|InternalBoundedCountHistogramFacet
name|readHistogramFacet
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalBoundedCountHistogramFacet
name|facet
init|=
operator|new
name|InternalBoundedCountHistogramFacet
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
DECL|method|readFrom
annotation|@
name|Override
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
name|readUTF
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
name|counts
operator|=
operator|new
name|int
index|[
name|size
index|]
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
name|counts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|counts
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|writeUTF
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
name|out
operator|.
name|writeVInt
argument_list|(
name|counts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

