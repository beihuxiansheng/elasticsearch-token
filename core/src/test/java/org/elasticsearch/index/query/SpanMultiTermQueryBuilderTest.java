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
name|MultiTermQuery
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
name|spans
operator|.
name|SpanMultiTermQueryWrapper
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

begin_class
DECL|class|SpanMultiTermQueryBuilderTest
specifier|public
class|class
name|SpanMultiTermQueryBuilderTest
extends|extends
name|BaseQueryTestCase
argument_list|<
name|SpanMultiTermQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|SpanMultiTermQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
init|=
name|RandomQueryBuilder
operator|.
name|createMultiTermQuery
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanMultiTermQueryBuilder
argument_list|(
name|multiTermQueryBuilder
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
name|SpanMultiTermQueryBuilder
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
name|SpanMultiTermQueryWrapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SpanMultiTermQueryWrapper
name|spanMultiTermQueryWrapper
init|=
operator|(
name|SpanMultiTermQueryWrapper
operator|)
name|query
decl_stmt|;
name|Query
name|multiTermQuery
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
name|assertThat
argument_list|(
name|multiTermQuery
argument_list|,
name|instanceOf
argument_list|(
name|MultiTermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|spanMultiTermQueryWrapper
operator|.
name|getWrappedQuery
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
operator|(
name|MultiTermQuery
operator|)
name|multiTermQuery
argument_list|)
operator|.
name|getWrappedQuery
argument_list|()
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
name|int
name|totalExpectedErrors
init|=
literal|0
decl_stmt|;
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
decl_stmt|;
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
name|multiTermQueryBuilder
operator|=
operator|new
name|RangeQueryBuilder
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|multiTermQueryBuilder
operator|=
literal|null
expr_stmt|;
block|}
name|totalExpectedErrors
operator|++
expr_stmt|;
block|}
else|else
block|{
name|multiTermQueryBuilder
operator|=
operator|new
name|RangeQueryBuilder
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
block|}
name|SpanMultiTermQueryBuilder
name|queryBuilder
init|=
operator|new
name|SpanMultiTermQueryBuilder
argument_list|(
name|multiTermQueryBuilder
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
comment|/**      * test checks that we throw an {@link UnsupportedOperationException} if the query wrapped      * by {@link SpanMultiTermQueryBuilder} does not generate a lucene {@link MultiTermQuery}.      * This is currently the case for {@link RangeQueryBuilder} when the target field is mapped      * to a date.      */
annotation|@
name|Test
DECL|method|testUnsupportedInnerQueryType
specifier|public
name|void
name|testUnsupportedInnerQueryType
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
comment|// test makes only sense if we have at least one type registered with date field mapping
if|if
condition|(
name|getCurrentTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|&&
name|context
operator|.
name|fieldMapper
argument_list|(
name|DATE_FIELD_NAME
argument_list|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|RangeQueryBuilder
name|query
init|=
operator|new
name|RangeQueryBuilder
argument_list|(
name|DATE_FIELD_NAME
argument_list|)
decl_stmt|;
operator|new
name|SpanMultiTermQueryBuilder
argument_list|(
name|query
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected, range query on date fields should not generate a lucene "
operator|+
name|MultiTermQuery
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
assert|assert
operator|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"unsupported inner query, should be "
operator|+
name|MultiTermQuery
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
assert|;
block|}
block|}
block|}
block|}
end_class

end_unit

