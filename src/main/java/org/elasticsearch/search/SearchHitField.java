begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
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
name|io
operator|.
name|stream
operator|.
name|Streamable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A single field name and values part of a {@link SearchHit}.  *  *  * @see SearchHit  */
end_comment

begin_interface
DECL|interface|SearchHitField
specifier|public
interface|interface
name|SearchHitField
extends|extends
name|Streamable
extends|,
name|Iterable
argument_list|<
name|Object
argument_list|>
block|{
comment|/**      * The name of the field.      */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * The name of the field.      */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * The first value of the hit.      */
DECL|method|value
name|Object
name|value
parameter_list|()
function_decl|;
comment|/**      * The first value of the hit.      */
DECL|method|getValue
name|Object
name|getValue
parameter_list|()
function_decl|;
comment|/**      * The field values.      */
DECL|method|values
name|List
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|()
function_decl|;
comment|/**      * The field values.      */
DECL|method|getValues
name|List
argument_list|<
name|Object
argument_list|>
name|getValues
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

