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
name|action
operator|.
name|support
operator|.
name|ToXContentToBytes
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
name|NamedWriteable
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
name|AggregatorFactories
operator|.
name|Builder
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

begin_comment
comment|/**  * A factory that knows how to create an {@link PipelineAggregator} of a  * specific type.  */
end_comment

begin_class
DECL|class|PipelineAggregationBuilder
specifier|public
specifier|abstract
class|class
name|PipelineAggregationBuilder
extends|extends
name|ToXContentToBytes
implements|implements
name|NamedWriteable
implements|,
name|BaseAggregationBuilder
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|bucketsPaths
specifier|protected
specifier|final
name|String
index|[]
name|bucketsPaths
decl_stmt|;
comment|/**      * Constructs a new pipeline aggregator factory.      *      * @param name      *            The aggregation name      */
DECL|method|PipelineAggregationBuilder
specifier|protected
name|PipelineAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[name] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bucketsPaths
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[bucketsPaths] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|bucketsPaths
operator|=
name|bucketsPaths
expr_stmt|;
block|}
comment|/** Return this aggregation's name. */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Return the consumed buckets paths. */
DECL|method|getBucketsPaths
specifier|public
specifier|final
name|String
index|[]
name|getBucketsPaths
parameter_list|()
block|{
return|return
name|bucketsPaths
return|;
block|}
comment|/**      * Internal: Validates the state of this factory (makes sure the factory is properly      * configured)      */
DECL|method|validate
specifier|protected
specifier|abstract
name|void
name|validate
parameter_list|(
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
index|[]
name|factories
parameter_list|,
name|List
argument_list|<
name|PipelineAggregationBuilder
argument_list|>
name|pipelineAggregatorFactories
parameter_list|)
function_decl|;
comment|/**      * Creates the pipeline aggregator      *      * @return The created aggregator      */
DECL|method|create
specifier|protected
specifier|abstract
name|PipelineAggregator
name|create
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Associate metadata with this {@link PipelineAggregationBuilder}. */
annotation|@
name|Override
DECL|method|setMetaData
specifier|public
specifier|abstract
name|PipelineAggregationBuilder
name|setMetaData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|subAggregations
specifier|public
name|PipelineAggregationBuilder
name|subAggregations
parameter_list|(
name|Builder
name|subFactories
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Aggregation ["
operator|+
name|name
operator|+
literal|"] cannot define sub-aggregations"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

