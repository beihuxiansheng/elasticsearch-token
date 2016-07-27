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
name|bytes
operator|.
name|BytesArray
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
name|plugins
operator|.
name|Plugin
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
name|ESIntegTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|assertAcked
import|;
end_import

begin_class
DECL|class|StoredScriptsIT
specifier|public
class|class
name|StoredScriptsIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|SCRIPT_MAX_SIZE_IN_BYTES
specifier|private
specifier|static
specifier|final
name|int
name|SCRIPT_MAX_SIZE_IN_BYTES
init|=
literal|64
decl_stmt|;
DECL|field|LANG
specifier|private
specifier|static
specifier|final
name|String
name|LANG
init|=
name|MockScriptEngine
operator|.
name|NAME
decl_stmt|;
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|ScriptService
operator|.
name|SCRIPT_MAX_SIZE_IN_BYTES
operator|.
name|getKey
argument_list|()
argument_list|,
name|SCRIPT_MAX_SIZE_IN_BYTES
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|CustomScriptPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutStoredScript
argument_list|()
operator|.
name|setScriptLang
argument_list|(
name|LANG
argument_list|)
operator|.
name|setId
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"script\":\"1\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|script
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareGetStoredScript
argument_list|(
name|LANG
argument_list|,
literal|"foobar"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStoredScript
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|script
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|script
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareDeleteStoredScript
argument_list|()
operator|.
name|setId
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|setScriptLang
argument_list|(
name|LANG
argument_list|)
argument_list|)
expr_stmt|;
name|script
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareGetStoredScript
argument_list|(
name|LANG
argument_list|,
literal|"foobar"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStoredScript
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|script
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutStoredScript
argument_list|()
operator|.
name|setScriptLang
argument_list|(
literal|"lang#"
argument_list|)
operator|.
name|setId
argument_list|(
literal|"id#"
argument_list|)
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Validation Failed: 1: id can't contain: '#';2: lang can't contain: '#';"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxScriptSize
specifier|public
name|void
name|testMaxScriptSize
parameter_list|()
block|{
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutStoredScript
argument_list|()
operator|.
name|setScriptLang
argument_list|(
name|LANG
argument_list|)
operator|.
name|setId
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|randomAsciiOfLength
argument_list|(
name|SCRIPT_MAX_SIZE_IN_BYTES
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Limit of script size in bytes [64] has been exceeded for script [foobar] with size [65]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|CustomScriptPlugin
specifier|public
specifier|static
class|class
name|CustomScriptPlugin
extends|extends
name|MockScriptPlugin
block|{
annotation|@
name|Override
DECL|method|pluginScripts
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|pluginScripts
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"1"
argument_list|,
name|script
lambda|->
literal|"1"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

