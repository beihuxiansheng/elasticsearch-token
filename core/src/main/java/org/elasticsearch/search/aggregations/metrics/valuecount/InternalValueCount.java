begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.valuecount
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
name|valuecount
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|InternalAggregation
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
name|metrics
operator|.
name|InternalNumericMetricsAggregation
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * An internal implementation of {@link ValueCount}.  */
end_comment

begin_class
DECL|class|InternalValueCount
specifier|public
class|class
name|InternalValueCount
extends|extends
name|InternalNumericMetricsAggregation
operator|.
name|SingleValue
implements|implements
name|ValueCount
block|{
DECL|field|value
specifier|private
specifier|final
name|long
name|value
decl_stmt|;
DECL|method|InternalValueCount
specifier|public
name|InternalValueCount
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
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
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|InternalValueCount
specifier|public
name|InternalValueCount
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|value
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|ValueCountAggregationBuilder
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|double
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
name|InternalAggregation
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|long
name|valueCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|valueCount
operator|+=
operator|(
operator|(
name|InternalValueCount
operator|)
name|aggregation
operator|)
operator|.
name|value
expr_stmt|;
block|}
return|return
operator|new
name|InternalValueCount
argument_list|(
name|name
argument_list|,
name|valueCount
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|VALUE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"count["
operator|+
name|value
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|InternalValueCount
name|that
init|=
operator|(
name|InternalValueCount
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|value
argument_list|,
name|that
operator|.
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

