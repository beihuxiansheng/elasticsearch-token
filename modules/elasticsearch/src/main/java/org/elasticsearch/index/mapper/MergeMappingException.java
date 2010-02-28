begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MergeMappingException
specifier|public
class|class
name|MergeMappingException
extends|extends
name|MapperException
block|{
DECL|field|failures
specifier|private
specifier|final
name|String
index|[]
name|failures
decl_stmt|;
DECL|method|MergeMappingException
specifier|public
name|MergeMappingException
parameter_list|(
name|String
index|[]
name|failures
parameter_list|)
block|{
name|super
argument_list|(
literal|"Merge failed with failures ["
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|failures
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|this
operator|.
name|failures
operator|=
name|failures
expr_stmt|;
block|}
DECL|method|failures
specifier|public
name|String
index|[]
name|failures
parameter_list|()
block|{
return|return
name|failures
return|;
block|}
block|}
end_class

end_unit

