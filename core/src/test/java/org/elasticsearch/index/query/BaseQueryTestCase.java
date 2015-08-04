begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
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
name|IndexMetaData
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
name|compress
operator|.
name|CompressedXContent
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
name|FilterStreamInput
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
name|IndexNameModule
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
name|analysis
operator|.
name|AnalysisModule
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
name|IndexCacheModule
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
name|MapperService
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
name|functionscore
operator|.
name|FunctionScoreModule
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
name|support
operator|.
name|QueryParsers
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
name|settings
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
name|index
operator|.
name|similarity
operator|.
name|SimilarityModule
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
name|IndicesQueriesModule
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
name|TestSearchContext
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
name|VersionUtils
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
name|is
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
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
name|nullValue
import|;
end_import

begin_class
DECL|class|BaseQueryTestCase
specifier|public
specifier|abstract
class|class
name|BaseQueryTestCase
parameter_list|<
name|QB
extends|extends
name|AbstractQueryBuilder
parameter_list|<
name|QB
parameter_list|>
parameter_list|>
extends|extends
name|ESTestCase
block|{
DECL|field|OBJECT_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|OBJECT_FIELD_NAME
init|=
literal|"mapped_object"
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
DECL|field|INT_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|INT_FIELD_NAME
init|=
literal|"mapped_int"
decl_stmt|;
DECL|field|STRING_FIELD_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|STRING_FIELD_NAME
init|=
literal|"mapped_string"
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
name|DATE_FIELD_NAME
block|,
name|INT_FIELD_NAME
block|,
name|STRING_FIELD_NAME
block|,
name|DOUBLE_FIELD_NAME
block|,
name|BOOLEAN_FIELD_NAME
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
DECL|field|queryParserService
specifier|private
specifier|static
name|IndexQueryParserService
name|queryParserService
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
comment|/**      * Setup for the whole base test class.      * @throws IOException      */
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
name|BaseQueryTestCase
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
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_1_0_0
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|index
operator|=
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
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
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
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
name|IndicesQueriesModule
argument_list|()
argument_list|,
operator|new
name|ScriptModule
argument_list|(
name|settings
argument_list|)
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
name|IndexCacheModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|AnalysisModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|SimilarityModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexNameModule
argument_list|(
name|index
argument_list|)
argument_list|,
operator|new
name|FunctionScoreModule
argument_list|()
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
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|createInjector
argument_list|()
expr_stmt|;
name|queryParserService
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|IndexQueryParserService
operator|.
name|class
argument_list|)
expr_stmt|;
name|MapperService
name|mapperService
init|=
name|queryParserService
operator|.
name|mapperService
decl_stmt|;
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
name|mapperService
operator|.
name|merge
argument_list|(
name|type
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
name|type
argument_list|,
name|DATE_FIELD_NAME
argument_list|,
literal|"type=date"
argument_list|,
name|INT_FIELD_NAME
argument_list|,
literal|"type=integer"
argument_list|,
name|DOUBLE_FIELD_NAME
argument_list|,
literal|"type=double"
argument_list|,
name|BOOLEAN_FIELD_NAME
argument_list|,
literal|"type=boolean"
argument_list|,
name|STRING_FIELD_NAME
argument_list|,
literal|"type=string"
argument_list|,
name|OBJECT_FIELD_NAME
argument_list|,
literal|"type=object"
argument_list|)
operator|.
name|string
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// also add mappings for two inner field in the object field
name|mapperService
operator|.
name|merge
argument_list|(
name|type
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
literal|"{\"properties\":{\""
operator|+
name|OBJECT_FIELD_NAME
operator|+
literal|"\":{\"type\":\"object\","
operator|+
literal|"\"properties\":{\""
operator|+
name|DATE_FIELD_NAME
operator|+
literal|"\":{\"type\":\"date\"},\""
operator|+
name|INT_FIELD_NAME
operator|+
literal|"\":{\"type\":\"integer\"}}}}}"
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|currentTypes
index|[
name|i
index|]
operator|=
name|type
expr_stmt|;
block|}
name|namedWriteableRegistry
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|NamedWriteableRegistry
operator|.
name|class
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
name|queryParserService
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
comment|//some query (e.g. range query) have a different behaviour depending on whether the current search context is set or not
comment|//which is why we randomly set the search context, which will internally also do QueryParseContext.setTypes(types)
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|types
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
annotation|@
name|After
DECL|method|afterTest
specifier|public
name|void
name|afterTest
parameter_list|()
block|{
name|QueryParseContext
operator|.
name|removeTypes
argument_list|()
expr_stmt|;
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
DECL|method|createTestQueryBuilder
specifier|protected
specifier|final
name|QB
name|createTestQueryBuilder
parameter_list|()
block|{
name|QB
name|query
init|=
name|doCreateTestQueryBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|supportsBoostAndQueryName
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|query
operator|.
name|boost
argument_list|(
literal|2.0f
operator|/
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|query
operator|.
name|queryName
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|query
return|;
block|}
comment|/**      * Create the query that is being tested      */
DECL|method|doCreateTestQueryBuilder
specifier|protected
specifier|abstract
name|QB
name|doCreateTestQueryBuilder
parameter_list|()
function_decl|;
comment|/**      * Generic test that creates new query from the test query and checks both for equality      * and asserts equality on the two queries.      */
annotation|@
name|Test
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|QB
name|testQuery
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
name|QueryParseContext
name|context
init|=
name|createContext
argument_list|()
decl_stmt|;
name|String
name|contentString
init|=
name|testQuery
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|assertQueryHeader
argument_list|(
name|parser
argument_list|,
name|testQuery
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|QueryBuilder
name|newQuery
init|=
name|queryParserService
operator|.
name|queryParser
argument_list|(
name|testQuery
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|newQuery
argument_list|,
name|testQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testQuery
argument_list|,
name|newQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testQuery
operator|.
name|hashCode
argument_list|()
argument_list|,
name|newQuery
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test creates the {@link Query} from the {@link QueryBuilder} under test and delegates the      * assertions being made on the result to the implementing subclass.      */
annotation|@
name|Test
DECL|method|testToQuery
specifier|public
name|void
name|testToQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryParseContext
name|context
init|=
name|createContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|QB
name|firstQuery
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
name|Query
name|firstLuceneQuery
init|=
name|firstQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertLuceneQuery
argument_list|(
name|firstQuery
argument_list|,
name|firstLuceneQuery
argument_list|,
name|context
argument_list|)
expr_stmt|;
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
name|firstQuery
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
name|FilterStreamInput
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
name|QueryBuilder
argument_list|<
name|?
extends|extends
name|QueryBuilder
argument_list|>
name|prototype
init|=
name|queryParserService
operator|.
name|queryParser
argument_list|(
name|firstQuery
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getBuilderPrototype
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|QB
name|secondQuery
init|=
operator|(
name|QB
operator|)
name|prototype
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|//query _name never should affect the result of toQuery, we randomly set it to make sure
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|secondQuery
operator|.
name|queryName
argument_list|(
name|secondQuery
operator|.
name|queryName
argument_list|()
operator|==
literal|null
condition|?
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
else|:
name|secondQuery
operator|.
name|queryName
argument_list|()
operator|+
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Query
name|secondLuceneQuery
init|=
name|secondQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertLuceneQuery
argument_list|(
name|secondQuery
argument_list|,
name|secondLuceneQuery
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"two equivalent query builders lead to different lucene queries"
argument_list|,
name|secondLuceneQuery
argument_list|,
name|equalTo
argument_list|(
name|firstLuceneQuery
argument_list|)
argument_list|)
expr_stmt|;
comment|//if the initial lucene query is null, changing its boost won't have any effect, we shouldn't test that
comment|//otherwise makes sure that boost is taken into account in toQuery
if|if
condition|(
name|firstLuceneQuery
operator|!=
literal|null
condition|)
block|{
name|secondQuery
operator|.
name|boost
argument_list|(
name|firstQuery
operator|.
name|boost
argument_list|()
operator|+
literal|1f
operator|+
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
comment|//some queries don't support boost, their setter is a no-op
if|if
condition|(
name|supportsBoostAndQueryName
argument_list|()
condition|)
block|{
name|Query
name|thirdLuceneQuery
init|=
name|secondQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"modifying the boost doesn't affect the corresponding lucene query"
argument_list|,
name|firstLuceneQuery
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|thirdLuceneQuery
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Few queries allow you to set the boost and queryName but don't do anything with it. This method allows      * to disable boost and queryName related tests for those queries.      */
DECL|method|supportsBoostAndQueryName
specifier|protected
name|boolean
name|supportsBoostAndQueryName
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Checks the result of {@link QueryBuilder#toQuery(QueryParseContext)} given the original {@link QueryBuilder} and {@link QueryParseContext}.      * Verifies that named queries and boost are properly handled and delegates to {@link #doAssertLuceneQuery(AbstractQueryBuilder, Query, QueryParseContext)}      * for query specific checks.      */
DECL|method|assertLuceneQuery
specifier|protected
specifier|final
name|void
name|assertLuceneQuery
parameter_list|(
name|QB
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|queryBuilder
operator|.
name|queryName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Query
name|namedQuery
init|=
name|context
operator|.
name|copyNamedQueries
argument_list|()
operator|.
name|get
argument_list|(
name|queryBuilder
operator|.
name|queryName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|namedQuery
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|boost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doAssertLuceneQuery
argument_list|(
name|queryBuilder
argument_list|,
name|query
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks the result of {@link QueryBuilder#toQuery(QueryParseContext)} given the original {@link QueryBuilder} and {@link QueryParseContext}.      * Contains the query specific checks to be implemented by subclasses.      */
DECL|method|doAssertLuceneQuery
specifier|protected
specifier|abstract
name|void
name|doAssertLuceneQuery
parameter_list|(
name|QB
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Test serialization and deserialization of the test query.      */
annotation|@
name|Test
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|QB
name|testQuery
init|=
name|createTestQueryBuilder
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
name|testQuery
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
name|FilterStreamInput
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
name|QueryBuilder
argument_list|<
name|?
extends|extends
name|QueryBuilder
argument_list|>
name|prototype
init|=
name|queryParserService
operator|.
name|queryParser
argument_list|(
name|testQuery
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getBuilderPrototype
argument_list|()
decl_stmt|;
name|QueryBuilder
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
name|testQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializedQuery
operator|.
name|hashCode
argument_list|()
argument_list|,
name|testQuery
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserializedQuery
argument_list|,
name|testQuery
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @return a new {@link QueryParseContext} based on the base test index and queryParserService      */
DECL|method|createContext
specifier|protected
specifier|static
name|QueryParseContext
name|createContext
parameter_list|()
block|{
name|QueryParseContext
name|queryParseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|index
argument_list|,
name|queryParserService
argument_list|)
decl_stmt|;
name|queryParseContext
operator|.
name|parseFieldMatcher
argument_list|(
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
return|return
name|queryParseContext
return|;
block|}
DECL|method|assertQueryHeader
specifier|protected
specifier|static
name|void
name|assertQueryHeader
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|expectedParserName
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|is
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
name|is
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
name|currentName
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedParserName
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
name|is
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertValidate
specifier|protected
specifier|static
name|void
name|assertValidate
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|int
name|totalExpectedErrors
parameter_list|)
block|{
name|QueryValidationException
name|queryValidationException
init|=
name|queryBuilder
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|totalExpectedErrors
operator|>
literal|0
condition|)
block|{
name|assertThat
argument_list|(
name|queryValidationException
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryValidationException
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|totalExpectedErrors
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|queryValidationException
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * create a random value for either {@link BaseQueryTestCase#BOOLEAN_FIELD_NAME}, {@link BaseQueryTestCase#INT_FIELD_NAME},      * {@link BaseQueryTestCase#DOUBLE_FIELD_NAME} or {@link BaseQueryTestCase#STRING_FIELD_NAME}, or a String value by default      */
DECL|method|randomValueForField
specifier|protected
specifier|static
name|Object
name|randomValueForField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Object
name|value
decl_stmt|;
switch|switch
condition|(
name|fieldName
condition|)
block|{
case|case
name|BOOLEAN_FIELD_NAME
case|:
name|value
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
break|break;
case|case
name|INT_FIELD_NAME
case|:
name|value
operator|=
name|randomInt
argument_list|()
expr_stmt|;
break|break;
case|case
name|DOUBLE_FIELD_NAME
case|:
name|value
operator|=
name|randomDouble
argument_list|()
expr_stmt|;
break|break;
case|case
name|STRING_FIELD_NAME
case|:
name|value
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
break|break;
default|default :
name|value
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Helper method to return a random rewrite method      */
DECL|method|getRandomRewriteMethod
specifier|protected
specifier|static
name|String
name|getRandomRewriteMethod
parameter_list|()
block|{
name|String
name|rewrite
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|rewrite
operator|=
name|randomFrom
argument_list|(
name|QueryParsers
operator|.
name|CONSTANT_SCORE
argument_list|,
name|QueryParsers
operator|.
name|SCORING_BOOLEAN
argument_list|,
name|QueryParsers
operator|.
name|CONSTANT_SCORE_BOOLEAN
argument_list|)
operator|.
name|getPreferredName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rewrite
operator|=
name|randomFrom
argument_list|(
name|QueryParsers
operator|.
name|TOP_TERMS
argument_list|,
name|QueryParsers
operator|.
name|TOP_TERMS_BOOST
argument_list|,
name|QueryParsers
operator|.
name|TOP_TERMS_BLENDED_FREQS
argument_list|)
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|"1"
expr_stmt|;
block|}
return|return
name|rewrite
return|;
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
DECL|method|getRandomType
specifier|protected
name|String
name|getRandomType
parameter_list|()
block|{
return|return
operator|(
name|currentTypes
operator|.
name|length
operator|==
literal|0
operator|)
condition|?
name|MetaData
operator|.
name|ALL
else|:
name|randomFrom
argument_list|(
name|currentTypes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

