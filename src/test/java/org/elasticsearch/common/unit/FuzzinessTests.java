begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
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
name|util
operator|.
name|LuceneTestCase
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
name|XContent
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
name|ElasticsearchTestCase
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
name|io
operator|.
name|IOException
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
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|number
operator|.
name|IsCloseTo
operator|.
name|closeTo
import|;
end_import

begin_class
DECL|class|FuzzinessTests
specifier|public
class|class
name|FuzzinessTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
parameter_list|()
block|{
name|String
index|[]
name|options
init|=
operator|new
name|String
index|[]
block|{
literal|"1.0"
block|,
literal|"1"
block|,
literal|"1.000000"
block|}
decl_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|asByte
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|asInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|asFloat
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|asDouble
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1d
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|asLong
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|asShort
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseFromXContent
specifier|public
name|void
name|testParseFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|50
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
block|{
name|XContent
name|xcontent
init|=
name|XContentType
operator|.
name|JSON
operator|.
name|xContent
argument_list|()
decl_stmt|;
name|float
name|floatValue
init|=
name|randomFloat
argument_list|()
decl_stmt|;
name|String
name|json
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|Fuzziness
operator|.
name|X_FIELD_NAME
argument_list|,
name|floatValue
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|xcontent
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|Fuzziness
name|parse
init|=
name|Fuzziness
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|asFloat
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|floatValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|asDouble
argument_list|()
argument_list|,
name|closeTo
argument_list|(
operator|(
name|double
operator|)
name|floatValue
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|XContent
name|xcontent
init|=
name|XContentType
operator|.
name|JSON
operator|.
name|xContent
argument_list|()
decl_stmt|;
name|Integer
name|intValue
init|=
name|frequently
argument_list|()
condition|?
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
else|:
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Float
name|floatRep
init|=
name|randomFloat
argument_list|()
decl_stmt|;
name|Number
name|value
init|=
name|intValue
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|value
operator|=
operator|new
name|Float
argument_list|(
name|floatRep
operator|+=
name|intValue
argument_list|)
expr_stmt|;
block|}
name|String
name|json
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|Fuzziness
operator|.
name|X_FIELD_NAME
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|value
operator|.
name|toString
argument_list|()
else|:
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|xcontent
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Fuzziness
name|parse
init|=
name|Fuzziness
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|asInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|int
operator|)
name|parse
operator|.
name|asShort
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|int
operator|)
name|parse
operator|.
name|asByte
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|asLong
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|intValue
argument_list|()
operator|>=
literal|1
condition|)
block|{
name|assertThat
argument_list|(
name|parse
operator|.
name|asDistance
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|2
argument_list|,
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|intValue
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|intValue
condition|)
block|{
case|case
literal|1
case|:
name|assertThat
argument_list|(
name|parse
argument_list|,
name|sameInstance
argument_list|(
name|Fuzziness
operator|.
name|ONE
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|assertThat
argument_list|(
name|parse
argument_list|,
name|sameInstance
argument_list|(
name|Fuzziness
operator|.
name|TWO
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0
case|:
name|assertThat
argument_list|(
name|parse
argument_list|,
name|sameInstance
argument_list|(
name|Fuzziness
operator|.
name|ZERO
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
block|}
block|{
name|XContent
name|xcontent
init|=
name|XContentType
operator|.
name|JSON
operator|.
name|xContent
argument_list|()
decl_stmt|;
name|String
name|json
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|Fuzziness
operator|.
name|X_FIELD_NAME
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|"AUTO"
else|:
literal|"auto"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|json
operator|=
name|Fuzziness
operator|.
name|AUTO
operator|.
name|toXContent
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
expr_stmt|;
block|}
name|XContentParser
name|parser
init|=
name|xcontent
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|Fuzziness
name|parse
init|=
name|Fuzziness
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
argument_list|,
name|sameInstance
argument_list|(
name|Fuzziness
operator|.
name|AUTO
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"d"
block|,
literal|"H"
block|,
literal|"ms"
block|,
literal|"s"
block|,
literal|"S"
block|,
literal|"w"
block|}
decl_stmt|;
name|String
name|actual
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
operator|+
name|randomFrom
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|XContent
name|xcontent
init|=
name|XContentType
operator|.
name|JSON
operator|.
name|xContent
argument_list|()
decl_stmt|;
name|String
name|json
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|Fuzziness
operator|.
name|X_FIELD_NAME
argument_list|,
name|actual
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|xcontent
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|Fuzziness
name|parse
init|=
name|Fuzziness
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|asTimeValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|actual
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAuto
specifier|public
name|void
name|testAuto
parameter_list|()
block|{
specifier|final
name|int
name|codePoints
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|string
init|=
name|randomRealisticUnicodeOfCodepointLength
argument_list|(
name|codePoints
argument_list|)
decl_stmt|;
if|if
condition|(
name|codePoints
operator|<=
literal|2
condition|)
block|{
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asSimilarity
argument_list|(
name|string
argument_list|)
argument_list|)
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|codePoints
operator|>
literal|5
condition|)
block|{
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asSimilarity
argument_list|(
name|string
argument_list|)
argument_list|)
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asSimilarity
argument_list|(
name|string
argument_list|)
argument_list|)
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asByte
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asFloat
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asDouble
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1d
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asLong
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asShort
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asTimeValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"1"
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAsDistance
specifier|public
name|void
name|testAsDistance
parameter_list|()
block|{
specifier|final
name|int
name|iters
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|50
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|integer
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|value
init|=
literal|""
operator|+
operator|(
name|randomBoolean
argument_list|()
condition|?
name|integer
operator|.
name|intValue
argument_list|()
else|:
name|integer
operator|.
name|floatValue
argument_list|()
operator|)
decl_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|value
argument_list|)
operator|.
name|asDistance
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|2
argument_list|,
name|integer
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|LuceneTestCase
operator|.
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://github.com/elastic/elasticsearch/issues/10638"
argument_list|)
DECL|method|testSimilarityToDistance
specifier|public
name|void
name|testSimilarityToDistance
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
literal|0.5f
argument_list|)
operator|.
name|asDistance
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
literal|0.66f
argument_list|)
operator|.
name|asDistance
argument_list|(
literal|"abcefg"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
literal|0.8f
argument_list|)
operator|.
name|asDistance
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Fuzziness
operator|.
name|fromSimilarity
argument_list|(
literal|0.8f
argument_list|)
operator|.
name|asDistance
argument_list|(
literal|"abcefg"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|Fuzziness
operator|.
name|ONE
operator|.
name|asSimilarity
argument_list|(
literal|"abcefg"
argument_list|)
argument_list|,
name|closeTo
argument_list|(
literal|0.8f
argument_list|,
literal|0.05
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|Fuzziness
operator|.
name|TWO
operator|.
name|asSimilarity
argument_list|(
literal|"abcefg"
argument_list|)
argument_list|,
name|closeTo
argument_list|(
literal|0.66f
argument_list|,
literal|0.05
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|Fuzziness
operator|.
name|ONE
operator|.
name|asSimilarity
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|closeTo
argument_list|(
literal|0.5f
argument_list|,
literal|0.05
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|iters
init|=
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Fuzziness
name|fuzziness
init|=
name|Fuzziness
operator|.
name|fromEdits
argument_list|(
name|between
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|string
init|=
name|rarely
argument_list|()
condition|?
name|randomRealisticUnicodeOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
else|:
name|randomRealisticUnicodeOfLengthBetween
argument_list|(
literal|4
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|float
name|similarity
init|=
name|fuzziness
operator|.
name|asSimilarity
argument_list|(
name|string
argument_list|)
decl_stmt|;
if|if
condition|(
name|similarity
operator|!=
literal|0.0f
condition|)
block|{
name|Fuzziness
name|similarityBased
init|=
name|Fuzziness
operator|.
name|build
argument_list|(
name|similarity
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|similarityBased
operator|.
name|asSimilarity
argument_list|(
name|string
argument_list|)
argument_list|,
name|closeTo
argument_list|(
name|similarity
argument_list|,
literal|0.05
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|similarityBased
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|2
argument_list|,
name|fuzziness
operator|.
name|asDistance
argument_list|(
name|string
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

