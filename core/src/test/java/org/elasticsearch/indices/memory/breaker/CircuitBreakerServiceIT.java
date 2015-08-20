begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.memory.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|memory
operator|.
name|breaker
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|index
operator|.
name|IndexRequestBuilder
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
name|breaker
operator|.
name|CircuitBreaker
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
name|breaker
operator|.
name|CircuitBreakingException
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
name|TimeValue
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
name|BreakerSettings
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
name|CircuitBreakerStats
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
name|HierarchyCircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
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
name|sort
operator|.
name|SortOrder
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
name|ESIntegTestCase
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
name|ESIntegTestCase
operator|.
name|ClusterScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|concurrent
operator|.
name|TimeUnit
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
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
operator|.
name|cardinality
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
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|TEST
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertFailures
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThanOrEqualTo
import|;
end_import

begin_comment
comment|/**  * Integration tests for InternalCircuitBreakerService  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|TEST
argument_list|,
name|randomDynamicTemplates
operator|=
literal|false
argument_list|)
DECL|class|CircuitBreakerServiceIT
specifier|public
class|class
name|CircuitBreakerServiceIT
extends|extends
name|ESIntegTestCase
block|{
comment|/** Reset all breaker settings back to their defaults */
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> resetting breaker settings"
argument_list|)
expr_stmt|;
name|Settings
name|resetSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|DEFAULT_FIELDDATA_BREAKER_LIMIT
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|DEFAULT_FIELDDATA_OVERHEAD_CONSTANT
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|DEFAULT_REQUEST_BREAKER_LIMIT
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|REQUEST_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
literal|1.0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
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
name|resetSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/** Returns true if any of the nodes used a noop breaker */
DECL|method|noopBreakerUsed
specifier|private
name|boolean
name|noopBreakerUsed
parameter_list|()
block|{
name|NodesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setBreaker
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeStats
name|nodeStats
range|:
name|stats
control|)
block|{
if|if
condition|(
name|nodeStats
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|)
operator|.
name|getLimit
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|nodeStats
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|)
operator|.
name|getLimit
argument_list|()
operator|==
literal|0
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
annotation|@
name|Test
DECL|method|testMemoryBreaker
specifier|public
name|void
name|testMemoryBreaker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|noopBreakerUsed
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> noop breakers used, skipping test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"cb-test"
argument_list|,
literal|1
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
comment|// index some different terms so we have some field data for loading
name|int
name|docCount
init|=
name|scaledRandomIntBetween
argument_list|(
literal|300
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|reqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|id
init|=
literal|0
init|;
name|id
operator|<
name|docCount
condition|;
name|id
operator|++
control|)
block|{
name|reqs
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"cb-test"
argument_list|,
literal|"type"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|reqs
argument_list|)
expr_stmt|;
comment|// clear field data cache (thus setting the loaded field data back to 0)
name|clearFieldData
argument_list|()
expr_stmt|;
comment|// Update circuit breaker settings
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
literal|"100b"
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
literal|1.05
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
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
name|settings
argument_list|)
argument_list|)
expr_stmt|;
comment|// execute a search that loads field data (sorting on the "test" field)
comment|// again, this time it should trip the breaker
name|SearchRequestBuilder
name|searchRequest
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"cb-test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"test"
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
decl_stmt|;
name|assertFailures
argument_list|(
name|searchRequest
argument_list|,
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
name|containsString
argument_list|(
literal|"Data too large, data for [test] would be larger than limit of [100/100b]"
argument_list|)
argument_list|)
expr_stmt|;
name|NodesStatsResponse
name|stats
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setBreaker
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|breaks
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeStats
name|stat
range|:
name|stats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|CircuitBreakerStats
name|breakerStats
init|=
name|stat
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|)
decl_stmt|;
name|breaks
operator|+=
name|breakerStats
operator|.
name|getTrippedCount
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|breaks
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRamAccountingTermsEnum
specifier|public
name|void
name|testRamAccountingTermsEnum
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|noopBreakerUsed
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> noop breakers used, skipping test"
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
comment|// Create an index where the mappings have a field data filter
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"ramtest"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"mappings\": {\"type\": {\"properties\": {\"test\": "
operator|+
literal|"{\"type\": \"string\",\"fielddata\": {\"filter\": {\"regex\": {\"pattern\": \"^value.*\"}}}}}}}}"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"ramtest"
argument_list|)
expr_stmt|;
comment|// index some different terms so we have some field data for loading
name|int
name|docCount
init|=
name|scaledRandomIntBetween
argument_list|(
literal|300
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|reqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|id
init|=
literal|0
init|;
name|id
operator|<
name|docCount
condition|;
name|id
operator|++
control|)
block|{
name|reqs
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"ramtest"
argument_list|,
literal|"type"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|reqs
argument_list|)
expr_stmt|;
comment|// execute a search that loads field data (sorting on the "test" field)
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"ramtest"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"test"
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// clear field data cache (thus setting the loaded field data back to 0)
name|clearFieldData
argument_list|()
expr_stmt|;
comment|// Update circuit breaker settings
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
literal|"100b"
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
literal|1.05
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
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
name|settings
argument_list|)
argument_list|)
expr_stmt|;
comment|// execute a search that loads field data (sorting on the "test" field)
comment|// again, this time it should trip the breaker
name|assertFailures
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"ramtest"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"test"
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
argument_list|,
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
name|containsString
argument_list|(
literal|"Data too large, data for [test] would be larger than limit of [100/100b]"
argument_list|)
argument_list|)
expr_stmt|;
name|NodesStatsResponse
name|stats
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setBreaker
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|breaks
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeStats
name|stat
range|:
name|stats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|CircuitBreakerStats
name|breakerStats
init|=
name|stat
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|)
decl_stmt|;
name|breaks
operator|+=
name|breakerStats
operator|.
name|getTrippedCount
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|breaks
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that a breaker correctly redistributes to a different breaker, in      * this case, the fielddata breaker borrows space from the request breaker      */
annotation|@
name|Test
DECL|method|testParentChecking
specifier|public
name|void
name|testParentChecking
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|noopBreakerUsed
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> noop breakers used, skipping test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"cb-test"
argument_list|,
literal|1
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
comment|// index some different terms so we have some field data for loading
name|int
name|docCount
init|=
name|scaledRandomIntBetween
argument_list|(
literal|300
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|reqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|id
init|=
literal|0
init|;
name|id
operator|<
name|docCount
condition|;
name|id
operator|++
control|)
block|{
name|reqs
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"cb-test"
argument_list|,
literal|"type"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|reqs
argument_list|)
expr_stmt|;
comment|// We need the request limit beforehand, just from a single node because the limit should always be the same
name|long
name|beforeReqLimit
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setBreaker
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getNodes
argument_list|()
index|[
literal|0
index|]
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|)
operator|.
name|getLimit
argument_list|()
decl_stmt|;
name|Settings
name|resetSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
literal|"10b"
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
literal|1.0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
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
name|resetSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// Perform a search to load field data for the "test" field
try|try
block|{
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"cb-test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"test"
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|errMsg
init|=
literal|"[fielddata] Data too large, data for [test] would be larger than limit of [10/10b]"
decl_stmt|;
name|assertThat
argument_list|(
literal|"Exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|" should contain a CircuitBreakingException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|errMsg
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFailures
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"cb-test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"test"
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
argument_list|,
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
name|containsString
argument_list|(
literal|"Data too large, data for [test] would be larger than limit of [10/10b]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Adjust settings so the parent breaker will fail, but the fielddata breaker doesn't
name|resetSettings
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|TOTAL_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
literal|"15b"
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
literal|"90%"
argument_list|)
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
literal|1.0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|resetSettings
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|// Perform a search to load field data for the "test" field
try|try
block|{
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"cb-test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"test"
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|errMsg
init|=
literal|"[parent] Data too large, data for [test] would be larger than limit of [15/15b]"
decl_stmt|;
name|assertThat
argument_list|(
literal|"Exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|" should contain a CircuitBreakingException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|errMsg
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRequestBreaker
specifier|public
name|void
name|testRequestBreaker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|noopBreakerUsed
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> noop breakers used, skipping test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"cb-test"
argument_list|,
literal|1
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
comment|// Make request breaker limited to a small amount
name|Settings
name|resetSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
literal|"10b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
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
name|resetSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// index some different terms so we have some field data for loading
name|int
name|docCount
init|=
name|scaledRandomIntBetween
argument_list|(
literal|300
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|reqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|id
init|=
literal|0
init|;
name|id
operator|<
name|docCount
condition|;
name|id
operator|++
control|)
block|{
name|reqs
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"cb-test"
argument_list|,
literal|"type"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|reqs
argument_list|)
expr_stmt|;
comment|// A cardinality aggregation uses BigArrays and thus the REQUEST breaker
try|try
block|{
name|client
operator|.
name|prepareSearch
argument_list|(
literal|"cb-test"
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
name|cardinality
argument_list|(
literal|"card"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"aggregation should have tripped the breaker"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|errMsg
init|=
literal|"CircuitBreakingException[[request] Data too large, data for [<reused_arrays>] would be larger than limit of [10/10b]]"
decl_stmt|;
name|assertThat
argument_list|(
literal|"Exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|" should contain a CircuitBreakingException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|errMsg
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Issues a cache clear and waits 30 seconds for the field data breaker to be cleared */
DECL|method|clearFieldData
specifier|public
name|void
name|clearFieldData
parameter_list|()
throws|throws
name|Exception
block|{
name|client
argument_list|()
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
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|NodesStatsResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|clear
argument_list|()
operator|.
name|setBreaker
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeStats
name|nStats
range|:
name|resp
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
literal|"fielddata breaker never reset back to 0"
argument_list|,
name|nStats
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|)
operator|.
name|getEstimated
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomCircuitBreakerRegistration
specifier|public
name|void
name|testCustomCircuitBreakerRegistration
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterable
argument_list|<
name|CircuitBreakerService
argument_list|>
name|serviceIter
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstances
argument_list|(
name|CircuitBreakerService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|breakerName
init|=
literal|"customBreaker"
decl_stmt|;
name|BreakerSettings
name|breakerSettings
init|=
operator|new
name|BreakerSettings
argument_list|(
name|breakerName
argument_list|,
literal|8
argument_list|,
literal|1.03
argument_list|)
decl_stmt|;
name|CircuitBreaker
name|breaker
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CircuitBreakerService
name|s
range|:
name|serviceIter
control|)
block|{
name|s
operator|.
name|registerBreaker
argument_list|(
name|breakerSettings
argument_list|)
expr_stmt|;
name|breaker
operator|=
name|s
operator|.
name|getBreaker
argument_list|(
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|breaker
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|breaker
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
literal|16
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CircuitBreakingException
name|e
parameter_list|)
block|{
comment|// ignore, we forced a circuit break
block|}
block|}
name|NodesStatsResponse
name|stats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|clear
argument_list|()
operator|.
name|setBreaker
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|breaks
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeStats
name|stat
range|:
name|stats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|CircuitBreakerStats
name|breakerStats
init|=
name|stat
operator|.
name|getBreaker
argument_list|()
operator|.
name|getStats
argument_list|(
name|breakerName
argument_list|)
decl_stmt|;
name|breaks
operator|+=
name|breakerStats
operator|.
name|getTrippedCount
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|breaks
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
