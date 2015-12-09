begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport.simulate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|Map
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
name|not
import|;
end_import

begin_class
DECL|class|WriteableIngestDocumentTests
specifier|public
class|class
name|WriteableIngestDocumentTests
extends|extends
name|ESTestCase
block|{
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAndMetadata
init|=
name|RandomDocumentPicks
operator|.
name|randomSource
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
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
operator|.
name|length
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
name|sourceAndMetadata
operator|.
name|put
argument_list|(
name|randomFrom
argument_list|(
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ingestMetadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|numFields
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
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
name|ingestMetadata
operator|.
name|put
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|WriteableIngestDocument
name|ingestDocument
init|=
operator|new
name|WriteableIngestDocument
argument_list|(
operator|new
name|IngestDocument
argument_list|(
name|sourceAndMetadata
argument_list|,
name|ingestMetadata
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherSourceAndMetadata
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|otherSourceAndMetadata
operator|=
name|RandomDocumentPicks
operator|.
name|randomSource
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|otherSourceAndMetadata
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|sourceAndMetadata
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|numFields
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|otherSourceAndMetadata
operator|.
name|put
argument_list|(
name|randomFrom
argument_list|(
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|changed
operator|=
literal|true
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherIngestMetadata
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|otherIngestMetadata
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|numFields
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
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
name|otherIngestMetadata
operator|.
name|put
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|otherIngestMetadata
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|ingestMetadata
argument_list|)
expr_stmt|;
block|}
name|WriteableIngestDocument
name|otherIngestDocument
init|=
operator|new
name|WriteableIngestDocument
argument_list|(
operator|new
name|IngestDocument
argument_list|(
name|otherSourceAndMetadata
argument_list|,
name|otherIngestMetadata
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|changed
condition|)
block|{
name|assertThat
argument_list|(
name|ingestDocument
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|otherIngestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|otherIngestDocument
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|ingestDocument
argument_list|,
name|equalTo
argument_list|(
name|otherIngestDocument
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|otherIngestDocument
argument_list|,
name|equalTo
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|otherIngestDocument
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|WriteableIngestDocument
name|thirdIngestDocument
init|=
operator|new
name|WriteableIngestDocument
argument_list|(
operator|new
name|IngestDocument
argument_list|(
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|sourceAndMetadata
argument_list|)
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|ingestMetadata
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|thirdIngestDocument
argument_list|,
name|equalTo
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
argument_list|,
name|equalTo
argument_list|(
name|thirdIngestDocument
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdIngestDocument
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAndMetadata
init|=
name|RandomDocumentPicks
operator|.
name|randomSource
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
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
operator|.
name|length
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
name|sourceAndMetadata
operator|.
name|put
argument_list|(
name|randomFrom
argument_list|(
name|IngestDocument
operator|.
name|MetaData
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ingestMetadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|numFields
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
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
name|ingestMetadata
operator|.
name|put
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
name|RandomDocumentPicks
operator|.
name|randomSource
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|WriteableIngestDocument
name|writeableIngestDocument
init|=
operator|new
name|WriteableIngestDocument
argument_list|(
operator|new
name|IngestDocument
argument_list|(
name|sourceAndMetadata
argument_list|,
name|ingestMetadata
argument_list|)
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|writeableIngestDocument
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|streamInput
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|WriteableIngestDocument
name|otherWriteableIngestDocument
init|=
name|WriteableIngestDocument
operator|.
name|readWriteableIngestDocumentFrom
argument_list|(
name|streamInput
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|otherWriteableIngestDocument
argument_list|,
name|equalTo
argument_list|(
name|writeableIngestDocument
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

