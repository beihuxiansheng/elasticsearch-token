begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|search
operator|.
name|highlight
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
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
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
name|builder
operator|.
name|SearchSourceBuilder
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
name|integration
operator|.
name|AbstractNodesTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
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
name|action
operator|.
name|search
operator|.
name|SearchType
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|*
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
name|xcontent
operator|.
name|QueryBuilders
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|TimeValue
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HighlightSearchTests
specifier|public
class|class
name|HighlightSearchTests
extends|extends
name|AbstractNodesTests
block|{
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|method|createNodes
annotation|@
name|BeforeClass
specifier|public
name|void
name|createNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server2"
argument_list|)
expr_stmt|;
name|client
operator|=
name|getClient
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|create
argument_list|(
name|createIndexRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Update mapping (_all to store and have term vectors)"
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|putMapping
argument_list|(
name|putMappingRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|source
argument_list|(
name|mapping
argument_list|()
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|index
argument_list|(
name|client
argument_list|(
literal|"server1"
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"test"
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|refresh
argument_list|(
name|refreshRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|closeNodes
annotation|@
name|AfterClass
specifier|public
name|void
name|closeNodes
parameter_list|()
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeAllNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|getClient
specifier|protected
name|Client
name|getClient
parameter_list|()
block|{
return|return
name|client
argument_list|(
literal|"server1"
argument_list|)
return|;
block|}
DECL|method|testSimpleHighlighting
annotation|@
name|Test
specifier|public
name|void
name|testSimpleHighlighting
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchSourceBuilder
name|source
init|=
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|termQuery
argument_list|(
literal|"_all"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
operator|.
name|from
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|(
literal|60
argument_list|)
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|highlight
argument_list|(
name|highlight
argument_list|()
operator|.
name|field
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|order
argument_list|(
literal|"score"
argument_list|)
operator|.
name|preTags
argument_list|(
literal|"<xxx>"
argument_list|)
operator|.
name|postTags
argument_list|(
literal|"</xxx>"
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|)
operator|.
name|searchType
argument_list|(
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|searchResponse
operator|.
name|shardFailures
argument_list|()
argument_list|)
argument_list|,
name|searchResponse
operator|.
name|shardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|SearchHit
name|hit
init|=
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
index|[
name|i
index|]
decl_stmt|;
comment|//            System.out.println(hit.target() + ": " +  hit.explanation());
name|assertThat
argument_list|(
literal|"id["
operator|+
name|hit
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|,
name|hit
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
literal|100
operator|-
name|i
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//            System.out.println(hit.shard() + ": " + hit.highlightFields());
name|assertThat
argument_list|(
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|fragments
argument_list|()
operator|.
name|length
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|searchResponse
operator|=
name|client
operator|.
name|searchScroll
argument_list|(
name|searchScrollRequest
argument_list|(
name|searchResponse
operator|.
name|scrollId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|40
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|40
condition|;
name|i
operator|++
control|)
block|{
name|SearchHit
name|hit
init|=
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|assertThat
argument_list|(
literal|"id["
operator|+
name|hit
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|,
name|hit
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
literal|100
operator|-
literal|60
operator|-
literal|1
operator|-
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPrefixHighlightingOnSpecificField
annotation|@
name|Test
specifier|public
name|void
name|testPrefixHighlightingOnSpecificField
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchSourceBuilder
name|source
init|=
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|prefixQuery
argument_list|(
literal|"multi"
argument_list|,
literal|"te"
argument_list|)
argument_list|)
operator|.
name|from
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|(
literal|60
argument_list|)
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|highlight
argument_list|(
name|highlight
argument_list|()
operator|.
name|field
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|order
argument_list|(
literal|"score"
argument_list|)
operator|.
name|preTags
argument_list|(
literal|"<xxx>"
argument_list|)
operator|.
name|postTags
argument_list|(
literal|"</xxx>"
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|)
operator|.
name|searchType
argument_list|(
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|searchResponse
operator|.
name|shardFailures
argument_list|()
argument_list|)
argument_list|,
name|searchResponse
operator|.
name|shardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|SearchHit
name|hit
init|=
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
index|[
name|i
index|]
decl_stmt|;
comment|//            System.out.println(hit.target() + ": " +  hit.explanation());
comment|//            assertThat("id[" + hit.id() + "]", hit.id(), equalTo(Integer.toString(100 - i - 1)));
comment|//            System.out.println(hit.shard() + ": " + hit.highlightFields());
name|assertThat
argument_list|(
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|fragments
argument_list|()
operator|.
name|length
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPrefixHighlightingOnAllField
annotation|@
name|Test
specifier|public
name|void
name|testPrefixHighlightingOnAllField
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchSourceBuilder
name|source
init|=
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|prefixQuery
argument_list|(
literal|"_all"
argument_list|,
literal|"te"
argument_list|)
argument_list|)
operator|.
name|from
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|(
literal|60
argument_list|)
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|highlight
argument_list|(
name|highlight
argument_list|()
operator|.
name|field
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|order
argument_list|(
literal|"score"
argument_list|)
operator|.
name|preTags
argument_list|(
literal|"<xxx>"
argument_list|)
operator|.
name|postTags
argument_list|(
literal|"</xxx>"
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|)
operator|.
name|searchType
argument_list|(
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|searchResponse
operator|.
name|shardFailures
argument_list|()
argument_list|)
argument_list|,
name|searchResponse
operator|.
name|shardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|SearchHit
name|hit
init|=
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
index|[
name|i
index|]
decl_stmt|;
comment|//            System.out.println(hit.target() + ": " +  hit.explanation());
comment|//            assertThat("id[" + hit.id() + "]", hit.id(), equalTo(Integer.toString(100 - i - 1)));
comment|//            System.out.println(hit.shard() + ": " + hit.highlightFields());
name|assertThat
argument_list|(
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|fragments
argument_list|()
operator|.
name|length
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|index
specifier|private
name|void
name|index
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|,
name|int
name|age
parameter_list|)
throws|throws
name|IOException
block|{
name|client
operator|.
name|index
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
name|id
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|(
name|id
argument_list|,
name|nameValue
argument_list|,
name|age
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|mapping
specifier|public
name|XContentBuilder
name|mapping
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|field
argument_list|(
literal|"termVector"
argument_list|,
literal|"with_positions_offsets"
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
return|;
block|}
DECL|method|source
specifier|private
name|XContentBuilder
name|source
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|,
name|int
name|age
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|multi
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|nameValue
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
name|age
condition|;
name|i
operator|++
control|)
block|{
name|multi
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|nameValue
argument_list|)
expr_stmt|;
block|}
return|return
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|nameValue
operator|+
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"age"
argument_list|,
name|age
argument_list|)
operator|.
name|field
argument_list|(
literal|"multi"
argument_list|,
name|multi
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"_boost"
argument_list|,
name|age
operator|*
literal|10
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
block|}
end_class

end_unit

