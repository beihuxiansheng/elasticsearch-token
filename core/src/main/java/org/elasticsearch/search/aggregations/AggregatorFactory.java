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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|ToXContentToBytes
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
name|NamedWriteable
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
name|lease
operator|.
name|Releasables
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
name|util
operator|.
name|BigArrays
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
name|util
operator|.
name|ObjectArray
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * A factory that knows how to create an {@link Aggregator} of a specific type.  */
end_comment

begin_class
DECL|class|AggregatorFactory
specifier|public
specifier|abstract
class|class
name|AggregatorFactory
extends|extends
name|ToXContentToBytes
implements|implements
name|NamedWriteable
argument_list|<
name|AggregatorFactory
argument_list|>
block|{
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|protected
name|String
name|type
decl_stmt|;
DECL|field|parent
specifier|protected
name|AggregatorFactory
name|parent
decl_stmt|;
DECL|field|factories
specifier|protected
name|AggregatorFactories
name|factories
init|=
name|AggregatorFactories
operator|.
name|EMPTY
decl_stmt|;
DECL|field|metaData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
decl_stmt|;
DECL|field|context
specifier|private
name|AggregationContext
name|context
decl_stmt|;
comment|/**      * Constructs a new aggregator factory.      *      * @param name  The aggregation name      * @param type  The aggregation type      */
DECL|method|AggregatorFactory
specifier|public
name|AggregatorFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
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
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Initializes this factory with the given {@link AggregationContext} ready      * to create {@link Aggregator}s      */
DECL|method|init
specifier|public
specifier|final
name|void
name|init
parameter_list|(
name|AggregationContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|doInit
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|factories
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allows the {@link AggregatorFactory} to initialize any state prior to      * using it to create {@link Aggregator}s.      *       * @param context      *            the {@link AggregationContext} to use during initialization.      */
DECL|method|doInit
specifier|protected
name|void
name|doInit
parameter_list|(
name|AggregationContext
name|context
parameter_list|)
block|{     }
comment|/**      * Registers sub-factories with this factory. The sub-factory will be responsible for the creation of sub-aggregators under the      * aggregator created by this factory.      *      * @param subFactories  The sub-factories      * @return  this factory (fluent interface)      */
DECL|method|subFactories
specifier|public
name|AggregatorFactory
name|subFactories
parameter_list|(
name|AggregatorFactories
name|subFactories
parameter_list|)
block|{
name|this
operator|.
name|factories
operator|=
name|subFactories
expr_stmt|;
name|this
operator|.
name|factories
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
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
comment|/**      * Validates the state of this factory (makes sure the factory is properly configured)      */
DECL|method|validate
specifier|public
specifier|final
name|void
name|validate
parameter_list|()
block|{
name|doValidate
argument_list|()
expr_stmt|;
name|factories
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return  The parent factory if one exists (will always return {@code null} for top level aggregator factories).      */
DECL|method|parent
specifier|public
name|AggregatorFactory
name|parent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|createInternal
specifier|protected
specifier|abstract
name|Aggregator
name|createInternal
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|boolean
name|collectsFromSingleBucket
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
function_decl|;
comment|/**      * Creates the aggregator      *      * @param parent                The parent aggregator (if this is a top level factory, the parent will be {@code null})      * @param collectsFromSingleBucket  If true then the created aggregator will only be collected with<tt>0</tt> as a bucket ordinal.      *                              Some factories can take advantage of this in order to return more optimized implementations.      *      * @return                      The created aggregator      */
DECL|method|create
specifier|public
specifier|final
name|Aggregator
name|create
parameter_list|(
name|Aggregator
name|parent
parameter_list|,
name|boolean
name|collectsFromSingleBucket
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createInternal
argument_list|(
name|context
argument_list|,
name|parent
argument_list|,
name|collectsFromSingleBucket
argument_list|,
name|this
operator|.
name|factories
operator|.
name|createPipelineAggregators
argument_list|()
argument_list|,
name|this
operator|.
name|metaData
argument_list|)
return|;
block|}
DECL|method|doValidate
specifier|public
name|void
name|doValidate
parameter_list|()
block|{     }
DECL|method|setMetaData
specifier|public
name|void
name|setMetaData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
specifier|final
name|AggregatorFactory
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|name
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|AggregatorFactory
name|factory
init|=
name|doReadFrom
argument_list|(
name|name
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|factory
operator|.
name|factories
operator|=
name|AggregatorFactories
operator|.
name|EMPTY
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|factory
operator|.
name|factories
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|factory
operator|.
name|metaData
operator|=
name|in
operator|.
name|readMap
argument_list|()
expr_stmt|;
return|return
name|factory
return|;
block|}
comment|// NORELEASE make this abstract when agg refactor complete
DECL|method|doReadFrom
specifier|protected
name|AggregatorFactory
name|doReadFrom
parameter_list|(
name|String
name|name
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
specifier|final
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
name|doWriteTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|factories
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeMap
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
block|}
comment|// NORELEASE make this abstract when agg refactor complete
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
block|{     }
annotation|@
name|Override
DECL|method|toXContent
specifier|public
specifier|final
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
if|if
condition|(
name|this
operator|.
name|metaData
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"meta"
argument_list|,
name|this
operator|.
name|metaData
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|internalXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|factories
operator|!=
literal|null
operator|&&
name|factories
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"aggregations"
argument_list|)
expr_stmt|;
name|factories
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
operator|.
name|endObject
argument_list|()
return|;
block|}
comment|// NORELEASE make this method abstract when agg refactor complete
DECL|method|internalXContent
specifier|protected
name|XContentBuilder
name|internalXContent
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
return|return
name|builder
return|;
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
name|type
return|;
block|}
comment|/**      * Utility method. Given an {@link AggregatorFactory} that creates {@link Aggregator}s that only know how      * to collect bucket<tt>0</tt>, this returns an aggregator that can collect any bucket.      */
DECL|method|asMultiBucketAggregator
specifier|protected
specifier|static
name|Aggregator
name|asMultiBucketAggregator
parameter_list|(
specifier|final
name|AggregatorFactory
name|factory
parameter_list|,
specifier|final
name|AggregationContext
name|context
parameter_list|,
specifier|final
name|Aggregator
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Aggregator
name|first
init|=
name|factory
operator|.
name|create
argument_list|(
name|parent
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|BigArrays
name|bigArrays
init|=
name|context
operator|.
name|bigArrays
argument_list|()
decl_stmt|;
return|return
operator|new
name|Aggregator
argument_list|()
block|{
name|ObjectArray
argument_list|<
name|Aggregator
argument_list|>
name|aggregators
decl_stmt|;
name|ObjectArray
argument_list|<
name|LeafBucketCollector
argument_list|>
name|collectors
decl_stmt|;
block|{
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
name|aggregators
operator|=
name|bigArrays
operator|.
name|newObjectArray
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|aggregators
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|first
argument_list|)
expr_stmt|;
name|collectors
operator|=
name|bigArrays
operator|.
name|newObjectArray
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|first
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|AggregationContext
name|context
parameter_list|()
block|{
return|return
name|first
operator|.
name|context
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Aggregator
name|parent
parameter_list|()
block|{
return|return
name|first
operator|.
name|parent
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|first
operator|.
name|needsScores
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Aggregator
name|subAggregator
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aggregators
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Aggregator
name|aggregator
init|=
name|aggregators
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|!=
literal|null
condition|)
block|{
name|aggregator
operator|.
name|preCollection
argument_list|()
expr_stmt|;
block|}
block|}
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
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aggregators
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Aggregator
name|aggregator
init|=
name|aggregators
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|!=
literal|null
condition|)
block|{
name|aggregator
operator|.
name|postCollection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
specifier|final
name|LeafReaderContext
name|ctx
parameter_list|)
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collectors
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|collectors
operator|.
name|set
argument_list|(
name|i
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LeafBucketCollector
argument_list|()
block|{
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|aggregators
operator|=
name|bigArrays
operator|.
name|grow
argument_list|(
name|aggregators
argument_list|,
name|bucket
operator|+
literal|1
argument_list|)
expr_stmt|;
name|collectors
operator|=
name|bigArrays
operator|.
name|grow
argument_list|(
name|collectors
argument_list|,
name|bucket
operator|+
literal|1
argument_list|)
expr_stmt|;
name|LeafBucketCollector
name|collector
init|=
name|collectors
operator|.
name|get
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|collector
operator|==
literal|null
condition|)
block|{
name|Aggregator
name|aggregator
init|=
name|aggregators
operator|.
name|get
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
name|aggregator
operator|=
name|factory
operator|.
name|create
argument_list|(
name|parent
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|aggregator
operator|.
name|preCollection
argument_list|()
expr_stmt|;
name|aggregators
operator|.
name|set
argument_list|(
name|bucket
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
block|}
name|collector
operator|=
name|aggregator
operator|.
name|getLeafCollector
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|collectors
operator|.
name|set
argument_list|(
name|bucket
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|buildAggregation
parameter_list|(
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bucket
operator|<
name|aggregators
operator|.
name|size
argument_list|()
condition|)
block|{
name|Aggregator
name|aggregator
init|=
name|aggregators
operator|.
name|get
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|!=
literal|null
condition|)
block|{
return|return
name|aggregator
operator|.
name|buildAggregation
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
return|return
name|buildEmptyAggregation
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|buildEmptyAggregation
parameter_list|()
block|{
return|return
name|first
operator|.
name|buildEmptyAggregation
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|aggregators
argument_list|,
name|collectors
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|factories
argument_list|,
name|metaData
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
name|doHashCode
argument_list|()
argument_list|)
return|;
block|}
comment|// NORELEASE make this method abstract here when agg refactor complete (so
comment|// that subclasses are forced to implement it)
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This method should be implemented by a sub-class and should not rely on this method. When agg re-factoring is complete this method will be made abstract."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|AggregatorFactory
name|other
init|=
operator|(
name|AggregatorFactory
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
name|other
operator|.
name|name
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|type
argument_list|,
name|other
operator|.
name|type
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|metaData
argument_list|,
name|other
operator|.
name|metaData
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|factories
argument_list|,
name|other
operator|.
name|factories
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|doEquals
argument_list|(
name|obj
argument_list|)
return|;
block|}
comment|// NORELEASE make this method abstract here when agg refactor complete (so
comment|// that subclasses are forced to implement it)
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This method should be implemented by a sub-class and should not rely on this method. When agg re-factoring is complete this method will be made abstract."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

