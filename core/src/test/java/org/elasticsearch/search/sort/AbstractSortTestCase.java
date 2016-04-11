begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|search
operator|.
name|SortField
import|;
end_import

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
name|Accountable
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
name|IndexSettings
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
name|cache
operator|.
name|bitset
operator|.
name|BitsetFilterCache
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|mapper
operator|.
name|ContentPath
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
name|mapper
operator|.
name|MappedFieldType
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
name|mapper
operator|.
name|Mapper
operator|.
name|BuilderContext
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
name|mapper
operator|.
name|core
operator|.
name|DoubleFieldMapper
operator|.
name|DoubleFieldType
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
operator|.
name|Nested
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
name|index
operator|.
name|query
operator|.
name|QueryShardContext
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
name|shard
operator|.
name|ShardId
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
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCache
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
name|CompiledScript
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
name|Script
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
name|ScriptContext
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
name|ScriptContextRegistry
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
name|ScriptEngineRegistry
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
name|ScriptService
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
name|ScriptServiceTests
operator|.
name|TestEngineService
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
name|ScriptSettings
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
name|watcher
operator|.
name|ResourceWatcherService
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
name|nio
operator|.
name|file
operator|.
name|Path
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
DECL|class|AbstractSortTestCase
specifier|public
specifier|abstract
class|class
name|AbstractSortTestCase
parameter_list|<
name|T
extends|extends
name|SortBuilder
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|ESTestCase
block|{
DECL|field|namedWriteableRegistry
specifier|protected
specifier|static
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|NUMBER_OF_TESTBUILDERS
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_TESTBUILDERS
init|=
literal|20
decl_stmt|;
DECL|field|indicesQueriesRegistry
specifier|static
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|static
name|ScriptService
name|scriptService
decl_stmt|;
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
name|Path
name|genericConfigFolder
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Settings
name|baseSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_CONF_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|genericConfigFolder
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|baseSettings
argument_list|)
decl_stmt|;
name|ScriptContextRegistry
name|scriptContextRegistry
init|=
operator|new
name|ScriptContextRegistry
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|ScriptEngineRegistry
name|scriptEngineRegistry
init|=
operator|new
name|ScriptEngineRegistry
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ScriptEngineRegistry
operator|.
name|ScriptEngineRegistration
argument_list|(
name|TestEngineService
operator|.
name|class
argument_list|,
name|TestEngineService
operator|.
name|TYPES
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ScriptSettings
name|scriptSettings
init|=
operator|new
name|ScriptSettings
argument_list|(
name|scriptEngineRegistry
argument_list|,
name|scriptContextRegistry
argument_list|)
decl_stmt|;
name|scriptService
operator|=
operator|new
name|ScriptService
argument_list|(
name|baseSettings
argument_list|,
name|environment
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|TestEngineService
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ResourceWatcherService
argument_list|(
name|baseSettings
argument_list|,
literal|null
argument_list|)
argument_list|,
name|scriptEngineRegistry
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptSettings
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|CompiledScript
name|compile
parameter_list|(
name|Script
name|script
parameter_list|,
name|ScriptContext
name|scriptContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
return|return
operator|new
name|CompiledScript
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"mockName"
argument_list|,
literal|"test"
argument_list|,
name|script
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|()
expr_stmt|;
name|indicesQueriesRegistry
operator|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|namedWriteableRegistry
argument_list|)
operator|.
name|getQueryParserRegistry
argument_list|()
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
comment|/** Returns random sort that is put under test */
DECL|method|createTestItem
specifier|protected
specifier|abstract
name|T
name|createTestItem
parameter_list|()
function_decl|;
comment|/** Returns mutated version of original so the returned sort is different in terms of equals/hashcode */
DECL|method|mutate
specifier|protected
specifier|abstract
name|T
name|mutate
parameter_list|(
name|T
name|original
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Parse the sort from xContent. Just delegate to the SortBuilder's static fromXContent method. */
DECL|method|fromXContent
specifier|protected
specifier|abstract
name|T
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Test that creates new sort from a random test sort and checks both for equality      */
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
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
name|T
name|testItem
init|=
name|createTestItem
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
name|testItem
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
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|XContentParser
name|itemParser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|shuffled
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|itemParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|/*              * filter out name of sort, or field name to sort on for element fieldSort              */
name|itemParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|elementName
init|=
name|itemParser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|itemParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
name|reset
argument_list|(
name|itemParser
argument_list|)
expr_stmt|;
name|T
name|parsedItem
init|=
name|fromXContent
argument_list|(
name|context
argument_list|,
name|elementName
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|testItem
argument_list|,
name|parsedItem
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testItem
argument_list|,
name|parsedItem
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testItem
operator|.
name|hashCode
argument_list|()
argument_list|,
name|parsedItem
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * test that build() outputs a {@link SortField} that is similar to the one      * we would get when parsing the xContent the sort builder is rendering out      */
DECL|method|testBuildSortField
specifier|public
name|void
name|testBuildSortField
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryShardContext
name|mockShardContext
init|=
name|createMockShardContext
argument_list|()
decl_stmt|;
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
name|T
name|sortBuilder
init|=
name|createTestItem
argument_list|()
decl_stmt|;
name|SortField
name|sortField
init|=
name|sortBuilder
operator|.
name|build
argument_list|(
name|mockShardContext
argument_list|)
decl_stmt|;
name|sortFieldAssertions
argument_list|(
name|sortBuilder
argument_list|,
name|sortField
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sortFieldAssertions
specifier|protected
specifier|abstract
name|void
name|sortFieldAssertions
parameter_list|(
name|T
name|builder
parameter_list|,
name|SortField
name|sortField
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Test serialization and deserialization of the test sort.      */
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
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
name|T
name|testsort
init|=
name|createTestItem
argument_list|()
decl_stmt|;
name|T
name|deserializedsort
init|=
name|copyItem
argument_list|(
name|testsort
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testsort
argument_list|,
name|deserializedsort
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testsort
operator|.
name|hashCode
argument_list|()
argument_list|,
name|deserializedsort
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|testsort
argument_list|,
name|deserializedsort
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test equality and hashCode properties      */
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|IOException
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
name|T
name|firstsort
init|=
name|createTestItem
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"sort is equal to null"
argument_list|,
name|firstsort
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"sort is equal to incompatible type"
argument_list|,
name|firstsort
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sort is not equal to self"
argument_list|,
name|firstsort
operator|.
name|equals
argument_list|(
name|firstsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"same sort's hashcode returns different values if called multiple times"
argument_list|,
name|firstsort
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstsort
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"different sorts should not be equal"
argument_list|,
name|mutate
argument_list|(
name|firstsort
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|firstsort
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"different sorts should have different hashcode"
argument_list|,
name|mutate
argument_list|(
name|firstsort
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|firstsort
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|T
name|secondsort
init|=
name|copyItem
argument_list|(
name|firstsort
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sort is not equal to self"
argument_list|,
name|secondsort
operator|.
name|equals
argument_list|(
name|secondsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sort is not equal to its copy"
argument_list|,
name|firstsort
operator|.
name|equals
argument_list|(
name|secondsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|secondsort
operator|.
name|equals
argument_list|(
name|firstsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"sort copy's hashcode is different from original hashcode"
argument_list|,
name|secondsort
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstsort
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|T
name|thirdsort
init|=
name|copyItem
argument_list|(
name|secondsort
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sort is not equal to self"
argument_list|,
name|thirdsort
operator|.
name|equals
argument_list|(
name|thirdsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sort is not equal to its copy"
argument_list|,
name|secondsort
operator|.
name|equals
argument_list|(
name|thirdsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"sort copy's hashcode is different from original hashcode"
argument_list|,
name|secondsort
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdsort
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
name|firstsort
operator|.
name|equals
argument_list|(
name|thirdsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"sort copy's hashcode is different from original hashcode"
argument_list|,
name|firstsort
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdsort
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
name|thirdsort
operator|.
name|equals
argument_list|(
name|secondsort
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|thirdsort
operator|.
name|equals
argument_list|(
name|firstsort
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createMockShardContext
specifier|protected
name|QueryShardContext
name|createMockShardContext
parameter_list|()
block|{
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"_na_"
argument_list|)
decl_stmt|;
name|IndexSettings
name|idxSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|IndicesFieldDataCache
name|cache
init|=
operator|new
name|IndicesFieldDataCache
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexFieldDataService
name|ifds
init|=
operator|new
name|IndexFieldDataService
argument_list|(
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|cache
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|BitsetFilterCache
name|bitsetFilterCache
init|=
operator|new
name|BitsetFilterCache
argument_list|(
name|idxSettings
argument_list|,
operator|new
name|BitsetFilterCache
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{             }
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryShardContext
argument_list|(
name|idxSettings
argument_list|,
name|bitsetFilterCache
argument_list|,
name|ifds
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|scriptService
argument_list|,
name|indicesQueriesRegistry
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|fieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|provideMappedFieldType
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectMapper
name|getObjectMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|BuilderContext
name|context
init|=
operator|new
name|BuilderContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|ContentPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ObjectMapper
operator|.
name|Builder
argument_list|<>
argument_list|(
name|name
argument_list|)
operator|.
name|nested
argument_list|(
name|Nested
operator|.
name|newNested
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Return a field type. We use {@link DoubleFieldType} by default since it is compatible with all sort modes      * Tests that require other field type than double can override this.      */
DECL|method|provideMappedFieldType
specifier|protected
name|MappedFieldType
name|provideMappedFieldType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|DoubleFieldType
name|doubleFieldType
init|=
operator|new
name|DoubleFieldType
argument_list|()
decl_stmt|;
name|doubleFieldType
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|doubleFieldType
operator|.
name|setHasDocValues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|doubleFieldType
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|copyItem
specifier|private
name|T
name|copyItem
parameter_list|(
name|T
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
operator|(
name|T
operator|)
name|namedWriteableRegistry
operator|.
name|getReader
argument_list|(
name|SortBuilder
operator|.
name|class
argument_list|,
name|original
operator|.
name|getWriteableName
argument_list|()
argument_list|)
operator|.
name|read
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

