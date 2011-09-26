begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
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
name|collect
operator|.
name|Maps
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
name|component
operator|.
name|AbstractComponent
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
name|Inject
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
name|settings
operator|.
name|Settings
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DiscoveryNodeService
specifier|public
class|class
name|DiscoveryNodeService
extends|extends
name|AbstractComponent
block|{
DECL|field|customAttributesProviders
specifier|private
specifier|final
name|List
argument_list|<
name|CustomAttributesProvider
argument_list|>
name|customAttributesProviders
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|CustomAttributesProvider
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|DiscoveryNodeService
annotation|@
name|Inject
specifier|public
name|DiscoveryNodeService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|addCustomAttributeProvider
specifier|public
name|DiscoveryNodeService
name|addCustomAttributeProvider
parameter_list|(
name|CustomAttributesProvider
name|customAttributesProvider
parameter_list|)
block|{
name|customAttributesProviders
operator|.
name|add
argument_list|(
name|customAttributesProvider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|buildAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildAttributes
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|settings
operator|.
name|getByPrefix
argument_list|(
literal|"node."
argument_list|)
operator|.
name|getAsMap
argument_list|()
argument_list|)
decl_stmt|;
name|attributes
operator|.
name|remove
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
comment|// name is extracted in other places
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
literal|"client"
argument_list|)
condition|)
block|{
if|if
condition|(
name|attributes
operator|.
name|get
argument_list|(
literal|"client"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
name|attributes
operator|.
name|remove
argument_list|(
literal|"client"
argument_list|)
expr_stmt|;
comment|// this is the default
block|}
else|else
block|{
comment|// if we are client node, don't store data ...
name|attributes
operator|.
name|put
argument_list|(
literal|"data"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
literal|"data"
argument_list|)
condition|)
block|{
if|if
condition|(
name|attributes
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
name|attributes
operator|.
name|remove
argument_list|(
literal|"data"
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|CustomAttributesProvider
name|provider
range|:
name|customAttributesProviders
control|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|customAttributes
init|=
name|provider
operator|.
name|buildAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|customAttributes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|customAttributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|attributes
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to build custom attributes from provider [{}]"
argument_list|,
name|e
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|attributes
return|;
block|}
DECL|interface|CustomAttributesProvider
specifier|public
specifier|static
interface|interface
name|CustomAttributesProvider
block|{
DECL|method|buildAttributes
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildAttributes
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

