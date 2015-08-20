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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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

begin_class
DECL|class|ScriptContextRegistryTests
specifier|public
class|class
name|ScriptContextRegistryTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testValidateCustomScriptContextsOperation
specifier|public
name|void
name|testValidateCustomScriptContextsOperation
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
specifier|final
name|String
name|rejectedContext
range|:
name|ScriptContextRegistry
operator|.
name|RESERVED_SCRIPT_CONTEXTS
control|)
block|{
try|try
block|{
comment|//try to register a prohibited script context
operator|new
name|ScriptContextRegistry
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
literal|"test"
argument_list|,
name|rejectedContext
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ScriptContextRegistry initialization should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|Matchers
operator|.
name|containsString
argument_list|(
literal|"["
operator|+
name|rejectedContext
operator|+
literal|"] is a reserved name, it cannot be registered as a custom script context"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testValidateCustomScriptContextsPluginName
specifier|public
name|void
name|testValidateCustomScriptContextsPluginName
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
specifier|final
name|String
name|rejectedContext
range|:
name|ScriptContextRegistry
operator|.
name|RESERVED_SCRIPT_CONTEXTS
control|)
block|{
try|try
block|{
comment|//try to register a prohibited script context
operator|new
name|ScriptContextRegistry
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
name|rejectedContext
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ScriptContextRegistry initialization should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|Matchers
operator|.
name|containsString
argument_list|(
literal|"["
operator|+
name|rejectedContext
operator|+
literal|"] is a reserved name, it cannot be registered as a custom script context"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testValidateCustomScriptContextsEmptyPluginName
specifier|public
name|void
name|testValidateCustomScriptContextsEmptyPluginName
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
literal|""
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testValidateCustomScriptContextsEmptyOperation
specifier|public
name|void
name|testValidateCustomScriptContextsEmptyOperation
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
literal|"test"
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDuplicatedPluginScriptContexts
specifier|public
name|void
name|testDuplicatedPluginScriptContexts
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
comment|//try to register a prohibited script context
operator|new
name|ScriptContextRegistry
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
literal|"testplugin"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
literal|"testplugin"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ScriptContextRegistry initialization should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|Matchers
operator|.
name|containsString
argument_list|(
literal|"script context [testplugin_test] cannot be registered twice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNonDuplicatedPluginScriptContexts
specifier|public
name|void
name|testNonDuplicatedPluginScriptContexts
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|ScriptContextRegistry
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
literal|"testplugin1"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
operator|new
name|ScriptContext
operator|.
name|Plugin
argument_list|(
literal|"testplugin2"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
