begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.stress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|stress
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
name|SearchResponse
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
name|client
operator|.
name|Requests
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
name|xcontent
operator|.
name|XContentQueryBuilder
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
name|util
operator|.
name|StopWatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|CountDownLatch
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
name|CyclicBarrier
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
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
name|*
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
name|xcontent
operator|.
name|FilterBuilders
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
name|index
operator|.
name|query
operator|.
name|xcontent
operator|.
name|QueryBuilders
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
name|node
operator|.
name|NodeBuilder
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
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
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
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
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
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NodesStressTest
specifier|public
class|class
name|NodesStressTest
block|{
DECL|field|nodes
specifier|private
name|Node
index|[]
name|nodes
decl_stmt|;
DECL|field|numberOfNodes
specifier|private
name|int
name|numberOfNodes
init|=
literal|2
decl_stmt|;
DECL|field|clients
specifier|private
name|Client
index|[]
name|clients
decl_stmt|;
DECL|field|idGenerator
specifier|private
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|fieldNumLimit
specifier|private
name|int
name|fieldNumLimit
init|=
literal|50
decl_stmt|;
DECL|field|searcherIterations
specifier|private
name|long
name|searcherIterations
init|=
literal|10
decl_stmt|;
DECL|field|searcherThreads
specifier|private
name|Searcher
index|[]
name|searcherThreads
init|=
operator|new
name|Searcher
index|[
literal|1
index|]
decl_stmt|;
DECL|field|indexIterations
specifier|private
name|long
name|indexIterations
init|=
literal|10
decl_stmt|;
DECL|field|indexThreads
specifier|private
name|Indexer
index|[]
name|indexThreads
init|=
operator|new
name|Indexer
index|[
literal|1
index|]
decl_stmt|;
DECL|field|sleepAfterDone
specifier|private
name|TimeValue
name|sleepAfterDone
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|latch
specifier|private
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|barrier1
specifier|private
name|CyclicBarrier
name|barrier1
decl_stmt|;
DECL|field|barrier2
specifier|private
name|CyclicBarrier
name|barrier2
decl_stmt|;
DECL|method|NodesStressTest
specifier|public
name|NodesStressTest
parameter_list|()
block|{     }
DECL|method|numberOfNodes
specifier|public
name|NodesStressTest
name|numberOfNodes
parameter_list|(
name|int
name|numberOfNodes
parameter_list|)
block|{
name|this
operator|.
name|numberOfNodes
operator|=
name|numberOfNodes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fieldNumLimit
specifier|public
name|NodesStressTest
name|fieldNumLimit
parameter_list|(
name|int
name|fieldNumLimit
parameter_list|)
block|{
name|this
operator|.
name|fieldNumLimit
operator|=
name|fieldNumLimit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|searchIterations
specifier|public
name|NodesStressTest
name|searchIterations
parameter_list|(
name|int
name|searchIterations
parameter_list|)
block|{
name|this
operator|.
name|searcherIterations
operator|=
name|searchIterations
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|searcherThreads
specifier|public
name|NodesStressTest
name|searcherThreads
parameter_list|(
name|int
name|numberOfSearcherThreads
parameter_list|)
block|{
name|searcherThreads
operator|=
operator|new
name|Searcher
index|[
name|numberOfSearcherThreads
index|]
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexIterations
specifier|public
name|NodesStressTest
name|indexIterations
parameter_list|(
name|long
name|indexIterations
parameter_list|)
block|{
name|this
operator|.
name|indexIterations
operator|=
name|indexIterations
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexThreads
specifier|public
name|NodesStressTest
name|indexThreads
parameter_list|(
name|int
name|numberOfWriterThreads
parameter_list|)
block|{
name|indexThreads
operator|=
operator|new
name|Indexer
index|[
name|numberOfWriterThreads
index|]
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|sleepAfterDone
specifier|public
name|NodesStressTest
name|sleepAfterDone
parameter_list|(
name|TimeValue
name|time
parameter_list|)
block|{
name|this
operator|.
name|sleepAfterDone
operator|=
name|time
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|NodesStressTest
name|build
parameter_list|(
name|Settings
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|settings
operator|=
name|settingsBuilder
argument_list|()
comment|//                .put("index.engine.robin.refreshInterval", 1, TimeUnit.SECONDS)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|5
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|nodes
operator|=
operator|new
name|Node
index|[
name|numberOfNodes
index|]
expr_stmt|;
name|clients
operator|=
operator|new
name|Client
index|[
name|numberOfNodes
index|]
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
name|numberOfNodes
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|nodeBuilder
argument_list|()
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
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"node"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
name|clients
index|[
name|i
index|]
operator|=
name|nodes
index|[
name|i
index|]
operator|.
name|client
argument_list|()
expr_stmt|;
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
name|searcherThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|searcherThreads
index|[
name|i
index|]
operator|=
operator|new
name|Searcher
argument_list|(
name|i
argument_list|)
expr_stmt|;
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
name|indexThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexThreads
index|[
name|i
index|]
operator|=
operator|new
name|Indexer
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|barrier1
operator|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|barrier2
operator|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// warmup
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|Indexer
name|warmup
init|=
operator|new
name|Indexer
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|max
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|warmup
operator|.
name|start
argument_list|()
expr_stmt|;
name|barrier1
operator|.
name|await
argument_list|()
expr_stmt|;
name|barrier2
operator|.
name|await
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|stopWatch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done Warmup, took ["
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|searcherThreads
operator|.
name|length
operator|+
name|indexThreads
operator|.
name|length
argument_list|)
expr_stmt|;
name|barrier1
operator|=
operator|new
name|CyclicBarrier
argument_list|(
name|searcherThreads
operator|.
name|length
operator|+
name|indexThreads
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|barrier2
operator|=
operator|new
name|CyclicBarrier
argument_list|(
name|searcherThreads
operator|.
name|length
operator|+
name|indexThreads
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Thread
name|t
range|:
name|searcherThreads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|indexThreads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|barrier1
operator|.
name|await
argument_list|()
expr_stmt|;
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
name|stopWatch
operator|.
name|start
argument_list|()
expr_stmt|;
name|barrier2
operator|.
name|await
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|stopWatch
operator|.
name|stop
argument_list|()
expr_stmt|;
for|for
control|(
name|Client
name|client
range|:
name|clients
control|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done, took ["
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepAfterDone
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|Searcher
class|class
name|Searcher
extends|extends
name|Thread
block|{
DECL|field|id
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|counter
name|long
name|counter
init|=
literal|0
decl_stmt|;
DECL|field|max
name|long
name|max
init|=
name|searcherIterations
decl_stmt|;
DECL|method|Searcher
name|Searcher
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|super
argument_list|(
literal|"Searcher"
operator|+
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier1
operator|.
name|await
argument_list|()
expr_stmt|;
name|barrier2
operator|.
name|await
argument_list|()
expr_stmt|;
for|for
control|(
init|;
name|counter
operator|<
name|max
condition|;
name|counter
operator|++
control|)
block|{
name|Client
name|client
init|=
name|client
argument_list|(
name|counter
argument_list|)
decl_stmt|;
name|XContentQueryBuilder
name|query
init|=
name|termQuery
argument_list|(
literal|"num"
argument_list|,
name|counter
operator|%
name|fieldNumLimit
argument_list|)
decl_stmt|;
name|query
operator|=
name|constantScoreQuery
argument_list|(
name|queryFilter
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|SearchResponse
name|search
init|=
name|client
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
comment|//                    System.out.println("Got search response, hits [" + search.hits().totalHits() + "]");
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to search:"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|Indexer
class|class
name|Indexer
extends|extends
name|Thread
block|{
DECL|field|id
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|counter
name|long
name|counter
init|=
literal|0
decl_stmt|;
DECL|field|max
name|long
name|max
init|=
name|indexIterations
decl_stmt|;
DECL|method|Indexer
name|Indexer
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|super
argument_list|(
literal|"Indexer"
operator|+
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|max
name|Indexer
name|max
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier1
operator|.
name|await
argument_list|()
expr_stmt|;
name|barrier2
operator|.
name|await
argument_list|()
expr_stmt|;
for|for
control|(
init|;
name|counter
operator|<
name|max
condition|;
name|counter
operator|++
control|)
block|{
name|Client
name|client
init|=
name|client
argument_list|(
name|counter
argument_list|)
decl_stmt|;
name|long
name|id
init|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|client
operator|.
name|index
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|source
argument_list|(
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"num"
argument_list|,
name|id
operator|%
name|fieldNumLimit
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Indexer ["
operator|+
name|id
operator|+
literal|"]: Done"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to index:"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|client
specifier|private
name|Client
name|client
parameter_list|(
name|long
name|i
parameter_list|)
block|{
return|return
name|clients
index|[
operator|(
call|(
name|int
call|)
argument_list|(
name|i
operator|%
name|clients
operator|.
name|length
argument_list|)
operator|)
index|]
return|;
block|}
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
name|NodesStressTest
name|test
init|=
operator|new
name|NodesStressTest
argument_list|()
operator|.
name|numberOfNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|indexThreads
argument_list|(
literal|5
argument_list|)
operator|.
name|indexIterations
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
operator|.
name|searcherThreads
argument_list|(
literal|5
argument_list|)
operator|.
name|searchIterations
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
operator|.
name|sleepAfterDone
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
name|EMPTY_SETTINGS
argument_list|)
decl_stmt|;
name|test
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

