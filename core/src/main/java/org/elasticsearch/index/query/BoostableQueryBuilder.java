begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Query builder which allow setting some boost  */
end_comment

begin_interface
DECL|interface|BoostableQueryBuilder
specifier|public
interface|interface
name|BoostableQueryBuilder
parameter_list|<
name|B
extends|extends
name|BoostableQueryBuilder
parameter_list|<
name|B
parameter_list|>
parameter_list|>
block|{
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
name|B
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

