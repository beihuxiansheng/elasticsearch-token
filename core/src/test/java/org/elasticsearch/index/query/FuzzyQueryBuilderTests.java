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
name|FuzzyQuery
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
name|NumericRangeQuery
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
name|collect
operator|.
name|Tuple
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
name|unit
operator|.
name|Fuzziness
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
DECL|class|FuzzyQueryBuilderTests
specifier|public
class|class
name|FuzzyQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|FuzzyQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|FuzzyQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldAndValue
init|=
name|getRandomFieldNameAndValue
argument_list|()
decl_stmt|;
name|FuzzyQueryBuilder
name|query
init|=
operator|new
name|FuzzyQueryBuilder
argument_list|(
name|fieldAndValue
operator|.
name|v1
argument_list|()
argument_list|,
name|fieldAndValue
operator|.
name|v2
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|query
operator|.
name|fuzziness
argument_list|(
name|randomFuzziness
argument_list|(
name|query
operator|.
name|fieldName
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
name|prefixLength
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
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
name|maxExpansions
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
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
name|transpositions
argument_list|(
name|randomBoolean
argument_list|()
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
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|FuzzyQueryBuilder
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
name|isNumericFieldName
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
operator|||
name|queryBuilder
operator|.
name|fieldName
argument_list|()
operator|.
name|equals
argument_list|(
name|DATE_FIELD_NAME
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|NumericRangeQuery
operator|.
name|class
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
name|FuzzyQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
try|try
block|{
operator|new
name|FuzzyQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
operator|new
name|FuzzyQueryBuilder
argument_list|(
literal|""
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be empty"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
operator|new
name|FuzzyQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testUnsupportedFuzzinessForStringType
specifier|public
name|void
name|testUnsupportedFuzzinessForStringType
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
name|FuzzyQueryBuilder
name|fuzzyQueryBuilder
init|=
operator|new
name|FuzzyQueryBuilder
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|fuzzyQueryBuilder
operator|.
name|fuzziness
argument_list|(
name|Fuzziness
operator|.
name|build
argument_list|(
name|randomFrom
argument_list|(
literal|"a string which is not auto"
argument_list|,
literal|"3h"
argument_list|,
literal|"200s"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|fuzzyQueryBuilder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed with NumberFormatException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|Matchers
operator|.
name|containsString
argument_list|(
literal|"For input string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testToQueryWithStringField
specifier|public
name|void
name|testToQueryWithStringField
parameter_list|()
throws|throws
name|IOException
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
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"    \"fuzzy\":{\n"
operator|+
literal|"        \""
operator|+
name|STRING_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"            \"value\":\"sh\",\n"
operator|+
literal|"            \"fuzziness\": \"AUTO\",\n"
operator|+
literal|"            \"prefix_length\":1,\n"
operator|+
literal|"            \"boost\":2.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|Query
name|parsedQuery
init|=
name|parseQuery
argument_list|(
name|query
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
name|FuzzyQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|FuzzyQuery
name|fuzzyQuery
init|=
operator|(
name|FuzzyQuery
operator|)
name|parsedQuery
decl_stmt|;
name|assertThat
argument_list|(
name|fuzzyQuery
operator|.
name|getTerm
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|Term
argument_list|(
name|STRING_FIELD_NAME
argument_list|,
literal|"sh"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fuzzyQuery
operator|.
name|getMaxEdits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Fuzziness
operator|.
name|AUTO
operator|.
name|asDistance
argument_list|(
literal|"sh"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fuzzyQuery
operator|.
name|getPrefixLength
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fuzzyQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToQueryWithNumericField
specifier|public
name|void
name|testToQueryWithNumericField
parameter_list|()
throws|throws
name|IOException
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
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"    \"fuzzy\":{\n"
operator|+
literal|"        \""
operator|+
name|INT_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"            \"value\":12,\n"
operator|+
literal|"            \"fuzziness\":5,\n"
operator|+
literal|"            \"boost\":2.0\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|Query
name|parsedQuery
init|=
name|parseQuery
argument_list|(
name|query
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
name|NumericRangeQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|NumericRangeQuery
name|fuzzyQuery
init|=
operator|(
name|NumericRangeQuery
operator|)
name|parsedQuery
decl_stmt|;
name|assertThat
argument_list|(
name|fuzzyQuery
operator|.
name|getMin
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fuzzyQuery
operator|.
name|getMax
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|17l
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

