begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|analysis
operator|.
name|MockAnalyzer
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|LeafReader
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
name|SortedNumericDocValues
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|common
operator|.
name|compress
operator|.
name|CompressedXContent
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
name|ToXContent
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
name|index
operator|.
name|IndexService
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
name|FieldMapper
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
name|MapperService
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
name|ESSingleNodeTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
DECL|class|BooleanFieldMapperTests
specifier|public
class|class
name|BooleanFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|indexService
name|IndexService
name|indexService
decl_stmt|;
DECL|field|parser
name|DocumentMapperParser
name|parser
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|indexService
operator|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|parser
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
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
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
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
name|defaultMapper
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"field"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
init|;
name|IndexWriter
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|getRandom
argument_list|()
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|w
operator|.
name|addDocuments
argument_list|(
name|doc
operator|.
name|docs
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
init|)
block|{
specifier|final
name|LeafReader
name|leaf
init|=
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|// boolean fields are indexed and have doc values by default
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"T"
argument_list|)
argument_list|,
name|leaf
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|SortedNumericDocValues
name|values
init|=
name|leaf
operator|.
name|getSortedNumericDocValues
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|values
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
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
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
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
name|defaultMapper
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|FieldMapper
name|mapper
init|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":{\"type\":\"boolean\"}}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
comment|// now change some parameters
name|mapping
operator|=
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
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
argument_list|)
operator|.
name|field
argument_list|(
literal|"doc_values"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|field
argument_list|(
literal|"null_value"
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
expr_stmt|;
name|defaultMapper
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|mapper
operator|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|mapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":{\"type\":\"boolean\",\"doc_values\":false,\"null_value\":true}}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiFields
specifier|public
name|void
name|testMultiFields
parameter_list|()
throws|throws
name|IOException
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
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"as_string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
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
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BytesReference
name|source
init|=
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
literal|"field"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field.as_string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

