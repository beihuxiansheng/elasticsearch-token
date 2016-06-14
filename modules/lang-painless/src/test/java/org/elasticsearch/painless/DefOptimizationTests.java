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
DECL|class|DefOptimizationTests
specifier|public
class|class
name|DefOptimizationTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testIntBraceArrayOptiLoad
specifier|public
name|void
name|testIntBraceArrayOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 0; def y = new int[1]; y[0] = 5; x = y[0]; return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayLoad(Ljava/lang/Object;I)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntBraceArrayOptiStore
specifier|public
name|void
name|testIntBraceArrayOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 1; def y = new int[1]; y[0] = x; return y[0];"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayStore(Ljava/lang/Object;II)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntBraceListOptiLoad
specifier|public
name|void
name|testIntBraceListOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 0; def y = new ArrayList(); y.add(5); x = y[0]; return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayLoad(Ljava/lang/Object;I)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntBraceListOptiStore
specifier|public
name|void
name|testIntBraceListOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 1; def y = new ArrayList(); y.add(0); y[0] = x; return y[0];"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayStore(Ljava/lang/Object;II)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntBraceMapOptiLoad
specifier|public
name|void
name|testIntBraceMapOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 0; def y = new HashMap(); y.put(0, 5); x = y[0];"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayLoad(Ljava/lang/Object;I)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntBraceMapOptiStore
specifier|public
name|void
name|testIntBraceMapOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 1; def y = new HashMap(); y.put(0, 1); y[0] = x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayStore(Ljava/lang/Object;II)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntFieldListOptiLoad
specifier|public
name|void
name|testIntFieldListOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 0; def y = new ArrayList(); y.add(5); x = y.0;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntFieldListOptiStore
specifier|public
name|void
name|testIntFieldListOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 1; def y = new ArrayList(); y.add(0); y.0 = x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;I)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntFieldMapOptiLoad
specifier|public
name|void
name|testIntFieldMapOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 0; def y = new HashMap(); y.put('0', 5); x = y.0; return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntFieldMapOptiStore
specifier|public
name|void
name|testIntFieldMapOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x = 1; def y = new HashMap(); y.put('0', 1); y.0 = x; return y.0;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;I)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntCall0Opti
specifier|public
name|void
name|testIntCall0Opti
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x; def y = new HashMap(); y['int'] = 1; x = y.get('int'); return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC get(Ljava/lang/Object;Ljava/lang/String;)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntCall1Opti
specifier|public
name|void
name|testIntCall1Opti
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x; def y = new HashMap(); y['int'] = 1; x = y.get('int');"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC get(Ljava/lang/Object;Ljava/lang/String;)I"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleBraceArrayOptiLoad
specifier|public
name|void
name|testDoubleBraceArrayOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 0; def y = new double[1]; y[0] = 5.0; x = y[0]; return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayLoad(Ljava/lang/Object;I)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleBraceArrayOptiStore
specifier|public
name|void
name|testDoubleBraceArrayOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 1; def y = new double[1]; y[0] = x; return y[0];"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayStore(Ljava/lang/Object;ID)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleBraceListOptiLoad
specifier|public
name|void
name|testDoubleBraceListOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 0.0; def y = new ArrayList(); y.add(5.0); x = y[0]; return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayLoad(Ljava/lang/Object;I)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleBraceListOptiStore
specifier|public
name|void
name|testDoubleBraceListOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 1.0; def y = new ArrayList(); y.add(0.0); y[0] = x; return y[0];"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayStore(Ljava/lang/Object;ID)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleBraceMapOptiLoad
specifier|public
name|void
name|testDoubleBraceMapOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 0.0; def y = new HashMap(); y.put(0, 5.0); x = y[0];"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayLoad(Ljava/lang/Object;I)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleBraceMapOptiStore
specifier|public
name|void
name|testDoubleBraceMapOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 1.0; def y = new HashMap(); y.put(0, 2.0); y[0] = x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC arrayStore(Ljava/lang/Object;ID)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleFieldListOptiLoad
specifier|public
name|void
name|testDoubleFieldListOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 0; def y = new ArrayList(); y.add(5.0); x = y.0;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleFieldListOptiStore
specifier|public
name|void
name|testDoubleFieldListOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 1.0; def y = new ArrayList(); y.add(0); y.0 = x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;D)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleFieldMapOptiLoad
specifier|public
name|void
name|testDoubleFieldMapOptiLoad
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 0; def y = new HashMap(); y.put('0', 5.0); x = y.0; return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleFieldMapOptiStore
specifier|public
name|void
name|testDoubleFieldMapOptiStore
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x = 1.0; def y = new HashMap(); y.put('0', 1.0); y.0 = x; return y.0;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC 0(Ljava/lang/Object;D)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleCall0Opti
specifier|public
name|void
name|testDoubleCall0Opti
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x; def y = new HashMap(); y['double'] = 1.0; x = y.get('double'); return x;"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC get(Ljava/lang/Object;Ljava/lang/String;)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleCall1Opti
specifier|public
name|void
name|testDoubleCall1Opti
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"double x; def y = new HashMap(); y['double'] = 1.0; x = y.get('double');"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC get(Ljava/lang/Object;Ljava/lang/String;)D"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|exec
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalCast
specifier|public
name|void
name|testIllegalCast
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"int x;\ndef y = new HashMap();\ny['double'] = 1.0;\nx = y.get('double');\n"
decl_stmt|;
name|assertBytecodeExists
argument_list|(
name|script
argument_list|,
literal|"INVOKEDYNAMIC get(Ljava/lang/Object;Ljava/lang/String;)I"
argument_list|)
expr_stmt|;
specifier|final
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
name|script
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
literal|"Cannot cast java.lang.Double to java.lang.Integer"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMulOptLHS
specifier|public
name|void
name|testMulOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x * y"
argument_list|,
literal|"INVOKEDYNAMIC mul(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMulOptRHS
specifier|public
name|void
name|testMulOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x * y"
argument_list|,
literal|"INVOKEDYNAMIC mul(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMulOptRet
specifier|public
name|void
name|testMulOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x * y"
argument_list|,
literal|"INVOKEDYNAMIC mul(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivOptLHS
specifier|public
name|void
name|testDivOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x / y"
argument_list|,
literal|"INVOKEDYNAMIC div(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivOptRHS
specifier|public
name|void
name|testDivOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x / y"
argument_list|,
literal|"INVOKEDYNAMIC div(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivOptRet
specifier|public
name|void
name|testDivOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x / y"
argument_list|,
literal|"INVOKEDYNAMIC div(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemOptLHS
specifier|public
name|void
name|testRemOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x % y"
argument_list|,
literal|"INVOKEDYNAMIC rem(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemOptRHS
specifier|public
name|void
name|testRemOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x % y"
argument_list|,
literal|"INVOKEDYNAMIC rem(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemOptRet
specifier|public
name|void
name|testRemOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x % y"
argument_list|,
literal|"INVOKEDYNAMIC rem(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddOptLHS
specifier|public
name|void
name|testAddOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x + y"
argument_list|,
literal|"INVOKEDYNAMIC add(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddOptRHS
specifier|public
name|void
name|testAddOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x + y"
argument_list|,
literal|"INVOKEDYNAMIC add(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddOptRet
specifier|public
name|void
name|testAddOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x + y"
argument_list|,
literal|"INVOKEDYNAMIC add(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubOptLHS
specifier|public
name|void
name|testSubOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x - y"
argument_list|,
literal|"INVOKEDYNAMIC sub(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubOptRHS
specifier|public
name|void
name|testSubOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x - y"
argument_list|,
literal|"INVOKEDYNAMIC sub(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubOptRet
specifier|public
name|void
name|testSubOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x - y"
argument_list|,
literal|"INVOKEDYNAMIC sub(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLshOptLHS
specifier|public
name|void
name|testLshOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x<< y"
argument_list|,
literal|"INVOKEDYNAMIC lsh(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLshOptRHS
specifier|public
name|void
name|testLshOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x<< y"
argument_list|,
literal|"INVOKEDYNAMIC lsh(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLshOptRet
specifier|public
name|void
name|testLshOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x<< y"
argument_list|,
literal|"INVOKEDYNAMIC lsh(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRshOptLHS
specifier|public
name|void
name|testRshOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x>> y"
argument_list|,
literal|"INVOKEDYNAMIC rsh(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRshOptRHS
specifier|public
name|void
name|testRshOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x>> y"
argument_list|,
literal|"INVOKEDYNAMIC rsh(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRshOptRet
specifier|public
name|void
name|testRshOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x>> y"
argument_list|,
literal|"INVOKEDYNAMIC rsh(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUshOptLHS
specifier|public
name|void
name|testUshOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x>>> y"
argument_list|,
literal|"INVOKEDYNAMIC ush(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUshOptRHS
specifier|public
name|void
name|testUshOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x>>> y"
argument_list|,
literal|"INVOKEDYNAMIC ush(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUshOptRet
specifier|public
name|void
name|testUshOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x>>> y"
argument_list|,
literal|"INVOKEDYNAMIC ush(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndOptLHS
specifier|public
name|void
name|testAndOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x& y"
argument_list|,
literal|"INVOKEDYNAMIC and(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndOptRHS
specifier|public
name|void
name|testAndOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x& y"
argument_list|,
literal|"INVOKEDYNAMIC and(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndOptRet
specifier|public
name|void
name|testAndOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x& y"
argument_list|,
literal|"INVOKEDYNAMIC and(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrOptLHS
specifier|public
name|void
name|testOrOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x | y"
argument_list|,
literal|"INVOKEDYNAMIC or(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrOptRHS
specifier|public
name|void
name|testOrOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x | y"
argument_list|,
literal|"INVOKEDYNAMIC or(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrOptRet
specifier|public
name|void
name|testOrOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x | y"
argument_list|,
literal|"INVOKEDYNAMIC or(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testXorOptLHS
specifier|public
name|void
name|testXorOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x ^ y"
argument_list|,
literal|"INVOKEDYNAMIC xor(ILjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testXorOptRHS
specifier|public
name|void
name|testXorOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x ^ y"
argument_list|,
literal|"INVOKEDYNAMIC xor(Ljava/lang/Object;I)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testXorOptRet
specifier|public
name|void
name|testXorOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; def y = 2; double d = x ^ y"
argument_list|,
literal|"INVOKEDYNAMIC xor(Ljava/lang/Object;Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanXorOptLHS
specifier|public
name|void
name|testBooleanXorOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"boolean x = true; def y = true; return x ^ y"
argument_list|,
literal|"INVOKEDYNAMIC xor(ZLjava/lang/Object;)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanXorOptRHS
specifier|public
name|void
name|testBooleanXorOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = true; boolean y = true; return x ^ y"
argument_list|,
literal|"INVOKEDYNAMIC xor(Ljava/lang/Object;Z)Ljava/lang/Object;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanXorOptRet
specifier|public
name|void
name|testBooleanXorOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = true; def y = true; boolean v = x ^ y"
argument_list|,
literal|"INVOKEDYNAMIC xor(Ljava/lang/Object;Ljava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLtOptLHS
specifier|public
name|void
name|testLtOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x< y"
argument_list|,
literal|"INVOKEDYNAMIC lt(ILjava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLtOptRHS
specifier|public
name|void
name|testLtOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x< y"
argument_list|,
literal|"INVOKEDYNAMIC lt(Ljava/lang/Object;I)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLteOptLHS
specifier|public
name|void
name|testLteOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x<= y"
argument_list|,
literal|"INVOKEDYNAMIC lte(ILjava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLteOptRHS
specifier|public
name|void
name|testLteOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x<= y"
argument_list|,
literal|"INVOKEDYNAMIC lte(Ljava/lang/Object;I)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqOptLHS
specifier|public
name|void
name|testEqOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x == y"
argument_list|,
literal|"INVOKEDYNAMIC eq(ILjava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqOptRHS
specifier|public
name|void
name|testEqOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x == y"
argument_list|,
literal|"INVOKEDYNAMIC eq(Ljava/lang/Object;I)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNeqOptLHS
specifier|public
name|void
name|testNeqOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x != y"
argument_list|,
literal|"INVOKEDYNAMIC eq(ILjava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNeqOptRHS
specifier|public
name|void
name|testNeqOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x != y"
argument_list|,
literal|"INVOKEDYNAMIC eq(Ljava/lang/Object;I)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGteOptLHS
specifier|public
name|void
name|testGteOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x>= y"
argument_list|,
literal|"INVOKEDYNAMIC gte(ILjava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGteOptRHS
specifier|public
name|void
name|testGteOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x>= y"
argument_list|,
literal|"INVOKEDYNAMIC gte(Ljava/lang/Object;I)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGtOptLHS
specifier|public
name|void
name|testGtOptLHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"int x = 1; def y = 2; return x> y"
argument_list|,
literal|"INVOKEDYNAMIC gt(ILjava/lang/Object;)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGtOptRHS
specifier|public
name|void
name|testGtOptRHS
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; int y = 2; return x> y"
argument_list|,
literal|"INVOKEDYNAMIC gt(Ljava/lang/Object;I)Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnaryMinusOptRet
specifier|public
name|void
name|testUnaryMinusOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; double y = -x; return y"
argument_list|,
literal|"INVOKEDYNAMIC neg(Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnaryNotOptRet
specifier|public
name|void
name|testUnaryNotOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; double y = ~x; return y"
argument_list|,
literal|"INVOKEDYNAMIC not(Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnaryPlusOptRet
specifier|public
name|void
name|testUnaryPlusOptRet
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = 1; double y = +x; return y"
argument_list|,
literal|"INVOKEDYNAMIC plus(Ljava/lang/Object;)D"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

