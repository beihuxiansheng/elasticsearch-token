begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|action
operator|.
name|ActionResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
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
name|ClusterIndexHealth
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ClusterHealthResponse
specifier|public
class|class
name|ClusterHealthResponse
implements|implements
name|ActionResponse
implements|,
name|Iterable
argument_list|<
name|ClusterIndexHealth
argument_list|>
block|{
DECL|field|clusterName
specifier|private
name|String
name|clusterName
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
DECL|field|activePrimaryShards
name|int
name|activePrimaryShards
init|=
literal|0
decl_stmt|;
DECL|field|timedOut
name|boolean
name|timedOut
init|=
literal|false
decl_stmt|;
DECL|field|status
name|ClusterHealthStatus
name|status
init|=
name|ClusterHealthStatus
operator|.
name|RED
decl_stmt|;
DECL|field|validationFailures
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|validationFailures
decl_stmt|;
DECL|field|indices
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterIndexHealth
argument_list|>
name|indices
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|ClusterHealthResponse
name|ClusterHealthResponse
parameter_list|()
block|{     }
DECL|method|ClusterHealthResponse
specifier|public
name|ClusterHealthResponse
parameter_list|(
name|String
name|clusterName
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
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|validationFailures
operator|=
name|validationFailures
expr_stmt|;
block|}
DECL|method|clusterName
specifier|public
name|String
name|clusterName
parameter_list|()
block|{
return|return
name|clusterName
return|;
block|}
DECL|method|getClusterName
specifier|public
name|String
name|getClusterName
parameter_list|()
block|{
return|return
name|clusterName
argument_list|()
return|;
block|}
comment|/**      * The validation failures on the cluster level (without index validation failures).      */
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
comment|/**      * The validation failures on the cluster level (without index validation failures).      */
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
comment|/**      * All the validation failures, including index level validation failures.      */
DECL|method|allValidationFailures
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|allValidationFailures
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|allFailures
init|=
name|newArrayList
argument_list|(
name|validationFailures
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ClusterIndexHealth
name|indexHealth
range|:
name|indices
operator|.
name|values
argument_list|()
control|)
block|{
name|allFailures
operator|.
name|addAll
argument_list|(
name|indexHealth
operator|.
name|validationFailures
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|allFailures
return|;
block|}
comment|/**      * All the validation failures, including index level validation failures.      */
DECL|method|getAllValidationFailures
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllValidationFailures
parameter_list|()
block|{
return|return
name|allValidationFailures
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
comment|/**      *<tt>true</tt> if the waitForXXX has timeout out and did not match.      */
DECL|method|timedOut
specifier|public
name|boolean
name|timedOut
parameter_list|()
block|{
return|return
name|this
operator|.
name|timedOut
return|;
block|}
DECL|method|isTimedOut
specifier|public
name|boolean
name|isTimedOut
parameter_list|()
block|{
return|return
name|this
operator|.
name|timedOut
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
DECL|method|indices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterIndexHealth
argument_list|>
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterIndexHealth
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|indices
argument_list|()
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ClusterIndexHealth
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|indices
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|clusterName
operator|=
name|in
operator|.
name|readUTF
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
name|ClusterIndexHealth
name|indexHealth
init|=
name|readClusterIndexHealth
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|indices
operator|.
name|put
argument_list|(
name|indexHealth
operator|.
name|index
argument_list|()
argument_list|,
name|indexHealth
argument_list|)
expr_stmt|;
block|}
name|timedOut
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
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
DECL|method|writeTo
annotation|@
name|Override
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
name|clusterName
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
name|indices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterIndexHealth
name|indexHealth
range|:
name|this
control|)
block|{
name|indexHealth
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|timedOut
argument_list|)
expr_stmt|;
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

