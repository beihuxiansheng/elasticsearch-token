begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.put
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
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
name|ActionRequestValidationException
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
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
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
name|XContentType
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
name|yaml
operator|.
name|YamlXContent
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
name|Index
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
name|util
operator|.
name|Base64
import|;
end_import

begin_class
DECL|class|PutMappingRequestTests
specifier|public
class|class
name|PutMappingRequestTests
extends|extends
name|ESTestCase
block|{
DECL|method|testValidation
specifier|public
name|void
name|testValidation
parameter_list|()
block|{
name|PutMappingRequest
name|r
init|=
operator|new
name|PutMappingRequest
argument_list|(
literal|"myindex"
argument_list|)
decl_stmt|;
name|ActionRequestValidationException
name|ex
init|=
name|r
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"type validation should fail"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"type is missing"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|type
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ex
operator|=
name|r
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"type validation should fail"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"type is empty"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|type
argument_list|(
literal|"mytype"
argument_list|)
expr_stmt|;
name|ex
operator|=
name|r
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"source validation should fail"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"source is missing"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|source
argument_list|(
literal|""
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|ex
operator|=
name|r
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"source validation should fail"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"source is empty"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|source
argument_list|(
literal|"somevalidmapping"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|ex
operator|=
name|r
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"validation should succeed"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|r
operator|.
name|setConcreteIndex
argument_list|(
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|ex
operator|=
name|r
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"source validation should fail"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Validation Failed: 1: either concrete index or unresolved indices can be set,"
operator|+
literal|" concrete index: [[foo/bar]] and indices: [myindex];"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuildFromSimplifiedDef
specifier|public
name|void
name|testBuildFromSimplifiedDef
parameter_list|()
block|{
comment|// test that method rejects input where input varargs fieldname/properites are not paired correctly
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
literal|"type"
argument_list|,
literal|"only_field"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"mapping source must be pairs of fieldnames and properties definition."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPutMappingRequestSerialization
specifier|public
name|void
name|testPutMappingRequestSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|PutMappingRequest
name|request
init|=
operator|new
name|PutMappingRequest
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|String
name|mapping
init|=
name|YamlXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|request
operator|.
name|source
argument_list|(
name|mapping
argument_list|,
name|XContentType
operator|.
name|YAML
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|mapping
argument_list|)
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|YAML
argument_list|)
argument_list|,
name|request
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|bytesStreamOutput
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|request
operator|.
name|writeTo
argument_list|(
name|bytesStreamOutput
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|bytesStreamOutput
operator|.
name|bytes
argument_list|()
operator|.
name|toBytesRef
argument_list|()
operator|.
name|bytes
argument_list|)
decl_stmt|;
name|PutMappingRequest
name|serialized
init|=
operator|new
name|PutMappingRequest
argument_list|()
decl_stmt|;
name|serialized
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|String
name|source
init|=
name|serialized
operator|.
name|source
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|mapping
argument_list|)
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|YAML
argument_list|)
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerializationBwc
specifier|public
name|void
name|testSerializationBwc
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|Base64
operator|.
name|getDecoder
argument_list|()
operator|.
name|decode
argument_list|(
literal|"ADwDAQNmb28MAA8tLS0KZm9vOiAiYmFyIgoAPAMAAAA="
argument_list|)
decl_stmt|;
specifier|final
name|Version
name|version
init|=
name|randomFrom
argument_list|(
name|Version
operator|.
name|V_5_0_0
argument_list|,
name|Version
operator|.
name|V_5_0_1
argument_list|,
name|Version
operator|.
name|V_5_0_2
argument_list|,
name|Version
operator|.
name|V_5_1_1
argument_list|,
name|Version
operator|.
name|V_5_1_2
argument_list|,
name|Version
operator|.
name|V_5_2_0
argument_list|)
decl_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
init|)
block|{
name|in
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|PutMappingRequest
name|request
init|=
operator|new
name|PutMappingRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|String
name|mapping
init|=
name|YamlXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|mapping
argument_list|)
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|YAML
argument_list|)
argument_list|,
name|request
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

