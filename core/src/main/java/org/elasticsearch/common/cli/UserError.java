begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.cli
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|cli
package|;
end_package

begin_comment
comment|/**  * An exception representing a user fixable problem in {@link CliTool} usage.  */
end_comment

begin_class
DECL|class|UserError
specifier|public
class|class
name|UserError
extends|extends
name|Exception
block|{
comment|/** The exist status the cli should use when catching this user error. */
DECL|field|exitStatus
specifier|public
specifier|final
name|CliTool
operator|.
name|ExitStatus
name|exitStatus
decl_stmt|;
comment|/** Constructs a UserError with an exit status and message to show the user. */
DECL|method|UserError
specifier|public
name|UserError
parameter_list|(
name|CliTool
operator|.
name|ExitStatus
name|exitStatus
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitStatus
operator|=
name|exitStatus
expr_stmt|;
block|}
block|}
end_class

end_unit

