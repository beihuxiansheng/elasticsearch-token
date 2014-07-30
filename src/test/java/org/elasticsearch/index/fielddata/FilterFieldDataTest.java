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
name|document
operator|.
name|Field
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
name|StringField
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
name|RandomAccessOrds
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
name|ImmutableSettings
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
name|Random
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
DECL|class|FilterFieldDataTest
specifier|public
class|class
name|FilterFieldDataTest
extends|extends
name|AbstractFieldDataTests
block|{
annotation|@
name|Override
DECL|method|getFieldDataType
specifier|protected
name|FieldDataType
name|getFieldDataType
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Test
DECL|method|testFilterByFrequency
specifier|public
name|void
name|testFilterByFrequency
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|getRandom
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
literal|1000
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
literal|"id"
argument_list|,
literal|""
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
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"high_freq"
argument_list|,
literal|"100"
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
name|StringField
argument_list|(
literal|"low_freq"
argument_list|,
literal|"100"
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
name|StringField
argument_list|(
literal|"med_freq"
argument_list|,
literal|"100"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"high_freq"
argument_list|,
literal|"10"
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
name|StringField
argument_list|(
literal|"med_freq"
argument_list|,
literal|"10"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|5
operator|==
literal|0
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"high_freq"
argument_list|,
literal|"5"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AtomicReaderContext
name|context
init|=
name|refreshReader
argument_list|()
decl_stmt|;
name|String
index|[]
name|formats
init|=
operator|new
name|String
index|[]
block|{
literal|"fst"
block|,
literal|"paged_bytes"
block|}
decl_stmt|;
for|for
control|(
name|String
name|format
range|:
name|formats
control|)
block|{
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min_segment_size"
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min"
argument_list|,
literal|0.0d
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.max"
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|100
else|:
literal|0.5d
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"high_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|2L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min_segment_size"
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min"
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|101
else|:
literal|101d
operator|/
literal|200.0d
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.max"
argument_list|,
literal|201
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"high_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|1L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// test # docs with value
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min_segment_size"
argument_list|,
literal|101
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min"
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|101
else|:
literal|101d
operator|/
literal|200.0d
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"med_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|2L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min_segment_size"
argument_list|,
literal|101
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min"
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|101
else|:
literal|101d
operator|/
literal|200.0d
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"med_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|2L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.regex.pattern"
argument_list|,
literal|"\\d{2,3}"
argument_list|)
comment|// allows 10& 100
operator|.
name|put
argument_list|(
literal|"filter.frequency.min_segment_size"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.frequency.min"
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|1
else|:
literal|1d
operator|/
literal|200.0d
argument_list|)
comment|// 100, 10, 5
operator|.
name|put
argument_list|(
literal|"filter.frequency.max"
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|99
else|:
literal|99d
operator|/
literal|200.0d
argument_list|)
argument_list|)
decl_stmt|;
comment|// 100
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"high_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|1L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testFilterByRegExp
specifier|public
name|void
name|testFilterByRegExp
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|hundred
init|=
literal|0
decl_stmt|;
name|int
name|ten
init|=
literal|0
decl_stmt|;
name|int
name|five
init|=
literal|0
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
literal|1000
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
literal|"id"
argument_list|,
literal|""
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
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|hundred
operator|++
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"high_freq"
argument_list|,
literal|"100"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|ten
operator|++
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"high_freq"
argument_list|,
literal|"10"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|5
operator|==
literal|0
condition|)
block|{
name|five
operator|++
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"high_freq"
argument_list|,
literal|"5"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
name|hundred
operator|+
literal|" "
operator|+
name|ten
operator|+
literal|" "
operator|+
name|five
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AtomicReaderContext
name|context
init|=
name|refreshReader
argument_list|()
decl_stmt|;
name|String
index|[]
name|formats
init|=
operator|new
name|String
index|[]
block|{
literal|"fst"
block|,
literal|"paged_bytes"
block|}
decl_stmt|;
for|for
control|(
name|String
name|format
range|:
name|formats
control|)
block|{
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.regex.pattern"
argument_list|,
literal|"\\d"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"high_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|1L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|ifdService
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FieldDataType
name|fieldDataType
init|=
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter.regex.pattern"
argument_list|,
literal|"\\d{1,2}"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOrdinalsFieldData
name|fieldData
init|=
name|getForField
argument_list|(
name|fieldDataType
argument_list|,
literal|"high_freq"
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|loadDirect
init|=
name|fieldData
operator|.
name|loadDirect
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|RandomAccessOrds
name|bytesValues
init|=
name|loadDirect
operator|.
name|getOrdinalsValues
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|2L
argument_list|,
name|equalTo
argument_list|(
name|bytesValues
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bytesValues
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

