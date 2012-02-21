begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.datehistogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|datehistogram
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
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|Map
import|;
end_import

begin_comment
comment|/**  * A facet builder of date histogram facets.  */
end_comment

begin_class
DECL|class|DateHistogramFacetBuilder
specifier|public
class|class
name|DateHistogramFacetBuilder
extends|extends
name|AbstractFacetBuilder
block|{
DECL|field|keyFieldName
specifier|private
name|String
name|keyFieldName
decl_stmt|;
DECL|field|valueFieldName
specifier|private
name|String
name|valueFieldName
decl_stmt|;
DECL|field|interval
specifier|private
name|String
name|interval
init|=
literal|null
decl_stmt|;
DECL|field|preZone
specifier|private
name|String
name|preZone
init|=
literal|null
decl_stmt|;
DECL|field|postZone
specifier|private
name|String
name|postZone
init|=
literal|null
decl_stmt|;
DECL|field|preOffset
name|long
name|preOffset
init|=
literal|0
decl_stmt|;
DECL|field|postOffset
name|long
name|postOffset
init|=
literal|0
decl_stmt|;
DECL|field|factor
name|float
name|factor
init|=
literal|1.0f
decl_stmt|;
DECL|field|comparatorType
specifier|private
name|DateHistogramFacet
operator|.
name|ComparatorType
name|comparatorType
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
DECL|field|lang
specifier|private
name|String
name|lang
decl_stmt|;
comment|/**      * Constructs a new date histogram facet with the provided facet logical name.      *      * @param name The logical name of the facet      */
DECL|method|DateHistogramFacetBuilder
specifier|public
name|DateHistogramFacetBuilder
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
comment|/**      * The field name to perform the histogram facet. Translates to perform the histogram facet      * using the provided field as both the {@link #keyField(String)} and {@link #valueField(String)}.      */
DECL|method|field
specifier|public
name|DateHistogramFacetBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|keyFieldName
operator|=
name|field
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The field name to use in order to control where the hit will "fall into" within the histogram      * entries. Essentially, using the key field numeric value, the hit will be "rounded" into the relevant      * bucket controlled by the interval.      */
DECL|method|keyField
specifier|public
name|DateHistogramFacetBuilder
name|keyField
parameter_list|(
name|String
name|keyField
parameter_list|)
block|{
name|this
operator|.
name|keyFieldName
operator|=
name|keyField
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The field name to use as the value of the hit to compute data based on values within the interval      * (for example, total).      */
DECL|method|valueField
specifier|public
name|DateHistogramFacetBuilder
name|valueField
parameter_list|(
name|String
name|valueField
parameter_list|)
block|{
name|this
operator|.
name|valueFieldName
operator|=
name|valueField
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|valueScript
specifier|public
name|DateHistogramFacetBuilder
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
name|DateHistogramFacetBuilder
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
comment|/**      * The language of the value script.      */
DECL|method|lang
specifier|public
name|DateHistogramFacetBuilder
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
comment|/**      * The interval used to control the bucket "size" where each key value of a hit will fall into. Check      * the docs for all available values.      */
DECL|method|interval
specifier|public
name|DateHistogramFacetBuilder
name|interval
parameter_list|(
name|String
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
comment|/**      * Sets the pre time zone to use when bucketing the values. This timezone will be applied before      * rounding off the result.      *<p/>      * Can either be in the form of "-10:00" or      * one of the values listed here: http://joda-time.sourceforge.net/timezones.html.      */
DECL|method|preZone
specifier|public
name|DateHistogramFacetBuilder
name|preZone
parameter_list|(
name|String
name|preZone
parameter_list|)
block|{
name|this
operator|.
name|preZone
operator|=
name|preZone
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the post time zone to use when bucketing the values. This timezone will be applied after      * rounding off the result.      *<p/>      * Can either be in the form of "-10:00" or      * one of the values listed here: http://joda-time.sourceforge.net/timezones.html.      */
DECL|method|postZone
specifier|public
name|DateHistogramFacetBuilder
name|postZone
parameter_list|(
name|String
name|postZone
parameter_list|)
block|{
name|this
operator|.
name|postZone
operator|=
name|postZone
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets a pre offset that will be applied before rounding the results.      */
DECL|method|preOffset
specifier|public
name|DateHistogramFacetBuilder
name|preOffset
parameter_list|(
name|TimeValue
name|preOffset
parameter_list|)
block|{
name|this
operator|.
name|preOffset
operator|=
name|preOffset
operator|.
name|millis
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets a post offset that will be applied after rounding the results.      */
DECL|method|postOffset
specifier|public
name|DateHistogramFacetBuilder
name|postOffset
parameter_list|(
name|TimeValue
name|postOffset
parameter_list|)
block|{
name|this
operator|.
name|postOffset
operator|=
name|postOffset
operator|.
name|millis
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the factor that will be used to multiply the value with before and divided      * by after the rounding of the results.      */
DECL|method|factor
specifier|public
name|DateHistogramFacetBuilder
name|factor
parameter_list|(
name|float
name|factor
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|comparator
specifier|public
name|DateHistogramFacetBuilder
name|comparator
parameter_list|(
name|DateHistogramFacet
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
comment|/**      * Should the facet run in global mode (not bounded by the search query) or not (bounded by      * the search query). Defaults to<tt>false</tt>.      */
annotation|@
name|Override
DECL|method|global
specifier|public
name|DateHistogramFacetBuilder
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
name|DateHistogramFacetBuilder
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
comment|/**      * An additional filter used to further filter down the set of documents the facet will run on.      */
annotation|@
name|Override
DECL|method|facetFilter
specifier|public
name|DateHistogramFacetBuilder
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
name|DateHistogramFacetBuilder
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
name|keyFieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"field must be set on date histogram facet for facet ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|interval
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"interval must be set on date histogram facet for facet ["
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
name|DateHistogramFacet
operator|.
name|TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|valueFieldName
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
name|builder
operator|.
name|field
argument_list|(
literal|"value_field"
argument_list|,
name|valueFieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|keyFieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|valueScript
operator|!=
literal|null
condition|)
block|{
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
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"interval"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
if|if
condition|(
name|preZone
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"pre_zone"
argument_list|,
name|preZone
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|postZone
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"post_zone"
argument_list|,
name|postZone
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|preOffset
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"pre_offset"
argument_list|,
name|preOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|postOffset
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"post_offset"
argument_list|,
name|postOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|factor
operator|!=
literal|1.0f
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"factor"
argument_list|,
name|factor
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

