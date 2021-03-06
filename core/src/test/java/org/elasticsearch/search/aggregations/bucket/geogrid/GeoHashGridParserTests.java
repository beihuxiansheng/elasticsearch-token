begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.geogrid
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|geogrid
package|;
end_package

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
name|xcontent
operator|.
name|XContentParser
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
name|json
operator|.
name|JsonXContent
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
name|query
operator|.
name|QueryParseContext
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
name|ESTestCase
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
DECL|class|GeoHashGridParserTests
specifier|public
class|class
name|GeoHashGridParserTests
extends|extends
name|ESTestCase
block|{
DECL|method|testParseValidFromInts
specifier|public
name|void
name|testParseValidFromInts
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|precision
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|XContentParser
name|stParser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
literal|"{\"field\":\"my_loc\", \"precision\":"
operator|+
name|precision
operator|+
literal|", \"size\": 500, \"shard_size\": 550}"
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|stParser
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|stParser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// can create a factory
name|assertNotNull
argument_list|(
name|GeoGridAggregationBuilder
operator|.
name|parse
argument_list|(
literal|"geohash_grid"
argument_list|,
name|parseContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseValidFromStrings
specifier|public
name|void
name|testParseValidFromStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|precision
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|XContentParser
name|stParser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
literal|"{\"field\":\"my_loc\", \"precision\":\""
operator|+
name|precision
operator|+
literal|"\", \"size\": \"500\", \"shard_size\": \"550\"}"
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|stParser
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|stParser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// can create a factory
name|assertNotNull
argument_list|(
name|GeoGridAggregationBuilder
operator|.
name|parse
argument_list|(
literal|"geohash_grid"
argument_list|,
name|parseContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseErrorOnNonIntPrecision
specifier|public
name|void
name|testParseErrorOnNonIntPrecision
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentParser
name|stParser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
literal|"{\"field\":\"my_loc\", \"precision\":\"2.0\"}"
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|stParser
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|stParser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|token
argument_list|)
expr_stmt|;
try|try
block|{
name|GeoGridAggregationBuilder
operator|.
name|parse
argument_list|(
literal|"geohash_grid"
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|NumberFormatException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"For input string: \"2.0\""
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseErrorOnBooleanPrecision
specifier|public
name|void
name|testParseErrorOnBooleanPrecision
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentParser
name|stParser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
literal|"{\"field\":\"my_loc\", \"precision\":false}"
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|stParser
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|stParser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|token
argument_list|)
expr_stmt|;
try|try
block|{
name|GeoGridAggregationBuilder
operator|.
name|parse
argument_list|(
literal|"geohash_grid"
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"[geohash_grid] precision doesn't support values of type: VALUE_BOOLEAN"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseErrorOnPrecisionOutOfRange
specifier|public
name|void
name|testParseErrorOnPrecisionOutOfRange
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentParser
name|stParser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
literal|"{\"field\":\"my_loc\", \"precision\":\"13\"}"
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|stParser
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|stParser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|token
argument_list|)
expr_stmt|;
try|try
block|{
name|GeoGridAggregationBuilder
operator|.
name|parse
argument_list|(
literal|"geohash_grid"
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid geohash aggregation precision of 13. Must be between 1 and 12."
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
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

