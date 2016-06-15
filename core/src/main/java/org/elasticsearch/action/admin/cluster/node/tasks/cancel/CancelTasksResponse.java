begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.tasks.cancel
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|cancel
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
name|FailedNodeException
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
name|TaskOperationFailure
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskInfo
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

begin_comment
comment|/**  * Returns the list of tasks that were cancelled  */
end_comment

begin_class
DECL|class|CancelTasksResponse
specifier|public
class|class
name|CancelTasksResponse
extends|extends
name|ListTasksResponse
block|{
DECL|method|CancelTasksResponse
specifier|public
name|CancelTasksResponse
parameter_list|()
block|{     }
DECL|method|CancelTasksResponse
specifier|public
name|CancelTasksResponse
parameter_list|(
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
parameter_list|,
name|List
argument_list|<
name|TaskOperationFailure
argument_list|>
name|taskFailures
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|FailedNodeException
argument_list|>
name|nodeFailures
parameter_list|)
block|{
name|super
argument_list|(
name|tasks
argument_list|,
name|taskFailures
argument_list|,
name|nodeFailures
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

