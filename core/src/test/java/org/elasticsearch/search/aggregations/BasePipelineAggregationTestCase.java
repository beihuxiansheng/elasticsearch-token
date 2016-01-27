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
name|cluster
operator|.
name|ClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|inject
operator|.
name|AbstractModule
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
name|inject
operator|.
name|Injector
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
name|inject
operator|.
name|ModulesBuilder
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
name|inject
operator|.
name|util
operator|.
name|Providers
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
name|settings
operator|.
name|SettingsFilter
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
name|SettingsModule
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
name|env
operator|.
name|EnvironmentModule
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
name|Index
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
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
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
name|breaker
operator|.
name|NoneCircuitBreakerService
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
name|script
operator|.
name|ScriptModule
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
name|search
operator|.
name|aggregations
operator|.
name|pipeline
operator|.
name|PipelineAggregatorFactory
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
name|internal
operator|.
name|SearchContext
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
name|elasticsearch
operator|.
name|test
operator|.
name|IndexSettingsModule
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
name|TestSearchContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPoolModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
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
DECL|class|BasePipelineAggregationTestCase
specifier|public
specifier|abstract
class|class
name|BasePipelineAggregationTestCase
parameter_list|<
name|AF
extends|extends
name|PipelineAggregatorFactory
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
DECL|field|OBJECT_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|OBJECT_FIELD_NAME
init|=
literal|"mapped_object"
decl_stmt|;
DECL|field|mappedFieldNames
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|mappedFieldNames
init|=
operator|new
name|String
index|[]
block|{
name|STRING_FIELD_NAME
block|,
name|INT_FIELD_NAME
block|,
name|DOUBLE_FIELD_NAME
block|,
name|BOOLEAN_FIELD_NAME
block|,
name|DATE_FIELD_NAME
block|,
name|OBJECT_FIELD_NAME
block|}
decl_stmt|;
DECL|field|injector
specifier|private
specifier|static
name|Injector
name|injector
decl_stmt|;
DECL|field|index
specifier|private
specifier|static
name|Index
name|index
decl_stmt|;
DECL|field|currentTypes
specifier|private
specifier|static
name|String
index|[]
name|currentTypes
decl_stmt|;
DECL|method|getCurrentTypes
specifier|protected
specifier|static
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
specifier|static
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|aggParsers
specifier|private
specifier|static
name|AggregatorParsers
name|aggParsers
decl_stmt|;
DECL|field|parseFieldMatcher
specifier|private
specifier|static
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|field|queriesRegistry
specifier|private
specifier|static
name|IndicesQueriesRegistry
name|queriesRegistry
decl_stmt|;
DECL|method|createTestAggregatorFactory
specifier|protected
specifier|abstract
name|AF
name|createTestAggregatorFactory
parameter_list|()
function_decl|;
comment|/**      * Setup for the whole base test class.      */
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|BasePipelineAggregationTestCase
operator|.
name|class
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|()
expr_stmt|;
name|index
operator|=
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|SettingsModule
name|settingsModule
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|,
operator|new
name|SettingsFilter
argument_list|(
name|settings
argument_list|)
argument_list|)
decl_stmt|;
name|injector
operator|=
operator|new
name|ModulesBuilder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|EnvironmentModule
argument_list|(
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|)
argument_list|,
name|settingsModule
argument_list|,
operator|new
name|ThreadPoolModule
argument_list|(
operator|new
name|ThreadPool
argument_list|(
name|settings
argument_list|)
argument_list|)
argument_list|,
operator|new
name|ScriptModule
argument_list|(
name|settingsModule
argument_list|)
argument_list|,
operator|new
name|IndicesModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bindMapperExtension
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|SearchModule
argument_list|(
name|settings
argument_list|,
name|namedWriteableRegistry
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configureSearch
parameter_list|()
block|{
comment|// Skip me
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureSuggesters
parameter_list|()
block|{
comment|// Skip me
block|}
block|}
argument_list|,
operator|new
name|IndexSettingsModule
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
argument_list|,
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
operator|(
name|ClusterService
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|CircuitBreakerService
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|NoneCircuitBreakerService
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|NamedWriteableRegistry
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|namedWriteableRegistry
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|createInjector
argument_list|()
expr_stmt|;
name|aggParsers
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|AggregatorParsers
operator|.
name|class
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
name|queriesRegistry
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|IndicesQueriesRegistry
operator|.
name|class
argument_list|)
expr_stmt|;
name|parseFieldMatcher
operator|=
name|ParseFieldMatcher
operator|.
name|STRICT
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
name|terminate
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|injector
operator|=
literal|null
expr_stmt|;
name|index
operator|=
literal|null
expr_stmt|;
name|aggParsers
operator|=
literal|null
expr_stmt|;
name|currentTypes
operator|=
literal|null
expr_stmt|;
name|namedWriteableRegistry
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|beforeTest
specifier|public
name|void
name|beforeTest
parameter_list|()
block|{
comment|//set some random types to be queried as part the search request, before each test
name|String
index|[]
name|types
init|=
name|getRandomTypes
argument_list|()
decl_stmt|;
name|TestSearchContext
name|testSearchContext
init|=
operator|new
name|TestSearchContext
argument_list|()
decl_stmt|;
name|testSearchContext
operator|.
name|setTypes
argument_list|(
name|types
argument_list|)
expr_stmt|;
name|SearchContext
operator|.
name|setCurrent
argument_list|(
name|testSearchContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|afterTest
specifier|public
name|void
name|afterTest
parameter_list|()
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
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
name|AF
name|testAgg
init|=
name|createTestAggregatorFactory
argument_list|()
decl_stmt|;
name|AggregatorFactories
name|factories
init|=
name|AggregatorFactories
operator|.
name|builder
argument_list|()
operator|.
name|skipResolveOrder
argument_list|()
operator|.
name|addPipelineAggregator
argument_list|(
name|testAgg
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|contentString
init|=
name|factories
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|contentString
argument_list|)
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentString
argument_list|)
operator|.
name|createParser
argument_list|(
name|contentString
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queriesRegistry
argument_list|)
decl_stmt|;
name|parseContext
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|(
name|parseFieldMatcher
argument_list|)
expr_stmt|;
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
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
operator|.
name|name
argument_list|()
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
operator|.
name|type
argument_list|()
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
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
name|PipelineAggregatorFactory
name|newAgg
init|=
name|aggParsers
operator|.
name|pipelineAggregator
argument_list|(
name|testAgg
operator|.
name|getWriteableName
argument_list|()
argument_list|)
operator|.
name|parse
argument_list|(
name|testAgg
operator|.
name|name
argument_list|()
argument_list|,
name|parser
argument_list|,
name|parseContext
argument_list|)
decl_stmt|;
name|assertSame
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
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
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
name|assertNotNull
argument_list|(
name|newAgg
argument_list|)
expr_stmt|;
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
comment|/**      * Test serialization and deserialization of the test AggregatorFactory.      */
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|AF
name|testAgg
init|=
name|createTestAggregatorFactory
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
name|testAgg
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
name|PipelineAggregatorFactory
name|prototype
init|=
name|aggParsers
operator|.
name|pipelineAggregator
argument_list|(
name|testAgg
operator|.
name|getWriteableName
argument_list|()
argument_list|)
operator|.
name|getFactoryPrototype
argument_list|()
decl_stmt|;
name|PipelineAggregatorFactory
name|deserializedQuery
init|=
name|prototype
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserializedQuery
argument_list|,
name|testAgg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializedQuery
operator|.
name|hashCode
argument_list|()
argument_list|,
name|testAgg
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserializedQuery
argument_list|,
name|testAgg
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
name|AF
name|firstAgg
init|=
name|createTestAggregatorFactory
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"aggregation is equal to null"
argument_list|,
name|firstAgg
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"aggregation is equal to incompatible type"
argument_list|,
name|firstAgg
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aggregation is not equal to self"
argument_list|,
name|firstAgg
operator|.
name|equals
argument_list|(
name|firstAgg
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"same aggregation's hashcode returns different values if called multiple times"
argument_list|,
name|firstAgg
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstAgg
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|AF
name|secondQuery
init|=
name|copyAggregation
argument_list|(
name|firstAgg
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"aggregation is not equal to self"
argument_list|,
name|secondQuery
operator|.
name|equals
argument_list|(
name|secondQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aggregation is not equal to its copy"
argument_list|,
name|firstAgg
operator|.
name|equals
argument_list|(
name|secondQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|secondQuery
operator|.
name|equals
argument_list|(
name|firstAgg
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"aggregation copy's hashcode is different from original hashcode"
argument_list|,
name|secondQuery
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstAgg
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|AF
name|thirdQuery
init|=
name|copyAggregation
argument_list|(
name|secondQuery
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"aggregation is not equal to self"
argument_list|,
name|thirdQuery
operator|.
name|equals
argument_list|(
name|thirdQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aggregation is not equal to its copy"
argument_list|,
name|secondQuery
operator|.
name|equals
argument_list|(
name|thirdQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"aggregation copy's hashcode is different from original hashcode"
argument_list|,
name|secondQuery
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdQuery
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
name|firstAgg
operator|.
name|equals
argument_list|(
name|thirdQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"aggregation copy's hashcode is different from original hashcode"
argument_list|,
name|firstAgg
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdQuery
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|thirdQuery
operator|.
name|equals
argument_list|(
name|secondQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|thirdQuery
operator|.
name|equals
argument_list|(
name|firstAgg
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// we use the streaming infra to create a copy of the query provided as
comment|// argument
DECL|method|copyAggregation
specifier|private
name|AF
name|copyAggregation
parameter_list|(
name|AF
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
name|PipelineAggregatorFactory
name|prototype
init|=
name|aggParsers
operator|.
name|pipelineAggregator
argument_list|(
name|agg
operator|.
name|getWriteableName
argument_list|()
argument_list|)
operator|.
name|getFactoryPrototype
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|AF
name|secondAgg
init|=
operator|(
name|AF
operator|)
name|prototype
operator|.
name|readFrom
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
DECL|method|getRandomTypes
specifier|protected
name|String
index|[]
name|getRandomTypes
parameter_list|()
block|{
name|String
index|[]
name|types
decl_stmt|;
if|if
condition|(
name|currentTypes
operator|.
name|length
operator|>
literal|0
operator|&&
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numberOfQueryTypes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|currentTypes
operator|.
name|length
argument_list|)
decl_stmt|;
name|types
operator|=
operator|new
name|String
index|[
name|numberOfQueryTypes
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
name|numberOfQueryTypes
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|randomFrom
argument_list|(
name|currentTypes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|types
operator|=
operator|new
name|String
index|[]
block|{
name|MetaData
operator|.
name|ALL
block|}
expr_stmt|;
block|}
else|else
block|{
name|types
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
return|return
name|types
return|;
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

