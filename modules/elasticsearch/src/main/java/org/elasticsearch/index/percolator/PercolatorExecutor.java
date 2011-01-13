begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
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
name|analysis
operator|.
name|TokenStream
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
name|document
operator|.
name|Fieldable
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|memory
operator|.
name|MemoryIndex
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
name|Collector
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
name|IndexSearcher
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
name|Scorer
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
name|MapBuilder
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
name|common
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
name|common
operator|.
name|logging
operator|.
name|ESLogger
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
name|Lucene
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
name|XContentFactory
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
name|engine
operator|.
name|Engine
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
name|field
operator|.
name|data
operator|.
name|FieldData
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|*
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
name|IndexQueryParserService
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
name|service
operator|.
name|IndexService
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
name|shard
operator|.
name|service
operator|.
name|IndexShard
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
name|io
operator|.
name|Reader
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
name|index
operator|.
name|mapper
operator|.
name|SourceToParse
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|PercolatorExecutor
specifier|public
class|class
name|PercolatorExecutor
extends|extends
name|AbstractIndexComponent
block|{
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
block|{
DECL|field|source
specifier|private
specifier|final
name|byte
index|[]
name|source
decl_stmt|;
DECL|field|offset
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|this
argument_list|(
name|source
argument_list|,
literal|0
argument_list|,
name|source
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|Request
specifier|public
name|Request
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
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
DECL|method|source
specifier|public
name|byte
index|[]
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
block|}
DECL|class|Response
specifier|public
specifier|static
specifier|final
class|class
name|Response
block|{
DECL|field|matches
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|matches
decl_stmt|;
DECL|field|mappersAdded
specifier|private
specifier|final
name|boolean
name|mappersAdded
decl_stmt|;
DECL|method|Response
specifier|public
name|Response
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|matches
parameter_list|,
name|boolean
name|mappersAdded
parameter_list|)
block|{
name|this
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
name|this
operator|.
name|mappersAdded
operator|=
name|mappersAdded
expr_stmt|;
block|}
DECL|method|mappersAdded
specifier|public
name|boolean
name|mappersAdded
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappersAdded
return|;
block|}
DECL|method|matches
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|matches
parameter_list|()
block|{
return|return
name|matches
return|;
block|}
block|}
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|queryParserService
specifier|private
specifier|final
name|IndexQueryParserService
name|queryParserService
decl_stmt|;
DECL|field|queries
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|queries
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|method|PercolatorExecutor
annotation|@
name|Inject
specifier|public
name|PercolatorExecutor
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
name|IndexQueryParserService
name|queryParserService
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
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|queryParserService
operator|=
name|queryParserService
expr_stmt|;
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|old
init|=
name|queries
decl_stmt|;
name|queries
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|()
expr_stmt|;
name|old
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|addQuery
specifier|public
name|void
name|addQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|QueryBuilder
name|queryBuilder
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|smileBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|FastByteArrayOutputStream
name|unsafeBytes
init|=
name|builder
operator|.
name|unsafeStream
argument_list|()
decl_stmt|;
name|addQuery
argument_list|(
name|name
argument_list|,
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Failed to add query ["
operator|+
name|name
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|addQuery
specifier|public
name|void
name|addQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|addQuery
argument_list|(
name|name
argument_list|,
name|source
argument_list|,
literal|0
argument_list|,
name|source
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|addQuery
specifier|public
name|void
name|addQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|source
parameter_list|,
name|int
name|sourceOffset
parameter_list|,
name|int
name|sourceLength
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
name|sourceOffset
argument_list|,
name|sourceLength
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|,
name|sourceOffset
argument_list|,
name|sourceLength
argument_list|)
expr_stmt|;
name|Query
name|query
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|IndexQueryParser
name|queryParser
init|=
name|queryParserService
operator|.
name|defaultIndexQueryParser
argument_list|()
decl_stmt|;
name|query
operator|=
name|queryParser
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
operator|.
name|query
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|addQuery
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Failed to add query ["
operator|+
name|name
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
DECL|method|addQuery
specifier|public
specifier|synchronized
name|void
name|addQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|queries
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|queries
argument_list|)
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|removeQuery
specifier|public
specifier|synchronized
name|void
name|removeQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|queries
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|queries
argument_list|)
operator|.
name|remove
argument_list|(
name|name
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|percolate
specifier|public
name|Response
name|percolate
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|percolate
argument_list|(
name|request
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|percolate
specifier|public
name|Response
name|percolate
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|IndexService
name|percolatorIndex
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|IndexShard
name|percolatorShard
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|ParsedDocument
name|doc
init|=
literal|null
decl_stmt|;
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
name|request
operator|.
name|source
argument_list|()
argument_list|)
operator|.
name|createParser
argument_list|(
name|request
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|IndexQueryParser
name|queryParser
init|=
name|queryParserService
operator|.
name|defaultIndexQueryParser
argument_list|()
decl_stmt|;
name|query
operator|=
name|queryParser
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
operator|.
name|query
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// the first level should be the type
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
name|type
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|mapperService
operator|.
name|documentMapperWithAutoCreate
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|doc
operator|=
name|docMapper
operator|.
name|parse
argument_list|(
name|source
argument_list|(
name|parser
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
operator|.
name|flyweight
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PercolatorException
argument_list|(
name|index
argument_list|,
literal|"failed to parse request"
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
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PercolatorException
argument_list|(
name|index
argument_list|,
literal|"No doc to percolate in the request"
argument_list|)
throw|;
block|}
comment|// first, parse the source doc into a MemoryIndex
specifier|final
name|MemoryIndex
name|memoryIndex
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|TokenStream
name|tokenStream
init|=
name|field
operator|.
name|tokenStreamValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenStream
operator|!=
literal|null
condition|)
block|{
name|memoryIndex
operator|.
name|addField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|tokenStream
argument_list|,
name|field
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Reader
name|reader
init|=
name|field
operator|.
name|readerValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|memoryIndex
operator|.
name|addField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|doc
operator|.
name|analyzer
argument_list|()
operator|.
name|reusableTokenStream
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|reader
argument_list|)
argument_list|,
name|field
operator|.
name|getBoost
argument_list|()
operator|*
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Failed to analyze field ["
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
name|value
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|memoryIndex
operator|.
name|addField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|doc
operator|.
name|analyzer
argument_list|()
operator|.
name|reusableTokenStream
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|FastStringReader
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|field
operator|.
name|getBoost
argument_list|()
operator|*
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Failed to analyze field ["
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
specifier|final
name|IndexSearcher
name|searcher
init|=
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|matches
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|Lucene
operator|.
name|ExistsCollector
name|collector
init|=
operator|new
name|Lucene
operator|.
name|ExistsCollector
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|entry
range|:
name|queries
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
block|{
name|searcher
operator|.
name|search
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"] failed to execute query"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collector
operator|.
name|exists
argument_list|()
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|Engine
operator|.
name|Searcher
name|percolatorSearcher
init|=
name|percolatorShard
operator|.
name|searcher
argument_list|()
decl_stmt|;
try|try
block|{
name|percolatorSearcher
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|QueryCollector
argument_list|(
name|logger
argument_list|,
name|queries
argument_list|,
name|searcher
argument_list|,
name|percolatorIndex
argument_list|,
name|matches
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to execute"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|percolatorSearcher
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Response
argument_list|(
name|matches
argument_list|,
name|doc
operator|.
name|mappersAdded
argument_list|()
argument_list|)
return|;
block|}
DECL|class|QueryCollector
specifier|static
class|class
name|QueryCollector
extends|extends
name|Collector
block|{
DECL|field|searcher
specifier|private
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|percolatorIndex
specifier|private
specifier|final
name|IndexService
name|percolatorIndex
decl_stmt|;
DECL|field|matches
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|matches
decl_stmt|;
DECL|field|queries
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|queries
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|collector
specifier|private
specifier|final
name|Lucene
operator|.
name|ExistsCollector
name|collector
init|=
operator|new
name|Lucene
operator|.
name|ExistsCollector
argument_list|()
decl_stmt|;
DECL|method|QueryCollector
name|QueryCollector
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|queries
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|IndexService
name|percolatorIndex
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|matches
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|queries
operator|=
name|queries
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|percolatorIndex
operator|=
name|percolatorIndex
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
block|}
DECL|field|fieldData
specifier|private
name|FieldData
name|fieldData
decl_stmt|;
DECL|method|setScorer
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{         }
DECL|method|collect
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|fieldData
operator|.
name|stringValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|queries
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
comment|// log???
return|return;
block|}
comment|// run the query
try|try
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
if|if
condition|(
name|collector
operator|.
name|exists
argument_list|()
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"["
operator|+
name|id
operator|+
literal|"] failed to execute query"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setNextReader
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldData
operator|=
name|percolatorIndex
operator|.
name|cache
argument_list|()
operator|.
name|fieldData
argument_list|()
operator|.
name|cache
argument_list|(
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|STRING
argument_list|,
name|reader
argument_list|,
name|IdFieldMapper
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|acceptsDocsOutOfOrder
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

