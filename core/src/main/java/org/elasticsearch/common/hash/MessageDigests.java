begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.hash
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
package|;
end_package

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

begin_class
DECL|class|MessageDigests
specifier|public
class|class
name|MessageDigests
block|{
DECL|field|MD5_DIGEST
specifier|private
specifier|static
specifier|final
name|MessageDigest
name|MD5_DIGEST
decl_stmt|;
DECL|field|SHA_1_DIGEST
specifier|private
specifier|static
specifier|final
name|MessageDigest
name|SHA_1_DIGEST
decl_stmt|;
DECL|field|SHA_256_DIGEST
specifier|private
specifier|static
specifier|final
name|MessageDigest
name|SHA_256_DIGEST
decl_stmt|;
static|static
block|{
try|try
block|{
name|MD5_DIGEST
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
expr_stmt|;
name|SHA_1_DIGEST
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA-1"
argument_list|)
expr_stmt|;
name|SHA_256_DIGEST
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA-256"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unexpected exception creating MessageDigest instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|md5
specifier|public
specifier|static
name|MessageDigest
name|md5
parameter_list|()
block|{
return|return
name|clone
argument_list|(
name|MD5_DIGEST
argument_list|)
return|;
block|}
DECL|method|sha1
specifier|public
specifier|static
name|MessageDigest
name|sha1
parameter_list|()
block|{
return|return
name|clone
argument_list|(
name|SHA_1_DIGEST
argument_list|)
return|;
block|}
DECL|method|sha256
specifier|public
specifier|static
name|MessageDigest
name|sha256
parameter_list|()
block|{
return|return
name|clone
argument_list|(
name|SHA_256_DIGEST
argument_list|)
return|;
block|}
DECL|method|clone
specifier|private
specifier|static
name|MessageDigest
name|clone
parameter_list|(
name|MessageDigest
name|messageDigest
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|MessageDigest
operator|)
name|messageDigest
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unexpected exception cloning MessageDigest instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|HEX_DIGITS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEX_DIGITS
init|=
literal|"0123456789abcdef"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|method|toHexString
specifier|public
specifier|static
name|String
name|toHexString
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"bytes"
argument_list|)
throw|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|2
operator|*
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|bytes
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
name|b
operator|>>
literal|4
operator|&
literal|0xf
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

