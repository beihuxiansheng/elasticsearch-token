begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
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

begin_comment
comment|/**  * A Query that matches documents matching boolean combinations of other queries.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BoolQueryBuilder
specifier|public
class|class
name|BoolQueryBuilder
extends|extends
name|BaseQueryBuilder
block|{
DECL|field|mustClauses
specifier|private
name|ArrayList
argument_list|<
name|XContentQueryBuilder
argument_list|>
name|mustClauses
init|=
operator|new
name|ArrayList
argument_list|<
name|XContentQueryBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mustNotClauses
specifier|private
name|ArrayList
argument_list|<
name|XContentQueryBuilder
argument_list|>
name|mustNotClauses
init|=
operator|new
name|ArrayList
argument_list|<
name|XContentQueryBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|shouldClauses
specifier|private
name|ArrayList
argument_list|<
name|XContentQueryBuilder
argument_list|>
name|shouldClauses
init|=
operator|new
name|ArrayList
argument_list|<
name|XContentQueryBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|disableCoord
specifier|private
name|Boolean
name|disableCoord
decl_stmt|;
DECL|field|minimumNumberShouldMatch
specifier|private
name|int
name|minimumNumberShouldMatch
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Adds a query that<b>must</b> appear in the matching documents.      */
DECL|method|must
specifier|public
name|BoolQueryBuilder
name|must
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
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
comment|/**      * Adds a query that<b>must not</b> appear in the matching documents.      */
DECL|method|mustNot
specifier|public
name|BoolQueryBuilder
name|mustNot
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
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
comment|/**      * Adds a query that<i>should</i> appear in the matching documents. For a boolean query with no      *<tt>MUST</tt> clauses one or more<code>SHOULD</code> clauses must match a document      * for the BooleanQuery to match.      *      * @see #minimumNumberShouldMatch(int)      */
DECL|method|should
specifier|public
name|BoolQueryBuilder
name|should
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
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
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|BoolQueryBuilder
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
comment|/**      * Disables<tt>Similarity#coord(int,int)</tt> in scoring. Defualts to<tt>false</tt>.      */
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
comment|/**      * Specifies a minimum number of the optional (should) boolean clauses which must be satisfied.      *      *<p>By default no optional clauses are necessary for a match      * (unless there are no required clauses).  If this method is used,      * then the specified number of clauses is required.      *      *<p>Use of this method is totally independent of specifying that      * any specific clauses are required (or prohibited).  This number will      * only be compared against the number of matching optional clauses.      *      * @param minimumNumberShouldMatch the number of optional clauses that must match      */
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
name|minimumNumberShouldMatch
operator|=
name|minimumNumberShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Return<code>true</code> if the query being built has no clause yet      */
DECL|method|hasClauses
specifier|public
name|boolean
name|hasClauses
parameter_list|()
block|{
return|return
operator|!
name|mustClauses
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|mustNotClauses
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|shouldClauses
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|doXContent
annotation|@
name|Override
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
literal|"bool"
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
name|disableCoord
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"disable_coord"
argument_list|,
name|disableCoord
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minimumNumberShouldMatch
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"minimum_number_should_match"
argument_list|,
name|minimumNumberShouldMatch
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|doXArrayContent
specifier|private
name|void
name|doXArrayContent
parameter_list|(
name|String
name|field
parameter_list|,
name|List
argument_list|<
name|XContentQueryBuilder
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
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|field
argument_list|)
expr_stmt|;
for|for
control|(
name|XContentQueryBuilder
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
block|}
block|}
end_class

end_unit

