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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|ShardSearchFailure
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
name|nio
operator|.
name|file
operator|.
name|Path
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
comment|/**  * Tests for the Groovy security permissions  */
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
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|)
DECL|class|GroovySecurityTests
specifier|public
class|class
name|GroovySecurityTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
literal|"test requires security manager to be enabled"
argument_list|,
name|System
operator|.
name|getSecurityManager
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEvilGroovyScripts
specifier|public
name|void
name|testEvilGroovyScripts
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nodes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"script.inline"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.indexed"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
name|nodes
argument_list|,
name|nodeSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
name|nodes
operator|+
literal|""
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
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
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
name|assertSuccess
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// numeric field access
name|assertSuccess
argument_list|(
literal|"def foo = doc['foo'].value; if (foo == null) { return 5; }"
argument_list|)
expr_stmt|;
comment|// string field access
name|assertSuccess
argument_list|(
literal|"def bar = doc['bar'].value; if (bar == null) { return 5; }"
argument_list|)
expr_stmt|;
comment|// List
name|assertSuccess
argument_list|(
literal|"def list = [doc['foo'].value, 3, 4]; def v = list.get(1); list.add(10)"
argument_list|)
expr_stmt|;
comment|// Ranges
name|assertSuccess
argument_list|(
literal|"def range = 1..doc['foo'].value; def v = range.get(0)"
argument_list|)
expr_stmt|;
comment|// Maps
name|assertSuccess
argument_list|(
literal|"def v = doc['foo'].value; def m = [:]; m.put(\\\"value\\\", v)"
argument_list|)
expr_stmt|;
comment|// Times
name|assertSuccess
argument_list|(
literal|"def t = Instant.now().getMillis()"
argument_list|)
expr_stmt|;
comment|// GroovyCollections
name|assertSuccess
argument_list|(
literal|"def n = [1,2,3]; GroovyCollections.max(n)"
argument_list|)
expr_stmt|;
comment|// Fail cases:
comment|// AccessControlException[access denied ("java.io.FilePermission" "<<ALL FILES>>" "execute")]
name|assertFailure
argument_list|(
literal|"pr = Runtime.getRuntime().exec(\\\"touch /tmp/gotcha\\\"); pr.waitFor()"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("java.lang.RuntimePermission" "accessClassInPackage.sun.reflect")]
name|assertFailure
argument_list|(
literal|"d = new DateTime(); d.getClass().getDeclaredMethod(\\\"year\\\").setAccessible(true)"
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"d = new DateTime(); d.\\\"${'get' + 'Class'}\\\"()."
operator|+
literal|"\\\"${'getDeclared' + 'Method'}\\\"(\\\"year\\\").\\\"${'set' + 'Accessible'}\\\"(false)"
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"Class.forName(\\\"org.joda.time.DateTime\\\").getDeclaredMethod(\\\"year\\\").setAccessible(true)"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("groovy.security.GroovyCodeSourcePermission" "/groovy/shell")]
name|assertFailure
argument_list|(
literal|"Eval.me('2 + 2')"
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"Eval.x(5, 'x + 2')"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("java.lang.RuntimePermission" "accessDeclaredMembers")]
name|assertFailure
argument_list|(
literal|"d = new Date(); java.lang.reflect.Field f = Date.class.getDeclaredField(\\\"fastTime\\\");"
operator|+
literal|" f.setAccessible(true); f.get(\\\"fastTime\\\")"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("java.io.FilePermission" "<<ALL FILES>>" "execute")]
name|assertFailure
argument_list|(
literal|"def methodName = 'ex'; Runtime.\\\"${'get' + 'Runtime'}\\\"().\\\"${methodName}ec\\\"(\\\"touch /tmp/gotcha2\\\")"
argument_list|)
expr_stmt|;
comment|// test a directory we normally have access to, but the groovy script does not.
name|Path
name|dir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// TODO: figure out the necessary escaping for windows paths here :)
if|if
condition|(
operator|!
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
comment|// access denied ("java.io.FilePermission" ".../tempDir-00N" "read")
name|assertFailure
argument_list|(
literal|"new File(\\\""
operator|+
name|dir
operator|+
literal|"\\\").exists()"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertSuccess
specifier|private
name|void
name|assertSuccess
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
name|assertEquals
argument_list|(
literal|1
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
DECL|method|assertFailure
specifier|private
name|void
name|assertFailure
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
name|assertEquals
argument_list|(
literal|0
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
name|ShardSearchFailure
name|fails
index|[]
init|=
name|resp
operator|.
name|getShardFailures
argument_list|()
decl_stmt|;
comment|// TODO: GroovyScriptExecutionException needs work
for|for
control|(
name|ShardSearchFailure
name|fail
range|:
name|fails
control|)
block|{
name|assertTrue
argument_list|(
name|fail
operator|.
name|getCause
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"AccessControlException[access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

