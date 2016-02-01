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
name|DirectoryStream
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
name|common
operator|.
name|cli
operator|.
name|CliTool
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
name|cli
operator|.
name|CliToolTestCase
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
name|cli
operator|.
name|Terminal
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

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressFileSystems
argument_list|(
literal|"*"
argument_list|)
DECL|class|RemovePluginCommandTests
specifier|public
class|class
name|RemovePluginCommandTests
extends|extends
name|ESTestCase
block|{
comment|/** Creates a test environment with bin, config and plugins directories. */
DECL|method|createEnv
specifier|static
name|Environment
name|createEnv
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|home
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|home
operator|.
name|resolve
argument_list|(
literal|"bin"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|home
operator|.
name|resolve
argument_list|(
literal|"bin"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"elasticsearch"
argument_list|)
argument_list|)
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
return|return
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|method|removePlugin
specifier|static
name|CliToolTestCase
operator|.
name|CaptureOutputTerminal
name|removePlugin
parameter_list|(
name|String
name|name
parameter_list|,
name|Environment
name|env
parameter_list|)
throws|throws
name|Exception
block|{
name|CliToolTestCase
operator|.
name|CaptureOutputTerminal
name|terminal
init|=
operator|new
name|CliToolTestCase
operator|.
name|CaptureOutputTerminal
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|VERBOSE
argument_list|)
decl_stmt|;
name|CliTool
operator|.
name|ExitStatus
name|status
init|=
operator|new
name|RemovePluginCommand
argument_list|(
name|terminal
argument_list|,
name|name
argument_list|)
operator|.
name|execute
argument_list|(
name|env
operator|.
name|settings
argument_list|()
argument_list|,
name|env
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CliTool
operator|.
name|ExitStatus
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
DECL|method|assertRemoveCleaned
specifier|static
name|void
name|assertRemoveCleaned
parameter_list|(
name|Environment
name|env
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|file
range|:
name|stream
control|)
block|{
if|if
condition|(
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|".removing"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Removal dir still exists, "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testMissing
specifier|public
name|void
name|testMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Environment
name|env
init|=
name|createEnv
argument_list|()
decl_stmt|;
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
block|{
name|removePlugin
argument_list|(
literal|"dne"
argument_list|,
name|env
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
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
literal|"Plugin dne not found"
argument_list|)
argument_list|)
expr_stmt|;
name|assertRemoveCleaned
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Environment
name|env
init|=
name|createEnv
argument_list|()
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"plugin.jar"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"subdir"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"other"
argument_list|)
argument_list|)
expr_stmt|;
name|removePlugin
argument_list|(
literal|"fake"
argument_list|,
name|env
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"other"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertRemoveCleaned
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
DECL|method|testBin
specifier|public
name|void
name|testBin
parameter_list|()
throws|throws
name|Exception
block|{
name|Environment
name|env
init|=
name|createEnv
argument_list|()
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|binDir
init|=
name|env
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|binDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|binDir
operator|.
name|resolve
argument_list|(
literal|"somescript"
argument_list|)
argument_list|)
expr_stmt|;
name|removePlugin
argument_list|(
literal|"fake"
argument_list|,
name|env
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|env
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"elasticsearch"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|binDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertRemoveCleaned
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
DECL|method|testBinNotDir
specifier|public
name|void
name|testBinNotDir
parameter_list|()
throws|throws
name|Exception
block|{
name|Environment
name|env
init|=
name|createEnv
argument_list|()
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"elasticsearch"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalStateException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|removePlugin
argument_list|(
literal|"elasticsearch"
argument_list|,
name|env
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"elasticsearch"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// did not remove
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|env
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"elasticsearch"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertRemoveCleaned
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

