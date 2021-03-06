begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
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
name|document
operator|.
name|IntPoint
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
name|NumericDocValuesField
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
name|search
operator|.
name|FieldValueQuery
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
name|store
operator|.
name|Directory
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
name|CheckedConsumer
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
name|metrics
operator|.
name|max
operator|.
name|InternalMax
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
name|metrics
operator|.
name|max
operator|.
name|MaxAggregationBuilder
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
name|metrics
operator|.
name|max
operator|.
name|MaxAggregator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_class
DECL|class|MaxAggregatorTests
specifier|public
class|class
name|MaxAggregatorTests
extends|extends
name|AggregatorTestCase
block|{
DECL|method|testNoDocs
specifier|public
name|void
name|testNoDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|testCase
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|iw
lambda|->
block|{
comment|// Intentionally not writing any docs
block|}
argument_list|,
name|max
lambda|->
block|{
name|assertEquals
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoMatchingField
specifier|public
name|void
name|testNoMatchingField
parameter_list|()
throws|throws
name|IOException
block|{
name|testCase
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|iw
lambda|->
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|singleton
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"wrong_number"
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|singleton
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"wrong_number"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|max
lambda|->
block|{
name|assertEquals
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeMatchesSortedNumericDocValues
specifier|public
name|void
name|testSomeMatchesSortedNumericDocValues
parameter_list|()
throws|throws
name|IOException
block|{
name|testCase
argument_list|(
operator|new
name|FieldValueQuery
argument_list|(
literal|"number"
argument_list|)
argument_list|,
name|iw
lambda|->
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|singleton
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|singleton
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|max
lambda|->
block|{
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeMatchesNumericDocValues
specifier|public
name|void
name|testSomeMatchesNumericDocValues
parameter_list|()
throws|throws
name|IOException
block|{
name|testCase
argument_list|(
operator|new
name|FieldValueQuery
argument_list|(
literal|"number"
argument_list|)
argument_list|,
name|iw
lambda|->
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|singleton
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|singleton
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|max
lambda|->
block|{
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueryFiltering
specifier|public
name|void
name|testQueryFiltering
parameter_list|()
throws|throws
name|IOException
block|{
name|testCase
argument_list|(
name|IntPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"number"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|iw
lambda|->
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|IntPoint
argument_list|(
literal|"number"
argument_list|,
literal|7
argument_list|)
argument_list|,
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|IntPoint
argument_list|(
literal|"number"
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|max
lambda|->
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueryFiltersAll
specifier|public
name|void
name|testQueryFiltersAll
parameter_list|()
throws|throws
name|IOException
block|{
name|testCase
argument_list|(
name|IntPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"number"
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|iw
lambda|->
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|IntPoint
argument_list|(
literal|"number"
argument_list|,
literal|7
argument_list|)
argument_list|,
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|IntPoint
argument_list|(
literal|"number"
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|max
lambda|->
block|{
name|assertEquals
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCase
specifier|private
name|void
name|testCase
parameter_list|(
name|Query
name|query
parameter_list|,
name|CheckedConsumer
argument_list|<
name|RandomIndexWriter
argument_list|,
name|IOException
argument_list|>
name|buildIndex
parameter_list|,
name|Consumer
argument_list|<
name|InternalMax
argument_list|>
name|verify
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|indexWriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|buildIndex
operator|.
name|accept
argument_list|(
name|indexWriter
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MaxAggregationBuilder
name|aggregationBuilder
init|=
operator|new
name|MaxAggregationBuilder
argument_list|(
literal|"_name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"number"
argument_list|)
decl_stmt|;
name|MappedFieldType
name|fieldType
init|=
operator|new
name|NumberFieldMapper
operator|.
name|NumberFieldType
argument_list|(
name|NumberFieldMapper
operator|.
name|NumberType
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|setName
argument_list|(
literal|"number"
argument_list|)
expr_stmt|;
name|MaxAggregator
name|aggregator
init|=
name|createAggregator
argument_list|(
name|aggregationBuilder
argument_list|,
name|indexSearcher
argument_list|,
name|fieldType
argument_list|)
decl_stmt|;
name|aggregator
operator|.
name|preCollection
argument_list|()
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
name|aggregator
operator|.
name|postCollection
argument_list|()
expr_stmt|;
name|verify
operator|.
name|accept
argument_list|(
operator|(
name|InternalMax
operator|)
name|aggregator
operator|.
name|buildAggregation
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|indexReader
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
block|}
end_class

end_unit

