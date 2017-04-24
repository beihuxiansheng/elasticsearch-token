begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.stats
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
name|stats
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
comment|/**  * Statistics over a set of values (either aggregated over field data or scripts)  */
end_comment

begin_interface
DECL|interface|Stats
specifier|public
interface|interface
name|Stats
extends|extends
name|NumericMetricsAggregation
operator|.
name|MultiValue
block|{
comment|/**      * @return The number of values that were aggregated.      */
DECL|method|getCount
name|long
name|getCount
parameter_list|()
function_decl|;
comment|/**      * @return The minimum value of all aggregated values.      */
DECL|method|getMin
name|double
name|getMin
parameter_list|()
function_decl|;
comment|/**      * @return The maximum value of all aggregated values.      */
DECL|method|getMax
name|double
name|getMax
parameter_list|()
function_decl|;
comment|/**      * @return The avg value over all aggregated values.      */
DECL|method|getAvg
name|double
name|getAvg
parameter_list|()
function_decl|;
comment|/**      * @return The sum of aggregated values.      */
DECL|method|getSum
name|double
name|getSum
parameter_list|()
function_decl|;
comment|/**      * @return The minimum value of all aggregated values as a String.      */
DECL|method|getMinAsString
name|String
name|getMinAsString
parameter_list|()
function_decl|;
comment|/**      * @return The maximum value of all aggregated values as a String.      */
DECL|method|getMaxAsString
name|String
name|getMaxAsString
parameter_list|()
function_decl|;
comment|/**      * @return The avg value over all aggregated values as a String.      */
DECL|method|getAvgAsString
name|String
name|getAvgAsString
parameter_list|()
function_decl|;
comment|/**      * @return The sum of aggregated values as a String.      */
DECL|method|getSumAsString
name|String
name|getSumAsString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

