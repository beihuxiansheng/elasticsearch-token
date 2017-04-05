begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
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
name|bytes
operator|.
name|BytesReference
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
name|ToXContent
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
name|XContentFactory
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
name|XContentParser
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
name|XContentType
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
name|Map
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
name|xcontent
operator|.
name|XContentHelper
operator|.
name|toXContent
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
name|xcontent
operator|.
name|XContentParserUtils
operator|.
name|ensureExpectedToken
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertToXContentEquivalent
import|;
end_import

begin_class
DECL|class|ProfileResultTests
specifier|public
class|class
name|ProfileResultTests
extends|extends
name|ESTestCase
block|{
DECL|method|createTestItem
specifier|public
specifier|static
name|ProfileResult
name|createTestItem
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|String
name|type
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|description
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|timingsSize
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|timings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|timingsSize
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
name|timingsSize
condition|;
name|i
operator|++
control|)
block|{
name|long
name|time
init|=
name|randomNonNegativeLong
argument_list|()
operator|/
name|timingsSize
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// also often use "small" values in tests
name|time
operator|=
name|randomNonNegativeLong
argument_list|()
operator|%
literal|10000
expr_stmt|;
block|}
name|timings
operator|.
name|put
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|time
argument_list|)
expr_stmt|;
comment|// don't overflow Long.MAX_VALUE;
block|}
name|int
name|childrenSize
init|=
name|depth
operator|>
literal|0
condition|?
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
else|:
literal|0
decl_stmt|;
name|List
argument_list|<
name|ProfileResult
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|childrenSize
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
name|childrenSize
condition|;
name|i
operator|++
control|)
block|{
name|children
operator|.
name|add
argument_list|(
name|createTestItem
argument_list|(
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ProfileResult
argument_list|(
name|type
argument_list|,
name|description
argument_list|,
name|timings
argument_list|,
name|children
argument_list|)
return|;
block|}
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|ProfileResult
name|profileResult
init|=
name|createTestItem
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|XContentType
name|xContentType
init|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|humanReadable
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|BytesReference
name|originalBytes
init|=
name|toShuffledXContent
argument_list|(
name|profileResult
argument_list|,
name|xContentType
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|ProfileResult
name|parsed
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|xContentType
operator|.
name|xContent
argument_list|()
argument_list|,
name|originalBytes
argument_list|)
init|)
block|{
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
name|parsed
operator|=
name|ProfileResult
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|profileResult
operator|.
name|getTime
argument_list|()
argument_list|,
name|parsed
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertToXContentEquivalent
argument_list|(
name|originalBytes
argument_list|,
name|toXContent
argument_list|(
name|parsed
argument_list|,
name|xContentType
argument_list|,
name|humanReadable
argument_list|)
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
block|}
DECL|method|testToXContent
specifier|public
name|void
name|testToXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ProfileResult
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
operator|new
name|ProfileResult
argument_list|(
literal|"child1"
argument_list|,
literal|"desc1"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"key1"
argument_list|,
literal|100L
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
operator|new
name|ProfileResult
argument_list|(
literal|"child2"
argument_list|,
literal|"desc2"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"key1"
argument_list|,
literal|123356L
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|timings3
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|timings3
operator|.
name|put
argument_list|(
literal|"key1"
argument_list|,
literal|123456L
argument_list|)
expr_stmt|;
name|timings3
operator|.
name|put
argument_list|(
literal|"key2"
argument_list|,
literal|100000L
argument_list|)
expr_stmt|;
name|ProfileResult
name|result
init|=
operator|new
name|ProfileResult
argument_list|(
literal|"someType"
argument_list|,
literal|"some description"
argument_list|,
name|timings3
argument_list|,
name|children
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|result
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  \"type\" : \"someType\",\n"
operator|+
literal|"  \"description\" : \"some description\",\n"
operator|+
literal|"  \"time_in_nanos\" : 223456,\n"
operator|+
literal|"  \"breakdown\" : {\n"
operator|+
literal|"    \"key1\" : 123456,\n"
operator|+
literal|"    \"key2\" : 100000\n"
operator|+
literal|"  },\n"
operator|+
literal|"  \"children\" : [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      \"type\" : \"child1\",\n"
operator|+
literal|"      \"description\" : \"desc1\",\n"
operator|+
literal|"      \"time_in_nanos\" : 100,\n"
operator|+
literal|"      \"breakdown\" : {\n"
operator|+
literal|"        \"key1\" : 100\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      \"type\" : \"child2\",\n"
operator|+
literal|"      \"description\" : \"desc2\",\n"
operator|+
literal|"      \"time_in_nanos\" : 123356,\n"
operator|+
literal|"      \"breakdown\" : {\n"
operator|+
literal|"        \"key1\" : 123356\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
operator|.
name|humanReadable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  \"type\" : \"someType\",\n"
operator|+
literal|"  \"description\" : \"some description\",\n"
operator|+
literal|"  \"time\" : \"223.4micros\",\n"
operator|+
literal|"  \"time_in_nanos\" : 223456,\n"
operator|+
literal|"  \"breakdown\" : {\n"
operator|+
literal|"    \"key1\" : 123456,\n"
operator|+
literal|"    \"key2\" : 100000\n"
operator|+
literal|"  },\n"
operator|+
literal|"  \"children\" : [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      \"type\" : \"child1\",\n"
operator|+
literal|"      \"description\" : \"desc1\",\n"
operator|+
literal|"      \"time\" : \"100nanos\",\n"
operator|+
literal|"      \"time_in_nanos\" : 100,\n"
operator|+
literal|"      \"breakdown\" : {\n"
operator|+
literal|"        \"key1\" : 100\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      \"type\" : \"child2\",\n"
operator|+
literal|"      \"description\" : \"desc2\",\n"
operator|+
literal|"      \"time\" : \"123.3micros\",\n"
operator|+
literal|"      \"time_in_nanos\" : 123356,\n"
operator|+
literal|"      \"breakdown\" : {\n"
operator|+
literal|"        \"key1\" : 123356\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ProfileResult
argument_list|(
literal|"profileName"
argument_list|,
literal|"some description"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"key1"
argument_list|,
literal|12345678L
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
operator|.
name|humanReadable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  \"type\" : \"profileName\",\n"
operator|+
literal|"  \"description\" : \"some description\",\n"
operator|+
literal|"  \"time\" : \"12.3ms\",\n"
operator|+
literal|"  \"time_in_nanos\" : 12345678,\n"
operator|+
literal|"  \"breakdown\" : {\n"
operator|+
literal|"    \"key1\" : 12345678\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ProfileResult
argument_list|(
literal|"profileName"
argument_list|,
literal|"some description"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"key1"
argument_list|,
literal|1234567890L
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
operator|.
name|humanReadable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  \"type\" : \"profileName\",\n"
operator|+
literal|"  \"description\" : \"some description\",\n"
operator|+
literal|"  \"time\" : \"1.2s\",\n"
operator|+
literal|"  \"time_in_nanos\" : 1234567890,\n"
operator|+
literal|"  \"breakdown\" : {\n"
operator|+
literal|"    \"key1\" : 1234567890\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

