begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|base
operator|.
name|Function
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|Iterators
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|InternalAggregation
operator|.
name|ReduceContext
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
name|aggregations
operator|.
name|support
operator|.
name|AggregationPath
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_comment
comment|/**  * An internal implementation of {@link Aggregations}.  */
end_comment

begin_class
DECL|class|InternalAggregations
specifier|public
class|class
name|InternalAggregations
implements|implements
name|Aggregations
implements|,
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|EMPTY
specifier|public
specifier|final
specifier|static
name|InternalAggregations
name|EMPTY
init|=
operator|new
name|InternalAggregations
argument_list|()
decl_stmt|;
DECL|field|SUPERTYPE_CAST
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|InternalAggregation
argument_list|,
name|Aggregation
argument_list|>
name|SUPERTYPE_CAST
init|=
operator|new
name|Function
argument_list|<
name|InternalAggregation
argument_list|,
name|Aggregation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Aggregation
name|apply
parameter_list|(
name|InternalAggregation
name|input
parameter_list|)
block|{
return|return
name|input
return|;
block|}
block|}
decl_stmt|;
DECL|field|aggregations
specifier|private
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|aggregationsAsMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|InternalAggregation
argument_list|>
name|aggregationsAsMap
decl_stmt|;
DECL|method|InternalAggregations
specifier|private
name|InternalAggregations
parameter_list|()
block|{     }
comment|/**      * Constructs a new addAggregation.      */
DECL|method|InternalAggregations
specifier|public
name|InternalAggregations
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|)
block|{
name|this
operator|.
name|aggregations
operator|=
name|aggregations
expr_stmt|;
block|}
comment|/**      * Iterates over the {@link Aggregation}s.      */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Aggregation
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|aggregations
operator|.
name|iterator
argument_list|()
argument_list|,
name|SUPERTYPE_CAST
argument_list|)
return|;
block|}
comment|/**      * The list of {@link Aggregation}s.      */
DECL|method|asList
specifier|public
name|List
argument_list|<
name|Aggregation
argument_list|>
name|asList
parameter_list|()
block|{
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|aggregations
argument_list|,
name|SUPERTYPE_CAST
argument_list|)
return|;
block|}
comment|/**      * Returns the {@link Aggregation}s keyed by map.      */
DECL|method|asMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Aggregation
argument_list|>
name|asMap
parameter_list|()
block|{
return|return
name|getAsMap
argument_list|()
return|;
block|}
comment|/**      * Returns the {@link Aggregation}s keyed by map.      */
DECL|method|getAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Aggregation
argument_list|>
name|getAsMap
parameter_list|()
block|{
if|if
condition|(
name|aggregationsAsMap
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|InternalAggregation
argument_list|>
name|aggregationsAsMap
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|aggregationsAsMap
operator|.
name|put
argument_list|(
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|,
name|aggregation
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|aggregationsAsMap
operator|=
name|aggregationsAsMap
expr_stmt|;
block|}
return|return
name|Maps
operator|.
name|transformValues
argument_list|(
name|aggregationsAsMap
argument_list|,
name|SUPERTYPE_CAST
argument_list|)
return|;
block|}
comment|/**      * @return the aggregation of the specified name.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|get
specifier|public
parameter_list|<
name|A
extends|extends
name|Aggregation
parameter_list|>
name|A
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|A
operator|)
name|asMap
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|AggregationPath
name|aggPath
init|=
name|AggregationPath
operator|.
name|parse
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|getProperty
argument_list|(
name|aggPath
operator|.
name|getPathElementsAsStringList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
name|String
name|aggName
init|=
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|InternalAggregation
name|aggregation
init|=
name|get
argument_list|(
name|aggName
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Cannot find an aggregation named ["
operator|+
name|aggName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|aggregation
operator|.
name|getProperty
argument_list|(
name|path
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|path
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Reduces the given lists of addAggregation.      *      * @param aggregationsList  A list of aggregation to reduce      * @return                  The reduced addAggregation      */
DECL|method|reduce
specifier|public
specifier|static
name|InternalAggregations
name|reduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregations
argument_list|>
name|aggregationsList
parameter_list|,
name|ReduceContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|aggregationsList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// first we collect all aggregations of the same type and list them together
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|InternalAggregation
argument_list|>
argument_list|>
name|aggByName
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalAggregations
name|aggregations
range|:
name|aggregationsList
control|)
block|{
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
operator|.
name|aggregations
control|)
block|{
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggs
init|=
name|aggByName
operator|.
name|get
argument_list|(
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggs
operator|==
literal|null
condition|)
block|{
name|aggs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|aggregationsList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|aggByName
operator|.
name|put
argument_list|(
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|,
name|aggs
argument_list|)
expr_stmt|;
block|}
name|aggs
operator|.
name|add
argument_list|(
name|aggregation
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now we can use the first aggregation of each list to handle the reduce of its list
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|reducedAggregations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|InternalAggregation
argument_list|>
argument_list|>
name|entry
range|:
name|aggByName
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|InternalAggregation
name|first
init|=
name|aggregations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// the list can't be empty as it's created on demand
name|reducedAggregations
operator|.
name|add
argument_list|(
name|first
operator|.
name|reduce
argument_list|(
operator|new
name|InternalAggregation
operator|.
name|ReduceContext
argument_list|(
name|aggregations
argument_list|,
name|context
operator|.
name|bigArrays
argument_list|()
argument_list|,
name|context
operator|.
name|scriptService
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalAggregations
argument_list|(
name|reducedAggregations
argument_list|)
return|;
block|}
comment|/** The fields required to write this addAggregation to xcontent */
DECL|class|Fields
specifier|static
class|class
name|Fields
block|{
DECL|field|AGGREGATIONS
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|AGGREGATIONS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"aggregations"
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
if|if
condition|(
name|aggregations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|builder
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|AGGREGATIONS
argument_list|)
expr_stmt|;
name|toXContentInternal
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
comment|/**      * Directly write all the addAggregation without their bounding object. Used by sub-addAggregation (non top level addAggregation)      */
DECL|method|toXContentInternal
specifier|public
name|XContentBuilder
name|toXContentInternal
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
for|for
control|(
name|Aggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
operator|(
operator|(
name|InternalAggregation
operator|)
name|aggregation
operator|)
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|readAggregations
specifier|public
specifier|static
name|InternalAggregations
name|readAggregations
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalAggregations
name|result
init|=
operator|new
name|InternalAggregations
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|readOptionalAggregations
specifier|public
specifier|static
name|InternalAggregations
name|readOptionalAggregations
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readOptionalStreamable
argument_list|(
operator|new
name|InternalAggregations
argument_list|()
argument_list|)
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|aggregations
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
name|aggregationsAsMap
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aggregations
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|BytesReference
name|type
init|=
name|in
operator|.
name|readBytesReference
argument_list|()
decl_stmt|;
name|InternalAggregation
name|aggregation
init|=
name|AggregationStreams
operator|.
name|stream
argument_list|(
name|type
argument_list|)
operator|.
name|readResult
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|aggregations
operator|.
name|add
argument_list|(
name|aggregation
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
name|writeVInt
argument_list|(
name|aggregations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Aggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalAggregation
name|internal
init|=
operator|(
name|InternalAggregation
operator|)
name|aggregation
decl_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|internal
operator|.
name|type
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
name|internal
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

