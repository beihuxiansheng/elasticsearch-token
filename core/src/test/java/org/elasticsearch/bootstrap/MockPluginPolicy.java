begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedRunner
import|;
end_import

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
name|LuceneTestCase
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
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|CodeSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PermissionCollection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permissions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Policy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|ProtectionDomain
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Simulates in unit tests per-plugin permissions.  * Unit tests for plugins do not have a proper plugin structure,  * so we don't know which codebases to apply the permission to.  *<p>  * As an approximation, we just exclude es/test/framework classes,  * because they will be present in stacks and fail tests for the   * simple case where an AccessController block is missing, because  * java security checks every codebase in the stacktrace, and we  * are sure to pollute it.  */
end_comment

begin_class
DECL|class|MockPluginPolicy
specifier|final
class|class
name|MockPluginPolicy
extends|extends
name|Policy
block|{
DECL|field|standardPolicy
specifier|final
name|ESPolicy
name|standardPolicy
decl_stmt|;
DECL|field|extraPermissions
specifier|final
name|PermissionCollection
name|extraPermissions
decl_stmt|;
DECL|field|excludedSources
specifier|final
name|Set
argument_list|<
name|CodeSource
argument_list|>
name|excludedSources
decl_stmt|;
comment|/**      * Create a new MockPluginPolicy with dynamic {@code permissions} and      * adding the extra plugin permissions from {@code insecurePluginProp} to      * all code except test classes.      */
DECL|method|MockPluginPolicy
name|MockPluginPolicy
parameter_list|(
name|Permissions
name|permissions
parameter_list|,
name|String
name|insecurePluginProp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// the hack begins!
comment|// parse whole policy file, with and without the substitution, compute the delta
name|standardPolicy
operator|=
operator|new
name|ESPolicy
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
name|URL
name|bogus
init|=
operator|new
name|URL
argument_list|(
literal|"file:/bogus"
argument_list|)
decl_stmt|;
comment|// its "any old codebase" this time: generic permissions
name|PermissionCollection
name|smallPermissions
init|=
name|standardPolicy
operator|.
name|template
operator|.
name|getPermissions
argument_list|(
operator|new
name|CodeSource
argument_list|(
name|bogus
argument_list|,
operator|(
name|Certificate
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Permission
argument_list|>
name|small
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
name|smallPermissions
operator|.
name|elements
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// set the URL for the property substitution, this time it will also have special permissions
name|System
operator|.
name|setProperty
argument_list|(
name|insecurePluginProp
argument_list|,
name|bogus
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ESPolicy
name|biggerPolicy
init|=
operator|new
name|ESPolicy
argument_list|(
name|permissions
argument_list|)
decl_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|insecurePluginProp
argument_list|)
expr_stmt|;
name|PermissionCollection
name|bigPermissions
init|=
name|biggerPolicy
operator|.
name|template
operator|.
name|getPermissions
argument_list|(
operator|new
name|CodeSource
argument_list|(
name|bogus
argument_list|,
operator|(
name|Certificate
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Permission
argument_list|>
name|big
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
name|bigPermissions
operator|.
name|elements
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// compute delta to remove all the generic permissions
comment|// we want equals() vs implies() for this check, in case we need
comment|// to pass along any UnresolvedPermission to the plugin
name|big
operator|.
name|removeAll
argument_list|(
name|small
argument_list|)
expr_stmt|;
comment|// build collection of the special permissions for easy checking
name|extraPermissions
operator|=
operator|new
name|Permissions
argument_list|()
expr_stmt|;
for|for
control|(
name|Permission
name|p
range|:
name|big
control|)
block|{
name|extraPermissions
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|excludedSources
operator|=
operator|new
name|HashSet
argument_list|<
name|CodeSource
argument_list|>
argument_list|()
expr_stmt|;
comment|// exclude some obvious places
comment|// es core
name|excludedSources
operator|.
name|add
argument_list|(
name|Bootstrap
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// es test framework
name|excludedSources
operator|.
name|add
argument_list|(
name|getClass
argument_list|()
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// lucene test framework
name|excludedSources
operator|.
name|add
argument_list|(
name|LuceneTestCase
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// test runner
name|excludedSources
operator|.
name|add
argument_list|(
name|RandomizedRunner
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// junit library
name|excludedSources
operator|.
name|add
argument_list|(
name|Assert
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// scripts
name|excludedSources
operator|.
name|add
argument_list|(
operator|new
name|CodeSource
argument_list|(
operator|new
name|URL
argument_list|(
literal|"file:"
operator|+
name|BootstrapInfo
operator|.
name|UNTRUSTED_CODEBASE
argument_list|)
argument_list|,
operator|(
name|Certificate
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
operator|.
name|debug
argument_list|(
literal|"Apply permissions [{}] excluding codebases [{}]"
argument_list|,
name|extraPermissions
argument_list|,
name|excludedSources
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|implies
specifier|public
name|boolean
name|implies
parameter_list|(
name|ProtectionDomain
name|domain
parameter_list|,
name|Permission
name|permission
parameter_list|)
block|{
name|CodeSource
name|codeSource
init|=
name|domain
operator|.
name|getCodeSource
argument_list|()
decl_stmt|;
comment|// codesource can be null when reducing privileges via doPrivileged()
if|if
condition|(
name|codeSource
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|standardPolicy
operator|.
name|implies
argument_list|(
name|domain
argument_list|,
name|permission
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|excludedSources
operator|.
name|contains
argument_list|(
name|codeSource
argument_list|)
operator|==
literal|false
operator|&&
name|codeSource
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"test-classes"
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|extraPermissions
operator|.
name|implies
argument_list|(
name|permission
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

