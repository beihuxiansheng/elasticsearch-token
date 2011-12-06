begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.stress.indexing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|stress
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|jsr166y
operator|.
name|ThreadLocalRandom
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
name|get
operator|.
name|GetResponse
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
name|SizeValue
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
name|Node
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
name|settingsBuilder
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
comment|/**  * Checks that index operation does not create duplicate documents.  */
end_comment

begin_class
DECL|class|ConcurrentIndexingVersioningTest
specifier|public
class|class
name|ConcurrentIndexingVersioningTest
block|{
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
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Node
name|node1
init|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
name|Node
name|node2
init|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|client
init|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|client
argument_list|(
literal|true
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
specifier|final
name|int
name|NUMBER_OF_DOCS
init|=
literal|10000
decl_stmt|;
specifier|final
name|int
name|NUMBER_OF_THREADS
init|=
literal|10
decl_stmt|;
specifier|final
name|long
name|NUMBER_OF_ITERATIONS
init|=
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"10k"
argument_list|)
operator|.
name|singles
argument_list|()
decl_stmt|;
specifier|final
name|long
name|DELETE_EVERY
init|=
literal|10
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUMBER_OF_THREADS
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|NUMBER_OF_THREADS
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
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
try|try
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
name|NUMBER_OF_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|i
operator|%
name|DELETE_EVERY
operator|)
operator|==
literal|0
condition|)
block|{
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUMBER_OF_DOCS
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUMBER_OF_DOCS
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
block|}
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
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done indexing, verifying docs"
argument_list|)
expr_stmt|;
name|client
operator|.
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
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
name|NUMBER_OF_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|SearchResponse
name|response
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"_id"
argument_list|,
name|id
argument_list|)
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
name|response
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|>
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|i
operator|+
literal|"] FAIL, HITS ["
operator|+
name|response
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
name|GetResponse
name|getResponse
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|id
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
name|getResponse
operator|.
name|exists
argument_list|()
condition|)
block|{
name|long
name|version
init|=
name|getResponse
operator|.
name|version
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|getResponse
operator|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|id
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|getResponse
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|i
operator|+
literal|"] FAIL, EXISTED, and NOT_EXISTED"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|version
operator|!=
name|getResponse
operator|.
name|version
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|i
operator|+
literal|"] FAIL, DIFFERENT VERSIONS: ["
operator|+
name|version
operator|+
literal|"], ["
operator|+
name|getResponse
operator|.
name|version
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|getResponse
operator|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|id
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|getResponse
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|i
operator|+
literal|"] FAIL, EXISTED, and NOT_EXISTED"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done."
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|node1
operator|.
name|close
argument_list|()
expr_stmt|;
name|node2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

