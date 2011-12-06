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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|Facet
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

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|RangeFacet
specifier|public
interface|interface
name|RangeFacet
extends|extends
name|Facet
extends|,
name|Iterable
argument_list|<
name|RangeFacet
operator|.
name|Entry
argument_list|>
block|{
comment|/**      * The type of the filter facet.      */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"range"
decl_stmt|;
comment|/**      * An ordered list of range facet entries.      */
DECL|method|entries
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|()
function_decl|;
comment|/**      * An ordered list of range facet entries.      */
DECL|method|getEntries
name|List
argument_list|<
name|Entry
argument_list|>
name|getEntries
parameter_list|()
function_decl|;
DECL|class|Entry
specifier|public
class|class
name|Entry
block|{
DECL|field|from
name|double
name|from
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|to
name|double
name|to
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|fromAsString
name|String
name|fromAsString
decl_stmt|;
DECL|field|toAsString
name|String
name|toAsString
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|totalCount
name|long
name|totalCount
decl_stmt|;
DECL|field|total
name|double
name|total
decl_stmt|;
DECL|field|min
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
comment|/**          * Internal field used in facet collection          */
DECL|field|foundInDoc
name|boolean
name|foundInDoc
decl_stmt|;
DECL|method|Entry
name|Entry
parameter_list|()
block|{         }
DECL|method|from
specifier|public
name|double
name|from
parameter_list|()
block|{
return|return
name|this
operator|.
name|from
return|;
block|}
DECL|method|getFrom
specifier|public
name|double
name|getFrom
parameter_list|()
block|{
return|return
name|from
argument_list|()
return|;
block|}
DECL|method|fromAsString
specifier|public
name|String
name|fromAsString
parameter_list|()
block|{
if|if
condition|(
name|fromAsString
operator|!=
literal|null
condition|)
block|{
return|return
name|fromAsString
return|;
block|}
return|return
name|Double
operator|.
name|toString
argument_list|(
name|from
argument_list|)
return|;
block|}
DECL|method|getFromAsString
specifier|public
name|String
name|getFromAsString
parameter_list|()
block|{
return|return
name|fromAsString
argument_list|()
return|;
block|}
DECL|method|to
specifier|public
name|double
name|to
parameter_list|()
block|{
return|return
name|this
operator|.
name|to
return|;
block|}
DECL|method|getTo
specifier|public
name|double
name|getTo
parameter_list|()
block|{
return|return
name|to
argument_list|()
return|;
block|}
DECL|method|toAsString
specifier|public
name|String
name|toAsString
parameter_list|()
block|{
if|if
condition|(
name|toAsString
operator|!=
literal|null
condition|)
block|{
return|return
name|toAsString
return|;
block|}
return|return
name|Double
operator|.
name|toString
argument_list|(
name|to
argument_list|)
return|;
block|}
DECL|method|getToAsString
specifier|public
name|String
name|getToAsString
parameter_list|()
block|{
return|return
name|toAsString
argument_list|()
return|;
block|}
DECL|method|count
specifier|public
name|long
name|count
parameter_list|()
block|{
return|return
name|this
operator|.
name|count
return|;
block|}
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
argument_list|()
return|;
block|}
DECL|method|totalCount
specifier|public
name|long
name|totalCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalCount
return|;
block|}
DECL|method|getTotalCount
specifier|public
name|long
name|getTotalCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalCount
return|;
block|}
DECL|method|total
specifier|public
name|double
name|total
parameter_list|()
block|{
return|return
name|this
operator|.
name|total
return|;
block|}
DECL|method|getTotal
specifier|public
name|double
name|getTotal
parameter_list|()
block|{
return|return
name|total
argument_list|()
return|;
block|}
comment|/**          * The mean of this facet interval.          */
DECL|method|mean
specifier|public
name|double
name|mean
parameter_list|()
block|{
if|if
condition|(
name|totalCount
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|total
operator|/
name|totalCount
return|;
block|}
comment|/**          * The mean of this facet interval.          */
DECL|method|getMean
specifier|public
name|double
name|getMean
parameter_list|()
block|{
return|return
name|mean
argument_list|()
return|;
block|}
DECL|method|min
specifier|public
name|double
name|min
parameter_list|()
block|{
return|return
name|this
operator|.
name|min
return|;
block|}
DECL|method|getMin
specifier|public
name|double
name|getMin
parameter_list|()
block|{
return|return
name|this
operator|.
name|min
return|;
block|}
DECL|method|max
specifier|public
name|double
name|max
parameter_list|()
block|{
return|return
name|this
operator|.
name|max
return|;
block|}
DECL|method|getMax
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|this
operator|.
name|max
return|;
block|}
block|}
block|}
end_interface

end_unit

