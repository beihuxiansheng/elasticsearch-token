begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
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
name|ActionResponse
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
name|bulk
operator|.
name|BulkItemResponse
operator|.
name|Failure
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
name|ShardSearchFailure
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|ArrayList
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
name|lang
operator|.
name|Math
operator|.
name|min
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
name|unmodifiableList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|ShardSearchFailure
operator|.
name|readShardSearchFailure
import|;
end_import

begin_comment
comment|/**  * Response used for actions that index many documents using a scroll request.  */
end_comment

begin_class
DECL|class|BulkIndexByScrollResponse
specifier|public
class|class
name|BulkIndexByScrollResponse
extends|extends
name|ActionResponse
implements|implements
name|ToXContent
block|{
DECL|field|took
specifier|private
name|TimeValue
name|took
decl_stmt|;
DECL|field|status
specifier|private
name|BulkByScrollTask
operator|.
name|Status
name|status
decl_stmt|;
DECL|field|indexingFailures
specifier|private
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
decl_stmt|;
DECL|field|searchFailures
specifier|private
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
decl_stmt|;
DECL|method|BulkIndexByScrollResponse
specifier|public
name|BulkIndexByScrollResponse
parameter_list|()
block|{     }
DECL|method|BulkIndexByScrollResponse
specifier|public
name|BulkIndexByScrollResponse
parameter_list|(
name|TimeValue
name|took
parameter_list|,
name|BulkByScrollTask
operator|.
name|Status
name|status
parameter_list|,
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
parameter_list|,
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
parameter_list|)
block|{
name|this
operator|.
name|took
operator|=
name|took
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|requireNonNull
argument_list|(
name|status
argument_list|,
literal|"Null status not supported"
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexingFailures
operator|=
name|indexingFailures
expr_stmt|;
name|this
operator|.
name|searchFailures
operator|=
name|searchFailures
expr_stmt|;
block|}
DECL|method|getTook
specifier|public
name|TimeValue
name|getTook
parameter_list|()
block|{
return|return
name|took
return|;
block|}
DECL|method|getStatus
specifier|protected
name|BulkByScrollTask
operator|.
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getUpdated
specifier|public
name|long
name|getUpdated
parameter_list|()
block|{
return|return
name|status
operator|.
name|getUpdated
argument_list|()
return|;
block|}
DECL|method|getBatches
specifier|public
name|int
name|getBatches
parameter_list|()
block|{
return|return
name|status
operator|.
name|getBatches
argument_list|()
return|;
block|}
DECL|method|getVersionConflicts
specifier|public
name|long
name|getVersionConflicts
parameter_list|()
block|{
return|return
name|status
operator|.
name|getVersionConflicts
argument_list|()
return|;
block|}
DECL|method|getNoops
specifier|public
name|long
name|getNoops
parameter_list|()
block|{
return|return
name|status
operator|.
name|getNoops
argument_list|()
return|;
block|}
comment|/**      * The reason that the request was canceled or null if it hasn't been.      */
DECL|method|getReasonCancelled
specifier|public
name|String
name|getReasonCancelled
parameter_list|()
block|{
return|return
name|status
operator|.
name|getReasonCancelled
argument_list|()
return|;
block|}
comment|/**      * All of the indexing failures. Version conflicts are only included if the request sets abortOnVersionConflict to true (the      * default).      */
DECL|method|getIndexingFailures
specifier|public
name|List
argument_list|<
name|Failure
argument_list|>
name|getIndexingFailures
parameter_list|()
block|{
return|return
name|indexingFailures
return|;
block|}
comment|/**      * All search failures.      */
DECL|method|getSearchFailures
specifier|public
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|getSearchFailures
parameter_list|()
block|{
return|return
name|searchFailures
return|;
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
name|took
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|status
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|indexingFailures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Failure
name|failure
range|:
name|indexingFailures
control|)
block|{
name|failure
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|searchFailures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardSearchFailure
name|failure
range|:
name|searchFailures
control|)
block|{
name|failure
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
name|took
operator|=
name|TimeValue
operator|.
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|status
operator|=
operator|new
name|BulkByScrollTask
operator|.
name|Status
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|indexingFailuresCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|indexingFailuresCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexingFailuresCount
condition|;
name|i
operator|++
control|)
block|{
name|indexingFailures
operator|.
name|add
argument_list|(
name|Failure
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indexingFailures
operator|=
name|unmodifiableList
argument_list|(
name|indexingFailures
argument_list|)
expr_stmt|;
name|int
name|searchFailuresCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|searchFailuresCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchFailuresCount
condition|;
name|i
operator|++
control|)
block|{
name|searchFailures
operator|.
name|add
argument_list|(
name|readShardSearchFailure
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|searchFailures
operator|=
name|unmodifiableList
argument_list|(
name|searchFailures
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"took"
argument_list|,
name|took
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|innerXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"failures"
argument_list|)
expr_stmt|;
for|for
control|(
name|Failure
name|failure
range|:
name|indexingFailures
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|failure
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ShardSearchFailure
name|failure
range|:
name|searchFailures
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|failure
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
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
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"BulkIndexByScrollResponse["
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"took="
argument_list|)
operator|.
name|append
argument_list|(
name|took
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|status
operator|.
name|innerToString
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|",indexing_failures="
argument_list|)
operator|.
name|append
argument_list|(
name|getIndexingFailures
argument_list|()
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|min
argument_list|(
literal|3
argument_list|,
name|getIndexingFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|",search_failures="
argument_list|)
operator|.
name|append
argument_list|(
name|getSearchFailures
argument_list|()
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|min
argument_list|(
literal|3
argument_list|,
name|getSearchFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

