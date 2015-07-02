begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|NumericRangeQuery
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
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|BytesArray
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
name|compress
operator|.
name|CompressedXContent
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
name|Injector
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
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|ElasticsearchSingleNodeTest
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
name|TestSearchContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
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
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToBytesFromClasspath
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
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
name|instanceOf
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
name|is
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndexQueryParserFilterDateRangeFormatTests
specifier|public
class|class
name|IndexQueryParserFilterDateRangeFormatTests
extends|extends
name|ElasticsearchSingleNodeTest
block|{
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|field|queryParser
specifier|private
name|IndexQueryParserService
name|queryParser
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|injector
operator|=
name|indexService
operator|.
name|injector
argument_list|()
expr_stmt|;
name|MapperService
name|mapperService
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/mapping.json"
argument_list|)
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"person"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
literal|"person"
argument_list|)
operator|.
name|parse
argument_list|(
literal|"person"
argument_list|,
literal|"1"
argument_list|,
operator|new
name|BytesArray
argument_list|(
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/data.json"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|dynamicMappingsUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|preparePutMapping
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"person"
argument_list|)
operator|.
name|setSource
argument_list|(
name|doc
operator|.
name|dynamicMappingsUpdate
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|queryParser
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|IndexQueryParserService
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|queryParser
specifier|private
name|IndexQueryParserService
name|queryParser
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|queryParser
return|;
block|}
annotation|@
name|Test
DECL|method|testDateRangeFilterFormat
specifier|public
name|void
name|testDateRangeFilterFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexQueryParserService
name|queryParser
init|=
name|queryParser
argument_list|()
decl_stmt|;
name|String
name|query
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/date_range_filter_format.json"
argument_list|)
decl_stmt|;
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
operator|.
name|query
argument_list|()
expr_stmt|;
comment|// Sadly from NoCacheFilter, we can not access to the delegate filter so we can not check
comment|// it's the one we are expecting
comment|// Test Invalid format
name|query
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/date_range_filter_format_invalid.json"
argument_list|)
expr_stmt|;
try|try
block|{
name|SearchContext
operator|.
name|setCurrent
argument_list|(
operator|new
name|TestSearchContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// We need to rewrite, because range on date field initially returns LateParsingQuery
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|rewrite
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A Range Filter with a specific format but with an unexpected date should raise a QueryParsingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|e
parameter_list|)
block|{
comment|// We expect it
block|}
finally|finally
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDateRangeQueryFormat
specifier|public
name|void
name|testDateRangeQueryFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexQueryParserService
name|queryParser
init|=
name|queryParser
argument_list|()
decl_stmt|;
comment|// We test 01/01/2012 from gte and 2030 for lt
name|String
name|query
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/date_range_query_format.json"
argument_list|)
decl_stmt|;
name|Query
name|parsedQuery
decl_stmt|;
try|try
block|{
name|SearchContext
operator|.
name|setCurrent
argument_list|(
operator|new
name|TestSearchContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// We need to rewrite, because range on date field initially returns LateParsingQuery
name|parsedQuery
operator|=
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|rewrite
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
empty_stmt|;
block|}
name|assertThat
argument_list|(
name|parsedQuery
argument_list|,
name|instanceOf
argument_list|(
name|NumericRangeQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Min value was 01/01/2012 (dd/MM/yyyy)
name|DateTime
name|min
init|=
name|DateTime
operator|.
name|parse
argument_list|(
literal|"2012-01-01T00:00:00.000+00"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|NumericRangeQuery
operator|)
name|parsedQuery
operator|)
operator|.
name|getMin
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|min
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Max value was 2030 (yyyy)
name|DateTime
name|max
init|=
name|DateTime
operator|.
name|parse
argument_list|(
literal|"2030-01-01T00:00:00.000+00"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|NumericRangeQuery
operator|)
name|parsedQuery
operator|)
operator|.
name|getMax
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|max
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test Invalid format
name|query
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/date_range_query_format_invalid.json"
argument_list|)
expr_stmt|;
try|try
block|{
name|SearchContext
operator|.
name|setCurrent
argument_list|(
operator|new
name|TestSearchContext
argument_list|()
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|rewrite
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A Range Query with a specific format but with an unexpected date should raise a QueryParsingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|e
parameter_list|)
block|{
comment|// We expect it
block|}
finally|finally
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDateRangeBoundaries
specifier|public
name|void
name|testDateRangeBoundaries
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexQueryParserService
name|queryParser
init|=
name|queryParser
argument_list|()
decl_stmt|;
name|String
name|query
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/date_range_query_boundaries_inclusive.json"
argument_list|)
decl_stmt|;
name|Query
name|parsedQuery
decl_stmt|;
try|try
block|{
name|SearchContext
operator|.
name|setCurrent
argument_list|(
operator|new
name|TestSearchContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// We need to rewrite, because range on date field initially returns LateParsingQuery
name|parsedQuery
operator|=
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|rewrite
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|parsedQuery
argument_list|,
name|instanceOf
argument_list|(
name|NumericRangeQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|NumericRangeQuery
name|rangeQuery
init|=
operator|(
name|NumericRangeQuery
operator|)
name|parsedQuery
decl_stmt|;
name|DateTime
name|min
init|=
name|DateTime
operator|.
name|parse
argument_list|(
literal|"2014-11-01T00:00:00.000+00"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rangeQuery
operator|.
name|getMin
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|min
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rangeQuery
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|DateTime
name|max
init|=
name|DateTime
operator|.
name|parse
argument_list|(
literal|"2014-12-08T23:59:59.999+00"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rangeQuery
operator|.
name|getMax
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|max
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rangeQuery
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/query/date_range_query_boundaries_exclusive.json"
argument_list|)
expr_stmt|;
try|try
block|{
name|SearchContext
operator|.
name|setCurrent
argument_list|(
operator|new
name|TestSearchContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// We need to rewrite, because range on date field initially returns LateParsingQuery
name|parsedQuery
operator|=
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|rewrite
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|parsedQuery
argument_list|,
name|instanceOf
argument_list|(
name|NumericRangeQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|rangeQuery
operator|=
operator|(
name|NumericRangeQuery
operator|)
name|parsedQuery
expr_stmt|;
name|min
operator|=
name|DateTime
operator|.
name|parse
argument_list|(
literal|"2014-11-30T23:59:59.999+00"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rangeQuery
operator|.
name|getMin
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|min
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rangeQuery
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|max
operator|=
name|DateTime
operator|.
name|parse
argument_list|(
literal|"2014-12-08T00:00:00.000+00"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rangeQuery
operator|.
name|getMax
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|max
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rangeQuery
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

