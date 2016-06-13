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
comment|/** Tests for unary operators across different types */
end_comment

begin_class
DECL|class|UnaryTests
specifier|public
class|class
name|UnaryTests
extends|extends
name|ScriptTestCase
block|{
comment|/** basic tests */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return !true;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; return !x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2
argument_list|,
name|exec
argument_list|(
literal|"return ~1;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2
argument_list|,
name|exec
argument_list|(
literal|"byte x = 1; return ~x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return +1;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"double x = 1; return +x;"
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
literal|"return -1;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|2
argument_list|,
name|exec
argument_list|(
literal|"short x = 2; return -x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegationInt
specifier|public
name|void
name|testNegationInt
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return -1;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return -(-1);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"return -0;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPlus
specifier|public
name|void
name|testPlus
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"byte x = (byte)-1; return +x"
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
literal|"short x = (short)-1; return +x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|65535
argument_list|,
name|exec
argument_list|(
literal|"char x = (char)-1; return +x"
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
literal|"int x = -1; return +x"
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
literal|"long x = -1L; return +x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1.0F
argument_list|,
name|exec
argument_list|(
literal|"float x = -1F; return +x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1.0
argument_list|,
name|exec
argument_list|(
literal|"double x = -1.0; return +x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

