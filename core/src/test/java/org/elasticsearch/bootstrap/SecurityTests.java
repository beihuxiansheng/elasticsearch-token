begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
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
name|Constants
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
name|io
operator|.
name|PathUtils
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
name|io
operator|.
name|FilePermission
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
name|security
operator|.
name|PermissionCollection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permissions
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

begin_class
DECL|class|SecurityTests
specifier|public
class|class
name|SecurityTests
extends|extends
name|ESTestCase
block|{
comment|/** test generated permissions */
DECL|method|testGeneratedPermissions
specifier|public
name|void
name|testGeneratedPermissions
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// make a fake ES home and ensure we only grant permissions to that.
name|Path
name|esHome
init|=
name|path
operator|.
name|resolve
argument_list|(
literal|"esHome"
argument_list|)
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsBuilder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|esHome
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Path
name|fakeTmpDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|String
name|realTmpDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
name|Permissions
name|permissions
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|,
name|fakeTmpDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|permissions
operator|=
name|Security
operator|.
name|createPermissions
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|,
name|realTmpDir
argument_list|)
expr_stmt|;
block|}
comment|// the fake es home
name|assertNoPermissions
argument_list|(
name|esHome
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// its parent
name|assertNoPermissions
argument_list|(
name|esHome
operator|.
name|getParent
argument_list|()
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// some other sibling
name|assertNoPermissions
argument_list|(
name|esHome
operator|.
name|getParent
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"other"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// double check we overwrote java.io.tmpdir correctly for the test
name|assertNoPermissions
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
name|realTmpDir
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
comment|/** test generated permissions for all configured paths */
DECL|method|testEnvironmentPaths
specifier|public
name|void
name|testEnvironmentPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// make a fake ES home and ensure we only grant permissions to that.
name|Path
name|esHome
init|=
name|path
operator|.
name|resolve
argument_list|(
literal|"esHome"
argument_list|)
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsBuilder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"home"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.scripts"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"scripts"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.plugins"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"plugins"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|putArray
argument_list|(
literal|"path.data"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"data1"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"data2"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.shared_data"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"custom"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"path.logs"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"logs"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
literal|"pidfile"
argument_list|,
name|esHome
operator|.
name|resolve
argument_list|(
literal|"test.pid"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Path
name|fakeTmpDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|String
name|realTmpDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
name|Permissions
name|permissions
decl_stmt|;
name|Environment
name|environment
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|,
name|fakeTmpDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|environment
operator|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|permissions
operator|=
name|Security
operator|.
name|createPermissions
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|,
name|realTmpDir
argument_list|)
expr_stmt|;
block|}
comment|// the fake es home
name|assertNoPermissions
argument_list|(
name|esHome
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// its parent
name|assertNoPermissions
argument_list|(
name|esHome
operator|.
name|getParent
argument_list|()
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// some other sibling
name|assertNoPermissions
argument_list|(
name|esHome
operator|.
name|getParent
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"other"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// double check we overwrote java.io.tmpdir correctly for the test
name|assertNoPermissions
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
name|realTmpDir
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// check that all directories got permissions:
comment|// bin file: ro
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|binFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// lib file: ro
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|libFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// config file: ro
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// scripts file: ro
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|scriptsFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// plugins: ro
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// data paths: r/w
for|for
control|(
name|Path
name|dataPath
range|:
name|environment
operator|.
name|dataFiles
argument_list|()
control|)
block|{
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|dataPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Path
name|dataPath
range|:
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
control|)
block|{
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|dataPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|sharedDataFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// logs: r/w
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|logsFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// temp dir: r/w
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|fakeTmpDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
comment|// PID file: delete only (for the shutdown hook)
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|environment
operator|.
name|pidFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"delete"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnsureExists
specifier|public
name|void
name|testEnsureExists
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// directory exists
name|Path
name|exists
init|=
name|p
operator|.
name|resolve
argument_list|(
literal|"exists"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|Security
operator|.
name|ensureDirectoryExists
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createTempFile
argument_list|(
name|exists
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnsureNotExists
specifier|public
name|void
name|testEnsureNotExists
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// directory does not exist: create it
name|Path
name|notExists
init|=
name|p
operator|.
name|resolve
argument_list|(
literal|"notexists"
argument_list|)
decl_stmt|;
name|Security
operator|.
name|ensureDirectoryExists
argument_list|(
name|notExists
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createTempFile
argument_list|(
name|notExists
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnsureRegularFile
specifier|public
name|void
name|testEnsureRegularFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// regular file
name|Path
name|regularFile
init|=
name|p
operator|.
name|resolve
argument_list|(
literal|"regular"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|regularFile
argument_list|)
expr_stmt|;
try|try
block|{
name|Security
operator|.
name|ensureDirectoryExists
argument_list|(
name|regularFile
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testEnsureSymlink
specifier|public
name|void
name|testEnsureSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|exists
init|=
name|p
operator|.
name|resolve
argument_list|(
literal|"exists"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|exists
argument_list|)
expr_stmt|;
comment|// symlink
name|Path
name|linkExists
init|=
name|p
operator|.
name|resolve
argument_list|(
literal|"linkExists"
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|createSymbolicLink
argument_list|(
name|linkExists
argument_list|,
name|exists
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"test requires filesystem that supports symbolic links"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"test cannot create symbolic links with security manager enabled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Security
operator|.
name|ensureDirectoryExists
argument_list|(
name|linkExists
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createTempFile
argument_list|(
name|linkExists
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnsureBrokenSymlink
specifier|public
name|void
name|testEnsureBrokenSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
name|createTempDir
argument_list|()
decl_stmt|;
comment|// broken symlink
name|Path
name|brokenLink
init|=
name|p
operator|.
name|resolve
argument_list|(
literal|"brokenLink"
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|createSymbolicLink
argument_list|(
name|brokenLink
argument_list|,
name|p
operator|.
name|resolve
argument_list|(
literal|"nonexistent"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"test requires filesystem that supports symbolic links"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"test cannot create symbolic links with security manager enabled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Security
operator|.
name|ensureDirectoryExists
argument_list|(
name|brokenLink
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{}
block|}
comment|/** We only grant this to special jars */
DECL|method|testUnsafeAccess
specifier|public
name|void
name|testUnsafeAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"test requires security manager"
argument_list|,
name|System
operator|.
name|getSecurityManager
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
comment|// class could be legitimately loaded, so we might not fail until setAccessible
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|expected
parameter_list|)
block|{
comment|// ok
block|}
catch|catch
parameter_list|(
name|Exception
name|somethingElse
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"perhaps JVM doesn't have Unsafe?"
argument_list|,
name|somethingElse
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** can't execute processes */
DECL|method|testProcessExecution
specifier|public
name|void
name|testProcessExecution
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"test requires security manager"
argument_list|,
name|System
operator|.
name|getSecurityManager
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
literal|"ls"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|expected
parameter_list|)
block|{}
block|}
comment|/** When a configured dir is a symlink, test that permissions work on link target */
DECL|method|testSymlinkPermissions
specifier|public
name|void
name|testSymlinkPermissions
parameter_list|()
throws|throws
name|IOException
block|{
comment|// see https://github.com/elastic/elasticsearch/issues/12170
name|assumeFalse
argument_list|(
literal|"windows does not automatically grant permission to the target of symlinks"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|target
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|target
argument_list|)
expr_stmt|;
comment|// symlink
name|Path
name|link
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"link"
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|createSymbolicLink
argument_list|(
name|link
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"test requires filesystem that supports symbolic links"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"test cannot create symbolic links with security manager enabled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Permissions
name|permissions
init|=
operator|new
name|Permissions
argument_list|()
decl_stmt|;
name|Security
operator|.
name|addPath
argument_list|(
name|permissions
argument_list|,
literal|"testing"
argument_list|,
name|link
argument_list|,
literal|"read"
argument_list|)
expr_stmt|;
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|link
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|link
operator|.
name|resolve
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
name|assertExactPermissions
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
operator|.
name|resolve
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"read"
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
comment|/**       * checks exact file permissions, meaning those and only those for that path.      */
DECL|method|assertExactPermissions
specifier|static
name|void
name|assertExactPermissions
parameter_list|(
name|FilePermission
name|expected
parameter_list|,
name|PermissionCollection
name|actual
parameter_list|)
block|{
name|String
name|target
init|=
name|expected
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// see javadocs
name|Set
argument_list|<
name|String
argument_list|>
name|permissionSet
init|=
name|asSet
argument_list|(
name|expected
operator|.
name|getActions
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|read
init|=
name|permissionSet
operator|.
name|remove
argument_list|(
literal|"read"
argument_list|)
decl_stmt|;
name|boolean
name|readlink
init|=
name|permissionSet
operator|.
name|remove
argument_list|(
literal|"readlink"
argument_list|)
decl_stmt|;
name|boolean
name|write
init|=
name|permissionSet
operator|.
name|remove
argument_list|(
literal|"write"
argument_list|)
decl_stmt|;
name|boolean
name|delete
init|=
name|permissionSet
operator|.
name|remove
argument_list|(
literal|"delete"
argument_list|)
decl_stmt|;
name|boolean
name|execute
init|=
name|permissionSet
operator|.
name|remove
argument_list|(
literal|"execute"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"unrecognized permission: "
operator|+
name|permissionSet
argument_list|,
name|permissionSet
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|read
argument_list|,
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"read"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readlink
argument_list|,
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"readlink"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|write
argument_list|,
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"write"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|delete
argument_list|,
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"delete"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|execute
argument_list|,
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"execute"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * checks that this path has no permissions      */
DECL|method|assertNoPermissions
specifier|static
name|void
name|assertNoPermissions
parameter_list|(
name|Path
name|path
parameter_list|,
name|PermissionCollection
name|actual
parameter_list|)
block|{
name|String
name|target
init|=
name|path
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"read"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"readlink"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"write"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"delete"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|actual
operator|.
name|implies
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|target
argument_list|,
literal|"execute"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
