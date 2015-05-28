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
name|DisjunctionMaxQuery
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
name|Collection
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

begin_comment
comment|/**  * A query that generates the union of documents produced by its sub-queries, and that scores each document  * with the maximum score for that document as produced by any sub-query, plus a tie breaking increment for any  * additional matching sub-queries.  */
end_comment

begin_class
DECL|class|DisMaxQueryBuilder
specifier|public
class|class
name|DisMaxQueryBuilder
extends|extends
name|QueryBuilder
argument_list|<
name|DisMaxQueryBuilder
argument_list|>
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|DisMaxQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"dis_max"
decl_stmt|;
DECL|field|queries
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|QueryBuilder
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|/** Default multiplication factor for breaking ties in document scores.*/
DECL|field|DEFAULT_TIE_BREAKER
specifier|public
specifier|static
name|float
name|DEFAULT_TIE_BREAKER
init|=
literal|0.0f
decl_stmt|;
DECL|field|tieBreaker
specifier|private
name|float
name|tieBreaker
init|=
name|DEFAULT_TIE_BREAKER
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|DisMaxQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|DisMaxQueryBuilder
argument_list|()
decl_stmt|;
comment|/**      * Add a sub-query to this disjunction.      */
DECL|method|add
specifier|public
name|DisMaxQueryBuilder
name|add
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|queries
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
comment|/**      * @return an immutable list copy of the current sub-queries of this disjunction      */
DECL|method|queries
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|queries
parameter_list|()
block|{
return|return
name|this
operator|.
name|queries
return|;
block|}
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
annotation|@
name|Override
DECL|method|boost
specifier|public
name|DisMaxQueryBuilder
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
comment|/**      * @return the boost for this query      */
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
name|this
operator|.
name|boost
return|;
block|}
comment|/**      * The score of each non-maximum disjunct for a document is multiplied by this weight      * and added into the final score.  If non-zero, the value should be small, on the order of 0.1, which says that      * 10 occurrences of word in a lower-scored field that is also in a higher scored field is just as good as a unique      * word in the lower scored field (i.e., one that is not in any higher scored field.      */
DECL|method|tieBreaker
specifier|public
name|DisMaxQueryBuilder
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
comment|/**      * @return the tie breaker score      * @see DisMaxQueryBuilder#tieBreaker(float)      */
DECL|method|tieBreaker
specifier|public
name|float
name|tieBreaker
parameter_list|()
block|{
return|return
name|this
operator|.
name|tieBreaker
return|;
block|}
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|DisMaxQueryBuilder
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
comment|/**      * @return the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|String
name|queryName
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryName
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
name|builder
operator|.
name|field
argument_list|(
literal|"tie_breaker"
argument_list|,
name|tieBreaker
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
name|boost
argument_list|)
expr_stmt|;
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
name|startArray
argument_list|(
literal|"queries"
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryBuilder
name|queryBuilder
range|:
name|queries
control|)
block|{
name|queryBuilder
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toQuery
specifier|public
name|Query
name|toQuery
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|QueryParsingException
throws|,
name|IOException
block|{
comment|// return null if there are no queries at all
name|Collection
argument_list|<
name|Query
argument_list|>
name|luceneQueries
init|=
name|toQueries
argument_list|(
name|queries
argument_list|,
name|parseContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|luceneQueries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DisjunctionMaxQuery
name|query
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|luceneQueries
argument_list|,
name|tieBreaker
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|QueryValidationException
name|validate
parameter_list|()
block|{
comment|// nothing to validate, clauses are optional
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|DisMaxQueryBuilder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DisMaxQueryBuilder
name|disMax
init|=
operator|new
name|DisMaxQueryBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|queryBuilders
init|=
name|in
operator|.
name|readNamedWritableList
argument_list|()
decl_stmt|;
name|disMax
operator|.
name|queries
operator|.
name|addAll
argument_list|(
name|queryBuilders
argument_list|)
expr_stmt|;
name|disMax
operator|.
name|tieBreaker
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|disMax
operator|.
name|queryName
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|disMax
operator|.
name|boost
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
return|return
name|disMax
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
name|writeNamedWritableList
argument_list|(
name|this
operator|.
name|queries
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|tieBreaker
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|queries
argument_list|,
name|tieBreaker
argument_list|,
name|boost
argument_list|,
name|queryName
argument_list|)
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DisMaxQueryBuilder
name|other
init|=
operator|(
name|DisMaxQueryBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|queries
argument_list|,
name|other
operator|.
name|queries
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|tieBreaker
argument_list|,
name|other
operator|.
name|tieBreaker
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|boost
argument_list|,
name|other
operator|.
name|boost
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|queryName
argument_list|,
name|other
operator|.
name|queryName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|queryId
specifier|public
name|String
name|queryId
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
block|}
end_class

end_unit

