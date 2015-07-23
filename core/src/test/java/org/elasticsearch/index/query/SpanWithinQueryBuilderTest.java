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
name|SpanQuery
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
name|SpanWithinQuery
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

begin_class
DECL|class|SpanWithinQueryBuilderTest
specifier|public
class|class
name|SpanWithinQueryBuilderTest
extends|extends
name|BaseQueryTestCase
argument_list|<
name|SpanWithinQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateExpectedQuery
specifier|protected
name|Query
name|doCreateExpectedQuery
parameter_list|(
name|SpanWithinQueryBuilder
name|testQueryBuilder
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanQuery
name|big
init|=
operator|(
name|SpanQuery
operator|)
name|testQueryBuilder
operator|.
name|big
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|SpanQuery
name|little
init|=
operator|(
name|SpanQuery
operator|)
name|testQueryBuilder
operator|.
name|little
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanWithinQuery
argument_list|(
name|big
argument_list|,
name|little
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|SpanWithinQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
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
literal|2
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanWithinQueryBuilder
argument_list|(
name|spanTermQueries
index|[
literal|0
index|]
argument_list|,
name|spanTermQueries
index|[
literal|1
index|]
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testValidate
specifier|public
name|void
name|testValidate
parameter_list|()
block|{
name|int
name|totalExpectedErrors
init|=
literal|0
decl_stmt|;
name|SpanQueryBuilder
name|bigSpanQueryBuilder
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|bigSpanQueryBuilder
operator|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|totalExpectedErrors
operator|++
expr_stmt|;
block|}
else|else
block|{
name|bigSpanQueryBuilder
operator|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
name|SpanQueryBuilder
name|littleSpanQueryBuilder
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|littleSpanQueryBuilder
operator|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|totalExpectedErrors
operator|++
expr_stmt|;
block|}
else|else
block|{
name|littleSpanQueryBuilder
operator|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
name|SpanWithinQueryBuilder
name|queryBuilder
init|=
operator|new
name|SpanWithinQueryBuilder
argument_list|(
name|bigSpanQueryBuilder
argument_list|,
name|littleSpanQueryBuilder
argument_list|)
decl_stmt|;
name|assertValidate
argument_list|(
name|queryBuilder
argument_list|,
name|totalExpectedErrors
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
DECL|method|testNullBig
specifier|public
name|void
name|testNullBig
parameter_list|()
block|{
operator|new
name|SpanWithinQueryBuilder
argument_list|(
literal|null
argument_list|,
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
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
DECL|method|testNullLittle
specifier|public
name|void
name|testNullLittle
parameter_list|()
block|{
operator|new
name|SpanWithinQueryBuilder
argument_list|(
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

