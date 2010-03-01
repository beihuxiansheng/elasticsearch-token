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

begin_class
DECL|class|TagTuple
specifier|public
specifier|final
class|class
name|TagTuple
block|{
DECL|field|handle
specifier|private
specifier|final
name|String
name|handle
decl_stmt|;
DECL|field|suffix
specifier|private
specifier|final
name|String
name|suffix
decl_stmt|;
DECL|method|TagTuple
specifier|public
name|TagTuple
parameter_list|(
name|String
name|handle
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|suffix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Suffix must be provided."
argument_list|)
throw|;
block|}
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|suffix
operator|=
name|suffix
expr_stmt|;
block|}
DECL|method|getHandle
specifier|public
name|String
name|getHandle
parameter_list|()
block|{
return|return
name|handle
return|;
block|}
DECL|method|getSuffix
specifier|public
name|String
name|getSuffix
parameter_list|()
block|{
return|return
name|suffix
return|;
block|}
block|}
end_class

end_unit

