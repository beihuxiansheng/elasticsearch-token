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
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonServiceException
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
name|model
operator|.
name|AmazonS3Exception
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
name|model
operator|.
name|ObjectMetadata
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
name|model
operator|.
name|PutObjectResult
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
name|model
operator|.
name|S3Object
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
name|model
operator|.
name|UploadPartRequest
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
name|model
operator|.
name|UploadPartResult
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
name|common
operator|.
name|logging
operator|.
name|ESLogger
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
name|Loggers
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomDouble
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TestAmazonS3
specifier|public
class|class
name|TestAmazonS3
extends|extends
name|AmazonS3Wrapper
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|writeFailureRate
specifier|private
name|double
name|writeFailureRate
init|=
literal|0.0
decl_stmt|;
DECL|field|readFailureRate
specifier|private
name|double
name|readFailureRate
init|=
literal|0.0
decl_stmt|;
DECL|field|randomPrefix
specifier|private
name|String
name|randomPrefix
decl_stmt|;
DECL|field|accessCounts
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|accessCounts
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|incrementAndGet
specifier|private
name|long
name|incrementAndGet
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|AtomicLong
name|value
init|=
name|accessCounts
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
name|accessCounts
operator|.
name|putIfAbsent
argument_list|(
name|path
argument_list|,
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
return|return
literal|1
return|;
block|}
DECL|method|TestAmazonS3
specifier|public
name|TestAmazonS3
parameter_list|(
name|AmazonS3
name|delegate
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|randomPrefix
operator|=
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.aws.test.random"
argument_list|)
expr_stmt|;
name|writeFailureRate
operator|=
name|settings
operator|.
name|getAsDouble
argument_list|(
literal|"cloud.aws.test.write_failures"
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|readFailureRate
operator|=
name|settings
operator|.
name|getAsDouble
argument_list|(
literal|"cloud.aws.test.read_failures"
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putObject
specifier|public
name|PutObjectResult
name|putObject
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
name|key
parameter_list|,
name|InputStream
name|input
parameter_list|,
name|ObjectMetadata
name|metadata
parameter_list|)
throws|throws
name|AmazonClientException
throws|,
name|AmazonServiceException
block|{
if|if
condition|(
name|shouldFail
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|,
name|writeFailureRate
argument_list|)
condition|)
block|{
name|long
name|length
init|=
name|metadata
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
name|long
name|partToRead
init|=
call|(
name|long
call|)
argument_list|(
name|length
operator|*
name|randomDouble
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|long
name|cur
init|=
literal|0
init|;
name|cur
operator|<
name|partToRead
condition|;
name|cur
operator|+=
name|buffer
operator|.
name|length
control|)
block|{
try|try
block|{
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
call|(
name|int
call|)
argument_list|(
name|partToRead
operator|-
name|cur
operator|>
name|buffer
operator|.
name|length
condition|?
name|buffer
operator|.
name|length
else|:
name|partToRead
operator|-
name|cur
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"cannot read input stream"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> random write failure on putObject method: throwing an exception for [bucket={}, key={}]"
argument_list|,
name|bucketName
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|AmazonS3Exception
name|ex
init|=
operator|new
name|AmazonS3Exception
argument_list|(
literal|"Random S3 exception"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setStatusCode
argument_list|(
literal|400
argument_list|)
expr_stmt|;
name|ex
operator|.
name|setErrorCode
argument_list|(
literal|"RequestTimeout"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|putObject
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|,
name|input
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|uploadPart
specifier|public
name|UploadPartResult
name|uploadPart
parameter_list|(
name|UploadPartRequest
name|request
parameter_list|)
throws|throws
name|AmazonClientException
throws|,
name|AmazonServiceException
block|{
if|if
condition|(
name|shouldFail
argument_list|(
name|request
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|request
operator|.
name|getKey
argument_list|()
argument_list|,
name|writeFailureRate
argument_list|)
condition|)
block|{
name|long
name|length
init|=
name|request
operator|.
name|getPartSize
argument_list|()
decl_stmt|;
name|long
name|partToRead
init|=
call|(
name|long
call|)
argument_list|(
name|length
operator|*
name|randomDouble
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|long
name|cur
init|=
literal|0
init|;
name|cur
operator|<
name|partToRead
condition|;
name|cur
operator|+=
name|buffer
operator|.
name|length
control|)
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|request
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
call|(
name|int
call|)
argument_list|(
name|partToRead
operator|-
name|cur
operator|>
name|buffer
operator|.
name|length
condition|?
name|buffer
operator|.
name|length
else|:
name|partToRead
operator|-
name|cur
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"cannot read input stream"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> random write failure on uploadPart method: throwing an exception for [bucket={}, key={}]"
argument_list|,
name|request
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|request
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|AmazonS3Exception
name|ex
init|=
operator|new
name|AmazonS3Exception
argument_list|(
literal|"Random S3 write exception"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setStatusCode
argument_list|(
literal|400
argument_list|)
expr_stmt|;
name|ex
operator|.
name|setErrorCode
argument_list|(
literal|"RequestTimeout"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|uploadPart
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getObject
specifier|public
name|S3Object
name|getObject
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|AmazonClientException
throws|,
name|AmazonServiceException
block|{
if|if
condition|(
name|shouldFail
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|,
name|readFailureRate
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> random read failure on getObject method: throwing an exception for [bucket={}, key={}]"
argument_list|,
name|bucketName
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|AmazonS3Exception
name|ex
init|=
operator|new
name|AmazonS3Exception
argument_list|(
literal|"Random S3 read exception"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setStatusCode
argument_list|(
literal|404
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getObject
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|)
return|;
block|}
block|}
DECL|method|shouldFail
specifier|private
name|boolean
name|shouldFail
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
name|key
parameter_list|,
name|double
name|probability
parameter_list|)
block|{
if|if
condition|(
name|probability
operator|>
literal|0.0
condition|)
block|{
name|String
name|path
init|=
name|randomPrefix
operator|+
literal|"-"
operator|+
name|bucketName
operator|+
literal|"+"
operator|+
name|key
decl_stmt|;
name|path
operator|+=
literal|"/"
operator|+
name|incrementAndGet
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|hashCode
argument_list|(
name|path
argument_list|)
argument_list|)
operator|<
name|Integer
operator|.
name|MAX_VALUE
operator|*
name|probability
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|hashCode
specifier|private
name|int
name|hashCode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
try|try
block|{
name|MessageDigest
name|digest
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|digest
operator|.
name|digest
argument_list|(
name|path
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
return|return
operator|(
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"cannot calculate hashcode"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"cannot calculate hashcode"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

