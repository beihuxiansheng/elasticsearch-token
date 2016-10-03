begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexClusterStateUpdateRequest
import|;
end_import

begin_comment
comment|/**  * Enables filtering the index templates that will be applied for an index, per create index request.  */
end_comment

begin_interface
DECL|interface|IndexTemplateFilter
specifier|public
interface|interface
name|IndexTemplateFilter
block|{
comment|/**      * @return  {@code true} if the given template should be applied on the newly created index,      *          {@code false} otherwise.      */
DECL|method|apply
name|boolean
name|apply
parameter_list|(
name|CreateIndexClusterStateUpdateRequest
name|request
parameter_list|,
name|IndexTemplateMetaData
name|template
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

