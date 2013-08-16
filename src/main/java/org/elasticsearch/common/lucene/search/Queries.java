begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
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
name|*
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
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Queries
specifier|public
class|class
name|Queries
block|{
comment|/* In general we should never us a static query instance and share it.      * In this case the instance is immutable so that's ok.*/
DECL|field|NO_MATCH_QUERY
specifier|public
specifier|final
specifier|static
name|Query
name|NO_MATCH_QUERY
init|=
name|MatchNoDocsQuery
operator|.
name|INSTANCE
decl_stmt|;
DECL|field|MATCH_ALL_DOCS_FILTER
specifier|private
specifier|static
specifier|final
name|Filter
name|MATCH_ALL_DOCS_FILTER
init|=
operator|new
name|MatchAllDocsFilter
argument_list|()
decl_stmt|;
comment|/**      * A match all docs filter. Note, requires no caching!.      */
DECL|field|MATCH_ALL_FILTER
specifier|public
specifier|final
specifier|static
name|Filter
name|MATCH_ALL_FILTER
init|=
operator|new
name|MatchAllDocsFilter
argument_list|()
decl_stmt|;
DECL|field|MATCH_NO_FILTER
specifier|public
specifier|final
specifier|static
name|Filter
name|MATCH_NO_FILTER
init|=
operator|new
name|MatchNoDocsFilter
argument_list|()
decl_stmt|;
DECL|field|disjuncts
specifier|private
specifier|final
specifier|static
name|Field
name|disjuncts
decl_stmt|;
static|static
block|{
name|Field
name|disjunctsX
decl_stmt|;
try|try
block|{
name|disjunctsX
operator|=
name|DisjunctionMaxQuery
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"disjuncts"
argument_list|)
expr_stmt|;
name|disjunctsX
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|disjunctsX
operator|=
literal|null
expr_stmt|;
block|}
name|disjuncts
operator|=
name|disjunctsX
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|disMaxClauses
specifier|public
specifier|static
name|List
argument_list|<
name|Query
argument_list|>
name|disMaxClauses
parameter_list|(
name|DisjunctionMaxQuery
name|query
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|List
argument_list|<
name|Query
argument_list|>
operator|)
name|disjuncts
operator|.
name|get
argument_list|(
name|query
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|newMatchAllQuery
specifier|public
specifier|static
name|Query
name|newMatchAllQuery
parameter_list|()
block|{
comment|// We don't use MatchAllDocsQuery, its slower than the one below ... (much slower)
comment|// NEVER cache this XConstantScore Query it's not immutable and based on #3521
comment|// some code might set a boost on this query.
return|return
operator|new
name|XConstantScoreQuery
argument_list|(
name|MATCH_ALL_DOCS_FILTER
argument_list|)
return|;
block|}
comment|/**      * Optimizes the given query and returns the optimized version of it.      */
DECL|method|optimizeQuery
specifier|public
specifier|static
name|Query
name|optimizeQuery
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
name|q
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|booleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|booleanQuery
operator|.
name|getClauses
argument_list|()
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|BooleanClause
name|clause
init|=
name|clauses
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|clause
operator|.
name|getOccur
argument_list|()
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
condition|)
block|{
name|Query
name|query
init|=
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|booleanQuery
operator|.
name|getBoost
argument_list|()
operator|*
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|optimizeQuery
argument_list|(
name|query
argument_list|)
return|;
block|}
if|if
condition|(
name|clause
operator|.
name|getOccur
argument_list|()
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
operator|&&
name|booleanQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Query
name|query
init|=
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|booleanQuery
operator|.
name|getBoost
argument_list|()
operator|*
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|optimizeQuery
argument_list|(
name|query
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|q
return|;
block|}
DECL|method|isNegativeQuery
specifier|public
specifier|static
name|boolean
name|isNegativeQuery
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|clauses
argument_list|()
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
operator|!
name|clause
operator|.
name|isProhibited
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|fixNegativeQueryIfNeeded
specifier|public
specifier|static
name|Query
name|fixNegativeQueryIfNeeded
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
name|isNegativeQuery
argument_list|(
name|q
argument_list|)
condition|)
block|{
name|BooleanQuery
name|newBq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
operator|.
name|clone
argument_list|()
decl_stmt|;
name|newBq
operator|.
name|add
argument_list|(
name|newMatchAllQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|newBq
return|;
block|}
return|return
name|q
return|;
block|}
DECL|method|isConstantMatchAllQuery
specifier|public
specifier|static
name|boolean
name|isConstantMatchAllQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|XConstantScoreQuery
condition|)
block|{
name|XConstantScoreQuery
name|scoreQuery
init|=
operator|(
name|XConstantScoreQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|scoreQuery
operator|.
name|getFilter
argument_list|()
operator|instanceof
name|MatchAllDocsFilter
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|applyMinimumShouldMatch
specifier|public
specifier|static
name|void
name|applyMinimumShouldMatch
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
annotation|@
name|Nullable
name|String
name|minimumShouldMatch
parameter_list|)
block|{
if|if
condition|(
name|minimumShouldMatch
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|optionalClauses
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|query
operator|.
name|clauses
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getOccur
argument_list|()
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
condition|)
block|{
name|optionalClauses
operator|++
expr_stmt|;
block|}
block|}
name|int
name|msm
init|=
name|calculateMinShouldMatch
argument_list|(
name|optionalClauses
argument_list|,
name|minimumShouldMatch
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|msm
condition|)
block|{
name|query
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|msm
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|spaceAroundLessThanPattern
specifier|private
specifier|static
name|Pattern
name|spaceAroundLessThanPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\s+<\\s*)|(\\s*<\\s+)"
argument_list|)
decl_stmt|;
DECL|field|spacePattern
specifier|private
specifier|static
name|Pattern
name|spacePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
DECL|field|lessThanPattern
specifier|private
specifier|static
name|Pattern
name|lessThanPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<"
argument_list|)
decl_stmt|;
DECL|method|calculateMinShouldMatch
specifier|public
specifier|static
name|int
name|calculateMinShouldMatch
parameter_list|(
name|int
name|optionalClauseCount
parameter_list|,
name|String
name|spec
parameter_list|)
block|{
name|int
name|result
init|=
name|optionalClauseCount
decl_stmt|;
name|spec
operator|=
name|spec
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|-
literal|1
operator|<
name|spec
operator|.
name|indexOf
argument_list|(
literal|"<"
argument_list|)
condition|)
block|{
comment|/* we have conditional spec(s) */
name|spec
operator|=
name|spaceAroundLessThanPattern
operator|.
name|matcher
argument_list|(
name|spec
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|spacePattern
operator|.
name|split
argument_list|(
name|spec
argument_list|)
control|)
block|{
name|String
index|[]
name|parts
init|=
name|lessThanPattern
operator|.
name|split
argument_list|(
name|s
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|upperBound
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|optionalClauseCount
operator|<=
name|upperBound
condition|)
block|{
return|return
name|result
return|;
block|}
else|else
block|{
name|result
operator|=
name|calculateMinShouldMatch
argument_list|(
name|optionalClauseCount
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/* otherwise, simple expresion */
if|if
condition|(
operator|-
literal|1
operator|<
name|spec
operator|.
name|indexOf
argument_list|(
literal|'%'
argument_list|)
condition|)
block|{
comment|/* percentage - assume the % was the last char.  If not, let Integer.parseInt fail. */
name|spec
operator|=
name|spec
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|spec
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|int
name|percent
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|spec
argument_list|)
decl_stmt|;
name|float
name|calc
init|=
operator|(
name|result
operator|*
name|percent
operator|)
operator|*
operator|(
literal|1
operator|/
literal|100f
operator|)
decl_stmt|;
name|result
operator|=
name|calc
operator|<
literal|0
condition|?
name|result
operator|+
operator|(
name|int
operator|)
name|calc
else|:
operator|(
name|int
operator|)
name|calc
expr_stmt|;
block|}
else|else
block|{
name|int
name|calc
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|spec
argument_list|)
decl_stmt|;
name|result
operator|=
name|calc
operator|<
literal|0
condition|?
name|result
operator|+
name|calc
else|:
name|calc
expr_stmt|;
block|}
return|return
operator|(
name|optionalClauseCount
operator|<
name|result
condition|?
name|optionalClauseCount
else|:
operator|(
name|result
operator|<
literal|0
condition|?
literal|0
else|:
name|result
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

