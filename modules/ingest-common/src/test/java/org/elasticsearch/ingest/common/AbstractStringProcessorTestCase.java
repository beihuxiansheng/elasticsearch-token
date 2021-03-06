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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|IngestDocumentMatcher
operator|.
name|assertIngestDocument
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
DECL|class|AbstractStringProcessorTestCase
specifier|public
specifier|abstract
class|class
name|AbstractStringProcessorTestCase
extends|extends
name|ESTestCase
block|{
DECL|method|newProcessor
specifier|protected
specifier|abstract
name|AbstractStringProcessor
name|newProcessor
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|ignoreMissing
parameter_list|,
name|String
name|targetField
parameter_list|)
function_decl|;
DECL|method|modifyInput
specifier|protected
name|String
name|modifyInput
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|input
return|;
block|}
DECL|method|expectedResult
specifier|protected
specifier|abstract
name|String
name|expectedResult
parameter_list|(
name|String
name|input
parameter_list|)
function_decl|;
DECL|method|testProcessor
specifier|public
name|void
name|testProcessor
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
name|String
name|fieldValue
init|=
name|RandomDocumentPicks
operator|.
name|randomString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
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
name|modifyInput
argument_list|(
name|fieldValue
argument_list|)
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
name|newProcessor
argument_list|(
name|fieldName
argument_list|,
name|randomBoolean
argument_list|()
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
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedResult
argument_list|(
name|fieldValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldNotFound
specifier|public
name|void
name|testFieldNotFound
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newProcessor
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
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
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
decl_stmt|;
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
DECL|method|testFieldNotFoundWithIgnoreMissing
specifier|public
name|void
name|testFieldNotFoundWithIgnoreMissing
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newProcessor
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|IngestDocument
name|originalIngestDocument
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
name|IngestDocument
name|ingestDocument
init|=
operator|new
name|IngestDocument
argument_list|(
name|originalIngestDocument
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|originalIngestDocument
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullValue
specifier|public
name|void
name|testNullValue
parameter_list|()
throws|throws
name|Exception
block|{
name|Processor
name|processor
init|=
name|newProcessor
argument_list|(
literal|"field"
argument_list|,
literal|false
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
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
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field [field] is null, cannot process it."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullValueWithIgnoreMissing
specifier|public
name|void
name|testNullValueWithIgnoreMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Processor
name|processor
init|=
name|newProcessor
argument_list|(
literal|"field"
argument_list|,
literal|true
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|IngestDocument
name|originalIngestDocument
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
name|IngestDocument
name|ingestDocument
init|=
operator|new
name|IngestDocument
argument_list|(
name|originalIngestDocument
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|originalIngestDocument
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonStringValue
specifier|public
name|void
name|testNonStringValue
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newProcessor
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
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
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|fieldName
argument_list|,
name|randomInt
argument_list|()
argument_list|)
expr_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"] of type [java.lang.Integer] cannot be cast to [java.lang.String]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonStringValueWithIgnoreMissing
specifier|public
name|void
name|testNonStringValueWithIgnoreMissing
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newProcessor
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
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
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|fieldName
argument_list|,
name|randomInt
argument_list|()
argument_list|)
expr_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"] of type [java.lang.Integer] cannot be cast to [java.lang.String]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTargetField
specifier|public
name|void
name|testTargetField
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
name|String
name|fieldValue
init|=
name|RandomDocumentPicks
operator|.
name|randomString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
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
name|modifyInput
argument_list|(
name|fieldValue
argument_list|)
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
name|newProcessor
argument_list|(
name|fieldName
argument_list|,
name|randomBoolean
argument_list|()
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
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|targetFieldName
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedResult
argument_list|(
name|fieldValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

