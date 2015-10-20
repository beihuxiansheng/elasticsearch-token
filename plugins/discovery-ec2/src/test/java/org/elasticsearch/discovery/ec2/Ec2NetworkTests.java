begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.ec2
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|ec2
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
name|aws
operator|.
name|network
operator|.
name|Ec2NameResolver
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
name|test
operator|.
name|ESTestCase
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
name|InetAddress
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|arrayContaining
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_comment
comment|/**  * Test for EC2 network.host settings.  */
end_comment

begin_class
DECL|class|Ec2NetworkTests
specifier|public
class|class
name|Ec2NetworkTests
extends|extends
name|ESTestCase
block|{
comment|/**      * Test for network.host: _ec2_      */
DECL|method|testNetworkHostEc2
specifier|public
name|void
name|testNetworkHostEc2
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"local-ipv4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test for network.host: _ec2:publicIp_      */
DECL|method|testNetworkHostEc2PublicIp
specifier|public
name|void
name|testNetworkHostEc2PublicIp
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2:publicIp_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"public-ipv4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test for network.host: _ec2:privateIp_      */
DECL|method|testNetworkHostEc2PrivateIp
specifier|public
name|void
name|testNetworkHostEc2PrivateIp
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2:privateIp_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"local-ipv4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test for network.host: _ec2:privateIpv4_      */
DECL|method|testNetworkHostEc2PrivateIpv4
specifier|public
name|void
name|testNetworkHostEc2PrivateIpv4
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2:privateIpv4_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"local-ipv4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test for network.host: _ec2:privateDns_      */
DECL|method|testNetworkHostEc2PrivateDns
specifier|public
name|void
name|testNetworkHostEc2PrivateDns
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2:privateDns_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"local-hostname"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test for network.host: _ec2:publicIpv4_      */
DECL|method|testNetworkHostEc2PublicIpv4
specifier|public
name|void
name|testNetworkHostEc2PublicIpv4
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2:publicIpv4_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"public-ipv4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test for network.host: _ec2:publicDns_      */
DECL|method|testNetworkHostEc2PublicDns
specifier|public
name|void
name|testNetworkHostEc2PublicDns
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_ec2:publicDns_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO we need to replace that with a mock. For now we check the URL we are supposed to reach.
try|try
block|{
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"public-hostname"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test that we don't have any regression with network host core settings such as      * network.host: _local_      */
DECL|method|testNetworkHostCoreLocal
specifier|public
name|void
name|testNetworkHostCoreLocal
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"_local_"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|networkService
operator|.
name|addCustomNameResolver
argument_list|(
operator|new
name|Ec2NameResolver
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
name|InetAddress
index|[]
name|addresses
init|=
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|addresses
argument_list|,
name|arrayContaining
argument_list|(
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
literal|"_local_"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

