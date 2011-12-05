begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|StrictDynamicMappingException
specifier|public
class|class
name|StrictDynamicMappingException
extends|extends
name|MapperException
block|{
DECL|method|StrictDynamicMappingException
specifier|public
name|StrictDynamicMappingException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
literal|"mapping set to strict, dynamic introduction of ["
operator|+
name|fieldName
operator|+
literal|"] within ["
operator|+
name|path
operator|+
literal|"] is not allowed"
argument_list|)
expr_stmt|;
block|}
DECL|method|status
annotation|@
name|Override
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|RestStatus
operator|.
name|BAD_REQUEST
return|;
block|}
block|}
end_class

end_unit

