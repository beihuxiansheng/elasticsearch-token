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
name|ActionRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|ElasticsearchClient
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

begin_comment
comment|/**  * A delete document action request builder.  */
end_comment

begin_class
DECL|class|DeleteIndexedScriptRequestBuilder
specifier|public
class|class
name|DeleteIndexedScriptRequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|DeleteIndexedScriptRequest
argument_list|,
name|DeleteIndexedScriptResponse
argument_list|,
name|DeleteIndexedScriptRequestBuilder
argument_list|>
block|{
DECL|method|DeleteIndexedScriptRequestBuilder
specifier|public
name|DeleteIndexedScriptRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|DeleteIndexedScriptAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|DeleteIndexedScriptRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the language of the script to delete.      */
DECL|method|setScriptLang
specifier|public
name|DeleteIndexedScriptRequestBuilder
name|setScriptLang
parameter_list|(
name|String
name|scriptLang
parameter_list|)
block|{
name|request
operator|.
name|scriptLang
argument_list|(
name|scriptLang
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the id of the document to delete.      */
DECL|method|setId
specifier|public
name|DeleteIndexedScriptRequestBuilder
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|request
operator|.
name|id
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type of versioning to use. Defaults to {@link org.elasticsearch.index.VersionType#INTERNAL}.      */
DECL|method|setVersionType
specifier|public
name|DeleteIndexedScriptRequestBuilder
name|setVersionType
parameter_list|(
name|VersionType
name|versionType
parameter_list|)
block|{
name|request
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit
