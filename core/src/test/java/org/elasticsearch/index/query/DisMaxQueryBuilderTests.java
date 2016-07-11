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
name|BoostQuery
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
name|DisjunctionMaxQuery
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
name|PrefixQuery
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
name|MatchNoDocsQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|AbstractQueryTestCase
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
name|Collection
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|closeTo
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
name|instanceOf
import|;
end_import

begin_class
DECL|class|DisMaxQueryBuilderTests
specifier|public
class|class
name|DisMaxQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|DisMaxQueryBuilder
argument_list|>
block|{
comment|/**      * @return a {@link DisMaxQueryBuilder} with random inner queries      */
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|DisMaxQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|DisMaxQueryBuilder
name|dismax
init|=
operator|new
name|DisMaxQueryBuilder
argument_list|()
decl_stmt|;
name|int
name|clauses
init|=
name|randomIntBetween
argument_list|(
literal|1
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
name|clauses
condition|;
name|i
operator|++
control|)
block|{
name|dismax
operator|.
name|add
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
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|dismax
operator|.
name|tieBreaker
argument_list|(
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
return|return
name|dismax
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|DisMaxQueryBuilder
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
name|Collection
argument_list|<
name|Query
argument_list|>
name|queries
init|=
name|AbstractQueryBuilder
operator|.
name|toQueries
argument_list|(
name|queryBuilder
operator|.
name|innerQueries
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|DisjunctionMaxQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|disjunctionMaxQuery
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|disjunctionMaxQuery
operator|.
name|getTieBreakerMultiplier
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|tieBreaker
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|disjunctionMaxQuery
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queries
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Query
argument_list|>
name|queryIterator
init|=
name|queries
operator|.
name|iterator
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
name|disjunctionMaxQuery
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|disjunctionMaxQuery
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|queryIterator
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAlternateVersions
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|DisMaxQueryBuilder
argument_list|>
name|getAlternateVersions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|DisMaxQueryBuilder
argument_list|>
name|alternateVersions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|QueryBuilder
name|innerQuery
init|=
name|createTestQueryBuilder
argument_list|()
operator|.
name|innerQueries
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DisMaxQueryBuilder
name|expectedQuery
init|=
operator|new
name|DisMaxQueryBuilder
argument_list|()
decl_stmt|;
name|expectedQuery
operator|.
name|add
argument_list|(
name|innerQuery
argument_list|)
expr_stmt|;
name|String
name|contentString
init|=
literal|"{\n"
operator|+
literal|"    \"dis_max\" : {\n"
operator|+
literal|"        \"queries\" : "
operator|+
name|innerQuery
operator|.
name|toString
argument_list|()
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
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
comment|/**      * Test with empty inner query body, this should be converted to a {@link MatchNoDocsQuery}.      * To test this, we use inner {@link ConstantScoreQueryBuilder} with empty inner filter.      */
DECL|method|testInnerQueryEmptyException
specifier|public
name|void
name|testInnerQueryEmptyException
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|queryString
init|=
literal|"{ \""
operator|+
name|DisMaxQueryBuilder
operator|.
name|NAME
operator|+
literal|"\" :"
operator|+
literal|"             { \"queries\" : [ {\""
operator|+
name|ConstantScoreQueryBuilder
operator|.
name|NAME
operator|+
literal|"\" : { \"filter\" : { } } } ] "
operator|+
literal|"             }"
operator|+
literal|"           }"
decl_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|parseQuery
argument_list|(
name|queryString
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|QueryShardContext
name|context
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|Query
name|luceneQuery
init|=
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|luceneQuery
argument_list|,
name|instanceOf
argument_list|(
name|MatchNoDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|MatchNoDocsQuery
operator|)
name|luceneQuery
operator|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"MatchNoDocsQuery[\"no clauses for dismax query.\"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
name|DisMaxQueryBuilder
name|disMaxQuery
init|=
operator|new
name|DisMaxQueryBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|disMaxQuery
operator|.
name|add
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
DECL|method|testToQueryInnerPrefixQuery
specifier|public
name|void
name|testToQueryInnerPrefixQuery
parameter_list|()
throws|throws
name|Exception
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
name|String
name|queryAsString
init|=
literal|"{\n"
operator|+
literal|"    \"dis_max\":{\n"
operator|+
literal|"        \"queries\":[\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"prefix\":{\n"
operator|+
literal|"                    \""
operator|+
name|STRING_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"                        \"value\":\"sh\",\n"
operator|+
literal|"                        \"boost\":1.2\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                }\n"
operator|+
literal|"            }\n"
operator|+
literal|"        ]\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|Query
name|query
init|=
name|parseQuery
argument_list|(
name|queryAsString
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
name|query
argument_list|,
name|instanceOf
argument_list|(
name|DisjunctionMaxQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|disjunctionMaxQuery
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|query
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|disjuncts
init|=
name|disjunctionMaxQuery
operator|.
name|getDisjuncts
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|disjuncts
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
name|assertThat
argument_list|(
name|disjuncts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|disjuncts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|boostQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|1.2
argument_list|,
literal|0.00001
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
name|PrefixQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|PrefixQuery
name|firstQ
init|=
operator|(
name|PrefixQuery
operator|)
name|boostQuery
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// since age is automatically registered in data, we encode it as numeric
name|assertThat
argument_list|(
name|firstQ
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|Term
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
literal|"sh"
argument_list|)
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
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"dis_max\" : {\n"
operator|+
literal|"    \"tie_breaker\" : 0.7,\n"
operator|+
literal|"    \"queries\" : [ {\n"
operator|+
literal|"      \"term\" : {\n"
operator|+
literal|"        \"age\" : {\n"
operator|+
literal|"          \"value\" : 34,\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      \"term\" : {\n"
operator|+
literal|"        \"age\" : {\n"
operator|+
literal|"          \"value\" : 35,\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    } ],\n"
operator|+
literal|"    \"boost\" : 1.2\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|DisMaxQueryBuilder
name|parsed
init|=
operator|(
name|DisMaxQueryBuilder
operator|)
name|parseQuery
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|checkGeneratedJson
argument_list|(
name|json
argument_list|,
name|parsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|1.2
argument_list|,
name|parsed
operator|.
name|boost
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|0.7
argument_list|,
name|parsed
operator|.
name|tieBreaker
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|2
argument_list|,
name|parsed
operator|.
name|innerQueries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

