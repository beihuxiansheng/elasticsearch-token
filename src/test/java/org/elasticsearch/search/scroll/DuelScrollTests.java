begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.scroll
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|scroll
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntOpenHashSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
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
name|action
operator|.
name|search
operator|.
name|SearchType
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
name|search
operator|.
name|SearchHits
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
name|SortBuilder
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
name|SortBuilders
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
name|ElasticsearchIntegrationTest
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
name|util
operator|.
name|Arrays
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
name|assertNoFailures
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DuelScrollTests
specifier|public
class|class
name|DuelScrollTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testDuel_queryThenFetch
specifier|public
name|void
name|testDuel_queryThenFetch
parameter_list|()
throws|throws
name|Exception
block|{
name|TestContext
name|context
init|=
name|create
argument_list|(
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
argument_list|,
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
decl_stmt|;
name|SearchResponse
name|control
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|context
operator|.
name|searchType
argument_list|)
operator|.
name|addSort
argument_list|(
name|context
operator|.
name|sort
argument_list|)
operator|.
name|setSize
argument_list|(
name|context
operator|.
name|numDocs
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|control
argument_list|)
expr_stmt|;
name|SearchHits
name|sh
init|=
name|control
operator|.
name|getHits
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sh
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sh
operator|.
name|getHits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchScrollResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|context
operator|.
name|searchType
argument_list|)
operator|.
name|addSort
argument_list|(
name|context
operator|.
name|sort
argument_list|)
operator|.
name|setSize
argument_list|(
name|context
operator|.
name|scrollRequestSize
argument_list|)
operator|.
name|setScroll
argument_list|(
literal|"10m"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|searchScrollResponse
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|context
operator|.
name|scrollRequestSize
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
name|sh
operator|.
name|getAt
argument_list|(
name|counter
operator|++
argument_list|)
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|iter
init|=
literal|1
decl_stmt|;
name|String
name|scrollId
init|=
name|searchScrollResponse
operator|.
name|getScrollId
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|searchScrollResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearchScroll
argument_list|(
name|scrollId
argument_list|)
operator|.
name|setScroll
argument_list|(
literal|"10m"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertNoFailures
argument_list|(
name|searchScrollResponse
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|int
name|expectedLength
decl_stmt|;
name|int
name|scrollSlice
init|=
operator|++
name|iter
operator|*
name|context
operator|.
name|scrollRequestSize
decl_stmt|;
if|if
condition|(
name|scrollSlice
operator|<=
name|context
operator|.
name|numDocs
condition|)
block|{
name|expectedLength
operator|=
name|context
operator|.
name|scrollRequestSize
expr_stmt|;
block|}
else|else
block|{
name|expectedLength
operator|=
name|context
operator|.
name|scrollRequestSize
operator|-
operator|(
name|scrollSlice
operator|-
name|context
operator|.
name|numDocs
operator|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|expectedLength
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
name|sh
operator|.
name|getAt
argument_list|(
name|counter
operator|++
argument_list|)
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|scrollId
operator|=
name|searchScrollResponse
operator|.
name|getScrollId
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|counter
argument_list|,
name|equalTo
argument_list|(
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|clearScroll
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDuel_queryAndFetch
specifier|public
name|void
name|testDuel_queryAndFetch
parameter_list|()
throws|throws
name|Exception
block|{
comment|// *_QUERY_AND_FETCH search types are tricky: the ordering can be incorrect, since it returns num_shards * (from + size)
comment|// a subsequent scroll call can return hits that should have been in the hits of the first scroll call.
name|TestContext
name|context
init|=
name|create
argument_list|(
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
argument_list|,
name|SearchType
operator|.
name|QUERY_AND_FETCH
argument_list|)
decl_stmt|;
name|SearchResponse
name|searchScrollResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|context
operator|.
name|searchType
argument_list|)
operator|.
name|addSort
argument_list|(
name|context
operator|.
name|sort
argument_list|)
operator|.
name|setSize
argument_list|(
name|context
operator|.
name|scrollRequestSize
argument_list|)
operator|.
name|setScroll
argument_list|(
literal|"10m"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|searchScrollResponse
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|counter
init|=
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
decl_stmt|;
name|String
name|scrollId
init|=
name|searchScrollResponse
operator|.
name|getScrollId
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|searchScrollResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearchScroll
argument_list|(
name|scrollId
argument_list|)
operator|.
name|setScroll
argument_list|(
literal|"10m"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertNoFailures
argument_list|(
name|searchScrollResponse
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|counter
operator|+=
name|searchScrollResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
expr_stmt|;
name|scrollId
operator|=
name|searchScrollResponse
operator|.
name|getScrollId
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|counter
argument_list|,
name|equalTo
argument_list|(
name|context
operator|.
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|clearScroll
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|private
name|TestContext
name|create
parameter_list|(
name|SearchType
modifier|...
name|searchTypes
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|jsonBuilder
argument_list|()
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
literal|"field1"
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
name|startObject
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"nested"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"nested"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field3"
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
name|startObject
argument_list|(
literal|"field4"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
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
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
literal|2
operator|+
name|randomInt
argument_list|(
literal|512
argument_list|)
decl_stmt|;
name|int
name|scrollRequestSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|rarely
argument_list|()
condition|?
name|numDocs
else|:
name|numDocs
operator|/
literal|2
argument_list|)
decl_stmt|;
name|boolean
name|unevenRouting
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|int
name|numMissingDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
name|numDocs
operator|/
literal|100
argument_list|)
decl_stmt|;
name|IntOpenHashSet
name|missingDocs
init|=
operator|new
name|IntOpenHashSet
argument_list|(
name|numMissingDocs
argument_list|)
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
name|numMissingDocs
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
operator|!
name|missingDocs
operator|.
name|add
argument_list|(
name|randomInt
argument_list|(
name|numDocs
argument_list|)
argument_list|)
condition|)
block|{}
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|IndexRequestBuilder
name|indexRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|missingDocs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexRequestBuilder
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
literal|"field1"
argument_list|,
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"nested"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field3"
argument_list|,
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"field4"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|unevenRouting
operator|&&
name|randomInt
argument_list|(
literal|3
argument_list|)
operator|<=
literal|2
condition|)
block|{
name|indexRequestBuilder
operator|.
name|setRouting
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|false
argument_list|,
name|indexRequestBuilder
argument_list|)
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
specifier|final
name|SortBuilder
name|sort
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|sort
operator|=
name|SortBuilders
operator|.
name|fieldSort
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|missing
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
name|SortBuilders
operator|.
name|fieldSort
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|missing
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|sort
operator|=
name|SortBuilders
operator|.
name|fieldSort
argument_list|(
literal|"nested.field3"
argument_list|)
operator|.
name|missing
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
name|SortBuilders
operator|.
name|fieldSort
argument_list|(
literal|"nested.field4"
argument_list|)
operator|.
name|missing
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
name|sort
operator|.
name|order
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASC
else|:
name|SortOrder
operator|.
name|DESC
argument_list|)
expr_stmt|;
name|SearchType
name|searchType
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|searchTypes
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"numDocs={}, scrollRequestSize={}, sort={}, searchType={}"
argument_list|,
name|numDocs
argument_list|,
name|scrollRequestSize
argument_list|,
name|sort
argument_list|,
name|searchType
argument_list|)
expr_stmt|;
return|return
operator|new
name|TestContext
argument_list|(
name|numDocs
argument_list|,
name|scrollRequestSize
argument_list|,
name|sort
argument_list|,
name|searchType
argument_list|)
return|;
block|}
DECL|class|TestContext
class|class
name|TestContext
block|{
DECL|field|numDocs
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|scrollRequestSize
specifier|final
name|int
name|scrollRequestSize
decl_stmt|;
DECL|field|sort
specifier|final
name|SortBuilder
name|sort
decl_stmt|;
DECL|field|searchType
specifier|final
name|SearchType
name|searchType
decl_stmt|;
DECL|method|TestContext
name|TestContext
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|int
name|scrollRequestSize
parameter_list|,
name|SortBuilder
name|sort
parameter_list|,
name|SearchType
name|searchType
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|scrollRequestSize
operator|=
name|scrollRequestSize
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|searchType
operator|=
name|searchType
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

