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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|BestBucketsDeferringCollector
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
name|DeferringBucketCollector
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
name|internal
operator|.
name|SearchContext
operator|.
name|Lifetime
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
name|query
operator|.
name|QueryPhaseExecutionException
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
comment|/**  * Base implementation for concrete aggregators.  */
end_comment

begin_class
DECL|class|AggregatorBase
specifier|public
specifier|abstract
class|class
name|AggregatorBase
extends|extends
name|Aggregator
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|parent
specifier|protected
specifier|final
name|Aggregator
name|parent
decl_stmt|;
DECL|field|context
specifier|protected
specifier|final
name|AggregationContext
name|context
decl_stmt|;
DECL|field|metaData
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
decl_stmt|;
DECL|field|subAggregators
specifier|protected
specifier|final
name|Aggregator
index|[]
name|subAggregators
decl_stmt|;
DECL|field|collectableSubAggregators
specifier|protected
name|BucketCollector
name|collectableSubAggregators
decl_stmt|;
DECL|field|subAggregatorbyName
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Aggregator
argument_list|>
name|subAggregatorbyName
decl_stmt|;
DECL|field|recordingWrapper
specifier|private
name|DeferringBucketCollector
name|recordingWrapper
decl_stmt|;
DECL|field|pipelineAggregators
specifier|private
specifier|final
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
decl_stmt|;
comment|/**      * Constructs a new Aggregator.      *      * @param name                  The name of the aggregation      * @param factories             The factories for all the sub-aggregators under this aggregator      * @param context               The aggregation context      * @param parent                The parent aggregator (may be {@code null} for top level aggregators)      * @param metaData              The metaData associated with this aggregator      */
DECL|method|AggregatorBase
specifier|protected
name|AggregatorBase
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|AggregationContext
name|context
parameter_list|,
name|Aggregator
name|parent
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
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|pipelineAggregators
operator|=
name|pipelineAggregators
expr_stmt|;
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
assert|assert
name|factories
operator|!=
literal|null
operator|:
literal|"sub-factories provided to BucketAggregator must not be null, use AggragatorFactories.EMPTY instead"
assert|;
name|this
operator|.
name|subAggregators
operator|=
name|factories
operator|.
name|createSubAggregators
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|searchContext
argument_list|()
operator|.
name|addReleasable
argument_list|(
name|this
argument_list|,
name|Lifetime
operator|.
name|PHASE
argument_list|)
expr_stmt|;
comment|// Register a safeguard to highlight any invalid construction logic (call to this constructor without subsequent preCollection call)
name|collectableSubAggregators
operator|=
operator|new
name|BucketCollector
argument_list|()
block|{
name|void
name|badState
parameter_list|()
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|AggregatorBase
operator|.
name|this
operator|.
name|context
operator|.
name|searchContext
argument_list|()
argument_list|,
literal|"preCollection not called on new Aggregator before use"
argument_list|,
literal|null
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|reader
parameter_list|)
block|{
name|badState
argument_list|()
expr_stmt|;
assert|assert
literal|false
assert|;
return|return
literal|null
return|;
comment|// unreachable but compiler does not agree
block|}
annotation|@
name|Override
specifier|public
name|void
name|preCollection
parameter_list|()
throws|throws
name|IOException
block|{
name|badState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postCollection
parameter_list|()
throws|throws
name|IOException
block|{
name|badState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
name|badState
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
comment|// unreachable
block|}
block|}
expr_stmt|;
block|}
comment|/**      * Most aggregators don't need scores, make sure to extend this method if      * your aggregator needs them.      */
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
for|for
control|(
name|Aggregator
name|agg
range|:
name|subAggregators
control|)
block|{
if|if
condition|(
name|agg
operator|.
name|needsScores
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|metaData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|()
block|{
return|return
name|this
operator|.
name|metaData
return|;
block|}
DECL|method|pipelineAggregators
specifier|public
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|()
block|{
return|return
name|this
operator|.
name|pipelineAggregators
return|;
block|}
comment|/**      * Get a {@link LeafBucketCollector} for the given ctx, which should      * delegate to the given collector.      */
DECL|method|getLeafCollector
specifier|protected
specifier|abstract
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|LeafBucketCollector
name|sub
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
specifier|final
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafBucketCollector
name|sub
init|=
name|collectableSubAggregators
operator|.
name|getLeafCollector
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
return|return
name|getLeafCollector
argument_list|(
name|ctx
argument_list|,
name|sub
argument_list|)
return|;
block|}
comment|/**      * Can be overridden by aggregator implementation to be called back when the collection phase starts.      */
DECL|method|doPreCollection
specifier|protected
name|void
name|doPreCollection
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|preCollection
specifier|public
specifier|final
name|void
name|preCollection
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BucketCollector
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BucketCollector
argument_list|>
name|deferredCollectors
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
name|subAggregators
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|shouldDefer
argument_list|(
name|subAggregators
index|[
name|i
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
name|recordingWrapper
operator|==
literal|null
condition|)
block|{
name|recordingWrapper
operator|=
name|getDeferringCollector
argument_list|()
expr_stmt|;
block|}
name|deferredCollectors
operator|.
name|add
argument_list|(
name|subAggregators
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|subAggregators
index|[
name|i
index|]
operator|=
name|recordingWrapper
operator|.
name|wrap
argument_list|(
name|subAggregators
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectors
operator|.
name|add
argument_list|(
name|subAggregators
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|recordingWrapper
operator|!=
literal|null
condition|)
block|{
name|recordingWrapper
operator|.
name|setDeferredCollector
argument_list|(
name|deferredCollectors
argument_list|)
expr_stmt|;
name|collectors
operator|.
name|add
argument_list|(
name|recordingWrapper
argument_list|)
expr_stmt|;
block|}
name|collectableSubAggregators
operator|=
name|BucketCollector
operator|.
name|wrap
argument_list|(
name|collectors
argument_list|)
expr_stmt|;
name|doPreCollection
argument_list|()
expr_stmt|;
name|collectableSubAggregators
operator|.
name|preCollection
argument_list|()
expr_stmt|;
block|}
DECL|method|getDeferringCollector
specifier|public
name|DeferringBucketCollector
name|getDeferringCollector
parameter_list|()
block|{
comment|// Default impl is a collector that selects the best buckets
comment|// but an alternative defer policy may be based on best docs.
return|return
operator|new
name|BestBucketsDeferringCollector
argument_list|(
name|context
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * This method should be overidden by subclasses that want to defer calculation      * of a child aggregation until a first pass is complete and a set of buckets has      * been pruned.      * Deferring collection will require the recording of all doc/bucketIds from the first      * pass and then the sub class should call {@link #runDeferredCollections(long...)}      * for the selected set of buckets that survive the pruning.      * @param aggregator the child aggregator      * @return true if the aggregator should be deferred      * until a first pass at collection has completed      */
DECL|method|shouldDefer
specifier|protected
name|boolean
name|shouldDefer
parameter_list|(
name|Aggregator
name|aggregator
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|runDeferredCollections
specifier|protected
specifier|final
name|void
name|runDeferredCollections
parameter_list|(
name|long
modifier|...
name|bucketOrds
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Being lenient here - ignore calls where there are no deferred collections to playback
if|if
condition|(
name|recordingWrapper
operator|!=
literal|null
condition|)
block|{
name|recordingWrapper
operator|.
name|replay
argument_list|(
name|bucketOrds
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return  The name of the aggregation.      */
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @return  The parent aggregator of this aggregator. The addAggregation are hierarchical in the sense that some can      *          be composed out of others (more specifically, bucket addAggregation can define other addAggregation that will      *          be aggregated per bucket). This method returns the direct parent aggregator that contains this aggregator, or      *          {@code null} if there is none (meaning, this aggregator is a top level one)      */
annotation|@
name|Override
DECL|method|parent
specifier|public
name|Aggregator
name|parent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|subAggregators
specifier|public
name|Aggregator
index|[]
name|subAggregators
parameter_list|()
block|{
return|return
name|subAggregators
return|;
block|}
annotation|@
name|Override
DECL|method|subAggregator
specifier|public
name|Aggregator
name|subAggregator
parameter_list|(
name|String
name|aggName
parameter_list|)
block|{
if|if
condition|(
name|subAggregatorbyName
operator|==
literal|null
condition|)
block|{
name|subAggregatorbyName
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|subAggregators
operator|.
name|length
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
name|subAggregators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subAggregatorbyName
operator|.
name|put
argument_list|(
name|subAggregators
index|[
name|i
index|]
operator|.
name|name
argument_list|()
argument_list|,
name|subAggregators
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|subAggregatorbyName
operator|.
name|get
argument_list|(
name|aggName
argument_list|)
return|;
block|}
comment|/**      * @return  The current aggregation context.      */
annotation|@
name|Override
DECL|method|context
specifier|public
name|AggregationContext
name|context
parameter_list|()
block|{
return|return
name|context
return|;
block|}
comment|/**      * Called after collection of all document is done.      */
annotation|@
name|Override
DECL|method|postCollection
specifier|public
specifier|final
name|void
name|postCollection
parameter_list|()
throws|throws
name|IOException
block|{
comment|// post-collect this agg before subs to make it possible to buffer and then replay in postCollection()
name|doPostCollection
argument_list|()
expr_stmt|;
name|collectableSubAggregators
operator|.
name|postCollection
argument_list|()
expr_stmt|;
block|}
comment|/** Called upon release of the aggregator. */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|doClose
argument_list|()
expr_stmt|;
block|}
comment|/** Release instance-specific data. */
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{}
comment|/**      * Can be overridden by aggregator implementation to be called back when the collection phase ends.      */
DECL|method|doPostCollection
specifier|protected
name|void
name|doPostCollection
parameter_list|()
throws|throws
name|IOException
block|{     }
DECL|method|buildEmptySubAggregations
specifier|protected
specifier|final
name|InternalAggregations
name|buildEmptySubAggregations
parameter_list|()
block|{
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Aggregator
name|aggregator
range|:
name|subAggregators
control|)
block|{
name|aggs
operator|.
name|add
argument_list|(
name|aggregator
operator|.
name|buildEmptyAggregation
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalAggregations
argument_list|(
name|aggs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

