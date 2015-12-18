begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Analyzer
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
name|IndexOptions
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
name|LeafReader
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
name|MultiReader
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
name|SlowCompositeReaderWrapper
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
name|ElasticsearchException
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
name|mapper
operator|.
name|ParseContext
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

begin_comment
comment|/**  * Implementation of {@link PercolatorIndex} that can hold multiple Lucene documents by  * opening multiple {@link MemoryIndex} based IndexReaders and wrapping them via a single top level reader.  */
end_comment

begin_class
DECL|class|MultiDocumentPercolatorIndex
class|class
name|MultiDocumentPercolatorIndex
implements|implements
name|PercolatorIndex
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
DECL|method|MultiDocumentPercolatorIndex
name|MultiDocumentPercolatorIndex
parameter_list|(
name|CloseableThreadLocal
argument_list|<
name|MemoryIndex
argument_list|>
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|PercolateContext
name|context
parameter_list|,
name|ParsedDocument
name|parsedDocument
parameter_list|)
block|{
name|IndexReader
index|[]
name|memoryIndices
init|=
operator|new
name|IndexReader
index|[
name|parsedDocument
operator|.
name|docs
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|List
argument_list|<
name|ParseContext
operator|.
name|Document
argument_list|>
name|docs
init|=
name|parsedDocument
operator|.
name|docs
argument_list|()
decl_stmt|;
name|int
name|rootDocIndex
init|=
name|docs
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
assert|assert
name|rootDocIndex
operator|>
literal|0
assert|;
name|MemoryIndex
name|rootDocMemoryIndex
init|=
literal|null
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ParseContext
operator|.
name|Document
name|d
init|=
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|MemoryIndex
name|memoryIndex
decl_stmt|;
if|if
condition|(
name|rootDocIndex
operator|==
name|i
condition|)
block|{
comment|// the last doc is always the rootDoc, since that is usually the biggest document it make sense
comment|// to reuse the MemoryIndex it uses
name|memoryIndex
operator|=
name|rootDocMemoryIndex
operator|=
name|cache
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|memoryIndex
operator|=
operator|new
name|MemoryIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|Analyzer
name|analyzer
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|parsedDocument
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|mappers
argument_list|()
operator|.
name|indexAnalyzer
argument_list|()
decl_stmt|;
name|memoryIndices
index|[
name|i
index|]
operator|=
name|indexDoc
argument_list|(
name|d
argument_list|,
name|analyzer
argument_list|,
name|memoryIndex
argument_list|)
operator|.
name|createSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|MultiReader
name|mReader
init|=
operator|new
name|MultiReader
argument_list|(
name|memoryIndices
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|slowReader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|mReader
argument_list|)
decl_stmt|;
specifier|final
name|IndexSearcher
name|slowSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|slowReader
argument_list|)
decl_stmt|;
name|slowSearcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocSearcher
name|docSearcher
init|=
operator|new
name|DocSearcher
argument_list|(
name|slowSearcher
argument_list|,
name|rootDocMemoryIndex
argument_list|)
decl_stmt|;
name|context
operator|.
name|initialize
argument_list|(
name|docSearcher
argument_list|,
name|parsedDocument
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
name|ElasticsearchException
argument_list|(
literal|"Failed to create index for percolator with nested document "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|indexDoc
name|MemoryIndex
name|indexDoc
parameter_list|(
name|ParseContext
operator|.
name|Document
name|d
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|MemoryIndex
name|memoryIndex
parameter_list|)
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|d
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
operator|&&
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
try|try
block|{
comment|// TODO: instead of passing null here, we can have a CTL<Map<String,TokenStream>> and pass previous,
comment|// like the indexer does
try|try
init|(
name|TokenStream
name|tokenStream
init|=
name|field
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|,
literal|null
argument_list|)
init|)
block|{
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Failed to create token stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|memoryIndex
return|;
block|}
DECL|class|DocSearcher
specifier|private
class|class
name|DocSearcher
extends|extends
name|Engine
operator|.
name|Searcher
block|{
DECL|field|rootDocMemoryIndex
specifier|private
specifier|final
name|MemoryIndex
name|rootDocMemoryIndex
decl_stmt|;
DECL|method|DocSearcher
specifier|private
name|DocSearcher
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|MemoryIndex
name|rootDocMemoryIndex
parameter_list|)
block|{
name|super
argument_list|(
literal|"percolate"
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|rootDocMemoryIndex
operator|=
name|rootDocMemoryIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|reader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|rootDocMemoryIndex
operator|.
name|reset
argument_list|()
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
name|ElasticsearchException
argument_list|(
literal|"failed to close IndexReader in percolator with nested doc"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

