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
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|MapperQueryParser
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
name|queryParser
operator|.
name|MultiFieldMapperQueryParser
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
name|queryParser
operator|.
name|MultiFieldQueryParserSettings
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
name|queryParser
operator|.
name|QueryParserSettings
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Similarity
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
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Maps
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
name|Index
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
name|cache
operator|.
name|IndexCache
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
name|engine
operator|.
name|IndexEngine
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
name|FieldMapper
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
name|FieldMappers
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
name|MapperService
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
name|QueryParsingException
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
name|similarity
operator|.
name|SimilarityService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|QueryParseContext
specifier|public
class|class
name|QueryParseContext
block|{
DECL|field|index
specifier|private
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|indexQueryParser
name|XContentIndexQueryParser
name|indexQueryParser
decl_stmt|;
DECL|field|namedFilters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Filter
argument_list|>
name|namedFilters
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|queryParser
specifier|private
specifier|final
name|MapperQueryParser
name|queryParser
init|=
operator|new
name|MapperQueryParser
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|field|multiFieldQueryParser
specifier|private
specifier|final
name|MultiFieldMapperQueryParser
name|multiFieldQueryParser
init|=
operator|new
name|MultiFieldMapperQueryParser
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|field|parser
specifier|private
name|XContentParser
name|parser
decl_stmt|;
DECL|method|QueryParseContext
specifier|public
name|QueryParseContext
parameter_list|(
name|Index
name|index
parameter_list|,
name|XContentIndexQueryParser
name|indexQueryParser
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexQueryParser
operator|=
name|indexQueryParser
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|XContentParser
name|jp
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|jp
expr_stmt|;
name|this
operator|.
name|namedFilters
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|parser
specifier|public
name|XContentParser
name|parser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
DECL|method|scriptService
specifier|public
name|ScriptService
name|scriptService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|scriptService
return|;
block|}
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|mapperService
return|;
block|}
DECL|method|indexEngine
specifier|public
name|IndexEngine
name|indexEngine
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|indexEngine
return|;
block|}
DECL|method|similarityService
annotation|@
name|Nullable
specifier|public
name|SimilarityService
name|similarityService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|similarityService
return|;
block|}
DECL|method|searchSimilarity
specifier|public
name|Similarity
name|searchSimilarity
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|similarityService
operator|!=
literal|null
condition|?
name|indexQueryParser
operator|.
name|similarityService
operator|.
name|defaultSearchSimilarity
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|indexCache
specifier|public
name|IndexCache
name|indexCache
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|indexCache
return|;
block|}
DECL|method|singleQueryParser
specifier|public
name|MapperQueryParser
name|singleQueryParser
parameter_list|(
name|QueryParserSettings
name|settings
parameter_list|)
block|{
name|queryParser
operator|.
name|reset
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|queryParser
return|;
block|}
DECL|method|multiQueryParser
specifier|public
name|MultiFieldMapperQueryParser
name|multiQueryParser
parameter_list|(
name|MultiFieldQueryParserSettings
name|settings
parameter_list|)
block|{
name|multiFieldQueryParser
operator|.
name|reset
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|multiFieldQueryParser
return|;
block|}
DECL|method|cacheFilter
specifier|public
name|Filter
name|cacheFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|indexCache
operator|.
name|filter
argument_list|()
operator|.
name|cache
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|addNamedFilter
specifier|public
name|void
name|addNamedFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|namedFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
DECL|method|copyNamedFilters
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Filter
argument_list|>
name|copyNamedFilters
parameter_list|()
block|{
if|if
condition|(
name|namedFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|namedFilters
argument_list|)
return|;
block|}
DECL|method|parseInnerQuery
specifier|public
name|Query
name|parseInnerQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
comment|// move to START object
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
assert|;
name|String
name|queryName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
comment|// move to the next START_OBJECT
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|||
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
assert|;
name|XContentQueryParser
name|queryParser
init|=
name|indexQueryParser
operator|.
name|queryParser
argument_list|(
name|queryName
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"No query registered for ["
operator|+
name|queryName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Query
name|result
init|=
name|queryParser
operator|.
name|parse
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|||
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
comment|// if we are at END_OBJECT, move to the next one...
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|parseInnerFilter
specifier|public
name|Filter
name|parseInnerFilter
parameter_list|()
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
comment|// move to START object
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
assert|;
name|String
name|filterName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
comment|// move to the next START_OBJECT or START_ARRAY
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|||
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
assert|;
name|XContentFilterParser
name|filterParser
init|=
name|indexQueryParser
operator|.
name|filterParser
argument_list|(
name|filterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"No filter registered for ["
operator|+
name|filterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Filter
name|result
init|=
name|filterParser
operator|.
name|parse
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|||
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
comment|// if we are at END_OBJECT, move to the next one...
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|parseInnerFilter
specifier|public
name|Filter
name|parseInnerFilter
parameter_list|(
name|String
name|filterName
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentFilterParser
name|filterParser
init|=
name|indexQueryParser
operator|.
name|filterParser
argument_list|(
name|filterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"No filter registered for ["
operator|+
name|filterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Filter
name|result
init|=
name|filterParser
operator|.
name|parse
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// don't move to the nextToken in this case...
comment|//        if (parser.currentToken() == XContentParser.Token.END_OBJECT || parser.currentToken() == XContentParser.Token.END_ARRAY) {
comment|//            // if we are at END_OBJECT, move to the next one...
comment|//            parser.nextToken();
comment|//        }
return|return
name|result
return|;
block|}
DECL|method|fieldMapper
specifier|public
name|FieldMapper
name|fieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldMappers
name|fieldMappers
init|=
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|smartNameFieldMappers
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fieldMappers
operator|.
name|mapper
argument_list|()
return|;
block|}
DECL|method|indexName
specifier|public
name|String
name|indexName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldMapper
name|smartMapper
init|=
name|fieldMapper
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartMapper
operator|==
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
return|return
name|smartMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
return|;
block|}
DECL|method|smartFieldMappers
specifier|public
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartFieldMappers
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|smartName
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

