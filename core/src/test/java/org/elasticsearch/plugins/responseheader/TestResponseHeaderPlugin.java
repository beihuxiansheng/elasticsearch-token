begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins.responseheader
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|responseheader
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|ActionPlugin
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_class
DECL|class|TestResponseHeaderPlugin
specifier|public
class|class
name|TestResponseHeaderPlugin
extends|extends
name|Plugin
implements|implements
name|ActionPlugin
block|{
annotation|@
name|Override
DECL|method|getRestHandlers
specifier|public
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RestHandler
argument_list|>
argument_list|>
name|getRestHandlers
parameter_list|()
block|{
return|return
name|singletonList
argument_list|(
name|TestResponseHeaderRestAction
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

