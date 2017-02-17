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
name|CompositeReaderContext
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
name|index
operator|.
name|IndexReaderContext
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
name|Collector
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
name|IndexSearcher
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|QueryCache
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
name|QueryCachingPolicy
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
name|Weight
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|settings
operator|.
name|Settings
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
name|MockBigArrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|query
operator|.
name|DisabledQueryCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
operator|.
name|Engine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldDataCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldDataService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryShardContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|NoneCircuitBreakerService
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
name|fetch
operator|.
name|FetchPhase
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
name|fetch
operator|.
name|subphase
operator|.
name|DocValueFieldsFetchSubPhase
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
name|fetch
operator|.
name|subphase
operator|.
name|FetchSourceSubPhase
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
name|ContextIndexSearcher
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
name|lookup
operator|.
name|SearchLookup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Base class for testing {@link Aggregator} implementations.  * Provides helpers for constructing and searching an {@link Aggregator} implementation based on a provided  * {@link AggregationBuilder} instance.  */
end_comment

begin_class
DECL|class|AggregatorTestCase
specifier|public
specifier|abstract
class|class
name|AggregatorTestCase
extends|extends
name|ESTestCase
block|{
DECL|method|createAggregator
specifier|protected
parameter_list|<
name|A
extends|extends
name|Aggregator
parameter_list|,
name|B
extends|extends
name|AggregationBuilder
parameter_list|>
name|A
name|createAggregator
parameter_list|(
name|B
name|aggregationBuilder
parameter_list|,
name|IndexSearcher
name|indexSearcher
parameter_list|,
name|MappedFieldType
modifier|...
name|fieldTypes
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSettings
name|indexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"_index"
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|0
argument_list|)
operator|.
name|creationDate
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Searcher
name|searcher
init|=
operator|new
name|Engine
operator|.
name|Searcher
argument_list|(
literal|"aggregator_test"
argument_list|,
name|indexSearcher
argument_list|)
decl_stmt|;
name|QueryCache
name|queryCache
init|=
operator|new
name|DisabledQueryCache
argument_list|(
name|indexSettings
argument_list|)
decl_stmt|;
name|QueryCachingPolicy
name|queryCachingPolicy
init|=
operator|new
name|QueryCachingPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onUse
parameter_list|(
name|Query
name|query
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|boolean
name|shouldCache
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
comment|// never cache a query
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
name|ContextIndexSearcher
name|contextIndexSearcher
init|=
operator|new
name|ContextIndexSearcher
argument_list|(
name|searcher
argument_list|,
name|queryCache
argument_list|,
name|queryCachingPolicy
argument_list|)
decl_stmt|;
name|CircuitBreakerService
name|circuitBreakerService
init|=
operator|new
name|NoneCircuitBreakerService
argument_list|()
decl_stmt|;
name|SearchContext
name|searchContext
init|=
name|mock
argument_list|(
name|SearchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|numberOfShards
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|searcher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|contextIndexSearcher
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|bigArrays
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|MockBigArrays
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|circuitBreakerService
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|fetchPhase
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|FetchPhase
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|FetchSourceSubPhase
argument_list|()
argument_list|,
operator|new
name|DocValueFieldsFetchSubPhase
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: now just needed for top_hits, this will need to be revised for other agg unit tests:
name|MapperService
name|mapperService
init|=
name|mock
argument_list|(
name|MapperService
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mapperService
operator|.
name|hasNested
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|mapperService
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mapperService
argument_list|)
expr_stmt|;
name|SearchLookup
name|searchLookup
init|=
operator|new
name|SearchLookup
argument_list|(
name|mapperService
argument_list|,
name|mock
argument_list|(
name|IndexFieldDataService
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"type"
block|}
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|lookup
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|searchLookup
argument_list|)
expr_stmt|;
name|QueryShardContext
name|queryShardContext
init|=
name|mock
argument_list|(
name|QueryShardContext
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|MappedFieldType
name|fieldType
range|:
name|fieldTypes
control|)
block|{
name|when
argument_list|(
name|queryShardContext
operator|.
name|fieldMapper
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|queryShardContext
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
argument_list|)
operator|.
name|then
argument_list|(
name|invocation
lambda|->
name|fieldType
operator|.
name|fielddataBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|indexSettings
argument_list|,
name|fieldType
argument_list|,
operator|new
name|IndexFieldDataCache
operator|.
name|None
argument_list|()
argument_list|,
name|circuitBreakerService
argument_list|,
name|mock
argument_list|(
name|MapperService
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|searchContext
operator|.
name|getQueryShardContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queryShardContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|A
name|aggregator
init|=
operator|(
name|A
operator|)
name|aggregationBuilder
operator|.
name|build
argument_list|(
name|searchContext
argument_list|,
literal|null
argument_list|)
operator|.
name|create
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|aggregator
return|;
block|}
DECL|method|search
specifier|protected
parameter_list|<
name|A
extends|extends
name|InternalAggregation
parameter_list|,
name|C
extends|extends
name|Aggregator
parameter_list|>
name|A
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|AggregationBuilder
name|builder
parameter_list|,
name|MappedFieldType
modifier|...
name|fieldTypes
parameter_list|)
throws|throws
name|IOException
block|{
name|C
name|a
init|=
name|createAggregator
argument_list|(
name|builder
argument_list|,
name|searcher
argument_list|,
name|fieldTypes
argument_list|)
decl_stmt|;
try|try
block|{
name|a
operator|.
name|preCollection
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|a
operator|.
name|postCollection
argument_list|()
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|A
name|internalAgg
init|=
operator|(
name|A
operator|)
name|a
operator|.
name|buildAggregation
argument_list|(
literal|0L
argument_list|)
decl_stmt|;
return|return
name|internalAgg
return|;
block|}
finally|finally
block|{
name|closeAgg
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Divides the provided {@link IndexSearcher} in sub-searcher, one for each segment,      * builds an aggregator for each sub-searcher filtered by the provided {@link Query} and      * returns the reduced {@link InternalAggregation}.      */
DECL|method|searchAndReduce
specifier|protected
parameter_list|<
name|A
extends|extends
name|InternalAggregation
parameter_list|,
name|C
extends|extends
name|Aggregator
parameter_list|>
name|A
name|searchAndReduce
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|AggregationBuilder
name|builder
parameter_list|,
name|MappedFieldType
modifier|...
name|fieldTypes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReaderContext
name|ctx
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
specifier|final
name|ShardSearcher
index|[]
name|subSearchers
decl_stmt|;
if|if
condition|(
name|ctx
operator|instanceof
name|LeafReaderContext
condition|)
block|{
name|subSearchers
operator|=
operator|new
name|ShardSearcher
index|[
literal|1
index|]
expr_stmt|;
name|subSearchers
index|[
literal|0
index|]
operator|=
operator|new
name|ShardSearcher
argument_list|(
operator|(
name|LeafReaderContext
operator|)
name|ctx
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|CompositeReaderContext
name|compCTX
init|=
operator|(
name|CompositeReaderContext
operator|)
name|ctx
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|compCTX
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|subSearchers
operator|=
operator|new
name|ShardSearcher
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|searcherIDX
init|=
literal|0
init|;
name|searcherIDX
operator|<
name|subSearchers
operator|.
name|length
condition|;
name|searcherIDX
operator|++
control|)
block|{
specifier|final
name|LeafReaderContext
name|leave
init|=
name|compCTX
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|searcherIDX
argument_list|)
decl_stmt|;
name|subSearchers
index|[
name|searcherIDX
index|]
operator|=
operator|new
name|ShardSearcher
argument_list|(
name|leave
argument_list|,
name|compCTX
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Query
name|rewritten
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createWeight
argument_list|(
name|rewritten
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|C
name|root
init|=
name|createAggregator
argument_list|(
name|builder
argument_list|,
name|searcher
argument_list|,
name|fieldTypes
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|ShardSearcher
name|subSearcher
range|:
name|subSearchers
control|)
block|{
name|C
name|a
init|=
name|createAggregator
argument_list|(
name|builder
argument_list|,
name|subSearcher
argument_list|,
name|fieldTypes
argument_list|)
decl_stmt|;
try|try
block|{
name|a
operator|.
name|preCollection
argument_list|()
expr_stmt|;
name|subSearcher
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|a
operator|.
name|postCollection
argument_list|()
expr_stmt|;
name|aggs
operator|.
name|add
argument_list|(
name|a
operator|.
name|buildAggregation
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeAgg
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|aggs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|A
name|internalAgg
init|=
operator|(
name|A
operator|)
name|aggs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|doReduce
argument_list|(
name|aggs
argument_list|,
operator|new
name|InternalAggregation
operator|.
name|ReduceContext
argument_list|(
name|root
operator|.
name|context
argument_list|()
operator|.
name|bigArrays
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|internalAgg
return|;
block|}
block|}
finally|finally
block|{
name|closeAgg
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeAgg
specifier|private
name|void
name|closeAgg
parameter_list|(
name|Aggregator
name|agg
parameter_list|)
block|{
name|agg
operator|=
name|DeferringBucketCollector
operator|.
name|unwrap
argument_list|(
name|agg
argument_list|)
expr_stmt|;
name|agg
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Aggregator
name|sub
range|:
operator|(
operator|(
name|AggregatorBase
operator|)
name|agg
operator|)
operator|.
name|subAggregators
control|)
block|{
name|closeAgg
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ShardSearcher
specifier|private
specifier|static
class|class
name|ShardSearcher
extends|extends
name|IndexSearcher
block|{
DECL|field|ctx
specifier|private
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|ctx
decl_stmt|;
DECL|method|ShardSearcher
name|ShardSearcher
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|IndexReaderContext
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|search
argument_list|(
name|ctx
argument_list|,
name|weight
argument_list|,
name|collector
argument_list|)
expr_stmt|;
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
literal|"ShardSearcher("
operator|+
name|ctx
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

