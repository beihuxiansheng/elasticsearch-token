begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_comment
comment|/**  * A class that represents a stale shard copy.  */
end_comment

begin_class
DECL|class|StaleShard
specifier|public
class|class
name|StaleShard
block|{
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|allocationId
specifier|private
specifier|final
name|String
name|allocationId
decl_stmt|;
DECL|method|StaleShard
specifier|public
name|StaleShard
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|allocationId
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|allocationId
operator|=
name|allocationId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"stale shard, shard "
operator|+
name|shardId
operator|+
literal|", alloc. id ["
operator|+
name|allocationId
operator|+
literal|"]"
return|;
block|}
comment|/**      * The shard id of the stale shard.      */
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
comment|/**      * The allocation id of the stale shard.      */
DECL|method|getAllocationId
specifier|public
name|String
name|getAllocationId
parameter_list|()
block|{
return|return
name|allocationId
return|;
block|}
block|}
end_class

end_unit

