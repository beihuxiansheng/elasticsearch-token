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
name|ConstantScoreQuery
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
comment|/**  * A query that wraps a filter and simply returns a constant score equal to the  * query boost for every document in the filter.  */
end_comment

begin_class
DECL|class|ConstantScoreQueryBuilder
specifier|public
class|class
name|ConstantScoreQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|ConstantScoreQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"constant_score"
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
DECL|field|INNER_QUERY_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|INNER_QUERY_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"filter"
argument_list|,
literal|"query"
argument_list|)
decl_stmt|;
DECL|field|filterBuilder
specifier|private
specifier|final
name|QueryBuilder
name|filterBuilder
decl_stmt|;
comment|/**      * A query that wraps another query and simply returns a constant score equal to the      * query boost for every document in the query.      *      * @param filterBuilder The query to wrap in a constant score query      */
DECL|method|ConstantScoreQueryBuilder
specifier|public
name|ConstantScoreQueryBuilder
parameter_list|(
name|QueryBuilder
name|filterBuilder
parameter_list|)
block|{
if|if
condition|(
name|filterBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner clause [filter] cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|filterBuilder
operator|=
name|filterBuilder
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ConstantScoreQueryBuilder
specifier|public
name|ConstantScoreQueryBuilder
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
name|filterBuilder
operator|=
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|QueryBuilder
operator|.
name|class
argument_list|)
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
name|writeNamedWriteable
argument_list|(
name|filterBuilder
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the query that was wrapped in this constant score query      */
DECL|method|innerQuery
specifier|public
name|QueryBuilder
name|innerQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterBuilder
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
name|INNER_QUERY_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|filterBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
name|ConstantScoreQueryBuilder
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
name|query
init|=
literal|null
decl_stmt|;
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
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
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
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
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INNER_QUERY_FIELD
argument_list|)
condition|)
block|{
if|if
condition|(
name|queryFound
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
literal|"["
operator|+
name|ConstantScoreQueryBuilder
operator|.
name|NAME
operator|+
literal|"]"
operator|+
literal|" accepts only one 'filter' element."
argument_list|)
throw|;
block|}
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
expr_stmt|;
name|queryFound
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
literal|"[constant_score] query does not support ["
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
name|getParseFieldMatcher
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
name|getParseFieldMatcher
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
literal|"[constant_score] query does not support ["
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
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"unexpected token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|queryFound
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
literal|"[constant_score] requires a 'filter' element"
argument_list|)
throw|;
block|}
name|ConstantScoreQueryBuilder
name|constantScoreBuilder
init|=
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|constantScoreBuilder
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|constantScoreBuilder
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
return|return
name|constantScoreBuilder
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
name|innerFilter
init|=
name|filterBuilder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerFilter
operator|==
literal|null
condition|)
block|{
comment|// return null so that parent queries (e.g. bool) also ignore this
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|innerFilter
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
name|filterBuilder
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
name|ConstantScoreQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|filterBuilder
argument_list|,
name|other
operator|.
name|filterBuilder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|queryRewriteContext
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryBuilder
name|rewrite
init|=
name|filterBuilder
operator|.
name|rewrite
argument_list|(
name|queryRewriteContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrite
operator|!=
name|filterBuilder
condition|)
block|{
return|return
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|rewrite
argument_list|)
return|;
block|}
return|return
name|this
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
name|InnerHitBuilder
operator|.
name|extractInnerHits
argument_list|(
name|filterBuilder
argument_list|,
name|innerHits
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

