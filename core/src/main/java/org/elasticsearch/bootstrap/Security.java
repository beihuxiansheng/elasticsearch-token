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
name|elasticsearch
operator|.
name|common
operator|.
name|SuppressForbidden
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
name|plugins
operator|.
name|PluginInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|file
operator|.
name|AccessMode
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
name|FileAlreadyExistsException
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
name|NotDirectoryException
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
name|NoSuchAlgorithmException
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
name|security
operator|.
name|Policy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|URIParameter
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

begin_comment
comment|/**   * Initializes SecurityManager with necessary permissions.  *<br>  *<h1>Initialization</h1>  * The JVM is not initially started with security manager enabled,  * instead we turn it on early in the startup process. This is a tradeoff  * between security and ease of use:  *<ul>  *<li>Assigns file permissions to user-configurable paths that can  *       be specified from the command-line or {@code elasticsearch.yml}.</li>  *<li>Allows for some contained usage of native code that would not  *       otherwise be permitted.</li>  *</ul>  *<br>  *<h1>Permissions</h1>  * Permissions use a policy file packaged as a resource, this file is  * also used in tests. File permissions are generated dynamically and  * combined with this policy file.  *<p>  * For each configured path, we ensure it exists and is accessible before  * granting permissions, otherwise directory creation would require  * permissions to parent directories.  *<p>  * In some exceptional cases, permissions are assigned to specific jars only,  * when they are so dangerous that general code should not be granted the  * permission, but there are extenuating circumstances.  *<p>  * Scripts (groovy, javascript, python) are assigned minimal permissions. This does not provide adequate  * sandboxing, as these scripts still have access to ES classes, and could  * modify members, etc that would cause bad things to happen later on their  * behalf (no package protections are yet in place, this would need some  * cleanups to the scripting apis). But still it can provide some defense for users  * that enable dynamic scripting without being fully aware of the consequences.  *<br>  *<h1>Disabling Security</h1>  * SecurityManager can be disabled completely with this setting:  *<pre>  * es.security.manager.enabled = false  *</pre>  *<br>  *<h1>Debugging Security</h1>  * A good place to start when there is a problem is to turn on security debugging:  *<pre>  * JAVA_OPTS="-Djava.security.debug=access,failure" bin/elasticsearch  *</pre>  * See<a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/troubleshooting-security.html">  * Troubleshooting Security</a> for information.  */
end_comment

begin_class
DECL|class|Security
specifier|final
class|class
name|Security
block|{
comment|/** no instantiation */
DECL|method|Security
specifier|private
name|Security
parameter_list|()
block|{}
comment|/**       * Initializes SecurityManager for the environment      * Can only happen once!      */
DECL|method|configure
specifier|static
name|void
name|configure
parameter_list|(
name|Environment
name|environment
parameter_list|)
throws|throws
name|Exception
block|{
comment|// enable security policy: union of template and environment-based paths, and possibly plugin permissions
name|Policy
operator|.
name|setPolicy
argument_list|(
operator|new
name|ESPolicy
argument_list|(
name|createPermissions
argument_list|(
name|environment
argument_list|)
argument_list|,
name|getPluginPermissions
argument_list|(
name|environment
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// enable security manager
name|System
operator|.
name|setSecurityManager
argument_list|(
operator|new
name|SecurityManager
argument_list|()
block|{
comment|// we disable this completely, because its granted otherwise:
comment|// 'Note: The "exitVM.*" permission is automatically granted to
comment|// all code loaded from the application class path, thus enabling
comment|// applications to terminate themselves.'
annotation|@
name|Override
specifier|public
name|void
name|checkExit
parameter_list|(
name|int
name|status
parameter_list|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"exit("
operator|+
name|status
operator|+
literal|") not allowed by system policy"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// do some basic tests
name|selfTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets properties (codebase URLs) for policy files.      * we look for matching plugins and set URLs to fit      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"proper use of URL"
argument_list|)
DECL|method|getPluginPermissions
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Policy
argument_list|>
name|getPluginPermissions
parameter_list|(
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Policy
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|)
condition|)
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
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|plugin
range|:
name|stream
control|)
block|{
name|Path
name|policyFile
init|=
name|plugin
operator|.
name|resolve
argument_list|(
name|PluginInfo
operator|.
name|ES_PLUGIN_POLICY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|policyFile
argument_list|)
condition|)
block|{
comment|// first get a list of URLs for the plugins' jars:
name|List
argument_list|<
name|URL
argument_list|>
name|codebases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|jarStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|plugin
argument_list|,
literal|"*.jar"
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|jar
range|:
name|jarStream
control|)
block|{
name|codebases
operator|.
name|add
argument_list|(
name|jar
operator|.
name|toUri
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// parse the plugin's policy file into a set of permissions
name|Policy
name|policy
init|=
name|readPolicy
argument_list|(
name|policyFile
operator|.
name|toUri
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|,
name|codebases
operator|.
name|toArray
argument_list|(
operator|new
name|URL
index|[
name|codebases
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
comment|// consult this policy for each of the plugin's jars:
for|for
control|(
name|URL
name|url
range|:
name|codebases
control|)
block|{
if|if
condition|(
name|map
operator|.
name|put
argument_list|(
name|url
operator|.
name|getFile
argument_list|()
argument_list|,
name|policy
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// just be paranoid ok?
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"per-plugin permissions already granted for jar file: "
operator|+
name|url
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
return|;
block|}
comment|/**      * Reads and returns the specified {@code policyFile}.      *<p>      * Resources (e.g. jar files and directories) listed in {@code codebases} location      * will be provided to the policy file via a system property of the short name:      * e.g.<code>${codebase.joda-convert-1.2.jar}</code> would map to full URL.      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"accesses fully qualified URLs to configure security"
argument_list|)
DECL|method|readPolicy
specifier|static
name|Policy
name|readPolicy
parameter_list|(
name|URL
name|policyFile
parameter_list|,
name|URL
name|codebases
index|[]
parameter_list|)
block|{
try|try
block|{
try|try
block|{
comment|// set codebase properties
for|for
control|(
name|URL
name|url
range|:
name|codebases
control|)
block|{
name|String
name|shortName
init|=
name|PathUtils
operator|.
name|get
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"codebase."
operator|+
name|shortName
argument_list|,
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Policy
operator|.
name|getInstance
argument_list|(
literal|"JavaPolicy"
argument_list|,
operator|new
name|URIParameter
argument_list|(
name|policyFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
comment|// clear codebase properties
for|for
control|(
name|URL
name|url
range|:
name|codebases
control|)
block|{
name|String
name|shortName
init|=
name|PathUtils
operator|.
name|get
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"codebase."
operator|+
name|shortName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to parse policy file `"
operator|+
name|policyFile
operator|+
literal|"`"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** returns dynamic Permissions to configured paths */
DECL|method|createPermissions
specifier|static
name|Permissions
name|createPermissions
parameter_list|(
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|Permissions
name|policy
init|=
operator|new
name|Permissions
argument_list|()
decl_stmt|;
comment|// read-only dirs
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.home"
argument_list|,
name|environment
operator|.
name|binFile
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.home"
argument_list|,
name|environment
operator|.
name|libFile
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.plugins"
argument_list|,
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.conf"
argument_list|,
name|environment
operator|.
name|configFile
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.scripts"
argument_list|,
name|environment
operator|.
name|scriptsFile
argument_list|()
argument_list|,
literal|"read,readlink"
argument_list|)
expr_stmt|;
comment|// read-write dirs
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"java.io.tmpdir"
argument_list|,
name|environment
operator|.
name|tmpFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.logs"
argument_list|,
name|environment
operator|.
name|logsFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
if|if
condition|(
name|environment
operator|.
name|sharedDataFile
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.shared_data"
argument_list|,
name|environment
operator|.
name|sharedDataFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Path
name|path
range|:
name|environment
operator|.
name|dataFiles
argument_list|()
control|)
block|{
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.data"
argument_list|,
name|path
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Path
name|path
range|:
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
control|)
block|{
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.data"
argument_list|,
name|path
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Path
name|path
range|:
name|environment
operator|.
name|repoFiles
argument_list|()
control|)
block|{
name|addPath
argument_list|(
name|policy
argument_list|,
literal|"path.repo"
argument_list|,
name|path
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|environment
operator|.
name|pidFile
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// we just need permission to remove the file if its elsewhere.
name|policy
operator|.
name|add
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
argument_list|)
expr_stmt|;
block|}
return|return
name|policy
return|;
block|}
comment|/**      * Add access to path (and all files underneath it)      * @param policy current policy to add permissions to      * @param configurationName the configuration name associated with the path (for error messages only)      * @param path the path itself      * @param permissions set of filepermissions to grant to the path      */
DECL|method|addPath
specifier|static
name|void
name|addPath
parameter_list|(
name|Permissions
name|policy
parameter_list|,
name|String
name|configurationName
parameter_list|,
name|Path
name|path
parameter_list|,
name|String
name|permissions
parameter_list|)
block|{
comment|// paths may not exist yet, this also checks accessibility
try|try
block|{
name|ensureDirectoryExists
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to access '"
operator|+
name|configurationName
operator|+
literal|"' ("
operator|+
name|path
operator|+
literal|")"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// add each path twice: once for itself, again for files underneath it
name|policy
operator|.
name|add
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|permissions
argument_list|)
argument_list|)
expr_stmt|;
name|policy
operator|.
name|add
argument_list|(
operator|new
name|FilePermission
argument_list|(
name|path
operator|.
name|toString
argument_list|()
operator|+
name|path
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getSeparator
argument_list|()
operator|+
literal|"-"
argument_list|,
name|permissions
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ensures configured directory {@code path} exists.      * @throws IOException if {@code path} exists, but is not a directory, not accessible, or broken symbolic link.      */
DECL|method|ensureDirectoryExists
specifier|static
name|void
name|ensureDirectoryExists
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// this isn't atomic, but neither is createDirectories.
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// verify access, following links (throws exception if something is wrong)
comment|// we only check READ as a sanity test
name|path
operator|.
name|getFileSystem
argument_list|()
operator|.
name|provider
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|path
operator|.
name|toRealPath
argument_list|()
argument_list|,
name|AccessMode
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// doesn't exist, or not a directory
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// convert optional specific exception so the context is clear
name|IOException
name|e2
init|=
operator|new
name|NotDirectoryException
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|e2
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
block|}
block|}
comment|/** Simple checks that everything is ok */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"accesses jvm default tempdir as a self-test"
argument_list|)
DECL|method|selfTest
specifier|static
name|void
name|selfTest
parameter_list|()
throws|throws
name|IOException
block|{
comment|// check we can manipulate temporary files
try|try
block|{
name|Path
name|p
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{
comment|// potentially virus scanner
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|problem
parameter_list|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Security misconfiguration: cannot access java.io.tmpdir"
argument_list|,
name|problem
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

