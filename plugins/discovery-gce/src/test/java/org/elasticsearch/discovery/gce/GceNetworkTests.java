begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|gce
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
name|gce
operator|.
name|network
operator|.
name|GceNameResolver
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * Test for GCE network.host settings.  * Related to https://github.com/elastic/elasticsearch/issues/13605  */
end_comment

begin_class
DECL|class|GceNetworkTests
specifier|public
class|class
name|GceNetworkTests
extends|extends
name|ESTestCase
block|{
comment|/**      * Test for network.host: _gce_      */
DECL|method|testNetworkHostGceDefault
specifier|public
name|void
name|testNetworkHostGceDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
literal|"_gce_"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"10.240.0.2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for network.host: _gce:privateIp_      */
DECL|method|testNetworkHostPrivateIp
specifier|public
name|void
name|testNetworkHostPrivateIp
parameter_list|()
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
literal|"_gce:privateIp_"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"10.240.0.2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for network.host: _gce:hostname_      */
DECL|method|testNetworkHostPrivateDns
specifier|public
name|void
name|testNetworkHostPrivateDns
parameter_list|()
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
literal|"_gce:hostname_"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for network.host: _gce:doesnotexist_      * This should raise an IllegalArgumentException as this setting does not exist      */
DECL|method|testNetworkHostWrongSetting
specifier|public
name|void
name|testNetworkHostWrongSetting
parameter_list|()
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
literal|"_gce:doesnotexist_"
argument_list|,
operator|(
name|InetAddress
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test with multiple network interfaces:      * network.host: _gce:privateIp:0_      * network.host: _gce:privateIp:1_      */
DECL|method|testNetworkHostPrivateIpInterface
specifier|public
name|void
name|testNetworkHostPrivateIpInterface
parameter_list|()
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
literal|"_gce:privateIp:0_"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"10.240.0.2"
argument_list|)
argument_list|)
expr_stmt|;
name|resolveGce
argument_list|(
literal|"_gce:privateIp:1_"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"10.150.0.1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that we don't have any regression with network host core settings such as      * network.host: _local_      */
DECL|method|networkHostCoreLocal
specifier|public
name|void
name|networkHostCoreLocal
parameter_list|()
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
literal|"_local_"
argument_list|,
operator|new
name|NetworkService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|resolveBindHostAddresses
argument_list|(
operator|new
name|String
index|[]
block|{
name|NetworkService
operator|.
name|DEFAULT_NETWORK_HOST
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Utility test method to test different settings      * @param gceNetworkSetting tested network.host property      * @param expected expected InetAddress, null if we expect an exception      * @throws IOException Well... If something goes wrong :)      */
DECL|method|resolveGce
specifier|private
name|void
name|resolveGce
parameter_list|(
name|String
name|gceNetworkSetting
parameter_list|,
name|InetAddress
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|resolveGce
argument_list|(
name|gceNetworkSetting
argument_list|,
name|expected
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|InetAddress
index|[]
block|{
name|expected
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Utility test method to test different settings      * @param gceNetworkSetting tested network.host property      * @param expected expected InetAddress, null if we expect an exception      * @throws IOException Well... If something goes wrong :)      */
DECL|method|resolveGce
specifier|private
name|void
name|resolveGce
parameter_list|(
name|String
name|gceNetworkSetting
parameter_list|,
name|InetAddress
index|[]
name|expected
parameter_list|)
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
name|gceNetworkSetting
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GceMetadataServiceMock
name|mock
init|=
operator|new
name|GceMetadataServiceMock
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|nodeSettings
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|GceNameResolver
argument_list|(
name|nodeSettings
argument_list|,
name|mock
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|InetAddress
index|[]
name|addresses
init|=
name|networkService
operator|.
name|resolveBindHostAddresses
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"We should get a IllegalArgumentException when setting network.host: _gce:doesnotexist_"
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|addresses
argument_list|,
name|arrayContaining
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|expected
operator|!=
literal|null
condition|)
block|{
comment|// We were expecting something and not an exception
throw|throw
name|e
throw|;
block|}
comment|// We check that we get the expected exception
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"is not one of the supported GCE network.host setting"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

