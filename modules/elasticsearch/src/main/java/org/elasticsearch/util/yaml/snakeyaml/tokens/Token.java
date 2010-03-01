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
name|YAMLException
import|;
end_import

begin_comment
comment|/**  * @see<a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information  */
end_comment

begin_class
DECL|class|Token
specifier|public
specifier|abstract
class|class
name|Token
block|{
DECL|enum|ID
specifier|public
enum|enum
name|ID
block|{
DECL|enum constant|Alias
DECL|enum constant|Anchor
DECL|enum constant|BlockEnd
DECL|enum constant|BlockEntry
DECL|enum constant|BlockMappingStart
DECL|enum constant|BlockSequenceStart
DECL|enum constant|Directive
DECL|enum constant|DocumentEnd
DECL|enum constant|DocumentStart
DECL|enum constant|FlowEntry
DECL|enum constant|FlowMappingEnd
DECL|enum constant|FlowMappingStart
DECL|enum constant|FlowSequenceEnd
DECL|enum constant|FlowSequenceStart
DECL|enum constant|Key
DECL|enum constant|Scalar
DECL|enum constant|StreamEnd
DECL|enum constant|StreamStart
DECL|enum constant|Tag
DECL|enum constant|Value
name|Alias
block|,
name|Anchor
block|,
name|BlockEnd
block|,
name|BlockEntry
block|,
name|BlockMappingStart
block|,
name|BlockSequenceStart
block|,
name|Directive
block|,
name|DocumentEnd
block|,
name|DocumentStart
block|,
name|FlowEntry
block|,
name|FlowMappingEnd
block|,
name|FlowMappingStart
block|,
name|FlowSequenceEnd
block|,
name|FlowSequenceStart
block|,
name|Key
block|,
name|Scalar
block|,
name|StreamEnd
block|,
name|StreamStart
block|,
name|Tag
block|,
name|Value
block|}
DECL|field|startMark
specifier|private
specifier|final
name|Mark
name|startMark
decl_stmt|;
DECL|field|endMark
specifier|private
specifier|final
name|Mark
name|endMark
decl_stmt|;
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|Mark
name|startMark
parameter_list|,
name|Mark
name|endMark
parameter_list|)
block|{
if|if
condition|(
name|startMark
operator|==
literal|null
operator|||
name|endMark
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YAMLException
argument_list|(
literal|"Token requires marks."
argument_list|)
throw|;
block|}
name|this
operator|.
name|startMark
operator|=
name|startMark
expr_stmt|;
name|this
operator|.
name|endMark
operator|=
name|endMark
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"("
operator|+
name|getArguments
argument_list|()
operator|+
literal|")>"
return|;
block|}
DECL|method|getStartMark
specifier|public
name|Mark
name|getStartMark
parameter_list|()
block|{
return|return
name|startMark
return|;
block|}
DECL|method|getEndMark
specifier|public
name|Mark
name|getEndMark
parameter_list|()
block|{
return|return
name|endMark
return|;
block|}
comment|/**      * @see __repr__ for Token in PyYAML      */
DECL|method|getArguments
specifier|protected
name|String
name|getArguments
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/**      * For error reporting.      *      * @see class variable 'id' in PyYAML      */
DECL|method|getTokenId
specifier|public
specifier|abstract
name|ID
name|getTokenId
parameter_list|()
function_decl|;
comment|/*      * for tests only      */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Token
condition|)
block|{
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|obj
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

