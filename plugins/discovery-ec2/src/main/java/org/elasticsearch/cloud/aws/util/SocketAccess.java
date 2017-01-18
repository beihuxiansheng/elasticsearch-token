begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|SpecialPermission
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
name|SocketPermission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_comment
comment|/**  * This plugin uses aws libraries to connect to aws services. For these remote calls the plugin needs  * {@link SocketPermission} 'connect' to establish connections. This class wraps the operations requiring access in  * {@link AccessController#doPrivileged(PrivilegedAction)} blocks.  */
end_comment

begin_class
DECL|class|SocketAccess
specifier|public
specifier|final
class|class
name|SocketAccess
block|{
DECL|field|SPECIAL_PERMISSION
specifier|private
specifier|static
specifier|final
name|SpecialPermission
name|SPECIAL_PERMISSION
init|=
operator|new
name|SpecialPermission
argument_list|()
decl_stmt|;
DECL|method|SocketAccess
specifier|private
name|SocketAccess
parameter_list|()
block|{}
DECL|method|doPrivileged
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|doPrivileged
parameter_list|(
name|PrivilegedAction
argument_list|<
name|T
argument_list|>
name|operation
parameter_list|)
block|{
name|checkSpecialPermission
argument_list|()
expr_stmt|;
return|return
name|AccessController
operator|.
name|doPrivileged
argument_list|(
name|operation
argument_list|)
return|;
block|}
DECL|method|doPrivilegedIOException
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|doPrivilegedIOException
parameter_list|(
name|PrivilegedExceptionAction
argument_list|<
name|T
argument_list|>
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSpecialPermission
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|AccessController
operator|.
name|doPrivileged
argument_list|(
name|operation
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
DECL|method|checkSpecialPermission
specifier|private
specifier|static
name|void
name|checkSpecialPermission
parameter_list|()
block|{
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|checkPermission
argument_list|(
name|SPECIAL_PERMISSION
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

