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
name|document
operator|.
name|FuzzyCompletionQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|Operations
import|;
end_import

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
name|common
operator|.
name|ParseField
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
name|ObjectParser
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Fuzzy options for completion suggester  */
end_comment

begin_class
DECL|class|FuzzyOptions
specifier|public
class|class
name|FuzzyOptions
implements|implements
name|ToXContent
implements|,
name|Writeable
argument_list|<
name|FuzzyOptions
argument_list|>
block|{
DECL|field|FUZZY_OPTIONS
specifier|static
specifier|final
name|ParseField
name|FUZZY_OPTIONS
init|=
operator|new
name|ParseField
argument_list|(
literal|"fuzzy"
argument_list|)
decl_stmt|;
DECL|field|TRANSPOSITION_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TRANSPOSITION_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"transpositions"
argument_list|)
decl_stmt|;
DECL|field|MIN_LENGTH_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|MIN_LENGTH_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"min_length"
argument_list|)
decl_stmt|;
DECL|field|PREFIX_LENGTH_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|PREFIX_LENGTH_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"prefix_length"
argument_list|)
decl_stmt|;
DECL|field|UNICODE_AWARE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|UNICODE_AWARE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"unicode_aware"
argument_list|)
decl_stmt|;
DECL|field|MAX_DETERMINIZED_STATES_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|MAX_DETERMINIZED_STATES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"max_determinized_states"
argument_list|)
decl_stmt|;
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|Builder
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|FUZZY_OPTIONS
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|Builder
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|PARSER
operator|.
name|declareInt
argument_list|(
name|Builder
operator|::
name|setFuzzyMinLength
argument_list|,
name|MIN_LENGTH_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|Builder
operator|::
name|setMaxDeterminizedStates
argument_list|,
name|MAX_DETERMINIZED_STATES_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareBoolean
argument_list|(
name|Builder
operator|::
name|setUnicodeAware
argument_list|,
name|UNICODE_AWARE_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|Builder
operator|::
name|setFuzzyPrefixLength
argument_list|,
name|PREFIX_LENGTH_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareBoolean
argument_list|(
name|Builder
operator|::
name|setTranspositions
argument_list|,
name|TRANSPOSITION_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareValue
argument_list|(
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
block|{
try|try
block|{
name|a
operator|.
name|setFuzziness
argument_list|(
name|Fuzziness
operator|.
name|parse
argument_list|(
name|b
argument_list|)
operator|.
name|asDistance
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|,
name|Fuzziness
operator|.
name|FIELD
argument_list|)
expr_stmt|;
block|}
DECL|field|editDistance
specifier|private
name|int
name|editDistance
decl_stmt|;
DECL|field|transpositions
specifier|private
name|boolean
name|transpositions
decl_stmt|;
DECL|field|fuzzyMinLength
specifier|private
name|int
name|fuzzyMinLength
decl_stmt|;
DECL|field|fuzzyPrefixLength
specifier|private
name|int
name|fuzzyPrefixLength
decl_stmt|;
DECL|field|unicodeAware
specifier|private
name|boolean
name|unicodeAware
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
name|int
name|maxDeterminizedStates
decl_stmt|;
DECL|method|FuzzyOptions
specifier|private
name|FuzzyOptions
parameter_list|(
name|int
name|editDistance
parameter_list|,
name|boolean
name|transpositions
parameter_list|,
name|int
name|fuzzyMinLength
parameter_list|,
name|int
name|fuzzyPrefixLength
parameter_list|,
name|boolean
name|unicodeAware
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|this
operator|.
name|editDistance
operator|=
name|editDistance
expr_stmt|;
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
name|this
operator|.
name|fuzzyMinLength
operator|=
name|fuzzyMinLength
expr_stmt|;
name|this
operator|.
name|fuzzyPrefixLength
operator|=
name|fuzzyPrefixLength
expr_stmt|;
name|this
operator|.
name|unicodeAware
operator|=
name|unicodeAware
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|maxDeterminizedStates
expr_stmt|;
block|}
DECL|method|FuzzyOptions
specifier|private
name|FuzzyOptions
parameter_list|()
block|{     }
DECL|method|parse
specifier|static
name|FuzzyOptions
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Returns the maximum number of edits      */
DECL|method|getEditDistance
specifier|public
name|int
name|getEditDistance
parameter_list|()
block|{
return|return
name|editDistance
return|;
block|}
comment|/**      * Returns if transpositions option is set      *      * if transpositions is set, then swapping one character for another counts as one edit instead of two.      */
DECL|method|isTranspositions
specifier|public
name|boolean
name|isTranspositions
parameter_list|()
block|{
return|return
name|transpositions
return|;
block|}
comment|/**      * Returns the length of input prefix after which edits are applied      */
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
comment|/**      * Returns the minimum length of the input prefix required to apply any edits      */
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
comment|/**      * Returns if all measurements (like edit distance, transpositions and lengths) are in unicode code      * points (actual letters) instead of bytes.      */
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
comment|/**      * Returns the maximum automaton states allowed for fuzzy expansion      */
DECL|method|getMaxDeterminizedStates
specifier|public
name|int
name|getMaxDeterminizedStates
parameter_list|()
block|{
return|return
name|maxDeterminizedStates
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FuzzyOptions
name|that
init|=
operator|(
name|FuzzyOptions
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|editDistance
operator|!=
name|that
operator|.
name|editDistance
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|transpositions
operator|!=
name|that
operator|.
name|transpositions
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|fuzzyMinLength
operator|!=
name|that
operator|.
name|fuzzyMinLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|fuzzyPrefixLength
operator|!=
name|that
operator|.
name|fuzzyPrefixLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|unicodeAware
operator|!=
name|that
operator|.
name|unicodeAware
condition|)
return|return
literal|false
return|;
return|return
name|maxDeterminizedStates
operator|==
name|that
operator|.
name|maxDeterminizedStates
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|editDistance
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|transpositions
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fuzzyMinLength
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fuzzyPrefixLength
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|unicodeAware
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|maxDeterminizedStates
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
name|builder
operator|.
name|startObject
argument_list|(
name|FUZZY_OPTIONS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fuzziness
operator|.
name|FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|editDistance
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|TRANSPOSITION_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|transpositions
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|MIN_LENGTH_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|fuzzyMinLength
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|PREFIX_LENGTH_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|fuzzyPrefixLength
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|UNICODE_AWARE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|unicodeAware
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|MAX_DETERMINIZED_STATES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readFuzzyOptions
specifier|public
specifier|static
name|FuzzyOptions
name|readFuzzyOptions
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FuzzyOptions
name|fuzzyOptions
init|=
operator|new
name|FuzzyOptions
argument_list|()
decl_stmt|;
name|fuzzyOptions
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|fuzzyOptions
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|FuzzyOptions
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|transpositions
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|unicodeAware
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|editDistance
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|fuzzyMinLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|fuzzyPrefixLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
return|return
name|this
return|;
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|transpositions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|unicodeAware
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|editDistance
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fuzzyMinLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fuzzyPrefixLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
block|}
comment|/**      * Options for fuzzy queries      */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|editDistance
specifier|private
name|int
name|editDistance
init|=
name|FuzzyCompletionQuery
operator|.
name|DEFAULT_MAX_EDITS
decl_stmt|;
DECL|field|transpositions
specifier|private
name|boolean
name|transpositions
init|=
name|FuzzyCompletionQuery
operator|.
name|DEFAULT_TRANSPOSITIONS
decl_stmt|;
DECL|field|fuzzyMinLength
specifier|private
name|int
name|fuzzyMinLength
init|=
name|FuzzyCompletionQuery
operator|.
name|DEFAULT_MIN_FUZZY_LENGTH
decl_stmt|;
DECL|field|fuzzyPrefixLength
specifier|private
name|int
name|fuzzyPrefixLength
init|=
name|FuzzyCompletionQuery
operator|.
name|DEFAULT_NON_FUZZY_PREFIX
decl_stmt|;
DECL|field|unicodeAware
specifier|private
name|boolean
name|unicodeAware
init|=
name|FuzzyCompletionQuery
operator|.
name|DEFAULT_UNICODE_AWARE
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
name|int
name|maxDeterminizedStates
init|=
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{         }
comment|/**          * Sets the level of fuzziness used to create suggestions using a {@link Fuzziness} instance.          * The default value is {@link Fuzziness#ONE} which allows for an "edit distance" of one.          */
DECL|method|setFuzziness
specifier|public
name|Builder
name|setFuzziness
parameter_list|(
name|int
name|editDistance
parameter_list|)
block|{
if|if
condition|(
name|editDistance
argument_list|<
literal|0
operator|||
name|editDistance
argument_list|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fuzziness must be between 0 and 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|editDistance
operator|=
name|editDistance
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the level of fuzziness used to create suggestions using a {@link Fuzziness} instance.          * The default value is {@link Fuzziness#ONE} which allows for an "edit distance" of one.          */
DECL|method|setFuzziness
specifier|public
name|Builder
name|setFuzziness
parameter_list|(
name|Fuzziness
name|fuzziness
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fuzziness
argument_list|,
literal|"fuzziness must not be null"
argument_list|)
expr_stmt|;
return|return
name|setFuzziness
argument_list|(
name|fuzziness
operator|.
name|asDistance
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Sets if transpositions (swapping one character for another) counts as one character          * change or two.          * Defaults to true, meaning it uses the fuzzier option of counting transpositions as          * a single change.          */
DECL|method|setTranspositions
specifier|public
name|Builder
name|setTranspositions
parameter_list|(
name|boolean
name|transpositions
parameter_list|)
block|{
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the minimum length of input string before fuzzy suggestions are returned, defaulting          * to 3.          */
DECL|method|setFuzzyMinLength
specifier|public
name|Builder
name|setFuzzyMinLength
parameter_list|(
name|int
name|fuzzyMinLength
parameter_list|)
block|{
if|if
condition|(
name|fuzzyMinLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fuzzyMinLength must not be negative"
argument_list|)
throw|;
block|}
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
comment|/**          * Sets the minimum length of the input, which is not checked for fuzzy alternatives, defaults to 1          */
DECL|method|setFuzzyPrefixLength
specifier|public
name|Builder
name|setFuzzyPrefixLength
parameter_list|(
name|int
name|fuzzyPrefixLength
parameter_list|)
block|{
if|if
condition|(
name|fuzzyPrefixLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fuzzyPrefixLength must not be negative"
argument_list|)
throw|;
block|}
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
comment|/**          * Sets the maximum automaton states allowed for the fuzzy expansion          */
DECL|method|setMaxDeterminizedStates
specifier|public
name|Builder
name|setMaxDeterminizedStates
parameter_list|(
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
if|if
condition|(
name|maxDeterminizedStates
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxDeterminizedStates must not be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|maxDeterminizedStates
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Set to true if all measurements (like edit distance, transpositions and lengths) are in unicode          * code points (actual letters) instead of bytes. Default is false.          */
DECL|method|setUnicodeAware
specifier|public
name|Builder
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
DECL|method|build
specifier|public
name|FuzzyOptions
name|build
parameter_list|()
block|{
return|return
operator|new
name|FuzzyOptions
argument_list|(
name|editDistance
argument_list|,
name|transpositions
argument_list|,
name|fuzzyMinLength
argument_list|,
name|fuzzyPrefixLength
argument_list|,
name|unicodeAware
argument_list|,
name|maxDeterminizedStates
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

