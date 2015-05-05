begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_comment
DECL|package|org.elasticsearch.test
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
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
name|RandomInts
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
name|ElasticsearchException
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
name|BulkItemResponse
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
name|bulk
operator|.
name|BulkResponse
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|junit
operator|.
name|Assert
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
name|Random
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
name|CopyOnWriteArrayList
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
name|Semaphore
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|emptyIterable
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

begin_class
DECL|class|BackgroundIndexer
specifier|public
class|class
name|BackgroundIndexer
implements|implements
name|AutoCloseable
block|{
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|writers
specifier|final
name|Thread
index|[]
name|writers
decl_stmt|;
DECL|field|stopLatch
specifier|final
name|CountDownLatch
name|stopLatch
decl_stmt|;
DECL|field|failures
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|Throwable
argument_list|>
name|failures
decl_stmt|;
DECL|field|stop
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|idGenerator
specifier|final
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|indexCounter
specifier|final
name|AtomicLong
name|indexCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|startLatch
specifier|final
name|CountDownLatch
name|startLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|hasBudget
specifier|final
name|AtomicBoolean
name|hasBudget
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// when set to true, writers will acquire writes from a semaphore
DECL|field|availableBudget
specifier|final
name|Semaphore
name|availableBudget
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|minFieldSize
specifier|volatile
name|int
name|minFieldSize
init|=
literal|10
decl_stmt|;
DECL|field|maxFieldSize
specifier|volatile
name|int
name|maxFieldSize
init|=
literal|140
decl_stmt|;
comment|/**      * Start indexing in the background using a random number of threads.      *      * @param index  index name to index into      * @param type   document type      * @param client client to use      */
DECL|method|BackgroundIndexer
specifier|public
name|BackgroundIndexer
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|client
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start indexing in the background using a random number of threads. Indexing will be paused after numOfDocs docs has      * been indexed.      *      * @param index     index name to index into      * @param type      document type      * @param client    client to use      * @param numOfDocs number of document to index before pausing. Set to -1 to have no limit.      */
DECL|method|BackgroundIndexer
specifier|public
name|BackgroundIndexer
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Client
name|client
parameter_list|,
name|int
name|numOfDocs
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|client
argument_list|,
name|numOfDocs
argument_list|,
name|RandomizedTest
operator|.
name|scaledRandomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start indexing in the background using a given number of threads. Indexing will be paused after numOfDocs docs has      * been indexed.      *      * @param index       index name to index into      * @param type        document type      * @param client      client to use      * @param numOfDocs   number of document to index before pausing. Set to -1 to have no limit.      * @param writerCount number of indexing threads to use      */
DECL|method|BackgroundIndexer
specifier|public
name|BackgroundIndexer
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Client
name|client
parameter_list|,
name|int
name|numOfDocs
parameter_list|,
specifier|final
name|int
name|writerCount
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|client
argument_list|,
name|numOfDocs
argument_list|,
name|writerCount
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start indexing in the background using a given number of threads. Indexing will be paused after numOfDocs docs has      * been indexed.      *      * @param index       index name to index into      * @param type        document type      * @param client      client to use      * @param numOfDocs   number of document to index before pausing. Set to -1 to have no limit.      * @param writerCount number of indexing threads to use      * @param autoStart   set to true to start indexing as soon as all threads have been created.      * @param random      random instance to use      */
DECL|method|BackgroundIndexer
specifier|public
name|BackgroundIndexer
parameter_list|(
specifier|final
name|String
name|index
parameter_list|,
specifier|final
name|String
name|type
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|,
specifier|final
name|int
name|numOfDocs
parameter_list|,
specifier|final
name|int
name|writerCount
parameter_list|,
name|boolean
name|autoStart
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
if|if
condition|(
name|random
operator|==
literal|null
condition|)
block|{
name|random
operator|=
name|RandomizedTest
operator|.
name|getRandom
argument_list|()
expr_stmt|;
block|}
name|failures
operator|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|writers
operator|=
operator|new
name|Thread
index|[
name|writerCount
index|]
expr_stmt|;
name|stopLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|writers
operator|.
name|length
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating {} indexing threads (auto start: [{}], numOfDocs: [{}])"
argument_list|,
name|writerCount
argument_list|,
name|autoStart
argument_list|,
name|numOfDocs
argument_list|)
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
name|writers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|indexerId
init|=
name|i
decl_stmt|;
specifier|final
name|boolean
name|batch
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|threadRandom
init|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|writers
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|id
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|startLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"**** starting indexing thread {}"
argument_list|,
name|indexerId
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|batch
condition|)
block|{
name|int
name|batchSize
init|=
name|threadRandom
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|hasBudget
operator|.
name|get
argument_list|()
condition|)
block|{
name|batchSize
operator|=
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|batchSize
argument_list|,
name|availableBudget
operator|.
name|availablePermits
argument_list|()
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// always try to get at least one
if|if
condition|(
operator|!
name|availableBudget
operator|.
name|tryAcquire
argument_list|(
name|batchSize
argument_list|,
literal|250
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
comment|// time out -> check if we have to stop.
continue|continue;
block|}
block|}
name|BulkRequestBuilder
name|bulkRequest
init|=
name|client
operator|.
name|prepareBulk
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
name|batchSize
condition|;
name|i
operator|++
control|)
block|{
name|id
operator|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
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
name|generateSource
argument_list|(
name|id
argument_list|,
name|threadRandom
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BulkResponse
name|bulkResponse
init|=
name|bulkRequest
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|BulkItemResponse
name|bulkItemResponse
range|:
name|bulkResponse
control|)
block|{
if|if
condition|(
operator|!
name|bulkItemResponse
operator|.
name|isFailed
argument_list|()
condition|)
block|{
name|indexCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"bulk request failure, id: ["
operator|+
name|bulkItemResponse
operator|.
name|getFailure
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|"] message: "
operator|+
name|bulkItemResponse
operator|.
name|getFailure
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|hasBudget
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|availableBudget
operator|.
name|tryAcquire
argument_list|(
literal|250
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
comment|// time out -> check if we have to stop.
continue|continue;
block|}
name|id
operator|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|client
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
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
name|generateSource
argument_list|(
name|id
argument_list|,
name|threadRandom
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|indexCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"**** done indexing thread {}  stop: {} numDocsIndexed: {}"
argument_list|,
name|indexerId
argument_list|,
name|stop
operator|.
name|get
argument_list|()
argument_list|,
name|indexCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|failures
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"**** failed indexing thread {} on doc id {}"
argument_list|,
name|e
argument_list|,
name|indexerId
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|writers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|autoStart
condition|)
block|{
name|start
argument_list|(
name|numOfDocs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|generateSource
specifier|private
name|XContentBuilder
name|generateSource
parameter_list|(
name|long
name|id
parameter_list|,
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|contentLength
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
name|minFieldSize
argument_list|,
name|maxFieldSize
argument_list|)
decl_stmt|;
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
name|contentLength
argument_list|)
decl_stmt|;
while|while
condition|(
name|text
operator|.
name|length
argument_list|()
operator|<
name|contentLength
condition|)
block|{
name|int
name|tokenLength
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|contentLength
operator|-
name|text
operator|.
name|length
argument_list|()
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|text
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|RandomStrings
operator|.
name|randomRealisticUnicodeOfCodepointLength
argument_list|(
name|random
argument_list|,
name|tokenLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|smileBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"text"
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|setBudget
specifier|private
name|void
name|setBudget
parameter_list|(
name|int
name|numOfDocs
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"updating budget to [{}]"
argument_list|,
name|numOfDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|numOfDocs
operator|>=
literal|0
condition|)
block|{
name|hasBudget
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|availableBudget
operator|.
name|release
argument_list|(
name|numOfDocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hasBudget
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Start indexing with no limit to the number of documents */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|start
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start indexing      *      * @param numOfDocs number of document to index before pausing. Set to -1 to have no limit.      */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|int
name|numOfDocs
parameter_list|)
block|{
assert|assert
operator|!
name|stop
operator|.
name|get
argument_list|()
operator|:
literal|"background indexer can not be started after it has stopped"
assert|;
name|setBudget
argument_list|(
name|numOfDocs
argument_list|)
expr_stmt|;
name|startLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
comment|/** Pausing indexing by setting current document limit to 0 */
DECL|method|pauseIndexing
specifier|public
name|void
name|pauseIndexing
parameter_list|()
block|{
name|availableBudget
operator|.
name|drainPermits
argument_list|()
expr_stmt|;
name|setBudget
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Continue indexing after it has paused. No new document limit will be set */
DECL|method|continueIndexing
specifier|public
name|void
name|continueIndexing
parameter_list|()
block|{
name|continueIndexing
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Continue indexing after it has paused.      *      * @param numOfDocs number of document to index before pausing. Set to -1 to have no limit.      */
DECL|method|continueIndexing
specifier|public
name|void
name|continueIndexing
parameter_list|(
name|int
name|numOfDocs
parameter_list|)
block|{
name|setBudget
argument_list|(
name|numOfDocs
argument_list|)
expr_stmt|;
block|}
comment|/** Stop all background threads * */
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertThat
argument_list|(
literal|"timeout while waiting for indexing threads to stop"
argument_list|,
name|stopLatch
operator|.
name|await
argument_list|(
literal|6
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoFailures
argument_list|()
expr_stmt|;
block|}
DECL|method|totalIndexedDocs
specifier|public
name|long
name|totalIndexedDocs
parameter_list|()
block|{
return|return
name|indexCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getFailures
specifier|public
name|Throwable
index|[]
name|getFailures
parameter_list|()
block|{
return|return
name|failures
operator|.
name|toArray
argument_list|(
operator|new
name|Throwable
index|[
name|failures
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|assertNoFailures
specifier|public
name|void
name|assertNoFailures
parameter_list|()
block|{
name|Assert
operator|.
name|assertThat
argument_list|(
name|failures
argument_list|,
name|emptyIterable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** the minimum size in code points of a payload field in the indexed documents */
DECL|method|setMinFieldSize
specifier|public
name|void
name|setMinFieldSize
parameter_list|(
name|int
name|fieldSize
parameter_list|)
block|{
name|minFieldSize
operator|=
name|fieldSize
expr_stmt|;
block|}
comment|/** the minimum size in code points of a payload field in the indexed documents */
DECL|method|setMaxFieldSize
specifier|public
name|void
name|setMaxFieldSize
parameter_list|(
name|int
name|fieldSize
parameter_list|)
block|{
name|maxFieldSize
operator|=
name|fieldSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

