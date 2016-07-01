begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.component
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
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
name|lease
operator|.
name|Releasable
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|LifecycleComponent
specifier|public
interface|interface
name|LifecycleComponent
extends|extends
name|Releasable
block|{
DECL|method|lifecycleState
name|Lifecycle
operator|.
name|State
name|lifecycleState
parameter_list|()
function_decl|;
DECL|method|addLifecycleListener
name|void
name|addLifecycleListener
parameter_list|(
name|LifecycleListener
name|listener
parameter_list|)
function_decl|;
DECL|method|removeLifecycleListener
name|void
name|removeLifecycleListener
parameter_list|(
name|LifecycleListener
name|listener
parameter_list|)
function_decl|;
DECL|method|start
name|void
name|start
parameter_list|()
function_decl|;
DECL|method|stop
name|void
name|stop
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

