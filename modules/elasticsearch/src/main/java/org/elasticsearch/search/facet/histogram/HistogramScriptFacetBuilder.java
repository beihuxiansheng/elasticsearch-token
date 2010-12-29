begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.histogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|histogram
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
name|collect
operator|.
name|Maps
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
name|index
operator|.
name|query
operator|.
name|xcontent
operator|.
name|XContentFilterBuilder
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
name|builder
operator|.
name|SearchSourceBuilderException
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
name|facet
operator|.
name|AbstractFacetBuilder
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
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HistogramScriptFacetBuilder
specifier|public
class|class
name|HistogramScriptFacetBuilder
extends|extends
name|AbstractFacetBuilder
block|{
DECL|field|lang
specifier|private
name|String
name|lang
decl_stmt|;
DECL|field|keyFieldName
specifier|private
name|String
name|keyFieldName
decl_stmt|;
DECL|field|keyScript
specifier|private
name|String
name|keyScript
decl_stmt|;
DECL|field|valueScript
specifier|private
name|String
name|valueScript
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
DECL|field|interval
specifier|private
name|long
name|interval
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|comparatorType
specifier|private
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|method|HistogramScriptFacetBuilder
specifier|public
name|HistogramScriptFacetBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * The language of the script.      */
DECL|method|lang
specifier|public
name|HistogramScriptFacetBuilder
name|lang
parameter_list|(
name|String
name|lang
parameter_list|)
block|{
name|this
operator|.
name|lang
operator|=
name|lang
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|keyField
specifier|public
name|HistogramScriptFacetBuilder
name|keyField
parameter_list|(
name|String
name|keyFieldName
parameter_list|)
block|{
name|this
operator|.
name|keyFieldName
operator|=
name|keyFieldName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|keyScript
specifier|public
name|HistogramScriptFacetBuilder
name|keyScript
parameter_list|(
name|String
name|keyScript
parameter_list|)
block|{
name|this
operator|.
name|keyScript
operator|=
name|keyScript
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|valueScript
specifier|public
name|HistogramScriptFacetBuilder
name|valueScript
parameter_list|(
name|String
name|valueScript
parameter_list|)
block|{
name|this
operator|.
name|valueScript
operator|=
name|valueScript
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|interval
specifier|public
name|HistogramScriptFacetBuilder
name|interval
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|param
specifier|public
name|HistogramScriptFacetBuilder
name|param
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|comparator
specifier|public
name|HistogramScriptFacetBuilder
name|comparator
parameter_list|(
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|)
block|{
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Marks the facet to run in a global scope, not bounded by any query.      */
DECL|method|global
annotation|@
name|Override
specifier|public
name|HistogramScriptFacetBuilder
name|global
parameter_list|(
name|boolean
name|global
parameter_list|)
block|{
name|super
operator|.
name|global
argument_list|(
name|global
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Marks the facet to run in a specific scope.      */
DECL|method|scope
annotation|@
name|Override
specifier|public
name|HistogramScriptFacetBuilder
name|scope
parameter_list|(
name|String
name|scope
parameter_list|)
block|{
name|super
operator|.
name|scope
argument_list|(
name|scope
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|facetFilter
specifier|public
name|HistogramScriptFacetBuilder
name|facetFilter
parameter_list|(
name|XContentFilterBuilder
name|filter
parameter_list|)
block|{
name|this
operator|.
name|facetFilter
operator|=
name|filter
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
name|toXContent
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
if|if
condition|(
name|keyScript
operator|==
literal|null
operator|&&
name|keyFieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"key_script or key_field must be set on histogram script facet for facet ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|valueScript
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"value_script must be set on histogram script facet for facet ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|HistogramFacet
operator|.
name|TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|keyFieldName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"key_field"
argument_list|,
name|keyFieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|keyScript
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"key_script"
argument_list|,
name|keyScript
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"value_script"
argument_list|,
name|valueScript
argument_list|)
expr_stmt|;
if|if
condition|(
name|lang
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"lang"
argument_list|,
name|lang
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|interval
operator|>
literal|0
condition|)
block|{
comment|// interval is optional in script facet, can be defined by the key script
name|builder
operator|.
name|field
argument_list|(
literal|"interval"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|params
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"params"
argument_list|,
name|this
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comparatorType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"comparator"
argument_list|,
name|comparatorType
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|addFilterFacetAndGlobal
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

