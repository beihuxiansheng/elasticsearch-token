begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.azure.classic.management
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|classic
operator|.
name|management
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|core
operator|.
name|Builder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|core
operator|.
name|DefaultBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|core
operator|.
name|utils
operator|.
name|KeyStoreType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|management
operator|.
name|compute
operator|.
name|ComputeManagementClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|management
operator|.
name|compute
operator|.
name|ComputeManagementService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|management
operator|.
name|compute
operator|.
name|models
operator|.
name|HostedServiceGetDetailedResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|management
operator|.
name|configuration
operator|.
name|ManagementConfiguration
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
name|cloud
operator|.
name|azure
operator|.
name|classic
operator|.
name|AzureServiceRemoteException
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AzureComputeServiceImpl
specifier|public
class|class
name|AzureComputeServiceImpl
extends|extends
name|AbstractLifecycleComponent
implements|implements
name|AzureComputeService
block|{
DECL|field|client
specifier|private
specifier|final
name|ComputeManagementClient
name|client
decl_stmt|;
DECL|field|serviceName
specifier|private
specifier|final
name|String
name|serviceName
decl_stmt|;
annotation|@
name|Inject
DECL|method|AzureComputeServiceImpl
specifier|public
name|AzureComputeServiceImpl
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|String
name|subscriptionId
init|=
name|Management
operator|.
name|SUBSCRIPTION_ID_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|serviceName
operator|=
name|Management
operator|.
name|SERVICE_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|String
name|keystorePath
init|=
name|Management
operator|.
name|KEYSTORE_PATH_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|String
name|keystorePassword
init|=
name|Management
operator|.
name|KEYSTORE_PASSWORD_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|KeyStoreType
name|keystoreType
init|=
name|Management
operator|.
name|KEYSTORE_TYPE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"creating new Azure client for [{}], [{}]"
argument_list|,
name|subscriptionId
argument_list|,
name|serviceName
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Azure SDK configuration uses DefaultBuilder which uses java.util.ServiceLoader to load the
comment|// various Azure services. By default, this will use the current thread's context classloader
comment|// to load services. Since the current thread refers to the main application classloader it
comment|// won't find any Azure service implementation.
comment|// Here we basically create a new DefaultBuilder that uses the current class classloader to load services.
name|DefaultBuilder
name|builder
init|=
operator|new
name|DefaultBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Builder
operator|.
name|Exports
name|exports
range|:
name|ServiceLoader
operator|.
name|load
argument_list|(
name|Builder
operator|.
name|Exports
operator|.
name|class
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
control|)
block|{
name|exports
operator|.
name|register
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
comment|// And create a new blank configuration based on the previous DefaultBuilder
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|configuration
operator|.
name|setProperty
argument_list|(
name|Configuration
operator|.
name|PROPERTY_LOG_HTTP_REQUESTS
argument_list|,
name|logger
operator|.
name|isTraceEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|managementConfig
init|=
name|ManagementConfiguration
operator|.
name|configure
argument_list|(
literal|null
argument_list|,
name|configuration
argument_list|,
name|Management
operator|.
name|ENDPOINT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|subscriptionId
argument_list|,
name|keystorePath
argument_list|,
name|keystorePassword
argument_list|,
name|keystoreType
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"creating new Azure client for [{}], [{}]"
argument_list|,
name|subscriptionId
argument_list|,
name|serviceName
argument_list|)
expr_stmt|;
name|client
operator|=
name|ComputeManagementService
operator|.
name|create
argument_list|(
name|managementConfig
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
name|ElasticsearchException
argument_list|(
literal|"Unable to configure Azure compute service"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getServiceDetails
specifier|public
name|HostedServiceGetDetailedResponse
name|getServiceDetails
parameter_list|()
block|{
try|try
block|{
return|return
name|client
operator|.
name|getHostedServicesOperations
argument_list|()
operator|.
name|getDetailed
argument_list|(
name|serviceName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AzureServiceRemoteException
argument_list|(
literal|"can not get list of azure nodes"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"error while closing Azure client"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
