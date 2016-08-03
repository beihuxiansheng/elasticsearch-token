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
name|TermQuery
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
name|SpanTermQuery
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
name|ParsingException
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
name|BytesRefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|io
operator|.
name|JsonStringEncoder
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
DECL|class|SpanTermQueryBuilderTests
specifier|public
class|class
name|SpanTermQueryBuilderTests
extends|extends
name|AbstractTermQueryTestCase
argument_list|<
name|SpanTermQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|SpanTermQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|Object
name|value
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|fieldName
operator|=
name|STRING_FIELD_NAME
expr_stmt|;
block|}
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
name|value
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// generate unicode string in 10% of cases
name|JsonStringEncoder
name|encoder
init|=
name|JsonStringEncoder
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|value
operator|=
operator|new
name|String
argument_list|(
name|encoder
operator|.
name|quoteAsString
argument_list|(
name|randomUnicodeOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
name|fieldName
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
return|return
name|createQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createQueryBuilder
specifier|protected
name|SpanTermQueryBuilder
name|createQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
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
name|SpanTermQueryBuilder
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
name|SpanTermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SpanTermQuery
name|spanTermQuery
init|=
operator|(
name|SpanTermQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|spanTermQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|MappedFieldType
name|mapper
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|Term
name|term
init|=
operator|(
operator|(
name|TermQuery
operator|)
name|mapper
operator|.
name|termQuery
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|)
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|spanTermQuery
operator|.
name|getTerm
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|spanTermQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param amount the number of clauses that will be returned      * @return an array of random {@link SpanTermQueryBuilder} with same field name      */
DECL|method|createSpanTermQueryBuilders
specifier|public
name|SpanTermQueryBuilder
index|[]
name|createSpanTermQueryBuilders
parameter_list|(
name|int
name|amount
parameter_list|)
block|{
name|SpanTermQueryBuilder
index|[]
name|clauses
init|=
operator|new
name|SpanTermQueryBuilder
index|[
name|amount
index|]
decl_stmt|;
name|SpanTermQueryBuilder
name|first
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
name|clauses
index|[
literal|0
index|]
operator|=
name|first
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|++
control|)
block|{
comment|// we need same field name in all clauses, so we only randomize value
name|SpanTermQueryBuilder
name|spanTermQuery
init|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|first
operator|.
name|fieldName
argument_list|()
argument_list|,
name|getRandomValueForFieldName
argument_list|(
name|first
operator|.
name|fieldName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|spanTermQuery
operator|.
name|boost
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
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|spanTermQuery
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
name|clauses
index|[
name|i
index|]
operator|=
name|spanTermQuery
expr_stmt|;
block|}
return|return
name|clauses
return|;
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
literal|"{    \"span_term\" : { \"user\" : { \"value\" : \"kimchy\", \"boost\" : 2.0 } }}    "
decl_stmt|;
name|SpanTermQueryBuilder
name|parsed
init|=
operator|(
name|SpanTermQueryBuilder
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
literal|"kimchy"
argument_list|,
name|parsed
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|2.0
argument_list|,
name|parsed
operator|.
name|boost
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseFailsWithMultipleFields
specifier|public
name|void
name|testParseFailsWithMultipleFields
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"span_term\" : {\n"
operator|+
literal|"    \"message1\" : {\n"
operator|+
literal|"      \"term\" : \"this\"\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"message2\" : {\n"
operator|+
literal|"      \"term\" : \"this\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
try|try
block|{
name|parseQuery
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"parseQuery should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"[span_term] query doesn't support multiple fields, found [message1] and [message2]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

