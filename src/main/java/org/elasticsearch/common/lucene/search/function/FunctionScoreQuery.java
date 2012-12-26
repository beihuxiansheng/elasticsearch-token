begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|*
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|ToStringUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A query that allows for a pluggable boost function to be applied to it.  */
end_comment

begin_class
DECL|class|FunctionScoreQuery
specifier|public
class|class
name|FunctionScoreQuery
extends|extends
name|Query
block|{
DECL|field|subQuery
name|Query
name|subQuery
decl_stmt|;
DECL|field|function
specifier|final
name|ScoreFunction
name|function
decl_stmt|;
DECL|method|FunctionScoreQuery
specifier|public
name|FunctionScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|ScoreFunction
name|function
parameter_list|)
block|{
name|this
operator|.
name|subQuery
operator|=
name|subQuery
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
DECL|method|getSubQuery
specifier|public
name|Query
name|getSubQuery
parameter_list|()
block|{
return|return
name|subQuery
return|;
block|}
DECL|method|getFunction
specifier|public
name|ScoreFunction
name|getFunction
parameter_list|()
block|{
return|return
name|function
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|newQ
init|=
name|subQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newQ
operator|==
name|subQuery
condition|)
return|return
name|this
return|;
name|FunctionScoreQuery
name|bq
init|=
operator|(
name|FunctionScoreQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
decl_stmt|;
name|bq
operator|.
name|subQuery
operator|=
name|newQ
expr_stmt|;
return|return
name|bq
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|subQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|Weight
name|subQueryWeight
init|=
name|subQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|CustomBoostFactorWeight
argument_list|(
name|subQueryWeight
argument_list|)
return|;
block|}
DECL|class|CustomBoostFactorWeight
class|class
name|CustomBoostFactorWeight
extends|extends
name|Weight
block|{
DECL|field|subQueryWeight
specifier|final
name|Weight
name|subQueryWeight
decl_stmt|;
DECL|method|CustomBoostFactorWeight
specifier|public
name|CustomBoostFactorWeight
parameter_list|(
name|Weight
name|subQueryWeight
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|subQueryWeight
operator|=
name|subQueryWeight
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|FunctionScoreQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
name|subQueryWeight
operator|.
name|getValueForNormalization
argument_list|()
decl_stmt|;
name|sum
operator|*=
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|subQueryWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
operator|*
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|subQueryScorer
init|=
name|subQueryWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|scoreDocsInOrder
argument_list|,
literal|false
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|subQueryScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|function
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|CustomBoostFactorScorer
argument_list|(
name|this
argument_list|,
name|subQueryScorer
argument_list|,
name|function
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|subQueryExpl
init|=
name|subQueryWeight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subQueryExpl
operator|.
name|isMatch
argument_list|()
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
name|function
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Explanation
name|functionExplanation
init|=
name|function
operator|.
name|explainScore
argument_list|(
name|doc
argument_list|,
name|subQueryExpl
argument_list|)
decl_stmt|;
name|float
name|sc
init|=
name|getBoost
argument_list|()
operator|*
name|functionExplanation
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Explanation
name|res
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|sc
argument_list|,
literal|"custom score, product of:"
argument_list|)
decl_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
name|functionExplanation
argument_list|)
expr_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"queryBoost"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
DECL|class|CustomBoostFactorScorer
specifier|static
class|class
name|CustomBoostFactorScorer
extends|extends
name|Scorer
block|{
DECL|field|subQueryBoost
specifier|private
specifier|final
name|float
name|subQueryBoost
decl_stmt|;
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|function
specifier|private
specifier|final
name|ScoreFunction
name|function
decl_stmt|;
DECL|method|CustomBoostFactorScorer
specifier|private
name|CustomBoostFactorScorer
parameter_list|(
name|CustomBoostFactorWeight
name|w
parameter_list|,
name|Scorer
name|scorer
parameter_list|,
name|ScoreFunction
name|function
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|subQueryBoost
operator|=
name|w
operator|.
name|getQuery
argument_list|()
operator|.
name|getBoost
argument_list|()
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|subQueryBoost
operator|*
name|function
operator|.
name|score
argument_list|(
name|scorer
operator|.
name|docID
argument_list|()
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|freq
argument_list|()
return|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"custom score ("
argument_list|)
operator|.
name|append
argument_list|(
name|subQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|",function="
argument_list|)
operator|.
name|append
argument_list|(
name|function
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FunctionScoreQuery
name|other
init|=
operator|(
name|FunctionScoreQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|this
operator|.
name|subQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|subQuery
argument_list|)
operator|&&
name|this
operator|.
name|function
operator|.
name|equals
argument_list|(
name|other
operator|.
name|function
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|subQuery
operator|.
name|hashCode
argument_list|()
operator|+
literal|31
operator|*
name|function
operator|.
name|hashCode
argument_list|()
operator|^
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

