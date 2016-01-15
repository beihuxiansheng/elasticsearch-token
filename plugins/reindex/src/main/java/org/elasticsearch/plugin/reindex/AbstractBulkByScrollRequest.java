begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|reindex
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
name|action
operator|.
name|search
operator|.
name|SearchRequest
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
name|ReplicationRequest
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_class
DECL|class|AbstractBulkByScrollRequest
specifier|public
specifier|abstract
class|class
name|AbstractBulkByScrollRequest
parameter_list|<
name|Self
extends|extends
name|AbstractBulkByScrollRequest
parameter_list|<
name|Self
parameter_list|>
parameter_list|>
extends|extends
name|ActionRequest
argument_list|<
name|Self
argument_list|>
block|{
DECL|field|SIZE_ALL_MATCHES
specifier|public
specifier|static
specifier|final
name|int
name|SIZE_ALL_MATCHES
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DEFAULT_SCROLL_TIMEOUT
specifier|private
specifier|static
specifier|final
name|TimeValue
name|DEFAULT_SCROLL_TIMEOUT
init|=
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_SCROLL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SCROLL_SIZE
init|=
literal|100
decl_stmt|;
comment|/**      * The search to be executed.      */
DECL|field|source
specifier|private
name|SearchRequest
name|source
decl_stmt|;
comment|/**      * Maximum number of processed documents. Defaults to -1 meaning process all      * documents.      */
DECL|field|size
specifier|private
name|int
name|size
init|=
name|SIZE_ALL_MATCHES
decl_stmt|;
comment|/**      * Should version conflicts cause aborts? Defaults to true.      */
DECL|field|abortOnVersionConflict
specifier|private
name|boolean
name|abortOnVersionConflict
init|=
literal|true
decl_stmt|;
comment|/**      * Call refresh on the indexes we've written to after the request ends?      */
DECL|field|refresh
specifier|private
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
comment|/**      * Timeout to wait for the shards on to be available for each bulk request?      */
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
init|=
name|ReplicationRequest
operator|.
name|DEFAULT_TIMEOUT
decl_stmt|;
comment|/**      * Consistency level for write requests.      */
DECL|field|consistency
specifier|private
name|WriteConsistencyLevel
name|consistency
init|=
name|WriteConsistencyLevel
operator|.
name|DEFAULT
decl_stmt|;
DECL|method|AbstractBulkByScrollRequest
specifier|public
name|AbstractBulkByScrollRequest
parameter_list|()
block|{     }
DECL|method|AbstractBulkByScrollRequest
specifier|public
name|AbstractBulkByScrollRequest
parameter_list|(
name|SearchRequest
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
comment|// Set the defaults which differ from SearchRequest's defaults.
name|source
operator|.
name|scroll
argument_list|(
name|DEFAULT_SCROLL_TIMEOUT
argument_list|)
expr_stmt|;
name|source
operator|.
name|source
argument_list|(
operator|new
name|SearchSourceBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|source
argument_list|()
operator|.
name|version
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|source
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|(
name|DEFAULT_SCROLL_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * `this` cast to Self. Used for building fluent methods without cast      * warnings.      */
DECL|method|self
specifier|protected
specifier|abstract
name|Self
name|self
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|e
init|=
name|source
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|source
argument_list|()
operator|.
name|from
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|e
operator|=
name|addValidationError
argument_list|(
literal|"from is not supported in this context"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|false
operator|==
operator|(
name|size
operator|==
operator|-
literal|1
operator|||
name|size
operator|>
literal|0
operator|)
condition|)
block|{
name|e
operator|=
name|addValidationError
argument_list|(
literal|"size should be greater than 0 if the request is limited to some number of documents or -1 if it isn't but it was ["
operator|+
name|size
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
comment|/**      * Maximum number of processed documents. Defaults to -1 meaning process all      * documents.      */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * Maximum number of processed documents. Defaults to -1 meaning process all      * documents.      */
DECL|method|setSize
specifier|public
name|Self
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|self
argument_list|()
return|;
block|}
comment|/**      * Should version conflicts cause aborts? Defaults to false.      */
DECL|method|isAbortOnVersionConflict
specifier|public
name|boolean
name|isAbortOnVersionConflict
parameter_list|()
block|{
return|return
name|abortOnVersionConflict
return|;
block|}
comment|/**      * Should version conflicts cause aborts? Defaults to false.      */
DECL|method|setAbortOnVersionConflict
specifier|public
name|Self
name|setAbortOnVersionConflict
parameter_list|(
name|boolean
name|abortOnVersionConflict
parameter_list|)
block|{
name|this
operator|.
name|abortOnVersionConflict
operator|=
name|abortOnVersionConflict
expr_stmt|;
return|return
name|self
argument_list|()
return|;
block|}
comment|/**      * Sets abortOnVersionConflict based on REST-friendly names.      */
DECL|method|setConflicts
specifier|public
name|void
name|setConflicts
parameter_list|(
name|String
name|conflicts
parameter_list|)
block|{
switch|switch
condition|(
name|conflicts
condition|)
block|{
case|case
literal|"proceed"
case|:
name|setAbortOnVersionConflict
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return;
case|case
literal|"abort"
case|:
name|setAbortOnVersionConflict
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"conflicts may only be \"proceed\" or \"abort\" but was ["
operator|+
name|conflicts
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * The search request that matches the documents to process.      */
DECL|method|getSource
specifier|public
name|SearchRequest
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
comment|/**      * Call refresh on the indexes we've written to after the request ends?      */
DECL|method|isRefresh
specifier|public
name|boolean
name|isRefresh
parameter_list|()
block|{
return|return
name|refresh
return|;
block|}
comment|/**      * Call refresh on the indexes we've written to after the request ends?      */
DECL|method|setRefresh
specifier|public
name|Self
name|setRefresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
return|return
name|self
argument_list|()
return|;
block|}
comment|/**      * Timeout to wait for the shards on to be available for each bulk request?      */
DECL|method|getTimeout
specifier|public
name|TimeValue
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
comment|/**      * Timeout to wait for the shards on to be available for each bulk request?      */
DECL|method|setTimeout
specifier|public
name|Self
name|setTimeout
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
return|return
name|self
argument_list|()
return|;
block|}
comment|/**      * Consistency level for write requests.      */
DECL|method|getConsistency
specifier|public
name|WriteConsistencyLevel
name|getConsistency
parameter_list|()
block|{
return|return
name|consistency
return|;
block|}
comment|/**      * Consistency level for write requests.      */
DECL|method|setConsistency
specifier|public
name|Self
name|setConsistency
parameter_list|(
name|WriteConsistencyLevel
name|consistency
parameter_list|)
block|{
name|this
operator|.
name|consistency
operator|=
name|consistency
expr_stmt|;
return|return
name|self
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createTask
specifier|public
name|Task
name|createTask
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|action
parameter_list|)
block|{
return|return
operator|new
name|BulkByScrollTask
argument_list|(
name|id
argument_list|,
name|type
argument_list|,
name|action
argument_list|,
name|this
operator|::
name|getDescription
argument_list|)
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
name|source
operator|=
operator|new
name|SearchRequest
argument_list|()
expr_stmt|;
name|source
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|abortOnVersionConflict
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
name|refresh
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
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
name|consistency
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
name|source
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|abortOnVersionConflict
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|refresh
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
name|writeByte
argument_list|(
name|consistency
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Append a short description of the search request to a StringBuilder. Used      * to make toString.      */
DECL|method|searchToString
specifier|protected
name|void
name|searchToString
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
if|if
condition|(
name|source
operator|.
name|indices
argument_list|()
operator|!=
literal|null
operator|&&
name|source
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|source
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|"[all indices]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|.
name|types
argument_list|()
operator|!=
literal|null
operator|&&
name|source
operator|.
name|types
argument_list|()
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|source
operator|.
name|types
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

