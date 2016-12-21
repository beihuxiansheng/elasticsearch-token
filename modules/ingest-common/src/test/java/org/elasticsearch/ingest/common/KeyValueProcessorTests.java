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
name|List
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
name|equalTo
import|;
end_import

begin_class
DECL|class|KeyValueProcessorTests
specifier|public
class|class
name|KeyValueProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
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
literal|"first=hello&second=world&second=universe"
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
literal|"&"
argument_list|,
literal|"="
argument_list|,
literal|null
argument_list|,
literal|"target"
argument_list|,
literal|false
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
literal|"target.first"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
literal|"target.second"
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"world"
argument_list|,
literal|"universe"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRootTarget
specifier|public
name|void
name|testRootTarget
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
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
literal|"myField"
argument_list|,
literal|"first=hello&second=world&second=universe"
argument_list|)
expr_stmt|;
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|"myField"
argument_list|,
literal|"&"
argument_list|,
literal|"="
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
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
literal|"first"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
literal|"second"
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"world"
argument_list|,
literal|"universe"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeySameAsSourceField
specifier|public
name|void
name|testKeySameAsSourceField
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
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
literal|"first"
argument_list|,
literal|"first=hello"
argument_list|)
expr_stmt|;
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|"first"
argument_list|,
literal|"&"
argument_list|,
literal|"="
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
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
literal|"first"
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"first=hello"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncludeKeys
specifier|public
name|void
name|testIncludeKeys
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
literal|"first=hello&second=world&second=universe"
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
literal|"&"
argument_list|,
literal|"="
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"first"
argument_list|)
argument_list|,
literal|"target"
argument_list|,
literal|false
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
literal|"target.first"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ingestDocument
operator|.
name|hasField
argument_list|(
literal|"target.second"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingField
specifier|public
name|void
name|testMissingField
parameter_list|()
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
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|"unknown"
argument_list|,
literal|"&"
argument_list|,
literal|"="
argument_list|,
literal|null
argument_list|,
literal|"target"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|exception
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
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
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field [unknown] not present as part of path [unknown]"
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
name|fieldName
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
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|fieldName
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|"target"
argument_list|,
literal|true
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
DECL|method|testNonExistentWithIgnoreMissing
specifier|public
name|void
name|testNonExistentWithIgnoreMissing
parameter_list|()
throws|throws
name|Exception
block|{
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
name|emptyMap
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
name|Processor
name|processor
init|=
operator|new
name|KeyValueProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|"unknown"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|"target"
argument_list|,
literal|true
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
block|}
end_class

end_unit
