begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|data
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DocFieldData
specifier|public
specifier|abstract
class|class
name|DocFieldData
parameter_list|<
name|T
extends|extends
name|FieldData
parameter_list|>
block|{
DECL|field|fieldData
specifier|protected
specifier|final
name|T
name|fieldData
decl_stmt|;
DECL|field|docId
specifier|protected
name|int
name|docId
decl_stmt|;
DECL|method|DocFieldData
specifier|protected
name|DocFieldData
parameter_list|(
name|T
name|fieldData
parameter_list|)
block|{
name|this
operator|.
name|fieldData
operator|=
name|fieldData
expr_stmt|;
block|}
DECL|method|setDocId
name|void
name|setDocId
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|fieldName
argument_list|()
return|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
operator|!
name|fieldData
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|stringValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getStringValue
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|stringValue
argument_list|()
return|;
block|}
DECL|method|getType
specifier|public
name|FieldDataType
name|getType
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|type
argument_list|()
return|;
block|}
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|multiValued
argument_list|()
return|;
block|}
block|}
end_class

end_unit

