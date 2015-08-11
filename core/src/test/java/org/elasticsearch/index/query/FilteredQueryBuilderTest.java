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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|nullValue
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|FilteredQueryBuilderTest
specifier|public
class|class
name|FilteredQueryBuilderTest
extends|extends
name|BaseQueryTestCase
argument_list|<
name|FilteredQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|FilteredQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|QueryBuilder
name|queryBuilder
init|=
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|QueryBuilder
name|filterBuilder
init|=
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilteredQueryBuilder
argument_list|(
name|queryBuilder
argument_list|,
name|filterBuilder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|FilteredQueryBuilder
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
name|Query
name|innerQuery
init|=
name|queryBuilder
operator|.
name|innerQuery
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerQuery
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Query
name|innerFilter
init|=
name|queryBuilder
operator|.
name|innerFilter
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerFilter
operator|==
literal|null
operator|||
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|innerFilter
argument_list|)
condition|)
block|{
name|innerQuery
operator|.
name|setBoost
argument_list|(
name|queryBuilder
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|equalTo
argument_list|(
name|innerQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|innerQuery
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|ConstantScoreQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|innerFilter
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
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|innerQuery
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
name|get
argument_list|(
literal|1
argument_list|)
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
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|innerFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testValidation
specifier|public
name|void
name|testValidation
parameter_list|()
block|{
name|QueryBuilder
name|valid
init|=
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|QueryBuilder
name|invalid
init|=
name|RandomQueryBuilder
operator|.
name|createInvalidQuery
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// invalid cases
name|FilteredQueryBuilder
name|qb
init|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|invalid
argument_list|)
decl_stmt|;
name|QueryValidationException
name|result
init|=
name|qb
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|valid
argument_list|,
name|invalid
argument_list|)
expr_stmt|;
name|result
operator|=
name|qb
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|invalid
argument_list|,
name|valid
argument_list|)
expr_stmt|;
name|result
operator|=
name|qb
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|invalid
argument_list|,
name|invalid
argument_list|)
expr_stmt|;
name|result
operator|=
name|qb
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// valid cases
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|valid
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|qb
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|qb
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
literal|null
argument_list|,
name|valid
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|qb
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|valid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|qb
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
name|qb
operator|=
operator|new
name|FilteredQueryBuilder
argument_list|(
name|valid
argument_list|,
name|valid
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|qb
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

