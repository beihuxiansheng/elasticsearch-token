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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|MAX_INDY_STRING_CONCAT_ARGS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

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
comment|// boolean
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|true
argument_list|,
name|exec
argument_list|(
literal|"String s = 'cat'; return s + true;"
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
literal|"String s = 'cat'; return s + (byte)3;"
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
literal|"String s = 'cat'; return s + (short)3;"
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
literal|"String s = 'cat'; return s + 't';"
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
literal|"String s = 'cat'; return s + (char)40;"
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
literal|"String s = 'cat'; return s + 2;"
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
literal|"String s = 'cat'; return s + 2L;"
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
literal|"String s = 'cat'; return s + 2F;"
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
literal|"String s = 'cat'; return s + 2.0;"
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
literal|"String s = 'cat'; return s + s;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendMultiple
specifier|public
name|void
name|testAppendMultiple
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"cat"
operator|+
literal|true
operator|+
literal|"abc"
operator|+
literal|null
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cat\"; return s + true + 'abc' + null;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendMany
specifier|public
name|void
name|testAppendMany
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|MAX_INDY_STRING_CONCAT_ARGS
operator|-
literal|5
init|;
name|i
operator|<
name|MAX_INDY_STRING_CONCAT_ARGS
operator|+
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|doTestAppendMany
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestAppendMany
specifier|private
name|void
name|doTestAppendMany
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|StringBuilder
name|script
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"String s = \"cat\"; return s"
argument_list|)
decl_stmt|;
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"cat"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|s
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%03d"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|script
operator|.
name|append
argument_list|(
literal|" + '"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|"'.toString()"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|s
init|=
name|script
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"every string part should be separatly pushed to stack."
argument_list|,
name|Debugger
operator|.
name|toString
argument_list|(
name|s
argument_list|)
operator|.
name|contains
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"LDC \"%03d\""
argument_list|,
name|count
operator|/
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|toString
argument_list|()
argument_list|,
name|exec
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedConcats
specifier|public
name|void
name|testNestedConcats
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"foo1010foo"
argument_list|,
name|exec
argument_list|(
literal|"String s = 'foo'; String x = '10'; return s + Integer.parseInt(x + x) + s;"
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
literal|"String s = 'x'; return s.charAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|120
argument_list|,
name|exec
argument_list|(
literal|"String s = 'x'; return s.codePointAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"String s = 'x'; return s.compareTo('x');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xx"
argument_list|,
name|exec
argument_list|(
literal|"String s = 'x'; return s.concat('x');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"String s = 'xy'; return s.endsWith('y');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"String t = 'abcde'; return t.indexOf('cd', 1);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"String t = 'abcde'; return t.isEmpty();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"String t = 'abcde'; return t.length();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cdcde"
argument_list|,
name|exec
argument_list|(
literal|"String t = 'abcde'; return t.replace('ab', 'cd');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"String s = 'xy'; return s.startsWith('y');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"e"
argument_list|,
name|exec
argument_list|(
literal|"String t = 'abcde'; return t.substring(4, 5);"
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
literal|"String s = 'a'; return s.toCharArray();"
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
literal|"String s = ' a '; return s.trim();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|exec
argument_list|(
literal|"return 'x'.charAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|120
argument_list|,
name|exec
argument_list|(
literal|"return 'x'.codePointAt(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"return 'x'.compareTo('x');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xx"
argument_list|,
name|exec
argument_list|(
literal|"return 'x'.concat('x');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return 'xy'.endsWith('y');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"return 'abcde'.indexOf('cd', 1);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return 'abcde'.isEmpty();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"return 'abcde'.length();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cdcde"
argument_list|,
name|exec
argument_list|(
literal|"return 'abcde'.replace('ab', 'cd');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return 'xy'.startsWith('y');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"e"
argument_list|,
name|exec
argument_list|(
literal|"return 'abcde'.substring(4, 5);"
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
literal|"return 'a'.toCharArray();"
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
literal|"return ' a '.trim();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringAndCharacter
specifier|public
name|void
name|testStringAndCharacter
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|exec
argument_list|(
literal|"return (char)\"c\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|exec
argument_list|(
literal|"return (char)'c'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|exec
argument_list|(
literal|"return (String)(char)\"c\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|exec
argument_list|(
literal|"return (String)(char)'c'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|exec
argument_list|(
literal|"String s = \"c\"; (char)s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|exec
argument_list|(
literal|"String s = 'c'; (char)s"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"cc"
argument_list|,
name|exec
argument_list|(
literal|"return (String)(char)\"cc\""
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassCastException
name|cce
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|cce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Cannot cast from [String] to [char]."
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
literal|"cc"
argument_list|,
name|exec
argument_list|(
literal|"return (String)(char)'cc'"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassCastException
name|cce
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|cce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Cannot cast from [String] to [char]."
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|exec
argument_list|(
literal|"String s = \"cc\"; (char)s"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassCastException
name|cce
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|cce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Cannot cast [String] with length greater than one to [char]."
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|exec
argument_list|(
literal|"String s = 'cc'; (char)s"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassCastException
name|cce
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|cce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Cannot cast [String] with length greater than one to [char]."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

