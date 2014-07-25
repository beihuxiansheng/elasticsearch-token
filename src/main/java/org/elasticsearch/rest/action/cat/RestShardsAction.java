begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
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
name|state
operator|.
name|ClusterStateRequest
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
name|state
operator|.
name|ClusterStateResponse
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
name|indices
operator|.
name|stats
operator|.
name|CommonStats
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
name|indices
operator|.
name|stats
operator|.
name|IndicesStatsRequest
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
name|indices
operator|.
name|stats
operator|.
name|IndicesStatsResponse
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
name|Strings
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
name|Table
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
name|Inject
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
name|rest
operator|.
name|RestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestResponseListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestTable
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_class
DECL|class|RestShardsAction
specifier|public
class|class
name|RestShardsAction
extends|extends
name|AbstractCatAction
block|{
annotation|@
name|Inject
DECL|method|RestShardsAction
specifier|public
name|RestShardsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/shards"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/shards/{index}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/shards\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/shards/{index}\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
specifier|public
name|void
name|doRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ClusterStateRequest
name|clusterStateRequest
init|=
operator|new
name|ClusterStateRequest
argument_list|()
decl_stmt|;
name|clusterStateRequest
operator|.
name|local
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"local"
argument_list|,
name|clusterStateRequest
operator|.
name|local
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|clusterStateRequest
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|clear
argument_list|()
operator|.
name|nodes
argument_list|(
literal|true
argument_list|)
operator|.
name|routingTable
argument_list|(
literal|true
argument_list|)
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|state
argument_list|(
name|clusterStateRequest
argument_list|,
operator|new
name|RestActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|processResponse
parameter_list|(
specifier|final
name|ClusterStateResponse
name|clusterStateResponse
parameter_list|)
block|{
name|IndicesStatsRequest
name|indicesStatsRequest
init|=
operator|new
name|IndicesStatsRequest
argument_list|()
decl_stmt|;
name|indicesStatsRequest
operator|.
name|all
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|stats
argument_list|(
name|indicesStatsRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|IndicesStatsResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|IndicesStatsResponse
name|indicesStatsResponse
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|buildTable
argument_list|(
name|request
argument_list|,
name|clusterStateResponse
argument_list|,
name|indicesStatsResponse
argument_list|)
argument_list|,
name|channel
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
name|Table
name|getTableWithHeader
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
block|{
name|Table
name|table
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|table
operator|.
name|startHeaders
argument_list|()
operator|.
name|addCell
argument_list|(
literal|"index"
argument_list|,
literal|"default:true;alias:i,idx;desc:index name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"shard"
argument_list|,
literal|"default:true;alias:s,sh;desc:shard name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"prirep"
argument_list|,
literal|"alias:p,pr,primaryOrReplica;default:true;desc:primary or replica"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"state"
argument_list|,
literal|"default:true;alias:st;desc:shard state"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"docs"
argument_list|,
literal|"alias:d,dc;text-align:right;desc:number of docs in shard"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"store"
argument_list|,
literal|"alias:sto;text-align:right;desc:store size of shard (how much disk it uses)"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"ip"
argument_list|,
literal|"default:true;desc:ip of node where it lives"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"node"
argument_list|,
literal|"default:true;alias:n;desc:name of node where it lives"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"completion.size"
argument_list|,
literal|"alias:cs,completionSize;default:false;text-align:right;desc:size of completion"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"fielddata.memory_size"
argument_list|,
literal|"alias:fm,fielddataMemory;default:false;text-align:right;desc:used fielddata cache"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"fielddata.evictions"
argument_list|,
literal|"alias:fe,fielddataEvictions;default:false;text-align:right;desc:fielddata evictions"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"filter_cache.memory_size"
argument_list|,
literal|"alias:fcm,filterCacheMemory;default:false;text-align:right;desc:used filter cache"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"filter_cache.evictions"
argument_list|,
literal|"alias:fce,filterCacheEvictions;default:false;text-align:right;desc:filter cache evictions"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"flush.total"
argument_list|,
literal|"alias:ft,flushTotal;default:false;text-align:right;desc:number of flushes"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"flush.total_time"
argument_list|,
literal|"alias:ftt,flushTotalTime;default:false;text-align:right;desc:time spent in flush"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.current"
argument_list|,
literal|"alias:gc,getCurrent;default:false;text-align:right;desc:number of current get ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.time"
argument_list|,
literal|"alias:gti,getTime;default:false;text-align:right;desc:time spent in get"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.total"
argument_list|,
literal|"alias:gto,getTotal;default:false;text-align:right;desc:number of get ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.exists_time"
argument_list|,
literal|"alias:geti,getExistsTime;default:false;text-align:right;desc:time spent in successful gets"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.exists_total"
argument_list|,
literal|"alias:geto,getExistsTotal;default:false;text-align:right;desc:number of successful gets"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.missing_time"
argument_list|,
literal|"alias:gmti,getMissingTime;default:false;text-align:right;desc:time spent in failed gets"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"get.missing_total"
argument_list|,
literal|"alias:gmto,getMissingTotal;default:false;text-align:right;desc:number of failed gets"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"id_cache.memory_size"
argument_list|,
literal|"alias:im,idCacheMemory;default:false;text-align:right;desc:used id cache"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"indexing.delete_current"
argument_list|,
literal|"alias:idc,indexingDeleteCurrent;default:false;text-align:right;desc:number of current deletions"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"indexing.delete_time"
argument_list|,
literal|"alias:idti,indexingDeleteTime;default:false;text-align:right;desc:time spent in deletions"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"indexing.delete_total"
argument_list|,
literal|"alias:idto,indexingDeleteTotal;default:false;text-align:right;desc:number of delete ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"indexing.index_current"
argument_list|,
literal|"alias:iic,indexingIndexCurrent;default:false;text-align:right;desc:number of current indexing ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"indexing.index_time"
argument_list|,
literal|"alias:iiti,indexingIndexTime;default:false;text-align:right;desc:time spent in indexing"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"indexing.index_total"
argument_list|,
literal|"alias:iito,indexingIndexTotal;default:false;text-align:right;desc:number of indexing ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.current"
argument_list|,
literal|"alias:mc,mergesCurrent;default:false;text-align:right;desc:number of current merges"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.current_docs"
argument_list|,
literal|"alias:mcd,mergesCurrentDocs;default:false;text-align:right;desc:number of current merging docs"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.current_size"
argument_list|,
literal|"alias:mcs,mergesCurrentSize;default:false;text-align:right;desc:size of current merges"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.total"
argument_list|,
literal|"alias:mt,mergesTotal;default:false;text-align:right;desc:number of completed merge ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.total_docs"
argument_list|,
literal|"alias:mtd,mergesTotalDocs;default:false;text-align:right;desc:docs merged"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.total_size"
argument_list|,
literal|"alias:mts,mergesTotalSize;default:false;text-align:right;desc:size merged"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"merges.total_time"
argument_list|,
literal|"alias:mtt,mergesTotalTime;default:false;text-align:right;desc:time spent in merges"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"percolate.current"
argument_list|,
literal|"alias:pc,percolateCurrent;default:false;text-align:right;desc:number of current percolations"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"percolate.memory_size"
argument_list|,
literal|"alias:pm,percolateMemory;default:false;text-align:right;desc:memory used by percolations"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"percolate.queries"
argument_list|,
literal|"alias:pq,percolateQueries;default:false;text-align:right;desc:number of registered percolation queries"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"percolate.time"
argument_list|,
literal|"alias:pti,percolateTime;default:false;text-align:right;desc:time spent percolating"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"percolate.total"
argument_list|,
literal|"alias:pto,percolateTotal;default:false;text-align:right;desc:total percolations"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"refresh.total"
argument_list|,
literal|"alias:rto,refreshTotal;default:false;text-align:right;desc:total refreshes"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"refresh.time"
argument_list|,
literal|"alias:rti,refreshTime;default:false;text-align:right;desc:time spent in refreshes"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.fetch_current"
argument_list|,
literal|"alias:sfc,searchFetchCurrent;default:false;text-align:right;desc:current fetch phase ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.fetch_time"
argument_list|,
literal|"alias:sfti,searchFetchTime;default:false;text-align:right;desc:time spent in fetch phase"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.fetch_total"
argument_list|,
literal|"alias:sfto,searchFetchTotal;default:false;text-align:right;desc:total fetch ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.open_contexts"
argument_list|,
literal|"alias:so,searchOpenContexts;default:false;text-align:right;desc:open search contexts"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.query_current"
argument_list|,
literal|"alias:sqc,searchQueryCurrent;default:false;text-align:right;desc:current query phase ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.query_time"
argument_list|,
literal|"alias:sqti,searchQueryTime;default:false;text-align:right;desc:time spent in query phase"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"search.query_total"
argument_list|,
literal|"alias:sqto,searchQueryTotal;default:false;text-align:right;desc:total query phase ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"segments.count"
argument_list|,
literal|"alias:sc,segmentsCount;default:false;text-align:right;desc:number of segments"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"segments.memory"
argument_list|,
literal|"alias:sm,segmentsMemory;default:false;text-align:right;desc:memory used by segments"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"segments.index_writer_memory"
argument_list|,
literal|"alias:siwm,segmentsIndexWriterMemory;default:false;text-align:right;desc:memory used by index writer"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"segments.index_writer_max_memory"
argument_list|,
literal|"alias:siwmx,segmentsIndexWriterMaxMemory;default:false;text-align:right;desc:maximum memory index writer may use before it must write buffered documents to a new segment"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"segments.version_map_memory"
argument_list|,
literal|"alias:svmm,segmentsVersionMapMemory;default:false;text-align:right;desc:memory used by version map"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"segments.fixed_bitset_memory"
argument_list|,
literal|"alias:sfbm,fixedBitsetMemory;default:false;text-align:right;desc:memory used by fixed bit sets for nested object field types and type filters for types referred in _parent fields"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"warmer.current"
argument_list|,
literal|"alias:wc,warmerCurrent;default:false;text-align:right;desc:current warmer ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"warmer.total"
argument_list|,
literal|"alias:wto,warmerTotal;default:false;text-align:right;desc:total warmer ops"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"warmer.total_time"
argument_list|,
literal|"alias:wtt,warmerTotalTime;default:false;text-align:right;desc:time spent in warmers"
argument_list|)
expr_stmt|;
name|table
operator|.
name|endHeaders
argument_list|()
expr_stmt|;
return|return
name|table
return|;
block|}
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|ClusterStateResponse
name|state
parameter_list|,
name|IndicesStatsResponse
name|stats
parameter_list|)
block|{
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|request
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|state
operator|.
name|getState
argument_list|()
operator|.
name|routingTable
argument_list|()
operator|.
name|allShards
argument_list|()
control|)
block|{
name|CommonStats
name|shardStats
init|=
name|stats
operator|.
name|asMap
argument_list|()
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
name|table
operator|.
name|startRow
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shard
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shard
operator|.
name|primary
argument_list|()
condition|?
literal|"p"
else|:
literal|"r"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shard
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getDocs
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getStore
argument_list|()
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shard
operator|.
name|assignedToNode
argument_list|()
condition|)
block|{
name|String
name|ip
init|=
name|state
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|getHostAddress
argument_list|()
decl_stmt|;
name|StringBuilder
name|name
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|name
operator|.
name|append
argument_list|(
name|state
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shard
operator|.
name|relocating
argument_list|()
condition|)
block|{
name|String
name|reloIp
init|=
name|state
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shard
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
operator|.
name|getHostAddress
argument_list|()
decl_stmt|;
name|String
name|reloNme
init|=
name|state
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shard
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
decl_stmt|;
name|name
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|name
operator|.
name|append
argument_list|(
name|reloIp
argument_list|)
expr_stmt|;
name|name
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|name
operator|.
name|append
argument_list|(
name|reloNme
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|addCell
argument_list|(
name|ip
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|addCell
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getCompletion
argument_list|()
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getFieldData
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getFieldData
argument_list|()
operator|.
name|getEvictions
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getFilterCache
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getFilterCache
argument_list|()
operator|.
name|getEvictions
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getFlush
argument_list|()
operator|.
name|getTotal
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getFlush
argument_list|()
operator|.
name|getTotalTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|getExistsTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|getExistsCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|getMissingTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getGet
argument_list|()
operator|.
name|getMissingCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIdCache
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getCurrentNumDocs
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getCurrentSize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getTotal
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getTotalNumDocs
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getTotalSize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getMerge
argument_list|()
operator|.
name|getTotalTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getPercolate
argument_list|()
operator|.
name|getCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getPercolate
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getPercolate
argument_list|()
operator|.
name|getNumQueries
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getPercolate
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getPercolate
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getRefresh
argument_list|()
operator|.
name|getTotal
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getRefresh
argument_list|()
operator|.
name|getTotalTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getFetchCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getFetchTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getFetchCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getOpenContexts
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getQueryCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getQueryTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSearch
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getQueryCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSegments
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSegments
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSegments
argument_list|()
operator|.
name|getIndexWriterMemory
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSegments
argument_list|()
operator|.
name|getIndexWriterMaxMemory
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSegments
argument_list|()
operator|.
name|getVersionMapMemory
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getSegments
argument_list|()
operator|.
name|getFixedBitSetMemory
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getWarmer
argument_list|()
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getWarmer
argument_list|()
operator|.
name|total
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|shardStats
operator|==
literal|null
condition|?
literal|null
else|:
name|shardStats
operator|.
name|getWarmer
argument_list|()
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|endRow
argument_list|()
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

