begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|AbstractModule
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
name|multibindings
operator|.
name|Multibinder
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ShardAllocationModule
specifier|public
class|class
name|ShardAllocationModule
extends|extends
name|AbstractModule
block|{
DECL|field|allocations
specifier|private
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|NodeAllocation
argument_list|>
argument_list|>
name|allocations
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|ShardAllocationModule
specifier|public
name|ShardAllocationModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{     }
DECL|method|addNodeAllocation
specifier|public
name|void
name|addNodeAllocation
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|NodeAllocation
argument_list|>
name|nodeAllocation
parameter_list|)
block|{
name|allocations
operator|.
name|add
argument_list|(
name|nodeAllocation
argument_list|)
expr_stmt|;
block|}
DECL|method|configure
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ShardsAllocation
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|Multibinder
argument_list|<
name|NodeAllocation
argument_list|>
name|allocationMultibinder
init|=
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|NodeAllocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|allocationMultibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|SameShardNodeAllocation
operator|.
name|class
argument_list|)
expr_stmt|;
name|allocationMultibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|ReplicaAfterPrimaryActiveNodeAllocation
operator|.
name|class
argument_list|)
expr_stmt|;
name|allocationMultibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|ThrottlingNodeAllocation
operator|.
name|class
argument_list|)
expr_stmt|;
name|allocationMultibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|RebalanceOnlyWhenActiveNodeAllocation
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|NodeAllocation
argument_list|>
name|allocation
range|:
name|allocations
control|)
block|{
name|allocationMultibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|NodeAllocations
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

