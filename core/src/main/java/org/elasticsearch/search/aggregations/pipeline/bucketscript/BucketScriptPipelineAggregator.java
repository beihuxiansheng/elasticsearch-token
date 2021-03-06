begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.bucketscript
package|package
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
name|bucketscript
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
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
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
name|AggregationExecutionException
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
name|InternalMultiBucketAggregation
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
name|BucketHelpers
operator|.
name|GapPolicy
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
name|InternalSimpleValue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
import|;
end_import

begin_import
import|import static
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
name|BucketHelpers
operator|.
name|resolveBucketValue
import|;
end_import

begin_class
DECL|class|BucketScriptPipelineAggregator
specifier|public
class|class
name|BucketScriptPipelineAggregator
extends|extends
name|PipelineAggregator
block|{
DECL|field|formatter
specifier|private
specifier|final
name|DocValueFormat
name|formatter
decl_stmt|;
DECL|field|gapPolicy
specifier|private
specifier|final
name|GapPolicy
name|gapPolicy
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|bucketsPathsMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bucketsPathsMap
decl_stmt|;
DECL|method|BucketScriptPipelineAggregator
specifier|public
name|BucketScriptPipelineAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bucketsPathsMap
parameter_list|,
name|Script
name|script
parameter_list|,
name|DocValueFormat
name|formatter
parameter_list|,
name|GapPolicy
name|gapPolicy
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bucketsPathsMap
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|bucketsPathsMap
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|this
operator|.
name|bucketsPathsMap
operator|=
name|bucketsPathsMap
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|this
operator|.
name|gapPolicy
operator|=
name|gapPolicy
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|BucketScriptPipelineAggregator
specifier|public
name|BucketScriptPipelineAggregator
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
name|script
operator|=
operator|new
name|Script
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|formatter
operator|=
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|DocValueFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|gapPolicy
operator|=
name|GapPolicy
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|bucketsPathsMap
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|in
operator|.
name|readGenericValue
argument_list|()
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
name|script
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeNamedWriteable
argument_list|(
name|formatter
argument_list|)
expr_stmt|;
name|gapPolicy
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|bucketsPathsMap
argument_list|)
expr_stmt|;
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
name|BucketScriptPipelineAggregationBuilder
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalAggregation
name|reduce
parameter_list|(
name|InternalAggregation
name|aggregation
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|InternalMultiBucketAggregation
argument_list|<
name|InternalMultiBucketAggregation
argument_list|,
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
argument_list|>
name|originalAgg
init|=
operator|(
name|InternalMultiBucketAggregation
argument_list|<
name|InternalMultiBucketAggregation
argument_list|,
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
argument_list|>
operator|)
name|aggregation
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
argument_list|>
name|buckets
init|=
name|originalAgg
operator|.
name|getBuckets
argument_list|()
decl_stmt|;
name|ExecutableScript
operator|.
name|Factory
name|factory
init|=
name|reduceContext
operator|.
name|scriptService
argument_list|()
operator|.
name|compile
argument_list|(
name|script
argument_list|,
name|ExecutableScript
operator|.
name|AGGS_CONTEXT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
argument_list|>
name|newBuckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
name|bucket
range|:
name|buckets
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|script
operator|.
name|getParams
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|vars
operator|.
name|putAll
argument_list|(
name|script
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|skipBucket
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|bucketsPathsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|varName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|bucketsPath
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Double
name|value
init|=
name|resolveBucketValue
argument_list|(
name|originalAgg
argument_list|,
name|bucket
argument_list|,
name|bucketsPath
argument_list|,
name|gapPolicy
argument_list|)
decl_stmt|;
if|if
condition|(
name|GapPolicy
operator|.
name|SKIP
operator|==
name|gapPolicy
operator|&&
operator|(
name|value
operator|==
literal|null
operator|||
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|)
condition|)
block|{
name|skipBucket
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|vars
operator|.
name|put
argument_list|(
name|varName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|skipBucket
condition|)
block|{
name|newBuckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ExecutableScript
name|executableScript
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|vars
argument_list|)
decl_stmt|;
name|Object
name|returned
init|=
name|executableScript
operator|.
name|run
argument_list|()
decl_stmt|;
if|if
condition|(
name|returned
operator|==
literal|null
condition|)
block|{
name|newBuckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|returned
operator|instanceof
name|Number
operator|)
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"series_arithmetic script for reducer ["
operator|+
name|name
argument_list|()
operator|+
literal|"] must return a Number"
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggs
init|=
name|StreamSupport
operator|.
name|stream
argument_list|(
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
parameter_list|(
name|p
parameter_list|)
lambda|->
operator|(
name|InternalAggregation
operator|)
name|p
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|aggs
operator|.
name|add
argument_list|(
operator|new
name|InternalSimpleValue
argument_list|(
name|name
argument_list|()
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|returned
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|formatter
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
name|metaData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
name|newBucket
init|=
name|originalAgg
operator|.
name|createBucket
argument_list|(
operator|new
name|InternalAggregations
argument_list|(
name|aggs
argument_list|)
argument_list|,
name|bucket
argument_list|)
decl_stmt|;
name|newBuckets
operator|.
name|add
argument_list|(
name|newBucket
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|originalAgg
operator|.
name|create
argument_list|(
name|newBuckets
argument_list|)
return|;
block|}
block|}
end_class

end_unit

