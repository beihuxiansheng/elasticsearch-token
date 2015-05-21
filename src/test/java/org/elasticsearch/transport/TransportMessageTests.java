begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
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
name|test
operator|.
name|ElasticsearchTestCase
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
name|is
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportMessageTests
specifier|public
class|class
name|TransportMessageTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
operator|new
name|Message
argument_list|()
decl_stmt|;
name|message
operator|.
name|putHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|message
operator|.
name|putHeader
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|message
operator|.
name|putInContext
argument_list|(
literal|"key3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|out
operator|.
name|setVersion
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|in
operator|.
name|setVersion
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|Message
argument_list|()
expr_stmt|;
name|message
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
operator|.
name|getHeaders
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|message
operator|.
name|getHeader
argument_list|(
literal|"key1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|message
operator|.
name|getHeader
argument_list|(
literal|"key2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
operator|.
name|isContextEmpty
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure that casting is not needed
name|String
name|key1
init|=
name|message
operator|.
name|getHeader
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key1
argument_list|,
name|is
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyHeadersAndContext
specifier|public
name|void
name|testCopyHeadersAndContext
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|m1
init|=
operator|new
name|Message
argument_list|()
decl_stmt|;
name|m1
operator|.
name|putHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|m1
operator|.
name|putHeader
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|m1
operator|.
name|putInContext
argument_list|(
literal|"key3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|Message
name|m2
init|=
operator|new
name|Message
argument_list|(
name|m1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|m2
operator|.
name|getHeaders
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|m2
operator|.
name|getHeader
argument_list|(
literal|"key1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|m2
operator|.
name|getHeader
argument_list|(
literal|"key2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|m2
operator|.
name|getFromContext
argument_list|(
literal|"key3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value3"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure that casting is not needed
name|String
name|key3
init|=
name|m2
operator|.
name|getFromContext
argument_list|(
literal|"key3"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key3
argument_list|,
name|is
argument_list|(
literal|"value3"
argument_list|)
argument_list|)
expr_stmt|;
name|testContext
argument_list|(
name|m2
argument_list|,
literal|"key3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
block|}
comment|// ensure that generic arg like this is not needed: TransportMessage<?> transportMessage
DECL|method|testContext
specifier|private
name|void
name|testContext
parameter_list|(
name|TransportMessage
name|transportMessage
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|expectedValue
parameter_list|)
block|{
name|String
name|result
init|=
name|transportMessage
operator|.
name|getFromContext
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|is
argument_list|(
name|expectedValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|Message
specifier|private
specifier|static
class|class
name|Message
extends|extends
name|TransportMessage
argument_list|<
name|Message
argument_list|>
block|{
DECL|method|Message
specifier|private
name|Message
parameter_list|()
block|{         }
DECL|method|Message
specifier|private
name|Message
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

