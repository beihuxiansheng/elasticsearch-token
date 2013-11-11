begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|Map
import|;
end_import

begin_class
DECL|class|NodeSettingsSource
specifier|abstract
class|class
name|NodeSettingsSource
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|NodeSettingsSource
name|EMPTY
init|=
operator|new
name|NodeSettingsSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Settings
name|settings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|/**      * @return  the settings for the node represented by the given ordinal, or {@code null} if there are not settings defined (in which      *          case a random settings will be generated for the node)      */
DECL|method|settings
specifier|public
specifier|abstract
name|Settings
name|settings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
function_decl|;
DECL|class|Immutable
specifier|public
specifier|static
class|class
name|Immutable
extends|extends
name|NodeSettingsSource
block|{
DECL|field|settingsPerNode
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Settings
argument_list|>
name|settingsPerNode
decl_stmt|;
DECL|method|Immutable
specifier|private
name|Immutable
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|Settings
argument_list|>
name|settingsPerNode
parameter_list|)
block|{
name|this
operator|.
name|settingsPerNode
operator|=
name|settingsPerNode
expr_stmt|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|settingsPerNode
operator|.
name|get
argument_list|(
name|nodeOrdinal
argument_list|)
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|settingsPerNode
specifier|private
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Integer
argument_list|,
name|Settings
argument_list|>
name|settingsPerNode
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
DECL|method|Builder
specifier|private
name|Builder
parameter_list|()
block|{             }
DECL|method|set
specifier|public
name|Builder
name|set
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|settingsPerNode
operator|.
name|put
argument_list|(
name|ordinal
argument_list|,
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|Immutable
name|build
parameter_list|()
block|{
return|return
operator|new
name|Immutable
argument_list|(
name|settingsPerNode
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

