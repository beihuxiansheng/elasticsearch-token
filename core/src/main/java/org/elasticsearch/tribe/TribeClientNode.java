begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tribe
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
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
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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

begin_comment
comment|/**  * An internal node that connects to a remove cluster, as part of a tribe node.  */
end_comment

begin_class
DECL|class|TribeClientNode
class|class
name|TribeClientNode
extends|extends
name|Node
block|{
DECL|method|TribeClientNode
name|TribeClientNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

