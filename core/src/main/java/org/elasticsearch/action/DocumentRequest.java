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
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Generic interface to group ActionRequest, which perform writes to a single document  * Action requests implementing this can be part of {@link org.elasticsearch.action.bulk.BulkRequest}  */
end_comment

begin_interface
DECL|interface|DocumentRequest
specifier|public
interface|interface
name|DocumentRequest
parameter_list|<
name|T
parameter_list|>
extends|extends
name|IndicesRequest
block|{
comment|/**      * Get the index that this request operates on      * @return the index      */
DECL|method|index
name|String
name|index
parameter_list|()
function_decl|;
comment|/**      * Get the type that this request operates on      * @return the type      */
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * Get the id of the document for this request      * @return the id      */
DECL|method|id
name|String
name|id
parameter_list|()
function_decl|;
comment|/**      * Get the options for this request      * @return the indices options      */
DECL|method|indicesOptions
name|IndicesOptions
name|indicesOptions
parameter_list|()
function_decl|;
comment|/**      * Set the routing for this request      * @return the Request      */
DECL|method|routing
name|T
name|routing
parameter_list|(
name|String
name|routing
parameter_list|)
function_decl|;
comment|/**      * Get the routing for this request      * @return the Routing      */
DECL|method|routing
name|String
name|routing
parameter_list|()
function_decl|;
comment|/**      * Get the parent for this request      * @return the Parent      */
DECL|method|parent
name|String
name|parent
parameter_list|()
function_decl|;
comment|/**      * Get the document version for this request      * @return the document version      */
DECL|method|version
name|long
name|version
parameter_list|()
function_decl|;
comment|/**      * Sets the version, which will perform the operation only if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|version
name|T
name|version
parameter_list|(
name|long
name|version
parameter_list|)
function_decl|;
comment|/**      * Get the document version type for this request      * @return the document version type      */
DECL|method|versionType
name|VersionType
name|versionType
parameter_list|()
function_decl|;
comment|/**      * Sets the versioning type. Defaults to {@link VersionType#INTERNAL}.      */
DECL|method|versionType
name|T
name|versionType
parameter_list|(
name|VersionType
name|versionType
parameter_list|)
function_decl|;
comment|/**      * Get the requested document operation type of the request      * @return the operation type {@link OpType}      */
DECL|method|opType
name|OpType
name|opType
parameter_list|()
function_decl|;
comment|/**      * Requested operation type to perform on the document      */
DECL|enum|OpType
enum|enum
name|OpType
block|{
comment|/**          * Index the source. If there an existing document with the id, it will          * be replaced.          */
DECL|enum constant|INDEX
name|INDEX
argument_list|(
literal|0
argument_list|)
block|,
comment|/**          * Creates the resource. Simply adds it to the index, if there is an existing          * document with the id, then it won't be removed.          */
DECL|enum constant|CREATE
name|CREATE
argument_list|(
literal|1
argument_list|)
block|,
comment|/** Updates a document */
DECL|enum constant|UPDATE
name|UPDATE
argument_list|(
literal|2
argument_list|)
block|,
comment|/** Deletes a document */
DECL|enum constant|DELETE
name|DELETE
argument_list|(
literal|3
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
DECL|method|OpType
name|OpType
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
name|ROOT
argument_list|)
expr_stmt|;
block|}
DECL|method|getId
specifier|public
name|byte
name|getId
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
DECL|method|fromId
specifier|public
specifier|static
name|OpType
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
switch|switch
condition|(
name|id
condition|)
block|{
case|case
literal|0
case|:
return|return
name|INDEX
return|;
case|case
literal|1
case|:
return|return
name|CREATE
return|;
case|case
literal|2
case|:
return|return
name|UPDATE
return|;
case|case
literal|3
case|:
return|return
name|DELETE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown opType: ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|fromString
specifier|public
specifier|static
name|OpType
name|fromString
parameter_list|(
name|String
name|sOpType
parameter_list|)
block|{
name|String
name|lowerCase
init|=
name|sOpType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
for|for
control|(
name|OpType
name|opType
range|:
name|OpType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|opType
operator|.
name|getLowercase
argument_list|()
operator|.
name|equals
argument_list|(
name|lowerCase
argument_list|)
condition|)
block|{
return|return
name|opType
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown opType: ["
operator|+
name|sOpType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/** read a document write (index/delete/update) request */
DECL|method|readDocumentRequest
specifier|static
name|DocumentRequest
name|readDocumentRequest
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|DocumentRequest
name|documentRequest
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
operator|new
name|IndexRequest
argument_list|()
decl_stmt|;
name|indexRequest
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|documentRequest
operator|=
name|indexRequest
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
name|DeleteRequest
name|deleteRequest
init|=
operator|new
name|DeleteRequest
argument_list|()
decl_stmt|;
name|deleteRequest
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|documentRequest
operator|=
name|deleteRequest
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|2
condition|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|updateRequest
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|documentRequest
operator|=
name|updateRequest
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid request type ["
operator|+
name|type
operator|+
literal|" ]"
argument_list|)
throw|;
block|}
return|return
name|documentRequest
return|;
block|}
comment|/** write a document write (index/delete/update) request*/
DECL|method|writeDocumentRequest
specifier|static
name|void
name|writeDocumentRequest
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|DocumentRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
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
operator|(
operator|(
name|IndexRequest
operator|)
name|request
operator|)
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
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
operator|(
operator|(
name|DeleteRequest
operator|)
name|request
operator|)
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|UpdateRequest
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
operator|(
operator|(
name|UpdateRequest
operator|)
name|request
operator|)
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid request ["
operator|+
name|request
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" ]"
argument_list|)
throw|;
block|}
block|}
block|}
end_interface

end_unit

