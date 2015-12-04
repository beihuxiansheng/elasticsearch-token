begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.groovy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|groovy
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
name|CompiledScript
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
name|ScriptException
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
name|ESTestCase
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
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * Tests for the Groovy security permissions  */
end_comment

begin_class
DECL|class|GroovySecurityTests
specifier|public
class|class
name|GroovySecurityTests
extends|extends
name|ESTestCase
block|{
DECL|field|se
specifier|private
name|GroovyScriptEngineService
name|se
decl_stmt|;
static|static
block|{
comment|// ensure we load all the timezones in the parent classloader with all permissions
comment|// relates to https://github.com/elastic/elasticsearch/issues/14524
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
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
name|se
operator|=
operator|new
name|GroovyScriptEngineService
argument_list|(
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
expr_stmt|;
comment|// otherwise will exit your VM and other bad stuff
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
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|se
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testEvilGroovyScripts
specifier|public
name|void
name|testEvilGroovyScripts
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Plain test
name|assertSuccess
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// field access
name|assertSuccess
argument_list|(
literal|"def foo = doc['foo'].value; if (foo == null) { return 5; }"
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
literal|"def v = doc['foo'].value; def m = [:]; m.put(\"value\", v)"
argument_list|)
expr_stmt|;
comment|// serialization to json (this is best effort considering the unsafe etc at play)
name|assertSuccess
argument_list|(
literal|"def x = 5; groovy.json.JsonOutput.toJson(x)"
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
literal|"pr = Runtime.getRuntime().exec(\"touch /tmp/gotcha\"); pr.waitFor()"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("java.lang.RuntimePermission" "accessClassInPackage.sun.reflect")]
name|assertFailure
argument_list|(
literal|"d = new DateTime(); d.getClass().getDeclaredMethod(\"year\").setAccessible(true)"
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"d = new DateTime(); d.\"${'get' + 'Class'}\"()."
operator|+
literal|"\"${'getDeclared' + 'Method'}\"(\"year\").\"${'set' + 'Accessible'}\"(false)"
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"Class.forName(\"org.joda.time.DateTime\").getDeclaredMethod(\"year\").setAccessible(true)"
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
literal|"d = new Date(); java.lang.reflect.Field f = Date.class.getDeclaredField(\"fastTime\");"
operator|+
literal|" f.setAccessible(true); f.get(\"fastTime\")"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("java.io.FilePermission" "<<ALL FILES>>" "execute")]
name|assertFailure
argument_list|(
literal|"def methodName = 'ex'; Runtime.\"${'get' + 'Runtime'}\"().\"${methodName}ec\"(\"touch /tmp/gotcha2\")"
argument_list|)
expr_stmt|;
comment|// AccessControlException[access denied ("java.lang.RuntimePermission" "modifyThreadGroup")]
name|assertFailure
argument_list|(
literal|"t = new Thread({ println 3 });"
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
literal|"new File(\""
operator|+
name|dir
operator|+
literal|"\").exists()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** runs a script */
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|// we add a "mock document" containing a single field "foo" that returns 4 (abusing a jdk class with a getValue() method)
name|vars
operator|.
name|put
argument_list|(
literal|"doc"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<
name|Object
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|null
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|se
operator|.
name|executable
argument_list|(
operator|new
name|CompiledScript
argument_list|(
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"test"
argument_list|,
literal|"js"
argument_list|,
name|se
operator|.
name|compile
argument_list|(
name|script
argument_list|)
argument_list|)
argument_list|,
name|vars
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/** asserts that a script runs without exception */
DECL|method|assertSuccess
specifier|private
name|void
name|assertSuccess
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|doTest
argument_list|(
name|script
argument_list|)
expr_stmt|;
block|}
comment|/** asserts that a script triggers securityexception */
DECL|method|assertFailure
specifier|private
name|void
name|assertFailure
parameter_list|(
name|String
name|script
parameter_list|)
block|{
try|try
block|{
name|doTest
argument_list|(
name|script
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ScriptException
name|expected
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|expected
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"unexpected exception: "
operator|+
name|cause
argument_list|,
name|cause
operator|instanceof
name|SecurityException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

