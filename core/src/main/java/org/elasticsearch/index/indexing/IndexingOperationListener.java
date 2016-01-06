begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.indexing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
operator|.
name|Engine
import|;
end_import

begin_comment
comment|/**  * An indexing listener for indexing, delete, events.  */
end_comment

begin_class
DECL|class|IndexingOperationListener
specifier|public
specifier|abstract
class|class
name|IndexingOperationListener
block|{
comment|/**      * Called before the indexing occurs.      */
DECL|method|preIndex
specifier|public
name|Engine
operator|.
name|Index
name|preIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|operation
parameter_list|)
block|{
return|return
name|operation
return|;
block|}
comment|/**      * Called after the indexing operation occurred.      */
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
block|{      }
comment|/**      * Called after the indexing operation occurred with exception.      */
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{      }
comment|/**      * Called before the delete occurs.      */
DECL|method|preDelete
specifier|public
name|Engine
operator|.
name|Delete
name|preDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
return|return
name|delete
return|;
block|}
comment|/**      * Called after the delete operation occurred.      */
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{      }
comment|/**      * Called after the delete operation occurred with exception.      */
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{      }
block|}
end_class

end_unit

