begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|suggest
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
name|broadcast
operator|.
name|BroadcastOperationRequestBuilder
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
name|search
operator|.
name|suggest
operator|.
name|SuggestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestionBuilder
import|;
end_import

begin_comment
comment|/**  * A suggest action request builder.  */
end_comment

begin_class
DECL|class|SuggestRequestBuilder
specifier|public
class|class
name|SuggestRequestBuilder
extends|extends
name|BroadcastOperationRequestBuilder
argument_list|<
name|SuggestRequest
argument_list|,
name|SuggestResponse
argument_list|,
name|SuggestRequestBuilder
argument_list|>
block|{
DECL|field|suggest
specifier|final
name|SuggestBuilder
name|suggest
init|=
operator|new
name|SuggestBuilder
argument_list|()
decl_stmt|;
DECL|method|SuggestRequestBuilder
specifier|public
name|SuggestRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|SuggestAction
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
name|SuggestRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a definition for suggestions to the request      * @param name the name for the suggestion that will also be used in the response      * @param suggestion the suggestion configuration      */
DECL|method|addSuggestion
specifier|public
name|SuggestRequestBuilder
name|addSuggestion
parameter_list|(
name|String
name|name
parameter_list|,
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|suggestion
parameter_list|)
block|{
name|suggest
operator|.
name|addSuggestion
argument_list|(
name|name
argument_list|,
name|suggestion
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A comma separated list of routing values to control the shards the search will be executed on.      */
DECL|method|setRouting
specifier|public
name|SuggestRequestBuilder
name|setRouting
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSuggestText
specifier|public
name|SuggestRequestBuilder
name|setSuggestText
parameter_list|(
name|String
name|globalText
parameter_list|)
block|{
name|this
operator|.
name|suggest
operator|.
name|setGlobalText
argument_list|(
name|globalText
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the preference to execute the search. Defaults to randomize across shards. Can be set to      *<tt>_local</tt> to prefer local shards,<tt>_primary</tt> to execute only on primary shards,      * _shards:x,y to operate on shards x&amp; y, or a custom value, which guarantees that the same order      * will be used across different requests.      */
DECL|method|setPreference
specifier|public
name|SuggestRequestBuilder
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
comment|/**      * The routing values to control the shards that the search will be executed on.      */
DECL|method|setRouting
specifier|public
name|SuggestRequestBuilder
name|setRouting
parameter_list|(
name|String
modifier|...
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|beforeExecute
specifier|protected
name|SuggestRequest
name|beforeExecute
parameter_list|(
name|SuggestRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|suggest
argument_list|(
name|suggest
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
block|}
end_class

end_unit

