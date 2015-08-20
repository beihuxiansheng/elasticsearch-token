begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_comment
comment|/**  * A listener for action responses or failures.  *  *  */
end_comment

begin_interface
DECL|interface|ActionListener
specifier|public
interface|interface
name|ActionListener
parameter_list|<
name|Response
parameter_list|>
block|{
comment|/**      * A response handler.      */
DECL|method|onResponse
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
function_decl|;
comment|/**      * A failure handler.      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
function_decl|;
block|}
end_interface

end_unit
