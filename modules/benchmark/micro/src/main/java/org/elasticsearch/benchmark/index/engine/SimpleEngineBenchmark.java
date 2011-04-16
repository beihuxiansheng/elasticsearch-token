begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|index
operator|.
name|engine
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|LoadFirstFieldSelector
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
name|Term
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
name|MatchAllDocsQuery
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
name|TermQuery
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
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|memory
operator|.
name|ByteBufferCache
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
name|StopWatch
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
name|Lucene
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
name|index
operator|.
name|Index
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
name|analysis
operator|.
name|AnalysisService
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
name|bloom
operator|.
name|none
operator|.
name|NoneBloomCache
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
name|deletionpolicy
operator|.
name|KeepOnlyLastDeletionPolicy
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
name|deletionpolicy
operator|.
name|SnapshotDeletionPolicy
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
name|engine
operator|.
name|robin
operator|.
name|RobinEngine
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
name|ParsedDocument
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
name|merge
operator|.
name|policy
operator|.
name|LogByteSizeMergePolicyProvider
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
name|merge
operator|.
name|scheduler
operator|.
name|ConcurrentMergeSchedulerProvider
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
name|settings
operator|.
name|IndexSettingsService
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
name|shard
operator|.
name|ShardId
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
name|similarity
operator|.
name|SimilarityService
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
name|store
operator|.
name|Store
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
name|store
operator|.
name|memory
operator|.
name|ByteBufferStore
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
name|translog
operator|.
name|fs
operator|.
name|FsTranslog
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
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|lucene
operator|.
name|DocumentBuilder
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
name|common
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SimpleEngineBenchmark
specifier|public
class|class
name|SimpleEngineBenchmark
block|{
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|engine
specifier|private
specifier|final
name|Engine
name|engine
decl_stmt|;
DECL|field|idGenerator
specifier|private
specifier|final
name|AtomicInteger
name|idGenerator
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|contentItems
specifier|private
name|String
index|[]
name|contentItems
init|=
operator|new
name|String
index|[]
block|{
literal|"test1"
block|,
literal|"test2"
block|,
literal|"test3"
block|}
decl_stmt|;
DECL|field|TRANSLOG_PAYLOAD
specifier|private
specifier|static
name|byte
index|[]
name|TRANSLOG_PAYLOAD
init|=
operator|new
name|byte
index|[
literal|12
index|]
decl_stmt|;
DECL|field|lastRefreshedId
specifier|private
specifier|volatile
name|int
name|lastRefreshedId
init|=
literal|0
decl_stmt|;
DECL|field|create
specifier|private
name|boolean
name|create
init|=
literal|false
decl_stmt|;
DECL|field|searcherIterations
specifier|private
name|int
name|searcherIterations
init|=
literal|10
decl_stmt|;
DECL|field|searcherThreads
specifier|private
name|Thread
index|[]
name|searcherThreads
init|=
operator|new
name|Thread
index|[
literal|1
index|]
decl_stmt|;
DECL|field|writerIterations
specifier|private
name|int
name|writerIterations
init|=
literal|10
decl_stmt|;
DECL|field|writerThreads
specifier|private
name|Thread
index|[]
name|writerThreads
init|=
operator|new
name|Thread
index|[
literal|1
index|]
decl_stmt|;
DECL|field|refreshSchedule
specifier|private
name|TimeValue
name|refreshSchedule
init|=
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|flushSchedule
specifier|private
name|TimeValue
name|flushSchedule
init|=
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
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
comment|// scheduled thread pool for both refresh and flush operations
DECL|field|scheduledExecutorService
specifier|private
name|ScheduledExecutorService
name|scheduledExecutorService
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|method|SimpleEngineBenchmark
specifier|public
name|SimpleEngineBenchmark
parameter_list|(
name|Store
name|store
parameter_list|,
name|Engine
name|engine
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|engine
operator|=
name|engine
expr_stmt|;
block|}
DECL|method|numberOfContentItems
specifier|public
name|SimpleEngineBenchmark
name|numberOfContentItems
parameter_list|(
name|int
name|numberOfContentItems
parameter_list|)
block|{
name|contentItems
operator|=
operator|new
name|String
index|[
name|numberOfContentItems
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
name|contentItems
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|contentItems
index|[
name|i
index|]
operator|=
literal|"content"
operator|+
name|i
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|searcherThreads
specifier|public
name|SimpleEngineBenchmark
name|searcherThreads
parameter_list|(
name|int
name|numberOfSearcherThreads
parameter_list|)
block|{
name|searcherThreads
operator|=
operator|new
name|Thread
index|[
name|numberOfSearcherThreads
index|]
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|searcherIterations
specifier|public
name|SimpleEngineBenchmark
name|searcherIterations
parameter_list|(
name|int
name|searcherIterations
parameter_list|)
block|{
name|this
operator|.
name|searcherIterations
operator|=
name|searcherIterations
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|writerThreads
specifier|public
name|SimpleEngineBenchmark
name|writerThreads
parameter_list|(
name|int
name|numberOfWriterThreads
parameter_list|)
block|{
name|writerThreads
operator|=
operator|new
name|Thread
index|[
name|numberOfWriterThreads
index|]
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|writerIterations
specifier|public
name|SimpleEngineBenchmark
name|writerIterations
parameter_list|(
name|int
name|writerIterations
parameter_list|)
block|{
name|this
operator|.
name|writerIterations
operator|=
name|writerIterations
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|refreshSchedule
specifier|public
name|SimpleEngineBenchmark
name|refreshSchedule
parameter_list|(
name|TimeValue
name|refreshSchedule
parameter_list|)
block|{
name|this
operator|.
name|refreshSchedule
operator|=
name|refreshSchedule
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|flushSchedule
specifier|public
name|SimpleEngineBenchmark
name|flushSchedule
parameter_list|(
name|TimeValue
name|flushSchedule
parameter_list|)
block|{
name|this
operator|.
name|flushSchedule
operator|=
name|flushSchedule
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|create
specifier|public
name|SimpleEngineBenchmark
name|create
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|create
operator|=
name|create
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|SimpleEngineBenchmark
name|build
parameter_list|()
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
name|Thread
argument_list|(
operator|new
name|SearcherThread
argument_list|()
argument_list|,
literal|"Searcher["
operator|+
name|i
operator|+
literal|"]"
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
name|writerThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writerThreads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|WriterThread
argument_list|()
argument_list|,
literal|"Writer["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|searcherThreads
operator|.
name|length
operator|+
name|writerThreads
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
name|writerThreads
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
name|writerThreads
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// warmup by indexing all content items
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
for|for
control|(
name|String
name|contentItem
range|:
name|contentItems
control|)
block|{
name|int
name|id
init|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|String
name|sId
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|doc
argument_list|()
operator|.
name|add
argument_list|(
name|field
argument_list|(
literal|"_id"
argument_list|,
name|sId
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|field
argument_list|(
literal|"content"
argument_list|,
name|contentItem
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ParsedDocument
name|pDoc
init|=
operator|new
name|ParsedDocument
argument_list|(
name|sId
argument_list|,
name|sId
argument_list|,
literal|"type"
argument_list|,
literal|null
argument_list|,
name|doc
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|,
name|TRANSLOG_PAYLOAD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|engine
operator|.
name|create
argument_list|(
operator|new
name|Engine
operator|.
name|Create
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_id"
argument_list|,
name|sId
argument_list|)
argument_list|,
name|pDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|engine
operator|.
name|index
argument_list|(
operator|new
name|Engine
operator|.
name|Index
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_id"
argument_list|,
name|sId
argument_list|)
argument_list|,
name|pDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|engine
operator|.
name|refresh
argument_list|(
operator|new
name|Engine
operator|.
name|Refresh
argument_list|(
literal|true
argument_list|)
argument_list|)
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
literal|"Warmup of ["
operator|+
name|contentItems
operator|.
name|length
operator|+
literal|"] content items, took "
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|run
specifier|public
name|void
name|run
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
name|writerThreads
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
name|Refresher
name|refresher
init|=
operator|new
name|Refresher
argument_list|()
decl_stmt|;
name|scheduledExecutorService
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|refresher
argument_list|,
name|refreshSchedule
operator|.
name|millis
argument_list|()
argument_list|,
name|refreshSchedule
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|Flusher
name|flusher
init|=
operator|new
name|Flusher
argument_list|()
decl_stmt|;
name|scheduledExecutorService
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|flusher
argument_list|,
name|flushSchedule
operator|.
name|millis
argument_list|()
argument_list|,
name|flushSchedule
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Summary"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   -- Readers ["
operator|+
name|searcherThreads
operator|.
name|length
operator|+
literal|"] with ["
operator|+
name|searcherIterations
operator|+
literal|"] iterations"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   -- Writers ["
operator|+
name|writerThreads
operator|.
name|length
operator|+
literal|"] with ["
operator|+
name|writerIterations
operator|+
literal|"] iterations"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   -- Took: "
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   -- Refresh ["
operator|+
name|refresher
operator|.
name|id
operator|+
literal|"] took: "
operator|+
name|refresher
operator|.
name|stopWatch
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   -- Flush ["
operator|+
name|flusher
operator|.
name|id
operator|+
literal|"] took: "
operator|+
name|flusher
operator|.
name|stopWatch
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   -- Store size "
operator|+
name|store
operator|.
name|estimateSize
argument_list|()
argument_list|)
expr_stmt|;
name|scheduledExecutorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|engine
operator|.
name|refresh
argument_list|(
operator|new
name|Engine
operator|.
name|Refresh
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|stopWatch
operator|=
operator|new
name|StopWatch
argument_list|()
expr_stmt|;
name|stopWatch
operator|.
name|start
argument_list|()
expr_stmt|;
name|Engine
operator|.
name|Searcher
name|searcher
init|=
name|engine
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|idGenerator
operator|.
name|get
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
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
literal|"   -- Indexed ["
operator|+
name|idGenerator
operator|.
name|get
argument_list|()
operator|+
literal|"] docs, found ["
operator|+
name|topDocs
operator|.
name|totalHits
operator|+
literal|"] hits, took "
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
DECL|method|content
specifier|private
name|String
name|content
parameter_list|(
name|long
name|number
parameter_list|)
block|{
return|return
name|contentItems
index|[
operator|(
call|(
name|int
call|)
argument_list|(
name|number
operator|%
name|contentItems
operator|.
name|length
argument_list|)
operator|)
index|]
return|;
block|}
DECL|class|Flusher
specifier|private
class|class
name|Flusher
implements|implements
name|Runnable
block|{
DECL|field|stopWatch
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|stopWatch
operator|.
name|start
argument_list|(
literal|""
operator|+
operator|++
name|id
argument_list|)
expr_stmt|;
name|engine
operator|.
name|flush
argument_list|(
operator|new
name|Engine
operator|.
name|Flush
argument_list|()
argument_list|)
expr_stmt|;
name|stopWatch
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Refresher
specifier|private
class|class
name|Refresher
implements|implements
name|Runnable
block|{
DECL|field|stopWatch
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|method|run
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|stopWatch
operator|.
name|start
argument_list|(
literal|""
operator|+
operator|++
name|id
argument_list|)
expr_stmt|;
name|int
name|lastId
init|=
name|idGenerator
operator|.
name|get
argument_list|()
decl_stmt|;
name|engine
operator|.
name|refresh
argument_list|(
operator|new
name|Engine
operator|.
name|Refresh
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|lastRefreshedId
operator|=
name|lastId
expr_stmt|;
name|stopWatch
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|SearcherThread
specifier|private
class|class
name|SearcherThread
implements|implements
name|Runnable
block|{
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searcherIterations
condition|;
name|i
operator|++
control|)
block|{
name|Engine
operator|.
name|Searcher
name|searcher
init|=
name|engine
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
name|content
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// read one
name|searcher
operator|.
name|searcher
argument_list|()
operator|.
name|doc
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
operator|new
name|LoadFirstFieldSelector
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|release
argument_list|()
expr_stmt|;
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
name|out
operator|.
name|println
argument_list|(
literal|"Searcher thread failed"
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
DECL|class|WriterThread
specifier|private
class|class
name|WriterThread
implements|implements
name|Runnable
block|{
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|writerIterations
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|String
name|sId
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|doc
argument_list|()
operator|.
name|add
argument_list|(
name|field
argument_list|(
literal|"_id"
argument_list|,
name|sId
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|field
argument_list|(
literal|"content"
argument_list|,
name|content
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ParsedDocument
name|pDoc
init|=
operator|new
name|ParsedDocument
argument_list|(
name|sId
argument_list|,
name|sId
argument_list|,
literal|"type"
argument_list|,
literal|null
argument_list|,
name|doc
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|,
name|TRANSLOG_PAYLOAD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|engine
operator|.
name|create
argument_list|(
operator|new
name|Engine
operator|.
name|Create
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_id"
argument_list|,
name|sId
argument_list|)
argument_list|,
name|pDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|engine
operator|.
name|index
argument_list|(
operator|new
name|Engine
operator|.
name|Index
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_id"
argument_list|,
name|sId
argument_list|)
argument_list|,
name|pDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|out
operator|.
name|println
argument_list|(
literal|"Writer thread failed"
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
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
literal|"index"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|EMPTY_SETTINGS
decl_stmt|;
comment|//        Store store = new RamStore(shardId, settings);
name|Store
name|store
init|=
operator|new
name|ByteBufferStore
argument_list|(
name|shardId
argument_list|,
name|settings
argument_list|,
literal|null
argument_list|,
operator|new
name|ByteBufferCache
argument_list|(
name|settings
argument_list|)
argument_list|)
decl_stmt|;
comment|//        Store store = new NioFsStore(shardId, settings);
name|store
operator|.
name|deleteContent
argument_list|()
expr_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|ThreadPool
argument_list|()
decl_stmt|;
name|SnapshotDeletionPolicy
name|deletionPolicy
init|=
operator|new
name|SnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastDeletionPolicy
argument_list|(
name|shardId
argument_list|,
name|settings
argument_list|)
argument_list|)
decl_stmt|;
name|Engine
name|engine
init|=
operator|new
name|RobinEngine
argument_list|(
name|shardId
argument_list|,
name|settings
argument_list|,
operator|new
name|IndexSettingsService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|,
name|settings
argument_list|)
argument_list|,
name|store
argument_list|,
name|deletionPolicy
argument_list|,
operator|new
name|FsTranslog
argument_list|(
name|shardId
argument_list|,
name|EMPTY_SETTINGS
argument_list|,
operator|new
name|File
argument_list|(
literal|"work/fs-translog"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|,
operator|new
name|LogByteSizeMergePolicyProvider
argument_list|(
name|store
argument_list|,
operator|new
name|IndexSettingsService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|,
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
argument_list|,
operator|new
name|ConcurrentMergeSchedulerProvider
argument_list|(
name|shardId
argument_list|,
name|settings
argument_list|)
argument_list|,
operator|new
name|AnalysisService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SimilarityService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
operator|new
name|NoneBloomCache
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|engine
operator|.
name|start
argument_list|()
expr_stmt|;
name|SimpleEngineBenchmark
name|benchmark
init|=
operator|new
name|SimpleEngineBenchmark
argument_list|(
name|store
argument_list|,
name|engine
argument_list|)
operator|.
name|numberOfContentItems
argument_list|(
literal|1000
argument_list|)
operator|.
name|searcherThreads
argument_list|(
literal|50
argument_list|)
operator|.
name|searcherIterations
argument_list|(
literal|10000
argument_list|)
operator|.
name|writerThreads
argument_list|(
literal|10
argument_list|)
operator|.
name|writerIterations
argument_list|(
literal|10000
argument_list|)
operator|.
name|refreshSchedule
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
operator|.
name|flushSchedule
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|)
operator|.
name|create
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|benchmark
operator|.
name|run
argument_list|()
expr_stmt|;
name|engine
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

