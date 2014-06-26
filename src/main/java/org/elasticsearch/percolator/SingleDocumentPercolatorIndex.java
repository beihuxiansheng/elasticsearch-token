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

begin_comment
comment|/**  * Implementation of {@link PercolatorIndex} that can only hold a single Lucene document  * and is optimized for that  */
end_comment

begin_class
DECL|class|SingleDocumentPercolatorIndex
class|class
name|SingleDocumentPercolatorIndex
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
DECL|method|SingleDocumentPercolatorIndex
name|SingleDocumentPercolatorIndex
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
name|MemoryIndex
name|memoryIndex
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|TokenStream
name|tokenStream
init|=
name|field
operator|.
name|tokenStream
argument_list|(
name|parsedDocument
operator|.
name|analyzer
argument_list|()
argument_list|,
literal|null
argument_list|)
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
name|ElasticsearchException
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
name|initialize
argument_list|(
operator|new
name|DocEngineSearcher
argument_list|(
name|memoryIndex
argument_list|)
argument_list|,
name|parsedDocument
argument_list|)
expr_stmt|;
block|}
DECL|class|DocEngineSearcher
specifier|private
class|class
name|DocEngineSearcher
implements|implements
name|Engine
operator|.
name|Searcher
block|{
DECL|field|searcher
specifier|private
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|memoryIndex
specifier|private
specifier|final
name|MemoryIndex
name|memoryIndex
decl_stmt|;
DECL|method|DocEngineSearcher
specifier|public
name|DocEngineSearcher
parameter_list|(
name|MemoryIndex
name|memoryIndex
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|memoryIndex
operator|=
name|memoryIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|source
specifier|public
name|String
name|source
parameter_list|()
block|{
return|return
literal|"percolate"
return|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|IndexReader
name|reader
parameter_list|()
block|{
return|return
name|searcher
operator|.
name|getIndexReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|searcher
specifier|public
name|IndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|searcher
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
try|try
block|{
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|memoryIndex
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
literal|"failed to close percolator in-memory index"
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

