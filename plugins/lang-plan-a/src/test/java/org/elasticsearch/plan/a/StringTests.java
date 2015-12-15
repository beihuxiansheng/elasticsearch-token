begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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

begin_class
DECL|class|StringTests
specifier|public
class|class
name|StringTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testAppend
specifier|public
name|void
name|testAppend
parameter_list|()
block|{
comment|// boolean
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|true
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + true;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// byte
name|assertEquals
argument_list|(
literal|"cat"
operator|+
operator|(
name|byte
operator|)
literal|3
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + (byte)3;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// short
name|assertEquals
argument_list|(
literal|"cat"
operator|+
operator|(
name|short
operator|)
literal|3
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + (short)3;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// char
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|'t'
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + 't';"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cat"
operator|+
operator|(
name|char
operator|)
literal|40
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + (char)40;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|2
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + 2;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|2L
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + 2L;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// float
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|2F
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + 2F;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// double
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|2.0
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + 2.0;"
argument_list|)
argument_list|)
expr_stmt|;
comment|// String
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|"cat"
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + s;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringAPI
specifier|public
name|void
name|testStringAPI
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|exec
argument_list|(
literal|"return new String();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|exec
argument_list|(
literal|"String s = \"x\"; return s.charAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|120
argument_list|,
name|exec
argument_list|(
literal|"String s = \"x\"; return s.codePointAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"String s = \"x\"; return s.compareTo(\"x\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xx"
argument_list|,
name|exec
argument_list|(
literal|"String s = \"x\"; return s.concat(\"x\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"String s = \"xy\"; return s.endsWith(\"y\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"String t = \"abcde\"; return t.indexOf(\"cd\", 1);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"String t = \"abcde\"; return t.isEmpty();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"String t = \"abcde\"; return t.length();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cdcde"
argument_list|,
name|exec
argument_list|(
literal|"String t = \"abcde\"; return t.replace(\"ab\", \"cd\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"String s = \"xy\"; return s.startsWith(\"y\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"e"
argument_list|,
name|exec
argument_list|(
literal|"String t = \"abcde\"; return t.substring(4, 5);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|97
argument_list|,
operator|(
operator|(
name|char
index|[]
operator|)
name|exec
argument_list|(
literal|"String s = \"a\"; return s.toCharArray();"
argument_list|)
operator|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|exec
argument_list|(
literal|"String s = \" a \"; return s.trim();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|exec
argument_list|(
literal|"return \"x\".charAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|120
argument_list|,
name|exec
argument_list|(
literal|"return \"x\".codePointAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"return \"x\".compareTo(\"x\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xx"
argument_list|,
name|exec
argument_list|(
literal|"return \"x\".concat(\"x\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return \"xy\".endsWith(\"y\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"return \"abcde\".indexOf(\"cd\", 1);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return \"abcde\".isEmpty();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"return \"abcde\".length();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cdcde"
argument_list|,
name|exec
argument_list|(
literal|"return \"abcde\".replace(\"ab\", \"cd\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return \"xy\".startsWith(\"y\");"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"e"
argument_list|,
name|exec
argument_list|(
literal|"return \"abcde\".substring(4, 5);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|97
argument_list|,
operator|(
operator|(
name|char
index|[]
operator|)
name|exec
argument_list|(
literal|"return \"a\".toCharArray();"
argument_list|)
operator|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|exec
argument_list|(
literal|"return \" a \".trim();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

