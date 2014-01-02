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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Arrays
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

begin_comment
comment|/**  * Same as {@link MatchQueryBuilder} but supports multiple fields.  */
end_comment

begin_class
DECL|class|MultiMatchQueryBuilder
specifier|public
class|class
name|MultiMatchQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|MultiMatchQueryBuilder
argument_list|>
block|{
DECL|field|text
specifier|private
specifier|final
name|Object
name|text
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
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
DECL|field|type
specifier|private
name|MatchQueryBuilder
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|operator
specifier|private
name|MatchQueryBuilder
operator|.
name|Operator
name|operator
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
DECL|field|boost
specifier|private
name|Float
name|boost
decl_stmt|;
DECL|field|slop
specifier|private
name|Integer
name|slop
decl_stmt|;
DECL|field|fuzziness
specifier|private
name|Fuzziness
name|fuzziness
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|Integer
name|prefixLength
decl_stmt|;
DECL|field|maxExpansions
specifier|private
name|Integer
name|maxExpansions
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
DECL|field|rewrite
specifier|private
name|String
name|rewrite
init|=
literal|null
decl_stmt|;
DECL|field|fuzzyRewrite
specifier|private
name|String
name|fuzzyRewrite
init|=
literal|null
decl_stmt|;
DECL|field|useDisMax
specifier|private
name|Boolean
name|useDisMax
decl_stmt|;
DECL|field|tieBreaker
specifier|private
name|Float
name|tieBreaker
decl_stmt|;
DECL|field|lenient
specifier|private
name|Boolean
name|lenient
decl_stmt|;
DECL|field|cutoffFrequency
specifier|private
name|Float
name|cutoffFrequency
init|=
literal|null
decl_stmt|;
DECL|field|zeroTermsQuery
specifier|private
name|MatchQueryBuilder
operator|.
name|ZeroTermsQuery
name|zeroTermsQuery
init|=
literal|null
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
comment|/**      * Constructs a new text query.      */
DECL|method|MultiMatchQueryBuilder
specifier|public
name|MultiMatchQueryBuilder
parameter_list|(
name|Object
name|text
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|fields
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
comment|/**      * Adds a field to run the multi match against.      */
DECL|method|field
specifier|public
name|MultiMatchQueryBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
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
comment|/**      * Adds a field to run the multi match against with a specific boost.      */
DECL|method|field
specifier|public
name|MultiMatchQueryBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
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
comment|/**      * Sets the type of the text query.      */
DECL|method|type
specifier|public
name|MultiMatchQueryBuilder
name|type
parameter_list|(
name|MatchQueryBuilder
operator|.
name|Type
name|type
parameter_list|)
block|{
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
comment|/**      * Sets the operator to use when using a boolean query. Defaults to<tt>OR</tt>.      */
DECL|method|operator
specifier|public
name|MultiMatchQueryBuilder
name|operator
parameter_list|(
name|MatchQueryBuilder
operator|.
name|Operator
name|operator
parameter_list|)
block|{
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
name|MultiMatchQueryBuilder
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
comment|/**      * Set the boost to apply to the query.      */
DECL|method|boost
specifier|public
name|MultiMatchQueryBuilder
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
comment|/**      * Set the phrase slop if evaluated to a phrase query type.      */
DECL|method|slop
specifier|public
name|MultiMatchQueryBuilder
name|slop
parameter_list|(
name|int
name|slop
parameter_list|)
block|{
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
comment|/**      * Sets the fuzziness used when evaluated to a fuzzy query type. Defaults to "AUTO".      */
DECL|method|fuzziness
specifier|public
name|MultiMatchQueryBuilder
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
DECL|method|prefixLength
specifier|public
name|MultiMatchQueryBuilder
name|prefixLength
parameter_list|(
name|int
name|prefixLength
parameter_list|)
block|{
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
comment|/**      * When using fuzzy or prefix type query, the number of term expansions to use. Defaults to unbounded      * so its recommended to set it to a reasonable value for faster execution.      */
DECL|method|maxExpansions
specifier|public
name|MultiMatchQueryBuilder
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
DECL|method|minimumShouldMatch
specifier|public
name|MultiMatchQueryBuilder
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
DECL|method|rewrite
specifier|public
name|MultiMatchQueryBuilder
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
DECL|method|fuzzyRewrite
specifier|public
name|MultiMatchQueryBuilder
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
DECL|method|useDisMax
specifier|public
name|MultiMatchQueryBuilder
name|useDisMax
parameter_list|(
name|Boolean
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
DECL|method|tieBreaker
specifier|public
name|MultiMatchQueryBuilder
name|tieBreaker
parameter_list|(
name|Float
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
comment|/**      * Sets whether format based failures will be ignored.      */
DECL|method|lenient
specifier|public
name|MultiMatchQueryBuilder
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
comment|/**      * Set a cutoff value in [0..1] (or absolute number>=1) representing the      * maximum threshold of a terms document frequency to be considered a low      * frequency term.      */
DECL|method|cutoffFrequency
specifier|public
name|MultiMatchQueryBuilder
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
DECL|method|zeroTermsQuery
specifier|public
name|MultiMatchQueryBuilder
name|zeroTermsQuery
parameter_list|(
name|MatchQueryBuilder
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
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|MultiMatchQueryBuilder
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
name|MultiMatchQueryParser
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
name|text
argument_list|)
expr_stmt|;
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
name|lget
argument_list|()
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
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|operator
operator|!=
literal|null
condition|)
block|{
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
name|boost
operator|!=
literal|null
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
name|slop
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"slop"
argument_list|,
name|slop
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
name|maxExpansions
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"max_expansions"
argument_list|,
name|maxExpansions
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
literal|null
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
if|if
condition|(
name|zeroTermsQuery
operator|!=
literal|null
condition|)
block|{
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

