begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.slice
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|slice
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
name|DocValuesType
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
name|IndexReader
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|Query
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
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
name|ParseFieldMatcher
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
name|NamedWriteableAwareStreamInput
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
name|NamedWriteableRegistry
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
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|MatchNoDocsQuery
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
name|XContentParser
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
name|XContentHelper
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
name|index
operator|.
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|MappedFieldType
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
name|internal
operator|.
name|UidFieldMapper
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
name|query
operator|.
name|MatchAllQueryBuilder
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
name|query
operator|.
name|QueryParseContext
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
name|query
operator|.
name|QueryParser
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
name|query
operator|.
name|QueryShardContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesRegistry
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
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|instanceOf
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|SliceBuilderTests
specifier|public
class|class
name|SliceBuilderTests
extends|extends
name|ESTestCase
block|{
DECL|field|MAX_SLICE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_SLICE
init|=
literal|20
decl_stmt|;
DECL|field|namedWriteableRegistry
specifier|private
specifier|static
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|indicesQueriesRegistry
specifier|private
specifier|static
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
comment|/**      * setup for the whole base test class      */
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|()
expr_stmt|;
name|indicesQueriesRegistry
operator|=
operator|new
name|IndicesQueriesRegistry
argument_list|()
expr_stmt|;
name|QueryParser
argument_list|<
name|MatchAllQueryBuilder
argument_list|>
name|parser
init|=
name|MatchAllQueryBuilder
operator|::
name|fromXContent
decl_stmt|;
name|indicesQueriesRegistry
operator|.
name|register
argument_list|(
name|parser
argument_list|,
name|MatchAllQueryBuilder
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|namedWriteableRegistry
operator|=
literal|null
expr_stmt|;
name|indicesQueriesRegistry
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|randomSliceBuilder
specifier|private
name|SliceBuilder
name|randomSliceBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|max
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
name|MAX_SLICE
argument_list|)
decl_stmt|;
name|int
name|id
init|=
name|randomInt
argument_list|(
name|max
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
decl_stmt|;
return|return
operator|new
name|SliceBuilder
argument_list|(
name|field
argument_list|,
name|id
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|serializedCopy
specifier|private
specifier|static
name|SliceBuilder
name|serializedCopy
parameter_list|(
name|SliceBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|original
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
return|return
operator|new
name|SliceBuilder
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|SliceBuilder
name|original
init|=
name|randomSliceBuilder
argument_list|()
decl_stmt|;
name|SliceBuilder
name|deserialized
init|=
name|serializedCopy
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserialized
argument_list|,
name|original
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|original
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserialized
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|Exception
block|{
name|SliceBuilder
name|firstBuilder
init|=
name|randomSliceBuilder
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"sliceBuilder is equal to null"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"sliceBuilder is equal to incompatible type"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not equal to self"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
name|firstBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"same searchFrom's hashcode returns different values if called multiple times"
argument_list|,
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SliceBuilder
name|secondBuilder
init|=
name|serializedCopy
argument_list|(
name|firstBuilder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not equal to self"
argument_list|,
name|secondBuilder
operator|.
name|equals
argument_list|(
name|secondBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not equal to its copy"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
name|secondBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|secondBuilder
operator|.
name|equals
argument_list|(
name|firstBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"sliceBuilder copy's hashcode is different from original hashcode"
argument_list|,
name|secondBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SliceBuilder
name|thirdBuilder
init|=
name|serializedCopy
argument_list|(
name|secondBuilder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not equal to self"
argument_list|,
name|thirdBuilder
operator|.
name|equals
argument_list|(
name|thirdBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not equal to its copy"
argument_list|,
name|secondBuilder
operator|.
name|equals
argument_list|(
name|thirdBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"sliceBuilder copy's hashcode is different from original hashcode"
argument_list|,
name|secondBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not transitive"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
name|thirdBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"sliceBuilder copy's hashcode is different from original hashcode"
argument_list|,
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not symmetric"
argument_list|,
name|thirdBuilder
operator|.
name|equals
argument_list|(
name|secondBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sliceBuilder is not symmetric"
argument_list|,
name|thirdBuilder
operator|.
name|equals
argument_list|(
name|firstBuilder
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|Exception
block|{
name|SliceBuilder
name|sliceBuilder
init|=
name|randomSliceBuilder
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|sliceBuilder
operator|.
name|innerToXContent
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|shuffleXContent
argument_list|(
name|builder
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|SliceBuilder
name|secondSliceBuilder
init|=
name|SliceBuilder
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|sliceBuilder
argument_list|,
name|secondSliceBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sliceBuilder
argument_list|,
name|secondSliceBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sliceBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|secondSliceBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidArguments
specifier|public
name|void
name|testInvalidArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SliceBuilder
argument_list|(
literal|"field"
argument_list|,
operator|-
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"id must be greater than or equal to 0"
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SliceBuilder
argument_list|(
literal|"field"
argument_list|,
literal|10
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"max must be greater than 1"
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SliceBuilder
argument_list|(
literal|"field"
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"max must be greater than 1"
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SliceBuilder
argument_list|(
literal|"field"
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"max must be greater than id"
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SliceBuilder
argument_list|(
literal|"field"
argument_list|,
literal|1000
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"max must be greater than id"
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SliceBuilder
argument_list|(
literal|"field"
argument_list|,
literal|1001
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"max must be greater than id"
argument_list|)
expr_stmt|;
block|}
DECL|method|testToFilter
specifier|public
name|void
name|testToFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
try|try
init|(
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|QueryShardContext
name|context
init|=
name|mock
argument_list|(
name|QueryShardContext
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|MappedFieldType
name|fieldType
init|=
operator|new
name|MappedFieldType
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryShardContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|fieldType
operator|.
name|setName
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setHasDocValues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|fieldMapper
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|SliceBuilder
name|builder
init|=
operator|new
name|SliceBuilder
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|TermsSliceQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|IndexReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|when
argument_list|(
name|context
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|MappedFieldType
name|fieldType
init|=
operator|new
name|MappedFieldType
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryShardContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|fieldType
operator|.
name|setName
argument_list|(
literal|"field_doc_values"
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setHasDocValues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|fieldMapper
argument_list|(
literal|"field_doc_values"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|IndexNumericFieldData
name|fd
init|=
name|mock
argument_list|(
name|IndexNumericFieldData
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|SliceBuilder
name|builder
init|=
operator|new
name|SliceBuilder
argument_list|(
literal|"field_doc_values"
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|DocValuesSliceQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|IndexReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|when
argument_list|(
name|context
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// numSlices> numShards
name|int
name|numSlices
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|numShards
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|9
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|AtomicInteger
argument_list|>
name|numSliceMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSlices
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numShards
condition|;
name|j
operator|++
control|)
block|{
name|SliceBuilder
name|slice
init|=
operator|new
name|SliceBuilder
argument_list|(
literal|"_uid"
argument_list|,
name|i
argument_list|,
name|numSlices
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|slice
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
name|j
argument_list|,
name|numShards
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|TermsSliceQuery
operator|||
name|q
operator|instanceof
name|MatchAllDocsQuery
condition|)
block|{
name|AtomicInteger
name|count
init|=
name|numSliceMap
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|count
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numSliceMap
operator|.
name|put
argument_list|(
name|j
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|MatchAllDocsQuery
condition|)
block|{
name|assertThat
argument_list|(
name|count
operator|.
name|get
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
else|else
block|{
name|assertThat
argument_list|(
name|q
argument_list|,
name|instanceOf
argument_list|(
name|MatchNoDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|AtomicInteger
argument_list|>
name|e
range|:
name|numSliceMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|total
operator|+=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|total
argument_list|,
name|equalTo
argument_list|(
name|numSlices
argument_list|)
argument_list|)
expr_stmt|;
comment|// numShards> numSlices
name|numShards
operator|=
name|randomIntBetween
argument_list|(
literal|4
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|numSlices
operator|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
name|numShards
operator|-
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|targetShards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSlices
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numShards
condition|;
name|j
operator|++
control|)
block|{
name|SliceBuilder
name|slice
init|=
operator|new
name|SliceBuilder
argument_list|(
literal|"_uid"
argument_list|,
name|i
argument_list|,
name|numSlices
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|slice
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
name|j
argument_list|,
name|numShards
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|MatchNoDocsQuery
operator|==
literal|false
condition|)
block|{
name|assertThat
argument_list|(
name|q
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|targetShards
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertThat
argument_list|(
name|targetShards
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numShards
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|targetShards
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numShards
argument_list|)
argument_list|)
expr_stmt|;
comment|// numShards == numSlices
name|numShards
operator|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|numSlices
operator|=
name|numShards
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSlices
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numShards
condition|;
name|j
operator|++
control|)
block|{
name|SliceBuilder
name|slice
init|=
operator|new
name|SliceBuilder
argument_list|(
literal|"_uid"
argument_list|,
name|i
argument_list|,
name|numSlices
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|slice
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
name|j
argument_list|,
name|numShards
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|j
condition|)
block|{
name|assertThat
argument_list|(
name|q
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|q
argument_list|,
name|instanceOf
argument_list|(
name|MatchNoDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
try|try
init|(
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|MappedFieldType
name|fieldType
init|=
operator|new
name|MappedFieldType
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryShardContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|fieldType
operator|.
name|setName
argument_list|(
literal|"field_without_doc_values"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|fieldMapper
argument_list|(
literal|"field_without_doc_values"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|SliceBuilder
name|builder
init|=
operator|new
name|SliceBuilder
argument_list|(
literal|"field_without_doc_values"
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|exc
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|builder
operator|.
name|toFilter
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|exc
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"cannot load numeric doc values"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

