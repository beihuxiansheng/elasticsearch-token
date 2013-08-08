begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
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
name|index
operator|.
name|IndexableField
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
name|ExtendedMemoryIndex
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
name|util
operator|.
name|CloseableThreadLocal
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
name|ElasticSearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
operator|.
name|PercolateShardRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
operator|.
name|PercolateShardResponse
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
name|bytes
operator|.
name|BytesReference
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
name|component
operator|.
name|AbstractComponent
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
name|lucene
operator|.
name|search
operator|.
name|XConstantScoreQuery
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
name|text
operator|.
name|Text
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
name|unit
operator|.
name|ByteSizeUnit
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
name|unit
operator|.
name|ByteSizeValue
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|DocumentMapper
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
name|mapper
operator|.
name|ParsedDocument
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
name|internal
operator|.
name|UidFieldMapper
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
name|percolator
operator|.
name|stats
operator|.
name|ShardPercolateService
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
name|shard
operator|.
name|service
operator|.
name|IndexShard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|concurrent
operator|.
name|ConcurrentMap
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
name|source
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
operator|.
name|QueryCollector
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|PercolatorService
specifier|public
class|class
name|PercolatorService
extends|extends
name|AbstractComponent
block|{
DECL|field|cache
specifier|private
specifier|final
name|CloseableThreadLocal
argument_list|<
name|MemoryIndex
argument_list|>
name|cache
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
annotation|@
name|Inject
DECL|method|PercolatorService
specifier|public
name|PercolatorService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
specifier|final
name|long
name|maxReuseBytes
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"indices.memory.memory_index.size_per_thread"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|1
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|cache
operator|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|MemoryIndex
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|MemoryIndex
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ExtendedMemoryIndex
argument_list|(
literal|false
argument_list|,
name|maxReuseBytes
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|matchPercolate
specifier|public
name|PercolateShardResponse
name|matchPercolate
parameter_list|(
specifier|final
name|PercolateShardRequest
name|request
parameter_list|)
block|{
return|return
name|preparePercolate
argument_list|(
name|request
argument_list|,
operator|new
name|PercolateAction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PercolateShardResponse
name|doPercolateAction
parameter_list|(
name|PercolateContext
name|context
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Text
argument_list|>
name|matches
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|query
operator|==
literal|null
condition|)
block|{
name|matches
operator|=
operator|new
name|ArrayList
argument_list|<
name|Text
argument_list|>
argument_list|()
expr_stmt|;
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
name|Text
argument_list|,
name|Query
argument_list|>
name|entry
range|:
name|context
operator|.
name|percolateQueries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|collector
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|context
operator|.
name|docSearcher
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
if|if
condition|(
operator|!
name|context
operator|.
name|limit
operator|||
name|count
operator|<
name|context
operator|.
name|size
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
name|count
operator|++
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
name|context
operator|.
name|indexShard
operator|.
name|searcher
argument_list|()
decl_stmt|;
try|try
block|{
name|Match
name|match
init|=
name|match
argument_list|(
name|logger
argument_list|,
name|context
operator|.
name|percolateQueries
argument_list|,
name|context
operator|.
name|docSearcher
argument_list|,
name|context
operator|.
name|fieldDataService
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|percolatorSearcher
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|query
argument_list|,
name|match
argument_list|)
expr_stmt|;
name|matches
operator|=
name|match
operator|.
name|matches
argument_list|()
expr_stmt|;
name|count
operator|=
name|match
operator|.
name|counter
argument_list|()
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
name|debug
argument_list|(
literal|"failed to execute"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PercolateException
argument_list|(
name|context
operator|.
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|,
literal|"failed to execute"
argument_list|,
name|e
argument_list|)
throw|;
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
name|PercolateShardResponse
argument_list|(
name|matches
operator|.
name|toArray
argument_list|(
operator|new
name|Text
index|[
name|matches
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|count
argument_list|,
name|context
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|countPercolate
specifier|public
name|PercolateShardResponse
name|countPercolate
parameter_list|(
specifier|final
name|PercolateShardRequest
name|request
parameter_list|)
block|{
return|return
name|preparePercolate
argument_list|(
name|request
argument_list|,
operator|new
name|PercolateAction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PercolateShardResponse
name|doPercolateAction
parameter_list|(
name|PercolateContext
name|context
parameter_list|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|context
operator|.
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
name|Text
argument_list|,
name|Query
argument_list|>
name|entry
range|:
name|context
operator|.
name|percolateQueries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|collector
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|context
operator|.
name|docSearcher
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
name|count
operator|++
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
name|context
operator|.
name|indexShard
operator|.
name|searcher
argument_list|()
decl_stmt|;
try|try
block|{
name|Count
name|countCollector
init|=
name|count
argument_list|(
name|logger
argument_list|,
name|context
operator|.
name|percolateQueries
argument_list|,
name|context
operator|.
name|docSearcher
argument_list|,
name|context
operator|.
name|fieldDataService
argument_list|)
decl_stmt|;
name|percolatorSearcher
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|query
argument_list|,
name|countCollector
argument_list|)
expr_stmt|;
name|count
operator|=
name|countCollector
operator|.
name|counter
argument_list|()
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
name|PercolateShardResponse
argument_list|(
name|count
argument_list|,
name|context
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|preparePercolate
specifier|private
name|PercolateShardResponse
name|preparePercolate
parameter_list|(
name|PercolateShardRequest
name|request
parameter_list|,
name|PercolateAction
name|action
parameter_list|)
block|{
name|IndexService
name|percolateIndexService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|percolateIndexService
operator|.
name|shardSafe
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
name|ShardPercolateService
name|shardPercolateService
init|=
name|indexShard
operator|.
name|shardPercolateService
argument_list|()
decl_stmt|;
name|shardPercolateService
operator|.
name|prePercolate
argument_list|()
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
name|ConcurrentMap
argument_list|<
name|Text
argument_list|,
name|Query
argument_list|>
name|percolateQueries
init|=
name|indexShard
operator|.
name|percolateRegistry
argument_list|()
operator|.
name|percolateQueries
argument_list|()
decl_stmt|;
if|if
condition|(
name|percolateQueries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|PercolateShardResponse
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
return|;
block|}
specifier|final
name|PercolateContext
name|context
init|=
operator|new
name|PercolateContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|percolateQueries
operator|=
name|percolateQueries
expr_stmt|;
name|context
operator|.
name|indexShard
operator|=
name|indexShard
expr_stmt|;
name|ParsedDocument
name|parsedDocument
init|=
name|parsePercolate
argument_list|(
name|percolateIndexService
argument_list|,
name|request
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|docSource
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|docSource
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|parsedDocument
operator|=
name|parseFetchedDoc
argument_list|(
name|request
operator|.
name|docSource
argument_list|()
argument_list|,
name|percolateIndexService
argument_list|,
name|request
operator|.
name|documentType
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parsedDocument
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"No doc to percolate in the request"
argument_list|)
throw|;
block|}
if|if
condition|(
name|context
operator|.
name|size
operator|<
literal|0
condition|)
block|{
name|context
operator|.
name|size
operator|=
literal|0
expr_stmt|;
block|}
comment|// first, parse the source doc into a MemoryIndex
specifier|final
name|MemoryIndex
name|memoryIndex
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
comment|// TODO: This means percolation does not support nested docs...
comment|// So look into: ByteBufferDirectory
for|for
control|(
name|IndexableField
name|field
range|:
name|parsedDocument
operator|.
name|rootDoc
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
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// no need to index the UID field
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|TokenStream
name|tokenStream
decl_stmt|;
try|try
block|{
name|tokenStream
operator|=
name|field
operator|.
name|tokenStream
argument_list|(
name|parsedDocument
operator|.
name|analyzer
argument_list|()
argument_list|)
expr_stmt|;
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
name|boost
argument_list|()
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
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Failed to create token stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|context
operator|.
name|docSearcher
operator|=
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
expr_stmt|;
name|context
operator|.
name|fieldDataService
operator|=
name|percolateIndexService
operator|.
name|fieldData
argument_list|()
expr_stmt|;
name|IndexCache
name|indexCache
init|=
name|percolateIndexService
operator|.
name|cache
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|action
operator|.
name|doPercolateAction
argument_list|(
name|context
argument_list|)
return|;
block|}
finally|finally
block|{
comment|// explicitly clear the reader, since we can only register on callback on SegmentReader
name|indexCache
operator|.
name|clear
argument_list|(
name|context
operator|.
name|docSearcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|fieldDataService
operator|.
name|clear
argument_list|(
name|context
operator|.
name|docSearcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|memoryIndex
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|shardPercolateService
operator|.
name|postPercolate
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parsePercolate
specifier|private
name|ParsedDocument
name|parsePercolate
parameter_list|(
name|IndexService
name|documentIndexService
parameter_list|,
name|PercolateShardRequest
name|request
parameter_list|,
name|PercolateContext
name|context
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|BytesReference
name|source
init|=
name|request
operator|.
name|source
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
operator|||
name|source
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
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
comment|// we need to check the "doc" here, so the next token will be START_OBJECT which is
comment|// the actual document starting
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
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"Either specify doc or get, not both"
argument_list|)
throw|;
block|}
name|MapperService
name|mapperService
init|=
name|documentIndexService
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|mapperService
operator|.
name|documentMapperWithAutoCreate
argument_list|(
name|request
operator|.
name|documentType
argument_list|()
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
name|request
operator|.
name|documentType
argument_list|()
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
if|if
condition|(
name|context
operator|.
name|query
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"Either specify query or filter, not both"
argument_list|)
throw|;
block|}
name|context
operator|.
name|query
operator|=
name|documentIndexService
operator|.
name|queryParserService
argument_list|()
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
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|query
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"Either specify query or filter, not both"
argument_list|)
throw|;
block|}
name|Filter
name|filter
init|=
name|documentIndexService
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
operator|.
name|filter
argument_list|()
decl_stmt|;
name|context
operator|.
name|query
operator|=
operator|new
name|XConstantScoreQuery
argument_list|(
name|filter
argument_list|)
expr_stmt|;
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
literal|"size"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|context
operator|.
name|limit
operator|=
literal|true
expr_stmt|;
name|context
operator|.
name|size
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"size is set to ["
operator|+
name|context
operator|.
name|size
operator|+
literal|"] and is expected to be higher or equal to 0"
argument_list|)
throw|;
block|}
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
name|ElasticSearchParseException
argument_list|(
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
return|return
name|doc
return|;
block|}
DECL|method|parseFetchedDoc
specifier|private
name|ParsedDocument
name|parseFetchedDoc
parameter_list|(
name|BytesReference
name|fetchedDoc
parameter_list|,
name|IndexService
name|documentIndexService
parameter_list|,
name|String
name|type
parameter_list|)
block|{
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
name|fetchedDoc
argument_list|)
operator|.
name|createParser
argument_list|(
name|fetchedDoc
argument_list|)
expr_stmt|;
name|MapperService
name|mapperService
init|=
name|documentIndexService
operator|.
name|mapperService
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
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
name|ElasticSearchParseException
argument_list|(
literal|"No doc to percolate in the request"
argument_list|)
throw|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|interface|PercolateAction
interface|interface
name|PercolateAction
block|{
DECL|method|doPercolateAction
name|PercolateShardResponse
name|doPercolateAction
parameter_list|(
name|PercolateContext
name|context
parameter_list|)
function_decl|;
block|}
DECL|class|PercolateContext
specifier|public
class|class
name|PercolateContext
block|{
DECL|field|limit
specifier|public
name|boolean
name|limit
decl_stmt|;
DECL|field|size
specifier|public
name|int
name|size
decl_stmt|;
DECL|field|query
name|Query
name|query
decl_stmt|;
DECL|field|percolateQueries
name|ConcurrentMap
argument_list|<
name|Text
argument_list|,
name|Query
argument_list|>
name|percolateQueries
decl_stmt|;
DECL|field|docSearcher
name|IndexSearcher
name|docSearcher
decl_stmt|;
DECL|field|indexShard
name|IndexShard
name|indexShard
decl_stmt|;
DECL|field|fieldDataService
name|IndexFieldDataService
name|fieldDataService
decl_stmt|;
block|}
DECL|class|Constants
specifier|public
specifier|static
specifier|final
class|class
name|Constants
block|{
DECL|field|TYPE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_NAME
init|=
literal|"_percolator"
decl_stmt|;
block|}
block|}
end_class

end_unit

