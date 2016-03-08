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
comment|/** Tests for increment/decrement operators across all data types */
end_comment

begin_class
DECL|class|IncrementTests
specifier|public
class|class
name|IncrementTests
extends|extends
name|ScriptTestCase
block|{
comment|/** incrementing byte values */
DECL|method|testIncrementByte
specifier|public
name|void
name|testIncrementByte
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)0; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)0; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)0; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)0; return --x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** incrementing char values */
DECL|method|testIncrementChar
specifier|public
name|void
name|testIncrementChar
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|0
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)0; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)1; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)0; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** incrementing short values */
DECL|method|testIncrementShort
specifier|public
name|void
name|testIncrementShort
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)0; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)0; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)0; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"short x = (short)0; return --x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** incrementing integer values */
DECL|method|testIncrementInt
specifier|public
name|void
name|testIncrementInt
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = 0; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = 0; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 0; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 0; return --x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** incrementing long values */
DECL|method|testIncrementLong
specifier|public
name|void
name|testIncrementLong
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = 0; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|exec
argument_list|(
literal|"long x = 0; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = 0; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1L
argument_list|,
name|exec
argument_list|(
literal|"long x = 0; return --x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** incrementing float values */
DECL|method|testIncrementFloat
specifier|public
name|void
name|testIncrementFloat
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0F
argument_list|,
name|exec
argument_list|(
literal|"float x = 0F; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0F
argument_list|,
name|exec
argument_list|(
literal|"float x = 0F; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"float x = 0F; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1F
argument_list|,
name|exec
argument_list|(
literal|"float x = 0F; return --x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** incrementing double values */
DECL|method|testIncrementDouble
specifier|public
name|void
name|testIncrementDouble
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0D
argument_list|,
name|exec
argument_list|(
literal|"double x = 0.0; return x++;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0D
argument_list|,
name|exec
argument_list|(
literal|"double x = 0.0; return x--;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1D
argument_list|,
name|exec
argument_list|(
literal|"double x = 0.0; return ++x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1D
argument_list|,
name|exec
argument_list|(
literal|"double x = 0.0; return --x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

