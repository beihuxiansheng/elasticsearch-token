begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
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
name|fielddata
operator|.
name|DoubleValues
import|;
end_import

begin_comment
comment|/**  * Package private base class for dense double values.  */
end_comment

begin_class
DECL|class|DenseDoubleValues
specifier|abstract
class|class
name|DenseDoubleValues
extends|extends
name|DoubleValues
block|{
DECL|method|DenseDoubleValues
specifier|protected
name|DenseDoubleValues
parameter_list|(
name|boolean
name|multiValued
parameter_list|)
block|{
name|super
argument_list|(
name|multiValued
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
specifier|final
name|int
name|setDocument
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
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

