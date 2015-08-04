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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
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
name|common
operator|.
name|Nullable
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
name|fielddata
operator|.
name|ScriptDocValues
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
name|AbstractDoubleSearchScript
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
name|ExecutableScript
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
name|ExplainableSearchScript
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
name|NativeScriptFactory
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
name|script
operator|.
name|ScriptService
operator|.
name|ScriptType
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
name|test
operator|.
name|ESIntegTestCase
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
operator|.
name|ClusterScope
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
operator|.
name|Scope
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
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
name|Map
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
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
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
name|scriptFunction
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|SUITE
argument_list|,
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|ExplainableScriptIT
specifier|public
class|class
name|ExplainableScriptIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"plugin.types"
argument_list|,
name|ExplainableScriptPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testNativeExplainScript
specifier|public
name|void
name|testNativeExplainScript
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|ExecutionException
block|{
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|indexRequests
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|indexRequests
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
literal|"type"
argument_list|)
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
literal|"number_field"
argument_list|,
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"text"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
name|indexRequests
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
name|prepareRefresh
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
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
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"text"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|scriptFunction
argument_list|(
operator|new
name|Script
argument_list|(
literal|"native_explainable_script"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"native"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|boostMode
argument_list|(
literal|"replace"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|ElasticsearchAssertions
operator|.
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|SearchHits
name|hits
init|=
name|response
operator|.
name|getHits
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|hits
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|20l
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|idCounter
init|=
literal|19
decl_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|hits
operator|.
name|getHits
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|idCounter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|explanation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|idCounter
argument_list|)
operator|+
literal|" = This script returned "
operator|+
name|Double
operator|.
name|toString
argument_list|(
name|idCounter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|explanation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"1.0 = tf(freq=1.0), with freq of"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|explanation
argument_list|()
operator|.
name|getDetails
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|idCounter
operator|--
expr_stmt|;
block|}
block|}
DECL|class|MyNativeScriptFactory
specifier|static
class|class
name|MyNativeScriptFactory
implements|implements
name|NativeScriptFactory
block|{
annotation|@
name|Override
DECL|method|newScript
specifier|public
name|ExecutableScript
name|newScript
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
return|return
operator|new
name|MyScript
argument_list|()
return|;
block|}
block|}
DECL|class|MyScript
specifier|static
class|class
name|MyScript
extends|extends
name|AbstractDoubleSearchScript
implements|implements
name|ExplainableSearchScript
implements|,
name|ExecutableScript
block|{
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|subQueryScore
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|scoreExp
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|subQueryScore
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"_score: "
argument_list|,
name|subQueryScore
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
call|(
name|float
call|)
argument_list|(
name|runAsDouble
argument_list|()
argument_list|)
argument_list|,
literal|"This script returned "
operator|+
name|runAsDouble
argument_list|()
argument_list|,
name|scoreExp
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|runAsDouble
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
return|return
operator|(
call|(
name|Number
call|)
argument_list|(
operator|(
name|ScriptDocValues
operator|)
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"number_field"
argument_list|)
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

