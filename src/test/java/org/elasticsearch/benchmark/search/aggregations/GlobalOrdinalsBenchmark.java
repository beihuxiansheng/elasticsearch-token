begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
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
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntIntHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectHashSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
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
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthResponse
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
name|admin
operator|.
name|cluster
operator|.
name|stats
operator|.
name|ClusterStatsResponse
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
name|bulk
operator|.
name|BulkRequestBuilder
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
name|SearchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|search
operator|.
name|aggregations
operator|.
name|TermsAggregationSearchBenchmark
operator|.
name|StatsResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
operator|.
name|Bootstrap
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|SizeValue
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|Discovery
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
name|IndexAlreadyExistsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
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
name|AggregationBuilders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportModule
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
operator|.
name|nodeBuilder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GlobalOrdinalsBenchmark
specifier|public
class|class
name|GlobalOrdinalsBenchmark
block|{
DECL|field|INDEX_NAME
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
literal|"index"
decl_stmt|;
DECL|field|TYPE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_NAME
init|=
literal|"type"
decl_stmt|;
DECL|field|QUERY_WARMUP
specifier|private
specifier|static
specifier|final
name|int
name|QUERY_WARMUP
init|=
literal|25
decl_stmt|;
DECL|field|QUERY_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|QUERY_COUNT
init|=
literal|100
decl_stmt|;
DECL|field|FIELD_START
specifier|private
specifier|static
specifier|final
name|int
name|FIELD_START
init|=
literal|1
decl_stmt|;
DECL|field|FIELD_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|FIELD_LIMIT
init|=
literal|1
operator|<<
literal|22
decl_stmt|;
DECL|field|USE_DOC_VALUES
specifier|private
specifier|static
specifier|final
name|boolean
name|USE_DOC_VALUES
init|=
literal|false
decl_stmt|;
DECL|field|COUNT
specifier|static
name|long
name|COUNT
init|=
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"5m"
argument_list|)
operator|.
name|singles
argument_list|()
decl_stmt|;
DECL|field|node
specifier|static
name|Node
name|node
decl_stmt|;
DECL|field|client
specifier|static
name|Client
name|client
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.logger.prefix"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Bootstrap
operator|.
name|initializeNatives
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
literal|"-1"
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|TransportModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|clusterName
init|=
name|GlobalOrdinalsBenchmark
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|node
operator|=
name|nodeBuilder
argument_list|()
operator|.
name|clusterName
argument_list|(
name|clusterName
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
name|client
operator|=
name|node
operator|.
name|client
argument_list|()
expr_stmt|;
try|try
block|{
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|addMapping
argument_list|(
name|TYPE_NAME
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|TYPE_NAME
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"dynamic_templates"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"default"
argument_list|)
operator|.
name|field
argument_list|(
literal|"match"
argument_list|,
literal|"*"
argument_list|)
operator|.
name|field
argument_list|(
literal|"match_mapping_type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"mapping"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"doc_values"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ObjectHashSet
argument_list|<
name|String
argument_list|>
name|uniqueTerms
init|=
operator|new
name|ObjectHashSet
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
name|FIELD_LIMIT
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|added
decl_stmt|;
do|do
block|{
name|added
operator|=
name|uniqueTerms
operator|.
name|add
argument_list|(
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|added
condition|)
do|;
block|}
name|String
index|[]
name|sValues
init|=
name|uniqueTerms
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|uniqueTerms
operator|=
literal|null
expr_stmt|;
name|BulkRequestBuilder
name|builder
init|=
name|client
operator|.
name|prepareBulk
argument_list|()
decl_stmt|;
name|IntIntHashMap
name|tracker
init|=
operator|new
name|IntIntHashMap
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fieldSuffix
init|=
literal|1
init|;
name|fieldSuffix
operator|<=
name|FIELD_LIMIT
condition|;
name|fieldSuffix
operator|<<=
literal|1
control|)
block|{
name|int
name|index
init|=
name|tracker
operator|.
name|putOrAdd
argument_list|(
name|fieldSuffix
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
name|fieldSuffix
condition|)
block|{
name|index
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|fieldSuffix
argument_list|)
expr_stmt|;
name|fieldValues
operator|.
name|put
argument_list|(
literal|"field_"
operator|+
name|fieldSuffix
argument_list|,
name|sValues
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldValues
operator|.
name|put
argument_list|(
literal|"field_"
operator|+
name|fieldSuffix
argument_list|,
name|sValues
index|[
name|index
index|]
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|put
argument_list|(
name|fieldSuffix
argument_list|,
operator|++
name|index
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
name|INDEX_NAME
argument_list|,
name|TYPE_NAME
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|fieldValues
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|numberOfActions
argument_list|()
operator|>=
literal|1000
condition|)
block|{
name|builder
operator|.
name|get
argument_list|()
expr_stmt|;
name|builder
operator|=
name|client
operator|.
name|prepareBulk
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|builder
operator|.
name|numberOfActions
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IndexAlreadyExistsException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Index already exists, ignoring indexing phase, waiting for green"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|clusterHealthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"10m"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterHealthResponse
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"--> Timed out waiting for cluster health"
argument_list|)
expr_stmt|;
block|}
block|}
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"logger.index.fielddata.ordinals"
argument_list|,
literal|"DEBUG"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|COUNT
operator|=
name|client
operator|.
name|prepareCount
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Number of docs in index: "
operator|+
name|COUNT
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StatsResult
argument_list|>
name|stats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fieldSuffix
init|=
name|FIELD_START
init|;
name|fieldSuffix
operator|<=
name|FIELD_LIMIT
condition|;
name|fieldSuffix
operator|<<=
literal|1
control|)
block|{
name|String
name|fieldName
init|=
literal|"field_"
operator|+
name|fieldSuffix
decl_stmt|;
name|String
name|name
init|=
literal|"global_ordinals-"
operator|+
name|fieldName
decl_stmt|;
if|if
condition|(
name|USE_DOC_VALUES
condition|)
block|{
name|fieldName
operator|=
name|fieldName
operator|+
literal|".doc_values"
expr_stmt|;
name|name
operator|=
name|name
operator|+
literal|"_doc_values"
expr_stmt|;
comment|// can't have . in agg name
block|}
name|stats
operator|.
name|add
argument_list|(
name|terms
argument_list|(
name|name
argument_list|,
name|fieldName
argument_list|,
literal|"global_ordinals_low_cardinality"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|fieldSuffix
init|=
name|FIELD_START
init|;
name|fieldSuffix
operator|<=
name|FIELD_LIMIT
condition|;
name|fieldSuffix
operator|<<=
literal|1
control|)
block|{
name|String
name|fieldName
init|=
literal|"field_"
operator|+
name|fieldSuffix
decl_stmt|;
name|String
name|name
init|=
literal|"ordinals-"
operator|+
name|fieldName
decl_stmt|;
if|if
condition|(
name|USE_DOC_VALUES
condition|)
block|{
name|fieldName
operator|=
name|fieldName
operator|+
literal|".doc_values"
expr_stmt|;
name|name
operator|=
name|name
operator|+
literal|"_doc_values"
expr_stmt|;
comment|// can't have . in agg name
block|}
name|stats
operator|.
name|add
argument_list|(
name|terms
argument_list|(
name|name
argument_list|,
name|fieldName
argument_list|,
literal|"ordinals"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------------ SUMMARY -----------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%30s%10s%10s%15s\n"
argument_list|,
literal|"name"
argument_list|,
literal|"took"
argument_list|,
literal|"millis"
argument_list|,
literal|"fieldata size"
argument_list|)
expr_stmt|;
for|for
control|(
name|StatsResult
name|stat
range|:
name|stats
control|)
block|{
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%30s%10s%10d%15s\n"
argument_list|,
name|stat
operator|.
name|name
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|stat
operator|.
name|took
argument_list|)
argument_list|,
operator|(
name|stat
operator|.
name|took
operator|/
name|QUERY_COUNT
operator|)
argument_list|,
name|stat
operator|.
name|fieldDataMemoryUsed
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------------ SUMMARY -----------------------------------------"
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|terms
specifier|private
specifier|static
name|StatsResult
name|terms
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|executionHint
parameter_list|)
block|{
name|long
name|totalQueryTime
decl_stmt|;
comment|// LM VALUE
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClearCache
argument_list|()
operator|.
name|setFieldDataCache
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Warmup ("
operator|+
name|name
operator|+
literal|")..."
argument_list|)
expr_stmt|;
comment|// run just the child query, warm up first
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|QUERY_WARMUP
condition|;
name|j
operator|++
control|)
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|terms
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|field
argument_list|)
operator|.
name|executionHint
argument_list|(
name|executionHint
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|j
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Loading ("
operator|+
name|field
operator|+
literal|"): took: "
operator|+
name|searchResponse
operator|.
name|getTook
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|!=
name|COUNT
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"--> mismatch on hits"
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Warmup ("
operator|+
name|name
operator|+
literal|") DONE"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Running ("
operator|+
name|name
operator|+
literal|")..."
argument_list|)
expr_stmt|;
name|totalQueryTime
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|QUERY_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|terms
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|field
argument_list|)
operator|.
name|executionHint
argument_list|(
name|executionHint
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|!=
name|COUNT
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"--> mismatch on hits"
argument_list|)
expr_stmt|;
block|}
name|totalQueryTime
operator|+=
name|searchResponse
operator|.
name|getTookInMillis
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Terms Agg ("
operator|+
name|name
operator|+
literal|"): "
operator|+
operator|(
name|totalQueryTime
operator|/
name|QUERY_COUNT
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|String
name|nodeId
init|=
name|node
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Discovery
operator|.
name|class
argument_list|)
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|ClusterStatsResponse
name|clusterStateResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|setNodesIds
argument_list|(
name|nodeId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Heap used: "
operator|+
name|clusterStateResponse
operator|.
name|getNodesStats
argument_list|()
operator|.
name|getJvm
argument_list|()
operator|.
name|getHeapUsed
argument_list|()
argument_list|)
expr_stmt|;
name|ByteSizeValue
name|fieldDataMemoryUsed
init|=
name|clusterStateResponse
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getFieldData
argument_list|()
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Fielddata memory size: "
operator|+
name|fieldDataMemoryUsed
argument_list|)
expr_stmt|;
return|return
operator|new
name|StatsResult
argument_list|(
name|name
argument_list|,
name|totalQueryTime
argument_list|,
name|fieldDataMemoryUsed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

