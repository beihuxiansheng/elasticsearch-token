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
name|ElasticSearchIllegalArgumentException
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
name|Facet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
comment|/**  * Numeric histogram facet.  *  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|HistogramFacet
specifier|public
interface|interface
name|HistogramFacet
extends|extends
name|Facet
extends|,
name|Iterable
argument_list|<
name|HistogramFacet
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
literal|"histogram"
decl_stmt|;
comment|/**      * An ordered list of histogram facet entries.      */
DECL|method|entries
name|List
argument_list|<
name|?
extends|extends
name|Entry
argument_list|>
name|entries
parameter_list|()
function_decl|;
comment|/**      * An ordered list of histogram facet entries.      */
DECL|method|getEntries
name|List
argument_list|<
name|?
extends|extends
name|Entry
argument_list|>
name|getEntries
parameter_list|()
function_decl|;
DECL|enum|ComparatorType
specifier|public
specifier|static
enum|enum
name|ComparatorType
block|{
DECL|enum constant|KEY
name|KEY
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|"key"
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Entry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Entry
name|o1
parameter_list|,
name|Entry
name|o2
parameter_list|)
block|{
comment|// push nulls to the end
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
name|o1
operator|.
name|key
argument_list|()
operator|<
name|o2
operator|.
name|key
argument_list|()
condition|?
operator|-
literal|1
else|:
operator|(
name|o1
operator|.
name|key
argument_list|()
operator|==
name|o2
operator|.
name|key
argument_list|()
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
block|}
argument_list|)
block|,
DECL|enum constant|COUNT
name|COUNT
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|"count"
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Entry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Entry
name|o1
parameter_list|,
name|Entry
name|o2
parameter_list|)
block|{
comment|// push nulls to the end
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
name|o1
operator|.
name|count
argument_list|()
operator|<
name|o2
operator|.
name|count
argument_list|()
condition|?
operator|-
literal|1
else|:
operator|(
name|o1
operator|.
name|count
argument_list|()
operator|==
name|o2
operator|.
name|count
argument_list|()
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
block|}
argument_list|)
block|,
DECL|enum constant|TOTAL
name|TOTAL
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|,
literal|"total"
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Entry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Entry
name|o1
parameter_list|,
name|Entry
name|o2
parameter_list|)
block|{
comment|// push nulls to the end
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
name|o1
operator|.
name|total
argument_list|()
operator|<
name|o2
operator|.
name|total
argument_list|()
condition|?
operator|-
literal|1
else|:
operator|(
name|o1
operator|.
name|total
argument_list|()
operator|==
name|o2
operator|.
name|total
argument_list|()
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
block|}
argument_list|)
block|;
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Entry
argument_list|>
name|comparator
decl_stmt|;
DECL|method|ComparatorType
name|ComparatorType
parameter_list|(
name|byte
name|id
parameter_list|,
name|String
name|description
parameter_list|,
name|Comparator
argument_list|<
name|Entry
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|this
operator|.
name|description
return|;
block|}
DECL|method|comparator
specifier|public
name|Comparator
argument_list|<
name|Entry
argument_list|>
name|comparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
DECL|method|fromId
specifier|public
specifier|static
name|ComparatorType
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|KEY
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|COUNT
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
return|return
name|TOTAL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No type argument match for histogram comparator ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|ComparatorType
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"key"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|KEY
return|;
block|}
elseif|else
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|COUNT
return|;
block|}
elseif|else
if|if
condition|(
literal|"total"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|TOTAL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No type argument match for histogram comparator ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|interface|Entry
specifier|public
interface|interface
name|Entry
block|{
comment|/**          * The key value of the histogram.          */
DECL|method|key
name|long
name|key
parameter_list|()
function_decl|;
comment|/**          * The key value of the histogram.          */
DECL|method|getKey
name|long
name|getKey
parameter_list|()
function_decl|;
comment|/**          * The number of hits that fall within that key "range" or "interval".          */
DECL|method|count
name|long
name|count
parameter_list|()
function_decl|;
comment|/**          * The number of hits that fall within that key "range" or "interval".          */
DECL|method|getCount
name|long
name|getCount
parameter_list|()
function_decl|;
comment|/**          * The total count of values aggregated to compute the total.          */
DECL|method|totalCount
name|long
name|totalCount
parameter_list|()
function_decl|;
comment|/**          * The total count of values aggregated to compute the total.          */
DECL|method|getTotalCount
name|long
name|getTotalCount
parameter_list|()
function_decl|;
comment|/**          * The sum / total of the value field that fall within this key "interval".          */
DECL|method|total
name|double
name|total
parameter_list|()
function_decl|;
comment|/**          * The sum / total of the value field that fall within this key "interval".          */
DECL|method|getTotal
name|double
name|getTotal
parameter_list|()
function_decl|;
comment|/**          * The mean of this facet interval.          */
DECL|method|mean
name|double
name|mean
parameter_list|()
function_decl|;
comment|/**          * The mean of this facet interval.          */
DECL|method|getMean
name|double
name|getMean
parameter_list|()
function_decl|;
comment|/**          * The minimum value.          */
DECL|method|min
name|double
name|min
parameter_list|()
function_decl|;
comment|/**          * The minimum value.          */
DECL|method|getMin
name|double
name|getMin
parameter_list|()
function_decl|;
comment|/**          * The maximum value.          */
DECL|method|max
name|double
name|max
parameter_list|()
function_decl|;
comment|/**          * The maximum value.          */
DECL|method|getMax
name|double
name|getMax
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

