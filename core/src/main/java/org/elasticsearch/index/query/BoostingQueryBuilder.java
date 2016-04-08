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
name|BoostingQuery
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
comment|/**  * The BoostingQuery class can be used to effectively demote results that match a given query.  * Unlike the "NOT" clause, this still selects documents that contain undesirable terms,  * but reduces their overall score:  *<p>  * Query balancedQuery = new BoostingQuery(positiveQuery, negativeQuery, 0.01f);  * In this scenario the positiveQuery contains the mandatory, desirable criteria which is used to  * select all matching documents, and the negativeQuery contains the undesirable elements which  * are simply used to lessen the scores. Documents that match the negativeQuery have their score  * multiplied by the supplied "boost" parameter, so this should be less than 1 to achieve a  * demoting effect  */
end_comment

begin_class
DECL|class|BoostingQueryBuilder
specifier|public
class|class
name|BoostingQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|BoostingQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"boosting"
decl_stmt|;
DECL|field|QUERY_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|QUERY_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|POSITIVE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|POSITIVE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"positive"
argument_list|)
decl_stmt|;
DECL|field|NEGATIVE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|NEGATIVE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"negative"
argument_list|)
decl_stmt|;
DECL|field|NEGATIVE_BOOST_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|NEGATIVE_BOOST_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"negative_boost"
argument_list|)
decl_stmt|;
DECL|field|positiveQuery
specifier|private
specifier|final
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|positiveQuery
decl_stmt|;
DECL|field|negativeQuery
specifier|private
specifier|final
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|negativeQuery
decl_stmt|;
DECL|field|negativeBoost
specifier|private
name|float
name|negativeBoost
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Create a new {@link BoostingQueryBuilder}      *      * @param positiveQuery the positive query for this boosting query.      * @param negativeQuery the negative query for this boosting query.      */
DECL|method|BoostingQueryBuilder
specifier|public
name|BoostingQueryBuilder
parameter_list|(
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|positiveQuery
parameter_list|,
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|negativeQuery
parameter_list|)
block|{
if|if
condition|(
name|positiveQuery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner clause [positive] cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|negativeQuery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner clause [negative] cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|positiveQuery
operator|=
name|positiveQuery
expr_stmt|;
name|this
operator|.
name|negativeQuery
operator|=
name|negativeQuery
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|BoostingQueryBuilder
specifier|public
name|BoostingQueryBuilder
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
name|positiveQuery
operator|=
name|in
operator|.
name|readQuery
argument_list|()
expr_stmt|;
name|negativeQuery
operator|=
name|in
operator|.
name|readQuery
argument_list|()
expr_stmt|;
name|negativeBoost
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
name|out
operator|.
name|writeQuery
argument_list|(
name|positiveQuery
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeQuery
argument_list|(
name|negativeQuery
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|negativeBoost
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the positive query for this boosting query.      */
DECL|method|positiveQuery
specifier|public
name|QueryBuilder
name|positiveQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|positiveQuery
return|;
block|}
comment|/**      * Get the negative query for this boosting query.      */
DECL|method|negativeQuery
specifier|public
name|QueryBuilder
name|negativeQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|negativeQuery
return|;
block|}
comment|/**      * Set the negative boost factor.      */
DECL|method|negativeBoost
specifier|public
name|BoostingQueryBuilder
name|negativeBoost
parameter_list|(
name|float
name|negativeBoost
parameter_list|)
block|{
if|if
condition|(
name|negativeBoost
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query requires negativeBoost to be set to positive value"
argument_list|)
throw|;
block|}
name|this
operator|.
name|negativeBoost
operator|=
name|negativeBoost
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the negative boost factor.      */
DECL|method|negativeBoost
specifier|public
name|float
name|negativeBoost
parameter_list|()
block|{
return|return
name|this
operator|.
name|negativeBoost
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
name|POSITIVE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|positiveQuery
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|NEGATIVE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|negativeQuery
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|NEGATIVE_BOOST_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|negativeBoost
argument_list|)
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
name|BoostingQueryBuilder
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
name|QueryBuilder
name|positiveQuery
init|=
literal|null
decl_stmt|;
name|boolean
name|positiveQueryFound
init|=
literal|false
decl_stmt|;
name|QueryBuilder
name|negativeQuery
init|=
literal|null
decl_stmt|;
name|boolean
name|negativeQueryFound
init|=
literal|false
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|float
name|negativeBoost
init|=
operator|-
literal|1
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|POSITIVE_FIELD
argument_list|)
condition|)
block|{
name|positiveQuery
operator|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
expr_stmt|;
name|positiveQueryFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|NEGATIVE_FIELD
argument_list|)
condition|)
block|{
name|negativeQuery
operator|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
expr_stmt|;
name|negativeQueryFound
operator|=
literal|true
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
literal|"[boosting] query does not support ["
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
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|NEGATIVE_BOOST_FIELD
argument_list|)
condition|)
block|{
name|negativeBoost
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|NAME_FIELD
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
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|BOOST_FIELD
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
literal|"[boosting] query does not support ["
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
name|positiveQueryFound
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
literal|"[boosting] query requires 'positive' query to be set'"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|negativeQueryFound
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
literal|"[boosting] query requires 'negative' query to be set'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|negativeBoost
operator|<
literal|0
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
literal|"[boosting] query requires 'negative_boost' to be set to be a positive value'"
argument_list|)
throw|;
block|}
name|BoostingQueryBuilder
name|boostingQuery
init|=
operator|new
name|BoostingQueryBuilder
argument_list|(
name|positiveQuery
argument_list|,
name|negativeQuery
argument_list|)
decl_stmt|;
name|boostingQuery
operator|.
name|negativeBoost
argument_list|(
name|negativeBoost
argument_list|)
expr_stmt|;
name|boostingQuery
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|boostingQuery
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
return|return
name|boostingQuery
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
name|Query
name|positive
init|=
name|positiveQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Query
name|negative
init|=
name|negativeQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|// make upstream queries ignore this query by returning `null`
comment|// if either inner query builder returns null
if|if
condition|(
name|positive
operator|==
literal|null
operator|||
name|negative
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|BoostingQuery
argument_list|(
name|positive
argument_list|,
name|negative
argument_list|,
name|negativeBoost
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
name|negativeBoost
argument_list|,
name|positiveQuery
argument_list|,
name|negativeQuery
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
name|BoostingQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|negativeBoost
argument_list|,
name|other
operator|.
name|negativeBoost
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|positiveQuery
argument_list|,
name|other
operator|.
name|positiveQuery
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|negativeQuery
argument_list|,
name|other
operator|.
name|negativeQuery
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|queryRewriteContext
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryBuilder
name|positiveQuery
init|=
name|this
operator|.
name|positiveQuery
operator|.
name|rewrite
argument_list|(
name|queryRewriteContext
argument_list|)
decl_stmt|;
name|QueryBuilder
name|negativeQuery
init|=
name|this
operator|.
name|negativeQuery
operator|.
name|rewrite
argument_list|(
name|queryRewriteContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|positiveQuery
operator|!=
name|this
operator|.
name|positiveQuery
operator|||
name|negativeQuery
operator|!=
name|this
operator|.
name|negativeQuery
condition|)
block|{
name|BoostingQueryBuilder
name|newQueryBuilder
init|=
operator|new
name|BoostingQueryBuilder
argument_list|(
name|positiveQuery
argument_list|,
name|negativeQuery
argument_list|)
decl_stmt|;
name|newQueryBuilder
operator|.
name|negativeBoost
operator|=
name|negativeBoost
expr_stmt|;
return|return
name|newQueryBuilder
return|;
block|}
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

