begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
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
name|terms
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
name|search
operator|.
name|DocValueFormat
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
name|AggregationStreams
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
name|InternalAggregations
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
name|pipeline
operator|.
name|PipelineAggregator
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
DECL|class|LongTerms
specifier|public
class|class
name|LongTerms
extends|extends
name|InternalTerms
argument_list|<
name|LongTerms
argument_list|,
name|LongTerms
operator|.
name|Bucket
argument_list|>
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"terms"
argument_list|,
literal|"lterms"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|static
specifier|final
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
name|LongTerms
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|LongTerms
name|buckets
init|=
operator|new
name|LongTerms
argument_list|()
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
name|format
argument_list|()
argument_list|,
operator|(
name|boolean
operator|)
name|context
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"showDocCountError"
argument_list|)
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|put
argument_list|(
literal|"showDocCountError"
argument_list|,
name|bucket
operator|.
name|showDocCountError
argument_list|)
expr_stmt|;
name|context
operator|.
name|attributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|context
operator|.
name|format
argument_list|(
name|bucket
operator|.
name|format
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
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
specifier|static
class|class
name|Bucket
extends|extends
name|InternalTerms
operator|.
name|Bucket
block|{
DECL|field|term
name|long
name|term
decl_stmt|;
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|DocValueFormat
name|format
parameter_list|,
name|boolean
name|showDocCountError
parameter_list|)
block|{
name|super
argument_list|(
name|format
argument_list|,
name|showDocCountError
argument_list|)
expr_stmt|;
block|}
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|long
name|term
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|boolean
name|showDocCountError
parameter_list|,
name|long
name|docCountError
parameter_list|,
name|DocValueFormat
name|format
parameter_list|)
block|{
name|super
argument_list|(
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|showDocCountError
argument_list|,
name|docCountError
argument_list|,
name|format
argument_list|)
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsString
specifier|public
name|String
name|getKeyAsString
parameter_list|()
block|{
return|return
name|format
operator|.
name|format
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsNumber
specifier|public
name|Number
name|getKeyAsNumber
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|compareTerm
name|int
name|compareTerm
parameter_list|(
name|Terms
operator|.
name|Bucket
name|other
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|term
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|other
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newBucket
name|Bucket
name|newBucket
parameter_list|(
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggs
parameter_list|,
name|long
name|docCountError
parameter_list|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
name|term
argument_list|,
name|docCount
argument_list|,
name|aggs
argument_list|,
name|showDocCountError
argument_list|,
name|docCountError
argument_list|,
name|format
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
name|term
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|docCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|docCountError
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|showDocCountError
condition|)
block|{
name|docCountError
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|aggregations
operator|=
name|InternalAggregations
operator|.
name|readAggregations
argument_list|(
name|in
argument_list|)
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
name|term
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|showDocCountError
condition|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|docCountError
argument_list|)
expr_stmt|;
block|}
name|aggregations
operator|.
name|writeTo
argument_list|(
name|out
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
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|format
operator|!=
name|DocValueFormat
operator|.
name|RAW
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|KEY_AS_STRING
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|DOC_COUNT
argument_list|,
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|showDocCountError
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|InternalTerms
operator|.
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
argument_list|,
name|getDocCountError
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
DECL|method|LongTerms
name|LongTerms
parameter_list|()
block|{}
comment|// for serialization
DECL|method|LongTerms
specifier|public
name|LongTerms
parameter_list|(
name|String
name|name
parameter_list|,
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|DocValueFormat
name|format
parameter_list|,
name|int
name|requiredSize
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|long
name|minDocCount
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|InternalTerms
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|,
name|boolean
name|showTermDocCountError
parameter_list|,
name|long
name|docCountError
parameter_list|,
name|long
name|otherDocCount
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
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
name|order
argument_list|,
name|format
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|buckets
argument_list|,
name|showTermDocCountError
argument_list|,
name|docCountError
argument_list|,
name|otherDocCount
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
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
DECL|method|create
specifier|public
name|LongTerms
name|create
parameter_list|(
name|List
argument_list|<
name|Bucket
argument_list|>
name|buckets
parameter_list|)
block|{
return|return
operator|new
name|LongTerms
argument_list|(
name|this
operator|.
name|name
argument_list|,
name|this
operator|.
name|order
argument_list|,
name|this
operator|.
name|format
argument_list|,
name|this
operator|.
name|requiredSize
argument_list|,
name|this
operator|.
name|shardSize
argument_list|,
name|this
operator|.
name|minDocCount
argument_list|,
name|buckets
argument_list|,
name|this
operator|.
name|showTermDocCountError
argument_list|,
name|this
operator|.
name|docCountError
argument_list|,
name|this
operator|.
name|otherDocCount
argument_list|,
name|this
operator|.
name|pipelineAggregators
argument_list|()
argument_list|,
name|this
operator|.
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createBucket
specifier|public
name|Bucket
name|createBucket
parameter_list|(
name|InternalAggregations
name|aggregations
parameter_list|,
name|Bucket
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
name|prototype
operator|.
name|term
argument_list|,
name|prototype
operator|.
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|prototype
operator|.
name|showDocCountError
argument_list|,
name|prototype
operator|.
name|docCountError
argument_list|,
name|prototype
operator|.
name|format
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|LongTerms
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
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
name|terms
operator|.
name|InternalTerms
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|,
name|long
name|docCountError
parameter_list|,
name|long
name|otherDocCount
parameter_list|,
name|InternalTerms
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|LongTerms
argument_list|(
name|name
argument_list|,
name|prototype
operator|.
name|order
argument_list|,
operator|(
operator|(
name|LongTerms
operator|)
name|prototype
operator|)
operator|.
name|format
argument_list|,
name|prototype
operator|.
name|requiredSize
argument_list|,
name|prototype
operator|.
name|shardSize
argument_list|,
name|prototype
operator|.
name|minDocCount
argument_list|,
name|buckets
argument_list|,
name|prototype
operator|.
name|showTermDocCountError
argument_list|,
name|docCountError
argument_list|,
name|otherDocCount
argument_list|,
name|prototype
operator|.
name|pipelineAggregators
argument_list|()
argument_list|,
name|prototype
operator|.
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
name|this
operator|.
name|docCountError
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|InternalOrder
operator|.
name|Streams
operator|.
name|readOrder
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|in
operator|.
name|readValueFormat
argument_list|()
expr_stmt|;
name|this
operator|.
name|requiredSize
operator|=
name|readSize
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardSize
operator|=
name|readSize
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|showTermDocCountError
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|minDocCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|otherDocCount
operator|=
name|in
operator|.
name|readVLong
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
name|InternalTerms
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|Bucket
name|bucket
init|=
operator|new
name|Bucket
argument_list|(
name|format
argument_list|,
name|showTermDocCountError
argument_list|)
decl_stmt|;
name|bucket
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|buckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
name|this
operator|.
name|bucketMap
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
name|out
operator|.
name|writeLong
argument_list|(
name|docCountError
argument_list|)
expr_stmt|;
name|InternalOrder
operator|.
name|Streams
operator|.
name|writeOrder
argument_list|(
name|order
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeValueFormat
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|writeSize
argument_list|(
name|requiredSize
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|writeSize
argument_list|(
name|shardSize
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|showTermDocCountError
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|minDocCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|otherDocCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|InternalTerms
operator|.
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|bucket
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
name|builder
operator|.
name|field
argument_list|(
name|InternalTerms
operator|.
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
argument_list|,
name|docCountError
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SUM_OF_OTHER_DOC_COUNTS
argument_list|,
name|otherDocCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|CommonFields
operator|.
name|BUCKETS
argument_list|)
expr_stmt|;
for|for
control|(
name|InternalTerms
operator|.
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|bucket
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

