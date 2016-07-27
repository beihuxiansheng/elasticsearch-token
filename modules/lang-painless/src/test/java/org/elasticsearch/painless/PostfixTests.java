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
DECL|class|PostfixTests
specifier|public
class|class
name|PostfixTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testConstantPostfixes
specifier|public
name|void
name|testConstantPostfixes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|exec
argument_list|(
literal|"2.toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"[1, 2, 3, 4, 5][3]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4"
argument_list|,
name|exec
argument_list|(
literal|"[1, 2, 3, 4, 5][3].toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"new int[] {1, 2, 3, 4, 5}[2]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4"
argument_list|,
name|exec
argument_list|(
literal|"(2 + 2).toString()"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConditionalPostfixes
specifier|public
name|void
name|testConditionalPostfixes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"boolean b = false; (b ? 4 : 5).toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"Map x = new HashMap(); x['test'] = 3;"
operator|+
literal|"Map y = new HashMap(); y['test'] = 4;"
operator|+
literal|"boolean b = true;"
operator|+
literal|"return (int)(b ? x : y).get('test')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAssignmentPostfixes
specifier|public
name|void
name|testAssignmentPostfixes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"int x; '3' == (x = 3).toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"int x; (x = 3).compareTo(4)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"long[] x; (x = new long[1])[0] = 3; return x[0]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int x; ((x)) = 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefConditionalPostfixes
specifier|public
name|void
name|testDefConditionalPostfixes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"def b = false; (b ? 4 : 5).toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"def x = new HashMap(); x['test'] = 3;"
operator|+
literal|"def y = new HashMap(); y['test'] = 4;"
operator|+
literal|"boolean b = true;"
operator|+
literal|"return (b ? x : y).get('test')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefAssignmentPostfixes
specifier|public
name|void
name|testDefAssignmentPostfixes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x; '3' == (x = 3).toString()"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exec
argument_list|(
literal|"def x; (x = 3).compareTo(4)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|exec
argument_list|(
literal|"def x; (x = new long[1])[0] = 3; return x[0]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"def x; ((x)) = 2; return x;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

