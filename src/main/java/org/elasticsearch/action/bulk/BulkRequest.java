begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|ActionRequest
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
name|ActionRequestValidationException
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
name|WriteConsistencyLevel
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
name|delete
operator|.
name|DeleteRequest
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
name|index
operator|.
name|IndexRequest
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
name|replication
operator|.
name|ReplicationType
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
name|update
operator|.
name|UpdateRequest
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
name|bytes
operator|.
name|BytesArray
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
name|XContent
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
name|XContentParser
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
name|VersionType
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
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  * A bulk request holds an ordered {@link IndexRequest}s and {@link DeleteRequest}s and allows to executes  * it in a single batch.  *  * @see org.elasticsearch.client.Client#bulk(BulkRequest)  */
end_comment

begin_class
DECL|class|BulkRequest
specifier|public
class|class
name|BulkRequest
extends|extends
name|ActionRequest
argument_list|<
name|BulkRequest
argument_list|>
block|{
DECL|field|REQUEST_OVERHEAD
specifier|private
specifier|static
specifier|final
name|int
name|REQUEST_OVERHEAD
init|=
literal|50
decl_stmt|;
DECL|field|requests
specifier|final
name|List
argument_list|<
name|ActionRequest
argument_list|>
name|requests
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|payloads
name|List
argument_list|<
name|Object
argument_list|>
name|payloads
init|=
literal|null
decl_stmt|;
DECL|field|replicationType
specifier|private
name|ReplicationType
name|replicationType
init|=
name|ReplicationType
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|consistencyLevel
specifier|private
name|WriteConsistencyLevel
name|consistencyLevel
init|=
name|WriteConsistencyLevel
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
DECL|field|sizeInBytes
specifier|private
name|long
name|sizeInBytes
init|=
literal|0
decl_stmt|;
comment|/**      * Adds a list of requests to be executed. Either index or delete requests.      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|ActionRequest
modifier|...
name|requests
parameter_list|)
block|{
for|for
control|(
name|ActionRequest
name|request
range|:
name|requests
control|)
block|{
name|add
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|ActionRequest
name|request
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|ActionRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
block|{
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|add
argument_list|(
operator|(
name|IndexRequest
operator|)
name|request
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|add
argument_list|(
operator|(
name|DeleteRequest
operator|)
name|request
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|UpdateRequest
condition|)
block|{
name|add
argument_list|(
operator|(
name|UpdateRequest
operator|)
name|request
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No support for request ["
operator|+
name|request
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Adds a list of requests to be executed. Either index or delete requests.      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|Iterable
argument_list|<
name|ActionRequest
argument_list|>
name|requests
parameter_list|)
block|{
for|for
control|(
name|ActionRequest
name|request
range|:
name|requests
control|)
block|{
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|add
argument_list|(
operator|(
name|IndexRequest
operator|)
name|request
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|add
argument_list|(
operator|(
name|DeleteRequest
operator|)
name|request
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No support for request ["
operator|+
name|request
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link IndexRequest} to the list of actions to execute. Follows the same behavior of {@link IndexRequest}      * (for example, if no id is provided, one will be generated, or usage of the create flag).      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|IndexRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
return|return
name|internalAdd
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|IndexRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
block|{
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
return|return
name|internalAdd
argument_list|(
name|request
argument_list|,
name|payload
argument_list|)
return|;
block|}
DECL|method|internalAdd
name|BulkRequest
name|internalAdd
parameter_list|(
name|IndexRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|addPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|sizeInBytes
operator|+=
name|request
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
operator|+
name|REQUEST_OVERHEAD
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link UpdateRequest} to the list of actions to execute.      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|UpdateRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
return|return
name|internalAdd
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|UpdateRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
block|{
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
return|return
name|internalAdd
argument_list|(
name|request
argument_list|,
name|payload
argument_list|)
return|;
block|}
DECL|method|internalAdd
name|BulkRequest
name|internalAdd
parameter_list|(
name|UpdateRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|addPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|doc
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|+=
name|request
operator|.
name|doc
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|upsertRequest
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|+=
name|request
operator|.
name|upsertRequest
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|script
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|+=
name|request
operator|.
name|script
argument_list|()
operator|.
name|length
argument_list|()
operator|*
literal|2
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Adds an {@link DeleteRequest} to the list of actions to execute.      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|DeleteRequest
name|request
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|DeleteRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|addPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|sizeInBytes
operator|+=
name|REQUEST_OVERHEAD
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addPayload
specifier|private
name|void
name|addPayload
parameter_list|(
name|Object
name|payload
parameter_list|)
block|{
if|if
condition|(
name|payloads
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|payloads
operator|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|requests
operator|.
name|size
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
comment|// add requests#size-1 elements to the payloads if it null (we add for an *existing* request)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|payloads
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|payloads
operator|.
name|add
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
comment|/**      * The list of requests in this bulk request.      */
DECL|method|requests
specifier|public
name|List
argument_list|<
name|ActionRequest
argument_list|>
name|requests
parameter_list|()
block|{
return|return
name|this
operator|.
name|requests
return|;
block|}
comment|/**      * The list of optional payloads associated with requests in the same order as the requests. Note, elements within      * it might be null if no payload has been provided.      *<p/>      * Note, if no payloads have been provided, this method will return null (as to conserve memory overhead).      */
annotation|@
name|Nullable
DECL|method|payloads
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|payloads
parameter_list|()
block|{
return|return
name|this
operator|.
name|payloads
return|;
block|}
comment|/**      * The number of actions in the bulk request.      */
DECL|method|numberOfActions
specifier|public
name|int
name|numberOfActions
parameter_list|()
block|{
return|return
name|requests
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * The estimated size in bytes of the bulk request.      */
DECL|method|estimatedSizeInBytes
specifier|public
name|long
name|estimatedSizeInBytes
parameter_list|()
block|{
return|return
name|sizeInBytes
return|;
block|}
comment|/**      * Adds a framed data in binary format      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|add
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|length
argument_list|,
name|contentUnsafe
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Adds a framed data in binary format      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultIndex
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultType
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|add
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|length
argument_list|)
argument_list|,
name|contentUnsafe
argument_list|,
name|defaultIndex
argument_list|,
name|defaultType
argument_list|)
return|;
block|}
comment|/**      * Adds a framed data in binary format      */
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|BytesReference
name|data
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultIndex
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultType
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|add
argument_list|(
name|data
argument_list|,
name|contentUnsafe
argument_list|,
name|defaultIndex
argument_list|,
name|defaultType
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|BulkRequest
name|add
parameter_list|(
name|BytesReference
name|data
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultIndex
parameter_list|,
annotation|@
name|Nullable
name|String
name|defaultType
parameter_list|,
annotation|@
name|Nullable
name|Object
name|payload
parameter_list|)
throws|throws
name|Exception
block|{
name|XContent
name|xContent
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|int
name|from
init|=
literal|0
decl_stmt|;
name|int
name|length
init|=
name|data
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
name|marker
init|=
name|xContent
operator|.
name|streamSeparator
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|nextMarker
init|=
name|findNextMarker
argument_list|(
name|marker
argument_list|,
name|from
argument_list|,
name|data
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextMarker
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
comment|// now parse the action
name|XContentParser
name|parser
init|=
name|xContent
operator|.
name|createParser
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
comment|// move pointers
name|from
operator|=
name|nextMarker
operator|+
literal|1
expr_stmt|;
comment|// Move to START_OBJECT
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
comment|// Move to FIELD_NAME, that's the action
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
assert|;
name|String
name|action
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|String
name|index
init|=
name|defaultIndex
decl_stmt|;
name|String
name|type
init|=
name|defaultType
decl_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
name|String
name|routing
init|=
literal|null
decl_stmt|;
name|String
name|parent
init|=
literal|null
decl_stmt|;
name|String
name|timestamp
init|=
literal|null
decl_stmt|;
name|Long
name|ttl
init|=
literal|null
decl_stmt|;
name|String
name|opType
init|=
literal|null
decl_stmt|;
name|long
name|version
init|=
literal|0
decl_stmt|;
name|VersionType
name|versionType
init|=
name|VersionType
operator|.
name|INTERNAL
decl_stmt|;
name|String
name|percolate
init|=
literal|null
decl_stmt|;
name|int
name|retryOnConflict
init|=
literal|0
decl_stmt|;
comment|// at this stage, next token can either be END_OBJECT (and use default index and type, with auto generated id)
comment|// or START_OBJECT which will have another set of parameters
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"_index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|index
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|type
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|id
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_routing"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"routing"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|routing
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_parent"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"parent"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|parent
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_timestamp"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"timestamp"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|timestamp
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_ttl"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"ttl"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|ttl
operator|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|.
name|millis
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ttl
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"op_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"opType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|opType
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_version"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"version"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_version_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_versionType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"version_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"versionType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|versionType
operator|=
name|VersionType
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"percolate"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_percolate"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|percolate
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_retry_on_conflict"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_retryOnConflict"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|retryOnConflict
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
literal|"delete"
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|add
argument_list|(
operator|new
name|DeleteRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextMarker
operator|=
name|findNextMarker
argument_list|(
name|marker
argument_list|,
name|from
argument_list|,
name|data
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextMarker
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
comment|// order is important, we set parent after routing, so routing will be set to parent if not set explicitly
comment|// we use internalAdd so we don't fork here, this allows us not to copy over the big byte array to small chunks
comment|// of index request. All index requests are still unsafe if applicable.
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
if|if
condition|(
name|opType
operator|==
literal|null
condition|)
block|{
name|internalAdd
argument_list|(
operator|new
name|IndexRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|timestamp
argument_list|(
name|timestamp
argument_list|)
operator|.
name|ttl
argument_list|(
name|ttl
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
operator|.
name|source
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|,
name|contentUnsafe
argument_list|)
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|internalAdd
argument_list|(
operator|new
name|IndexRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|timestamp
argument_list|(
name|timestamp
argument_list|)
operator|.
name|ttl
argument_list|(
name|ttl
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
operator|.
name|create
argument_list|(
literal|"create"
operator|.
name|equals
argument_list|(
name|opType
argument_list|)
argument_list|)
operator|.
name|source
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|,
name|contentUnsafe
argument_list|)
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"create"
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|internalAdd
argument_list|(
operator|new
name|IndexRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|timestamp
argument_list|(
name|timestamp
argument_list|)
operator|.
name|ttl
argument_list|(
name|ttl
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
operator|.
name|create
argument_list|(
literal|true
argument_list|)
operator|.
name|source
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|,
name|contentUnsafe
argument_list|)
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"update"
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|internalAdd
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|retryOnConflict
argument_list|(
name|retryOnConflict
argument_list|)
operator|.
name|source
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|)
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
comment|// move pointers
name|from
operator|=
name|nextMarker
operator|+
literal|1
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
comment|/**      * Sets the consistency level of write. Defaults to {@link org.elasticsearch.action.WriteConsistencyLevel#DEFAULT}      */
DECL|method|consistencyLevel
specifier|public
name|BulkRequest
name|consistencyLevel
parameter_list|(
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|)
block|{
name|this
operator|.
name|consistencyLevel
operator|=
name|consistencyLevel
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|consistencyLevel
specifier|public
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|()
block|{
return|return
name|this
operator|.
name|consistencyLevel
return|;
block|}
comment|/**      * Should a refresh be executed post this bulk operation causing the operations to      * be searchable. Note, heavy indexing should not set this to<tt>true</tt>. Defaults      * to<tt>false</tt>.      */
DECL|method|refresh
specifier|public
name|BulkRequest
name|refresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|refresh
specifier|public
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|this
operator|.
name|refresh
return|;
block|}
comment|/**      * Set the replication type for this operation.      */
DECL|method|replicationType
specifier|public
name|BulkRequest
name|replicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|this
operator|.
name|replicationType
operator|=
name|replicationType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|replicationType
specifier|public
name|ReplicationType
name|replicationType
parameter_list|()
block|{
return|return
name|this
operator|.
name|replicationType
return|;
block|}
DECL|method|findNextMarker
specifier|private
name|int
name|findNextMarker
parameter_list|(
name|byte
name|marker
parameter_list|,
name|int
name|from
parameter_list|,
name|BytesReference
name|data
parameter_list|,
name|int
name|length
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|from
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
name|marker
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"no requests added"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ActionRequestValidationException
name|ex
init|=
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|validationException
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
operator|new
name|ActionRequestValidationException
argument_list|()
expr_stmt|;
block|}
name|validationException
operator|.
name|addValidationErrors
argument_list|(
name|ex
operator|.
name|validationErrors
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|validationException
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
name|replicationType
operator|=
name|ReplicationType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|consistencyLevel
operator|=
name|WriteConsistencyLevel
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
name|IndexRequest
name|request
init|=
operator|new
name|IndexRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
name|DeleteRequest
name|request
init|=
operator|new
name|DeleteRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|2
condition|)
block|{
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
name|refresh
operator|=
name|in
operator|.
name|readBoolean
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
name|writeByte
argument_list|(
name|replicationType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|consistencyLevel
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|requests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ActionRequest
name|request
range|:
name|requests
control|)
block|{
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|UpdateRequest
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

