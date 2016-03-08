begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|test
package|;
end_package

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
name|ESTestCase
block|{     }
DECL|class|DummyAbstractTests
specifier|public
specifier|static
specifier|abstract
class|class
name|DummyAbstractTests
extends|extends
name|ESTestCase
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
name|ESTestCase
block|{     }
DECL|class|WrongNameTheSecond
specifier|public
specifier|static
specifier|final
class|class
name|WrongNameTheSecond
extends|extends
name|ESTestCase
block|{     }
DECL|class|PlainUnit
specifier|public
specifier|static
specifier|final
class|class
name|PlainUnit
extends|extends
name|TestCase
block|{     }
block|}
end_class

end_unit

