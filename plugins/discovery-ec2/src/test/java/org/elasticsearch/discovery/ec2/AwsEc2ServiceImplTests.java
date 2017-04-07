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
name|com
operator|.
name|amazonaws
operator|.
name|ClientConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|Protocol
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|DefaultAWSCredentialsProviderChain
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
name|MockSecureSettings
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
name|Setting
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
name|ec2
operator|.
name|AwsEc2Service
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
name|AwsEc2ServiceImpl
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
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
name|is
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
name|nullValue
import|;
end_import

begin_class
DECL|class|AwsEc2ServiceImplTests
specifier|public
class|class
name|AwsEc2ServiceImplTests
extends|extends
name|ESTestCase
block|{
DECL|method|testAWSCredentialsWithSystemProviders
specifier|public
name|void
name|testAWSCredentialsWithSystemProviders
parameter_list|()
block|{
name|AWSCredentialsProvider
name|credentialsProvider
init|=
name|AwsEc2ServiceImpl
operator|.
name|buildCredentials
argument_list|(
name|logger
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|credentialsProvider
argument_list|,
name|instanceOf
argument_list|(
name|DefaultAWSCredentialsProviderChain
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAWSCredentialsWithElasticsearchAwsSettings
specifier|public
name|void
name|testAWSCredentialsWithElasticsearchAwsSettings
parameter_list|()
block|{
name|MockSecureSettings
name|secureSettings
init|=
operator|new
name|MockSecureSettings
argument_list|()
decl_stmt|;
name|secureSettings
operator|.
name|setString
argument_list|(
literal|"discovery.ec2.access_key"
argument_list|,
literal|"aws_key"
argument_list|)
expr_stmt|;
name|secureSettings
operator|.
name|setString
argument_list|(
literal|"discovery.ec2.secret_key"
argument_list|,
literal|"aws_secret"
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|setSecureSettings
argument_list|(
name|secureSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSCredentialsWithElasticsearchSettingsTest
argument_list|(
name|settings
argument_list|,
literal|"aws_key"
argument_list|,
literal|"aws_secret"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAWSCredentialsWithElasticsearchAwsSettingsBackcompat
specifier|public
name|void
name|testAWSCredentialsWithElasticsearchAwsSettingsBackcompat
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|KEY_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_key"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|SECRET_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_secret"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSCredentialsWithElasticsearchSettingsTest
argument_list|(
name|settings
argument_list|,
literal|"aws_key"
argument_list|,
literal|"aws_secret"
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|KEY_SETTING
operator|,
name|AwsEc2Service
operator|.
name|SECRET_SETTING
block|}
block|)
function|;
block|}
end_class

begin_function
DECL|method|testAWSCredentialsWithElasticsearchEc2SettingsBackcompat
specifier|public
name|void
name|testAWSCredentialsWithElasticsearchEc2SettingsBackcompat
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|KEY_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_key"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|SECRET_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_secret"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSCredentialsWithElasticsearchSettingsTest
argument_list|(
name|settings
argument_list|,
literal|"ec2_key"
argument_list|,
literal|"ec2_secret"
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|KEY_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|SECRET_SETTING
block|}
block|)
function|;
end_function

begin_function
unit|}      public
DECL|method|testAWSCredentialsWithElasticsearchAwsAndEc2Settings
name|void
name|testAWSCredentialsWithElasticsearchAwsAndEc2Settings
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|KEY_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_key"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|SECRET_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_secret"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|KEY_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_key"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|SECRET_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_secret"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSCredentialsWithElasticsearchSettingsTest
argument_list|(
name|settings
argument_list|,
literal|"ec2_key"
argument_list|,
literal|"ec2_secret"
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|KEY_SETTING
operator|,
name|AwsEc2Service
operator|.
name|SECRET_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|KEY_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|SECRET_SETTING
block|}
block|)
function|;
end_function

begin_function
unit|}      protected
DECL|method|launchAWSCredentialsWithElasticsearchSettingsTest
name|void
name|launchAWSCredentialsWithElasticsearchSettingsTest
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|expectedKey
parameter_list|,
name|String
name|expectedSecret
parameter_list|)
block|{
name|AWSCredentials
name|credentials
init|=
name|AwsEc2ServiceImpl
operator|.
name|buildCredentials
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|credentials
operator|.
name|getAWSAccessKeyId
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedKey
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|credentials
operator|.
name|getAWSSecretKey
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedSecret
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testAWSDefaultConfiguration
specifier|public
name|void
name|testAWSDefaultConfiguration
parameter_list|()
block|{
name|launchAWSConfigurationTest
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Protocol
operator|.
name|HTTPS
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|ClientConfiguration
operator|.
name|DEFAULT_SOCKET_TIMEOUT
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testAWSConfigurationWithAwsSettings
specifier|public
name|void
name|testAWSConfigurationWithAwsSettings
parameter_list|()
block|{
name|MockSecureSettings
name|secureSettings
init|=
operator|new
name|MockSecureSettings
argument_list|()
decl_stmt|;
name|secureSettings
operator|.
name|setString
argument_list|(
literal|"discovery.ec2.proxy.username"
argument_list|,
literal|"aws_proxy_username"
argument_list|)
expr_stmt|;
name|secureSettings
operator|.
name|setString
argument_list|(
literal|"discovery.ec2.proxy.password"
argument_list|,
literal|"aws_proxy_password"
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.ec2.protocol"
argument_list|,
literal|"http"
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.ec2.proxy.host"
argument_list|,
literal|"aws_proxy_host"
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.ec2.proxy.port"
argument_list|,
literal|8080
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.ec2.read_timeout"
argument_list|,
literal|"10s"
argument_list|)
operator|.
name|setSecureSettings
argument_list|(
name|secureSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSConfigurationTest
argument_list|(
name|settings
argument_list|,
name|Protocol
operator|.
name|HTTP
argument_list|,
literal|"aws_proxy_host"
argument_list|,
literal|8080
argument_list|,
literal|"aws_proxy_username"
argument_list|,
literal|"aws_proxy_password"
argument_list|,
literal|null
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testAWSConfigurationWithAwsSettingsBackcompat
specifier|public
name|void
name|testAWSConfigurationWithAwsSettingsBackcompat
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROTOCOL_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"http"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_HOST_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_proxy_host"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_PORT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|8080
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_USERNAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_proxy_username"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_PASSWORD_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_proxy_password"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|SIGNER_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"AWS3SignerType"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|READ_TIMEOUT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"10s"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSConfigurationTest
argument_list|(
name|settings
argument_list|,
name|Protocol
operator|.
name|HTTP
argument_list|,
literal|"aws_proxy_host"
argument_list|,
literal|8080
argument_list|,
literal|"aws_proxy_username"
argument_list|,
literal|"aws_proxy_password"
argument_list|,
literal|"AWS3SignerType"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|PROTOCOL_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_HOST_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_PORT_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_USERNAME_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_PASSWORD_SETTING
operator|,
name|AwsEc2Service
operator|.
name|SIGNER_SETTING
operator|,
name|AwsEc2Service
operator|.
name|READ_TIMEOUT
block|}
block|)
function|;
end_function

begin_function
unit|}      public
DECL|method|testAWSConfigurationWithAwsAndEc2Settings
name|void
name|testAWSConfigurationWithAwsAndEc2Settings
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROTOCOL_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"http"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_HOST_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_proxy_host"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_PORT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|8080
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_USERNAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_proxy_username"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|PROXY_PASSWORD_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"aws_proxy_password"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|SIGNER_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"AWS3SignerType"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|READ_TIMEOUT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"20s"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROTOCOL_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"https"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_HOST_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_proxy_host"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_PORT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|8081
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_USERNAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_proxy_username"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_PASSWORD_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2_proxy_password"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|SIGNER_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NoOpSignerType"
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|READ_TIMEOUT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"10s"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launchAWSConfigurationTest
argument_list|(
name|settings
argument_list|,
name|Protocol
operator|.
name|HTTPS
argument_list|,
literal|"ec2_proxy_host"
argument_list|,
literal|8081
argument_list|,
literal|"ec2_proxy_username"
argument_list|,
literal|"ec2_proxy_password"
argument_list|,
literal|"NoOpSignerType"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|PROTOCOL_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_HOST_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_PORT_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_USERNAME_SETTING
operator|,
name|AwsEc2Service
operator|.
name|PROXY_PASSWORD_SETTING
operator|,
name|AwsEc2Service
operator|.
name|SIGNER_SETTING
operator|,
name|AwsEc2Service
operator|.
name|READ_TIMEOUT
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROTOCOL_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_HOST_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_PORT_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_USERNAME_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|PROXY_PASSWORD_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|SIGNER_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|READ_TIMEOUT
block|}
block|)
function|;
end_function

begin_function
unit|}      protected
DECL|method|launchAWSConfigurationTest
name|void
name|launchAWSConfigurationTest
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Protocol
name|expectedProtocol
parameter_list|,
name|String
name|expectedProxyHost
parameter_list|,
name|int
name|expectedProxyPort
parameter_list|,
name|String
name|expectedProxyUsername
parameter_list|,
name|String
name|expectedProxyPassword
parameter_list|,
name|String
name|expectedSigner
parameter_list|,
name|int
name|expectedReadTimeout
parameter_list|)
block|{
name|ClientConfiguration
name|configuration
init|=
name|AwsEc2ServiceImpl
operator|.
name|buildConfiguration
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getResponseMetadataCacheSize
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getProtocol
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedProtocol
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getProxyHost
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedProxyHost
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getProxyPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedProxyPort
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getProxyUsername
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedProxyUsername
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getProxyPassword
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedProxyPassword
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getSignerOverride
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedSigner
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|configuration
operator|.
name|getSocketTimeout
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedReadTimeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testDefaultEndpoint
specifier|public
name|void
name|testDefaultEndpoint
parameter_list|()
block|{
name|String
name|endpoint
init|=
name|AwsEc2ServiceImpl
operator|.
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|endpoint
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testSpecificEndpoint
specifier|public
name|void
name|testSpecificEndpoint
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|DISCOVERY_EC2
operator|.
name|ENDPOINT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2.endpoint"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|endpoint
init|=
name|AwsEc2ServiceImpl
operator|.
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|endpoint
argument_list|,
name|is
argument_list|(
literal|"ec2.endpoint"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testSpecificEndpointBackcompat
specifier|public
name|void
name|testSpecificEndpointBackcompat
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|ENDPOINT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"ec2.endpoint"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|endpoint
init|=
name|AwsEc2ServiceImpl
operator|.
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|endpoint
argument_list|,
name|is
argument_list|(
literal|"ec2.endpoint"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|ENDPOINT_SETTING
block|}
block|)
function|;
end_function

begin_function
unit|}      public
DECL|method|testRegionWithAwsSettings
name|void
name|testRegionWithAwsSettings
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|REGION_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|randomFrom
argument_list|(
literal|"eu-west"
argument_list|,
literal|"eu-west-1"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|endpoint
init|=
name|AwsEc2ServiceImpl
operator|.
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|endpoint
argument_list|,
name|is
argument_list|(
literal|"ec2.eu-west-1.amazonaws.com"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|REGION_SETTING
block|}
block|)
function|;
end_function

begin_function
unit|}      public
DECL|method|testRegionWithAwsAndEc2Settings
name|void
name|testRegionWithAwsAndEc2Settings
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|REGION_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|randomFrom
argument_list|(
literal|"eu-west"
argument_list|,
literal|"eu-west-1"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|REGION_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|randomFrom
argument_list|(
literal|"us-west"
argument_list|,
literal|"us-west-1"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|endpoint
init|=
name|AwsEc2ServiceImpl
operator|.
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|endpoint
argument_list|,
name|is
argument_list|(
literal|"ec2.us-west-1.amazonaws.com"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|REGION_SETTING
operator|,
name|AwsEc2Service
operator|.
name|CLOUD_EC2
operator|.
name|REGION_SETTING
block|}
block|)
function|;
end_function

begin_function
unit|}      public
DECL|method|testInvalidRegion
name|void
name|testInvalidRegion
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AwsEc2Service
operator|.
name|REGION_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"does-not-exist"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|AwsEc2ServiceImpl
operator|.
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"No automatic endpoint could be derived from region"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSettingDeprecationsAndWarnings
argument_list|(
operator|new
name|Setting
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AwsEc2Service
operator|.
name|REGION_SETTING
block|}
block|)
function|;
end_function

unit|} }
end_unit

