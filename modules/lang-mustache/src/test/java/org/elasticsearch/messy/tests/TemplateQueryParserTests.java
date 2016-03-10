begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.messy.tests
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|messy
operator|.
name|tests
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
name|MatchAllDocsQuery
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
name|search
operator|.
name|Query
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
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|common
operator|.
name|ParsingException
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
name|multibindings
operator|.
name|Multibinder
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
name|analysis
operator|.
name|AnalysisRegistry
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
name|AnalysisService
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
name|IndexFieldDataCache
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
name|QueryBuilder
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
name|query
operator|.
name|TemplateQueryParser
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
name|ScoreFunctionParser
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
name|index
operator|.
name|similarity
operator|.
name|SimilarityService
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
name|mapper
operator|.
name|MapperRegistry
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
name|ScriptModule
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
name|mustache
operator|.
name|MustacheScriptEngineService
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
name|test
operator|.
name|InternalSettingsPlugin
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
name|Before
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
name|lang
operator|.
name|reflect
operator|.
name|Proxy
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
name|containsString
import|;
end_import

begin_comment
comment|/**  * Test parsing and executing a template request.  */
end_comment

begin_comment
comment|// NOTE: this can't be migrated to ESSingleNodeTestCase because of the custom path.conf
end_comment

begin_class
DECL|class|TemplateQueryParserTests
specifier|public
class|class
name|TemplateQueryParserTests
extends|extends
name|ESTestCase
block|{
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|field|context
specifier|private
name|QueryShardContext
name|context
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
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
name|this
operator|.
name|getDataPath
argument_list|(
literal|"config"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Client
name|proxy
init|=
operator|(
name|Client
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|Client
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|Client
operator|.
name|class
block|}
operator|,
parameter_list|(
name|proxy1
parameter_list|,
name|method
parameter_list|,
name|args
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"client is just a dummy"
argument_list|)
throw|;
block|}
block|)
function|;
name|IndexSettings
name|idxSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"test"
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|Index
name|index
init|=
name|idxSettings
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|SettingsModule
name|settingsModule
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|ScriptModule
name|scriptModule
init|=
operator|new
name|ScriptModule
argument_list|()
decl_stmt|;
name|scriptModule
operator|.
name|prepareSettings
parameter_list|(
name|settingsModule
parameter_list|)
constructor_decl|;
comment|// TODO: make this use a mock engine instead of mustache and it will no longer be messy!
name|scriptModule
operator|.
name|addScriptEngine
argument_list|(
operator|new
name|ScriptEngineRegistry
operator|.
name|ScriptEngineRegistration
argument_list|(
name|MustacheScriptEngineService
operator|.
name|class
argument_list|,
name|MustacheScriptEngineService
operator|.
name|TYPES
argument_list|)
argument_list|)
expr_stmt|;
name|settingsModule
operator|.
name|registerSetting
parameter_list|(
name|InternalSettingsPlugin
operator|.
name|VERSION_CREATED
parameter_list|)
constructor_decl|;
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
name|SearchModule
argument_list|(
name|settings
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configureSearch
parameter_list|()
block|{
comment|// skip so we don't need transport
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureSuggesters
parameter_list|()
block|{
comment|// skip so we don't need IndicesService
block|}
block|}
argument_list|,
name|scriptModule
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
name|Client
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
comment|// not needed here
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|ScoreFunctionParser
operator|.
name|class
argument_list|)
expr_stmt|;
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
block|}
block|}
argument_list|)
operator|.
name|createInjector
argument_list|()
expr_stmt|;
name|AnalysisService
name|analysisService
init|=
operator|new
name|AnalysisRegistry
argument_list|(
literal|null
argument_list|,
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
name|idxSettings
argument_list|)
decl_stmt|;
name|ScriptService
name|scriptService
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|ScriptService
operator|.
name|class
argument_list|)
decl_stmt|;
name|SimilarityService
name|similarityService
init|=
operator|new
name|SimilarityService
argument_list|(
name|idxSettings
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|MapperRegistry
name|mapperRegistry
init|=
operator|new
name|IndicesModule
argument_list|()
operator|.
name|getMapperRegistry
argument_list|()
decl_stmt|;
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
name|idxSettings
argument_list|,
name|analysisService
argument_list|,
name|similarityService
argument_list|,
name|mapperRegistry
argument_list|,
parameter_list|()
lambda|->
name|context
argument_list|)
decl_stmt|;
name|IndicesFieldDataCache
name|cache
init|=
operator|new
name|IndicesFieldDataCache
argument_list|(
name|settings
argument_list|,
operator|new
name|IndexFieldDataCache
operator|.
name|Listener
argument_list|()
block|{}
argument_list|)
decl_stmt|;
name|IndexFieldDataService
name|indexFieldDataService
init|=
operator|new
name|IndexFieldDataService
argument_list|(
name|idxSettings
argument_list|,
name|cache
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|CircuitBreakerService
operator|.
name|class
argument_list|)
argument_list|,
name|mapperService
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
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{              }
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
block|{              }
block|}
argument_list|)
decl_stmt|;
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|IndicesQueriesRegistry
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|=
operator|new
name|QueryShardContext
argument_list|(
name|idxSettings
argument_list|,
name|bitsetFilterCache
argument_list|,
name|indexFieldDataService
argument_list|,
name|mapperService
argument_list|,
name|similarityService
argument_list|,
name|scriptService
argument_list|,
name|indicesQueriesRegistry
argument_list|)
expr_stmt|;
block|}
end_class

begin_function
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
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
block|}
end_function

begin_function
DECL|method|testParser
specifier|public
name|void
name|testParser
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|templateString
init|=
literal|"{"
operator|+
literal|"\"query\":{\"match_{{template}}\": {}},"
operator|+
literal|"\"params\":{\"template\":\"all\"}"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|templateSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|templateString
argument_list|)
operator|.
name|createParser
argument_list|(
name|templateString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|templateSourceParser
argument_list|)
expr_stmt|;
name|templateSourceParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|TemplateQueryParser
name|parser
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TemplateQueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|QueryBuilder
operator|.
name|rewriteQuery
argument_list|(
name|parser
operator|.
name|fromXContent
argument_list|(
name|context
operator|.
name|parseContext
argument_list|()
argument_list|)
argument_list|,
name|context
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Parsing template query failed."
argument_list|,
name|query
operator|instanceof
name|MatchAllDocsQuery
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testParseTemplateAsSingleStringWithConditionalClause
specifier|public
name|void
name|testParseTemplateAsSingleStringWithConditionalClause
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|templateString
init|=
literal|"{"
operator|+
literal|"  \"inline\" : \"{ \\\"match_{{#use_it}}{{template}}{{/use_it}}\\\":{} }\","
operator|+
literal|"  \"params\":{"
operator|+
literal|"    \"template\":\"all\","
operator|+
literal|"    \"use_it\": true"
operator|+
literal|"  }"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|templateSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|templateString
argument_list|)
operator|.
name|createParser
argument_list|(
name|templateString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|templateSourceParser
argument_list|)
expr_stmt|;
name|TemplateQueryParser
name|parser
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TemplateQueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|QueryBuilder
operator|.
name|rewriteQuery
argument_list|(
name|parser
operator|.
name|fromXContent
argument_list|(
name|context
operator|.
name|parseContext
argument_list|()
argument_list|)
argument_list|,
name|context
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Parsing template query failed."
argument_list|,
name|query
operator|instanceof
name|MatchAllDocsQuery
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**      * Test that the template query parser can parse and evaluate template      * expressed as a single string but still it expects only the query      * specification (thus this test should fail with specific exception).      */
end_comment

begin_function
DECL|method|testParseTemplateFailsToParseCompleteQueryAsSingleString
specifier|public
name|void
name|testParseTemplateFailsToParseCompleteQueryAsSingleString
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|templateString
init|=
literal|"{"
operator|+
literal|"  \"inline\" : \"{ \\\"size\\\": \\\"{{size}}\\\", \\\"query\\\":{\\\"match_all\\\":{}}}\","
operator|+
literal|"  \"params\":{"
operator|+
literal|"    \"size\":2"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|templateSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|templateString
argument_list|)
operator|.
name|createParser
argument_list|(
name|templateString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|templateSourceParser
argument_list|)
expr_stmt|;
name|TemplateQueryParser
name|parser
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TemplateQueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|fromXContent
argument_list|(
name|context
operator|.
name|parseContext
argument_list|()
argument_list|)
operator|.
name|rewrite
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ParsingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
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
name|containsString
argument_list|(
literal|"query malformed, no field after start_object"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|testParserCanExtractTemplateNames
specifier|public
name|void
name|testParserCanExtractTemplateNames
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|templateString
init|=
literal|"{ \"file\": \"storedTemplate\" ,\"params\":{\"template\":\"all\" } } "
decl_stmt|;
name|XContentParser
name|templateSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|templateString
argument_list|)
operator|.
name|createParser
argument_list|(
name|templateString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|templateSourceParser
argument_list|)
expr_stmt|;
name|templateSourceParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|TemplateQueryParser
name|parser
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TemplateQueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|QueryBuilder
operator|.
name|rewriteQuery
argument_list|(
name|parser
operator|.
name|fromXContent
argument_list|(
name|context
operator|.
name|parseContext
argument_list|()
argument_list|)
argument_list|,
name|context
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Parsing template query failed."
argument_list|,
name|query
operator|instanceof
name|MatchAllDocsQuery
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testMustRewrite
specifier|public
name|void
name|testMustRewrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|templateString
init|=
literal|"{ \"file\": \"storedTemplate\" ,\"params\":{\"template\":\"all\" } } "
decl_stmt|;
name|XContentParser
name|templateSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|templateString
argument_list|)
operator|.
name|createParser
argument_list|(
name|templateString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|templateSourceParser
argument_list|)
expr_stmt|;
name|templateSourceParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|TemplateQueryParser
name|parser
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TemplateQueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|fromXContent
argument_list|(
name|context
operator|.
name|parseContext
argument_list|()
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"this query must be rewritten first"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit
