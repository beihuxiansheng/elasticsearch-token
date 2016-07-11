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

begin_class
DECL|class|PromotionTests
specifier|public
class|class
name|PromotionTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testBinaryPromotion
specifier|public
name|void
name|testBinaryPromotion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// byte/byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/char
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/short
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/int
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/long
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/float
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/double
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/byte
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/short
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/int
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/long
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/float
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/double
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"char x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/byte
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/char
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/int
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/long
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/float
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/double
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"short x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/byte
name|assertEquals
argument_list|(
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/char
name|assertEquals
argument_list|(
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/short
name|assertEquals
argument_list|(
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/int
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/long
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/float
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/double
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/byte
name|assertEquals
argument_list|(
literal|1L
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/char
name|assertEquals
argument_list|(
literal|1L
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/short
name|assertEquals
argument_list|(
literal|1L
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/int
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/long
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/float
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/double
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"long x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/byte
name|assertEquals
argument_list|(
literal|1F
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/char
name|assertEquals
argument_list|(
literal|1F
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/short
name|assertEquals
argument_list|(
literal|1F
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/int
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/long
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/float
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/double
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"float x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/byte
name|assertEquals
argument_list|(
literal|1.0
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; byte y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/char
name|assertEquals
argument_list|(
literal|1.0
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; char y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/short
name|assertEquals
argument_list|(
literal|1.0
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; short y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/int
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; int y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/long
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; long y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/float
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; float y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/double
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; double y = 1; return x+y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBinaryPromotionConst
specifier|public
name|void
name|testBinaryPromotionConst
parameter_list|()
throws|throws
name|Exception
block|{
comment|// byte/byte
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/char
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/short
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/int
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/long
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/float
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte/double
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return (byte)1 + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/byte
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/char
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/short
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/int
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/long
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/float
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char/double
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return (char)1 + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/byte
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/char
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/short
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/int
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/long
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/float
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short/double
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return (short)1 + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/byte
name|assertEquals
argument_list|(
literal|1
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1 + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/char
name|assertEquals
argument_list|(
literal|1
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1 + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/short
name|assertEquals
argument_list|(
literal|1
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1 + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/int
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1 + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/long
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return 1 + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/float
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return 1 + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int/double
name|assertEquals
argument_list|(
literal|1
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return 1 + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/byte
name|assertEquals
argument_list|(
literal|1L
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1L + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/char
name|assertEquals
argument_list|(
literal|1L
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1L + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/short
name|assertEquals
argument_list|(
literal|1L
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1L + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/int
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1L + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/long
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return 1L + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/float
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return 1L + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long/double
name|assertEquals
argument_list|(
literal|1L
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return 1L + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/byte
name|assertEquals
argument_list|(
literal|1F
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1F + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/char
name|assertEquals
argument_list|(
literal|1F
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1F + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/short
name|assertEquals
argument_list|(
literal|1F
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1F + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/int
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1F + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/long
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return 1F + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/float
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return 1F + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float/double
name|assertEquals
argument_list|(
literal|1F
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return 1F + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/byte
name|assertEquals
argument_list|(
literal|1.0
operator|+
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + (byte)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/char
name|assertEquals
argument_list|(
literal|1.0
operator|+
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + (char)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/short
name|assertEquals
argument_list|(
literal|1.0
operator|+
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + (short)1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/int
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + 1;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/long
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + 1L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/float
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + 1F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double/double
name|assertEquals
argument_list|(
literal|1.0
operator|+
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"return 1.0 + 1.0;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

