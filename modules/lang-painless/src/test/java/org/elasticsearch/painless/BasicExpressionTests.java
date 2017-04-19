begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
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
literal|"return 5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6L
argument_list|,
name|exec
argument_list|(
literal|"return 6l"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7L
argument_list|,
name|exec
argument_list|(
literal|"return 7L"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7.0d
argument_list|,
name|exec
argument_list|(
literal|"return 7.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18.0d
argument_list|,
name|exec
argument_list|(
literal|"return 18d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|19.0d
argument_list|,
name|exec
argument_list|(
literal|"return 19.0d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20.0d
argument_list|,
name|exec
argument_list|(
literal|"return 20D"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|21.0d
argument_list|,
name|exec
argument_list|(
literal|"return 21.0D"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|32.0F
argument_list|,
name|exec
argument_list|(
literal|"return 32.0f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|33.0F
argument_list|,
name|exec
argument_list|(
literal|"return 33f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|34.0F
argument_list|,
name|exec
argument_list|(
literal|"return 34.0F"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|35.0F
argument_list|,
name|exec
argument_list|(
literal|"return 35F"
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
literal|"return (byte)255"
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
literal|"return (short)5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|exec
argument_list|(
literal|"return \"string\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|exec
argument_list|(
literal|"return 'string'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"return null"
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
literal|"return (char)'x';"
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
DECL|method|testStringEscapes
specifier|public
name|void
name|testStringEscapes
parameter_list|()
block|{
comment|// The readability of this test suffers from having to escape `\` and `"` in java strings. Please be careful. Sorry!
comment|// `\\` is a `\`
name|assertEquals
argument_list|(
literal|"\\string"
argument_list|,
name|exec
argument_list|(
literal|"\"\\\\string\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\string"
argument_list|,
name|exec
argument_list|(
literal|"'\\\\string'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// `\"` is a `"` if surrounded by `"`s
name|assertEquals
argument_list|(
literal|"\"string"
argument_list|,
name|exec
argument_list|(
literal|"\"\\\"string\""
argument_list|)
argument_list|)
expr_stmt|;
name|Exception
name|e
init|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|exec
argument_list|(
literal|"'\\\"string'"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected character ['\\\"]. The only valid escape sequences in strings starting with ['] are [\\\\] and [\\']."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// `\'` is a `'` if surrounded by `'`s
name|e
operator|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|exec
argument_list|(
literal|"\"\\'string\""
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected character [\"\\']. The only valid escape sequences in strings starting with [\"] are [\\\\] and [\\\"]."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"'string"
argument_list|,
name|exec
argument_list|(
literal|"'\\'string'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// We don't break native escapes like new line
name|assertEquals
argument_list|(
literal|"\nstring"
argument_list|,
name|exec
argument_list|(
literal|"\"\nstring\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\nstring"
argument_list|,
name|exec
argument_list|(
literal|"'\nstring'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// And we're ok with strings with multiple escape sequences
name|assertEquals
argument_list|(
literal|"\\str\"in\\g"
argument_list|,
name|exec
argument_list|(
literal|"\"\\\\str\\\"in\\\\g\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"st\\r'i\\ng"
argument_list|,
name|exec
argument_list|(
literal|"'st\\\\r\\'i\\\\ng'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringTermination
specifier|public
name|void
name|testStringTermination
parameter_list|()
block|{
comment|// `'` inside of a string delimited with `"` should be ok
name|assertEquals
argument_list|(
literal|"test'"
argument_list|,
name|exec
argument_list|(
literal|"\"test'\""
argument_list|)
argument_list|)
expr_stmt|;
comment|// `"` inside of a string delimited with `'` should be ok
name|assertEquals
argument_list|(
literal|"test\""
argument_list|,
name|exec
argument_list|(
literal|"'test\"'"
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
DECL|method|testIllegalDefCast
specifier|public
name|void
name|testIllegalDefCast
parameter_list|()
block|{
name|Exception
name|exception
init|=
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
literal|"def x = 1.0; int y = x; return y;"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"cannot be cast"
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|=
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
literal|"def x = (short)1; byte y = x; return y;"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"cannot be cast"
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
comment|/**      * Test boxed def objects in various places      */
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
literal|"return params.get(\"x\");"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"x"
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|true
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
literal|"int y = params.get(\"x\"); return y;"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"x"
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|true
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
literal|"return 5> params.get(\"x\");"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"x"
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|true
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
DECL|method|testNullSafeDeref
specifier|public
name|void
name|testNullSafeDeref
parameter_list|()
block|{
comment|// Objects in general
comment|//   Call
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"String a = null;  return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|exec
argument_list|(
literal|"String a = 'foo'; return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def    a = null;  return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|exec
argument_list|(
literal|"def    a = 'foo'; return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Call with primitive result
name|assertMustBeNullable
argument_list|(
literal|"String a = null;  return a?.length()"
argument_list|)
expr_stmt|;
name|assertMustBeNullable
argument_list|(
literal|"String a = 'foo'; return a?.length()"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def    a = null;  return a?.length()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"def    a = 'foo'; return a?.length()"
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Read shortcut
name|assertMustBeNullable
argument_list|(
literal|"org.elasticsearch.painless.FeatureTest a = null; return a?.x"
argument_list|)
expr_stmt|;
name|assertMustBeNullable
argument_list|(
literal|"org.elasticsearch.painless.FeatureTest a = new org.elasticsearch.painless.FeatureTest(); return a?.x"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def    a = null;  return a?.x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def    a = new org.elasticsearch.painless.FeatureTest(); return a?.x"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Maps
comment|//   Call
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"Map a = null;        return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{}"
argument_list|,
name|exec
argument_list|(
literal|"Map a = [:];         return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = null;        return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{}"
argument_list|,
name|exec
argument_list|(
literal|"def a = [:];         return a?.toString()"
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Call with primitive result
name|assertMustBeNullable
argument_list|(
literal|"Map a = [:];  return a?.size()"
argument_list|)
expr_stmt|;
name|assertMustBeNullable
argument_list|(
literal|"Map a = null; return a?.size()"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = null;        return a?.size()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def a = [:];         return a?.size()"
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Read shortcut
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"Map a = null;        return a?.other"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read shortcut
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"Map a = ['other':1]; return a?.other"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read shortcut
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = null;        return a?.other"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read shortcut
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"def a = ['other':1]; return a?.other"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read shortcut
comment|// Array
comment|// Since you can't invoke methods on arrays we skip the toString and hashCode tests
name|assertMustBeNullable
argument_list|(
literal|"int[] a = null;             return a?.length"
argument_list|)
expr_stmt|;
name|assertMustBeNullable
argument_list|(
literal|"int[] a = new int[] {2, 3}; return a?.length"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = null;               return a?.length"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"def a = new int[] {2, 3};   return a?.length"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Results from maps (should just work but let's test anyway)
name|FeatureTest
name|t
init|=
operator|new
name|FeatureTest
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"Map a = ['thing': params.t]; return a.other?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"Map a = ['thing': params.t]; return a.other?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = ['thing': params.t]; return a.other?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = ['thing': params.t]; return a.other?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"Map a = ['other': params.t]; return a.other?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"Map a = ['other': params.t]; return a.other?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def a = ['other': params.t]; return a.other?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def a = ['other': params.t]; return a.other?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Chains
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"Map a = ['thing': ['cat': params.t]]; return a.other?.cat?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"Map a = ['thing': ['cat': params.t]]; return a.other?.cat?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = ['thing': ['cat': params.t]]; return a.other?.cat?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = ['thing': ['cat': params.t]]; return a.other?.cat?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"Map a = ['other': ['cat': params.t]]; return a.other?.cat?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"Map a = ['other': ['cat': params.t]]; return a.other?.cat?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def a = ['other': ['cat': params.t]]; return a.other?.cat?.getX()"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"def a = ['other': ['cat': params.t]]; return a.other?.cat?.x"
argument_list|,
name|singletonMap
argument_list|(
literal|"t"
argument_list|,
name|t
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assignments
name|assertNull
argument_list|(
name|exec
argument_list|(
literal|"def a = [:];\n"
operator|+
literal|"a.missing_length = a.missing?.length();\n"
operator|+
literal|"return a.missing_length"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"def a = [:];\n"
operator|+
literal|"a.missing = 'foo';\n"
operator|+
literal|"a.missing_length = a.missing?.length();\n"
operator|+
literal|"return a.missing_length"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Writes, all unsupported at this point
comment|//        assertEquals(null, exec("org.elasticsearch.painless.FeatureTest a = null; return a?.x"));            // Read field
comment|//        assertEquals(null, exec("org.elasticsearch.painless.FeatureTest a = null; a?.x = 7; return a?.x"));  // Write field
comment|//        assertEquals(null, exec("Map a = null; a?.other = 'wow'; return a?.other")); // Write shortcut
comment|//        assertEquals(null, exec("def a = null; a?.other = 'cat'; return a?.other")); // Write shortcut
comment|//        assertEquals(null, exec("Map a = ['thing': 'bar']; a.other?.cat = 'no'; return a.other?.cat"));
comment|//        assertEquals(null, exec("def a = ['thing': 'bar']; a.other?.cat = 'no'; return a.other?.cat"));
comment|//        assertEquals(null, exec("Map a = ['thing': 'bar']; a.other?.cat?.dog = 'wombat'; return a.other?.cat?.dog"));
comment|//        assertEquals(null, exec("def a = ['thing': 'bar']; a.other?.cat?.dog = 'wombat'; return a.other?.cat?.dog"));
block|}
DECL|method|assertMustBeNullable
specifier|private
name|void
name|assertMustBeNullable
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|Exception
name|e
init|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|false
argument_list|,
parameter_list|()
lambda|->
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Result of null safe operator must be nullable"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

