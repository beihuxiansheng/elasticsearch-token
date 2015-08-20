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
name|Query
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
name|*
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
name|*
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|OrQueryBuilderTests
specifier|public
class|class
name|OrQueryBuilderTests
extends|extends
name|BaseQueryTestCase
argument_list|<
name|OrQueryBuilder
argument_list|>
block|{
comment|/**      * @return an OrQueryBuilder with random limit between 0 and 20      */
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|OrQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|OrQueryBuilder
name|query
init|=
operator|new
name|OrQueryBuilder
argument_list|()
decl_stmt|;
name|int
name|subQueries
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
name|subQueries
condition|;
name|i
operator|++
control|)
block|{
name|query
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
name|OrQueryBuilder
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
name|queryBuilder
operator|.
name|innerQueries
argument_list|()
operator|.
name|isEmpty
argument_list|()
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
name|List
argument_list|<
name|Query
argument_list|>
name|innerQueries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryBuilder
name|subQuery
range|:
name|queryBuilder
operator|.
name|innerQueries
argument_list|()
control|)
block|{
name|Query
name|innerQuery
init|=
name|subQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|// ignore queries that are null
if|if
condition|(
name|innerQuery
operator|!=
literal|null
condition|)
block|{
name|innerQueries
operator|.
name|add
argument_list|(
name|innerQuery
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|innerQueries
operator|.
name|isEmpty
argument_list|()
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
name|innerQueries
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
name|innerQueries
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
control|)
block|{
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
name|SHOULD
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
block|}
block|}
comment|/**      * test corner case where no inner queries exist      */
annotation|@
name|Test
DECL|method|testNoInnerQueries
specifier|public
name|void
name|testNoInnerQueries
parameter_list|()
throws|throws
name|QueryShardException
throws|,
name|IOException
block|{
name|OrQueryBuilder
name|orQuery
init|=
operator|new
name|OrQueryBuilder
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|orQuery
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAlternateVersions
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|OrQueryBuilder
argument_list|>
name|getAlternateVersions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|OrQueryBuilder
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
name|OrQueryBuilder
name|expectedQuery
init|=
operator|new
name|OrQueryBuilder
argument_list|(
name|innerQuery
argument_list|)
decl_stmt|;
name|String
name|contentString
init|=
literal|"{ \"or\" : [ "
operator|+
name|innerQuery
operator|+
literal|"] }"
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
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|QueryParsingException
operator|.
name|class
argument_list|)
DECL|method|testMissingFiltersSection
specifier|public
name|void
name|testMissingFiltersSection
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|queryString
init|=
literal|"{ \"or\" : {}"
decl_stmt|;
name|parseQuery
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidate
specifier|public
name|void
name|testValidate
parameter_list|()
block|{
name|OrQueryBuilder
name|orQuery
init|=
operator|new
name|OrQueryBuilder
argument_list|()
decl_stmt|;
name|int
name|iters
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|int
name|totalExpectedErrors
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
name|iters
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
name|orQuery
operator|.
name|add
argument_list|(
name|RandomQueryBuilder
operator|.
name|createInvalidQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|orQuery
operator|.
name|add
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
name|orQuery
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
block|}
name|assertValidate
argument_list|(
name|orQuery
argument_list|,
name|totalExpectedErrors
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
