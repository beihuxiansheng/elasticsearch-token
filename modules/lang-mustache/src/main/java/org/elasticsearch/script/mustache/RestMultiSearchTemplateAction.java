begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.mustache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mustache
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|node
operator|.
name|NodeClient
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
name|settings
operator|.
name|Settings
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
name|BaseRestHandler
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
name|RestController
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
name|RestRequest
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
name|action
operator|.
name|RestToXContentListener
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
name|action
operator|.
name|search
operator|.
name|RestMultiSearchAction
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
name|action
operator|.
name|search
operator|.
name|RestSearchAction
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|POST
import|;
end_import

begin_class
DECL|class|RestMultiSearchTemplateAction
specifier|public
class|class
name|RestMultiSearchTemplateAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|RESPONSE_PARAMS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|RESPONSE_PARAMS
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|RestSearchAction
operator|.
name|TYPED_KEYS_PARAM
argument_list|)
decl_stmt|;
DECL|field|allowExplicitIndex
specifier|private
specifier|final
name|boolean
name|allowExplicitIndex
decl_stmt|;
DECL|method|RestMultiSearchTemplateAction
specifier|public
name|RestMultiSearchTemplateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowExplicitIndex
operator|=
name|MULTI_ALLOW_EXPLICIT_INDEX
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_msearch/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/_msearch/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_msearch/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_msearch/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/{type}/_msearch/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_msearch/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"multi_search_template_action"
return|;
block|}
annotation|@
name|Override
DECL|method|prepareRequest
specifier|public
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiSearchTemplateRequest
name|multiRequest
init|=
name|parseRequest
argument_list|(
name|request
argument_list|,
name|allowExplicitIndex
argument_list|)
decl_stmt|;
return|return
name|channel
lambda|->
name|client
operator|.
name|execute
argument_list|(
name|MultiSearchTemplateAction
operator|.
name|INSTANCE
argument_list|,
name|multiRequest
argument_list|,
operator|new
name|RestToXContentListener
argument_list|<>
argument_list|(
name|channel
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Parses a {@link RestRequest} body and returns a {@link MultiSearchTemplateRequest}      */
DECL|method|parseRequest
specifier|public
specifier|static
name|MultiSearchTemplateRequest
name|parseRequest
parameter_list|(
name|RestRequest
name|restRequest
parameter_list|,
name|boolean
name|allowExplicitIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiSearchTemplateRequest
name|multiRequest
init|=
operator|new
name|MultiSearchTemplateRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|restRequest
operator|.
name|hasParam
argument_list|(
literal|"max_concurrent_searches"
argument_list|)
condition|)
block|{
name|multiRequest
operator|.
name|maxConcurrentSearchRequests
argument_list|(
name|restRequest
operator|.
name|paramAsInt
argument_list|(
literal|"max_concurrent_searches"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RestMultiSearchAction
operator|.
name|parseMultiLineRequest
argument_list|(
name|restRequest
argument_list|,
name|multiRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|allowExplicitIndex
argument_list|,
parameter_list|(
name|searchRequest
parameter_list|,
name|bytes
parameter_list|)
lambda|->
block|{
try|try
block|{
name|SearchTemplateRequest
name|searchTemplateRequest
init|=
name|RestSearchTemplateAction
operator|.
name|parse
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchTemplateRequest
operator|.
name|getScript
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|searchTemplateRequest
operator|.
name|setRequest
argument_list|(
name|searchRequest
argument_list|)
expr_stmt|;
name|multiRequest
operator|.
name|add
argument_list|(
name|searchTemplateRequest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Malformed search template"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Exception when parsing search template request"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|multiRequest
return|;
block|}
annotation|@
name|Override
DECL|method|supportsContentStream
specifier|public
name|boolean
name|supportsContentStream
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|responseParams
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|responseParams
parameter_list|()
block|{
return|return
name|RESPONSE_PARAMS
return|;
block|}
block|}
end_class

end_unit

