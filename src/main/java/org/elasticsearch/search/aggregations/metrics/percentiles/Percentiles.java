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
name|Aggregation
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|Percentiles
specifier|public
interface|interface
name|Percentiles
extends|extends
name|Aggregation
extends|,
name|Iterable
argument_list|<
name|Percentiles
operator|.
name|Percentile
argument_list|>
block|{
DECL|interface|Percentile
specifier|public
specifier|static
interface|interface
name|Percentile
block|{
DECL|method|getPercent
name|double
name|getPercent
parameter_list|()
function_decl|;
DECL|method|getValue
name|double
name|getValue
parameter_list|()
function_decl|;
block|}
DECL|method|percentile
name|double
name|percentile
parameter_list|(
name|double
name|percent
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

