begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant
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
name|significant
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|BytesArray
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
name|BytesText
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
name|InternalAggregation
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
name|significant
operator|.
name|heuristics
operator|.
name|SignificanceHeuristic
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
name|significant
operator|.
name|heuristics
operator|.
name|SignificanceHeuristicStreams
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
name|Collection
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
DECL|class|SignificantStringTerms
specifier|public
class|class
name|SignificantStringTerms
extends|extends
name|InternalSignificantTerms
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|InternalAggregation
operator|.
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"significant_terms"
argument_list|,
literal|"sigsterms"
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
name|SignificantStringTerms
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SignificantStringTerms
name|buckets
init|=
operator|new
name|SignificantStringTerms
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
block|}
DECL|class|Bucket
specifier|public
specifier|static
class|class
name|Bucket
extends|extends
name|InternalSignificantTerms
operator|.
name|Bucket
block|{
DECL|field|termBytes
name|BytesRef
name|termBytes
decl_stmt|;
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|subsetDf
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetDf
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
block|{
name|super
argument_list|(
name|subsetDf
argument_list|,
name|subsetSize
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|,
name|aggregations
argument_list|)
expr_stmt|;
name|this
operator|.
name|termBytes
operator|=
name|term
expr_stmt|;
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
name|BytesText
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|termBytes
argument_list|)
argument_list|)
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
comment|// this method is needed for scripted numeric aggregations
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|termBytes
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTerm
name|int
name|compareTerm
parameter_list|(
name|SignificantTerms
operator|.
name|Bucket
name|other
parameter_list|)
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|termBytes
argument_list|,
operator|(
operator|(
name|Bucket
operator|)
name|other
operator|)
operator|.
name|termBytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|termBytes
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newBucket
name|Bucket
name|newBucket
parameter_list|(
name|long
name|subsetDf
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetDf
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
name|termBytes
argument_list|,
name|subsetDf
argument_list|,
name|subsetSize
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|,
name|aggregations
argument_list|)
return|;
block|}
block|}
DECL|method|SignificantStringTerms
name|SignificantStringTerms
parameter_list|()
block|{}
comment|// for serialization
DECL|method|SignificantStringTerms
specifier|public
name|SignificantStringTerms
parameter_list|(
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|requiredSize
parameter_list|,
name|long
name|minDocCount
parameter_list|,
name|SignificanceHeuristic
name|significanceHeuristic
parameter_list|,
name|Collection
argument_list|<
name|InternalSignificantTerms
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|)
block|{
name|super
argument_list|(
name|subsetSize
argument_list|,
name|supersetSize
argument_list|,
name|name
argument_list|,
name|requiredSize
argument_list|,
name|minDocCount
argument_list|,
name|significanceHeuristic
argument_list|,
name|buckets
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
DECL|method|newAggregation
name|InternalSignificantTerms
name|newAggregation
parameter_list|(
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|List
argument_list|<
name|InternalSignificantTerms
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|)
block|{
return|return
operator|new
name|SignificantStringTerms
argument_list|(
name|subsetSize
argument_list|,
name|supersetSize
argument_list|,
name|getName
argument_list|()
argument_list|,
name|requiredSize
argument_list|,
name|minDocCount
argument_list|,
name|significanceHeuristic
argument_list|,
name|buckets
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
name|this
operator|.
name|name
operator|=
name|in
operator|.
name|readString
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
name|minDocCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|subsetSize
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|supersetSize
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|significanceHeuristic
operator|=
name|SignificanceHeuristicStreams
operator|.
name|read
argument_list|(
name|in
argument_list|)
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
name|InternalSignificantTerms
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
name|BytesRef
name|term
init|=
name|in
operator|.
name|readBytesRef
argument_list|()
decl_stmt|;
name|long
name|subsetDf
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|long
name|supersetDf
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|Bucket
name|readBucket
init|=
operator|new
name|Bucket
argument_list|(
name|term
argument_list|,
name|subsetDf
argument_list|,
name|subsetSize
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|,
name|InternalAggregations
operator|.
name|readAggregations
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|readBucket
operator|.
name|updateScore
argument_list|(
name|significanceHeuristic
argument_list|)
expr_stmt|;
name|buckets
operator|.
name|add
argument_list|(
name|readBucket
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
name|writeSize
argument_list|(
name|requiredSize
argument_list|,
name|out
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
name|subsetSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|supersetSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
name|significanceHeuristic
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
name|InternalSignificantTerms
operator|.
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|out
operator|.
name|writeBytesRef
argument_list|(
operator|(
operator|(
name|Bucket
operator|)
name|bucket
operator|)
operator|.
name|termBytes
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
name|subsetDf
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
name|supersetDf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|InternalAggregations
operator|)
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|)
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
literal|"doc_count"
argument_list|,
name|subsetSize
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
name|InternalSignificantTerms
operator|.
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
comment|//There is a condition (presumably when only one shard has a bucket?) where reduce is not called
comment|// and I end up with buckets that contravene the user's min_doc_count criteria in my reducer
if|if
condition|(
name|bucket
operator|.
name|subsetDf
operator|>=
name|minDocCount
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|utf8Field
argument_list|(
name|CommonFields
operator|.
name|KEY
argument_list|,
operator|(
operator|(
name|Bucket
operator|)
name|bucket
operator|)
operator|.
name|termBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|DOC_COUNT
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"score"
argument_list|,
name|bucket
operator|.
name|score
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"bg_count"
argument_list|,
name|bucket
operator|.
name|supersetDf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|InternalAggregations
operator|)
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|)
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
block|}
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

