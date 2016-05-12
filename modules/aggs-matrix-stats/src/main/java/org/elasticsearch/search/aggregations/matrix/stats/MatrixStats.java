begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.matrix.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|matrix
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
name|Aggregation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
comment|/**  * Interface for MatrixStats Metric Aggregation  */
end_comment

begin_interface
DECL|interface|MatrixStats
specifier|public
interface|interface
name|MatrixStats
extends|extends
name|Aggregation
block|{
comment|/** return the total document count */
DECL|method|getDocCount
name|long
name|getDocCount
parameter_list|()
function_decl|;
comment|/** return total field count (differs from docCount if there are missing values) */
DECL|method|getFieldCount
name|long
name|getFieldCount
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** return the field mean */
DECL|method|getMean
name|Double
name|getMean
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** return the field variance */
DECL|method|getVariance
name|Double
name|getVariance
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** return the skewness of the distribution */
DECL|method|getSkewness
name|Double
name|getSkewness
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** return the kurtosis of the distribution */
DECL|method|getKurtosis
name|Double
name|getKurtosis
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** return the upper triangle of the covariance matrix */
DECL|method|getCovariance
name|Map
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|>
name|getCovariance
parameter_list|()
function_decl|;
comment|/** return the covariance between field x and field y */
DECL|method|getCovariance
name|Double
name|getCovariance
parameter_list|(
name|String
name|fieldX
parameter_list|,
name|String
name|fieldY
parameter_list|)
function_decl|;
comment|/** return the upper triangle of the pearson product-moment correlation matrix */
DECL|method|getCorrelation
name|Map
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|>
name|getCorrelation
parameter_list|()
function_decl|;
comment|/** return the correlation coefficient of field x and field y */
DECL|method|getCorrelation
name|Double
name|getCorrelation
parameter_list|(
name|String
name|fieldX
parameter_list|,
name|String
name|fieldY
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

