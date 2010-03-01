begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.error
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
name|error
package|;
end_package

begin_comment
comment|/**  * @see<a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information  */
end_comment

begin_class
DECL|class|MarkedYAMLException
specifier|public
class|class
name|MarkedYAMLException
extends|extends
name|YAMLException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|9119388488683035101L
decl_stmt|;
DECL|field|context
specifier|private
name|String
name|context
decl_stmt|;
DECL|field|contextMark
specifier|private
name|Mark
name|contextMark
decl_stmt|;
DECL|field|problem
specifier|private
name|String
name|problem
decl_stmt|;
DECL|field|problemMark
specifier|private
name|Mark
name|problemMark
decl_stmt|;
DECL|field|note
specifier|private
name|String
name|note
decl_stmt|;
DECL|method|MarkedYAMLException
specifier|protected
name|MarkedYAMLException
parameter_list|(
name|String
name|context
parameter_list|,
name|Mark
name|contextMark
parameter_list|,
name|String
name|problem
parameter_list|,
name|Mark
name|problemMark
parameter_list|,
name|String
name|note
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|contextMark
argument_list|,
name|problem
argument_list|,
name|problemMark
argument_list|,
name|note
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|MarkedYAMLException
specifier|protected
name|MarkedYAMLException
parameter_list|(
name|String
name|context
parameter_list|,
name|Mark
name|contextMark
parameter_list|,
name|String
name|problem
parameter_list|,
name|Mark
name|problemMark
parameter_list|,
name|String
name|note
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|context
operator|+
literal|"; "
operator|+
name|problem
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|contextMark
operator|=
name|contextMark
expr_stmt|;
name|this
operator|.
name|problem
operator|=
name|problem
expr_stmt|;
name|this
operator|.
name|problemMark
operator|=
name|problemMark
expr_stmt|;
name|this
operator|.
name|note
operator|=
name|note
expr_stmt|;
block|}
DECL|method|MarkedYAMLException
specifier|protected
name|MarkedYAMLException
parameter_list|(
name|String
name|context
parameter_list|,
name|Mark
name|contextMark
parameter_list|,
name|String
name|problem
parameter_list|,
name|Mark
name|problemMark
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|contextMark
argument_list|,
name|problem
argument_list|,
name|problemMark
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|MarkedYAMLException
specifier|protected
name|MarkedYAMLException
parameter_list|(
name|String
name|context
parameter_list|,
name|Mark
name|contextMark
parameter_list|,
name|String
name|problem
parameter_list|,
name|Mark
name|problemMark
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|contextMark
argument_list|,
name|problem
argument_list|,
name|problemMark
argument_list|,
literal|null
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|lines
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|append
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|lines
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextMark
operator|!=
literal|null
operator|&&
operator|(
name|problem
operator|==
literal|null
operator|||
name|problemMark
operator|==
literal|null
operator|||
operator|(
name|contextMark
operator|.
name|getName
argument_list|()
operator|!=
name|problemMark
operator|.
name|getName
argument_list|()
operator|)
operator|||
operator|(
name|contextMark
operator|.
name|getLine
argument_list|()
operator|!=
name|problemMark
operator|.
name|getLine
argument_list|()
operator|)
operator|||
operator|(
name|contextMark
operator|.
name|getColumn
argument_list|()
operator|!=
name|problemMark
operator|.
name|getColumn
argument_list|()
operator|)
operator|)
condition|)
block|{
name|lines
operator|.
name|append
argument_list|(
name|contextMark
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lines
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|problem
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|append
argument_list|(
name|problem
argument_list|)
expr_stmt|;
name|lines
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|problemMark
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|append
argument_list|(
name|problemMark
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lines
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|note
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|append
argument_list|(
name|note
argument_list|)
expr_stmt|;
name|lines
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|lines
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getContext
specifier|public
name|String
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|getContextMark
specifier|public
name|Mark
name|getContextMark
parameter_list|()
block|{
return|return
name|contextMark
return|;
block|}
DECL|method|getProblem
specifier|public
name|String
name|getProblem
parameter_list|()
block|{
return|return
name|problem
return|;
block|}
DECL|method|getProblemMark
specifier|public
name|Mark
name|getProblemMark
parameter_list|()
block|{
return|return
name|problemMark
return|;
block|}
block|}
end_class

end_unit

