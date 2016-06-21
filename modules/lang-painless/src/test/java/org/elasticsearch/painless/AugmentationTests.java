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
DECL|class|AugmentationTests
specifier|public
class|class
name|AugmentationTests
extends|extends
name|ScriptTestCase
block|{
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"rmuir is working on this"
argument_list|)
DECL|method|testCapturingReference
specifier|public
name|void
name|testCapturingReference
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int foo(Supplier t) { return t.get() }"
operator|+
literal|"def l = new ArrayList(); l.add(1);"
operator|+
literal|"return foo(l::getLength);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIterable_Any
specifier|public
name|void
name|testIterable_Any
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"List l = new ArrayList(); l.add(1); l.any(x -> x == 1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

