begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Maps
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
name|IndexShardRoutingTable
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
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentBuilderString
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
name|Arrays
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
name|Locale
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
implements|,
name|ToXContent
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
DECL|field|activePrimaryShards
specifier|private
name|int
name|activePrimaryShards
init|=
literal|0
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
DECL|field|shards
specifier|private
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
specifier|private
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
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|IndexRoutingTable
name|indexRoutingTable
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|indexMetaData
operator|.
name|index
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|indexMetaData
operator|.
name|getNumberOfShards
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfReplicas
operator|=
name|indexMetaData
operator|.
name|getNumberOfReplicas
argument_list|()
expr_stmt|;
name|this
operator|.
name|validationFailures
operator|=
name|indexRoutingTable
operator|.
name|validate
argument_list|(
name|indexMetaData
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|shardRoutingTable
range|:
name|indexRoutingTable
control|)
block|{
name|int
name|shardId
init|=
name|shardRoutingTable
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
decl_stmt|;
name|shards
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
operator|new
name|ClusterShardHealth
argument_list|(
name|shardId
argument_list|,
name|shardRoutingTable
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// update the index status
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|GREEN
expr_stmt|;
for|for
control|(
name|ClusterShardHealth
name|shardHealth
range|:
name|shards
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|shardHealth
operator|.
name|isPrimaryActive
argument_list|()
condition|)
block|{
name|activePrimaryShards
operator|++
expr_stmt|;
block|}
name|activeShards
operator|+=
name|shardHealth
operator|.
name|getActiveShards
argument_list|()
expr_stmt|;
name|relocatingShards
operator|+=
name|shardHealth
operator|.
name|getRelocatingShards
argument_list|()
expr_stmt|;
name|initializingShards
operator|+=
name|shardHealth
operator|.
name|getInitializingShards
argument_list|()
expr_stmt|;
name|unassignedShards
operator|+=
name|shardHealth
operator|.
name|getUnassignedShards
argument_list|()
expr_stmt|;
if|if
condition|(
name|shardHealth
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
name|shardHealth
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
comment|// do not override an existing red
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
operator|!
name|validationFailures
operator|.
name|isEmpty
argument_list|()
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
name|shards
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// might be since none has been created yet (two phase index creation)
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
block|}
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
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
name|this
operator|.
name|validationFailures
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
name|this
operator|.
name|shards
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
name|readString
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
name|getId
argument_list|()
argument_list|,
name|shardHealth
argument_list|)
expr_stmt|;
block|}
name|validationFailures
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|in
operator|.
name|readStringArray
argument_list|()
argument_list|)
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
name|writeString
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
name|writeString
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|STATUS
specifier|static
specifier|final
name|XContentBuilderString
name|STATUS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
DECL|field|NUMBER_OF_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|NUMBER_OF_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"number_of_shards"
argument_list|)
decl_stmt|;
DECL|field|NUMBER_OF_REPLICAS
specifier|static
specifier|final
name|XContentBuilderString
name|NUMBER_OF_REPLICAS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"number_of_replicas"
argument_list|)
decl_stmt|;
DECL|field|ACTIVE_PRIMARY_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|ACTIVE_PRIMARY_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"active_primary_shards"
argument_list|)
decl_stmt|;
DECL|field|ACTIVE_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|ACTIVE_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"active_shards"
argument_list|)
decl_stmt|;
DECL|field|RELOCATING_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|RELOCATING_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"relocating_shards"
argument_list|)
decl_stmt|;
DECL|field|INITIALIZING_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|INITIALIZING_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"initializing_shards"
argument_list|)
decl_stmt|;
DECL|field|UNASSIGNED_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|UNASSIGNED_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"unassigned_shards"
argument_list|)
decl_stmt|;
DECL|field|VALIDATION_FAILURES
specifier|static
specifier|final
name|XContentBuilderString
name|VALIDATION_FAILURES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"validation_failures"
argument_list|)
decl_stmt|;
DECL|field|SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shards"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY_ACTIVE
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARY_ACTIVE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primary_active"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATUS
argument_list|,
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUMBER_OF_SHARDS
argument_list|,
name|getNumberOfShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUMBER_OF_REPLICAS
argument_list|,
name|getNumberOfReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACTIVE_PRIMARY_SHARDS
argument_list|,
name|getActivePrimaryShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACTIVE_SHARDS
argument_list|,
name|getActiveShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RELOCATING_SHARDS
argument_list|,
name|getRelocatingShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INITIALIZING_SHARDS
argument_list|,
name|getInitializingShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|UNASSIGNED_SHARDS
argument_list|,
name|getUnassignedShards
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getValidationFailures
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|VALIDATION_FAILURES
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|validationFailure
range|:
name|getValidationFailures
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|validationFailure
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|"shards"
operator|.
name|equals
argument_list|(
name|params
operator|.
name|param
argument_list|(
literal|"level"
argument_list|,
literal|"indices"
argument_list|)
argument_list|)
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterShardHealth
name|shardHealth
range|:
name|shards
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|shardHealth
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATUS
argument_list|,
name|shardHealth
operator|.
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARY_ACTIVE
argument_list|,
name|shardHealth
operator|.
name|isPrimaryActive
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACTIVE_SHARDS
argument_list|,
name|shardHealth
operator|.
name|getActiveShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RELOCATING_SHARDS
argument_list|,
name|shardHealth
operator|.
name|getRelocatingShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INITIALIZING_SHARDS
argument_list|,
name|shardHealth
operator|.
name|getInitializingShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|UNASSIGNED_SHARDS
argument_list|,
name|shardHealth
operator|.
name|getUnassignedShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

