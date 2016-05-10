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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|CallSite
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_class
DECL|class|DynamicCallSiteTests
specifier|public
class|class
name|DynamicCallSiteTests
extends|extends
name|ESTestCase
block|{
comment|/** calls toString() on integers, twice */
DECL|method|testOneType
specifier|public
name|void
name|testOneType
parameter_list|()
throws|throws
name|Throwable
block|{
name|CallSite
name|site
init|=
name|DynamicCallSite
operator|.
name|bootstrap
argument_list|(
name|MethodHandles
operator|.
name|publicLookup
argument_list|()
argument_list|,
literal|"toString"
argument_list|,
name|MethodType
operator|.
name|methodType
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|DynamicCallSite
operator|.
name|METHOD_CALL
argument_list|)
decl_stmt|;
name|MethodHandle
name|handle
init|=
name|site
operator|.
name|dynamicInvoker
argument_list|()
decl_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// invoke with integer, needs lookup
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// invoked with integer again: should be cached
name|assertEquals
argument_list|(
literal|"6"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoTypes
specifier|public
name|void
name|testTwoTypes
parameter_list|()
throws|throws
name|Throwable
block|{
name|CallSite
name|site
init|=
name|DynamicCallSite
operator|.
name|bootstrap
argument_list|(
name|MethodHandles
operator|.
name|publicLookup
argument_list|()
argument_list|,
literal|"toString"
argument_list|,
name|MethodType
operator|.
name|methodType
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|DynamicCallSite
operator|.
name|METHOD_CALL
argument_list|)
decl_stmt|;
name|MethodHandle
name|handle
init|=
name|site
operator|.
name|dynamicInvoker
argument_list|()
decl_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.5"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
literal|1.5f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// both these should be cached
name|assertEquals
argument_list|(
literal|"6"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2.5"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
literal|2.5f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testTooManyTypes
specifier|public
name|void
name|testTooManyTypes
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// if this changes, test must be rewritten
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|DynamicCallSite
operator|.
name|InliningCacheCallSite
operator|.
name|MAX_DEPTH
argument_list|)
expr_stmt|;
name|CallSite
name|site
init|=
name|DynamicCallSite
operator|.
name|bootstrap
argument_list|(
name|MethodHandles
operator|.
name|publicLookup
argument_list|()
argument_list|,
literal|"toString"
argument_list|,
name|MethodType
operator|.
name|methodType
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|DynamicCallSite
operator|.
name|METHOD_CALL
argument_list|)
decl_stmt|;
name|MethodHandle
name|handle
init|=
name|site
operator|.
name|dynamicInvoker
argument_list|()
decl_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.5"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
literal|1.5f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"6"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3.2"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
literal|3.2d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
operator|new
name|String
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|handle
operator|.
name|invoke
argument_list|(
name|Character
operator|.
name|valueOf
argument_list|(
literal|'c'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertDepthEquals
argument_list|(
name|site
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDepthEquals
specifier|static
name|void
name|assertDepthEquals
parameter_list|(
name|CallSite
name|site
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
name|DynamicCallSite
operator|.
name|InliningCacheCallSite
name|dsite
init|=
operator|(
name|DynamicCallSite
operator|.
name|InliningCacheCallSite
operator|)
name|site
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|dsite
operator|.
name|depth
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

