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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_class
DECL|class|LambdaTests
specifier|public
class|class
name|LambdaTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testNoArgLambda
specifier|public
name|void
name|testNoArgLambda
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"Optional.empty().orElseGet(() -> 1);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoArgLambdaDef
specifier|public
name|void
name|testNoArgLambdaDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"def x = Optional.empty(); x.orElseGet(() -> 1);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLambdaWithArgs
specifier|public
name|void
name|testLambdaWithArgs
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"short"
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add('looooong'); l.add('short'); "
operator|+
literal|"l.sort((a, b) -> a.length() - b.length()); return l.get(0)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLambdaWithTypedArgs
specifier|public
name|void
name|testLambdaWithTypedArgs
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"short"
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add('looooong'); l.add('short'); "
operator|+
literal|"l.sort((String a, String b) -> a.length() - b.length()); return l.get(0)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveLambdas
specifier|public
name|void
name|testPrimitiveLambdas
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(x -> x + 1).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveLambdasWithTypedArgs
specifier|public
name|void
name|testPrimitiveLambdasWithTypedArgs
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(int x -> x + 1).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveLambdasDef
specifier|public
name|void
name|testPrimitiveLambdasDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(x -> x + 1).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveLambdasWithTypedArgsDef
specifier|public
name|void
name|testPrimitiveLambdasWithTypedArgsDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(int x -> x + 1).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveLambdasConvertible
specifier|public
name|void
name|testPrimitiveLambdasConvertible
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add((short)1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(long x -> (int)1).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveArgs
specifier|public
name|void
name|testPrimitiveArgs
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int applyOne(IntFunction arg) { arg.apply(1) } applyOne(x -> x + 1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveArgsTyped
specifier|public
name|void
name|testPrimitiveArgsTyped
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int applyOne(IntFunction arg) { arg.apply(1) } applyOne(int x -> x + 1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitiveArgsTypedOddly
specifier|public
name|void
name|testPrimitiveArgsTypedOddly
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|exec
argument_list|(
literal|"long applyOne(IntFunction arg) { arg.apply(1) } applyOne(long x -> x + 1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleStatements
specifier|public
name|void
name|testMultipleStatements
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int applyOne(IntFunction arg) { arg.apply(1) } applyOne(x -> { def y = x + 1; return y })"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnneededCurlyStatements
specifier|public
name|void
name|testUnneededCurlyStatements
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int applyOne(IntFunction arg) { arg.apply(1) } applyOne(x -> { x + 1 })"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** interface ignores return value */
DECL|method|testVoidReturn
specifier|public
name|void
name|testVoidReturn
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"List list = new ArrayList(); "
operator|+
literal|"list.add(2); "
operator|+
literal|"List list2 = new ArrayList(); "
operator|+
literal|"list.forEach(x -> list2.add(x));"
operator|+
literal|"return list[0]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** interface ignores return value */
DECL|method|testVoidReturnDef
specifier|public
name|void
name|testVoidReturnDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"def list = new ArrayList(); "
operator|+
literal|"list.add(2); "
operator|+
literal|"List list2 = new ArrayList(); "
operator|+
literal|"list.forEach(x -> list2.add(x));"
operator|+
literal|"return list[0]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoLambdas
specifier|public
name|void
name|testTwoLambdas
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"testingcdefg"
argument_list|,
name|exec
argument_list|(
literal|"org.elasticsearch.painless.FeatureTest test = new org.elasticsearch.painless.FeatureTest(2,3);"
operator|+
literal|"return test.twoFunctionsOfX(x -> 'testing'.concat(x), y -> 'abcdefg'.substring(y))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedLambdas
specifier|public
name|void
name|testNestedLambdas
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"Optional.empty().orElseGet(() -> Optional.empty().orElseGet(() -> 1));"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLambdaInLoop
specifier|public
name|void
name|testLambdaInLoop
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|exec
argument_list|(
literal|"int sum = 0; "
operator|+
literal|"for (int i = 0; i< 100; i++) {"
operator|+
literal|"  sum += Optional.empty().orElseGet(() -> 1);"
operator|+
literal|"}"
operator|+
literal|"return sum;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapture
specifier|public
name|void
name|testCapture
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return Optional.empty().orElseGet(() -> x);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoCaptures
specifier|public
name|void
name|testTwoCaptures
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"1test"
argument_list|,
name|exec
argument_list|(
literal|"int x = 1; String y = 'test'; return Optional.empty().orElseGet(() -> x + y);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturesAreReadOnly
specifier|public
name|void
name|testCapturesAreReadOnly
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(x -> { l = null; return x + 1 }).sum();"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is read-only"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"def type tracking"
argument_list|)
DECL|method|testOnlyCapturesAreReadOnly
specifier|public
name|void
name|testOnlyCapturesAreReadOnly
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(x -> { x += 1; return x }).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Lambda parameters shouldn't be able to mask a variable already in scope */
DECL|method|testNoParamMasking
specifier|public
name|void
name|testNoParamMasking
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"int x = 0; List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(x -> { x += 1; return x }).sum();"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"already defined"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCaptureDef
specifier|public
name|void
name|testCaptureDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; def y = Optional.empty(); y.orElseGet(() -> x);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedCapture
specifier|public
name|void
name|testNestedCapture
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"boolean x = false; int y = 1;"
operator|+
literal|"return Optional.empty().orElseGet(() -> x ? 5 : Optional.empty().orElseGet(() -> y));"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedCaptureParams
specifier|public
name|void
name|testNestedCaptureParams
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int foo(Function f) { return f.apply(1) }"
operator|+
literal|"return foo(x -> foo(y -> x + 1))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrongArity
specifier|public
name|void
name|testWrongArity
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
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
block|{
name|exec
argument_list|(
literal|"Optional.empty().orElseGet(x -> x);"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Incorrect number of parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrongArityDef
specifier|public
name|void
name|testWrongArityDef
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"def y = Optional.empty(); return y.orElseGet(x -> x);"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Incorrect number of parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrongArityNotEnough
specifier|public
name|void
name|testWrongArityNotEnough
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
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
block|{
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(() -> 5).sum();"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Incorrect number of parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrongArityNotEnoughDef
specifier|public
name|void
name|testWrongArityNotEnoughDef
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectScriptThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(() -> 5).sum();"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Incorrect number of parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLambdaInFunction
specifier|public
name|void
name|testLambdaInFunction
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def foo() { Optional.empty().orElseGet(() -> 5) } return foo();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLambdaCaptureFunctionParam
specifier|public
name|void
name|testLambdaCaptureFunctionParam
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def foo(int x) { Optional.empty().orElseGet(() -> x) } return foo(5);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReservedCapture
specifier|public
name|void
name|testReservedCapture
parameter_list|()
block|{
name|String
name|compare
init|=
literal|"boolean compare(Supplier s, def v) {s.get() == v}"
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> new ArrayList(), new ArrayList())"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { new ArrayList() }, new ArrayList())"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"number"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { return params['key'] }, 'value')"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { return params['nokey'] }, 'value')"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { return params['nokey'] }, null)"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { return params['number'] }, 2)"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { return params['number'] }, 'value')"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { if (params['number'] == 2) { return params['number'] }"
operator|+
literal|"else { return params['key'] } }, 'value')"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { if (params['number'] == 2) { return params['number'] }"
operator|+
literal|"else { return params['key'] } }, 2)"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { if (params['number'] == 1) { return params['number'] }"
operator|+
literal|"else { return params['key'] } }, 'value')"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
name|compare
operator|+
literal|"compare(() -> { if (params['number'] == 1) { return params['number'] }"
operator|+
literal|"else { return params['key'] } }, 2)"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReturnVoid
specifier|public
name|void
name|testReturnVoid
parameter_list|()
block|{
name|Throwable
name|expected
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
literal|"StringBuilder b = new StringBuilder(); List l = [1, 2]; l.stream().mapToLong(i -> b.setLength(i))"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Cannot cast from [void] to [long]."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReturnVoidDef
specifier|public
name|void
name|testReturnVoidDef
parameter_list|()
block|{
comment|// If we can catch the error at compile time we do
name|Exception
name|expected
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
literal|"StringBuilder b = new StringBuilder(); def l = [1, 2]; l.stream().mapToLong(i -> b.setLength(i))"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Cannot cast from [void] to [def]."
argument_list|)
argument_list|)
expr_stmt|;
comment|// Otherwise we convert the void into a null
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"def b = new StringBuilder(); def l = [1, 2]; l.stream().map(i -> b.setLength(i)).collect(Collectors.toList())"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|exec
argument_list|(
literal|"def b = new StringBuilder(); List l = [1, 2]; l.stream().map(i -> b.setLength(i)).collect(Collectors.toList())"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

