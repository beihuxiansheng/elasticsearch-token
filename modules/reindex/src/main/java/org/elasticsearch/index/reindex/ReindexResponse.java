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
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|BulkByScrollTask
operator|.
name|Status
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
name|List
import|;
end_import

begin_comment
comment|/**  * Response for the ReindexAction.  */
end_comment

begin_class
DECL|class|ReindexResponse
specifier|public
class|class
name|ReindexResponse
extends|extends
name|BulkIndexByScrollResponse
block|{
DECL|method|ReindexResponse
specifier|public
name|ReindexResponse
parameter_list|()
block|{     }
DECL|method|ReindexResponse
specifier|public
name|ReindexResponse
parameter_list|(
name|TimeValue
name|took
parameter_list|,
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
parameter_list|,
name|boolean
name|timedOut
parameter_list|)
block|{
name|super
argument_list|(
name|took
argument_list|,
name|status
argument_list|,
name|indexingFailures
argument_list|,
name|searchFailures
argument_list|,
name|timedOut
argument_list|)
expr_stmt|;
block|}
DECL|method|getCreated
specifier|public
name|long
name|getCreated
parameter_list|()
block|{
return|return
name|getStatus
argument_list|()
operator|.
name|getCreated
argument_list|()
return|;
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
name|getTook
argument_list|()
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"timed_out"
argument_list|,
name|isTimedOut
argument_list|()
argument_list|)
expr_stmt|;
name|getStatus
argument_list|()
operator|.
name|innerXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
literal|true
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
name|getIndexingFailures
argument_list|()
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
name|getSearchFailures
argument_list|()
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
literal|"ReindexResponse["
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
name|getTook
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|getStatus
argument_list|()
operator|.
name|innerToString
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|,
literal|false
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

