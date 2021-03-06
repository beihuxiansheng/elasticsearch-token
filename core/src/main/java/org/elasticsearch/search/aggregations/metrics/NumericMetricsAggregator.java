begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics
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
name|Aggregator
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
name|pipeline
operator|.
name|PipelineAggregator
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
name|internal
operator|.
name|SearchContext
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
DECL|class|NumericMetricsAggregator
specifier|public
specifier|abstract
class|class
name|NumericMetricsAggregator
extends|extends
name|MetricsAggregator
block|{
DECL|method|NumericMetricsAggregator
specifier|private
name|NumericMetricsAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
DECL|class|SingleValue
specifier|public
specifier|abstract
specifier|static
class|class
name|SingleValue
extends|extends
name|NumericMetricsAggregator
block|{
DECL|method|SingleValue
specifier|protected
name|SingleValue
parameter_list|(
name|String
name|name
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
DECL|method|metric
specifier|public
specifier|abstract
name|double
name|metric
parameter_list|(
name|long
name|owningBucketOrd
parameter_list|)
function_decl|;
block|}
DECL|class|MultiValue
specifier|public
specifier|abstract
specifier|static
class|class
name|MultiValue
extends|extends
name|NumericMetricsAggregator
block|{
DECL|method|MultiValue
specifier|protected
name|MultiValue
parameter_list|(
name|String
name|name
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
DECL|method|hasMetric
specifier|public
specifier|abstract
name|boolean
name|hasMetric
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|metric
specifier|public
specifier|abstract
name|double
name|metric
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|owningBucketOrd
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

