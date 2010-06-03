begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facets
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facets
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
comment|/**  * A search facet.  *  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|Facet
specifier|public
interface|interface
name|Facet
block|{
DECL|enum|Type
enum|enum
name|Type
block|{
comment|/**          * Count type facet.          */
DECL|enum constant|COUNT
name|COUNT
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|MULTI_COUNT
name|MULTI_COUNT
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|;
DECL|field|id
name|byte
name|id
decl_stmt|;
DECL|method|Type
name|Type
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|fromId
specifier|public
specifier|static
name|Type
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|COUNT
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|MULTI_COUNT
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No match for id ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * The "logical" name of the search facet.      */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * The "logical" name of the search facet.      */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * The type of the facet.      */
DECL|method|type
name|Type
name|type
parameter_list|()
function_decl|;
comment|/**      * The type of the facet.      */
DECL|method|getType
name|Type
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

