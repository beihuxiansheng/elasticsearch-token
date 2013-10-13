begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
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
name|settings
operator|.
name|ImmutableSettings
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
name|ElasticSearchTestCase
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
name|assertThat
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
name|not
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CompressedStringTests
specifier|public
class|class
name|CompressedStringTests
extends|extends
name|ElasticSearchTestCase
block|{
annotation|@
name|Test
DECL|method|simpleTestsLZF
specifier|public
name|void
name|simpleTestsLZF
parameter_list|()
throws|throws
name|IOException
block|{
name|simpleTests
argument_list|(
literal|"lzf"
argument_list|)
expr_stmt|;
block|}
DECL|method|simpleTests
specifier|public
name|void
name|simpleTests
parameter_list|(
name|String
name|compressor
parameter_list|)
throws|throws
name|IOException
block|{
name|CompressorFactory
operator|.
name|configure
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"compress.default.type"
argument_list|,
name|compressor
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|str
init|=
literal|"this is a simple string"
decl_stmt|;
name|CompressedString
name|cstr
init|=
operator|new
name|CompressedString
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cstr
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|CompressedString
argument_list|(
name|str
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|cstr
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|str2
init|=
literal|"this is a simple string 2"
decl_stmt|;
name|CompressedString
name|cstr2
init|=
operator|new
name|CompressedString
argument_list|(
name|str2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cstr2
operator|.
name|string
argument_list|()
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|str
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|CompressedString
argument_list|(
name|str2
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|cstr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|CompressedString
argument_list|(
name|str2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|cstr2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

