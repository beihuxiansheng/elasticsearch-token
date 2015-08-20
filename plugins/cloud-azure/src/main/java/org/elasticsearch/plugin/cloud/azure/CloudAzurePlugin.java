begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.cloud.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|cloud
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|AzureModule
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
name|inject
operator|.
name|Module
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
name|discovery
operator|.
name|DiscoveryModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|azure
operator|.
name|AzureDiscovery
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
name|snapshots
operator|.
name|blobstore
operator|.
name|BlobStoreIndexShardRepository
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
name|store
operator|.
name|IndexStoreModule
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
name|store
operator|.
name|smbmmapfs
operator|.
name|SmbMmapFsIndexStore
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
name|store
operator|.
name|smbsimplefs
operator|.
name|SmbSimpleFsIndexStore
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
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoriesModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|azure
operator|.
name|AzureRepository
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
name|Collection
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|AzureModule
operator|.
name|isSnapshotReady
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CloudAzurePlugin
specifier|public
class|class
name|CloudAzurePlugin
extends|extends
name|Plugin
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|CloudAzurePlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CloudAzurePlugin
specifier|public
name|CloudAzurePlugin
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"starting azure plugin..."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"cloud-azure"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Cloud Azure Plugin"
return|;
block|}
annotation|@
name|Override
DECL|method|nodeModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|nodeModules
parameter_list|()
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|AzureModule
operator|.
name|isCloudReady
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|modules
operator|.
name|add
argument_list|(
operator|new
name|AzureModule
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|RepositoriesModule
name|module
parameter_list|)
block|{
if|if
condition|(
name|isSnapshotReady
argument_list|(
name|settings
argument_list|,
name|logger
argument_list|)
condition|)
block|{
name|module
operator|.
name|registerRepository
argument_list|(
name|AzureRepository
operator|.
name|TYPE
argument_list|,
name|AzureRepository
operator|.
name|class
argument_list|,
name|BlobStoreIndexShardRepository
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|DiscoveryModule
name|discoveryModule
parameter_list|)
block|{
name|discoveryModule
operator|.
name|addDiscoveryType
argument_list|(
literal|"azure"
argument_list|,
name|AzureDiscovery
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|IndexStoreModule
name|storeModule
parameter_list|)
block|{
name|storeModule
operator|.
name|addIndexStore
argument_list|(
literal|"smb_mmap_fs"
argument_list|,
name|SmbMmapFsIndexStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|storeModule
operator|.
name|addIndexStore
argument_list|(
literal|"smb_simple_fs"
argument_list|,
name|SmbSimpleFsIndexStore
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
