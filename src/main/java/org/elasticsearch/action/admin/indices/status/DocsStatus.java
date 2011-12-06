begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.status
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
name|status
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DocsStatus
specifier|public
class|class
name|DocsStatus
block|{
DECL|field|numDocs
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
DECL|field|maxDoc
name|int
name|maxDoc
init|=
literal|0
decl_stmt|;
DECL|field|deletedDocs
name|int
name|deletedDocs
init|=
literal|0
decl_stmt|;
comment|/**      * The number of docs.      */
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
comment|/**      * The number of docs.      */
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
argument_list|()
return|;
block|}
comment|/**      * The max doc.      */
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
comment|/**      * The max doc.      */
DECL|method|getMaxDoc
specifier|public
name|int
name|getMaxDoc
parameter_list|()
block|{
return|return
name|maxDoc
argument_list|()
return|;
block|}
comment|/**      * The number of deleted docs in the index.      */
DECL|method|deletedDocs
specifier|public
name|int
name|deletedDocs
parameter_list|()
block|{
return|return
name|deletedDocs
return|;
block|}
comment|/**      * The number of deleted docs in the index.      */
DECL|method|getDeletedDocs
specifier|public
name|int
name|getDeletedDocs
parameter_list|()
block|{
return|return
name|deletedDocs
argument_list|()
return|;
block|}
block|}
end_class

end_unit

