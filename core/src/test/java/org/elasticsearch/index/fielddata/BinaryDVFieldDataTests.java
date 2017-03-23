begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectArrayList
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
name|LeafReaderContext
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
name|settings
operator|.
name|Settings
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
name|util
operator|.
name|CollectionUtils
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
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|SourceToParse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|BinaryDVFieldDataTests
specifier|public
class|class
name|BinaryDVFieldDataTests
extends|extends
name|AbstractFieldDataTestCase
block|{
annotation|@
name|Override
DECL|method|hasDocValues
specifier|protected
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|testDocValue
specifier|public
name|void
name|testDocValue
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
literal|"test"
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
literal|"binary"
argument_list|)
operator|.
name|field
argument_list|(
literal|"doc_values"
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
decl_stmt|;
specifier|final
name|DocumentMapper
name|mapper
init|=
name|mapperService
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ObjectArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|bytesList1
init|=
operator|new
name|ObjectArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|bytesList1
operator|.
name|add
argument_list|(
name|randomBytes
argument_list|()
argument_list|)
expr_stmt|;
name|bytesList1
operator|.
name|add
argument_list|(
name|randomBytes
argument_list|()
argument_list|)
expr_stmt|;
name|XContentBuilder
name|doc
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
literal|"field"
argument_list|)
operator|.
name|value
argument_list|(
name|bytesList1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|value
argument_list|(
name|bytesList1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|ParsedDocument
name|d
init|=
name|mapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|,
name|doc
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
operator|.
name|rootDoc
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes1
init|=
name|randomBytes
argument_list|()
decl_stmt|;
name|doc
operator|=
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
name|bytes1
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|d
operator|=
name|mapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|,
name|doc
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
operator|.
name|rootDoc
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|d
operator|=
name|mapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"3"
argument_list|,
name|doc
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
operator|.
name|rootDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// test remove duplicate value
name|ObjectArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|bytesList2
init|=
operator|new
name|ObjectArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|bytesList2
operator|.
name|add
argument_list|(
name|randomBytes
argument_list|()
argument_list|)
expr_stmt|;
name|bytesList2
operator|.
name|add
argument_list|(
name|randomBytes
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
literal|"field"
argument_list|)
operator|.
name|value
argument_list|(
name|bytesList2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|value
argument_list|(
name|bytesList2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|value
argument_list|(
name|bytesList2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|d
operator|=
name|mapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|,
name|doc
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
operator|.
name|rootDoc
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|readers
init|=
name|refreshReader
argument_list|()
decl_stmt|;
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
init|=
name|getForField
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|reader
range|:
name|readers
control|)
block|{
name|AtomicFieldData
name|fieldData
init|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|SortedBinaryDocValues
name|bytesValues
init|=
name|fieldData
operator|.
name|getBytesValues
argument_list|()
decl_stmt|;
name|CollectionUtils
operator|.
name|sortAndDedup
argument_list|(
name|bytesList1
argument_list|)
expr_stmt|;
name|bytesValues
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|count
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
name|bytesValues
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytesList1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|valueAt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytesList1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bytesValues
operator|.
name|setDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|count
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
name|bytesValues
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bytesValues
operator|.
name|setDocument
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|count
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionUtils
operator|.
name|sortAndDedup
argument_list|(
name|bytesList2
argument_list|)
expr_stmt|;
name|bytesValues
operator|.
name|setDocument
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|count
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
name|bytesValues
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytesList2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|valueAt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytesList2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomBytes
specifier|private
name|byte
index|[]
name|randomBytes
parameter_list|()
block|{
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldDataType
specifier|protected
name|String
name|getFieldDataType
parameter_list|()
block|{
return|return
literal|"binary"
return|;
block|}
block|}
end_class

end_unit

