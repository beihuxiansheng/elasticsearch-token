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
literal|"Optional.empty().orElseGet(() -> { return 1; });"
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
literal|"def x = Optional.empty(); x.orElseGet(() -> { return 1; });"
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
literal|"l.sort((a, b) -> { a.length() - b.length(); }); return l.get(0)"
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
literal|"l.sort((String a, String b) -> { (a.length() - b.length()); }); return l.get(0)"
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
literal|"return l.stream().mapToInt(x -> { x + 1; }).sum();"
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
literal|"return l.stream().mapToInt(int x -> { x + 1; }).sum();"
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
literal|"return l.stream().mapToInt(x -> { x + 1; }).sum();"
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
literal|"return l.stream().mapToInt(int x -> { x + 1; }).sum();"
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
literal|"List l = new ArrayList(); l.add(1); l.add(1); "
operator|+
literal|"return l.stream().mapToInt(byte x -> { return x; }).sum();"
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
literal|"int applyOne(IntFunction arg) { arg.apply(1) } applyOne(x -> { x + 1; })"
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
literal|"int applyOne(IntFunction arg) { arg.apply(1) } applyOne(int x -> { x + 1; })"
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
literal|"long applyOne(IntFunction arg) { arg.apply(1) } applyOne(long x -> { x + 1; })"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

