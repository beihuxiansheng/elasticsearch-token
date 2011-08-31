begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|builder
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
DECL|class|BuilderRawFieldTests
specifier|public
class|class
name|BuilderRawFieldTests
block|{
DECL|method|testJsonRawField
annotation|@
name|Test
specifier|public
name|void
name|testJsonRawField
parameter_list|()
throws|throws
name|IOException
block|{
name|testRawField
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
block|}
DECL|method|testSmileRawField
annotation|@
name|Test
specifier|public
name|void
name|testSmileRawField
parameter_list|()
throws|throws
name|IOException
block|{
name|testRawField
argument_list|(
name|XContentType
operator|.
name|SMILE
argument_list|)
expr_stmt|;
block|}
DECL|method|testRawField
specifier|private
name|void
name|testRawField
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
name|rawField
argument_list|(
literal|"_source"
argument_list|,
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|type
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"s_field"
argument_list|,
literal|"s_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|copiedBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|type
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|copiedBytes
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
name|XContentParser
operator|.
name|Token
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
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field1"
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
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
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
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_source"
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
name|XContentParser
operator|.
name|Token
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
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"s_field"
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
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"s_value"
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
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
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
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field2"
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
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
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
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

