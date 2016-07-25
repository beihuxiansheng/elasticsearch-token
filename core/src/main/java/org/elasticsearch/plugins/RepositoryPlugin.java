begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|Repository
import|;
end_import

begin_comment
comment|/**  * An extension point for {@link Plugin} implementations to add custom snapshot repositories.  */
end_comment

begin_interface
DECL|interface|RepositoryPlugin
specifier|public
interface|interface
name|RepositoryPlugin
block|{
comment|/**      * Returns repository types added by this plugin.      *      * @param env The environment for the local node, which may be used for the local settings and path.repo      *      * The key of the returned {@link Map} is the type name of the repository and      * the value is a factory to construct the {@link Repository} interface.      */
DECL|method|getRepositories
specifier|default
name|Map
argument_list|<
name|String
argument_list|,
name|Repository
operator|.
name|Factory
argument_list|>
name|getRepositories
parameter_list|(
name|Environment
name|env
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
end_interface

end_unit

