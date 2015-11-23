begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor.gsub
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|gsub
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
DECL|class|GsubProcessorTests
specifier|public
class|class
name|GsubProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|method|testGsub
specifier|public
name|void
name|testGsub
parameter_list|()
throws|throws
name|IOException
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
name|numFields
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|GsubExpression
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
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
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
name|expressions
operator|.
name|add
argument_list|(
operator|new
name|GsubExpression
argument_list|(
name|fieldName
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\."
argument_list|)
argument_list|,
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Processor
name|processor
init|=
operator|new
name|GsubProcessor
argument_list|(
name|expressions
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
name|GsubExpression
name|expression
range|:
name|expressions
control|)
block|{
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getPropertyValue
argument_list|(
name|expression
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"127-0-0-1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGsubNotAStringValue
specifier|public
name|void
name|testGsubNotAStringValue
parameter_list|()
throws|throws
name|IOException
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
name|setPropertyValue
argument_list|(
name|fieldName
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GsubExpression
argument_list|>
name|gsubExpressions
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|GsubExpression
argument_list|(
name|fieldName
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\."
argument_list|)
argument_list|,
literal|"-"
argument_list|)
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|GsubProcessor
argument_list|(
name|gsubExpressions
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
literal|"processor execution should have failed"
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
literal|"] of type [java.lang.Integer] cannot be cast to [java.lang.String]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGsubNullValue
specifier|public
name|void
name|testGsubNullValue
parameter_list|()
throws|throws
name|IOException
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
name|List
argument_list|<
name|GsubExpression
argument_list|>
name|gsubExpressions
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|GsubExpression
argument_list|(
name|fieldName
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\."
argument_list|)
argument_list|,
literal|"-"
argument_list|)
argument_list|)
decl_stmt|;
name|Processor
name|processor
init|=
operator|new
name|GsubProcessor
argument_list|(
name|gsubExpressions
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
literal|"processor execution should have failed"
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
literal|"] is null, cannot match pattern."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

