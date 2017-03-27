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
name|NoSuchFileException
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

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
name|cli
operator|.
name|ExitCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|MockTerminal
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
name|junit
operator|.
name|Before
import|;
end_import

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressFileSystems
argument_list|(
literal|"*"
argument_list|)
DECL|class|ListPluginsCommandTests
specifier|public
class|class
name|ListPluginsCommandTests
extends|extends
name|ESTestCase
block|{
DECL|field|home
specifier|private
name|Path
name|home
decl_stmt|;
DECL|field|env
specifier|private
name|Environment
name|env
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|home
operator|=
name|createTempDir
argument_list|()
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|home
operator|.
name|resolve
argument_list|(
literal|"plugins"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"path.home"
argument_list|,
name|home
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|env
operator|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|listPlugins
specifier|static
name|MockTerminal
name|listPlugins
parameter_list|(
name|Path
name|home
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|listPlugins
argument_list|(
name|home
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|listPlugins
specifier|static
name|MockTerminal
name|listPlugins
parameter_list|(
name|Path
name|home
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|argsAndHome
init|=
operator|new
name|String
index|[
name|args
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|args
argument_list|,
literal|0
argument_list|,
name|argsAndHome
argument_list|,
literal|0
argument_list|,
name|args
operator|.
name|length
argument_list|)
expr_stmt|;
name|argsAndHome
index|[
name|args
operator|.
name|length
index|]
operator|=
literal|"-Epath.home="
operator|+
name|home
expr_stmt|;
name|MockTerminal
name|terminal
init|=
operator|new
name|MockTerminal
argument_list|()
decl_stmt|;
name|int
name|status
init|=
operator|new
name|ListPluginsCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|addShutdownHook
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
operator|.
name|main
argument_list|(
name|argsAndHome
argument_list|,
name|terminal
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ExitCodes
operator|.
name|OK
argument_list|,
name|status
argument_list|)
expr_stmt|;
return|return
name|terminal
return|;
block|}
DECL|method|buildMultiline
specifier|private
specifier|static
name|String
name|buildMultiline
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|args
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|,
literal|"\n"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|buildFakePlugin
specifier|private
specifier|static
name|void
name|buildFakePlugin
parameter_list|(
specifier|final
name|Environment
name|env
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|classname
parameter_list|)
throws|throws
name|IOException
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
name|description
argument_list|,
name|name
argument_list|,
name|classname
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|buildFakePlugin
specifier|private
specifier|static
name|void
name|buildFakePlugin
parameter_list|(
specifier|final
name|Environment
name|env
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|classname
parameter_list|,
specifier|final
name|boolean
name|hasNativeController
parameter_list|)
throws|throws
name|IOException
block|{
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"description"
argument_list|,
name|description
argument_list|,
literal|"name"
argument_list|,
name|name
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
name|classname
argument_list|,
literal|"has.native.controller"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|hasNativeController
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginsDirMissing
specifier|public
name|void
name|testPluginsDirMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Files
operator|.
name|delete
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
argument_list|)
expr_stmt|;
name|IOException
name|e
init|=
name|expectThrows
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|listPlugins
argument_list|(
name|home
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Plugins directory missing: "
operator|+
name|env
operator|.
name|pluginsFile
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoPlugins
specifier|public
name|void
name|testNoPlugins
parameter_list|()
throws|throws
name|Exception
block|{
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnePlugin
specifier|public
name|void
name|testOnePlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc"
argument_list|,
literal|"fake"
argument_list|,
literal|"org.fake"
argument_list|)
expr_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|buildMultiline
argument_list|(
literal|"fake"
argument_list|)
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoPlugins
specifier|public
name|void
name|testTwoPlugins
parameter_list|()
throws|throws
name|Exception
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc"
argument_list|,
literal|"fake1"
argument_list|,
literal|"org.fake"
argument_list|)
expr_stmt|;
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 2"
argument_list|,
literal|"fake2"
argument_list|,
literal|"org.fake"
argument_list|)
expr_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|buildMultiline
argument_list|(
literal|"fake1"
argument_list|,
literal|"fake2"
argument_list|)
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginWithVerbose
specifier|public
name|void
name|testPluginWithVerbose
parameter_list|()
throws|throws
name|Exception
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc"
argument_list|,
literal|"fake_plugin"
argument_list|,
literal|"org.fake"
argument_list|)
expr_stmt|;
name|String
index|[]
name|params
init|=
block|{
literal|"-v"
block|}
decl_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|buildMultiline
argument_list|(
literal|"Plugins directory: "
operator|+
name|env
operator|.
name|pluginsFile
argument_list|()
argument_list|,
literal|"fake_plugin"
argument_list|,
literal|"- Plugin information:"
argument_list|,
literal|"Name: fake_plugin"
argument_list|,
literal|"Description: fake desc"
argument_list|,
literal|"Version: 1.0"
argument_list|,
literal|"Native Controller: false"
argument_list|,
literal|" * Classname: org.fake"
argument_list|)
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginWithNativeController
specifier|public
name|void
name|testPluginWithNativeController
parameter_list|()
throws|throws
name|Exception
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 1"
argument_list|,
literal|"fake_plugin1"
argument_list|,
literal|"org.fake"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
index|[]
name|params
init|=
block|{
literal|"-v"
block|}
decl_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|buildMultiline
argument_list|(
literal|"Plugins directory: "
operator|+
name|env
operator|.
name|pluginsFile
argument_list|()
argument_list|,
literal|"fake_plugin1"
argument_list|,
literal|"- Plugin information:"
argument_list|,
literal|"Name: fake_plugin1"
argument_list|,
literal|"Description: fake desc 1"
argument_list|,
literal|"Version: 1.0"
argument_list|,
literal|"Native Controller: true"
argument_list|,
literal|" * Classname: org.fake"
argument_list|)
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginWithVerboseMultiplePlugins
specifier|public
name|void
name|testPluginWithVerboseMultiplePlugins
parameter_list|()
throws|throws
name|Exception
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 1"
argument_list|,
literal|"fake_plugin1"
argument_list|,
literal|"org.fake"
argument_list|)
expr_stmt|;
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 2"
argument_list|,
literal|"fake_plugin2"
argument_list|,
literal|"org.fake2"
argument_list|)
expr_stmt|;
name|String
index|[]
name|params
init|=
block|{
literal|"-v"
block|}
decl_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|buildMultiline
argument_list|(
literal|"Plugins directory: "
operator|+
name|env
operator|.
name|pluginsFile
argument_list|()
argument_list|,
literal|"fake_plugin1"
argument_list|,
literal|"- Plugin information:"
argument_list|,
literal|"Name: fake_plugin1"
argument_list|,
literal|"Description: fake desc 1"
argument_list|,
literal|"Version: 1.0"
argument_list|,
literal|"Native Controller: false"
argument_list|,
literal|" * Classname: org.fake"
argument_list|,
literal|"fake_plugin2"
argument_list|,
literal|"- Plugin information:"
argument_list|,
literal|"Name: fake_plugin2"
argument_list|,
literal|"Description: fake desc 2"
argument_list|,
literal|"Version: 1.0"
argument_list|,
literal|"Native Controller: false"
argument_list|,
literal|" * Classname: org.fake2"
argument_list|)
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginWithoutVerboseMultiplePlugins
specifier|public
name|void
name|testPluginWithoutVerboseMultiplePlugins
parameter_list|()
throws|throws
name|Exception
block|{
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 1"
argument_list|,
literal|"fake_plugin1"
argument_list|,
literal|"org.fake"
argument_list|)
expr_stmt|;
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 2"
argument_list|,
literal|"fake_plugin2"
argument_list|,
literal|"org.fake2"
argument_list|)
expr_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|String
name|output
init|=
name|terminal
operator|.
name|getOutput
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|buildMultiline
argument_list|(
literal|"fake_plugin1"
argument_list|,
literal|"fake_plugin2"
argument_list|)
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginWithoutDescriptorFile
specifier|public
name|void
name|testPluginWithoutDescriptorFile
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|pluginDir
init|=
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake1"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|pluginDir
argument_list|)
expr_stmt|;
name|NoSuchFileException
name|e
init|=
name|expectThrows
argument_list|(
name|NoSuchFileException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|listPlugins
argument_list|(
name|home
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pluginDir
operator|.
name|resolve
argument_list|(
name|PluginInfo
operator|.
name|ES_PLUGIN_PROPERTIES
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPluginWithWrongDescriptorFile
specifier|public
name|void
name|testPluginWithWrongDescriptorFile
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|pluginDir
init|=
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake1"
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
name|listPlugins
argument_list|(
name|home
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|descriptorPath
init|=
name|pluginDir
operator|.
name|resolve
argument_list|(
name|PluginInfo
operator|.
name|ES_PLUGIN_PROPERTIES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"property [name] is missing in ["
operator|+
name|descriptorPath
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExistingIncompatiblePlugin
specifier|public
name|void
name|testExistingIncompatiblePlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|PluginTestUtil
operator|.
name|writeProperties
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake_plugin1"
argument_list|)
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc 1"
argument_list|,
literal|"name"
argument_list|,
literal|"fake_plugin1"
argument_list|,
literal|"version"
argument_list|,
literal|"1.0"
argument_list|,
literal|"elasticsearch.version"
argument_list|,
name|Version
operator|.
name|fromString
argument_list|(
literal|"1.0.0"
argument_list|)
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
literal|"org.fake1"
argument_list|)
expr_stmt|;
name|buildFakePlugin
argument_list|(
name|env
argument_list|,
literal|"fake desc 2"
argument_list|,
literal|"fake_plugin2"
argument_list|,
literal|"org.fake2"
argument_list|)
expr_stmt|;
name|MockTerminal
name|terminal
init|=
name|listPlugins
argument_list|(
name|home
argument_list|)
decl_stmt|;
specifier|final
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"plugin [%s] is incompatible with version [%s]; was designed for version [%s]"
argument_list|,
literal|"fake_plugin1"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fake_plugin1\n"
operator|+
literal|"WARNING: "
operator|+
name|message
operator|+
literal|"\n"
operator|+
literal|"fake_plugin2\n"
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|params
init|=
block|{
literal|"-s"
block|}
decl_stmt|;
name|terminal
operator|=
name|listPlugins
argument_list|(
name|home
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fake_plugin1\nfake_plugin2\n"
argument_list|,
name|terminal
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

