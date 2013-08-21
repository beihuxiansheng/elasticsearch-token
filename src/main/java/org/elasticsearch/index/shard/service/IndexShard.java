begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard.service
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|service
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardRouting
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
name|Nullable
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
name|bytes
operator|.
name|BytesReference
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
name|filter
operator|.
name|FilterCacheStats
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
name|filter
operator|.
name|ShardFilterCache
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
name|id
operator|.
name|IdCacheStats
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
name|id
operator|.
name|ShardIdCache
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
name|EngineException
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
name|fielddata
operator|.
name|FieldDataStats
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
name|fielddata
operator|.
name|ShardFieldData
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
name|flush
operator|.
name|FlushStats
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
name|get
operator|.
name|GetStats
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
name|get
operator|.
name|ShardGetService
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
name|indexing
operator|.
name|IndexingStats
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
name|indexing
operator|.
name|ShardIndexingService
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
name|mapper
operator|.
name|SourceToParse
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
name|MergeStats
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
name|percolator
operator|.
name|PercolatorQueriesRegistry
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
name|percolator
operator|.
name|stats
operator|.
name|ShardPercolateService
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
name|refresh
operator|.
name|RefreshStats
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
name|search
operator|.
name|stats
operator|.
name|SearchStats
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
name|search
operator|.
name|stats
operator|.
name|ShardSearchService
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
name|DocsStats
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
name|IndexShardComponent
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
name|store
operator|.
name|StoreStats
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
name|warmer
operator|.
name|ShardIndexWarmerService
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
name|warmer
operator|.
name|WarmerStats
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
name|suggest
operator|.
name|completion
operator|.
name|CompletionStats
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|IndexShard
specifier|public
interface|interface
name|IndexShard
extends|extends
name|IndexShardComponent
block|{
DECL|method|indexingService
name|ShardIndexingService
name|indexingService
parameter_list|()
function_decl|;
DECL|method|getService
name|ShardGetService
name|getService
parameter_list|()
function_decl|;
DECL|method|searchService
name|ShardSearchService
name|searchService
parameter_list|()
function_decl|;
DECL|method|warmerService
name|ShardIndexWarmerService
name|warmerService
parameter_list|()
function_decl|;
DECL|method|filterCache
name|ShardFilterCache
name|filterCache
parameter_list|()
function_decl|;
DECL|method|idCache
name|ShardIdCache
name|idCache
parameter_list|()
function_decl|;
DECL|method|fieldData
name|ShardFieldData
name|fieldData
parameter_list|()
function_decl|;
DECL|method|routingEntry
name|ShardRouting
name|routingEntry
parameter_list|()
function_decl|;
DECL|method|docStats
name|DocsStats
name|docStats
parameter_list|()
function_decl|;
DECL|method|storeStats
name|StoreStats
name|storeStats
parameter_list|()
function_decl|;
DECL|method|indexingStats
name|IndexingStats
name|indexingStats
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
function_decl|;
DECL|method|searchStats
name|SearchStats
name|searchStats
parameter_list|(
name|String
modifier|...
name|groups
parameter_list|)
function_decl|;
DECL|method|getStats
name|GetStats
name|getStats
parameter_list|()
function_decl|;
DECL|method|mergeStats
name|MergeStats
name|mergeStats
parameter_list|()
function_decl|;
DECL|method|refreshStats
name|RefreshStats
name|refreshStats
parameter_list|()
function_decl|;
DECL|method|flushStats
name|FlushStats
name|flushStats
parameter_list|()
function_decl|;
DECL|method|warmerStats
name|WarmerStats
name|warmerStats
parameter_list|()
function_decl|;
DECL|method|filterCacheStats
name|FilterCacheStats
name|filterCacheStats
parameter_list|()
function_decl|;
DECL|method|idCacheStats
name|IdCacheStats
name|idCacheStats
parameter_list|()
function_decl|;
DECL|method|fieldDataStats
name|FieldDataStats
name|fieldDataStats
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
function_decl|;
DECL|method|completionStats
name|CompletionStats
name|completionStats
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
function_decl|;
DECL|method|percolateRegistry
name|PercolatorQueriesRegistry
name|percolateRegistry
parameter_list|()
function_decl|;
DECL|method|shardPercolateService
name|ShardPercolateService
name|shardPercolateService
parameter_list|()
function_decl|;
DECL|method|state
name|IndexShardState
name|state
parameter_list|()
function_decl|;
DECL|method|prepareCreate
name|Engine
operator|.
name|Create
name|prepareCreate
parameter_list|(
name|SourceToParse
name|source
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|create
name|ParsedDocument
name|create
parameter_list|(
name|Engine
operator|.
name|Create
name|create
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|prepareIndex
name|Engine
operator|.
name|Index
name|prepareIndex
parameter_list|(
name|SourceToParse
name|source
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|index
name|ParsedDocument
name|index
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|prepareDelete
name|Engine
operator|.
name|Delete
name|prepareDelete
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|delete
name|void
name|delete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|prepareDeleteByQuery
name|Engine
operator|.
name|DeleteByQuery
name|prepareDeleteByQuery
parameter_list|(
name|BytesReference
name|querySource
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|filteringAliases
parameter_list|,
name|String
modifier|...
name|types
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|deleteByQuery
name|void
name|deleteByQuery
parameter_list|(
name|Engine
operator|.
name|DeleteByQuery
name|deleteByQuery
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|get
name|Engine
operator|.
name|GetResult
name|get
parameter_list|(
name|Engine
operator|.
name|Get
name|get
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|refresh
name|void
name|refresh
parameter_list|(
name|Engine
operator|.
name|Refresh
name|refresh
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Engine
operator|.
name|Flush
name|flush
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|optimize
name|void
name|optimize
parameter_list|(
name|Engine
operator|.
name|Optimize
name|optimize
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|snapshot
parameter_list|<
name|T
parameter_list|>
name|T
name|snapshot
parameter_list|(
name|Engine
operator|.
name|SnapshotHandler
argument_list|<
name|T
argument_list|>
name|snapshotHandler
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|recover
name|void
name|recover
parameter_list|(
name|Engine
operator|.
name|RecoveryHandler
name|recoveryHandler
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|searcher
name|Engine
operator|.
name|Searcher
name|searcher
parameter_list|()
function_decl|;
comment|/**      * Returns<tt>true</tt> if this shard can ignore a recovery attempt made to it (since the already doing/done it)      */
DECL|method|ignoreRecoveryAttempt
specifier|public
name|boolean
name|ignoreRecoveryAttempt
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

