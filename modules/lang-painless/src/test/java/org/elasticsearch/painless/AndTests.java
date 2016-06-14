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
comment|/** Tests for and operator across all types */
end_comment

begin_class
DECL|class|AndTests
specifier|public
class|class
name|AndTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|5
operator|&
literal|3
argument_list|,
name|exec
argument_list|(
literal|"return 5& 3;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|&
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"return 5& 3L;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5L
operator|&
literal|3
argument_list|,
name|exec
argument_list|(
literal|"return 5L& 3;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; long y = 3; return x& y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInt
specifier|public
name|void
name|testInt
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|5
operator|&
literal|12
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; int y = 12; return x& y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|&
operator|-
literal|12
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; int y = -12; return x& y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
operator|&
literal|15
operator|&
literal|3
argument_list|,
name|exec
argument_list|(
literal|"int x = 7; int y = 15; int z = 3; return x& y& z;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntConst
specifier|public
name|void
name|testIntConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|5
operator|&
literal|12
argument_list|,
name|exec
argument_list|(
literal|"return 5& 12;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|&
operator|-
literal|12
argument_list|,
name|exec
argument_list|(
literal|"return 5& -12;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
operator|&
literal|15
operator|&
literal|3
argument_list|,
name|exec
argument_list|(
literal|"return 7& 15& 3;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLong
specifier|public
name|void
name|testLong
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|5L
operator|&
literal|12L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; long y = 12; return x& y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5L
operator|&
operator|-
literal|12L
argument_list|,
name|exec
argument_list|(
literal|"long x = 5; long y = -12; return x& y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7L
operator|&
literal|15L
operator|&
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"long x = 7; long y = 15; long z = 3; return x& y& z;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongConst
specifier|public
name|void
name|testLongConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|5L
operator|&
literal|12L
argument_list|,
name|exec
argument_list|(
literal|"return 5L& 12L;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5L
operator|&
operator|-
literal|12L
argument_list|,
name|exec
argument_list|(
literal|"return 5L& -12L;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7L
operator|&
literal|15L
operator|&
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"return 7L& 15L& 3L;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegal
specifier|public
name|void
name|testIllegal
parameter_list|()
throws|throws
name|Exception
block|{
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"float x = (float)4; int y = 1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"double x = (double)4; int y = 1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDef
specifier|public
name|void
name|testDef
parameter_list|()
block|{
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"def x = (float)4; def y = (byte)1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"def x = (double)4; def y = (byte)1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = true;  def y = true; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = true;  def y = false; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; def y = true; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; def y = false; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefTypedLHS
specifier|public
name|void
name|testDefTypedLHS
parameter_list|()
block|{
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"float x = (float)4; def y = (byte)1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"double x = (double)4; def y = (byte)1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = (int)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = (long)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = (int)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = (long)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = (int)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = (long)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = (int)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = (long)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"int x = (int)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = (long)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)4; def y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)4; def y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)4; def y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = (int)4; def y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = (long)4; def y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true;  def y = true; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = true;  def y = false; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; def y = true; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; def y = false; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefTypedRHS
specifier|public
name|void
name|testDefTypedRHS
parameter_list|()
block|{
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"def x = (float)4; byte y = (byte)1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectScriptThrows
argument_list|(
name|ClassCastException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"def x = (double)4; byte y = (byte)1; return x& y"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; byte y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; byte y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; byte y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; byte y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; byte y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; short y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; short y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; short y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; short y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; short y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; char y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; char y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; char y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; char y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; char y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; int y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; int y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; int y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; int y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; int y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; long y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; long y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; long y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; long y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; long y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (byte)4; byte y = (byte)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (short)4; short y = (short)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (char)4; char y = (char)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def x = (int)4; int y = (int)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"def x = (long)4; long y = (long)1; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = true;  boolean y = true; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = true;  boolean y = false; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; boolean y = true; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"def x = false; boolean y = false; return x& y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

