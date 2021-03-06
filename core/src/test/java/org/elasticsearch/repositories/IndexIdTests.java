begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
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
name|UUIDs
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
name|xcontent
operator|.
name|ToXContent
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
name|XContentBuilder
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
name|XContentParser
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
name|json
operator|.
name|JsonXContent
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

begin_comment
comment|/**  * Tests for the {@link IndexId} class.  */
end_comment

begin_class
DECL|class|IndexIdTests
specifier|public
class|class
name|IndexIdTests
extends|extends
name|ESTestCase
block|{
DECL|method|testEqualsAndHashCode
specifier|public
name|void
name|testEqualsAndHashCode
parameter_list|()
block|{
comment|// assert equals and hashcode
name|String
name|name
init|=
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
decl_stmt|;
name|IndexId
name|indexId1
init|=
operator|new
name|IndexId
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|IndexId
name|indexId2
init|=
operator|new
name|IndexId
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|indexId1
argument_list|,
name|indexId2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexId1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|indexId2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert equals when using index name for id
name|id
operator|=
name|name
expr_stmt|;
name|indexId1
operator|=
operator|new
name|IndexId
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|indexId2
operator|=
operator|new
name|IndexId
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexId1
argument_list|,
name|indexId2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexId1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|indexId2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|//assert not equals when name or id differ
name|indexId2
operator|=
operator|new
name|IndexId
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|indexId1
argument_list|,
name|indexId2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|indexId1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|indexId2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|indexId2
operator|=
operator|new
name|IndexId
argument_list|(
name|name
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|indexId1
argument_list|,
name|indexId2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|indexId1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|indexId2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexId
name|indexId
init|=
operator|new
name|IndexId
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|indexId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexId
argument_list|,
operator|new
name|IndexId
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testXContent
specifier|public
name|void
name|testXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexId
name|indexId
init|=
operator|new
name|IndexId
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
decl_stmt|;
name|indexId
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
specifier|final
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentFieldName
operator|.
name|equals
argument_list|(
name|IndexId
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|name
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentFieldName
operator|.
name|equals
argument_list|(
name|IndexId
operator|.
name|ID
argument_list|)
condition|)
block|{
name|id
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexId
argument_list|,
operator|new
name|IndexId
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

