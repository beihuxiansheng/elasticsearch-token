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

begin_comment
comment|/** Tests method overloading */
end_comment

begin_class
DECL|class|OverloadTests
specifier|public
class|class
name|OverloadTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testMethod
specifier|public
name|void
name|testMethod
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"return 'abc123abc'.indexOf('c');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|exec
argument_list|(
literal|"return 'abc123abc'.indexOf('c', 3);"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"return 'abc123abc'.indexOf('c', 3, 'bogus');"
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
literal|"[indexOf] with [3] arguments"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMethodDynamic
specifier|public
name|void
name|testMethodDynamic
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"def x = 'abc123abc'; return x.indexOf('c');"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|exec
argument_list|(
literal|"def x = 'abc123abc'; return x.indexOf('c', 3);"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"def x = 'abc123abc'; return x.indexOf('c', 3, 'bogus');"
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
literal|"dynamic method [indexOf] with signature [(String,int,String)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstructor
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"org.elasticsearch.painless.FeatureTest f = new org.elasticsearch.painless.FeatureTest();"
operator|+
literal|"return f.x == 0&& f.y == 0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"org.elasticsearch.painless.FeatureTest f = new org.elasticsearch.painless.FeatureTest(1, 2);"
operator|+
literal|"return f.x == 1&& f.y == 2;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStatic
specifier|public
name|void
name|testStatic
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"return org.elasticsearch.painless.FeatureTest.overloadedStatic();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|exec
argument_list|(
literal|"return org.elasticsearch.painless.FeatureTest.overloadedStatic(false);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

