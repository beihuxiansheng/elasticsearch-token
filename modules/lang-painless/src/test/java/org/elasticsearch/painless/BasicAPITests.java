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
DECL|class|BasicAPITests
specifier|public
class|class
name|BasicAPITests
extends|extends
name|ScriptTestCase
block|{
DECL|method|testListIterator
specifier|public
name|void
name|testListIterator
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"List x = new ArrayList(); x.add(2); x.add(3); x.add(-2); Iterator y = x.iterator(); "
operator|+
literal|"int total = 0; while (y.hasNext()) total += y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|exec
argument_list|(
literal|"List x = new ArrayList(); x.add(\"a\"); x.add(\"b\"); x.add(\"c\"); "
operator|+
literal|"Iterator y = x.iterator(); String total = \"\"; while (y.hasNext()) total += y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(2); x.add(3); x.add(-2); def y = x.iterator(); "
operator|+
literal|"def total = 0; while (y.hasNext()) total += y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetIterator
specifier|public
name|void
name|testSetIterator
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"Set x = new HashSet(); x.add(2); x.add(3); x.add(-2); Iterator y = x.iterator(); "
operator|+
literal|"int total = 0; while (y.hasNext()) total += y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|exec
argument_list|(
literal|"Set x = new HashSet(); x.add(\"a\"); x.add(\"b\"); x.add(\"c\"); "
operator|+
literal|"Iterator y = x.iterator(); String total = \"\"; while (y.hasNext()) total += y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"def x = new HashSet(); x.add(2); x.add(3); x.add(-2); def y = x.iterator(); "
operator|+
literal|"def total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapIterator
specifier|public
name|void
name|testMapIterator
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"Map x = new HashMap(); x.put(2, 2); x.put(3, 3); x.put(-2, -2); Iterator y = x.keySet().iterator(); "
operator|+
literal|"int total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|exec
argument_list|(
literal|"Map x = new HashMap(); x.put(2, 2); x.put(3, 3); x.put(-2, -2); Iterator y = x.values().iterator(); "
operator|+
literal|"int total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test loads and stores with a map */
DECL|method|testMapLoadStore
specifier|public
name|void
name|testMapLoadStore
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new HashMap(); x.abc = 5; return x.abc;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new HashMap(); x['abc'] = 5; return x['abc'];"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test loads and stores with update script equivalent */
DECL|method|testUpdateMapLoadStore
specifier|public
name|void
name|testUpdateMapLoadStore
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|load
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
name|_source
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|load
operator|.
name|put
argument_list|(
literal|"load5"
argument_list|,
literal|"testvalue"
argument_list|)
expr_stmt|;
name|_source
operator|.
name|put
argument_list|(
literal|"load"
argument_list|,
name|load
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"_source"
argument_list|,
name|_source
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"ctx"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testvalue"
argument_list|,
name|exec
argument_list|(
literal|"ctx._source['load'].5 = ctx._source['load'].remove('load5')"
argument_list|,
name|params
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test loads and stores with a list */
DECL|method|testListLoadStore
specifier|public
name|void
name|testListLoadStore
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(3); x.0 = 5; return x.0;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(3); x[0] = 5; return x[0];"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test shortcut for getters with isXXXX */
DECL|method|testListEmpty
specifier|public
name|void
name|testListEmpty
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); return x.empty;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|exec
argument_list|(
literal|"def x = new HashMap(); return x.empty;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test list method invocation */
DECL|method|testListGet
specifier|public
name|void
name|testListGet
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(5); return x.get(0);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(5); def index = 0; return x.get(index);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testListAsArray
specifier|public
name|void
name|testListAsArray
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(5); return x.length"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"def x = new ArrayList(); x.add(5); return x[0]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"List x = new ArrayList(); x.add('Hallo'); return x.length"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefAssignments
specifier|public
name|void
name|testDefAssignments
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|exec
argument_list|(
literal|"int x; def y = 2.0; x = (int)y;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInternalBoxing
specifier|public
name|void
name|testInternalBoxing
parameter_list|()
block|{
name|assertBytecodeExists
argument_list|(
literal|"def x = true"
argument_list|,
literal|"INVOKESTATIC java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = (byte)1"
argument_list|,
literal|"INVOKESTATIC java/lang/Byte.valueOf (B)Ljava/lang/Byte;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = (short)1"
argument_list|,
literal|"INVOKESTATIC java/lang/Short.valueOf (S)Ljava/lang/Short;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = (char)1"
argument_list|,
literal|"INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = 1"
argument_list|,
literal|"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = 1L"
argument_list|,
literal|"INVOKESTATIC java/lang/Long.valueOf (J)Ljava/lang/Long;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = 1F"
argument_list|,
literal|"INVOKESTATIC java/lang/Float.valueOf (F)Ljava/lang/Float;"
argument_list|)
expr_stmt|;
name|assertBytecodeExists
argument_list|(
literal|"def x = 1D"
argument_list|,
literal|"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;"
argument_list|)
expr_stmt|;
block|}
DECL|method|testInterfaceDefaultMethods
specifier|public
name|void
name|testInterfaceDefaultMethods
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"Map map = new HashMap(); return map.getOrDefault(5,1);"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"def map = new HashMap(); return map.getOrDefault(5,1);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInterfacesHaveObject
specifier|public
name|void
name|testInterfacesHaveObject
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"{}"
argument_list|,
name|exec
argument_list|(
literal|"Map map = new HashMap(); return map.toString();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{}"
argument_list|,
name|exec
argument_list|(
literal|"def map = new HashMap(); return map.toString();"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimitivesHaveMethods
specifier|public
name|void
name|testPrimitivesHaveMethods
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return x.intValue();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return x.toString();"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exec
argument_list|(
literal|"int x = 5; return x.compareTo(5);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

