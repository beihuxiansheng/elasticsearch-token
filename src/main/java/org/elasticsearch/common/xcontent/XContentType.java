begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
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
name|json
operator|.
name|JsonXContent
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
name|smile
operator|.
name|SmileXContent
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
name|yaml
operator|.
name|YamlXContent
import|;
end_import

begin_comment
comment|/**  * The content type of {@link org.elasticsearch.common.xcontent.XContent}.  */
end_comment

begin_enum
DECL|enum|XContentType
specifier|public
enum|enum
name|XContentType
block|{
comment|/**      * A JSON based content type.      */
DECL|enum constant|JSON
name|JSON
argument_list|(
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|restContentType
parameter_list|()
block|{
return|return
literal|"application/json; charset=UTF-8"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|shortName
parameter_list|()
block|{
return|return
literal|"json"
return|;
block|}
annotation|@
name|Override
specifier|public
name|XContent
name|xContent
parameter_list|()
block|{
return|return
name|JsonXContent
operator|.
name|jsonXContent
return|;
block|}
block|}
block|,
comment|/**      * The jackson based smile binary format. Fast and compact binary format.      */
DECL|enum constant|SMILE
name|SMILE
argument_list|(
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|restContentType
parameter_list|()
block|{
return|return
literal|"application/smile"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|shortName
parameter_list|()
block|{
return|return
literal|"smile"
return|;
block|}
annotation|@
name|Override
specifier|public
name|XContent
name|xContent
parameter_list|()
block|{
return|return
name|SmileXContent
operator|.
name|smileXContent
return|;
block|}
block|}
block|,
comment|/**      * A YAML based content type.      */
DECL|enum constant|YAML
name|YAML
argument_list|(
literal|2
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|restContentType
parameter_list|()
block|{
return|return
literal|"application/yaml"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|shortName
parameter_list|()
block|{
return|return
literal|"yaml"
return|;
block|}
annotation|@
name|Override
specifier|public
name|XContent
name|xContent
parameter_list|()
block|{
return|return
name|YamlXContent
operator|.
name|yamlXContent
return|;
block|}
block|}
block|;
DECL|method|fromRestContentType
specifier|public
specifier|static
name|XContentType
name|fromRestContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
literal|"application/json"
operator|.
name|equals
argument_list|(
name|contentType
argument_list|)
operator|||
literal|"json"
operator|.
name|equalsIgnoreCase
argument_list|(
name|contentType
argument_list|)
condition|)
block|{
return|return
name|JSON
return|;
block|}
if|if
condition|(
literal|"application/smile"
operator|.
name|equals
argument_list|(
name|contentType
argument_list|)
operator|||
literal|"smile"
operator|.
name|equalsIgnoreCase
argument_list|(
name|contentType
argument_list|)
condition|)
block|{
return|return
name|SMILE
return|;
block|}
if|if
condition|(
literal|"application/yaml"
operator|.
name|equals
argument_list|(
name|contentType
argument_list|)
operator|||
literal|"yaml"
operator|.
name|equalsIgnoreCase
argument_list|(
name|contentType
argument_list|)
condition|)
block|{
return|return
name|YAML
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|method|XContentType
name|XContentType
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|int
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|restContentType
specifier|public
specifier|abstract
name|String
name|restContentType
parameter_list|()
function_decl|;
DECL|method|shortName
specifier|public
specifier|abstract
name|String
name|shortName
parameter_list|()
function_decl|;
DECL|method|xContent
specifier|public
specifier|abstract
name|XContent
name|xContent
parameter_list|()
function_decl|;
block|}
end_enum

end_unit

