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
name|python
operator|.
name|core
operator|.
name|PyException
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
comment|/**  * Tests for Python security permissions  */
end_comment

begin_class
DECL|class|PythonSecurityTests
specifier|public
class|class
name|PythonSecurityTests
extends|extends
name|ESTestCase
block|{
DECL|field|se
specifier|private
name|PythonScriptEngineService
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
name|PythonScriptEngineService
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
literal|"python"
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
name|PyException
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
comment|// TODO: fix jython localization bugs: https://github.com/elastic/elasticsearch/issues/13967
comment|// this is the correct assert:
comment|// assertNotNull("null cause for exception: " + expected, cause);
name|assertNotNull
argument_list|(
literal|"null cause for exception"
argument_list|,
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
comment|/** Test some py scripts that are ok */
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
literal|"from java.lang import Math\nMath.cos(0)"
argument_list|)
expr_stmt|;
block|}
comment|/** Test some py scripts that should hit security exception */
DECL|method|testNotOK
specifier|public
name|void
name|testNotOK
parameter_list|()
block|{
comment|// sanity check :)
name|assertFailure
argument_list|(
literal|"from java.lang import Runtime\nRuntime.getRuntime().halt(0)"
argument_list|)
expr_stmt|;
comment|// check a few things more restrictive than the ordinary policy
comment|// no network
name|assertFailure
argument_list|(
literal|"from java.net import Socket\nSocket(\"localhost\", 1024)"
argument_list|)
expr_stmt|;
comment|// no files
name|assertFailure
argument_list|(
literal|"from java.io import File\nFile.createTempFile(\"test\", \"tmp\")"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

