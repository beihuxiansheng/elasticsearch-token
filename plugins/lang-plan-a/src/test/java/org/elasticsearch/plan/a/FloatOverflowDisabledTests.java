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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/** Tests floating point overflow with numeric overflow disabled */
end_comment

begin_class
DECL|class|FloatOverflowDisabledTests
specifier|public
class|class
name|FloatOverflowDisabledTests
extends|extends
name|ScriptTestCase
block|{
annotation|@
name|Override
DECL|method|getSettings
specifier|protected
name|Settings
name|getSettings
parameter_list|()
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|super
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|PlanAScriptEngineService
operator|.
name|NUMERIC_OVERFLOW
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|testAssignmentAdditionOverflow
specifier|public
name|void
name|testAssignmentAdditionOverflow
parameter_list|()
block|{
comment|// float
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; x += 3.4028234663852886E38f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = -3.4028234663852886E38f; x += -3.4028234663852886E38f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// double
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; x += 1.7976931348623157E308; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = -1.7976931348623157E308; x += -1.7976931348623157E308; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAssignmentSubtractionOverflow
specifier|public
name|void
name|testAssignmentSubtractionOverflow
parameter_list|()
block|{
comment|// float
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; x -= -3.4028234663852886E38f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = -3.4028234663852886E38f; x -= 3.4028234663852886E38f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// double
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; x -= -1.7976931348623157E308; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = -1.7976931348623157E308; x -= 1.7976931348623157E308; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAssignmentMultiplicationOverflow
specifier|public
name|void
name|testAssignmentMultiplicationOverflow
parameter_list|()
block|{
comment|// float
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; x *= 3.4028234663852886E38f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; x *= -3.4028234663852886E38f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// double
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; x *= 1.7976931348623157E308; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; x *= -1.7976931348623157E308; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAssignmentDivisionOverflow
specifier|public
name|void
name|testAssignmentDivisionOverflow
parameter_list|()
block|{
comment|// float
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; x /= 1.401298464324817E-45f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; x /= -1.401298464324817E-45f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = 1.0f; x /= 0.0f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// double
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; x /= 4.9E-324; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; x /= -4.9E-324; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.0f; x /= 0.0; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAddition
specifier|public
name|void
name|testAddition
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; float y = 3.4028234663852886E38f; return x + y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; double y = 1.7976931348623157E308; return x + y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAdditionConst
specifier|public
name|void
name|testAdditionConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return 3.4028234663852886E38f + 3.4028234663852886E38f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1.7976931348623157E308 + 1.7976931348623157E308;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testSubtraction
specifier|public
name|void
name|testSubtraction
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"float x = -3.4028234663852886E38f; float y = 3.4028234663852886E38f; return x - y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = -1.7976931348623157E308; double y = 1.7976931348623157E308; return x - y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testSubtractionConst
specifier|public
name|void
name|testSubtractionConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return -3.4028234663852886E38f - 3.4028234663852886E38f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return -1.7976931348623157E308 - 1.7976931348623157E308;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testMultiplication
specifier|public
name|void
name|testMultiplication
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; float y = 3.4028234663852886E38f; return x * y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; double y = 1.7976931348623157E308; return x * y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testMultiplicationConst
specifier|public
name|void
name|testMultiplicationConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return 3.4028234663852886E38f * 3.4028234663852886E38f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1.7976931348623157E308 * 1.7976931348623157E308;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testDivision
specifier|public
name|void
name|testDivision
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"float x = 3.4028234663852886E38f; float y = 1.401298464324817E-45f; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = 1.0f; float y = 0.0f; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.7976931348623157E308; double y = 4.9E-324; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.0; double y = 0.0; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testDivisionConst
specifier|public
name|void
name|testDivisionConst
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|exec
argument_list|(
literal|"return 3.4028234663852886E38f / 1.401298464324817E-45f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1.0f / 0.0f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1.7976931348623157E308 / 4.9E-324;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1.0 / 0.0;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testDivisionNaN
specifier|public
name|void
name|testDivisionNaN
parameter_list|()
throws|throws
name|Exception
block|{
comment|// float division, constant division, and assignment
try|try
block|{
name|exec
argument_list|(
literal|"float x = 0f; float y = 0f; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 0f / 0f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = 0f; x /= 0f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// double division, constant division, and assignment
try|try
block|{
name|exec
argument_list|(
literal|"double x = 0.0; double y = 0.0; return x / y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 0.0 / 0.0;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 0.0; x /= 0.0; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testRemainderNaN
specifier|public
name|void
name|testRemainderNaN
parameter_list|()
throws|throws
name|Exception
block|{
comment|// float division, constant division, and assignment
try|try
block|{
name|exec
argument_list|(
literal|"float x = 1f; float y = 0f; return x % y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1f % 0f;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"float x = 1f; x %= 0f; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
comment|// double division, constant division, and assignment
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.0; double y = 0.0; return x % y;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"return 1.0 % 0.0;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 1.0; x %= 0.0; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|expected
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

