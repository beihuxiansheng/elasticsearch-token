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

begin_comment
comment|/**  * A container for a {@link MappedFieldType} which can be updated and is reference counted.  */
end_comment

begin_class
DECL|class|MappedFieldTypeReference
specifier|public
class|class
name|MappedFieldTypeReference
block|{
DECL|field|fieldType
specifier|private
name|MappedFieldType
name|fieldType
decl_stmt|;
comment|// the current field type this reference points to
DECL|method|MappedFieldTypeReference
specifier|public
name|MappedFieldTypeReference
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
name|fieldType
operator|.
name|freeze
argument_list|()
expr_stmt|;
comment|// ensure frozen
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|MappedFieldType
name|get
parameter_list|()
block|{
return|return
name|fieldType
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
name|fieldType
operator|.
name|freeze
argument_list|()
expr_stmt|;
comment|// ensure frozen
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
block|}
block|}
end_class

end_unit

