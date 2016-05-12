begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|matrix
operator|.
name|stats
operator|.
name|MatrixStats
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
name|aggregations
operator|.
name|matrix
operator|.
name|stats
operator|.
name|MatrixStatsAggregatorBuilder
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|MatrixStatsAggregationBuilders
specifier|public
class|class
name|MatrixStatsAggregationBuilders
block|{
comment|/**      * Create a new {@link MatrixStats} aggregation with the given name.      */
DECL|method|matrixStats
specifier|public
specifier|static
name|MatrixStatsAggregatorBuilder
name|matrixStats
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MatrixStatsAggregatorBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

