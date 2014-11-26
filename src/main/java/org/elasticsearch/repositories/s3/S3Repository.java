begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|blobstore
operator|.
name|BlobPath
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
name|blobstore
operator|.
name|BlobStore
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
name|index
operator|.
name|snapshots
operator|.
name|IndexShardRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoryName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositorySettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|blobstore
operator|.
name|BlobStoreRepository
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * Shared file system implementation of the BlobStoreRepository  *<p/>  * Shared file system repository supports the following settings  *<dl>  *<dt>{@code bucket}</dt><dd>S3 bucket</dd>  *<dt>{@code region}</dt><dd>S3 region. Defaults to us-east</dd>  *<dt>{@code base_path}</dt><dd>Specifies the path within bucket to repository data. Defaults to root directory.</dd>  *<dt>{@code concurrent_streams}</dt><dd>Number of concurrent read/write stream (per repository on each node). Defaults to 5.</dd>  *<dt>{@code chunk_size}</dt><dd>Large file can be divided into chunks. This parameter specifies the chunk size. Defaults to not chucked.</dd>  *<dt>{@code compress}</dt><dd>If set to true metadata files will be stored compressed. Defaults to false.</dd>  *</dl>  */
end_comment

begin_class
DECL|class|S3Repository
specifier|public
class|class
name|S3Repository
extends|extends
name|BlobStoreRepository
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|String
name|TYPE
init|=
literal|"s3"
decl_stmt|;
DECL|field|blobStore
specifier|private
specifier|final
name|S3BlobStore
name|blobStore
decl_stmt|;
DECL|field|basePath
specifier|private
specifier|final
name|BlobPath
name|basePath
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|ByteSizeValue
name|chunkSize
decl_stmt|;
DECL|field|compress
specifier|private
name|boolean
name|compress
decl_stmt|;
comment|/**      * Constructs new shared file system repository      *      * @param name                 repository name      * @param repositorySettings   repository settings      * @param indexShardRepository index shard repository      * @param s3Service            S3 service      * @throws IOException      */
annotation|@
name|Inject
DECL|method|S3Repository
specifier|public
name|S3Repository
parameter_list|(
name|RepositoryName
name|name
parameter_list|,
name|RepositorySettings
name|repositorySettings
parameter_list|,
name|IndexShardRepository
name|indexShardRepository
parameter_list|,
name|AwsS3Service
name|s3Service
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
operator|.
name|getName
argument_list|()
argument_list|,
name|repositorySettings
argument_list|,
name|indexShardRepository
argument_list|)
expr_stmt|;
name|String
name|bucket
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"bucket"
argument_list|,
name|componentSettings
operator|.
name|get
argument_list|(
literal|"bucket"
argument_list|)
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
name|RepositoryException
argument_list|(
name|name
operator|.
name|name
argument_list|()
argument_list|,
literal|"No bucket defined for s3 gateway"
argument_list|)
throw|;
block|}
name|String
name|endpoint
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"endpoint"
argument_list|,
name|componentSettings
operator|.
name|get
argument_list|(
literal|"endpoint"
argument_list|)
argument_list|)
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
literal|"https"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|protocol
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"s3.protocol"
argument_list|,
name|protocol
argument_list|)
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|protocol
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"protocol"
argument_list|,
name|protocol
argument_list|)
expr_stmt|;
name|String
name|region
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"region"
argument_list|,
name|componentSettings
operator|.
name|get
argument_list|(
literal|"region"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|region
operator|==
literal|null
condition|)
block|{
comment|// Bucket setting is not set - use global region setting
name|String
name|regionSetting
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"cloud.aws.region"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.aws.region"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|regionSetting
operator|!=
literal|null
condition|)
block|{
name|regionSetting
operator|=
name|regionSetting
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"us-east"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
comment|// Default bucket - setting region to null
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
literal|"us-west-2"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"us-west-2"
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
literal|"ap-southeast-2"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"ap-southeast-2"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ap-northeast"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"ap-northeast-1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ap-northeast-1"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"ap-northeast-1"
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
literal|"sa-east"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"sa-east-1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sa-east-1"
operator|.
name|equals
argument_list|(
name|regionSetting
argument_list|)
condition|)
block|{
name|region
operator|=
literal|"sa-east-1"
expr_stmt|;
block|}
block|}
block|}
name|boolean
name|serverSideEncryption
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"server_side_encryption"
argument_list|,
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"server_side_encryption"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|ByteSizeValue
name|bufferSize
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBytesSize
argument_list|(
literal|"buffer_size"
argument_list|,
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"buffer_size"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|maxRetries
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsInt
argument_list|(
literal|"max_retries"
argument_list|,
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"max_retries"
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using bucket [{}], region [{}], endpoint [{}], protocol [{}], chunk_size [{}], server_side_encryption [{}], buffer_size [{}], max_retries [{}]"
argument_list|,
name|bucket
argument_list|,
name|region
argument_list|,
name|endpoint
argument_list|,
name|protocol
argument_list|,
name|chunkSize
argument_list|,
name|serverSideEncryption
argument_list|,
name|bufferSize
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|blobStore
operator|=
operator|new
name|S3BlobStore
argument_list|(
name|settings
argument_list|,
name|s3Service
operator|.
name|client
argument_list|(
name|endpoint
argument_list|,
name|protocol
argument_list|,
name|region
argument_list|,
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"access_key"
argument_list|)
argument_list|,
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"secret_key"
argument_list|)
argument_list|,
name|maxRetries
argument_list|)
argument_list|,
name|bucket
argument_list|,
name|region
argument_list|,
name|serverSideEncryption
argument_list|,
name|bufferSize
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|String
name|basePath
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"base_path"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|basePath
argument_list|)
condition|)
block|{
name|BlobPath
name|path
init|=
operator|new
name|BlobPath
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|elem
range|:
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|basePath
argument_list|,
literal|'/'
argument_list|)
control|)
block|{
name|path
operator|=
name|path
operator|.
name|add
argument_list|(
name|elem
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|basePath
operator|=
name|path
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|basePath
operator|=
name|BlobPath
operator|.
name|cleanPath
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|blobStore
specifier|protected
name|BlobStore
name|blobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
annotation|@
name|Override
DECL|method|basePath
specifier|protected
name|BlobPath
name|basePath
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|isCompress
specifier|protected
name|boolean
name|isCompress
parameter_list|()
block|{
return|return
name|compress
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|chunkSize
specifier|protected
name|ByteSizeValue
name|chunkSize
parameter_list|()
block|{
return|return
name|chunkSize
return|;
block|}
block|}
end_class

end_unit

