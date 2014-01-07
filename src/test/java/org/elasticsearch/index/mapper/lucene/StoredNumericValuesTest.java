begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|lucene
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
name|*
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
name|search
operator|.
name|IndexSearcher
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
name|Numbers
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
name|Lucene
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
name|fieldvisitor
operator|.
name|CustomFieldsVisitor
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
name|*
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
DECL|class|StoredNumericValuesTest
specifier|public
class|class
name|StoredNumericValuesTest
block|{
annotation|@
name|Test
DECL|method|testBytesAndNumericRepresentation
specifier|public
name|void
name|testBytesAndNumericRepresentation
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
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
name|MapperTestUtils
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
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|1.1
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|value
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|(
literal|2
argument_list|)
operator|.
name|value
argument_list|(
literal|3
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
argument_list|,
name|doc
operator|.
name|analyzer
argument_list|()
argument_list|)
expr_stmt|;
comment|// Indexing a doc in the old way
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|NumericType
operator|.
name|INT
argument_list|)
expr_stmt|;
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"field1"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Numbers
operator|.
name|intToBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"field2"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Numbers
operator|.
name|floatToBytes
argument_list|(
literal|1.1f
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"field3"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Numbers
operator|.
name|longToBytes
argument_list|(
literal|1l
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"field3"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Numbers
operator|.
name|longToBytes
argument_list|(
literal|2l
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"field3"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Numbers
operator|.
name|longToBytes
argument_list|(
literal|3l
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"field1"
argument_list|,
literal|"field2"
argument_list|,
literal|"field3"
argument_list|)
argument_list|)
decl_stmt|;
name|CustomFieldsVisitor
name|fieldsVisitor
init|=
operator|new
name|CustomFieldsVisitor
argument_list|(
name|fields
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
name|fieldsVisitor
argument_list|)
expr_stmt|;
name|fieldsVisitor
operator|.
name|postProcess
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldsVisitor
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
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Float
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1.1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
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
operator|(
name|Long
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Long
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Long
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3l
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure the doc gets loaded as if it was stored in the new way
name|fieldsVisitor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|doc
argument_list|(
literal|1
argument_list|,
name|fieldsVisitor
argument_list|)
expr_stmt|;
name|fieldsVisitor
operator|.
name|postProcess
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldsVisitor
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
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Integer
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Float
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1.1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
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
operator|(
name|Long
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Long
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Long
operator|)
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3l
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

