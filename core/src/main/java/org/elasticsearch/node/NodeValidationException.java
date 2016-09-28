begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|node
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
name|settings
operator|.
name|Settings
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
name|transport
operator|.
name|BoundTransportAddress
import|;
end_import

begin_comment
comment|/**  * An exception thrown during node validation. Node validation runs immediately before a node  * begins accepting network requests in  * {@link Node#validateNodeBeforeAcceptingRequests(Settings, BoundTransportAddress)}. This  * exception is a checked exception that is declared as thrown from this method for the purpose  * of bubbling up to the user.  */
end_comment

begin_class
DECL|class|NodeValidationException
specifier|public
class|class
name|NodeValidationException
extends|extends
name|Exception
block|{
comment|/**      * Creates a node validation exception with the specified validation message to be displayed to      * the user.      *      * @param message the message to display to the user      */
DECL|method|NodeValidationException
specifier|public
name|NodeValidationException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

