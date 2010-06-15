begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|fs
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
name|inject
operator|.
name|Inject
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
name|gateway
operator|.
name|Gateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|fs
operator|.
name|FsGateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|AbstractIndexComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|IndexGateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|IndexShardGateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|FileSystemUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|FsIndexGateway
specifier|public
class|class
name|FsIndexGateway
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexGateway
block|{
DECL|field|location
specifier|private
specifier|final
name|String
name|location
decl_stmt|;
DECL|field|indexGatewayHome
specifier|private
name|File
name|indexGatewayHome
decl_stmt|;
DECL|method|FsIndexGateway
annotation|@
name|Inject
specifier|public
name|FsIndexGateway
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Environment
name|environment
parameter_list|,
name|Gateway
name|gateway
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|String
name|location
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"location"
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|gateway
operator|instanceof
name|FsGateway
condition|)
block|{
name|indexGatewayHome
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|(
operator|(
name|FsGateway
operator|)
name|gateway
operator|)
operator|.
name|gatewayHome
argument_list|()
argument_list|,
literal|"indices"
argument_list|)
argument_list|,
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexGatewayHome
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|environment
operator|.
name|workWithClusterFile
argument_list|()
argument_list|,
literal|"gateway"
argument_list|)
argument_list|,
literal|"indices"
argument_list|)
argument_list|,
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|location
operator|=
name|Strings
operator|.
name|cleanPath
argument_list|(
name|indexGatewayHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexGatewayHome
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|location
argument_list|)
argument_list|,
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|indexGatewayHome
operator|.
name|exists
argument_list|()
operator|&&
name|indexGatewayHome
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
block|{
name|boolean
name|result
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|indexGatewayHome
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|indexGatewayHome
operator|.
name|exists
argument_list|()
operator|&&
name|indexGatewayHome
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IndexException
argument_list|(
name|index
argument_list|,
literal|"Failed to create index gateway at ["
operator|+
name|indexGatewayHome
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|shardGatewayClass
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|IndexShardGateway
argument_list|>
name|shardGatewayClass
parameter_list|()
block|{
return|return
name|FsIndexShardGateway
operator|.
name|class
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delete
condition|)
block|{
return|return;
block|}
try|try
block|{
name|String
index|[]
name|files
init|=
name|indexGatewayHome
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
operator|||
name|files
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|deleteRecursively
argument_list|(
name|indexGatewayHome
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
DECL|method|indexGatewayHome
specifier|public
name|File
name|indexGatewayHome
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexGatewayHome
return|;
block|}
block|}
end_class

end_unit

