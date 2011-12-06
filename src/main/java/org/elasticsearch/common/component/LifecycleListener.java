begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|LifecycleListener
specifier|public
interface|interface
name|LifecycleListener
block|{
DECL|method|beforeStart
name|void
name|beforeStart
parameter_list|()
function_decl|;
DECL|method|afterStart
name|void
name|afterStart
parameter_list|()
function_decl|;
DECL|method|beforeStop
name|void
name|beforeStop
parameter_list|()
function_decl|;
DECL|method|afterStop
name|void
name|afterStop
parameter_list|()
function_decl|;
DECL|method|beforeClose
name|void
name|beforeClose
parameter_list|()
function_decl|;
DECL|method|afterClose
name|void
name|afterClose
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

