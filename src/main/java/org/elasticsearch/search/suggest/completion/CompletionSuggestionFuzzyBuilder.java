begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|XFuzzySuggester
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
name|unit
operator|.
name|Fuzziness
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CompletionSuggestionFuzzyBuilder
specifier|public
class|class
name|CompletionSuggestionFuzzyBuilder
extends|extends
name|SuggestBuilder
operator|.
name|SuggestionBuilder
argument_list|<
name|CompletionSuggestionFuzzyBuilder
argument_list|>
block|{
DECL|method|CompletionSuggestionFuzzyBuilder
specifier|public
name|CompletionSuggestionFuzzyBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
literal|"completion"
argument_list|)
expr_stmt|;
block|}
DECL|field|fuzziness
specifier|private
name|Fuzziness
name|fuzziness
init|=
name|Fuzziness
operator|.
name|ONE
decl_stmt|;
DECL|field|fuzzyTranspositions
specifier|private
name|boolean
name|fuzzyTranspositions
init|=
name|XFuzzySuggester
operator|.
name|DEFAULT_TRANSPOSITIONS
decl_stmt|;
DECL|field|fuzzyMinLength
specifier|private
name|int
name|fuzzyMinLength
init|=
name|XFuzzySuggester
operator|.
name|DEFAULT_MIN_FUZZY_LENGTH
decl_stmt|;
DECL|field|fuzzyPrefixLength
specifier|private
name|int
name|fuzzyPrefixLength
init|=
name|XFuzzySuggester
operator|.
name|DEFAULT_NON_FUZZY_PREFIX
decl_stmt|;
DECL|field|unicodeAware
specifier|private
name|boolean
name|unicodeAware
init|=
name|XFuzzySuggester
operator|.
name|DEFAULT_UNICODE_AWARE
decl_stmt|;
DECL|method|getFuzziness
specifier|public
name|Fuzziness
name|getFuzziness
parameter_list|()
block|{
return|return
name|fuzziness
return|;
block|}
DECL|method|setFuzziness
specifier|public
name|CompletionSuggestionFuzzyBuilder
name|setFuzziness
parameter_list|(
name|Fuzziness
name|fuzziness
parameter_list|)
block|{
name|this
operator|.
name|fuzziness
operator|=
name|fuzziness
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|isFuzzyTranspositions
specifier|public
name|boolean
name|isFuzzyTranspositions
parameter_list|()
block|{
return|return
name|fuzzyTranspositions
return|;
block|}
DECL|method|setFuzzyTranspositions
specifier|public
name|CompletionSuggestionFuzzyBuilder
name|setFuzzyTranspositions
parameter_list|(
name|boolean
name|fuzzyTranspositions
parameter_list|)
block|{
name|this
operator|.
name|fuzzyTranspositions
operator|=
name|fuzzyTranspositions
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getFuzzyMinLength
specifier|public
name|int
name|getFuzzyMinLength
parameter_list|()
block|{
return|return
name|fuzzyMinLength
return|;
block|}
DECL|method|setFuzzyMinLength
specifier|public
name|CompletionSuggestionFuzzyBuilder
name|setFuzzyMinLength
parameter_list|(
name|int
name|fuzzyMinLength
parameter_list|)
block|{
name|this
operator|.
name|fuzzyMinLength
operator|=
name|fuzzyMinLength
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getFuzzyPrefixLength
specifier|public
name|int
name|getFuzzyPrefixLength
parameter_list|()
block|{
return|return
name|fuzzyPrefixLength
return|;
block|}
DECL|method|setFuzzyPrefixLength
specifier|public
name|CompletionSuggestionFuzzyBuilder
name|setFuzzyPrefixLength
parameter_list|(
name|int
name|fuzzyPrefixLength
parameter_list|)
block|{
name|this
operator|.
name|fuzzyPrefixLength
operator|=
name|fuzzyPrefixLength
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|isUnicodeAware
specifier|public
name|boolean
name|isUnicodeAware
parameter_list|()
block|{
return|return
name|unicodeAware
return|;
block|}
DECL|method|setUnicodeAware
specifier|public
name|CompletionSuggestionFuzzyBuilder
name|setUnicodeAware
parameter_list|(
name|boolean
name|unicodeAware
parameter_list|)
block|{
name|this
operator|.
name|unicodeAware
operator|=
name|unicodeAware
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|innerToXContent
specifier|protected
name|XContentBuilder
name|innerToXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"fuzzy"
argument_list|)
expr_stmt|;
if|if
condition|(
name|fuzziness
operator|!=
name|Fuzziness
operator|.
name|ONE
condition|)
block|{
name|fuzziness
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyTranspositions
operator|!=
name|XFuzzySuggester
operator|.
name|DEFAULT_TRANSPOSITIONS
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"transpositions"
argument_list|,
name|fuzzyTranspositions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyMinLength
operator|!=
name|XFuzzySuggester
operator|.
name|DEFAULT_MIN_FUZZY_LENGTH
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"min_length"
argument_list|,
name|fuzzyMinLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyPrefixLength
operator|!=
name|XFuzzySuggester
operator|.
name|DEFAULT_NON_FUZZY_PREFIX
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"prefix_length"
argument_list|,
name|fuzzyPrefixLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|unicodeAware
operator|!=
name|XFuzzySuggester
operator|.
name|DEFAULT_UNICODE_AWARE
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"unicode_aware"
argument_list|,
name|unicodeAware
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

