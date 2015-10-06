begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.javascript
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|WrappedException
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
comment|/**  * Tests for the Javascript security permissions  */
end_comment

begin_class
DECL|class|JavaScriptSecurityTests
specifier|public
class|class
name|JavaScriptSecurityTests
extends|extends
name|ESTestCase
block|{
DECL|field|se
specifier|private
name|JavaScriptScriptEngineService
name|se
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|se
operator|=
operator|new
name|JavaScriptScriptEngineService
argument_list|(
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|se
operator|.
name|close
argument_list|()
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
name|se
operator|.
name|execute
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
comment|/** assert that a security exception is hit */
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
name|WrappedException
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
comment|/** Test some javascripts that are ok */
DECL|method|testOK
specifier|public
name|void
name|testOK
parameter_list|()
block|{
name|assertSuccess
argument_list|(
literal|"1 + 2"
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
literal|"Math.cos(Math.PI)"
argument_list|)
expr_stmt|;
block|}
comment|/** Test some javascripts that should hit security exception */
DECL|method|testNotOK
specifier|public
name|void
name|testNotOK
parameter_list|()
block|{
comment|// sanity check :)
name|assertFailure
argument_list|(
literal|"java.lang.Runtime.getRuntime().halt(0)"
argument_list|)
expr_stmt|;
comment|// check a few things more restrictive than the ordinary policy
comment|// no network
name|assertFailure
argument_list|(
literal|"new java.net.Socket(\"localhost\", 1024)"
argument_list|)
expr_stmt|;
comment|// no files
name|assertFailure
argument_list|(
literal|"java.io.File.createTempFile(\"test\", \"tmp\")"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

