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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestSecurityManager
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|systemPropertyAsBoolean
import|;
end_import

begin_comment
comment|/**   * Installs test security manager (ensures it happens regardless of which  * test case happens to be first, test ordering, etc).   *<p>  * Note that this is BS, this should be done by the jvm (by passing -Djava.security.manager).  * turning it on/off needs to be the role of maven, not this stuff.  */
end_comment

begin_class
DECL|class|SecurityHack
class|class
name|SecurityHack
block|{
static|static
block|{
comment|// for IDEs, we check that security.policy is set
if|if
condition|(
name|systemPropertyAsBoolean
argument_list|(
literal|"tests.security.manager"
argument_list|,
literal|true
argument_list|)
operator|&&
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.policy"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setSecurityManager
argument_list|(
operator|new
name|TestSecurityManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// does nothing, just easy way to make sure the class is loaded.
DECL|method|ensureInitialized
specifier|static
name|void
name|ensureInitialized
parameter_list|()
block|{}
block|}
end_class

end_unit

