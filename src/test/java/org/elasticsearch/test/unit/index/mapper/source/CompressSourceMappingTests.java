begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.mapper.source
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|mapper
operator|.
name|source
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|compress
operator|.
name|CompressorFactory
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
name|ParsedDocument
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
name|unit
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CompressSourceMappingTests
specifier|public
class|class
name|CompressSourceMappingTests
block|{
annotation|@
name|Test
DECL|method|testCompressDisabled
specifier|public
name|void
name|testCompressDisabled
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
literal|"_source"
argument_list|)
operator|.
name|field
argument_list|(
literal|"compress"
argument_list|,
literal|false
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|documentMapper
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
name|documentMapper
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
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"_source"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|CompressorFactory
operator|.
name|isCompressed
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompressEnabled
specifier|public
name|void
name|testCompressEnabled
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
literal|"_source"
argument_list|)
operator|.
name|field
argument_list|(
literal|"compress"
argument_list|,
literal|true
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|documentMapper
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
name|documentMapper
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
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"_source"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|CompressorFactory
operator|.
name|isCompressed
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompressThreshold
specifier|public
name|void
name|testCompressThreshold
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
literal|"_source"
argument_list|)
operator|.
name|field
argument_list|(
literal|"compress_threshold"
argument_list|,
literal|"200b"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|documentMapper
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
name|documentMapper
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
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"_source"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|CompressorFactory
operator|.
name|isCompressed
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|documentMapper
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
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2 xxxxxxxxxxxxxx yyyyyyyyyyyyyyyyyyy zzzzzzzzzzzzzzzzz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2 xxxxxxxxxxxxxx yyyyyyyyyyyyyyyyyyy zzzzzzzzzzzzzzzzz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2 xxxxxxxxxxxxxx yyyyyyyyyyyyyyyyyyy zzzzzzzzzzzzzzzzz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2 xxxxxxxxxxxxxx yyyyyyyyyyyyyyyyyyy zzzzzzzzzzzzzzzzz"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|bytes
operator|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"_source"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CompressorFactory
operator|.
name|isCompressed
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

