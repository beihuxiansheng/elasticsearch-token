begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|PluginsAndModules
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|contains
import|;
end_import

begin_class
DECL|class|PluginInfoTests
specifier|public
class|class
name|PluginInfoTests
extends|extends
name|ESTestCase
block|{
DECL|method|testReadFromProperties
specifier|public
name|void
name|testReadFromProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
literal|"my_plugin"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|,
literal|"java.version"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.specification.version"
argument_list|)
argument_list|,
literal|"classname"
argument_list|,
literal|"FakePlugin"
argument_list|)
expr_stmt|;
name|PluginInfo
name|info
init|=
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"my_plugin"
argument_list|,
name|info
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fake desc"
argument_list|,
name|info
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|info
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FakePlugin"
argument_list|,
name|info
operator|.
name|getClassname
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|isIsolated
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadFromPropertiesNameMissing
specifier|public
name|void
name|testReadFromPropertiesNameMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected missing name exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Property [name] is missing in"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"name"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected missing name exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Property [name] is missing in"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesDescriptionMissing
specifier|public
name|void
name|testReadFromPropertiesDescriptionMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"name"
argument_list|,
literal|"fake-plugin"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected missing description exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"[description] is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesVersionMissing
specifier|public
name|void
name|testReadFromPropertiesVersionMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
literal|"fake-plugin"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected missing version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"[version] is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesElasticsearchVersionMissing
specifier|public
name|void
name|testReadFromPropertiesElasticsearchVersionMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
literal|"my_plugin"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected missing elasticsearch version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"[elasticsearch.version] is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesJavaVersionMissing
specifier|public
name|void
name|testReadFromPropertiesJavaVersionMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
literal|"my_plugin"
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected missing java version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"[java.version] is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesJavaVersionIncompatible
specifier|public
name|void
name|testReadFromPropertiesJavaVersionIncompatible
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pluginName
init|=
literal|"fake-plugin"
decl_stmt|;
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
name|pluginName
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|,
literal|"java.version"
argument_list|,
literal|"1000000.0"
argument_list|,
literal|"classname"
argument_list|,
literal|"FakePlugin"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected incompatible java version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
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
name|pluginName
operator|+
literal|" requires Java"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesBadJavaVersionFormat
specifier|public
name|void
name|testReadFromPropertiesBadJavaVersionFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pluginName
init|=
literal|"fake-plugin"
decl_stmt|;
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
name|pluginName
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|,
literal|"java.version"
argument_list|,
literal|"1.7.0_80"
argument_list|,
literal|"classname"
argument_list|,
literal|"FakePlugin"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected bad java version format exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
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
name|equals
argument_list|(
literal|"version string must be a sequence of nonnegative decimal integers separated by \".\"'s and may have leading zeros but was 1.7.0_80"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesBogusElasticsearchVersion
specifier|public
name|void
name|testReadFromPropertiesBogusElasticsearchVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|,
literal|"name"
argument_list|,
literal|"my_plugin"
argument_list|,
literal|"elasticsearch.version"
argument_list|,
literal|"bogus"
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected bogus elasticsearch version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"version needs to contain major, minor, and revision"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesOldElasticsearchVersion
specifier|public
name|void
name|testReadFromPropertiesOldElasticsearchVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
literal|"my_plugin"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|V_1_7_0
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected old elasticsearch version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Was designed for version [1.7.0]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReadFromPropertiesJvmMissingClassname
specifier|public
name|void
name|testReadFromPropertiesJvmMissingClassname
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pluginDir
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|pluginDir
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
argument_list|,
literal|"name"
argument_list|,
literal|"my_plugin"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|,
literal|"java.version"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.specification.version"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected old elasticsearch version exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Property [classname] is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPluginListSorted
specifier|public
name|void
name|testPluginListSorted
parameter_list|()
block|{
name|PluginsAndModules
name|pluginsInfo
init|=
operator|new
name|PluginsAndModules
argument_list|()
decl_stmt|;
name|pluginsInfo
operator|.
name|addPlugin
argument_list|(
operator|new
name|PluginInfo
argument_list|(
literal|"c"
argument_list|,
literal|"foo"
argument_list|,
literal|"dummy"
argument_list|,
literal|"dummyclass"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|pluginsInfo
operator|.
name|addPlugin
argument_list|(
operator|new
name|PluginInfo
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|,
literal|"dummy"
argument_list|,
literal|"dummyclass"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|pluginsInfo
operator|.
name|addPlugin
argument_list|(
operator|new
name|PluginInfo
argument_list|(
literal|"e"
argument_list|,
literal|"foo"
argument_list|,
literal|"dummy"
argument_list|,
literal|"dummyclass"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|pluginsInfo
operator|.
name|addPlugin
argument_list|(
operator|new
name|PluginInfo
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|,
literal|"dummy"
argument_list|,
literal|"dummyclass"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|pluginsInfo
operator|.
name|addPlugin
argument_list|(
operator|new
name|PluginInfo
argument_list|(
literal|"d"
argument_list|,
literal|"foo"
argument_list|,
literal|"dummy"
argument_list|,
literal|"dummyclass"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|infos
init|=
name|pluginsInfo
operator|.
name|getPluginInfos
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|infos
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
parameter_list|(
name|input
parameter_list|)
lambda|->
name|input
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|names
argument_list|,
name|contains
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|"d"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

