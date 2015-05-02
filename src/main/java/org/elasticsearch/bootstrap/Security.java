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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|StringHelper
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|PermissionCollection
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

begin_comment
comment|/**   * Initializes securitymanager with necessary permissions.  *<p>  * We use a template file (the one we test with), and add additional   * permissions based on the environment (data paths, etc)  */
end_comment

begin_class
DECL|class|Security
class|class
name|Security
block|{
comment|/** template policy file, the one used in tests */
DECL|field|POLICY_RESOURCE
specifier|static
specifier|final
name|String
name|POLICY_RESOURCE
init|=
literal|"security.policy"
decl_stmt|;
comment|/**       * Initializes securitymanager for the environment      * Can only happen once!      */
DECL|method|configure
specifier|static
name|void
name|configure
parameter_list|(
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|ESLogger
name|log
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Security
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//String prop = System.getProperty("java.io.tmpdir");
comment|//log.trace("java.io.tmpdir {}", prop);
comment|// init lucene random seed. it will use /dev/urandom where available.
name|StringHelper
operator|.
name|randomId
argument_list|()
expr_stmt|;
name|InputStream
name|config
init|=
name|Security
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|POLICY_RESOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchFileException
argument_list|(
name|POLICY_RESOURCE
argument_list|)
throw|;
block|}
name|Path
name|newConfig
init|=
name|processTemplate
argument_list|(
name|config
argument_list|,
name|environment
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.policy"
argument_list|,
name|newConfig
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// retrieve the parsed policy we created: its useful if something goes wrong
name|Policy
name|policy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|policy
operator|=
name|Policy
operator|.
name|getInstance
argument_list|(
literal|"JavaPolicy"
argument_list|,
operator|new
name|URIParameter
argument_list|(
name|newConfig
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|impossible
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|impossible
argument_list|)
throw|;
block|}
name|PermissionCollection
name|permissions
init|=
name|policy
operator|.
name|getPermissions
argument_list|(
name|Security
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"generated permissions: {}"
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
name|System
operator|.
name|setSecurityManager
argument_list|(
operator|new
name|SecurityManager
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// don't hide securityexception here, it means java.io.tmpdir is not accessible!
name|Files
operator|.
name|delete
argument_list|(
name|newConfig
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|broken
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"unable to properly access temporary files, permissions: {}"
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
throw|throw
name|broken
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// e.g. virus scanner on windows
block|}
block|}
comment|// package-private for testing
DECL|method|processTemplate
specifier|static
name|Path
name|processTemplate
parameter_list|(
name|InputStream
name|template
parameter_list|,
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|processed
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
init|(
name|OutputStream
name|output
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|processed
argument_list|)
argument_list|)
init|)
block|{
comment|// copy the template as-is.
try|try
init|(
name|InputStream
name|in
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|template
argument_list|)
init|)
block|{
name|ByteStreams
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|//  all policy files are UTF-8:
comment|//  https://docs.oracle.com/javase/7/docs/technotes/guides/security/PolicyFiles.html
try|try
init|(
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|output
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"grant {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
comment|// add permissions for all configured paths.
comment|// TODO: improve test infra so we can reduce permissions where read/write
comment|// is not really needed...
name|addPath
argument_list|(
name|writer
argument_list|,
name|environment
operator|.
name|homeFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|writer
argument_list|,
name|environment
operator|.
name|configFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|writer
argument_list|,
name|environment
operator|.
name|logsFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
name|addPath
argument_list|(
name|writer
argument_list|,
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
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
name|writer
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
name|writer
argument_list|,
name|path
argument_list|,
literal|"read,readlink,write,delete"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"};"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|processed
return|;
block|}
DECL|method|addPath
specifier|static
name|void
name|addPath
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|Path
name|path
parameter_list|,
name|String
name|permissions
parameter_list|)
throws|throws
name|IOException
block|{
comment|// paths may not exist yet
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// add each path twice: once for itself, again for files underneath it
name|writer
operator|.
name|write
argument_list|(
literal|"permission java.io.FilePermission \""
operator|+
name|encode
argument_list|(
name|path
argument_list|)
operator|+
literal|"\", \""
operator|+
name|permissions
operator|+
literal|"\";"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"permission java.io.FilePermission \""
operator|+
name|encode
argument_list|(
name|path
argument_list|)
operator|+
literal|"${/}-\", \""
operator|+
name|permissions
operator|+
literal|"\";"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Any backslashes in paths must be escaped, because it is the escape character when parsing.
comment|// See "Note Regarding File Path Specifications on Windows Systems".
comment|// https://docs.oracle.com/javase/7/docs/technotes/guides/security/PolicyFiles.html
DECL|method|encode
specifier|static
name|String
name|encode
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"\\\\"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

