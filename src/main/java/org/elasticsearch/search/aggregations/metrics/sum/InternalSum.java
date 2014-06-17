begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.sum
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
name|sum
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueFormatterStreams
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
comment|/** * */
end_comment

begin_class
DECL|class|InternalSum
specifier|public
class|class
name|InternalSum
extends|extends
name|InternalNumericMetricsAggregation
operator|.
name|SingleValue
implements|implements
name|Sum
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"sum"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|final
specifier|static
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
name|InternalSum
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalSum
name|result
init|=
operator|new
name|InternalSum
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
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
DECL|field|sum
specifier|private
name|double
name|sum
decl_stmt|;
DECL|method|InternalSum
name|InternalSum
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalSum
name|InternalSum
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|sum
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|sum
operator|=
name|sum
expr_stmt|;
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
name|sum
return|;
block|}
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
block|{
return|return
name|sum
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
name|InternalSum
name|reduce
parameter_list|(
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|double
name|sum
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
name|sum
operator|+=
operator|(
operator|(
name|InternalSum
operator|)
name|aggregation
operator|)
operator|.
name|sum
expr_stmt|;
block|}
return|return
operator|new
name|InternalSum
argument_list|(
name|name
argument_list|,
name|sum
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
name|valueFormatter
operator|=
name|ValueFormatterStreams
operator|.
name|readOptional
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|sum
operator|=
name|in
operator|.
name|readDouble
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
name|ValueFormatterStreams
operator|.
name|writeOptional
argument_list|(
name|valueFormatter
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|sum
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
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|VALUE
argument_list|,
name|sum
argument_list|)
expr_stmt|;
if|if
condition|(
name|valueFormatter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|VALUE_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|sum
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

