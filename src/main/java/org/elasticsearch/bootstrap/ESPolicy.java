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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|URIParameter
import|;
end_import

begin_comment
comment|/** custom policy for union of static and dynamic permissions */
end_comment

begin_class
DECL|class|ESPolicy
specifier|final
class|class
name|ESPolicy
extends|extends
name|Policy
block|{
comment|/** template policy file, the one used in tests */
DECL|field|POLICY_RESOURCE
specifier|static
specifier|final
name|String
name|POLICY_RESOURCE
init|=
literal|"security.policy"
decl_stmt|;
DECL|field|template
specifier|final
name|Policy
name|template
decl_stmt|;
DECL|field|dynamic
specifier|final
name|PermissionCollection
name|dynamic
decl_stmt|;
DECL|method|ESPolicy
specifier|public
name|ESPolicy
parameter_list|(
name|PermissionCollection
name|dynamic
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|POLICY_RESOURCE
argument_list|)
operator|.
name|toURI
argument_list|()
decl_stmt|;
name|this
operator|.
name|template
operator|=
name|Policy
operator|.
name|getInstance
argument_list|(
literal|"JavaPolicy"
argument_list|,
operator|new
name|URIParameter
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|dynamic
operator|=
name|dynamic
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"fast equals check is desired"
argument_list|)
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
comment|// run groovy scripts with no permissions
if|if
condition|(
literal|"/groovy/script"
operator|.
name|equals
argument_list|(
name|domain
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|template
operator|.
name|implies
argument_list|(
name|domain
argument_list|,
name|permission
argument_list|)
operator|||
name|dynamic
operator|.
name|implies
argument_list|(
name|permission
argument_list|)
return|;
block|}
block|}
end_class

end_unit

