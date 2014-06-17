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
name|AggregationStreams
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
name|java
operator|.
name|io
operator|.
name|IOException
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
implements|implements
name|ValueCount
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"value_count"
argument_list|,
literal|"vcount"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|private
specifier|static
specifier|final
name|AggregationStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|AggregationStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalValueCount
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalValueCount
name|count
init|=
operator|new
name|InternalValueCount
argument_list|()
decl_stmt|;
name|count
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|count
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
parameter_list|()
block|{
name|AggregationStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|method|InternalValueCount
name|InternalValueCount
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalValueCount
specifier|public
name|InternalValueCount
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
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
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalAggregation
name|reduce
parameter_list|(
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
name|reduceContext
operator|.
name|aggregations
argument_list|()
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
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
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
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
return|return
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|VALUE
argument_list|,
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
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
block|}
end_class

end_unit

