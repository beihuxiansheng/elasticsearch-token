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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|docset
operator|.
name|DocIdSets
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
name|*
import|;
end_import

begin_comment
comment|/**  * A query that allows for a pluggable boost function / filter. If it matches  * the filter, it will be boosted by the formula.  */
end_comment

begin_class
DECL|class|FiltersFunctionScoreQuery
specifier|public
class|class
name|FiltersFunctionScoreQuery
extends|extends
name|Query
block|{
DECL|class|FilterFunction
specifier|public
specifier|static
class|class
name|FilterFunction
block|{
DECL|field|filter
specifier|public
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|function
specifier|public
specifier|final
name|ScoreFunction
name|function
decl_stmt|;
DECL|method|FilterFunction
specifier|public
name|FilterFunction
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|ScoreFunction
name|function
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
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
name|FilterFunction
name|that
init|=
operator|(
name|FilterFunction
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|?
operator|!
name|filter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|filter
argument_list|)
else|:
name|that
operator|.
name|filter
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|function
operator|!=
literal|null
condition|?
operator|!
name|function
operator|.
name|equals
argument_list|(
name|that
operator|.
name|function
argument_list|)
else|:
name|that
operator|.
name|function
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|filter
operator|!=
literal|null
condition|?
name|filter
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|function
operator|!=
literal|null
condition|?
name|function
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|enum|ScoreMode
specifier|public
specifier|static
enum|enum
name|ScoreMode
block|{
DECL|enum constant|First
DECL|enum constant|Avg
DECL|enum constant|Max
DECL|enum constant|Sum
DECL|enum constant|Min
DECL|enum constant|Multiply
name|First
block|,
name|Avg
block|,
name|Max
block|,
name|Sum
block|,
name|Min
block|,
name|Multiply
block|}
DECL|field|subQuery
name|Query
name|subQuery
decl_stmt|;
DECL|field|filterFunctions
specifier|final
name|FilterFunction
index|[]
name|filterFunctions
decl_stmt|;
DECL|field|scoreMode
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|field|maxBoost
specifier|final
name|float
name|maxBoost
decl_stmt|;
DECL|field|combineFunction
specifier|protected
name|CombineFunction
name|combineFunction
decl_stmt|;
DECL|method|FiltersFunctionScoreQuery
specifier|public
name|FiltersFunctionScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|,
name|FilterFunction
index|[]
name|filterFunctions
parameter_list|,
name|float
name|maxBoost
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
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
name|this
operator|.
name|filterFunctions
operator|=
name|filterFunctions
expr_stmt|;
name|this
operator|.
name|maxBoost
operator|=
name|maxBoost
expr_stmt|;
name|combineFunction
operator|=
name|CombineFunction
operator|.
name|MULT
expr_stmt|;
block|}
DECL|method|setCombineFunction
specifier|public
name|FiltersFunctionScoreQuery
name|setCombineFunction
parameter_list|(
name|CombineFunction
name|combineFunction
parameter_list|)
block|{
name|this
operator|.
name|combineFunction
operator|=
name|combineFunction
expr_stmt|;
return|return
name|this
return|;
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
DECL|method|getFilterFunctions
specifier|public
name|FilterFunction
index|[]
name|getFilterFunctions
parameter_list|()
block|{
return|return
name|filterFunctions
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
name|FiltersFunctionScoreQuery
name|bq
init|=
operator|(
name|FiltersFunctionScoreQuery
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
argument_list|,
name|filterFunctions
operator|.
name|length
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
DECL|field|docSets
specifier|final
name|Bits
index|[]
name|docSets
decl_stmt|;
DECL|method|CustomBoostFactorWeight
specifier|public
name|CustomBoostFactorWeight
parameter_list|(
name|Weight
name|subQueryWeight
parameter_list|,
name|int
name|filterFunctionLength
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
name|this
operator|.
name|docSets
operator|=
operator|new
name|Bits
index|[
name|filterFunctionLength
index|]
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|FiltersFunctionScoreQuery
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
comment|// we ignore scoreDocsInOrder parameter, because we need to score in
comment|// order if documents are scored with a script. The
comment|// ShardLookup depends on in order scoring.
name|Scorer
name|subQueryScorer
init|=
name|subQueryWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FilterFunction
name|filterFunction
init|=
name|filterFunctions
index|[
name|i
index|]
decl_stmt|;
name|filterFunction
operator|.
name|function
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|docSets
index|[
name|i
index|]
operator|=
name|DocIdSets
operator|.
name|toSafeBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|filterFunction
operator|.
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CustomBoostFactorScorer
argument_list|(
name|this
argument_list|,
name|subQueryScorer
argument_list|,
name|scoreMode
argument_list|,
name|filterFunctions
argument_list|,
name|maxBoost
argument_list|,
name|docSets
argument_list|,
name|combineFunction
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
comment|// First: Gather explanations for all filters
name|List
argument_list|<
name|ComplexExplanation
argument_list|>
name|filterExplanations
init|=
operator|new
name|ArrayList
argument_list|<
name|ComplexExplanation
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FilterFunction
name|filterFunction
range|:
name|filterFunctions
control|)
block|{
name|Bits
name|docSet
init|=
name|DocIdSets
operator|.
name|toSafeBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|filterFunction
operator|.
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|docSet
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|filterFunction
operator|.
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
name|filterFunction
operator|.
name|function
operator|.
name|explainScore
argument_list|(
name|doc
argument_list|,
name|subQueryExpl
argument_list|)
decl_stmt|;
name|double
name|factor
init|=
name|functionExplanation
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|float
name|sc
init|=
name|CombineFunction
operator|.
name|toFloat
argument_list|(
name|factor
argument_list|)
decl_stmt|;
name|ComplexExplanation
name|filterExplanation
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|sc
argument_list|,
literal|"function score, product of:"
argument_list|)
decl_stmt|;
name|filterExplanation
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
literal|1.0f
argument_list|,
literal|"match filter: "
operator|+
name|filterFunction
operator|.
name|filter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|filterExplanation
operator|.
name|addDetail
argument_list|(
name|functionExplanation
argument_list|)
expr_stmt|;
name|filterExplanations
operator|.
name|add
argument_list|(
name|filterExplanation
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|filterExplanations
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|float
name|sc
init|=
name|getBoost
argument_list|()
operator|*
name|subQueryExpl
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
literal|"function score, no filter match, product of:"
argument_list|)
decl_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
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
comment|// Second: Compute the factor that would have been computed by the
comment|// filters
name|double
name|factor
init|=
literal|1.0
decl_stmt|;
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|First
case|:
name|factor
operator|=
name|filterExplanations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|Max
case|:
name|factor
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterExplanations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|factor
operator|=
name|Math
operator|.
name|max
argument_list|(
name|filterExplanations
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Min
case|:
name|factor
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterExplanations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|factor
operator|=
name|Math
operator|.
name|min
argument_list|(
name|filterExplanations
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Multiply
case|:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterExplanations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|factor
operator|*=
name|filterExplanations
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
break|break;
default|default:
comment|// Avg / Total
name|double
name|totalFactor
init|=
literal|0.0f
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterExplanations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|totalFactor
operator|+=
name|filterExplanations
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
name|factor
operator|=
name|totalFactor
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|Avg
condition|)
block|{
name|factor
operator|/=
name|count
expr_stmt|;
block|}
block|}
block|}
name|ComplexExplanation
name|factorExplanaition
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|CombineFunction
operator|.
name|toFloat
argument_list|(
name|factor
argument_list|)
argument_list|,
literal|"function score, score mode ["
operator|+
name|scoreMode
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"]"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterExplanations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|factorExplanaition
operator|.
name|addDetail
argument_list|(
name|filterExplanations
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|combineFunction
operator|.
name|explain
argument_list|(
name|getBoost
argument_list|()
argument_list|,
name|subQueryExpl
argument_list|,
name|factorExplanaition
argument_list|,
name|maxBoost
argument_list|)
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
DECL|field|filterFunctions
specifier|private
specifier|final
name|FilterFunction
index|[]
name|filterFunctions
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|field|maxBoost
specifier|private
specifier|final
name|float
name|maxBoost
decl_stmt|;
DECL|field|docSets
specifier|private
specifier|final
name|Bits
index|[]
name|docSets
decl_stmt|;
DECL|field|scoreCombiner
specifier|private
specifier|final
name|CombineFunction
name|scoreCombiner
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
name|ScoreMode
name|scoreMode
parameter_list|,
name|FilterFunction
index|[]
name|filterFunctions
parameter_list|,
name|float
name|maxBoost
parameter_list|,
name|Bits
index|[]
name|docSets
parameter_list|,
name|CombineFunction
name|scoreCombiner
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
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
name|this
operator|.
name|filterFunctions
operator|=
name|filterFunctions
expr_stmt|;
name|this
operator|.
name|maxBoost
operator|=
name|maxBoost
expr_stmt|;
name|this
operator|.
name|docSets
operator|=
name|docSets
expr_stmt|;
name|this
operator|.
name|scoreCombiner
operator|=
name|scoreCombiner
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
name|int
name|docId
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
name|double
name|factor
init|=
literal|1.0f
decl_stmt|;
name|float
name|subQueryScore
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|First
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docSets
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|factor
operator|=
name|filterFunctions
index|[
name|i
index|]
operator|.
name|function
operator|.
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryScore
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|Max
condition|)
block|{
name|double
name|maxFactor
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docSets
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|maxFactor
operator|=
name|Math
operator|.
name|max
argument_list|(
name|filterFunctions
index|[
name|i
index|]
operator|.
name|function
operator|.
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryScore
argument_list|)
argument_list|,
name|maxFactor
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxFactor
operator|!=
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
name|factor
operator|=
name|maxFactor
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|Min
condition|)
block|{
name|double
name|minFactor
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docSets
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|minFactor
operator|=
name|Math
operator|.
name|min
argument_list|(
name|filterFunctions
index|[
name|i
index|]
operator|.
name|function
operator|.
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryScore
argument_list|)
argument_list|,
name|minFactor
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minFactor
operator|!=
name|Float
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
name|factor
operator|=
name|minFactor
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|Multiply
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docSets
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|factor
operator|*=
name|filterFunctions
index|[
name|i
index|]
operator|.
name|function
operator|.
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryScore
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Avg / Total
name|double
name|totalFactor
init|=
literal|0.0f
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docSets
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|totalFactor
operator|+=
name|filterFunctions
index|[
name|i
index|]
operator|.
name|function
operator|.
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryScore
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
name|factor
operator|=
name|totalFactor
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|Avg
condition|)
block|{
name|factor
operator|/=
name|count
expr_stmt|;
block|}
block|}
block|}
return|return
name|scoreCombiner
operator|.
name|combine
argument_list|(
name|subQueryBoost
argument_list|,
name|subQueryScore
argument_list|,
name|factor
argument_list|,
name|maxBoost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
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
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|cost
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
literal|"function score ("
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
literal|", functions: ["
argument_list|)
expr_stmt|;
for|for
control|(
name|FilterFunction
name|filterFunction
range|:
name|filterFunctions
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{filter("
argument_list|)
operator|.
name|append
argument_list|(
name|filterFunction
operator|.
name|filter
argument_list|)
operator|.
name|append
argument_list|(
literal|"), function ["
argument_list|)
operator|.
name|append
argument_list|(
name|filterFunction
operator|.
name|function
argument_list|)
operator|.
name|append
argument_list|(
literal|"]}"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"])"
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
name|o
operator|==
literal|null
operator|||
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
name|FiltersFunctionScoreQuery
name|other
init|=
operator|(
name|FiltersFunctionScoreQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getBoost
argument_list|()
operator|!=
name|other
operator|.
name|getBoost
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
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
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|filterFunctions
argument_list|,
name|other
operator|.
name|filterFunctions
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|filterFunctions
argument_list|)
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

