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
name|IndicesRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
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
name|StreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskId
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

begin_comment
comment|/**  * Request to update some documents. That means you can't change their type, id, index, or anything like that. This implements  * CompositeIndicesRequest but in a misleading way. Rather than returning all the subrequests that it will make it tries to return a  * representative set of subrequests. This is best-effort but better than {@linkplain ReindexRequest} because scripts can't change the  * destination index and things.  */
end_comment

begin_class
DECL|class|UpdateByQueryRequest
specifier|public
class|class
name|UpdateByQueryRequest
extends|extends
name|AbstractBulkIndexByScrollRequest
argument_list|<
name|UpdateByQueryRequest
argument_list|>
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
comment|/**      * Ingest pipeline to set on index requests made by this action.      */
DECL|field|pipeline
specifier|private
name|String
name|pipeline
decl_stmt|;
DECL|method|UpdateByQueryRequest
specifier|public
name|UpdateByQueryRequest
parameter_list|()
block|{     }
DECL|method|UpdateByQueryRequest
specifier|public
name|UpdateByQueryRequest
parameter_list|(
name|SearchRequest
name|search
parameter_list|)
block|{
name|this
argument_list|(
name|search
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|UpdateByQueryRequest
specifier|private
name|UpdateByQueryRequest
parameter_list|(
name|SearchRequest
name|search
parameter_list|,
name|boolean
name|setDefaults
parameter_list|)
block|{
name|super
argument_list|(
name|search
argument_list|,
name|setDefaults
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the ingest pipeline to set on index requests made by this action.      */
DECL|method|setPipeline
specifier|public
name|void
name|setPipeline
parameter_list|(
name|String
name|pipeline
parameter_list|)
block|{
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
block|}
comment|/**      * Ingest pipeline to set on index requests made by this action.      */
DECL|method|getPipeline
specifier|public
name|String
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
annotation|@
name|Override
DECL|method|self
specifier|protected
name|UpdateByQueryRequest
name|self
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|forSlice
name|UpdateByQueryRequest
name|forSlice
parameter_list|(
name|TaskId
name|slicingTask
parameter_list|,
name|SearchRequest
name|slice
parameter_list|)
block|{
name|UpdateByQueryRequest
name|request
init|=
name|doForSlice
argument_list|(
operator|new
name|UpdateByQueryRequest
argument_list|(
name|slice
argument_list|,
literal|false
argument_list|)
argument_list|,
name|slicingTask
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"update-by-query "
argument_list|)
expr_stmt|;
name|searchToString
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//update by query updates all documents that match a query. The indices and indices options that affect how
comment|//indices are resolved depend entirely on the inner search request. That's why the following methods delegate to it.
annotation|@
name|Override
DECL|method|indices
specifier|public
name|IndicesRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
assert|assert
name|getSearchRequest
argument_list|()
operator|!=
literal|null
assert|;
name|getSearchRequest
argument_list|()
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
assert|assert
name|getSearchRequest
argument_list|()
operator|!=
literal|null
assert|;
return|return
name|getSearchRequest
argument_list|()
operator|.
name|indices
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
assert|assert
name|getSearchRequest
argument_list|()
operator|!=
literal|null
assert|;
return|return
name|getSearchRequest
argument_list|()
operator|.
name|indicesOptions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|pipeline
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

