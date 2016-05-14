begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.health
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|health
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
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
name|ClusterState
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
name|metadata
operator|.
name|IndexMetaData
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
name|metadata
operator|.
name|MetaData
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
name|IndexRoutingTable
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
name|RoutingTable
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|RestStatus
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
name|Iterator
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterIndexHealth
operator|.
name|readClusterIndexHealth
import|;
end_import

begin_class
DECL|class|ClusterStateHealth
specifier|public
specifier|final
class|class
name|ClusterStateHealth
implements|implements
name|Iterable
argument_list|<
name|ClusterIndexHealth
argument_list|>
implements|,
name|Streamable
block|{
DECL|field|numberOfNodes
specifier|private
name|int
name|numberOfNodes
init|=
literal|0
decl_stmt|;
DECL|field|numberOfDataNodes
specifier|private
name|int
name|numberOfDataNodes
init|=
literal|0
decl_stmt|;
DECL|field|activeShards
specifier|private
name|int
name|activeShards
init|=
literal|0
decl_stmt|;
DECL|field|relocatingShards
specifier|private
name|int
name|relocatingShards
init|=
literal|0
decl_stmt|;
DECL|field|activePrimaryShards
specifier|private
name|int
name|activePrimaryShards
init|=
literal|0
decl_stmt|;
DECL|field|initializingShards
specifier|private
name|int
name|initializingShards
init|=
literal|0
decl_stmt|;
DECL|field|unassignedShards
specifier|private
name|int
name|unassignedShards
init|=
literal|0
decl_stmt|;
DECL|field|activeShardsPercent
specifier|private
name|double
name|activeShardsPercent
init|=
literal|100
decl_stmt|;
DECL|field|status
specifier|private
name|ClusterHealthStatus
name|status
init|=
name|ClusterHealthStatus
operator|.
name|RED
decl_stmt|;
DECL|field|indices
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterIndexHealth
argument_list|>
name|indices
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|readClusterHealth
specifier|public
specifier|static
name|ClusterStateHealth
name|readClusterHealth
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterStateHealth
name|clusterStateHealth
init|=
operator|new
name|ClusterStateHealth
argument_list|()
decl_stmt|;
name|clusterStateHealth
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|clusterStateHealth
return|;
block|}
DECL|method|ClusterStateHealth
name|ClusterStateHealth
parameter_list|()
block|{
comment|// only intended for serialization
block|}
comment|/**      * Creates a new<code>ClusterStateHealth</code> instance based on cluster meta data and its routing table as a convenience.      *      * @param clusterMetaData Current cluster meta data. Must not be null.      * @param routingTables   Current routing table. Must not be null.      */
DECL|method|ClusterStateHealth
specifier|public
name|ClusterStateHealth
parameter_list|(
name|MetaData
name|clusterMetaData
parameter_list|,
name|RoutingTable
name|routingTables
parameter_list|)
block|{
name|this
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|clusterMetaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTables
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new<code>ClusterStateHealth</code> instance considering the current cluster state and all indices in the cluster.      *      * @param clusterState The current cluster state. Must not be null.      */
DECL|method|ClusterStateHealth
specifier|public
name|ClusterStateHealth
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|this
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|getConcreteAllIndices
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new<code>ClusterStateHealth</code> instance considering the current cluster state and the provided index names.      *      * @param clusterState    The current cluster state. Must not be null.      * @param concreteIndices An array of index names to consider. Must not be null but may be empty.      */
DECL|method|ClusterStateHealth
specifier|public
name|ClusterStateHealth
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|)
block|{
name|numberOfNodes
operator|=
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|numberOfDataNodes
operator|=
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexRoutingTable
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ClusterIndexHealth
name|indexHealth
init|=
operator|new
name|ClusterIndexHealth
argument_list|(
name|indexMetaData
argument_list|,
name|indexRoutingTable
argument_list|)
decl_stmt|;
name|indices
operator|.
name|put
argument_list|(
name|indexHealth
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexHealth
argument_list|)
expr_stmt|;
block|}
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|GREEN
expr_stmt|;
for|for
control|(
name|ClusterIndexHealth
name|indexHealth
range|:
name|indices
operator|.
name|values
argument_list|()
control|)
block|{
name|activePrimaryShards
operator|+=
name|indexHealth
operator|.
name|getActivePrimaryShards
argument_list|()
expr_stmt|;
name|activeShards
operator|+=
name|indexHealth
operator|.
name|getActiveShards
argument_list|()
expr_stmt|;
name|relocatingShards
operator|+=
name|indexHealth
operator|.
name|getRelocatingShards
argument_list|()
expr_stmt|;
name|initializingShards
operator|+=
name|indexHealth
operator|.
name|getInitializingShards
argument_list|()
expr_stmt|;
name|unassignedShards
operator|+=
name|indexHealth
operator|.
name|getUnassignedShards
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexHealth
operator|.
name|getStatus
argument_list|()
operator|==
name|ClusterHealthStatus
operator|.
name|RED
condition|)
block|{
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexHealth
operator|.
name|getStatus
argument_list|()
operator|==
name|ClusterHealthStatus
operator|.
name|YELLOW
operator|&&
name|status
operator|!=
name|ClusterHealthStatus
operator|.
name|RED
condition|)
block|{
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|YELLOW
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clusterState
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
condition|)
block|{
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
block|}
comment|// shortcut on green
if|if
condition|(
name|status
operator|.
name|equals
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
condition|)
block|{
name|this
operator|.
name|activeShardsPercent
operator|=
literal|100
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shardRoutings
init|=
name|clusterState
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|allShards
argument_list|()
decl_stmt|;
name|int
name|activeShardCount
init|=
literal|0
decl_stmt|;
name|int
name|totalShardCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|shardRoutings
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|active
argument_list|()
condition|)
name|activeShardCount
operator|++
expr_stmt|;
name|totalShardCount
operator|++
expr_stmt|;
block|}
name|this
operator|.
name|activeShardsPercent
operator|=
operator|(
operator|(
operator|(
name|double
operator|)
name|activeShardCount
operator|)
operator|/
name|totalShardCount
operator|)
operator|*
literal|100
expr_stmt|;
block|}
block|}
DECL|method|getActiveShards
specifier|public
name|int
name|getActiveShards
parameter_list|()
block|{
return|return
name|activeShards
return|;
block|}
DECL|method|getRelocatingShards
specifier|public
name|int
name|getRelocatingShards
parameter_list|()
block|{
return|return
name|relocatingShards
return|;
block|}
DECL|method|getActivePrimaryShards
specifier|public
name|int
name|getActivePrimaryShards
parameter_list|()
block|{
return|return
name|activePrimaryShards
return|;
block|}
DECL|method|getInitializingShards
specifier|public
name|int
name|getInitializingShards
parameter_list|()
block|{
return|return
name|initializingShards
return|;
block|}
DECL|method|getUnassignedShards
specifier|public
name|int
name|getUnassignedShards
parameter_list|()
block|{
return|return
name|unassignedShards
return|;
block|}
DECL|method|getNumberOfNodes
specifier|public
name|int
name|getNumberOfNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|numberOfNodes
return|;
block|}
DECL|method|getNumberOfDataNodes
specifier|public
name|int
name|getNumberOfDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|numberOfDataNodes
return|;
block|}
DECL|method|getStatus
specifier|public
name|ClusterHealthStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterIndexHealth
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|getActiveShardsPercent
specifier|public
name|double
name|getActiveShardsPercent
parameter_list|()
block|{
return|return
name|activeShardsPercent
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|ClusterIndexHealth
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|indices
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|activePrimaryShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|activeShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|relocatingShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|initializingShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|unassignedShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|numberOfNodes
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|numberOfDataNodes
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ClusterIndexHealth
name|indexHealth
init|=
name|readClusterIndexHealth
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|indices
operator|.
name|put
argument_list|(
name|indexHealth
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexHealth
argument_list|)
expr_stmt|;
block|}
name|activeShardsPercent
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|activePrimaryShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|activeShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|relocatingShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|initializingShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|unassignedShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numberOfNodes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numberOfDataNodes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|status
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|indices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterIndexHealth
name|indexHealth
range|:
name|this
control|)
block|{
name|indexHealth
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeDouble
argument_list|(
name|activeShardsPercent
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

