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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ComplexExplanation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import

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
name|DecayFunction
import|;
end_import

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
block|{
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
DECL|field|decayFunction
specifier|static
specifier|final
name|DecayFunction
name|decayFunction
init|=
operator|new
name|ExponentialDecayScoreFunction
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getDecayFunction
specifier|public
name|DecayFunction
name|getDecayFunction
parameter_list|()
block|{
return|return
name|decayFunction
return|;
block|}
DECL|class|ExponentialDecayScoreFunction
specifier|final
specifier|static
class|class
name|ExponentialDecayScoreFunction
implements|implements
name|DecayFunction
block|{
annotation|@
name|Override
DECL|method|evaluate
specifier|public
name|double
name|evaluate
parameter_list|(
name|double
name|value
parameter_list|,
name|double
name|scale
parameter_list|)
block|{
return|return
name|Math
operator|.
name|exp
argument_list|(
name|scale
operator|*
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explainFunction
specifier|public
name|Explanation
name|explainFunction
parameter_list|(
name|String
name|valueExpl
parameter_list|,
name|double
name|value
parameter_list|,
name|double
name|scale
parameter_list|)
block|{
name|ComplexExplanation
name|ce
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|ce
operator|.
name|setValue
argument_list|(
operator|(
name|float
operator|)
name|evaluate
argument_list|(
name|value
argument_list|,
name|scale
argument_list|)
argument_list|)
expr_stmt|;
name|ce
operator|.
name|setDescription
argument_list|(
literal|"exp(- "
operator|+
name|valueExpl
operator|+
literal|" * "
operator|+
operator|-
literal|1
operator|*
name|scale
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|ce
return|;
block|}
annotation|@
name|Override
DECL|method|processScale
specifier|public
name|double
name|processScale
parameter_list|(
name|double
name|scale
parameter_list|,
name|double
name|decay
parameter_list|)
block|{
return|return
name|Math
operator|.
name|log
argument_list|(
name|decay
argument_list|)
operator|/
name|scale
return|;
block|}
block|}
block|}
end_class

end_unit

