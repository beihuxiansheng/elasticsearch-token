begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|xcontent
operator|.
name|NamedXContentRegistry
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
name|env
operator|.
name|Environment
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
name|IndicesModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchModule
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
name|AbstractQueryTestCase
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
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasSize
import|;
end_import

begin_class
DECL|class|BaseAggregationTestCase
specifier|public
specifier|abstract
class|class
name|BaseAggregationTestCase
parameter_list|<
name|AB
extends|extends
name|AbstractAggregationBuilder
parameter_list|<
name|AB
parameter_list|>
parameter_list|>
extends|extends
name|ESTestCase
block|{
DECL|field|STRING_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|STRING_FIELD_NAME
init|=
literal|"mapped_string"
decl_stmt|;
DECL|field|INT_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|INT_FIELD_NAME
init|=
literal|"mapped_int"
decl_stmt|;
DECL|field|DOUBLE_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|DOUBLE_FIELD_NAME
init|=
literal|"mapped_double"
decl_stmt|;
DECL|field|BOOLEAN_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|BOOLEAN_FIELD_NAME
init|=
literal|"mapped_boolean"
decl_stmt|;
DECL|field|DATE_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|DATE_FIELD_NAME
init|=
literal|"mapped_date"
decl_stmt|;
DECL|field|IP_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|IP_FIELD_NAME
init|=
literal|"mapped_ip"
decl_stmt|;
DECL|field|currentTypes
specifier|private
name|String
index|[]
name|currentTypes
decl_stmt|;
DECL|method|getCurrentTypes
specifier|protected
name|String
index|[]
name|getCurrentTypes
parameter_list|()
block|{
return|return
name|currentTypes
return|;
block|}
DECL|field|namedWriteableRegistry
specifier|private
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|xContentRegistry
specifier|private
name|NamedXContentRegistry
name|xContentRegistry
decl_stmt|;
DECL|field|parseFieldMatcher
specifier|protected
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|method|createTestAggregatorBuilder
specifier|protected
specifier|abstract
name|AB
name|createTestAggregatorBuilder
parameter_list|()
function_decl|;
comment|/**      * Setup for the whole base test class.      */
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
name|AbstractQueryTestCase
operator|.
name|class
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndicesModule
name|indicesModule
init|=
operator|new
name|IndicesModule
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|SearchModule
name|searchModule
init|=
operator|new
name|SearchModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NamedWriteableRegistry
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|addAll
argument_list|(
name|indicesModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
name|entries
operator|.
name|addAll
argument_list|(
name|searchModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|xContentRegistry
operator|=
operator|new
name|NamedXContentRegistry
argument_list|(
name|searchModule
operator|.
name|getNamedXContents
argument_list|()
argument_list|)
expr_stmt|;
comment|//create some random type with some default field, those types will stick around for all of the subclasses
name|currentTypes
operator|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
index|]
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
name|currentTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|type
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|currentTypes
index|[
name|i
index|]
operator|=
name|type
expr_stmt|;
block|}
name|parseFieldMatcher
operator|=
name|ParseFieldMatcher
operator|.
name|STRICT
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|xContentRegistry
specifier|protected
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|()
block|{
return|return
name|xContentRegistry
return|;
block|}
comment|/**      * Generic test that creates new AggregatorFactory from the test      * AggregatorFactory and checks both for equality and asserts equality on      * the two queries.      */
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|AB
name|testAgg
init|=
name|createTestAggregatorBuilder
argument_list|()
decl_stmt|;
name|AggregatorFactories
operator|.
name|Builder
name|factoriesBuilder
init|=
name|AggregatorFactories
operator|.
name|builder
argument_list|()
operator|.
name|addAggregator
argument_list|(
name|testAgg
argument_list|)
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
name|factoriesBuilder
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
name|XContentBuilder
name|shuffled
init|=
name|shuffleXContent
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|shuffled
argument_list|)
decl_stmt|;
name|AggregationBuilder
name|newAgg
init|=
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|newAgg
argument_list|,
name|testAgg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
argument_list|,
name|newAgg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
operator|.
name|hashCode
argument_list|()
argument_list|,
name|newAgg
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|protected
name|AggregationBuilder
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|parser
argument_list|,
name|parseFieldMatcher
argument_list|)
decl_stmt|;
name|assertSame
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
argument_list|)
expr_stmt|;
name|AggregatorFactories
operator|.
name|Builder
name|parsed
init|=
name|AggregatorFactories
operator|.
name|parseAggregators
argument_list|(
name|parseContext
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parsed
operator|.
name|getAggregatorFactories
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parsed
operator|.
name|getPipelineAggregatorFactories
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|AggregationBuilder
name|newAgg
init|=
name|parsed
operator|.
name|getAggregatorFactories
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newAgg
argument_list|)
expr_stmt|;
return|return
name|newAgg
return|;
block|}
comment|/**      * Test serialization and deserialization of the test AggregatorFactory.      */
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|AB
name|testAgg
init|=
name|createTestAggregatorBuilder
argument_list|()
decl_stmt|;
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
name|output
operator|.
name|writeNamedWriteable
argument_list|(
name|testAgg
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
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
name|AggregationBuilder
name|deserialized
init|=
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|AggregationBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testAgg
argument_list|,
name|deserialized
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
operator|.
name|hashCode
argument_list|()
argument_list|,
name|deserialized
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|testAgg
argument_list|,
name|deserialized
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO we only change name and boost, we should extend by any sub-test supplying a "mutate" method that randomly changes one
comment|// aspect of the object under test
name|checkEqualsAndHashCode
argument_list|(
name|createTestAggregatorBuilder
argument_list|()
argument_list|,
name|this
operator|::
name|copyAggregation
argument_list|)
expr_stmt|;
block|}
comment|// we use the streaming infra to create a copy of the query provided as
comment|// argument
DECL|method|copyAggregation
specifier|private
name|AB
name|copyAggregation
parameter_list|(
name|AB
name|agg
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
name|agg
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
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|AB
name|secondAgg
init|=
operator|(
name|AB
operator|)
name|namedWriteableRegistry
operator|.
name|getReader
argument_list|(
name|AggregationBuilder
operator|.
name|class
argument_list|,
name|agg
operator|.
name|getWriteableName
argument_list|()
argument_list|)
operator|.
name|read
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|secondAgg
return|;
block|}
block|}
block|}
DECL|method|randomNumericField
specifier|public
name|String
name|randomNumericField
parameter_list|()
block|{
name|int
name|randomInt
init|=
name|randomInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|randomInt
condition|)
block|{
case|case
literal|0
case|:
return|return
name|DATE_FIELD_NAME
return|;
case|case
literal|1
case|:
return|return
name|DOUBLE_FIELD_NAME
return|;
case|case
literal|2
case|:
default|default:
return|return
name|INT_FIELD_NAME
return|;
block|}
block|}
block|}
end_class

end_unit

