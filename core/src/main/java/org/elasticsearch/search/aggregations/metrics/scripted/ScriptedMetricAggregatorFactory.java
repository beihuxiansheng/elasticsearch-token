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
name|script
operator|.
name|ExecutableScript
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
name|SearchScript
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
name|SearchParseException
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
name|Aggregator
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
name|AggregatorFactories
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
name|AggregatorFactory
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

begin_class
DECL|class|ScriptedMetricAggregatorFactory
specifier|public
class|class
name|ScriptedMetricAggregatorFactory
extends|extends
name|AggregatorFactory
argument_list|<
name|ScriptedMetricAggregatorFactory
argument_list|>
block|{
DECL|field|mapScript
specifier|private
specifier|final
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|SearchScript
argument_list|>
name|mapScript
decl_stmt|;
DECL|field|combineScript
specifier|private
specifier|final
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|ExecutableScript
argument_list|>
name|combineScript
decl_stmt|;
DECL|field|reduceScript
specifier|private
specifier|final
name|Script
name|reduceScript
decl_stmt|;
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
decl_stmt|;
DECL|field|initScript
specifier|private
specifier|final
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|ExecutableScript
argument_list|>
name|initScript
decl_stmt|;
DECL|method|ScriptedMetricAggregatorFactory
specifier|public
name|ScriptedMetricAggregatorFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|SearchScript
argument_list|>
name|mapScript
parameter_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|ExecutableScript
argument_list|>
name|initScript
parameter_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|ExecutableScript
argument_list|>
name|combineScript
parameter_list|,
name|Script
name|reduceScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|AggregatorFactories
operator|.
name|Builder
name|subFactories
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|subFactories
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapScript
operator|=
name|mapScript
expr_stmt|;
name|this
operator|.
name|initScript
operator|=
name|initScript
expr_stmt|;
name|this
operator|.
name|combineScript
operator|=
name|combineScript
expr_stmt|;
name|this
operator|.
name|reduceScript
operator|=
name|reduceScript
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInternal
specifier|public
name|Aggregator
name|createInternal
parameter_list|(
name|Aggregator
name|parent
parameter_list|,
name|boolean
name|collectsFromSingleBucket
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|collectsFromSingleBucket
operator|==
literal|false
condition|)
block|{
return|return
name|asMultiBucketAggregator
argument_list|(
name|this
argument_list|,
name|context
argument_list|,
name|parent
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
name|this
operator|.
name|params
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|params
operator|=
name|deepCopyParams
argument_list|(
name|params
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
literal|"_agg"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ExecutableScript
name|initScript
init|=
name|this
operator|.
name|initScript
operator|.
name|apply
argument_list|(
name|params
argument_list|)
decl_stmt|;
specifier|final
name|SearchScript
name|mapScript
init|=
name|this
operator|.
name|mapScript
operator|.
name|apply
argument_list|(
name|params
argument_list|)
decl_stmt|;
specifier|final
name|ExecutableScript
name|combineScript
init|=
name|this
operator|.
name|combineScript
operator|.
name|apply
argument_list|(
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Script
name|reduceScript
init|=
name|deepCopyScript
argument_list|(
name|this
operator|.
name|reduceScript
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|initScript
operator|!=
literal|null
condition|)
block|{
name|initScript
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ScriptedMetricAggregator
argument_list|(
name|name
argument_list|,
name|mapScript
argument_list|,
name|combineScript
argument_list|,
name|reduceScript
argument_list|,
name|params
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
DECL|method|deepCopyScript
specifier|private
specifier|static
name|Script
name|deepCopyScript
parameter_list|(
name|Script
name|script
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
name|script
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|params
operator|=
name|deepCopyParams
argument_list|(
name|params
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Script
argument_list|(
name|script
operator|.
name|getType
argument_list|()
argument_list|,
name|script
operator|.
name|getLang
argument_list|()
argument_list|,
name|script
operator|.
name|getIdOrCode
argument_list|()
argument_list|,
name|params
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|deepCopyParams
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|deepCopyParams
parameter_list|(
name|T
name|original
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|T
name|clone
decl_stmt|;
if|if
condition|(
name|original
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|originalMap
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|original
decl_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|clonedMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|e
range|:
name|originalMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|clonedMap
operator|.
name|put
argument_list|(
name|deepCopyParams
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|,
name|deepCopyParams
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|clone
operator|=
operator|(
name|T
operator|)
name|clonedMap
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|original
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|?
argument_list|>
name|originalList
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|original
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|clonedList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|originalList
control|)
block|{
name|clonedList
operator|.
name|add
argument_list|(
name|deepCopyParams
argument_list|(
name|o
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|clone
operator|=
operator|(
name|T
operator|)
name|clonedList
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|original
operator|instanceof
name|String
operator|||
name|original
operator|instanceof
name|Integer
operator|||
name|original
operator|instanceof
name|Long
operator|||
name|original
operator|instanceof
name|Short
operator|||
name|original
operator|instanceof
name|Byte
operator|||
name|original
operator|instanceof
name|Float
operator|||
name|original
operator|instanceof
name|Double
operator|||
name|original
operator|instanceof
name|Character
operator|||
name|original
operator|instanceof
name|Boolean
condition|)
block|{
name|clone
operator|=
name|original
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Can only clone primitives, String, ArrayList, and HashMap. Found: "
operator|+
name|original
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|null
argument_list|)
throw|;
block|}
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

