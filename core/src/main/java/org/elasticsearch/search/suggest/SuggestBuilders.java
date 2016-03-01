begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
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
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|CompletionSuggestionBuilder
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
name|phrase
operator|.
name|PhraseSuggestionBuilder
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
name|term
operator|.
name|TermSuggestionBuilder
import|;
end_import

begin_comment
comment|/**  * A static factory for building suggester lookup queries  */
end_comment

begin_class
DECL|class|SuggestBuilders
specifier|public
specifier|abstract
class|class
name|SuggestBuilders
block|{
comment|/**      * Creates a term suggestion lookup query with the provided<code>name</code>      *      * @return a {@link org.elasticsearch.search.suggest.term.TermSuggestionBuilder}      * instance      */
DECL|method|termSuggestion
specifier|public
specifier|static
name|TermSuggestionBuilder
name|termSuggestion
parameter_list|()
block|{
return|return
operator|new
name|TermSuggestionBuilder
argument_list|()
return|;
block|}
comment|/**      * Creates a phrase suggestion lookup query with the provided<code>name</code>      *      * @return a {@link org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder}      * instance      */
DECL|method|phraseSuggestion
specifier|public
specifier|static
name|PhraseSuggestionBuilder
name|phraseSuggestion
parameter_list|()
block|{
return|return
operator|new
name|PhraseSuggestionBuilder
argument_list|()
return|;
block|}
comment|/**      * Creates a completion suggestion lookup query with the provided<code>name</code>      *      * @return a {@link org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder}      * instance      */
DECL|method|completionSuggestion
specifier|public
specifier|static
name|CompletionSuggestionBuilder
name|completionSuggestion
parameter_list|()
block|{
return|return
operator|new
name|CompletionSuggestionBuilder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

