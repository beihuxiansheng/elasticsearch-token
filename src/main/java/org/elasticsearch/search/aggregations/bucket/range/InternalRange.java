begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
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
name|Lists
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
name|text
operator|.
name|StringText
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
name|text
operator|.
name|Text
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
name|search
operator|.
name|aggregations
operator|.
name|*
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
name|bucket
operator|.
name|BucketStreamContext
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
name|bucket
operator|.
name|BucketStreams
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
name|format
operator|.
name|ValueFormatter
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
name|format
operator|.
name|ValueFormatterStreams
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalRange
specifier|public
class|class
name|InternalRange
parameter_list|<
name|B
extends|extends
name|InternalRange
operator|.
name|Bucket
parameter_list|>
extends|extends
name|InternalMultiBucketAggregation
implements|implements
name|Range
block|{
DECL|field|FACTORY
specifier|static
specifier|final
name|Factory
name|FACTORY
init|=
operator|new
name|Factory
argument_list|()
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"range"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|private
specifier|final
specifier|static
name|AggregationStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|AggregationStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalRange
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalRange
name|ranges
init|=
operator|new
name|InternalRange
argument_list|()
decl_stmt|;
name|ranges
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|ranges
return|;
block|}
block|}
decl_stmt|;
DECL|field|BUCKET_STREAM
specifier|private
specifier|final
specifier|static
name|BucketStreams
operator|.
name|Stream
argument_list|<
name|Bucket
argument_list|>
name|BUCKET_STREAM
init|=
operator|new
name|BucketStreams
operator|.
name|Stream
argument_list|<
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Bucket
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|BucketStreamContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Bucket
name|buckets
init|=
operator|new
name|Bucket
argument_list|(
name|context
operator|.
name|keyed
argument_list|()
argument_list|,
name|context
operator|.
name|formatter
argument_list|()
argument_list|)
decl_stmt|;
name|buckets
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|buckets
return|;
block|}
annotation|@
name|Override
specifier|public
name|BucketStreamContext
name|getBucketStreamContext
parameter_list|(
name|Bucket
name|bucket
parameter_list|)
block|{
name|BucketStreamContext
name|context
init|=
operator|new
name|BucketStreamContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|formatter
argument_list|(
name|bucket
operator|.
name|formatter
argument_list|)
expr_stmt|;
name|context
operator|.
name|keyed
argument_list|(
name|bucket
operator|.
name|keyed
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStream
specifier|public
specifier|static
name|void
name|registerStream
parameter_list|()
block|{
name|AggregationStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
name|BucketStreams
operator|.
name|registerStream
argument_list|(
name|BUCKET_STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|Bucket
specifier|public
specifier|static
class|class
name|Bucket
extends|extends
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
implements|implements
name|Range
operator|.
name|Bucket
block|{
DECL|field|keyed
specifier|protected
specifier|transient
specifier|final
name|boolean
name|keyed
decl_stmt|;
DECL|field|formatter
specifier|protected
specifier|transient
specifier|final
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|field|from
specifier|private
name|double
name|from
decl_stmt|;
DECL|field|to
specifier|private
name|double
name|to
decl_stmt|;
DECL|field|docCount
specifier|private
name|long
name|docCount
decl_stmt|;
DECL|field|aggregations
name|InternalAggregations
name|aggregations
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|boolean
name|keyed
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|)
block|{
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
block|}
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|boolean
name|keyed
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|)
block|{
name|this
argument_list|(
name|keyed
argument_list|,
name|formatter
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
operator|!=
literal|null
condition|?
name|key
else|:
name|generateKey
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|formatter
argument_list|)
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|aggregations
operator|=
name|aggregations
expr_stmt|;
block|}
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsText
specifier|public
name|Text
name|getKeyAsText
parameter_list|()
block|{
return|return
operator|new
name|StringText
argument_list|(
name|getKey
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFrom
specifier|public
name|Number
name|getFrom
parameter_list|()
block|{
return|return
name|from
return|;
block|}
annotation|@
name|Override
DECL|method|getTo
specifier|public
name|Number
name|getTo
parameter_list|()
block|{
return|return
name|to
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|long
name|getDocCount
parameter_list|()
block|{
return|return
name|docCount
return|;
block|}
annotation|@
name|Override
DECL|method|getAggregations
specifier|public
name|Aggregations
name|getAggregations
parameter_list|()
block|{
return|return
name|aggregations
return|;
block|}
DECL|method|getFactory
specifier|protected
name|Factory
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|,
name|?
argument_list|>
name|getFactory
parameter_list|()
block|{
return|return
name|FACTORY
return|;
block|}
DECL|method|reduce
name|Bucket
name|reduce
parameter_list|(
name|List
argument_list|<
name|Bucket
argument_list|>
name|ranges
parameter_list|,
name|ReduceContext
name|context
parameter_list|)
block|{
name|long
name|docCount
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|InternalAggregations
argument_list|>
name|aggregationsList
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Bucket
name|range
range|:
name|ranges
control|)
block|{
name|docCount
operator|+=
name|range
operator|.
name|docCount
expr_stmt|;
name|aggregationsList
operator|.
name|add
argument_list|(
name|range
operator|.
name|aggregations
argument_list|)
expr_stmt|;
block|}
specifier|final
name|InternalAggregations
name|aggs
init|=
name|InternalAggregations
operator|.
name|reduce
argument_list|(
name|aggregationsList
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
name|getFactory
argument_list|()
operator|.
name|createBucket
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|docCount
argument_list|,
name|aggs
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|)
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
if|if
condition|(
name|keyed
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
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
name|CommonFields
operator|.
name|KEY
argument_list|,
name|key
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
name|from
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|FROM
argument_list|,
name|from
argument_list|)
expr_stmt|;
if|if
condition|(
name|formatter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|FROM_AS_STRING
argument_list|,
name|formatter
operator|.
name|format
argument_list|(
name|from
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|TO
argument_list|,
name|to
argument_list|)
expr_stmt|;
if|if
condition|(
name|formatter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|TO_AS_STRING
argument_list|,
name|formatter
operator|.
name|format
argument_list|(
name|to
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|DOC_COUNT
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|aggregations
operator|.
name|toXContentInternal
argument_list|(
name|builder
argument_list|,
name|params
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
DECL|method|generateKey
specifier|protected
name|String
name|generateKey
parameter_list|(
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|from
argument_list|)
condition|?
literal|"*"
else|:
name|formatter
operator|!=
literal|null
condition|?
name|formatter
operator|.
name|format
argument_list|(
name|from
argument_list|)
else|:
name|ValueFormatter
operator|.
name|RAW
operator|.
name|format
argument_list|(
name|from
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|to
argument_list|)
condition|?
literal|"*"
else|:
name|formatter
operator|!=
literal|null
condition|?
name|formatter
operator|.
name|format
argument_list|(
name|to
argument_list|)
else|:
name|ValueFormatter
operator|.
name|RAW
operator|.
name|format
argument_list|(
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
block|{          }
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
block|{          }
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
parameter_list|<
name|B
extends|extends
name|Bucket
parameter_list|,
name|R
extends|extends
name|InternalRange
parameter_list|<
name|B
parameter_list|>
parameter_list|>
block|{
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|create
specifier|public
name|R
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|B
argument_list|>
name|ranges
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|boolean
name|keyed
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
return|return
operator|(
name|R
operator|)
operator|new
name|InternalRange
argument_list|<>
argument_list|(
name|name
argument_list|,
name|ranges
argument_list|,
name|formatter
argument_list|,
name|keyed
argument_list|,
name|metaData
argument_list|)
return|;
block|}
DECL|method|createBucket
specifier|public
name|B
name|createBucket
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|boolean
name|keyed
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|)
block|{
return|return
operator|(
name|B
operator|)
operator|new
name|Bucket
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|)
return|;
block|}
block|}
DECL|field|ranges
specifier|private
name|List
argument_list|<
name|B
argument_list|>
name|ranges
decl_stmt|;
DECL|field|rangeMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|B
argument_list|>
name|rangeMap
decl_stmt|;
DECL|field|formatter
specifier|private
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|field|keyed
specifier|private
name|boolean
name|keyed
decl_stmt|;
DECL|method|InternalRange
specifier|public
name|InternalRange
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalRange
specifier|public
name|InternalRange
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|B
argument_list|>
name|ranges
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|boolean
name|keyed
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|getBuckets
specifier|public
name|List
argument_list|<
name|B
argument_list|>
name|getBuckets
parameter_list|()
block|{
return|return
name|ranges
return|;
block|}
annotation|@
name|Override
DECL|method|getBucketByKey
specifier|public
name|B
name|getBucketByKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|rangeMap
operator|==
literal|null
condition|)
block|{
name|rangeMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Range
operator|.
name|Bucket
name|bucket
range|:
name|ranges
control|)
block|{
name|rangeMap
operator|.
name|put
argument_list|(
name|bucket
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|B
operator|)
name|bucket
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rangeMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getFactory
specifier|protected
name|Factory
argument_list|<
name|B
argument_list|,
name|?
argument_list|>
name|getFactory
parameter_list|()
block|{
return|return
name|FACTORY
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalAggregation
name|reduce
parameter_list|(
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
init|=
name|reduceContext
operator|.
name|aggregations
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Bucket
argument_list|>
index|[]
name|rangeList
init|=
operator|new
name|List
index|[
name|ranges
operator|.
name|size
argument_list|()
index|]
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
name|rangeList
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|rangeList
index|[
name|i
index|]
operator|=
operator|new
name|ArrayList
argument_list|<
name|Bucket
argument_list|>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalRange
argument_list|<
name|?
argument_list|>
name|ranges
init|=
operator|(
name|InternalRange
argument_list|<
name|?
argument_list|>
operator|)
name|aggregation
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Bucket
name|range
range|:
name|ranges
operator|.
name|ranges
control|)
block|{
name|rangeList
index|[
name|i
operator|++
index|]
operator|.
name|add
argument_list|(
name|range
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|B
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|this
operator|.
name|ranges
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|ranges
operator|.
name|add
argument_list|(
operator|(
name|B
operator|)
name|rangeList
index|[
name|i
index|]
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reduce
argument_list|(
name|rangeList
index|[
name|i
index|]
argument_list|,
name|reduceContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getFactory
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ranges
argument_list|,
name|formatter
argument_list|,
name|keyed
argument_list|,
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|formatter
operator|=
name|ValueFormatterStreams
operator|.
name|readOptional
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|keyed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|B
argument_list|>
name|ranges
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|size
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
name|String
name|key
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
name|ranges
operator|.
name|add
argument_list|(
name|getFactory
argument_list|()
operator|.
name|createBucket
argument_list|(
name|key
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
name|InternalAggregations
operator|.
name|readAggregations
argument_list|(
name|in
argument_list|)
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|this
operator|.
name|rangeMap
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ValueFormatterStreams
operator|.
name|writeOptional
argument_list|(
name|formatter
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|keyed
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|B
name|bucket
range|:
name|ranges
control|)
block|{
name|out
operator|.
name|writeOptionalString
argument_list|(
operator|(
operator|(
name|Bucket
operator|)
name|bucket
operator|)
operator|.
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
operator|(
operator|(
name|Bucket
operator|)
name|bucket
operator|)
operator|.
name|from
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
operator|(
operator|(
name|Bucket
operator|)
name|bucket
operator|)
operator|.
name|to
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
operator|(
operator|(
name|Bucket
operator|)
name|bucket
operator|)
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|aggregations
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
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
name|keyed
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|CommonFields
operator|.
name|BUCKETS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|CommonFields
operator|.
name|BUCKETS
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|B
name|range
range|:
name|ranges
control|)
block|{
name|range
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keyed
condition|)
block|{
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

