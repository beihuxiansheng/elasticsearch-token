begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|ScriptException
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
name|test
operator|.
name|ESTestCase
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
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|ScriptProcessorFactoryTests
specifier|public
class|class
name|ScriptProcessorFactoryTests
extends|extends
name|ESTestCase
block|{
DECL|field|factory
specifier|private
name|ScriptProcessor
operator|.
name|Factory
name|factory
decl_stmt|;
DECL|field|ingestScriptParamToType
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ingestScriptParamToType
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"stored"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"source"
argument_list|,
literal|"inline"
argument_list|)
expr_stmt|;
name|ingestScriptParamToType
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
block|{
name|factory
operator|=
operator|new
name|ScriptProcessor
operator|.
name|Factory
argument_list|(
name|mock
argument_list|(
name|ScriptService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFactoryValidationWithDefaultLang
specifier|public
name|void
name|testFactoryValidationWithDefaultLang
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|randomType
init|=
name|randomFrom
argument_list|(
literal|"id"
argument_list|,
literal|"source"
argument_list|)
decl_stmt|;
name|configMap
operator|.
name|put
argument_list|(
name|randomType
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|ScriptProcessor
name|processor
init|=
name|factory
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|configMap
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getScript
argument_list|()
operator|.
name|getLang
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Script
operator|.
name|DEFAULT_SCRIPT_LANG
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getScript
argument_list|()
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ingestScriptParamToType
operator|.
name|get
argument_list|(
name|randomType
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getScript
argument_list|()
operator|.
name|getParams
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFactoryValidationWithParams
specifier|public
name|void
name|testFactoryValidationWithParams
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|randomType
init|=
name|randomFrom
argument_list|(
literal|"id"
argument_list|,
literal|"source"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|randomParams
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|configMap
operator|.
name|put
argument_list|(
name|randomType
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"params"
argument_list|,
name|randomParams
argument_list|)
expr_stmt|;
name|ScriptProcessor
name|processor
init|=
name|factory
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|configMap
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getScript
argument_list|()
operator|.
name|getLang
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Script
operator|.
name|DEFAULT_SCRIPT_LANG
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getScript
argument_list|()
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ingestScriptParamToType
operator|.
name|get
argument_list|(
name|randomType
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getScript
argument_list|()
operator|.
name|getParams
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|randomParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFactoryValidationForMultipleScriptingTypes
specifier|public
name|void
name|testFactoryValidationForMultipleScriptingTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"source"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"lang"
argument_list|,
literal|"mockscript"
argument_list|)
expr_stmt|;
name|ElasticsearchException
name|exception
init|=
name|expectThrows
argument_list|(
name|ElasticsearchException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|factory
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|configMap
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"Only one of [id] or [source] may be configured"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFactoryValidationAtLeastOneScriptingType
specifier|public
name|void
name|testFactoryValidationAtLeastOneScriptingType
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"lang"
argument_list|,
literal|"mockscript"
argument_list|)
expr_stmt|;
name|ElasticsearchException
name|exception
init|=
name|expectThrows
argument_list|(
name|ElasticsearchException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|factory
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|configMap
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"Need [id] or [source] parameter to refer to scripts"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInlineBackcompat
specifier|public
name|void
name|testInlineBackcompat
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"inline"
argument_list|,
literal|"code"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|configMap
argument_list|)
expr_stmt|;
name|assertWarnings
argument_list|(
literal|"Specifying script source with [inline] is deprecated, use [source] instead."
argument_list|)
expr_stmt|;
block|}
DECL|method|testFactoryInvalidateWithInvalidCompiledScript
specifier|public
name|void
name|testFactoryInvalidateWithInvalidCompiledScript
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|randomType
init|=
name|randomFrom
argument_list|(
literal|"source"
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|ScriptService
name|mockedScriptService
init|=
name|mock
argument_list|(
name|ScriptService
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScriptException
name|thrownException
init|=
operator|new
name|ScriptException
argument_list|(
literal|"compile-time exception"
argument_list|,
operator|new
name|RuntimeException
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
literal|"script"
argument_list|,
literal|"mockscript"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockedScriptService
operator|.
name|compile
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|thrownException
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|ScriptProcessor
operator|.
name|Factory
argument_list|(
name|mockedScriptService
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|configMap
operator|.
name|put
argument_list|(
literal|"lang"
argument_list|,
literal|"mockscript"
argument_list|)
expr_stmt|;
name|configMap
operator|.
name|put
argument_list|(
name|randomType
argument_list|,
literal|"my_script"
argument_list|)
expr_stmt|;
name|ElasticsearchException
name|exception
init|=
name|expectThrows
argument_list|(
name|ElasticsearchException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|factory
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|configMap
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"compile-time exception"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

