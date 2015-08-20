begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|queries
operator|.
name|ExtendedCommonTermsQuery
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|FuzzyQuery
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
name|search
operator|.
name|Query
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|support
operator|.
name|QueryParsers
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
name|search
operator|.
name|MatchQuery
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

begin_comment
comment|/**  * Match query is a query that analyzes the text and constructs a query as the result of the analysis. It  * can construct different queries based on the type provided.  */
end_comment

begin_class
DECL|class|MatchQueryBuilder
specifier|public
class|class
name|MatchQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|MatchQueryBuilder
argument_list|>
block|{
comment|/** The default name for the match query */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"match"
decl_stmt|;
comment|/** The default mode terms are combined in a match query */
DECL|field|DEFAULT_OPERATOR
specifier|public
specifier|static
specifier|final
name|Operator
name|DEFAULT_OPERATOR
init|=
name|Operator
operator|.
name|OR
decl_stmt|;
comment|/** The default mode match query type */
DECL|field|DEFAULT_TYPE
specifier|public
specifier|static
specifier|final
name|MatchQuery
operator|.
name|Type
name|DEFAULT_TYPE
init|=
name|MatchQuery
operator|.
name|Type
operator|.
name|BOOLEAN
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
DECL|field|type
specifier|private
name|MatchQuery
operator|.
name|Type
name|type
init|=
name|DEFAULT_TYPE
decl_stmt|;
DECL|field|operator
specifier|private
name|Operator
name|operator
init|=
name|DEFAULT_OPERATOR
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
name|MatchQuery
operator|.
name|DEFAULT_PHRASE_SLOP
decl_stmt|;
DECL|field|fuzziness
specifier|private
name|Fuzziness
name|fuzziness
init|=
literal|null
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
DECL|field|maxExpansions
specifier|private
name|int
name|maxExpansions
init|=
name|FuzzyQuery
operator|.
name|defaultMaxExpansions
decl_stmt|;
DECL|field|fuzzyTranspositions
specifier|private
name|boolean
name|fuzzyTranspositions
init|=
name|FuzzyQuery
operator|.
name|defaultTranspositions
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
DECL|field|fuzzyRewrite
specifier|private
name|String
name|fuzzyRewrite
init|=
literal|null
decl_stmt|;
DECL|field|lenient
specifier|private
name|boolean
name|lenient
init|=
name|MatchQuery
operator|.
name|DEFAULT_LENIENCY
decl_stmt|;
DECL|field|zeroTermsQuery
specifier|private
name|MatchQuery
operator|.
name|ZeroTermsQuery
name|zeroTermsQuery
init|=
name|MatchQuery
operator|.
name|DEFAULT_ZERO_TERMS_QUERY
decl_stmt|;
DECL|field|cutoffFrequency
specifier|private
name|Float
name|cutoffFrequency
init|=
literal|null
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|MatchQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/**      * Constructs a new match query.      */
DECL|method|MatchQueryBuilder
specifier|public
name|MatchQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"] requires fieldName"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"] requires query value"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/** Returns the field name used in this query. */
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
comment|/** Returns the value used in this query. */
DECL|method|value
specifier|public
name|Object
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
comment|/** Sets the type of the text query. */
DECL|method|type
specifier|public
name|MatchQueryBuilder
name|type
parameter_list|(
name|MatchQuery
operator|.
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"] requires type to be non-null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Get the type of the query. */
DECL|method|type
specifier|public
name|MatchQuery
operator|.
name|Type
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
comment|/** Sets the operator to use when using a boolean query. Defaults to<tt>OR</tt>. */
DECL|method|operator
specifier|public
name|MatchQueryBuilder
name|operator
parameter_list|(
name|Operator
name|operator
parameter_list|)
block|{
if|if
condition|(
name|operator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"] requires operator to be non-null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the analyzer to use. Defaults to use explicit mapping config for the field, or, if not      * set, the default search analyzer.      */
DECL|method|analyzer
specifier|public
name|MatchQueryBuilder
name|analyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Get the analyzer to use, if previously set, otherwise<tt>null</tt> */
DECL|method|analyzer
specifier|public
name|String
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
comment|/** Sets a slop factor for phrase queries */
DECL|method|slop
specifier|public
name|MatchQueryBuilder
name|slop
parameter_list|(
name|int
name|slop
parameter_list|)
block|{
if|if
condition|(
name|slop
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No negative slop allowed."
argument_list|)
throw|;
block|}
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Get the slop factor for phrase queries. */
DECL|method|slop
specifier|public
name|int
name|slop
parameter_list|()
block|{
return|return
name|this
operator|.
name|slop
return|;
block|}
comment|/** Sets the fuzziness used when evaluated to a fuzzy query type. Defaults to "AUTO". */
DECL|method|fuzziness
specifier|public
name|MatchQueryBuilder
name|fuzziness
parameter_list|(
name|Object
name|fuzziness
parameter_list|)
block|{
name|this
operator|.
name|fuzziness
operator|=
name|Fuzziness
operator|.
name|build
argument_list|(
name|fuzziness
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**  Gets the fuzziness used when evaluated to a fuzzy query type. */
DECL|method|fuzziness
specifier|public
name|Fuzziness
name|fuzziness
parameter_list|()
block|{
return|return
name|this
operator|.
name|fuzziness
return|;
block|}
comment|/**      * Sets the length of a length of common (non-fuzzy) prefix for fuzzy match queries      * @param prefixLength non-negative length of prefix      * @throws IllegalArgumentException in case the prefix is negative      */
DECL|method|prefixLength
specifier|public
name|MatchQueryBuilder
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
literal|"No negative prefix length allowed."
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
comment|/**      * Gets the length of a length of common (non-fuzzy) prefix for fuzzy match queries      */
DECL|method|prefixLength
specifier|public
name|int
name|prefixLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefixLength
return|;
block|}
comment|/**      * When using fuzzy or prefix type query, the number of term expansions to use. Defaults to unbounded      * so its recommended to set it to a reasonable value for faster execution.      */
DECL|method|maxExpansions
specifier|public
name|MatchQueryBuilder
name|maxExpansions
parameter_list|(
name|int
name|maxExpansions
parameter_list|)
block|{
name|this
operator|.
name|maxExpansions
operator|=
name|maxExpansions
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the (optional) number of term expansions when using fuzzy or prefix type query.      */
DECL|method|maxExpansions
specifier|public
name|int
name|maxExpansions
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxExpansions
return|;
block|}
comment|/**      * Sets an optional cutoff value in [0..1] (or absolute number>=1) representing the      * maximum threshold of a terms document frequency to be considered a low      * frequency term.      */
DECL|method|cutoffFrequency
specifier|public
name|MatchQueryBuilder
name|cutoffFrequency
parameter_list|(
name|float
name|cutoff
parameter_list|)
block|{
name|this
operator|.
name|cutoffFrequency
operator|=
name|cutoff
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Gets the optional cutoff value, can be<tt>null</tt> if not set previously */
DECL|method|cutoffFrequency
specifier|public
name|Float
name|cutoffFrequency
parameter_list|()
block|{
return|return
name|this
operator|.
name|cutoffFrequency
return|;
block|}
comment|/** Sets optional minimumShouldMatch value to apply to the query */
DECL|method|minimumShouldMatch
specifier|public
name|MatchQueryBuilder
name|minimumShouldMatch
parameter_list|(
name|String
name|minimumShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|minimumShouldMatch
operator|=
name|minimumShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Gets the minimumShouldMatch value */
DECL|method|minimumShouldMatch
specifier|public
name|String
name|minimumShouldMatch
parameter_list|()
block|{
return|return
name|this
operator|.
name|minimumShouldMatch
return|;
block|}
comment|/** Sets the fuzzy_rewrite parameter controlling how the fuzzy query will get rewritten */
DECL|method|fuzzyRewrite
specifier|public
name|MatchQueryBuilder
name|fuzzyRewrite
parameter_list|(
name|String
name|fuzzyRewrite
parameter_list|)
block|{
name|this
operator|.
name|fuzzyRewrite
operator|=
name|fuzzyRewrite
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the fuzzy_rewrite parameter      * @see #fuzzyRewrite(String)      */
DECL|method|fuzzyRewrite
specifier|public
name|String
name|fuzzyRewrite
parameter_list|()
block|{
return|return
name|this
operator|.
name|fuzzyRewrite
return|;
block|}
comment|/**      * Sets whether transpositions are supported in fuzzy queries.<p>      * The default metric used by fuzzy queries to determine a match is the Damerau-Levenshtein      * distance formula which supports transpositions. Setting transposition to false will      * switch to classic Levenshtein distance.<br>      * If not set, Damerau-Levenshtein distance metric will be used.      */
DECL|method|fuzzyTranspositions
specifier|public
name|MatchQueryBuilder
name|fuzzyTranspositions
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
comment|/** Gets the fuzzy query transposition setting. */
DECL|method|fuzzyTranspositions
specifier|public
name|boolean
name|fuzzyTranspositions
parameter_list|()
block|{
return|return
name|this
operator|.
name|fuzzyTranspositions
return|;
block|}
comment|/**      * Sets whether format based failures will be ignored.      * @deprecated use #lenient() instead      */
annotation|@
name|Deprecated
DECL|method|setLenient
specifier|public
name|MatchQueryBuilder
name|setLenient
parameter_list|(
name|boolean
name|lenient
parameter_list|)
block|{
return|return
name|lenient
argument_list|(
name|lenient
argument_list|)
return|;
block|}
comment|/**      * Sets whether format based failures will be ignored.      */
DECL|method|lenient
specifier|public
name|MatchQueryBuilder
name|lenient
parameter_list|(
name|boolean
name|lenient
parameter_list|)
block|{
name|this
operator|.
name|lenient
operator|=
name|lenient
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets leniency setting that controls if format based failures will be ignored.      */
DECL|method|lenient
specifier|public
name|boolean
name|lenient
parameter_list|()
block|{
return|return
name|this
operator|.
name|lenient
return|;
block|}
comment|/**      * Sets query to use in case no query terms are available, e.g. after analysis removed them.      * Defaults to {@link MatchQuery.ZeroTermsQuery#NONE}, but can be set to      * {@link MatchQuery.ZeroTermsQuery#ALL} instead.      */
DECL|method|zeroTermsQuery
specifier|public
name|MatchQueryBuilder
name|zeroTermsQuery
parameter_list|(
name|MatchQuery
operator|.
name|ZeroTermsQuery
name|zeroTermsQuery
parameter_list|)
block|{
name|this
operator|.
name|zeroTermsQuery
operator|=
name|zeroTermsQuery
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the setting for handling zero terms queries.      * @see #zeroTermsQuery(ZeroTermsQuery)      */
DECL|method|zeroTermsQuery
specifier|public
name|MatchQuery
operator|.
name|ZeroTermsQuery
name|zeroTermsQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|zeroTermsQuery
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|public
name|void
name|doXContent
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
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"operator"
argument_list|,
name|operator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"slop"
argument_list|,
name|slop
argument_list|)
expr_stmt|;
if|if
condition|(
name|fuzziness
operator|!=
literal|null
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
name|builder
operator|.
name|field
argument_list|(
literal|"prefix_length"
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"max_expansions"
argument_list|,
name|maxExpansions
argument_list|)
expr_stmt|;
if|if
condition|(
name|minimumShouldMatch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"minimum_should_match"
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyRewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fuzzy_rewrite"
argument_list|,
name|fuzzyRewrite
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE 4 UPGRADE we need to document this& test this
name|builder
operator|.
name|field
argument_list|(
literal|"fuzzy_transpositions"
argument_list|,
name|fuzzyTranspositions
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lenient"
argument_list|,
name|lenient
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"zero_terms_query"
argument_list|,
name|zeroTermsQuery
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cutoffFrequency
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"cutoff_frequency"
argument_list|,
name|cutoffFrequency
argument_list|)
expr_stmt|;
block|}
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// validate context specific fields
if|if
condition|(
name|analyzer
operator|!=
literal|null
operator|&&
name|context
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"[match] analyzer ["
operator|+
name|analyzer
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
name|MatchQuery
name|matchQuery
init|=
operator|new
name|MatchQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|setOccur
argument_list|(
name|operator
operator|.
name|toBooleanClauseOccur
argument_list|()
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setPhraseSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setFuzziness
argument_list|(
name|fuzziness
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setFuzzyPrefixLength
argument_list|(
name|prefixLength
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setMaxExpansions
argument_list|(
name|maxExpansions
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setTranspositions
argument_list|(
name|fuzzyTranspositions
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setFuzzyRewriteMethod
argument_list|(
name|QueryParsers
operator|.
name|parseRewriteMethod
argument_list|(
name|context
operator|.
name|parseFieldMatcher
argument_list|()
argument_list|,
name|fuzzyRewrite
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setLenient
argument_list|(
name|lenient
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setCommonTermsCutoff
argument_list|(
name|cutoffFrequency
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setZeroTermsQuery
argument_list|(
name|zeroTermsQuery
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|matchQuery
operator|.
name|parse
argument_list|(
name|type
argument_list|,
name|fieldName
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|applyMinimumShouldMatch
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|query
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ExtendedCommonTermsQuery
condition|)
block|{
operator|(
operator|(
name|ExtendedCommonTermsQuery
operator|)
name|query
operator|)
operator|.
name|setLowFreqMinimumNumberShouldMatch
argument_list|(
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|MatchQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|value
argument_list|,
name|other
operator|.
name|value
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|type
argument_list|,
name|other
operator|.
name|type
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|operator
argument_list|,
name|other
operator|.
name|operator
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|analyzer
argument_list|,
name|other
operator|.
name|analyzer
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|slop
argument_list|,
name|other
operator|.
name|slop
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fuzziness
argument_list|,
name|other
operator|.
name|fuzziness
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
name|maxExpansions
argument_list|,
name|other
operator|.
name|maxExpansions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|minimumShouldMatch
argument_list|,
name|other
operator|.
name|minimumShouldMatch
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fuzzyRewrite
argument_list|,
name|other
operator|.
name|fuzzyRewrite
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lenient
argument_list|,
name|other
operator|.
name|lenient
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fuzzyTranspositions
argument_list|,
name|other
operator|.
name|fuzzyTranspositions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|zeroTermsQuery
argument_list|,
name|other
operator|.
name|zeroTermsQuery
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|cutoffFrequency
argument_list|,
name|other
operator|.
name|cutoffFrequency
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
name|fieldName
argument_list|,
name|value
argument_list|,
name|type
argument_list|,
name|operator
argument_list|,
name|analyzer
argument_list|,
name|slop
argument_list|,
name|fuzziness
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|minimumShouldMatch
argument_list|,
name|fuzzyRewrite
argument_list|,
name|lenient
argument_list|,
name|fuzzyTranspositions
argument_list|,
name|zeroTermsQuery
argument_list|,
name|cutoffFrequency
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|MatchQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|MatchQueryBuilder
name|matchQuery
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readGenericValue
argument_list|()
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|type
operator|=
name|MatchQuery
operator|.
name|Type
operator|.
name|readTypeFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|operator
operator|=
name|Operator
operator|.
name|readOperatorFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|slop
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|prefixLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|maxExpansions
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|fuzzyTranspositions
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|lenient
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|zeroTermsQuery
operator|=
name|MatchQuery
operator|.
name|ZeroTermsQuery
operator|.
name|readZeroTermsQueryFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// optional fields
name|matchQuery
operator|.
name|analyzer
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|minimumShouldMatch
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|matchQuery
operator|.
name|fuzzyRewrite
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|matchQuery
operator|.
name|fuzziness
operator|=
name|Fuzziness
operator|.
name|readFuzzinessFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|matchQuery
operator|.
name|cutoffFrequency
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
block|}
return|return
name|matchQuery
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|type
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|operator
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
name|slop
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
name|maxExpansions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|fuzzyTranspositions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|lenient
argument_list|)
expr_stmt|;
name|zeroTermsQuery
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// optional fields
name|out
operator|.
name|writeOptionalString
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|minimumShouldMatch
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|fuzzyRewrite
argument_list|)
expr_stmt|;
if|if
condition|(
name|fuzziness
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fuzziness
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cutoffFrequency
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|cutoffFrequency
argument_list|)
expr_stmt|;
block|}
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
name|NAME
return|;
block|}
block|}
end_class

end_unit

