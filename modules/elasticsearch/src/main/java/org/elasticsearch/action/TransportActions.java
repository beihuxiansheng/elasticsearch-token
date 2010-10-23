begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportActions
specifier|public
class|class
name|TransportActions
block|{
DECL|field|BULK
specifier|public
specifier|static
specifier|final
name|String
name|BULK
init|=
literal|"indices/bulk"
decl_stmt|;
DECL|field|INDEX
specifier|public
specifier|static
specifier|final
name|String
name|INDEX
init|=
literal|"indices/index/shard/index"
decl_stmt|;
DECL|field|COUNT
specifier|public
specifier|static
specifier|final
name|String
name|COUNT
init|=
literal|"indices/count"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"indices/index/shard/delete"
decl_stmt|;
DECL|field|DELETE_BY_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_BY_QUERY
init|=
literal|"indices/deleteByQuery"
decl_stmt|;
DECL|field|GET
specifier|public
specifier|static
specifier|final
name|String
name|GET
init|=
literal|"indices/get"
decl_stmt|;
DECL|field|SEARCH
specifier|public
specifier|static
specifier|final
name|String
name|SEARCH
init|=
literal|"indices/search"
decl_stmt|;
DECL|field|SEARCH_SCROLL
specifier|public
specifier|static
specifier|final
name|String
name|SEARCH_SCROLL
init|=
literal|"indices/searchScroll"
decl_stmt|;
DECL|field|TERMS
specifier|public
specifier|static
specifier|final
name|String
name|TERMS
init|=
literal|"indices/terms"
decl_stmt|;
DECL|field|MORE_LIKE_THIS
specifier|public
specifier|static
specifier|final
name|String
name|MORE_LIKE_THIS
init|=
literal|"indices/moreLikeThis"
decl_stmt|;
DECL|class|Admin
specifier|public
specifier|static
class|class
name|Admin
block|{
DECL|class|Indices
specifier|public
specifier|static
class|class
name|Indices
block|{
DECL|field|CREATE
specifier|public
specifier|static
specifier|final
name|String
name|CREATE
init|=
literal|"indices/createIndex"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"indices/deleteIndex"
decl_stmt|;
DECL|field|OPEN
specifier|public
specifier|static
specifier|final
name|String
name|OPEN
init|=
literal|"indices/openIndex"
decl_stmt|;
DECL|field|CLOSE
specifier|public
specifier|static
specifier|final
name|String
name|CLOSE
init|=
literal|"indices/closeIndex"
decl_stmt|;
DECL|field|FLUSH
specifier|public
specifier|static
specifier|final
name|String
name|FLUSH
init|=
literal|"indices/flush"
decl_stmt|;
DECL|field|REFRESH
specifier|public
specifier|static
specifier|final
name|String
name|REFRESH
init|=
literal|"indices/refresh"
decl_stmt|;
DECL|field|OPTIMIZE
specifier|public
specifier|static
specifier|final
name|String
name|OPTIMIZE
init|=
literal|"indices/optimize"
decl_stmt|;
DECL|field|STATUS
specifier|public
specifier|static
specifier|final
name|String
name|STATUS
init|=
literal|"indices/status"
decl_stmt|;
DECL|field|ALIASES
specifier|public
specifier|static
specifier|final
name|String
name|ALIASES
init|=
literal|"indices/aliases"
decl_stmt|;
DECL|field|UPDATE_SETTINGS
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_SETTINGS
init|=
literal|"indices/updateSettings"
decl_stmt|;
DECL|class|Gateway
specifier|public
specifier|static
class|class
name|Gateway
block|{
DECL|field|SNAPSHOT
specifier|public
specifier|static
specifier|final
name|String
name|SNAPSHOT
init|=
literal|"indices/gateway/snapshot"
decl_stmt|;
block|}
DECL|class|Mapping
specifier|public
specifier|static
class|class
name|Mapping
block|{
DECL|field|PUT
specifier|public
specifier|static
specifier|final
name|String
name|PUT
init|=
literal|"indices/mapping/put"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"indices/mapping/delete"
decl_stmt|;
block|}
DECL|class|Cache
specifier|public
specifier|static
class|class
name|Cache
block|{
DECL|field|CLEAR
specifier|public
specifier|static
specifier|final
name|String
name|CLEAR
init|=
literal|"indices/cache/clear"
decl_stmt|;
block|}
block|}
DECL|class|Cluster
specifier|public
specifier|static
class|class
name|Cluster
block|{
DECL|field|STATE
specifier|public
specifier|static
specifier|final
name|String
name|STATE
init|=
literal|"/cluster/state"
decl_stmt|;
DECL|field|HEALTH
specifier|public
specifier|static
specifier|final
name|String
name|HEALTH
init|=
literal|"/cluster/health"
decl_stmt|;
DECL|class|Node
specifier|public
specifier|static
class|class
name|Node
block|{
DECL|field|INFO
specifier|public
specifier|static
specifier|final
name|String
name|INFO
init|=
literal|"/cluster/nodes/info"
decl_stmt|;
DECL|field|STATS
specifier|public
specifier|static
specifier|final
name|String
name|STATS
init|=
literal|"/cluster/nodes/stats"
decl_stmt|;
DECL|field|SHUTDOWN
specifier|public
specifier|static
specifier|final
name|String
name|SHUTDOWN
init|=
literal|"/cluster/nodes/shutdown"
decl_stmt|;
DECL|field|RESTART
specifier|public
specifier|static
specifier|final
name|String
name|RESTART
init|=
literal|"/cluster/nodes/restart"
decl_stmt|;
block|}
DECL|class|Ping
specifier|public
specifier|static
class|class
name|Ping
block|{
DECL|field|SINGLE
specifier|public
specifier|static
specifier|final
name|String
name|SINGLE
init|=
literal|"/cluster/ping/single"
decl_stmt|;
DECL|field|REPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATION
init|=
literal|"/cluster/ping/replication"
decl_stmt|;
DECL|field|BROADCAST
specifier|public
specifier|static
specifier|final
name|String
name|BROADCAST
init|=
literal|"/cluster/ping/broadcast"
decl_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

