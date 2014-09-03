begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.indexedscripts.delete
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|indexedscripts
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
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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
comment|/**  * A request to delete a script from the script index based on its scriptLang and id. Best created using  *<p/>  *<p>The operation requires the , {@link #scriptLang(String)} and {@link #id(String)} to  * be set.  *  * @see DeleteIndexedScriptResponse  * @see org.elasticsearch.client.Client#deleteIndexedScript(DeleteIndexedScriptRequest)  */
end_comment

begin_class
DECL|class|DeleteIndexedScriptRequest
specifier|public
class|class
name|DeleteIndexedScriptRequest
extends|extends
name|ActionRequest
argument_list|<
name|DeleteIndexedScriptRequest
argument_list|>
implements|implements
name|IndicesRequest
block|{
DECL|field|scriptLang
specifier|private
name|String
name|scriptLang
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
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
DECL|method|DeleteIndexedScriptRequest
specifier|public
name|DeleteIndexedScriptRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new delete request against the specified index with the scriptLang and id.      *      * @param scriptLang  The scriptLang of the document      * @param id    The id of the document      */
DECL|method|DeleteIndexedScriptRequest
specifier|public
name|DeleteIndexedScriptRequest
parameter_list|(
name|String
name|scriptLang
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|scriptLang
operator|=
name|scriptLang
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
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
literal|null
decl_stmt|;
if|if
condition|(
name|scriptLang
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"scriptLang is missing"
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
literal|"] for version scriptLang ["
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
name|ScriptService
operator|.
name|SCRIPT_INDEX
block|}
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
comment|/**      * The scriptLang of the document to delete.      */
DECL|method|scriptLang
specifier|public
name|String
name|scriptLang
parameter_list|()
block|{
return|return
name|scriptLang
return|;
block|}
comment|/**      * Sets the scriptLang of the document to delete.      */
DECL|method|scriptLang
specifier|public
name|DeleteIndexedScriptRequest
name|scriptLang
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|scriptLang
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
name|DeleteIndexedScriptRequest
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
comment|/**      * Sets the version, which will cause the delete operation to only be performed if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|version
specifier|public
name|DeleteIndexedScriptRequest
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
name|DeleteIndexedScriptRequest
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
name|scriptLang
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
name|writeString
argument_list|(
name|scriptLang
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|id
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
name|ScriptService
operator|.
name|SCRIPT_INDEX
operator|+
literal|"]["
operator|+
name|scriptLang
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

