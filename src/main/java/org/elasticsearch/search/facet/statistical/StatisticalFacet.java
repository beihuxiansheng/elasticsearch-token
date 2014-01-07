begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.statistical
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|statistical
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

begin_comment
comment|/**  * Numeric statistical information.  */
end_comment

begin_interface
DECL|interface|StatisticalFacet
specifier|public
interface|interface
name|StatisticalFacet
extends|extends
name|Facet
block|{
comment|/**      * The type of the filter facet.      */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"statistical"
decl_stmt|;
comment|/**      * The number of values counted.      */
DECL|method|getCount
name|long
name|getCount
parameter_list|()
function_decl|;
comment|/**      * The total (sum) of values.      */
DECL|method|getTotal
name|double
name|getTotal
parameter_list|()
function_decl|;
comment|/**      * The sum of squares of the values.      */
DECL|method|getSumOfSquares
name|double
name|getSumOfSquares
parameter_list|()
function_decl|;
comment|/**      * The mean (average) of the values.      */
DECL|method|getMean
name|double
name|getMean
parameter_list|()
function_decl|;
comment|/**      * The minimum value.      */
DECL|method|getMin
name|double
name|getMin
parameter_list|()
function_decl|;
comment|/**      * The maximum value.      */
DECL|method|getMax
name|double
name|getMax
parameter_list|()
function_decl|;
comment|/**      * Variance of the values.      */
DECL|method|getVariance
name|double
name|getVariance
parameter_list|()
function_decl|;
comment|/**      * Standard deviation of the values.      */
DECL|method|getStdDeviation
name|double
name|getStdDeviation
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

