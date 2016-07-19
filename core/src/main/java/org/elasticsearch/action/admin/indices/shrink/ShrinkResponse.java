begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.shrink
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|shrink
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexResponse
import|;
end_import

begin_class
DECL|class|ShrinkResponse
specifier|public
specifier|final
class|class
name|ShrinkResponse
extends|extends
name|CreateIndexResponse
block|{
DECL|method|ShrinkResponse
name|ShrinkResponse
parameter_list|()
block|{     }
DECL|method|ShrinkResponse
name|ShrinkResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|,
name|boolean
name|shardsAcked
parameter_list|)
block|{
name|super
argument_list|(
name|acknowledged
argument_list|,
name|shardsAcked
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

