begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_comment
comment|/**  *<code>FilterLongValues</code> contains another {@link LongValues}, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality.  */
end_comment

begin_class
DECL|class|FilterLongValues
specifier|public
class|class
name|FilterLongValues
extends|extends
name|LongValues
block|{
DECL|field|delegate
specifier|protected
specifier|final
name|LongValues
name|delegate
decl_stmt|;
DECL|method|FilterLongValues
specifier|protected
name|FilterLongValues
parameter_list|(
name|LongValues
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
operator|.
name|isMultiValued
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextValue
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|nextValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

