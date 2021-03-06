begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.scriptfilter
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|scriptfilter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchResponse
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
name|index
operator|.
name|IndexModule
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
name|ScriptDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|MockScriptPlugin
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
name|ScriptType
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
name|sort
operator|.
name|SortOrder
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
name|Base64
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|emptyMap
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
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|scriptQuery
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
name|assertAcked
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
DECL|class|ScriptQuerySearchIT
specifier|public
class|class
name|ScriptQuerySearchIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|CustomScriptPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|class|CustomScriptPlugin
specifier|public
specifier|static
class|class
name|CustomScriptPlugin
extends|extends
name|MockScriptPlugin
block|{
annotation|@
name|Override
DECL|method|pluginScripts
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|pluginScripts
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|scripts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['num1'].value"
argument_list|,
name|vars
lambda|->
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
return|return
name|doc
operator|.
name|get
argument_list|(
literal|"num1"
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['num1'].value> 1"
argument_list|,
name|vars
lambda|->
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
name|ScriptDocValues
operator|.
name|Doubles
name|num1
init|=
operator|(
name|ScriptDocValues
operator|.
name|Doubles
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"num1"
argument_list|)
decl_stmt|;
return|return
name|num1
operator|.
name|getValue
argument_list|()
operator|>
literal|1
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['num1'].value> param1"
argument_list|,
name|vars
lambda|->
block|{
name|Integer
name|param1
init|=
operator|(
name|Integer
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"param1"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
name|ScriptDocValues
operator|.
name|Doubles
name|num1
init|=
operator|(
name|ScriptDocValues
operator|.
name|Doubles
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"num1"
argument_list|)
decl_stmt|;
return|return
name|num1
operator|.
name|getValue
argument_list|()
operator|>
name|param1
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['binaryData'].get(0).length"
argument_list|,
name|vars
lambda|->
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|ScriptDocValues
operator|.
name|BytesRefs
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"binaryData"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['binaryData'].get(0).length> 15"
argument_list|,
name|vars
lambda|->
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|ScriptDocValues
operator|.
name|BytesRefs
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"binaryData"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
operator|>
literal|15
return|;
block|}
argument_list|)
expr_stmt|;
return|return
name|scripts
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|indexSettings
argument_list|()
argument_list|)
comment|// aggressive filter caching so that we can assert on the number of iterations of the script filters
operator|.
name|put
argument_list|(
name|IndexModule
operator|.
name|INDEX_QUERY_CACHE_ENABLED_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|IndexModule
operator|.
name|INDEX_QUERY_CACHE_EVERYTHING_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|testCustomScriptBinaryField
specifier|public
name|void
name|testCustomScriptBinaryField
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|randomBytesDoc1
init|=
name|getRandomBytes
argument_list|(
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|randomBytesDoc2
init|=
name|getRandomBytes
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|assertAcked
argument_list|(
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
literal|"my-index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"my-type"
argument_list|,
name|createMappingSource
argument_list|(
literal|"binary"
argument_list|)
argument_list|)
operator|.
name|setSettings
argument_list|(
name|indexSettings
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"my-index"
argument_list|,
literal|"my-type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"binaryData"
argument_list|,
name|Base64
operator|.
name|getEncoder
argument_list|()
operator|.
name|encodeToString
argument_list|(
name|randomBytesDoc1
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"my-index"
argument_list|,
literal|"my-type"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"binaryData"
argument_list|,
name|Base64
operator|.
name|getEncoder
argument_list|()
operator|.
name|encodeToString
argument_list|(
name|randomBytesDoc2
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|scriptQuery
argument_list|(
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['binaryData'].get(0).length> 15"
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sbinaryData"
argument_list|,
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['binaryData'].get(0).length"
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sbinaryData"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomBytes
specifier|private
name|byte
index|[]
name|getRandomBytes
parameter_list|(
name|int
name|len
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|randomBytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|randomBytes
argument_list|)
expr_stmt|;
return|return
name|randomBytes
return|;
block|}
DECL|method|createMappingSource
specifier|private
name|XContentBuilder
name|createMappingSource
parameter_list|(
name|String
name|fieldType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"my-type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"binaryData"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|fieldType
argument_list|)
operator|.
name|field
argument_list|(
literal|"doc_values"
argument_list|,
literal|"true"
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
return|;
block|}
DECL|method|testCustomScriptBoost
specifier|public
name|void
name|testCustomScriptBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"running doc['num1'].value> 1"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|scriptQuery
argument_list|(
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['num1'].value> 1"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"num1"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sNum1"
argument_list|,
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['num1'].value"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3.0
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"param1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"running doc['num1'].value> param1"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|scriptQuery
argument_list|(
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['num1'].value> param1"
argument_list|,
name|params
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"num1"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sNum1"
argument_list|,
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['num1'].value"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3.0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"param1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"running doc['num1'].value> param1"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|scriptQuery
argument_list|(
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['num1'].value> param1"
argument_list|,
name|params
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"num1"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sNum1"
argument_list|,
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|CustomScriptPlugin
operator|.
name|NAME
argument_list|,
literal|"doc['num1'].value"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|scriptCounter
specifier|private
specifier|static
name|AtomicInteger
name|scriptCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|incrementScriptCounter
specifier|public
specifier|static
name|int
name|incrementScriptCounter
parameter_list|()
block|{
return|return
name|scriptCounter
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

