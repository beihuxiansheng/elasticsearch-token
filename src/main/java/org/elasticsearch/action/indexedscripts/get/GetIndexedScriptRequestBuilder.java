begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.indexedscripts.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|indexedscripts
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
name|ActionListener
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
name|Client
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
name|index
operator|.
name|VersionType
import|;
end_import

begin_comment
comment|/**  * A get document action request builder.  */
end_comment

begin_class
DECL|class|GetIndexedScriptRequestBuilder
specifier|public
class|class
name|GetIndexedScriptRequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|GetIndexedScriptRequest
argument_list|,
name|GetIndexedScriptResponse
argument_list|,
name|GetIndexedScriptRequestBuilder
argument_list|,
name|Client
argument_list|>
block|{
DECL|method|GetIndexedScriptRequestBuilder
specifier|public
name|GetIndexedScriptRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|GetIndexedScriptRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the type of the document to fetch. If set to<tt>null</tt>, will use just the id to fetch the      * first document matching it.      */
DECL|method|setScriptLang
specifier|public
name|GetIndexedScriptRequestBuilder
name|setScriptLang
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|)
block|{
name|request
operator|.
name|scriptLang
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the id of the document to fetch.      */
DECL|method|setId
specifier|public
name|GetIndexedScriptRequestBuilder
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
comment|/**      * Sets the preference to execute the search. Defaults to randomize across shards. Can be set to      *<tt>_local</tt> to prefer local shards,<tt>_primary</tt> to execute only on primary shards, or      * a custom value, which guarantees that the same order will be used across different requests.      */
DECL|method|setPreference
specifier|public
name|GetIndexedScriptRequestBuilder
name|setPreference
parameter_list|(
name|String
name|preference
parameter_list|)
block|{
name|request
operator|.
name|preference
argument_list|(
name|preference
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should a refresh be executed before this get operation causing the operation to      * return the latest value. Note, heavy get should not set this to<tt>true</tt>. Defaults      * to<tt>false</tt>.      */
DECL|method|setRefresh
specifier|public
name|GetIndexedScriptRequestBuilder
name|setRefresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|request
operator|.
name|refresh
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRealtime
specifier|public
name|GetIndexedScriptRequestBuilder
name|setRealtime
parameter_list|(
name|Boolean
name|realtime
parameter_list|)
block|{
name|request
operator|.
name|realtime
argument_list|(
name|realtime
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the version, which will cause the get operation to only be performed if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|setVersion
specifier|public
name|GetIndexedScriptRequestBuilder
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|request
operator|.
name|version
argument_list|(
name|version
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the versioning type. Defaults to {@link org.elasticsearch.index.VersionType#INTERNAL}.      */
DECL|method|setVersionType
specifier|public
name|GetIndexedScriptRequestBuilder
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
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|GetIndexedScriptResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|getIndexedScript
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

