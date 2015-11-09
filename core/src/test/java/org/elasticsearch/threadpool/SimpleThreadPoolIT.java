begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
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
name|info
operator|.
name|NodeInfo
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
name|info
operator|.
name|NodesInfoResponse
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
name|xcontent
operator|.
name|ToXContent
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentParser
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
name|json
operator|.
name|JsonXContent
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
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|elasticsearch
operator|.
name|test
operator|.
name|ESIntegTestCase
operator|.
name|Scope
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
name|ESSingleNodeTestCase
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
name|InternalTestCluster
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
name|hamcrest
operator|.
name|RegexMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
operator|.
name|Names
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
operator|.
name|TribeIT
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertNoFailures
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
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|)
DECL|class|SimpleThreadPoolIT
specifier|public
class|class
name|SimpleThreadPoolIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|testThreadNames
specifier|public
name|void
name|testThreadNames
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|preNodeStartThreadNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|l
range|:
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
control|)
block|{
name|ThreadInfo
name|threadInfo
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|l
argument_list|)
decl_stmt|;
if|if
condition|(
name|threadInfo
operator|!=
literal|null
condition|)
block|{
name|preNodeStartThreadNames
operator|.
name|add
argument_list|(
name|threadInfo
operator|.
name|getThreadName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"pre node threads are {}"
argument_list|,
name|preNodeStartThreadNames
argument_list|)
expr_stmt|;
name|String
name|node
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"do some indexing, flushing, optimize, and searches"
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|IndexRequestBuilder
index|[]
name|builders
init|=
operator|new
name|IndexRequestBuilder
index|[
name|numDocs
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|builders
index|[
name|i
index|]
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"str_value"
argument_list|,
literal|"s"
operator|+
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"str_values"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
operator|+
operator|(
name|i
operator|*
literal|2
operator|)
block|,
literal|"s"
operator|+
operator|(
name|i
operator|*
literal|2
operator|+
literal|1
operator|)
block|}
argument_list|)
operator|.
name|field
argument_list|(
literal|"l_value"
argument_list|,
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"l_values"
argument_list|,
operator|new
name|int
index|[]
block|{
name|i
operator|*
literal|2
block|,
name|i
operator|*
literal|2
operator|+
literal|1
block|}
argument_list|)
operator|.
name|field
argument_list|(
literal|"d_value"
argument_list|,
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"d_values"
argument_list|,
operator|new
name|double
index|[]
block|{
name|i
operator|*
literal|2
block|,
name|i
operator|*
literal|2
operator|+
literal|1
block|}
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|int
name|numSearches
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|100
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
name|assertNoFailures
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"str_value"
argument_list|,
literal|"s"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoFailures
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"l_value"
argument_list|,
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|threadNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|l
range|:
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
control|)
block|{
name|ThreadInfo
name|threadInfo
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|l
argument_list|)
decl_stmt|;
if|if
condition|(
name|threadInfo
operator|!=
literal|null
condition|)
block|{
name|threadNames
operator|.
name|add
argument_list|(
name|threadInfo
operator|.
name|getThreadName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"post node threads are {}"
argument_list|,
name|threadNames
argument_list|)
expr_stmt|;
name|threadNames
operator|.
name|removeAll
argument_list|(
name|preNodeStartThreadNames
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"post node *new* threads are {}"
argument_list|,
name|threadNames
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|threadName
range|:
name|threadNames
control|)
block|{
comment|// ignore some shared threads we know that are created within the same VM, like the shared discovery one
comment|// or the ones that are occasionally come up from ESSingleNodeTestCase
if|if
condition|(
name|threadName
operator|.
name|contains
argument_list|(
literal|"["
operator|+
name|ESSingleNodeTestCase
operator|.
name|nodeName
argument_list|()
operator|+
literal|"]"
argument_list|)
operator|||
name|threadName
operator|.
name|contains
argument_list|(
literal|"Keep-Alive-Timer"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|nodePrefix
init|=
literal|"("
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|InternalTestCluster
operator|.
name|TRANSPORT_CLIENT_PREFIX
argument_list|)
operator|+
literal|")?("
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|ESIntegTestCase
operator|.
name|SUITE_CLUSTER_NODE_PREFIX
argument_list|)
operator|+
literal|"|"
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|ESIntegTestCase
operator|.
name|TEST_CLUSTER_NODE_PREFIX
argument_list|)
operator|+
literal|"|"
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|TribeIT
operator|.
name|SECOND_CLUSTER_NODE_PREFIX
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertThat
argument_list|(
name|threadName
argument_list|,
name|RegexMatcher
operator|.
name|matches
argument_list|(
literal|"\\["
operator|+
name|nodePrefix
operator|+
literal|"\\d+\\]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUpdatingThreadPoolSettings
specifier|public
name|void
name|testUpdatingThreadPoolSettings
parameter_list|()
throws|throws
name|Exception
block|{
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ThreadPool
name|threadPool
init|=
name|internalCluster
argument_list|()
operator|.
name|getDataNodeInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Check that settings are changed
name|assertThat
argument_list|(
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|threadPool
operator|.
name|executor
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
operator|)
operator|.
name|getQueue
argument_list|()
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
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
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"threadpool.search.queue_size"
argument_list|,
literal|2000
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|threadPool
operator|.
name|executor
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
operator|)
operator|.
name|getQueue
argument_list|()
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that threads continue executing when executor is replaced
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Executor
name|oldExecutor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|threadPool
operator|.
name|executor
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|ex
parameter_list|)
block|{
comment|//
block|}
block|}
argument_list|)
expr_stmt|;
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
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"threadpool.search.queue_size"
argument_list|,
literal|1000
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|threadPool
operator|.
name|executor
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|oldExecutor
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|oldExecutor
operator|)
operator|.
name|isShutdown
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|oldExecutor
operator|)
operator|.
name|isTerminating
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|oldExecutor
operator|)
operator|.
name|isTerminated
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// Make sure that new thread executor is functional
name|threadPool
operator|.
name|executor
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|ex
parameter_list|)
block|{
comment|//
block|}
block|}
argument_list|)
expr_stmt|;
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
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"threadpool.search.queue_size"
argument_list|,
literal|500
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// Check that node info is correct
name|NodesInfoResponse
name|nodesInfoResponse
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
name|prepareNodesInfo
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|NodeInfo
name|nodeInfo
init|=
name|nodesInfoResponse
operator|.
name|getNodes
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ThreadPool
operator|.
name|Info
name|info
range|:
name|nodeInfo
operator|.
name|getThreadPool
argument_list|()
control|)
block|{
if|if
condition|(
name|info
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|Names
operator|.
name|SEARCH
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|info
operator|.
name|getThreadPoolType
argument_list|()
argument_list|,
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|FIXED
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertThat
argument_list|(
name|found
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThreadPoolLeakingThreadsWithTribeNode
specifier|public
name|void
name|testThreadPoolLeakingThreadsWithTribeNode
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"thread_pool_leaking_threads_tribe_node"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.t1.cluster.name"
argument_list|,
literal|"non_existing_cluster"
argument_list|)
comment|//trigger initialization failure of one of the tribes (doesn't require starting the node)
operator|.
name|put
argument_list|(
literal|"tribe.t1.plugin.mandatory"
argument_list|,
literal|"non_existing"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"The node startup is supposed to fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|//all good
name|assertThat
argument_list|(
name|t
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"mandatory plugins [non_existing]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getPoolSettingsThroughJson
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getPoolSettingsThroughJson
parameter_list|(
name|ThreadPoolInfo
name|info
parameter_list|,
name|String
name|poolName
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|info
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|poolsMap
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
init|)
block|{
name|poolsMap
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
return|return
call|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
call|)
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|poolsMap
operator|.
name|get
argument_list|(
literal|"thread_pool"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|poolName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

