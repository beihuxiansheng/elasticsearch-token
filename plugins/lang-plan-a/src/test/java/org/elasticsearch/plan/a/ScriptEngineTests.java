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
name|script
operator|.
name|CompiledScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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
name|HashMap
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

begin_class
DECL|class|ScriptEngineTests
specifier|public
class|class
name|ScriptEngineTests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testSimpleEquation
specifier|public
name|void
name|testSimpleEquation
parameter_list|()
block|{
specifier|final
name|Object
name|value
init|=
name|exec
argument_list|(
literal|"return 1 + 2;"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapAccess
specifier|public
name|void
name|testMapAccess
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj2
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|obj2
operator|.
name|put
argument_list|(
literal|"prop2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|obj1
operator|.
name|put
argument_list|(
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|obj1
operator|.
name|put
argument_list|(
literal|"obj2"
argument_list|,
name|obj2
argument_list|)
expr_stmt|;
name|obj1
operator|.
name|put
argument_list|(
literal|"l"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"2"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"obj1"
argument_list|,
name|obj1
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|exec
argument_list|(
literal|"return input.get(\"obj1\");"
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|obj1
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|obj1
operator|.
name|get
argument_list|(
literal|"prop1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|obj1
operator|.
name|get
argument_list|(
literal|"obj2"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"prop2"
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|exec
argument_list|(
literal|"return ((List)((Map<String, Object>)input.get(\"obj1\")).get(\"l\")).get(0);"
argument_list|,
name|vars
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|testAccessListInScript
specifier|public
name|void
name|testAccessListInScript
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj2
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|obj2
operator|.
name|put
argument_list|(
literal|"prop2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|obj1
operator|.
name|put
argument_list|(
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|obj1
operator|.
name|put
argument_list|(
literal|"obj2"
argument_list|,
name|obj2
argument_list|)
expr_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"l"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|,
name|obj1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|exec
argument_list|(
literal|"return ((List)input.get(\"l\")).size();"
argument_list|,
name|vars
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|exec
argument_list|(
literal|"return ((List)input.get(\"l\")).get(0);"
argument_list|,
name|vars
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|exec
argument_list|(
literal|"return ((List)input.get(\"l\")).get(3);"
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|obj1
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|obj1
operator|.
name|get
argument_list|(
literal|"prop1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|obj1
operator|.
name|get
argument_list|(
literal|"obj2"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"prop2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|exec
argument_list|(
literal|"return ((Map<String, Object>)((List)input.get(\"l\")).get(3)).get(\"prop1\");"
argument_list|,
name|vars
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangingVarsCrossExecution1
specifier|public
name|void
name|testChangingVarsCrossExecution1
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"ctx"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|Object
name|compiledScript
init|=
name|scriptEngine
operator|.
name|compile
argument_list|(
literal|"return ((Map<String, Object>)input.get(\"ctx\")).get(\"value\");"
argument_list|)
decl_stmt|;
name|ExecutableScript
name|script
init|=
name|scriptEngine
operator|.
name|executable
argument_list|(
operator|new
name|CompiledScript
argument_list|(
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"testChangingVarsCrossExecution1"
argument_list|,
literal|"plan-a"
argument_list|,
name|compiledScript
argument_list|)
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|script
operator|.
name|run
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|o
operator|=
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangingVarsCrossExecution2
specifier|public
name|void
name|testChangingVarsCrossExecution2
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Object
name|compiledScript
init|=
name|scriptEngine
operator|.
name|compile
argument_list|(
literal|"return input.get(\"value\");"
argument_list|)
decl_stmt|;
name|ExecutableScript
name|script
init|=
name|scriptEngine
operator|.
name|executable
argument_list|(
operator|new
name|CompiledScript
argument_list|(
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"testChangingVarsCrossExecution2"
argument_list|,
literal|"plan-a"
argument_list|,
name|compiledScript
argument_list|)
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|script
operator|.
name|setNextVar
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|script
operator|.
name|run
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|script
operator|.
name|setNextVar
argument_list|(
literal|"value"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|value
operator|=
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

