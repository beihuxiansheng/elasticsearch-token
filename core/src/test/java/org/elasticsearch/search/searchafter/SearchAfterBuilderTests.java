begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.searchafter
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|searchafter
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
name|ParseFieldMatcher
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
name|geo
operator|.
name|GeoPoint
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
name|NamedWriteableRegistry
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
name|text
operator|.
name|Text
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|EqualsHashCodeTestUtils
operator|.
name|checkEqualsAndHashCode
import|;
end_import

begin_class
DECL|class|SearchAfterBuilderTests
specifier|public
class|class
name|SearchAfterBuilderTests
extends|extends
name|ESTestCase
block|{
DECL|field|NUMBER_OF_TESTBUILDERS
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_TESTBUILDERS
init|=
literal|20
decl_stmt|;
DECL|method|randomSearchAfterBuilder
specifier|private
specifier|static
name|SearchAfterBuilder
name|randomSearchAfterBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numSearchFrom
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|SearchAfterBuilder
name|searchAfterBuilder
init|=
operator|new
name|SearchAfterBuilder
argument_list|()
decl_stmt|;
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|numSearchFrom
index|]
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
name|numSearchFrom
condition|;
name|i
operator|++
control|)
block|{
name|int
name|branch
init|=
name|randomInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|branch
condition|)
block|{
case|case
literal|0
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomInt
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomFloat
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomLong
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomDouble
argument_list|()
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomByte
argument_list|()
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|values
index|[
name|i
index|]
operator|=
name|randomShort
argument_list|()
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|values
index|[
name|i
index|]
operator|=
operator|new
name|Text
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|values
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
break|break;
block|}
block|}
name|searchAfterBuilder
operator|.
name|setSortValues
argument_list|(
name|values
argument_list|)
expr_stmt|;
return|return
name|searchAfterBuilder
return|;
block|}
comment|// We build a json version of the search_after first in order to
comment|// ensure that every number type remain the same before/after xcontent (de)serialization.
comment|// This is not a problem because the final type of each field value is extracted from associated sort field.
comment|// This little trick ensure that equals and hashcode are the same when using the xcontent serialization.
DECL|method|randomJsonSearchFromBuilder
specifier|private
name|SearchAfterBuilder
name|randomJsonSearchFromBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numSearchAfter
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|XContentBuilder
name|jsonBuilder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|jsonBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|jsonBuilder
operator|.
name|startArray
argument_list|(
literal|"search_after"
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
name|numSearchAfter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|branch
init|=
name|randomInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|branch
condition|)
block|{
case|case
literal|0
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomByte
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
name|randomShort
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|jsonBuilder
operator|.
name|value
argument_list|(
operator|new
name|Text
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|jsonBuilder
operator|.
name|nullValue
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|jsonBuilder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|jsonBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|jsonBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
return|return
name|SearchAfterBuilder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
DECL|method|serializedCopy
specifier|private
specifier|static
name|SearchAfterBuilder
name|serializedCopy
parameter_list|(
name|SearchAfterBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|copyWriteable
argument_list|(
name|original
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|,
name|SearchAfterBuilder
operator|::
operator|new
argument_list|)
return|;
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|SearchAfterBuilder
name|original
init|=
name|randomSearchAfterBuilder
argument_list|()
decl_stmt|;
name|SearchAfterBuilder
name|deserialized
init|=
name|serializedCopy
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserialized
argument_list|,
name|original
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|original
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserialized
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
comment|// TODO add equals tests with mutating the original object
name|checkEqualsAndHashCode
argument_list|(
name|randomSearchAfterBuilder
argument_list|()
argument_list|,
name|SearchAfterBuilderTests
operator|::
name|serializedCopy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
literal|20
condition|;
name|runs
operator|++
control|)
block|{
name|SearchAfterBuilder
name|searchAfterBuilder
init|=
name|randomJsonSearchFromBuilder
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|searchAfterBuilder
operator|.
name|innerToXContent
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|shuffleXContent
argument_list|(
name|builder
argument_list|)
argument_list|)
decl_stmt|;
operator|new
name|QueryParseContext
argument_list|(
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|SearchAfterBuilder
name|secondSearchAfterBuilder
init|=
name|SearchAfterBuilder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|searchAfterBuilder
argument_list|,
name|secondSearchAfterBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|searchAfterBuilder
argument_list|,
name|secondSearchAfterBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|searchAfterBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|secondSearchAfterBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWithNullArray
specifier|public
name|void
name|testWithNullArray
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchAfterBuilder
name|builder
init|=
operator|new
name|SearchAfterBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|setSortValues
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail on null array."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
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
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|"Values cannot be null."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWithEmptyArray
specifier|public
name|void
name|testWithEmptyArray
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchAfterBuilder
name|builder
init|=
operator|new
name|SearchAfterBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|setSortValues
argument_list|(
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail on empty array."
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
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|"Values must contains at least one value."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Explicitly tests what you can't list as a sortValue. What you can list is tested by {@link #randomSearchAfterBuilder()}.      */
DECL|method|testBadTypes
specifier|public
name|void
name|testBadTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|randomSearchFromBuilderWithSortValueThrows
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|randomSearchFromBuilderWithSortValueThrows
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|randomSearchFromBuilderWithSortValueThrows
argument_list|(
name|randomSearchAfterBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|randomSearchFromBuilderWithSortValueThrows
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|randomSearchFromBuilderWithSortValueThrows
specifier|private
specifier|static
name|void
name|randomSearchFromBuilderWithSortValueThrows
parameter_list|(
name|Object
name|containing
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a valid one
name|SearchAfterBuilder
name|builder
init|=
name|randomSearchAfterBuilder
argument_list|()
decl_stmt|;
comment|// Now replace its values with one containing the passed in object
name|Object
index|[]
name|values
init|=
name|builder
operator|.
name|getSortValues
argument_list|()
decl_stmt|;
name|values
index|[
name|between
argument_list|(
literal|0
argument_list|,
name|values
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
operator|=
name|containing
expr_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|builder
operator|.
name|setSortValues
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't handle search_after field value of type ["
operator|+
name|containing
operator|.
name|getClass
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

