begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.shorts
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|data
operator|.
name|shorts
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
name|NumericField
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
name|field
operator|.
name|data
operator|.
name|FieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Tuple
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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|lucene
operator|.
name|DocumentBuilder
operator|.
name|*
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
name|field
operator|.
name|data
operator|.
name|FieldDataOptions
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Tuple
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
name|*
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|ShortFieldDataTests
specifier|public
class|class
name|ShortFieldDataTests
block|{
DECL|method|intFieldDataTests
annotation|@
name|Test
specifier|public
name|void
name|intFieldDataTests
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"svalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"mvalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|104
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"svalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"mvalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|104
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"mvalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|105
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"svalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|7
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"mvalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|102
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"svalue"
argument_list|)
operator|.
name|setIntValue
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|indexWriter
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|ShortFieldData
operator|.
name|load
argument_list|(
name|reader
argument_list|,
literal|"svalue"
argument_list|,
name|fieldDataOptions
argument_list|()
operator|.
name|withFreqs
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|ShortFieldData
operator|.
name|load
argument_list|(
name|reader
argument_list|,
literal|"mvalue"
argument_list|,
name|fieldDataOptions
argument_list|()
operator|.
name|withFreqs
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|ShortFieldData
name|sFieldData
init|=
name|ShortFieldData
operator|.
name|load
argument_list|(
name|reader
argument_list|,
literal|"svalue"
argument_list|,
name|fieldDataOptions
argument_list|()
operator|.
name|withFreqs
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|ShortFieldData
name|mFieldData
init|=
name|ShortFieldData
operator|.
name|load
argument_list|(
name|reader
argument_list|,
literal|"mvalue"
argument_list|,
name|fieldDataOptions
argument_list|()
operator|.
name|withFreqs
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|fieldName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"svalue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|FieldData
operator|.
name|Type
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|multiValued
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|fieldName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"mvalue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|FieldData
operator|.
name|Type
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|multiValued
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// svalue
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|hasValue
argument_list|(
literal|0
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
name|sFieldData
operator|.
name|docFieldData
argument_list|(
literal|0
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|value
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|docFieldData
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|0
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|docFieldData
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|0
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|docFieldData
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|hasValue
argument_list|(
literal|1
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
name|sFieldData
operator|.
name|value
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|1
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|hasValue
argument_list|(
literal|2
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
name|sFieldData
operator|.
name|value
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|2
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|2
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|hasValue
argument_list|(
literal|3
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|hasValue
argument_list|(
literal|4
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
name|sFieldData
operator|.
name|value
argument_list|(
literal|4
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|4
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sFieldData
operator|.
name|values
argument_list|(
literal|4
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// check order is correct
specifier|final
name|ArrayList
argument_list|<
name|Tuple
argument_list|<
name|Short
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Tuple
argument_list|<
name|Short
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|sFieldData
operator|.
name|forEachValue
argument_list|(
operator|new
name|ShortFieldData
operator|.
name|ValueProc
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onValue
parameter_list|(
name|short
name|value
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|tuple
argument_list|(
name|value
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
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
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|v2
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
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|v2
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
name|values
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|v2
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// mvalue
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|hasValue
argument_list|(
literal|0
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
name|mFieldData
operator|.
name|value
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|values
argument_list|(
literal|0
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|values
argument_list|(
literal|0
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|hasValue
argument_list|(
literal|1
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
name|mFieldData
operator|.
name|value
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|docFieldData
argument_list|(
literal|1
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|values
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|docFieldData
argument_list|(
literal|1
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|values
argument_list|(
literal|1
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|docFieldData
argument_list|(
literal|1
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|docFieldData
argument_list|(
literal|1
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|105
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|hasValue
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|hasValue
argument_list|(
literal|3
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
name|mFieldData
operator|.
name|value
argument_list|(
literal|3
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|102
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|values
argument_list|(
literal|3
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|values
argument_list|(
literal|3
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|102
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mFieldData
operator|.
name|hasValue
argument_list|(
literal|4
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check order is correct
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mFieldData
operator|.
name|forEachValue
argument_list|(
operator|new
name|ShortFieldData
operator|.
name|ValueProc
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onValue
parameter_list|(
name|short
name|value
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|tuple
argument_list|(
name|value
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
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
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|102
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|v2
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
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|104
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|v2
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
name|values
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|105
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|v2
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
end_class

end_unit

