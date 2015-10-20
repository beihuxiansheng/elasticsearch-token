begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.cache.query.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cache
operator|.
name|query
operator|.
name|terms
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|containsString
import|;
end_import

begin_class
DECL|class|TermsLookupTests
specifier|public
class|class
name|TermsLookupTests
extends|extends
name|ESTestCase
block|{
DECL|method|testTermsLookup
specifier|public
name|void
name|testTermsLookup
parameter_list|()
block|{
name|String
name|index
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|routing
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|TermsLookup
name|termsLookup
init|=
operator|new
name|TermsLookup
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|termsLookup
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|index
argument_list|,
name|termsLookup
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|type
argument_list|,
name|termsLookup
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|termsLookup
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|termsLookup
operator|.
name|path
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routing
argument_list|,
name|termsLookup
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
name|String
name|type
init|=
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|type
operator|=
literal|null
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|id
operator|=
literal|null
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|path
operator|=
literal|null
expr_stmt|;
break|break;
block|}
try|try
block|{
operator|new
name|TermsLookup
argument_list|(
literal|null
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|containsString
argument_list|(
literal|"[terms] query lookup element requires specifying"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|TermsLookup
name|termsLookup
init|=
name|randomTermsLookup
argument_list|()
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|termsLookup
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|TermsLookup
name|deserializedLookup
init|=
name|TermsLookup
operator|.
name|readTermsLookupFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserializedLookup
argument_list|,
name|termsLookup
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializedLookup
operator|.
name|hashCode
argument_list|()
argument_list|,
name|termsLookup
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserializedLookup
argument_list|,
name|termsLookup
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|randomTermsLookup
specifier|public
specifier|static
name|TermsLookup
name|randomTermsLookup
parameter_list|()
block|{
return|return
operator|new
name|TermsLookup
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
else|:
literal|null
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'_'
argument_list|)
argument_list|)
operator|.
name|routing
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

