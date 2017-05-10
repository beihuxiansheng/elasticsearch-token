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
name|terms
operator|.
name|InternalTerms
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
name|terms
operator|.
name|UnmappedTerms
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_comment
comment|/**  * Result of the running the significant terms aggregation on an unmapped field.  */
end_comment

begin_class
DECL|class|UnmappedSignificantTerms
specifier|public
class|class
name|UnmappedSignificantTerms
extends|extends
name|InternalSignificantTerms
argument_list|<
name|UnmappedSignificantTerms
argument_list|,
name|UnmappedSignificantTerms
operator|.
name|Bucket
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"umsigterms"
decl_stmt|;
comment|/**      * Concrete type that can't be built because Java needs a concrete type so {@link InternalTerms.Bucket} can have a self type but      * {@linkplain UnmappedTerms} doesn't ever need to build it because it never returns any buckets.      */
DECL|class|Bucket
specifier|protected
specifier|abstract
specifier|static
class|class
name|Bucket
extends|extends
name|InternalSignificantTerms
operator|.
name|Bucket
argument_list|<
name|Bucket
argument_list|>
block|{
DECL|method|Bucket
specifier|private
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
parameter_list|,
name|DocValueFormat
name|format
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
argument_list|,
name|format
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|UnmappedSignificantTerms
specifier|public
name|UnmappedSignificantTerms
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|requiredSize
parameter_list|,
name|long
name|minDocCount
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
name|requiredSize
argument_list|,
name|minDocCount
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|UnmappedSignificantTerms
specifier|public
name|UnmappedSignificantTerms
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTermTypeInfoTo
specifier|protected
name|void
name|writeTermTypeInfoTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Nothing to write
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
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
name|SignificantStringTerms
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|UnmappedSignificantTerms
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
name|UnmappedSignificantTerms
argument_list|(
name|name
argument_list|,
name|requiredSize
argument_list|,
name|minDocCount
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not supported for UnmappedSignificantTerms"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|UnmappedSignificantTerms
name|create
parameter_list|(
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|List
argument_list|<
name|Bucket
argument_list|>
name|buckets
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not supported for UnmappedSignificantTerms"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
name|InternalAggregation
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|aggregation
operator|instanceof
name|UnmappedSignificantTerms
operator|)
condition|)
block|{
return|return
name|aggregation
operator|.
name|reduce
argument_list|(
name|aggregations
argument_list|,
name|reduceContext
argument_list|)
return|;
block|}
block|}
return|return
name|this
return|;
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
name|startArray
argument_list|(
name|CommonFields
operator|.
name|BUCKETS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|createBucketsArray
specifier|protected
name|Bucket
index|[]
name|createBucketsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|Bucket
index|[
name|size
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getBucketsInternal
specifier|protected
name|List
argument_list|<
name|Bucket
argument_list|>
name|getBucketsInternal
parameter_list|()
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBucketByKey
specifier|public
name|SignificantTerms
operator|.
name|Bucket
name|getBucketByKey
parameter_list|(
name|String
name|term
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSignificanceHeuristic
specifier|protected
name|SignificanceHeuristic
name|getSignificanceHeuristic
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSubsetSize
specifier|protected
name|long
name|getSubsetSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getSupersetSize
specifier|protected
name|long
name|getSupersetSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

