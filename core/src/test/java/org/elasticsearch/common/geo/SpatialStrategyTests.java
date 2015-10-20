begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
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
name|containsString
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

begin_class
DECL|class|SpatialStrategyTests
specifier|public
class|class
name|SpatialStrategyTests
extends|extends
name|ESTestCase
block|{
DECL|method|testValidOrdinals
specifier|public
name|void
name|testValidOrdinals
parameter_list|()
block|{
name|assertThat
argument_list|(
name|SpatialStrategy
operator|.
name|TERM
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SpatialStrategy
operator|.
name|RECURSIVE
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testwriteTo
specifier|public
name|void
name|testwriteTo
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|SpatialStrategy
operator|.
name|TERM
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
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
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|SpatialStrategy
operator|.
name|RECURSIVE
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
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
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testReadFrom
specifier|public
name|void
name|testReadFrom
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
init|(
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
init|)
block|{
name|assertThat
argument_list|(
name|SpatialStrategy
operator|.
name|TERM
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|SpatialStrategy
operator|.
name|TERM
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
init|(
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
init|)
block|{
name|assertThat
argument_list|(
name|SpatialStrategy
operator|.
name|TERM
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|SpatialStrategy
operator|.
name|RECURSIVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testInvalidReadFrom
specifier|public
name|void
name|testInvalidReadFrom
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
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
init|)
block|{
name|SpatialStrategy
operator|.
name|TERM
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Unknown SpatialStrategy ordinal ["
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

