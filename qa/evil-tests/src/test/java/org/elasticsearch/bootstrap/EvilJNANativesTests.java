begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|io
operator|.
name|PathUtils
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
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|anyOf
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThanOrEqualTo
import|;
end_import

begin_class
DECL|class|EvilJNANativesTests
specifier|public
class|class
name|EvilJNANativesTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSetMaximumNumberOfThreads
specifier|public
name|void
name|testSetMaximumNumberOfThreads
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
literal|"/proc/self/limits"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lines
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
if|if
condition|(
name|line
operator|!=
literal|null
operator|&&
name|line
operator|.
name|startsWith
argument_list|(
literal|"Max processes"
argument_list|)
condition|)
block|{
specifier|final
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|limit
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fields
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|JNANatives
operator|.
name|MAX_NUMBER_OF_THREADS
argument_list|,
name|equalTo
argument_list|(
name|limit
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|fail
argument_list|(
literal|"should have read max processes from /proc/self/limits"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|JNANatives
operator|.
name|MAX_NUMBER_OF_THREADS
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSetMaxSizeVirtualMemory
specifier|public
name|void
name|testSetMaxSizeVirtualMemory
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
literal|"/proc/self/limits"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lines
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
if|if
condition|(
name|line
operator|!=
literal|null
operator|&&
name|line
operator|.
name|startsWith
argument_list|(
literal|"Max address space"
argument_list|)
condition|)
block|{
specifier|final
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|limit
init|=
name|fields
index|[
literal|3
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|JNANatives
operator|.
name|rlimitToString
argument_list|(
name|JNANatives
operator|.
name|MAX_SIZE_VIRTUAL_MEMORY
argument_list|)
argument_list|,
name|limit
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|fail
argument_list|(
literal|"should have read max size virtual memory from /proc/self/limits"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|MAC_OS_X
condition|)
block|{
name|assertThat
argument_list|(
name|JNANatives
operator|.
name|MAX_SIZE_VIRTUAL_MEMORY
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|JNANatives
operator|.
name|MAX_SIZE_VIRTUAL_MEMORY
argument_list|,
name|equalTo
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

