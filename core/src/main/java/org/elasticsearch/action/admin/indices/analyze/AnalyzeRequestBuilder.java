begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.analyze
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
name|analyze
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
name|single
operator|.
name|shard
operator|.
name|SingleShardOperationRequestBuilder
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|AnalyzeRequestBuilder
specifier|public
class|class
name|AnalyzeRequestBuilder
extends|extends
name|SingleShardOperationRequestBuilder
argument_list|<
name|AnalyzeRequest
argument_list|,
name|AnalyzeResponse
argument_list|,
name|AnalyzeRequestBuilder
argument_list|>
block|{
DECL|method|AnalyzeRequestBuilder
specifier|public
name|AnalyzeRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|AnalyzeAction
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
name|AnalyzeRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AnalyzeRequestBuilder
specifier|public
name|AnalyzeRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|AnalyzeAction
name|action
parameter_list|,
name|String
name|index
parameter_list|,
name|String
modifier|...
name|text
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|AnalyzeRequest
argument_list|(
name|index
argument_list|)
operator|.
name|text
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the analyzer name to use in order to analyze the text.      *      * @param analyzer The analyzer name.      */
DECL|method|setAnalyzer
specifier|public
name|AnalyzeRequestBuilder
name|setAnalyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|request
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the field that its analyzer will be used to analyze the text. Note, requires an index      * to be set.      */
DECL|method|setField
specifier|public
name|AnalyzeRequestBuilder
name|setField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|request
operator|.
name|field
argument_list|(
name|field
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Instead of setting the analyzer, sets the tokenizer as name that will be used as part of a custom      * analyzer.      */
DECL|method|setTokenizer
specifier|public
name|AnalyzeRequestBuilder
name|setTokenizer
parameter_list|(
name|String
name|tokenizer
parameter_list|)
block|{
name|request
operator|.
name|tokenizer
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Instead of setting the analyzer, sets the tokenizer using custom settings that will be used as part of a custom      * analyzer.      */
DECL|method|setTokenizer
specifier|public
name|AnalyzeRequestBuilder
name|setTokenizer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|tokenizer
parameter_list|)
block|{
name|request
operator|.
name|tokenizer
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add token filter setting that will be used on top of a tokenizer provided.      */
DECL|method|addTokenFilter
specifier|public
name|AnalyzeRequestBuilder
name|addTokenFilter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|tokenFilter
parameter_list|)
block|{
name|request
operator|.
name|addTokenFilter
argument_list|(
name|tokenFilter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a name of token filter that will be used on top of a tokenizer provided.      */
DECL|method|addTokenFilter
specifier|public
name|AnalyzeRequestBuilder
name|addTokenFilter
parameter_list|(
name|String
name|tokenFilter
parameter_list|)
block|{
name|request
operator|.
name|addTokenFilter
argument_list|(
name|tokenFilter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add char filter setting that will be used on top of a tokenizer provided.      */
DECL|method|addCharFilter
specifier|public
name|AnalyzeRequestBuilder
name|addCharFilter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|charFilter
parameter_list|)
block|{
name|request
operator|.
name|addCharFilter
argument_list|(
name|charFilter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a name of char filter that will be used before the tokenizer.      */
DECL|method|addCharFilter
specifier|public
name|AnalyzeRequestBuilder
name|addCharFilter
parameter_list|(
name|String
name|tokenFilter
parameter_list|)
block|{
name|request
operator|.
name|addCharFilter
argument_list|(
name|tokenFilter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets explain      */
DECL|method|setExplain
specifier|public
name|AnalyzeRequestBuilder
name|setExplain
parameter_list|(
name|boolean
name|explain
parameter_list|)
block|{
name|request
operator|.
name|explain
argument_list|(
name|explain
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets attributes that will include results      */
DECL|method|setAttributes
specifier|public
name|AnalyzeRequestBuilder
name|setAttributes
parameter_list|(
name|String
name|attributes
parameter_list|)
block|{
name|request
operator|.
name|attributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets texts to analyze      */
DECL|method|setText
specifier|public
name|AnalyzeRequestBuilder
name|setText
parameter_list|(
name|String
modifier|...
name|texts
parameter_list|)
block|{
name|request
operator|.
name|text
argument_list|(
name|texts
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

