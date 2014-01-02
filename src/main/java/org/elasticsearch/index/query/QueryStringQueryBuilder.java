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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectFloatOpenHashMap
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
name|List
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_comment
comment|/**  * A query that parses a query string and runs it. There are two modes that this operates. The first,  * when no field is added (using {@link #field(String)}, will run the query once and non prefixed fields  * will use the {@link #defaultField(String)} set. The second, when one or more fields are added  * (using {@link #field(String)}), will run the parsed query against the provided fields, and combine  * them either using DisMax or a plain boolean query (see {@link #useDisMax(boolean)}).  *<p/>  */
end_comment

begin_class
DECL|class|QueryStringQueryBuilder
specifier|public
class|class
name|QueryStringQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|QueryStringQueryBuilder
argument_list|>
block|{
DECL|enum|Operator
specifier|public
specifier|static
enum|enum
name|Operator
block|{
DECL|enum constant|OR
name|OR
block|,
DECL|enum constant|AND
name|AND
block|}
DECL|field|queryString
specifier|private
specifier|final
name|String
name|queryString
decl_stmt|;
DECL|field|defaultField
specifier|private
name|String
name|defaultField
decl_stmt|;
DECL|field|defaultOperator
specifier|private
name|Operator
name|defaultOperator
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
DECL|field|quoteAnalyzer
specifier|private
name|String
name|quoteAnalyzer
decl_stmt|;
DECL|field|quoteFieldSuffix
specifier|private
name|String
name|quoteFieldSuffix
decl_stmt|;
DECL|field|autoGeneratePhraseQueries
specifier|private
name|Boolean
name|autoGeneratePhraseQueries
decl_stmt|;
DECL|field|allowLeadingWildcard
specifier|private
name|Boolean
name|allowLeadingWildcard
decl_stmt|;
DECL|field|lowercaseExpandedTerms
specifier|private
name|Boolean
name|lowercaseExpandedTerms
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|Boolean
name|enablePositionIncrements
decl_stmt|;
DECL|field|analyzeWildcard
specifier|private
name|Boolean
name|analyzeWildcard
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fuzziness
specifier|private
name|Fuzziness
name|fuzziness
decl_stmt|;
DECL|field|fuzzyPrefixLength
specifier|private
name|int
name|fuzzyPrefixLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fuzzyMaxExpansions
specifier|private
name|int
name|fuzzyMaxExpansions
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fuzzyRewrite
specifier|private
name|String
name|fuzzyRewrite
decl_stmt|;
DECL|field|phraseSlop
specifier|private
name|int
name|phraseSlop
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fields
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|fieldsBoosts
specifier|private
name|ObjectFloatOpenHashMap
argument_list|<
name|String
argument_list|>
name|fieldsBoosts
decl_stmt|;
DECL|field|useDisMax
specifier|private
name|Boolean
name|useDisMax
decl_stmt|;
DECL|field|tieBreaker
specifier|private
name|float
name|tieBreaker
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|rewrite
specifier|private
name|String
name|rewrite
init|=
literal|null
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
DECL|field|lenient
specifier|private
name|Boolean
name|lenient
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|method|QueryStringQueryBuilder
specifier|public
name|QueryStringQueryBuilder
parameter_list|(
name|String
name|queryString
parameter_list|)
block|{
name|this
operator|.
name|queryString
operator|=
name|queryString
expr_stmt|;
block|}
comment|/**      * The default field to run against when no prefix field is specified. Only relevant when      * not explicitly adding fields the query string will run against.      */
DECL|method|defaultField
specifier|public
name|QueryStringQueryBuilder
name|defaultField
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|this
operator|.
name|defaultField
operator|=
name|defaultField
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to run the query string against.      */
DECL|method|field
specifier|public
name|QueryStringQueryBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to run the query string against with a specific boost.      */
DECL|method|field
specifier|public
name|QueryStringQueryBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsBoosts
operator|==
literal|null
condition|)
block|{
name|fieldsBoosts
operator|=
operator|new
name|ObjectFloatOpenHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fieldsBoosts
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|boost
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * When more than one field is used with the query string, should queries be combined using      * dis max, or boolean query. Defaults to dis max (<tt>true</tt>).      */
DECL|method|useDisMax
specifier|public
name|QueryStringQueryBuilder
name|useDisMax
parameter_list|(
name|boolean
name|useDisMax
parameter_list|)
block|{
name|this
operator|.
name|useDisMax
operator|=
name|useDisMax
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * When more than one field is used with the query string, and combined queries are using      * dis max, control the tie breaker for it.      */
DECL|method|tieBreaker
specifier|public
name|QueryStringQueryBuilder
name|tieBreaker
parameter_list|(
name|float
name|tieBreaker
parameter_list|)
block|{
name|this
operator|.
name|tieBreaker
operator|=
name|tieBreaker
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the boolean operator of the query parser used to parse the query string.      *<p/>      *<p>In default mode ({@link FieldQueryBuilder.Operator#OR}) terms without any modifiers      * are considered optional: for example<code>capital of Hungary</code> is equal to      *<code>capital OR of OR Hungary</code>.      *<p/>      *<p>In {@link FieldQueryBuilder.Operator#AND} mode terms are considered to be in conjunction: the      * above mentioned query is parsed as<code>capital AND of AND Hungary</code>      */
DECL|method|defaultOperator
specifier|public
name|QueryStringQueryBuilder
name|defaultOperator
parameter_list|(
name|Operator
name|defaultOperator
parameter_list|)
block|{
name|this
operator|.
name|defaultOperator
operator|=
name|defaultOperator
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The optional analyzer used to analyze the query string. Note, if a field has search analyzer      * defined for it, then it will be used automatically. Defaults to the smart search analyzer.      */
DECL|method|analyzer
specifier|public
name|QueryStringQueryBuilder
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
comment|/**      * The optional analyzer used to analyze the query string for phrase searches. Note, if a field has search (quote) analyzer      * defined for it, then it will be used automatically. Defaults to the smart search analyzer.      */
DECL|method|quoteAnalyzer
specifier|public
name|QueryStringQueryBuilder
name|quoteAnalyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|quoteAnalyzer
operator|=
name|analyzer
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set to true if phrase queries will be automatically generated      * when the analyzer returns more than one term from whitespace      * delimited text.      * NOTE: this behavior may not be suitable for all languages.      *<p/>      * Set to false if phrase queries should only be generated when      * surrounded by double quotes.      */
DECL|method|autoGeneratePhraseQueries
specifier|public
name|QueryStringQueryBuilder
name|autoGeneratePhraseQueries
parameter_list|(
name|boolean
name|autoGeneratePhraseQueries
parameter_list|)
block|{
name|this
operator|.
name|autoGeneratePhraseQueries
operator|=
name|autoGeneratePhraseQueries
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should leading wildcards be allowed or not. Defaults to<tt>true</tt>.      */
DECL|method|allowLeadingWildcard
specifier|public
name|QueryStringQueryBuilder
name|allowLeadingWildcard
parameter_list|(
name|boolean
name|allowLeadingWildcard
parameter_list|)
block|{
name|this
operator|.
name|allowLeadingWildcard
operator|=
name|allowLeadingWildcard
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Whether terms of wildcard, prefix, fuzzy and range queries are to be automatically      * lower-cased or not.  Default is<tt>true</tt>.      */
DECL|method|lowercaseExpandedTerms
specifier|public
name|QueryStringQueryBuilder
name|lowercaseExpandedTerms
parameter_list|(
name|boolean
name|lowercaseExpandedTerms
parameter_list|)
block|{
name|this
operator|.
name|lowercaseExpandedTerms
operator|=
name|lowercaseExpandedTerms
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set to<tt>true</tt> to enable position increments in result query. Defaults to      *<tt>true</tt>.      *<p/>      *<p>When set, result phrase and multi-phrase queries will be aware of position increments.      * Useful when e.g. a StopFilter increases the position increment of the token that follows an omitted token.      */
DECL|method|enablePositionIncrements
specifier|public
name|QueryStringQueryBuilder
name|enablePositionIncrements
parameter_list|(
name|boolean
name|enablePositionIncrements
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the edit distance for fuzzy queries. Default is "AUTO".      */
DECL|method|fuzziness
specifier|public
name|QueryStringQueryBuilder
name|fuzziness
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
comment|/**      * Set the minimum prefix length for fuzzy queries. Default is 1.      */
DECL|method|fuzzyPrefixLength
specifier|public
name|QueryStringQueryBuilder
name|fuzzyPrefixLength
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
DECL|method|fuzzyMaxExpansions
specifier|public
name|QueryStringQueryBuilder
name|fuzzyMaxExpansions
parameter_list|(
name|int
name|fuzzyMaxExpansions
parameter_list|)
block|{
name|this
operator|.
name|fuzzyMaxExpansions
operator|=
name|fuzzyMaxExpansions
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fuzzyRewrite
specifier|public
name|QueryStringQueryBuilder
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
comment|/**      * Sets the default slop for phrases.  If zero, then exact phrase matches      * are required. Default value is zero.      */
DECL|method|phraseSlop
specifier|public
name|QueryStringQueryBuilder
name|phraseSlop
parameter_list|(
name|int
name|phraseSlop
parameter_list|)
block|{
name|this
operator|.
name|phraseSlop
operator|=
name|phraseSlop
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set to<tt>true</tt> to enable analysis on wildcard and prefix queries.      */
DECL|method|analyzeWildcard
specifier|public
name|QueryStringQueryBuilder
name|analyzeWildcard
parameter_list|(
name|boolean
name|analyzeWildcard
parameter_list|)
block|{
name|this
operator|.
name|analyzeWildcard
operator|=
name|analyzeWildcard
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|rewrite
specifier|public
name|QueryStringQueryBuilder
name|rewrite
parameter_list|(
name|String
name|rewrite
parameter_list|)
block|{
name|this
operator|.
name|rewrite
operator|=
name|rewrite
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minimumShouldMatch
specifier|public
name|QueryStringQueryBuilder
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
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|QueryStringQueryBuilder
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional field name suffix to automatically try and add to the field searched when using quoted text.      */
DECL|method|quoteFieldSuffix
specifier|public
name|QueryStringQueryBuilder
name|quoteFieldSuffix
parameter_list|(
name|String
name|quoteFieldSuffix
parameter_list|)
block|{
name|this
operator|.
name|quoteFieldSuffix
operator|=
name|quoteFieldSuffix
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the query string parser to be lenient when parsing field values, defaults to the index      * setting and if not set, defaults to false.      */
DECL|method|lenient
specifier|public
name|QueryStringQueryBuilder
name|lenient
parameter_list|(
name|Boolean
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
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|QueryStringQueryBuilder
name|queryName
parameter_list|(
name|String
name|queryName
parameter_list|)
block|{
name|this
operator|.
name|queryName
operator|=
name|queryName
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
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
name|QueryStringQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultField
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"default_field"
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|fieldsBoosts
operator|!=
literal|null
operator|&&
name|fieldsBoosts
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|field
operator|+=
literal|"^"
operator|+
name|fieldsBoosts
operator|.
name|get
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|value
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|useDisMax
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"use_dis_max"
argument_list|,
name|useDisMax
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tieBreaker
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"tie_breaker"
argument_list|,
name|tieBreaker
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|defaultOperator
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"default_operator"
argument_list|,
name|defaultOperator
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|quoteAnalyzer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"quote_analyzer"
argument_list|,
name|quoteAnalyzer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|autoGeneratePhraseQueries
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"auto_generate_phrase_queries"
argument_list|,
name|autoGeneratePhraseQueries
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allowLeadingWildcard
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"allow_leading_wildcard"
argument_list|,
name|allowLeadingWildcard
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lowercaseExpandedTerms
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"lowercase_expanded_terms"
argument_list|,
name|lowercaseExpandedTerms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enablePositionIncrements
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enable_position_increments"
argument_list|,
name|enablePositionIncrements
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|boost
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyPrefixLength
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fuzzy_prefix_length"
argument_list|,
name|fuzzyPrefixLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyMaxExpansions
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fuzzy_max_expansions"
argument_list|,
name|fuzzyMaxExpansions
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
if|if
condition|(
name|phraseSlop
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"phrase_slop"
argument_list|,
name|phraseSlop
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|analyzeWildcard
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"analyze_wildcard"
argument_list|,
name|analyzeWildcard
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"rewrite"
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
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
name|quoteFieldSuffix
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"quote_field_suffix"
argument_list|,
name|quoteFieldSuffix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lenient
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"lenient"
argument_list|,
name|lenient
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|queryName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

