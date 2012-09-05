begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.mapper.object
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|mapper
operator|.
name|object
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
name|bytes
operator|.
name|BytesArray
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
name|XContentFactory
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
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|MapperParsingException
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
name|unit
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|SimpleObjectMappingTests
specifier|public
class|class
name|SimpleObjectMappingTests
block|{
DECL|method|testDifferentInnerObjectTokenFailure
specifier|public
name|void
name|testDifferentInnerObjectTokenFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
try|try
block|{
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|" {\n"
operator|+
literal|"      \"object\": {\n"
operator|+
literal|"        \"array\":[\n"
operator|+
literal|"        {\n"
operator|+
literal|"          \"object\": { \"value\": \"value\" }\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"          \"object\":\"value\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"        ]\n"
operator|+
literal|"      },\n"
operator|+
literal|"      \"value\":\"value\"\n"
operator|+
literal|"    }"
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
block|}
block|}
end_class

end_unit

