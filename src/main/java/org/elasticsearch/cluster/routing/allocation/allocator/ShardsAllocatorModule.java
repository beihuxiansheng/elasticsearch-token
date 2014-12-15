begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.allocator
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
operator|.
name|allocator
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
name|gateway
operator|.
name|GatewayAllocator
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardsAllocatorModule
specifier|public
class|class
name|ShardsAllocatorModule
extends|extends
name|AbstractModule
block|{
DECL|field|EVEN_SHARD_COUNT_ALLOCATOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|EVEN_SHARD_COUNT_ALLOCATOR_KEY
init|=
literal|"even_shard"
decl_stmt|;
DECL|field|BALANCED_ALLOCATOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BALANCED_ALLOCATOR_KEY
init|=
literal|"balanced"
decl_stmt|;
comment|// default
DECL|field|TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_KEY
init|=
literal|"cluster.routing.allocation.type"
decl_stmt|;
DECL|field|settings
specifier|private
name|Settings
name|settings
decl_stmt|;
DECL|field|shardsAllocator
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|ShardsAllocator
argument_list|>
name|shardsAllocator
decl_stmt|;
DECL|method|ShardsAllocatorModule
specifier|public
name|ShardsAllocatorModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|shardsAllocator
operator|=
name|loadShardsAllocator
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
if|if
condition|(
name|shardsAllocator
operator|==
literal|null
condition|)
block|{
name|shardsAllocator
operator|=
name|loadShardsAllocator
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|GatewayAllocator
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|ShardsAllocator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|shardsAllocator
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
DECL|method|loadShardsAllocator
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|ShardsAllocator
argument_list|>
name|loadShardsAllocator
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ShardsAllocator
argument_list|>
name|shardsAllocator
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|settings
operator|.
name|get
argument_list|(
name|TYPE_KEY
argument_list|,
name|BALANCED_ALLOCATOR_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|BALANCED_ALLOCATOR_KEY
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|shardsAllocator
operator|=
name|BalancedShardsAllocator
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|EVEN_SHARD_COUNT_ALLOCATOR_KEY
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|shardsAllocator
operator|=
name|EvenShardsCountAllocator
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
name|shardsAllocator
operator|=
name|settings
operator|.
name|getAsClass
argument_list|(
name|TYPE_KEY
argument_list|,
name|BalancedShardsAllocator
operator|.
name|class
argument_list|,
literal|"org.elasticsearch.cluster.routing.allocation.allocator."
argument_list|,
literal|"Allocator"
argument_list|)
expr_stmt|;
block|}
return|return
name|shardsAllocator
return|;
block|}
block|}
end_class

end_unit

