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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|AndQueryBuilderTest
specifier|public
class|class
name|AndQueryBuilderTest
extends|extends
name|BaseQueryTestCase
argument_list|<
name|AndQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createExpectedQuery
specifier|protected
name|Query
name|createExpectedQuery
parameter_list|(
name|AndQueryBuilder
name|queryBuilder
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|QueryParsingException
throws|,
name|IOException
block|{
if|if
condition|(
name|queryBuilder
operator|.
name|filters
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryBuilder
name|subQuery
range|:
name|queryBuilder
operator|.
name|filters
argument_list|()
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|subQuery
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
comment|/**      * @return a AndQueryBuilder with random limit between 0 and 20      */
annotation|@
name|Override
DECL|method|createTestQueryBuilder
specifier|protected
name|AndQueryBuilder
name|createTestQueryBuilder
parameter_list|()
block|{
name|AndQueryBuilder
name|query
init|=
operator|new
name|AndQueryBuilder
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
name|create
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
name|query
operator|.
name|queryName
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
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
DECL|method|assertLuceneQuery
specifier|protected
name|void
name|assertLuceneQuery
parameter_list|(
name|AndQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|.
name|queryName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Query
name|namedQuery
init|=
name|context
operator|.
name|copyNamedFilters
argument_list|()
operator|.
name|get
argument_list|(
name|queryBuilder
operator|.
name|queryName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|namedQuery
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
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
name|QueryParsingException
throws|,
name|IOException
block|{
name|AndQueryBuilder
name|andQuery
init|=
operator|new
name|AndQueryBuilder
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|andQuery
operator|.
name|toQuery
argument_list|(
name|createContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|QueryParseContext
name|context
init|=
name|createContext
argument_list|()
decl_stmt|;
name|String
name|queryString
init|=
literal|"{ \"and\" : {}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|queryString
argument_list|)
operator|.
name|createParser
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|assertQueryHeader
argument_list|(
name|parser
argument_list|,
name|AndQueryBuilder
operator|.
name|PROTOTYPE
operator|.
name|queryId
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|indexQueryParserService
argument_list|()
operator|.
name|queryParser
argument_list|(
name|AndQueryBuilder
operator|.
name|PROTOTYPE
operator|.
name|queryId
argument_list|()
argument_list|)
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

