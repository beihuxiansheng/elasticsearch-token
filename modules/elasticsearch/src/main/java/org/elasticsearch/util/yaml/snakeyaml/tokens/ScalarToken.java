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
DECL|class|ScalarToken
specifier|public
specifier|final
class|class
name|ScalarToken
extends|extends
name|Token
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|plain
specifier|private
specifier|final
name|boolean
name|plain
decl_stmt|;
DECL|field|style
specifier|private
specifier|final
name|char
name|style
decl_stmt|;
DECL|method|ScalarToken
specifier|public
name|ScalarToken
parameter_list|(
name|String
name|value
parameter_list|,
name|Mark
name|startMark
parameter_list|,
name|Mark
name|endMark
parameter_list|,
name|boolean
name|plain
parameter_list|)
block|{
name|this
argument_list|(
name|value
argument_list|,
name|plain
argument_list|,
name|startMark
argument_list|,
name|endMark
argument_list|,
operator|(
name|char
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ScalarToken
specifier|public
name|ScalarToken
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|plain
parameter_list|,
name|Mark
name|startMark
parameter_list|,
name|Mark
name|endMark
parameter_list|,
name|char
name|style
parameter_list|)
block|{
name|super
argument_list|(
name|startMark
argument_list|,
name|endMark
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|plain
operator|=
name|plain
expr_stmt|;
name|this
operator|.
name|style
operator|=
name|style
expr_stmt|;
block|}
DECL|method|getPlain
specifier|public
name|boolean
name|getPlain
parameter_list|()
block|{
return|return
name|this
operator|.
name|plain
return|;
block|}
DECL|method|getValue
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|getStyle
specifier|public
name|char
name|getStyle
parameter_list|()
block|{
return|return
name|this
operator|.
name|style
return|;
block|}
annotation|@
name|Override
DECL|method|getArguments
specifier|protected
name|String
name|getArguments
parameter_list|()
block|{
return|return
literal|"value="
operator|+
name|value
operator|+
literal|", plain="
operator|+
name|plain
operator|+
literal|", style="
operator|+
name|style
return|;
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
name|Scalar
return|;
block|}
block|}
end_class

end_unit

