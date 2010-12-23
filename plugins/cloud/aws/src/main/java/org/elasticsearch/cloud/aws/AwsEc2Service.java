begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|BasicAWSCredentials
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
name|ec2
operator|.
name|AmazonEC2
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
name|ec2
operator|.
name|AmazonEC2Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|SettingsFilter
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AwsEc2Service
specifier|public
class|class
name|AwsEc2Service
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|AwsEc2Service
argument_list|>
block|{
DECL|field|client
specifier|private
name|AmazonEC2Client
name|client
decl_stmt|;
DECL|method|AwsEc2Service
annotation|@
name|Inject
specifier|public
name|AwsEc2Service
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|SettingsFilter
name|settingsFilter
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|settingsFilter
operator|.
name|addFilter
argument_list|(
operator|new
name|AwsSettingsFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|client
specifier|public
specifier|synchronized
name|AmazonEC2
name|client
parameter_list|()
block|{
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
name|ClientConfiguration
name|clientConfiguration
init|=
operator|new
name|ClientConfiguration
argument_list|()
decl_stmt|;
name|String
name|protocol
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"protocol"
argument_list|,
literal|"http"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"http"
operator|.
name|equals
argument_list|(
name|protocol
argument_list|)
condition|)
block|{
name|clientConfiguration
operator|.
name|setProtocol
argument_list|(
name|Protocol
operator|.
name|HTTP
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"https"
operator|.
name|equals
argument_list|(
name|protocol
argument_list|)
condition|)
block|{
name|clientConfiguration
operator|.
name|setProtocol
argument_list|(
name|Protocol
operator|.
name|HTTPS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No protocol supported ["
operator|+
name|protocol
operator|+
literal|"], can either be [http] or [https]"
argument_list|)
throw|;
block|}
name|String
name|account
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"access_key"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.account"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"secret_key"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.key"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No s3 access_key defined for s3 gateway"
argument_list|)
throw|;
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No s3 secret_key defined for s3 gateway"
argument_list|)
throw|;
block|}
name|this
operator|.
name|client
operator|=
operator|new
name|AmazonEC2Client
argument_list|(
operator|new
name|BasicAWSCredentials
argument_list|(
name|account
argument_list|,
name|key
argument_list|)
argument_list|,
name|clientConfiguration
argument_list|)
expr_stmt|;
if|if
condition|(
name|componentSettings
operator|.
name|get
argument_list|(
literal|"ec2.endpoint"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|setEndpoint
argument_list|(
name|componentSettings
operator|.
name|get
argument_list|(
literal|"ec2.endpoint"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|componentSettings
operator|.
name|get
argument_list|(
literal|"region"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|endpoint
decl_stmt|;
name|String
name|region
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"region"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"us-east"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.us-east-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"us-east-1"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.us-east-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"us-west"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.us-west-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"us-west-1"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.us-west-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ap-southeast"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.ap-southeast-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ap-southeast-1"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.ap-southeast-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"eu-west"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.eu-west-1.amazonaws.com"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"eu-west-1"
operator|.
name|equals
argument_list|(
name|region
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|endpoint
operator|=
literal|"ec2.eu-west-1.amazonaws.com"
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No automatic endpoint could be derived from region ["
operator|+
name|region
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|client
operator|.
name|setEndpoint
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|client
return|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

