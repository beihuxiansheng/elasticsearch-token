begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|CodepointSetGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
operator|.
name|NodeClient
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
name|logging
operator|.
name|DeprecationLogger
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
name|org
operator|.
name|mockito
operator|.
name|InOrder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|inOrder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * Tests {@link DeprecationRestHandler}.  */
end_comment

begin_class
DECL|class|DeprecationRestHandlerTests
specifier|public
class|class
name|DeprecationRestHandlerTests
extends|extends
name|ESTestCase
block|{
DECL|field|handler
specifier|private
specifier|final
name|RestHandler
name|handler
init|=
name|mock
argument_list|(
name|RestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Note: Headers should only use US ASCII (and this inevitably becomes one!).      */
DECL|field|deprecationMessage
specifier|private
specifier|final
name|String
name|deprecationMessage
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
decl_stmt|;
DECL|field|deprecationLogger
specifier|private
specifier|final
name|DeprecationLogger
name|deprecationLogger
init|=
name|mock
argument_list|(
name|DeprecationLogger
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testNullHandler
specifier|public
name|void
name|testNullHandler
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|DeprecationRestHandler
argument_list|(
literal|null
argument_list|,
name|deprecationMessage
argument_list|,
name|deprecationLogger
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidDeprecationMessageThrowsException
specifier|public
name|void
name|testInvalidDeprecationMessageThrowsException
parameter_list|()
block|{
name|String
name|invalidDeprecationMessage
init|=
name|randomFrom
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|,
literal|"     "
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
name|DeprecationRestHandler
argument_list|(
name|handler
argument_list|,
name|invalidDeprecationMessage
argument_list|,
name|deprecationLogger
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullDeprecationLogger
specifier|public
name|void
name|testNullDeprecationLogger
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|DeprecationRestHandler
argument_list|(
name|handler
argument_list|,
name|deprecationMessage
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHandleRequestLogsWarningThenForwards
specifier|public
name|void
name|testHandleRequestLogsWarningThenForwards
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
name|mock
argument_list|(
name|RestRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|RestChannel
name|channel
init|=
name|mock
argument_list|(
name|RestChannel
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeClient
name|client
init|=
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|DeprecationRestHandler
name|deprecatedHandler
init|=
operator|new
name|DeprecationRestHandler
argument_list|(
name|handler
argument_list|,
name|deprecationMessage
argument_list|,
name|deprecationLogger
argument_list|)
decl_stmt|;
comment|// test it
name|deprecatedHandler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|InOrder
name|inOrder
init|=
name|inOrder
argument_list|(
name|handler
argument_list|,
name|request
argument_list|,
name|channel
argument_list|,
name|deprecationLogger
argument_list|)
decl_stmt|;
comment|// log, then forward
name|inOrder
operator|.
name|verify
argument_list|(
name|deprecationLogger
argument_list|)
operator|.
name|deprecated
argument_list|(
name|deprecationMessage
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verifyNoMoreInteractions
argument_list|()
expr_stmt|;
block|}
DECL|method|testValidHeaderValue
specifier|public
name|void
name|testValidHeaderValue
parameter_list|()
block|{
name|ASCIIHeaderGenerator
name|generator
init|=
operator|new
name|ASCIIHeaderGenerator
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|generator
operator|.
name|ofCodeUnitsLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// empty text, not a valid header
name|assertFalse
argument_list|(
name|DeprecationRestHandler
operator|.
name|validHeaderValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|Exception
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
name|DeprecationRestHandler
operator|.
name|requireValidHeader
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"header value must contain only US ASCII text"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|DeprecationRestHandler
operator|.
name|validHeaderValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|value
argument_list|,
name|DeprecationRestHandler
operator|.
name|requireValidHeader
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidHeaderValue
specifier|public
name|void
name|testInvalidHeaderValue
parameter_list|()
block|{
name|ASCIIHeaderGenerator
name|generator
init|=
operator|new
name|ASCIIHeaderGenerator
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|generator
operator|.
name|ofCodeUnitsLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|25
argument_list|)
operator|+
name|randomFrom
argument_list|(
literal|'\t'
argument_list|,
literal|'\0'
argument_list|,
literal|'\n'
argument_list|,
operator|(
name|char
operator|)
literal|27
comment|/* ESC */
argument_list|,
operator|(
name|char
operator|)
literal|31
comment|/* unit separator*/
argument_list|,
operator|(
name|char
operator|)
literal|127
comment|/* DEL */
argument_list|)
operator|+
name|generator
operator|.
name|ofCodeUnitsLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|DeprecationRestHandler
operator|.
name|validHeaderValue
argument_list|(
name|value
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
name|DeprecationRestHandler
operator|.
name|requireValidHeader
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidHeaderValueNull
specifier|public
name|void
name|testInvalidHeaderValueNull
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|DeprecationRestHandler
operator|.
name|validHeaderValue
argument_list|(
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
name|DeprecationRestHandler
operator|.
name|requireValidHeader
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidHeaderValueEmpty
specifier|public
name|void
name|testInvalidHeaderValueEmpty
parameter_list|()
block|{
name|String
name|blank
init|=
name|randomFrom
argument_list|(
literal|""
argument_list|,
literal|"\t"
argument_list|,
literal|"    "
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|DeprecationRestHandler
operator|.
name|validHeaderValue
argument_list|(
name|blank
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
name|DeprecationRestHandler
operator|.
name|requireValidHeader
argument_list|(
name|blank
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@code ASCIIHeaderGenerator} only uses characters expected to be valid in headers (simplified US-ASCII).      */
DECL|class|ASCIIHeaderGenerator
specifier|private
specifier|static
class|class
name|ASCIIHeaderGenerator
extends|extends
name|CodepointSetGenerator
block|{
comment|/**          * Create a character array for characters [{@code from}, {@code to}].          *          * @param from Starting code point (inclusive).          * @param to Ending code point (inclusive).          * @return Never {@code null}.          */
DECL|method|asciiFromTo
specifier|static
name|char
index|[]
name|asciiFromTo
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|to
operator|-
name|from
operator|+
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|from
init|;
name|i
operator|<=
name|to
condition|;
operator|++
name|i
control|)
block|{
name|chars
index|[
name|i
operator|-
name|from
index|]
operator|=
operator|(
name|char
operator|)
name|i
expr_stmt|;
block|}
return|return
name|chars
return|;
block|}
comment|/**          * Create a generator for characters [32, 126].          */
DECL|method|ASCIIHeaderGenerator
name|ASCIIHeaderGenerator
parameter_list|()
block|{
name|super
argument_list|(
name|asciiFromTo
argument_list|(
literal|32
argument_list|,
literal|126
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

