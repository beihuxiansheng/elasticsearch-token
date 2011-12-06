begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|VersionType
specifier|public
enum|enum
name|VersionType
block|{
DECL|enum constant|INTERNAL
name|INTERNAL
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|EXTERNAL
name|EXTERNAL
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|byte
name|value
decl_stmt|;
DECL|method|VersionType
name|VersionType
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|byte
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|VersionType
name|fromString
parameter_list|(
name|String
name|versionType
parameter_list|)
block|{
if|if
condition|(
literal|"internal"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|INTERNAL
return|;
block|}
elseif|else
if|if
condition|(
literal|"external"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|EXTERNAL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No version type match ["
operator|+
name|versionType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|VersionType
name|fromString
parameter_list|(
name|String
name|versionType
parameter_list|,
name|VersionType
name|defaultVersionType
parameter_list|)
block|{
if|if
condition|(
name|versionType
operator|==
literal|null
condition|)
block|{
return|return
name|defaultVersionType
return|;
block|}
if|if
condition|(
literal|"internal"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|INTERNAL
return|;
block|}
elseif|else
if|if
condition|(
literal|"external"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|EXTERNAL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No version type match ["
operator|+
name|versionType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromValue
specifier|public
specifier|static
name|VersionType
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
return|return
name|INTERNAL
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
return|return
name|EXTERNAL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No version type match ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

