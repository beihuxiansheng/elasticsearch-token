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
name|core
operator|.
name|WhitespaceAnalyzer
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
name|FieldType
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
name|StoredField
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|NoMergePolicy
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
name|Term
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|TermQuery
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
name|TopDocs
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
name|store
operator|.
name|Directory
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|analysis
operator|.
name|AnalyzerProvider
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
name|CharFilterFactory
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
name|TokenFilterFactory
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
name|TokenizerFactory
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
name|Uid
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
name|PercolatorFieldMapper
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
name|PercolatorQueriesRegistry
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
name|ExtractQueryTermsService
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
name|ShardId
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
name|indices
operator|.
name|IndicesModule
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
name|SearchShardTarget
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
name|ContextIndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Collections
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|PercolatorServiceTests
specifier|public
class|class
name|PercolatorServiceTests
extends|extends
name|ESTestCase
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|IndexWriter
name|indexWriter
decl_stmt|;
DECL|field|directoryReader
specifier|private
name|DirectoryReader
name|directoryReader
decl_stmt|;
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|config
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
name|directoryReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCount
specifier|public
name|void
name|testCount
parameter_list|()
throws|throws
name|Exception
block|{
name|PercolateContext
name|context
init|=
name|mock
argument_list|(
name|PercolateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|shardTarget
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SearchShardTarget
argument_list|(
literal|"_id"
argument_list|,
literal|"_index"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|percolatorTypeFilter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|isOnlyCount
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PercolatorQueriesRegistry
name|registry
init|=
name|createRegistry
argument_list|()
decl_stmt|;
name|addPercolatorQuery
argument_list|(
literal|"1"
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
argument_list|,
name|indexWriter
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|addPercolatorQuery
argument_list|(
literal|"2"
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
argument_list|,
name|indexWriter
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|addPercolatorQuery
argument_list|(
literal|"3"
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"monkey"
argument_list|)
argument_list|)
argument_list|,
name|indexWriter
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|IndexSearcher
name|shardSearcher
init|=
name|newSearcher
argument_list|(
name|directoryReader
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ContextIndexSearcher
argument_list|(
operator|new
name|Engine
operator|.
name|Searcher
argument_list|(
literal|"test"
argument_list|,
name|shardSearcher
argument_list|)
argument_list|,
name|shardSearcher
operator|.
name|getQueryCache
argument_list|()
argument_list|,
name|shardSearcher
operator|.
name|getQueryCachingPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|MemoryIndex
name|memoryIndex
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|memoryIndex
operator|.
name|addField
argument_list|(
literal|"field"
argument_list|,
literal|"the quick brown fox jumps over the lazy dog"
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
name|percolateSearcher
init|=
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|docSearcher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|percolateSearcher
argument_list|)
expr_stmt|;
name|PercolateShardResponse
name|response
init|=
name|PercolatorService
operator|.
name|doPercolate
argument_list|(
name|context
argument_list|,
name|registry
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|topDocs
argument_list|()
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTopMatching
specifier|public
name|void
name|testTopMatching
parameter_list|()
throws|throws
name|Exception
block|{
name|PercolateContext
name|context
init|=
name|mock
argument_list|(
name|PercolateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|shardTarget
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SearchShardTarget
argument_list|(
literal|"_id"
argument_list|,
literal|"_index"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|percolatorTypeFilter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PercolatorQueriesRegistry
name|registry
init|=
name|createRegistry
argument_list|()
decl_stmt|;
name|addPercolatorQuery
argument_list|(
literal|"1"
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
argument_list|,
name|indexWriter
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|addPercolatorQuery
argument_list|(
literal|"2"
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"monkey"
argument_list|)
argument_list|)
argument_list|,
name|indexWriter
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|addPercolatorQuery
argument_list|(
literal|"3"
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
argument_list|,
name|indexWriter
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|IndexSearcher
name|shardSearcher
init|=
name|newSearcher
argument_list|(
name|directoryReader
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ContextIndexSearcher
argument_list|(
operator|new
name|Engine
operator|.
name|Searcher
argument_list|(
literal|"test"
argument_list|,
name|shardSearcher
argument_list|)
argument_list|,
name|shardSearcher
operator|.
name|getQueryCache
argument_list|()
argument_list|,
name|shardSearcher
operator|.
name|getQueryCachingPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|MemoryIndex
name|memoryIndex
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|memoryIndex
operator|.
name|addField
argument_list|(
literal|"field"
argument_list|,
literal|"the quick brown fox jumps over the lazy dog"
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
name|percolateSearcher
init|=
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|docSearcher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|percolateSearcher
argument_list|)
expr_stmt|;
name|PercolateShardResponse
name|response
init|=
name|PercolatorService
operator|.
name|doPercolate
argument_list|(
name|context
argument_list|,
name|registry
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|response
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addPercolatorQuery
name|void
name|addPercolatorQuery
parameter_list|(
name|String
name|id
parameter_list|,
name|Query
name|query
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|PercolatorQueriesRegistry
name|registry
parameter_list|)
throws|throws
name|IOException
block|{
name|registry
operator|.
name|getPercolateQueries
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|id
argument_list|)
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|ParseContext
operator|.
name|Document
name|document
init|=
operator|new
name|ParseContext
operator|.
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|extractedQueryTermsFieldType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|extractedQueryTermsFieldType
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|extractedQueryTermsFieldType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|extractedQueryTermsFieldType
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|ExtractQueryTermsService
operator|.
name|extractQueryTerms
argument_list|(
name|query
argument_list|,
name|document
argument_list|,
name|PercolatorFieldMapper
operator|.
name|EXTRACTED_TERMS_FULL_FIELD_NAME
argument_list|,
name|PercolatorFieldMapper
operator|.
name|UNKNOWN_QUERY_FULL_FIELD_NAME
argument_list|,
name|extractedQueryTermsFieldType
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
DECL|method|createRegistry
name|PercolatorQueriesRegistry
name|createRegistry
parameter_list|()
block|{
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"_index"
argument_list|)
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
operator|new
name|IndexMetaData
operator|.
name|Builder
argument_list|(
literal|"_index"
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PercolatorQueriesRegistry
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
literal|0
argument_list|)
argument_list|,
name|indexSettings
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

