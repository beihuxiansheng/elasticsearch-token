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
DECL|method|testUnsupported
specifier|public
name|void
name|testUnsupported
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|exec
argument_list|(
literal|"DoubleStream.Builder builder = DoubleStream.builder();"
operator|+
literal|"builder.add(2.0); builder.add(1.0); builder.add(3.0);"
operator|+
literal|"builder.build().reduce(Double::unsupported);"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

