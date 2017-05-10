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
name|ArrayList
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
name|Setting
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
name|env
operator|.
name|Environment
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
name|elasticsearch
operator|.
name|test
operator|.
name|InternalSettingsPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|watcher
operator|.
name|ResourceWatcherService
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
import|;
end_import

begin_class
DECL|class|NativeScriptTests
specifier|public
class|class
name|NativeScriptTests
extends|extends
name|ESTestCase
block|{
DECL|method|testNativeScript
specifier|public
name|void
name|testNativeScript
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"testNativeScript"
argument_list|)
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ScriptModule
name|scriptModule
init|=
operator|new
name|ScriptModule
argument_list|(
name|settings
argument_list|,
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|,
literal|null
argument_list|,
name|singletonList
argument_list|(
operator|new
name|NativeScriptEngine
argument_list|(
name|settings
argument_list|,
name|singletonMap
argument_list|(
literal|"my"
argument_list|,
operator|new
name|MyNativeScriptFactory
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|scriptSettings
init|=
name|scriptModule
operator|.
name|getSettings
argument_list|()
decl_stmt|;
name|scriptSettings
operator|.
name|add
argument_list|(
name|InternalSettingsPlugin
operator|.
name|VERSION_CREATED
argument_list|)
expr_stmt|;
name|Script
name|script
init|=
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|NativeScriptEngine
operator|.
name|NAME
argument_list|,
literal|"my"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|CompiledScript
name|compiledScript
init|=
name|scriptModule
operator|.
name|getScriptService
argument_list|()
operator|.
name|compile
argument_list|(
name|script
argument_list|,
name|ScriptContext
operator|.
name|Standard
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|ExecutableScript
name|executable
init|=
name|scriptModule
operator|.
name|getScriptService
argument_list|()
operator|.
name|executable
argument_list|(
name|compiledScript
argument_list|,
name|script
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|executable
operator|.
name|run
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFineGrainedSettingsDontAffectNativeScripts
specifier|public
name|void
name|testFineGrainedSettingsDontAffectNativeScripts
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|ScriptType
name|scriptType
init|=
name|randomFrom
argument_list|(
name|ScriptType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"script"
operator|+
literal|"."
operator|+
name|scriptType
operator|.
name|getName
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ScriptContext
name|scriptContext
init|=
name|randomFrom
argument_list|(
name|ScriptContext
operator|.
name|Standard
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"script"
operator|+
literal|"."
operator|+
name|scriptContext
operator|.
name|getKey
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Settings
name|settings
init|=
name|builder
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|ResourceWatcherService
name|resourceWatcherService
init|=
operator|new
name|ResourceWatcherService
argument_list|(
name|settings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NativeScriptFactory
argument_list|>
name|nativeScriptFactoryMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|nativeScriptFactoryMap
operator|.
name|put
argument_list|(
literal|"my"
argument_list|,
operator|new
name|MyNativeScriptFactory
argument_list|()
argument_list|)
expr_stmt|;
name|ScriptEngineRegistry
name|scriptEngineRegistry
init|=
operator|new
name|ScriptEngineRegistry
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|NativeScriptEngine
argument_list|(
name|settings
argument_list|,
name|nativeScriptFactoryMap
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ScriptContextRegistry
name|scriptContextRegistry
init|=
operator|new
name|ScriptContextRegistry
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|ScriptSettings
name|scriptSettings
init|=
operator|new
name|ScriptSettings
argument_list|(
name|scriptEngineRegistry
argument_list|,
name|scriptContextRegistry
argument_list|)
decl_stmt|;
name|ScriptService
name|scriptService
init|=
operator|new
name|ScriptService
argument_list|(
name|settings
argument_list|,
name|environment
argument_list|,
name|resourceWatcherService
argument_list|,
name|scriptEngineRegistry
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptSettings
argument_list|)
decl_stmt|;
for|for
control|(
name|ScriptContext
name|scriptContext
range|:
name|scriptContextRegistry
operator|.
name|scriptContexts
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|scriptService
operator|.
name|compile
argument_list|(
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|NativeScriptEngine
operator|.
name|NAME
argument_list|,
literal|"my"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|,
name|scriptContext
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MyNativeScriptFactory
specifier|public
specifier|static
class|class
name|MyNativeScriptFactory
implements|implements
name|NativeScriptFactory
block|{
annotation|@
name|Override
DECL|method|newScript
specifier|public
name|ExecutableScript
name|newScript
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
return|return
operator|new
name|MyScript
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"my"
return|;
block|}
block|}
DECL|class|MyScript
specifier|static
class|class
name|MyScript
extends|extends
name|AbstractExecutableScript
block|{
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
literal|"test"
return|;
block|}
block|}
block|}
end_class

end_unit

