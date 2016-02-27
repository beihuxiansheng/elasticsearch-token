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
name|com
operator|.
name|amazonaws
operator|.
name|Protocol
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
name|AwsS3Service
operator|.
name|CLOUD_S3
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
name|Setting
operator|.
name|SettingsProperty
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

begin_comment
comment|/**  * Shared file system implementation of the BlobStoreRepository  *<p>  * Shared file system repository supports the following settings  *<dl>  *<dt>{@code bucket}</dt><dd>S3 bucket</dd>  *<dt>{@code region}</dt><dd>S3 region. Defaults to us-east</dd>  *<dt>{@code base_path}</dt><dd>Specifies the path within bucket to repository data. Defaults to root directory.</dd>  *<dt>{@code concurrent_streams}</dt><dd>Number of concurrent read/write stream (per repository on each node). Defaults to 5.</dd>  *<dt>{@code chunk_size}</dt><dd>Large file can be divided into chunks. This parameter specifies the chunk size. Defaults to not chucked.</dd>  *<dt>{@code compress}</dt><dd>If set to true metadata files will be stored compressed. Defaults to false.</dd>  *</dl>  */
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
comment|/**      * Global S3 repositories settings. Starting with: repositories.s3      */
DECL|interface|Repositories
specifier|public
interface|interface
name|Repositories
block|{
comment|/**          * repositories.s3.access_key: AWS Access key specific for all S3 Repositories API calls. Defaults to cloud.aws.s3.access_key.          * @see CLOUD_S3#KEY_SETTING          */
DECL|field|KEY_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|KEY_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"repositories.s3.access_key"
argument_list|,
name|CLOUD_S3
operator|.
name|KEY_SETTING
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.secret_key: AWS Secret key specific for all S3 Repositories API calls. Defaults to cloud.aws.s3.secret_key.          * @see CLOUD_S3#SECRET_SETTING          */
DECL|field|SECRET_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|SECRET_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"repositories.s3.secret_key"
argument_list|,
name|CLOUD_S3
operator|.
name|SECRET_SETTING
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.region: Region specific for all S3 Repositories API calls. Defaults to cloud.aws.s3.region.          * @see CLOUD_S3#REGION_SETTING          */
DECL|field|REGION_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|REGION_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"repositories.s3.region"
argument_list|,
name|CLOUD_S3
operator|.
name|REGION_SETTING
argument_list|,
name|s
lambda|->
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.endpoint: Endpoint specific for all S3 Repositories API calls. Defaults to cloud.aws.s3.endpoint.          * @see CLOUD_S3#ENDPOINT_SETTING          */
DECL|field|ENDPOINT_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|ENDPOINT_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"repositories.s3.endpoint"
argument_list|,
name|CLOUD_S3
operator|.
name|ENDPOINT_SETTING
argument_list|,
name|s
lambda|->
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.protocol: Protocol specific for all S3 Repositories API calls. Defaults to cloud.aws.s3.protocol.          * @see CLOUD_S3#PROTOCOL_SETTING          */
DECL|field|PROTOCOL_SETTING
name|Setting
argument_list|<
name|Protocol
argument_list|>
name|PROTOCOL_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"repositories.s3.protocol"
argument_list|,
name|CLOUD_S3
operator|.
name|PROTOCOL_SETTING
argument_list|,
name|s
lambda|->
name|Protocol
operator|.
name|valueOf
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.bucket: The name of the bucket to be used for snapshots.          */
DECL|field|BUCKET_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|BUCKET_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"repositories.s3.bucket"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.server_side_encryption: When set to true files are encrypted on server side using AES256 algorithm.          * Defaults to false.          */
DECL|field|SERVER_SIDE_ENCRYPTION_SETTING
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|SERVER_SIDE_ENCRYPTION_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"repositories.s3.server_side_encryption"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.buffer_size: Minimum threshold below which the chunk is uploaded using a single request. Beyond this threshold,          * the S3 repository will use the AWS Multipart Upload API to split the chunk into several parts, each of buffer_size length, and          * to upload each part in its own request. Note that setting a buffer size lower than 5mb is not allowed since it will prevents the          * use of the Multipart API and may result in upload errors. Defaults to 5mb.          */
DECL|field|BUFFER_SIZE_SETTING
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|BUFFER_SIZE_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"repositories.s3.buffer_size"
argument_list|,
name|S3BlobStore
operator|.
name|MIN_BUFFER_SIZE
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.max_retries: Number of retries in case of S3 errors. Defaults to 3.          */
DECL|field|MAX_RETRIES_SETTING
name|Setting
argument_list|<
name|Integer
argument_list|>
name|MAX_RETRIES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"repositories.s3.max_retries"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.chunk_size: Big files can be broken down into chunks during snapshotting if needed. Defaults to 100m.          */
DECL|field|CHUNK_SIZE_SETTING
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|CHUNK_SIZE_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"repositories.s3.chunk_size"
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
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.compress: When set to true metadata files are stored in compressed format. This setting doesnât affect index          * files that are already compressed by default. Defaults to false.          */
DECL|field|COMPRESS_SETTING
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|COMPRESS_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"repositories.s3.compress"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.storage_class: Sets the S3 storage class type for the backup files. Values may be standard, reduced_redundancy,          * standard_ia. Defaults to standard.          */
DECL|field|STORAGE_CLASS_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|STORAGE_CLASS_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"repositories.s3.storage_class"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.canned_acl: The S3 repository supports all S3 canned ACLs : private, public-read, public-read-write,          * authenticated-read, log-delivery-write, bucket-owner-read, bucket-owner-full-control. Defaults to private.          */
DECL|field|CANNED_ACL_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|CANNED_ACL_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"repositories.s3.canned_acl"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * repositories.s3.base_path: Specifies the path within bucket to repository data. Defaults to root directory.          */
DECL|field|BASE_PATH_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|BASE_PATH_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"repositories.s3.base_path"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
block|}
comment|/**      * Per S3 repository specific settings. Same settings as Repositories settings but without the repositories.s3 prefix.      * If undefined, they use the repositories.s3.xxx equivalent setting.      */
DECL|interface|Repository
specifier|public
interface|interface
name|Repository
block|{
comment|/**          * access_key          * @see  Repositories#KEY_SETTING          */
DECL|field|KEY_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|KEY_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"access_key"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|,
name|SettingsProperty
operator|.
name|Filtered
argument_list|)
decl_stmt|;
comment|/**          * secret_key          * @see  Repositories#SECRET_SETTING          */
DECL|field|SECRET_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|SECRET_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"secret_key"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|,
name|SettingsProperty
operator|.
name|Filtered
argument_list|)
decl_stmt|;
comment|/**          * bucket          * @see  Repositories#BUCKET_SETTING          */
DECL|field|BUCKET_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|BUCKET_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"bucket"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * endpoint          * @see  Repositories#ENDPOINT_SETTING          */
DECL|field|ENDPOINT_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|ENDPOINT_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"endpoint"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * protocol          * @see  Repositories#PROTOCOL_SETTING          */
DECL|field|PROTOCOL_SETTING
name|Setting
argument_list|<
name|Protocol
argument_list|>
name|PROTOCOL_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"protocol"
argument_list|,
literal|"https"
argument_list|,
name|s
lambda|->
name|Protocol
operator|.
name|valueOf
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * region          * @see  Repositories#REGION_SETTING          */
DECL|field|REGION_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|REGION_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"region"
argument_list|,
literal|""
argument_list|,
name|s
lambda|->
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * server_side_encryption          * @see  Repositories#SERVER_SIDE_ENCRYPTION_SETTING          */
DECL|field|SERVER_SIDE_ENCRYPTION_SETTING
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|SERVER_SIDE_ENCRYPTION_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"server_side_encryption"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * buffer_size          * @see  Repositories#BUFFER_SIZE_SETTING          */
DECL|field|BUFFER_SIZE_SETTING
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|BUFFER_SIZE_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"buffer_size"
argument_list|,
name|S3BlobStore
operator|.
name|MIN_BUFFER_SIZE
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * max_retries          * @see  Repositories#MAX_RETRIES_SETTING          */
DECL|field|MAX_RETRIES_SETTING
name|Setting
argument_list|<
name|Integer
argument_list|>
name|MAX_RETRIES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"max_retries"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * chunk_size          * @see  Repositories#CHUNK_SIZE_SETTING          */
DECL|field|CHUNK_SIZE_SETTING
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|CHUNK_SIZE_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"chunk_size"
argument_list|,
literal|"-1"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * compress          * @see  Repositories#COMPRESS_SETTING          */
DECL|field|COMPRESS_SETTING
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|COMPRESS_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"compress"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * storage_class          * @see  Repositories#STORAGE_CLASS_SETTING          */
DECL|field|STORAGE_CLASS_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|STORAGE_CLASS_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"storage_class"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * canned_acl          * @see  Repositories#CANNED_ACL_SETTING          */
DECL|field|CANNED_ACL_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|CANNED_ACL_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"canned_acl"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
comment|/**          * base_path          * @see  Repositories#BASE_PATH_SETTING          */
DECL|field|BASE_PATH_SETTING
name|Setting
argument_list|<
name|String
argument_list|>
name|BASE_PATH_SETTING
init|=
name|Setting
operator|.
name|simpleString
argument_list|(
literal|"base_path"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
block|}
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
comment|/**      * Constructs new shared file system repository      *      * @param name                 repository name      * @param repositorySettings   repository settings      * @param indexShardRepository index shard repository      * @param s3Service            S3 service      */
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
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|BUCKET_SETTING
argument_list|,
name|Repositories
operator|.
name|BUCKET_SETTING
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
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|ENDPOINT_SETTING
argument_list|,
name|Repositories
operator|.
name|ENDPOINT_SETTING
argument_list|)
decl_stmt|;
name|Protocol
name|protocol
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|PROTOCOL_SETTING
argument_list|,
name|Repositories
operator|.
name|PROTOCOL_SETTING
argument_list|)
decl_stmt|;
name|String
name|region
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|REGION_SETTING
argument_list|,
name|Repositories
operator|.
name|REGION_SETTING
argument_list|)
decl_stmt|;
comment|// If no region is defined either in region, repositories.s3.region, cloud.aws.s3.region or cloud.aws.region
comment|// we fallback to Default bucket - null
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|region
argument_list|)
condition|)
block|{
name|region
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|serverSideEncryption
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|SERVER_SIDE_ENCRYPTION_SETTING
argument_list|,
name|Repositories
operator|.
name|SERVER_SIDE_ENCRYPTION_SETTING
argument_list|)
decl_stmt|;
name|ByteSizeValue
name|bufferSize
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|BUFFER_SIZE_SETTING
argument_list|,
name|Repositories
operator|.
name|BUFFER_SIZE_SETTING
argument_list|)
decl_stmt|;
name|Integer
name|maxRetries
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|MAX_RETRIES_SETTING
argument_list|,
name|Repositories
operator|.
name|MAX_RETRIES_SETTING
argument_list|)
decl_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|CHUNK_SIZE_SETTING
argument_list|,
name|Repositories
operator|.
name|CHUNK_SIZE_SETTING
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|COMPRESS_SETTING
argument_list|,
name|Repositories
operator|.
name|COMPRESS_SETTING
argument_list|)
expr_stmt|;
comment|// Parse and validate the user's S3 Storage Class setting
name|String
name|storageClass
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|STORAGE_CLASS_SETTING
argument_list|,
name|Repositories
operator|.
name|STORAGE_CLASS_SETTING
argument_list|)
decl_stmt|;
name|String
name|cannedACL
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|CANNED_ACL_SETTING
argument_list|,
name|Repositories
operator|.
name|CANNED_ACL_SETTING
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using bucket [{}], region [{}], endpoint [{}], protocol [{}], chunk_size [{}], server_side_encryption [{}], buffer_size [{}], max_retries [{}], cannedACL [{}], storageClass [{}]"
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
argument_list|,
name|cannedACL
argument_list|,
name|storageClass
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|KEY_SETTING
argument_list|,
name|Repositories
operator|.
name|KEY_SETTING
argument_list|)
decl_stmt|;
name|String
name|secret
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|SECRET_SETTING
argument_list|,
name|Repositories
operator|.
name|SECRET_SETTING
argument_list|)
decl_stmt|;
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
name|key
argument_list|,
name|secret
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
argument_list|,
name|cannedACL
argument_list|,
name|storageClass
argument_list|)
expr_stmt|;
name|String
name|basePath
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|BASE_PATH_SETTING
argument_list|,
name|Repositories
operator|.
name|BASE_PATH_SETTING
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
DECL|method|getValue
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|RepositorySettings
name|repositorySettings
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
name|repositoriesSetting
parameter_list|)
block|{
if|if
condition|(
name|repositorySetting
operator|.
name|exists
argument_list|(
name|repositorySettings
operator|.
name|settings
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|repositorySetting
operator|.
name|get
argument_list|(
name|repositorySettings
operator|.
name|settings
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|repositoriesSetting
operator|.
name|get
argument_list|(
name|repositorySettings
operator|.
name|globalSettings
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

