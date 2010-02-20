begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.search
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
name|action
operator|.
name|search
operator|.
name|ShardSearchFailure
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
name|Scroll
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
name|AbstractServersTests
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
name|json
operator|.
name|JsonQueryBuilders
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|TransportTwoServersSearchTests
specifier|public
class|class
name|TransportTwoServersSearchTests
extends|extends
name|AbstractServersTests
block|{
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|method|createServers
annotation|@
name|BeforeClass
specifier|public
name|void
name|createServers
parameter_list|()
throws|throws
name|Exception
block|{
name|startServer
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|startServer
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
DECL|method|closeServers
annotation|@
name|AfterClass
specifier|public
name|void
name|closeServers
parameter_list|()
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeAllServers
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
DECL|method|testDfsQueryThenFetch
annotation|@
name|Test
specifier|public
name|void
name|testDfsQueryThenFetch
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
literal|"multi"
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
name|DFS_QUERY_THEN_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
comment|//
DECL|method|testDfsQueryThenFetchWithSort
annotation|@
name|Test
specifier|public
name|void
name|testDfsQueryThenFetchWithSort
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
literal|"multi"
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
name|sort
argument_list|(
literal|"age"
argument_list|,
literal|false
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
name|DFS_QUERY_THEN_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
name|i
argument_list|)
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
name|i
operator|+
literal|60
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQueryThenFetch
annotation|@
name|Test
specifier|public
name|void
name|testQueryThenFetch
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
literal|"multi"
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
operator|new
name|Scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
DECL|method|testQueryThenFetchWithSort
annotation|@
name|Test
specifier|public
name|void
name|testQueryThenFetchWithSort
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
literal|"multi"
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
name|sort
argument_list|(
literal|"age"
argument_list|,
literal|false
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
operator|new
name|Scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
name|i
argument_list|)
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
name|i
operator|+
literal|60
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQueryAndFetch
annotation|@
name|Test
specifier|public
name|void
name|testQueryAndFetch
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
literal|"multi"
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
literal|20
argument_list|)
operator|.
name|explain
argument_list|(
literal|true
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
name|QUERY_AND_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
comment|// 20 per shard
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
block|}
comment|// TODO support scrolling
comment|//        searchResponse = searchScrollAction.submit(new SearchScrollRequest(searchResponse.scrollId())).actionGet();
comment|//
comment|//        assertEquals(100, searchResponse.hits().totalHits());
comment|//        assertEquals(40, searchResponse.hits().hits().length);
comment|//        for (int i = 0; i< 40; i++) {
comment|//            SearchHit hit = searchResponse.hits().hits()[i];
comment|//            assertEquals("id[" + hit.id() + "]", Integer.toString(100 - 60 - 1 - i), hit.id());
comment|//        }
block|}
DECL|method|testDfsQueryAndFetch
annotation|@
name|Test
specifier|public
name|void
name|testDfsQueryAndFetch
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
literal|"multi"
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
literal|20
argument_list|)
operator|.
name|explain
argument_list|(
literal|true
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
name|DFS_QUERY_AND_FETCH
argument_list|)
operator|.
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
comment|// 20 per shard
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
block|}
comment|// TODO support scrolling
comment|//        searchResponse = searchScrollAction.submit(new SearchScrollRequest(searchResponse.scrollId())).actionGet();
comment|//
comment|//        assertEquals(100, searchResponse.hits().totalHits());
comment|//        assertEquals(40, searchResponse.hits().hits().length);
comment|//        for (int i = 0; i< 40; i++) {
comment|//            SearchHit hit = searchResponse.hits().hits()[i];
comment|//            assertEquals("id[" + hit.id() + "]", Integer.toString(100 - 60 - 1 - i), hit.id());
comment|//        }
block|}
DECL|method|testSimpleFacets
annotation|@
name|Test
specifier|public
name|void
name|testSimpleFacets
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchSourceBuilder
name|sourceBuilder
init|=
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|termQuery
argument_list|(
literal|"multi"
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
literal|20
argument_list|)
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|facets
argument_list|(
name|facets
argument_list|()
operator|.
name|facet
argument_list|(
literal|"all"
argument_list|,
name|termQuery
argument_list|(
literal|"multi"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
operator|.
name|facet
argument_list|(
literal|"test1"
argument_list|,
name|termQuery
argument_list|(
literal|"name"
argument_list|,
literal|"test1"
argument_list|)
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
name|sourceBuilder
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
name|facets
argument_list|()
operator|.
name|countFacet
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|count
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|facets
argument_list|()
operator|.
name|countFacet
argument_list|(
literal|"all"
argument_list|)
operator|.
name|count
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100l
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleFacetsTwice
annotation|@
name|Test
specifier|public
name|void
name|testSimpleFacetsTwice
parameter_list|()
throws|throws
name|Exception
block|{
name|testSimpleFacets
argument_list|()
expr_stmt|;
name|testSimpleFacets
argument_list|()
expr_stmt|;
block|}
DECL|method|testFailedSearch
annotation|@
name|Test
specifier|public
name|void
name|testFailedSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Start Testing failed search"
argument_list|)
expr_stmt|;
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
literal|"{ xxx }"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Failures:"
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardSearchFailure
name|searchFailure
range|:
name|searchResponse
operator|.
name|shardFailures
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Reason : "
operator|+
name|searchFailure
operator|.
name|reason
argument_list|()
operator|+
literal|", shard "
operator|+
name|searchFailure
operator|.
name|shard
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Done Testing failed search"
argument_list|)
expr_stmt|;
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
DECL|method|source
specifier|private
name|String
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
literal|"{ type1 : { \"id\" : \""
operator|+
name|id
operator|+
literal|"\", \"name\" : \""
operator|+
operator|(
name|nameValue
operator|+
name|id
operator|)
operator|+
literal|"\", age : "
operator|+
name|age
operator|+
literal|", multi : \""
operator|+
name|multi
operator|.
name|toString
argument_list|()
operator|+
literal|"\", _boost : "
operator|+
operator|(
name|age
operator|*
literal|10
operator|)
operator|+
literal|" } }"
return|;
block|}
block|}
end_class

end_unit

