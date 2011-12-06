begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.health
package|package
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
name|health
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
name|ImmutableList
import|;
end_import

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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterShardHealth
operator|.
name|readClusterShardHealth
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterIndexHealth
specifier|public
class|class
name|ClusterIndexHealth
implements|implements
name|Iterable
argument_list|<
name|ClusterShardHealth
argument_list|>
implements|,
name|Streamable
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|numberOfShards
specifier|private
name|int
name|numberOfShards
decl_stmt|;
DECL|field|numberOfReplicas
specifier|private
name|int
name|numberOfReplicas
decl_stmt|;
DECL|field|activeShards
name|int
name|activeShards
init|=
literal|0
decl_stmt|;
DECL|field|relocatingShards
name|int
name|relocatingShards
init|=
literal|0
decl_stmt|;
DECL|field|initializingShards
name|int
name|initializingShards
init|=
literal|0
decl_stmt|;
DECL|field|unassignedShards
name|int
name|unassignedShards
init|=
literal|0
decl_stmt|;
DECL|field|activePrimaryShards
name|int
name|activePrimaryShards
init|=
literal|0
decl_stmt|;
DECL|field|status
name|ClusterHealthStatus
name|status
init|=
name|ClusterHealthStatus
operator|.
name|RED
decl_stmt|;
DECL|field|shards
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|ClusterShardHealth
argument_list|>
name|shards
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|validationFailures
name|List
argument_list|<
name|String
argument_list|>
name|validationFailures
decl_stmt|;
DECL|method|ClusterIndexHealth
specifier|private
name|ClusterIndexHealth
parameter_list|()
block|{     }
DECL|method|ClusterIndexHealth
specifier|public
name|ClusterIndexHealth
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|numberOfShards
parameter_list|,
name|int
name|numberOfReplicas
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|validationFailures
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|numberOfShards
expr_stmt|;
name|this
operator|.
name|numberOfReplicas
operator|=
name|numberOfReplicas
expr_stmt|;
name|this
operator|.
name|validationFailures
operator|=
name|validationFailures
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
argument_list|()
return|;
block|}
DECL|method|validationFailures
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|validationFailures
parameter_list|()
block|{
return|return
name|this
operator|.
name|validationFailures
return|;
block|}
DECL|method|getValidationFailures
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getValidationFailures
parameter_list|()
block|{
return|return
name|validationFailures
argument_list|()
return|;
block|}
DECL|method|numberOfShards
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|numberOfShards
return|;
block|}
DECL|method|getNumberOfShards
specifier|public
name|int
name|getNumberOfShards
parameter_list|()
block|{
return|return
name|numberOfShards
argument_list|()
return|;
block|}
DECL|method|numberOfReplicas
specifier|public
name|int
name|numberOfReplicas
parameter_list|()
block|{
return|return
name|numberOfReplicas
return|;
block|}
DECL|method|getNumberOfReplicas
specifier|public
name|int
name|getNumberOfReplicas
parameter_list|()
block|{
return|return
name|numberOfReplicas
argument_list|()
return|;
block|}
DECL|method|activeShards
specifier|public
name|int
name|activeShards
parameter_list|()
block|{
return|return
name|activeShards
return|;
block|}
DECL|method|getActiveShards
specifier|public
name|int
name|getActiveShards
parameter_list|()
block|{
return|return
name|activeShards
argument_list|()
return|;
block|}
DECL|method|relocatingShards
specifier|public
name|int
name|relocatingShards
parameter_list|()
block|{
return|return
name|relocatingShards
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
argument_list|()
return|;
block|}
DECL|method|activePrimaryShards
specifier|public
name|int
name|activePrimaryShards
parameter_list|()
block|{
return|return
name|activePrimaryShards
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
argument_list|()
return|;
block|}
DECL|method|initializingShards
specifier|public
name|int
name|initializingShards
parameter_list|()
block|{
return|return
name|initializingShards
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
argument_list|()
return|;
block|}
DECL|method|unassignedShards
specifier|public
name|int
name|unassignedShards
parameter_list|()
block|{
return|return
name|unassignedShards
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
argument_list|()
return|;
block|}
DECL|method|status
specifier|public
name|ClusterHealthStatus
name|status
parameter_list|()
block|{
return|return
name|status
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
argument_list|()
return|;
block|}
DECL|method|shards
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|ClusterShardHealth
argument_list|>
name|shards
parameter_list|()
block|{
return|return
name|this
operator|.
name|shards
return|;
block|}
DECL|method|getShards
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|ClusterShardHealth
argument_list|>
name|getShards
parameter_list|()
block|{
return|return
name|shards
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|ClusterShardHealth
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|shards
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|readClusterIndexHealth
specifier|public
specifier|static
name|ClusterIndexHealth
name|readClusterIndexHealth
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterIndexHealth
name|indexHealth
init|=
operator|new
name|ClusterIndexHealth
argument_list|()
decl_stmt|;
name|indexHealth
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|indexHealth
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
name|index
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|numberOfShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|numberOfReplicas
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
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
name|ClusterShardHealth
name|shardHealth
init|=
name|readClusterShardHealth
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|shards
operator|.
name|put
argument_list|(
name|shardHealth
operator|.
name|id
argument_list|()
argument_list|,
name|shardHealth
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|validationFailures
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
name|validationFailures
operator|.
name|add
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|writeUTF
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numberOfShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numberOfReplicas
argument_list|)
expr_stmt|;
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
name|shards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterShardHealth
name|shardHealth
range|:
name|this
control|)
block|{
name|shardHealth
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|validationFailures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|failure
range|:
name|validationFailures
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

