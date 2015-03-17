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
name|collect
operator|.
name|ImmutableMap
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
name|ConstantScoreQuery
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
name|Filter
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
name|FilteredQuery
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
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|inject
operator|.
name|Inject
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|SearchParseElement
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
name|SearchPhase
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
name|global
operator|.
name|GlobalAggregator
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
name|reducers
operator|.
name|Reducer
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
name|reducers
operator|.
name|SiblingReducer
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
DECL|class|AggregationPhase
specifier|public
class|class
name|AggregationPhase
implements|implements
name|SearchPhase
block|{
DECL|field|parseElement
specifier|private
specifier|final
name|AggregationParseElement
name|parseElement
decl_stmt|;
DECL|field|binaryParseElement
specifier|private
specifier|final
name|AggregationBinaryParseElement
name|binaryParseElement
decl_stmt|;
annotation|@
name|Inject
DECL|method|AggregationPhase
specifier|public
name|AggregationPhase
parameter_list|(
name|AggregationParseElement
name|parseElement
parameter_list|,
name|AggregationBinaryParseElement
name|binaryParseElement
parameter_list|)
block|{
name|this
operator|.
name|parseElement
operator|=
name|parseElement
expr_stmt|;
name|this
operator|.
name|binaryParseElement
operator|=
name|binaryParseElement
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseElements
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|SearchParseElement
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"aggregations"
argument_list|,
name|parseElement
argument_list|)
operator|.
name|put
argument_list|(
literal|"aggs"
argument_list|,
name|parseElement
argument_list|)
operator|.
name|put
argument_list|(
literal|"aggregations_binary"
argument_list|,
name|binaryParseElement
argument_list|)
operator|.
name|put
argument_list|(
literal|"aggregationsBinary"
argument_list|,
name|binaryParseElement
argument_list|)
operator|.
name|put
argument_list|(
literal|"aggs_binary"
argument_list|,
name|binaryParseElement
argument_list|)
operator|.
name|put
argument_list|(
literal|"aggsBinary"
argument_list|,
name|binaryParseElement
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|aggregations
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|AggregationContext
name|aggregationContext
init|=
operator|new
name|AggregationContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|context
operator|.
name|aggregations
argument_list|()
operator|.
name|aggregationContext
argument_list|(
name|aggregationContext
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Aggregator
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Aggregator
index|[]
name|aggregators
decl_stmt|;
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
decl_stmt|;
try|try
block|{
name|AggregatorFactories
name|factories
init|=
name|context
operator|.
name|aggregations
argument_list|()
operator|.
name|factories
argument_list|()
decl_stmt|;
name|aggregators
operator|=
name|factories
operator|.
name|createTopLevelAggregators
argument_list|(
name|aggregationContext
argument_list|)
expr_stmt|;
name|reducers
operator|=
name|factories
operator|.
name|createReducers
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AggregationInitializationException
argument_list|(
literal|"Could not initialize aggregators"
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aggregators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|aggregators
index|[
name|i
index|]
operator|instanceof
name|GlobalAggregator
operator|==
literal|false
condition|)
block|{
name|collectors
operator|.
name|add
argument_list|(
name|aggregators
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|aggregations
argument_list|()
operator|.
name|aggregators
argument_list|(
name|aggregators
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|collectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|queryCollectors
argument_list|()
operator|.
name|put
argument_list|(
name|AggregationPhase
operator|.
name|class
argument_list|,
operator|(
name|BucketCollector
operator|.
name|wrap
argument_list|(
name|collectors
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SearchContext
name|context
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
if|if
condition|(
name|context
operator|.
name|aggregations
argument_list|()
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|aggregations
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|aggregations
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// no need to compute the aggs twice, they should be computed on a per context basis
return|return;
block|}
name|Aggregator
index|[]
name|aggregators
init|=
name|context
operator|.
name|aggregations
argument_list|()
operator|.
name|aggregators
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Aggregator
argument_list|>
name|globals
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
name|aggregators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|aggregators
index|[
name|i
index|]
operator|instanceof
name|GlobalAggregator
condition|)
block|{
name|globals
operator|.
name|add
argument_list|(
name|aggregators
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// optimize the global collector based execution
if|if
condition|(
operator|!
name|globals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|BucketCollector
name|collector
init|=
name|BucketCollector
operator|.
name|wrap
argument_list|(
name|globals
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|Queries
operator|.
name|MATCH_ALL_FILTER
argument_list|)
decl_stmt|;
name|Filter
name|searchFilter
init|=
name|context
operator|.
name|searchFilter
argument_list|(
name|context
operator|.
name|types
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchFilter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|searchFilter
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to execute global aggregators"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|aggregators
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Aggregator
name|aggregator
range|:
name|context
operator|.
name|aggregations
argument_list|()
operator|.
name|aggregators
argument_list|()
control|)
block|{
try|try
block|{
name|aggregator
operator|.
name|postCollection
argument_list|()
expr_stmt|;
name|aggregations
operator|.
name|add
argument_list|(
name|aggregator
operator|.
name|buildAggregation
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Failed to build aggregation ["
operator|+
name|aggregator
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|aggregations
argument_list|(
operator|new
name|InternalAggregations
argument_list|(
name|aggregations
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
init|=
name|context
operator|.
name|aggregations
argument_list|()
operator|.
name|factories
argument_list|()
operator|.
name|createReducers
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SiblingReducer
argument_list|>
name|siblingReducers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|reducers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Reducer
name|reducer
range|:
name|reducers
control|)
block|{
if|if
condition|(
name|reducer
operator|instanceof
name|SiblingReducer
condition|)
block|{
name|siblingReducers
operator|.
name|add
argument_list|(
operator|(
name|SiblingReducer
operator|)
name|reducer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid reducer named ["
operator|+
name|reducer
operator|.
name|name
argument_list|()
operator|+
literal|"] of type ["
operator|+
name|reducer
operator|.
name|type
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"]. Only sibling reducers are allowed at the top level"
argument_list|)
throw|;
block|}
block|}
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|reducers
argument_list|(
name|siblingReducers
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Failed to build top level reducers"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// disable aggregations so that they don't run on next pages in case of scrolling
name|context
operator|.
name|aggregations
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|queryCollectors
argument_list|()
operator|.
name|remove
argument_list|(
name|AggregationPhase
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

