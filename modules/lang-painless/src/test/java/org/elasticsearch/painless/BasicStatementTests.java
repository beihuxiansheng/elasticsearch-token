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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|BasicStatementTests
specifier|public
class|class
name|BasicStatementTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testIfStatement
specifier|public
name|void
name|testIfStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; if (x == 5) return 1; return 0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = 4; if (x == 5) return 1; else return 0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int x = 4; if (x == 5) return 1; else if (x == 4) return 2; else return 0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x = 4; if (x == 5) return 1; else if (x == 4) return 1; else return 0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"int x = 5;\n"
operator|+
literal|"if (x == 5) {\n"
operator|+
literal|"    int y = 2;\n"
operator|+
literal|"    \n"
operator|+
literal|"    if (y == 2) {\n"
operator|+
literal|"        x = 3;\n"
operator|+
literal|"    }\n"
operator|+
literal|"    \n"
operator|+
literal|"}\n"
operator|+
literal|"\n"
operator|+
literal|"return x;\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWhileStatement
specifier|public
name|void
name|testWhileStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"aaaaaa"
argument_list|,
name|exec
argument_list|(
literal|"String c = \"a\"; int x; while (x< 5) { c += \"a\"; ++x; } return c;"
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|exec
argument_list|(
literal|" byte[][] b = new byte[5][5];       \n"
operator|+
literal|" byte x = 0, y;                     \n"
operator|+
literal|"                                    \n"
operator|+
literal|" while (x< 5) {                    \n"
operator|+
literal|"     y = 0;                         \n"
operator|+
literal|"                                    \n"
operator|+
literal|"     while (y< 5) {                \n"
operator|+
literal|"         b[x][y] = (byte)(x*y);     \n"
operator|+
literal|"         ++y;                       \n"
operator|+
literal|"     }                              \n"
operator|+
literal|"                                    \n"
operator|+
literal|"     ++x;                           \n"
operator|+
literal|" }                                  \n"
operator|+
literal|"                                    \n"
operator|+
literal|" return b;                          \n"
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|b
init|=
operator|(
name|byte
index|[]
index|[]
operator|)
name|value
decl_stmt|;
for|for
control|(
name|byte
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|5
condition|;
operator|++
name|x
control|)
block|{
for|for
control|(
name|byte
name|y
init|=
literal|0
init|;
name|y
operator|<
literal|5
condition|;
operator|++
name|y
control|)
block|{
name|assertEquals
argument_list|(
name|x
operator|*
name|y
argument_list|,
name|b
index|[
name|x
index|]
index|[
name|y
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testDoWhileStatement
specifier|public
name|void
name|testDoWhileStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"aaaaaa"
argument_list|,
name|exec
argument_list|(
literal|"String c = \"a\"; int x; do { c += \"a\"; ++x; } while (x< 5); return c;"
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|exec
argument_list|(
literal|" int[][] b = new int[5][5]; \n"
operator|+
literal|" int x = 0, y;                    \n"
operator|+
literal|"                                  \n"
operator|+
literal|" do {                             \n"
operator|+
literal|"     y = 0;                       \n"
operator|+
literal|"                                  \n"
operator|+
literal|"     do {                         \n"
operator|+
literal|"         b[x][y] = x*y;           \n"
operator|+
literal|"         ++y;                     \n"
operator|+
literal|"     } while (y< 5);             \n"
operator|+
literal|"                                  \n"
operator|+
literal|"     ++x;                         \n"
operator|+
literal|" } while (x< 5);                 \n"
operator|+
literal|"                                  \n"
operator|+
literal|" return b;                        \n"
argument_list|)
decl_stmt|;
name|int
index|[]
index|[]
name|b
init|=
operator|(
name|int
index|[]
index|[]
operator|)
name|value
decl_stmt|;
for|for
control|(
name|byte
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|5
condition|;
operator|++
name|x
control|)
block|{
for|for
control|(
name|byte
name|y
init|=
literal|0
init|;
name|y
operator|<
literal|5
condition|;
operator|++
name|y
control|)
block|{
name|assertEquals
argument_list|(
name|x
operator|*
name|y
argument_list|,
name|b
index|[
name|x
index|]
index|[
name|y
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testForStatement
specifier|public
name|void
name|testForStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"aaaaaa"
argument_list|,
name|exec
argument_list|(
literal|"String c = \"a\"; for (int x = 0; x< 5; ++x) c += \"a\"; return c;"
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|exec
argument_list|(
literal|" int[][] b = new int[5][5];  \n"
operator|+
literal|" for (int x = 0; x< 5; ++x) {     \n"
operator|+
literal|"     for (int y = 0; y< 5; ++y) { \n"
operator|+
literal|"         b[x][y] = x*y;            \n"
operator|+
literal|"     }                             \n"
operator|+
literal|" }                                 \n"
operator|+
literal|"                                   \n"
operator|+
literal|" return b;                         \n"
argument_list|)
decl_stmt|;
name|int
index|[]
index|[]
name|b
init|=
operator|(
name|int
index|[]
index|[]
operator|)
name|value
decl_stmt|;
for|for
control|(
name|byte
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|5
condition|;
operator|++
name|x
control|)
block|{
for|for
control|(
name|byte
name|y
init|=
literal|0
init|;
name|y
operator|<
literal|5
condition|;
operator|++
name|y
control|)
block|{
name|assertEquals
argument_list|(
name|x
operator|*
name|y
argument_list|,
name|b
index|[
name|x
index|]
index|[
name|y
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testIterableForEachStatement
specifier|public
name|void
name|testIterableForEachStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(2); l.add(3); int total = 0;"
operator|+
literal|" for (int x : l) total += x; return total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123"
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add('1'); l.add('2'); l.add('3'); String cat = '';"
operator|+
literal|" for (String x : l) cat += x; return cat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1236"
argument_list|,
name|exec
argument_list|(
literal|"Map m = new HashMap(); m.put('1', 1); m.put('2', 2); m.put('3', 3);"
operator|+
literal|" String cat = ''; int total = 0;"
operator|+
literal|" for (Map.Entry e : m.entrySet()) { cat += e.getKey(); total += e.getValue(); } return cat + total"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIterableForEachStatementDef
specifier|public
name|void
name|testIterableForEachStatementDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(1); l.add(2); l.add(3); int total = 0;"
operator|+
literal|" for (int x : l) total += x; return total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123"
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add('1'); l.add('2'); l.add('3'); String cat = '';"
operator|+
literal|" for (String x : l) cat += x; return cat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1236"
argument_list|,
name|exec
argument_list|(
literal|"def m = new HashMap(); m.put('1', 1); m.put('2', 2); m.put('3', 3);"
operator|+
literal|" String cat = ''; int total = 0;"
operator|+
literal|" for (Map.Entry e : m.entrySet()) { cat += e.getKey(); total += e.getValue(); } return cat + total"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testArrayForEachStatement
specifier|public
name|void
name|testArrayForEachStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|exec
argument_list|(
literal|"int[] a = new int[3]; a[0] = 1; a[1] = 2; a[2] = 3; int total = 0;"
operator|+
literal|" for (int x : a) total += x; return total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123"
argument_list|,
name|exec
argument_list|(
literal|"String[] a = new String[3]; a[0] = '1'; a[1] = '2'; a[2] = '3'; def total = '';"
operator|+
literal|" for (String x : a) total += x; return total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|exec
argument_list|(
literal|"int[][] i = new int[3][1]; i[0][0] = 1; i[1][0] = 2; i[2][0] = 3; int total = 0;"
operator|+
literal|" for (int[] j : i) total += j[0]; return total"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"working on it"
argument_list|)
DECL|method|testArrayForEachStatementDef
specifier|public
name|void
name|testArrayForEachStatementDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|exec
argument_list|(
literal|"def a = new int[3]; a[0] = 1; a[1] = 2; a[2] = 3; int total = 0;"
operator|+
literal|" for (int x : a) total += x; return total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123"
argument_list|,
name|exec
argument_list|(
literal|"def a = new String[3]; a[0] = '1'; a[1] = '2'; a[2] = '3'; def total = '';"
operator|+
literal|" for (String x : a) total += x; return total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|exec
argument_list|(
literal|"def i = new int[3][1]; i[0][0] = 1; i[1][0] = 2; i[2][0] = 3; int total = 0;"
operator|+
literal|" for (int[] j : i) total += j[0]; return total"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeclarationStatement
specifier|public
name|void
name|testDeclarationStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|,
name|exec
argument_list|(
literal|"byte a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|,
name|exec
argument_list|(
literal|"short a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
literal|2
argument_list|,
name|exec
argument_list|(
literal|"char a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|exec
argument_list|(
literal|"long a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2F
argument_list|,
name|exec
argument_list|(
literal|"float a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|exec
argument_list|(
literal|"double a = 2; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"boolean a = false; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|exec
argument_list|(
literal|"String a = \"string\"; return a;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HashMap
operator|.
name|class
argument_list|,
name|exec
argument_list|(
literal|"Map a = new HashMap(); return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"byte[] a = new byte[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|short
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"short[] a = new short[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|char
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"char[] a = new char[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"int[] a = new int[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|long
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"long[] a = new long[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|float
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"float[] a = new float[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|double
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"double[] a = new double[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|boolean
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"boolean[] a = new boolean[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|String
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"String[] a = new String[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Map
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"Map[] a = new Map[1]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|byte
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"byte[][] a = new byte[1][2]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|short
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"short[][][] a = new short[1][2][3]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|char
index|[]
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"char[][][][] a = new char[1][2][3][4]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int
index|[]
index|[]
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"int[][][][][] a = new int[1][2][3][4][5]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|long
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"long[][] a = new long[1][2]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|float
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"float[][][] a = new float[1][2][3]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|double
index|[]
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"double[][][][] a = new double[1][2][3][4]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|boolean
index|[]
index|[]
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"boolean[][][][][] a = new boolean[1][2][3][4][5]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|String
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"String[][] a = new String[1][2]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Map
index|[]
index|[]
index|[]
operator|.
expr|class
argument_list|,
name|exec
argument_list|(
literal|"Map[][][] a = new Map[1][2][3]; return a;"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testContinueStatement
specifier|public
name|void
name|testContinueStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|exec
argument_list|(
literal|"int x = 0, y = 0; while (x< 10) { ++x; if (x == 1) continue; ++y; } return y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBreakStatement
specifier|public
name|void
name|testBreakStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"int x = 0, y = 0; while (x< 10) { ++x; if (x == 5) break; ++y; } return y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|testReturnStatement
specifier|public
name|void
name|testReturnStatement
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|exec
argument_list|(
literal|"return 10;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return x;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"int[] x = new int[2]; x[1] = 4; return x[1];"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
operator|(
operator|(
name|short
index|[]
operator|)
name|exec
argument_list|(
literal|"short[] s = new short[3]; s[1] = 5; return s;"
argument_list|)
operator|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|exec
argument_list|(
literal|"Map s = new HashMap(); s.put(\"x\", 10); return s;"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

