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
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|ParseFieldMatcher
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentParser
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|greaterThan
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
name|is
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
DECL|class|SimpleQueryStringBuilderTests
specifier|public
class|class
name|SimpleQueryStringBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|SimpleQueryStringBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|SimpleQueryStringBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|result
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|result
operator|.
name|analyzeWildcard
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
name|result
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
name|result
operator|.
name|lowercaseExpandedTerms
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
name|result
operator|.
name|locale
argument_list|(
name|randomLocale
argument_list|(
name|getRandom
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
name|result
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
name|result
operator|.
name|analyzer
argument_list|(
name|randomAnalyzer
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
name|result
operator|.
name|defaultOperator
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
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|SimpleQueryStringFlag
argument_list|>
name|flagSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|SimpleQueryStringFlag
operator|.
name|values
argument_list|()
operator|.
name|length
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|flagSet
operator|.
name|add
argument_list|(
name|randomFrom
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|flagSet
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|flags
argument_list|(
name|flagSet
operator|.
name|toArray
argument_list|(
operator|new
name|SimpleQueryStringFlag
index|[
name|flagSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|fieldCount
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|.
name|put
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|STRING_FIELD_NAME
else|:
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|2.0f
operator|/
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"The quick brown fox."
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default boost."
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|,
name|qb
operator|.
name|boost
argument_list|()
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default boost field."
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_BOOST
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default flags."
argument_list|,
name|SimpleQueryStringFlag
operator|.
name|ALL
operator|.
name|value
argument_list|,
name|qb
operator|.
name|flags
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default flags field."
argument_list|,
name|SimpleQueryStringFlag
operator|.
name|ALL
operator|.
name|value
argument_list|()
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_FLAGS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default operator."
argument_list|,
name|Operator
operator|.
name|OR
argument_list|,
name|qb
operator|.
name|defaultOperator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default operator field."
argument_list|,
name|Operator
operator|.
name|OR
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_OPERATOR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default locale."
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|,
name|qb
operator|.
name|locale
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default locale field."
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_LOCALE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default analyze_wildcard."
argument_list|,
literal|false
argument_list|,
name|qb
operator|.
name|analyzeWildcard
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default analyze_wildcard field."
argument_list|,
literal|false
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_ANALYZE_WILDCARD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default lowercase_expanded_terms."
argument_list|,
literal|true
argument_list|,
name|qb
operator|.
name|lowercaseExpandedTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default lowercase_expanded_terms field."
argument_list|,
literal|true
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_LOWERCASE_EXPANDED_TERMS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default lenient."
argument_list|,
literal|false
argument_list|,
name|qb
operator|.
name|lenient
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default lenient field."
argument_list|,
literal|false
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_LENIENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default locale."
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|,
name|qb
operator|.
name|locale
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default default locale field."
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_LOCALE
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultNullLocale
specifier|public
name|void
name|testDefaultNullLocale
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"The quick brown fox."
argument_list|)
decl_stmt|;
name|qb
operator|.
name|locale
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Setting locale to null should result in returning to default value."
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_LOCALE
argument_list|,
name|qb
operator|.
name|locale
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultNullComplainFlags
specifier|public
name|void
name|testDefaultNullComplainFlags
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"The quick brown fox."
argument_list|)
decl_stmt|;
name|qb
operator|.
name|flags
argument_list|(
operator|(
name|SimpleQueryStringFlag
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Setting flags to null should result in returning to default value."
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_FLAGS
argument_list|,
name|qb
operator|.
name|flags
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultEmptyComplainFlags
specifier|public
name|void
name|testDefaultEmptyComplainFlags
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"The quick brown fox."
argument_list|)
decl_stmt|;
name|qb
operator|.
name|flags
argument_list|(
operator|new
name|SimpleQueryStringFlag
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Setting flags to empty should result in returning to default value."
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_FLAGS
argument_list|,
name|qb
operator|.
name|flags
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultNullComplainOp
specifier|public
name|void
name|testDefaultNullComplainOp
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"The quick brown fox."
argument_list|)
decl_stmt|;
name|qb
operator|.
name|defaultOperator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Setting operator to null should result in returning to default value."
argument_list|,
name|SimpleQueryStringBuilder
operator|.
name|DEFAULT_OPERATOR
argument_list|,
name|qb
operator|.
name|defaultOperator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check operator handling, and default field handling.
DECL|method|testDefaultOperatorHandling
specifier|public
name|void
name|testDefaultOperatorHandling
parameter_list|()
throws|throws
name|IOException
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"The quick brown fox."
argument_list|)
operator|.
name|field
argument_list|(
name|STRING_FIELD_NAME
argument_list|)
decl_stmt|;
name|QueryShardContext
name|shardContext
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|shardContext
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// to avoid occasional cases
comment|// in setup where we didn't
comment|// add types but strict field
comment|// resolution
name|BooleanQuery
name|boolQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|qb
operator|.
name|toQuery
argument_list|(
name|shardContext
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shouldClauses
argument_list|(
name|boolQuery
argument_list|)
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|qb
operator|.
name|defaultOperator
argument_list|(
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
name|boolQuery
operator|=
operator|(
name|BooleanQuery
operator|)
name|qb
operator|.
name|toQuery
argument_list|(
name|shardContext
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shouldClauses
argument_list|(
name|boolQuery
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|qb
operator|.
name|defaultOperator
argument_list|(
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
name|boolQuery
operator|=
operator|(
name|BooleanQuery
operator|)
name|qb
operator|.
name|toQuery
argument_list|(
name|shardContext
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shouldClauses
argument_list|(
name|boolQuery
argument_list|)
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalConstructorArg
specifier|public
name|void
name|testIllegalConstructorArg
parameter_list|()
block|{
try|try
block|{
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testFieldCannotBeNull
specifier|public
name|void
name|testFieldCannotBeNull
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|qb
operator|.
name|field
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"supplied field is null or empty."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFieldCannotBeNullAndWeighted
specifier|public
name|void
name|testFieldCannotBeNullAndWeighted
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|qb
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"supplied field is null or empty."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFieldCannotBeEmpty
specifier|public
name|void
name|testFieldCannotBeEmpty
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|qb
operator|.
name|field
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"supplied field is null or empty."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFieldCannotBeEmptyAndWeighted
specifier|public
name|void
name|testFieldCannotBeEmptyAndWeighted
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|qb
operator|.
name|field
argument_list|(
literal|""
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"supplied field is null or empty."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * The following should fail fast - never silently set the map containing      * fields and weights to null but refuse to accept null instead.      * */
DECL|method|testFieldsCannotBeSetToNull
specifier|public
name|void
name|testFieldsCannotBeSetToNull
parameter_list|()
block|{
name|SimpleQueryStringBuilder
name|qb
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|qb
operator|.
name|fields
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected NullPointerException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"fields cannot be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDefaultFieldParsing
specifier|public
name|void
name|testDefaultFieldParsing
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryParseContext
name|context
init|=
name|createParseContext
argument_list|()
decl_stmt|;
name|String
name|query
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|contentString
init|=
literal|"{\n"
operator|+
literal|"    \"simple_query_string\" : {\n"
operator|+
literal|"      \"query\" : \""
operator|+
name|query
operator|+
literal|"\""
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentString
argument_list|)
operator|.
name|createParser
argument_list|(
name|contentString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|SimpleQueryStringBuilder
name|queryBuilder
init|=
operator|(
name|SimpleQueryStringBuilder
operator|)
name|parseQuery
argument_list|(
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|fields
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|QueryShardContext
name|shardContext
init|=
name|createShardContext
argument_list|()
decl_stmt|;
comment|// the remaining tests requires either a mapping that we register with types in base test setup
comment|// no strict field resolution (version before V_1_4_0_Beta1)
if|if
condition|(
name|getCurrentTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|||
name|shardContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_4_0_Beta1
argument_list|)
condition|)
block|{
name|Query
name|luceneQuery
init|=
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|shardContext
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|luceneQuery
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|TermQuery
name|termQuery
init|=
operator|(
name|TermQuery
operator|)
name|luceneQuery
decl_stmt|;
name|assertThat
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|Term
argument_list|(
name|MetaData
operator|.
name|ALL
argument_list|,
name|query
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * This assumes that Lucene query parsing is being checked already, adding      * checks only for our parsing extensions.      *      * Also this relies on {@link SimpleQueryStringTests} to test most of the      * actual functionality of query parsing.      */
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|SimpleQueryStringBuilder
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
literal|""
operator|.
name|equals
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|MatchNoDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|BooleanQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|boolQuery
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
name|lowercaseExpandedTerms
argument_list|()
condition|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
name|boolQuery
operator|.
name|clauses
argument_list|()
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|TermQuery
condition|)
block|{
name|TermQuery
name|inner
init|=
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|inner
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|inner
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertThat
argument_list|(
name|boolQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|>
name|fieldsIterator
init|=
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|booleanClause
range|:
name|boolQuery
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|field
init|=
name|fieldsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTermOrBoostQuery
argument_list|(
name|booleanClause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|assertThat
argument_list|(
name|boolQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|field
init|=
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTermOrBoostQuery
argument_list|(
name|query
argument_list|,
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryBuilder
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|assertTermQuery
argument_list|(
name|query
argument_list|,
name|MetaData
operator|.
name|ALL
argument_list|,
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Encountered lucene query type we do not have a validation implementation for in our "
operator|+
name|SimpleQueryStringBuilderTests
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shouldClauses
specifier|private
specifier|static
name|int
name|shouldClauses
parameter_list|(
name|BooleanQuery
name|query
parameter_list|)
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|query
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
name|result
operator|++
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|testToQueryBoost
specifier|public
name|void
name|testToQueryBoost
parameter_list|()
throws|throws
name|IOException
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
name|QueryShardContext
name|shardContext
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|SimpleQueryStringBuilder
name|simpleQueryStringBuilder
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|simpleQueryStringBuilder
operator|.
name|field
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|simpleQueryStringBuilder
operator|.
name|toQuery
argument_list|(
name|shardContext
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|BoostQuery
name|boostQuery
init|=
operator|(
name|BoostQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|boostQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|boostQuery
operator|.
name|getQuery
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|simpleQueryStringBuilder
operator|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|simpleQueryStringBuilder
operator|.
name|field
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|simpleQueryStringBuilder
operator|.
name|boost
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|query
operator|=
name|simpleQueryStringBuilder
operator|.
name|toQuery
argument_list|(
name|shardContext
argument_list|)
expr_stmt|;
name|boostQuery
operator|=
operator|(
name|BoostQuery
operator|)
name|query
expr_stmt|;
name|assertThat
argument_list|(
name|boostQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|boostQuery
operator|.
name|getQuery
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|boostQuery
operator|=
operator|(
name|BoostQuery
operator|)
name|boostQuery
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|boostQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|boostQuery
operator|.
name|getQuery
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegativeFlags
specifier|public
name|void
name|testNegativeFlags
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|query
init|=
literal|"{\"simple_query_string\": {\"query\": \"foo bar\", \"flags\": -1}}"
decl_stmt|;
name|SimpleQueryStringBuilder
name|builder
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"foo bar"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|flags
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|assertParsedQuery
argument_list|(
name|query
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|SimpleQueryStringBuilder
name|otherBuilder
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|"foo bar"
argument_list|)
decl_stmt|;
name|otherBuilder
operator|.
name|flags
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
argument_list|,
name|equalTo
argument_list|(
name|otherBuilder
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

