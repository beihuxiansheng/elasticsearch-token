begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
operator|.
name|blobstore
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
name|model
operator|.
name|CannedAccessControlList
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
name|BlobStoreException
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|S3BlobStoreTests
specifier|public
class|class
name|S3BlobStoreTests
extends|extends
name|ESTestCase
block|{
DECL|method|testInitCannedACL
specifier|public
name|void
name|testInitCannedACL
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|aclList
init|=
operator|new
name|String
index|[]
block|{
literal|"private"
block|,
literal|"public-read"
block|,
literal|"public-read-write"
block|,
literal|"authenticated-read"
block|,
literal|"log-delivery-write"
block|,
literal|"bucket-owner-read"
block|,
literal|"bucket-owner-full-control"
block|}
decl_stmt|;
comment|//empty acl
name|assertThat
argument_list|(
name|S3BlobStore
operator|.
name|initCannedACL
argument_list|(
literal|null
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|CannedAccessControlList
operator|.
name|Private
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|S3BlobStore
operator|.
name|initCannedACL
argument_list|(
literal|""
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|CannedAccessControlList
operator|.
name|Private
argument_list|)
argument_list|)
expr_stmt|;
comment|// it should init cannedACL correctly
for|for
control|(
name|String
name|aclString
range|:
name|aclList
control|)
block|{
name|CannedAccessControlList
name|acl
init|=
name|S3BlobStore
operator|.
name|initCannedACL
argument_list|(
name|aclString
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|aclString
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// it should accept all aws cannedACLs
for|for
control|(
name|CannedAccessControlList
name|awsList
range|:
name|CannedAccessControlList
operator|.
name|values
argument_list|()
control|)
block|{
name|CannedAccessControlList
name|acl
init|=
name|S3BlobStore
operator|.
name|initCannedACL
argument_list|(
name|awsList
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|acl
argument_list|,
name|equalTo
argument_list|(
name|awsList
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidCannedACL
specifier|public
name|void
name|testInvalidCannedACL
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|S3BlobStore
operator|.
name|initCannedACL
argument_list|(
literal|"test_invalid"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CannedACL should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlobStoreException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"cannedACL is not valid: [test_invalid]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

