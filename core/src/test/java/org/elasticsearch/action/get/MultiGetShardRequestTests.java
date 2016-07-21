begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
package|;
end_package

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
name|index
operator|.
name|VersionType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|source
operator|.
name|FetchSourceContext
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
name|elasticsearch
operator|.
name|test
operator|.
name|VersionUtils
operator|.
name|randomVersion
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

begin_class
DECL|class|MultiGetShardRequestTests
specifier|public
class|class
name|MultiGetShardRequestTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|MultiGetRequest
name|multiGetRequest
init|=
operator|new
name|MultiGetRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|multiGetRequest
operator|.
name|preference
argument_list|(
name|randomAsciiOfLength
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
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
name|multiGetRequest
operator|.
name|realtime
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|multiGetRequest
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|multiGetRequest
operator|.
name|ignoreErrorsOnGeneratedFields
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|MultiGetShardRequest
name|multiGetShardRequest
init|=
operator|new
name|MultiGetShardRequest
argument_list|(
name|multiGetRequest
argument_list|,
literal|"index"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|iterations
argument_list|(
literal|10
argument_list|,
literal|30
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
name|numItems
condition|;
name|i
operator|++
control|)
block|{
name|MultiGetRequest
operator|.
name|Item
name|item
init|=
operator|new
name|MultiGetRequest
operator|.
name|Item
argument_list|(
literal|"alias-"
operator|+
name|randomAsciiOfLength
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|,
literal|"type"
argument_list|,
literal|"id-"
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numFields
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
name|fields
init|=
operator|new
name|String
index|[
name|numFields
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fields
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|fields
index|[
name|j
index|]
operator|=
name|randomAsciiOfLength
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|item
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|item
operator|.
name|version
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|item
operator|.
name|versionType
argument_list|(
name|randomFrom
argument_list|(
name|VersionType
operator|.
name|values
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
name|item
operator|.
name|fetchSourceContext
argument_list|(
operator|new
name|FetchSourceContext
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|multiGetShardRequest
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|out
operator|.
name|setVersion
argument_list|(
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|multiGetShardRequest
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
decl_stmt|;
name|in
operator|.
name|setVersion
argument_list|(
name|out
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|MultiGetShardRequest
name|multiGetShardRequest2
init|=
operator|new
name|MultiGetShardRequest
argument_list|()
decl_stmt|;
name|multiGetShardRequest2
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|preference
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|preference
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|realtime
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|realtime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|refresh
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|refresh
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|ignoreErrorsOnGeneratedFields
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|ignoreErrorsOnGeneratedFields
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|items
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|items
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|multiGetShardRequest2
operator|.
name|items
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|MultiGetRequest
operator|.
name|Item
name|item
init|=
name|multiGetShardRequest
operator|.
name|items
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|MultiGetRequest
operator|.
name|Item
name|item2
init|=
name|multiGetShardRequest2
operator|.
name|items
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|type
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|fields
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|version
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|versionType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|versionType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|item2
operator|.
name|fetchSourceContext
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|item
operator|.
name|fetchSourceContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|indices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multiGetShardRequest2
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|multiGetShardRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

