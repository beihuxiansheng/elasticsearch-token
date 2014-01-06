begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|hamcrest
operator|.
name|MatcherAssert
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
DECL|class|ByteSizeValueTests
specifier|public
class|class
name|ByteSizeValueTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testActual
specifier|public
name|void
name|testActual
parameter_list|()
block|{
name|MatcherAssert
operator|.
name|assertThat
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|4
argument_list|,
name|ByteSizeUnit
operator|.
name|GB
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4294967296l
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|assertThat
argument_list|(
name|ByteSizeUnit
operator|.
name|BYTES
operator|.
name|toBytes
argument_list|(
literal|10
argument_list|)
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|10
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeUnit
operator|.
name|KB
operator|.
name|toKB
argument_list|(
literal|10
argument_list|)
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|10
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
operator|.
name|kb
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeUnit
operator|.
name|MB
operator|.
name|toMB
argument_list|(
literal|10
argument_list|)
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|10
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
operator|.
name|mb
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeUnit
operator|.
name|GB
operator|.
name|toGB
argument_list|(
literal|10
argument_list|)
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|10
argument_list|,
name|ByteSizeUnit
operator|.
name|GB
argument_list|)
operator|.
name|gb
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertThat
argument_list|(
literal|"10b"
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|10
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5kb"
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|1024
operator|*
literal|1.5
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5mb"
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|1024
operator|*
literal|1.5
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5gb"
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|1024
operator|*
literal|1.5
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1536gb"
argument_list|,
name|is
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|1024
operator|*
literal|1.5
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|GB
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParsing
specifier|public
name|void
name|testParsing
parameter_list|()
block|{
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"12gb"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"12gb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"12G"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"12gb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"12GB"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"12gb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"12M"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"12mb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"1b"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"23kb"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"23kb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"23k"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"23kb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"23"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"23b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ElasticsearchParseException
operator|.
name|class
argument_list|)
DECL|method|testFailOnEmptyParsing
specifier|public
name|void
name|testFailOnEmptyParsing
parameter_list|()
block|{
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"23kb"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ElasticsearchParseException
operator|.
name|class
argument_list|)
DECL|method|testFailOnEmptyNumberParsing
specifier|public
name|void
name|testFailOnEmptyNumberParsing
parameter_list|()
block|{
name|assertThat
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"g"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"23b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

