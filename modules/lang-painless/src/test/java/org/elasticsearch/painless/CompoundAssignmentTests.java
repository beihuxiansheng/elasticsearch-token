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
comment|/**  * Tests compound assignments (+=, etc) across all data types  */
end_comment

begin_class
DECL|class|CompoundAssignmentTests
specifier|public
class|class
name|CompoundAssignmentTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testAddition
specifier|public
name|void
name|testAddition
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x += -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x += -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"char x = 5; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|5
argument_list|,
name|exec
argument_list|(
literal|"char x = 10; x += -5; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x += -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x += -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float
name|assertEquals
argument_list|(
literal|15F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x += -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double
name|assertEquals
argument_list|(
literal|15D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x += 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x += -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubtraction
specifier|public
name|void
name|testSubtraction
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x -= 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x -= 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"char x = 5; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|5
argument_list|,
name|exec
argument_list|(
literal|"char x = 10; x -= 5; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x -= 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x -= 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float
name|assertEquals
argument_list|(
literal|15F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x -= 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double
name|assertEquals
argument_list|(
literal|15D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x -= -10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x -= 10; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiplication
specifier|public
name|void
name|testMultiplication
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x *= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x *= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"char x = 5; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x *= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x *= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float
name|assertEquals
argument_list|(
literal|15F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x *= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double
name|assertEquals
argument_list|(
literal|15D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x *= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x *= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivision
specifier|public
name|void
name|testDivision
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = 45; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"byte x = 5; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = 45; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"short x = 5; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"char x = 45; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = 45; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = 45; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float
name|assertEquals
argument_list|(
literal|15F
argument_list|,
name|exec
argument_list|(
literal|"float x = 45f; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5F
argument_list|,
name|exec
argument_list|(
literal|"float x = 5f; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double
name|assertEquals
argument_list|(
literal|15D
argument_list|,
name|exec
argument_list|(
literal|"double x = 45.0; x /= 3; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|5D
argument_list|,
name|exec
argument_list|(
literal|"double x = 5.0; x /= -1; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivisionByZero
specifier|public
name|void
name|testDivisionByZero
parameter_list|()
block|{
comment|// byte
try|try
block|{
name|exec
argument_list|(
literal|"byte x = 1; x /= 0; return x;"
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
literal|"short x = 1; x /= 0; return x;"
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
comment|// char
try|try
block|{
name|exec
argument_list|(
literal|"char x = 1; x /= 0; return x;"
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
comment|// int
try|try
block|{
name|exec
argument_list|(
literal|"int x = 1; x /= 0; return x;"
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
literal|"long x = 1; x /= 0; return x;"
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
DECL|method|testRemainder
specifier|public
name|void
name|testRemainder
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|,
name|exec
argument_list|(
literal|"byte x = 15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|3
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|,
name|exec
argument_list|(
literal|"short x = 15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|3
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|3
argument_list|,
name|exec
argument_list|(
literal|"char x = (char) 15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"int x = 15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3
argument_list|,
name|exec
argument_list|(
literal|"int x = -15; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"long x = 15L; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"long x = -15L; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float
name|assertEquals
argument_list|(
literal|3F
argument_list|,
name|exec
argument_list|(
literal|"float x = 15F; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3F
argument_list|,
name|exec
argument_list|(
literal|"float x = -15F; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double
name|assertEquals
argument_list|(
literal|3D
argument_list|,
name|exec
argument_list|(
literal|"double x = 15.0; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3D
argument_list|,
name|exec
argument_list|(
literal|"double x = -15.0; x %= 4; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLeftShift
specifier|public
name|void
name|testLeftShift
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|60
argument_list|,
name|exec
argument_list|(
literal|"byte x = 15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|60
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|60
argument_list|,
name|exec
argument_list|(
literal|"short x = 15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|60
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|60
argument_list|,
name|exec
argument_list|(
literal|"char x = (char) 15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|exec
argument_list|(
literal|"int x = 15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|60
argument_list|,
name|exec
argument_list|(
literal|"int x = -15; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|60L
argument_list|,
name|exec
argument_list|(
literal|"long x = 15L; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|60L
argument_list|,
name|exec
argument_list|(
literal|"long x = -15L; x<<= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRightShift
specifier|public
name|void
name|testRightShift
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = 60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = 60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"char x = (char) 60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = 60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = -60; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = 60L; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = -60L; x>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnsignedRightShift
specifier|public
name|void
name|testUnsignedRightShift
parameter_list|()
block|{
comment|// byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = 60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|15
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte) -60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = 60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|15
argument_list|,
name|exec
argument_list|(
literal|"short x = (short) -60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|15
argument_list|,
name|exec
argument_list|(
literal|"char x = (char) 60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|exec
argument_list|(
literal|"int x = 60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|60
operator|>>>
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int x = -60; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|15L
argument_list|,
name|exec
argument_list|(
literal|"long x = 60L; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|60L
operator|>>>
literal|2
argument_list|,
name|exec
argument_list|(
literal|"long x = -60L; x>>>= 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAnd
specifier|public
name|void
name|testAnd
parameter_list|()
block|{
comment|// boolean
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true; x&= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true; x&= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; x&= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; x&= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = true; x&= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = true; x&= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; x&= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; x&= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = true; x[0]&= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = true; x[0]&= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = false; x[0]&= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = false; x[0]&= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = true; x[0]&= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = true; x[0]&= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = false; x[0]&= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = false; x[0]&= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|13
operator|&
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 13; x&= 14; return x;"
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
literal|13
operator|&
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 13; x&= 14; return x;"
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
literal|13
operator|&
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 13; x&= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|13
operator|&
literal|14
argument_list|,
name|exec
argument_list|(
literal|"int x = 13; x&= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|13
operator|&
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"long x = 13L; x&= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOr
specifier|public
name|void
name|testOr
parameter_list|()
block|{
comment|// boolean
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true; x |= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true; x |= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; x |= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; x |= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = true; x |= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = true; x |= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = false; x |= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; x |= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = true; x[0] |= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = true; x[0] |= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = false; x[0] |= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = false; x[0] |= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = true; x[0] |= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = true; x[0] |= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = false; x[0] |= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = false; x[0] |= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|13
operator||
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 13; x |= 14; return x;"
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
literal|13
operator||
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 13; x |= 14; return x;"
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
literal|13
operator||
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 13; x |= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|13
operator||
literal|14
argument_list|,
name|exec
argument_list|(
literal|"int x = 13; x |= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|13
operator||
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"long x = 13L; x |= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testXor
specifier|public
name|void
name|testXor
parameter_list|()
block|{
comment|// boolean
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true; x ^= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true; x ^= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; x ^= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; x ^= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = true; x ^= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = true; x ^= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = false; x ^= true; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; x ^= false; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = true; x[0] ^= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = true; x[0] ^= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = false; x[0] ^= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean[] x = new boolean[1]; x[0] = false; x[0] ^= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = true; x[0] ^= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = true; x[0] ^= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = false; x[0] ^= true; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def[] x = new def[1]; x[0] = false; x[0] ^= false; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|13
operator|^
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"byte x = 13; x ^= 14; return x;"
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
literal|13
operator|^
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"short x = 13; x ^= 14; return x;"
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
literal|13
operator|^
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"char x = 13; x ^= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|13
operator|^
literal|14
argument_list|,
name|exec
argument_list|(
literal|"int x = 13; x ^= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|13
operator|^
literal|14
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"long x = 13L; x ^= 14; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

