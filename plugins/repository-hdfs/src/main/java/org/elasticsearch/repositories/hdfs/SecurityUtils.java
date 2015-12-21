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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

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

begin_class
DECL|class|SecurityUtils
specifier|final
class|class
name|SecurityUtils
block|{
DECL|method|execute
specifier|static
parameter_list|<
name|V
parameter_list|>
name|V
name|execute
parameter_list|(
name|FileContextFactory
name|fcf
parameter_list|,
name|FcCallback
argument_list|<
name|V
argument_list|>
name|callback
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|execute
argument_list|(
name|fcf
operator|.
name|getFileContext
argument_list|()
argument_list|,
name|callback
argument_list|)
return|;
block|}
DECL|method|execute
specifier|static
parameter_list|<
name|V
parameter_list|>
name|V
name|execute
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|FcCallback
argument_list|<
name|V
argument_list|>
name|callback
parameter_list|)
throws|throws
name|IOException
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
comment|// unprivileged code such as scripts do not have SpecialPermission
name|sm
operator|.
name|checkPermission
argument_list|(
operator|new
name|SpecialPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|V
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|callback
operator|.
name|doInHdfs
argument_list|(
name|fc
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|pae
parameter_list|)
block|{
name|Throwable
name|th
init|=
name|pae
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
block|}
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
block|}
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
block|}
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|pae
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

