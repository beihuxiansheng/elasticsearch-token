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
name|ElasticSearchException
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
name|AbstractIndexComponent
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
name|analysis
operator|.
name|AnalysisService
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
name|IndexQueryParser
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
name|QueryBuilder
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
name|settings
operator|.
name|IndexSettings
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
name|util
operator|.
name|ThreadLocals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|XContentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|XContentParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentIndexQueryParser
specifier|public
class|class
name|XContentIndexQueryParser
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexQueryParser
block|{
DECL|class|Defaults
specifier|public
specifier|static
specifier|final
class|class
name|Defaults
block|{
DECL|field|QUERY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_PREFIX
init|=
literal|"index.queryparser.query"
decl_stmt|;
DECL|field|FILTER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FILTER_PREFIX
init|=
literal|"index.queryparser.filter"
decl_stmt|;
block|}
DECL|field|cache
specifier|private
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|QueryParseContext
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|QueryParseContext
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|QueryParseContext
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|QueryParseContext
argument_list|>
argument_list|(
operator|new
name|QueryParseContext
argument_list|(
name|index
argument_list|,
name|queryParserRegistry
argument_list|,
name|mapperService
argument_list|,
name|similarityService
argument_list|,
name|indexCache
argument_list|,
name|indexEngine
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|similarityService
specifier|private
specifier|final
name|SimilarityService
name|similarityService
decl_stmt|;
DECL|field|indexCache
specifier|private
specifier|final
name|IndexCache
name|indexCache
decl_stmt|;
DECL|field|indexEngine
specifier|private
specifier|final
name|IndexEngine
name|indexEngine
decl_stmt|;
DECL|field|queryParserRegistry
specifier|private
specifier|final
name|XContentQueryParserRegistry
name|queryParserRegistry
decl_stmt|;
DECL|method|XContentIndexQueryParser
annotation|@
name|Inject
specifier|public
name|XContentIndexQueryParser
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|IndexEngine
name|indexEngine
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|,
annotation|@
name|Nullable
name|SimilarityService
name|similarityService
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|XContentQueryParserFactory
argument_list|>
name|namedQueryParsers
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|XContentFilterParserFactory
argument_list|>
name|namedFilterParsers
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|similarityService
operator|=
name|similarityService
expr_stmt|;
name|this
operator|.
name|indexCache
operator|=
name|indexCache
expr_stmt|;
name|this
operator|.
name|indexEngine
operator|=
name|indexEngine
expr_stmt|;
name|List
argument_list|<
name|XContentQueryParser
argument_list|>
name|queryParsers
init|=
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|namedQueryParsers
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|queryParserGroups
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
name|XContentIndexQueryParser
operator|.
name|Defaults
operator|.
name|QUERY_PREFIX
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|XContentQueryParserFactory
argument_list|>
name|entry
range|:
name|namedQueryParsers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|queryParserName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|XContentQueryParserFactory
name|queryParserFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Settings
name|queryParserSettings
init|=
name|queryParserGroups
operator|.
name|get
argument_list|(
name|queryParserName
argument_list|)
decl_stmt|;
name|queryParsers
operator|.
name|add
argument_list|(
name|queryParserFactory
operator|.
name|create
argument_list|(
name|queryParserName
argument_list|,
name|queryParserSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|XContentFilterParser
argument_list|>
name|filterParsers
init|=
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|namedFilterParsers
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|filterParserGroups
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
name|XContentIndexQueryParser
operator|.
name|Defaults
operator|.
name|FILTER_PREFIX
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|XContentFilterParserFactory
argument_list|>
name|entry
range|:
name|namedFilterParsers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|filterParserName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|XContentFilterParserFactory
name|filterParserFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Settings
name|filterParserSettings
init|=
name|filterParserGroups
operator|.
name|get
argument_list|(
name|filterParserName
argument_list|)
decl_stmt|;
name|filterParsers
operator|.
name|add
argument_list|(
name|filterParserFactory
operator|.
name|create
argument_list|(
name|filterParserName
argument_list|,
name|filterParserSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|queryParserRegistry
operator|=
operator|new
name|XContentQueryParserRegistry
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|analysisService
argument_list|,
name|queryParsers
argument_list|,
name|filterParsers
argument_list|)
expr_stmt|;
block|}
DECL|method|name
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|queryParserRegistry
specifier|public
name|XContentQueryParserRegistry
name|queryParserRegistry
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryParserRegistry
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FastByteArrayOutputStream
name|unsafeBytes
init|=
name|queryBuilder
operator|.
name|buildAsUnsafeBytes
argument_list|()
decl_stmt|;
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|unsafeBytes
operator|.
name|unsafeByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|unsafeBytes
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|createParser
argument_list|(
name|unsafeBytes
operator|.
name|unsafeByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|unsafeBytes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parse
argument_list|(
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParsingException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|parse
argument_list|(
name|source
argument_list|,
literal|0
argument_list|,
name|source
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|parse
argument_list|(
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParsingException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|parse
argument_list|(
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParsingException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Failed to parse ["
operator|+
name|source
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
try|try
block|{
return|return
name|parse
argument_list|(
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|parse
specifier|private
name|Query
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|parseContext
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
return|;
block|}
block|}
end_class

end_unit

