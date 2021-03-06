begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.shards
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|shards
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
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
name|stream
operator|.
name|BytesStreamOutput
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
name|stream
operator|.
name|StreamInput
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
name|elasticsearch
operator|.
name|test
operator|.
name|VersionUtils
import|;
end_import

begin_class
DECL|class|ClusterSearchShardsRequestTests
specifier|public
class|class
name|ClusterSearchShardsRequestTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterSearchShardsRequest
name|request
init|=
operator|new
name|ClusterSearchShardsRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numIndices
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|String
index|[]
name|indices
init|=
operator|new
name|String
index|[
name|numIndices
index|]
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
name|numIndices
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|request
operator|.
name|indicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromOptions
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|request
operator|.
name|preference
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numRoutings
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|String
index|[]
name|routings
init|=
operator|new
name|String
index|[
name|numRoutings
index|]
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
name|numRoutings
condition|;
name|i
operator|++
control|)
block|{
name|routings
index|[
name|i
index|]
operator|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|routing
argument_list|(
name|routings
argument_list|)
expr_stmt|;
block|}
name|Version
name|version
init|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_5_0_0
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
init|)
block|{
name|in
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|ClusterSearchShardsRequest
name|deserialized
init|=
operator|new
name|ClusterSearchShardsRequest
argument_list|()
decl_stmt|;
name|deserialized
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|deserialized
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|request
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|deserialized
operator|.
name|indicesOptions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|routing
argument_list|()
argument_list|,
name|deserialized
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|preference
argument_list|()
argument_list|,
name|deserialized
operator|.
name|preference
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testIndicesMustNotBeNull
specifier|public
name|void
name|testIndicesMustNotBeNull
parameter_list|()
block|{
name|ClusterSearchShardsRequest
name|request
init|=
operator|new
name|ClusterSearchShardsRequest
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|request
operator|.
name|indices
argument_list|(
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|request
operator|.
name|indices
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|request
operator|.
name|indices
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index1"
block|,
literal|null
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

