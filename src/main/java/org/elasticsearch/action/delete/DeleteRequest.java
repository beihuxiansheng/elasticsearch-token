begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.delete
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
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
name|support
operator|.
name|replication
operator|.
name|ShardReplicationOperationRequest
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
name|lucene
operator|.
name|uid
operator|.
name|Versions
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
name|VersionType
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

begin_comment
comment|/**  * A request to delete a document from an index based on its type and id. Best created using  * {@link org.elasticsearch.client.Requests#deleteRequest(String)}.  *<p/>  *<p>The operation requires the {@link #index()}, {@link #type(String)} and {@link #id(String)} to  * be set.  *  * @see DeleteResponse  * @see org.elasticsearch.client.Client#delete(DeleteRequest)  * @see org.elasticsearch.client.Requests#deleteRequest(String)  */
end_comment

begin_class
DECL|class|DeleteRequest
specifier|public
class|class
name|DeleteRequest
extends|extends
name|ShardReplicationOperationRequest
argument_list|<
name|DeleteRequest
argument_list|>
block|{
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
annotation|@
name|Nullable
DECL|field|routing
specifier|private
name|String
name|routing
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
init|=
name|Versions
operator|.
name|MATCH_ANY
decl_stmt|;
DECL|field|versionType
specifier|private
name|VersionType
name|versionType
init|=
name|VersionType
operator|.
name|INTERNAL
decl_stmt|;
DECL|method|DeleteRequest
specifier|public
name|DeleteRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new delete request against the specified index. The {@link #type(String)} and {@link #id(String)}      * must be set.      */
DECL|method|DeleteRequest
specifier|public
name|DeleteRequest
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
comment|/**      * Constructs a new delete request against the specified index with the type and id.      *      * @param index The index to get the document from      * @param type  The type of the document      * @param id    The id of the document      */
DECL|method|DeleteRequest
specifier|public
name|DeleteRequest
parameter_list|(
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
block|}
comment|/**      * Copy constructor that creates a new delete request that is a copy of the one provided as an argument.      */
DECL|method|DeleteRequest
specifier|public
name|DeleteRequest
parameter_list|(
name|DeleteRequest
name|request
parameter_list|)
block|{
name|this
argument_list|(
name|request
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|/**      * Copy constructor that creates a new delete request that is a copy of the one provided as an argument.      * The new request will inherit though headers and context from the original request that caused it.      */
DECL|method|DeleteRequest
specifier|public
name|DeleteRequest
parameter_list|(
name|DeleteRequest
name|request
parameter_list|,
name|ActionRequest
name|originalRequest
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|originalRequest
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|request
operator|.
name|type
argument_list|()
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|request
operator|.
name|id
argument_list|()
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|request
operator|.
name|routing
argument_list|()
expr_stmt|;
name|this
operator|.
name|refresh
operator|=
name|request
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|request
operator|.
name|version
argument_list|()
expr_stmt|;
name|this
operator|.
name|versionType
operator|=
name|request
operator|.
name|versionType
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a delete request caused by some other request, which is provided as an      * argument so that its headers and context can be copied to the new request      */
DECL|method|DeleteRequest
specifier|public
name|DeleteRequest
parameter_list|(
name|ActionRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"type is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"id is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|versionType
operator|.
name|validateVersionForWrites
argument_list|(
name|version
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"illegal version value ["
operator|+
name|version
operator|+
literal|"] for version type ["
operator|+
name|versionType
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * The type of the document to delete.      */
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
comment|/**      * Sets the type of the document to delete.      */
DECL|method|type
specifier|public
name|DeleteRequest
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The id of the document to delete.      */
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
comment|/**      * Sets the id of the document to delete.      */
DECL|method|id
specifier|public
name|DeleteRequest
name|id
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the parent id of this document. Will simply set the routing to this value, as it is only      * used for routing with delete requests.      */
DECL|method|parent
specifier|public
name|DeleteRequest
name|parent
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
if|if
condition|(
name|routing
operator|==
literal|null
condition|)
block|{
name|routing
operator|=
name|parent
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Controls the shard routing of the request. Using this value to hash the shard      * and not the id.      */
DECL|method|routing
specifier|public
name|DeleteRequest
name|routing
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
if|if
condition|(
name|routing
operator|!=
literal|null
operator|&&
name|routing
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|routing
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Controls the shard routing of the delete request. Using this value to hash the shard      * and not the id.      */
DECL|method|routing
specifier|public
name|String
name|routing
parameter_list|()
block|{
return|return
name|this
operator|.
name|routing
return|;
block|}
comment|/**      * Should a refresh be executed post this index operation causing the operation to      * be searchable. Note, heavy indexing should not set this to<tt>true</tt>. Defaults      * to<tt>false</tt>.      */
DECL|method|refresh
specifier|public
name|DeleteRequest
name|refresh
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
name|this
return|;
block|}
DECL|method|refresh
specifier|public
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|this
operator|.
name|refresh
return|;
block|}
comment|/**      * Sets the version, which will cause the delete operation to only be performed if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|version
specifier|public
name|DeleteRequest
name|version
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|versionType
specifier|public
name|DeleteRequest
name|versionType
parameter_list|(
name|VersionType
name|versionType
parameter_list|)
block|{
name|this
operator|.
name|versionType
operator|=
name|versionType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|versionType
specifier|public
name|VersionType
name|versionType
parameter_list|()
block|{
return|return
name|this
operator|.
name|versionType
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
name|type
operator|=
name|in
operator|.
name|readSharedString
argument_list|()
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|routing
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|refresh
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|version
operator|=
name|Versions
operator|.
name|readVersion
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|versionType
operator|=
name|VersionType
operator|.
name|fromValue
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
name|out
operator|.
name|writeSharedString
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
name|writeOptionalString
argument_list|(
name|routing
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
name|Versions
operator|.
name|writeVersion
argument_list|(
name|version
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|versionType
operator|.
name|getValue
argument_list|()
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
literal|"delete {["
operator|+
name|index
operator|+
literal|"]["
operator|+
name|type
operator|+
literal|"]["
operator|+
name|id
operator|+
literal|"]}"
return|;
block|}
block|}
end_class

end_unit

