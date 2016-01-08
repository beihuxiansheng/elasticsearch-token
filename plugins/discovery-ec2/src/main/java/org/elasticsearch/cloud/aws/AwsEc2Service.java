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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|LifecycleComponent
import|;
end_import

begin_interface
DECL|interface|AwsEc2Service
specifier|public
interface|interface
name|AwsEc2Service
extends|extends
name|LifecycleComponent
argument_list|<
name|AwsEc2Service
argument_list|>
block|{
DECL|class|CLOUD_AWS
specifier|final
class|class
name|CLOUD_AWS
block|{
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"cloud.aws.access_key"
decl_stmt|;
DECL|field|SECRET
specifier|public
specifier|static
specifier|final
name|String
name|SECRET
init|=
literal|"cloud.aws.secret_key"
decl_stmt|;
DECL|field|PROTOCOL
specifier|public
specifier|static
specifier|final
name|String
name|PROTOCOL
init|=
literal|"cloud.aws.protocol"
decl_stmt|;
DECL|field|PROXY_HOST
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST
init|=
literal|"cloud.aws.proxy.host"
decl_stmt|;
DECL|field|PROXY_PORT
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PORT
init|=
literal|"cloud.aws.proxy.port"
decl_stmt|;
DECL|field|PROXY_USERNAME
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_USERNAME
init|=
literal|"cloud.aws.proxy.username"
decl_stmt|;
DECL|field|PROXY_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PASSWORD
init|=
literal|"cloud.aws.proxy.password"
decl_stmt|;
DECL|field|SIGNER
specifier|public
specifier|static
specifier|final
name|String
name|SIGNER
init|=
literal|"cloud.aws.signer"
decl_stmt|;
DECL|field|REGION
specifier|public
specifier|static
specifier|final
name|String
name|REGION
init|=
literal|"cloud.aws.region"
decl_stmt|;
block|}
DECL|class|CLOUD_EC2
specifier|final
class|class
name|CLOUD_EC2
block|{
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"cloud.aws.ec2.access_key"
decl_stmt|;
DECL|field|SECRET
specifier|public
specifier|static
specifier|final
name|String
name|SECRET
init|=
literal|"cloud.aws.ec2.secret_key"
decl_stmt|;
DECL|field|PROTOCOL
specifier|public
specifier|static
specifier|final
name|String
name|PROTOCOL
init|=
literal|"cloud.aws.ec2.protocol"
decl_stmt|;
DECL|field|PROXY_HOST
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST
init|=
literal|"cloud.aws.ec2.proxy.host"
decl_stmt|;
DECL|field|PROXY_PORT
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PORT
init|=
literal|"cloud.aws.ec2.proxy.port"
decl_stmt|;
DECL|field|PROXY_USERNAME
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_USERNAME
init|=
literal|"cloud.aws.ec2.proxy.username"
decl_stmt|;
DECL|field|PROXY_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PASSWORD
init|=
literal|"cloud.aws.ec2.proxy.password"
decl_stmt|;
DECL|field|SIGNER
specifier|public
specifier|static
specifier|final
name|String
name|SIGNER
init|=
literal|"cloud.aws.ec2.signer"
decl_stmt|;
DECL|field|ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|ENDPOINT
init|=
literal|"cloud.aws.ec2.endpoint"
decl_stmt|;
block|}
DECL|class|DISCOVERY_EC2
specifier|final
class|class
name|DISCOVERY_EC2
block|{
DECL|field|HOST_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|HOST_TYPE
init|=
literal|"discovery.ec2.host_type"
decl_stmt|;
DECL|field|ANY_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|ANY_GROUP
init|=
literal|"discovery.ec2.any_group"
decl_stmt|;
DECL|field|GROUPS
specifier|public
specifier|static
specifier|final
name|String
name|GROUPS
init|=
literal|"discovery.ec2.groups"
decl_stmt|;
DECL|field|TAG_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TAG_PREFIX
init|=
literal|"discovery.ec2.tag."
decl_stmt|;
DECL|field|AVAILABILITY_ZONES
specifier|public
specifier|static
specifier|final
name|String
name|AVAILABILITY_ZONES
init|=
literal|"discovery.ec2.availability_zones"
decl_stmt|;
DECL|field|NODE_CACHE_TIME
specifier|public
specifier|static
specifier|final
name|String
name|NODE_CACHE_TIME
init|=
literal|"discovery.ec2.node_cache_time"
decl_stmt|;
block|}
DECL|method|client
name|AmazonEC2
name|client
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

