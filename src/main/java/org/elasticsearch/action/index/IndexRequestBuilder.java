begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
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
name|ActionListener
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
name|support
operator|.
name|BaseRequestBuilder
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
name|client
operator|.
name|Client
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
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * An index document action request builder.  */
end_comment

begin_class
DECL|class|IndexRequestBuilder
specifier|public
class|class
name|IndexRequestBuilder
extends|extends
name|BaseRequestBuilder
argument_list|<
name|IndexRequest
argument_list|,
name|IndexResponse
argument_list|>
block|{
DECL|method|IndexRequestBuilder
specifier|public
name|IndexRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|IndexRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexRequestBuilder
specifier|public
name|IndexRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|,
annotation|@
name|Nullable
name|String
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|IndexRequest
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the index to index the document to.      */
DECL|method|setIndex
specifier|public
name|IndexRequestBuilder
name|setIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|request
operator|.
name|index
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type to index the document to.      */
DECL|method|setType
specifier|public
name|IndexRequestBuilder
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|request
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the id to index the document under. Optional, and if not set, one will be automatically      * generated.      */
DECL|method|setId
specifier|public
name|IndexRequestBuilder
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|request
operator|.
name|id
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls the shard routing of the request. Using this value to hash the shard      * and not the id.      */
DECL|method|setRouting
specifier|public
name|IndexRequestBuilder
name|setRouting
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the parent id of this document. If routing is not set, automatically set it as the      * routing as well.      */
DECL|method|setParent
specifier|public
name|IndexRequestBuilder
name|setParent
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
name|request
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the source.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|BytesReference
name|source
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|,
name|unsafe
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the source.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Index the Map as a JSON.      *      * @param source The map to index      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Index the Map as the provided content type.      *      * @param source The map to index      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|,
name|XContentType
name|contentType
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|,
name|contentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the document source to index.      *<p/>      *<p>Note, its preferable to either set it using {@link #setSource(org.elasticsearch.common.xcontent.XContentBuilder)}      * or using the {@link #setSource(byte[])}.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the content source to index.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|XContentBuilder
name|sourceBuilder
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|sourceBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the document to index in bytes form.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the document to index in bytes form (assumed to be safe to be used from different      * threads).      *      * @param source The source to index      * @param offset The offset in the byte array      * @param length The length of the data      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the document to index in bytes form.      *      * @param source The source to index      * @param offset The offset in the byte array      * @param length The length of the data      * @param unsafe Is the byte array safe to be used form a different thread      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|unsafe
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a simple document with a field and a value.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|String
name|field1
parameter_list|,
name|Object
name|value1
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|field1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a simple document with a field and value pairs.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|String
name|field1
parameter_list|,
name|Object
name|value1
parameter_list|,
name|String
name|field2
parameter_list|,
name|Object
name|value2
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|field1
argument_list|,
name|value1
argument_list|,
name|field2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a simple document with a field and value pairs.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|String
name|field1
parameter_list|,
name|Object
name|value1
parameter_list|,
name|String
name|field2
parameter_list|,
name|Object
name|value2
parameter_list|,
name|String
name|field3
parameter_list|,
name|Object
name|value3
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|field1
argument_list|,
name|value1
argument_list|,
name|field2
argument_list|,
name|value2
argument_list|,
name|field3
argument_list|,
name|value3
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a simple document with a field and value pairs.      */
DECL|method|setSource
specifier|public
name|IndexRequestBuilder
name|setSource
parameter_list|(
name|String
name|field1
parameter_list|,
name|Object
name|value1
parameter_list|,
name|String
name|field2
parameter_list|,
name|Object
name|value2
parameter_list|,
name|String
name|field3
parameter_list|,
name|Object
name|value3
parameter_list|,
name|String
name|field4
parameter_list|,
name|Object
name|value4
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|field1
argument_list|,
name|value1
argument_list|,
name|field2
argument_list|,
name|value2
argument_list|,
name|field3
argument_list|,
name|value3
argument_list|,
name|field4
argument_list|,
name|value4
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The content type that will be used to generate a document from user provided objects (like Map).      */
DECL|method|setContentType
specifier|public
name|IndexRequestBuilder
name|setContentType
parameter_list|(
name|XContentType
name|contentType
parameter_list|)
block|{
name|request
operator|.
name|contentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A timeout to wait if the index operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
DECL|method|setTimeout
specifier|public
name|IndexRequestBuilder
name|setTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A timeout to wait if the index operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
DECL|method|setTimeout
specifier|public
name|IndexRequestBuilder
name|setTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type of operation to perform.      */
DECL|method|setOpType
specifier|public
name|IndexRequestBuilder
name|setOpType
parameter_list|(
name|IndexRequest
operator|.
name|OpType
name|opType
parameter_list|)
block|{
name|request
operator|.
name|opType
argument_list|(
name|opType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets a string representation of the {@link #setOpType(org.elasticsearch.action.index.IndexRequest.OpType)}. Can      * be either "index" or "create".      */
DECL|method|setOpType
specifier|public
name|IndexRequestBuilder
name|setOpType
parameter_list|(
name|String
name|opType
parameter_list|)
block|{
name|request
operator|.
name|opType
argument_list|(
name|opType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set to<tt>true</tt> to force this index to use {@link org.elasticsearch.action.index.IndexRequest.OpType#CREATE}.      */
DECL|method|setCreate
specifier|public
name|IndexRequestBuilder
name|setCreate
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|request
operator|.
name|create
argument_list|(
name|create
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should a refresh be executed post this index operation causing the operation to      * be searchable. Note, heavy indexing should not set this to<tt>true</tt>. Defaults      * to<tt>false</tt>.      */
DECL|method|setRefresh
specifier|public
name|IndexRequestBuilder
name|setRefresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|request
operator|.
name|refresh
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the replication type for this operation.      */
DECL|method|setReplicationType
specifier|public
name|IndexRequestBuilder
name|setReplicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|request
operator|.
name|replicationType
argument_list|(
name|replicationType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the consistency level. Defaults to {@link org.elasticsearch.action.WriteConsistencyLevel#DEFAULT}.      */
DECL|method|setConsistencyLevel
specifier|public
name|IndexRequestBuilder
name|setConsistencyLevel
parameter_list|(
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|)
block|{
name|request
operator|.
name|consistencyLevel
argument_list|(
name|consistencyLevel
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the replication type for this operation.      */
DECL|method|setReplicationType
specifier|public
name|IndexRequestBuilder
name|setReplicationType
parameter_list|(
name|String
name|replicationType
parameter_list|)
block|{
name|request
operator|.
name|replicationType
argument_list|(
name|replicationType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the version, which will cause the index operation to only be performed if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|setVersion
specifier|public
name|IndexRequestBuilder
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|request
operator|.
name|version
argument_list|(
name|version
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the versioning type. Defaults to {@link VersionType#INTERNAL}.      */
DECL|method|setVersionType
specifier|public
name|IndexRequestBuilder
name|setVersionType
parameter_list|(
name|VersionType
name|versionType
parameter_list|)
block|{
name|request
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Causes the index request document to be percolated. The parameter is the percolate query      * to use to reduce the percolated queries that are going to run against this doc. Can be      * set to<tt>*</tt> to indicate that all percolate queries should be run.      */
DECL|method|setPercolate
specifier|public
name|IndexRequestBuilder
name|setPercolate
parameter_list|(
name|String
name|percolate
parameter_list|)
block|{
name|request
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the timestamp either as millis since the epoch, or, in the configured date format.      */
DECL|method|setTimestamp
specifier|public
name|IndexRequestBuilder
name|setTimestamp
parameter_list|(
name|String
name|timestamp
parameter_list|)
block|{
name|request
operator|.
name|timestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// Sets the relative ttl value. It musts be> 0 as it makes little sense otherwise.
DECL|method|setTTL
specifier|public
name|IndexRequestBuilder
name|setTTL
parameter_list|(
name|long
name|ttl
parameter_list|)
block|{
name|request
operator|.
name|ttl
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the listener be called on a separate thread if needed.      */
DECL|method|setListenerThreaded
specifier|public
name|IndexRequestBuilder
name|setListenerThreaded
parameter_list|(
name|boolean
name|listenerThreaded
parameter_list|)
block|{
name|request
operator|.
name|listenerThreaded
argument_list|(
name|listenerThreaded
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls if the operation will be executed on a separate thread when executed locally. Defaults      * to<tt>true</tt> when running in embedded mode.      */
DECL|method|setOperationThreaded
specifier|public
name|IndexRequestBuilder
name|setOperationThreaded
parameter_list|(
name|boolean
name|operationThreaded
parameter_list|)
block|{
name|request
operator|.
name|operationThreaded
argument_list|(
name|operationThreaded
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|index
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

