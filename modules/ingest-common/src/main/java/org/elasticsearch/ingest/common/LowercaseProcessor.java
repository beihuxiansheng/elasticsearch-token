begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Processor that converts the content of string fields to lowercase.  * Throws exception is the field is not of type string.  */
end_comment

begin_class
DECL|class|LowercaseProcessor
specifier|public
specifier|final
class|class
name|LowercaseProcessor
extends|extends
name|AbstractStringProcessor
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"lowercase"
decl_stmt|;
DECL|method|LowercaseProcessor
name|LowercaseProcessor
parameter_list|(
name|String
name|processorTag
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|processorTag
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|protected
name|String
name|process
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
specifier|final
class|class
name|Factory
extends|extends
name|AbstractStringProcessor
operator|.
name|Factory
block|{
DECL|method|Factory
specifier|public
name|Factory
parameter_list|()
block|{
name|super
argument_list|(
name|TYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newProcessor
specifier|protected
name|LowercaseProcessor
name|newProcessor
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|LowercaseProcessor
argument_list|(
name|tag
argument_list|,
name|field
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

