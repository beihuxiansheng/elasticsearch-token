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
name|Query
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanQuery
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
name|common
operator|.
name|ParseFieldMatcher
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
name|Iterator
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
DECL|class|SpanNearQueryBuilderTests
specifier|public
class|class
name|SpanNearQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|SpanNearQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|SpanNearQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|SpanTermQueryBuilder
index|[]
name|spanTermQueries
init|=
operator|new
name|SpanTermQueryBuilderTests
argument_list|()
operator|.
name|createSpanTermQueryBuilders
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|6
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQueryBuilder
name|queryBuilder
init|=
operator|new
name|SpanNearQueryBuilder
argument_list|(
name|spanTermQueries
index|[
literal|0
index|]
argument_list|,
name|randomIntBetween
argument_list|(
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|spanTermQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|queryBuilder
operator|.
name|addClause
argument_list|(
name|spanTermQueries
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|queryBuilder
operator|.
name|inOrder
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|queryBuilder
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|SpanNearQueryBuilder
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
name|instanceOf
argument_list|(
name|SpanNearQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SpanNearQuery
name|spanNearQuery
init|=
operator|(
name|SpanNearQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|spanNearQuery
operator|.
name|getSlop
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|slop
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|spanNearQuery
operator|.
name|isInOrder
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|inOrder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|spanNearQuery
operator|.
name|getClauses
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SpanQueryBuilder
argument_list|>
name|spanQueryBuilderIterator
init|=
name|queryBuilder
operator|.
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|SpanQuery
name|spanQuery
range|:
name|spanNearQuery
operator|.
name|getClauses
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|spanQuery
argument_list|,
name|equalTo
argument_list|(
name|spanQueryBuilderIterator
operator|.
name|next
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SpanNearQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[span_near] must include at least one clause"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|SpanNearQueryBuilder
name|spanNearQueryBuilder
init|=
operator|new
name|SpanNearQueryBuilder
argument_list|(
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|spanNearQueryBuilder
operator|.
name|addClause
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[span_near]  clauses cannot be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testClausesUnmodifiable
specifier|public
name|void
name|testClausesUnmodifiable
parameter_list|()
block|{
name|SpanNearQueryBuilder
name|spanNearQueryBuilder
init|=
operator|new
name|SpanNearQueryBuilder
argument_list|(
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|spanNearQueryBuilder
operator|.
name|clauses
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|"value2"
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
literal|"  \"span_near\" : {\n"
operator|+
literal|"    \"clauses\" : [ {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field\" : {\n"
operator|+
literal|"          \"value\" : \"value1\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field\" : {\n"
operator|+
literal|"          \"value\" : \"value2\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field\" : {\n"
operator|+
literal|"          \"value\" : \"value3\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    } ],\n"
operator|+
literal|"    \"slop\" : 12,\n"
operator|+
literal|"    \"in_order\" : false,\n"
operator|+
literal|"    \"boost\" : 1.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|SpanNearQueryBuilder
name|parsed
init|=
operator|(
name|SpanNearQueryBuilder
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
literal|3
argument_list|,
name|parsed
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|12
argument_list|,
name|parsed
operator|.
name|slop
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|false
argument_list|,
name|parsed
operator|.
name|inOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCollectPayloadsDeprecated
specifier|public
name|void
name|testCollectPayloadsDeprecated
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"We can remove support for ignoring collect_payloads in 6.0.0"
argument_list|,
literal|5
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|major
argument_list|)
expr_stmt|;
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"span_near\" : {\n"
operator|+
literal|"    \"clauses\" : [ {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field\" : {\n"
operator|+
literal|"          \"value\" : \"value1\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field\" : {\n"
operator|+
literal|"          \"value\" : \"value2\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field\" : {\n"
operator|+
literal|"          \"value\" : \"value3\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    } ],\n"
operator|+
literal|"    \"slop\" : 12,\n"
operator|+
literal|"    \"in_order\" : false,\n"
operator|+
literal|"    \"collect_payloads\" : false,\n"
operator|+
literal|"    \"boost\" : 1.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|parseQuery
argument_list|(
name|json
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// Just don't throw an error and we're fine
block|}
block|}
end_class

end_unit

