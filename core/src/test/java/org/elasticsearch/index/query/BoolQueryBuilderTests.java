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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|MatchAllDocsQuery
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
name|Query
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
name|bytes
operator|.
name|BytesReference
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
name|XContentBuilder
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
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|ArrayList
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
name|Iterator
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|boolQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|constantScoreQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|termQuery
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
name|equalTo
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

begin_class
DECL|class|BoolQueryBuilderTests
specifier|public
class|class
name|BoolQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|BoolQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|BoolQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|BoolQueryBuilder
name|query
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|query
operator|.
name|adjustPureNegative
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
name|query
operator|.
name|disableCoord
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
name|query
operator|.
name|minimumNumberShouldMatch
argument_list|(
name|randomMinimumShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|mustClauses
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
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
name|mustClauses
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|must
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|mustNotClauses
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
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
name|mustNotClauses
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|mustNot
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|shouldClauses
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
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
name|shouldClauses
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|should
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|filterClauses
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
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
name|filterClauses
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|filter
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|BoolQueryBuilder
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
if|if
condition|(
operator|!
name|queryBuilder
operator|.
name|hasClauses
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|clauses
operator|.
name|addAll
argument_list|(
name|getBooleanClauses
argument_list|(
name|queryBuilder
operator|.
name|must
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|clauses
operator|.
name|addAll
argument_list|(
name|getBooleanClauses
argument_list|(
name|queryBuilder
operator|.
name|mustNot
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|clauses
operator|.
name|addAll
argument_list|(
name|getBooleanClauses
argument_list|(
name|queryBuilder
operator|.
name|should
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|clauses
operator|.
name|addAll
argument_list|(
name|getBooleanClauses
argument_list|(
name|queryBuilder
operator|.
name|filter
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|clauses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
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
name|booleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|isCoordDisabled
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|disableCoord
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|adjustPureNegative
argument_list|()
condition|)
block|{
name|boolean
name|isNegative
init|=
literal|true
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|isProhibited
argument_list|()
operator|==
literal|false
condition|)
block|{
name|isNegative
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|isNegative
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|clauseIterator
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|booleanClause
range|:
name|booleanQuery
operator|.
name|getClauses
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|booleanClause
argument_list|,
name|instanceOf
argument_list|(
name|clauseIterator
operator|.
name|next
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getBooleanClauses
specifier|private
specifier|static
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|getBooleanClauses
parameter_list|(
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|queryBuilders
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryBuilder
name|query
range|:
name|queryBuilders
control|)
block|{
name|Query
name|innerQuery
init|=
name|query
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerQuery
operator|!=
literal|null
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|innerQuery
argument_list|,
name|occur
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|clauses
return|;
block|}
annotation|@
name|Override
DECL|method|getAlternateVersions
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|BoolQueryBuilder
argument_list|>
name|getAlternateVersions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|BoolQueryBuilder
argument_list|>
name|alternateVersions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|BoolQueryBuilder
name|tempQueryBuilder
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
name|BoolQueryBuilder
name|expectedQuery
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
name|String
name|contentString
init|=
literal|"{\n"
operator|+
literal|"    \"bool\" : {\n"
decl_stmt|;
if|if
condition|(
name|tempQueryBuilder
operator|.
name|must
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|QueryBuilder
name|must
init|=
name|tempQueryBuilder
operator|.
name|must
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|contentString
operator|+=
literal|"must: "
operator|+
name|must
operator|.
name|toString
argument_list|()
operator|+
literal|","
expr_stmt|;
name|expectedQuery
operator|.
name|must
argument_list|(
name|must
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tempQueryBuilder
operator|.
name|mustNot
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|QueryBuilder
name|mustNot
init|=
name|tempQueryBuilder
operator|.
name|mustNot
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|contentString
operator|+=
operator|(
name|randomBoolean
argument_list|()
condition|?
literal|"must_not: "
else|:
literal|"mustNot: "
operator|)
operator|+
name|mustNot
operator|.
name|toString
argument_list|()
operator|+
literal|","
expr_stmt|;
name|expectedQuery
operator|.
name|mustNot
argument_list|(
name|mustNot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tempQueryBuilder
operator|.
name|should
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|QueryBuilder
name|should
init|=
name|tempQueryBuilder
operator|.
name|should
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|contentString
operator|+=
literal|"should: "
operator|+
name|should
operator|.
name|toString
argument_list|()
operator|+
literal|","
expr_stmt|;
name|expectedQuery
operator|.
name|should
argument_list|(
name|should
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tempQueryBuilder
operator|.
name|filter
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|QueryBuilder
name|filter
init|=
name|tempQueryBuilder
operator|.
name|filter
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|contentString
operator|+=
literal|"filter: "
operator|+
name|filter
operator|.
name|toString
argument_list|()
operator|+
literal|","
expr_stmt|;
name|expectedQuery
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
name|contentString
operator|=
name|contentString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|contentString
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|contentString
operator|+=
literal|"    }    \n"
operator|+
literal|"}"
expr_stmt|;
name|alternateVersions
operator|.
name|put
argument_list|(
name|contentString
argument_list|,
name|expectedQuery
argument_list|)
expr_stmt|;
return|return
name|alternateVersions
return|;
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
name|BoolQueryBuilder
name|booleanQuery
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|booleanQuery
operator|.
name|must
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
block|{         }
try|try
block|{
name|booleanQuery
operator|.
name|mustNot
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
block|{         }
try|try
block|{
name|booleanQuery
operator|.
name|filter
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
block|{         }
try|try
block|{
name|booleanQuery
operator|.
name|should
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
block|{         }
block|}
comment|// https://github.com/elasticsearch/elasticsearch/issues/7240
DECL|method|testEmptyBooleanQuery
specifier|public
name|void
name|testEmptyBooleanQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|contentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BytesReference
name|query
init|=
name|contentBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"bool"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|Query
name|parsedQuery
init|=
name|parseQuery
argument_list|(
name|query
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parsedQuery
argument_list|,
name|Matchers
operator|.
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultMinShouldMatch
specifier|public
name|void
name|testDefaultMinShouldMatch
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Queries have a minShouldMatch of 0
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|parseQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|buildAsBytes
argument_list|()
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|bq
operator|=
operator|(
name|BooleanQuery
operator|)
name|parseQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|should
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|buildAsBytes
argument_list|()
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
comment|// Filters have a minShouldMatch of 0/1
name|ConstantScoreQuery
name|csq
init|=
operator|(
name|ConstantScoreQuery
operator|)
name|parseQuery
argument_list|(
name|constantScoreQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|buildAsBytes
argument_list|()
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
name|bq
operator|=
operator|(
name|BooleanQuery
operator|)
name|csq
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|csq
operator|=
operator|(
name|ConstantScoreQuery
operator|)
name|parseQuery
argument_list|(
name|constantScoreQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|should
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|buildAsBytes
argument_list|()
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
expr_stmt|;
name|bq
operator|=
operator|(
name|BooleanQuery
operator|)
name|csq
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinShouldMatchFilterWithoutShouldClauses
specifier|public
name|void
name|testMinShouldMatchFilterWithoutShouldClauses
parameter_list|()
throws|throws
name|Exception
block|{
name|BoolQueryBuilder
name|boolQueryBuilder
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
name|boolQueryBuilder
operator|.
name|filter
argument_list|(
operator|new
name|BoolQueryBuilder
argument_list|()
operator|.
name|must
argument_list|(
operator|new
name|MatchAllQueryBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|boolQueryBuilder
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
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
name|booleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanClause
name|booleanClause
init|=
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|booleanClause
operator|.
name|getOccur
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|booleanClause
operator|.
name|getQuery
argument_list|()
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
name|innerBooleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|booleanClause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|//we didn't set minimum should match initially, there are no should clauses so it should be 0
name|assertThat
argument_list|(
name|innerBooleanQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|innerBooleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanClause
name|innerBooleanClause
init|=
name|innerBooleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|innerBooleanClause
operator|.
name|getOccur
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|innerBooleanClause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinShouldMatchFilterWithShouldClauses
specifier|public
name|void
name|testMinShouldMatchFilterWithShouldClauses
parameter_list|()
throws|throws
name|Exception
block|{
name|BoolQueryBuilder
name|boolQueryBuilder
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
name|boolQueryBuilder
operator|.
name|filter
argument_list|(
operator|new
name|BoolQueryBuilder
argument_list|()
operator|.
name|must
argument_list|(
operator|new
name|MatchAllQueryBuilder
argument_list|()
argument_list|)
operator|.
name|should
argument_list|(
operator|new
name|MatchAllQueryBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|boolQueryBuilder
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
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
name|booleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanClause
name|booleanClause
init|=
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|booleanClause
operator|.
name|getOccur
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|booleanClause
operator|.
name|getQuery
argument_list|()
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
name|innerBooleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|booleanClause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|//we didn't set minimum should match initially, but there are should clauses so it should be 1
name|assertThat
argument_list|(
name|innerBooleanQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|innerBooleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanClause
name|innerBooleanClause1
init|=
name|innerBooleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|innerBooleanClause1
operator|.
name|getOccur
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|innerBooleanClause1
operator|.
name|getQuery
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanClause
name|innerBooleanClause2
init|=
name|innerBooleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|innerBooleanClause2
operator|.
name|getOccur
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|innerBooleanClause2
operator|.
name|getQuery
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MatchAllDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromJson
specifier|public
name|void
name|testFromJson
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|query
init|=
literal|"{"
operator|+
literal|"\"bool\" : {"
operator|+
literal|"  \"must\" : [ {"
operator|+
literal|"    \"term\" : {"
operator|+
literal|"      \"user\" : {"
operator|+
literal|"        \"value\" : \"kimchy\","
operator|+
literal|"        \"boost\" : 1.0"
operator|+
literal|"      }"
operator|+
literal|"    }"
operator|+
literal|"  } ],"
operator|+
literal|"  \"filter\" : [ {"
operator|+
literal|"    \"term\" : {"
operator|+
literal|"      \"tag\" : {"
operator|+
literal|"        \"value\" : \"tech\","
operator|+
literal|"        \"boost\" : 1.0"
operator|+
literal|"      }"
operator|+
literal|"    }"
operator|+
literal|"  } ],"
operator|+
literal|"  \"must_not\" : [ {"
operator|+
literal|"    \"range\" : {"
operator|+
literal|"      \"age\" : {"
operator|+
literal|"        \"from\" : 10,"
operator|+
literal|"        \"to\" : 20,"
operator|+
literal|"        \"include_lower\" : true,"
operator|+
literal|"        \"include_upper\" : true,"
operator|+
literal|"        \"boost\" : 1.0"
operator|+
literal|"      }"
operator|+
literal|"    }"
operator|+
literal|"  } ],"
operator|+
literal|"  \"should\" : [ {"
operator|+
literal|"    \"term\" : {"
operator|+
literal|"      \"tag\" : {"
operator|+
literal|"        \"value\" : \"wow\","
operator|+
literal|"        \"boost\" : 1.0"
operator|+
literal|"      }"
operator|+
literal|"    }"
operator|+
literal|"  }, {"
operator|+
literal|"    \"term\" : {"
operator|+
literal|"      \"tag\" : {"
operator|+
literal|"        \"value\" : \"elasticsearch\","
operator|+
literal|"        \"boost\" : 1.0"
operator|+
literal|"      }"
operator|+
literal|"    }"
operator|+
literal|"  } ],"
operator|+
literal|"  \"disable_coord\" : false,"
operator|+
literal|"  \"adjust_pure_negative\" : true,"
operator|+
literal|"  \"minimum_should_match\" : \"23\","
operator|+
literal|"  \"boost\" : 42.0"
operator|+
literal|"}"
operator|+
literal|"}"
decl_stmt|;
name|BoolQueryBuilder
name|queryBuilder
init|=
operator|(
name|BoolQueryBuilder
operator|)
name|parseQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|checkGeneratedJson
argument_list|(
name|query
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
literal|42
argument_list|,
name|queryBuilder
operator|.
name|boost
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
literal|"23"
argument_list|,
name|queryBuilder
operator|.
name|minimumShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
literal|"kimchy"
argument_list|,
operator|(
operator|(
name|TermQueryBuilder
operator|)
name|queryBuilder
operator|.
name|must
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

