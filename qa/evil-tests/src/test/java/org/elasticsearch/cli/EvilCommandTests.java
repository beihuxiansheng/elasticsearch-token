begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cli
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
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
name|CoreMatchers
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
name|isEmptyString
import|;
end_import

begin_class
DECL|class|EvilCommandTests
specifier|public
class|class
name|EvilCommandTests
extends|extends
name|ESTestCase
block|{
DECL|method|testCommandShutdownHook
specifier|public
name|void
name|testCommandShutdownHook
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|shouldThrow
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Command
name|command
init|=
operator|new
name|Command
argument_list|(
literal|"test-command-shutdown-hook"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|execute
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|OptionSet
name|options
parameter_list|)
throws|throws
name|Exception
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldThrow
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fail"
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|MockTerminal
name|terminal
init|=
operator|new
name|MockTerminal
argument_list|()
decl_stmt|;
name|command
operator|.
name|main
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|command
operator|.
name|shutdownHookThread
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// successful removal here asserts that the runtime hook was installed in Command#main
name|assertTrue
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|removeShutdownHook
argument_list|(
name|command
operator|.
name|shutdownHookThread
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|command
operator|.
name|shutdownHookThread
operator|.
name|get
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|command
operator|.
name|shutdownHookThread
operator|.
name|get
argument_list|()
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|closed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|output
init|=
name|terminal
operator|.
name|getOutput
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldThrow
condition|)
block|{
comment|// ensure that we dump the exception
name|assertThat
argument_list|(
name|output
argument_list|,
name|containsString
argument_list|(
literal|"java.io.IOException: fail"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure that we dump the stack trace too
name|assertThat
argument_list|(
name|output
argument_list|,
name|containsString
argument_list|(
literal|"\tat org.elasticsearch.cli.EvilCommandTests$1.close"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|output
argument_list|,
name|isEmptyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
