begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
comment|/**  * The {@link PlainShardIterator} is a {@link ShardsIterator} which iterates all  * shards or a given {@link ShardId shard id}  */
end_comment

begin_class
DECL|class|PlainShardIterator
specifier|public
class|class
name|PlainShardIterator
extends|extends
name|PlainShardsIterator
implements|implements
name|ShardIterator
block|{
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
comment|/**      * Creates a {@link PlainShardIterator} instance that iterates over a subset of the given shards      * this the a given<code>shardId</code>.      *      * @param shardId shard id of the group      * @param shards  shards to iterate      */
DECL|method|PlainShardIterator
specifier|public
name|PlainShardIterator
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
parameter_list|)
block|{
name|super
argument_list|(
name|shards
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
name|ShardIterator
name|that
init|=
operator|(
name|ShardIterator
operator|)
name|o
decl_stmt|;
return|return
name|shardId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|shardId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|shardId
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|ShardIterator
name|o
parameter_list|)
block|{
return|return
name|shardId
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|shardId
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

