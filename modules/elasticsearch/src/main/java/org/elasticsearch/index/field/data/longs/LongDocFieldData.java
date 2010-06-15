begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.longs
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
name|longs
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
name|joda
operator|.
name|time
operator|.
name|MutableDateTime
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
name|field
operator|.
name|data
operator|.
name|NumericDocFieldData
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|LongDocFieldData
specifier|public
class|class
name|LongDocFieldData
extends|extends
name|NumericDocFieldData
argument_list|<
name|LongFieldData
argument_list|>
block|{
DECL|method|LongDocFieldData
specifier|public
name|LongDocFieldData
parameter_list|(
name|LongFieldData
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
name|long
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
name|long
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
DECL|method|getDate
specifier|public
name|MutableDateTime
name|getDate
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|date
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getDates
specifier|public
name|MutableDateTime
index|[]
name|getDates
parameter_list|()
block|{
return|return
name|fieldData
operator|.
name|dates
argument_list|(
name|docId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

