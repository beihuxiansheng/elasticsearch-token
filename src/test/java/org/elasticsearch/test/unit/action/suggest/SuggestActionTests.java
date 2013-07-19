begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.action.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
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
name|search
operator|.
name|SearchPhaseExecutionException
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
name|search
operator|.
name|ShardSearchFailure
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
name|suggest
operator|.
name|SuggestRequestBuilder
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
name|suggest
operator|.
name|SuggestResponse
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
name|search
operator|.
name|suggest
operator|.
name|Suggest
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
operator|.
name|SuggestionBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestSearchTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SuggestActionTests
specifier|public
class|class
name|SuggestActionTests
extends|extends
name|SuggestSearchTests
block|{
DECL|method|searchSuggest
specifier|protected
name|Suggest
name|searchSuggest
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|suggestText
parameter_list|,
name|int
name|expectShardsFailed
parameter_list|,
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
modifier|...
name|suggestions
parameter_list|)
block|{
name|SuggestRequestBuilder
name|builder
init|=
name|client
operator|.
name|prepareSuggest
argument_list|()
decl_stmt|;
if|if
condition|(
name|suggestText
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setSuggestText
argument_list|(
name|suggestText
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|suggestion
range|:
name|suggestions
control|)
block|{
name|builder
operator|.
name|addSuggestion
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
block|}
name|SuggestResponse
name|actionGet
init|=
name|builder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|actionGet
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|actionGet
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectShardsFailed
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectShardsFailed
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SearchPhaseExecutionException
argument_list|(
literal|"suggest"
argument_list|,
literal|"Suggest execution failed"
argument_list|,
operator|new
name|ShardSearchFailure
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
return|return
name|actionGet
operator|.
name|getSuggest
argument_list|()
return|;
block|}
block|}
end_class

end_unit

