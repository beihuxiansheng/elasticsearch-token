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
comment|/**  * Interface for a class used to gather information about a cluster at  * regular intervals  */
end_comment

begin_interface
DECL|interface|ClusterInfoService
specifier|public
interface|interface
name|ClusterInfoService
block|{
comment|/** The latest cluster information */
DECL|method|getClusterInfo
name|ClusterInfo
name|getClusterInfo
parameter_list|()
function_decl|;
comment|/** Add a listener that will be called every time new information is gathered */
DECL|method|addListener
name|void
name|addListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Interface for listeners to implement in order to perform actions when      * new information about the cluster has been gathered      */
DECL|interface|Listener
interface|interface
name|Listener
block|{
DECL|method|onNewInfo
name|void
name|onNewInfo
parameter_list|(
name|ClusterInfo
name|info
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

