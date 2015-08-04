begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.python
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|python
package|;
end_package

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
name|org
operator|.
name|junit
operator|.
name|After
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|CyclicBarrier
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
name|ThreadLocalRandom
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
name|atomic
operator|.
name|AtomicBoolean
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
comment|/**  *  */
end_comment

begin_class
DECL|class|PythonScriptMultiThreadedTest
specifier|public
class|class
name|PythonScriptMultiThreadedTest
extends|extends
name|ESTestCase
block|{
annotation|@
name|After
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// We need to clear some system properties
name|System
operator|.
name|clearProperty
argument_list|(
literal|"python.cachedir.skip"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"python.console.encoding"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExecutableNoRuntimeParams
specifier|public
name|void
name|testExecutableNoRuntimeParams
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PythonScriptEngineService
name|se
init|=
operator|new
name|PythonScriptEngineService
argument_list|(
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|compiled
init|=
name|se
operator|.
name|compile
argument_list|(
literal|"x + y"
argument_list|)
decl_stmt|;
specifier|final
name|CompiledScript
name|compiledScript
init|=
operator|new
name|CompiledScript
argument_list|(
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"testExecutableNoRuntimeParams"
argument_list|,
literal|"python"
argument_list|,
name|compiled
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|4
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|threads
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|threads
operator|.
name|length
operator|+
literal|1
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|long
name|x
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|y
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|addition
init|=
name|x
operator|+
name|y
decl_stmt|;
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
name|vars
operator|.
name|put
argument_list|(
literal|"x"
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"y"
argument_list|,
name|y
argument_list|)
expr_stmt|;
name|ExecutableScript
name|script
init|=
name|se
operator|.
name|executable
argument_list|(
name|compiledScript
argument_list|,
name|vars
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|long
name|result
init|=
operator|(
operator|(
name|Number
operator|)
name|script
operator|.
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|equalTo
argument_list|(
name|addition
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"failed"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|failed
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//    @Test public void testExecutableWithRuntimeParams() throws Exception {
comment|//        final PythonScriptEngineService se = new PythonScriptEngineService(Settings.Builder.EMPTY_SETTINGS);
comment|//        final Object compiled = se.compile("x + y");
comment|//        final AtomicBoolean failed = new AtomicBoolean();
comment|//
comment|//        Thread[] threads = new Thread[50];
comment|//        final CountDownLatch latch = new CountDownLatch(threads.length);
comment|//        final CyclicBarrier barrier = new CyclicBarrier(threads.length + 1);
comment|//        for (int i = 0; i< threads.length; i++) {
comment|//            threads[i] = new Thread(new Runnable() {
comment|//                @Override public void run() {
comment|//                    try {
comment|//                        barrier.await();
comment|//                        long x = ThreadLocalRandom.current().nextInt();
comment|//                        Map<String, Object> vars = new HashMap<String, Object>();
comment|//                        vars.put("x", x);
comment|//                        ExecutableScript script = se.executable(compiled, vars);
comment|//                        Map<String, Object> runtimeVars = new HashMap<String, Object>();
comment|//                        for (int i = 0; i< 100000; i++) {
comment|//                            long y = ThreadLocalRandom.current().nextInt();
comment|//                            long addition = x + y;
comment|//                            runtimeVars.put("y", y);
comment|//                            long result = ((Number) script.run(runtimeVars)).longValue();
comment|//                            assertThat(result, equalTo(addition));
comment|//                        }
comment|//                    } catch (Throwable t) {
comment|//                        failed.set(true);
comment|//                        logger.error("failed", t);
comment|//                    } finally {
comment|//                        latch.countDown();
comment|//                    }
comment|//                }
comment|//            });
comment|//        }
comment|//        for (int i = 0; i< threads.length; i++) {
comment|//            threads[i].start();
comment|//        }
comment|//        barrier.await();
comment|//        latch.await();
comment|//        assertThat(failed.get(), equalTo(false));
comment|//    }
annotation|@
name|Test
DECL|method|testExecute
specifier|public
name|void
name|testExecute
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PythonScriptEngineService
name|se
init|=
operator|new
name|PythonScriptEngineService
argument_list|(
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|compiled
init|=
name|se
operator|.
name|compile
argument_list|(
literal|"x + y"
argument_list|)
decl_stmt|;
specifier|final
name|CompiledScript
name|compiledScript
init|=
operator|new
name|CompiledScript
argument_list|(
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"testExecute"
argument_list|,
literal|"python"
argument_list|,
name|compiled
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|4
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|threads
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|threads
operator|.
name|length
operator|+
literal|1
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeVars
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|long
name|x
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|y
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|addition
init|=
name|x
operator|+
name|y
decl_stmt|;
name|runtimeVars
operator|.
name|put
argument_list|(
literal|"x"
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|runtimeVars
operator|.
name|put
argument_list|(
literal|"y"
argument_list|,
name|y
argument_list|)
expr_stmt|;
name|long
name|result
init|=
operator|(
operator|(
name|Number
operator|)
name|se
operator|.
name|execute
argument_list|(
name|compiledScript
argument_list|,
name|runtimeVars
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|equalTo
argument_list|(
name|addition
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"failed"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|failed
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

