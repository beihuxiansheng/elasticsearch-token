begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
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
name|MatcherAssert
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JacksonLocationTests
specifier|public
class|class
name|JacksonLocationTests
block|{
DECL|method|testLocationExtraction
annotation|@
name|Test
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
name|FastByteArrayOutputStream
name|os
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
name|JsonGenerator
name|gen
init|=
operator|new
name|JsonFactory
argument_list|()
operator|.
name|createJsonGenerator
argument_list|(
name|os
argument_list|,
name|JsonEncoding
operator|.
name|UTF8
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
name|byte
index|[]
name|data
init|=
name|os
operator|.
name|copiedByteArray
argument_list|()
decl_stmt|;
name|FastByteArrayInputStream
name|is
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|JsonParser
name|parser
init|=
operator|new
name|JsonFactory
argument_list|()
operator|.
name|createJsonParser
argument_list|(
name|is
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
comment|//        int location1 = is.position();
comment|//        parser.skipChildren();
comment|//        int location2 = is.position();
comment|//        byte[] sourceData = new byte[location2 - location1];
comment|//        System.arraycopy(data, location1, sourceData, 0, sourceData.length);
comment|//        System.out.println(Unicode.fromBytes(sourceData));
comment|//        JsonParser sourceParser = new JsonFactory().createJsonParser(new FastByteArrayInputStream(sourceData));
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.START_OBJECT));
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.FIELD_NAME)); // "value"
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.VALUE_STRING));
comment|//        assertThat(sourceParser.nextToken(), equalTo(JsonToken.END_OBJECT));
block|}
block|}
end_class

end_unit

