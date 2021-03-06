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
name|FieldMaskingSpanQuery
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
DECL|class|FieldMaskingSpanQueryBuilderTests
specifier|public
class|class
name|FieldMaskingSpanQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|FieldMaskingSpanQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|FieldMaskingSpanQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|String
name|fieldName
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|fieldName
operator|=
name|randomFrom
argument_list|(
name|MAPPED_FIELD_NAMES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldName
operator|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|SpanTermQueryBuilder
name|innerQuery
init|=
operator|new
name|SpanTermQueryBuilderTests
argument_list|()
operator|.
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
return|return
operator|new
name|FieldMaskingSpanQueryBuilder
argument_list|(
name|innerQuery
argument_list|,
name|fieldName
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
name|FieldMaskingSpanQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fieldInQuery
init|=
name|queryBuilder
operator|.
name|fieldName
argument_list|()
decl_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|getQueryShardContext
argument_list|()
operator|.
name|fieldMapper
argument_list|(
name|fieldInQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|fieldInQuery
operator|=
name|fieldType
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|FieldMaskingSpanQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|FieldMaskingSpanQuery
name|fieldMaskingSpanQuery
init|=
operator|(
name|FieldMaskingSpanQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|fieldMaskingSpanQuery
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|fieldInQuery
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMaskingSpanQuery
operator|.
name|getMaskedQuery
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|innerQuery
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
operator|.
name|getQueryShardContext
argument_list|()
argument_list|)
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
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|FieldMaskingSpanQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|"maskedField"
argument_list|)
argument_list|)
expr_stmt|;
name|SpanQueryBuilder
name|span
init|=
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|"name"
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
name|FieldMaskingSpanQueryBuilder
argument_list|(
name|span
argument_list|,
literal|null
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
name|FieldMaskingSpanQueryBuilder
argument_list|(
name|span
argument_list|,
literal|""
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
literal|"  \"field_masking_span\" : {\n"
operator|+
literal|"    \"query\" : {\n"
operator|+
literal|"      \"span_term\" : {\n"
operator|+
literal|"        \"value\" : {\n"
operator|+
literal|"          \"value\" : 0.5,\n"
operator|+
literal|"          \"boost\" : 0.23\n"
operator|+
literal|"        }\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"field\" : \"mapped_geo_shape\",\n"
operator|+
literal|"    \"boost\" : 42.0,\n"
operator|+
literal|"    \"_name\" : \"KPI\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|FieldMaskingSpanQueryBuilder
name|parsed
init|=
operator|(
name|FieldMaskingSpanQueryBuilder
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
literal|42.0
argument_list|,
name|parsed
operator|.
name|boost
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|0.23
argument_list|,
name|parsed
operator|.
name|innerQuery
argument_list|()
operator|.
name|boost
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

