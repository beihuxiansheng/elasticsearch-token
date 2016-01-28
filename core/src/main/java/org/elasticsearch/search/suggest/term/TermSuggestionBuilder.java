begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.term
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|term
package|;
end_package

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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_ACCURACY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_MAX_EDITS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_MAX_INSPECTIONS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_MAX_TERM_FREQ
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_MIN_DOC_FREQ
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_MIN_WORD_LENGTH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
operator|.
name|DEFAULT_PREFIX_LENGTH
import|;
end_import

begin_comment
comment|/**  * Defines the actual suggest command. Each command uses the global options  * unless defined in the suggestion itself. All options are the same as the  * global options, but are only applicable for this suggestion.  */
end_comment

begin_class
DECL|class|TermSuggestionBuilder
specifier|public
class|class
name|TermSuggestionBuilder
extends|extends
name|SuggestionBuilder
argument_list|<
name|TermSuggestionBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|public
specifier|static
specifier|final
name|TermSuggestionBuilder
name|PROTOTYPE
init|=
operator|new
name|TermSuggestionBuilder
argument_list|(
literal|"_na_"
argument_list|)
decl_stmt|;
comment|// name doesn't matter
DECL|field|SUGGESTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SUGGESTION_NAME
init|=
literal|"term"
decl_stmt|;
DECL|field|suggestMode
specifier|private
name|SuggestMode
name|suggestMode
init|=
name|SuggestMode
operator|.
name|MISSING
decl_stmt|;
DECL|field|accuracy
specifier|private
name|Float
name|accuracy
init|=
name|DEFAULT_ACCURACY
decl_stmt|;
DECL|field|sort
specifier|private
name|SortBy
name|sort
init|=
name|SortBy
operator|.
name|SCORE
decl_stmt|;
DECL|field|stringDistance
specifier|private
name|StringDistanceImpl
name|stringDistance
init|=
name|StringDistanceImpl
operator|.
name|INTERNAL
decl_stmt|;
DECL|field|maxEdits
specifier|private
name|Integer
name|maxEdits
init|=
name|DEFAULT_MAX_EDITS
decl_stmt|;
DECL|field|maxInspections
specifier|private
name|Integer
name|maxInspections
init|=
name|DEFAULT_MAX_INSPECTIONS
decl_stmt|;
DECL|field|maxTermFreq
specifier|private
name|Float
name|maxTermFreq
init|=
name|DEFAULT_MAX_TERM_FREQ
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|Integer
name|prefixLength
init|=
name|DEFAULT_PREFIX_LENGTH
decl_stmt|;
DECL|field|minWordLength
specifier|private
name|Integer
name|minWordLength
init|=
name|DEFAULT_MIN_WORD_LENGTH
decl_stmt|;
DECL|field|minDocFreq
specifier|private
name|Float
name|minDocFreq
init|=
name|DEFAULT_MIN_DOC_FREQ
decl_stmt|;
comment|/**      * @param name      *            The name of this suggestion. This is a required parameter.      */
DECL|method|TermSuggestionBuilder
specifier|public
name|TermSuggestionBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * The global suggest mode controls what suggested terms are included or      * controls for what suggest text tokens, terms should be suggested for.      * Three possible values can be specified:      *<ol>      *<li><code>missing</code> - Only suggest terms in the suggest text that      * aren't in the index. This is the default.      *<li><code>popular</code> - Only suggest terms that occur in more docs      * then the original suggest text term.      *<li><code>always</code> - Suggest any matching suggest terms based on      * tokens in the suggest text.      *</ol>      */
DECL|method|suggestMode
specifier|public
name|TermSuggestionBuilder
name|suggestMode
parameter_list|(
name|SuggestMode
name|suggestMode
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|suggestMode
argument_list|,
literal|"suggestMode must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|suggestMode
operator|=
name|suggestMode
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the suggest mode setting.      */
DECL|method|suggestMode
specifier|public
name|SuggestMode
name|suggestMode
parameter_list|()
block|{
return|return
name|suggestMode
return|;
block|}
comment|/**      * s how similar the suggested terms at least need to be compared to the      * original suggest text tokens. A value between 0 and 1 can be specified.      * This value will be compared to the string distance result of each      * candidate spelling correction.      *<p>      * Default is<tt>0.5</tt>      */
DECL|method|accuracy
specifier|public
name|TermSuggestionBuilder
name|accuracy
parameter_list|(
name|float
name|accuracy
parameter_list|)
block|{
if|if
condition|(
name|accuracy
argument_list|<
literal|0.0f
operator|||
name|accuracy
argument_list|>
literal|1.0f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"accuracy must be between 0 and 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|accuracy
operator|=
name|accuracy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the accuracy setting.      */
DECL|method|accuracy
specifier|public
name|Float
name|accuracy
parameter_list|()
block|{
return|return
name|accuracy
return|;
block|}
comment|/**      * Sets how to sort the suggest terms per suggest text token. Two possible      * values:      *<ol>      *<li><code>score</code> - Sort should first be based on score, then      * document frequency and then the term itself.      *<li><code>frequency</code> - Sort should first be based on document      * frequency, then score and then the term itself.      *</ol>      *<p>      * What the score is depends on the suggester being used.      */
DECL|method|sort
specifier|public
name|TermSuggestionBuilder
name|sort
parameter_list|(
name|SortBy
name|sort
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sort
argument_list|,
literal|"sort must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the sort setting.      */
DECL|method|sort
specifier|public
name|SortBy
name|sort
parameter_list|()
block|{
return|return
name|sort
return|;
block|}
comment|/**      * Sets what string distance implementation to use for comparing how similar      * suggested terms are. Five possible values can be specified:      *<ol>      *<li><code>internal</code> - This is the default and is based on      *<code>damerau_levenshtein</code>, but highly optimized for comparing      * string distance for terms inside the index.      *<li><code>damerau_levenshtein</code> - String distance algorithm based on      * Damerau-Levenshtein algorithm.      *<li><code>levenstein</code> - String distance algorithm based on      * Levenstein edit distance algorithm.      *<li><code>jarowinkler</code> - String distance algorithm based on      * Jaro-Winkler algorithm.      *<li><code>ngram</code> - String distance algorithm based on character      * n-grams.      *</ol>      */
DECL|method|stringDistance
specifier|public
name|TermSuggestionBuilder
name|stringDistance
parameter_list|(
name|StringDistanceImpl
name|stringDistance
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|stringDistance
argument_list|,
literal|"stringDistance must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|stringDistance
operator|=
name|stringDistance
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the string distance implementation setting.      */
DECL|method|stringDistance
specifier|public
name|StringDistanceImpl
name|stringDistance
parameter_list|()
block|{
return|return
name|stringDistance
return|;
block|}
comment|/**      * Sets the maximum edit distance candidate suggestions can have in order to      * be considered as a suggestion. Can only be a value between 1 and 2. Any      * other value result in an bad request error being thrown. Defaults to      *<tt>2</tt>.      */
DECL|method|maxEdits
specifier|public
name|TermSuggestionBuilder
name|maxEdits
parameter_list|(
name|int
name|maxEdits
parameter_list|)
block|{
if|if
condition|(
name|maxEdits
argument_list|<
literal|1
operator|||
name|maxEdits
argument_list|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxEdits must be between 1 and 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxEdits
operator|=
name|maxEdits
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the maximum edit distance setting.      */
DECL|method|maxEdits
specifier|public
name|Integer
name|maxEdits
parameter_list|()
block|{
return|return
name|maxEdits
return|;
block|}
comment|/**      * A factor that is used to multiply with the size in order to inspect more      * candidate suggestions. Can improve accuracy at the cost of performance.      * Defaults to<tt>5</tt>.      */
DECL|method|maxInspections
specifier|public
name|TermSuggestionBuilder
name|maxInspections
parameter_list|(
name|int
name|maxInspections
parameter_list|)
block|{
if|if
condition|(
name|maxInspections
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxInspections must be positive"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxInspections
operator|=
name|maxInspections
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the factor for inspecting more candidate suggestions setting.      */
DECL|method|maxInspections
specifier|public
name|Integer
name|maxInspections
parameter_list|()
block|{
return|return
name|maxInspections
return|;
block|}
comment|/**      * Sets a maximum threshold in number of documents a suggest text token can      * exist in order to be corrected. Can be a relative percentage number (e.g      * 0.4) or an absolute number to represent document frequencies. If an value      * higher than 1 is specified then fractional can not be specified. Defaults      * to<tt>0.01</tt>.      *<p>      * This can be used to exclude high frequency terms from being suggested.      * High frequency terms are usually spelled correctly on top of this this      * also improves the suggest performance.      */
DECL|method|maxTermFreq
specifier|public
name|TermSuggestionBuilder
name|maxTermFreq
parameter_list|(
name|float
name|maxTermFreq
parameter_list|)
block|{
if|if
condition|(
name|maxTermFreq
operator|<
literal|0.0f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTermFreq must be positive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxTermFreq
operator|>
literal|1.0f
operator|&&
name|maxTermFreq
operator|!=
name|Math
operator|.
name|floor
argument_list|(
name|maxTermFreq
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"if maxTermFreq is greater than 1, it must not be a fraction"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxTermFreq
operator|=
name|maxTermFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the maximum term frequency threshold setting.      */
DECL|method|maxTermFreq
specifier|public
name|Float
name|maxTermFreq
parameter_list|()
block|{
return|return
name|maxTermFreq
return|;
block|}
comment|/**      * Sets the number of minimal prefix characters that must match in order be      * a candidate suggestion. Defaults to 1. Increasing this number improves      * suggest performance. Usually misspellings don't occur in the beginning of      * terms.      */
DECL|method|prefixLength
specifier|public
name|TermSuggestionBuilder
name|prefixLength
parameter_list|(
name|int
name|prefixLength
parameter_list|)
block|{
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength must be positive"
argument_list|)
throw|;
block|}
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the minimum prefix length that must match setting.      */
DECL|method|prefixLength
specifier|public
name|Integer
name|prefixLength
parameter_list|()
block|{
return|return
name|prefixLength
return|;
block|}
comment|/**      * The minimum length a suggest text term must have in order to be      * corrected. Defaults to<tt>4</tt>.      */
DECL|method|minWordLength
specifier|public
name|TermSuggestionBuilder
name|minWordLength
parameter_list|(
name|int
name|minWordLength
parameter_list|)
block|{
if|if
condition|(
name|minWordLength
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minWordLength must be greater or equal to 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minWordLength
operator|=
name|minWordLength
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the minimum length of a text term to be corrected setting.      */
DECL|method|minWordLength
specifier|public
name|Integer
name|minWordLength
parameter_list|()
block|{
return|return
name|minWordLength
return|;
block|}
comment|/**      * Sets a minimal threshold in number of documents a suggested term should      * appear in. This can be specified as an absolute number or as a relative      * percentage of number of documents. This can improve quality by only      * suggesting high frequency terms. Defaults to 0f and is not enabled. If a      * value higher than 1 is specified then the number cannot be fractional.      */
DECL|method|minDocFreq
specifier|public
name|TermSuggestionBuilder
name|minDocFreq
parameter_list|(
name|float
name|minDocFreq
parameter_list|)
block|{
if|if
condition|(
name|minDocFreq
operator|<
literal|0.0f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minDocFreq must be positive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minDocFreq
operator|>
literal|1.0f
operator|&&
name|minDocFreq
operator|!=
name|Math
operator|.
name|floor
argument_list|(
name|minDocFreq
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"if minDocFreq is greater than 1, it must not be a fraction"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minDocFreq
operator|=
name|minDocFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the minimal threshold for the frequency of a term appearing in the      * document set setting.      */
DECL|method|minDocFreq
specifier|public
name|Float
name|minDocFreq
parameter_list|()
block|{
return|return
name|minDocFreq
return|;
block|}
annotation|@
name|Override
DECL|method|innerToXContent
specifier|public
name|XContentBuilder
name|innerToXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|suggestMode
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"suggest_mode"
argument_list|,
name|suggestMode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|accuracy
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"accuracy"
argument_list|,
name|accuracy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"sort"
argument_list|,
name|sort
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stringDistance
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"string_distance"
argument_list|,
name|stringDistance
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxEdits
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"max_edits"
argument_list|,
name|maxEdits
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxInspections
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"max_inspections"
argument_list|,
name|maxInspections
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxTermFreq
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"max_term_freq"
argument_list|,
name|maxTermFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefixLength
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"prefix_length"
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minWordLength
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"min_word_length"
argument_list|,
name|minWordLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minDocFreq
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"min_doc_freq"
argument_list|,
name|minDocFreq
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|innerFromXContent
specifier|protected
name|TermSuggestionBuilder
name|innerFromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|SUGGESTION_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|public
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|suggestMode
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|accuracy
argument_list|)
expr_stmt|;
name|sort
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|stringDistance
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxEdits
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxInspections
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|maxTermFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|prefixLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|minWordLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|minDocFreq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|public
name|TermSuggestionBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|TermSuggestionBuilder
name|builder
init|=
operator|new
name|TermSuggestionBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|builder
operator|.
name|suggestMode
operator|=
name|SuggestMode
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|builder
operator|.
name|accuracy
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|builder
operator|.
name|sort
operator|=
name|SortBy
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|builder
operator|.
name|stringDistance
operator|=
name|StringDistanceImpl
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|builder
operator|.
name|maxEdits
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|builder
operator|.
name|maxInspections
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|builder
operator|.
name|maxTermFreq
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|builder
operator|.
name|prefixLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|builder
operator|.
name|minWordLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|builder
operator|.
name|minDocFreq
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|TermSuggestionBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|suggestMode
argument_list|,
name|other
operator|.
name|suggestMode
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|accuracy
argument_list|,
name|other
operator|.
name|accuracy
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|sort
argument_list|,
name|other
operator|.
name|sort
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|stringDistance
argument_list|,
name|other
operator|.
name|stringDistance
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxEdits
argument_list|,
name|other
operator|.
name|maxEdits
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxInspections
argument_list|,
name|other
operator|.
name|maxInspections
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxTermFreq
argument_list|,
name|other
operator|.
name|maxTermFreq
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|prefixLength
argument_list|,
name|other
operator|.
name|prefixLength
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|minWordLength
argument_list|,
name|other
operator|.
name|minWordLength
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|minDocFreq
argument_list|,
name|other
operator|.
name|minDocFreq
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|suggestMode
argument_list|,
name|accuracy
argument_list|,
name|sort
argument_list|,
name|stringDistance
argument_list|,
name|maxEdits
argument_list|,
name|maxInspections
argument_list|,
name|maxTermFreq
argument_list|,
name|prefixLength
argument_list|,
name|minWordLength
argument_list|,
name|minDocFreq
argument_list|)
return|;
block|}
comment|/** An enum representing the valid suggest modes. */
DECL|enum|SuggestMode
specifier|public
enum|enum
name|SuggestMode
implements|implements
name|Writeable
argument_list|<
name|SuggestMode
argument_list|>
block|{
comment|/** Only suggest terms in the suggest text that aren't in the index. This is the default. */
DECL|enum constant|MISSING
name|MISSING
block|,
comment|/** Only suggest terms that occur in more docs then the original suggest text term. */
DECL|enum constant|POPULAR
name|POPULAR
block|,
comment|/** Suggest any matching suggest terms based on tokens in the suggest text. */
DECL|enum constant|ALWAYS
name|ALWAYS
block|;
DECL|field|PROTOTYPE
specifier|protected
specifier|static
name|SuggestMode
name|PROTOTYPE
init|=
name|MISSING
decl_stmt|;
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|SuggestMode
name|readFrom
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown SuggestMode ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|SuggestMode
name|fromString
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|str
argument_list|,
literal|"Input string is null"
argument_list|)
expr_stmt|;
return|return
name|valueOf
argument_list|(
name|str
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** An enum representing the valid sorting options */
DECL|enum|SortBy
specifier|public
enum|enum
name|SortBy
implements|implements
name|Writeable
argument_list|<
name|SortBy
argument_list|>
block|{
comment|/** Sort should first be based on score, then document frequency and then the term itself. */
DECL|enum constant|SCORE
name|SCORE
block|,
comment|/** Sort should first be based on document frequency, then score and then the term itself. */
DECL|enum constant|FREQUENCY
name|FREQUENCY
block|;
DECL|field|PROTOTYPE
specifier|protected
specifier|static
name|SortBy
name|PROTOTYPE
init|=
name|SCORE
decl_stmt|;
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|SortBy
name|readFrom
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown SortBy ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|SortBy
name|fromString
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|str
argument_list|,
literal|"Input string is null"
argument_list|)
expr_stmt|;
return|return
name|valueOf
argument_list|(
name|str
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** An enum representing the valid string edit distance algorithms for determining suggestions. */
DECL|enum|StringDistanceImpl
specifier|public
enum|enum
name|StringDistanceImpl
implements|implements
name|Writeable
argument_list|<
name|StringDistanceImpl
argument_list|>
block|{
comment|/** This is the default and is based on<code>damerau_levenshtein</code>, but highly optimized          * for comparing string distance for terms inside the index. */
DECL|enum constant|INTERNAL
name|INTERNAL
block|,
comment|/** String distance algorithm based on Damerau-Levenshtein algorithm. */
DECL|enum constant|DAMERAU_LEVENSHTEIN
name|DAMERAU_LEVENSHTEIN
block|,
comment|/** String distance algorithm based on Levenstein edit distance algorithm. */
DECL|enum constant|LEVENSTEIN
name|LEVENSTEIN
block|,
comment|/** String distance algorithm based on Jaro-Winkler algorithm. */
DECL|enum constant|JAROWINKLER
name|JAROWINKLER
block|,
comment|/** String distance algorithm based on character n-grams. */
DECL|enum constant|NGRAM
name|NGRAM
block|;
DECL|field|PROTOTYPE
specifier|protected
specifier|static
name|StringDistanceImpl
name|PROTOTYPE
init|=
name|INTERNAL
decl_stmt|;
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|StringDistanceImpl
name|readFrom
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown StringDistanceImpl ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|StringDistanceImpl
name|fromString
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|str
argument_list|,
literal|"Input string is null"
argument_list|)
expr_stmt|;
return|return
name|valueOf
argument_list|(
name|str
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

