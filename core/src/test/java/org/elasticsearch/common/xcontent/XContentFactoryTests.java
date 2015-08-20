begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|dataformat
operator|.
name|cbor
operator|.
name|CBORConstants
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|dataformat
operator|.
name|smile
operator|.
name|SmileConstants
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|XContentFactoryTests
specifier|public
class|class
name|XContentFactoryTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testGuessJson
specifier|public
name|void
name|testGuessJson
parameter_list|()
throws|throws
name|IOException
block|{
name|testGuessType
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGuessSmile
specifier|public
name|void
name|testGuessSmile
parameter_list|()
throws|throws
name|IOException
block|{
name|testGuessType
argument_list|(
name|XContentType
operator|.
name|SMILE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGuessYaml
specifier|public
name|void
name|testGuessYaml
parameter_list|()
throws|throws
name|IOException
block|{
name|testGuessType
argument_list|(
name|XContentType
operator|.
name|YAML
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGuessCbor
specifier|public
name|void
name|testGuessCbor
parameter_list|()
throws|throws
name|IOException
block|{
name|testGuessType
argument_list|(
name|XContentType
operator|.
name|CBOR
argument_list|)
expr_stmt|;
block|}
DECL|method|testGuessType
specifier|private
name|void
name|testGuessType
parameter_list|(
name|XContentType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|BytesArray
name|bytesArray
init|=
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|toBytesArray
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|StreamInput
operator|.
name|wrap
argument_list|(
name|bytesArray
operator|.
name|array
argument_list|()
argument_list|,
name|bytesArray
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|bytesArray
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
comment|// CBOR is binary, cannot use String
if|if
condition|(
name|type
operator|!=
name|XContentType
operator|.
name|CBOR
condition|)
block|{
name|assertThat
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCBORBasedOnMajorObjectDetection
specifier|public
name|void
name|testCBORBasedOnMajorObjectDetection
parameter_list|()
block|{
comment|// for this {"f "=> 5} perl encoder for example generates:
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xA1
block|,
operator|(
name|byte
operator|)
literal|0x43
block|,
operator|(
name|byte
operator|)
literal|0x66
block|,
operator|(
name|byte
operator|)
literal|6f
block|,
operator|(
name|byte
operator|)
literal|6f
block|,
operator|(
name|byte
operator|)
literal|0x5
block|}
decl_stmt|;
name|assertThat
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|XContentType
operator|.
name|CBOR
argument_list|)
argument_list|)
expr_stmt|;
comment|//assertThat(((Number) XContentHelper.convertToMap(bytes, true).v2().get("foo")).intValue(), equalTo(5));
comment|// this if for {"foo" : 5} in python CBOR
name|bytes
operator|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xA1
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x66
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x5
block|}
expr_stmt|;
name|assertThat
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|XContentType
operator|.
name|CBOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|bytes
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|v2
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// also make sure major type check doesn't collide with SMILE and JSON, just in case
name|assertThat
argument_list|(
name|CBORConstants
operator|.
name|hasMajorType
argument_list|(
name|CBORConstants
operator|.
name|MAJOR_TYPE_OBJECT
argument_list|,
name|SmileConstants
operator|.
name|HEADER_BYTE_1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CBORConstants
operator|.
name|hasMajorType
argument_list|(
name|CBORConstants
operator|.
name|MAJOR_TYPE_OBJECT
argument_list|,
operator|(
name|byte
operator|)
literal|'{'
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CBORConstants
operator|.
name|hasMajorType
argument_list|(
name|CBORConstants
operator|.
name|MAJOR_TYPE_OBJECT
argument_list|,
operator|(
name|byte
operator|)
literal|' '
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CBORConstants
operator|.
name|hasMajorType
argument_list|(
name|CBORConstants
operator|.
name|MAJOR_TYPE_OBJECT
argument_list|,
operator|(
name|byte
operator|)
literal|'-'
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCBORBasedOnMagicHeaderDetection
specifier|public
name|void
name|testCBORBasedOnMagicHeaderDetection
parameter_list|()
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xd9
block|,
operator|(
name|byte
operator|)
literal|0xd9
block|,
operator|(
name|byte
operator|)
literal|0xf7
block|}
decl_stmt|;
name|assertThat
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|XContentType
operator|.
name|CBOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyStream
specifier|public
name|void
name|testEmptyStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
