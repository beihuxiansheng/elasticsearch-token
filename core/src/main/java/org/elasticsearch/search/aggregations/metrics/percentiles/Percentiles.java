begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.percentiles
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
name|percentiles
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
name|aggregations
operator|.
name|metrics
operator|.
name|NumericMetricsAggregation
import|;
end_import

begin_comment
comment|/**  * An aggregation that computes approximate percentiles.  */
end_comment

begin_interface
DECL|interface|Percentiles
specifier|public
interface|interface
name|Percentiles
extends|extends
name|NumericMetricsAggregation
operator|.
name|MultiValue
extends|,
name|Iterable
argument_list|<
name|Percentile
argument_list|>
block|{
DECL|field|TYPE_NAME
name|String
name|TYPE_NAME
init|=
literal|"percentiles"
decl_stmt|;
comment|/**      * Return the value associated with the provided percentile.      */
DECL|method|percentile
name|double
name|percentile
parameter_list|(
name|double
name|percent
parameter_list|)
function_decl|;
comment|/**      * Return the value associated with the provided percentile as a String.      */
DECL|method|percentileAsString
name|String
name|percentileAsString
parameter_list|(
name|double
name|percent
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

