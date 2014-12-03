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
name|Filter
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
name|Strings
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
name|collect
operator|.
name|Tuple
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
name|inject
operator|.
name|Inject
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
operator|.
name|InnerHitsQueryParserHelper
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
name|query
operator|.
name|support
operator|.
name|XContentStructure
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
name|search
operator|.
name|child
operator|.
name|CustomQueryWrappingFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SubSearchContext
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|HasParentQueryParser
operator|.
name|createParentQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryParserUtils
operator|.
name|ensureNotDeleteByQuery
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|HasParentFilterParser
specifier|public
class|class
name|HasParentFilterParser
implements|implements
name|FilterParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"has_parent"
decl_stmt|;
DECL|field|innerHitsQueryParserHelper
specifier|private
specifier|final
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
decl_stmt|;
annotation|@
name|Inject
DECL|method|HasParentFilterParser
specifier|public
name|HasParentFilterParser
parameter_list|(
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
parameter_list|)
block|{
name|this
operator|.
name|innerHitsQueryParserHelper
operator|=
name|innerHitsQueryParserHelper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Filter
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|ensureNotDeleteByQuery
argument_list|(
name|NAME
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
name|boolean
name|filterFound
init|=
literal|false
decl_stmt|;
name|String
name|parentType
init|=
literal|null
decl_stmt|;
name|Tuple
argument_list|<
name|String
argument_list|,
name|SubSearchContext
argument_list|>
name|innerHits
init|=
literal|null
decl_stmt|;
name|String
name|filterName
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
name|XContentStructure
operator|.
name|InnerQuery
name|iq
init|=
literal|null
decl_stmt|;
name|XContentStructure
operator|.
name|InnerFilter
name|innerFilter
init|=
literal|null
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
comment|// Usually, the query would be parsed here, but the child
comment|// type may not have been extracted yet, so use the
comment|// XContentStructure.<type> facade to parse if available,
comment|// or delay parsing if not.
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|iq
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerQuery
argument_list|(
name|parseContext
argument_list|,
name|parentType
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|parentType
block|}
argument_list|)
expr_stmt|;
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|innerFilter
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerFilter
argument_list|(
name|parseContext
argument_list|,
name|parentType
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|parentType
block|}
argument_list|)
expr_stmt|;
name|filterFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"inner_hits"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|innerHits
operator|=
name|innerHitsQueryParserHelper
operator|.
name|parse
argument_list|(
name|parseContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] filter does not support ["
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"parent_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"parentType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|parentType
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filterName
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
literal|"_cache"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// noop to be backwards compatible
block|}
elseif|else
if|if
condition|(
literal|"_cache_key"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_cacheKey"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// noop to be backwards compatible
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] filter does not support ["
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
name|queryFound
operator|&&
operator|!
name|filterFound
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] filter requires 'query' or 'filter' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parentType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] filter requires 'parent_type' field"
argument_list|)
throw|;
block|}
name|Query
name|innerQuery
decl_stmt|;
if|if
condition|(
name|queryFound
condition|)
block|{
name|innerQuery
operator|=
name|iq
operator|.
name|asQuery
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerQuery
operator|=
name|innerFilter
operator|.
name|asFilter
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|innerQuery
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Query
name|parentQuery
init|=
name|createParentQuery
argument_list|(
name|innerQuery
argument_list|,
name|parentType
argument_list|,
literal|false
argument_list|,
name|parseContext
argument_list|,
name|innerHits
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentQuery
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedFilter
argument_list|(
name|filterName
argument_list|,
operator|new
name|CustomQueryWrappingFilter
argument_list|(
name|parentQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CustomQueryWrappingFilter
argument_list|(
name|parentQuery
argument_list|)
return|;
block|}
block|}
end_class

end_unit

