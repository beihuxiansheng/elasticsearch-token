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
name|MasterNodeReadOperationRequest
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
name|Priority
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|readTimeValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterHealthRequest
specifier|public
class|class
name|ClusterHealthRequest
extends|extends
name|MasterNodeReadOperationRequest
argument_list|<
name|ClusterHealthRequest
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
decl_stmt|;
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
init|=
operator|new
name|TimeValue
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|waitForStatus
specifier|private
name|ClusterHealthStatus
name|waitForStatus
decl_stmt|;
DECL|field|waitForRelocatingShards
specifier|private
name|int
name|waitForRelocatingShards
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|waitForActiveShards
specifier|private
name|int
name|waitForActiveShards
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|waitForNodes
specifier|private
name|String
name|waitForNodes
init|=
literal|""
decl_stmt|;
DECL|field|waitForEvents
specifier|private
name|Priority
name|waitForEvents
init|=
literal|null
decl_stmt|;
DECL|method|ClusterHealthRequest
name|ClusterHealthRequest
parameter_list|()
block|{     }
DECL|method|ClusterHealthRequest
specifier|public
name|ClusterHealthRequest
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
DECL|method|indices
specifier|public
name|ClusterHealthRequest
name|indices
parameter_list|(
name|String
index|[]
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
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|IndicesOptions
operator|.
name|lenientExpandOpen
argument_list|()
return|;
block|}
DECL|method|timeout
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
DECL|method|timeout
specifier|public
name|ClusterHealthRequest
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
if|if
condition|(
name|masterNodeTimeout
operator|==
name|DEFAULT_MASTER_NODE_TIMEOUT
condition|)
block|{
name|masterNodeTimeout
operator|=
name|timeout
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|ClusterHealthRequest
name|timeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
return|return
name|this
operator|.
name|timeout
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|timeout
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".timeout"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|waitForStatus
specifier|public
name|ClusterHealthStatus
name|waitForStatus
parameter_list|()
block|{
return|return
name|waitForStatus
return|;
block|}
DECL|method|waitForStatus
specifier|public
name|ClusterHealthRequest
name|waitForStatus
parameter_list|(
name|ClusterHealthStatus
name|waitForStatus
parameter_list|)
block|{
name|this
operator|.
name|waitForStatus
operator|=
name|waitForStatus
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|waitForGreenStatus
specifier|public
name|ClusterHealthRequest
name|waitForGreenStatus
parameter_list|()
block|{
return|return
name|waitForStatus
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
return|;
block|}
DECL|method|waitForYellowStatus
specifier|public
name|ClusterHealthRequest
name|waitForYellowStatus
parameter_list|()
block|{
return|return
name|waitForStatus
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|)
return|;
block|}
DECL|method|waitForRelocatingShards
specifier|public
name|int
name|waitForRelocatingShards
parameter_list|()
block|{
return|return
name|waitForRelocatingShards
return|;
block|}
DECL|method|waitForRelocatingShards
specifier|public
name|ClusterHealthRequest
name|waitForRelocatingShards
parameter_list|(
name|int
name|waitForRelocatingShards
parameter_list|)
block|{
name|this
operator|.
name|waitForRelocatingShards
operator|=
name|waitForRelocatingShards
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|waitForActiveShards
specifier|public
name|int
name|waitForActiveShards
parameter_list|()
block|{
return|return
name|waitForActiveShards
return|;
block|}
DECL|method|waitForActiveShards
specifier|public
name|ClusterHealthRequest
name|waitForActiveShards
parameter_list|(
name|int
name|waitForActiveShards
parameter_list|)
block|{
name|this
operator|.
name|waitForActiveShards
operator|=
name|waitForActiveShards
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|waitForNodes
specifier|public
name|String
name|waitForNodes
parameter_list|()
block|{
return|return
name|waitForNodes
return|;
block|}
comment|/**      * Waits for N number of nodes. Use "12" for exact mapping, ">12" and "<12" for range.      */
DECL|method|waitForNodes
specifier|public
name|ClusterHealthRequest
name|waitForNodes
parameter_list|(
name|String
name|waitForNodes
parameter_list|)
block|{
name|this
operator|.
name|waitForNodes
operator|=
name|waitForNodes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|waitForEvents
specifier|public
name|ClusterHealthRequest
name|waitForEvents
parameter_list|(
name|Priority
name|waitForEvents
parameter_list|)
block|{
name|this
operator|.
name|waitForEvents
operator|=
name|waitForEvents
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|waitForEvents
specifier|public
name|Priority
name|waitForEvents
parameter_list|()
block|{
return|return
name|this
operator|.
name|waitForEvents
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|indices
operator|=
name|Strings
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|indices
operator|=
operator|new
name|String
index|[
name|size
index|]
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
block|}
name|timeout
operator|=
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|waitForStatus
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
block|}
name|waitForRelocatingShards
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|waitForActiveShards
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|waitForNodes
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|waitForEvents
operator|=
name|Priority
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|indices
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
name|timeout
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|waitForStatus
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|waitForStatus
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|waitForRelocatingShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|waitForActiveShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|waitForNodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|waitForEvents
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Priority
operator|.
name|writeTo
argument_list|(
name|waitForEvents
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

