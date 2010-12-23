begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.s3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|s3
package|;
end_package

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
name|cloud
operator|.
name|aws
operator|.
name|AwsS3Service
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
name|aws
operator|.
name|blobstore
operator|.
name|S3BlobStore
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
name|ClusterName
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
name|ClusterService
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
name|MetaDataCreateIndexService
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
name|ByteSizeUnit
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
name|ByteSizeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|blobstore
operator|.
name|BlobStoreGateway
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
name|gateway
operator|.
name|s3
operator|.
name|S3IndexGatewayModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|S3Gateway
specifier|public
class|class
name|S3Gateway
extends|extends
name|BlobStoreGateway
block|{
DECL|method|S3Gateway
annotation|@
name|Inject
specifier|public
name|S3Gateway
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|MetaDataCreateIndexService
name|createIndexService
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|AwsS3Service
name|s3Service
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|createIndexService
argument_list|)
expr_stmt|;
name|String
name|bucket
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"bucket"
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucket
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No bucket defined for s3 gateway"
argument_list|)
throw|;
block|}
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
name|region
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.aws.region"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|regionSetting
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.aws.region"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"us-east"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"us-east-1"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"us-west"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"us-west-1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"us-west-1"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"us-west-1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ap-southeast"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"ap-southeast-1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ap-southeast-1"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"ap-southeast-1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"eu-west"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"EU"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"eu-west-1"
operator|.
name|equals
argument_list|(
name|regionSetting
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"EU"
expr_stmt|;
block|}
block|}
block|}
name|ByteSizeValue
name|chunkSize
init|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|100
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using bucket [{}], region [{}], chunk_size [{}]"
argument_list|,
name|bucket
argument_list|,
name|region
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
name|initialize
argument_list|(
operator|new
name|S3BlobStore
argument_list|(
name|settings
argument_list|,
name|s3Service
operator|.
name|client
argument_list|()
argument_list|,
name|bucket
argument_list|,
name|region
argument_list|,
name|threadPool
operator|.
name|cached
argument_list|()
argument_list|)
argument_list|,
name|clusterName
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|type
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"s3"
return|;
block|}
DECL|method|suggestIndexGateway
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|suggestIndexGateway
parameter_list|()
block|{
return|return
name|S3IndexGatewayModule
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

