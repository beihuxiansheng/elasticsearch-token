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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_comment
comment|/** tests for throw/try/catch in painless */
end_comment

begin_class
DECL|class|TryCatchTests
specifier|public
class|class
name|TryCatchTests
extends|extends
name|ScriptTestCase
block|{
comment|/** throws an exception */
DECL|method|testThrow
specifier|public
name|void
name|testThrow
parameter_list|()
block|{
name|RuntimeException
name|exception
init|=
name|expectScriptThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"throw new RuntimeException('test')"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** catches the exact exception */
DECL|method|testCatch
specifier|public
name|void
name|testCatch
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"try { if (params.param == 'true') throw new RuntimeException('test'); } "
operator|+
literal|"catch (RuntimeException e) { return 1; } return 2;"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"param"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** catches superclass of the exception */
DECL|method|testCatchSuperclass
specifier|public
name|void
name|testCatchSuperclass
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"try { if (params.param == 'true') throw new RuntimeException('test'); } "
operator|+
literal|"catch (Exception e) { return 1; } return 2;"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"param"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** tries to catch a different type of exception */
DECL|method|testNoCatch
specifier|public
name|void
name|testNoCatch
parameter_list|()
block|{
name|RuntimeException
name|exception
init|=
name|expectScriptThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"try { if (params.param == 'true') throw new RuntimeException('test'); } "
operator|+
literal|"catch (ArithmeticException e) { return 1; } return 2;"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"param"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

