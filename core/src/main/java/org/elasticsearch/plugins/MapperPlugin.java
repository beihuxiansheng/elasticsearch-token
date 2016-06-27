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
name|index
operator|.
name|mapper
operator|.
name|MetadataFieldMapper
import|;
end_import

begin_comment
comment|/**  * An extension point for {@link Plugin} implementations to add custom mappers  */
end_comment

begin_interface
DECL|interface|MapperPlugin
specifier|public
interface|interface
name|MapperPlugin
block|{
comment|/**      * Returns additional mapper implementations added by this plugin.      *      * The key of the returned {@link Map} is the unique name for the mapper which will be used      * as the mapping {@code type}, and the value is a {@link Mapper.TypeParser} to parse the      * mapper settings into a {@link Mapper}.      */
DECL|method|getMappers
specifier|default
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
name|emptyMap
argument_list|()
return|;
block|}
comment|/**      * Returns additional metadata mapper implementations added by this plugin.      *      * The key of the returned {@link Map} is the unique name for the metadata mapper, which      * is used in the mapping json to configure the metadata mapper, and the value is a      * {@link MetadataFieldMapper.TypeParser} to parse the mapper settings into a      * {@link MetadataFieldMapper}.      */
DECL|method|getMetadataMappers
specifier|default
name|Map
argument_list|<
name|String
argument_list|,
name|MetadataFieldMapper
operator|.
name|TypeParser
argument_list|>
name|getMetadataMappers
parameter_list|()
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

