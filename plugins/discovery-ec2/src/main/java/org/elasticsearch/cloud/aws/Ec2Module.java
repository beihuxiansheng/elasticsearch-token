begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
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
name|AbstractModule
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
name|ec2
operator|.
name|Ec2Discovery
import|;
end_import

begin_class
DECL|class|Ec2Module
specifier|public
class|class
name|Ec2Module
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|AwsEc2Service
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|AwsEc2ServiceImpl
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
comment|/**      * Check if discovery is meant to start      * @return true if we can start discovery features      */
DECL|method|isEc2DiscoveryActive
specifier|public
specifier|static
name|boolean
name|isEc2DiscoveryActive
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
comment|// User set discovery.type: ec2
if|if
condition|(
operator|!
name|Ec2Discovery
operator|.
name|EC2
operator|.
name|equalsIgnoreCase
argument_list|(
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"discovery.type not set to {}"
argument_list|,
name|Ec2Discovery
operator|.
name|EC2
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

