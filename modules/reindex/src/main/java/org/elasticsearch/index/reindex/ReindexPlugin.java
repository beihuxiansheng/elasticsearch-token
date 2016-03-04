begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionModule
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
name|network
operator|.
name|NetworkModule
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

begin_class
DECL|class|ReindexPlugin
specifier|public
class|class
name|ReindexPlugin
extends|extends
name|Plugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"reindex"
decl_stmt|;
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"The Reindex module adds APIs to reindex from one index to another or update documents in place."
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ActionModule
name|actionModule
parameter_list|)
block|{
name|actionModule
operator|.
name|registerAction
argument_list|(
name|ReindexAction
operator|.
name|INSTANCE
argument_list|,
name|TransportReindexAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|actionModule
operator|.
name|registerAction
argument_list|(
name|UpdateByQueryAction
operator|.
name|INSTANCE
argument_list|,
name|TransportUpdateByQueryAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|NetworkModule
name|restModule
parameter_list|)
block|{
name|restModule
operator|.
name|registerRestHandler
argument_list|(
name|RestReindexAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|restModule
operator|.
name|registerRestHandler
argument_list|(
name|RestUpdateByQueryAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

