begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|transport
operator|.
name|TransportClient
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
name|network
operator|.
name|NetworkModule
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|Mapper
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
name|MapperPlugin
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
name|search
operator|.
name|SearchModule
import|;
end_import

begin_class
DECL|class|PercolatorPlugin
specifier|public
class|class
name|PercolatorPlugin
extends|extends
name|Plugin
implements|implements
name|MapperPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"percolator"
decl_stmt|;
DECL|field|transportClientMode
specifier|private
specifier|final
name|boolean
name|transportClientMode
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|method|PercolatorPlugin
specifier|public
name|PercolatorPlugin
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|transportClientMode
operator|=
name|transportClientMode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ActionModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|registerAction
argument_list|(
name|PercolateAction
operator|.
name|INSTANCE
argument_list|,
name|TransportPercolateAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerAction
argument_list|(
name|MultiPercolateAction
operator|.
name|INSTANCE
argument_list|,
name|TransportMultiPercolateAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|NetworkModule
name|module
parameter_list|)
block|{
if|if
condition|(
name|transportClientMode
operator|==
literal|false
condition|)
block|{
name|module
operator|.
name|registerRestHandler
argument_list|(
name|RestPercolateAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerRestHandler
argument_list|(
name|RestMultiPercolateAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|SearchModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|registerQuery
argument_list|(
name|PercolateQueryBuilder
operator|::
operator|new
argument_list|,
name|PercolateQueryBuilder
operator|::
name|fromXContent
argument_list|,
name|PercolateQueryBuilder
operator|.
name|QUERY_NAME_FIELD
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerFetchSubPhase
argument_list|(
operator|new
name|PercolatorHighlightSubFetchPhase
argument_list|(
name|settings
argument_list|,
name|module
operator|.
name|getHighlighters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|PercolatorFieldMapper
operator|.
name|INDEX_MAP_UNMAPPED_FIELDS_AS_STRING_SETTING
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMappers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|getMappers
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|PercolatorFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|PercolatorFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
return|;
block|}
DECL|method|transportClientMode
specifier|static
name|boolean
name|transportClientMode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|TransportClient
operator|.
name|CLIENT_TYPE
operator|.
name|equals
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|Client
operator|.
name|CLIENT_TYPE_SETTING_S
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

