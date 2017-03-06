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
name|ParsingException
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
name|Map
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
name|AbstractQueryBuilder
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
comment|/** Default multiplication factor for breaking ties in document scores.*/
DECL|field|DEFAULT_TIE_BREAKER
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_TIE_BREAKER
init|=
literal|0.0f
decl_stmt|;
DECL|field|TIE_BREAKER_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TIE_BREAKER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"tie_breaker"
argument_list|)
decl_stmt|;
DECL|field|QUERIES_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|QUERIES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"queries"
argument_list|)
decl_stmt|;
DECL|field|queries
specifier|private
specifier|final
name|List
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
DECL|field|tieBreaker
specifier|private
name|float
name|tieBreaker
init|=
name|DEFAULT_TIE_BREAKER
decl_stmt|;
DECL|method|DisMaxQueryBuilder
specifier|public
name|DisMaxQueryBuilder
parameter_list|()
block|{     }
comment|/**      * Read from a stream.      */
DECL|method|DisMaxQueryBuilder
specifier|public
name|DisMaxQueryBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|queries
operator|.
name|addAll
argument_list|(
name|readQueries
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|tieBreaker
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
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
block|}
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
literal|"inner dismax query clause cannot be null"
argument_list|)
throw|;
block|}
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
DECL|method|innerQueries
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|innerQueries
parameter_list|()
block|{
return|return
name|this
operator|.
name|queries
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
name|TIE_BREAKER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|tieBreaker
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|QUERIES_FIELD
operator|.
name|getPreferredName
argument_list|()
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
DECL|method|fromXContent
specifier|public
specifier|static
name|DisMaxQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|float
name|tieBreaker
init|=
name|DisMaxQueryBuilder
operator|.
name|DEFAULT_TIE_BREAKER
decl_stmt|;
specifier|final
name|List
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
name|boolean
name|queriesFound
init|=
literal|false
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
name|QUERIES_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queriesFound
operator|=
literal|true
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[dis_max] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
name|QUERIES_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queriesFound
operator|=
literal|true
expr_stmt|;
while|while
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[dis_max] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|AbstractQueryBuilder
operator|.
name|BOOST_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TIE_BREAKER_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|tieBreaker
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|AbstractQueryBuilder
operator|.
name|NAME_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[dis_max] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|queriesFound
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[dis_max] requires 'queries' field with at least one clause"
argument_list|)
throw|;
block|}
name|DisMaxQueryBuilder
name|disMaxQuery
init|=
operator|new
name|DisMaxQueryBuilder
argument_list|()
decl_stmt|;
name|disMaxQuery
operator|.
name|tieBreaker
argument_list|(
name|tieBreaker
argument_list|)
expr_stmt|;
name|disMaxQuery
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
name|disMaxQuery
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryBuilder
name|query
range|:
name|queries
control|)
block|{
name|disMaxQuery
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|disMaxQuery
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
name|context
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
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|(
literal|"no clauses for dismax query."
argument_list|)
return|;
block|}
return|return
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|luceneQueries
argument_list|,
name|tieBreaker
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
name|queries
argument_list|,
name|tieBreaker
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
name|DisMaxQueryBuilder
name|other
parameter_list|)
block|{
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
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|extractInnerHitBuilders
specifier|protected
name|void
name|extractInnerHitBuilders
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitBuilder
argument_list|>
name|innerHits
parameter_list|)
block|{
for|for
control|(
name|QueryBuilder
name|query
range|:
name|queries
control|)
block|{
name|InnerHitBuilder
operator|.
name|extractInnerHits
argument_list|(
name|query
argument_list|,
name|innerHits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

