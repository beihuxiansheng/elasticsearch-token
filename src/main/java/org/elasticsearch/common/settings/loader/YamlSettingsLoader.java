begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings.loader
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|loader
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
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Settings loader that loads (parses) the settings in a yaml format by flattening them  * into a map.  */
end_comment

begin_class
DECL|class|YamlSettingsLoader
specifier|public
class|class
name|YamlSettingsLoader
extends|extends
name|XContentSettingsLoader
block|{
annotation|@
name|Override
DECL|method|contentType
specifier|public
name|XContentType
name|contentType
parameter_list|()
block|{
return|return
name|XContentType
operator|.
name|YAML
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|load
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|IOException
block|{
comment|// replace tabs with whitespace (yaml does not accept tabs, but many users might use it still...)
return|return
name|super
operator|.
name|load
argument_list|(
name|source
operator|.
name|replace
argument_list|(
literal|"\t"
argument_list|,
literal|"  "
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

