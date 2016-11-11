begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.deps.jackson
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|deps
operator|.
name|jackson
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
name|core
operator|.
name|JsonFactory
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
name|core
operator|.
name|JsonGenerator
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
name|core
operator|.
name|JsonParser
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
name|core
operator|.
name|JsonToken
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
DECL|class|JacksonLocationTests
specifier|public
class|class
name|JacksonLocationTests
extends|extends
name|ESTestCase
block|{
DECL|method|testLocationExtraction
specifier|public
name|void
name|testLocationExtraction
parameter_list|()
throws|throws
name|IOException
block|{
comment|// {
comment|//    "index" : "test",
comment|//    "source" : {
comment|//         value : "something"
comment|//    }
comment|// }
name|BytesStreamOutput
name|os
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|JsonGenerator
name|gen
init|=
operator|new
name|JsonFactory
argument_list|()
operator|.
name|createGenerator
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|writeStringField
argument_list|(
literal|"index"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeFieldName
argument_list|(
literal|"source"
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|writeStringField
argument_list|(
literal|"value"
argument_list|,
literal|"something"
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|close
argument_list|()
expr_stmt|;
name|JsonParser
name|parser
init|=
operator|new
name|JsonFactory
argument_list|()
operator|.
name|createParser
argument_list|(
name|os
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|JsonToken
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|JsonToken
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// "index"
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|JsonToken
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|JsonToken
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// "source"
comment|//        JsonLocation location1 = parser.getCurrentLocation();
comment|//        parser.skipChildren();
comment|//        JsonLocation location2 = parser.getCurrentLocation();
comment|//
comment|//        byte[] sourceData = new byte[(int) (location2.getByteOffset() - location1.getByteOffset())];
comment|//        System.arraycopy(data, (int) location1.getByteOffset(), sourceData, 0, sourceData.length);
comment|//
comment|//        JsonParser sourceParser = new JsonFactory().createJsonParser(new FastByteArrayInputStream(sourceData));
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.START_OBJECT));
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.FIELD_NAME)); // "value"
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.VALUE_STRING));
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.END_OBJECT));
block|}
block|}
end_class

end_unit

