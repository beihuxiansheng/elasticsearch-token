begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
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
name|inject
operator|.
name|ModuleTestCase
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
operator|.
name|Property
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
name|is
import|;
end_import

begin_class
DECL|class|SettingsModuleTests
specifier|public
class|class
name|SettingsModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|method|testValidate
specifier|public
name|void
name|testValidate
parameter_list|()
block|{
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
literal|"cluster.routing.allocation.balance.shard"
argument_list|,
literal|"2.0"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
block|}
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
literal|"cluster.routing.allocation.balance.shard"
argument_list|,
literal|"[2.0]"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
try|try
block|{
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Failed to parse value [[2.0]] for setting [cluster.routing.allocation.balance.shard]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRegisterSettings
specifier|public
name|void
name|testRegisterSettings
parameter_list|()
block|{
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
literal|"some.custom.setting"
argument_list|,
literal|"2.0"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|floatSetting
argument_list|(
literal|"some.custom.setting"
argument_list|,
literal|1.0f
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
block|}
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
literal|"some.custom.setting"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|floatSetting
argument_list|(
literal|"some.custom.setting"
argument_list|,
literal|1.0f
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Failed to parse value [false] for setting [some.custom.setting]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testTribeSetting
specifier|public
name|void
name|testTribeSetting
parameter_list|()
block|{
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
literal|"tribe.t1.cluster.routing.allocation.balance.shard"
argument_list|,
literal|"2.0"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
block|}
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
literal|"tribe.t1.cluster.routing.allocation.balance.shard"
argument_list|,
literal|"[2.0]"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
try|try
block|{
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"tribe.t1 validation failed: Failed to parse value [[2.0]] for setting [cluster.routing.allocation.balance.shard]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSpecialTribeSetting
specifier|public
name|void
name|testSpecialTribeSetting
parameter_list|()
block|{
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
literal|"tribe.blocks.write"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
block|}
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
literal|"tribe.blocks.write"
argument_list|,
literal|"BOOM"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
try|try
block|{
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Failed to parse value [BOOM] cannot be parsed to boolean [ true/1/on/yes OR false/0/off/no ]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"tribe.blocks.wtf"
argument_list|,
literal|"BOOM"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
try|try
block|{
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"tribe.blocks validation failed: unknown setting [wtf]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testLoggerSettings
specifier|public
name|void
name|testLoggerSettings
parameter_list|()
block|{
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
literal|"logger._root"
argument_list|,
literal|"TRACE"
argument_list|)
operator|.
name|put
argument_list|(
literal|"logger.transport"
argument_list|,
literal|"INFO"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
block|}
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
literal|"logger._root"
argument_list|,
literal|"BOOM"
argument_list|)
operator|.
name|put
argument_list|(
literal|"logger.transport"
argument_list|,
literal|"WOW"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
try|try
block|{
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"No enum constant org.elasticsearch.common.logging.ESLoggerFactory.LogLevel.BOOM"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRegisterSettingsFilter
specifier|public
name|void
name|testRegisterSettingsFilter
parameter_list|()
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
literal|"foo.bar"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar.foo"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar.baz"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"foo.bar"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"bar.foo"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|,
name|Property
operator|.
name|Filtered
argument_list|)
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"bar.baz"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerSettingsFilter
argument_list|(
literal|"foo.*"
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|registerSettingsFilter
argument_list|(
literal|"bar.foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"filter [bar.foo] has already been registered"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|Settings
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|==
name|settings
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|SettingsFilter
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|filter
argument_list|(
name|settings
argument_list|)
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|SettingsFilter
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|filter
argument_list|(
name|settings
argument_list|)
operator|.
name|getAsMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"bar.baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|SettingsFilter
operator|.
name|class
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|filter
argument_list|(
name|settings
argument_list|)
operator|.
name|getAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"bar.baz"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMutuallyExclusiveScopes
specifier|public
name|void
name|testMutuallyExclusiveScopes
parameter_list|()
block|{
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"foo.bar"
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"foo.bar"
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
argument_list|)
expr_stmt|;
comment|// Those should fail
try|try
block|{
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No scope should fail"
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
name|containsString
argument_list|(
literal|"No scope found for setting"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Some settings have both scopes - that's fine too if they have per-node defaults
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"foo.bar"
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"foo.bar"
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"already registered"
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
name|containsString
argument_list|(
literal|"Cannot register setting [foo.bar] twice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|module
operator|.
name|registerSetting
argument_list|(
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"foo.bar"
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"already registered"
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
name|containsString
argument_list|(
literal|"Cannot register setting [foo.bar] twice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

