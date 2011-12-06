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
name|JsonParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
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
name|ElasticSearchIllegalStateException
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
name|support
operator|.
name|AbstractXContentParser
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
comment|/**  *  */
end_comment

begin_class
DECL|class|JsonXContentParser
specifier|public
class|class
name|JsonXContentParser
extends|extends
name|AbstractXContentParser
block|{
DECL|field|parser
specifier|final
name|JsonParser
name|parser
decl_stmt|;
DECL|method|JsonXContentParser
specifier|public
name|JsonXContentParser
parameter_list|(
name|JsonParser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
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
DECL|method|nextToken
specifier|public
name|Token
name|nextToken
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|convertToken
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|skipChildren
specifier|public
name|void
name|skipChildren
parameter_list|()
throws|throws
name|IOException
block|{
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|currentToken
specifier|public
name|Token
name|currentToken
parameter_list|()
block|{
return|return
name|convertToken
argument_list|(
name|parser
operator|.
name|getCurrentToken
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|numberType
specifier|public
name|NumberType
name|numberType
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|convertNumberType
argument_list|(
name|parser
operator|.
name|getNumberType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|estimatedNumberType
specifier|public
name|boolean
name|estimatedNumberType
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|currentName
specifier|public
name|String
name|currentName
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getCurrentName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doBooleanValue
specifier|protected
name|boolean
name|doBooleanValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getBooleanValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|text
specifier|public
name|String
name|text
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getText
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasTextCharacters
specifier|public
name|boolean
name|hasTextCharacters
parameter_list|()
block|{
return|return
name|parser
operator|.
name|hasTextCharacters
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|textCharacters
specifier|public
name|char
index|[]
name|textCharacters
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getTextCharacters
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|textLength
specifier|public
name|int
name|textLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getTextLength
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|textOffset
specifier|public
name|int
name|textOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getTextOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numberValue
specifier|public
name|Number
name|numberValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getNumberValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doShortValue
specifier|public
name|short
name|doShortValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getShortValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doIntValue
specifier|public
name|int
name|doIntValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getIntValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doLongValue
specifier|public
name|long
name|doLongValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getLongValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doFloatValue
specifier|public
name|float
name|doFloatValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getFloatValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doDoubleValue
specifier|public
name|double
name|doDoubleValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getDoubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getBinaryValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
DECL|method|convertNumberType
specifier|private
name|NumberType
name|convertNumberType
parameter_list|(
name|JsonParser
operator|.
name|NumberType
name|numberType
parameter_list|)
block|{
switch|switch
condition|(
name|numberType
condition|)
block|{
case|case
name|INT
case|:
return|return
name|NumberType
operator|.
name|INT
return|;
case|case
name|LONG
case|:
return|return
name|NumberType
operator|.
name|LONG
return|;
case|case
name|FLOAT
case|:
return|return
name|NumberType
operator|.
name|FLOAT
return|;
case|case
name|DOUBLE
case|:
return|return
name|NumberType
operator|.
name|DOUBLE
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No matching token for number_type ["
operator|+
name|numberType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|convertToken
specifier|private
name|Token
name|convertToken
parameter_list|(
name|JsonToken
name|token
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|FIELD_NAME
case|:
return|return
name|Token
operator|.
name|FIELD_NAME
return|;
case|case
name|VALUE_FALSE
case|:
case|case
name|VALUE_TRUE
case|:
return|return
name|Token
operator|.
name|VALUE_BOOLEAN
return|;
case|case
name|VALUE_STRING
case|:
return|return
name|Token
operator|.
name|VALUE_STRING
return|;
case|case
name|VALUE_NUMBER_INT
case|:
case|case
name|VALUE_NUMBER_FLOAT
case|:
return|return
name|Token
operator|.
name|VALUE_NUMBER
return|;
case|case
name|VALUE_NULL
case|:
return|return
name|Token
operator|.
name|VALUE_NULL
return|;
case|case
name|START_OBJECT
case|:
return|return
name|Token
operator|.
name|START_OBJECT
return|;
case|case
name|END_OBJECT
case|:
return|return
name|Token
operator|.
name|END_OBJECT
return|;
case|case
name|START_ARRAY
case|:
return|return
name|Token
operator|.
name|START_ARRAY
return|;
case|case
name|END_ARRAY
case|:
return|return
name|Token
operator|.
name|END_ARRAY
return|;
case|case
name|VALUE_EMBEDDED_OBJECT
case|:
return|return
name|Token
operator|.
name|VALUE_EMBEDDED_OBJECT
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No matching token for json_token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

