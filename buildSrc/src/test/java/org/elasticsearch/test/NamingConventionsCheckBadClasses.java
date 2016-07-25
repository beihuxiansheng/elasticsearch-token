begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * These inner classes all fail the NamingConventionsCheck. They have to live in the tests or else they won't be scanned.  */
end_comment

begin_class
DECL|class|NamingConventionsCheckBadClasses
specifier|public
class|class
name|NamingConventionsCheckBadClasses
block|{
DECL|class|NotImplementingTests
specifier|public
specifier|static
specifier|final
class|class
name|NotImplementingTests
block|{     }
DECL|class|WrongName
specifier|public
specifier|static
specifier|final
class|class
name|WrongName
extends|extends
name|UnitTestCase
block|{
comment|/*          * Dummy test so the tests pass. We do this *and* skip the tests so anyone who jumps back to a branch without these tests can still          * compile without a failure. That is because clean doesn't actually clean these....          */
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
block|{}
block|}
DECL|class|DummyAbstractTests
specifier|public
specifier|abstract
specifier|static
class|class
name|DummyAbstractTests
extends|extends
name|UnitTestCase
block|{     }
DECL|interface|DummyInterfaceTests
specifier|public
interface|interface
name|DummyInterfaceTests
block|{     }
DECL|class|InnerTests
specifier|public
specifier|static
specifier|final
class|class
name|InnerTests
extends|extends
name|UnitTestCase
block|{
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
block|{}
block|}
DECL|class|WrongNameTheSecond
specifier|public
specifier|static
specifier|final
class|class
name|WrongNameTheSecond
extends|extends
name|UnitTestCase
block|{
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
block|{}
block|}
DECL|class|PlainUnit
specifier|public
specifier|static
specifier|final
class|class
name|PlainUnit
extends|extends
name|TestCase
block|{
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
block|{}
block|}
DECL|class|UnitTestCase
specifier|public
specifier|abstract
specifier|static
class|class
name|UnitTestCase
extends|extends
name|TestCase
block|{     }
DECL|class|IntegTestCase
specifier|public
specifier|abstract
specifier|static
class|class
name|IntegTestCase
extends|extends
name|UnitTestCase
block|{     }
block|}
end_class

end_unit

