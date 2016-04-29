begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
package|;
end_package

begin_comment
comment|/**  * Enables listening to master changes events of the local node (when the local node becomes the master, and when the local  * node cease being a master).  */
end_comment

begin_interface
DECL|interface|LocalNodeMasterListener
specifier|public
interface|interface
name|LocalNodeMasterListener
block|{
comment|/**      * Called when local node is elected to be the master      */
DECL|method|onMaster
name|void
name|onMaster
parameter_list|()
function_decl|;
comment|/**      * Called when the local node used to be the master, a new master was elected and it's no longer the local node.      */
DECL|method|offMaster
name|void
name|offMaster
parameter_list|()
function_decl|;
comment|/**      * The name of the executor that the implementation of the callbacks of this lister should be executed on. The thread      * that is responsible for managing instances of this lister is the same thread handling the cluster state events. If      * the work done is the callbacks above is inexpensive, this value may be {@link org.elasticsearch.threadpool.ThreadPool.Names#SAME SAME}      * (indicating that the callbacks will run on the same thread as the cluster state events are fired with). On the other hand,      * if the logic in the callbacks are heavier and take longer to process (or perhaps involve blocking due to IO operations),      * prefer to execute them on a separate more appropriate executor (eg. {@link org.elasticsearch.threadpool.ThreadPool.Names#GENERIC GENERIC}      * or {@link org.elasticsearch.threadpool.ThreadPool.Names#MANAGEMENT MANAGEMENT}).      *      * @return The name of the executor that will run the callbacks of this listener.      */
DECL|method|executorName
name|String
name|executorName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

