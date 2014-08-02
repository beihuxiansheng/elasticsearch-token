begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search.function
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|index
operator|.
name|AtomicReaderContext
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
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|WeightFactorFunction
specifier|public
class|class
name|WeightFactorFunction
extends|extends
name|ScoreFunction
block|{
DECL|field|SCORE_ONE
specifier|private
specifier|static
specifier|final
name|ScoreFunction
name|SCORE_ONE
init|=
operator|new
name|ScoreOne
argument_list|(
name|CombineFunction
operator|.
name|MULT
argument_list|)
decl_stmt|;
DECL|field|scoreFunction
specifier|private
specifier|final
name|ScoreFunction
name|scoreFunction
decl_stmt|;
DECL|field|weight
specifier|private
name|float
name|weight
init|=
literal|1.0f
decl_stmt|;
DECL|method|WeightFactorFunction
specifier|public
name|WeightFactorFunction
parameter_list|(
name|float
name|weight
parameter_list|,
name|ScoreFunction
name|scoreFunction
parameter_list|)
block|{
name|super
argument_list|(
name|CombineFunction
operator|.
name|MULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|scoreFunction
operator|instanceof
name|BoostScoreFunction
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
name|BoostScoreFunction
operator|.
name|BOOST_WEIGHT_ERROR_MESSAGE
argument_list|)
throw|;
block|}
if|if
condition|(
name|scoreFunction
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|scoreFunction
operator|=
name|SCORE_ONE
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|scoreFunction
operator|=
name|scoreFunction
expr_stmt|;
block|}
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
DECL|method|WeightFactorFunction
specifier|public
name|WeightFactorFunction
parameter_list|(
name|float
name|weight
parameter_list|)
block|{
name|super
argument_list|(
name|CombineFunction
operator|.
name|MULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|scoreFunction
operator|=
name|SCORE_ONE
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|scoreFunction
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|double
name|score
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|subQueryScore
parameter_list|)
block|{
return|return
name|scoreFunction
operator|.
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryScore
argument_list|)
operator|*
name|getWeight
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|explainScore
specifier|public
name|Explanation
name|explainScore
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|Explanation
name|functionScoreExplanation
decl_stmt|;
name|Explanation
name|functionExplanation
init|=
name|scoreFunction
operator|.
name|explainScore
argument_list|(
name|docId
argument_list|,
name|score
argument_list|)
decl_stmt|;
name|functionScoreExplanation
operator|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|functionExplanation
operator|.
name|getValue
argument_list|()
operator|*
operator|(
name|float
operator|)
name|getWeight
argument_list|()
argument_list|,
literal|"product of:"
argument_list|)
expr_stmt|;
name|functionScoreExplanation
operator|.
name|addDetail
argument_list|(
name|functionExplanation
argument_list|)
expr_stmt|;
name|functionScoreExplanation
operator|.
name|addDetail
argument_list|(
name|explainWeight
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|functionScoreExplanation
return|;
block|}
DECL|method|explainWeight
specifier|public
name|Explanation
name|explainWeight
parameter_list|()
block|{
return|return
operator|new
name|Explanation
argument_list|(
name|getWeight
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
return|;
block|}
DECL|method|getWeight
specifier|public
name|float
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
DECL|class|ScoreOne
specifier|private
specifier|static
class|class
name|ScoreOne
extends|extends
name|ScoreFunction
block|{
DECL|method|ScoreOne
specifier|protected
name|ScoreOne
parameter_list|(
name|CombineFunction
name|scoreCombiner
parameter_list|)
block|{
name|super
argument_list|(
name|scoreCombiner
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{          }
annotation|@
name|Override
DECL|method|score
specifier|public
name|double
name|score
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|subQueryScore
parameter_list|)
block|{
return|return
literal|1.0
return|;
block|}
annotation|@
name|Override
DECL|method|explainScore
specifier|public
name|Explanation
name|explainScore
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|subQueryScore
parameter_list|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|1.0f
argument_list|,
literal|"constant score 1.0 - no function provided"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

