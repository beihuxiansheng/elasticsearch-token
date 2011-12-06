begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.floats
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
operator|.
name|floats
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|data
operator|.
name|NumericDocFieldData
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FloatDocFieldData
specifier|public
class|class
name|FloatDocFieldData
extends|extends
name|NumericDocFieldData
argument_list|<
name|FloatFieldData
argument_list|>
block|{
DECL|method|FloatDocFieldData
specifier|public
name|FloatDocFieldData
parameter_list|(
name|FloatFieldData
name|fieldData
parameter_list|)
block|{
name|super
argument_list|(
name|fieldData
argument_list|)
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
name|float
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|values
argument_list|(
name|docId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

