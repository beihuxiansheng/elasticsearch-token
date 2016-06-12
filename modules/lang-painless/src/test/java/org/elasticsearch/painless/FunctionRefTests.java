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
DECL|class|FunctionRefTests
specifier|public
class|class
name|FunctionRefTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testStaticMethodReference
specifier|public
name|void
name|testStaticMethodReference
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(2); l.add(1); l.sort(Integer::compare); return l.get(0);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStaticMethodReferenceDef
specifier|public
name|void
name|testStaticMethodReferenceDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(2); l.add(1); l.sort(Integer::compare); return l.get(0);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testVirtualMethodReference
specifier|public
name|void
name|testVirtualMethodReference
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.add(1); return l.stream().mapToInt(Integer::intValue).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testVirtualMethodReferenceDef
specifier|public
name|void
name|testVirtualMethodReferenceDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(1); l.add(1); return l.stream().mapToInt(Integer::intValue).sum();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCtorMethodReference
specifier|public
name|void
name|testCtorMethodReference
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3.0D
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1.0); l.add(2.0); "
operator|+
literal|"DoubleStream doubleStream = l.stream().mapToDouble(Double::doubleValue);"
operator|+
literal|"DoubleSummaryStatistics stats = doubleStream.collect(DoubleSummaryStatistics::new, "
operator|+
literal|"DoubleSummaryStatistics::accept, "
operator|+
literal|"DoubleSummaryStatistics::combine); "
operator|+
literal|"return stats.getSum()"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCtorMethodReferenceDef
specifier|public
name|void
name|testCtorMethodReferenceDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3.0D
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); l.add(1.0); l.add(2.0); "
operator|+
literal|"def doubleStream = l.stream().mapToDouble(Double::doubleValue);"
operator|+
literal|"def stats = doubleStream.collect(DoubleSummaryStatistics::new, "
operator|+
literal|"DoubleSummaryStatistics::accept, "
operator|+
literal|"DoubleSummaryStatistics::combine); "
operator|+
literal|"return stats.getSum()"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReference
specifier|public
name|void
name|testCapturingMethodReference
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"Integer x = Integer.valueOf(5); return Optional.empty().orElseGet(x::toString);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); return Optional.empty().orElseGet(l::toString);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceDefImpl
specifier|public
name|void
name|testCapturingMethodReferenceDefImpl
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"def x = Integer.valueOf(5); return Optional.empty().orElseGet(x::toString);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); return Optional.empty().orElseGet(l::toString);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceDefInterface
specifier|public
name|void
name|testCapturingMethodReferenceDefInterface
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"Integer x = Integer.valueOf(5); def opt = Optional.empty(); return opt.orElseGet(x::toString);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); def opt = Optional.empty(); return opt.orElseGet(l::toString);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceDefEverywhere
specifier|public
name|void
name|testCapturingMethodReferenceDefEverywhere
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"def x = Integer.valueOf(5); def opt = Optional.empty(); return opt.orElseGet(x::toString);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|exec
argument_list|(
literal|"def l = new ArrayList(); def opt = Optional.empty(); return opt.orElseGet(l::toString);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceMultipleLambdas
specifier|public
name|void
name|testCapturingMethodReferenceMultipleLambdas
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"testingcdefg"
argument_list|,
name|exec
argument_list|(
literal|"String x = 'testing';"
operator|+
literal|"String y = 'abcdefg';"
operator|+
literal|"org.elasticsearch.painless.FeatureTest test = new org.elasticsearch.painless.FeatureTest(2,3);"
operator|+
literal|"return test.twoFunctionsOfX(x::concat, y::substring);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceMultipleLambdasDefImpls
specifier|public
name|void
name|testCapturingMethodReferenceMultipleLambdasDefImpls
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"testingcdefg"
argument_list|,
name|exec
argument_list|(
literal|"def x = 'testing';"
operator|+
literal|"def y = 'abcdefg';"
operator|+
literal|"org.elasticsearch.painless.FeatureTest test = new org.elasticsearch.painless.FeatureTest(2,3);"
operator|+
literal|"return test.twoFunctionsOfX(x::concat, y::substring);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceMultipleLambdasDefInterface
specifier|public
name|void
name|testCapturingMethodReferenceMultipleLambdasDefInterface
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"testingcdefg"
argument_list|,
name|exec
argument_list|(
literal|"String x = 'testing';"
operator|+
literal|"String y = 'abcdefg';"
operator|+
literal|"def test = new org.elasticsearch.painless.FeatureTest(2,3);"
operator|+
literal|"return test.twoFunctionsOfX(x::concat, y::substring);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapturingMethodReferenceMultipleLambdasDefEverywhere
specifier|public
name|void
name|testCapturingMethodReferenceMultipleLambdasDefEverywhere
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"testingcdefg"
argument_list|,
name|exec
argument_list|(
literal|"def x = 'testing';"
operator|+
literal|"def y = 'abcdefg';"
operator|+
literal|"def test = new org.elasticsearch.painless.FeatureTest(2,3);"
operator|+
literal|"return test.twoFunctionsOfX(x::concat, y::substring);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOwnStaticMethodReference
specifier|public
name|void
name|testOwnStaticMethodReference
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int mycompare(int i, int j) { j - i } "
operator|+
literal|"List l = new ArrayList(); l.add(2); l.add(1); l.sort(this::mycompare); return l.get(0);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOwnStaticMethodReferenceDef
specifier|public
name|void
name|testOwnStaticMethodReferenceDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int mycompare(int i, int j) { j - i } "
operator|+
literal|"def l = new ArrayList(); l.add(2); l.add(1); l.sort(this::mycompare); return l.get(0);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInterfaceDefaultMethod
specifier|public
name|void
name|testInterfaceDefaultMethod
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|exec
argument_list|(
literal|"String f(BiFunction function) { function.apply('foo', 'bar') }"
operator|+
literal|"Map map = new HashMap(); f(map::getOrDefault)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInterfaceDefaultMethodDef
specifier|public
name|void
name|testInterfaceDefaultMethodDef
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|exec
argument_list|(
literal|"String f(BiFunction function) { function.apply('foo', 'bar') }"
operator|+
literal|"def map = new HashMap(); f(map::getOrDefault)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMethodMissing
specifier|public
name|void
name|testMethodMissing
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
literal|"List l = new ArrayList(); l.add(2); l.add(1); l.sort(Integer::bogus); return l.get(0);"
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
literal|"Unknown reference"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNotFunctionalInterface
specifier|public
name|void
name|testNotFunctionalInterface
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
literal|"List l = new ArrayList(); l.add(2); l.add(1); l.add(Integer::bogus); return l.get(0);"
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
literal|"Cannot convert function reference"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncompatible
specifier|public
name|void
name|testIncompatible
parameter_list|()
block|{
name|expectScriptThrows
argument_list|(
name|BootstrapMethodError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(2); l.add(1); l.sort(String::startsWith); return l.get(0);"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

