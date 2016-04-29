begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/** Tests integer overflow with numeric overflow disabled */
end_comment

begin_class
DECL|class|IntegerOverflowDisabledTests
specifier|public
class|class
name|IntegerOverflowDisabledTests
extends|extends
name|ScriptTestCase
block|{
comment|/** wire overflow to true for all tests */
annotation|@
name|Override
DECL|method|exec
specifier|public
name|Object
name|exec
parameter_list|(
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
name|exec
argument_list|(
name|script
argument_list|,
name|vars
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|CompilerSettings
operator|.
name|NUMERIC_OVERFLOW
argument_list|,
literal|"false"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testAssignmentAdditionOverflow
specifier|public
name|void
name|testAssignmentAdditionOverflow
parameter_list|()
block|{
comment|// byte
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 0; x += 128; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 0; x += -129; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// short
try|try
block|{
name|exec
argument_list|(
literal|"short x = 0; x += 32768; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 0; x += -32769; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// char
try|try
block|{
name|exec
argument_list|(
literal|"char x = 0; x += 65536; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"char x = 0; x += -65536; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// int
try|try
block|{
name|exec
argument_list|(
literal|"int x = 1; x += 2147483647; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"int x = -2; x += -2147483647; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// long
try|try
block|{
name|exec
argument_list|(
literal|"long x = 1; x += 9223372036854775807L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -2; x += -9223372036854775807L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAssignmentSubtractionOverflow
specifier|public
name|void
name|testAssignmentSubtractionOverflow
parameter_list|()
block|{
comment|// byte
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 0; x -= -128; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 0; x -= 129; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// short
try|try
block|{
name|exec
argument_list|(
literal|"short x = 0; x -= -32768; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 0; x -= 32769; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// char
try|try
block|{
name|exec
argument_list|(
literal|"char x = 0; x -= -65536; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"char x = 0; x -= 65536; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// int
try|try
block|{
name|exec
argument_list|(
literal|"int x = 1; x -= -2147483647; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"int x = -2; x -= 2147483647; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// long
try|try
block|{
name|exec
argument_list|(
literal|"long x = 1; x -= -9223372036854775807L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -2; x -= 9223372036854775807L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAssignmentMultiplicationOverflow
specifier|public
name|void
name|testAssignmentMultiplicationOverflow
parameter_list|()
block|{
comment|// byte
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 2; x *= 128; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 2; x *= -128; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// char
try|try
block|{
name|exec
argument_list|(
literal|"char x = 2; x *= 65536; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"char x = 2; x *= -65536; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// int
try|try
block|{
name|exec
argument_list|(
literal|"int x = 2; x *= 2147483647; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"int x = 2; x *= -2147483647; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// long
try|try
block|{
name|exec
argument_list|(
literal|"long x = 2; x *= 9223372036854775807L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = 2; x *= -9223372036854775807L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAssignmentDivisionOverflow
specifier|public
name|void
name|testAssignmentDivisionOverflow
parameter_list|()
block|{
comment|// byte
try|try
block|{
name|exec
argument_list|(
literal|"byte x = (byte) -128; x /= -1; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// short
try|try
block|{
name|exec
argument_list|(
literal|"short x = (short) -32768; x /= -1; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// cannot happen for char: unsigned
comment|// int
try|try
block|{
name|exec
argument_list|(
literal|"int x = -2147483647 - 1; x /= -1; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// long
try|try
block|{
name|exec
argument_list|(
literal|"long x = -9223372036854775807L - 1L; x /=-1L; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testIncrementOverFlow
specifier|public
name|void
name|testIncrementOverFlow
parameter_list|()
throws|throws
name|Exception
block|{
comment|// byte
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 127; ++x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 127; x++; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = (byte) -128; --x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"byte x = (byte) -128; x--; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// short
try|try
block|{
name|exec
argument_list|(
literal|"short x = 32767; ++x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"short x = 32767; x++; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"short x = (short) -32768; --x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"short x = (short) -32768; x--; return x;"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// char
try|try
block|{
name|exec
argument_list|(
literal|"char x = 65535; ++x; return x;"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"char x = 65535; x++; return x;"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"char x = (char) 0; --x; return x;"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"char x = (char) 0; x--; return x;"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// int
try|try
block|{
name|exec
argument_list|(
literal|"int x = 2147483647; ++x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"int x = 2147483647; x++; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"int x = (int) -2147483648L; --x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"int x = (int) -2147483648L; x--; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// long
try|try
block|{
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; ++x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; x++; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -9223372036854775807L - 1L; --x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -9223372036854775807L - 1L; x--; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAddition
specifier|public
name|void
name|testAddition
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = 2147483647; int y = 2147483647; return x + y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; long y = 9223372036854775807L; return x + y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAdditionConst
specifier|public
name|void
name|testAdditionConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return 2147483647 + 2147483647;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 9223372036854775807L + 9223372036854775807L;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testSubtraction
specifier|public
name|void
name|testSubtraction
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = -10; int y = 2147483647; return x - y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -10L; long y = 9223372036854775807L; return x - y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testSubtractionConst
specifier|public
name|void
name|testSubtractionConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return -10 - 2147483647;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return -10L - 9223372036854775807L;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testMultiplication
specifier|public
name|void
name|testMultiplication
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = 2147483647; int y = 2147483647; return x * y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; long y = 9223372036854775807L; return x * y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testMultiplicationConst
specifier|public
name|void
name|testMultiplicationConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return 2147483647 * 2147483647;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 9223372036854775807L * 9223372036854775807L;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testDivision
specifier|public
name|void
name|testDivision
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = -2147483647 - 1; int y = -1; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -9223372036854775808L; long y = -1L; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testDivisionConst
specifier|public
name|void
name|testDivisionConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return (-2147483648) / -1;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return (-9223372036854775808L) / -1L;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testNegationOverflow
specifier|public
name|void
name|testNegationOverflow
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = -2147483648; x = -x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -9223372036854775808L; x = -x; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testNegationOverflowConst
specifier|public
name|void
name|testNegationOverflowConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = -(-2147483648); return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"long x = -(-9223372036854775808L); return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

