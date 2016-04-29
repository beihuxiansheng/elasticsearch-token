begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
import|;
end_import

begin_comment
comment|/**  * Listener for task registration/unregistration  */
end_comment

begin_interface
DECL|interface|MockTaskManagerListener
specifier|public
interface|interface
name|MockTaskManagerListener
block|{
DECL|method|onTaskRegistered
name|void
name|onTaskRegistered
parameter_list|(
name|Task
name|task
parameter_list|)
function_decl|;
DECL|method|onTaskUnregistered
name|void
name|onTaskUnregistered
parameter_list|(
name|Task
name|task
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

