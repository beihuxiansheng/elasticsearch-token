begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ResourceNotFoundException
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AliasesNotFoundException
specifier|public
class|class
name|AliasesNotFoundException
extends|extends
name|ResourceNotFoundException
block|{
DECL|method|AliasesNotFoundException
specifier|public
name|AliasesNotFoundException
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
name|super
argument_list|(
literal|"aliases "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|names
argument_list|)
operator|+
literal|" missing"
argument_list|)
expr_stmt|;
name|this
operator|.
name|setResources
argument_list|(
literal|"aliases"
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
DECL|method|AliasesNotFoundException
specifier|public
name|AliasesNotFoundException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

