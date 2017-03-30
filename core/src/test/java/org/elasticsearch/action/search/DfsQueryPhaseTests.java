begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|ScoreDoc
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
name|TermStatistics
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|MockDirectoryWrapper
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
name|ActionListener
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
name|BigArrays
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
name|concurrent
operator|.
name|AtomicArray
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
name|search
operator|.
name|DocValueFormat
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
name|SearchPhaseResult
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
name|SearchShardTarget
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
name|dfs
operator|.
name|DfsSearchResult
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
name|query
operator|.
name|QuerySearchRequest
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
name|query
operator|.
name|QuerySearchResult
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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|Transport
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
name|io
operator|.
name|UncheckedIOException
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
name|AtomicReference
import|;
end_import

begin_class
DECL|class|DfsQueryPhaseTests
specifier|public
class|class
name|DfsQueryPhaseTests
extends|extends
name|ESTestCase
block|{
DECL|method|newSearchResult
specifier|private
specifier|static
name|DfsSearchResult
name|newSearchResult
parameter_list|(
name|int
name|shardIndex
parameter_list|,
name|long
name|requestId
parameter_list|,
name|SearchShardTarget
name|target
parameter_list|)
block|{
name|DfsSearchResult
name|result
init|=
operator|new
name|DfsSearchResult
argument_list|(
name|requestId
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|result
operator|.
name|setShardIndex
argument_list|(
name|shardIndex
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|testDfsWith2Shards
specifier|public
name|void
name|testDfsWith2Shards
parameter_list|()
throws|throws
name|IOException
block|{
name|AtomicArray
argument_list|<
name|DfsSearchResult
argument_list|>
name|results
init|=
operator|new
name|AtomicArray
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|AtomicArray
argument_list|<
name|SearchPhaseResult
argument_list|>
argument_list|>
name|responseRef
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|results
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|newSearchResult
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node1"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|set
argument_list|(
literal|1
argument_list|,
name|newSearchResult
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node2"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|termsStatistics
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|,
operator|new
name|TermStatistics
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|termsStatistics
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|,
operator|new
name|TermStatistics
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|SearchPhaseController
name|controller
init|=
operator|new
name|SearchPhaseController
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SearchTransportService
name|searchTransportService
init|=
operator|new
name|SearchTransportService
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"search.remote.connect"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|sendExecuteQuery
parameter_list|(
name|Transport
operator|.
name|Connection
name|connection
parameter_list|,
name|QuerySearchRequest
name|request
parameter_list|,
name|SearchTask
name|task
parameter_list|,
name|SearchActionListener
argument_list|<
name|QuerySearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|id
argument_list|()
operator|==
literal|1
condition|)
block|{
name|QuerySearchResult
name|queryResult
init|=
operator|new
name|QuerySearchResult
argument_list|(
literal|123
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node1"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|queryResult
operator|.
name|topDocs
argument_list|(
operator|new
name|TopDocs
argument_list|(
literal|1
argument_list|,
operator|new
name|ScoreDoc
index|[]
block|{
operator|new
name|ScoreDoc
argument_list|(
literal|42
argument_list|,
literal|1.0F
argument_list|)
block|}
argument_list|,
literal|2.0F
argument_list|)
argument_list|,
operator|new
name|DocValueFormat
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|queryResult
operator|.
name|size
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// the size of the result set
name|listener
operator|.
name|onResponse
argument_list|(
name|queryResult
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|id
argument_list|()
operator|==
literal|2
condition|)
block|{
name|QuerySearchResult
name|queryResult
init|=
operator|new
name|QuerySearchResult
argument_list|(
literal|123
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node2"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|queryResult
operator|.
name|topDocs
argument_list|(
operator|new
name|TopDocs
argument_list|(
literal|1
argument_list|,
operator|new
name|ScoreDoc
index|[]
block|{
operator|new
name|ScoreDoc
argument_list|(
literal|84
argument_list|,
literal|2.0F
argument_list|)
block|}
argument_list|,
literal|2.0F
argument_list|)
argument_list|,
operator|new
name|DocValueFormat
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|queryResult
operator|.
name|size
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// the size of the result set
name|listener
operator|.
name|onResponse
argument_list|(
name|queryResult
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"no such request ID: "
operator|+
name|request
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|MockSearchPhaseContext
name|mockSearchPhaseContext
init|=
operator|new
name|MockSearchPhaseContext
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|mockSearchPhaseContext
operator|.
name|searchTransport
operator|=
name|searchTransportService
expr_stmt|;
name|DfsQueryPhase
name|phase
init|=
operator|new
name|DfsQueryPhase
argument_list|(
name|results
argument_list|,
name|controller
argument_list|,
parameter_list|(
name|response
parameter_list|)
lambda|->
operator|new
name|SearchPhase
argument_list|(
literal|"test"
argument_list|)
block|{
block|@Override             public void run(
argument_list|)
throws|throws
name|IOException
block|{
name|responseRef
operator|.
name|set
argument_list|(
name|response
operator|.
name|results
argument_list|)
decl_stmt|;
block|}
block|}
end_class

begin_operator
operator|,
end_operator

begin_expr_stmt
name|mockSearchPhaseContext
end_expr_stmt

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"dfs_query"
argument_list|,
name|phase
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|phase
operator|.
name|run
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|mockSearchPhaseContext
operator|.
name|assertNoFailure
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNotNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNotNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|fetchResult
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|totalHits
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNotNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|fetchResult
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|totalHits
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|84
argument_list|,
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|mockSearchPhaseContext
operator|.
name|releasedSearchContexts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mockSearchPhaseContext
operator|.
name|numSuccess
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      public
DECL|method|testDfsWith1ShardFailed
name|void
name|testDfsWith1ShardFailed
parameter_list|()
throws|throws
name|IOException
block|{
name|AtomicArray
argument_list|<
name|DfsSearchResult
argument_list|>
name|results
init|=
operator|new
name|AtomicArray
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|AtomicArray
argument_list|<
name|SearchPhaseResult
argument_list|>
argument_list|>
name|responseRef
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|results
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|newSearchResult
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node1"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|set
argument_list|(
literal|1
argument_list|,
name|newSearchResult
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node2"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|termsStatistics
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|,
operator|new
name|TermStatistics
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|termsStatistics
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|,
operator|new
name|TermStatistics
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|SearchPhaseController
name|controller
init|=
operator|new
name|SearchPhaseController
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SearchTransportService
name|searchTransportService
init|=
operator|new
name|SearchTransportService
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"search.remote.connect"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|sendExecuteQuery
parameter_list|(
name|Transport
operator|.
name|Connection
name|connection
parameter_list|,
name|QuerySearchRequest
name|request
parameter_list|,
name|SearchTask
name|task
parameter_list|,
name|SearchActionListener
argument_list|<
name|QuerySearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|id
argument_list|()
operator|==
literal|1
condition|)
block|{
name|QuerySearchResult
name|queryResult
init|=
operator|new
name|QuerySearchResult
argument_list|(
literal|123
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node1"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|queryResult
operator|.
name|topDocs
argument_list|(
operator|new
name|TopDocs
argument_list|(
literal|1
argument_list|,
operator|new
name|ScoreDoc
index|[]
block|{
operator|new
name|ScoreDoc
argument_list|(
literal|42
argument_list|,
literal|1.0F
argument_list|)
block|}
argument_list|,
literal|2.0F
argument_list|)
argument_list|,
operator|new
name|DocValueFormat
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|queryResult
operator|.
name|size
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// the size of the result set
name|listener
operator|.
name|onResponse
argument_list|(
name|queryResult
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|id
argument_list|()
operator|==
literal|2
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|MockDirectoryWrapper
operator|.
name|FakeIOException
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"no such request ID: "
operator|+
name|request
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|MockSearchPhaseContext
name|mockSearchPhaseContext
init|=
operator|new
name|MockSearchPhaseContext
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|mockSearchPhaseContext
operator|.
name|searchTransport
operator|=
name|searchTransportService
expr_stmt|;
name|DfsQueryPhase
name|phase
init|=
operator|new
name|DfsQueryPhase
argument_list|(
name|results
argument_list|,
name|controller
argument_list|,
parameter_list|(
name|response
parameter_list|)
lambda|->
operator|new
name|SearchPhase
argument_list|(
literal|"test"
argument_list|)
block|{
block|@Override                 public void run(
argument_list|)
throws|throws
name|IOException
block|{
name|responseRef
operator|.
name|set
argument_list|(
name|response
operator|.
name|results
argument_list|)
decl_stmt|;
block|}
end_function

begin_expr_stmt
unit|},
name|mockSearchPhaseContext
end_expr_stmt

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"dfs_query"
argument_list|,
name|phase
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|phase
operator|.
name|run
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|mockSearchPhaseContext
operator|.
name|assertNoFailure
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNotNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNotNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|fetchResult
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|totalHits
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mockSearchPhaseContext
operator|.
name|numSuccess
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mockSearchPhaseContext
operator|.
name|failures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|mockSearchPhaseContext
operator|.
name|failures
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCause
argument_list|()
operator|instanceof
name|MockDirectoryWrapper
operator|.
name|FakeIOException
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mockSearchPhaseContext
operator|.
name|releasedSearchContexts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|mockSearchPhaseContext
operator|.
name|releasedSearchContexts
operator|.
name|contains
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertNull
argument_list|(
name|responseRef
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}       public
DECL|method|testFailPhaseOnException
name|void
name|testFailPhaseOnException
parameter_list|()
throws|throws
name|IOException
block|{
name|AtomicArray
argument_list|<
name|DfsSearchResult
argument_list|>
name|results
init|=
operator|new
name|AtomicArray
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|AtomicArray
argument_list|<
name|SearchPhaseResult
argument_list|>
argument_list|>
name|responseRef
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|results
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|newSearchResult
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node1"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|set
argument_list|(
literal|1
argument_list|,
name|newSearchResult
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node2"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|termsStatistics
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|,
operator|new
name|TermStatistics
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|termsStatistics
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|,
operator|new
name|TermStatistics
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|SearchPhaseController
name|controller
init|=
operator|new
name|SearchPhaseController
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SearchTransportService
name|searchTransportService
init|=
operator|new
name|SearchTransportService
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"search.remote.connect"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|sendExecuteQuery
parameter_list|(
name|Transport
operator|.
name|Connection
name|connection
parameter_list|,
name|QuerySearchRequest
name|request
parameter_list|,
name|SearchTask
name|task
parameter_list|,
name|SearchActionListener
argument_list|<
name|QuerySearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|id
argument_list|()
operator|==
literal|1
condition|)
block|{
name|QuerySearchResult
name|queryResult
init|=
operator|new
name|QuerySearchResult
argument_list|(
literal|123
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node1"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"na"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|queryResult
operator|.
name|topDocs
argument_list|(
operator|new
name|TopDocs
argument_list|(
literal|1
argument_list|,
operator|new
name|ScoreDoc
index|[]
block|{
operator|new
name|ScoreDoc
argument_list|(
literal|42
argument_list|,
literal|1.0F
argument_list|)
block|}
argument_list|,
literal|2.0F
argument_list|)
argument_list|,
operator|new
name|DocValueFormat
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|queryResult
operator|.
name|size
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// the size of the result set
name|listener
operator|.
name|onResponse
argument_list|(
name|queryResult
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|id
argument_list|()
operator|==
literal|2
condition|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
operator|new
name|MockDirectoryWrapper
operator|.
name|FakeIOException
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"no such request ID: "
operator|+
name|request
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|MockSearchPhaseContext
name|mockSearchPhaseContext
init|=
operator|new
name|MockSearchPhaseContext
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|mockSearchPhaseContext
operator|.
name|searchTransport
operator|=
name|searchTransportService
expr_stmt|;
name|DfsQueryPhase
name|phase
init|=
operator|new
name|DfsQueryPhase
argument_list|(
name|results
argument_list|,
name|controller
argument_list|,
parameter_list|(
name|response
parameter_list|)
lambda|->
operator|new
name|SearchPhase
argument_list|(
literal|"test"
argument_list|)
block|{
block|@Override                 public void run(
argument_list|)
throws|throws
name|IOException
block|{
name|responseRef
operator|.
name|set
argument_list|(
name|response
operator|.
name|results
argument_list|)
decl_stmt|;
block|}
end_function

begin_expr_stmt
unit|},
name|mockSearchPhaseContext
end_expr_stmt

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"dfs_query"
argument_list|,
name|phase
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|expectThrows
argument_list|(
name|UncheckedIOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|phase
operator|.
name|run
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|mockSearchPhaseContext
operator|.
name|releasedSearchContexts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// phase execution will clean up on the contexts
end_comment

unit|}   }
end_unit

