begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.mapper.all
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
name|all
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
name|document
operator|.
name|Document
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
name|bytes
operator|.
name|BytesArray
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
name|lucene
operator|.
name|all
operator|.
name|AllEntries
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
name|lucene
operator|.
name|all
operator|.
name|AllField
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
name|lucene
operator|.
name|all
operator|.
name|AllTokenStream
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
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToBytesFromClasspath
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
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
annotation|@
name|Test
DECL|class|SimpleAllMapperTests
specifier|public
class|class
name|SimpleAllMapperTests
block|{
annotation|@
name|Test
DECL|method|testSimpleAllMappers
specifier|public
name|void
name|testSimpleAllMappers
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|byte
index|[]
name|json
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/test1.json"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|json
argument_list|)
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|AllField
name|field
init|=
operator|(
name|AllField
operator|)
name|doc
operator|.
name|getFieldable
argument_list|(
literal|"_all"
argument_list|)
decl_stmt|;
name|AllEntries
name|allEntries
init|=
operator|(
operator|(
name|AllTokenStream
operator|)
name|field
operator|.
name|tokenStreamValue
argument_list|()
operator|)
operator|.
name|allEntries
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"address.last.location"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"name.last"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"simple1"
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
DECL|method|testSimpleAllMappersWithReparse
specifier|public
name|void
name|testSimpleAllMappersWithReparse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|String
name|builtMapping
init|=
name|docMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
comment|//        System.out.println(builtMapping);
comment|// reparse it
name|DocumentMapper
name|builtDocMapper
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|builtMapping
argument_list|)
decl_stmt|;
name|byte
index|[]
name|json
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/test1.json"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|builtDocMapper
operator|.
name|parse
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|json
argument_list|)
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|AllField
name|field
init|=
operator|(
name|AllField
operator|)
name|doc
operator|.
name|getFieldable
argument_list|(
literal|"_all"
argument_list|)
decl_stmt|;
name|AllEntries
name|allEntries
init|=
operator|(
operator|(
name|AllTokenStream
operator|)
name|field
operator|.
name|tokenStreamValue
argument_list|()
operator|)
operator|.
name|allEntries
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"address.last.location"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"name.last"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"simple1"
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
DECL|method|testSimpleAllMappersWithStore
specifier|public
name|void
name|testSimpleAllMappersWithStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/store-mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|byte
index|[]
name|json
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/test1.json"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|json
argument_list|)
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|AllField
name|field
init|=
operator|(
name|AllField
operator|)
name|doc
operator|.
name|getFieldable
argument_list|(
literal|"_all"
argument_list|)
decl_stmt|;
name|AllEntries
name|allEntries
init|=
operator|(
operator|(
name|AllTokenStream
operator|)
name|field
operator|.
name|tokenStreamValue
argument_list|()
operator|)
operator|.
name|allEntries
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"name.last"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"simple1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|text
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|equalTo
argument_list|(
name|allEntries
operator|.
name|buildText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleAllMappersWithReparseWithStore
specifier|public
name|void
name|testSimpleAllMappersWithReparseWithStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/store-mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|String
name|builtMapping
init|=
name|docMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|builtMapping
argument_list|)
expr_stmt|;
comment|// reparse it
name|DocumentMapper
name|builtDocMapper
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|builtMapping
argument_list|)
decl_stmt|;
name|byte
index|[]
name|json
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/test/unit/index/mapper/all/test1.json"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|builtDocMapper
operator|.
name|parse
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|json
argument_list|)
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|AllField
name|field
init|=
operator|(
name|AllField
operator|)
name|doc
operator|.
name|getFieldable
argument_list|(
literal|"_all"
argument_list|)
decl_stmt|;
name|AllEntries
name|allEntries
init|=
operator|(
operator|(
name|AllTokenStream
operator|)
name|field
operator|.
name|tokenStreamValue
argument_list|()
operator|)
operator|.
name|allEntries
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"name.last"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allEntries
operator|.
name|fields
argument_list|()
operator|.
name|contains
argument_list|(
literal|"simple1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|text
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|equalTo
argument_list|(
name|allEntries
operator|.
name|buildText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

