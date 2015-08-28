begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|analysis
operator|.
name|Analyzer
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
name|XContentBuilder
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
name|ESBackcompatTestCase
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
name|ESIntegTestCase
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|XContentFactory
operator|.
name|jsonBuilder
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
name|VersionUtils
operator|.
name|randomVersion
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
name|notNullValue
import|;
end_import

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|SUITE
argument_list|)
annotation|@
name|ESBackcompatTestCase
operator|.
name|CompatibilityVersion
argument_list|(
name|version
operator|=
name|Version
operator|.
name|V_1_2_0_ID
argument_list|)
comment|// we throw an exception if we create an index with _field_names that is 1.3
DECL|class|PreBuiltAnalyzerIntegrationIT
specifier|public
class|class
name|PreBuiltAnalyzerIntegrationIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"plugin.types"
argument_list|,
name|DummyAnalysisPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testThatPreBuiltAnalyzersAreNotClosedOnIndexClose
specifier|public
name|void
name|testThatPreBuiltAnalyzersAreNotClosedOnIndexClose
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|PreBuiltAnalyzers
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|loadedAnalyzers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|indexNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numIndices
init|=
name|scaledRandomIntBetween
argument_list|(
literal|2
argument_list|,
literal|4
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
name|numIndices
condition|;
name|i
operator|++
control|)
block|{
name|String
name|indexName
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|indexNames
operator|.
name|add
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
name|int
name|randomInt
init|=
name|randomInt
argument_list|(
name|PreBuiltAnalyzers
operator|.
name|values
argument_list|()
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|PreBuiltAnalyzers
name|preBuiltAnalyzer
init|=
name|PreBuiltAnalyzers
operator|.
name|values
argument_list|()
index|[
name|randomInt
index|]
decl_stmt|;
name|String
name|name
init|=
name|preBuiltAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Version
name|randomVersion
init|=
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|loadedAnalyzers
operator|.
name|containsKey
argument_list|(
name|preBuiltAnalyzer
argument_list|)
condition|)
block|{
name|loadedAnalyzers
operator|.
name|put
argument_list|(
name|preBuiltAnalyzer
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Version
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|loadedAnalyzers
operator|.
name|get
argument_list|(
name|preBuiltAnalyzer
argument_list|)
operator|.
name|add
argument_list|(
name|randomVersion
argument_list|)
expr_stmt|;
specifier|final
name|XContentBuilder
name|mapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|name
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|Settings
name|versionSettings
init|=
name|settings
argument_list|(
name|randomVersion
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|indexName
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
operator|.
name|setSettings
argument_list|(
name|versionSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|ensureGreen
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
comment|// index some amount of data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|randomIndex
init|=
name|indexNames
operator|.
name|get
argument_list|(
name|randomInt
argument_list|(
name|indexNames
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|randomId
init|=
name|randomInt
argument_list|()
operator|+
literal|""
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|randomAsciiOfLength
argument_list|(
name|scaledRandomIntBetween
argument_list|(
literal|5
argument_list|,
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|randomIndex
argument_list|,
literal|"type"
argument_list|,
name|randomId
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
comment|// close some of the indices
name|int
name|amountOfIndicesToClose
init|=
name|randomInt
argument_list|(
name|numIndices
operator|-
literal|1
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
name|amountOfIndicesToClose
condition|;
name|i
operator|++
control|)
block|{
name|String
name|indexName
init|=
name|indexNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
name|indexName
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|ensureGreen
argument_list|()
expr_stmt|;
comment|// check that all above configured analyzers have been loaded
name|assertThatAnalyzersHaveBeenLoaded
argument_list|(
name|loadedAnalyzers
argument_list|)
expr_stmt|;
comment|// check that all of the prebuiltanalyzers are still open
name|assertLuceneAnalyzersAreNotClosed
argument_list|(
name|loadedAnalyzers
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for #5030: Upgrading analysis plugins fails      * See https://github.com/elasticsearch/elasticsearch/issues/5030      */
annotation|@
name|Test
DECL|method|testThatPluginAnalyzersCanBeUpdated
specifier|public
name|void
name|testThatPluginAnalyzersCanBeUpdated
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|XContentBuilder
name|mapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
literal|"dummy"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
literal|"my_dummy"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|Settings
name|versionSettings
init|=
name|settings
argument_list|(
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_dummy.type"
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_dummy.filter"
argument_list|,
literal|"my_dummy_token_filter"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_dummy.char_filter"
argument_list|,
literal|"my_dummy_char_filter"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_dummy.tokenizer"
argument_list|,
literal|"my_dummy_tokenizer"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.tokenizer.my_dummy_tokenizer.type"
argument_list|,
literal|"dummy_tokenizer"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_dummy_token_filter.type"
argument_list|,
literal|"dummy_token_filter"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.char_filter.my_dummy_char_filter.type"
argument_list|,
literal|"dummy_char_filter"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test-analysis-dummy"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
operator|.
name|setSettings
argument_list|(
name|versionSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
block|}
DECL|method|assertThatAnalyzersHaveBeenLoaded
specifier|private
name|void
name|assertThatAnalyzersHaveBeenLoaded
parameter_list|(
name|Map
argument_list|<
name|PreBuiltAnalyzers
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|expectedLoadedAnalyzers
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PreBuiltAnalyzers
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|entry
range|:
name|expectedLoadedAnalyzers
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|Version
name|version
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
comment|// if it is not null in the cache, it has been loaded
name|assertThat
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getCache
argument_list|()
operator|.
name|get
argument_list|(
name|version
argument_list|)
argument_list|,
name|is
argument_list|(
name|notNullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// the close() method of a lucene analyzer sets the storedValue field to null
comment|// we simply check this via reflection - ugly but works
DECL|method|assertLuceneAnalyzersAreNotClosed
specifier|private
name|void
name|assertLuceneAnalyzersAreNotClosed
parameter_list|(
name|Map
argument_list|<
name|PreBuiltAnalyzers
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|loadedAnalyzers
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|NoSuchFieldException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PreBuiltAnalyzers
argument_list|,
name|List
argument_list|<
name|Version
argument_list|>
argument_list|>
name|preBuiltAnalyzerEntry
range|:
name|loadedAnalyzers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|PreBuiltAnalyzers
name|preBuiltAnalyzer
init|=
name|preBuiltAnalyzerEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|Version
name|version
range|:
name|preBuiltAnalyzerEntry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|Analyzer
name|analyzer
init|=
name|preBuiltAnalyzerEntry
operator|.
name|getKey
argument_list|()
operator|.
name|getCache
argument_list|()
operator|.
name|get
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
name|getFieldFromClass
argument_list|(
literal|"storedValue"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|boolean
name|currentAccessible
init|=
name|field
operator|.
name|isAccessible
argument_list|()
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|storedValue
init|=
name|field
operator|.
name|get
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
name|currentAccessible
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Analyzer %s in version %s seems to be closed"
argument_list|,
name|preBuiltAnalyzer
operator|.
name|name
argument_list|()
argument_list|,
name|version
argument_list|)
argument_list|,
name|storedValue
argument_list|,
name|is
argument_list|(
name|notNullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Searches for a field until it finds, loops through all superclasses      */
DECL|method|getFieldFromClass
specifier|private
name|Field
name|getFieldFromClass
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|Field
name|field
init|=
literal|null
decl_stmt|;
name|boolean
name|storedValueFieldFound
init|=
literal|false
decl_stmt|;
name|Class
name|clazz
init|=
name|obj
operator|.
name|getClass
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|storedValueFieldFound
condition|)
block|{
try|try
block|{
name|field
operator|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|storedValueFieldFound
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
name|e
parameter_list|)
block|{
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|Object
operator|.
name|class
operator|.
name|equals
argument_list|(
name|clazz
argument_list|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find storedValue field in class"
operator|+
name|clazz
argument_list|)
throw|;
block|}
return|return
name|field
return|;
block|}
block|}
end_class

end_unit

