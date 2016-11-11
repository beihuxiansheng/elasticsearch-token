begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|support
operator|.
name|WriteRequest
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
name|WriteRequest
operator|.
name|RefreshPolicy
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
name|WriteResponse
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
name|ReplicationResponse
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|StatusToXContent
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
name|index
operator|.
name|IndexSettings
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
name|seqno
operator|.
name|SequenceNumbersService
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * A base class for the response of a write operation that involves a single doc  */
end_comment

begin_class
DECL|class|DocWriteResponse
specifier|public
specifier|abstract
class|class
name|DocWriteResponse
extends|extends
name|ReplicationResponse
implements|implements
name|WriteResponse
implements|,
name|StatusToXContent
block|{
comment|/**      * An enum that represents the the results of CRUD operations, primarily used to communicate the type of      * operation that occurred.      */
DECL|enum|Result
specifier|public
enum|enum
name|Result
implements|implements
name|Writeable
block|{
DECL|enum constant|CREATED
name|CREATED
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|UPDATED
name|UPDATED
argument_list|(
literal|1
argument_list|)
block|,
DECL|enum constant|DELETED
name|DELETED
argument_list|(
literal|2
argument_list|)
block|,
DECL|enum constant|NOT_FOUND
name|NOT_FOUND
argument_list|(
literal|3
argument_list|)
block|,
DECL|enum constant|NOOP
name|NOOP
argument_list|(
literal|4
argument_list|)
block|;
DECL|field|op
specifier|private
specifier|final
name|byte
name|op
decl_stmt|;
DECL|field|lowercase
specifier|private
specifier|final
name|String
name|lowercase
decl_stmt|;
DECL|method|Result
name|Result
parameter_list|(
name|int
name|op
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
operator|(
name|byte
operator|)
name|op
expr_stmt|;
name|this
operator|.
name|lowercase
operator|=
name|this
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
DECL|method|getOp
specifier|public
name|byte
name|getOp
parameter_list|()
block|{
return|return
name|op
return|;
block|}
DECL|method|getLowercase
specifier|public
name|String
name|getLowercase
parameter_list|()
block|{
return|return
name|lowercase
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|Result
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Byte
name|opcode
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|opcode
condition|)
block|{
case|case
literal|0
case|:
return|return
name|CREATED
return|;
case|case
literal|1
case|:
return|return
name|UPDATED
return|;
case|case
literal|2
case|:
return|return
name|DELETED
return|;
case|case
literal|3
case|:
return|return
name|NOT_FOUND
return|;
case|case
literal|4
case|:
return|return
name|NOOP
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown result code: "
operator|+
name|opcode
argument_list|)
throw|;
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
name|out
operator|.
name|writeByte
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|field|seqNo
specifier|private
name|long
name|seqNo
decl_stmt|;
DECL|field|forcedRefresh
specifier|private
name|boolean
name|forcedRefresh
decl_stmt|;
DECL|field|result
specifier|protected
name|Result
name|result
decl_stmt|;
DECL|method|DocWriteResponse
specifier|public
name|DocWriteResponse
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|seqNo
parameter_list|,
name|long
name|version
parameter_list|,
name|Result
name|result
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
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
name|seqNo
operator|=
name|seqNo
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|// needed for deserialization
DECL|method|DocWriteResponse
specifier|protected
name|DocWriteResponse
parameter_list|()
block|{     }
comment|/**      * The change that occurred to the document.      */
DECL|method|getResult
specifier|public
name|Result
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
comment|/**      * The index the document was changed in.      */
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
operator|.
name|getIndexName
argument_list|()
return|;
block|}
comment|/**      * The exact shard the document was changed in.      */
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
comment|/**      * The type of the document changed.      */
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
return|;
block|}
comment|/**      * The id of the document changed.      */
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
comment|/**      * Returns the current version of the doc.      */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
comment|/**      * Returns the sequence number assigned for this change. Returns {@link SequenceNumbersService#UNASSIGNED_SEQ_NO} if the operation      * wasn't performed (i.e., an update operation that resulted in a NOOP).      */
DECL|method|getSeqNo
specifier|public
name|long
name|getSeqNo
parameter_list|()
block|{
return|return
name|seqNo
return|;
block|}
comment|/**      * Did this request force a refresh? Requests that set {@link WriteRequest#setRefreshPolicy(RefreshPolicy)} to      * {@link RefreshPolicy#IMMEDIATE} will always return true for this. Requests that set it to {@link RefreshPolicy#WAIT_UNTIL} will      * only return true here if they run out of refresh listener slots (see {@link IndexSettings#MAX_REFRESH_LISTENERS_PER_SHARD}).      */
DECL|method|forcedRefresh
specifier|public
name|boolean
name|forcedRefresh
parameter_list|()
block|{
return|return
name|forcedRefresh
return|;
block|}
annotation|@
name|Override
DECL|method|setForcedRefresh
specifier|public
name|void
name|setForcedRefresh
parameter_list|(
name|boolean
name|forcedRefresh
parameter_list|)
block|{
name|this
operator|.
name|forcedRefresh
operator|=
name|forcedRefresh
expr_stmt|;
block|}
comment|/** returns the rest status for this response (based on {@link ShardInfo#status()} */
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|getShardInfo
argument_list|()
operator|.
name|status
argument_list|()
return|;
block|}
comment|/**      * Gets the location of the written document as a string suitable for a {@code Location} header.      * @param routing any routing used in the request. If null the location doesn't include routing information.      */
DECL|method|getLocation
specifier|public
name|String
name|getLocation
parameter_list|(
annotation|@
name|Nullable
name|String
name|routing
parameter_list|)
block|{
comment|// Absolute path for the location of the document. This should be allowed as of HTTP/1.1:
comment|// https://tools.ietf.org/html/rfc7231#section-7.1.2
name|String
name|index
init|=
name|getIndex
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|getType
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|getId
argument_list|()
decl_stmt|;
name|String
name|routingStart
init|=
literal|"?routing="
decl_stmt|;
name|int
name|bufferSize
init|=
literal|3
operator|+
name|index
operator|.
name|length
argument_list|()
operator|+
name|type
operator|.
name|length
argument_list|()
operator|+
name|id
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|bufferSize
operator|+=
name|routingStart
operator|.
name|length
argument_list|()
operator|+
name|routing
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|StringBuilder
name|location
init|=
operator|new
name|StringBuilder
argument_list|(
name|bufferSize
argument_list|)
decl_stmt|;
name|location
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|location
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|location
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|location
operator|.
name|append
argument_list|(
name|routingStart
argument_list|)
operator|.
name|append
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
return|return
name|location
operator|.
name|toString
argument_list|()
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
name|shardId
operator|=
name|ShardId
operator|.
name|readShardId
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|type
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|version
operator|=
name|in
operator|.
name|readZLong
argument_list|()
expr_stmt|;
name|seqNo
operator|=
name|in
operator|.
name|readZLong
argument_list|()
expr_stmt|;
name|forcedRefresh
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|result
operator|=
name|Result
operator|.
name|readFrom
argument_list|(
name|in
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
name|shardId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeZLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeZLong
argument_list|(
name|seqNo
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|forcedRefresh
argument_list|)
expr_stmt|;
name|result
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|ReplicationResponse
operator|.
name|ShardInfo
name|shardInfo
init|=
name|getShardInfo
argument_list|()
decl_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"_index"
argument_list|,
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"_type"
argument_list|,
name|type
argument_list|)
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"_version"
argument_list|,
name|version
argument_list|)
operator|.
name|field
argument_list|(
literal|"result"
argument_list|,
name|getResult
argument_list|()
operator|.
name|getLowercase
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|forcedRefresh
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"forced_refresh"
argument_list|,
name|forcedRefresh
argument_list|)
expr_stmt|;
block|}
name|shardInfo
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// nocommit i'm not sure we want to expose it in the api but it will be handy for debugging while we work... remove this
name|builder
operator|.
name|field
argument_list|(
literal|"_shard_id"
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSeqNo
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_seq_no"
argument_list|,
name|getSeqNo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

