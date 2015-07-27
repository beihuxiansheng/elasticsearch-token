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
DECL|class|SpanNearQueryBuilderTest
specifier|public
class|class
name|SpanNearQueryBuilderTest
extends|extends
name|BaseQueryTestCase
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
name|SpanNearQueryBuilder
name|queryBuilder
init|=
operator|new
name|SpanNearQueryBuilder
argument_list|(
name|randomIntBetween
argument_list|(
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQueryBuilder
index|[]
name|spanTermQueries
init|=
operator|new
name|SpanTermQueryBuilderTest
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
for|for
control|(
name|SpanTermQueryBuilder
name|clause
range|:
name|spanTermQueries
control|)
block|{
name|queryBuilder
operator|.
name|clause
argument_list|(
name|clause
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
name|queryBuilder
operator|.
name|collectPayloads
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
annotation|@
name|Test
DECL|method|testValidate
specifier|public
name|void
name|testValidate
parameter_list|()
block|{
name|SpanNearQueryBuilder
name|queryBuilder
init|=
operator|new
name|SpanNearQueryBuilder
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertValidate
argument_list|(
name|queryBuilder
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// empty clause list
name|int
name|totalExpectedErrors
init|=
literal|0
decl_stmt|;
name|int
name|clauses
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
name|clauses
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
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|queryBuilder
operator|.
name|clause
argument_list|(
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryBuilder
operator|.
name|clause
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|totalExpectedErrors
operator|++
expr_stmt|;
block|}
else|else
block|{
name|queryBuilder
operator|.
name|clause
argument_list|(
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertValidate
argument_list|(
name|queryBuilder
argument_list|,
name|totalExpectedErrors
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

