begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.stats.extended
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
operator|.
name|extended
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
name|stats
operator|.
name|Stats
import|;
end_import

begin_comment
comment|/**  * Statistics over a set of values (either aggregated over field data or scripts)  */
end_comment

begin_interface
DECL|interface|ExtendedStats
specifier|public
interface|interface
name|ExtendedStats
extends|extends
name|Stats
block|{
comment|/**      * The sum of the squares of the collected values.      */
DECL|method|getSumOfSquares
name|double
name|getSumOfSquares
parameter_list|()
function_decl|;
comment|/**      * The variance of the collected values.      */
DECL|method|getVariance
name|double
name|getVariance
parameter_list|()
function_decl|;
comment|/**      * The standard deviation of the collected values.      */
DECL|method|getStdDeviation
name|double
name|getStdDeviation
parameter_list|()
function_decl|;
comment|/**      * The upper or lower bounds of the stdDeviation      */
DECL|method|getStdDeviationBound
name|double
name|getStdDeviationBound
parameter_list|(
name|Bounds
name|bound
parameter_list|)
function_decl|;
comment|/**      * The standard deviation of the collected values as a String.      */
DECL|method|getStdDeviationAsString
name|String
name|getStdDeviationAsString
parameter_list|()
function_decl|;
comment|/**      * The upper or lower bounds of stdDev of the collected values as a String.      */
DECL|method|getStdDeviationBoundAsString
name|String
name|getStdDeviationBoundAsString
parameter_list|(
name|Bounds
name|bound
parameter_list|)
function_decl|;
comment|/**      * The sum of the squares of the collected values as a String.      */
DECL|method|getSumOfSquaresAsString
name|String
name|getSumOfSquaresAsString
parameter_list|()
function_decl|;
comment|/**      * The variance of the collected values as a String.      */
DECL|method|getVarianceAsString
name|String
name|getVarianceAsString
parameter_list|()
function_decl|;
DECL|enum|Bounds
enum|enum
name|Bounds
block|{
DECL|enum constant|UPPER
DECL|enum constant|LOWER
name|UPPER
block|,
name|LOWER
block|}
block|}
end_interface

end_unit

