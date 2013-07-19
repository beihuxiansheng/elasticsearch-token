begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.common.io.streams
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|common
operator|.
name|io
operator|.
name|streams
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
name|BytesStreamInput
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
name|HandlesStreamInput
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
name|HandlesStreamOutput
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|HandlesStreamsTests
specifier|public
class|class
name|HandlesStreamsTests
block|{
annotation|@
name|Test
DECL|method|testSharedStringHandles
specifier|public
name|void
name|testSharedStringHandles
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test1
init|=
literal|"test1"
decl_stmt|;
name|String
name|test2
init|=
literal|"test2"
decl_stmt|;
name|String
name|test3
init|=
literal|"test3"
decl_stmt|;
name|String
name|test4
init|=
literal|"test4"
decl_stmt|;
name|String
name|test5
init|=
literal|"test5"
decl_stmt|;
name|String
name|test6
init|=
literal|"test6"
decl_stmt|;
name|BytesStreamOutput
name|bout
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|HandlesStreamOutput
name|out
init|=
operator|new
name|HandlesStreamOutput
argument_list|(
name|bout
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|test3
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|test4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|test4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|test5
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|test6
argument_list|)
expr_stmt|;
name|BytesStreamInput
name|bin
init|=
operator|new
name|BytesStreamInput
argument_list|(
name|bout
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|HandlesStreamInput
name|in
init|=
operator|new
name|HandlesStreamInput
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|s3
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|s4
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|s5
init|=
name|in
operator|.
name|readSharedString
argument_list|()
decl_stmt|;
name|String
name|s6
init|=
name|in
operator|.
name|readSharedString
argument_list|()
decl_stmt|;
name|String
name|s7
init|=
name|in
operator|.
name|readSharedString
argument_list|()
decl_stmt|;
name|String
name|s8
init|=
name|in
operator|.
name|readSharedString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|s1
argument_list|,
name|equalTo
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
argument_list|,
name|equalTo
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s3
argument_list|,
name|equalTo
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s4
argument_list|,
name|equalTo
argument_list|(
name|test3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s5
argument_list|,
name|equalTo
argument_list|(
name|test4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s6
argument_list|,
name|equalTo
argument_list|(
name|test4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s7
argument_list|,
name|equalTo
argument_list|(
name|test5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s8
argument_list|,
name|equalTo
argument_list|(
name|test6
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|s2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s5
argument_list|,
name|sameInstance
argument_list|(
name|s6
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

