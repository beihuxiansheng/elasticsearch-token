begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.shards
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|shards
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
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
name|IndicesRequest
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
name|health
operator|.
name|ClusterHealthStatus
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
name|IndicesOptions
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
name|master
operator|.
name|MasterNodeReadRequest
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
name|EnumSet
import|;
end_import

begin_comment
comment|/**  * Request for {@link IndicesShardStoresAction}  */
end_comment

begin_class
DECL|class|IndicesShardStoresRequest
specifier|public
class|class
name|IndicesShardStoresRequest
extends|extends
name|MasterNodeReadRequest
argument_list|<
name|IndicesShardStoresRequest
argument_list|>
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|indicesOptions
specifier|private
name|IndicesOptions
name|indicesOptions
init|=
name|IndicesOptions
operator|.
name|strictExpand
argument_list|()
decl_stmt|;
DECL|field|statuses
specifier|private
name|EnumSet
argument_list|<
name|ClusterHealthStatus
argument_list|>
name|statuses
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|,
name|ClusterHealthStatus
operator|.
name|RED
argument_list|)
decl_stmt|;
comment|/**      * Create a request for shard stores info for<code>indices</code>      */
DECL|method|IndicesShardStoresRequest
specifier|public
name|IndicesShardStoresRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
block|}
DECL|method|IndicesShardStoresRequest
name|IndicesShardStoresRequest
parameter_list|()
block|{     }
comment|/**      * Set statuses to filter shards to get stores info on.      * see {@link ClusterHealthStatus} for details.      * Defaults to "yellow" and "red" status      * @param shardStatuses acceptable values are "green", "yellow", "red" and "all"      */
DECL|method|shardStatuses
specifier|public
name|IndicesShardStoresRequest
name|shardStatuses
parameter_list|(
name|String
modifier|...
name|shardStatuses
parameter_list|)
block|{
name|statuses
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ClusterHealthStatus
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|statusString
range|:
name|shardStatuses
control|)
block|{
if|if
condition|(
literal|"all"
operator|.
name|equalsIgnoreCase
argument_list|(
name|statusString
argument_list|)
condition|)
block|{
name|statuses
operator|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|ClusterHealthStatus
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|statuses
operator|.
name|add
argument_list|(
name|ClusterHealthStatus
operator|.
name|fromString
argument_list|(
name|statusString
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Specifies what type of requested indices to ignore and wildcard indices expressions      * By default, expands wildcards to both open and closed indices      */
DECL|method|indicesOptions
specifier|public
name|IndicesShardStoresRequest
name|indicesOptions
parameter_list|(
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
name|this
operator|.
name|indicesOptions
operator|=
name|indicesOptions
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the indices for the shard stores request      */
annotation|@
name|Override
DECL|method|indices
specifier|public
name|IndicesShardStoresRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns the shard criteria to get store information on      */
DECL|method|shardStatuses
specifier|public
name|EnumSet
argument_list|<
name|ClusterHealthStatus
argument_list|>
name|shardStatuses
parameter_list|()
block|{
return|return
name|statuses
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|indicesOptions
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|statuses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterHealthStatus
name|status
range|:
name|statuses
control|)
block|{
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
block|}
name|indicesOptions
operator|.
name|writeIndicesOptions
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|int
name|nStatus
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|statuses
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ClusterHealthStatus
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nStatus
condition|;
name|i
operator|++
control|)
block|{
name|statuses
operator|.
name|add
argument_list|(
name|ClusterHealthStatus
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indicesOptions
operator|=
name|IndicesOptions
operator|.
name|readIndicesOptions
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
