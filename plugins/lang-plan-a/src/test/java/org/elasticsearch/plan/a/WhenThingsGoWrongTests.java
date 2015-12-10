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

begin_class
DECL|class|WhenThingsGoWrongTests
specifier|public
class|class
name|WhenThingsGoWrongTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testNullPointer
specifier|public
name|void
name|testNullPointer
parameter_list|()
block|{
try|try
block|{
name|exec
argument_list|(
literal|"int x = (int) ((Map) input).get(\"missing\"); return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit npe"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testInvalidShift
specifier|public
name|void
name|testInvalidShift
parameter_list|()
block|{
try|try
block|{
name|exec
argument_list|(
literal|"float x = 15F; x<<= 2; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit cce"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|exec
argument_list|(
literal|"double x = 15F; x<<= 2; return x;"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit cce"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|expected
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

