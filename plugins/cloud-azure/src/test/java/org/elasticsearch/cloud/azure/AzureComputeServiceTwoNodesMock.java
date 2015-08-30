begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
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
name|management
operator|.
name|compute
operator|.
name|models
operator|.
name|DeploymentSlot
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
name|DeploymentStatus
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
name|compute
operator|.
name|models
operator|.
name|InstanceEndpoint
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
name|RoleInstance
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
name|management
operator|.
name|AzureComputeServiceAbstractMock
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
name|network
operator|.
name|NetworkService
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|Arrays
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
name|util
operator|.
name|CollectionUtils
operator|.
name|newSingletonArrayList
import|;
end_import

begin_comment
comment|/**  * Mock Azure API with two started nodes  */
end_comment

begin_class
DECL|class|AzureComputeServiceTwoNodesMock
specifier|public
class|class
name|AzureComputeServiceTwoNodesMock
extends|extends
name|AzureComputeServiceAbstractMock
block|{
DECL|class|TestPlugin
specifier|public
specifier|static
class|class
name|TestPlugin
extends|extends
name|Plugin
block|{
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"mock-compute-service"
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
literal|"plugs in a mock compute service for testing"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|AzureModule
name|azureModule
parameter_list|)
block|{
name|azureModule
operator|.
name|computeServiceImpl
operator|=
name|AzureComputeServiceTwoNodesMock
operator|.
name|class
expr_stmt|;
block|}
block|}
DECL|field|networkService
name|NetworkService
name|networkService
decl_stmt|;
annotation|@
name|Inject
DECL|method|AzureComputeServiceTwoNodesMock
specifier|protected
name|AzureComputeServiceTwoNodesMock
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|networkService
operator|=
name|networkService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServiceDetails
specifier|public
name|HostedServiceGetDetailedResponse
name|getServiceDetails
parameter_list|()
block|{
name|HostedServiceGetDetailedResponse
name|response
init|=
operator|new
name|HostedServiceGetDetailedResponse
argument_list|()
decl_stmt|;
name|HostedServiceGetDetailedResponse
operator|.
name|Deployment
name|deployment
init|=
operator|new
name|HostedServiceGetDetailedResponse
operator|.
name|Deployment
argument_list|()
decl_stmt|;
comment|// Fake the deployment
name|deployment
operator|.
name|setName
argument_list|(
literal|"dummy"
argument_list|)
expr_stmt|;
name|deployment
operator|.
name|setDeploymentSlot
argument_list|(
name|DeploymentSlot
operator|.
name|Production
argument_list|)
expr_stmt|;
name|deployment
operator|.
name|setStatus
argument_list|(
name|DeploymentStatus
operator|.
name|Running
argument_list|)
expr_stmt|;
comment|// Fake a first instance
name|RoleInstance
name|instance1
init|=
operator|new
name|RoleInstance
argument_list|()
decl_stmt|;
name|instance1
operator|.
name|setInstanceName
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
comment|// Fake the private IP
name|instance1
operator|.
name|setIPAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// Fake the public IP
name|InstanceEndpoint
name|endpoint1
init|=
operator|new
name|InstanceEndpoint
argument_list|()
decl_stmt|;
name|endpoint1
operator|.
name|setName
argument_list|(
literal|"elasticsearch"
argument_list|)
expr_stmt|;
name|endpoint1
operator|.
name|setVirtualIPAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|)
expr_stmt|;
name|endpoint1
operator|.
name|setPort
argument_list|(
literal|9400
argument_list|)
expr_stmt|;
name|instance1
operator|.
name|setInstanceEndpoints
argument_list|(
name|newSingletonArrayList
argument_list|(
name|endpoint1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fake a first instance
name|RoleInstance
name|instance2
init|=
operator|new
name|RoleInstance
argument_list|()
decl_stmt|;
name|instance2
operator|.
name|setInstanceName
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
comment|// Fake the private IP
name|instance2
operator|.
name|setIPAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// Fake the public IP
name|InstanceEndpoint
name|endpoint2
init|=
operator|new
name|InstanceEndpoint
argument_list|()
decl_stmt|;
name|endpoint2
operator|.
name|setName
argument_list|(
literal|"elasticsearch"
argument_list|)
expr_stmt|;
name|endpoint2
operator|.
name|setVirtualIPAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|)
expr_stmt|;
name|endpoint2
operator|.
name|setPort
argument_list|(
literal|9401
argument_list|)
expr_stmt|;
name|instance2
operator|.
name|setInstanceEndpoints
argument_list|(
name|newSingletonArrayList
argument_list|(
name|endpoint2
argument_list|)
argument_list|)
expr_stmt|;
name|deployment
operator|.
name|setRoleInstances
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|instance1
argument_list|,
name|instance2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDeployments
argument_list|(
name|newSingletonArrayList
argument_list|(
name|deployment
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

