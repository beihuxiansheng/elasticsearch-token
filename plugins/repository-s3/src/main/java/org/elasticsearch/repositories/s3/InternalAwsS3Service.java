begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.s3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

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
name|BasicAWSCredentials
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
name|InstanceProfileCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|http
operator|.
name|IdleConnectionReaper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|internal
operator|.
name|StaticCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|S3ClientOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|cluster
operator|.
name|metadata
operator|.
name|RepositoryMetaData
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
name|Strings
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
name|collect
operator|.
name|Tuple
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
name|logging
operator|.
name|DeprecationLogger
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
name|SecureString
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|s3
operator|.
name|S3Repository
operator|.
name|getValue
import|;
end_import

begin_class
DECL|class|InternalAwsS3Service
class|class
name|InternalAwsS3Service
extends|extends
name|AbstractLifecycleComponent
implements|implements
name|AwsS3Service
block|{
comment|// pkg private for tests
DECL|field|CLIENT_NAME
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|CLIENT_NAME
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"client"
argument_list|,
literal|"default"
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * (acceskey, endpoint) -&gt; client      */
DECL|field|clients
specifier|private
name|Map
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|,
name|AmazonS3Client
argument_list|>
name|clients
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|InternalAwsS3Service
name|InternalAwsS3Service
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
block|}
annotation|@
name|Override
DECL|method|client
specifier|public
specifier|synchronized
name|AmazonS3
name|client
parameter_list|(
name|RepositoryMetaData
name|metadata
parameter_list|,
name|Settings
name|repositorySettings
parameter_list|)
block|{
name|String
name|clientName
init|=
name|CLIENT_NAME
operator|.
name|get
argument_list|(
name|repositorySettings
argument_list|)
decl_stmt|;
name|String
name|foundEndpoint
init|=
name|findEndpoint
argument_list|(
name|logger
argument_list|,
name|repositorySettings
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|)
decl_stmt|;
name|AWSCredentialsProvider
name|credentials
init|=
name|buildCredentials
argument_list|(
name|logger
argument_list|,
name|deprecationLogger
argument_list|,
name|settings
argument_list|,
name|repositorySettings
argument_list|,
name|clientName
argument_list|)
decl_stmt|;
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clientDescriptor
init|=
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|foundEndpoint
argument_list|,
name|credentials
operator|.
name|getCredentials
argument_list|()
operator|.
name|getAWSAccessKeyId
argument_list|()
argument_list|)
decl_stmt|;
name|AmazonS3Client
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|clientDescriptor
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
return|return
name|client
return|;
block|}
name|Integer
name|maxRetries
init|=
name|getValue
argument_list|(
name|metadata
operator|.
name|settings
argument_list|()
argument_list|,
name|settings
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|MAX_RETRIES_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|MAX_RETRIES_SETTING
argument_list|)
decl_stmt|;
name|boolean
name|useThrottleRetries
init|=
name|getValue
argument_list|(
name|metadata
operator|.
name|settings
argument_list|()
argument_list|,
name|settings
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|USE_THROTTLE_RETRIES_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|USE_THROTTLE_RETRIES_SETTING
argument_list|)
decl_stmt|;
comment|// If the user defined a path style access setting, we rely on it,
comment|// otherwise we use the default value set by the SDK
name|Boolean
name|pathStyleAccess
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|S3Repository
operator|.
name|Repository
operator|.
name|PATH_STYLE_ACCESS_SETTING
operator|.
name|exists
argument_list|(
name|metadata
operator|.
name|settings
argument_list|()
argument_list|)
operator|||
name|S3Repository
operator|.
name|Repositories
operator|.
name|PATH_STYLE_ACCESS_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|pathStyleAccess
operator|=
name|getValue
argument_list|(
name|metadata
operator|.
name|settings
argument_list|()
argument_list|,
name|settings
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|PATH_STYLE_ACCESS_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|PATH_STYLE_ACCESS_SETTING
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"creating S3 client with client_name [{}], endpoint [{}], max_retries [{}], "
operator|+
literal|"use_throttle_retries [{}], path_style_access [{}]"
argument_list|,
name|clientName
argument_list|,
name|foundEndpoint
argument_list|,
name|maxRetries
argument_list|,
name|useThrottleRetries
argument_list|,
name|pathStyleAccess
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|AmazonS3Client
argument_list|(
name|credentials
argument_list|,
name|buildConfiguration
argument_list|(
name|logger
argument_list|,
name|repositorySettings
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|maxRetries
argument_list|,
name|foundEndpoint
argument_list|,
name|useThrottleRetries
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|pathStyleAccess
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|setS3ClientOptions
argument_list|(
operator|new
name|S3ClientOptions
argument_list|()
operator|.
name|withPathStyleAccess
argument_list|(
name|pathStyleAccess
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundEndpoint
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|client
operator|.
name|setEndpoint
argument_list|(
name|foundEndpoint
argument_list|)
expr_stmt|;
block|}
name|clients
operator|.
name|put
argument_list|(
name|clientDescriptor
argument_list|,
name|client
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
comment|// pkg private for tests
DECL|method|buildConfiguration
specifier|static
name|ClientConfiguration
name|buildConfiguration
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|Settings
name|repositorySettings
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|clientName
parameter_list|,
name|Integer
name|maxRetries
parameter_list|,
name|String
name|endpoint
parameter_list|,
name|boolean
name|useThrottleRetries
parameter_list|)
block|{
name|ClientConfiguration
name|clientConfiguration
init|=
operator|new
name|ClientConfiguration
argument_list|()
decl_stmt|;
comment|// the response metadata cache is only there for diagnostics purposes,
comment|// but can force objects from every response to the old generation.
name|clientConfiguration
operator|.
name|setResponseMetadataCacheSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Protocol
name|protocol
init|=
name|getConfigValue
argument_list|(
name|repositorySettings
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|PROTOCOL_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|PROTOCOL_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|PROTOCOL_SETTING
argument_list|)
decl_stmt|;
name|clientConfiguration
operator|.
name|setProtocol
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|String
name|proxyHost
init|=
name|getConfigValue
argument_list|(
literal|null
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|PROXY_HOST_SETTING
argument_list|,
literal|null
argument_list|,
name|CLOUD_S3
operator|.
name|PROXY_HOST_SETTING
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|proxyHost
argument_list|)
condition|)
block|{
name|Integer
name|proxyPort
init|=
name|getConfigValue
argument_list|(
literal|null
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|PROXY_PORT_SETTING
argument_list|,
literal|null
argument_list|,
name|CLOUD_S3
operator|.
name|PROXY_PORT_SETTING
argument_list|)
decl_stmt|;
try|try
init|(
name|SecureString
name|proxyUsername
init|=
name|getConfigValue
argument_list|(
literal|null
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|PROXY_USERNAME_SETTING
argument_list|,
literal|null
argument_list|,
name|CLOUD_S3
operator|.
name|PROXY_USERNAME_SETTING
argument_list|)
init|;                  SecureString proxyPassword = getConfigValue(null
operator|,
init|settings
operator|,
init|clientName
operator|,
init|S3Repository.PROXY_PASSWORD_SETTING
operator|,
init|null
operator|,
init|CLOUD_S3.PROXY_PASSWORD_SETTING)
block|)
block|{
name|clientConfiguration
operator|.
name|withProxyHost
argument_list|(
name|proxyHost
argument_list|)
operator|.
name|withProxyPort
argument_list|(
name|proxyPort
argument_list|)
operator|.
name|withProxyUsername
argument_list|(
name|proxyUsername
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withProxyPassword
argument_list|(
name|proxyPassword
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxRetries
operator|!=
literal|null
condition|)
block|{
comment|// If not explicitly set, default to 3 with exponential backoff policy
name|clientConfiguration
operator|.
name|setMaxErrorRetry
argument_list|(
name|maxRetries
argument_list|)
expr_stmt|;
block|}
name|clientConfiguration
operator|.
name|setUseThrottleRetries
parameter_list|(
name|useThrottleRetries
parameter_list|)
constructor_decl|;
comment|// #155: we might have 3rd party users using older S3 API version
name|String
name|awsSigner
init|=
name|CLOUD_S3
operator|.
name|SIGNER_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|awsSigner
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"using AWS API signer [{}]"
argument_list|,
name|awsSigner
argument_list|)
expr_stmt|;
name|AwsSigner
operator|.
name|configureSigner
argument_list|(
name|awsSigner
argument_list|,
name|clientConfiguration
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
block|}
name|TimeValue
name|readTimeout
init|=
name|getConfigValue
argument_list|(
literal|null
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|READ_TIMEOUT_SETTING
argument_list|,
literal|null
argument_list|,
name|CLOUD_S3
operator|.
name|READ_TIMEOUT
argument_list|)
decl_stmt|;
name|clientConfiguration
operator|.
name|setSocketTimeout
argument_list|(
operator|(
name|int
operator|)
name|readTimeout
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|clientConfiguration
return|;
block|}
end_class

begin_function
DECL|method|buildCredentials
specifier|public
specifier|static
name|AWSCredentialsProvider
name|buildCredentials
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|DeprecationLogger
name|deprecationLogger
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Settings
name|repositorySettings
parameter_list|,
name|String
name|clientName
parameter_list|)
block|{
try|try
init|(
name|SecureString
name|key
init|=
name|getConfigValue
argument_list|(
name|repositorySettings
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|ACCESS_KEY_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|KEY_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|KEY_SETTING
argument_list|)
init|;
name|SecureString
name|secret
operator|=
name|getConfigValue
argument_list|(
name|repositorySettings
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|SECRET_KEY_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|SECRET_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|SECRET_SETTING
argument_list|)
init|)
block|{
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|secret
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Using instance profile credentials"
argument_list|)
expr_stmt|;
return|return
operator|new
name|PrivilegedInstanceProfileCredentialsProvider
argument_list|()
return|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Using basic key/secret credentials"
argument_list|)
expr_stmt|;
return|return
operator|new
name|StaticCredentialsProvider
argument_list|(
operator|new
name|BasicAWSCredentials
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|secret
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_function

begin_comment
comment|// pkg private for tests
end_comment

begin_comment
comment|/** Returns the endpoint the client should use, based on the available endpoint settings found. */
end_comment

begin_function
DECL|method|findEndpoint
specifier|static
name|String
name|findEndpoint
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|Settings
name|repositorySettings
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|clientName
parameter_list|)
block|{
name|String
name|endpoint
init|=
name|getConfigValue
argument_list|(
name|repositorySettings
argument_list|,
name|settings
argument_list|,
name|clientName
argument_list|,
name|S3Repository
operator|.
name|ENDPOINT_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repository
operator|.
name|ENDPOINT_SETTING
argument_list|,
name|S3Repository
operator|.
name|Repositories
operator|.
name|ENDPOINT_SETTING
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|endpoint
argument_list|)
condition|)
block|{
comment|// No region has been set so we will use the default endpoint
if|if
condition|(
name|CLOUD_S3
operator|.
name|ENDPOINT_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|endpoint
operator|=
name|CLOUD_S3
operator|.
name|ENDPOINT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using explicit s3 endpoint [{}]"
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"using repository level endpoint [{}]"
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
block|}
return|return
name|endpoint
return|;
block|}
end_function

begin_comment
comment|/**      * Find the setting value, trying first with named configs,      * then falling back to repository and global repositories settings.      */
end_comment

begin_function
DECL|method|getConfigValue
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfigValue
parameter_list|(
name|Settings
name|repositorySettings
parameter_list|,
name|Settings
name|globalSettings
parameter_list|,
name|String
name|clientName
parameter_list|,
name|Setting
operator|.
name|AffixSetting
argument_list|<
name|T
argument_list|>
name|configSetting
parameter_list|,
name|Setting
argument_list|<
name|T
argument_list|>
name|repositorySetting
parameter_list|,
name|Setting
argument_list|<
name|T
argument_list|>
name|globalSetting
parameter_list|)
block|{
name|Setting
argument_list|<
name|T
argument_list|>
name|concreteSetting
init|=
name|configSetting
operator|.
name|getConcreteSettingForNamespace
argument_list|(
name|clientName
argument_list|)
decl_stmt|;
if|if
condition|(
name|concreteSetting
operator|.
name|exists
argument_list|(
name|globalSettings
argument_list|)
condition|)
block|{
return|return
name|concreteSetting
operator|.
name|get
argument_list|(
name|globalSettings
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|repositorySetting
operator|==
literal|null
condition|)
block|{
comment|// no repository setting, just use global setting
return|return
name|globalSetting
operator|.
name|get
argument_list|(
name|globalSettings
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|globalSettings
argument_list|,
name|repositorySetting
argument_list|,
name|globalSetting
argument_list|)
return|;
block|}
block|}
end_function

begin_function
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
end_function

begin_function
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
end_function

begin_function
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
for|for
control|(
name|AmazonS3Client
name|client
range|:
name|clients
operator|.
name|values
argument_list|()
control|)
block|{
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// Ensure that IdleConnectionReaper is shutdown
name|IdleConnectionReaper
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
end_function

begin_class
DECL|class|PrivilegedInstanceProfileCredentialsProvider
specifier|static
class|class
name|PrivilegedInstanceProfileCredentialsProvider
implements|implements
name|AWSCredentialsProvider
block|{
DECL|field|credentials
specifier|private
specifier|final
name|InstanceProfileCredentialsProvider
name|credentials
decl_stmt|;
DECL|method|PrivilegedInstanceProfileCredentialsProvider
specifier|private
name|PrivilegedInstanceProfileCredentialsProvider
parameter_list|()
block|{
name|this
operator|.
name|credentials
operator|=
operator|new
name|InstanceProfileCredentialsProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCredentials
specifier|public
name|AWSCredentials
name|getCredentials
parameter_list|()
block|{
return|return
name|SocketAccess
operator|.
name|doPrivileged
argument_list|(
name|credentials
operator|::
name|getCredentials
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|refresh
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|SocketAccess
operator|.
name|doPrivilegedVoid
argument_list|(
name|credentials
operator|::
name|refresh
argument_list|)
expr_stmt|;
block|}
block|}
end_class

unit|}
end_unit

