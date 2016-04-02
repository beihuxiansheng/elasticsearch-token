begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|percolator
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchResponse
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
name|query
operator|.
name|MultiMatchQueryBuilder
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
name|highlight
operator|.
name|HighlightBuilder
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
name|sort
operator|.
name|SortOrder
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
name|ESSingleNodeTestCase
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
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|query
operator|.
name|QueryBuilders
operator|.
name|boolQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|commonTermsQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|matchQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|multiMatchQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|percolatorQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|spanNearQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|spanNotQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|spanTermQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|termQuery
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
import|;
end_import

begin_class
DECL|class|PercolatorQuerySearchIT
specifier|public
class|class
name|PercolatorQuerySearchIT
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testPercolatorQuery
specifier|public
name|void
name|testPercolatorQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"field1"
argument_list|,
literal|"type=keyword"
argument_list|,
literal|"field2"
argument_list|,
literal|"type=keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|must
argument_list|(
name|matchQuery
argument_list|(
literal|"field2"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
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
name|prepareRefresh
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|BytesReference
name|source
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"percolating empty doc"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|percolatorQuery
argument_list|(
literal|"type"
argument_list|,
name|source
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|response
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"percolating doc with 1 field"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|percolatorQuery
argument_list|(
literal|"type"
argument_list|,
name|source
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"_uid"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|response
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"percolating doc with 2 fields"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|percolatorQuery
argument_list|(
literal|"type"
argument_list|,
name|source
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"_uid"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|response
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPercolatorSpecificQueries
specifier|public
name|void
name|testPercolatorSpecificQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"field1"
argument_list|,
literal|"type=text"
argument_list|,
literal|"field2"
argument_list|,
literal|"type=text"
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|commonTermsQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"quick brown fox"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|multiMatchQuery
argument_list|(
literal|"quick brown fox"
argument_list|,
literal|"field1"
argument_list|,
literal|"field2"
argument_list|)
operator|.
name|type
argument_list|(
name|MultiMatchQueryBuilder
operator|.
name|Type
operator|.
name|CROSS_FIELDS
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|spanNearQuery
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"quick"
argument_list|)
argument_list|,
literal|0
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
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
name|prepareRefresh
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|spanNotQuery
argument_list|(
name|spanNearQuery
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"quick"
argument_list|)
argument_list|,
literal|0
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|(
literal|true
argument_list|)
argument_list|,
name|spanNearQuery
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"the"
argument_list|)
argument_list|,
literal|0
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"lazy"
argument_list|)
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"dog"
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|dist
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// doesn't match
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|spanNotQuery
argument_list|(
name|spanNearQuery
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"quick"
argument_list|)
argument_list|,
literal|0
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|(
literal|true
argument_list|)
argument_list|,
name|spanNearQuery
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"the"
argument_list|)
argument_list|,
literal|0
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"lazy"
argument_list|)
argument_list|)
operator|.
name|clause
argument_list|(
name|spanTermQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"dog"
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|dist
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
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
name|prepareRefresh
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|BytesReference
name|source
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"the quick brown fox jumps over the lazy dog"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"the quick brown fox falls down into the well"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|percolatorQuery
argument_list|(
literal|"type"
argument_list|,
name|source
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"_uid"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|response
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|score
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|score
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|score
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|3
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|3
argument_list|)
operator|.
name|score
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPercolatorQueryWithHighlighting
specifier|public
name|void
name|testPercolatorQueryWithHighlighting
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"field1"
argument_list|,
literal|"type=text"
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"brown fox"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"lazy dog"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"jumps"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"dog"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorFieldMapper
operator|.
name|TYPE_NAME
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
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
name|prepareRefresh
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|BytesReference
name|document
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"The quick brown fox jumps over the lazy dog"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|percolatorQuery
argument_list|(
literal|"type"
argument_list|,
name|document
argument_list|)
argument_list|)
operator|.
name|highlighter
argument_list|(
operator|new
name|HighlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"_uid"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fragments
argument_list|()
index|[
literal|0
index|]
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"The quick<em>brown</em><em>fox</em> jumps over the lazy dog"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fragments
argument_list|()
index|[
literal|0
index|]
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"The quick brown fox jumps over the<em>lazy</em><em>dog</em>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fragments
argument_list|()
index|[
literal|0
index|]
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"The quick brown fox<em>jumps</em> over the lazy dog"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|3
argument_list|)
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fragments
argument_list|()
index|[
literal|0
index|]
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"The quick brown fox jumps over the lazy<em>dog</em>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|4
argument_list|)
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fragments
argument_list|()
index|[
literal|0
index|]
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"The quick brown<em>fox</em> jumps over the lazy dog"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

