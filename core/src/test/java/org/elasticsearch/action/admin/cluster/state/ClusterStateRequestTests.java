begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.state
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
name|state
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Unit tests for the {@link ClusterStateRequest}.  */
end_comment

begin_class
DECL|class|ClusterStateRequestTests
specifier|public
class|class
name|ClusterStateRequestTests
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
name|int
name|iterations
init|=
name|randomIntBetween
argument_list|(
literal|5
argument_list|,
literal|20
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|IndicesOptions
name|indicesOptions
init|=
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
decl_stmt|;
name|ClusterStateRequest
name|clusterStateRequest
init|=
operator|new
name|ClusterStateRequest
argument_list|()
operator|.
name|routingTable
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|metaData
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|nodes
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|blocks
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|indices
argument_list|(
literal|"testindex"
argument_list|,
literal|"testindex2"
argument_list|)
operator|.
name|indicesOptions
argument_list|(
name|indicesOptions
argument_list|)
decl_stmt|;
name|Version
name|testVersion
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
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|output
operator|.
name|setVersion
argument_list|(
name|testVersion
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|StreamInput
name|streamInput
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|streamInput
operator|.
name|setVersion
argument_list|(
name|testVersion
argument_list|)
expr_stmt|;
name|ClusterStateRequest
name|deserializedCSRequest
init|=
operator|new
name|ClusterStateRequest
argument_list|()
decl_stmt|;
name|deserializedCSRequest
operator|.
name|readFrom
argument_list|(
name|streamInput
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|deserializedCSRequest
operator|.
name|routingTable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clusterStateRequest
operator|.
name|routingTable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|deserializedCSRequest
operator|.
name|metaData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clusterStateRequest
operator|.
name|metaData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|deserializedCSRequest
operator|.
name|nodes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clusterStateRequest
operator|.
name|nodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|deserializedCSRequest
operator|.
name|blocks
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clusterStateRequest
operator|.
name|blocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|deserializedCSRequest
operator|.
name|indices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clusterStateRequest
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertOptionsMatch
argument_list|(
name|deserializedCSRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|clusterStateRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertOptionsMatch
specifier|private
specifier|static
name|void
name|assertOptionsMatch
parameter_list|(
name|IndicesOptions
name|in
parameter_list|,
name|IndicesOptions
name|out
parameter_list|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|ignoreUnavailable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|out
operator|.
name|ignoreUnavailable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|expandWildcardsClosed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|out
operator|.
name|expandWildcardsClosed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|expandWildcardsOpen
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|out
operator|.
name|expandWildcardsOpen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|allowNoIndices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|out
operator|.
name|allowNoIndices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

