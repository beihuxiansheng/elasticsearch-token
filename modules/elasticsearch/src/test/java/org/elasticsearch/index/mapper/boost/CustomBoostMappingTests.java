begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.boost
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|boost
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
name|MapperTests
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
name|ParsedDocument
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

begin_class
annotation|@
name|Test
DECL|class|CustomBoostMappingTests
specifier|public
class|class
name|CustomBoostMappingTests
block|{
DECL|method|testCustomBoostValues
annotation|@
name|Test
specifier|public
name|void
name|testCustomBoostValues
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
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"short"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"byte"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"double"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
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
name|mapper
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
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
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
literal|"s_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|"s_value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1l
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|4.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|5.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|6.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|7.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|8.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|"20100101"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|9.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|masterDoc
argument_list|()
operator|.
name|getFieldable
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

