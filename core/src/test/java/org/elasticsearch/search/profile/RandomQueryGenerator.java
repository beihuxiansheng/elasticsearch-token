begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
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
name|util
operator|.
name|English
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
name|unit
operator|.
name|Fuzziness
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
name|BoolQueryBuilder
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
name|CommonTermsQueryBuilder
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
name|DisMaxQueryBuilder
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
name|FuzzyQueryBuilder
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
name|IdsQueryBuilder
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
name|Operator
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
name|QueryBuilder
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
name|QueryBuilders
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
name|RangeQueryBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomBoolean
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomFloat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomInt
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomIntBetween
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|RandomQueryGenerator
specifier|public
class|class
name|RandomQueryGenerator
block|{
DECL|method|randomQueryBuilder
specifier|public
specifier|static
name|QueryBuilder
name|randomQueryBuilder
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|numericFields
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Must supply at least one string field"
argument_list|,
name|stringFields
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Must supply at least one numeric field"
argument_list|,
name|numericFields
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// If depth is exhausted, or 50% of the time return a terminal
comment|// Helps limit ridiculously large compound queries
if|if
condition|(
name|depth
operator|==
literal|0
operator|||
name|randomBoolean
argument_list|()
condition|)
block|{
return|return
name|randomTerminalQuery
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|randomTerminalQuery
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|randomBoolQuery
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
argument_list|)
return|;
case|case
literal|3
case|:
comment|// disabled for now because of https://issues.apache.org/jira/browse/LUCENE-6781
comment|//return randomBoostingQuery(stringFields, numericFields, numDocs, depth);
case|case
literal|4
case|:
return|return
name|randomConstantScoreQuery
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
argument_list|)
return|;
case|case
literal|5
case|:
return|return
name|randomDisMaxQuery
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
argument_list|)
return|;
default|default:
return|return
name|randomTerminalQuery
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// fuzzy queries will be removed in 4.0
DECL|method|randomTerminalQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomTerminalQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|numericFields
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|6
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|randomTermQuery
argument_list|(
name|stringFields
argument_list|,
name|numDocs
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|randomTermsQuery
argument_list|(
name|stringFields
argument_list|,
name|numDocs
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|randomRangeQuery
argument_list|(
name|numericFields
argument_list|,
name|numDocs
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
return|;
case|case
literal|4
case|:
return|return
name|randomCommonTermsQuery
argument_list|(
name|stringFields
argument_list|,
name|numDocs
argument_list|)
return|;
case|case
literal|5
case|:
return|return
name|randomFuzzyQuery
argument_list|(
name|stringFields
argument_list|)
return|;
case|case
literal|6
case|:
return|return
name|randomIDsQuery
argument_list|()
return|;
default|default:
return|return
name|randomTermQuery
argument_list|(
name|stringFields
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
block|}
DECL|method|randomQueryString
specifier|private
specifier|static
name|String
name|randomQueryString
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|StringBuilder
name|qsBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|qsBuilder
operator|.
name|append
argument_list|(
name|English
operator|.
name|intToEnglish
argument_list|(
name|randomInt
argument_list|(
name|max
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|qsBuilder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|qsBuilder
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|randomField
specifier|private
specifier|static
name|String
name|randomField
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|randomInt
argument_list|(
name|fields
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomTermQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomTermQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
name|randomField
argument_list|(
name|fields
argument_list|)
argument_list|,
name|randomQueryString
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomTermsQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomTermsQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|int
name|numTerms
init|=
name|randomInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numTerms
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|randomQueryString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|QueryBuilders
operator|.
name|termsQuery
argument_list|(
name|randomField
argument_list|(
name|fields
argument_list|)
argument_list|,
name|terms
argument_list|)
return|;
block|}
DECL|method|randomRangeQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomRangeQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|QueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
name|randomField
argument_list|(
name|fields
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|RangeQueryBuilder
operator|)
name|q
operator|)
operator|.
name|from
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|numDocs
operator|/
literal|2
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|RangeQueryBuilder
operator|)
name|q
operator|)
operator|.
name|to
argument_list|(
name|randomIntBetween
argument_list|(
name|numDocs
operator|/
literal|2
argument_list|,
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
DECL|method|randomBoolQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomBoolQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|numericFields
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|QueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
decl_stmt|;
name|int
name|numClause
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
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
name|numClause
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|BoolQueryBuilder
operator|)
name|q
operator|)
operator|.
name|must
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numClause
operator|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
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
name|numClause
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|BoolQueryBuilder
operator|)
name|q
operator|)
operator|.
name|should
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numClause
operator|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
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
name|numClause
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|BoolQueryBuilder
operator|)
name|q
operator|)
operator|.
name|mustNot
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
DECL|method|randomBoostingQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomBoostingQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|numericFields
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
return|return
name|QueryBuilders
operator|.
name|boostingQuery
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|,
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|boost
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
operator|.
name|negativeBoost
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
return|;
block|}
DECL|method|randomConstantScoreQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomConstantScoreQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|numericFields
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
return|return
name|QueryBuilders
operator|.
name|constantScoreQuery
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomCommonTermsQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomCommonTermsQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|int
name|numTerms
init|=
name|randomInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|QueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|commonTermsQuery
argument_list|(
name|randomField
argument_list|(
name|fields
argument_list|)
argument_list|,
name|randomQueryString
argument_list|(
name|numTerms
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|CommonTermsQueryBuilder
operator|)
name|q
operator|)
operator|.
name|boost
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|CommonTermsQueryBuilder
operator|)
name|q
operator|)
operator|.
name|cutoffFrequency
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|CommonTermsQueryBuilder
operator|)
name|q
operator|)
operator|.
name|highFreqMinimumShouldMatch
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|randomInt
argument_list|(
name|numTerms
argument_list|)
argument_list|)
argument_list|)
operator|.
name|highFreqOperator
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|Operator
operator|.
name|AND
else|:
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|CommonTermsQueryBuilder
operator|)
name|q
operator|)
operator|.
name|lowFreqMinimumShouldMatch
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|randomInt
argument_list|(
name|numTerms
argument_list|)
argument_list|)
argument_list|)
operator|.
name|lowFreqOperator
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|Operator
operator|.
name|AND
else|:
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// fuzzy queries will be removed in 4.0
annotation|@
name|Deprecated
DECL|method|randomFuzzyQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomFuzzyQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
name|QueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|fuzzyQuery
argument_list|(
name|randomField
argument_list|(
name|fields
argument_list|)
argument_list|,
name|randomQueryString
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|boost
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|AUTO
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|ONE
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|TWO
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|ZERO
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|fromEdits
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|AUTO
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|maxExpansions
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|randomInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|prefixLength
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|randomInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|FuzzyQueryBuilder
operator|)
name|q
operator|)
operator|.
name|transpositions
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
DECL|method|randomDisMaxQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomDisMaxQuery
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|numericFields
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|QueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|disMaxQuery
argument_list|()
decl_stmt|;
name|int
name|numClauses
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
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
name|numClauses
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|DisMaxQueryBuilder
operator|)
name|q
operator|)
operator|.
name|add
argument_list|(
name|randomQueryBuilder
argument_list|(
name|stringFields
argument_list|,
name|numericFields
argument_list|,
name|numDocs
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|DisMaxQueryBuilder
operator|)
name|q
operator|)
operator|.
name|boost
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|DisMaxQueryBuilder
operator|)
name|q
operator|)
operator|.
name|tieBreaker
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
DECL|method|randomIDsQuery
specifier|private
specifier|static
name|QueryBuilder
name|randomIDsQuery
parameter_list|()
block|{
name|QueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|idsQuery
argument_list|()
decl_stmt|;
name|int
name|numIDs
init|=
name|randomInt
argument_list|(
literal|100
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
name|numIDs
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|IdsQueryBuilder
operator|)
name|q
operator|)
operator|.
name|addIds
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|randomInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|(
operator|(
name|IdsQueryBuilder
operator|)
name|q
operator|)
operator|.
name|boost
argument_list|(
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
block|}
end_class

end_unit

