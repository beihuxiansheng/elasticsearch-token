begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
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
name|core
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
name|core
operator|.
name|TemplateService
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
name|TestTemplateService
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
name|core
operator|.
name|ValueSource
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
name|core
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
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|SetProcessorTests
specifier|public
class|class
name|SetProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSetExistingFields
specifier|public
name|void
name|testSetExistingFields
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
name|randomExistingFieldName
argument_list|(
name|random
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
decl_stmt|;
name|Object
name|fieldValue
init|=
name|RandomDocumentPicks
operator|.
name|randomFieldValue
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
name|createSetProcessor
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
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
name|hasField
argument_list|(
name|fieldName
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
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|fieldValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetNewFields
specifier|public
name|void
name|testSetNewFields
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
comment|//used to verify that there are no conflicts between subsequent fields going to be added
name|IngestDocument
name|testIngestDocument
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
name|Object
name|fieldValue
init|=
name|RandomDocumentPicks
operator|.
name|randomFieldValue
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
name|testIngestDocument
argument_list|,
name|fieldValue
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
name|createSetProcessor
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
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
name|hasField
argument_list|(
name|fieldName
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
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|fieldValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetFieldsTypeMismatch
specifier|public
name|void
name|testSetFieldsTypeMismatch
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
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|Processor
name|processor
init|=
name|createSetProcessor
argument_list|(
literal|"field.inner"
argument_list|,
literal|"value"
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
name|fail
argument_list|(
literal|"processor execute should have failed"
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
literal|"cannot set [inner] with parent object of type [java.lang.String] as part of path [field.inner]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSetMetadata
specifier|public
name|void
name|testSetMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|IngestDocument
operator|.
name|MetaData
name|randomMetaData
init|=
name|randomFrom
argument_list|(
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
name|createSetProcessor
argument_list|(
name|randomMetaData
operator|.
name|getFieldName
argument_list|()
argument_list|,
literal|"_value"
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
name|randomMetaData
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|"_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createSetProcessor
specifier|private
specifier|static
name|Processor
name|createSetProcessor
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|fieldValue
parameter_list|)
block|{
name|TemplateService
name|templateService
init|=
name|TestTemplateService
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
operator|new
name|SetProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|templateService
operator|.
name|compile
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|ValueSource
operator|.
name|wrap
argument_list|(
name|fieldValue
argument_list|,
name|templateService
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
