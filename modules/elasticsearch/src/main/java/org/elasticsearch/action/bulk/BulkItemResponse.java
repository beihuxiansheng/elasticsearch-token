begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ActionResponse
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
name|DeleteResponse
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
name|IndexResponse
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

begin_comment
comment|/**  * Represents a single item response for an action executed as part of the bulk API. Holds the index/type/id  * of the relevant action, and if it has failed or not (with the failure message incase it failed).  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BulkItemResponse
specifier|public
class|class
name|BulkItemResponse
implements|implements
name|Streamable
block|{
comment|/**      * Represents a failure.      */
DECL|class|Failure
specifier|public
specifier|static
class|class
name|Failure
block|{
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
DECL|method|Failure
specifier|public
name|Failure
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|message
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
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**          * The index name of the action.          */
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
comment|/**          * The index name of the action.          */
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
comment|/**          * The type of the action.          */
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**          * The type of the action.          */
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
argument_list|()
return|;
block|}
comment|/**          * The id of the action.          */
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**          * The id of the action.          */
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/**          * The failure message.          */
DECL|method|message
specifier|public
name|String
name|message
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
comment|/**          * The failure message.          */
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
argument_list|()
return|;
block|}
block|}
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|opType
specifier|private
name|String
name|opType
decl_stmt|;
DECL|field|response
specifier|private
name|ActionResponse
name|response
decl_stmt|;
DECL|field|failure
specifier|private
name|Failure
name|failure
decl_stmt|;
DECL|method|BulkItemResponse
name|BulkItemResponse
parameter_list|()
block|{      }
DECL|method|BulkItemResponse
specifier|public
name|BulkItemResponse
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|opType
parameter_list|,
name|ActionResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|opType
operator|=
name|opType
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
DECL|method|BulkItemResponse
specifier|public
name|BulkItemResponse
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|opType
parameter_list|,
name|Failure
name|failure
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|opType
operator|=
name|opType
expr_stmt|;
name|this
operator|.
name|failure
operator|=
name|failure
expr_stmt|;
block|}
comment|/**      * The numeric order of the item matching the same request order in the bulk request.      */
DECL|method|itemId
specifier|public
name|int
name|itemId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * The operation type ("index", "create" or "delete").      */
DECL|method|opType
specifier|public
name|String
name|opType
parameter_list|()
block|{
return|return
name|this
operator|.
name|opType
return|;
block|}
comment|/**      * The index name of the action.      */
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
return|return
name|failure
operator|.
name|index
argument_list|()
return|;
block|}
if|if
condition|(
name|response
operator|instanceof
name|IndexResponse
condition|)
block|{
return|return
operator|(
operator|(
name|IndexResponse
operator|)
name|response
operator|)
operator|.
name|index
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|response
operator|instanceof
name|DeleteResponse
condition|)
block|{
return|return
operator|(
operator|(
name|DeleteResponse
operator|)
name|response
operator|)
operator|.
name|index
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * The index name of the action.      */
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
comment|/**      * The type of the action.      */
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
return|return
name|failure
operator|.
name|type
argument_list|()
return|;
block|}
if|if
condition|(
name|response
operator|instanceof
name|IndexResponse
condition|)
block|{
return|return
operator|(
operator|(
name|IndexResponse
operator|)
name|response
operator|)
operator|.
name|type
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|response
operator|instanceof
name|DeleteResponse
condition|)
block|{
return|return
operator|(
operator|(
name|DeleteResponse
operator|)
name|response
operator|)
operator|.
name|type
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * The type of the action.      */
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
argument_list|()
return|;
block|}
comment|/**      * The id of the action.      */
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
return|return
name|failure
operator|.
name|id
argument_list|()
return|;
block|}
if|if
condition|(
name|response
operator|instanceof
name|IndexResponse
condition|)
block|{
return|return
operator|(
operator|(
name|IndexResponse
operator|)
name|response
operator|)
operator|.
name|id
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|response
operator|instanceof
name|DeleteResponse
condition|)
block|{
return|return
operator|(
operator|(
name|DeleteResponse
operator|)
name|response
operator|)
operator|.
name|id
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * The id of the action.      */
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
argument_list|()
return|;
block|}
comment|/**      * The version of the action.      */
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|response
operator|instanceof
name|IndexResponse
condition|)
block|{
return|return
operator|(
operator|(
name|IndexResponse
operator|)
name|response
operator|)
operator|.
name|version
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|response
operator|instanceof
name|DeleteResponse
condition|)
block|{
return|return
operator|(
operator|(
name|DeleteResponse
operator|)
name|response
operator|)
operator|.
name|version
argument_list|()
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * The actual response ({@link IndexResponse} or {@link DeleteResponse}).<tt>null</tt> in      * case of failure.      */
DECL|method|response
specifier|public
parameter_list|<
name|T
extends|extends
name|ActionResponse
parameter_list|>
name|T
name|response
parameter_list|()
block|{
return|return
operator|(
name|T
operator|)
name|response
return|;
block|}
comment|/**      * Is this a failed execution of an operation.      */
DECL|method|failed
specifier|public
name|boolean
name|failed
parameter_list|()
block|{
return|return
name|failure
operator|!=
literal|null
return|;
block|}
comment|/**      * Is this a failed execution of an operation.      */
DECL|method|isFailed
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
name|failed
argument_list|()
return|;
block|}
comment|/**      * The failure message,<tt>null</tt> if it did not fail.      */
DECL|method|failureMessage
specifier|public
name|String
name|failureMessage
parameter_list|()
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
return|return
name|failure
operator|.
name|message
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * The failure message,<tt>null</tt> if it did not fail.      */
DECL|method|getFailureMessage
specifier|public
name|String
name|getFailureMessage
parameter_list|()
block|{
return|return
name|failureMessage
argument_list|()
return|;
block|}
comment|/**      * The actual failure object if there was a failure.      */
DECL|method|failure
specifier|public
name|Failure
name|failure
parameter_list|()
block|{
return|return
name|this
operator|.
name|failure
return|;
block|}
comment|/**      * The actual failure object if there was a failure.      */
DECL|method|getFailure
specifier|public
name|Failure
name|getFailure
parameter_list|()
block|{
return|return
name|failure
argument_list|()
return|;
block|}
DECL|method|readBulkItem
specifier|public
specifier|static
name|BulkItemResponse
name|readBulkItem
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BulkItemResponse
name|response
init|=
operator|new
name|BulkItemResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|response
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
name|id
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|opType
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|byte
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
name|response
operator|=
operator|new
name|IndexResponse
argument_list|()
expr_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
name|response
operator|=
operator|new
name|DeleteResponse
argument_list|()
expr_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|failure
operator|=
operator|new
name|Failure
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
argument_list|,
name|in
operator|.
name|readUTF
argument_list|()
argument_list|,
name|in
operator|.
name|readUTF
argument_list|()
argument_list|,
name|in
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
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
name|writeVInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|opType
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|response
operator|instanceof
name|IndexResponse
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|response
operator|instanceof
name|DeleteResponse
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failure
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
name|writeUTF
argument_list|(
name|failure
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|failure
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|failure
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|failure
operator|.
name|message
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

