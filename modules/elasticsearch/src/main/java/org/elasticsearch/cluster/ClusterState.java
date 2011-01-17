begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
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
name|block
operator|.
name|ClusterBlocks
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
name|cluster
operator|.
name|routing
operator|.
name|RoutingNodes
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
name|allocation
operator|.
name|AllocationExplanation
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
name|allocation
operator|.
name|RoutingAllocation
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
name|io
operator|.
name|stream
operator|.
name|*
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
DECL|class|ClusterState
specifier|public
class|class
name|ClusterState
block|{
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|routingTable
specifier|private
specifier|final
name|RoutingTable
name|routingTable
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|DiscoveryNodes
name|nodes
decl_stmt|;
DECL|field|metaData
specifier|private
specifier|final
name|MetaData
name|metaData
decl_stmt|;
DECL|field|blocks
specifier|private
specifier|final
name|ClusterBlocks
name|blocks
decl_stmt|;
DECL|field|allocationExplanation
specifier|private
specifier|final
name|AllocationExplanation
name|allocationExplanation
decl_stmt|;
comment|// built on demand
DECL|field|routingNodes
specifier|private
specifier|volatile
name|RoutingNodes
name|routingNodes
decl_stmt|;
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|long
name|version
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
argument_list|,
name|state
operator|.
name|routingTable
argument_list|()
argument_list|,
name|state
operator|.
name|nodes
argument_list|()
argument_list|,
name|state
operator|.
name|blocks
argument_list|()
argument_list|,
name|state
operator|.
name|allocationExplanation
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|long
name|version
parameter_list|,
name|MetaData
name|metaData
parameter_list|,
name|RoutingTable
name|routingTable
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|,
name|ClusterBlocks
name|blocks
parameter_list|,
name|AllocationExplanation
name|allocationExplanation
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
name|this
operator|.
name|routingTable
operator|=
name|routingTable
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|allocationExplanation
operator|=
name|allocationExplanation
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
argument_list|()
return|;
block|}
DECL|method|nodes
specifier|public
name|DiscoveryNodes
name|nodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodes
return|;
block|}
DECL|method|getNodes
specifier|public
name|DiscoveryNodes
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
argument_list|()
return|;
block|}
DECL|method|metaData
specifier|public
name|MetaData
name|metaData
parameter_list|()
block|{
return|return
name|this
operator|.
name|metaData
return|;
block|}
DECL|method|getMetaData
specifier|public
name|MetaData
name|getMetaData
parameter_list|()
block|{
return|return
name|metaData
argument_list|()
return|;
block|}
DECL|method|routingTable
specifier|public
name|RoutingTable
name|routingTable
parameter_list|()
block|{
return|return
name|routingTable
return|;
block|}
DECL|method|getRoutingTable
specifier|public
name|RoutingTable
name|getRoutingTable
parameter_list|()
block|{
return|return
name|routingTable
argument_list|()
return|;
block|}
DECL|method|routingNodes
specifier|public
name|RoutingNodes
name|routingNodes
parameter_list|()
block|{
return|return
name|routingTable
operator|.
name|routingNodes
argument_list|(
name|metaData
argument_list|,
name|blocks
argument_list|)
return|;
block|}
DECL|method|getRoutingNodes
specifier|public
name|RoutingNodes
name|getRoutingNodes
parameter_list|()
block|{
return|return
name|readOnlyRoutingNodes
argument_list|()
return|;
block|}
DECL|method|blocks
specifier|public
name|ClusterBlocks
name|blocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|blocks
return|;
block|}
DECL|method|getBlocks
specifier|public
name|ClusterBlocks
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
DECL|method|allocationExplanation
specifier|public
name|AllocationExplanation
name|allocationExplanation
parameter_list|()
block|{
return|return
name|this
operator|.
name|allocationExplanation
return|;
block|}
DECL|method|getAllocationExplanation
specifier|public
name|AllocationExplanation
name|getAllocationExplanation
parameter_list|()
block|{
return|return
name|allocationExplanation
argument_list|()
return|;
block|}
comment|/**      * Returns a built (on demand) routing nodes view of the routing table.<b>NOTE, the routing nodes      * are mutable, use them just for read operations</b>      */
DECL|method|readOnlyRoutingNodes
specifier|public
name|RoutingNodes
name|readOnlyRoutingNodes
parameter_list|()
block|{
if|if
condition|(
name|routingNodes
operator|!=
literal|null
condition|)
block|{
return|return
name|routingNodes
return|;
block|}
name|routingNodes
operator|=
name|routingTable
operator|.
name|routingNodes
argument_list|(
name|metaData
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
return|return
name|routingNodes
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|newClusterStateBuilder
specifier|public
specifier|static
name|Builder
name|newClusterStateBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|version
specifier|private
name|long
name|version
init|=
literal|0
decl_stmt|;
DECL|field|metaData
specifier|private
name|MetaData
name|metaData
init|=
name|MetaData
operator|.
name|EMPTY_META_DATA
decl_stmt|;
DECL|field|routingTable
specifier|private
name|RoutingTable
name|routingTable
init|=
name|RoutingTable
operator|.
name|EMPTY_ROUTING_TABLE
decl_stmt|;
DECL|field|nodes
specifier|private
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|EMPTY_NODES
decl_stmt|;
DECL|field|blocks
specifier|private
name|ClusterBlocks
name|blocks
init|=
name|ClusterBlocks
operator|.
name|EMPTY_CLUSTER_BLOCK
decl_stmt|;
DECL|field|allocationExplanation
specifier|private
name|AllocationExplanation
name|allocationExplanation
init|=
name|AllocationExplanation
operator|.
name|EMPTY
decl_stmt|;
DECL|method|nodes
specifier|public
name|Builder
name|nodes
parameter_list|(
name|DiscoveryNodes
operator|.
name|Builder
name|nodesBuilder
parameter_list|)
block|{
return|return
name|nodes
argument_list|(
name|nodesBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodes
specifier|public
name|Builder
name|nodes
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|routingTable
specifier|public
name|Builder
name|routingTable
parameter_list|(
name|RoutingTable
operator|.
name|Builder
name|routingTable
parameter_list|)
block|{
return|return
name|routingTable
argument_list|(
name|routingTable
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|routingResult
specifier|public
name|Builder
name|routingResult
parameter_list|(
name|RoutingAllocation
operator|.
name|Result
name|routingResult
parameter_list|)
block|{
name|this
operator|.
name|routingTable
operator|=
name|routingResult
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocationExplanation
operator|=
name|routingResult
operator|.
name|explanation
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|routingTable
specifier|public
name|Builder
name|routingTable
parameter_list|(
name|RoutingTable
name|routingTable
parameter_list|)
block|{
name|this
operator|.
name|routingTable
operator|=
name|routingTable
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|metaData
specifier|public
name|Builder
name|metaData
parameter_list|(
name|MetaData
operator|.
name|Builder
name|metaDataBuilder
parameter_list|)
block|{
return|return
name|metaData
argument_list|(
name|metaDataBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|metaData
specifier|public
name|Builder
name|metaData
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
block|{
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|blocks
specifier|public
name|Builder
name|blocks
parameter_list|(
name|ClusterBlocks
operator|.
name|Builder
name|blocksBuilder
parameter_list|)
block|{
return|return
name|blocks
argument_list|(
name|blocksBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|blocks
specifier|public
name|Builder
name|blocks
parameter_list|(
name|ClusterBlocks
name|block
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
name|block
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|allocationExplanation
specifier|public
name|Builder
name|allocationExplanation
parameter_list|(
name|AllocationExplanation
name|allocationExplanation
parameter_list|)
block|{
name|this
operator|.
name|allocationExplanation
operator|=
name|allocationExplanation
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|version
specifier|public
name|Builder
name|version
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|state
specifier|public
name|Builder
name|state
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|state
operator|.
name|version
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|state
operator|.
name|nodes
argument_list|()
expr_stmt|;
name|this
operator|.
name|routingTable
operator|=
name|state
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|this
operator|.
name|metaData
operator|=
name|state
operator|.
name|metaData
argument_list|()
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|state
operator|.
name|blocks
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocationExplanation
operator|=
name|state
operator|.
name|allocationExplanation
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|ClusterState
name|build
parameter_list|()
block|{
return|return
operator|new
name|ClusterState
argument_list|(
name|version
argument_list|,
name|metaData
argument_list|,
name|routingTable
argument_list|,
name|nodes
argument_list|,
name|blocks
argument_list|,
name|allocationExplanation
argument_list|)
return|;
block|}
DECL|method|toBytes
specifier|public
specifier|static
name|byte
index|[]
name|toBytes
parameter_list|(
name|ClusterState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesStreamOutput
name|os
init|=
name|CachedStreamOutput
operator|.
name|cachedBytes
argument_list|()
decl_stmt|;
name|writeTo
argument_list|(
name|state
argument_list|,
name|os
argument_list|)
expr_stmt|;
return|return
name|os
operator|.
name|copiedByteArray
argument_list|()
return|;
block|}
DECL|method|fromBytes
specifier|public
specifier|static
name|ClusterState
name|fromBytes
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|DiscoveryNode
name|localNode
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFrom
argument_list|(
operator|new
name|BytesStreamInput
argument_list|(
name|data
argument_list|)
argument_list|,
name|localNode
argument_list|)
return|;
block|}
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|MetaData
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|RoutingTable
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|state
operator|.
name|routingTable
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|ClusterBlocks
operator|.
name|Builder
operator|.
name|writeClusterBlocks
argument_list|(
name|state
operator|.
name|blocks
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|state
operator|.
name|allocationExplanation
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|ClusterState
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|,
annotation|@
name|Nullable
name|DiscoveryNode
name|localNode
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|version
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|builder
operator|.
name|metaData
operator|=
name|MetaData
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|builder
operator|.
name|routingTable
operator|=
name|RoutingTable
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|builder
operator|.
name|nodes
operator|=
name|DiscoveryNodes
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|,
name|localNode
argument_list|)
expr_stmt|;
name|builder
operator|.
name|blocks
operator|=
name|ClusterBlocks
operator|.
name|Builder
operator|.
name|readClusterBlocks
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|builder
operator|.
name|allocationExplanation
operator|=
name|AllocationExplanation
operator|.
name|readAllocationExplanation
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

