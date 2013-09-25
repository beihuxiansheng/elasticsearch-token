begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.service
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|service
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

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
name|common
operator|.
name|inject
operator|.
name|Injector
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
name|IndexComponent
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
name|IndexShardMissingException
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
name|aliases
operator|.
name|IndexAliasesService
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
name|IndexCache
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
name|IndexEngine
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
name|IndexFieldDataService
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
name|gateway
operator|.
name|IndexGateway
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
name|MapperService
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
name|IndexQueryParserService
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
name|IndexStore
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|IndexService
specifier|public
interface|interface
name|IndexService
extends|extends
name|IndexComponent
extends|,
name|Iterable
argument_list|<
name|IndexShard
argument_list|>
block|{
DECL|method|injector
name|Injector
name|injector
parameter_list|()
function_decl|;
DECL|method|gateway
name|IndexGateway
name|gateway
parameter_list|()
function_decl|;
DECL|method|cache
name|IndexCache
name|cache
parameter_list|()
function_decl|;
DECL|method|fieldData
name|IndexFieldDataService
name|fieldData
parameter_list|()
function_decl|;
DECL|method|settingsService
name|IndexSettingsService
name|settingsService
parameter_list|()
function_decl|;
DECL|method|analysisService
name|AnalysisService
name|analysisService
parameter_list|()
function_decl|;
DECL|method|mapperService
name|MapperService
name|mapperService
parameter_list|()
function_decl|;
DECL|method|queryParserService
name|IndexQueryParserService
name|queryParserService
parameter_list|()
function_decl|;
DECL|method|similarityService
name|SimilarityService
name|similarityService
parameter_list|()
function_decl|;
DECL|method|aliasesService
name|IndexAliasesService
name|aliasesService
parameter_list|()
function_decl|;
DECL|method|engine
name|IndexEngine
name|engine
parameter_list|()
function_decl|;
DECL|method|store
name|IndexStore
name|store
parameter_list|()
function_decl|;
DECL|method|createShard
name|IndexShard
name|createShard
parameter_list|(
name|int
name|sShardId
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
comment|/**      * Removes the shard, does not delete local data or the gateway.      */
DECL|method|removeShard
name|void
name|removeShard
parameter_list|(
name|int
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|numberOfShards
name|int
name|numberOfShards
parameter_list|()
function_decl|;
DECL|method|shardIds
name|ImmutableSet
argument_list|<
name|Integer
argument_list|>
name|shardIds
parameter_list|()
function_decl|;
DECL|method|hasShard
name|boolean
name|hasShard
parameter_list|(
name|int
name|shardId
parameter_list|)
function_decl|;
DECL|method|shard
name|IndexShard
name|shard
parameter_list|(
name|int
name|shardId
parameter_list|)
function_decl|;
DECL|method|shardSafe
name|IndexShard
name|shardSafe
parameter_list|(
name|int
name|shardId
parameter_list|)
throws|throws
name|IndexShardMissingException
function_decl|;
DECL|method|shardInjector
name|Injector
name|shardInjector
parameter_list|(
name|int
name|shardId
parameter_list|)
function_decl|;
DECL|method|shardInjectorSafe
name|Injector
name|shardInjectorSafe
parameter_list|(
name|int
name|shardId
parameter_list|)
throws|throws
name|IndexShardMissingException
function_decl|;
DECL|method|indexUUID
name|String
name|indexUUID
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

