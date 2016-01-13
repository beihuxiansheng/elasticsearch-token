begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.single.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|single
operator|.
name|shard
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
name|ValidateActions
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
name|common
operator|.
name|Nullable
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SingleShardRequest
specifier|public
specifier|abstract
class|class
name|SingleShardRequest
parameter_list|<
name|Request
extends|extends
name|SingleShardRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|>
extends|extends
name|ActionRequest
argument_list|<
name|Request
argument_list|>
implements|implements
name|IndicesRequest
block|{
DECL|field|INDICES_OPTIONS
specifier|public
specifier|static
specifier|final
name|IndicesOptions
name|INDICES_OPTIONS
init|=
name|IndicesOptions
operator|.
name|strictSingleIndexNoExpandForbidClosed
argument_list|()
decl_stmt|;
comment|/**      * The concrete index name      *      * Whether index property is optional depends on the concrete implementation. If index property is required the      * concrete implementation should use {@link #validateNonNullIndex()} to check if the index property has been set      */
annotation|@
name|Nullable
DECL|field|index
specifier|protected
name|String
name|index
decl_stmt|;
DECL|field|internalShardId
name|ShardId
name|internalShardId
decl_stmt|;
DECL|field|threadedOperation
specifier|private
name|boolean
name|threadedOperation
init|=
literal|true
decl_stmt|;
DECL|method|SingleShardRequest
specifier|public
name|SingleShardRequest
parameter_list|()
block|{     }
DECL|method|SingleShardRequest
specifier|protected
name|SingleShardRequest
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
block|}
DECL|method|SingleShardRequest
specifier|protected
name|SingleShardRequest
parameter_list|(
name|ActionRequest
argument_list|<
name|?
argument_list|>
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|SingleShardRequest
specifier|protected
name|SingleShardRequest
parameter_list|(
name|ActionRequest
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|/**      * @return a validation exception if the index property hasn't been set      */
DECL|method|validateNonNullIndex
specifier|protected
name|ActionRequestValidationException
name|validateNonNullIndex
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
name|ValidateActions
operator|.
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
comment|/**      * @return The concrete index this request is targeted for or<code>null</code> if index is optional.      *         Whether index property is optional depends on the concrete implementation. If index property      *         is required the concrete implementation should use {@link #validateNonNullIndex()} to check      *         if the index property has been set      */
annotation|@
name|Nullable
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
comment|/**      * Sets the index.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|index
specifier|public
specifier|final
name|Request
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
operator|(
name|Request
operator|)
name|this
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
operator|new
name|String
index|[]
block|{
name|index
block|}
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
name|INDICES_OPTIONS
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
comment|/**      * Controls if the operation will be executed on a separate thread when executed locally.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|operationThreaded
specifier|public
specifier|final
name|Request
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
operator|(
name|Request
operator|)
name|this
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|internalShardId
operator|=
name|ShardId
operator|.
name|readShardId
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|index
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
comment|// no need to pass threading over the network, they are always false when coming throw a thread pool
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
name|writeOptionalStreamable
argument_list|(
name|internalShardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

