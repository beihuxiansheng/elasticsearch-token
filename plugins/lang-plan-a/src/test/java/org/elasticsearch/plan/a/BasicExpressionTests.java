begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.plan.a
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plan
operator|.
name|a
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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_class
DECL|class|BasicExpressionTests
specifier|public
class|class
name|BasicExpressionTests
extends|extends
name|ScriptTestCase
block|{
comment|/** simple tests returning a constant value */
DECL|method|testReturnConstant
specifier|public
name|void
name|testReturnConstant
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"return 5;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7L
argument_list|,
name|exec
argument_list|(
literal|"return 7L;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7.0
argument_list|,
name|exec
argument_list|(
literal|"return 7.0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|32.0F
argument_list|,
name|exec
argument_list|(
literal|"return 32.0F;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|255
argument_list|,
name|exec
argument_list|(
literal|"return (byte)255;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|5
argument_list|,
name|exec
argument_list|(
literal|"return (short)5;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|exec
argument_list|(
literal|"return \"string\";"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return true;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return false;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"return null;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReturnConstantChar
specifier|public
name|void
name|testReturnConstantChar
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|exec
argument_list|(
literal|"return 'x';"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstantCharTruncation
specifier|public
name|void
name|testConstantCharTruncation
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|'è '
argument_list|,
name|exec
argument_list|(
literal|"return (char)100000;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** declaring variables for primitive types */
DECL|method|testDeclareVariable
specifier|public
name|void
name|testDeclareVariable
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int i = 5; return i;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7L
argument_list|,
name|exec
argument_list|(
literal|"long l = 7; return l;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7.0
argument_list|,
name|exec
argument_list|(
literal|"double d = 7; return d;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|32.0F
argument_list|,
name|exec
argument_list|(
literal|"float f = 32F; return f;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|255
argument_list|,
name|exec
argument_list|(
literal|"byte b = (byte)255; return b;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|5
argument_list|,
name|exec
argument_list|(
literal|"short s = (short)5; return s;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|exec
argument_list|(
literal|"String s = \"string\"; return s;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean v = true; return v;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean v = false; return v;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCast
specifier|public
name|void
name|testCast
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"return (int)1.0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|100
argument_list|,
name|exec
argument_list|(
literal|"double x = 100; return (byte)x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"Map x = new HashMap();\n"
operator|+
literal|"Object y = x;\n"
operator|+
literal|"((Map)y).put(2, 3);\n"
operator|+
literal|"return x.get(2);\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCat
specifier|public
name|void
name|testCat
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"aaabbb"
argument_list|,
name|exec
argument_list|(
literal|"return \"aaa\" + \"bbb\";"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaabbb"
argument_list|,
name|exec
argument_list|(
literal|"String aaa = \"aaa\", bbb = \"bbb\"; return aaa + bbb;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaabbbbbbbbb"
argument_list|,
name|exec
argument_list|(
literal|"String aaa = \"aaa\", bbb = \"bbb\"; int x;\n"
operator|+
literal|"for (; x< 3; ++x) \n"
operator|+
literal|"    aaa += bbb;\n"
operator|+
literal|"return aaa;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testComp
specifier|public
name|void
name|testComp
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return 2< 3;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"int x = 4; char y = 2; return x< y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return 3<= 3;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"int x = 3; char y = 3; return x<= y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return 2> 3;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"int x = 4; long y = 2; return x> y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return 3>= 4;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"double x = 3; float y = 3; return x>= y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return 3 == 4;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"double x = 3; float y = 3; return x == y;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return 3 != 4;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"double x = 3; float y = 3; return x != y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**       * Test boxed objects in various places      */
DECL|method|testBoxing
specifier|public
name|void
name|testBoxing
parameter_list|()
block|{
comment|// return
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"return input.get(\"x\");"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"x"
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// assignment
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"int y = (Integer)input.get(\"x\"); return y;"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"x"
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// comparison
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return 5> (Integer)input.get(\"x\");"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"x"
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBool
specifier|public
name|void
name|testBool
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return true&& true;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean a = true, b = false; return a&& b;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return true || true;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean a = true, b = false; return a || b;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConditional
specifier|public
name|void
name|testConditional
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return x> 3 ? 1 : 0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"String a = null; return a != null ? 1 : 0;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrecedence
specifier|public
name|void
name|testPrecedence
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return (x+x)/x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"boolean t = true, f = false; return t&& (f || t);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

