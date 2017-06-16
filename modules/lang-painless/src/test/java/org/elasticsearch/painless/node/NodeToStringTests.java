begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|CompilerSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Cast
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|MethodKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|RuntimeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Struct
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|FeatureTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|GenericElasticsearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Locals
operator|.
name|Variable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Location
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Operation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|ScriptClassInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|antlr
operator|.
name|Walker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

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
name|List
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|singletonList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
operator|.
name|SSource
operator|.
name|MainMethodReserved
import|;
end_import

begin_comment
comment|/**  * Tests {@link Object#toString} implementations on all extensions of {@link ANode}.  */
end_comment

begin_class
DECL|class|NodeToStringTests
specifier|public
class|class
name|NodeToStringTests
extends|extends
name|ESTestCase
block|{
DECL|field|definition
specifier|private
specifier|final
name|Definition
name|definition
init|=
name|Definition
operator|.
name|BUILTINS
decl_stmt|;
DECL|method|testEAssignment
specifier|public
name|void
name|testEAssignment
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i))\n"
operator|+
literal|"  (SExpression (EAssignment (EVariable i) = (ENumeric 2)))\n"
operator|+
literal|"  (SReturn (EVariable i)))"
argument_list|,
literal|"def i;\n"
operator|+
literal|"i = 2;\n"
operator|+
literal|"return i"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|operator
range|:
operator|new
name|String
index|[]
block|{
literal|"+"
block|,
literal|"-"
block|,
literal|"*"
block|,
literal|"/"
block|,
literal|"%"
block|,
literal|"&"
block|,
literal|"^"
block|,
literal|"|"
block|,
literal|"<<"
block|,
literal|">>"
block|,
literal|">>>"
block|}
control|)
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i (ENumeric 1)))\n"
operator|+
literal|"  (SExpression (EAssignment (EVariable i) "
operator|+
name|operator
operator|+
literal|"= (ENumeric 2)))\n"
operator|+
literal|"  (SReturn (EVariable i)))"
argument_list|,
literal|"def i = 1;\n"
operator|+
literal|"i "
operator|+
name|operator
operator|+
literal|"= 2;\n"
operator|+
literal|"return i"
argument_list|)
expr_stmt|;
block|}
comment|// Compound
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i))\n"
operator|+
literal|"  (SReturn (EAssignment (EVariable i) = (ENumeric 2))))"
argument_list|,
literal|"def i;\n"
operator|+
literal|"return i = 2"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i))\n"
operator|+
literal|"  (SReturn (EAssignment (EVariable i) ++ post)))"
argument_list|,
literal|"def i;\n"
operator|+
literal|"return i++"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i))\n"
operator|+
literal|"  (SReturn (EAssignment (EVariable i) ++ pre)))"
argument_list|,
literal|"def i;\n"
operator|+
literal|"return ++i"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i))\n"
operator|+
literal|"  (SReturn (EAssignment (EVariable i) -- post)))"
argument_list|,
literal|"def i;\n"
operator|+
literal|"return i--"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def i))\n"
operator|+
literal|"  (SReturn (EAssignment (EVariable i) -- pre)))"
argument_list|,
literal|"def i;\n"
operator|+
literal|"return --i"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEBinary
specifier|public
name|void
name|testEBinary
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) * (ENumeric 1))))"
argument_list|,
literal|"return 1 * 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) / (ENumeric 1))))"
argument_list|,
literal|"return 1 / 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) % (ENumeric 1))))"
argument_list|,
literal|"return 1 % 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) + (ENumeric 1))))"
argument_list|,
literal|"return 1 + 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) - (ENumeric 1))))"
argument_list|,
literal|"return 1 - 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (EString 'asb') =~ (ERegex /cat/))))"
argument_list|,
literal|"return 'asb' =~ /cat/"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (EString 'asb') ==~ (ERegex /cat/))))"
argument_list|,
literal|"return 'asb' ==~ /cat/"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1)<< (ENumeric 1))))"
argument_list|,
literal|"return 1<< 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1)>> (ENumeric 1))))"
argument_list|,
literal|"return 1>> 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1)>>> (ENumeric 1))))"
argument_list|,
literal|"return 1>>> 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1)& (ENumeric 1))))"
argument_list|,
literal|"return 1& 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) ^ (ENumeric 1))))"
argument_list|,
literal|"return 1 ^ 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBinary (ENumeric 1) | (ENumeric 1))))"
argument_list|,
literal|"return 1 | 1"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEBool
specifier|public
name|void
name|testEBool
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBool (EBoolean true)&& (EBoolean false))))"
argument_list|,
literal|"return true&& false"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBool (EBoolean true) || (EBoolean false))))"
argument_list|,
literal|"return true || false"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEBoolean
specifier|public
name|void
name|testEBoolean
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBoolean true)))"
argument_list|,
literal|"return true"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EBoolean false)))"
argument_list|,
literal|"return false"
argument_list|)
expr_stmt|;
block|}
DECL|method|testECallLocal
specifier|public
name|void
name|testECallLocal
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SFunction def a\n"
operator|+
literal|"    (SReturn (EBoolean true)))\n"
operator|+
literal|"  (SReturn (ECallLocal a)))"
argument_list|,
literal|"def a() {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"}\n"
operator|+
literal|"return a()"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SFunction def a (Args (Pair int i) (Pair int j))\n"
operator|+
literal|"    (SReturn (EBoolean true)))\n"
operator|+
literal|"  (SReturn (ECallLocal a (Args (ENumeric 1) (ENumeric 2)))))"
argument_list|,
literal|"def a(int i, int j) {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"}\n"
operator|+
literal|"return a(1, 2)"
argument_list|)
expr_stmt|;
block|}
DECL|method|testECapturingFunctionRef
specifier|public
name|void
name|testECapturingFunctionRef
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration Integer x (PCallInvoke (EStatic Integer) valueOf (Args (ENumeric 5)))))\n"
operator|+
literal|"  (SReturn (PCallInvoke (PCallInvoke (EStatic Optional) empty) orElseGet (Args (ECapturingFunctionRef x toString)))))"
argument_list|,
literal|"Integer x = Integer.valueOf(5);\n"
operator|+
literal|"return Optional.empty().orElseGet(x::toString)"
argument_list|)
expr_stmt|;
block|}
DECL|method|testECast
specifier|public
name|void
name|testECast
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|AExpression
name|child
init|=
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|Cast
name|cast
init|=
operator|new
name|Cast
argument_list|(
name|Definition
operator|.
name|STRING_TYPE
argument_list|,
name|Definition
operator|.
name|INT_OBJ_TYPE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"(ECast Integer (EConstant String 'test'))"
argument_list|,
operator|new
name|ECast
argument_list|(
name|l
argument_list|,
name|child
argument_list|,
name|cast
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|child
operator|=
operator|new
name|EBinary
argument_list|(
name|l
argument_list|,
name|Operation
operator|.
name|ADD
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|"test"
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|cast
operator|=
operator|new
name|Cast
argument_list|(
name|Definition
operator|.
name|INT_OBJ_TYPE
argument_list|,
name|Definition
operator|.
name|BOOLEAN_OBJ_TYPE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(ECast Boolean (EBinary (EConstant String 'test') + (EConstant Integer 12)))"
argument_list|,
operator|new
name|ECast
argument_list|(
name|l
argument_list|,
name|child
argument_list|,
name|cast
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEComp
specifier|public
name|void
name|testEComp
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a)< (ENumeric 10))))"
argument_list|,
literal|"return params.a< 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a)<= (ENumeric 10))))"
argument_list|,
literal|"return params.a<= 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a)> (ENumeric 10))))"
argument_list|,
literal|"return params.a> 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a)>= (ENumeric 10))))"
argument_list|,
literal|"return params.a>= 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a) == (ENumeric 10))))"
argument_list|,
literal|"return params.a == 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a) === (ENumeric 10))))"
argument_list|,
literal|"return params.a === 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a) != (ENumeric 10))))"
argument_list|,
literal|"return params.a != 10"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EComp (PField (EVariable params) a) !== (ENumeric 10))))"
argument_list|,
literal|"return params.a !== 10"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEConditional
specifier|public
name|void
name|testEConditional
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EConditional (PField (EVariable params) a) (ENumeric 1) (ENumeric 6))))"
argument_list|,
literal|"return params.a ? 1 : 6"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEConstant
specifier|public
name|void
name|testEConstant
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"(EConstant String '121')"
argument_list|,
operator|new
name|EConstant
argument_list|(
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"121"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(EConstant String '92 ')"
argument_list|,
operator|new
name|EConstant
argument_list|(
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"92 "
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(EConstant Integer 1237)"
argument_list|,
operator|new
name|EConstant
argument_list|(
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1237
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(EConstant Boolean true)"
argument_list|,
operator|new
name|EConstant
argument_list|(
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEDecimal
specifier|public
name|void
name|testEDecimal
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EDecimal 1.0)))"
argument_list|,
literal|"return 1.0"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EDecimal 14.121d)))"
argument_list|,
literal|"return 14.121d"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EDecimal 2234.1f)))"
argument_list|,
literal|"return 2234.1f"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EDecimal 14.121D)))"
argument_list|,
literal|"return 14.121D"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EDecimal 1234.1F)))"
argument_list|,
literal|"return 1234.1F"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEElvis
specifier|public
name|void
name|testEElvis
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EElvis (PField (EVariable params) a) (ENumeric 1))))"
argument_list|,
literal|"return params.a ?: 1"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEExplicit
specifier|public
name|void
name|testEExplicit
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EExplicit byte (PField (EVariable params) a))))"
argument_list|,
literal|"return (byte)(params.a)"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEFunctionRef
specifier|public
name|void
name|testEFunctionRef
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn "
operator|+
literal|"(PCallInvoke (PCallInvoke (EStatic Optional) empty) orElseGet (Args (EFunctionRef Optional empty)))))"
argument_list|,
literal|"return Optional.empty().orElseGet(Optional::empty)"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEInstanceOf
specifier|public
name|void
name|testEInstanceOf
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EInstanceof (ENewObj Object) Object)))"
argument_list|,
literal|"return new Object() instanceof Object"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EInstanceof (ENumeric 12) double)))"
argument_list|,
literal|"return 12 instanceof double"
argument_list|)
expr_stmt|;
block|}
DECL|method|testELambda
specifier|public
name|void
name|testELambda
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (PCallInvoke (EStatic Optional) empty) orElseGet (Args "
operator|+
literal|"(ELambda (SReturn (ENumeric 1)))))))"
argument_list|,
literal|"return Optional.empty().orElseGet(() -> {\n"
operator|+
literal|"  return 1\n"
operator|+
literal|"})"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (PCallInvoke (EStatic Optional) empty) orElseGet (Args "
operator|+
literal|"(ELambda (SReturn (ENumeric 1)))))))"
argument_list|,
literal|"return Optional.empty().orElseGet(() -> 1)"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (PCallInvoke (PCallInvoke (EListInit (ENumeric 1) (ENumeric 2) (ENumeric 3)) stream) "
operator|+
literal|"mapToInt (Args (ELambda (Pair def x)\n"
operator|+
literal|"  (SReturn (EBinary (EVariable x) + (ENumeric 1)))))) sum)))"
argument_list|,
literal|"return [1, 2, 3].stream().mapToInt((def x) -> {\n"
operator|+
literal|"  return x + 1\n"
operator|+
literal|"}).sum()"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (PCallInvoke (PCallInvoke (EListInit (ENumeric 1) (ENumeric 2) (ENumeric 3)) stream) "
operator|+
literal|"mapToInt (Args (ELambda (Pair null x)\n"
operator|+
literal|"  (SReturn (EBinary (EVariable x) + (ENumeric 1)))))) sum)))"
argument_list|,
literal|"return [1, 2, 3].stream().mapToInt(x -> x + 1).sum()"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EListInit (EString 'a') (EString 'b')) sort (Args (ELambda (Pair def a) (Pair def b)\n"
operator|+
literal|"  (SReturn (EBinary (PCallInvoke (EVariable a) length) - (PCallInvoke (EVariable b) length))))))))"
argument_list|,
literal|"return ['a', 'b'].sort((def a, def b) -> {\n"
operator|+
literal|"  return a.length() - b.length()\n"
operator|+
literal|"})"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EListInit (EString 'a') (EString 'b')) sort (Args (ELambda (Pair null a) (Pair null b)\n"
operator|+
literal|"  (SReturn (EBinary (PCallInvoke (EVariable a) length) - (PCallInvoke (EVariable b) length))))))))"
argument_list|,
literal|"return ['a', 'b'].sort((a, b) -> a.length() - b.length())"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EListInit (EString 'a') (EString 'b')) sort (Args (ELambda (Pair def a) (Pair def b)\n"
operator|+
literal|"  (SIf (EComp (EVariable a)< (EVariable b)) (SBlock "
operator|+
literal|"(SReturn (EBinary (PCallInvoke (EVariable a) length) - (PCallInvoke (EVariable b) length)))))\n"
operator|+
literal|"  (SReturn (ENumeric 1)))))))"
argument_list|,
literal|"return ['a', 'b'].sort((def a, def b) -> {\n"
operator|+
literal|"  if (a< b) {\n"
operator|+
literal|"    return a.length() - b.length()\n"
operator|+
literal|"  }\n"
operator|+
literal|"  return 1\n"
operator|+
literal|"})"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEListInit
specifier|public
name|void
name|testEListInit
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EListInit (ENumeric 1) (ENumeric 2) (EString 'cat') (EString 'dog') (ENewObj Object))))"
argument_list|,
literal|"return [1, 2, 'cat', 'dog', new Object()]"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EListInit)))"
argument_list|,
literal|"return []"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEMapInit
specifier|public
name|void
name|testEMapInit
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EMapInit "
operator|+
literal|"(Pair (EString 'a') (ENumeric 1)) "
operator|+
literal|"(Pair (EString 'b') (ENumeric 3)) "
operator|+
literal|"(Pair (ENumeric 12) (ENewObj Object)))))"
argument_list|,
literal|"return ['a': 1, 'b': 3, 12: new Object()]"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EMapInit)))"
argument_list|,
literal|"return [:]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testENewArray
specifier|public
name|void
name|testENewArray
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENewArray int dims (Args (ENumeric 10)))))"
argument_list|,
literal|"return new int[10]"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENewArray int dims (Args (ENumeric 10) (ENumeric 4) (ENumeric 5)))))"
argument_list|,
literal|"return new int[10][4][5]"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENewArray int init (Args (ENumeric 1) (ENumeric 2) (ENumeric 3)))))"
argument_list|,
literal|"return new int[] {1, 2, 3}"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENewArray def init (Args (ENumeric 1) (ENumeric 2) (EString 'bird')))))"
argument_list|,
literal|"return new def[] {1, 2, 'bird'}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testENewObj
specifier|public
name|void
name|testENewObj
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENewObj Object)))"
argument_list|,
literal|"return new Object()"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENewObj DateTimeException (Args (EString 'test')))))"
argument_list|,
literal|"return new DateTimeException('test')"
argument_list|)
expr_stmt|;
block|}
DECL|method|testENull
specifier|public
name|void
name|testENull
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENull)))"
argument_list|,
literal|"return null"
argument_list|)
expr_stmt|;
block|}
DECL|method|testENumeric
specifier|public
name|void
name|testENumeric
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 1)))"
argument_list|,
literal|"return 1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 114121d)))"
argument_list|,
literal|"return 114121d"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 114134f)))"
argument_list|,
literal|"return 114134f"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 114121D)))"
argument_list|,
literal|"return 114121D"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 111234F)))"
argument_list|,
literal|"return 111234F"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 774121l)))"
argument_list|,
literal|"return 774121l"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 881234L)))"
argument_list|,
literal|"return 881234L"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 1 16)))"
argument_list|,
literal|"return 0x1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 774121l 16)))"
argument_list|,
literal|"return 0x774121l"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 881234L 16)))"
argument_list|,
literal|"return 0x881234L"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 1 8)))"
argument_list|,
literal|"return 01"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 774121l 8)))"
argument_list|,
literal|"return 0774121l"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ENumeric 441234L 8)))"
argument_list|,
literal|"return 0441234L"
argument_list|)
expr_stmt|;
block|}
DECL|method|testERegex
specifier|public
name|void
name|testERegex
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ERegex /foo/)))"
argument_list|,
literal|"return /foo/"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ERegex /foo/ cix)))"
argument_list|,
literal|"return /foo/cix"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (ERegex /foo/ cix)))"
argument_list|,
literal|"return /foo/xci"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEStatic
specifier|public
name|void
name|testEStatic
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EStatic Optional) empty)))"
argument_list|,
literal|"return Optional.empty()"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEString
specifier|public
name|void
name|testEString
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EString 'foo')))"
argument_list|,
literal|"return 'foo'"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EString ' oo')))"
argument_list|,
literal|"return ' oo'"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EString 'fo ')))"
argument_list|,
literal|"return 'fo '"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EString ' o ')))"
argument_list|,
literal|"return ' o '"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEUnary
specifier|public
name|void
name|testEUnary
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EUnary ! (EBoolean true))))"
argument_list|,
literal|"return !true"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EUnary ~ (ENumeric 1))))"
argument_list|,
literal|"return ~1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EUnary + (ENumeric 1))))"
argument_list|,
literal|"return +1"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EUnary - (ENumeric 1))))"
argument_list|,
literal|"return -(1)"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEVariable
specifier|public
name|void
name|testEVariable
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (EVariable params)))"
argument_list|,
literal|"return params"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def a (ENumeric 1)))\n"
operator|+
literal|"  (SReturn (EVariable a)))"
argument_list|,
literal|"def a = 1;\n"
operator|+
literal|"return a"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPBrace
specifier|public
name|void
name|testPBrace
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PBrace (PField (EVariable params) a) (ENumeric 10))))"
argument_list|,
literal|"return params.a[10]"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PBrace (EVariable params) (EString 'a'))))"
argument_list|,
literal|"return params['a']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPCallInvoke
specifier|public
name|void
name|testPCallInvoke
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EStatic Optional) empty)))"
argument_list|,
literal|"return Optional.empty()"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EStatic Optional) of (Args (ENumeric 1)))))"
argument_list|,
literal|"return Optional.of(1)"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EStatic Objects) equals (Args (ENumeric 1) (ENumeric 2)))))"
argument_list|,
literal|"return Objects.equals(1, 2)"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PCallInvoke (EVariable params) equals (Args (ENumeric 1)))))"
argument_list|,
literal|"return params.equals(1)"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPField
specifier|public
name|void
name|testPField
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PField (EVariable params) a)))"
argument_list|,
literal|"return params.a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SReturn (PField nullSafe (EVariable params) a)))"
argument_list|,
literal|"return params?.a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int[] a (ENewArray int dims (Args (ENumeric 10)))))\n"
operator|+
literal|"  (SReturn (PField (EVariable a) length)))"
argument_list|,
literal|"int[] a = new int[10];\n"
operator|+
literal|"return a.length"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration org.elasticsearch.painless.FeatureTest a (ENewObj org.elasticsearch.painless.FeatureTest)))\n"
operator|+
literal|"  (SExpression (EAssignment (PField (EVariable a) x) = (ENumeric 10)))\n"
operator|+
literal|"  (SReturn (PField (EVariable a) x)))"
argument_list|,
literal|"org.elasticsearch.painless.FeatureTest a = new org.elasticsearch.painless.FeatureTest();\n"
operator|+
literal|"a.x = 10;\n"
operator|+
literal|"return a.x"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubArrayLength
specifier|public
name|void
name|testPSubArrayLength
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|PSubArrayLength
name|node
init|=
operator|new
name|PSubArrayLength
argument_list|(
name|l
argument_list|,
literal|"int"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubArrayLength (EVariable a))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeField (PSubArrayLength (EVariable a)))"
argument_list|,
operator|new
name|PSubNullSafeField
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubBrace
specifier|public
name|void
name|testPSubBrace
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|PSubBrace
name|node
init|=
operator|new
name|PSubBrace
argument_list|(
name|l
argument_list|,
name|Definition
operator|.
name|INT_TYPE
argument_list|,
operator|new
name|ENumeric
argument_list|(
name|l
argument_list|,
literal|"1"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubBrace (EVariable a) (ENumeric 1))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubCallInvoke
specifier|public
name|void
name|testPSubCallInvoke
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|RuntimeClass
name|c
init|=
name|definition
operator|.
name|getRuntimeClass
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|c
operator|.
name|methods
operator|.
name|get
argument_list|(
operator|new
name|MethodKey
argument_list|(
literal|"toString"
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|PSubCallInvoke
name|node
init|=
operator|new
name|PSubCallInvoke
argument_list|(
name|l
argument_list|,
name|m
argument_list|,
literal|null
argument_list|,
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubCallInvoke (EVariable a) toString)"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubCallInvoke (EVariable a) toString))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|m
operator|=
name|c
operator|.
name|methods
operator|.
name|get
argument_list|(
operator|new
name|MethodKey
argument_list|(
literal|"equals"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|=
operator|new
name|PSubCallInvoke
argument_list|(
name|l
argument_list|,
name|m
argument_list|,
literal|null
argument_list|,
name|singletonList
argument_list|(
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubCallInvoke (EVariable a) equals (Args (EVariable b)))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubCallInvoke (EVariable a) equals (Args (EVariable b))))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubDefArray
specifier|public
name|void
name|testPSubDefArray
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|PSubDefArray
name|node
init|=
operator|new
name|PSubDefArray
argument_list|(
name|l
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubDefArray (EVariable a) (EConstant Integer 1))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubDefCall
specifier|public
name|void
name|testPSubDefCall
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|PSubDefCall
name|node
init|=
operator|new
name|PSubDefCall
argument_list|(
name|l
argument_list|,
literal|"toString"
argument_list|,
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubDefCall (EVariable a) toString)"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubDefCall (EVariable a) toString))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|node
operator|=
operator|new
name|PSubDefCall
argument_list|(
name|l
argument_list|,
literal|"equals"
argument_list|,
name|singletonList
argument_list|(
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubDefCall (EVariable a) equals (Args (EVariable b)))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubDefCall (EVariable a) equals (Args (EVariable b))))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|node
operator|=
operator|new
name|PSubDefCall
argument_list|(
name|l
argument_list|,
literal|"superWeird"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"b"
argument_list|)
argument_list|,
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"c"
argument_list|)
argument_list|,
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubDefCall (EVariable a) superWeird (Args (EVariable b) (EVariable c) (EVariable d)))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubDefCall (EVariable a) superWeird (Args (EVariable b) (EVariable c) (EVariable d))))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubDefField
specifier|public
name|void
name|testPSubDefField
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|PSubDefField
name|node
init|=
operator|new
name|PSubDefField
argument_list|(
name|l
argument_list|,
literal|"ok"
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubDefField (EVariable a) ok)"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubDefField (EVariable a) ok))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubField
specifier|public
name|void
name|testPSubField
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Struct
name|s
init|=
name|definition
operator|.
name|getType
argument_list|(
name|Boolean
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|struct
decl_stmt|;
name|Field
name|f
init|=
name|s
operator|.
name|staticMembers
operator|.
name|get
argument_list|(
literal|"TRUE"
argument_list|)
decl_stmt|;
name|PSubField
name|node
init|=
operator|new
name|PSubField
argument_list|(
name|l
argument_list|,
name|f
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EStatic
argument_list|(
name|l
argument_list|,
literal|"Boolean"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubField (EStatic Boolean) TRUE)"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubField (EStatic Boolean) TRUE))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubListShortcut
specifier|public
name|void
name|testPSubListShortcut
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Struct
name|s
init|=
name|definition
operator|.
name|getType
argument_list|(
name|List
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|struct
decl_stmt|;
name|PSubListShortcut
name|node
init|=
operator|new
name|PSubListShortcut
argument_list|(
name|l
argument_list|,
name|s
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubListShortcut (EVariable a) (EConstant Integer 1))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubListShortcut (EVariable a) (EConstant Integer 1)))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|s
operator|=
name|definition
operator|.
name|getType
argument_list|(
name|List
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|struct
expr_stmt|;
name|node
operator|=
operator|new
name|PSubListShortcut
argument_list|(
name|l
argument_list|,
name|s
argument_list|,
operator|new
name|EBinary
argument_list|(
name|l
argument_list|,
name|Operation
operator|.
name|ADD
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubListShortcut (EVariable a) (EBinary (EConstant Integer 1) + (EConstant Integer 4)))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubMapShortcut
specifier|public
name|void
name|testPSubMapShortcut
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Struct
name|s
init|=
name|definition
operator|.
name|getType
argument_list|(
name|Map
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|struct
decl_stmt|;
name|PSubMapShortcut
name|node
init|=
operator|new
name|PSubMapShortcut
argument_list|(
name|l
argument_list|,
name|s
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubMapShortcut (EVariable a) (EConstant String 'cat'))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubMapShortcut (EVariable a) (EConstant String 'cat')))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|s
operator|=
name|definition
operator|.
name|getType
argument_list|(
name|Map
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|struct
expr_stmt|;
name|node
operator|=
operator|new
name|PSubMapShortcut
argument_list|(
name|l
argument_list|,
name|s
argument_list|,
operator|new
name|EBinary
argument_list|(
name|l
argument_list|,
name|Operation
operator|.
name|ADD
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubMapShortcut (EVariable a) (EBinary (EConstant Integer 1) + (EConstant Integer 4)))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSubShortcut
specifier|public
name|void
name|testPSubShortcut
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Struct
name|s
init|=
name|definition
operator|.
name|getType
argument_list|(
name|FeatureTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|struct
decl_stmt|;
name|Method
name|getter
init|=
name|s
operator|.
name|methods
operator|.
name|get
argument_list|(
operator|new
name|MethodKey
argument_list|(
literal|"getX"
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Method
name|setter
init|=
name|s
operator|.
name|methods
operator|.
name|get
argument_list|(
operator|new
name|MethodKey
argument_list|(
literal|"setX"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|PSubShortcut
name|node
init|=
operator|new
name|PSubShortcut
argument_list|(
name|l
argument_list|,
literal|"x"
argument_list|,
name|FeatureTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|getter
argument_list|,
name|setter
argument_list|)
decl_stmt|;
name|node
operator|.
name|prefix
operator|=
operator|new
name|EVariable
argument_list|(
name|l
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubShortcut (EVariable a) x)"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(PSubNullSafeCallInvoke (PSubShortcut (EVariable a) x))"
argument_list|,
operator|new
name|PSubNullSafeCallInvoke
argument_list|(
name|l
argument_list|,
name|node
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSBreak
specifier|public
name|void
name|testSBreak
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int itr (ENumeric 2)))\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int a (ENumeric 1)))\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int b (ENumeric 1)))\n"
operator|+
literal|"  (SDo (EComp (EVariable b)< (ENumeric 1000)) (SBlock\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable itr) ++ post))\n"
operator|+
literal|"    (SIf (EComp (EVariable itr)> (ENumeric 10000)) (SBlock (SBreak)))\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int tmp (EVariable a)))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable a) = (EVariable b)))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable b) = (EBinary (EVariable tmp) + (EVariable b))))))\n"
operator|+
literal|"  (SReturn (EVariable b)))"
argument_list|,
literal|"int itr = 2;\n"
operator|+
literal|"int a = 1;\n"
operator|+
literal|"int b = 1;\n"
operator|+
literal|"do {\n"
operator|+
literal|"  itr++;\n"
operator|+
literal|"  if (itr> 10000) {\n"
operator|+
literal|"    break\n"
operator|+
literal|"  }\n"
operator|+
literal|"  int tmp = a;\n"
operator|+
literal|"  a = b;\n"
operator|+
literal|"  b = tmp + b\n"
operator|+
literal|"} while (b< 1000);\n"
operator|+
literal|"return b"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSContinue
specifier|public
name|void
name|testSContinue
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int itr (ENumeric 2)))\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int a (ENumeric 1)))\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int b (ENumeric 1)))\n"
operator|+
literal|"  (SDo (EComp (EVariable b)< (ENumeric 1000)) (SBlock\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable itr) ++ post))\n"
operator|+
literal|"    (SIf (EComp (EVariable itr)< (ENumeric 10000)) (SBlock (SContinue)))\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int tmp (EVariable a)))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable a) = (EVariable b)))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable b) = (EBinary (EVariable tmp) + (EVariable b))))))\n"
operator|+
literal|"  (SReturn (EVariable b)))"
argument_list|,
literal|"int itr = 2;\n"
operator|+
literal|"int a = 1;\n"
operator|+
literal|"int b = 1;\n"
operator|+
literal|"do {\n"
operator|+
literal|"  itr++;\n"
operator|+
literal|"  if (itr< 10000) {\n"
operator|+
literal|"    continue\n"
operator|+
literal|"  }\n"
operator|+
literal|"  int tmp = a;\n"
operator|+
literal|"  a = b;\n"
operator|+
literal|"  b = tmp + b\n"
operator|+
literal|"} while (b< 1000);\n"
operator|+
literal|"return b"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSDeclBlock
specifier|public
name|void
name|testSDeclBlock
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def a))\n"
operator|+
literal|"  (SExpression (EAssignment (EVariable a) = (ENumeric 10)))\n"
operator|+
literal|"  (SReturn (EVariable a)))"
argument_list|,
literal|"def a;\n"
operator|+
literal|"a = 10;\n"
operator|+
literal|"return a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration def a (ENumeric 10)))\n"
operator|+
literal|"  (SReturn (EVariable a)))"
argument_list|,
literal|"def a = 10;\n"
operator|+
literal|"return a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock\n"
operator|+
literal|"    (SDeclaration def a)\n"
operator|+
literal|"    (SDeclaration def b)\n"
operator|+
literal|"    (SDeclaration def c))\n"
operator|+
literal|"  (SReturn (EVariable a)))"
argument_list|,
literal|"def a, b, c;\n"
operator|+
literal|"return a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock\n"
operator|+
literal|"    (SDeclaration def a (ENumeric 10))\n"
operator|+
literal|"    (SDeclaration def b (ENumeric 20))\n"
operator|+
literal|"    (SDeclaration def c (ENumeric 100)))\n"
operator|+
literal|"  (SReturn (EVariable a)))"
argument_list|,
literal|"def a = 10, b = 20, c = 100;\n"
operator|+
literal|"return a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock\n"
operator|+
literal|"    (SDeclaration def a (ENumeric 10))\n"
operator|+
literal|"    (SDeclaration def b)\n"
operator|+
literal|"    (SDeclaration def c (ENumeric 100)))\n"
operator|+
literal|"  (SReturn (EVariable a)))"
argument_list|,
literal|"def a = 10, b, c = 100;\n"
operator|+
literal|"return a"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SIf (PField (EVariable params) a) (SBlock\n"
operator|+
literal|"    (SDeclBlock\n"
operator|+
literal|"      (SDeclaration def a (ENumeric 10))\n"
operator|+
literal|"      (SDeclaration def b)\n"
operator|+
literal|"      (SDeclaration def c (ENumeric 100)))\n"
operator|+
literal|"    (SReturn (EVariable a))))\n"
operator|+
literal|"  (SReturn (EBoolean false)))"
argument_list|,
literal|"if (params.a) {"
operator|+
literal|"  def a = 10, b, c = 100;\n"
operator|+
literal|"  return a\n"
operator|+
literal|"}\n"
operator|+
literal|"return false"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSDo
specifier|public
name|void
name|testSDo
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int itr (ENumeric 2)))\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int a (ENumeric 1)))\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int b (ENumeric 1)))\n"
operator|+
literal|"  (SDo (EComp (EVariable b)< (ENumeric 1000)) (SBlock\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable itr) ++ post))\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int tmp (EVariable a)))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable a) = (EVariable b)))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable b) = (EBinary (EVariable tmp) + (EVariable b))))))\n"
operator|+
literal|"  (SReturn (EVariable b)))"
argument_list|,
literal|"int itr = 2;\n"
operator|+
literal|"int a = 1;\n"
operator|+
literal|"int b = 1;\n"
operator|+
literal|"do {\n"
operator|+
literal|"  itr++;\n"
operator|+
literal|"  int tmp = a;\n"
operator|+
literal|"  a = b;\n"
operator|+
literal|"  b = tmp + b\n"
operator|+
literal|"} while (b< 1000);\n"
operator|+
literal|"return b"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSEach
specifier|public
name|void
name|testSEach
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int l (ENumeric 0)))\n"
operator|+
literal|"  (SEach String s (EListInit (EString 'cat') (EString 'dog') (EString 'chicken')) (SBlock "
operator|+
literal|"(SExpression (EAssignment (EVariable l) += (PCallInvoke (EVariable s) length)))))\n"
operator|+
literal|"  (SReturn (EVariable l)))"
argument_list|,
literal|"int l = 0;\n"
operator|+
literal|"for (String s : ['cat', 'dog', 'chicken']) {\n"
operator|+
literal|"  l += s.length()\n"
operator|+
literal|"}\n"
operator|+
literal|"return l"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int l (ENumeric 0)))\n"
operator|+
literal|"  (SEach String s (EListInit (EString 'cat') (EString 'dog') (EString 'chicken')) (SBlock\n"
operator|+
literal|"    (SDeclBlock (SDeclaration String s2 (EBinary (EString 'dire ') + (EVariable s))))\n"
operator|+
literal|"    (SExpression (EAssignment (EVariable l) += (PCallInvoke (EVariable s2) length)))))\n"
operator|+
literal|"  (SReturn (EVariable l)))"
argument_list|,
literal|"int l = 0;\n"
operator|+
literal|"for (String s : ['cat', 'dog', 'chicken']) {\n"
operator|+
literal|"  String s2 = 'dire ' + s;\n"
operator|+
literal|"  l += s2.length()\n"
operator|+
literal|"}\n"
operator|+
literal|"return l"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSFor
specifier|public
name|void
name|testSFor
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int sum (ENumeric 0)))\n"
operator|+
literal|"  (SFor\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int i (ENumeric 0)))\n"
operator|+
literal|"    (EComp (EVariable i)< (ENumeric 1000))\n"
operator|+
literal|"    (EAssignment (EVariable i) ++ post)\n"
operator|+
literal|"    (SBlock (SExpression (EAssignment (EVariable sum) += (EVariable i)))))\n"
operator|+
literal|"  (SReturn (EVariable sum)))"
argument_list|,
literal|"int sum = 0;\n"
operator|+
literal|"for (int i = 0; i< 1000; i++) {\n"
operator|+
literal|"  sum += i\n"
operator|+
literal|"}\n"
operator|+
literal|"return sum"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int sum (ENumeric 0)))\n"
operator|+
literal|"  (SFor\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int i (ENumeric 0)))\n"
operator|+
literal|"    (EComp (EVariable i)< (ENumeric 1000))\n"
operator|+
literal|"    (EAssignment (EVariable i) ++ post)\n"
operator|+
literal|"    (SBlock (SFor\n"
operator|+
literal|"      (SDeclBlock (SDeclaration int j (ENumeric 0)))\n"
operator|+
literal|"      (EComp (EVariable j)< (ENumeric 1000))\n"
operator|+
literal|"      (EAssignment (EVariable j) ++ post)\n"
operator|+
literal|"      (SBlock (SExpression (EAssignment (EVariable sum) += (EBinary (EVariable i) * (EVariable j))))))))\n"
operator|+
literal|"  (SReturn (EVariable sum)))"
argument_list|,
literal|"int sum = 0;\n"
operator|+
literal|"for (int i = 0; i< 1000; i++) {\n"
operator|+
literal|"  for (int j = 0; j< 1000; j++) {\n"
operator|+
literal|"    sum += i * j\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
operator|+
literal|"return sum"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSIf
specifier|public
name|void
name|testSIf
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SIf (PField (EVariable param) a) (SBlock (SReturn (EBoolean true)))))"
argument_list|,
literal|"if (param.a) {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (SIf (PField (EVariable param) a) (SBlock\n"
operator|+
literal|"  (SIf (PField (EVariable param) b) (SBlock (SReturn (EBoolean true))))\n"
operator|+
literal|"  (SReturn (EBoolean false)))))"
argument_list|,
literal|"if (param.a) {\n"
operator|+
literal|"  if (param.b) {\n"
operator|+
literal|"    return true\n"
operator|+
literal|"  }\n"
operator|+
literal|"  return false\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSIfElse
specifier|public
name|void
name|testSIfElse
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SIfElse (PField (EVariable param) a)\n"
operator|+
literal|"  (SBlock (SReturn (EBoolean true)))\n"
operator|+
literal|"  (SBlock (SReturn (EBoolean false)))))"
argument_list|,
literal|"if (param.a) {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"} else {\n"
operator|+
literal|"  return false\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int i (ENumeric 0)))\n"
operator|+
literal|"  (SIfElse (PField (EVariable param) a)\n"
operator|+
literal|"    (SBlock (SIfElse (PField (EVariable param) b)\n"
operator|+
literal|"      (SBlock (SReturn (EBoolean true)))\n"
operator|+
literal|"      (SBlock (SReturn (EString 'cat')))))\n"
operator|+
literal|"    (SBlock (SReturn (EBoolean false)))))"
argument_list|,
literal|"int i = 0;\n"
operator|+
literal|"if (param.a) {\n"
operator|+
literal|"  if (param.b) {\n"
operator|+
literal|"    return true\n"
operator|+
literal|"  } else {\n"
operator|+
literal|"    return 'cat'\n"
operator|+
literal|"  }\n"
operator|+
literal|"} else {"
operator|+
literal|"  return false\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSSubEachArray
specifier|public
name|void
name|testSSubEachArray
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Variable
name|v
init|=
operator|new
name|Variable
argument_list|(
name|l
argument_list|,
literal|"test"
argument_list|,
name|Definition
operator|.
name|INT_TYPE
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|AExpression
name|e
init|=
operator|new
name|ENewArray
argument_list|(
name|l
argument_list|,
literal|"int"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SBlock
name|b
init|=
operator|new
name|SBlock
argument_list|(
name|l
argument_list|,
name|singletonList
argument_list|(
operator|new
name|SReturn
argument_list|(
name|l
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SSubEachArray
name|node
init|=
operator|new
name|SSubEachArray
argument_list|(
name|l
argument_list|,
name|v
argument_list|,
name|e
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"(SSubEachArray int test (ENewArray int init (Args (EConstant Integer 1) (EConstant Integer 2) (EConstant Integer 3))) "
operator|+
literal|"(SBlock (SReturn (EConstant Integer 5))))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSSubEachIterable
specifier|public
name|void
name|testSSubEachIterable
parameter_list|()
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Variable
name|v
init|=
operator|new
name|Variable
argument_list|(
name|l
argument_list|,
literal|"test"
argument_list|,
name|Definition
operator|.
name|INT_TYPE
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|AExpression
name|e
init|=
operator|new
name|EListInit
argument_list|(
name|l
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SBlock
name|b
init|=
operator|new
name|SBlock
argument_list|(
name|l
argument_list|,
name|singletonList
argument_list|(
operator|new
name|SReturn
argument_list|(
name|l
argument_list|,
operator|new
name|EConstant
argument_list|(
name|l
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SSubEachIterable
name|node
init|=
operator|new
name|SSubEachIterable
argument_list|(
name|l
argument_list|,
name|v
argument_list|,
name|e
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"(SSubEachIterable int test (EListInit (EConstant Integer 1) (EConstant Integer 2) (EConstant Integer 3)) (SBlock "
operator|+
literal|"(SReturn (EConstant Integer 5))))"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSThrow
specifier|public
name|void
name|testSThrow
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (SThrow (ENewObj RuntimeException)))"
argument_list|,
literal|"throw new RuntimeException()"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSWhile
specifier|public
name|void
name|testSWhile
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int i (ENumeric 0)))\n"
operator|+
literal|"  (SWhile (EComp (EVariable i)< (ENumeric 10)) (SBlock (SExpression (EAssignment (EVariable i) ++ post))))\n"
operator|+
literal|"  (SReturn (EVariable i)))"
argument_list|,
literal|"int i = 0;\n"
operator|+
literal|"while (i< 10) {\n"
operator|+
literal|"  i++\n"
operator|+
literal|"}\n"
operator|+
literal|"return i"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSFunction
specifier|public
name|void
name|testSFunction
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SFunction def a\n"
operator|+
literal|"    (SReturn (EBoolean true)))\n"
operator|+
literal|"  (SReturn (EBoolean true)))"
argument_list|,
literal|"def a() {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"}\n"
operator|+
literal|"return true"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SFunction def a (Args (Pair int i) (Pair int j))\n"
operator|+
literal|"    (SReturn (EBoolean true)))\n"
operator|+
literal|"  (SReturn (EBoolean true)))"
argument_list|,
literal|"def a(int i, int j) {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"}\n"
operator|+
literal|"return true"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SFunction def a (Args (Pair int i) (Pair int j))\n"
operator|+
literal|"    (SIf (EComp (EVariable i)< (EVariable j)) (SBlock (SReturn (EBoolean true))))\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int k (EBinary (EVariable i) + (EVariable j))))\n"
operator|+
literal|"    (SReturn (EVariable k)))\n"
operator|+
literal|"  (SReturn (EBoolean true)))"
argument_list|,
literal|"def a(int i, int j) {\n"
operator|+
literal|"  if (i< j) {\n"
operator|+
literal|"    return true\n"
operator|+
literal|"  }\n"
operator|+
literal|"  int k = i + j;\n"
operator|+
literal|"  return k\n"
operator|+
literal|"}\n"
operator|+
literal|"return true"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource\n"
operator|+
literal|"  (SFunction def a\n"
operator|+
literal|"    (SReturn (EBoolean true)))\n"
operator|+
literal|"  (SFunction def b\n"
operator|+
literal|"    (SReturn (EBoolean false)))\n"
operator|+
literal|"  (SReturn (EBoolean true)))"
argument_list|,
literal|"def a() {\n"
operator|+
literal|"  return true\n"
operator|+
literal|"}\n"
operator|+
literal|"def b() {\n"
operator|+
literal|"  return false\n"
operator|+
literal|"}\n"
operator|+
literal|"return true"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSTryAndSCatch
specifier|public
name|void
name|testSTryAndSCatch
parameter_list|()
block|{
name|assertToString
argument_list|(
literal|"(SSource (STry (SBlock (SReturn (ENumeric 1)))\n"
operator|+
literal|"  (SCatch Exception e (SBlock (SReturn (ENumeric 2))))))"
argument_list|,
literal|"try {\n"
operator|+
literal|"  return 1\n"
operator|+
literal|"} catch (Exception e) {\n"
operator|+
literal|"  return 2\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (STry (SBlock\n"
operator|+
literal|"  (SDeclBlock (SDeclaration int i (ENumeric 1)))\n"
operator|+
literal|"  (SReturn (ENumeric 1)))\n"
operator|+
literal|"  (SCatch Exception e (SBlock (SReturn (ENumeric 2))))))"
argument_list|,
literal|"try {\n"
operator|+
literal|"  int i = 1;"
operator|+
literal|"  return 1\n"
operator|+
literal|"} catch (Exception e) {\n"
operator|+
literal|"  return 2\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (STry (SBlock (SReturn (ENumeric 1)))\n"
operator|+
literal|"  (SCatch Exception e (SBlock\n"
operator|+
literal|"    (SDeclBlock (SDeclaration int i (ENumeric 1)))\n"
operator|+
literal|"    (SReturn (ENumeric 2))))))"
argument_list|,
literal|"try {\n"
operator|+
literal|"  return 1\n"
operator|+
literal|"} catch (Exception e) {"
operator|+
literal|"  int i = 1;\n"
operator|+
literal|"  return 2\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertToString
argument_list|(
literal|"(SSource (STry (SBlock (SReturn (ENumeric 1)))\n"
operator|+
literal|"  (SCatch NullPointerException e (SBlock (SReturn (ENumeric 2))))\n"
operator|+
literal|"  (SCatch Exception e (SBlock (SReturn (ENumeric 3))))))"
argument_list|,
literal|"try {\n"
operator|+
literal|"  return 1\n"
operator|+
literal|"} catch (NullPointerException e) {\n"
operator|+
literal|"  return 2\n"
operator|+
literal|"} catch (Exception e) {\n"
operator|+
literal|"  return 3\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertToString
specifier|private
name|void
name|assertToString
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|code
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|walk
argument_list|(
name|code
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|walk
specifier|private
name|SSource
name|walk
parameter_list|(
name|String
name|code
parameter_list|)
block|{
name|ScriptClassInfo
name|scriptClassInfo
init|=
operator|new
name|ScriptClassInfo
argument_list|(
name|definition
argument_list|,
name|GenericElasticsearchScript
operator|.
name|class
argument_list|)
decl_stmt|;
name|CompilerSettings
name|compilerSettings
init|=
operator|new
name|CompilerSettings
argument_list|()
decl_stmt|;
name|compilerSettings
operator|.
name|setRegexesEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|Walker
operator|.
name|buildPainlessTree
argument_list|(
name|scriptClassInfo
argument_list|,
operator|new
name|MainMethodReserved
argument_list|()
argument_list|,
name|getTestName
argument_list|()
argument_list|,
name|code
argument_list|,
name|compilerSettings
argument_list|,
name|definition
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Failed to compile: "
operator|+
name|code
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

