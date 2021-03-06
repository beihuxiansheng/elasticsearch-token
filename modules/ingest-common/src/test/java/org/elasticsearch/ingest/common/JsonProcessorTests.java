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
name|json
operator|.
name|JsonXContent
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
name|HashMap
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
DECL|class|JsonProcessorTests
specifier|public
class|class
name|JsonProcessorTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testExecute
specifier|public
name|void
name|testExecute
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|processorTag
init|=
name|randomAlphaOfLength
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|randomField
init|=
name|randomAlphaOfLength
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|randomTargetField
init|=
name|randomAlphaOfLength
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|JsonProcessor
name|jsonProcessor
init|=
operator|new
name|JsonProcessor
argument_list|(
name|processorTag
argument_list|,
name|randomField
argument_list|,
name|randomTargetField
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
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
name|Object
argument_list|>
name|randomJsonMap
init|=
name|RandomDocumentPicks
operator|.
name|randomSource
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|map
argument_list|(
name|randomJsonMap
argument_list|)
decl_stmt|;
name|String
name|randomJson
init|=
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
name|randomField
argument_list|,
name|randomJson
argument_list|)
expr_stmt|;
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
name|document
argument_list|)
decl_stmt|;
name|jsonProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonified
init|=
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|randomTargetField
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertIngestDocument
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|randomTargetField
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|jsonified
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidJson
specifier|public
name|void
name|testInvalidJson
parameter_list|()
block|{
name|JsonProcessor
name|jsonProcessor
init|=
operator|new
name|JsonProcessor
argument_list|(
literal|"tag"
argument_list|,
literal|"field"
argument_list|,
literal|"target_field"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
literal|"invalid json"
argument_list|)
expr_stmt|;
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
name|document
argument_list|)
decl_stmt|;
name|Exception
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
name|jsonProcessor
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
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Unrecognized token"
operator|+
literal|" 'invalid': was expecting ('true', 'false' or 'null')\n"
operator|+
literal|" at [Source: invalid json; line: 1, column: 8]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldMissing
specifier|public
name|void
name|testFieldMissing
parameter_list|()
block|{
name|JsonProcessor
name|jsonProcessor
init|=
operator|new
name|JsonProcessor
argument_list|(
literal|"tag"
argument_list|,
literal|"field"
argument_list|,
literal|"target_field"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|document
argument_list|)
decl_stmt|;
name|Exception
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
name|jsonProcessor
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
literal|"field [field] not present as part of path [field]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testAddToRoot
specifier|public
name|void
name|testAddToRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|processorTag
init|=
name|randomAlphaOfLength
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|randomTargetField
init|=
name|randomAlphaOfLength
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|JsonProcessor
name|jsonProcessor
init|=
operator|new
name|JsonProcessor
argument_list|(
name|processorTag
argument_list|,
literal|"a"
argument_list|,
name|randomTargetField
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|json
init|=
literal|"{\"a\": 1, \"b\": 2}"
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"see"
argument_list|)
expr_stmt|;
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
name|document
argument_list|)
decl_stmt|;
name|jsonProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"see"
argument_list|)
expr_stmt|;
name|IngestDocument
name|expectedIngestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
name|expected
argument_list|)
decl_stmt|;
name|assertIngestDocument
argument_list|(
name|ingestDocument
argument_list|,
name|expectedIngestDocument
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

