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
name|Query
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanQuery
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Matches spans which are near one another. One can specify slop, the maximum number  * of intervening unmatched positions, as well as whether matches are required to be in-order.  * The span near query maps to Lucene {@link SpanNearQuery}.  */
end_comment

begin_class
DECL|class|SpanNearQueryBuilder
specifier|public
class|class
name|SpanNearQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|SpanNearQueryBuilder
argument_list|>
implements|implements
name|SpanQueryBuilder
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"span_near"
decl_stmt|;
comment|/** Default for flag controlling whether matches are required to be in-order */
DECL|field|DEFAULT_IN_ORDER
specifier|public
specifier|static
name|boolean
name|DEFAULT_IN_ORDER
init|=
literal|true
decl_stmt|;
DECL|field|SLOP_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|SLOP_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"slop"
argument_list|)
decl_stmt|;
DECL|field|CLAUSES_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|CLAUSES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"clauses"
argument_list|)
decl_stmt|;
DECL|field|IN_ORDER_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|IN_ORDER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"in_order"
argument_list|)
decl_stmt|;
DECL|field|clauses
specifier|private
specifier|final
name|List
argument_list|<
name|SpanQueryBuilder
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|slop
specifier|private
specifier|final
name|int
name|slop
decl_stmt|;
DECL|field|inOrder
specifier|private
name|boolean
name|inOrder
init|=
name|DEFAULT_IN_ORDER
decl_stmt|;
comment|/**      * @param initialClause an initial span query clause      * @param slop controls the maximum number of intervening unmatched positions permitted      */
DECL|method|SpanNearQueryBuilder
specifier|public
name|SpanNearQueryBuilder
parameter_list|(
name|SpanQueryBuilder
name|initialClause
parameter_list|,
name|int
name|slop
parameter_list|)
block|{
if|if
condition|(
name|initialClause
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"] must include at least one clause"
argument_list|)
throw|;
block|}
name|this
operator|.
name|clauses
operator|.
name|add
argument_list|(
name|initialClause
argument_list|)
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|SpanNearQueryBuilder
specifier|public
name|SpanNearQueryBuilder
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
for|for
control|(
name|QueryBuilder
name|clause
range|:
name|readQueries
argument_list|(
name|in
argument_list|)
control|)
block|{
name|this
operator|.
name|clauses
operator|.
name|add
argument_list|(
operator|(
name|SpanQueryBuilder
operator|)
name|clause
argument_list|)
expr_stmt|;
block|}
name|slop
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|inOrder
operator|=
name|in
operator|.
name|readBoolean
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
name|clauses
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the maximum number of intervening unmatched positions permitted      */
DECL|method|slop
specifier|public
name|int
name|slop
parameter_list|()
block|{
return|return
name|this
operator|.
name|slop
return|;
block|}
comment|/**      * Add a span clause to the current list of clauses      */
DECL|method|addClause
specifier|public
name|SpanNearQueryBuilder
name|addClause
parameter_list|(
name|SpanQueryBuilder
name|clause
parameter_list|)
block|{
if|if
condition|(
name|clause
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"]  clauses cannot be null"
argument_list|)
throw|;
block|}
name|clauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return the {@link SpanQueryBuilder} clauses that were set for this query      */
DECL|method|clauses
specifier|public
name|List
argument_list|<
name|SpanQueryBuilder
argument_list|>
name|clauses
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|this
operator|.
name|clauses
argument_list|)
return|;
block|}
comment|/**      * When<code>inOrder</code> is true, the spans from each clause      * must be in the same order as in<code>clauses</code> and must be non-overlapping.      * Defaults to<code>true</code>      */
DECL|method|inOrder
specifier|public
name|SpanNearQueryBuilder
name|inOrder
parameter_list|(
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @see SpanNearQueryBuilder#inOrder(boolean)      */
DECL|method|inOrder
specifier|public
name|boolean
name|inOrder
parameter_list|()
block|{
return|return
name|this
operator|.
name|inOrder
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
name|startArray
argument_list|(
name|CLAUSES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SpanQueryBuilder
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
name|builder
operator|.
name|field
argument_list|(
name|SLOP_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|slop
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|IN_ORDER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|inOrder
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
name|Optional
argument_list|<
name|SpanNearQueryBuilder
argument_list|>
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
name|Integer
name|slop
init|=
literal|null
decl_stmt|;
name|boolean
name|inOrder
init|=
name|SpanNearQueryBuilder
operator|.
name|DEFAULT_IN_ORDER
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|SpanQueryBuilder
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|START_ARRAY
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
name|CLAUSES_FIELD
argument_list|)
condition|)
block|{
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
name|END_ARRAY
condition|)
block|{
name|Optional
argument_list|<
name|QueryBuilder
argument_list|>
name|query
init|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|isPresent
argument_list|()
operator|==
literal|false
operator|||
name|query
operator|.
name|get
argument_list|()
operator|instanceof
name|SpanQueryBuilder
operator|==
literal|false
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
literal|"spanNear [clauses] must be of type span query"
argument_list|)
throw|;
block|}
name|clauses
operator|.
name|add
argument_list|(
operator|(
name|SpanQueryBuilder
operator|)
name|query
operator|.
name|get
argument_list|()
argument_list|)
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
literal|"[span_near] query does not support ["
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
name|IN_ORDER_FIELD
argument_list|)
condition|)
block|{
name|inOrder
operator|=
name|parser
operator|.
name|booleanValue
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
name|SLOP_FIELD
argument_list|)
condition|)
block|{
name|slop
operator|=
name|parser
operator|.
name|intValue
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
literal|"[span_near] query does not support ["
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
literal|"[span_near] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|clauses
operator|.
name|isEmpty
argument_list|()
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
literal|"span_near must include [clauses]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|slop
operator|==
literal|null
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
literal|"span_near must include [slop]"
argument_list|)
throw|;
block|}
name|SpanNearQueryBuilder
name|queryBuilder
init|=
operator|new
name|SpanNearQueryBuilder
argument_list|(
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|slop
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|queryBuilder
operator|.
name|addClause
argument_list|(
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|queryBuilder
operator|.
name|inOrder
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|queryBuilder
argument_list|)
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
name|SpanQuery
index|[]
name|spanQueries
init|=
operator|new
name|SpanQuery
index|[
name|clauses
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|query
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
assert|assert
name|query
operator|instanceof
name|SpanQuery
assert|;
name|spanQueries
index|[
name|i
index|]
operator|=
operator|(
name|SpanQuery
operator|)
name|query
expr_stmt|;
block|}
return|return
operator|new
name|SpanNearQuery
argument_list|(
name|spanQueries
argument_list|,
name|slop
argument_list|,
name|inOrder
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
name|clauses
argument_list|,
name|slop
argument_list|,
name|inOrder
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
name|SpanNearQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|clauses
argument_list|,
name|other
operator|.
name|clauses
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|slop
argument_list|,
name|other
operator|.
name|slop
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|inOrder
argument_list|,
name|other
operator|.
name|inOrder
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
block|}
end_class

end_unit

