begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
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
name|ClusterChangedEvent
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
name|service
operator|.
name|ClusterApplier
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
name|service
operator|.
name|MasterService
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
name|discovery
operator|.
name|single
operator|.
name|SingleNodeDiscovery
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
operator|.
name|TribeService
operator|.
name|BLOCKS_METADATA_SETTING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
operator|.
name|TribeService
operator|.
name|BLOCKS_WRITE_SETTING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
operator|.
name|TribeService
operator|.
name|TRIBE_METADATA_BLOCK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
operator|.
name|TribeService
operator|.
name|TRIBE_WRITE_BLOCK
import|;
end_import

begin_comment
comment|/**  * A {@link Discovery} implementation that is used by {@link org.elasticsearch.tribe.TribeService}. This implementation  * doesn't support any clustering features. Most notably {@link #startInitialJoin()} does nothing and  * {@link #publish(ClusterChangedEvent, AckListener)} delegates state updates directly to the  * {@link org.elasticsearch.cluster.service.ClusterApplierService}.  */
end_comment

begin_class
DECL|class|TribeDiscovery
specifier|public
class|class
name|TribeDiscovery
extends|extends
name|SingleNodeDiscovery
implements|implements
name|Discovery
block|{
annotation|@
name|Inject
DECL|method|TribeDiscovery
specifier|public
name|TribeDiscovery
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|MasterService
name|masterService
parameter_list|,
name|ClusterApplier
name|clusterApplier
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|masterService
argument_list|,
name|clusterApplier
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInitialState
specifier|protected
name|ClusterState
name|createInitialState
parameter_list|(
name|DiscoveryNode
name|localNode
parameter_list|)
block|{
name|ClusterBlocks
operator|.
name|Builder
name|clusterBlocks
init|=
name|ClusterBlocks
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// don't add no_master / state recovery block
if|if
condition|(
name|BLOCKS_WRITE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|clusterBlocks
operator|.
name|addGlobalBlock
argument_list|(
name|TRIBE_WRITE_BLOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|BLOCKS_METADATA_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|clusterBlocks
operator|.
name|addGlobalBlock
argument_list|(
name|TRIBE_METADATA_BLOCK
argument_list|)
expr_stmt|;
block|}
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|localNode
argument_list|)
operator|.
name|localNodeId
argument_list|(
name|localNode
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|blocks
argument_list|(
name|clusterBlocks
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|startInitialJoin
specifier|public
specifier|synchronized
name|void
name|startInitialJoin
parameter_list|()
block|{
comment|// no state recovery required as tribe nodes don't persist cluster state
block|}
block|}
end_class

end_unit

