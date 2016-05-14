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
literal|3
argument_list|,
name|exec
argument_list|(
literal|"List<Object> x = new ArrayList(); x.add(2); x.add(3); x.add(-2); Iterator<Object> y = x.iterator(); "
operator|+
literal|"int total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|exec
argument_list|(
literal|"List<String> x = new ArrayList(); x.add(\"a\"); x.add(\"b\"); x.add(\"c\"); "
operator|+
literal|"Iterator<String> y = x.iterator(); String total = \"\"; while (y.hasNext()) total += y.next(); return total;"
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
literal|3
argument_list|,
name|exec
argument_list|(
literal|"Set<Object> x = new HashSet(); x.add(2); x.add(3); x.add(-2); Iterator<Object> y = x.iterator(); "
operator|+
literal|"int total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|exec
argument_list|(
literal|"Set<String> x = new HashSet(); x.add(\"a\"); x.add(\"b\"); x.add(\"c\"); "
operator|+
literal|"Iterator<String> y = x.iterator(); String total = \"\"; while (y.hasNext()) total += y.next(); return total;"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"List<String> x = new ArrayList<String>(); x.add('Hallo'); return x.length"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exec
argument_list|(
literal|"List<Object> x = new ArrayList<Object>(); x.add('Hallo'); return x.length"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

