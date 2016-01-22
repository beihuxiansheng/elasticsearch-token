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
name|common
operator|.
name|ContextAndHeaderHolder
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|// TODO: these really should just be part of ScriptService tests, there is nothing special about them
end_comment

begin_class
DECL|class|FileScriptTests
specifier|public
class|class
name|FileScriptTests
extends|extends
name|ESTestCase
block|{
DECL|method|makeScriptService
name|ScriptService
name|makeScriptService
parameter_list|(
name|Settings
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|homeDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|scriptsDir
init|=
name|homeDir
operator|.
name|resolve
argument_list|(
literal|"config"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"scripts"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|scriptsDir
argument_list|)
expr_stmt|;
name|Path
name|mockscript
init|=
name|scriptsDir
operator|.
name|resolve
argument_list|(
literal|"script1.mockscript"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|mockscript
argument_list|,
literal|"1"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
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
name|homeDir
argument_list|)
comment|// no file watching, so we don't need a ResourceWatcherService
operator|.
name|put
argument_list|(
name|ScriptService
operator|.
name|SCRIPT_AUTO_RELOAD_ENABLED_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|ScriptEngineService
argument_list|>
name|engines
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|MockScriptEngine
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ScriptEngineRegistry
name|scriptEngineRegistry
init|=
operator|new
name|ScriptEngineRegistry
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ScriptEngineRegistry
operator|.
name|ScriptEngineRegistration
argument_list|(
name|MockScriptEngine
operator|.
name|class
argument_list|,
name|MockScriptEngine
operator|.
name|TYPES
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
name|Collections
operator|.
name|emptyList
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
return|return
operator|new
name|ScriptService
argument_list|(
name|settings
argument_list|,
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|,
name|engines
argument_list|,
literal|null
argument_list|,
name|scriptEngineRegistry
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptSettings
argument_list|)
return|;
block|}
DECL|method|testFileScriptFound
specifier|public
name|void
name|testFileScriptFound
parameter_list|()
throws|throws
name|Exception
block|{
name|ContextAndHeaderHolder
name|contextAndHeaders
init|=
operator|new
name|ContextAndHeaderHolder
argument_list|()
decl_stmt|;
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
literal|"script.engine."
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|".file.aggs"
argument_list|,
literal|"off"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ScriptService
name|scriptService
init|=
name|makeScriptService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Script
name|script
init|=
operator|new
name|Script
argument_list|(
literal|"script1"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|FILE
argument_list|,
name|MockScriptEngine
operator|.
name|NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|scriptService
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
argument_list|,
name|contextAndHeaders
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllOpsDisabled
specifier|public
name|void
name|testAllOpsDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|ContextAndHeaderHolder
name|contextAndHeaders
init|=
operator|new
name|ContextAndHeaderHolder
argument_list|()
decl_stmt|;
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
literal|"script.engine."
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|".file.aggs"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine."
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|".file.search"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine."
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|".file.mapping"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine."
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|".file.update"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|put
argument_list|(
literal|"script.engine."
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|".file.ingest"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ScriptService
name|scriptService
init|=
name|makeScriptService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Script
name|script
init|=
operator|new
name|Script
argument_list|(
literal|"script1"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|FILE
argument_list|,
name|MockScriptEngine
operator|.
name|NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|ScriptContext
name|context
range|:
name|ScriptContext
operator|.
name|Standard
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|scriptService
operator|.
name|compile
argument_list|(
name|script
argument_list|,
name|context
argument_list|,
name|contextAndHeaders
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|context
operator|.
name|getKey
argument_list|()
operator|+
literal|" script should have been rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"scripts of type [file], operation ["
operator|+
name|context
operator|.
name|getKey
argument_list|()
operator|+
literal|"] and lang ["
operator|+
name|MockScriptEngine
operator|.
name|NAME
operator|+
literal|"] are disabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

