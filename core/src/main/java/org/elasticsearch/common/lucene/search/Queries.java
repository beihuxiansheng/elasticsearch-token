begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|Term
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
name|*
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
name|BooleanClause
operator|.
name|Occur
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
name|BytesRef
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|internal
operator|.
name|TypeFieldMapper
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
DECL|method|newMatchAllQuery
specifier|public
specifier|static
name|Query
name|newMatchAllQuery
parameter_list|()
block|{
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
comment|/** Return a query that matches no document. */
DECL|method|newMatchNoDocsQuery
specifier|public
specifier|static
name|Query
name|newMatchNoDocsQuery
parameter_list|()
block|{
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|newNestedFilter
specifier|public
specifier|static
name|Query
name|newNestedFilter
parameter_list|()
block|{
return|return
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"__"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newNonNestedFilter
specifier|public
specifier|static
name|Query
name|newNonNestedFilter
parameter_list|()
block|{
return|return
name|not
argument_list|(
name|newNestedFilter
argument_list|()
argument_list|)
return|;
block|}
DECL|method|filtered
specifier|public
specifier|static
name|BooleanQuery
name|filtered
parameter_list|(
annotation|@
name|Nullable
name|Query
name|query
parameter_list|,
annotation|@
name|Nullable
name|Query
name|filter
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|filter
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Return a query that matches all documents but those that match the given query. */
DECL|method|not
specifier|public
specifier|static
name|Query
name|not
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|isNegativeQuery
specifier|private
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
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDisableCoord
argument_list|(
name|bq
operator|.
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|builder
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
name|builder
operator|.
name|build
argument_list|()
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
name|ConstantScoreQuery
condition|)
block|{
return|return
name|isConstantMatchAllQuery
argument_list|(
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|MatchAllDocsQuery
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|applyMinimumShouldMatch
specifier|public
specifier|static
name|Query
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
return|return
name|query
return|;
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
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDisableCoord
argument_list|(
name|query
operator|.
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|query
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|msm
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|getBoost
argument_list|()
operator|!=
literal|1f
condition|)
block|{
return|return
operator|new
name|BoostQuery
argument_list|(
name|bq
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
return|return
name|bq
return|;
block|}
else|else
block|{
return|return
name|query
return|;
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
comment|/* otherwise, simple expression */
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

