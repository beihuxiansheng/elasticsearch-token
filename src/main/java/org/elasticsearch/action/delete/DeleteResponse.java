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
name|ActionWriteResponse
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * The response of the delete action.  *  * @see org.elasticsearch.action.delete.DeleteRequest  * @see org.elasticsearch.client.Client#delete(DeleteRequest)  */
end_comment

begin_class
DECL|class|DeleteResponse
specifier|public
class|class
name|DeleteResponse
extends|extends
name|ActionWriteResponse
block|{
DECL|field|index
specifier|private
name|String
name|index
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
DECL|field|found
specifier|private
name|boolean
name|found
decl_stmt|;
DECL|method|DeleteResponse
specifier|public
name|DeleteResponse
parameter_list|()
block|{      }
DECL|method|DeleteResponse
specifier|public
name|DeleteResponse
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
name|long
name|version
parameter_list|,
name|boolean
name|found
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
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|found
operator|=
name|found
expr_stmt|;
block|}
comment|/**      * The index the document was deleted from.      */
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
comment|/**      * The type of the document deleted.      */
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
comment|/**      * The id of the document deleted.      */
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
comment|/**      * The version of the delete operation.      */
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
comment|/**      * Returns<tt>true</tt> if a doc was found to delete.      */
DECL|method|isFound
specifier|public
name|boolean
name|isFound
parameter_list|()
block|{
return|return
name|found
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
name|index
operator|=
name|in
operator|.
name|readString
argument_list|()
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
name|readLong
argument_list|()
expr_stmt|;
name|found
operator|=
name|in
operator|.
name|readBoolean
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
name|out
operator|.
name|writeString
argument_list|(
name|index
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
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

