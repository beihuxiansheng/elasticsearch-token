begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|IngestDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|Processor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|RandomDocumentPicks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
operator|.
name|SortProcessor
operator|.
name|SortOrder
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
name|Collections
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
name|containsString
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
DECL|class|SortProcessorTests
specifier|public
class|class
name|SortProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSortStrings
specifier|public
name|void
name|testSortStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|String
name|value
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortIntegersNonRandom
specifier|public
name|void
name|testSortIntegersNonRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
index|[]
name|expectedResult
init|=
operator|new
name|Integer
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|10
block|,
literal|20
block|,
literal|21
block|,
literal|22
block|,
literal|50
block|,
literal|100
block|}
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|expectedResult
operator|.
name|length
argument_list|)
decl_stmt|;
name|fieldValue
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|expectedResult
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|fieldValue
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|SortOrder
operator|.
name|ASCENDING
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
operator|.
name|toArray
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedResult
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortIntegers
specifier|public
name|void
name|testSortIntegers
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|Integer
name|value
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortShorts
specifier|public
name|void
name|testSortShorts
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Short
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Short
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|Short
name|value
init|=
name|randomShort
argument_list|()
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortDoubles
specifier|public
name|void
name|testSortDoubles
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|Double
name|value
init|=
name|randomDoubleBetween
argument_list|(
literal|0.0
argument_list|,
literal|100.0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortFloats
specifier|public
name|void
name|testSortFloats
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Float
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Float
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|Float
name|value
init|=
name|randomFloat
argument_list|()
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortBytes
specifier|public
name|void
name|testSortBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Byte
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Byte
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|Byte
name|value
init|=
name|randomByte
argument_list|()
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortBooleans
specifier|public
name|void
name|testSortBooleans
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Boolean
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Boolean
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|Boolean
name|value
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortMixedStrings
specifier|public
name|void
name|testSortMixedStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|String
name|value
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|value
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortNonListField
specifier|public
name|void
name|testSortNonListField
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|randomFieldName
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|fieldName
argument_list|,
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] of type [java.lang.String] cannot be cast to [java.util.List]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortNonExistingField
specifier|public
name|void
name|testSortNonExistingField
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|randomFieldName
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"not present as part of path ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortNullValue
specifier|public
name|void
name|testSortNullValue
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"field"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|"field"
argument_list|,
name|order
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field [field] is null, cannot sort."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDescendingSortWithTargetField
specifier|public
name|void
name|testDescendingSortWithTargetField
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|String
name|value
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|String
name|targetFieldName
init|=
name|RandomDocumentPicks
operator|.
name|randomFieldName
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|SortOrder
operator|.
name|DESCENDING
argument_list|,
name|targetFieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|targetFieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testAscendingSortWithTargetField
specifier|public
name|void
name|testAscendingSortWithTargetField
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numItems
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldValue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numItems
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numItems
condition|;
name|j
operator|++
control|)
block|{
name|String
name|value
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|fieldValue
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|String
name|targetFieldName
init|=
name|RandomDocumentPicks
operator|.
name|randomFieldName
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|SortOrder
operator|.
name|ASCENDING
argument_list|,
name|targetFieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|targetFieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortWithTargetFieldLeavesOriginalUntouched
specifier|public
name|void
name|testSortWithTargetFieldLeavesOriginalUntouched
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|fieldValue
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|expectedResult
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fieldValue
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASCENDING
else|:
name|SortOrder
operator|.
name|DESCENDING
decl_stmt|;
if|if
condition|(
name|order
operator|.
name|equals
argument_list|(
name|SortOrder
operator|.
name|DESCENDING
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|expectedResult
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|RandomDocumentPicks
operator|.
name|addRandomField
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fieldValue
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|targetFieldName
init|=
name|fieldName
operator|+
literal|"foo"
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|SortProcessor
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
name|order
argument_list|,
name|targetFieldName
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|targetFieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|fieldValue
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

