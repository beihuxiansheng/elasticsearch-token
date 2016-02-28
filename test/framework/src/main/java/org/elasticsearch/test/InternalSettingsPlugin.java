begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|Setting
operator|.
name|SettingsProperty
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
name|SettingsModule
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

begin_class
DECL|class|InternalSettingsPlugin
specifier|public
specifier|final
class|class
name|InternalSettingsPlugin
extends|extends
name|Plugin
block|{
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"internal-settings-plugin"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"a plugin that allows to set values for internal settings which are can't be set via the ordinary API without this pluging installed"
return|;
block|}
DECL|field|VERSION_CREATED
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|VERSION_CREATED
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"index.version.created"
argument_list|,
literal|0
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|MERGE_ENABLED
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|MERGE_ENABLED
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.merge.enabled"
argument_list|,
literal|true
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_CREATION_DATE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Long
argument_list|>
name|INDEX_CREATION_DATE_SETTING
init|=
name|Setting
operator|.
name|longSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|SettingsModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|registerSetting
argument_list|(
name|VERSION_CREATED
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|MERGE_ENABLED
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerSetting
argument_list|(
name|INDEX_CREATION_DATE_SETTING
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

