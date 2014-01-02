begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
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
name|DoubleOpenHashSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|LongOpenHashSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectOpenHashSet
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
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
name|index
operator|.
name|Index
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
name|IndexFieldData
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
name|IndexFieldDataService
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
name|ContentPath
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
name|Mapper
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
name|core
operator|.
name|DoubleFieldMapper
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
name|core
operator|.
name|LongFieldMapper
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
name|core
operator|.
name|NumberFieldMapper
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
name|core
operator|.
name|StringFieldMapper
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
name|fielddata
operator|.
name|breaker
operator|.
name|DummyCircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|junit
operator|.
name|After
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
name|ArrayList
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
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FieldDataTermsFilterTests
specifier|public
class|class
name|FieldDataTermsFilterTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|ifdService
specifier|protected
name|IndexFieldDataService
name|ifdService
decl_stmt|;
DECL|field|writer
specifier|protected
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|reader
specifier|protected
name|AtomicReader
name|reader
decl_stmt|;
DECL|field|strMapper
specifier|protected
name|StringFieldMapper
name|strMapper
decl_stmt|;
DECL|field|lngMapper
specifier|protected
name|LongFieldMapper
name|lngMapper
decl_stmt|;
DECL|field|dblMapper
specifier|protected
name|DoubleFieldMapper
name|dblMapper
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// setup field mappers
name|strMapper
operator|=
operator|new
name|StringFieldMapper
operator|.
name|Builder
argument_list|(
literal|"str_value"
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
literal|null
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|lngMapper
operator|=
operator|new
name|LongFieldMapper
operator|.
name|Builder
argument_list|(
literal|"lng_value"
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
literal|null
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dblMapper
operator|=
operator|new
name|DoubleFieldMapper
operator|.
name|Builder
argument_list|(
literal|"dbl_value"
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
literal|null
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// create index and fielddata service
name|ifdService
operator|=
operator|new
name|IndexFieldDataService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
operator|new
name|DummyCircuitBreakerService
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|=
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
operator|new
name|StandardAnalyzer
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
literal|10
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|strMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
literal|"str"
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|LongField
argument_list|(
name|lngMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
name|dblMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|Double
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
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
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
DECL|method|getFieldData
specifier|protected
parameter_list|<
name|IFD
extends|extends
name|IndexFieldData
parameter_list|>
name|IFD
name|getFieldData
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|)
block|{
return|return
name|ifdService
operator|.
name|getForField
argument_list|(
name|fieldMapper
argument_list|)
return|;
block|}
DECL|method|getFieldData
specifier|protected
parameter_list|<
name|IFD
extends|extends
name|IndexNumericFieldData
parameter_list|>
name|IFD
name|getFieldData
parameter_list|(
name|NumberFieldMapper
name|fieldMapper
parameter_list|)
block|{
return|return
name|ifdService
operator|.
name|getForField
argument_list|(
name|fieldMapper
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testBytes
specifier|public
name|void
name|testBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|docs
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|ObjectOpenHashSet
argument_list|<
name|BytesRef
argument_list|>
name|hTerms
init|=
operator|new
name|ObjectOpenHashSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|cTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|(
literal|"str"
operator|+
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|hTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|cTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|FieldDataTermsFilter
name|hFilter
init|=
name|FieldDataTermsFilter
operator|.
name|newBytes
argument_list|(
name|getFieldData
argument_list|(
name|strMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|FixedBitSet
name|result
init|=
operator|new
name|FixedBitSet
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// filter from mapper
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|strMapper
operator|.
name|termsFilter
argument_list|(
name|ifdService
argument_list|,
name|cTerms
argument_list|,
literal|null
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// filter on a numeric field using BytesRef terms
comment|// should not match any docs
name|hFilter
operator|=
name|FieldDataTermsFilter
operator|.
name|newBytes
argument_list|(
name|getFieldData
argument_list|(
name|lngMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// filter on a numeric field using BytesRef terms
comment|// should not match any docs
name|hFilter
operator|=
name|FieldDataTermsFilter
operator|.
name|newBytes
argument_list|(
name|getFieldData
argument_list|(
name|dblMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLongs
specifier|public
name|void
name|testLongs
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|docs
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|LongOpenHashSet
name|hTerms
init|=
operator|new
name|LongOpenHashSet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|cTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|long
name|term
init|=
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|hTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|cTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|FieldDataTermsFilter
name|hFilter
init|=
name|FieldDataTermsFilter
operator|.
name|newLongs
argument_list|(
name|getFieldData
argument_list|(
name|lngMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|FixedBitSet
name|result
init|=
operator|new
name|FixedBitSet
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// filter from mapper
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|lngMapper
operator|.
name|termsFilter
argument_list|(
name|ifdService
argument_list|,
name|cTerms
argument_list|,
literal|null
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hFilter
operator|=
name|FieldDataTermsFilter
operator|.
name|newLongs
argument_list|(
name|getFieldData
argument_list|(
name|dblMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDoubles
specifier|public
name|void
name|testDoubles
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|docs
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|DoubleOpenHashSet
name|hTerms
init|=
operator|new
name|DoubleOpenHashSet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|cTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|Double
argument_list|>
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|double
name|term
init|=
name|Double
operator|.
name|valueOf
argument_list|(
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|hTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|cTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|FieldDataTermsFilter
name|hFilter
init|=
name|FieldDataTermsFilter
operator|.
name|newDoubles
argument_list|(
name|getFieldData
argument_list|(
name|dblMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|FixedBitSet
name|result
init|=
operator|new
name|FixedBitSet
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// filter from mapper
name|result
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|or
argument_list|(
name|dblMapper
operator|.
name|termsFilter
argument_list|(
name|ifdService
argument_list|,
name|cTerms
argument_list|,
literal|null
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|docs
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hFilter
operator|=
name|FieldDataTermsFilter
operator|.
name|newDoubles
argument_list|(
name|getFieldData
argument_list|(
name|lngMapper
argument_list|)
argument_list|,
name|hTerms
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|hFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoTerms
specifier|public
name|void
name|testNoTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldDataTermsFilter
name|hFilterBytes
init|=
name|FieldDataTermsFilter
operator|.
name|newBytes
argument_list|(
name|getFieldData
argument_list|(
name|strMapper
argument_list|)
argument_list|,
operator|new
name|ObjectOpenHashSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|FieldDataTermsFilter
name|hFilterLongs
init|=
name|FieldDataTermsFilter
operator|.
name|newLongs
argument_list|(
name|getFieldData
argument_list|(
name|lngMapper
argument_list|)
argument_list|,
operator|new
name|LongOpenHashSet
argument_list|()
argument_list|)
decl_stmt|;
name|FieldDataTermsFilter
name|hFilterDoubles
init|=
name|FieldDataTermsFilter
operator|.
name|newDoubles
argument_list|(
name|getFieldData
argument_list|(
name|dblMapper
argument_list|)
argument_list|,
operator|new
name|DoubleOpenHashSet
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|hFilterBytes
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|hFilterLongs
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|hFilterDoubles
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

