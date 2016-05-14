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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
operator|.
name|BytesReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|XContentGenerator
specifier|public
interface|interface
name|XContentGenerator
extends|extends
name|Closeable
block|{
DECL|method|contentType
name|XContentType
name|contentType
parameter_list|()
function_decl|;
DECL|method|usePrettyPrint
name|void
name|usePrettyPrint
parameter_list|()
function_decl|;
DECL|method|isPrettyPrint
name|boolean
name|isPrettyPrint
parameter_list|()
function_decl|;
DECL|method|usePrintLineFeedAtEnd
name|void
name|usePrintLineFeedAtEnd
parameter_list|()
function_decl|;
DECL|method|writeStartArray
name|void
name|writeStartArray
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|writeEndArray
name|void
name|writeEndArray
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|writeStartObject
name|void
name|writeStartObject
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|writeEndObject
name|void
name|writeEndObject
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|writeFieldName
name|void
name|writeFieldName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeString
name|void
name|writeString
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeString
name|void
name|writeString
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeUTF8String
name|void
name|writeUTF8String
parameter_list|(
name|byte
index|[]
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBinary
name|void
name|writeBinary
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBinary
name|void
name|writeBinary
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumber
name|void
name|writeNumber
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumber
name|void
name|writeNumber
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumber
name|void
name|writeNumber
parameter_list|(
name|double
name|d
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumber
name|void
name|writeNumber
parameter_list|(
name|float
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBoolean
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNull
name|void
name|writeNull
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|writeStringField
name|void
name|writeStringField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBooleanField
name|void
name|writeBooleanField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNullField
name|void
name|writeNullField
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumberField
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumberField
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumberField
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNumberField
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBinaryField
name|void
name|writeBinaryField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeArrayFieldStart
name|void
name|writeArrayFieldStart
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeObjectFieldStart
name|void
name|writeObjectFieldStart
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeRawField
name|void
name|writeRawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|InputStream
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeRawField
name|void
name|writeRawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BytesReference
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeRawValue
name|void
name|writeRawValue
parameter_list|(
name|BytesReference
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|copyCurrentStructure
name|void
name|copyCurrentStructure
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|flush
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

