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
name|settings
operator|.
name|ImmutableSettings
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
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Tests for the Groovy scripting sandbox  */
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
name|TEST
argument_list|)
DECL|class|GroovySandboxScriptTests
specifier|public
class|class
name|GroovySandboxScriptTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testSandboxedGroovyScript
specifier|public
name|void
name|testSandboxedGroovyScript
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
comment|// Plain test
name|testSuccess
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// List
name|testSuccess
argument_list|(
literal|"def list = [doc['foo'].value, 3, 4]; def v = list.get(1); list.add(10)"
argument_list|)
expr_stmt|;
comment|// Ranges
name|testSuccess
argument_list|(
literal|"def range = 1..doc['foo'].value; def v = range.get(0)"
argument_list|)
expr_stmt|;
comment|// Maps
name|testSuccess
argument_list|(
literal|"def v = doc['foo'].value; def m = [:]; m.put(\\\"value\\\", v)"
argument_list|)
expr_stmt|;
comment|// Times
name|testSuccess
argument_list|(
literal|"def t = Instant.now().getMillis()"
argument_list|)
expr_stmt|;
comment|// GroovyCollections
name|testSuccess
argument_list|(
literal|"def n = [1,2,3]; GroovyCollections.max(n)"
argument_list|)
expr_stmt|;
comment|// Fail cases
name|testFailure
argument_list|(
literal|"pr = Runtime.getRuntime().exec(\\\"touch /tmp/gotcha\\\"); pr.waitFor()"
argument_list|,
literal|"Method calls not allowed on [java.lang.Runtime]"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"d = new DateTime(); d.getClass().getDeclaredMethod(\\\"plus\\\").setAccessible(true)"
argument_list|,
literal|"Expression [MethodCallExpression] is not allowed: d.getClass()"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"d = new DateTime(); d.\\\"${'get' + 'Class'}\\\"()."
operator|+
literal|"\\\"${'getDeclared' + 'Method'}\\\"(\\\"now\\\").\\\"${'set' + 'Accessible'}\\\"(false)"
argument_list|,
literal|"Expression [MethodCallExpression] is not allowed: d.$(get + Class)().$(getDeclared + Method)(now).$(set + Accessible)(false)"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"Class.forName(\\\"DateTime\\\").getDeclaredMethod(\\\"plus\\\").setAccessible(true)"
argument_list|,
literal|"Expression [MethodCallExpression] is not allowed: java.lang.Class.forName(DateTime)"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"Eval.me('2 + 2')"
argument_list|,
literal|"Method calls not allowed on [groovy.util.Eval]"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"Eval.x(5, 'x + 2')"
argument_list|,
literal|"Method calls not allowed on [groovy.util.Eval]"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"t = new java.util.concurrent.ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, "
operator|+
literal|"new java.util.concurrent.LinkedBlockingQueue<Runnable>()); t.execute({ println 5 })"
argument_list|,
literal|"Expression [ConstructorCallExpression] is not allowed: new java.util.concurrent.ThreadPoolExecutor"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"d = new Date(); java.lang.reflect.Field f = Date.class.getDeclaredField(\\\"fastTime\\\");"
operator|+
literal|" f.setAccessible(true); f.get(\\\"fastTime\\\")"
argument_list|,
literal|"Method calls not allowed on [java.lang.reflect.Field]"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"t = new Thread({ println 3 }); t.start(); t.join()"
argument_list|,
literal|"Expression [ConstructorCallExpression] is not allowed: new java.lang.Thread"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"Thread.start({ println 4 })"
argument_list|,
literal|"Method calls not allowed on [java.lang.Thread]"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"import java.util.concurrent.ThreadPoolExecutor;"
argument_list|,
literal|"Importing [java.util.concurrent.ThreadPoolExecutor] is not allowed"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"s = new java.net.URL();"
argument_list|,
literal|"Expression [ConstructorCallExpression] is not allowed: new java.net.URL()"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"def methodName = 'ex'; Runtime.\\\"${'get' + 'Runtime'}\\\"().\\\"${methodName}ec\\\"(\\\"touch /tmp/gotcha2\\\")"
argument_list|,
literal|"Expression [MethodCallExpression] is not allowed: java.lang.Runtime.$(get + Runtime)().$methodNameec(touch /tmp/gotcha2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDynamicBlacklist
specifier|public
name|void
name|testDynamicBlacklist
parameter_list|()
throws|throws
name|Exception
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
name|testSuccess
argument_list|(
literal|"[doc['foo'].value, 3, 4].isEmpty()"
argument_list|)
expr_stmt|;
name|testSuccess
argument_list|(
literal|"[doc['foo'].value, 3, 4].size()"
argument_list|)
expr_stmt|;
comment|// Now we blacklist two methods, .isEmpty() and .size()
name|Settings
name|blacklistSettings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GroovyScriptEngineService
operator|.
name|GROOVY_SCRIPT_BLACKLIST_PATCH
argument_list|,
literal|"isEmpty,size"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|blacklistSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|testFailure
argument_list|(
literal|"[doc['foo'].value, 3, 4].isEmpty()"
argument_list|,
literal|"Expression [MethodCallExpression] is not allowed: [doc[foo].value, 3, 4].isEmpty()"
argument_list|)
expr_stmt|;
name|testFailure
argument_list|(
literal|"[doc['foo'].value, 3, 4].size()"
argument_list|,
literal|"Expression [MethodCallExpression] is not allowed: [doc[foo].value, 3, 4].size()"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSuccess
specifier|public
name|void
name|testSuccess
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> script: "
operator|+
name|script
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
name|setSource
argument_list|(
literal|"{\"query\": {\"match_all\": {}},"
operator|+
literal|"\"sort\":{\"_script\": {\"script\": \""
operator|+
name|script
operator|+
literal|"; doc['foo'].value + 2\", \"type\": \"number\", \"lang\": \"groovy\"}}}"
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
name|assertThat
argument_list|(
name|resp
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getSortValues
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|7.0
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailure
specifier|public
name|void
name|testFailure
parameter_list|(
name|String
name|script
parameter_list|,
name|String
name|failMessage
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> script: "
operator|+
name|script
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
name|setSource
argument_list|(
literal|"{\"query\": {\"match_all\": {}},"
operator|+
literal|"\"sort\":{\"_script\": {\"script\": \""
operator|+
name|script
operator|+
literal|"; doc['foo'].value + 2\", \"type\": \"number\", \"lang\": \"groovy\"}}}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"script: "
operator|+
name|script
operator|+
literal|" failed to be caught be the sandbox!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"script failed, but with incorrect message: "
operator|+
name|msg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
name|failMessage
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
block|}
end_class

end_unit

