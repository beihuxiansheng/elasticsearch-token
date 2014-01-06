begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|search
operator|.
name|aggregations
operator|.
name|Aggregator
operator|.
name|BucketAggregationMode
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
name|List
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
DECL|field|factories
specifier|private
specifier|final
name|AggregatorFactory
index|[]
name|factories
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
parameter_list|)
block|{
name|this
operator|.
name|factories
operator|=
name|factories
expr_stmt|;
block|}
comment|/**      * Create all aggregators so that they can be consumed with multiple buckets.      */
DECL|method|createSubAggregators
specifier|public
name|Aggregator
index|[]
name|createSubAggregators
parameter_list|(
name|Aggregator
name|parent
parameter_list|,
specifier|final
name|long
name|estimatedBucketsCount
parameter_list|)
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
specifier|final
name|AggregatorFactory
name|factory
init|=
name|factories
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Aggregator
name|first
init|=
name|factory
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
name|estimatedBucketsCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|.
name|bucketAggregationMode
argument_list|()
operator|==
name|BucketAggregationMode
operator|.
name|MULTI_BUCKETS
condition|)
block|{
comment|// This aggregator already supports multiple bucket ordinals, can be used directly
name|aggregators
index|[
name|i
index|]
operator|=
name|first
expr_stmt|;
continue|continue;
block|}
comment|// the aggregator doesn't support multiple ordinals, let's wrap it so that it does.
name|aggregators
index|[
name|i
index|]
operator|=
operator|new
name|Aggregator
argument_list|(
name|first
operator|.
name|name
argument_list|()
argument_list|,
name|BucketAggregationMode
operator|.
name|MULTI_BUCKETS
argument_list|,
name|AggregatorFactories
operator|.
name|EMPTY
argument_list|,
literal|1
argument_list|,
name|first
operator|.
name|context
argument_list|()
argument_list|,
name|first
operator|.
name|parent
argument_list|()
argument_list|)
block|{
name|ObjectArray
argument_list|<
name|Aggregator
argument_list|>
name|aggregators
decl_stmt|;
block|{
name|aggregators
operator|=
name|BigArrays
operator|.
name|newObjectArray
argument_list|(
name|estimatedBucketsCount
argument_list|,
name|context
operator|.
name|pageCacheRecycler
argument_list|()
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
for|for
control|(
name|long
name|i
init|=
literal|1
init|;
name|i
operator|<
name|estimatedBucketsCount
condition|;
operator|++
name|i
control|)
block|{
name|aggregators
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|factory
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
name|estimatedBucketsCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldCollect
parameter_list|()
block|{
return|return
name|first
operator|.
name|shouldCollect
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doPostCollection
parameter_list|()
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
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
name|owningBucketOrdinal
parameter_list|)
throws|throws
name|IOException
block|{
name|aggregators
operator|=
name|BigArrays
operator|.
name|grow
argument_list|(
name|aggregators
argument_list|,
name|owningBucketOrdinal
operator|+
literal|1
argument_list|)
expr_stmt|;
name|Aggregator
name|aggregator
init|=
name|aggregators
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
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
operator|.
name|context
argument_list|()
argument_list|,
name|parent
argument_list|,
name|estimatedBucketsCount
argument_list|)
expr_stmt|;
name|aggregators
operator|.
name|set
argument_list|(
name|owningBucketOrdinal
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
block|}
name|aggregator
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|buildAggregation
parameter_list|(
name|long
name|owningBucketOrdinal
parameter_list|)
block|{
return|return
name|aggregators
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
argument_list|)
operator|.
name|buildAggregation
argument_list|(
literal|0
argument_list|)
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
name|doRelease
parameter_list|()
block|{
name|Releasables
operator|.
name|release
argument_list|(
name|aggregators
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|0
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
DECL|method|Empty
specifier|private
name|Empty
parameter_list|()
block|{
name|super
argument_list|(
name|EMPTY_FACTORIES
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
parameter_list|,
name|long
name|estimatedBucketsCount
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
DECL|field|factories
specifier|private
name|List
argument_list|<
name|AggregatorFactory
argument_list|>
name|factories
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregatorFactory
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|AggregatorFactory
name|factory
parameter_list|)
block|{
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
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
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
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

