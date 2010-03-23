begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|json
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
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
name|codehaus
operator|.
name|jackson
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
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
name|filter
operator|.
name|FilterCache
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
name|util
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
name|io
operator|.
name|FastCharArrayReader
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
name|FastCharArrayWriter
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
name|FastStringReader
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
name|json
operator|.
name|Jackson
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|JsonIndexQueryParser
specifier|public
class|class
name|JsonIndexQueryParser
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
DECL|field|JSON_QUERY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|JSON_QUERY_PREFIX
init|=
literal|"index.queryparser.json.query"
decl_stmt|;
DECL|field|JSON_FILTER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|JSON_FILTER_PREFIX
init|=
literal|"index.queryparser.json.filter"
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
name|JsonQueryParseContext
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
name|JsonQueryParseContext
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
name|JsonQueryParseContext
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
name|JsonQueryParseContext
argument_list|>
argument_list|(
operator|new
name|JsonQueryParseContext
argument_list|(
name|index
argument_list|,
name|queryParserRegistry
argument_list|,
name|mapperService
argument_list|,
name|filterCache
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|jsonFactory
specifier|private
specifier|final
name|JsonFactory
name|jsonFactory
init|=
name|Jackson
operator|.
name|defaultJsonFactory
argument_list|()
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
DECL|field|filterCache
specifier|private
specifier|final
name|FilterCache
name|filterCache
decl_stmt|;
DECL|field|queryParserRegistry
specifier|private
specifier|final
name|JsonQueryParserRegistry
name|queryParserRegistry
decl_stmt|;
DECL|method|JsonIndexQueryParser
annotation|@
name|Inject
specifier|public
name|JsonIndexQueryParser
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
name|FilterCache
name|filterCache
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|JsonQueryParserFactory
argument_list|>
name|jsonQueryParsers
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|JsonFilterParserFactory
argument_list|>
name|jsonFilterParsers
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
name|filterCache
operator|=
name|filterCache
expr_stmt|;
name|List
argument_list|<
name|JsonQueryParser
argument_list|>
name|queryParsers
init|=
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|jsonQueryParsers
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
name|jsonQueryParserGroups
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
name|JsonIndexQueryParser
operator|.
name|Defaults
operator|.
name|JSON_QUERY_PREFIX
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
name|JsonQueryParserFactory
argument_list|>
name|entry
range|:
name|jsonQueryParsers
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
name|JsonQueryParserFactory
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
name|jsonQueryParserGroups
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
name|JsonFilterParser
argument_list|>
name|filterParsers
init|=
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|jsonFilterParsers
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
name|jsonFilterParserGroups
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
name|JsonIndexQueryParser
operator|.
name|Defaults
operator|.
name|JSON_FILTER_PREFIX
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
name|JsonFilterParserFactory
argument_list|>
name|entry
range|:
name|jsonFilterParsers
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
name|JsonFilterParserFactory
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
name|jsonFilterParserGroups
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
name|JsonQueryParserRegistry
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
name|JsonQueryParserRegistry
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
name|JsonParser
name|jp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FastCharArrayWriter
name|unsafeChars
init|=
name|queryBuilder
operator|.
name|buildAsUnsafeChars
argument_list|()
decl_stmt|;
name|jp
operator|=
name|jsonFactory
operator|.
name|createJsonParser
argument_list|(
operator|new
name|FastCharArrayReader
argument_list|(
name|unsafeChars
operator|.
name|unsafeCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|unsafeChars
operator|.
name|size
argument_list|()
argument_list|)
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
name|jp
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
name|jp
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|jp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
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
name|JsonParser
name|jp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jp
operator|=
name|jsonFactory
operator|.
name|createJsonParser
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
name|jp
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
name|jp
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|jp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
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
name|JsonParser
name|jp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jp
operator|=
name|jsonFactory
operator|.
name|createJsonParser
argument_list|(
operator|new
name|FastStringReader
argument_list|(
name|source
argument_list|)
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
name|jp
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
name|jp
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|jp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|JsonParser
name|jsonParser
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
name|jsonParser
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
name|JsonQueryParseContext
name|parseContext
parameter_list|,
name|JsonParser
name|jsonParser
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
name|jsonParser
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

