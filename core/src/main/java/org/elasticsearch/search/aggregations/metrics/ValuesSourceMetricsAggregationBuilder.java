begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics
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
name|Script
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
comment|/**  *  */
end_comment

begin_class
DECL|class|ValuesSourceMetricsAggregationBuilder
specifier|public
specifier|abstract
class|class
name|ValuesSourceMetricsAggregationBuilder
parameter_list|<
name|B
extends|extends
name|ValuesSourceMetricsAggregationBuilder
parameter_list|<
name|B
parameter_list|>
parameter_list|>
extends|extends
name|MetricsAggregationBuilder
argument_list|<
name|B
argument_list|>
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|script
specifier|private
name|Script
name|script
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|scriptString
specifier|private
name|String
name|scriptString
decl_stmt|;
comment|// TODO Remove in 3.0
annotation|@
name|Deprecated
DECL|field|lang
specifier|private
name|String
name|lang
decl_stmt|;
comment|// TODO Remove in 3.0
annotation|@
name|Deprecated
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
comment|// TODO Remove in 3.0
DECL|field|format
specifier|private
name|String
name|format
decl_stmt|;
DECL|field|missing
specifier|private
name|Object
name|missing
decl_stmt|;
DECL|method|ValuesSourceMetricsAggregationBuilder
specifier|protected
name|ValuesSourceMetricsAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|field
specifier|public
name|B
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
return|return
operator|(
name|B
operator|)
name|this
return|;
block|}
comment|/**      * The script to use for this aggregation      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|script
specifier|public
name|B
name|script
parameter_list|(
name|Script
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
return|return
operator|(
name|B
operator|)
name|this
return|;
block|}
comment|/**      * @deprecated use {@link #script(Script)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|script
specifier|public
name|B
name|script
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|this
operator|.
name|scriptString
operator|=
name|script
expr_stmt|;
return|return
operator|(
name|B
operator|)
name|this
return|;
block|}
comment|/**      * @deprecated use {@link #script(Script)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|lang
specifier|public
name|B
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
operator|(
name|B
operator|)
name|this
return|;
block|}
comment|/**      * @deprecated use {@link #script(Script)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|params
specifier|public
name|B
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
name|this
operator|.
name|params
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|params
operator|.
name|putAll
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|B
operator|)
name|this
return|;
block|}
comment|/**      * @deprecated use {@link #script(Script)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|param
specifier|public
name|B
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
name|this
operator|.
name|params
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|params
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
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
operator|(
name|B
operator|)
name|this
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|format
specifier|public
name|B
name|format
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
return|return
operator|(
name|B
operator|)
name|this
return|;
block|}
comment|/**      * Configure the value to use when documents miss a value.      */
DECL|method|missing
specifier|public
name|B
name|missing
parameter_list|(
name|Object
name|missingValue
parameter_list|)
block|{
name|this
operator|.
name|missing
operator|=
name|missingValue
expr_stmt|;
return|return
operator|(
name|B
operator|)
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|internalXContent
specifier|protected
name|void
name|internalXContent
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
name|field
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"script"
argument_list|,
name|script
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scriptString
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"script"
argument_list|,
name|scriptString
argument_list|)
expr_stmt|;
block|}
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
name|format
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
name|format
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
operator|&&
operator|!
name|this
operator|.
name|params
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"params"
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"missing"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

