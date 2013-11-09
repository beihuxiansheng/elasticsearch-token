begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchParseException
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
name|ShardOperationFailedException
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
name|xcontent
operator|.
name|ToXContent
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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

begin_comment
comment|/**  * Stores information about failures that occurred during shard snapshotting process  */
end_comment

begin_class
DECL|class|SnapshotShardFailure
specifier|public
class|class
name|SnapshotShardFailure
implements|implements
name|ShardOperationFailedException
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|reason
specifier|private
name|String
name|reason
decl_stmt|;
annotation|@
name|Nullable
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|status
specifier|private
name|RestStatus
name|status
decl_stmt|;
DECL|method|SnapshotShardFailure
specifier|private
name|SnapshotShardFailure
parameter_list|()
block|{      }
comment|/**      * Constructs new snapshot shard failure object      *      * @param nodeId  node where failure occurred      * @param index   index which the shard belongs to      * @param shardId shard id      * @param reason  failure reason      */
DECL|method|SnapshotShardFailure
specifier|public
name|SnapshotShardFailure
parameter_list|(
annotation|@
name|Nullable
name|String
name|nodeId
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
name|status
operator|=
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
expr_stmt|;
block|}
comment|/**      * Returns index where failure occurred      *      * @return index      */
annotation|@
name|Override
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
comment|/**      * Returns shard id where failure occurred      *      * @return shard id      */
annotation|@
name|Override
DECL|method|shardId
specifier|public
name|int
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
comment|/**      * Returns reason for the failure      *      * @return reason for the failure      */
annotation|@
name|Override
DECL|method|reason
specifier|public
name|String
name|reason
parameter_list|()
block|{
return|return
name|this
operator|.
name|reason
return|;
block|}
comment|/**      * Returns REST status corresponding to this failure      *      * @return REST status      */
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
comment|/**      * Returns node id where failure occurred      *      * @return node id      */
annotation|@
name|Nullable
DECL|method|nodeId
specifier|public
name|String
name|nodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
comment|/**      * Reads shard failure information from stream input      *      * @param in stream input      * @return shard failure information      * @throws IOException      */
DECL|method|readSnapshotShardFailure
specifier|public
specifier|static
name|SnapshotShardFailure
name|readSnapshotShardFailure
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotShardFailure
name|exp
init|=
operator|new
name|SnapshotShardFailure
argument_list|()
decl_stmt|;
name|exp
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|exp
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
name|nodeId
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|index
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|reason
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|status
operator|=
name|RestStatus
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
name|out
operator|.
name|writeOptionalString
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|RestStatus
operator|.
name|writeTo
argument_list|(
name|out
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|index
operator|+
literal|"]["
operator|+
name|shardId
operator|+
literal|"] failed, reason ["
operator|+
name|reason
operator|+
literal|"]"
return|;
block|}
comment|/**      * Serializes snapshot failure information into JSON      *      * @param snapshotShardFailure snapshot failure information      * @param builder              XContent builder      * @param params               additional parameters      * @throws IOException      */
DECL|method|toXContent
specifier|public
specifier|static
name|void
name|toXContent
parameter_list|(
name|SnapshotShardFailure
name|snapshotShardFailure
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|snapshotShardFailure
operator|.
name|nodeId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"node_id"
argument_list|,
name|snapshotShardFailure
operator|.
name|nodeId
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|snapshotShardFailure
operator|.
name|index
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"reason"
argument_list|,
name|snapshotShardFailure
operator|.
name|reason
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"shard_id"
argument_list|,
name|snapshotShardFailure
operator|.
name|shardId
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"status"
argument_list|,
name|snapshotShardFailure
operator|.
name|status
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
comment|/**      * Deserializes snapshot failure information from JSON      *      * @param parser JSON parser      * @return snapshot failure information      * @throws IOException      */
DECL|method|fromXContent
specifier|public
specifier|static
name|SnapshotShardFailure
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotShardFailure
name|snapshotShardFailure
init|=
operator|new
name|SnapshotShardFailure
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|snapshotShardFailure
operator|.
name|index
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"node_id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|snapshotShardFailure
operator|.
name|nodeId
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"reason"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|snapshotShardFailure
operator|.
name|reason
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"shard_id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|snapshotShardFailure
operator|.
name|shardId
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"status"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|snapshotShardFailure
operator|.
name|status
operator|=
name|RestStatus
operator|.
name|valueOf
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"unknown parameter ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"unexpected token  ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"unexpected token  ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|snapshotShardFailure
return|;
block|}
block|}
end_class

end_unit

