begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.sampler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|sampler
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
name|ValuesSourceAggregationBuilder
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
comment|/**  * Builder for the {@link Sampler} aggregation.  */
end_comment

begin_class
DECL|class|SamplerAggregationBuilder
specifier|public
class|class
name|SamplerAggregationBuilder
extends|extends
name|ValuesSourceAggregationBuilder
argument_list|<
name|SamplerAggregationBuilder
argument_list|>
block|{
DECL|field|shardSize
specifier|private
name|int
name|shardSize
init|=
name|SamplerAggregator
operator|.
name|Factory
operator|.
name|DEFAULT_SHARD_SAMPLE_SIZE
decl_stmt|;
comment|/**      * Sole constructor.      */
DECL|method|SamplerAggregationBuilder
specifier|public
name|SamplerAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalSampler
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the max num docs to be returned from each shard.      */
DECL|method|shardSize
specifier|public
name|SamplerAggregationBuilder
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doInternalXContent
specifier|protected
name|XContentBuilder
name|doInternalXContent
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
if|if
condition|(
name|shardSize
operator|!=
name|SamplerAggregator
operator|.
name|Factory
operator|.
name|DEFAULT_SHARD_SAMPLE_SIZE
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|SamplerAggregator
operator|.
name|SHARD_SIZE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|shardSize
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

