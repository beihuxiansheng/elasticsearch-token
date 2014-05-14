begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bench
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bench
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchRequestBuilder
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
name|search
operator|.
name|SearchType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|QueryBuilder
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
name|QueryBuilders
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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|between
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|randomFrom
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|randomBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|randomAsciiOfLengthBetween
import|;
end_import

begin_comment
comment|/**  * Utilities for building randomized benchmark tests.  */
end_comment

begin_class
DECL|class|BenchmarkTestUtil
specifier|public
class|class
name|BenchmarkTestUtil
block|{
DECL|field|BENCHMARK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|BENCHMARK_NAME
init|=
literal|"test_benchmark"
decl_stmt|;
DECL|field|COMPETITOR_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|COMPETITOR_PREFIX
init|=
literal|"competitor_"
decl_stmt|;
DECL|field|INDEX_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_TYPE
init|=
literal|"test_type"
decl_stmt|;
DECL|field|searchTypes
specifier|public
specifier|static
specifier|final
name|SearchType
index|[]
name|searchTypes
init|=
block|{
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
block|,
name|SearchType
operator|.
name|QUERY_THEN_FETCH
block|,
name|SearchType
operator|.
name|QUERY_AND_FETCH
block|,
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
block|,
name|SearchType
operator|.
name|COUNT
block|}
decl_stmt|;
DECL|enum|TestIndexField
specifier|public
specifier|static
enum|enum
name|TestIndexField
block|{
DECL|enum constant|INT_FIELD
name|INT_FIELD
argument_list|(
literal|"int_field"
argument_list|)
block|,
DECL|enum constant|FLOAT_FIELD
name|FLOAT_FIELD
argument_list|(
literal|"float_field"
argument_list|)
block|,
DECL|enum constant|BOOLEAN_FIELD
name|BOOLEAN_FIELD
argument_list|(
literal|"boolean_field"
argument_list|)
block|,
DECL|enum constant|STRING_FIELD
name|STRING_FIELD
argument_list|(
literal|"string_field"
argument_list|)
block|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|TestIndexField
name|TestIndexField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
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
DECL|enum|TestQueryType
specifier|public
specifier|static
enum|enum
name|TestQueryType
block|{
DECL|enum constant|MATCH_ALL
name|MATCH_ALL
block|{
annotation|@
name|Override
name|QueryBuilder
name|getQuery
parameter_list|()
block|{
return|return
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
return|;
block|}
block|}
block|,
DECL|enum constant|MATCH
name|MATCH
block|{
annotation|@
name|Override
name|QueryBuilder
name|getQuery
parameter_list|()
block|{
return|return
name|QueryBuilders
operator|.
name|matchQuery
argument_list|(
name|TestIndexField
operator|.
name|STRING_FIELD
operator|.
name|toString
argument_list|()
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|TERM
name|TERM
block|{
annotation|@
name|Override
name|QueryBuilder
name|getQuery
parameter_list|()
block|{
return|return
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
name|TestIndexField
operator|.
name|STRING_FIELD
operator|.
name|toString
argument_list|()
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|QUERY_STRING
name|QUERY_STRING
block|{
annotation|@
name|Override
name|QueryBuilder
name|getQuery
parameter_list|()
block|{
return|return
name|QueryBuilders
operator|.
name|queryString
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|WILDCARD
name|WILDCARD
block|{
annotation|@
name|Override
name|QueryBuilder
name|getQuery
parameter_list|()
block|{
return|return
name|QueryBuilders
operator|.
name|wildcardQuery
argument_list|(
name|TestIndexField
operator|.
name|STRING_FIELD
operator|.
name|toString
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|"*"
else|:
literal|"?"
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|getQuery
specifier|abstract
name|QueryBuilder
name|getQuery
parameter_list|()
function_decl|;
block|}
DECL|method|randomRequest
specifier|public
specifier|static
name|BenchmarkRequest
name|randomRequest
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
index|[]
name|indices
parameter_list|,
name|int
name|numExecutorNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|BenchmarkSettings
argument_list|>
name|competitionSettingsMap
parameter_list|,
name|int
name|lowRandomIntervalBound
parameter_list|,
name|int
name|highRandomIntervalBound
parameter_list|,
name|SearchRequest
modifier|...
name|requests
parameter_list|)
block|{
specifier|final
name|BenchmarkRequestBuilder
name|builder
init|=
operator|new
name|BenchmarkRequestBuilder
argument_list|(
name|client
argument_list|,
name|indices
argument_list|)
decl_stmt|;
specifier|final
name|BenchmarkSettings
name|settings
init|=
name|randomSettings
argument_list|(
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setIterations
argument_list|(
name|settings
operator|.
name|iterations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setConcurrency
argument_list|(
name|settings
operator|.
name|concurrency
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMultiplier
argument_list|(
name|settings
operator|.
name|multiplier
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSearchType
argument_list|(
name|settings
operator|.
name|searchType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setWarmup
argument_list|(
name|settings
operator|.
name|warmup
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNumExecutorNodes
argument_list|(
name|numExecutorNodes
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numCompetitors
init|=
name|between
argument_list|(
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
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
name|numCompetitors
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addCompetitor
argument_list|(
name|randomCompetitor
argument_list|(
name|client
argument_list|,
name|COMPETITOR_PREFIX
operator|+
name|i
argument_list|,
name|indices
argument_list|,
name|competitionSettingsMap
argument_list|,
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
argument_list|,
name|requests
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BenchmarkRequest
name|request
init|=
name|builder
operator|.
name|request
argument_list|()
decl_stmt|;
name|request
operator|.
name|benchmarkName
argument_list|(
name|BENCHMARK_NAME
argument_list|)
expr_stmt|;
name|request
operator|.
name|cascadeGlobalSettings
argument_list|()
expr_stmt|;
name|request
operator|.
name|applyLateBoundSettings
argument_list|(
name|indices
argument_list|,
operator|new
name|String
index|[]
block|{
name|INDEX_TYPE
block|}
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|randomRequest
specifier|public
specifier|static
name|BenchmarkRequest
name|randomRequest
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
index|[]
name|indices
parameter_list|,
name|int
name|numExecutorNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|BenchmarkSettings
argument_list|>
name|competitionSettingsMap
parameter_list|,
name|SearchRequest
modifier|...
name|requests
parameter_list|)
block|{
return|return
name|randomRequest
argument_list|(
name|client
argument_list|,
name|indices
argument_list|,
name|numExecutorNodes
argument_list|,
name|competitionSettingsMap
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|requests
argument_list|)
return|;
block|}
DECL|method|randomSearch
specifier|public
specifier|static
name|SearchRequest
name|randomSearch
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
index|[]
name|indices
parameter_list|)
block|{
specifier|final
name|SearchRequestBuilder
name|builder
init|=
operator|new
name|SearchRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setTypes
argument_list|(
name|INDEX_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setQuery
argument_list|(
name|randomFrom
argument_list|(
name|TestQueryType
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|request
argument_list|()
return|;
block|}
DECL|method|randomCompetitor
specifier|public
specifier|static
name|BenchmarkCompetitor
name|randomCompetitor
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|name
parameter_list|,
name|String
index|[]
name|indices
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|BenchmarkSettings
argument_list|>
name|competitionSettingsMap
parameter_list|,
name|int
name|lowRandomIntervalBound
parameter_list|,
name|int
name|highRandomIntervalBound
parameter_list|,
name|SearchRequest
modifier|...
name|requests
parameter_list|)
block|{
specifier|final
name|BenchmarkCompetitorBuilder
name|builder
init|=
operator|new
name|BenchmarkCompetitorBuilder
argument_list|()
decl_stmt|;
specifier|final
name|BenchmarkSettings
name|settings
init|=
name|randomSettings
argument_list|(
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setClearCachesSettings
argument_list|(
name|randomCacheSettings
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setIterations
argument_list|(
name|settings
operator|.
name|iterations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setConcurrency
argument_list|(
name|settings
operator|.
name|concurrency
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMultiplier
argument_list|(
name|settings
operator|.
name|multiplier
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSearchType
argument_list|(
name|settings
operator|.
name|searchType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setWarmup
argument_list|(
name|settings
operator|.
name|warmup
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|requests
operator|!=
literal|null
operator|&&
name|requests
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requests
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addSearchRequest
argument_list|(
name|requests
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|settings
operator|.
name|addSearchRequest
argument_list|(
name|requests
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|numSearches
init|=
name|between
argument_list|(
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
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
name|numSearches
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SearchRequest
name|searchRequest
init|=
name|randomSearch
argument_list|(
name|client
argument_list|,
name|indices
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addSearchRequest
argument_list|(
name|searchRequest
argument_list|)
expr_stmt|;
name|settings
operator|.
name|addSearchRequest
argument_list|(
name|searchRequest
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|competitionSettingsMap
operator|!=
literal|null
condition|)
block|{
name|competitionSettingsMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|randomCacheSettings
specifier|public
specifier|static
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
name|randomCacheSettings
parameter_list|()
block|{
specifier|final
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
name|settings
init|=
operator|new
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
argument_list|()
decl_stmt|;
name|settings
operator|.
name|filterCache
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|fieldDataCache
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|idCache
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|recycler
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|numFieldsToClear
init|=
name|between
argument_list|(
literal|1
argument_list|,
name|TestIndexField
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[
name|numFieldsToClear
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
name|numFieldsToClear
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
name|TestIndexField
operator|.
name|values
argument_list|()
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|settings
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
return|return
name|settings
return|;
block|}
DECL|method|randomSettings
specifier|public
specifier|static
name|BenchmarkSettings
name|randomSettings
parameter_list|(
name|int
name|lowRandomIntervalBound
parameter_list|,
name|int
name|highRandomIntervalBound
parameter_list|)
block|{
specifier|final
name|BenchmarkSettings
name|settings
init|=
operator|new
name|BenchmarkSettings
argument_list|()
decl_stmt|;
name|settings
operator|.
name|concurrency
argument_list|(
name|between
argument_list|(
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|settings
operator|.
name|iterations
argument_list|(
name|between
argument_list|(
name|lowRandomIntervalBound
argument_list|,
name|highRandomIntervalBound
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|settings
operator|.
name|multiplier
argument_list|(
name|between
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|settings
operator|.
name|warmup
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|settings
operator|.
name|searchType
argument_list|(
name|searchTypes
index|[
name|between
argument_list|(
literal|0
argument_list|,
name|searchTypes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|settings
return|;
block|}
block|}
end_class

end_unit

