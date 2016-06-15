begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
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
name|Nullable
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
name|ParseField
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
name|ParseFieldMatcherSupplier
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
name|Strings
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
name|bytes
operator|.
name|BytesReference
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|ConstructingObjectParser
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentHelper
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|emptyMap
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
name|common
operator|.
name|xcontent
operator|.
name|ConstructingObjectParser
operator|.
name|constructorArg
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ConstructingObjectParser
operator|.
name|optionalConstructorArg
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
operator|.
name|convertToMap
import|;
end_import

begin_comment
comment|/**  * Information about a persisted or running task. Running tasks just have a {@link #getTask()} while persisted tasks will have either a  * {@link #getError()} or {@link #getResponse()}.  */
end_comment

begin_class
DECL|class|PersistedTaskInfo
specifier|public
specifier|final
class|class
name|PersistedTaskInfo
implements|implements
name|Writeable
implements|,
name|ToXContent
block|{
DECL|field|completed
specifier|private
specifier|final
name|boolean
name|completed
decl_stmt|;
DECL|field|task
specifier|private
specifier|final
name|TaskInfo
name|task
decl_stmt|;
annotation|@
name|Nullable
DECL|field|error
specifier|private
specifier|final
name|BytesReference
name|error
decl_stmt|;
annotation|@
name|Nullable
DECL|field|response
specifier|private
specifier|final
name|BytesReference
name|response
decl_stmt|;
comment|/**      * Construct a {@linkplain PersistedTaskInfo} for a task for which we don't have a result or error. That usually means that the task      * is incomplete, but it could also mean that we waited for the task to complete but it didn't save any error information.      */
DECL|method|PersistedTaskInfo
specifier|public
name|PersistedTaskInfo
parameter_list|(
name|boolean
name|completed
parameter_list|,
name|TaskInfo
name|task
parameter_list|)
block|{
name|this
argument_list|(
name|completed
argument_list|,
name|task
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a {@linkplain PersistedTaskInfo} for a task that completed with an error.      */
DECL|method|PersistedTaskInfo
specifier|public
name|PersistedTaskInfo
parameter_list|(
name|TaskInfo
name|task
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|task
argument_list|,
name|toXContent
argument_list|(
name|error
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a {@linkplain PersistedTaskInfo} for a task that completed successfully.      */
DECL|method|PersistedTaskInfo
specifier|public
name|PersistedTaskInfo
parameter_list|(
name|TaskInfo
name|task
parameter_list|,
name|ToXContent
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|task
argument_list|,
literal|null
argument_list|,
name|toXContent
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|PersistedTaskInfo
specifier|private
name|PersistedTaskInfo
parameter_list|(
name|boolean
name|completed
parameter_list|,
name|TaskInfo
name|task
parameter_list|,
annotation|@
name|Nullable
name|BytesReference
name|error
parameter_list|,
annotation|@
name|Nullable
name|BytesReference
name|result
parameter_list|)
block|{
name|this
operator|.
name|completed
operator|=
name|completed
expr_stmt|;
name|this
operator|.
name|task
operator|=
name|requireNonNull
argument_list|(
name|task
argument_list|,
literal|"task is required"
argument_list|)
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|result
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|PersistedTaskInfo
specifier|public
name|PersistedTaskInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|completed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|task
operator|=
operator|new
name|TaskInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|error
operator|=
name|in
operator|.
name|readOptionalBytesReference
argument_list|()
expr_stmt|;
name|response
operator|=
name|in
operator|.
name|readOptionalBytesReference
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|completed
argument_list|)
expr_stmt|;
name|task
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalBytesReference
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalBytesReference
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the task that this wraps.      */
DECL|method|getTask
specifier|public
name|TaskInfo
name|getTask
parameter_list|()
block|{
return|return
name|task
return|;
block|}
comment|/**      * Get the error that finished this task. Will return null if the task didn't finish with an error, it hasn't yet finished, or didn't      * persist its result.      */
DECL|method|getError
specifier|public
name|BytesReference
name|getError
parameter_list|()
block|{
return|return
name|error
return|;
block|}
comment|/**      * Convert {@link #getError()} from XContent to a Map for easy processing. Will return an empty map if the task didn't finish with an      * error, hasn't yet finished, or didn't persist its result.      */
DECL|method|getErrorAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getErrorAsMap
parameter_list|()
block|{
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
return|return
name|emptyMap
argument_list|()
return|;
block|}
return|return
name|convertToMap
argument_list|(
name|error
argument_list|,
literal|false
argument_list|)
operator|.
name|v2
argument_list|()
return|;
block|}
comment|/**      * Get the response that this task finished with. Will return null if the task was finished by an error, it hasn't yet finished, or      * didn't persist its result.      */
DECL|method|getResponse
specifier|public
name|BytesReference
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
comment|/**      * Convert {@link #getResponse()} from XContent to a Map for easy processing. Will return an empty map if the task was finished with an      * error, hasn't yet finished, or didn't persist its result.      */
DECL|method|getResponseAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getResponseAsMap
parameter_list|()
block|{
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
name|emptyMap
argument_list|()
return|;
block|}
return|return
name|convertToMap
argument_list|(
name|response
argument_list|,
literal|false
argument_list|)
operator|.
name|v2
argument_list|()
return|;
block|}
DECL|method|isCompleted
specifier|public
name|boolean
name|isCompleted
parameter_list|()
block|{
return|return
name|completed
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
name|startObject
argument_list|()
expr_stmt|;
name|innerToXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|method|innerToXContent
specifier|public
name|XContentBuilder
name|innerToXContent
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
literal|"task"
argument_list|,
name|task
argument_list|)
expr_stmt|;
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|XContentHelper
operator|.
name|writeRawField
argument_list|(
literal|"error"
argument_list|,
name|error
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|XContentHelper
operator|.
name|writeRawField
argument_list|(
literal|"response"
argument_list|,
name|response
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|field|PARSER
specifier|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|PersistedTaskInfo
argument_list|,
name|ParseFieldMatcherSupplier
argument_list|>
name|PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"persisted_task_info"
argument_list|,
name|a
lambda|->
operator|new
name|PersistedTaskInfo
argument_list|(
literal|true
argument_list|,
operator|(
name|TaskInfo
operator|)
name|a
index|[
literal|0
index|]
argument_list|,
operator|(
name|BytesReference
operator|)
name|a
index|[
literal|1
index|]
argument_list|,
operator|(
name|BytesReference
operator|)
name|a
index|[
literal|2
index|]
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
name|PARSER
operator|.
name|declareObject
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
name|TaskInfo
operator|.
name|PARSER
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"task"
argument_list|)
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareRawObject
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareRawObject
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Strings
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// Implements equals and hashcode for testing
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|PersistedTaskInfo
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PersistedTaskInfo
name|other
init|=
operator|(
name|PersistedTaskInfo
operator|)
name|obj
decl_stmt|;
comment|/*          * Equality of error and result is done by converting them to a map first. Not efficient but ignores field order and spacing          * differences so perfect for testing.          */
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|completed
argument_list|,
name|other
operator|.
name|completed
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|task
argument_list|,
name|other
operator|.
name|task
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|getErrorAsMap
argument_list|()
argument_list|,
name|other
operator|.
name|getErrorAsMap
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|getResponseAsMap
argument_list|()
argument_list|,
name|other
operator|.
name|getResponseAsMap
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|/*          * Hashing of error and result is done by converting them to a map first. Not efficient but ignores field order and spacing          * differences so perfect for testing.          */
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|completed
argument_list|,
name|task
argument_list|,
name|getErrorAsMap
argument_list|()
argument_list|,
name|getResponseAsMap
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toXContent
specifier|private
specifier|static
name|BytesReference
name|toXContent
parameter_list|(
name|ToXContent
name|result
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|Requests
operator|.
name|INDEX_CONTENT_TYPE
argument_list|)
init|)
block|{
comment|// Elasticsearch's Response object never emit starting or ending objects. Most other implementers of ToXContent do....
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|result
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|bytes
argument_list|()
return|;
block|}
block|}
DECL|method|toXContent
specifier|private
specifier|static
name|BytesReference
name|toXContent
parameter_list|(
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|Requests
operator|.
name|INDEX_CONTENT_TYPE
argument_list|)
init|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|ElasticsearchException
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|bytes
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

