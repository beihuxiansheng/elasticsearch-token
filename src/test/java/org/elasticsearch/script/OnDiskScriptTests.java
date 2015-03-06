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
name|ExceptionsHelper
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
name|script
operator|.
name|mustache
operator|.
name|MustacheScriptEngineService
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
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
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

begin_comment
comment|//Use Suite scope so that paths get set correctly
end_comment

begin_class
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|SUITE
argument_list|)
DECL|class|OnDiskScriptTests
specifier|public
class|class
name|OnDiskScriptTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|public
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
comment|//Set path so ScriptService will pick up the test scripts
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
literal|"path.conf"
argument_list|,
name|this
operator|.
name|getResourcePath
argument_list|(
literal|"config"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine.expression.file.aggs"
argument_list|,
literal|"off"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine.mustache.file.aggs"
argument_list|,
literal|"off"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine.mustache.file.search"
argument_list|,
literal|"off"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine.mustache.file.mapping"
argument_list|,
literal|"off"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine.mustache.file.update"
argument_list|,
literal|"off"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testFieldOnDiskScript
specifier|public
name|void
name|testFieldOnDiskScript
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 2\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 3\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 4\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"bar\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|String
name|query
init|=
literal|"{ \"query\" : { \"match_all\": {}} , \"script_fields\" : { \"test1\" : { \"script_file\" : \"script1\" }, \"test2\" : { \"script_file\" : \"script2\", \"params\":{\"factor\":3}  }}, size:1}"
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
name|setSource
argument_list|(
name|query
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"scriptTest"
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
name|assertTrue
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|SearchHit
name|sh
init|=
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|sh
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|sh
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnDiskScriptsSameNameDifferentLang
specifier|public
name|void
name|testOnDiskScriptsSameNameDifferentLang
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 2\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 3\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 4\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"bar\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|String
name|query
init|=
literal|"{ \"query\" : { \"match_all\": {}} , \"script_fields\" : { \"test1\" : { \"script_file\" : \"script1\" }, \"test2\" : { \"script_file\" : \"script1\", \"lang\":\"expression\"  }}, size:1}"
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
name|setSource
argument_list|(
name|query
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"scriptTest"
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
name|assertTrue
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|SearchHit
name|sh
init|=
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|sh
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|sh
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10d
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartiallyDisabledOnDiskScripts
specifier|public
name|void
name|testPartiallyDisabledOnDiskScripts
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
comment|//test that although aggs are disabled for expression, search scripts work fine
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 2\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 3\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo 4\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builders
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
literal|"scriptTest"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"bar\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"{\"aggs\": {\"test\": { \"terms\" : { \"script_file\":\"script1\", \"lang\": \"expression\" } } } }"
decl_stmt|;
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
name|setSource
argument_list|(
name|source
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"aggs script should have been rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"scripts of type [file], operation [aggs] and lang [expression] are disabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|query
init|=
literal|"{ \"query\" : { \"match_all\": {}} , \"script_fields\" : { \"test1\" : { \"script_file\" : \"script1\", \"lang\":\"expression\" }}, size:1}"
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
name|setSource
argument_list|(
name|query
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"scriptTest"
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
name|assertTrue
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|SearchHit
name|sh
init|=
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|sh
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10d
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllOpsDisabledOnDiskScripts
specifier|public
name|void
name|testAllOpsDisabledOnDiskScripts
parameter_list|()
block|{
comment|//whether we even compile or cache the on disk scripts doesn't change the end result (the returned error)
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"scriptTest"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"theField\":\"foo\"}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|String
name|source
init|=
literal|"{\"aggs\": {\"test\": { \"terms\" : { \"script_file\":\"script1\", \"lang\": \"mustache\" } } } }"
decl_stmt|;
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
name|setSource
argument_list|(
name|source
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"aggs script should have been rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"scripts of type [file], operation [aggs] and lang [mustache] are disabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|query
init|=
literal|"{ \"query\" : { \"match_all\": {}} , \"script_fields\" : { \"test1\" : { \"script_file\" : \"script1\", \"lang\":\"mustache\" }}, size:1}"
decl_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setSource
argument_list|(
name|query
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"scriptTest"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"search script should have been rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"scripts of type [file], operation [search] and lang [mustache] are disabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"scriptTest"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setScript
argument_list|(
literal|"script1"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|FILE
argument_list|)
operator|.
name|setScriptLang
argument_list|(
name|MustacheScriptEngineService
operator|.
name|NAME
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"update script should have been rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"failed to execute script"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"scripts of type [file], operation [update] and lang [mustache] are disabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

