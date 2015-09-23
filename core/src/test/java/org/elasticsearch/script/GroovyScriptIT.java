begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
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
name|SearchPhaseExecutionException
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|CombineFunction
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
name|script
operator|.
name|groovy
operator|.
name|GroovyScriptEngineService
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
name|constantScoreQuery
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
name|scriptQuery
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
name|assertSearchHits
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
comment|/**  * Various tests for Groovy scripting  */
end_comment

begin_class
DECL|class|GroovyScriptIT
specifier|public
class|class
name|GroovyScriptIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Test
DECL|method|testGroovyBigDecimalTransformation
specifier|public
name|void
name|testGroovyBigDecimalTransformation
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Test that something that would usually be a BigDecimal is transformed into a Double
name|assertScript
argument_list|(
literal|"def n = 1.23; assert n instanceof Double;"
argument_list|)
expr_stmt|;
name|assertScript
argument_list|(
literal|"def n = 1.23G; assert n instanceof Double;"
argument_list|)
expr_stmt|;
name|assertScript
argument_list|(
literal|"def n = BigDecimal.ONE; assert n instanceof BigDecimal;"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertScript
specifier|public
name|void
name|assertScript
parameter_list|(
name|String
name|script
parameter_list|)
block|{
comment|// SearchResponse resp = client().prepareSearch("test")
comment|// .setSource(new BytesArray("{\"query\": {\"match_all\": {}}," +
comment|// "\"sort\":{\"_script\": {\"script\": \""+ script +
comment|// "; 1\", \"type\": \"number\", \"lang\": \"groovy\"}}}")).get();
comment|// assertNoFailures(resp); NOCOMMIT fix this
block|}
annotation|@
name|Test
DECL|method|testGroovyExceptionSerialization
specifier|public
name|void
name|testGroovyExceptionSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|reqs
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
name|randomIntBetween
argument_list|(
literal|50
argument_list|,
literal|500
argument_list|)
condition|;
name|i
operator|++
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
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|reqs
argument_list|)
expr_stmt|;
try|try
block|{
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
name|constantScoreQuery
argument_list|(
name|scriptQuery
argument_list|(
operator|new
name|Script
argument_list|(
literal|"1 == not_found"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|GroovyScriptEngineService
operator|.
name|NAME
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"should not contained NotSerializableTransportException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NotSerializableTransportException"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"should have contained GroovyScriptExecutionException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"GroovyScriptExecutionException"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"should have contained not_found"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No such property: not_found"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
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
name|constantScoreQuery
argument_list|(
name|scriptQuery
argument_list|(
operator|new
name|Script
argument_list|(
literal|"assert false"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"groovy"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"should not contained NotSerializableTransportException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NotSerializableTransportException"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"should have contained GroovyScriptExecutionException"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"GroovyScriptExecutionException"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"should have contained an assert error"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"AssertionError[assert false"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGroovyScriptAccess
specifier|public
name|void
name|testGroovyScriptAccess
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"quick brow fox jumped over the lazy dog"
argument_list|,
literal|"bar"
argument_list|,
literal|1
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
literal|"doc"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"fast jumping spiders"
argument_list|,
literal|"bar"
argument_list|,
literal|2
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
literal|"doc"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"dog spiders that can eat a dog"
argument_list|,
literal|"bar"
argument_list|,
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
comment|// doc[] access
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
name|functionScoreQuery
argument_list|(
name|scriptFunction
argument_list|(
operator|new
name|Script
argument_list|(
literal|"doc['bar'].value"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"groovy"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|boostMode
argument_list|(
name|CombineFunction
operator|.
name|REPLACE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertOrderedSearchHits
argument_list|(
name|resp
argument_list|,
literal|"3"
argument_list|,
literal|"2"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
DECL|method|testScoreAccess
specifier|public
name|void
name|testScoreAccess
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"quick brow fox jumped over the lazy dog"
argument_list|,
literal|"bar"
argument_list|,
literal|1
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
literal|"doc"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"fast jumping spiders"
argument_list|,
literal|"bar"
argument_list|,
literal|2
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
literal|"doc"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"dog spiders that can eat a dog"
argument_list|,
literal|"bar"
argument_list|,
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
comment|// _score can be accessed
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
name|functionScoreQuery
argument_list|(
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"dog"
argument_list|)
argument_list|,
name|scriptFunction
argument_list|(
operator|new
name|Script
argument_list|(
literal|"_score"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"groovy"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|boostMode
argument_list|(
name|CombineFunction
operator|.
name|REPLACE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|resp
argument_list|,
literal|"3"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// _score is comparable
comment|// NOTE: it is important to use 0.0 instead of 0 instead Groovy will do an integer comparison
comment|// and if the score if between 0 and 1 it will be considered equal to 0 due to the cast
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
name|functionScoreQuery
argument_list|(
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"dog"
argument_list|)
argument_list|,
name|scriptFunction
argument_list|(
operator|new
name|Script
argument_list|(
literal|"_score> 0.0 ? _score : 0"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"groovy"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|boostMode
argument_list|(
name|CombineFunction
operator|.
name|REPLACE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertNoFailures
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|resp
argument_list|,
literal|"3"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

