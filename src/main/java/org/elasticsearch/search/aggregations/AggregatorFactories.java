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
name|PipelineAggregatorFactory
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
name|AggregationContext
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AggregatorFactories
specifier|public
class|class
name|AggregatorFactories
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|AggregatorFactories
name|EMPTY
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
DECL|field|parent
specifier|private
name|AggregatorFactory
name|parent
decl_stmt|;
DECL|field|factories
specifier|private
name|AggregatorFactory
index|[]
name|factories
decl_stmt|;
DECL|field|pipelineAggregatorFactories
specifier|private
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregatorFactories
decl_stmt|;
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|AggregatorFactories
specifier|private
name|AggregatorFactories
parameter_list|(
name|AggregatorFactory
index|[]
name|factories
parameter_list|,
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregators
parameter_list|)
block|{
name|this
operator|.
name|factories
operator|=
name|factories
expr_stmt|;
name|this
operator|.
name|pipelineAggregatorFactories
operator|=
name|pipelineAggregators
expr_stmt|;
block|}
DECL|method|createPipelineAggregators
specifier|public
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|createPipelineAggregators
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PipelineAggregatorFactory
name|factory
range|:
name|this
operator|.
name|pipelineAggregatorFactories
control|)
block|{
name|pipelineAggregators
operator|.
name|add
argument_list|(
name|factory
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|pipelineAggregators
return|;
block|}
comment|/**      * Create all aggregators so that they can be consumed with multiple      * buckets.      */
DECL|method|createSubAggregators
specifier|public
name|Aggregator
index|[]
name|createSubAggregators
parameter_list|(
name|Aggregator
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|Aggregator
index|[]
name|aggregators
init|=
operator|new
name|Aggregator
index|[
name|count
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
name|factories
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
comment|// TODO: sometimes even sub aggregations always get called with bucket 0, eg. if
comment|// you have a terms agg under a top-level filter agg. We should have a way to
comment|// propagate the fact that only bucket 0 will be collected with single-bucket
comment|// aggs
specifier|final
name|boolean
name|collectsFromSingleBucket
init|=
literal|false
decl_stmt|;
name|aggregators
index|[
name|i
index|]
operator|=
name|factories
index|[
name|i
index|]
operator|.
name|create
argument_list|(
name|parent
operator|.
name|context
argument_list|()
argument_list|,
name|parent
argument_list|,
name|collectsFromSingleBucket
argument_list|)
expr_stmt|;
block|}
return|return
name|aggregators
return|;
block|}
DECL|method|createTopLevelAggregators
specifier|public
name|Aggregator
index|[]
name|createTopLevelAggregators
parameter_list|(
name|AggregationContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
comment|// These aggregators are going to be used with a single bucket ordinal, no need to wrap the PER_BUCKET ones
name|Aggregator
index|[]
name|aggregators
init|=
operator|new
name|Aggregator
index|[
name|factories
operator|.
name|length
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
name|factories
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// top-level aggs only get called with bucket 0
specifier|final
name|boolean
name|collectsFromSingleBucket
init|=
literal|true
decl_stmt|;
name|aggregators
index|[
name|i
index|]
operator|=
name|factories
index|[
name|i
index|]
operator|.
name|create
argument_list|(
name|ctx
argument_list|,
literal|null
argument_list|,
name|collectsFromSingleBucket
argument_list|)
expr_stmt|;
block|}
return|return
name|aggregators
return|;
block|}
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|factories
operator|.
name|length
return|;
block|}
DECL|method|setParent
name|void
name|setParent
parameter_list|(
name|AggregatorFactory
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
for|for
control|(
name|AggregatorFactory
name|factory
range|:
name|factories
control|)
block|{
name|factory
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
block|}
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
block|{
for|for
control|(
name|AggregatorFactory
name|factory
range|:
name|factories
control|)
block|{
name|factory
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|PipelineAggregatorFactory
name|factory
range|:
name|pipelineAggregatorFactories
control|)
block|{
name|factory
operator|.
name|validate
argument_list|(
name|parent
argument_list|,
name|factories
argument_list|,
name|pipelineAggregatorFactories
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Empty
specifier|private
specifier|final
specifier|static
class|class
name|Empty
extends|extends
name|AggregatorFactories
block|{
DECL|field|EMPTY_FACTORIES
specifier|private
specifier|static
specifier|final
name|AggregatorFactory
index|[]
name|EMPTY_FACTORIES
init|=
operator|new
name|AggregatorFactory
index|[
literal|0
index|]
decl_stmt|;
DECL|field|EMPTY_AGGREGATORS
specifier|private
specifier|static
specifier|final
name|Aggregator
index|[]
name|EMPTY_AGGREGATORS
init|=
operator|new
name|Aggregator
index|[
literal|0
index|]
decl_stmt|;
DECL|field|EMPTY_PIPELINE_AGGREGATORS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|EMPTY_PIPELINE_AGGREGATORS
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Empty
specifier|private
name|Empty
parameter_list|()
block|{
name|super
argument_list|(
name|EMPTY_FACTORIES
argument_list|,
name|EMPTY_PIPELINE_AGGREGATORS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSubAggregators
specifier|public
name|Aggregator
index|[]
name|createSubAggregators
parameter_list|(
name|Aggregator
name|parent
parameter_list|)
block|{
return|return
name|EMPTY_AGGREGATORS
return|;
block|}
annotation|@
name|Override
DECL|method|createTopLevelAggregators
specifier|public
name|Aggregator
index|[]
name|createTopLevelAggregators
parameter_list|(
name|AggregationContext
name|ctx
parameter_list|)
block|{
return|return
name|EMPTY_AGGREGATORS
return|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|names
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|factories
specifier|private
specifier|final
name|List
argument_list|<
name|AggregatorFactory
argument_list|>
name|factories
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|pipelineAggregatorFactories
specifier|private
specifier|final
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregatorFactories
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|addAggregator
specifier|public
name|Builder
name|addAggregator
parameter_list|(
name|AggregatorFactory
name|factory
parameter_list|)
block|{
if|if
condition|(
operator|!
name|names
operator|.
name|add
argument_list|(
name|factory
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Two sibling aggregations cannot have the same name: ["
operator|+
name|factory
operator|.
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|factories
operator|.
name|add
argument_list|(
name|factory
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addPipelineAggregator
specifier|public
name|Builder
name|addPipelineAggregator
parameter_list|(
name|PipelineAggregatorFactory
name|pipelineAggregatorFactory
parameter_list|)
block|{
name|this
operator|.
name|pipelineAggregatorFactories
operator|.
name|add
argument_list|(
name|pipelineAggregatorFactory
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|AggregatorFactories
name|build
parameter_list|()
block|{
if|if
condition|(
name|factories
operator|.
name|isEmpty
argument_list|()
operator|&&
name|pipelineAggregatorFactories
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|orderedpipelineAggregators
init|=
name|resolvePipelineAggregatorOrder
argument_list|(
name|this
operator|.
name|pipelineAggregatorFactories
argument_list|,
name|this
operator|.
name|factories
argument_list|)
decl_stmt|;
return|return
operator|new
name|AggregatorFactories
argument_list|(
name|factories
operator|.
name|toArray
argument_list|(
operator|new
name|AggregatorFactory
index|[
name|factories
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|orderedpipelineAggregators
argument_list|)
return|;
block|}
DECL|method|resolvePipelineAggregatorOrder
specifier|private
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|resolvePipelineAggregatorOrder
parameter_list|(
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregatorFactories
parameter_list|,
name|List
argument_list|<
name|AggregatorFactory
argument_list|>
name|aggFactories
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregatorFactoriesMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PipelineAggregatorFactory
name|factory
range|:
name|pipelineAggregatorFactories
control|)
block|{
name|pipelineAggregatorFactoriesMap
operator|.
name|put
argument_list|(
name|factory
operator|.
name|getName
argument_list|()
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|aggFactoryNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregatorFactory
name|aggFactory
range|:
name|aggFactories
control|)
block|{
name|aggFactoryNames
operator|.
name|add
argument_list|(
name|aggFactory
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|orderedPipelineAggregatorrs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|unmarkedFactories
init|=
operator|new
name|ArrayList
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
argument_list|(
name|pipelineAggregatorFactories
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|temporarilyMarked
init|=
operator|new
name|HashSet
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|unmarkedFactories
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|PipelineAggregatorFactory
name|factory
init|=
name|unmarkedFactories
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|resolvePipelineAggregatorOrder
argument_list|(
name|aggFactoryNames
argument_list|,
name|pipelineAggregatorFactoriesMap
argument_list|,
name|orderedPipelineAggregatorrs
argument_list|,
name|unmarkedFactories
argument_list|,
name|temporarilyMarked
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
return|return
name|orderedPipelineAggregatorrs
return|;
block|}
DECL|method|resolvePipelineAggregatorOrder
specifier|private
name|void
name|resolvePipelineAggregatorOrder
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|aggFactoryNames
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregatorFactoriesMap
parameter_list|,
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|orderedPipelineAggregators
parameter_list|,
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|unmarkedFactories
parameter_list|,
name|Set
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|temporarilyMarked
parameter_list|,
name|PipelineAggregatorFactory
name|factory
parameter_list|)
block|{
if|if
condition|(
name|temporarilyMarked
operator|.
name|contains
argument_list|(
name|factory
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cyclical dependancy found with pipeline aggregator ["
operator|+
name|factory
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|unmarkedFactories
operator|.
name|contains
argument_list|(
name|factory
argument_list|)
condition|)
block|{
name|temporarilyMarked
operator|.
name|add
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|String
index|[]
name|bucketsPaths
init|=
name|factory
operator|.
name|getBucketsPaths
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|bucketsPath
range|:
name|bucketsPaths
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|bucketsPathElements
init|=
name|AggregationPath
operator|.
name|parse
argument_list|(
name|bucketsPath
argument_list|)
operator|.
name|getPathElementsAsStringList
argument_list|()
decl_stmt|;
name|String
name|firstAggName
init|=
name|bucketsPathElements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketsPath
operator|.
name|equals
argument_list|(
literal|"_count"
argument_list|)
operator|||
name|bucketsPath
operator|.
name|equals
argument_list|(
literal|"_key"
argument_list|)
operator|||
name|aggFactoryNames
operator|.
name|contains
argument_list|(
name|firstAggName
argument_list|)
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|PipelineAggregatorFactory
name|matchingFactory
init|=
name|pipelineAggregatorFactoriesMap
operator|.
name|get
argument_list|(
name|firstAggName
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingFactory
operator|!=
literal|null
condition|)
block|{
name|resolvePipelineAggregatorOrder
argument_list|(
name|aggFactoryNames
argument_list|,
name|pipelineAggregatorFactoriesMap
argument_list|,
name|orderedPipelineAggregators
argument_list|,
name|unmarkedFactories
argument_list|,
name|temporarilyMarked
argument_list|,
name|matchingFactory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No aggregation found for path ["
operator|+
name|bucketsPath
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|unmarkedFactories
operator|.
name|remove
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|temporarilyMarked
operator|.
name|remove
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|orderedPipelineAggregators
operator|.
name|add
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

