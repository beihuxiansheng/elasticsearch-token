begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
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
name|DocWriteResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
operator|.
name|BulkResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|IndexResponse
import|;
end_import

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
name|WriteRequest
operator|.
name|RefreshPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|update
operator|.
name|UpdateResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexSettings
import|;
end_import

begin_comment
comment|/**  * Interface implemented by responses for actions that modify the documents in an index like {@link IndexResponse}, {@link UpdateResponse},  * and {@link BulkResponse}. Rather than implement this directly most implementers should extend {@link DocWriteResponse}.  */
end_comment

begin_interface
DECL|interface|WriteResponse
specifier|public
interface|interface
name|WriteResponse
block|{
comment|/**      * Mark the response as having forced a refresh? Requests that set {@link WriteRequest#setRefreshPolicy(RefreshPolicy)} to      * {@link RefreshPolicy#IMMEDIATE} should always mark this as true. Requests that set it to {@link RefreshPolicy#WAIT_UNTIL} will only      * set this to true if they run out of refresh listener slots (see {@link IndexSettings#MAX_REFRESH_LISTENERS_PER_SHARD}).      */
DECL|method|setForcedRefresh
name|void
name|setForcedRefresh
parameter_list|(
name|boolean
name|forcedRefresh
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

