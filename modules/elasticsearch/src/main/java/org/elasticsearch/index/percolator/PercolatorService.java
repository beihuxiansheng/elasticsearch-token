begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
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
name|index
operator|.
name|IndexReader
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
name|*
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
name|search
operator|.
name|TermFilter
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
name|index
operator|.
name|AbstractIndexComponent
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
name|field
operator|.
name|data
operator|.
name|FieldData
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|IdFieldMapper
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
name|SourceFieldMapper
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
name|SourceFieldSelector
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
name|TypeFieldMapper
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
name|service
operator|.
name|IndexService
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
name|IndexSettings
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
name|IndexShardState
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
name|service
operator|.
name|IndexShard
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
name|service
operator|.
name|OperationListener
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
name|IndicesLifecycle
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
name|IndicesService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|PercolatorService
specifier|public
class|class
name|PercolatorService
extends|extends
name|AbstractIndexComponent
block|{
DECL|field|INDEX_NAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
literal|"_percolator"
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|percolator
specifier|private
specifier|final
name|PercolatorExecutor
name|percolator
decl_stmt|;
DECL|field|shardLifecycleListener
specifier|private
specifier|final
name|ShardLifecycleListener
name|shardLifecycleListener
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|initialQueriesFetchDone
specifier|private
name|boolean
name|initialQueriesFetchDone
init|=
literal|false
decl_stmt|;
DECL|method|PercolatorService
annotation|@
name|Inject
specifier|public
name|PercolatorService
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|PercolatorExecutor
name|percolator
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|percolator
operator|=
name|percolator
expr_stmt|;
name|this
operator|.
name|shardLifecycleListener
operator|=
operator|new
name|ShardLifecycleListener
argument_list|()
expr_stmt|;
name|this
operator|.
name|indicesService
operator|.
name|indicesLifecycle
argument_list|()
operator|.
name|addListener
argument_list|(
name|shardLifecycleListener
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|this
operator|.
name|indicesService
operator|.
name|indicesLifecycle
argument_list|()
operator|.
name|removeListener
argument_list|(
name|shardLifecycleListener
argument_list|)
expr_stmt|;
block|}
DECL|method|percolate
specifier|public
name|PercolatorExecutor
operator|.
name|Response
name|percolate
parameter_list|(
name|PercolatorExecutor
operator|.
name|Request
name|request
parameter_list|)
throws|throws
name|PercolatorException
block|{
name|IndexService
name|percolatorIndex
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|INDEX_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|percolatorIndex
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PercolateIndexUnavailable
argument_list|(
operator|new
name|Index
argument_list|(
name|INDEX_NAME
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|percolatorIndex
operator|.
name|numberOfShards
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|PercolateIndexUnavailable
argument_list|(
operator|new
name|Index
argument_list|(
name|INDEX_NAME
argument_list|)
argument_list|)
throw|;
block|}
name|IndexShard
name|percolatorShard
init|=
name|percolatorIndex
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|percolator
operator|.
name|percolate
argument_list|(
name|request
argument_list|,
name|percolatorIndex
argument_list|,
name|percolatorShard
argument_list|)
return|;
block|}
DECL|method|loadQueries
specifier|private
name|void
name|loadQueries
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|IndexService
name|indexService
init|=
name|percolatorIndexService
argument_list|()
decl_stmt|;
name|IndexShard
name|shard
init|=
name|indexService
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Searcher
name|searcher
init|=
name|shard
operator|.
name|searcher
argument_list|()
decl_stmt|;
try|try
block|{
comment|// create a query to fetch all queries that are registered under the index name (which is the type
comment|// in the percolator).
name|Query
name|query
init|=
operator|new
name|DeletionAwareConstantScoreQuery
argument_list|(
name|indexQueriesFilter
argument_list|(
name|indexName
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|QueriesLoaderCollector
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PercolatorException
argument_list|(
name|index
argument_list|,
literal|"failed to load queries from percolator index"
argument_list|)
throw|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|indexQueriesFilter
specifier|private
name|Filter
name|indexQueriesFilter
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|percolatorIndexService
argument_list|()
operator|.
name|cache
argument_list|()
operator|.
name|filter
argument_list|()
operator|.
name|cache
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
name|indexName
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|percolatorAllocated
specifier|private
name|boolean
name|percolatorAllocated
parameter_list|()
block|{
if|if
condition|(
operator|!
name|indicesService
operator|.
name|hasIndex
argument_list|(
name|INDEX_NAME
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|percolatorIndexService
argument_list|()
operator|.
name|numberOfShards
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|percolatorIndexService
argument_list|()
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
operator|!=
name|IndexShardState
operator|.
name|STARTED
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|percolatorIndexService
specifier|private
name|IndexService
name|percolatorIndexService
parameter_list|()
block|{
return|return
name|indicesService
operator|.
name|indexService
argument_list|(
name|INDEX_NAME
argument_list|)
return|;
block|}
DECL|class|QueriesLoaderCollector
class|class
name|QueriesLoaderCollector
extends|extends
name|Collector
block|{
DECL|field|fieldData
specifier|private
name|FieldData
name|fieldData
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|method|setScorer
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{         }
DECL|method|collect
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|fieldData
operator|.
name|stringValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// the _source is the query
name|Document
name|document
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|,
name|SourceFieldSelector
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|source
init|=
name|document
operator|.
name|getBinaryValue
argument_list|(
name|SourceFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
try|try
block|{
name|percolator
operator|.
name|addQuery
argument_list|(
name|id
argument_list|,
name|source
argument_list|,
literal|0
argument_list|,
name|source
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to add query [{}]"
argument_list|,
name|e
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setNextReader
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|fieldData
operator|=
name|percolatorIndexService
argument_list|()
operator|.
name|cache
argument_list|()
operator|.
name|fieldData
argument_list|()
operator|.
name|cache
argument_list|(
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|STRING
argument_list|,
name|reader
argument_list|,
name|IdFieldMapper
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|acceptsDocsOutOfOrder
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|class|ShardLifecycleListener
class|class
name|ShardLifecycleListener
extends|extends
name|IndicesLifecycle
operator|.
name|Listener
block|{
DECL|method|afterIndexShardCreated
annotation|@
name|Override
specifier|public
name|void
name|afterIndexShardCreated
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
if|if
condition|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|INDEX_NAME
argument_list|)
condition|)
block|{
name|indexShard
operator|.
name|addListener
argument_list|(
operator|new
name|RealTimePercolatorOperationListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|afterIndexShardStarted
annotation|@
name|Override
specifier|public
name|void
name|afterIndexShardStarted
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
if|if
condition|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|INDEX_NAME
argument_list|)
condition|)
block|{
comment|// percolator index has started, fetch what we can from it and initialize the indices
comment|// we have
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|initialQueriesFetchDone
condition|)
block|{
return|return;
block|}
comment|// we load the queries for all existing indices
for|for
control|(
name|IndexService
name|indexService
range|:
name|indicesService
control|)
block|{
name|loadQueries
argument_list|(
name|indexService
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|initialQueriesFetchDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|equals
argument_list|(
name|index
argument_list|()
argument_list|)
condition|)
block|{
comment|// not our index, bail
return|return;
block|}
if|if
condition|(
operator|!
name|percolatorAllocated
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// we are only interested when the first shard on this node has been created for an index
comment|// when it does, fetch the relevant queries if not fetched already
if|if
condition|(
name|indicesService
operator|.
name|indexService
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|numberOfShards
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|initialQueriesFetchDone
condition|)
block|{
return|return;
block|}
comment|// we load queries for this index
name|loadQueries
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|initialQueriesFetchDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|class|RealTimePercolatorOperationListener
class|class
name|RealTimePercolatorOperationListener
extends|extends
name|OperationListener
block|{
DECL|method|beforeCreate
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Create
name|beforeCreate
parameter_list|(
name|Engine
operator|.
name|Create
name|create
parameter_list|)
block|{
name|percolator
operator|.
name|addQuery
argument_list|(
name|create
operator|.
name|id
argument_list|()
argument_list|,
name|create
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|create
return|;
block|}
DECL|method|beforeIndex
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Index
name|beforeIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
block|{
name|percolator
operator|.
name|addQuery
argument_list|(
name|index
operator|.
name|id
argument_list|()
argument_list|,
name|index
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|index
return|;
block|}
DECL|method|beforeDelete
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Delete
name|beforeDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
name|percolator
operator|.
name|removeQuery
argument_list|(
name|delete
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|delete
return|;
block|}
block|}
block|}
end_class

end_unit

