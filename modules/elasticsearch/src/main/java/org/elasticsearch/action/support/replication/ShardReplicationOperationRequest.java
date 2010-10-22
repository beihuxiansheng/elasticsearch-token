begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
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
name|ActionRequest
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
name|WriteConsistencyLevel
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
name|action
operator|.
name|Actions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ShardReplicationOperationRequest
specifier|public
specifier|abstract
class|class
name|ShardReplicationOperationRequest
implements|implements
name|ActionRequest
block|{
DECL|field|DEFAULT_TIMEOUT
specifier|public
specifier|static
specifier|final
name|TimeValue
name|DEFAULT_TIMEOUT
init|=
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
decl_stmt|;
DECL|field|timeout
specifier|protected
name|TimeValue
name|timeout
init|=
name|DEFAULT_TIMEOUT
decl_stmt|;
DECL|field|index
specifier|protected
name|String
name|index
decl_stmt|;
DECL|field|threadedListener
specifier|private
name|boolean
name|threadedListener
init|=
literal|false
decl_stmt|;
DECL|field|threadedOperation
specifier|private
name|boolean
name|threadedOperation
init|=
literal|true
decl_stmt|;
DECL|field|replicationType
specifier|private
name|ReplicationType
name|replicationType
init|=
name|ReplicationType
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|consistencyLevel
specifier|private
name|WriteConsistencyLevel
name|consistencyLevel
init|=
name|WriteConsistencyLevel
operator|.
name|DEFAULT
decl_stmt|;
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
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
DECL|method|index
specifier|public
name|ShardReplicationOperationRequest
name|index
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the listener be called on a separate thread if needed.      */
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|boolean
name|listenerThreaded
parameter_list|()
block|{
return|return
name|threadedListener
return|;
block|}
comment|/**      * Should the listener be called on a separate thread if needed.      */
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|ShardReplicationOperationRequest
name|listenerThreaded
parameter_list|(
name|boolean
name|threadedListener
parameter_list|)
block|{
name|this
operator|.
name|threadedListener
operator|=
name|threadedListener
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls if the operation will be executed on a separate thread when executed locally.      */
DECL|method|operationThreaded
specifier|public
name|boolean
name|operationThreaded
parameter_list|()
block|{
return|return
name|threadedOperation
return|;
block|}
comment|/**      * Controls if the operation will be executed on a separate thread when executed locally. Defaults      * to<tt>true</tt> when running in embedded mode.      */
DECL|method|operationThreaded
specifier|public
name|ShardReplicationOperationRequest
name|operationThreaded
parameter_list|(
name|boolean
name|threadedOperation
parameter_list|)
block|{
name|this
operator|.
name|threadedOperation
operator|=
name|threadedOperation
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The replication type.      */
DECL|method|replicationType
specifier|public
name|ReplicationType
name|replicationType
parameter_list|()
block|{
return|return
name|this
operator|.
name|replicationType
return|;
block|}
comment|/**      * Sets the replication type.      */
DECL|method|replicationType
specifier|public
name|ShardReplicationOperationRequest
name|replicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|this
operator|.
name|replicationType
operator|=
name|replicationType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|consistencyLevel
specifier|public
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|()
block|{
return|return
name|this
operator|.
name|consistencyLevel
return|;
block|}
DECL|method|consistencyLevel
specifier|public
name|ShardReplicationOperationRequest
name|consistencyLevel
parameter_list|(
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|)
block|{
name|this
operator|.
name|consistencyLevel
operator|=
name|consistencyLevel
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"index is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
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
name|replicationType
operator|=
name|ReplicationType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|consistencyLevel
operator|=
name|WriteConsistencyLevel
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|timeout
operator|=
name|TimeValue
operator|.
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|index
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
comment|// no need to serialize threaded* parameters, since they only matter locally
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
name|writeByte
argument_list|(
name|replicationType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|consistencyLevel
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|timeout
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called before the request gets forked into a local thread.      */
DECL|method|beforeLocalFork
specifier|public
name|void
name|beforeLocalFork
parameter_list|()
block|{      }
block|}
end_class

end_unit

