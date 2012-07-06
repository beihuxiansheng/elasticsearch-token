begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|json
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
name|JsonGenerator
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
name|BytesReference
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
name|Streams
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
name|*
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JsonXContentGenerator
specifier|public
class|class
name|JsonXContentGenerator
implements|implements
name|XContentGenerator
block|{
DECL|field|generator
specifier|protected
specifier|final
name|JsonGenerator
name|generator
decl_stmt|;
DECL|method|JsonXContentGenerator
specifier|public
name|JsonXContentGenerator
parameter_list|(
name|JsonGenerator
name|generator
parameter_list|)
block|{
name|this
operator|.
name|generator
operator|=
name|generator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|public
name|XContentType
name|contentType
parameter_list|()
block|{
return|return
name|XContentType
operator|.
name|JSON
return|;
block|}
annotation|@
name|Override
DECL|method|usePrettyPrint
specifier|public
name|void
name|usePrettyPrint
parameter_list|()
block|{
name|generator
operator|.
name|useDefaultPrettyPrinter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStartArray
specifier|public
name|void
name|writeStartArray
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeEndArray
specifier|public
name|void
name|writeEndArray
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStartObject
specifier|public
name|void
name|writeStartObject
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeEndObject
specifier|public
name|void
name|writeEndObject
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFieldName
specifier|public
name|void
name|writeFieldName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFieldName
specifier|public
name|void
name|writeFieldName
parameter_list|(
name|XContentString
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeString
specifier|public
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
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBinary
specifier|public
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
block|{
name|generator
operator|.
name|writeBinary
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBinary
specifier|public
name|void
name|writeBinary
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeBinary
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumber
specifier|public
name|void
name|writeNumber
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumber
specifier|public
name|void
name|writeNumber
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumber
specifier|public
name|void
name|writeNumber
parameter_list|(
name|double
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumber
specifier|public
name|void
name|writeNumber
parameter_list|(
name|float
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBoolean
specifier|public
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeBoolean
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStringField
specifier|public
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
block|{
name|generator
operator|.
name|writeStringField
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStringField
specifier|public
name|void
name|writeStringField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBooleanField
specifier|public
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
block|{
name|generator
operator|.
name|writeBooleanField
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBooleanField
specifier|public
name|void
name|writeBooleanField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNullField
specifier|public
name|void
name|writeNullField
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNullField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNullField
specifier|public
name|void
name|writeNullField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
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
block|{
name|generator
operator|.
name|writeNumberField
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
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
block|{
name|generator
operator|.
name|writeNumberField
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
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
block|{
name|generator
operator|.
name|writeNumberField
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
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
block|{
name|generator
operator|.
name|writeNumberField
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBinaryField
specifier|public
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
block|{
name|generator
operator|.
name|writeBinaryField
argument_list|(
name|fieldName
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBinaryField
specifier|public
name|void
name|writeBinaryField
parameter_list|(
name|XContentString
name|fieldName
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeBinary
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayFieldStart
specifier|public
name|void
name|writeArrayFieldStart
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeArrayFieldStart
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayFieldStart
specifier|public
name|void
name|writeArrayFieldStart
parameter_list|(
name|XContentString
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeObjectFieldStart
specifier|public
name|void
name|writeObjectFieldStart
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeObjectFieldStart
specifier|public
name|void
name|writeObjectFieldStart
parameter_list|(
name|XContentString
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeRawField
specifier|public
name|void
name|writeRawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|content
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRaw
argument_list|(
literal|", \""
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
literal|"\" : "
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeRawField
specifier|public
name|void
name|writeRawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|content
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRaw
argument_list|(
literal|", \""
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
literal|"\" : "
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|content
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeRawField
specifier|public
name|void
name|writeRawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|InputStream
name|content
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRaw
argument_list|(
literal|", \""
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
literal|"\" : "
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|Streams
operator|.
name|copy
argument_list|(
name|content
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeRawField
specifier|public
name|void
name|writeRawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BytesReference
name|content
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRaw
argument_list|(
literal|", \""
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeRaw
argument_list|(
literal|"\" : "
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|content
operator|.
name|writeTo
argument_list|(
name|bos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyCurrentStructure
specifier|public
name|void
name|copyCurrentStructure
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
comment|// the start of the parser
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
literal|null
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|parser
operator|instanceof
name|JsonXContentParser
condition|)
block|{
name|generator
operator|.
name|copyCurrentStructure
argument_list|(
operator|(
operator|(
name|JsonXContentParser
operator|)
name|parser
operator|)
operator|.
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XContentHelper
operator|.
name|copyCurrentStructure
argument_list|(
name|this
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

