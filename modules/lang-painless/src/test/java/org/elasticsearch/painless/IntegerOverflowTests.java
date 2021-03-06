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

begin_comment
comment|/** Tests integer overflow cases */
end_comment

begin_class
DECL|class|IntegerOverflowTests
specifier|public
class|class
name|IntegerOverflowTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testAssignmentAdditionOverflow
specifier|public
name|void
name|testAssignmentAdditionOverflow
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0
operator|+
literal|128
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 0; x += 128; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0
operator|+
operator|-
literal|129
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 0; x += -129; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0
operator|+
literal|32768
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 0; x += 32768; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0
operator|+
operator|-
literal|32769
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 0; x += -32769; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|0
operator|+
literal|65536
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 0; x += 65536; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|0
operator|+
operator|-
literal|65536
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 0; x += -65536; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|1
operator|+
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; x += 2147483647; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2
operator|+
operator|-
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = -2; x += -2147483647; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; x += 9223372036854775807L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2L
operator|+
operator|-
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = -2; x += -9223372036854775807L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAssignmentSubtractionOverflow
specifier|public
name|void
name|testAssignmentSubtractionOverflow
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0
operator|-
operator|-
literal|128
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 0; x -= -128; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0
operator|-
literal|129
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 0; x -= 129; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0
operator|-
operator|-
literal|32768
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 0; x -= -32768; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0
operator|-
literal|32769
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 0; x -= 32769; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|0
operator|-
operator|-
literal|65536
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 0; x -= -65536; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|0
operator|-
literal|65536
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 0; x -= 65536; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|1
operator|-
operator|-
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; x -= -2147483647; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2
operator|-
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = -2; x -= 2147483647; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|1L
operator|-
operator|-
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; x -= -9223372036854775807L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2L
operator|-
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = -2; x -= 9223372036854775807L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAssignmentMultiplicationOverflow
specifier|public
name|void
name|testAssignmentMultiplicationOverflow
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|2
operator|*
literal|128
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 2; x *= 128; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|2
operator|*
operator|-
literal|128
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 2; x *= -128; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|2
operator|*
literal|65536
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 2; x *= 65536; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|2
operator|*
operator|-
literal|65536
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 2; x *= -65536; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|2
operator|*
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = 2; x *= 2147483647; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
operator|*
operator|-
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = 2; x *= -2147483647; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|2L
operator|*
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = 2; x *= 9223372036854775807L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
operator|*
operator|-
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = 2; x *= -9223372036854775807L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAssignmentDivisionOverflow
specifier|public
name|void
name|testAssignmentDivisionOverflow
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|-
literal|128
operator|/
operator|-
literal|1
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -128; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
call|(
name|short
call|)
argument_list|(
operator|-
literal|32768
operator|/
operator|-
literal|1
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -32768; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// cannot happen for char: unsigned
comment|// int
name|assertEquals
argument_list|(
operator|(
operator|-
literal|2147483647
operator|-
literal|1
operator|)
operator|/
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = -2147483647 - 1; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
operator|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|)
operator|/
operator|-
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = -9223372036854775807L - 1L; x /=-1L; return x;"
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|128
argument_list|,
name|exec
argument_list|(
literal|"byte x = 127; ++x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|128
argument_list|,
name|exec
argument_list|(
literal|"byte x = 127; x++; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|129
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -128; --x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|129
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -128; x--; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|32768
argument_list|,
name|exec
argument_list|(
literal|"short x = 32767; ++x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|32768
argument_list|,
name|exec
argument_list|(
literal|"short x = 32767; x++; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|32769
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -32768; --x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|32769
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -32768; x--; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|65536
argument_list|,
name|exec
argument_list|(
literal|"char x = 65535; ++x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|65536
argument_list|,
name|exec
argument_list|(
literal|"char x = 65535; x++; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = (char) 0; --x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = (char) 0; x--; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|2147483647
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 2147483647; ++x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2147483647
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 2147483647; x++; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2147483648
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = (int) -2147483648L; --x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2147483648
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = (int) -2147483648L; x--; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|9223372036854775807L
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; ++x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9223372036854775807L
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; x++; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|-
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = -9223372036854775807L - 1L; --x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|-
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = -9223372036854775807L - 1L; x--; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddition
specifier|public
name|void
name|testAddition
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2147483647
operator|+
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = 2147483647; int y = 2147483647; return x + y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9223372036854775807L
operator|+
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; long y = 9223372036854775807L; return x + y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAdditionConst
specifier|public
name|void
name|testAdditionConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2147483647
operator|+
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"return 2147483647 + 2147483647;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9223372036854775807L
operator|+
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"return 9223372036854775807L + 9223372036854775807L;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubtraction
specifier|public
name|void
name|testSubtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|10
operator|-
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = -10; int y = 2147483647; return x - y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|10L
operator|-
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = -10L; long y = 9223372036854775807L; return x - y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubtractionConst
specifier|public
name|void
name|testSubtractionConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|10
operator|-
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"return -10 - 2147483647;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|10L
operator|-
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"return -10L - 9223372036854775807L;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiplication
specifier|public
name|void
name|testMultiplication
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2147483647
operator|*
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"int x = 2147483647; int y = 2147483647; return x * y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9223372036854775807L
operator|*
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"long x = 9223372036854775807L; long y = 9223372036854775807L; return x * y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiplicationConst
specifier|public
name|void
name|testMultiplicationConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2147483647
operator|*
literal|2147483647
argument_list|,
name|exec
argument_list|(
literal|"return 2147483647 * 2147483647;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9223372036854775807L
operator|*
literal|9223372036854775807L
argument_list|,
name|exec
argument_list|(
literal|"return 9223372036854775807L * 9223372036854775807L;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivision
specifier|public
name|void
name|testDivision
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|(
operator|-
literal|2147483647
operator|-
literal|1
operator|)
operator|/
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = -2147483648; int y = -1; return x / y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|)
operator|/
operator|-
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = -9223372036854775808L; long y = -1L; return x / y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivisionConst
specifier|public
name|void
name|testDivisionConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|(
operator|-
literal|2147483647
operator|-
literal|1
operator|)
operator|/
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (-2147483648) / -1;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|)
operator|/
operator|-
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return (-9223372036854775808L) / -1L;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegationOverflow
specifier|public
name|void
name|testNegationOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
operator|(
operator|-
literal|2147483647
operator|-
literal|1
operator|)
argument_list|,
name|exec
argument_list|(
literal|"int x = -2147483648; x = -x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
operator|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|)
argument_list|,
name|exec
argument_list|(
literal|"long x = -9223372036854775808L; x = -x; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegationOverflowConst
specifier|public
name|void
name|testNegationOverflowConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
operator|(
operator|-
literal|2147483647
operator|-
literal|1
operator|)
argument_list|,
name|exec
argument_list|(
literal|"int x = -(-2147483648); return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
operator|(
operator|-
literal|9223372036854775807L
operator|-
literal|1L
operator|)
argument_list|,
name|exec
argument_list|(
literal|"long x = -(-9223372036854775808L); return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

