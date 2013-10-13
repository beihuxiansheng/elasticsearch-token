begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.multifield.merge
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|multifield
operator|.
name|merge
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
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
name|bytes
operator|.
name|BytesReference
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
name|DocumentMapperParser
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
name|MapperTestUtils
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
name|util
operator|.
name|Arrays
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
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|DocumentMapper
operator|.
name|MergeFlags
operator|.
name|mergeFlags
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
DECL|class|JavaMultiFieldMergeTests
specifier|public
class|class
name|JavaMultiFieldMergeTests
extends|extends
name|ElasticSearchTestCase
block|{
annotation|@
name|Test
DECL|method|testMergeMultiField
specifier|public
name|void
name|testMergeMultiField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/multifield/merge/test-mapping1.json"
argument_list|)
decl_stmt|;
name|DocumentMapperParser
name|parser
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|parser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.indexed"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|BytesReference
name|json
init|=
operator|new
name|BytesArray
argument_list|(
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/multifield/merge/test-data.json"
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|json
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|IndexableField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|f
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"name.indexed"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|mapping
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/multifield/merge/test-mapping2.json"
argument_list|)
expr_stmt|;
name|DocumentMapper
name|docMapper2
init|=
name|parser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|DocumentMapper
operator|.
name|MergeResult
name|mergeResult
init|=
name|docMapper
operator|.
name|merge
argument_list|(
name|docMapper2
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
argument_list|)
argument_list|,
name|mergeResult
operator|.
name|hasConflicts
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|docMapper
operator|.
name|merge
argument_list|(
name|docMapper2
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|name
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.indexed"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed2"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed3"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
operator|new
name|BytesArray
argument_list|(
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/multifield/merge/test-data.json"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|docMapper
operator|.
name|parse
argument_list|(
name|json
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"name.indexed"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|mapping
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/multifield/merge/test-mapping3.json"
argument_list|)
expr_stmt|;
name|DocumentMapper
name|docMapper3
init|=
name|parser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|mergeResult
operator|=
name|docMapper
operator|.
name|merge
argument_list|(
name|docMapper3
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
argument_list|)
argument_list|,
name|mergeResult
operator|.
name|hasConflicts
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|docMapper
operator|.
name|merge
argument_list|(
name|docMapper3
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|name
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.indexed"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed2"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed3"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|mapping
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/multifield/merge/test-mapping4.json"
argument_list|)
expr_stmt|;
name|DocumentMapper
name|docMapper4
init|=
name|parser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|mergeResult
operator|=
name|docMapper
operator|.
name|merge
argument_list|(
name|docMapper4
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
argument_list|)
argument_list|,
name|mergeResult
operator|.
name|hasConflicts
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|docMapper
operator|.
name|merge
argument_list|(
name|docMapper4
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|name
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.indexed"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed2"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name.not_indexed3"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

