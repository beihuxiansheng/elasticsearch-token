begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
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
name|explain
operator|.
name|ExplainResponse
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
name|index
operator|.
name|IndexRequestBuilder
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
name|json
operator|.
name|JsonXContent
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
name|QueryBuilders
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
name|SearchHit
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
name|ESIntegTestCase
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Locale
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
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
name|singletonMap
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
name|assertAcked
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
name|assertSearchResponse
import|;
end_import

begin_class
DECL|class|ExistsIT
specifier|public
class|class
name|ExistsIT
extends|extends
name|ESIntegTestCase
block|{
comment|// TODO: move this to a unit test somewhere...
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|SearchResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|existsQuery
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|resp
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
operator|.
name|mustNot
argument_list|(
name|QueryBuilders
operator|.
name|existsQuery
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertSearchResponse
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
DECL|method|testExists
specifier|public
name|void
name|testExists
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|mapping
init|=
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"object"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"object"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
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
literal|"idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|barObject
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|barObject
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|barObject
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
name|singletonMap
argument_list|(
literal|"bar"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
index|[]
name|sources
init|=
operator|new
name|Map
index|[]
block|{
comment|// simple property
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
block|,
comment|// object fields
name|singletonMap
argument_list|(
literal|"bar"
argument_list|,
name|barObject
argument_list|)
block|,
name|singletonMap
argument_list|(
literal|"bar"
argument_list|,
name|singletonMap
argument_list|(
literal|"baz"
argument_list|,
literal|42
argument_list|)
argument_list|)
block|,
comment|// empty doc
name|emptyMap
argument_list|()
block|}
decl_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|reqs
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexRequestBuilder
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
range|:
name|sources
control|)
block|{
name|reqs
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// We do NOT index dummy documents, otherwise the type for these dummy documents
comment|// would have _field_names indexed while the current type might not which might
comment|// confuse the exists/missing parser at query time
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|reqs
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expected
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"f*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"bar.*"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"bar.foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"bar.bar"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"bar.bar.bar"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"foobar"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|long
name|numDocs
init|=
name|sources
operator|.
name|length
decl_stmt|;
name|SearchResponse
name|allDocs
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setSize
argument_list|(
name|sources
operator|.
name|length
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|allDocs
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|allDocs
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// exists
name|SearchResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|existsQuery
argument_list|(
name|fieldName
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|resp
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"exists(%s, %d) mapping: %s response: %s"
argument_list|,
name|fieldName
argument_list|,
name|count
argument_list|,
name|mapping
operator|.
name|string
argument_list|()
argument_list|,
name|resp
argument_list|)
argument_list|,
name|count
argument_list|,
name|resp
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
for|for
control|(
name|SearchHit
name|searchHit
range|:
name|allDocs
operator|.
name|getHits
argument_list|()
control|)
block|{
specifier|final
name|String
name|index
init|=
name|searchHit
operator|.
name|getIndex
argument_list|()
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|searchHit
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|String
name|id
init|=
name|searchHit
operator|.
name|getId
argument_list|()
decl_stmt|;
specifier|final
name|ExplainResponse
name|explanation
init|=
name|client
argument_list|()
operator|.
name|prepareExplain
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|existsQuery
argument_list|(
name|fieldName
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Explanation for [{}] / [{}] / [{}]: [{}]"
argument_list|,
name|fieldName
argument_list|,
name|id
argument_list|,
name|searchHit
operator|.
name|getSourceAsString
argument_list|()
argument_list|,
name|explanation
operator|.
name|getExplanation
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

