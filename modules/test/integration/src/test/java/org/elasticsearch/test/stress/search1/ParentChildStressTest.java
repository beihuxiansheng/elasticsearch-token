begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.stress.search1
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|stress
operator|.
name|search1
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
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|SearchResponse
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
name|ShardSearchFailure
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
name|client
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
name|search
operator|.
name|SearchHit
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
name|RemoteTransportException
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
name|*
import|;
end_import

begin_class
DECL|class|ParentChildStressTest
specifier|public
class|class
name|ParentChildStressTest
block|{
DECL|field|elasticNode
specifier|private
name|Node
name|elasticNode
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|PARENT_TYPE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|PARENT_TYPE_NAME
init|=
literal|"content"
decl_stmt|;
DECL|field|CHILD_TYPE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CHILD_TYPE_NAME
init|=
literal|"contentFiles"
decl_stmt|;
DECL|field|INDEX_NAME
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
literal|"acme"
decl_stmt|;
comment|/**      * Constructor.  Initialize elastic and create the index/mapping      */
DECL|method|ParentChildStressTest
specifier|public
name|ParentChildStressTest
parameter_list|()
block|{
name|NodeBuilder
name|nodeBuilder
init|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|nodeBuilder
operator|.
name|settings
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|elasticNode
operator|=
name|nodeBuilder
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
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|this
operator|.
name|elasticNode
operator|.
name|client
argument_list|()
expr_stmt|;
name|String
name|mapping
init|=
literal|"{\"contentFiles\": {"
operator|+
literal|"\"_parent\": {"
operator|+
literal|"\"type\" : \"content\""
operator|+
literal|"}}}"
decl_stmt|;
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
name|create
argument_list|(
operator|new
name|CreateIndexRequest
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|mapping
argument_list|(
name|CHILD_TYPE_NAME
argument_list|,
name|mapping
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteTransportException
name|e
parameter_list|)
block|{
comment|// usually means the index is already created.
block|}
block|}
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|elasticNode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Deletes the item from both the parent and child type locations.      */
DECL|method|deleteById
specifier|public
name|void
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|client
operator|.
name|prepareDelete
argument_list|(
name|INDEX_NAME
argument_list|,
name|PARENT_TYPE_NAME
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
name|client
operator|.
name|prepareDelete
argument_list|(
name|INDEX_NAME
argument_list|,
name|CHILD_TYPE_NAME
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
block|}
comment|/**      * Index a parent doc      */
DECL|method|indexParent
specifier|public
name|void
name|indexParent
parameter_list|(
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objectMap
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
comment|// index content
name|client
operator|.
name|prepareIndex
argument_list|(
name|INDEX_NAME
argument_list|,
name|PARENT_TYPE_NAME
argument_list|,
name|id
argument_list|)
operator|.
name|setSource
argument_list|(
name|builder
operator|.
name|map
argument_list|(
name|objectMap
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
comment|/**      * Index the file as a child doc      */
DECL|method|indexChild
specifier|public
name|void
name|indexChild
parameter_list|(
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objectMap
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
name|IndexRequestBuilder
name|indexRequestbuilder
init|=
name|client
operator|.
name|prepareIndex
argument_list|(
name|INDEX_NAME
argument_list|,
name|CHILD_TYPE_NAME
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|indexRequestbuilder
operator|=
name|indexRequestbuilder
operator|.
name|setParent
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|indexRequestbuilder
operator|=
name|indexRequestbuilder
operator|.
name|setSource
argument_list|(
name|builder
operator|.
name|map
argument_list|(
name|objectMap
argument_list|)
argument_list|)
expr_stmt|;
name|indexRequestbuilder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
comment|/**      * Execute a search based on a JSON String in QueryDSL format.      *      * Throws a RuntimeException if there are any shard failures to      * elevate the visibility of the problem.      */
DECL|method|executeSearch
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|executeSearch
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|SearchRequest
name|request
init|=
name|Requests
operator|.
name|searchRequest
argument_list|(
name|INDEX_NAME
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|failures
decl_stmt|;
name|SearchResponse
name|response
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|search
argument_list|(
name|request
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|failures
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
expr_stmt|;
comment|// throw an exception so that we see the shard failures
if|if
condition|(
name|failures
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|String
name|failuresStr
init|=
name|failures
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|failuresStr
operator|.
name|contains
argument_list|(
literal|"reason [No active shards]"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|failures
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SearchHit
name|hit
range|:
name|response
operator|.
name|hits
argument_list|()
control|)
block|{
name|String
name|sourceStr
init|=
name|hit
operator|.
name|sourceAsString
argument_list|()
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|sourceStr
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**      * Create a document as a parent and index it.      * Load a file and index it as a child.      */
DECL|method|indexDoc
specifier|public
name|String
name|indexDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objectMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|objectMap
operator|.
name|put
argument_list|(
literal|"title"
argument_list|,
literal|"this is a document"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objectMap2
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|objectMap2
operator|.
name|put
argument_list|(
literal|"description"
argument_list|,
literal|"child test"
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexParent
argument_list|(
name|id
argument_list|,
name|objectMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexChild
argument_list|(
name|id
argument_list|,
name|objectMap2
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**      * Perform the has_child query for the doc.      *      * Since it might take time to get indexed, it      * loops until it finds the doc.      */
DECL|method|searchDocByChild
specifier|public
name|void
name|searchDocByChild
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|String
name|dslString
init|=
literal|"{\"query\":{"
operator|+
literal|"\"has_child\":{"
operator|+
literal|"\"query\":{"
operator|+
literal|"\"field\":{"
operator|+
literal|"\"description\":\"child test\"}},"
operator|+
literal|"\"type\":\"contentFiles\"}}}"
decl_stmt|;
name|int
name|numTries
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|items
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|items
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|&&
name|numTries
operator|<
literal|20
condition|)
block|{
name|items
operator|=
name|executeSearch
argument_list|(
name|dslString
argument_list|)
expr_stmt|;
name|numTries
operator|++
expr_stmt|;
if|if
condition|(
name|items
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|items
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Exceeded number of retries"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Program to loop on:      * create parent/child doc      * search for the doc      * delete the doc      * repeat the above until shard failure.      *      * Eventually fails with:      *      * [shard [[74wz0lrXRSmSOsJOqgPvlw][acme][1]], reason [RemoteTransportException      * [[Kismet][inet[/10.10.30.52:9300]][search/phase/query]]; nested:      * QueryPhaseExecutionException[[acme][1]:      * query[ConstantScore(child_filter[contentFiles      * /content](filtered(file:mission      * file:statement)->FilterCacheFilterWrapper(      * _type:contentFiles)))],from[0],size[10]: Query Failed [Failed to execute      * child query [filtered(file:mission      * file:statement)->FilterCacheFilterWrapper(_type:contentFiles)]]]; nested:      * ]]      *      * @param args      */
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
block|{
name|ParentChildStressTest
name|elasticTest
init|=
operator|new
name|ParentChildStressTest
argument_list|()
decl_stmt|;
try|try
block|{
comment|// loop a bunch of times - usually fails before the count is done.
name|int
name|NUM_LOOPS
init|=
literal|1000
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Looping ["
operator|+
name|NUM_LOOPS
operator|+
literal|"] times:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
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
name|NUM_LOOPS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|elasticTest
operator|.
name|indexDoc
argument_list|()
decl_stmt|;
name|elasticTest
operator|.
name|searchDocByChild
argument_list|()
expr_stmt|;
name|elasticTest
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    Success: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|elasticTest
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|elasticTest
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

