begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|functionscore
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
name|common
operator|.
name|geo
operator|.
name|GeoPoint
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
name|query
operator|.
name|functionscore
operator|.
name|FunctionScoreQueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
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
name|ESBackcompatTestCase
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
name|ArrayList
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
name|concurrent
operator|.
name|ExecutionException
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
name|searchRequest
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
name|functionScoreQuery
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
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
operator|.
name|ScoreFunctionBuilders
operator|.
name|gaussDecayFunction
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
name|functionscore
operator|.
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
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
name|functionscore
operator|.
name|ScoreFunctionBuilders
operator|.
name|weightFactorFunction
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
name|searchSource
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
name|assertOrderedSearchHits
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FunctionScoreBackwardCompatibilityIT
specifier|public
class|class
name|FunctionScoreBackwardCompatibilityIT
extends|extends
name|ESBackcompatTestCase
block|{
comment|/**      * Simple upgrade test for function score.      */
DECL|method|testSimpleFunctionScoreParsingWorks
specifier|public
name|void
name|testSimpleFunctionScoreParsingWorks
parameter_list|()
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
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
literal|"type1"
argument_list|,
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
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"text"
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
literal|"loc"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"geo_point"
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
argument_list|)
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|int
name|numDocs
init|=
literal|10
decl_stmt|;
name|String
index|[]
name|ids
init|=
operator|new
name|String
index|[
name|numDocs
index|]
decl_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|indexBuilders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numDocs
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
name|indexBuilders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|()
operator|.
name|setType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
operator|.
name|setIndex
argument_list|(
literal|"test"
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
literal|"text"
argument_list|,
literal|"value "
operator|+
operator|(
name|i
operator|<
literal|5
condition|?
literal|"boosted"
else|:
literal|""
operator|)
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"loc"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|10
operator|+
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
literal|20
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ids
index|[
name|i
index|]
operator|=
name|id
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|checkFunctionScoreStillWorks
argument_list|(
name|ids
argument_list|)
expr_stmt|;
name|logClusterState
argument_list|()
expr_stmt|;
comment|// prevent any kind of allocation during the upgrade we recover from gateway
name|disableAllocation
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|boolean
name|upgraded
decl_stmt|;
name|int
name|upgradedNodesCounter
init|=
literal|1
decl_stmt|;
do|do
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"function_score bwc: upgrading {}st node"
argument_list|,
name|upgradedNodesCounter
operator|++
argument_list|)
expr_stmt|;
name|upgraded
operator|=
name|backwardsCluster
argument_list|()
operator|.
name|upgradeOneNode
argument_list|()
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|logClusterState
argument_list|()
expr_stmt|;
name|checkFunctionScoreStillWorks
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|upgraded
condition|)
do|;
name|enableAllocation
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"done function_score while upgrading"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commonNodeSettings
specifier|protected
name|Settings
name|commonNodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|commonNodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.inline"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|checkFunctionScoreStillWorks
specifier|private
name|void
name|checkFunctionScoreStillWorks
parameter_list|(
name|String
modifier|...
name|ids
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"text"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
index|[]
block|{
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|(
name|gaussDecayFunction
argument_list|(
literal|"loc"
argument_list|,
operator|new
name|GeoPoint
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
argument_list|,
literal|"1000km"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|(
name|scriptFunction
argument_list|(
operator|new
name|Script
argument_list|(
literal|"_index['text']['value'].tf()"
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|(
name|termQuery
argument_list|(
literal|"text"
argument_list|,
literal|"boosted"
argument_list|)
argument_list|,
name|weightFactorFunction
argument_list|(
literal|5
argument_list|)
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|assertOrderedSearchHits
argument_list|(
name|response
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

