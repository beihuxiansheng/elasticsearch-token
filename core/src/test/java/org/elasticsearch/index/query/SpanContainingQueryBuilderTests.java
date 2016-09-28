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
name|SpanContainingQuery
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
DECL|class|SpanContainingQueryBuilderTests
specifier|public
class|class
name|SpanContainingQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|SpanContainingQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|SpanContainingQueryBuilder
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
literal|2
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanContainingQueryBuilder
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
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|SpanContainingQueryBuilder
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
name|SpanContainingQuery
operator|.
name|class
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
name|SpanTermQueryBuilder
name|spanTermQuery
init|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SpanContainingQueryBuilder
argument_list|(
literal|null
argument_list|,
name|spanTermQuery
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|SpanContainingQueryBuilder
argument_list|(
name|spanTermQuery
argument_list|,
literal|null
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
literal|"  \"span_containing\" : {\n"
operator|+
literal|"    \"big\" : {\n"
operator|+
literal|"      \"span_near\" : {\n"
operator|+
literal|"        \"clauses\" : [ {\n"
operator|+
literal|"          \"span_term\" : {\n"
operator|+
literal|"            \"field1\" : {\n"
operator|+
literal|"              \"value\" : \"bar\",\n"
operator|+
literal|"              \"boost\" : 1.0\n"
operator|+
literal|"            }\n"
operator|+
literal|"          }\n"
operator|+
literal|"        }, {\n"
operator|+
literal|"          \"span_term\" : {\n"
operator|+
literal|"            \"field1\" : {\n"
operator|+
literal|"              \"value\" : \"baz\",\n"
operator|+
literal|"              \"boost\" : 1.0\n"
operator|+
literal|"            }\n"
operator|+
literal|"          }\n"
operator|+
literal|"        } ],\n"
operator|+
literal|"        \"slop\" : 5,\n"
operator|+
literal|"        \"in_order\" : true,\n"
operator|+
literal|"        \"boost\" : 1.0\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"little\" : {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"field1\" : {\n"
operator|+
literal|"          \"value\" : \"foo\",\n"
operator|+
literal|"          \"boost\" : 1.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"boost\" : 1.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|SpanContainingQueryBuilder
name|parsed
init|=
operator|(
name|SpanContainingQueryBuilder
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
literal|2
argument_list|,
operator|(
operator|(
name|SpanNearQueryBuilder
operator|)
name|parsed
operator|.
name|bigQuery
argument_list|()
operator|)
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
literal|"foo"
argument_list|,
operator|(
operator|(
name|SpanTermQueryBuilder
operator|)
name|parsed
operator|.
name|littleQuery
argument_list|()
operator|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

