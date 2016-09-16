begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.smile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|smile
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
name|common
operator|.
name|xcontent
operator|.
name|XContentGenerator
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
name|XContentType
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|nullValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JsonVsSmileTests
specifier|public
class|class
name|JsonVsSmileTests
extends|extends
name|ESTestCase
block|{
DECL|method|testCompareParsingTokens
specifier|public
name|void
name|testCompareParsingTokens
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesStreamOutput
name|xsonOs
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|XContentGenerator
name|xsonGen
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|SMILE
argument_list|)
operator|.
name|createGenerator
argument_list|(
name|xsonOs
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|jsonOs
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|XContentGenerator
name|jsonGen
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createGenerator
argument_list|(
name|jsonOs
argument_list|)
decl_stmt|;
name|xsonGen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|jsonGen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|xsonGen
operator|.
name|writeStringField
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|jsonGen
operator|.
name|writeStringField
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|xsonGen
operator|.
name|writeFieldName
argument_list|(
literal|"arr"
argument_list|)
expr_stmt|;
name|xsonGen
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
name|jsonGen
operator|.
name|writeFieldName
argument_list|(
literal|"arr"
argument_list|)
expr_stmt|;
name|jsonGen
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
name|xsonGen
operator|.
name|writeNumber
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|jsonGen
operator|.
name|writeNumber
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xsonGen
operator|.
name|writeNull
argument_list|()
expr_stmt|;
name|jsonGen
operator|.
name|writeNull
argument_list|()
expr_stmt|;
name|xsonGen
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
name|jsonGen
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
name|xsonGen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|jsonGen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|xsonGen
operator|.
name|close
argument_list|()
expr_stmt|;
name|jsonGen
operator|.
name|close
argument_list|()
expr_stmt|;
name|verifySameTokens
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|jsonOs
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|SMILE
argument_list|)
operator|.
name|createParser
argument_list|(
name|xsonOs
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySameTokens
specifier|private
name|void
name|verifySameTokens
parameter_list|(
name|XContentParser
name|parser1
parameter_list|,
name|XContentParser
name|parser2
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|XContentParser
operator|.
name|Token
name|token1
init|=
name|parser1
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token2
init|=
name|parser2
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token1
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|token2
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertThat
argument_list|(
name|token1
argument_list|,
name|equalTo
argument_list|(
name|token2
argument_list|)
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|token1
condition|)
block|{
case|case
name|FIELD_NAME
case|:
name|assertThat
argument_list|(
name|parser1
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|parser2
operator|.
name|currentName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|VALUE_STRING
case|:
name|assertThat
argument_list|(
name|parser1
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|parser2
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|VALUE_NUMBER
case|:
name|assertThat
argument_list|(
name|parser1
operator|.
name|numberType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|parser2
operator|.
name|numberType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser1
operator|.
name|numberValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|parser2
operator|.
name|numberValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

