begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|queries
operator|.
name|ExtendedCommonTermsQuery
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
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|MultiPhrasePrefixQuery
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
name|search
operator|.
name|Queries
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
name|mapper
operator|.
name|MappedFieldType
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
name|search
operator|.
name|MatchQuery
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
name|search
operator|.
name|MatchQuery
operator|.
name|ZeroTermsQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Locale
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|either
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
import|;
end_import

begin_class
DECL|class|MatchQueryBuilderTests
specifier|public
class|class
name|MatchQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|MatchQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|MatchQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|String
name|fieldName
init|=
name|randomFrom
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
name|BOOLEAN_FIELD_NAME
argument_list|,
name|INT_FIELD_NAME
argument_list|,
name|DOUBLE_FIELD_NAME
argument_list|,
name|DATE_FIELD_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|DATE_FIELD_NAME
argument_list|)
condition|)
block|{
name|assumeTrue
argument_list|(
literal|"test runs only when at least a type is registered"
argument_list|,
name|getCurrentTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|Object
name|value
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|STRING_FIELD_NAME
argument_list|)
condition|)
block|{
name|int
name|terms
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
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
name|terms
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
name|value
operator|=
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|getRandomValueForFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|MatchQueryBuilder
name|matchQuery
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|type
argument_list|(
name|randomFrom
argument_list|(
name|MatchQuery
operator|.
name|Type
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|operator
argument_list|(
name|randomFrom
argument_list|(
name|Operator
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|matchQuery
operator|.
name|analyzer
argument_list|(
name|randomFrom
argument_list|(
literal|"simple"
argument_list|,
literal|"keyword"
argument_list|,
literal|"whitespace"
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
name|matchQuery
operator|.
name|slop
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
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
name|matchQuery
operator|.
name|fuzziness
argument_list|(
name|randomFuzziness
argument_list|(
name|fieldName
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
name|matchQuery
operator|.
name|prefixLength
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
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
name|matchQuery
operator|.
name|minimumShouldMatch
argument_list|(
name|randomMinimumShouldMatch
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
name|matchQuery
operator|.
name|fuzzyRewrite
argument_list|(
name|getRandomRewriteMethod
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
name|matchQuery
operator|.
name|fuzzyTranspositions
argument_list|(
name|randomBoolean
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
name|matchQuery
operator|.
name|lenient
argument_list|(
name|randomBoolean
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
name|matchQuery
operator|.
name|zeroTermsQuery
argument_list|(
name|randomFrom
argument_list|(
name|MatchQuery
operator|.
name|ZeroTermsQuery
operator|.
name|values
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
name|matchQuery
operator|.
name|cutoffFrequency
argument_list|(
operator|(
name|float
operator|)
literal|10
operator|/
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|matchQuery
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|MatchQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|MatchAllDocsQuery
condition|)
block|{
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|zeroTermsQuery
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ZeroTermsQuery
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
switch|switch
condition|(
name|queryBuilder
operator|.
name|type
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
name|assertThat
argument_list|(
name|query
argument_list|,
name|either
argument_list|(
name|instanceOf
argument_list|(
name|BooleanQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|ExtendedCommonTermsQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|FuzzyQuery
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|PHRASE
case|:
name|assertThat
argument_list|(
name|query
argument_list|,
name|either
argument_list|(
name|instanceOf
argument_list|(
name|BooleanQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|PhraseQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|FuzzyQuery
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|PHRASE_PREFIX
case|:
name|assertThat
argument_list|(
name|query
argument_list|,
name|either
argument_list|(
name|instanceOf
argument_list|(
name|BooleanQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|MultiPhrasePrefixQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|FuzzyQuery
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
operator|&&
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|String
name|queryValue
init|=
name|queryBuilder
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|analyzer
argument_list|()
operator|==
literal|null
operator|||
name|queryBuilder
operator|.
name|analyzer
argument_list|()
operator|.
name|equals
argument_list|(
literal|"simple"
argument_list|)
condition|)
block|{
name|queryValue
operator|=
name|queryValue
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
block|}
name|Query
name|expectedTermQuery
init|=
name|fieldType
operator|.
name|termQuery
argument_list|(
name|queryValue
argument_list|,
name|context
argument_list|)
decl_stmt|;
comment|// the real query will have boost applied, so we set it to our expeced as well
name|expectedTermQuery
operator|.
name|setBoost
argument_list|(
name|queryBuilder
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedTermQuery
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|minimumShouldMatch
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// calculate expected minimumShouldMatch value
name|int
name|optionalClauses
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|bq
operator|.
name|clauses
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getOccur
argument_list|()
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
condition|)
block|{
name|optionalClauses
operator|++
expr_stmt|;
block|}
block|}
name|int
name|msm
init|=
name|Queries
operator|.
name|calculateMinShouldMatch
argument_list|(
name|optionalClauses
argument_list|,
name|queryBuilder
operator|.
name|minimumShouldMatch
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|msm
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|analyzer
argument_list|()
operator|==
literal|null
operator|&&
name|queryBuilder
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|bq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|queryBuilder
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|query
operator|instanceof
name|ExtendedCommonTermsQuery
condition|)
block|{
name|assertTrue
argument_list|(
name|queryBuilder
operator|.
name|cutoffFrequency
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ExtendedCommonTermsQuery
name|ectq
init|=
operator|(
name|ExtendedCommonTermsQuery
operator|)
name|query
decl_stmt|;
name|assertEquals
argument_list|(
name|queryBuilder
operator|.
name|cutoffFrequency
argument_list|()
argument_list|,
name|ectq
operator|.
name|getMaxTermFrequency
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
block|{
name|assertTrue
argument_list|(
name|queryBuilder
operator|.
name|fuzziness
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FuzzyQuery
name|fuzzyQuery
init|=
operator|(
name|FuzzyQuery
operator|)
name|query
decl_stmt|;
comment|// depending on analyzer being set or not we can have term lowercased along the way, so to simplify test we just
comment|// compare lowercased terms here
name|String
name|originalTermLc
init|=
name|queryBuilder
operator|.
name|value
argument_list|()
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
decl_stmt|;
name|String
name|actualTermLc
init|=
name|fuzzyQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
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
decl_stmt|;
name|assertThat
argument_list|(
name|actualTermLc
argument_list|,
name|equalTo
argument_list|(
name|originalTermLc
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|prefixLength
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|fuzzyQuery
operator|.
name|getPrefixLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|fuzzyTranspositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|fuzzyQuery
operator|.
name|getTranspositions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalValues
specifier|public
name|void
name|testIllegalValues
parameter_list|()
block|{
try|try
block|{
operator|new
name|MatchQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"value must not be non-null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
operator|new
name|MatchQueryBuilder
argument_list|(
literal|"fieldName"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"value must not be non-null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|MatchQueryBuilder
name|matchQuery
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
literal|"fieldName"
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
try|try
block|{
name|matchQuery
operator|.
name|prefixLength
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be positive"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|matchQuery
operator|.
name|maxExpansions
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be positive"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|matchQuery
operator|.
name|operator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be non-null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|matchQuery
operator|.
name|type
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be non-null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|matchQuery
operator|.
name|zeroTermsQuery
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be non-null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|QueryShardException
operator|.
name|class
argument_list|)
DECL|method|testBadAnalyzer
specifier|public
name|void
name|testBadAnalyzer
parameter_list|()
throws|throws
name|IOException
block|{
name|MatchQueryBuilder
name|matchQuery
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
literal|"fieldName"
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|analyzer
argument_list|(
literal|"bogusAnalyzer"
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

