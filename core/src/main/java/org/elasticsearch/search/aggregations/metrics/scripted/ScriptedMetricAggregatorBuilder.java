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
name|common
operator|.
name|ParseField
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
name|ScriptParameterParser
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
name|ScriptParameterParser
operator|.
name|ScriptParameterValue
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
name|AggregatorBuilder
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
operator|.
name|Builder
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
name|support
operator|.
name|AggregationContext
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
name|HashSet
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|ScriptedMetricAggregatorBuilder
specifier|public
class|class
name|ScriptedMetricAggregatorBuilder
extends|extends
name|AggregatorBuilder
argument_list|<
name|ScriptedMetricAggregatorBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|InternalScriptedMetric
operator|.
name|TYPE
operator|.
name|name
argument_list|()
decl_stmt|;
DECL|field|AGGREGATION_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|AGGREGATION_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|INIT_SCRIPT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|INIT_SCRIPT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"init_script"
argument_list|)
decl_stmt|;
DECL|field|MAP_SCRIPT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|MAP_SCRIPT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"map_script"
argument_list|)
decl_stmt|;
DECL|field|COMBINE_SCRIPT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|COMBINE_SCRIPT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"combine_script"
argument_list|)
decl_stmt|;
DECL|field|REDUCE_SCRIPT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|REDUCE_SCRIPT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"reduce_script"
argument_list|)
decl_stmt|;
DECL|field|PARAMS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|PARAMS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
DECL|field|REDUCE_PARAMS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|REDUCE_PARAMS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"reduce_params"
argument_list|)
decl_stmt|;
DECL|field|initScript
specifier|private
name|Script
name|initScript
decl_stmt|;
DECL|field|mapScript
specifier|private
name|Script
name|mapScript
decl_stmt|;
DECL|field|combineScript
specifier|private
name|Script
name|combineScript
decl_stmt|;
DECL|field|reduceScript
specifier|private
name|Script
name|reduceScript
decl_stmt|;
DECL|field|params
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
decl_stmt|;
DECL|method|ScriptedMetricAggregatorBuilder
specifier|public
name|ScriptedMetricAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalScriptedMetric
operator|.
name|TYPE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ScriptedMetricAggregatorBuilder
specifier|public
name|ScriptedMetricAggregatorBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|InternalScriptedMetric
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|initScript
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
name|Script
operator|.
name|SUPPLIER
argument_list|)
expr_stmt|;
name|mapScript
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
name|Script
operator|.
name|SUPPLIER
argument_list|)
expr_stmt|;
name|combineScript
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
name|Script
operator|.
name|SUPPLIER
argument_list|)
expr_stmt|;
name|reduceScript
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
name|Script
operator|.
name|SUPPLIER
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|params
operator|=
name|in
operator|.
name|readMap
argument_list|()
expr_stmt|;
block|}
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
name|writeOptionalStreamable
argument_list|(
name|initScript
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|mapScript
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|combineScript
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|reduceScript
argument_list|)
expr_stmt|;
name|boolean
name|hasParams
init|=
name|params
operator|!=
literal|null
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasParams
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasParams
condition|)
block|{
name|out
operator|.
name|writeMap
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|usesNewStyleSerialization
specifier|protected
name|boolean
name|usesNewStyleSerialization
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Set the<tt>init</tt> script.      */
DECL|method|initScript
specifier|public
name|ScriptedMetricAggregatorBuilder
name|initScript
parameter_list|(
name|Script
name|initScript
parameter_list|)
block|{
if|if
condition|(
name|initScript
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[initScript] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|initScript
operator|=
name|initScript
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the<tt>init</tt> script.      */
DECL|method|initScript
specifier|public
name|Script
name|initScript
parameter_list|()
block|{
return|return
name|initScript
return|;
block|}
comment|/**      * Set the<tt>map</tt> script.      */
DECL|method|mapScript
specifier|public
name|ScriptedMetricAggregatorBuilder
name|mapScript
parameter_list|(
name|Script
name|mapScript
parameter_list|)
block|{
if|if
condition|(
name|mapScript
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[mapScript] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|mapScript
operator|=
name|mapScript
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the<tt>map</tt> script.      */
DECL|method|mapScript
specifier|public
name|Script
name|mapScript
parameter_list|()
block|{
return|return
name|mapScript
return|;
block|}
comment|/**      * Set the<tt>combine</tt> script.      */
DECL|method|combineScript
specifier|public
name|ScriptedMetricAggregatorBuilder
name|combineScript
parameter_list|(
name|Script
name|combineScript
parameter_list|)
block|{
if|if
condition|(
name|combineScript
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[combineScript] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|combineScript
operator|=
name|combineScript
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the<tt>combine</tt> script.      */
DECL|method|combineScript
specifier|public
name|Script
name|combineScript
parameter_list|()
block|{
return|return
name|combineScript
return|;
block|}
comment|/**      * Set the<tt>reduce</tt> script.      */
DECL|method|reduceScript
specifier|public
name|ScriptedMetricAggregatorBuilder
name|reduceScript
parameter_list|(
name|Script
name|reduceScript
parameter_list|)
block|{
if|if
condition|(
name|reduceScript
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[reduceScript] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|reduceScript
operator|=
name|reduceScript
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the<tt>reduce</tt> script.      */
DECL|method|reduceScript
specifier|public
name|Script
name|reduceScript
parameter_list|()
block|{
return|return
name|reduceScript
return|;
block|}
comment|/**      * Set parameters that will be available in the<tt>init</tt>,      *<tt>map</tt> and<tt>combine</tt> phases.      */
DECL|method|params
specifier|public
name|ScriptedMetricAggregatorBuilder
name|params
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[params] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get parameters that will be available in the<tt>init</tt>,      *<tt>map</tt> and<tt>combine</tt> phases.      */
DECL|method|params
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|doBuild
specifier|protected
name|ScriptedMetricAggregatorFactory
name|doBuild
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|Builder
name|subfactoriesBuilder
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ScriptedMetricAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|initScript
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
name|subfactoriesBuilder
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|internalXContent
specifier|protected
name|XContentBuilder
name|internalXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|builderParams
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|initScript
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|INIT_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|initScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mapScript
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MAP_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|mapScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|combineScript
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|COMBINE_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|combineScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reduceScript
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|REDUCE_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|reduceScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|PARAMS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|ScriptedMetricAggregatorBuilder
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Script
name|initScript
init|=
literal|null
decl_stmt|;
name|Script
name|mapScript
init|=
literal|null
decl_stmt|;
name|Script
name|combineScript
init|=
literal|null
decl_stmt|;
name|Script
name|reduceScript
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|reduceParams
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|scriptParameters
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|scriptParameters
operator|.
name|add
argument_list|(
name|INIT_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|scriptParameters
operator|.
name|add
argument_list|(
name|MAP_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|scriptParameters
operator|.
name|add
argument_list|(
name|COMBINE_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|scriptParameters
operator|.
name|add
argument_list|(
name|REDUCE_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|ScriptParameterParser
name|scriptParameterParser
init|=
operator|new
name|ScriptParameterParser
argument_list|(
name|scriptParameters
argument_list|)
decl_stmt|;
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INIT_SCRIPT_FIELD
argument_list|)
condition|)
block|{
name|initScript
operator|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MAP_SCRIPT_FIELD
argument_list|)
condition|)
block|{
name|mapScript
operator|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|COMBINE_SCRIPT_FIELD
argument_list|)
condition|)
block|{
name|combineScript
operator|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|REDUCE_SCRIPT_FIELD
argument_list|)
condition|)
block|{
name|reduceScript
operator|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|PARAMS_FIELD
argument_list|)
condition|)
block|{
name|params
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|REDUCE_PARAMS_FIELD
argument_list|)
condition|)
block|{
name|reduceParams
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|scriptParameterParser
operator|.
name|token
argument_list|(
name|currentFieldName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|initScript
operator|==
literal|null
condition|)
block|{
comment|// Didn't find anything using the new API so try using the old one instead
name|ScriptParameterValue
name|scriptValue
init|=
name|scriptParameterParser
operator|.
name|getScriptParameterValue
argument_list|(
name|INIT_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|!=
literal|null
condition|)
block|{
name|initScript
operator|=
operator|new
name|Script
argument_list|(
name|scriptValue
operator|.
name|script
argument_list|()
argument_list|,
name|scriptValue
operator|.
name|scriptType
argument_list|()
argument_list|,
name|scriptParameterParser
operator|.
name|lang
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|initScript
operator|.
name|getParams
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"init_script params are not supported. Parameters for the "
operator|+
literal|"init_script must be specified in the params field on the scripted_metric aggregator not inside the init_script "
operator|+
literal|"object"
argument_list|)
throw|;
block|}
if|if
condition|(
name|mapScript
operator|==
literal|null
condition|)
block|{
comment|// Didn't find anything using the new API so try using the old one instead
name|ScriptParameterValue
name|scriptValue
init|=
name|scriptParameterParser
operator|.
name|getScriptParameterValue
argument_list|(
name|MAP_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|!=
literal|null
condition|)
block|{
name|mapScript
operator|=
operator|new
name|Script
argument_list|(
name|scriptValue
operator|.
name|script
argument_list|()
argument_list|,
name|scriptValue
operator|.
name|scriptType
argument_list|()
argument_list|,
name|scriptParameterParser
operator|.
name|lang
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|mapScript
operator|.
name|getParams
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"map_script params are not supported. Parameters for the map_script "
operator|+
literal|"must be specified in the params field on the scripted_metric aggregator not inside the map_script object"
argument_list|)
throw|;
block|}
if|if
condition|(
name|combineScript
operator|==
literal|null
condition|)
block|{
comment|// Didn't find anything using the new API so try using the old one instead
name|ScriptParameterValue
name|scriptValue
init|=
name|scriptParameterParser
operator|.
name|getScriptParameterValue
argument_list|(
name|COMBINE_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|!=
literal|null
condition|)
block|{
name|combineScript
operator|=
operator|new
name|Script
argument_list|(
name|scriptValue
operator|.
name|script
argument_list|()
argument_list|,
name|scriptValue
operator|.
name|scriptType
argument_list|()
argument_list|,
name|scriptParameterParser
operator|.
name|lang
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|combineScript
operator|.
name|getParams
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"combine_script params are not supported. Parameters for the "
operator|+
literal|"combine_script must be specified in the params field on the scripted_metric aggregator not inside the "
operator|+
literal|"combine_script object"
argument_list|)
throw|;
block|}
if|if
condition|(
name|reduceScript
operator|==
literal|null
condition|)
block|{
comment|// Didn't find anything using the new API so try using the old one instead
name|ScriptParameterValue
name|scriptValue
init|=
name|scriptParameterParser
operator|.
name|getScriptParameterValue
argument_list|(
name|REDUCE_SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|!=
literal|null
condition|)
block|{
name|reduceScript
operator|=
operator|new
name|Script
argument_list|(
name|scriptValue
operator|.
name|script
argument_list|()
argument_list|,
name|scriptValue
operator|.
name|scriptType
argument_list|()
argument_list|,
name|scriptParameterParser
operator|.
name|lang
argument_list|()
argument_list|,
name|reduceParams
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mapScript
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"map_script field is required in ["
operator|+
name|aggregationName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|ScriptedMetricAggregatorBuilder
name|factory
init|=
operator|new
name|ScriptedMetricAggregatorBuilder
argument_list|(
name|aggregationName
argument_list|)
decl_stmt|;
if|if
condition|(
name|initScript
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|initScript
argument_list|(
name|initScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mapScript
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|mapScript
argument_list|(
name|mapScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|combineScript
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|combineScript
argument_list|(
name|combineScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reduceScript
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|reduceScript
argument_list|(
name|reduceScript
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|params
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|initScript
argument_list|,
name|mapScript
argument_list|,
name|combineScript
argument_list|,
name|reduceScript
argument_list|,
name|params
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|ScriptedMetricAggregatorBuilder
name|other
init|=
operator|(
name|ScriptedMetricAggregatorBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|initScript
argument_list|,
name|other
operator|.
name|initScript
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|mapScript
argument_list|,
name|other
operator|.
name|mapScript
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|combineScript
argument_list|,
name|other
operator|.
name|combineScript
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|reduceScript
argument_list|,
name|other
operator|.
name|reduceScript
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|params
argument_list|,
name|other
operator|.
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit

