begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
package|;
end_package

begin_comment
comment|/**  * Allows to import data into elasticsearch via plugin  * Gets allocated on a node and eventually automatically re-allocated if needed  * @deprecated See blog post https://www.elastic.co/blog/deprecating_rivers  */
end_comment

begin_interface
annotation|@
name|Deprecated
DECL|interface|River
specifier|public
interface|interface
name|River
extends|extends
name|RiverComponent
block|{
comment|/**      * Called whenever the river is registered on a node, which can happen when:      * 1) the river _meta document gets indexed      * 2) an already registered river gets started on a node      */
DECL|method|start
name|void
name|start
parameter_list|()
function_decl|;
comment|/**      * Called when the river is closed on a node, which can happen when:      * 1) the river is deleted by deleting its type through the delete mapping api      * 2) the node where the river is allocated is shut down or the river gets rerouted to another node      */
DECL|method|close
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

