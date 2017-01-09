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
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|shards
operator|.
name|ClusterSearchShardsGroup
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
name|shards
operator|.
name|ClusterSearchShardsResponse
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
name|support
operator|.
name|PlainActionFuture
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
name|routing
operator|.
name|PlainShardIterator
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
name|ShardIterator
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
name|component
operator|.
name|AbstractComponent
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
name|Setting
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
name|TransportAddress
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
name|TimeValue
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
name|CountDown
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
name|shard
operator|.
name|ShardId
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
name|internal
operator|.
name|AliasFilter
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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportException
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
name|TransportService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ConcurrentHashMap
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
name|TimeoutException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Basic service for accessing remote clusters via gateway nodes  */
end_comment

begin_class
DECL|class|RemoteClusterService
specifier|public
specifier|final
class|class
name|RemoteClusterService
extends|extends
name|AbstractComponent
implements|implements
name|Closeable
block|{
comment|/**      * A list of initial seed nodes to discover eligible nodes from the remote cluster      */
comment|//TODO this should be an affix settings?
DECL|field|REMOTE_CLUSTERS_SEEDS
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Settings
argument_list|>
name|REMOTE_CLUSTERS_SEEDS
init|=
name|Setting
operator|.
name|groupSetting
argument_list|(
literal|"search.remote.seeds."
argument_list|,
name|RemoteClusterService
operator|::
name|validateRemoteClustersSeeds
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|)
decl_stmt|;
comment|/**      * The maximum number of connections that will be established to a remote cluster. For instance if there is only a single      * seed node, other nodes will be discovered up to the given number of nodes in this setting. The default is 3.      */
DECL|field|REMOTE_CONNECTIONS_PER_CLUSTER
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|REMOTE_CONNECTIONS_PER_CLUSTER
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"search.remote.connections_per_cluster"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|/**      * The initial connect timeout for remote cluster connections      */
DECL|field|REMOTE_INITIAL_CONNECTION_TIMEOUT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|REMOTE_INITIAL_CONNECTION_TIMEOUT_SETTING
init|=
name|Setting
operator|.
name|positiveTimeSetting
argument_list|(
literal|"search.remote.initial_connect_timeout"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|/**      * The name of a node attribute to select nodes that should be connected to in the remote cluster.      * For instance a node can be configured with<tt>node.node_attr.gateway: true</tt> in order to be eligible as a gateway node between      * clusters. In that case<tt>search.remote.node_attribute: gateway</tt> can be used to filter out other nodes in the remote cluster.      * The value of the setting is expected to be a boolean,<tt>true</tt> for nodes that can become gateways,<tt>false</tt> otherwise.      */
DECL|field|REMOTE_NODE_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|REMOTE_NODE_ATTRIBUTE
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"search.remote.node_attribute"
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|REMOTE_CLUSTER_INDEX_SEPARATOR
specifier|private
specifier|static
specifier|final
name|char
name|REMOTE_CLUSTER_INDEX_SEPARATOR
init|=
literal|':'
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|numRemoteConnections
specifier|private
specifier|final
name|int
name|numRemoteConnections
decl_stmt|;
DECL|field|remoteClusters
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|RemoteClusterConnection
argument_list|>
name|remoteClusters
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|method|RemoteClusterService
name|RemoteClusterService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|numRemoteConnections
operator|=
name|REMOTE_CONNECTIONS_PER_CLUSTER
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method updates the list of remote clusters. It's intended to be used as an update consumer on the settings infrastructure      * @param seedSettings the group settings returned from {@link #REMOTE_CLUSTERS_SEEDS}      * @param connectionListener a listener invoked once every configured cluster has been connected to      */
DECL|method|updateRemoteClusters
name|void
name|updateRemoteClusters
parameter_list|(
name|Settings
name|seedSettings
parameter_list|,
name|ActionListener
argument_list|<
name|Void
argument_list|>
name|connectionListener
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|RemoteClusterConnection
argument_list|>
name|remoteClusters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
argument_list|>
name|seeds
init|=
name|buildRemoteClustersSeeds
argument_list|(
name|seedSettings
argument_list|)
decl_stmt|;
if|if
condition|(
name|seeds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|connectionListener
operator|.
name|onResponse
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CountDown
name|countDown
init|=
operator|new
name|CountDown
argument_list|(
name|seeds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodePredicate
init|=
parameter_list|(
name|node
parameter_list|)
lambda|->
name|Version
operator|.
name|CURRENT
operator|.
name|isCompatible
argument_list|(
name|node
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|REMOTE_NODE_ATTRIBUTE
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
comment|// nodes can be tagged with node.attr.remote_gateway: true to allow a node to be a gateway node for
comment|// cross cluster search
name|String
name|attribute
init|=
name|REMOTE_NODE_ATTRIBUTE
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|nodePredicate
operator|=
name|nodePredicate
operator|.
name|and
argument_list|(
parameter_list|(
name|node
parameter_list|)
lambda|->
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|getOrDefault
argument_list|(
name|attribute
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
argument_list|>
name|entry
range|:
name|seeds
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|RemoteClusterConnection
name|remote
init|=
name|this
operator|.
name|remoteClusters
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|remote
operator|==
literal|null
condition|)
block|{
name|remote
operator|=
operator|new
name|RemoteClusterConnection
argument_list|(
name|settings
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|transportService
argument_list|,
name|numRemoteConnections
argument_list|,
name|nodePredicate
argument_list|)
expr_stmt|;
name|remoteClusters
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|remote
argument_list|)
expr_stmt|;
block|}
name|remote
operator|.
name|updateSeedNodes
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|ActionListener
operator|.
name|wrap
argument_list|(
name|response
lambda|->
block|{
if|if
condition|(
name|countDown
operator|.
name|countDown
argument_list|()
condition|)
block|{
name|connectionListener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|exception
lambda|->
block|{
if|if
condition|(
name|countDown
operator|.
name|fastForward
argument_list|()
condition|)
block|{
name|connectionListener
operator|.
name|onFailure
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|error
argument_list|(
literal|"failed to update seed list for cluster: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|remoteClusters
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|remoteClusters
operator|.
name|putAll
argument_list|(
name|this
operator|.
name|remoteClusters
argument_list|)
expr_stmt|;
name|this
operator|.
name|remoteClusters
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|remoteClusters
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns<code>true</code> if at least one remote cluster is configured      */
DECL|method|isCrossClusterSearchEnabled
name|boolean
name|isCrossClusterSearchEnabled
parameter_list|()
block|{
return|return
name|remoteClusters
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
return|;
block|}
comment|/**      * Filters out indices that refer to a remote cluster and adds them to the given per cluster indices map.      *      * @param perClusterIndices a map to fill with remote cluster indices from the given request indices      * @param requestIndices the indices in the search request to filter      * @return all indices in the requestIndices array that are not remote cluster indices      */
DECL|method|filterIndices
specifier|public
name|String
index|[]
name|filterIndices
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|perClusterIndices
parameter_list|,
name|String
index|[]
name|requestIndices
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|localIndicesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|requestIndices
control|)
block|{
name|int
name|i
init|=
name|index
operator|.
name|indexOf
argument_list|(
name|REMOTE_CLUSTER_INDEX_SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
name|String
name|remoteCluster
init|=
name|index
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|isRemoteClusterRegistered
argument_list|(
name|remoteCluster
argument_list|)
condition|)
block|{
name|String
name|remoteIndex
init|=
name|index
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|perClusterIndices
operator|.
name|get
argument_list|(
name|remoteCluster
argument_list|)
decl_stmt|;
if|if
condition|(
name|indices
operator|==
literal|null
condition|)
block|{
name|indices
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|perClusterIndices
operator|.
name|put
argument_list|(
name|remoteCluster
argument_list|,
name|indices
argument_list|)
expr_stmt|;
block|}
name|indices
operator|.
name|add
argument_list|(
name|remoteIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|localIndicesList
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|localIndicesList
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|localIndicesList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|localIndicesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Returns<code>true</code> iff the given cluster is configured as a remote cluster. Otherwise<code>false</code>      */
DECL|method|isRemoteClusterRegistered
name|boolean
name|isRemoteClusterRegistered
parameter_list|(
name|String
name|clusterName
parameter_list|)
block|{
return|return
name|remoteClusters
operator|.
name|containsKey
argument_list|(
name|clusterName
argument_list|)
return|;
block|}
DECL|method|collectSearchShards
name|void
name|collectSearchShards
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|remoteIndicesByCluster
parameter_list|,
name|ActionListener
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterSearchShardsResponse
argument_list|>
argument_list|>
name|listener
parameter_list|)
block|{
specifier|final
name|CountDown
name|responsesCountDown
init|=
operator|new
name|CountDown
argument_list|(
name|remoteIndicesByCluster
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterSearchShardsResponse
argument_list|>
name|searchShardsResponses
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|TransportException
argument_list|>
name|transportException
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|remoteIndicesByCluster
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|clusterName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|RemoteClusterConnection
name|remoteClusterConnection
init|=
name|remoteClusters
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteClusterConnection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no such remote cluster: "
operator|+
name|clusterName
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|remoteClusterConnection
operator|.
name|fetchSearchShards
argument_list|(
name|searchRequest
argument_list|,
name|indices
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ClusterSearchShardsResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClusterSearchShardsResponse
name|clusterSearchShardsResponse
parameter_list|)
block|{
name|searchShardsResponses
operator|.
name|put
argument_list|(
name|clusterName
argument_list|,
name|clusterSearchShardsResponse
argument_list|)
expr_stmt|;
if|if
condition|(
name|responsesCountDown
operator|.
name|countDown
argument_list|()
condition|)
block|{
name|TransportException
name|exception
init|=
name|transportException
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|searchShardsResponses
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|transportException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|TransportException
name|exception
init|=
operator|new
name|TransportException
argument_list|(
literal|"unable to communicate with remote cluster ["
operator|+
name|clusterName
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|transportException
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|exception
argument_list|)
operator|==
literal|false
condition|)
block|{
name|exception
operator|=
name|transportException
operator|.
name|accumulateAndGet
argument_list|(
name|exception
argument_list|,
parameter_list|(
name|previous
parameter_list|,
name|current
parameter_list|)
lambda|->
block|{
name|current
operator|.
name|addSuppressed
argument_list|(
name|previous
argument_list|)
expr_stmt|;
return|return
name|current
return|;
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|responsesCountDown
operator|.
name|countDown
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processRemoteShards
name|Function
argument_list|<
name|String
argument_list|,
name|Transport
operator|.
name|Connection
argument_list|>
name|processRemoteShards
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterSearchShardsResponse
argument_list|>
name|searchShardsResponses
parameter_list|,
name|List
argument_list|<
name|ShardIterator
argument_list|>
name|remoteShardIterators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|aliasFilterMap
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|Transport
operator|.
name|Connection
argument_list|>
argument_list|>
name|nodeToCluster
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ClusterSearchShardsResponse
argument_list|>
name|entry
range|:
name|searchShardsResponses
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|clusterName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ClusterSearchShardsResponse
name|searchShardsResponse
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|remoteNode
range|:
name|searchShardsResponse
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|nodeToCluster
operator|.
name|put
argument_list|(
name|remoteNode
operator|.
name|getId
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|getConnection
argument_list|(
name|remoteNode
argument_list|,
name|clusterName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|indicesAndFilters
init|=
name|searchShardsResponse
operator|.
name|getIndicesAndFilters
argument_list|()
decl_stmt|;
for|for
control|(
name|ClusterSearchShardsGroup
name|clusterSearchShardsGroup
range|:
name|searchShardsResponse
operator|.
name|getGroups
argument_list|()
control|)
block|{
comment|//add the cluster name to the remote index names for indices disambiguation
comment|//this ends up in the hits returned with the search response
name|ShardId
name|shardId
init|=
name|clusterSearchShardsGroup
operator|.
name|getShardId
argument_list|()
decl_stmt|;
name|Index
name|remoteIndex
init|=
name|shardId
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
name|clusterName
operator|+
name|REMOTE_CLUSTER_INDEX_SEPARATOR
operator|+
name|remoteIndex
operator|.
name|getName
argument_list|()
argument_list|,
name|remoteIndex
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
name|ShardIterator
name|shardIterator
init|=
operator|new
name|PlainShardIterator
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|clusterSearchShardsGroup
operator|.
name|getShards
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|remoteShardIterators
operator|.
name|add
argument_list|(
name|shardIterator
argument_list|)
expr_stmt|;
name|AliasFilter
name|aliasFilter
decl_stmt|;
if|if
condition|(
name|indicesAndFilters
operator|==
literal|null
condition|)
block|{
name|aliasFilter
operator|=
operator|new
name|AliasFilter
argument_list|(
literal|null
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|aliasFilter
operator|=
name|indicesAndFilters
operator|.
name|get
argument_list|(
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|aliasFilter
operator|!=
literal|null
assert|;
block|}
comment|// here we have to map the filters to the UUID since from now on we use the uuid for the lookup
name|aliasFilterMap
operator|.
name|put
argument_list|(
name|remoteIndex
operator|.
name|getUUID
argument_list|()
argument_list|,
name|aliasFilter
argument_list|)
expr_stmt|;
block|}
block|}
return|return
parameter_list|(
name|nodeId
parameter_list|)
lambda|->
block|{
name|Supplier
argument_list|<
name|Transport
operator|.
name|Connection
argument_list|>
name|supplier
init|=
name|nodeToCluster
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|supplier
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown remote node: "
operator|+
name|nodeId
argument_list|)
throw|;
block|}
return|return
name|supplier
operator|.
name|get
argument_list|()
return|;
block|}
return|;
block|}
comment|/**      * Returns a connection to the given node on the given remote cluster      * @throws IllegalArgumentException if the remote cluster is unknown      */
DECL|method|getConnection
specifier|private
name|Transport
operator|.
name|Connection
name|getConnection
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|String
name|cluster
parameter_list|)
block|{
name|RemoteClusterConnection
name|connection
init|=
name|remoteClusters
operator|.
name|get
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no such remote cluster: "
operator|+
name|cluster
argument_list|)
throw|;
block|}
return|return
name|connection
operator|.
name|getConnection
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|buildRemoteClustersSeeds
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
argument_list|>
name|buildRemoteClustersSeeds
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
argument_list|>
name|remoteClustersNodes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|clusterName
range|:
name|settings
operator|.
name|names
argument_list|()
control|)
block|{
name|String
index|[]
name|remoteHosts
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|remoteHost
range|:
name|remoteHosts
control|)
block|{
name|int
name|portSeparator
init|=
name|remoteHost
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
comment|// in case we have a IPv6 address ie. [::1]:9300
name|String
name|host
init|=
name|remoteHost
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|portSeparator
argument_list|)
decl_stmt|;
name|InetAddress
name|hostAddress
decl_stmt|;
try|try
block|{
name|hostAddress
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown host ["
operator|+
name|host
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|int
name|port
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|remoteHost
operator|.
name|substring
argument_list|(
name|portSeparator
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
name|clusterName
operator|+
literal|"#"
operator|+
name|remoteHost
argument_list|,
operator|new
name|TransportAddress
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|hostAddress
argument_list|,
name|port
argument_list|)
argument_list|)
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|remoteClustersNodes
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
name|nodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|remoteClustersNodes
operator|.
name|put
argument_list|(
name|clusterName
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|remoteClustersNodes
return|;
block|}
DECL|method|validateRemoteClustersSeeds
specifier|static
name|void
name|validateRemoteClustersSeeds
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
for|for
control|(
name|String
name|clusterName
range|:
name|settings
operator|.
name|names
argument_list|()
control|)
block|{
name|String
index|[]
name|remoteHosts
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteHosts
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no hosts set for remote cluster ["
operator|+
name|clusterName
operator|+
literal|"], at least one host is required"
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|remoteHost
range|:
name|remoteHosts
control|)
block|{
name|int
name|portSeparator
init|=
name|remoteHost
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
comment|// in case we have a IPv6 address ie. [::1]:9300
if|if
condition|(
name|portSeparator
operator|==
operator|-
literal|1
operator|||
name|portSeparator
operator|==
name|remoteHost
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"remote hosts need to be configured as [host:port], found ["
operator|+
name|remoteHost
operator|+
literal|"] "
operator|+
literal|"instead for remote cluster ["
operator|+
name|clusterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|String
name|host
init|=
name|remoteHost
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|portSeparator
argument_list|)
decl_stmt|;
try|try
block|{
name|InetAddress
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown host ["
operator|+
name|host
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|port
init|=
name|remoteHost
operator|.
name|substring
argument_list|(
name|portSeparator
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|Integer
name|portValue
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
decl_stmt|;
if|if
condition|(
name|portValue
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"port number must be> 0 but was: ["
operator|+
name|portValue
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"port must be a number, found ["
operator|+
name|port
operator|+
literal|"] instead for remote cluster ["
operator|+
name|clusterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**      * Connects to all remote clusters in a blocking fashion. This should be called on node startup to establish an initial connection      * to all configured seed nodes.      */
DECL|method|initializeRemoteClusters
name|void
name|initializeRemoteClusters
parameter_list|()
block|{
specifier|final
name|TimeValue
name|timeValue
init|=
name|REMOTE_INITIAL_CONNECTION_TIMEOUT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|PlainActionFuture
argument_list|<
name|Void
argument_list|>
name|future
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|updateRemoteClusters
argument_list|(
name|REMOTE_CLUSTERS_SEEDS
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|future
argument_list|)
expr_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|(
name|timeValue
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to connect to remote clusters within {}"
argument_list|,
name|timeValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to connect to remote clusters"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|remoteClusters
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

