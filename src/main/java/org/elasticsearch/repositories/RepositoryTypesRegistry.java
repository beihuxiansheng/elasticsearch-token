begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|Module
import|;
end_import

begin_comment
comment|/**  * Map of registered repository types and associated with these types modules  */
end_comment

begin_class
DECL|class|RepositoryTypesRegistry
specifier|public
class|class
name|RepositoryTypesRegistry
block|{
DECL|field|repositoryTypes
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|repositoryTypes
decl_stmt|;
comment|/**      * Creates new repository with given map of types      *      * @param repositoryTypes      */
DECL|method|RepositoryTypesRegistry
specifier|public
name|RepositoryTypesRegistry
parameter_list|(
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|repositoryTypes
parameter_list|)
block|{
name|this
operator|.
name|repositoryTypes
operator|=
name|repositoryTypes
expr_stmt|;
block|}
comment|/**      * Returns repository module class for the given type      *      * @param type repository type      * @return repository module class or null if type is not found      */
DECL|method|type
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|repositoryTypes
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
block|}
end_class

end_unit

