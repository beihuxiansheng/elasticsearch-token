begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
comment|/**  * Write Consistency Level control how many replicas should be active for a write operation to occur (a write operation  * can be index, or delete).  *  *  */
end_comment

begin_enum
DECL|enum|WriteConsistencyLevel
specifier|public
enum|enum
name|WriteConsistencyLevel
block|{
DECL|enum constant|DEFAULT
name|DEFAULT
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|ONE
name|ONE
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
DECL|enum constant|QUORUM
name|QUORUM
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|,
DECL|enum constant|ALL
name|ALL
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|;
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
DECL|method|WriteConsistencyLevel
name|WriteConsistencyLevel
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
name|WriteConsistencyLevel
name|fromId
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
return|return
name|DEFAULT
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
return|return
name|ONE
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|2
condition|)
block|{
return|return
name|QUORUM
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|3
condition|)
block|{
return|return
name|ALL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No write consistency match ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|WriteConsistencyLevel
name|fromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
return|return
name|DEFAULT
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"one"
argument_list|)
condition|)
block|{
return|return
name|ONE
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"quorum"
argument_list|)
condition|)
block|{
return|return
name|QUORUM
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
condition|)
block|{
return|return
name|ALL
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No write consistency match ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

