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
name|groovy
operator|.
name|lang
operator|.
name|MissingPropertyException
import|;
end_import

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
name|codehaus
operator|.
name|groovy
operator|.
name|control
operator|.
name|MultipleCompilationErrorsException
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
name|ScriptType
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
name|security
operator|.
name|PrivilegedActionException
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
name|Arrays
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
name|EMPTY
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
comment|// field access (via map)
name|assertSuccess
argument_list|(
literal|"def foo = doc['foo'].value; if (foo == null) { return 5; }"
argument_list|)
expr_stmt|;
comment|// field access (via list)
name|assertSuccess
argument_list|(
literal|"def foo = mylist[0]; if (foo == null) { return 5; }"
argument_list|)
expr_stmt|;
comment|// field access (via array)
name|assertSuccess
argument_list|(
literal|"def foo = myarray[0]; if (foo == null) { return 5; }"
argument_list|)
expr_stmt|;
comment|// field access (via object)
name|assertSuccess
argument_list|(
literal|"def foo = myobject.primitive.toString(); if (foo == null) { return 5; }"
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
literal|"def foo = myobject.object.toString(); if (foo == null) { return 5; }"
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
literal|"def foo = myobject.list[0].primitive.toString(); if (foo == null) { return 5; }"
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
comment|// Groovy closures
name|assertSuccess
argument_list|(
literal|"[1, 2, 3, 4].findAll { it % 2 == 0 }"
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
literal|"def buckets=[ [2, 4, 6, 8], [10, 12, 16, 14], [18, 22, 20, 24] ]; buckets[-3..-1].every { it.every { i -> i % 2 == 0 } }"
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
literal|"def val = \"\"; [1, 2, 3, 4].each { val += it }; val"
argument_list|)
expr_stmt|;
comment|// Groovy uses reflection to invoke closures. These reflective calls are optimized by the JVM after "sun.reflect.inflationThreshold"
comment|// invocations. After the inflation step, access to sun.reflect.MethodAccessorImpl is required from the security manager. This test,
comment|// assuming a inflation threshold below 100 (15 is current value on Oracle JVMs), checks that the relevant permission is available.
name|assertSuccess
argument_list|(
literal|"(1..100).collect{ it + 1 }"
argument_list|)
expr_stmt|;
comment|// Fail cases:
name|assertFailure
argument_list|(
literal|"pr = Runtime.getRuntime().exec(\"touch /tmp/gotcha\"); pr.waitFor()"
argument_list|,
name|MissingPropertyException
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// infamous:
name|assertFailure
argument_list|(
literal|"java.lang.Math.class.forName(\"java.lang.Runtime\")"
argument_list|,
name|PrivilegedActionException
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// filtered directly by our classloader
name|assertFailure
argument_list|(
literal|"getClass().getClassLoader().loadClass(\"java.lang.Runtime\").availableProcessors()"
argument_list|,
name|PrivilegedActionException
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// unfortunately, we have access to other classloaders (due to indy mechanism needing getClassLoader permission)
comment|// but we can't do much with them directly at least.
name|assertFailure
argument_list|(
literal|"myobject.getClass().getClassLoader().loadClass(\"java.lang.Runtime\").availableProcessors()"
argument_list|,
name|SecurityException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"d = new DateTime(); d.getClass().getDeclaredMethod(\"year\").setAccessible(true)"
argument_list|,
name|SecurityException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"d = new DateTime(); d.\"${'get' + 'Class'}\"()."
operator|+
literal|"\"${'getDeclared' + 'Method'}\"(\"year\").\"${'set' + 'Accessible'}\"(false)"
argument_list|,
name|SecurityException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"Class.forName(\"org.joda.time.DateTime\").getDeclaredMethod(\"year\").setAccessible(true)"
argument_list|,
name|MissingPropertyException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"Eval.me('2 + 2')"
argument_list|,
name|MissingPropertyException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"Eval.x(5, 'x + 2')"
argument_list|,
name|MissingPropertyException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"d = new Date(); java.lang.reflect.Field f = Date.class.getDeclaredField(\"fastTime\");"
operator|+
literal|" f.setAccessible(true); f.get(\"fastTime\")"
argument_list|,
name|MultipleCompilationErrorsException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"def methodName = 'ex'; Runtime.\"${'get' + 'Runtime'}\"().\"${methodName}ec\"(\"touch /tmp/gotcha2\")"
argument_list|,
name|MissingPropertyException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"t = new Thread({ println 3 });"
argument_list|,
name|MultipleCompilationErrorsException
operator|.
name|class
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
name|assertFailure
argument_list|(
literal|"new File(\""
operator|+
name|dir
operator|+
literal|"\").exists()"
argument_list|,
name|MultipleCompilationErrorsException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGroovyScriptsThatThrowErrors
specifier|public
name|void
name|testGroovyScriptsThatThrowErrors
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFailure
argument_list|(
literal|"assert false, \"msg\";"
argument_list|,
name|AssertionError
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFailure
argument_list|(
literal|"def foo=false; assert foo;"
argument_list|,
name|AssertionError
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Groovy's asserts require org.codehaus.groovy.runtime.InvokerHelper, so they are denied
name|assertFailure
argument_list|(
literal|"def foo=false; assert foo, \"msg2\";"
argument_list|,
name|NoClassDefFoundError
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testGroovyBugError
specifier|public
name|void
name|testGroovyBugError
parameter_list|()
block|{
comment|// this script throws a GroovyBugError because our security manager permissions prevent Groovy from accessing this private field
comment|// and Groovy does not handle it gracefully; this test will likely start failing if the bug is fixed upstream so that a
comment|// GroovyBugError no longer surfaces here in which case the script should be replaced with another script that intentionally
comment|// surfaces a GroovyBugError
name|assertFailure
argument_list|(
literal|"[1, 2].size"
argument_list|,
name|AssertionError
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|vars
operator|.
name|put
argument_list|(
literal|"mylist"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"myarray"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"myobject"
argument_list|,
operator|new
name|MyObject
argument_list|()
argument_list|)
expr_stmt|;
name|se
operator|.
name|executable
argument_list|(
operator|new
name|CompiledScript
argument_list|(
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
literal|null
argument_list|,
name|script
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
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
DECL|class|MyObject
specifier|public
specifier|static
class|class
name|MyObject
block|{
DECL|method|getPrimitive
specifier|public
name|int
name|getPrimitive
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getObject
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
return|return
literal|"value"
return|;
block|}
DECL|method|getList
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getList
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MyObject
argument_list|()
argument_list|)
return|;
block|}
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
comment|/** asserts that a script triggers the given exceptionclass */
DECL|method|assertFailure
specifier|private
name|void
name|assertFailure
parameter_list|(
name|String
name|script
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
name|exceptionClass
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
if|if
condition|(
name|exceptionClass
operator|.
name|isAssignableFrom
argument_list|(
name|cause
operator|.
name|getClass
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unexpected exception: "
operator|+
name|cause
argument_list|,
name|expected
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

