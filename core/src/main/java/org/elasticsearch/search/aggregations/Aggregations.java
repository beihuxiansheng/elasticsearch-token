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
comment|/**  * Represents a set of computed addAggregation.  */
end_comment

begin_interface
DECL|interface|Aggregations
specifier|public
interface|interface
name|Aggregations
extends|extends
name|Iterable
argument_list|<
name|Aggregation
argument_list|>
block|{
comment|/**      * The list of {@link Aggregation}s.      */
DECL|method|asList
name|List
argument_list|<
name|Aggregation
argument_list|>
name|asList
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Aggregation}s keyed by aggregation name.      */
DECL|method|asMap
name|Map
argument_list|<
name|String
argument_list|,
name|Aggregation
argument_list|>
name|asMap
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Aggregation}s keyed by aggregation name.      */
DECL|method|getAsMap
name|Map
argument_list|<
name|String
argument_list|,
name|Aggregation
argument_list|>
name|getAsMap
parameter_list|()
function_decl|;
comment|/**      * Returns the aggregation that is associated with the specified name.      */
DECL|method|get
parameter_list|<
name|A
extends|extends
name|Aggregation
parameter_list|>
name|A
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

