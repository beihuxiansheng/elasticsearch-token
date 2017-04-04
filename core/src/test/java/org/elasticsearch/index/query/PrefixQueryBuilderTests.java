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
name|PrefixQuery
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
name|ParsingException
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
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|prefixQuery
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

begin_class
DECL|class|PrefixQueryBuilderTests
specifier|public
class|class
name|PrefixQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|PrefixQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|PrefixQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|PrefixQueryBuilder
name|query
init|=
name|randomPrefixQuery
argument_list|()
decl_stmt|;
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
name|getRandomRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getAlternateVersions
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|PrefixQueryBuilder
argument_list|>
name|getAlternateVersions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PrefixQueryBuilder
argument_list|>
name|alternateVersions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|PrefixQueryBuilder
name|prefixQuery
init|=
name|randomPrefixQuery
argument_list|()
decl_stmt|;
name|String
name|contentString
init|=
literal|"{\n"
operator|+
literal|"    \"prefix\" : {\n"
operator|+
literal|"        \""
operator|+
name|prefixQuery
operator|.
name|fieldName
argument_list|()
operator|+
literal|"\" : \""
operator|+
name|prefixQuery
operator|.
name|value
argument_list|()
operator|+
literal|"\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|alternateVersions
operator|.
name|put
argument_list|(
name|contentString
argument_list|,
name|prefixQuery
argument_list|)
expr_stmt|;
return|return
name|alternateVersions
return|;
block|}
DECL|method|randomPrefixQuery
specifier|private
specifier|static
name|PrefixQueryBuilder
name|randomPrefixQuery
parameter_list|()
block|{
name|String
name|fieldName
init|=
name|randomBoolean
argument_list|()
condition|?
name|STRING_FIELD_NAME
else|:
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrefixQueryBuilder
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
name|PrefixQueryBuilder
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
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|PrefixQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|PrefixQuery
name|prefixQuery
init|=
operator|(
name|PrefixQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|prefixQuery
operator|.
name|getPrefix
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
name|assertThat
argument_list|(
name|prefixQuery
operator|.
name|getPrefix
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
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
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|PrefixQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|"text"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"field name is null or empty"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|PrefixQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field name is null or empty"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|PrefixQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value cannot be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBlendedRewriteMethod
specifier|public
name|void
name|testBlendedRewriteMethod
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|rewrite
init|=
literal|"top_terms_blended_freqs_10"
decl_stmt|;
name|Query
name|parsedQuery
init|=
name|parseQuery
argument_list|(
name|prefixQuery
argument_list|(
literal|"field"
argument_list|,
literal|"val"
argument_list|)
operator|.
name|rewrite
argument_list|(
name|rewrite
argument_list|)
argument_list|)
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parsedQuery
argument_list|,
name|instanceOf
argument_list|(
name|PrefixQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|PrefixQuery
name|prefixQuery
init|=
operator|(
name|PrefixQuery
operator|)
name|parsedQuery
decl_stmt|;
name|assertThat
argument_list|(
name|prefixQuery
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"val"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|prefixQuery
operator|.
name|getRewriteMethod
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MultiTermQuery
operator|.
name|TopTermsBlendedFreqScoringRewrite
operator|.
name|class
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
literal|"{    \"prefix\" : { \"user\" :  { \"value\" : \"ki\", \"boost\" : 2.0 } }}"
decl_stmt|;
name|PrefixQueryBuilder
name|parsed
init|=
operator|(
name|PrefixQueryBuilder
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
literal|"ki"
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
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|"user"
argument_list|,
name|parsed
operator|.
name|fieldName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumeric
specifier|public
name|void
name|testNumeric
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"test runs only when at least a type is registered"
argument_list|,
name|getCurrentTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|PrefixQueryBuilder
name|query
init|=
name|prefixQuery
argument_list|(
name|INT_FIELD_NAME
argument_list|,
literal|"12*"
argument_list|)
decl_stmt|;
name|QueryShardContext
name|context
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|QueryShardException
name|e
init|=
name|expectThrows
argument_list|(
name|QueryShardException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|query
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Can only use prefix queries on keyword and text fields - not on [mapped_int] which is of type [integer]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
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
literal|"    \"prefix\": {\n"
operator|+
literal|"      \"user1\": {\n"
operator|+
literal|"        \"value\": \"ki\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      \"user2\": {\n"
operator|+
literal|"        \"value\": \"ki\"\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|ParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|parseQuery
argument_list|(
name|json
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[prefix] query doesn't support multiple fields, found [user1] and [user2]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|shortJson
init|=
literal|"{\n"
operator|+
literal|"    \"prefix\": {\n"
operator|+
literal|"      \"user1\": \"ki\",\n"
operator|+
literal|"      \"user2\": \"ki\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|parseQuery
argument_list|(
name|shortJson
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[prefix] query doesn't support multiple fields, found [user1] and [user2]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

