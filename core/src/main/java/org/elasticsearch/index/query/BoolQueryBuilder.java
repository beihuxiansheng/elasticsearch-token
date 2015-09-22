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
name|search
operator|.
name|BooleanClause
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
name|MatchAllDocsQuery
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
name|ArrayList
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
name|Objects
import|;
end_import

begin_import
import|import static
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
operator|.
name|fixNegativeQueryIfNeeded
import|;
end_import

begin_comment
comment|/**  * A Query that matches documents matching boolean combinations of other queries.  */
end_comment

begin_class
DECL|class|BoolQueryBuilder
specifier|public
class|class
name|BoolQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|BoolQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"bool"
decl_stmt|;
DECL|field|ADJUST_PURE_NEGATIVE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|ADJUST_PURE_NEGATIVE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DISABLE_COORD_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DISABLE_COORD_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|BoolQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
DECL|field|mustClauses
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|mustClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|mustNotClauses
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|mustNotClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|filterClauses
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|filterClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|shouldClauses
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|shouldClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|disableCoord
specifier|private
name|boolean
name|disableCoord
init|=
name|DISABLE_COORD_DEFAULT
decl_stmt|;
DECL|field|adjustPureNegative
specifier|private
name|boolean
name|adjustPureNegative
init|=
name|ADJUST_PURE_NEGATIVE_DEFAULT
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
comment|/**      * Adds a query that<b>must</b> appear in the matching documents and will      * contribute to scoring. No<tt>null</tt> value allowed.      */
DECL|method|must
specifier|public
name|BoolQueryBuilder
name|must
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner bool query clause cannot be null"
argument_list|)
throw|;
block|}
name|mustClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the queries that<b>must</b> appear in the matching documents.      */
DECL|method|must
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|must
parameter_list|()
block|{
return|return
name|this
operator|.
name|mustClauses
return|;
block|}
comment|/**      * Adds a query that<b>must</b> appear in the matching documents but will      * not contribute to scoring. No<tt>null</tt> value allowed.      */
DECL|method|filter
specifier|public
name|BoolQueryBuilder
name|filter
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner bool query clause cannot be null"
argument_list|)
throw|;
block|}
name|filterClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the queries that<b>must</b> appear in the matching documents but don't conntribute to scoring      */
DECL|method|filter
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|filter
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterClauses
return|;
block|}
comment|/**      * Adds a query that<b>must not</b> appear in the matching documents.      * No<tt>null</tt> value allowed.      */
DECL|method|mustNot
specifier|public
name|BoolQueryBuilder
name|mustNot
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner bool query clause cannot be null"
argument_list|)
throw|;
block|}
name|mustNotClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the queries that<b>must not</b> appear in the matching documents.      */
DECL|method|mustNot
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|mustNot
parameter_list|()
block|{
return|return
name|this
operator|.
name|mustNotClauses
return|;
block|}
comment|/**      * Adds a clause that<i>should</i> be matched by the returned documents. For a boolean query with no      *<tt>MUST</tt> clauses one or more<code>SHOULD</code> clauses must match a document      * for the BooleanQuery to match. No<tt>null</tt> value allowed.      *      * @see #minimumNumberShouldMatch(int)      */
DECL|method|should
specifier|public
name|BoolQueryBuilder
name|should
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner bool query clause cannot be null"
argument_list|)
throw|;
block|}
name|shouldClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the list of clauses that<b>should</b> be matched by the returned documents.      *      * @see #should(QueryBuilder)      *  @see #minimumNumberShouldMatch(int)      */
DECL|method|should
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|should
parameter_list|()
block|{
return|return
name|this
operator|.
name|shouldClauses
return|;
block|}
comment|/**      * Disables<tt>Similarity#coord(int,int)</tt> in scoring. Defaults to<tt>false</tt>.      */
DECL|method|disableCoord
specifier|public
name|BoolQueryBuilder
name|disableCoord
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
block|{
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return whether the<tt>Similarity#coord(int,int)</tt> in scoring are disabled. Defaults to<tt>false</tt>.      */
DECL|method|disableCoord
specifier|public
name|boolean
name|disableCoord
parameter_list|()
block|{
return|return
name|this
operator|.
name|disableCoord
return|;
block|}
comment|/**      * Specifies a minimum number of the optional (should) boolean clauses which must be satisfied.      *<p>      * By default no optional clauses are necessary for a match      * (unless there are no required clauses).  If this method is used,      * then the specified number of clauses is required.      *<p>      * Use of this method is totally independent of specifying that      * any specific clauses are required (or prohibited).  This number will      * only be compared against the number of matching optional clauses.      *      * @param minimumNumberShouldMatch the number of optional clauses that must match      */
DECL|method|minimumNumberShouldMatch
specifier|public
name|BoolQueryBuilder
name|minimumNumberShouldMatch
parameter_list|(
name|int
name|minimumNumberShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|minimumShouldMatch
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|minimumNumberShouldMatch
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specifies a minimum number of the optional (should) boolean clauses which must be satisfied.      * @see BoolQueryBuilder#minimumNumberShouldMatch(int)      */
DECL|method|minimumNumberShouldMatch
specifier|public
name|BoolQueryBuilder
name|minimumNumberShouldMatch
parameter_list|(
name|String
name|minimumNumberShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|minimumShouldMatch
operator|=
name|minimumNumberShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return the string representation of the minimumShouldMatch settings for this query      */
DECL|method|minimumNumberShouldMatch
specifier|public
name|String
name|minimumNumberShouldMatch
parameter_list|()
block|{
return|return
name|this
operator|.
name|minimumShouldMatch
return|;
block|}
comment|/**      * Sets the minimum should match using the special syntax (for example, supporting percentage).      */
DECL|method|minimumShouldMatch
specifier|public
name|BoolQueryBuilder
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
comment|/**      * Returns<code>true</code> iff this query builder has at least one should, must, must not or filter clause.      * Otherwise<code>false</code>.      */
DECL|method|hasClauses
specifier|public
name|boolean
name|hasClauses
parameter_list|()
block|{
return|return
operator|!
operator|(
name|mustClauses
operator|.
name|isEmpty
argument_list|()
operator|&&
name|shouldClauses
operator|.
name|isEmpty
argument_list|()
operator|&&
name|mustNotClauses
operator|.
name|isEmpty
argument_list|()
operator|&&
name|filterClauses
operator|.
name|isEmpty
argument_list|()
operator|)
return|;
block|}
comment|/**      * If a boolean query contains only negative ("must not") clauses should the      * BooleanQuery be enhanced with a {@link MatchAllDocsQuery} in order to act      * as a pure exclude. The default is<code>true</code>.      */
DECL|method|adjustPureNegative
specifier|public
name|BoolQueryBuilder
name|adjustPureNegative
parameter_list|(
name|boolean
name|adjustPureNegative
parameter_list|)
block|{
name|this
operator|.
name|adjustPureNegative
operator|=
name|adjustPureNegative
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return the setting for the adjust_pure_negative setting in this query      */
DECL|method|adjustPureNegative
specifier|public
name|boolean
name|adjustPureNegative
parameter_list|()
block|{
return|return
name|this
operator|.
name|adjustPureNegative
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
name|NAME
argument_list|)
expr_stmt|;
name|doXArrayContent
argument_list|(
literal|"must"
argument_list|,
name|mustClauses
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|doXArrayContent
argument_list|(
literal|"filter"
argument_list|,
name|filterClauses
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|doXArrayContent
argument_list|(
literal|"must_not"
argument_list|,
name|mustNotClauses
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|doXArrayContent
argument_list|(
literal|"should"
argument_list|,
name|shouldClauses
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"disable_coord"
argument_list|,
name|disableCoord
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"adjust_pure_negative"
argument_list|,
name|adjustPureNegative
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
block|}
DECL|method|doXArrayContent
specifier|private
specifier|static
name|void
name|doXArrayContent
parameter_list|(
name|String
name|field
parameter_list|,
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|clauses
parameter_list|,
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
name|clauses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|builder
operator|.
name|startArray
argument_list|(
name|field
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryBuilder
name|clause
range|:
name|clauses
control|)
block|{
name|clause
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
name|endArray
argument_list|()
expr_stmt|;
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
name|BooleanQuery
operator|.
name|Builder
name|booleanQueryBuilder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|booleanQueryBuilder
operator|.
name|setDisableCoord
argument_list|(
name|disableCoord
argument_list|)
expr_stmt|;
name|addBooleanClauses
argument_list|(
name|context
argument_list|,
name|booleanQueryBuilder
argument_list|,
name|mustClauses
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|addBooleanClauses
argument_list|(
name|context
argument_list|,
name|booleanQueryBuilder
argument_list|,
name|mustNotClauses
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|addBooleanClauses
argument_list|(
name|context
argument_list|,
name|booleanQueryBuilder
argument_list|,
name|shouldClauses
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|addBooleanClauses
argument_list|(
name|context
argument_list|,
name|booleanQueryBuilder
argument_list|,
name|filterClauses
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|BooleanQuery
name|booleanQuery
init|=
name|booleanQueryBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
name|booleanQuery
operator|=
name|Queries
operator|.
name|applyMinimumShouldMatch
argument_list|(
name|booleanQuery
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
return|return
name|adjustPureNegative
condition|?
name|fixNegativeQueryIfNeeded
argument_list|(
name|booleanQuery
argument_list|)
else|:
name|booleanQuery
return|;
block|}
DECL|method|addBooleanClauses
specifier|private
name|void
name|addBooleanClauses
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|BooleanQuery
operator|.
name|Builder
name|booleanQueryBuilder
parameter_list|,
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|clauses
parameter_list|,
name|Occur
name|occurs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|QueryBuilder
name|query
range|:
name|clauses
control|)
block|{
name|Query
name|luceneQuery
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|occurs
condition|)
block|{
case|case
name|SHOULD
case|:
if|if
condition|(
name|context
operator|.
name|isFilter
argument_list|()
operator|&&
name|minimumShouldMatch
operator|==
literal|null
condition|)
block|{
name|minimumShouldMatch
operator|=
literal|"1"
expr_stmt|;
block|}
name|luceneQuery
operator|=
name|query
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|FILTER
case|:
case|case
name|MUST_NOT
case|:
name|luceneQuery
operator|=
name|query
operator|.
name|toFilter
argument_list|(
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|MUST
case|:
name|luceneQuery
operator|=
name|query
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|luceneQuery
operator|!=
literal|null
condition|)
block|{
name|booleanQueryBuilder
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|luceneQuery
argument_list|,
name|occurs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|adjustPureNegative
argument_list|,
name|disableCoord
argument_list|,
name|minimumShouldMatch
argument_list|,
name|mustClauses
argument_list|,
name|shouldClauses
argument_list|,
name|mustNotClauses
argument_list|,
name|filterClauses
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|BoolQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|adjustPureNegative
argument_list|,
name|other
operator|.
name|adjustPureNegative
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|disableCoord
argument_list|,
name|other
operator|.
name|disableCoord
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
name|mustClauses
argument_list|,
name|other
operator|.
name|mustClauses
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|shouldClauses
argument_list|,
name|other
operator|.
name|shouldClauses
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|mustNotClauses
argument_list|,
name|other
operator|.
name|mustNotClauses
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|filterClauses
argument_list|,
name|other
operator|.
name|filterClauses
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|BoolQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BoolQueryBuilder
name|boolQueryBuilder
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|queryBuilders
init|=
name|readQueries
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|boolQueryBuilder
operator|.
name|mustClauses
operator|.
name|addAll
argument_list|(
name|queryBuilders
argument_list|)
expr_stmt|;
name|queryBuilders
operator|=
name|readQueries
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|boolQueryBuilder
operator|.
name|mustNotClauses
operator|.
name|addAll
argument_list|(
name|queryBuilders
argument_list|)
expr_stmt|;
name|queryBuilders
operator|=
name|readQueries
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|boolQueryBuilder
operator|.
name|shouldClauses
operator|.
name|addAll
argument_list|(
name|queryBuilders
argument_list|)
expr_stmt|;
name|queryBuilders
operator|=
name|readQueries
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|boolQueryBuilder
operator|.
name|filterClauses
operator|.
name|addAll
argument_list|(
name|queryBuilders
argument_list|)
expr_stmt|;
name|boolQueryBuilder
operator|.
name|adjustPureNegative
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|boolQueryBuilder
operator|.
name|disableCoord
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|boolQueryBuilder
operator|.
name|minimumShouldMatch
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|boolQueryBuilder
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
name|writeQueries
argument_list|(
name|out
argument_list|,
name|mustClauses
argument_list|)
expr_stmt|;
name|writeQueries
argument_list|(
name|out
argument_list|,
name|mustNotClauses
argument_list|)
expr_stmt|;
name|writeQueries
argument_list|(
name|out
argument_list|,
name|shouldClauses
argument_list|)
expr_stmt|;
name|writeQueries
argument_list|(
name|out
argument_list|,
name|filterClauses
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|adjustPureNegative
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|disableCoord
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

