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
name|WildcardQuery
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

begin_class
DECL|class|WildcardQueryBuilderTests
specifier|public
class|class
name|WildcardQueryBuilderTests
extends|extends
name|BaseQueryTestCase
argument_list|<
name|WildcardQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|WildcardQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|WildcardQueryBuilder
name|query
decl_stmt|;
comment|// mapped or unmapped field
name|String
name|text
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|query
operator|=
operator|new
name|WildcardQueryBuilder
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|WildcardQueryBuilder
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|text
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
name|rewrite
argument_list|(
name|randomFrom
argument_list|(
name|getRandomRewriteMethod
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
name|WildcardQueryBuilder
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
name|WildcardQuery
operator|.
name|class
argument_list|)
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
name|WildcardQueryBuilder
name|wildcardQueryBuilder
init|=
operator|new
name|WildcardQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|wildcardQueryBuilder
operator|.
name|validate
argument_list|()
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|wildcardQueryBuilder
operator|=
operator|new
name|WildcardQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|wildcardQueryBuilder
operator|.
name|validate
argument_list|()
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|wildcardQueryBuilder
operator|=
operator|new
name|WildcardQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|wildcardQueryBuilder
operator|.
name|validate
argument_list|()
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|wildcardQueryBuilder
operator|=
operator|new
name|WildcardQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|wildcardQueryBuilder
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyValue
specifier|public
name|void
name|testEmptyValue
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryShardContext
name|context
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|WildcardQueryBuilder
name|wildcardQueryBuilder
init|=
operator|new
name|WildcardQueryBuilder
argument_list|(
name|getRandomType
argument_list|()
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|wildcardQueryBuilder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|,
name|WildcardQuery
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
