begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore.exp
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
operator|.
name|exp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
operator|.
name|DecayFunctionParser
import|;
end_import

begin_class
DECL|class|ExponentialDecayFunctionParser
specifier|public
class|class
name|ExponentialDecayFunctionParser
extends|extends
name|DecayFunctionParser
argument_list|<
name|ExponentialDecayFunctionBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|private
specifier|static
specifier|final
name|ExponentialDecayFunctionBuilder
name|PROTOTYPE
init|=
operator|new
name|ExponentialDecayFunctionBuilder
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
DECL|field|NAMES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|NAMES
init|=
block|{
literal|"exp"
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getNames
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
block|{
return|return
name|NAMES
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|ExponentialDecayFunctionBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

