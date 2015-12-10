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
name|ElasticsearchParseException
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
name|collect
operator|.
name|Tuple
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|TimeValue
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_class
DECL|class|SettingTests
specifier|public
class|class
name|SettingTests
extends|extends
name|ESTestCase
block|{
DECL|method|testGet
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|booleanSetting
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"foo.bar"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|booleanSetting
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|booleanSetting
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|booleanSetting
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testByteSize
specifier|public
name|void
name|testByteSize
parameter_list|()
block|{
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|byteSizeValueSetting
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"a.byte.size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|1024
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|byteSizeValueSetting
operator|.
name|isGroupSetting
argument_list|()
argument_list|)
expr_stmt|;
name|ByteSizeValue
name|byteSizeValue
init|=
name|byteSizeValueSetting
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|byteSizeValue
operator|.
name|bytes
argument_list|()
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|AtomicReference
argument_list|<
name|ByteSizeValue
argument_list|>
name|value
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|settingUpdater
init|=
name|byteSizeValueSetting
operator|.
name|newUpdater
argument_list|(
name|value
operator|::
name|set
argument_list|,
name|logger
argument_list|)
decl_stmt|;
try|try
block|{
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"a.byte.size"
argument_list|,
literal|12
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no unit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"failed to parse setting [a.byte.size] with value [12] as a size in bytes: unit is missing or unrecognized"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"a.byte.size"
argument_list|,
literal|"12b"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|12
argument_list|)
argument_list|,
name|value
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleUpdate
specifier|public
name|void
name|testSimpleUpdate
parameter_list|()
block|{
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|booleanSetting
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"foo.bar"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
name|atomicBoolean
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|settingUpdater
init|=
name|booleanSetting
operator|.
name|newUpdater
argument_list|(
name|atomicBoolean
operator|::
name|set
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|Settings
name|build
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
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|settingUpdater
operator|.
name|apply
argument_list|(
name|build
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|atomicBoolean
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|build
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|settingUpdater
operator|.
name|apply
argument_list|(
name|build
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|atomicBoolean
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// try update bogus value
name|build
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|"I am not a boolean"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|settingUpdater
operator|.
name|apply
argument_list|(
name|build
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"not a boolean"
argument_list|)
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
literal|"Failed to parse value [I am not a boolean] for setting [foo.bar]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUpdateNotDynamic
specifier|public
name|void
name|testUpdateNotDynamic
parameter_list|()
block|{
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|booleanSetting
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"foo.bar"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|booleanSetting
operator|.
name|isGroupSetting
argument_list|()
argument_list|)
expr_stmt|;
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
name|atomicBoolean
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|booleanSetting
operator|.
name|newUpdater
argument_list|(
name|atomicBoolean
operator|::
name|set
argument_list|,
name|logger
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"not dynamic"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"setting [foo.bar] is not dynamic"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUpdaterIsIsolated
specifier|public
name|void
name|testUpdaterIsIsolated
parameter_list|()
block|{
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|booleanSetting
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"foo.bar"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
name|ab1
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
name|ab2
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|settingUpdater
init|=
name|booleanSetting
operator|.
name|newUpdater
argument_list|(
name|ab1
operator|::
name|set
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|settingUpdater2
init|=
name|booleanSetting
operator|.
name|newUpdater
argument_list|(
name|ab2
operator|::
name|set
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ab1
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ab2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefault
specifier|public
name|void
name|testDefault
parameter_list|()
block|{
name|TimeValue
name|defautlValue
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1000000
argument_list|)
argument_list|)
decl_stmt|;
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|setting
init|=
name|Setting
operator|.
name|positiveTimeSetting
argument_list|(
literal|"my.time.value"
argument_list|,
name|defautlValue
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|setting
operator|.
name|isGroupSetting
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|aDefault
init|=
name|setting
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|defautlValue
operator|.
name|millis
argument_list|()
operator|+
literal|"ms"
argument_list|,
name|aDefault
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defautlValue
operator|.
name|millis
argument_list|()
argument_list|,
name|setting
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|Setting
argument_list|<
name|String
argument_list|>
name|secondaryDefault
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"foo.bar"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|get
argument_list|(
literal|"old.foo.bar"
argument_list|,
literal|"some_default"
argument_list|)
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"some_default"
argument_list|,
name|secondaryDefault
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"42"
argument_list|,
name|secondaryDefault
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"old.foo.bar"
argument_list|,
literal|42
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplexType
specifier|public
name|void
name|testComplexType
parameter_list|()
block|{
name|AtomicReference
argument_list|<
name|ComplexType
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Setting
argument_list|<
name|ComplexType
argument_list|>
name|setting
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"foo.bar"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
literal|""
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
operator|new
name|ComplexType
argument_list|(
name|s
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|setting
operator|.
name|isGroupSetting
argument_list|()
argument_list|)
expr_stmt|;
name|ref
operator|.
name|set
argument_list|(
name|setting
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|ComplexType
name|type
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|settingUpdater
init|=
name|setting
operator|.
name|newUpdater
argument_list|(
name|ref
operator|::
name|set
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"no update - type has not changed"
argument_list|,
name|type
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// change from default
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"update - type has changed"
argument_list|,
name|type
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|ref
operator|.
name|get
argument_list|()
operator|.
name|foo
argument_list|)
expr_stmt|;
comment|// change back to default...
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"update - type has changed"
argument_list|,
name|type
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|ref
operator|.
name|get
argument_list|()
operator|.
name|foo
argument_list|)
expr_stmt|;
block|}
DECL|method|testType
specifier|public
name|void
name|testType
parameter_list|()
block|{
name|Setting
argument_list|<
name|Integer
argument_list|>
name|integerSetting
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"foo.int.bar"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|integerSetting
operator|.
name|getScope
argument_list|()
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
expr_stmt|;
name|integerSetting
operator|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"foo.int.bar"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|integerSetting
operator|.
name|getScope
argument_list|()
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|INDEX
argument_list|)
expr_stmt|;
block|}
DECL|method|testGroups
specifier|public
name|void
name|testGroups
parameter_list|()
block|{
name|AtomicReference
argument_list|<
name|Settings
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Setting
argument_list|<
name|Settings
argument_list|>
name|setting
init|=
name|Setting
operator|.
name|groupSetting
argument_list|(
literal|"foo.bar."
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|setting
operator|.
name|isGroupSetting
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|settingUpdater
init|=
name|setting
operator|.
name|newUpdater
argument_list|(
name|ref
operator|::
name|set
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|Settings
name|currentInput
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar.1.value"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.2.value"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.3.value"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
name|previousInput
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|currentInput
argument_list|,
name|previousInput
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|asMap
init|=
name|settings
operator|.
name|getAsGroups
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|asMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"3"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|previousInput
operator|=
name|currentInput
expr_stmt|;
name|currentInput
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar.1.value"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.2.value"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.3.value"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Settings
name|current
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|currentInput
argument_list|,
name|previousInput
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|current
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|previousInput
operator|=
name|currentInput
expr_stmt|;
name|currentInput
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar.1.value"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.2.value"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// now update and check that we got it
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|currentInput
argument_list|,
name|previousInput
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|current
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|asMap
operator|=
name|ref
operator|.
name|get
argument_list|()
operator|.
name|getAsGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|asMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|previousInput
operator|=
name|currentInput
expr_stmt|;
name|currentInput
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar.1.value"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.2.value"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// now update and check that we got it
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|currentInput
argument_list|,
name|previousInput
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|current
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|asMap
operator|=
name|ref
operator|.
name|get
argument_list|()
operator|.
name|getAsGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|asMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|setting
operator|.
name|match
argument_list|(
literal|"foo.bar.baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|setting
operator|.
name|match
argument_list|(
literal|"foo.baz.bar"
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
name|predicateSettingUpdater
init|=
name|setting
operator|.
name|newUpdater
argument_list|(
name|ref
operator|::
name|set
argument_list|,
name|logger
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|assertFalse
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|predicateSettingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.bar.1.value"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.bar.2.value"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"not accepted"
argument_list|)
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
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"illegal value can't update [foo.bar.] from [{}] to [{1.value=1, 2.value=2}]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ComplexType
specifier|public
specifier|static
class|class
name|ComplexType
block|{
DECL|field|foo
specifier|final
name|String
name|foo
decl_stmt|;
DECL|method|ComplexType
specifier|public
name|ComplexType
parameter_list|(
name|String
name|foo
parameter_list|)
block|{
name|this
operator|.
name|foo
operator|=
name|foo
expr_stmt|;
block|}
block|}
DECL|class|Composite
specifier|public
specifier|static
class|class
name|Composite
block|{
DECL|field|b
specifier|private
name|Integer
name|b
decl_stmt|;
DECL|field|a
specifier|private
name|Integer
name|a
decl_stmt|;
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|Integer
name|a
parameter_list|,
name|Integer
name|b
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
block|}
DECL|method|testComposite
specifier|public
name|void
name|testComposite
parameter_list|()
block|{
name|Composite
name|c
init|=
operator|new
name|Composite
argument_list|()
decl_stmt|;
name|Setting
argument_list|<
name|Integer
argument_list|>
name|a
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"foo.int.bar.a"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|Setting
argument_list|<
name|Integer
argument_list|>
name|b
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"foo.int.bar.b"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
name|ClusterSettings
operator|.
name|SettingUpdater
argument_list|<
name|Tuple
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|settingUpdater
init|=
name|Setting
operator|.
name|compoundUpdater
argument_list|(
name|c
operator|::
name|set
argument_list|,
name|a
argument_list|,
name|b
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|a
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|b
argument_list|)
expr_stmt|;
name|Settings
name|build
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.int.bar.a"
argument_list|,
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|build
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|c
operator|.
name|a
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c
operator|.
name|b
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
name|aValue
init|=
name|c
operator|.
name|a
decl_stmt|;
name|assertFalse
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|build
argument_list|,
name|build
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|aValue
argument_list|,
name|c
operator|.
name|a
argument_list|)
expr_stmt|;
name|Settings
name|previous
init|=
name|build
decl_stmt|;
name|build
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo.int.bar.a"
argument_list|,
literal|2
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo.int.bar.b"
argument_list|,
literal|5
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|build
argument_list|,
name|previous
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|c
operator|.
name|a
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|c
operator|.
name|b
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// reset to default
name|assertTrue
argument_list|(
name|settingUpdater
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|build
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c
operator|.
name|a
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c
operator|.
name|b
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

