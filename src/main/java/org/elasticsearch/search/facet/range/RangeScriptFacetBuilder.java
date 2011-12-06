begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|range
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
name|Lists
import|;
end_import

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
name|FilterBuilder
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RangeScriptFacetBuilder
specifier|public
class|class
name|RangeScriptFacetBuilder
extends|extends
name|AbstractFacetBuilder
block|{
DECL|field|lang
specifier|private
name|String
name|lang
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
DECL|field|entries
specifier|private
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|RangeScriptFacetBuilder
specifier|public
name|RangeScriptFacetBuilder
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
name|RangeScriptFacetBuilder
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
DECL|method|keyScript
specifier|public
name|RangeScriptFacetBuilder
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
name|RangeScriptFacetBuilder
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
DECL|method|param
specifier|public
name|RangeScriptFacetBuilder
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
comment|/**      * Adds a range entry with explicit from and to.      *      * @param from The from range limit      * @param to   The to range limit      */
DECL|method|addRange
specifier|public
name|RangeScriptFacetBuilder
name|addRange
parameter_list|(
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|)
block|{
name|entries
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a range entry with explicit from and unbounded to.      *      * @param from the from range limit, to is unbounded.      */
DECL|method|addUnboundedTo
specifier|public
name|RangeScriptFacetBuilder
name|addUnboundedTo
parameter_list|(
name|double
name|from
parameter_list|)
block|{
name|entries
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|from
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a range entry with explicit to and unbounded from.      *      * @param to the to range limit, from is unbounded.      */
DECL|method|addUnboundedFrom
specifier|public
name|RangeScriptFacetBuilder
name|addUnboundedFrom
parameter_list|(
name|double
name|to
parameter_list|)
block|{
name|entries
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the facet run in global mode (not bounded by the search query) or not (bounded by      * the search query). Defaults to<tt>false</tt>.      */
DECL|method|global
specifier|public
name|RangeScriptFacetBuilder
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
annotation|@
name|Override
DECL|method|scope
specifier|public
name|RangeScriptFacetBuilder
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
name|RangeScriptFacetBuilder
name|facetFilter
parameter_list|(
name|FilterBuilder
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
comment|/**      * Sets the nested path the facet will execute on. A match (root object) will then cause all the      * nested objects matching the path to be computed into the facet.      */
DECL|method|nested
specifier|public
name|RangeScriptFacetBuilder
name|nested
parameter_list|(
name|String
name|nested
parameter_list|)
block|{
name|this
operator|.
name|nested
operator|=
name|nested
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
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
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"key_script must be set on range script facet for facet ["
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
literal|"value_script must be set on range script facet for facet ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"at least one range must be defined for range facet ["
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
name|RangeFacet
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"key_script"
argument_list|,
name|keyScript
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|startArray
argument_list|(
literal|"ranges"
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|entries
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|entry
operator|.
name|from
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"from"
argument_list|,
name|entry
operator|.
name|from
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|entry
operator|.
name|to
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"to"
argument_list|,
name|entry
operator|.
name|to
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
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
return|return
name|builder
return|;
block|}
DECL|class|Entry
specifier|private
specifier|static
class|class
name|Entry
block|{
DECL|field|from
specifier|final
name|double
name|from
decl_stmt|;
DECL|field|to
specifier|final
name|double
name|to
decl_stmt|;
DECL|method|Entry
specifier|private
name|Entry
parameter_list|(
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

