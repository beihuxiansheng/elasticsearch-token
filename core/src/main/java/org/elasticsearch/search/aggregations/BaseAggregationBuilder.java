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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Interface shared by {@link AggregationBuilder} and {@link PipelineAggregationBuilder} so they can conveniently share the same namespace  * for {@link XContentParser#namedObject(Class, String, Object)}.  */
end_comment

begin_interface
DECL|interface|BaseAggregationBuilder
specifier|public
interface|interface
name|BaseAggregationBuilder
block|{
comment|/**      * The name of the type of aggregation built by this builder.       */
DECL|method|getType
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * Set the aggregation's metadata. Returns {@code this} for chaining.      */
DECL|method|setMetaData
name|BaseAggregationBuilder
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
comment|/**      * Set the sub aggregations if this aggregation supports sub aggregations. Returns {@code this} for chaining.      */
DECL|method|subAggregations
name|BaseAggregationBuilder
name|subAggregations
parameter_list|(
name|Builder
name|subFactories
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

