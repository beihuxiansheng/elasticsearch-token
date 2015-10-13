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
name|common
operator|.
name|cli
operator|.
name|CliToolTestCase
operator|.
name|CaptureOutputTerminal
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|*
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
name|attribute
operator|.
name|BasicFileAttributes
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
name|attribute
operator|.
name|PosixFilePermissions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|PluginInfoTests
operator|.
name|writeProperties
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
name|*
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
name|*
import|;
end_import

begin_comment
comment|// there are some lucene file systems that seem to cause problems (deleted files, dirs instead of files)
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressFileSystems
argument_list|(
literal|"*"
argument_list|)
DECL|class|PluginManagerPermissionTests
specifier|public
class|class
name|PluginManagerPermissionTests
extends|extends
name|ESTestCase
block|{
DECL|field|pluginName
specifier|private
name|String
name|pluginName
init|=
literal|"my-plugin"
decl_stmt|;
DECL|field|terminal
specifier|private
name|CaptureOutputTerminal
name|terminal
init|=
operator|new
name|CaptureOutputTerminal
argument_list|()
decl_stmt|;
DECL|field|environment
specifier|private
name|Environment
name|environment
decl_stmt|;
DECL|field|supportsPermissions
specifier|private
name|boolean
name|supportsPermissions
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsBuilder
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|tempDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.plugins"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|environment
operator|=
operator|new
name|Environment
argument_list|(
name|settingsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|supportsPermissions
operator|=
name|tempDir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|supportedFileAttributeViews
argument_list|()
operator|.
name|contains
argument_list|(
literal|"posix"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatUnaccessibleBinDirectoryAbortsPluginInstallation
specifier|public
name|void
name|testThatUnaccessibleBinDirectoryAbortsPluginInstallation
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
literal|true
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|binPath
init|=
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|binPath
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|binPath
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException but did not happen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
comment|// exists, because of our weird permissions above
name|assertDirectoryExists
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terminal
operator|.
name|getTerminalOutput
argument_list|()
argument_list|,
name|hasItem
argument_list|(
name|containsString
argument_list|(
literal|"Error copying bin directory "
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|binPath
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatUnaccessiblePluginConfigDirectoryAbortsPluginInstallation
specifier|public
name|void
name|testThatUnaccessiblePluginConfigDirectoryAbortsPluginInstallation
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|binPath
init|=
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|binPath
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException but did not happen, terminal output was "
operator|+
name|terminal
operator|.
name|getTerminalOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
comment|// exists, because of our weird permissions above
name|assertDirectoryExists
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terminal
operator|.
name|getTerminalOutput
argument_list|()
argument_list|,
name|hasItem
argument_list|(
name|containsString
argument_list|(
literal|"Error copying config directory "
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// config/bin are not writable, but the plugin does not need to put anything into it
DECL|method|testThatPluginWithoutBinAndConfigWorksEvenIfPermissionsAreWrong
specifier|public
name|void
name|testThatPluginWithoutBinAndConfigWorksEvenIfPermissionsAreWrong
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|binPath
init|=
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|binPath
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|binPath
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|binPath
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// plugins directory no accessible, should leave no other left over directories
DECL|method|testThatNonWritablePluginsDirectoryLeavesNoLeftOver
specifier|public
name|void
name|testThatNonWritablePluginsDirectoryLeavesNoLeftOver
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException due to read-only plugins/ directory"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDirectoryExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatUnwriteableBackupFilesInConfigurationDirectoryAreReplaced
specifier|public
name|void
name|testThatUnwriteableBackupFilesInConfigurationDirectoryAreReplaced
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|boolean
name|pluginContainsExecutables
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
name|pluginContainsExecutables
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|configFile
init|=
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
name|Path
name|backupConfigFile
init|=
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml.new"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|backupConfigFile
argument_list|)
expr_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|backupConfigFile
argument_list|,
literal|"foo"
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|backupConfigFile
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"---------"
argument_list|)
argument_list|)
expr_stmt|;
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
if|if
condition|(
name|pluginContainsExecutables
condition|)
block|{
name|assertDirectoryExists
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertDirectoryExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertDirectoryExists
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileExists
argument_list|(
name|backupConfigFile
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|backupConfigFile
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rw-rw-rw-"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|backupConfigFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|content
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|backupConfigFile
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rw-rw-rw-"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatConfigDirectoryBeingAFileAbortsInstallationAndDoesNotAccidentallyDeleteThisFile
specifier|public
name|void
name|testThatConfigDirectoryBeingAFileAbortsInstallationAndDoesNotAccidentallyDeleteThisFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected plugin installation to fail, but didnt"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertFileExists
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatBinDirectoryBeingAFileAbortsInstallationAndDoesNotAccidentallyDeleteThisFile
specifier|public
name|void
name|testThatBinDirectoryBeingAFileAbortsInstallationAndDoesNotAccidentallyDeleteThisFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"File system does not support permissions, skipping"
argument_list|,
name|supportsPermissions
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|URL
name|pluginUrl
init|=
name|createPlugin
argument_list|(
literal|true
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|PluginManager
name|pluginManager
init|=
operator|new
name|PluginManager
argument_list|(
name|environment
argument_list|,
name|pluginUrl
argument_list|,
name|PluginManager
operator|.
name|OutputMode
operator|.
name|VERBOSE
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|pluginManager
operator|.
name|downloadAndExtract
argument_list|(
name|pluginName
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected plugin installation to fail, but didnt"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertFileExists
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileNotExists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createPlugin
specifier|private
name|URL
name|createPlugin
parameter_list|(
name|boolean
name|withBinDir
parameter_list|,
name|boolean
name|withConfigDir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|structure
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"fake-plugin"
argument_list|)
decl_stmt|;
name|writeProperties
argument_list|(
name|structure
argument_list|,
literal|"description"
argument_list|,
literal|"fake desc"
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
literal|"jvm"
argument_list|,
literal|"true"
argument_list|,
literal|"java.version"
argument_list|,
literal|"1.7"
argument_list|,
literal|"name"
argument_list|,
name|pluginName
argument_list|,
literal|"classname"
argument_list|,
name|pluginName
argument_list|)
expr_stmt|;
if|if
condition|(
name|withBinDir
condition|)
block|{
comment|// create bin dir
name|Path
name|binDir
init|=
name|structure
operator|.
name|resolve
argument_list|(
literal|"bin"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|binDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|binDir
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
argument_list|)
expr_stmt|;
comment|// create executable
name|Path
name|executable
init|=
name|binDir
operator|.
name|resolve
argument_list|(
literal|"my-binary"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|executable
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|executable
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|withConfigDir
condition|)
block|{
comment|// create bin dir
name|Path
name|configDir
init|=
name|structure
operator|.
name|resolve
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|configDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|configDir
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
argument_list|)
expr_stmt|;
comment|// create config file
name|Path
name|configFile
init|=
name|configDir
operator|.
name|resolve
argument_list|(
literal|"my-custom-config.yaml"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|configFile
argument_list|,
literal|"my custom config content"
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|configFile
argument_list|,
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
literal|"rw-r--r--"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Path
name|zip
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|structure
operator|.
name|getFileName
argument_list|()
operator|+
literal|".zip"
argument_list|)
decl_stmt|;
try|try
init|(
name|ZipOutputStream
name|stream
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|zip
argument_list|)
argument_list|)
init|)
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|structure
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
name|structure
operator|.
name|relativize
argument_list|(
name|file
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|file
argument_list|,
name|stream
argument_list|)
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|zip
operator|.
name|toUri
argument_list|()
operator|.
name|toURL
argument_list|()
return|;
block|}
block|}
end_class

end_unit
