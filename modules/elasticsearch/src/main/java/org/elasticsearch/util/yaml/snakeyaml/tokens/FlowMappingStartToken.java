begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.tokens
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|tokens
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|error
operator|.
name|Mark
import|;
end_import

begin_comment
comment|/**  * @see<a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information  */
end_comment

begin_class
DECL|class|FlowMappingStartToken
specifier|public
specifier|final
class|class
name|FlowMappingStartToken
extends|extends
name|Token
block|{
DECL|method|FlowMappingStartToken
specifier|public
name|FlowMappingStartToken
parameter_list|(
name|Mark
name|startMark
parameter_list|,
name|Mark
name|endMark
parameter_list|)
block|{
name|super
argument_list|(
name|startMark
argument_list|,
name|endMark
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTokenId
specifier|public
name|ID
name|getTokenId
parameter_list|()
block|{
return|return
name|ID
operator|.
name|FlowMappingStart
return|;
block|}
block|}
end_class

end_unit

