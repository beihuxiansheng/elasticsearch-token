begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.python
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|python
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|MapBuilder
import|;
end_import

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
name|ImmutableSettings
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
name|testng
operator|.
name|annotations
operator|.
name|BeforeTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|PythonScriptEngineTests
specifier|public
class|class
name|PythonScriptEngineTests
block|{
DECL|field|se
specifier|private
name|PythonScriptEngineService
name|se
decl_stmt|;
DECL|method|setup
annotation|@
name|BeforeTest
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|se
operator|=
operator|new
name|PythonScriptEngineService
argument_list|(
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleEquation
annotation|@
name|Test
specifier|public
name|void
name|testSimpleEquation
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|se
operator|.
name|execute
argument_list|(
name|se
operator|.
name|compile
argument_list|(
literal|"1 + 2"
argument_list|)
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapAccess
annotation|@
name|Test
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|MapBuilder
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|newMapBuilder
argument_list|()
decl|.
name|put
argument_list|(
literal|"prop2"
argument_list|,
literal|"value2"
argument_list|)
decl|.
name|map
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj1
init|=
name|MapBuilder
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|newMapBuilder
argument_list|()
decl|.
name|put
argument_list|(
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
decl|.
name|put
argument_list|(
literal|"obj2"
argument_list|,
name|obj2
argument_list|)
decl|.
name|put
argument_list|(
literal|"l"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"2"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
decl|.
name|map
argument_list|()
decl_stmt|;
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
name|o
init|=
name|se
operator|.
name|execute
argument_list|(
name|se
operator|.
name|compile
argument_list|(
literal|"obj1"
argument_list|)
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
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
name|o
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|obj1
operator|.
name|get
argument_list|(
literal|"prop1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
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
argument_list|)
operator|.
name|get
argument_list|(
literal|"prop2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|o
operator|=
name|se
operator|.
name|execute
argument_list|(
name|se
operator|.
name|compile
argument_list|(
literal|"obj1['l'][0]"
argument_list|)
argument_list|,
name|vars
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|String
operator|)
name|o
operator|)
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAccessListInScript
annotation|@
name|Test
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|MapBuilder
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|newMapBuilder
argument_list|()
decl|.
name|put
argument_list|(
literal|"prop2"
argument_list|,
literal|"value2"
argument_list|)
decl|.
name|map
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj1
init|=
name|MapBuilder
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|newMapBuilder
argument_list|()
decl|.
name|put
argument_list|(
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
decl|.
name|put
argument_list|(
literal|"obj2"
argument_list|,
name|obj2
argument_list|)
decl|.
name|map
argument_list|()
decl_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"l"
argument_list|,
name|Lists
operator|.
name|newArrayList
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
comment|//        Object o = se.execute(se.compile("l.length"), vars);
comment|//        assertThat(((Number) o).intValue(), equalTo(4));
name|Object
name|o
init|=
name|se
operator|.
name|execute
argument_list|(
name|se
operator|.
name|compile
argument_list|(
literal|"l[0]"
argument_list|)
argument_list|,
name|vars
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|String
operator|)
name|o
operator|)
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|o
operator|=
name|se
operator|.
name|execute
argument_list|(
name|se
operator|.
name|compile
argument_list|(
literal|"l[3]"
argument_list|)
argument_list|,
name|vars
argument_list|)
expr_stmt|;
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
name|o
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|obj1
operator|.
name|get
argument_list|(
literal|"prop1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
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
argument_list|)
operator|.
name|get
argument_list|(
literal|"prop2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|o
operator|=
name|se
operator|.
name|execute
argument_list|(
name|se
operator|.
name|compile
argument_list|(
literal|"l[3]['prop1']"
argument_list|)
argument_list|,
name|vars
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|String
operator|)
name|o
operator|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangingVarsCrossExecution1
annotation|@
name|Test
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|se
operator|.
name|compile
argument_list|(
literal|"ctx['value']"
argument_list|)
decl_stmt|;
name|ExecutableScript
name|script
init|=
name|se
operator|.
name|executable
argument_list|(
name|compiledScript
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
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
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
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangingVarsCrossExecution2
annotation|@
name|Test
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Object
name|compiledScript
init|=
name|se
operator|.
name|compile
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|ExecutableScript
name|script
init|=
name|se
operator|.
name|executable
argument_list|(
name|compiledScript
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
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
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
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

