begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
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
name|aggregations
operator|.
name|bucket
operator|.
name|children
operator|.
name|Children
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
name|terms
operator|.
name|Terms
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
name|tophits
operator|.
name|TopHits
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
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
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
name|assertSearchResponse
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|SuiteScopeTest
DECL|class|ChildrenTests
specifier|public
class|class
name|ChildrenTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|categoryToControl
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Control
argument_list|>
name|categoryToControl
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setupSuiteScopeCluster
specifier|public
name|void
name|setupSuiteScopeCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"article"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"comment"
argument_list|,
literal|"_parent"
argument_list|,
literal|"type=article"
argument_list|,
literal|"_id"
argument_list|,
literal|"index=not_analyzed"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|uniqueCategories
init|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|25
argument_list|)
index|]
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
name|uniqueCategories
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|uniqueCategories
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|catIndex
init|=
literal|0
decl_stmt|;
name|int
name|numParentDocs
init|=
name|randomIntBetween
argument_list|(
name|uniqueCategories
operator|.
name|length
argument_list|,
name|uniqueCategories
operator|.
name|length
operator|*
literal|5
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
name|numParentDocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
index|[]
name|categories
init|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|categories
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|category
init|=
name|categories
index|[
name|j
index|]
operator|=
name|uniqueCategories
index|[
name|catIndex
operator|++
operator|%
name|uniqueCategories
operator|.
name|length
index|]
decl_stmt|;
name|Control
name|control
init|=
name|categoryToControl
operator|.
name|get
argument_list|(
name|category
argument_list|)
decl_stmt|;
if|if
condition|(
name|control
operator|==
literal|null
condition|)
block|{
name|categoryToControl
operator|.
name|put
argument_list|(
name|category
argument_list|,
name|control
operator|=
operator|new
name|Control
argument_list|(
name|category
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|control
operator|.
name|articleIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"article"
argument_list|,
name|id
argument_list|)
operator|.
name|setCreate
argument_list|(
literal|true
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"category"
argument_list|,
name|categories
argument_list|,
literal|"randomized"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|commenters
init|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|5
argument_list|,
literal|50
argument_list|)
index|]
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
name|commenters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|commenters
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|id
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Control
name|control
range|:
name|categoryToControl
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|articleId
range|:
name|control
operator|.
name|articleIds
control|)
block|{
name|int
name|numChildDocsPerParent
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
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
name|numChildDocsPerParent
condition|;
name|i
operator|++
control|)
block|{
name|String
name|commenter
init|=
name|commenters
index|[
name|id
operator|%
name|commenters
operator|.
name|length
index|]
decl_stmt|;
name|String
name|idValue
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|id
operator|++
argument_list|)
decl_stmt|;
name|control
operator|.
name|commentIds
operator|.
name|add
argument_list|(
name|idValue
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|control
operator|.
name|commenterToCommentId
operator|.
name|get
argument_list|(
name|commenter
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|control
operator|.
name|commenterToCommentId
operator|.
name|put
argument_list|(
name|commenter
argument_list|,
name|ids
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|idValue
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"comment"
argument_list|,
name|idValue
argument_list|)
operator|.
name|setCreate
argument_list|(
literal|true
argument_list|)
operator|.
name|setParent
argument_list|(
name|articleId
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"commenter"
argument_list|,
name|commenter
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"article"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"category"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|,
literal|"randomized"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"article"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"category"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"randomized"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"article"
argument_list|,
literal|"c"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"category"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|,
literal|"randomized"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"article"
argument_list|,
literal|"d"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"category"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|,
literal|"randomized"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"comment"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|setParent
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"comment"
argument_list|,
literal|"c"
argument_list|)
operator|.
name|setParent
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|requests
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChildrenAggs
specifier|public
name|void
name|testChildrenAggs
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|searchResponse
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
name|matchQuery
argument_list|(
literal|"randomized"
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"category"
argument_list|)
operator|.
name|field
argument_list|(
literal|"category"
argument_list|)
operator|.
name|size
argument_list|(
literal|0
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|children
argument_list|(
literal|"to_comment"
argument_list|)
operator|.
name|childType
argument_list|(
literal|"comment"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|terms
argument_list|(
literal|"commenters"
argument_list|)
operator|.
name|field
argument_list|(
literal|"commenter"
argument_list|)
operator|.
name|size
argument_list|(
literal|0
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|topHits
argument_list|(
literal|"top_comments"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|searchResponse
argument_list|)
expr_stmt|;
name|Terms
name|categoryTerms
init|=
name|searchResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"category"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|categoryTerms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|categoryToControl
operator|.
name|size
argument_list|()
argument_list|)
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
name|Control
argument_list|>
name|entry1
range|:
name|categoryToControl
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Terms
operator|.
name|Bucket
name|categoryBucket
init|=
name|categoryTerms
operator|.
name|getBucketByKey
argument_list|(
name|entry1
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|entry1
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|entry1
operator|.
name|getValue
argument_list|()
operator|.
name|articleIds
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Children
name|childrenBucket
init|=
name|categoryBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"to_comment"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"to_comment"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|entry1
operator|.
name|getValue
argument_list|()
operator|.
name|commentIds
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Terms
name|commentersTerms
init|=
name|childrenBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"commenters"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|commentersTerms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|entry1
operator|.
name|getValue
argument_list|()
operator|.
name|commenterToCommentId
operator|.
name|size
argument_list|()
argument_list|)
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
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry2
range|:
name|entry1
operator|.
name|getValue
argument_list|()
operator|.
name|commenterToCommentId
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Terms
operator|.
name|Bucket
name|commentBucket
init|=
name|commentersTerms
operator|.
name|getBucketByKey
argument_list|(
name|entry2
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|commentBucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|entry2
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|commentBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|entry2
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TopHits
name|topHits
init|=
name|commentBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"top_comments"
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchHit
name|searchHit
range|:
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|getHits
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|entry2
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|searchHit
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testParentWithMultipleBuckets
specifier|public
name|void
name|testParentWithMultipleBuckets
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|searchResponse
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
name|matchQuery
argument_list|(
literal|"randomized"
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"category"
argument_list|)
operator|.
name|field
argument_list|(
literal|"category"
argument_list|)
operator|.
name|size
argument_list|(
literal|0
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|children
argument_list|(
literal|"to_comment"
argument_list|)
operator|.
name|childType
argument_list|(
literal|"comment"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|topHits
argument_list|(
literal|"top_comments"
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"_id"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|searchResponse
argument_list|)
expr_stmt|;
name|Terms
name|categoryTerms
init|=
name|searchResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"category"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|categoryTerms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Terms
operator|.
name|Bucket
name|bucket
range|:
name|categoryTerms
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"bucket="
operator|+
name|bucket
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Children
name|childrenBucket
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"to_comment"
argument_list|)
decl_stmt|;
name|TopHits
name|topHits
init|=
name|childrenBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"top_comments"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"total_hits={}"
argument_list|,
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|searchHit
range|:
name|topHits
operator|.
name|getHits
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"hit= {} {}"
argument_list|,
name|searchHit
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|searchHit
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Terms
operator|.
name|Bucket
name|categoryBucket
init|=
name|categoryTerms
operator|.
name|getBucketByKey
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3l
argument_list|)
argument_list|)
expr_stmt|;
name|Children
name|childrenBucket
init|=
name|categoryBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"to_comment"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"to_comment"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|TopHits
name|topHits
init|=
name|childrenBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"top_comments"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topHits
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
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|sortValues
argument_list|()
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topHits
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
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|categoryBucket
operator|=
name|categoryTerms
operator|.
name|getBucketByKey
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|childrenBucket
operator|=
name|categoryBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"to_comment"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"to_comment"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|topHits
operator|=
name|childrenBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"top_comments"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
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
name|topHits
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
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|categoryBucket
operator|=
name|categoryTerms
operator|.
name|getBucketByKey
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|categoryBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|childrenBucket
operator|=
name|categoryBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"to_comment"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"to_comment"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childrenBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|topHits
operator|=
name|childrenBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"top_comments"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|topHits
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
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
name|topHits
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
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|Control
specifier|private
specifier|static
specifier|final
class|class
name|Control
block|{
DECL|field|category
specifier|final
name|String
name|category
decl_stmt|;
DECL|field|articleIds
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|articleIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|commentIds
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|commentIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|commenterToCommentId
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|commenterToCommentId
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Control
specifier|private
name|Control
parameter_list|(
name|String
name|category
parameter_list|)
block|{
name|this
operator|.
name|category
operator|=
name|category
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

