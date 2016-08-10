begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_enum
DECL|enum|EnabledAttributeMapper
specifier|public
enum|enum
name|EnabledAttributeMapper
block|{
DECL|enum constant|ENABLED
DECL|enum constant|UNSET_ENABLED
DECL|enum constant|DISABLED
DECL|enum constant|UNSET_DISABLED
name|ENABLED
argument_list|(
literal|true
argument_list|)
block|,
name|UNSET_ENABLED
argument_list|(
literal|true
argument_list|)
block|,
name|DISABLED
argument_list|(
literal|false
argument_list|)
block|,
name|UNSET_DISABLED
argument_list|(
literal|false
argument_list|)
block|;
DECL|field|enabled
specifier|public
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|method|EnabledAttributeMapper
name|EnabledAttributeMapper
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|unset
specifier|public
name|boolean
name|unset
parameter_list|()
block|{
return|return
name|this
operator|==
name|UNSET_DISABLED
operator|||
name|this
operator|==
name|UNSET_ENABLED
return|;
block|}
block|}
end_enum

end_unit

