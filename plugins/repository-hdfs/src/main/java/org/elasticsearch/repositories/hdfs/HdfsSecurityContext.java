begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.hdfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|hdfs
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
name|io
operator|.
name|UncheckedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|ReflectPermission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketPermission
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
name|Permission
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|AuthPermission
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|PrivateCredentialPermission
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|ServicePermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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

begin_comment
comment|/**  * Oversees all the security specific logic for the HDFS Repository plugin.  *  * Keeps track of the current user for a given repository, as well as which  * permissions to grant the blob store restricted execution methods.  */
end_comment

begin_class
DECL|class|HdfsSecurityContext
class|class
name|HdfsSecurityContext
block|{
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|HdfsSecurityContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SIMPLE_AUTH_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|Permission
index|[]
name|SIMPLE_AUTH_PERMISSIONS
decl_stmt|;
DECL|field|KERBEROS_AUTH_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|Permission
index|[]
name|KERBEROS_AUTH_PERMISSIONS
decl_stmt|;
static|static
block|{
comment|// We can do FS ops with only a few elevated permissions:
name|SIMPLE_AUTH_PERMISSIONS
operator|=
operator|new
name|Permission
index|[]
block|{
operator|new
name|SocketPermission
argument_list|(
literal|"*"
argument_list|,
literal|"connect"
argument_list|)
block|,
comment|// 1) hadoop dynamic proxy is messy with access rules
operator|new
name|ReflectPermission
argument_list|(
literal|"suppressAccessChecks"
argument_list|)
block|,
comment|// 2) allow hadoop to add credentials to our Subject
operator|new
name|AuthPermission
argument_list|(
literal|"modifyPrivateCredentials"
argument_list|)
block|}
expr_stmt|;
comment|// If Security is enabled, we need all the following elevated permissions:
name|KERBEROS_AUTH_PERMISSIONS
operator|=
operator|new
name|Permission
index|[]
block|{
operator|new
name|SocketPermission
argument_list|(
literal|"*"
argument_list|,
literal|"connect"
argument_list|)
block|,
comment|// 1) hadoop dynamic proxy is messy with access rules
operator|new
name|ReflectPermission
argument_list|(
literal|"suppressAccessChecks"
argument_list|)
block|,
comment|// 2) allow hadoop to add credentials to our Subject
operator|new
name|AuthPermission
argument_list|(
literal|"modifyPrivateCredentials"
argument_list|)
block|,
comment|// 3) allow hadoop to act as the logged in Subject
operator|new
name|AuthPermission
argument_list|(
literal|"doAs"
argument_list|)
block|,
comment|// 4) Listen and resolve permissions for kerberos server principals
operator|new
name|SocketPermission
argument_list|(
literal|"localhost:0"
argument_list|,
literal|"listen,resolve"
argument_list|)
block|,
comment|// We add the following since hadoop requires the client to re-login when the kerberos ticket expires:
comment|// 5) All the permissions needed for UGI to do its weird JAAS hack
operator|new
name|RuntimePermission
argument_list|(
literal|"getClassLoader"
argument_list|)
block|,
operator|new
name|RuntimePermission
argument_list|(
literal|"setContextClassLoader"
argument_list|)
block|,
comment|// 6) Additional permissions for the login modules
operator|new
name|AuthPermission
argument_list|(
literal|"modifyPrincipals"
argument_list|)
block|,
operator|new
name|PrivateCredentialPermission
argument_list|(
literal|"org.apache.hadoop.security.Credentials * \"*\""
argument_list|,
literal|"read"
argument_list|)
block|,
operator|new
name|PrivateCredentialPermission
argument_list|(
literal|"javax.security.auth.kerberos.KerberosTicket * \"*\""
argument_list|,
literal|"read"
argument_list|)
block|,
operator|new
name|PrivateCredentialPermission
argument_list|(
literal|"javax.security.auth.kerberos.KeyTab * \"*\""
argument_list|,
literal|"read"
argument_list|)
comment|// Included later:
comment|// 7) allow code to initiate kerberos connections as the logged in user
comment|// Still far and away fewer permissions than the original full plugin policy
block|}
expr_stmt|;
block|}
comment|/**      * Locates the keytab file in the environment and verifies that it exists.      * Expects keytab file to exist at {@code $CONFIG_DIR$/repository-hdfs/krb5.keytab}      */
DECL|method|locateKeytabFile
specifier|static
name|Path
name|locateKeytabFile
parameter_list|(
name|Environment
name|environment
parameter_list|)
block|{
name|Path
name|keytabPath
init|=
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"repository-hdfs"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"krb5.keytab"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|keytabPath
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not locate keytab at ["
operator|+
name|keytabPath
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not locate keytab at ["
operator|+
name|keytabPath
operator|+
literal|"]"
argument_list|,
name|se
argument_list|)
throw|;
block|}
return|return
name|keytabPath
return|;
block|}
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|restrictedExecutionPermissions
specifier|private
specifier|final
name|Permission
index|[]
name|restrictedExecutionPermissions
decl_stmt|;
DECL|method|HdfsSecurityContext
name|HdfsSecurityContext
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|this
operator|.
name|ugi
operator|=
name|ugi
expr_stmt|;
name|this
operator|.
name|restrictedExecutionPermissions
operator|=
name|renderPermissions
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
block|}
DECL|method|renderPermissions
specifier|private
name|Permission
index|[]
name|renderPermissions
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|Permission
index|[]
name|permissions
decl_stmt|;
if|if
condition|(
name|ugi
operator|.
name|isFromKeytab
argument_list|()
condition|)
block|{
comment|// KERBEROS
comment|// Leave room to append one extra permission based on the logged in user's info.
name|int
name|permlen
init|=
name|KERBEROS_AUTH_PERMISSIONS
operator|.
name|length
operator|+
literal|1
decl_stmt|;
name|permissions
operator|=
operator|new
name|Permission
index|[
name|permlen
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|KERBEROS_AUTH_PERMISSIONS
argument_list|,
literal|0
argument_list|,
name|permissions
argument_list|,
literal|0
argument_list|,
name|KERBEROS_AUTH_PERMISSIONS
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Append a kerberos.ServicePermission to only allow initiating kerberos connections
comment|// as the logged in user.
name|permissions
index|[
name|permissions
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|new
name|ServicePermission
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|,
literal|"initiate"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// SIMPLE
name|permissions
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|SIMPLE_AUTH_PERMISSIONS
argument_list|,
name|SIMPLE_AUTH_PERMISSIONS
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|permissions
return|;
block|}
DECL|method|getRestrictedExecutionPermissions
name|Permission
index|[]
name|getRestrictedExecutionPermissions
parameter_list|()
block|{
return|return
name|restrictedExecutionPermissions
return|;
block|}
DECL|method|ensureLogin
name|void
name|ensureLogin
parameter_list|()
block|{
if|if
condition|(
name|ugi
operator|.
name|isFromKeytab
argument_list|()
condition|)
block|{
try|try
block|{
name|ugi
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
literal|"Could not re-authenticate"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

