begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|significant
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
name|standard
operator|.
name|StandardAnalyzer
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
name|Document
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
name|Field
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
name|SortedNumericDocValuesField
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
name|RandomIndexWriter
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
name|index
operator|.
name|analysis
operator|.
name|AnalyzerScope
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
name|NamedAnalyzer
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
name|MappedFieldType
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
name|NumberFieldMapper
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
name|SourceFieldMapper
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
name|TextFieldMapper
operator|.
name|TextFieldType
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
name|QueryShardContext
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
name|QueryShardException
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
name|aggregations
operator|.
name|AggregatorTestCase
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
name|aggregations
operator|.
name|bucket
operator|.
name|sampler
operator|.
name|Sampler
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
name|aggregations
operator|.
name|bucket
operator|.
name|sampler
operator|.
name|SamplerAggregationBuilder
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
name|aggregations
operator|.
name|bucket
operator|.
name|significant
operator|.
name|SignificantTerms
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
name|aggregations
operator|.
name|bucket
operator|.
name|significant
operator|.
name|SignificantTerms
operator|.
name|Bucket
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
name|aggregations
operator|.
name|bucket
operator|.
name|significant
operator|.
name|SignificantTextAggregationBuilder
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
name|Arrays
import|;
end_import

begin_class
DECL|class|SignificantTextAggregatorTests
specifier|public
class|class
name|SignificantTextAggregatorTests
extends|extends
name|AggregatorTestCase
block|{
comment|/**      * Uses the significant text aggregation to find the keywords in text fields      */
DECL|method|testSignificance
specifier|public
name|void
name|testSignificance
parameter_list|()
throws|throws
name|IOException
block|{
name|TextFieldType
name|textFieldType
init|=
operator|new
name|TextFieldType
argument_list|()
decl_stmt|;
name|textFieldType
operator|.
name|setName
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|textFieldType
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"my_analyzer"
argument_list|,
name|AnalyzerScope
operator|.
name|GLOBAL
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|indexWriterConfig
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|;
name|IndexWriter
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|indexWriterConfig
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"common "
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
literal|"odd "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|text
operator|.
name|append
argument_list|(
literal|"even separator"
operator|+
name|i
operator|+
literal|" duplicate duplicate duplicate duplicate duplicate duplicate "
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|textFieldType
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|json
init|=
literal|"{ \"text\" : \""
operator|+
name|text
operator|.
name|toString
argument_list|()
operator|+
literal|"\","
operator|+
literal|" \"json_only_field\" : \""
operator|+
name|text
operator|.
name|toString
argument_list|()
operator|+
literal|"\""
operator|+
literal|" }"
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"_source"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|json
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|SignificantTextAggregationBuilder
name|sigAgg
init|=
operator|new
name|SignificantTextAggregationBuilder
argument_list|(
literal|"sig_text"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|filterDuplicateText
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|sigAgg
operator|.
name|sourceFieldNames
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"json_only_field"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SamplerAggregationBuilder
name|aggBuilder
init|=
operator|new
name|SamplerAggregationBuilder
argument_list|(
literal|"sampler"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|sigAgg
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
init|)
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Search "odd" which should have no duplication
name|Sampler
name|sampler
init|=
name|searchAndReduce
argument_list|(
name|searcher
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"odd"
argument_list|)
argument_list|)
argument_list|,
name|aggBuilder
argument_list|,
name|textFieldType
argument_list|)
decl_stmt|;
name|SignificantTerms
name|terms
init|=
name|sampler
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"sig_text"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"even"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"duplicate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"common"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"odd"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Search "even" which will have duplication
name|sampler
operator|=
name|searchAndReduce
argument_list|(
name|searcher
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"even"
argument_list|)
argument_list|)
argument_list|,
name|aggBuilder
argument_list|,
name|textFieldType
argument_list|)
expr_stmt|;
name|terms
operator|=
name|sampler
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"sig_text"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"odd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"duplicate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"common"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"separator2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"separator4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"separator6"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"even"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Test documents with arrays of text      */
DECL|method|testSignificanceOnTextArrays
specifier|public
name|void
name|testSignificanceOnTextArrays
parameter_list|()
throws|throws
name|IOException
block|{
name|TextFieldType
name|textFieldType
init|=
operator|new
name|TextFieldType
argument_list|()
decl_stmt|;
name|textFieldType
operator|.
name|setName
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|textFieldType
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"my_analyzer"
argument_list|,
name|AnalyzerScope
operator|.
name|GLOBAL
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|indexWriterConfig
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|;
name|IndexWriter
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|indexWriterConfig
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
literal|"foo"
argument_list|,
name|textFieldType
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|json
init|=
literal|"{ \"text\" : [\"foo\",\"foo\"], \"title\" : [\"foo\", \"foo\"]}"
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"_source"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|json
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|SignificantTextAggregationBuilder
name|sigAgg
init|=
operator|new
name|SignificantTextAggregationBuilder
argument_list|(
literal|"sig_text"
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|sigAgg
operator|.
name|sourceFieldNames
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"title"
block|,
literal|"text"
block|}
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
init|)
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searchAndReduce
argument_list|(
name|searcher
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|sigAgg
argument_list|,
name|textFieldType
argument_list|)
expr_stmt|;
comment|// No significant results to be found in this test - only checking we don't end up
comment|// with the internal exception discovered in issue https://github.com/elastic/elasticsearch/issues/25029
block|}
block|}
block|}
block|}
end_class

end_unit

