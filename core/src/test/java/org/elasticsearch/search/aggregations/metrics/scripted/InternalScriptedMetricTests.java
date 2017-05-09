begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.scripted
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|scripted
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Writeable
operator|.
name|Reader
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
name|script
operator|.
name|MockScriptEngine
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
name|ScriptSettings
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
name|aggregations
operator|.
name|InternalAggregationTestCase
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
name|PipelineAggregator
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
name|List
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

begin_class
DECL|class|InternalScriptedMetricTests
specifier|public
class|class
name|InternalScriptedMetricTests
extends|extends
name|InternalAggregationTestCase
argument_list|<
name|InternalScriptedMetric
argument_list|>
block|{
DECL|field|REDUCE_SCRIPT_NAME
specifier|private
specifier|static
specifier|final
name|String
name|REDUCE_SCRIPT_NAME
init|=
literal|"reduceScript"
decl_stmt|;
comment|// randomized only once so that any random test instance has the same value
DECL|field|hasReduceScript
specifier|private
name|boolean
name|hasReduceScript
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|InternalScriptedMetric
name|createTestInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
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
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Script
name|reduceScript
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasReduceScript
condition|)
block|{
name|reduceScript
operator|=
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|MockScriptEngine
operator|.
name|NAME
argument_list|,
name|REDUCE_SCRIPT_NAME
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalScriptedMetric
argument_list|(
name|name
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|reduceScript
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
comment|/**      * Mock of the script service. The script that is run looks at the      * "_aggs" parameter visible when executing the script and simply returns the count.      * This should be equal to the number of input InternalScriptedMetrics that are reduced      * in total.      */
annotation|@
name|Override
DECL|method|mockScriptService
specifier|protected
name|ScriptService
name|mockScriptService
parameter_list|()
block|{
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
comment|// mock script always retuns the size of the input aggs list as result
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|MockScriptEngine
name|scriptEngine
init|=
operator|new
name|MockScriptEngine
argument_list|(
name|MockScriptEngine
operator|.
name|NAME
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|REDUCE_SCRIPT_NAME
argument_list|,
name|script
lambda|->
block|{
return|return
operator|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|script
operator|.
name|get
argument_list|(
literal|"_aggs"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
return|;
block|}
argument_list|)
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
name|scriptEngine
argument_list|)
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
try|try
block|{
return|return
operator|new
name|ScriptService
argument_list|(
name|settings
argument_list|,
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|,
literal|null
argument_list|,
name|scriptEngineRegistry
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptSettings
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|assertReduced
specifier|protected
name|void
name|assertReduced
parameter_list|(
name|InternalScriptedMetric
name|reduced
parameter_list|,
name|List
argument_list|<
name|InternalScriptedMetric
argument_list|>
name|inputs
parameter_list|)
block|{
name|InternalScriptedMetric
name|firstAgg
init|=
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|firstAgg
operator|.
name|getName
argument_list|()
argument_list|,
name|reduced
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|firstAgg
operator|.
name|pipelineAggregators
argument_list|()
argument_list|,
name|reduced
operator|.
name|pipelineAggregators
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|firstAgg
operator|.
name|getMetaData
argument_list|()
argument_list|,
name|reduced
operator|.
name|getMetaData
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasReduceScript
condition|)
block|{
name|assertEquals
argument_list|(
name|inputs
operator|.
name|size
argument_list|()
argument_list|,
name|reduced
operator|.
name|aggregation
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|inputs
operator|.
name|size
argument_list|()
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|reduced
operator|.
name|aggregation
argument_list|()
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Reader
argument_list|<
name|InternalScriptedMetric
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalScriptedMetric
operator|::
operator|new
return|;
block|}
block|}
end_class

end_unit

