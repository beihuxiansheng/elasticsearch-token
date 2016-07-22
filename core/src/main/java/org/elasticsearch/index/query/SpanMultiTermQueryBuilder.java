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
name|BoostQuery
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
name|MultiTermQuery
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
operator|.
name|SpanBoostQuery
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
name|SpanMultiTermQueryWrapper
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
comment|/**  * Query that allows wraping a {@link MultiTermQueryBuilder} (one of wildcard, fuzzy, prefix, term, range or regexp query)  * as a {@link SpanQueryBuilder} so it can be nested.  */
end_comment

begin_class
DECL|class|SpanMultiTermQueryBuilder
specifier|public
class|class
name|SpanMultiTermQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|SpanMultiTermQueryBuilder
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
literal|"span_multi"
decl_stmt|;
DECL|field|MATCH_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|MATCH_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"match"
argument_list|)
decl_stmt|;
DECL|field|multiTermQueryBuilder
specifier|private
specifier|final
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
decl_stmt|;
DECL|method|SpanMultiTermQueryBuilder
specifier|public
name|SpanMultiTermQueryBuilder
parameter_list|(
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
parameter_list|)
block|{
if|if
condition|(
name|multiTermQueryBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner multi term query cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|multiTermQueryBuilder
operator|=
name|multiTermQueryBuilder
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|SpanMultiTermQueryBuilder
specifier|public
name|SpanMultiTermQueryBuilder
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
name|multiTermQueryBuilder
operator|=
operator|(
name|MultiTermQueryBuilder
operator|)
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
name|multiTermQueryBuilder
argument_list|)
expr_stmt|;
block|}
DECL|method|innerQuery
specifier|public
name|MultiTermQueryBuilder
name|innerQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|multiTermQueryBuilder
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
name|MATCH_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|multiTermQueryBuilder
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
name|Optional
argument_list|<
name|SpanMultiTermQueryBuilder
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
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|MultiTermQueryBuilder
name|subQuery
init|=
literal|null
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
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MATCH_FIELD
argument_list|)
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
name|MultiTermQueryBuilder
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
literal|"[span_multi] ["
operator|+
name|MATCH_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|"] must be of type multi term query"
argument_list|)
throw|;
block|}
name|subQuery
operator|=
operator|(
name|MultiTermQueryBuilder
operator|)
name|query
operator|.
name|get
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
literal|"[span_multi] query does not support ["
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
literal|"[span_multi] query does not support ["
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
name|subQuery
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
literal|"[span_multi] must have ["
operator|+
name|MATCH_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|"] multi term query clause"
argument_list|)
throw|;
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|SpanMultiTermQueryBuilder
argument_list|(
name|subQuery
argument_list|)
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
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
name|Query
name|subQuery
init|=
name|multiTermQueryBuilder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
if|if
condition|(
name|subQuery
operator|instanceof
name|BoostQuery
condition|)
block|{
name|BoostQuery
name|boostQuery
init|=
operator|(
name|BoostQuery
operator|)
name|subQuery
decl_stmt|;
name|subQuery
operator|=
name|boostQuery
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|boost
operator|=
name|boostQuery
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
comment|//no MultiTermQuery extends SpanQuery, so SpanBoostQuery is not supported here
assert|assert
name|subQuery
operator|instanceof
name|SpanBoostQuery
operator|==
literal|false
assert|;
if|if
condition|(
name|subQuery
operator|instanceof
name|MultiTermQuery
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"unsupported inner query, should be "
operator|+
name|MultiTermQuery
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" but was "
operator|+
name|subQuery
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|SpanQuery
name|wrapper
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
operator|(
name|MultiTermQuery
operator|)
name|subQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
condition|)
block|{
name|wrapper
operator|=
operator|new
name|SpanBoostQuery
argument_list|(
name|wrapper
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapper
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
name|multiTermQueryBuilder
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
name|SpanMultiTermQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|multiTermQueryBuilder
argument_list|,
name|other
operator|.
name|multiTermQueryBuilder
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

