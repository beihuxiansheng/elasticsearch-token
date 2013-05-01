begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
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
name|ActionListener
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
name|ActionRequestBuilder
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
name|action
operator|.
name|delete
operator|.
name|DeleteRequest
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
name|delete
operator|.
name|DeleteRequestBuilder
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
name|index
operator|.
name|IndexRequest
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
name|index
operator|.
name|IndexRequestBuilder
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
name|replication
operator|.
name|ReplicationType
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
name|update
operator|.
name|UpdateRequest
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
name|update
operator|.
name|UpdateRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|internal
operator|.
name|InternalClient
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

begin_comment
comment|/**  * A bulk request holds an ordered {@link IndexRequest}s and {@link DeleteRequest}s and allows to executes  * it in a single batch.  */
end_comment

begin_class
DECL|class|BulkRequestBuilder
specifier|public
class|class
name|BulkRequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|BulkRequest
argument_list|,
name|BulkResponse
argument_list|,
name|BulkRequestBuilder
argument_list|>
block|{
DECL|method|BulkRequestBuilder
specifier|public
name|BulkRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|InternalClient
operator|)
name|client
argument_list|,
operator|new
name|BulkRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds an {@link IndexRequest} to the list of actions to execute. Follows the same behavior of {@link IndexRequest}      * (for example, if no id is provided, one will be generated, or usage of the create flag).      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|IndexRequest
name|request
parameter_list|)
block|{
name|super
operator|.
name|request
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link IndexRequest} to the list of actions to execute. Follows the same behavior of {@link IndexRequest}      * (for example, if no id is provided, one will be generated, or usage of the create flag).      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|IndexRequestBuilder
name|request
parameter_list|)
block|{
name|super
operator|.
name|request
operator|.
name|add
argument_list|(
name|request
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link DeleteRequest} to the list of actions to execute.      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|DeleteRequest
name|request
parameter_list|)
block|{
name|super
operator|.
name|request
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link DeleteRequest} to the list of actions to execute.      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|DeleteRequestBuilder
name|request
parameter_list|)
block|{
name|super
operator|.
name|request
operator|.
name|add
argument_list|(
name|request
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link DeleteRequest} to the list of actions to execute.      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|UpdateRequest
name|request
parameter_list|)
block|{
name|super
operator|.
name|request
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link DeleteRequest} to the list of actions to execute.      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|UpdateRequestBuilder
name|request
parameter_list|)
block|{
name|super
operator|.
name|request
operator|.
name|add
argument_list|(
name|request
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a framed data in binary format      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|)
throws|throws
name|Exception
block|{
name|request
operator|.
name|add
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|length
argument_list|,
name|contentUnsafe
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a framed data in binary format      */
DECL|method|add
specifier|public
name|BulkRequestBuilder
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultIndex
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultType
parameter_list|)
throws|throws
name|Exception
block|{
name|request
operator|.
name|add
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|length
argument_list|,
name|contentUnsafe
argument_list|,
name|defaultIndex
argument_list|,
name|defaultType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the replication type for this operation.      */
DECL|method|setReplicationType
specifier|public
name|BulkRequestBuilder
name|setReplicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|request
operator|.
name|replicationType
argument_list|(
name|replicationType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the consistency level. Defaults to {@link org.elasticsearch.action.WriteConsistencyLevel#DEFAULT}.      */
DECL|method|setConsistencyLevel
specifier|public
name|BulkRequestBuilder
name|setConsistencyLevel
parameter_list|(
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|)
block|{
name|request
operator|.
name|consistencyLevel
argument_list|(
name|consistencyLevel
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should a refresh be executed post this bulk operation causing the operations to      * be searchable. Note, heavy indexing should not set this to<tt>true</tt>. Defaults      * to<tt>false</tt>.      */
DECL|method|setRefresh
specifier|public
name|BulkRequestBuilder
name|setRefresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|request
operator|.
name|refresh
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The number of actions currently in the bulk.      */
DECL|method|numberOfActions
specifier|public
name|int
name|numberOfActions
parameter_list|()
block|{
return|return
name|request
operator|.
name|numberOfActions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
operator|(
operator|(
name|Client
operator|)
name|client
operator|)
operator|.
name|bulk
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

