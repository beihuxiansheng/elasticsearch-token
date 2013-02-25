begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|range
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
name|collect
operator|.
name|ImmutableList
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
name|InternalFacet
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
DECL|class|InternalRangeFacet
specifier|public
class|class
name|InternalRangeFacet
extends|extends
name|InternalFacet
implements|implements
name|RangeFacet
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
literal|"range"
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
name|readRangeFacet
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
DECL|field|entries
name|Entry
index|[]
name|entries
decl_stmt|;
DECL|method|InternalRangeFacet
name|InternalRangeFacet
parameter_list|()
block|{     }
DECL|method|InternalRangeFacet
specifier|public
name|InternalRangeFacet
parameter_list|(
name|String
name|name
parameter_list|,
name|Entry
index|[]
name|entries
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
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
name|RangeFacet
operator|.
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|getEntries
specifier|public
name|List
argument_list|<
name|Entry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|entries
argument_list|)
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
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
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
return|return
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|InternalRangeFacet
name|agg
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Facet
name|facet
range|:
name|facets
control|)
block|{
name|InternalRangeFacet
name|geoDistanceFacet
init|=
operator|(
name|InternalRangeFacet
operator|)
name|facet
decl_stmt|;
if|if
condition|(
name|agg
operator|==
literal|null
condition|)
block|{
name|agg
operator|=
name|geoDistanceFacet
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|geoDistanceFacet
operator|.
name|entries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RangeFacet
operator|.
name|Entry
name|aggEntry
init|=
name|agg
operator|.
name|entries
index|[
name|i
index|]
decl_stmt|;
name|RangeFacet
operator|.
name|Entry
name|currentEntry
init|=
name|geoDistanceFacet
operator|.
name|entries
index|[
name|i
index|]
decl_stmt|;
name|aggEntry
operator|.
name|count
operator|+=
name|currentEntry
operator|.
name|count
expr_stmt|;
name|aggEntry
operator|.
name|totalCount
operator|+=
name|currentEntry
operator|.
name|totalCount
expr_stmt|;
name|aggEntry
operator|.
name|total
operator|+=
name|currentEntry
operator|.
name|total
expr_stmt|;
if|if
condition|(
name|currentEntry
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
name|currentEntry
operator|.
name|min
expr_stmt|;
block|}
if|if
condition|(
name|currentEntry
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
name|currentEntry
operator|.
name|max
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|agg
return|;
block|}
DECL|method|readRangeFacet
specifier|public
specifier|static
name|InternalRangeFacet
name|readRangeFacet
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalRangeFacet
name|facet
init|=
operator|new
name|InternalRangeFacet
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
name|entries
operator|=
operator|new
name|Entry
index|[
name|in
operator|.
name|readVInt
argument_list|()
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
name|entries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Entry
name|entry
init|=
operator|new
name|Entry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|from
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|entry
operator|.
name|to
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|entry
operator|.
name|fromAsString
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|entry
operator|.
name|toAsString
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|entry
operator|.
name|count
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|totalCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|total
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|entry
operator|.
name|min
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|entry
operator|.
name|max
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|entries
index|[
name|i
index|]
operator|=
name|entry
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
name|writeVInt
argument_list|(
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|entries
control|)
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|entry
operator|.
name|from
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|entry
operator|.
name|to
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|fromAsString
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
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|fromAsString
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|toAsString
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
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|toAsString
argument_list|)
expr_stmt|;
block|}
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
DECL|field|RANGES
specifier|static
specifier|final
name|XContentBuilderString
name|RANGES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"ranges"
argument_list|)
decl_stmt|;
DECL|field|FROM
specifier|static
specifier|final
name|XContentBuilderString
name|FROM
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"from"
argument_list|)
decl_stmt|;
DECL|field|FROM_STR
specifier|static
specifier|final
name|XContentBuilderString
name|FROM_STR
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"from_str"
argument_list|)
decl_stmt|;
DECL|field|TO
specifier|static
specifier|final
name|XContentBuilderString
name|TO
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"to"
argument_list|)
decl_stmt|;
DECL|field|TO_STR
specifier|static
specifier|final
name|XContentBuilderString
name|TO_STR
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"to_str"
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
literal|"range"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|RANGES
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|entries
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|entry
operator|.
name|from
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FROM
argument_list|,
name|entry
operator|.
name|from
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|fromAsString
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FROM_STR
argument_list|,
name|entry
operator|.
name|fromAsString
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|entry
operator|.
name|to
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TO
argument_list|,
name|entry
operator|.
name|to
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|toAsString
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TO_STR
argument_list|,
name|entry
operator|.
name|toAsString
argument_list|)
expr_stmt|;
block|}
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
comment|// only output min and max if there are actually documents matching this range...
if|if
condition|(
name|entry
operator|.
name|getTotalCount
argument_list|()
operator|>
literal|0
condition|)
block|{
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
block|}
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
block|}
end_class

end_unit

