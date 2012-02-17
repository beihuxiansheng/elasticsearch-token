begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.update
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|update
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
name|support
operator|.
name|BaseRequestBuilder
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|UpdateRequestBuilder
specifier|public
class|class
name|UpdateRequestBuilder
extends|extends
name|BaseRequestBuilder
argument_list|<
name|UpdateRequest
argument_list|,
name|UpdateResponse
argument_list|>
block|{
DECL|method|UpdateRequestBuilder
specifier|public
name|UpdateRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|UpdateRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|UpdateRequestBuilder
specifier|public
name|UpdateRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|UpdateRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the index the document will exists on.      */
DECL|method|setIndex
specifier|public
name|UpdateRequestBuilder
name|setIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|request
operator|.
name|index
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type of the indexed document.      */
DECL|method|setType
specifier|public
name|UpdateRequestBuilder
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|request
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the id of the indexed document.      */
DECL|method|setId
specifier|public
name|UpdateRequestBuilder
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|request
operator|.
name|id
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls the shard routing of the request. Using this value to hash the shard      * and not the id.      */
DECL|method|setRouting
specifier|public
name|UpdateRequestBuilder
name|setRouting
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setParent
specifier|public
name|UpdateRequestBuilder
name|setParent
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
name|request
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The script to execute. Note, make sure not to send different script each times and instead      * use script params if possible with the same (automatically compiled) script.      */
DECL|method|setScript
specifier|public
name|UpdateRequestBuilder
name|setScript
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|request
operator|.
name|script
argument_list|(
name|script
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The language of the script to execute.      */
DECL|method|setScriptLang
specifier|public
name|UpdateRequestBuilder
name|setScriptLang
parameter_list|(
name|String
name|scriptLang
parameter_list|)
block|{
name|request
operator|.
name|scriptLang
argument_list|(
name|scriptLang
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the script parameters to use with the script.      */
DECL|method|setScriptParams
specifier|public
name|UpdateRequestBuilder
name|setScriptParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|scriptParams
parameter_list|)
block|{
name|request
operator|.
name|scriptParams
argument_list|(
name|scriptParams
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a script parameter.      */
DECL|method|addScriptParam
specifier|public
name|UpdateRequestBuilder
name|addScriptParam
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|request
operator|.
name|addScriptParam
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the number of retries of a version conflict occurs because the document was updated between      * getting it and updating it. Defaults to 1.      */
DECL|method|setRetryOnConflict
specifier|public
name|UpdateRequestBuilder
name|setRetryOnConflict
parameter_list|(
name|int
name|retryOnConflict
parameter_list|)
block|{
name|request
operator|.
name|retryOnConflict
argument_list|(
name|retryOnConflict
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A timeout to wait if the index operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
DECL|method|setTimeout
specifier|public
name|UpdateRequestBuilder
name|setTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A timeout to wait if the index operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
DECL|method|setTimeout
specifier|public
name|UpdateRequestBuilder
name|setTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the replication type.      */
DECL|method|setReplicationType
specifier|public
name|UpdateRequestBuilder
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
comment|/**      * Sets the consistency level of write. Defaults to {@link org.elasticsearch.action.WriteConsistencyLevel#DEFAULT}      */
DECL|method|setConsistencyLevel
specifier|public
name|UpdateRequestBuilder
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
comment|/**      * Causes the updated document to be percolated. The parameter is the percolate query      * to use to reduce the percolated queries that are going to run against this doc. Can be      * set to<tt>*</tt> to indicate that all percolate queries should be run.      */
DECL|method|setPercolate
specifier|public
name|UpdateRequestBuilder
name|setPercolate
parameter_list|(
name|String
name|percolate
parameter_list|)
block|{
name|request
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
expr_stmt|;
return|return
name|this
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
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|update
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

