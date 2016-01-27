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
name|NamedWriteableAwareStreamInput
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
name|common
operator|.
name|settings
operator|.
name|Settings
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
name|XContentHelper
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
name|MatchAllQueryParser
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
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesRegistry
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
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
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
DECL|field|namedWriteableRegistry
specifier|private
specifier|static
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|indicesQueriesRegistry
specifier|private
specifier|static
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
comment|/**      * setup for the whole base test class      */
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|()
expr_stmt|;
name|indicesQueriesRegistry
operator|=
operator|new
name|IndicesQueriesRegistry
argument_list|(
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"match_all"
argument_list|,
operator|new
name|MatchAllQueryParser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|namedWriteableRegistry
operator|=
literal|null
expr_stmt|;
name|indicesQueriesRegistry
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|randomSearchFromBuilder
specifier|private
specifier|final
name|SearchAfterBuilder
name|randomSearchFromBuilder
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
literal|8
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
specifier|final
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
literal|8
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
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
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
name|PROTOTYPE
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
literal|null
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
try|try
init|(
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|original
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|StreamInput
operator|.
name|wrap
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
return|return
name|SearchAfterBuilder
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
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
name|randomSearchFromBuilder
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
name|SearchAfterBuilder
name|firstBuilder
init|=
name|randomSearchFromBuilder
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"searchFrom is equal to null"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"searchFrom is equal to incompatible type"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not equal to self"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
name|firstBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"same searchFrom's hashcode returns different values if called multiple times"
argument_list|,
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SearchAfterBuilder
name|secondBuilder
init|=
name|serializedCopy
argument_list|(
name|firstBuilder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not equal to self"
argument_list|,
name|secondBuilder
operator|.
name|equals
argument_list|(
name|secondBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not equal to its copy"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
name|secondBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|secondBuilder
operator|.
name|equals
argument_list|(
name|firstBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"searchFrom copy's hashcode is different from original hashcode"
argument_list|,
name|secondBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SearchAfterBuilder
name|thirdBuilder
init|=
name|serializedCopy
argument_list|(
name|secondBuilder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not equal to self"
argument_list|,
name|thirdBuilder
operator|.
name|equals
argument_list|(
name|thirdBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not equal to its copy"
argument_list|,
name|secondBuilder
operator|.
name|equals
argument_list|(
name|thirdBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"searchFrom copy's hashcode is different from original hashcode"
argument_list|,
name|secondBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not transitive"
argument_list|,
name|firstBuilder
operator|.
name|equals
argument_list|(
name|thirdBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"searchFrom copy's hashcode is different from original hashcode"
argument_list|,
name|firstBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not symmetric"
argument_list|,
name|thirdBuilder
operator|.
name|equals
argument_list|(
name|secondBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"searchFrom is not symmetric"
argument_list|,
name|thirdBuilder
operator|.
name|equals
argument_list|(
name|firstBuilder
argument_list|)
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
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|)
decl_stmt|;
name|context
operator|.
name|parseFieldMatcher
argument_list|(
operator|new
name|ParseFieldMatcher
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
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
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|parser
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
name|PROTOTYPE
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
literal|null
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
DECL|method|testWithNullValue
specifier|public
name|void
name|testWithNullValue
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
name|builder
operator|.
name|setSortValues
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|1
block|,
literal|"1"
block|,
literal|null
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|serializedCopy
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail on null values"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
literal|"Can't handle search_after field value of type [null]"
argument_list|)
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
block|}
end_class

end_unit

