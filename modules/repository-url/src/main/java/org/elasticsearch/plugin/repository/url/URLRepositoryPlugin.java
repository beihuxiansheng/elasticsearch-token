begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.repository.url
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|repository
operator|.
name|url
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
name|settings
operator|.
name|Setting
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
name|xcontent
operator|.
name|NamedXContentRegistry
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|RepositoryPlugin
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|url
operator|.
name|URLRepository
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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
name|List
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

begin_class
DECL|class|URLRepositoryPlugin
specifier|public
class|class
name|URLRepositoryPlugin
extends|extends
name|Plugin
implements|implements
name|RepositoryPlugin
block|{
annotation|@
name|Override
DECL|method|getSettings
specifier|public
name|List
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|getSettings
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|URLRepository
operator|.
name|ALLOWED_URLS_SETTING
argument_list|,
name|URLRepository
operator|.
name|REPOSITORIES_URL_SETTING
argument_list|,
name|URLRepository
operator|.
name|SUPPORTED_PROTOCOLS_SETTING
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRepositories
specifier|public
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
parameter_list|,
name|NamedXContentRegistry
name|namedXContentRegistry
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|URLRepository
operator|.
name|TYPE
argument_list|,
name|metadata
lambda|->
operator|new
name|URLRepository
argument_list|(
name|metadata
argument_list|,
name|env
argument_list|,
name|namedXContentRegistry
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
