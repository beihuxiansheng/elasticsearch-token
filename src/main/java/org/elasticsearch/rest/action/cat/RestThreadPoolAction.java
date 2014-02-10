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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|node
operator|.
name|info
operator|.
name|NodeInfo
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
name|node
operator|.
name|info
operator|.
name|NodesInfoRequest
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
name|node
operator|.
name|info
operator|.
name|NodesInfoResponse
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
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsRequest
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|node
operator|.
name|DiscoveryNode
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
name|node
operator|.
name|DiscoveryNodes
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
name|common
operator|.
name|transport
operator|.
name|InetSocketTransportAddress
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
name|AbstractRestResponseActionListener
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
name|action
operator|.
name|support
operator|.
name|RestTable
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
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPoolStats
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
DECL|class|RestThreadPoolAction
specifier|public
class|class
name|RestThreadPoolAction
extends|extends
name|AbstractCatAction
block|{
DECL|field|SUPPORTED_NAMES
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|SUPPORTED_NAMES
init|=
operator|new
name|String
index|[]
block|{
name|ThreadPool
operator|.
name|Names
operator|.
name|BULK
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|FLUSH
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GET
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|INDEX
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MERGE
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|OPTIMIZE
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|PERCOLATE
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|REFRESH
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SNAPSHOT
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SUGGEST
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|WARMER
block|}
decl_stmt|;
DECL|field|SUPPORTED_ALIASES
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|SUPPORTED_ALIASES
init|=
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"f"
block|,
literal|"ge"
block|,
literal|"g"
block|,
literal|"i"
block|,
literal|"ma"
block|,
literal|"m"
block|,
literal|"o"
block|,
literal|"p"
block|,
literal|"r"
block|,
literal|"s"
block|,
literal|"sn"
block|,
literal|"su"
block|,
literal|"w"
block|}
decl_stmt|;
DECL|field|DEFAULT_THREAD_POOLS
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|DEFAULT_THREAD_POOLS
init|=
operator|new
name|String
index|[]
block|{
name|ThreadPool
operator|.
name|Names
operator|.
name|BULK
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|INDEX
block|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
block|,     }
decl_stmt|;
DECL|field|ALIAS_TO_THREAD_POOL
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ALIAS_TO_THREAD_POOL
decl_stmt|;
DECL|field|THREAD_POOL_TO_ALIAS
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|THREAD_POOL_TO_ALIAS
decl_stmt|;
static|static
block|{
name|ALIAS_TO_THREAD_POOL
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|SUPPORTED_NAMES
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|supportedThreadPool
range|:
name|SUPPORTED_NAMES
control|)
block|{
name|ALIAS_TO_THREAD_POOL
operator|.
name|put
argument_list|(
name|supportedThreadPool
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|supportedThreadPool
argument_list|)
expr_stmt|;
block|}
name|THREAD_POOL_TO_ALIAS
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|SUPPORTED_NAMES
operator|.
name|length
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
name|SUPPORTED_NAMES
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|THREAD_POOL_TO_ALIAS
operator|.
name|put
argument_list|(
name|SUPPORTED_NAMES
index|[
name|i
index|]
argument_list|,
name|SUPPORTED_ALIASES
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Inject
DECL|method|RestThreadPoolAction
specifier|public
name|RestThreadPoolAction
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
literal|"/_cat/thread_pool"
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
literal|"/_cat/thread_pool\n"
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
parameter_list|)
block|{
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
name|clear
argument_list|()
operator|.
name|nodes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
specifier|final
name|String
index|[]
name|pools
init|=
name|fetchSortedPools
argument_list|(
name|request
argument_list|,
name|DEFAULT_THREAD_POOLS
argument_list|)
decl_stmt|;
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
name|AbstractRestResponseActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|logger
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
specifier|final
name|ClusterStateResponse
name|clusterStateResponse
parameter_list|)
block|{
name|NodesInfoRequest
name|nodesInfoRequest
init|=
operator|new
name|NodesInfoRequest
argument_list|()
decl_stmt|;
name|nodesInfoRequest
operator|.
name|clear
argument_list|()
operator|.
name|process
argument_list|(
literal|true
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|,
operator|new
name|AbstractRestResponseActionListener
argument_list|<
name|NodesInfoResponse
argument_list|>
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|logger
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
specifier|final
name|NodesInfoResponse
name|nodesInfoResponse
parameter_list|)
block|{
name|NodesStatsRequest
name|nodesStatsRequest
init|=
operator|new
name|NodesStatsRequest
argument_list|()
decl_stmt|;
name|nodesStatsRequest
operator|.
name|clear
argument_list|()
operator|.
name|threadPool
argument_list|(
literal|true
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
name|nodesStats
argument_list|(
name|nodesStatsRequest
argument_list|,
operator|new
name|AbstractRestResponseActionListener
argument_list|<
name|NodesStatsResponse
argument_list|>
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|logger
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|NodesStatsResponse
name|nodesStatsResponse
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
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
name|nodesInfoResponse
argument_list|,
name|nodesStatsResponse
argument_list|,
name|pools
argument_list|)
argument_list|,
name|request
argument_list|,
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
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
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|,
literal|"default:false;alias:id,nodeId;desc:unique node id"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"pid"
argument_list|,
literal|"default:false;alias:p;desc:process id"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"host"
argument_list|,
literal|"alias:h;desc:host name"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"ip"
argument_list|,
literal|"alias:i;desc:ip address"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"port"
argument_list|,
literal|"default:false;alias:po;desc:bound transport port"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|requestedPools
init|=
name|fetchSortedPools
argument_list|(
name|request
argument_list|,
name|DEFAULT_THREAD_POOLS
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pool
range|:
name|SUPPORTED_NAMES
control|)
block|{
name|String
name|poolAlias
init|=
name|THREAD_POOL_TO_ALIAS
operator|.
name|get
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|boolean
name|display
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|requestedPool
range|:
name|requestedPools
control|)
block|{
if|if
condition|(
name|pool
operator|.
name|equals
argument_list|(
name|requestedPool
argument_list|)
condition|)
block|{
name|display
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|String
name|defaultDisplayVal
init|=
name|Boolean
operator|.
name|toString
argument_list|(
name|display
argument_list|)
decl_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|pool
operator|+
literal|".active"
argument_list|,
literal|"alias:"
operator|+
name|poolAlias
operator|+
literal|"a;default:"
operator|+
name|defaultDisplayVal
operator|+
literal|";text-align:right;desc:number of active "
operator|+
name|pool
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|pool
operator|+
literal|".size"
argument_list|,
literal|"alias:"
operator|+
name|poolAlias
operator|+
literal|"s;default:false;text-align:right;desc:number of active "
operator|+
name|pool
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|pool
operator|+
literal|".queue"
argument_list|,
literal|"alias:"
operator|+
name|poolAlias
operator|+
literal|"q;default:"
operator|+
name|defaultDisplayVal
operator|+
literal|";text-align:right;desc:number of "
operator|+
name|pool
operator|+
literal|" threads in queue"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|pool
operator|+
literal|".rejected"
argument_list|,
literal|"alias:"
operator|+
name|poolAlias
operator|+
literal|"r;default:"
operator|+
name|defaultDisplayVal
operator|+
literal|";text-align:right;desc:number of rejected "
operator|+
name|pool
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|pool
operator|+
literal|".largest"
argument_list|,
literal|"alias:"
operator|+
name|poolAlias
operator|+
literal|"l;default:false;text-align:right;desc:highest number of seen active "
operator|+
name|pool
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|pool
operator|+
literal|".completed"
argument_list|,
literal|"alias:"
operator|+
name|poolAlias
operator|+
literal|"c;default:false;text-align:right;desc:number of completed "
operator|+
name|pool
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
block|}
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
name|req
parameter_list|,
name|ClusterStateResponse
name|state
parameter_list|,
name|NodesInfoResponse
name|nodesInfo
parameter_list|,
name|NodesStatsResponse
name|nodesStats
parameter_list|,
name|String
index|[]
name|pools
parameter_list|)
block|{
name|boolean
name|fullId
init|=
name|req
operator|.
name|paramAsBoolean
argument_list|(
literal|"full_id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DiscoveryNodes
name|nodes
init|=
name|state
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|req
argument_list|)
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
name|NodeInfo
name|info
init|=
name|nodesInfo
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStats
name|stats
init|=
name|nodesStats
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|node
operator|.
name|id
argument_list|()
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
name|fullId
condition|?
name|node
operator|.
name|id
argument_list|()
else|:
name|Strings
operator|.
name|substring
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|info
operator|==
literal|null
condition|?
literal|null
else|:
name|info
operator|.
name|getProcess
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|node
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|node
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|address
argument_list|()
operator|instanceof
name|InetSocketTransportAddress
condition|)
block|{
name|table
operator|.
name|addCell
argument_list|(
operator|(
operator|(
name|InetSocketTransportAddress
operator|)
name|node
operator|.
name|address
argument_list|()
operator|)
operator|.
name|address
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|addCell
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ThreadPoolStats
operator|.
name|Stats
argument_list|>
name|poolThreadStats
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|poolThreadStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|poolThreadStats
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ThreadPoolStats
operator|.
name|Stats
argument_list|>
argument_list|(
literal|14
argument_list|)
expr_stmt|;
name|ThreadPoolStats
name|threadPoolStats
init|=
name|stats
operator|.
name|getThreadPool
argument_list|()
decl_stmt|;
for|for
control|(
name|ThreadPoolStats
operator|.
name|Stats
name|threadPoolStat
range|:
name|threadPoolStats
control|)
block|{
name|poolThreadStats
operator|.
name|put
argument_list|(
name|threadPoolStat
operator|.
name|getName
argument_list|()
argument_list|,
name|threadPoolStat
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|pool
range|:
name|SUPPORTED_NAMES
control|)
block|{
name|ThreadPoolStats
operator|.
name|Stats
name|poolStats
init|=
name|poolThreadStats
operator|.
name|get
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|poolStats
operator|==
literal|null
condition|?
literal|null
else|:
name|poolStats
operator|.
name|getActive
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|poolStats
operator|==
literal|null
condition|?
literal|null
else|:
name|poolStats
operator|.
name|getThreads
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|poolStats
operator|==
literal|null
condition|?
literal|null
else|:
name|poolStats
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|poolStats
operator|==
literal|null
condition|?
literal|null
else|:
name|poolStats
operator|.
name|getRejected
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|poolStats
operator|==
literal|null
condition|?
literal|null
else|:
name|poolStats
operator|.
name|getLargest
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|poolStats
operator|==
literal|null
condition|?
literal|null
else|:
name|poolStats
operator|.
name|getCompleted
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|// The thread pool columns should always be in the same order.
DECL|method|fetchSortedPools
specifier|private
name|String
index|[]
name|fetchSortedPools
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|String
index|[]
name|defaults
parameter_list|)
block|{
name|String
index|[]
name|headers
init|=
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"h"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|headers
operator|==
literal|null
condition|)
block|{
return|return
name|defaults
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|requestedPools
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|headers
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|header
range|:
name|headers
control|)
block|{
name|int
name|dotIndex
init|=
name|header
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|dotIndex
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|headerPrefix
init|=
name|header
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dotIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|THREAD_POOL_TO_ALIAS
operator|.
name|containsKey
argument_list|(
name|headerPrefix
argument_list|)
condition|)
block|{
name|requestedPools
operator|.
name|add
argument_list|(
name|headerPrefix
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ALIAS_TO_THREAD_POOL
operator|.
name|containsKey
argument_list|(
name|header
argument_list|)
condition|)
block|{
name|requestedPools
operator|.
name|add
argument_list|(
name|ALIAS_TO_THREAD_POOL
operator|.
name|get
argument_list|(
name|header
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|requestedPools
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|requestedPools
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

