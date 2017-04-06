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
name|services
operator|.
name|s3
operator|.
name|AbstractAmazonS3
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
name|common
operator|.
name|xcontent
operator|.
name|NamedXContentRegistry
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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|Repositories
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
name|Repository
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

begin_class
DECL|class|S3RepositoryTests
specifier|public
class|class
name|S3RepositoryTests
extends|extends
name|ESTestCase
block|{
DECL|class|DummyS3Client
specifier|private
specifier|static
class|class
name|DummyS3Client
extends|extends
name|AbstractAmazonS3
block|{
annotation|@
name|Override
DECL|method|doesBucketExist
specifier|public
name|boolean
name|doesBucketExist
parameter_list|(
name|String
name|bucketName
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|class|DummyS3Service
specifier|private
specifier|static
class|class
name|DummyS3Service
extends|extends
name|AbstractLifecycleComponent
implements|implements
name|AwsS3Service
block|{
DECL|method|DummyS3Service
name|DummyS3Service
parameter_list|()
block|{
name|super
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|client
specifier|public
name|AmazonS3
name|client
parameter_list|(
name|RepositoryMetaData
name|metadata
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
return|return
operator|new
name|DummyS3Client
argument_list|()
return|;
block|}
block|}
DECL|method|testSettingsResolution
specifier|public
name|void
name|testSettingsResolution
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|localSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Repository
operator|.
name|KEY_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"key1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
name|globalSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Repositories
operator|.
name|KEY_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"key2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SecureString
argument_list|(
literal|"key1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
name|getValue
argument_list|(
name|localSettings
argument_list|,
name|globalSettings
argument_list|,
name|Repository
operator|.
name|KEY_SETTING
argument_list|,
name|Repositories
operator|.
name|KEY_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SecureString
argument_list|(
literal|"key1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
name|getValue
argument_list|(
name|localSettings
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Repository
operator|.
name|KEY_SETTING
argument_list|,
name|Repositories
operator|.
name|KEY_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SecureString
argument_list|(
literal|"key2"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
name|getValue
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|globalSettings
argument_list|,
name|Repository
operator|.
name|KEY_SETTING
argument_list|,
name|Repositories
operator|.
name|KEY_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SecureString
argument_list|(
literal|""
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
name|getValue
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Repository
operator|.
name|KEY_SETTING
argument_list|,
name|Repositories
operator|.
name|KEY_SETTING
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
name|Repository
operator|.
name|KEY_SETTING
operator|,
name|Repositories
operator|.
name|KEY_SETTING
block|}
block|)
function|;
block|}
end_class

begin_function
DECL|method|testInvalidChunkBufferSizeSettings
specifier|public
name|void
name|testInvalidChunkBufferSizeSettings
parameter_list|()
throws|throws
name|IOException
block|{
comment|// chunk< buffer should fail
name|assertInvalidBuffer
argument_list|(
literal|10
argument_list|,
literal|5
argument_list|,
name|RepositoryException
operator|.
name|class
argument_list|,
literal|"chunk_size (5mb) can't be lower than buffer_size (10mb)."
argument_list|)
expr_stmt|;
comment|// chunk> buffer should pass
name|assertValidBuffer
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// chunk = buffer should pass
name|assertValidBuffer
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// buffer< 5mb should fail
name|assertInvalidBuffer
argument_list|(
literal|4
argument_list|,
literal|10
argument_list|,
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"Failed to parse value [4mb] for setting [buffer_size] must be>= 5mb"
argument_list|)
expr_stmt|;
comment|// chunk> 5tb should fail
name|assertInvalidBuffer
argument_list|(
literal|5
argument_list|,
literal|6000000
argument_list|,
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"Failed to parse value [5.7tb] for setting [chunk_size] must be<= 5tb"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|assertValidBuffer
specifier|private
name|void
name|assertValidBuffer
parameter_list|(
name|long
name|bufferMB
parameter_list|,
name|long
name|chunkMB
parameter_list|)
throws|throws
name|IOException
block|{
name|RepositoryMetaData
name|metadata
init|=
operator|new
name|RepositoryMetaData
argument_list|(
literal|"dummy-repo"
argument_list|,
literal|"mock"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Repository
operator|.
name|BUFFER_SIZE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|bufferMB
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Repository
operator|.
name|CHUNK_SIZE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|chunkMB
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|S3Repository
argument_list|(
name|metadata
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
operator|new
name|DummyS3Service
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|assertInvalidBuffer
specifier|private
name|void
name|assertInvalidBuffer
parameter_list|(
name|int
name|bufferMB
parameter_list|,
name|int
name|chunkMB
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|clazz
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|RepositoryMetaData
name|metadata
init|=
operator|new
name|RepositoryMetaData
argument_list|(
literal|"dummy-repo"
argument_list|,
literal|"mock"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Repository
operator|.
name|BUFFER_SIZE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|bufferMB
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Repository
operator|.
name|CHUNK_SIZE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|chunkMB
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|clazz
argument_list|,
parameter_list|()
lambda|->
operator|new
name|S3Repository
argument_list|(
name|metadata
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
operator|new
name|DummyS3Service
argument_list|()
argument_list|)
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
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testBasePathSetting
specifier|public
name|void
name|testBasePathSetting
parameter_list|()
throws|throws
name|IOException
block|{
name|RepositoryMetaData
name|metadata
init|=
operator|new
name|RepositoryMetaData
argument_list|(
literal|"dummy-repo"
argument_list|,
literal|"mock"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Repository
operator|.
name|BASE_PATH_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"/foo/bar"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|S3Repository
name|s3repo
init|=
operator|new
name|S3Repository
argument_list|(
name|metadata
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
operator|new
name|DummyS3Service
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo/bar/"
argument_list|,
name|s3repo
operator|.
name|basePath
argument_list|()
operator|.
name|buildAsString
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure leading `/` is removed and trailing is added
name|assertWarnings
argument_list|(
literal|"S3 repository base_path"
operator|+
literal|" trimming the leading `/`, and leading `/` will not be supported for the S3 repository in future releases"
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|RepositoryMetaData
argument_list|(
literal|"dummy-repo"
argument_list|,
literal|"mock"
argument_list|,
name|Settings
operator|.
name|EMPTY
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
name|Repositories
operator|.
name|BASE_PATH_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"/foo/bar"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|s3repo
operator|=
operator|new
name|S3Repository
argument_list|(
name|metadata
argument_list|,
name|settings
argument_list|,
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
operator|new
name|DummyS3Service
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo/bar/"
argument_list|,
name|s3repo
operator|.
name|basePath
argument_list|()
operator|.
name|buildAsString
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure leading `/` is removed and trailing is added
name|assertWarnings
argument_list|(
literal|"S3 repository base_path"
operator|+
literal|" trimming the leading `/`, and leading `/` will not be supported for the S3 repository in future releases"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testDefaultBufferSize
specifier|public
name|void
name|testDefaultBufferSize
parameter_list|()
block|{
name|ByteSizeValue
name|defaultBufferSize
init|=
name|S3Repository
operator|.
name|Repository
operator|.
name|BUFFER_SIZE_SETTING
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|defaultBufferSize
argument_list|,
name|Matchers
operator|.
name|lessThanOrEqualTo
argument_list|(
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
name|assertThat
argument_list|(
name|defaultBufferSize
argument_list|,
name|Matchers
operator|.
name|greaterThanOrEqualTo
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|5
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ByteSizeValue
name|defaultNodeBufferSize
init|=
name|S3Repository
operator|.
name|Repositories
operator|.
name|BUFFER_SIZE_SETTING
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|defaultBufferSize
argument_list|,
name|defaultNodeBufferSize
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

