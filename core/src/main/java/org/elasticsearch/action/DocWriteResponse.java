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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
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
name|StatusToXContent
block|{
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
name|version
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
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|// needed for deserialization
DECL|method|DocWriteResponse
specifier|protected
name|DocWriteResponse
parameter_list|()
block|{     }
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
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|_INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|_INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_index"
argument_list|)
decl_stmt|;
DECL|field|_TYPE
specifier|static
specifier|final
name|XContentBuilderString
name|_TYPE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_type"
argument_list|)
decl_stmt|;
DECL|field|_ID
specifier|static
specifier|final
name|XContentBuilderString
name|_ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
DECL|field|_VERSION
specifier|static
specifier|final
name|XContentBuilderString
name|_VERSION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_version"
argument_list|)
decl_stmt|;
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
name|Fields
operator|.
name|_INDEX
argument_list|,
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_TYPE
argument_list|,
name|type
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_ID
argument_list|,
name|id
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_VERSION
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|shardInfo
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

