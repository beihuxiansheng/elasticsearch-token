begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|get
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
name|action
operator|.
name|support
operator|.
name|single
operator|.
name|custom
operator|.
name|SingleCustomOperationRequest
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
name|Strings
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

begin_class
DECL|class|GetFieldMappingsIndexRequest
class|class
name|GetFieldMappingsIndexRequest
extends|extends
name|SingleCustomOperationRequest
argument_list|<
name|GetFieldMappingsIndexRequest
argument_list|>
implements|implements
name|IndicesRequest
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|probablySingleFieldRequest
specifier|private
name|boolean
name|probablySingleFieldRequest
decl_stmt|;
DECL|field|includeDefaults
specifier|private
name|boolean
name|includeDefaults
decl_stmt|;
DECL|field|fields
specifier|private
name|String
index|[]
name|fields
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|method|GetFieldMappingsIndexRequest
name|GetFieldMappingsIndexRequest
parameter_list|()
block|{     }
DECL|method|GetFieldMappingsIndexRequest
name|GetFieldMappingsIndexRequest
parameter_list|(
name|GetFieldMappingsRequest
name|other
parameter_list|,
name|String
name|index
parameter_list|,
name|boolean
name|probablySingleFieldRequest
parameter_list|)
block|{
name|this
operator|.
name|preferLocal
argument_list|(
name|other
operator|.
name|local
argument_list|)
expr_stmt|;
name|this
operator|.
name|probablySingleFieldRequest
operator|=
name|probablySingleFieldRequest
expr_stmt|;
name|this
operator|.
name|includeDefaults
operator|=
name|other
operator|.
name|includeDefaults
argument_list|()
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|other
operator|.
name|types
argument_list|()
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|other
operator|.
name|fields
argument_list|()
expr_stmt|;
assert|assert
name|index
operator|!=
literal|null
assert|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
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
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|IndicesOptions
operator|.
name|strictSingleIndexNoExpandForbidClosed
argument_list|()
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
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|types
return|;
block|}
DECL|method|fields
specifier|public
name|String
index|[]
name|fields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
DECL|method|probablySingleFieldRequest
specifier|public
name|boolean
name|probablySingleFieldRequest
parameter_list|()
block|{
return|return
name|probablySingleFieldRequest
return|;
block|}
DECL|method|includeDefaults
specifier|public
name|boolean
name|includeDefaults
parameter_list|()
block|{
return|return
name|includeDefaults
return|;
block|}
comment|/** Indicates whether default mapping settings should be returned */
DECL|method|includeDefaults
specifier|public
name|GetFieldMappingsIndexRequest
name|includeDefaults
parameter_list|(
name|boolean
name|includeDefaults
parameter_list|)
block|{
name|this
operator|.
name|includeDefaults
operator|=
name|includeDefaults
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
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
name|writeStringArray
argument_list|(
name|types
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|includeDefaults
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|probablySingleFieldRequest
argument_list|)
expr_stmt|;
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
name|types
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|fields
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|includeDefaults
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|probablySingleFieldRequest
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

