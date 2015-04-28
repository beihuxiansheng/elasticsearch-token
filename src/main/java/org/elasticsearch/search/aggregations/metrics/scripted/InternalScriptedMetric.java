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
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|ScriptService
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
name|AggregationStreams
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
name|InternalAggregation
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
name|metrics
operator|.
name|InternalMetricsAggregation
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

begin_class
DECL|class|InternalScriptedMetric
specifier|public
class|class
name|InternalScriptedMetric
extends|extends
name|InternalMetricsAggregation
implements|implements
name|ScriptedMetric
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"scripted_metric"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|final
specifier|static
name|AggregationStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|AggregationStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalScriptedMetric
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalScriptedMetric
name|result
init|=
operator|new
name|InternalScriptedMetric
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
parameter_list|()
block|{
name|AggregationStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|scriptLang
specifier|private
name|String
name|scriptLang
decl_stmt|;
DECL|field|scriptType
specifier|private
name|ScriptType
name|scriptType
decl_stmt|;
DECL|field|reduceScript
specifier|private
name|String
name|reduceScript
decl_stmt|;
DECL|field|reduceParams
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|reduceParams
decl_stmt|;
DECL|field|aggregation
specifier|private
name|Object
name|aggregation
decl_stmt|;
DECL|method|InternalScriptedMetric
specifier|private
name|InternalScriptedMetric
parameter_list|()
block|{     }
DECL|method|InternalScriptedMetric
specifier|private
name|InternalScriptedMetric
parameter_list|(
name|String
name|name
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
name|super
argument_list|(
name|name
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
DECL|method|InternalScriptedMetric
specifier|public
name|InternalScriptedMetric
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|aggregation
parameter_list|,
name|String
name|scriptLang
parameter_list|,
name|ScriptType
name|scriptType
parameter_list|,
name|String
name|reduceScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|reduceParams
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
name|this
argument_list|(
name|name
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|aggregation
operator|=
name|aggregation
expr_stmt|;
name|this
operator|.
name|scriptType
operator|=
name|scriptType
expr_stmt|;
name|this
operator|.
name|reduceScript
operator|=
name|reduceScript
expr_stmt|;
name|this
operator|.
name|reduceParams
operator|=
name|reduceParams
expr_stmt|;
name|this
operator|.
name|scriptLang
operator|=
name|scriptLang
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregation
specifier|public
name|Object
name|aggregation
parameter_list|()
block|{
return|return
name|aggregation
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalAggregation
name|reduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|aggregationObjects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalScriptedMetric
name|mapReduceAggregation
init|=
operator|(
name|InternalScriptedMetric
operator|)
name|aggregation
decl_stmt|;
name|aggregationObjects
operator|.
name|add
argument_list|(
name|mapReduceAggregation
operator|.
name|aggregation
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|InternalScriptedMetric
name|firstAggregation
init|=
operator|(
operator|(
name|InternalScriptedMetric
operator|)
name|aggregations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
decl_stmt|;
name|Object
name|aggregation
decl_stmt|;
if|if
condition|(
name|firstAggregation
operator|.
name|reduceScript
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
decl_stmt|;
if|if
condition|(
name|firstAggregation
operator|.
name|reduceParams
operator|!=
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|firstAggregation
operator|.
name|reduceParams
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
block|}
name|params
operator|.
name|put
argument_list|(
literal|"_aggs"
argument_list|,
name|aggregationObjects
argument_list|)
expr_stmt|;
name|ExecutableScript
name|script
init|=
name|reduceContext
operator|.
name|scriptService
argument_list|()
operator|.
name|executable
argument_list|(
operator|new
name|Script
argument_list|(
name|firstAggregation
operator|.
name|scriptLang
argument_list|,
name|firstAggregation
operator|.
name|reduceScript
argument_list|,
name|firstAggregation
operator|.
name|scriptType
argument_list|,
name|params
argument_list|)
argument_list|,
name|ScriptContext
operator|.
name|Standard
operator|.
name|AGGS
argument_list|)
decl_stmt|;
name|aggregation
operator|=
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aggregation
operator|=
name|aggregationObjects
expr_stmt|;
block|}
return|return
operator|new
name|InternalScriptedMetric
argument_list|(
name|firstAggregation
operator|.
name|getName
argument_list|()
argument_list|,
name|aggregation
argument_list|,
name|firstAggregation
operator|.
name|scriptLang
argument_list|,
name|firstAggregation
operator|.
name|scriptType
argument_list|,
name|firstAggregation
operator|.
name|reduceScript
argument_list|,
name|firstAggregation
operator|.
name|reduceParams
argument_list|,
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
literal|"value"
operator|.
name|equals
argument_list|(
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|aggregation
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path not supported for ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|scriptLang
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|scriptType
operator|=
name|ScriptType
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|reduceScript
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|reduceParams
operator|=
name|in
operator|.
name|readMap
argument_list|()
expr_stmt|;
name|aggregation
operator|=
name|in
operator|.
name|readGenericValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeOptionalString
argument_list|(
name|scriptLang
argument_list|)
expr_stmt|;
name|ScriptType
operator|.
name|writeTo
argument_list|(
name|scriptType
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|reduceScript
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeMap
argument_list|(
name|reduceParams
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|aggregation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
name|aggregation
argument_list|)
return|;
block|}
block|}
end_class

end_unit

