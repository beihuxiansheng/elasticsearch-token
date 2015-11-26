begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor.join
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|join
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
name|processor
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
name|test
operator|.
name|ESTestCase
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
name|equalTo
import|;
end_import

begin_class
DECL|class|JoinProcessorTests
specifier|public
class|class
name|JoinProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|field|SEPARATORS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|SEPARATORS
init|=
operator|new
name|String
index|[]
block|{
literal|"-"
block|,
literal|"_"
block|,
literal|"."
block|}
decl_stmt|;
DECL|method|testJoinStrings
specifier|public
name|void
name|testJoinStrings
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedResultMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numFields
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
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
name|String
name|separator
init|=
name|randomFrom
argument_list|(
name|SEPARATORS
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
name|String
name|expectedResult
init|=
literal|""
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
name|randomAsciiOfLengthBetween
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
operator|+=
name|value
expr_stmt|;
if|if
condition|(
name|j
operator|<
name|numItems
operator|-
literal|1
condition|)
block|{
name|expectedResult
operator|+=
name|separator
expr_stmt|;
block|}
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
name|expectedResultMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|separator
argument_list|)
expr_stmt|;
block|}
name|Processor
name|processor
init|=
operator|new
name|JoinProcessor
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|expectedResultMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testJoinIntegers
specifier|public
name|void
name|testJoinIntegers
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedResultMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numFields
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
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
name|String
name|separator
init|=
name|randomFrom
argument_list|(
name|SEPARATORS
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
name|String
name|expectedResult
init|=
literal|""
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
name|int
name|value
init|=
name|randomInt
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
operator|+=
name|value
expr_stmt|;
if|if
condition|(
name|j
operator|<
name|numItems
operator|-
literal|1
condition|)
block|{
name|expectedResult
operator|+=
name|separator
expr_stmt|;
block|}
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
name|expectedResultMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|expectedResult
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|separator
argument_list|)
expr_stmt|;
block|}
name|Processor
name|processor
init|=
operator|new
name|JoinProcessor
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|expectedResultMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testJoinNonListField
specifier|public
name|void
name|testJoinNonListField
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
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|join
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fieldName
argument_list|,
literal|"-"
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|JoinProcessor
argument_list|(
name|join
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
DECL|method|testJoinNonExistingField
specifier|public
name|void
name|testJoinNonExistingField
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
name|Processor
name|processor
init|=
operator|new
name|JoinProcessor
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fieldName
argument_list|,
literal|"-"
argument_list|)
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
literal|"] is null, cannot join."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

