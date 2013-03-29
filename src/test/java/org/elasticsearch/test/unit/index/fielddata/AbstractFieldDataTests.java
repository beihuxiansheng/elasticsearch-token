begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.fielddata
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
name|fielddata
package|;
end_package

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
name|sameInstance
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
name|List
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
name|index
operator|.
name|AtomicReader
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
name|AtomicReaderContext
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
name|SlowCompositeReaderWrapper
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
name|DoubleValues
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
name|FieldDataType
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
name|StringValues
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
name|testng
operator|.
name|annotations
operator|.
name|AfterMethod
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
name|BeforeMethod
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

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|AbstractFieldDataTests
specifier|public
specifier|abstract
class|class
name|AbstractFieldDataTests
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
DECL|field|readerContext
specifier|protected
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|method|getFieldDataType
specifier|protected
specifier|abstract
name|FieldDataType
name|getFieldDataType
parameter_list|()
function_decl|;
DECL|method|getForField
specifier|public
parameter_list|<
name|IFD
extends|extends
name|IndexFieldData
parameter_list|>
name|IFD
name|getForField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|ifdService
operator|.
name|getForField
argument_list|(
operator|new
name|FieldMapper
operator|.
name|Names
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|getFieldDataType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|BeforeMethod
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|refreshReader
specifier|protected
name|AtomicReaderContext
name|refreshReader
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|readerContext
operator|!=
literal|null
condition|)
block|{
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|AtomicReader
name|reader
init|=
operator|new
name|SlowCompositeReaderWrapper
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
decl_stmt|;
name|readerContext
operator|=
name|reader
operator|.
name|getContext
argument_list|()
expr_stmt|;
return|return
name|readerContext
return|;
block|}
annotation|@
name|AfterMethod
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|readerContext
operator|!=
literal|null
condition|)
block|{
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
block|}
DECL|class|StringValuesVerifierProc
specifier|public
specifier|static
class|class
name|StringValuesVerifierProc
implements|implements
name|StringValues
operator|.
name|ValueInDocProc
block|{
DECL|field|MISSING
specifier|private
specifier|static
specifier|final
name|String
name|MISSING
init|=
operator|new
name|String
argument_list|()
decl_stmt|;
DECL|field|docId
specifier|private
specifier|final
name|int
name|docId
decl_stmt|;
DECL|field|expected
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|idx
specifier|private
name|int
name|idx
decl_stmt|;
DECL|method|StringValuesVerifierProc
name|StringValuesVerifierProc
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
block|}
DECL|method|addExpected
specifier|public
name|StringValuesVerifierProc
name|addExpected
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addMissing
specifier|public
name|StringValuesVerifierProc
name|addMissing
parameter_list|()
block|{
name|expected
operator|.
name|add
argument_list|(
name|MISSING
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|onValue
specifier|public
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|assertThat
argument_list|(
name|docId
argument_list|,
name|equalTo
argument_list|(
name|this
operator|.
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|value
argument_list|,
name|equalTo
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMissing
specifier|public
name|void
name|onMissing
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|assertThat
argument_list|(
name|docId
argument_list|,
name|equalTo
argument_list|(
name|this
operator|.
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|MISSING
argument_list|,
name|sameInstance
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

