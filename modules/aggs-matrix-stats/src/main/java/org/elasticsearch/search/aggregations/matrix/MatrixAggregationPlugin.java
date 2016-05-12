begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.matrix
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|SearchModule
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
name|InternalMatrixStats
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
name|MatrixStatsParser
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

begin_class
DECL|class|MatrixAggregationPlugin
specifier|public
class|class
name|MatrixAggregationPlugin
extends|extends
name|Plugin
block|{
static|static
block|{
name|InternalMatrixStats
operator|.
name|registerStreams
argument_list|()
expr_stmt|;
block|}
DECL|method|MatrixAggregationPlugin
specifier|public
name|MatrixAggregationPlugin
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"aggs-matrix-stats"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Adds aggregations whose input are a list of numeric fields and output includes a matrix."
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|SearchModule
name|searchModule
parameter_list|)
block|{
name|searchModule
operator|.
name|registerAggregation
argument_list|(
name|MatrixStatsAggregatorBuilder
operator|::
operator|new
argument_list|,
operator|new
name|MatrixStatsParser
argument_list|()
argument_list|,
name|MatrixStatsAggregatorBuilder
operator|.
name|AGGREGATION_NAME_FIELD
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

