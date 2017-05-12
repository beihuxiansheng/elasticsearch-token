begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.join.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|aggregations
package|;
end_package

begin_class
DECL|class|JoinAggregationBuilders
specifier|public
specifier|abstract
class|class
name|JoinAggregationBuilders
block|{
comment|/**      * Create a new {@link Children} aggregation with the given name.      */
DECL|method|children
specifier|public
specifier|static
name|ChildrenAggregationBuilder
name|children
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|childType
parameter_list|)
block|{
return|return
operator|new
name|ChildrenAggregationBuilder
argument_list|(
name|name
argument_list|,
name|childType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

