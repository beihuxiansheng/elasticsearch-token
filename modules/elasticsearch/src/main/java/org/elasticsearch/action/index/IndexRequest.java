begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this   * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchGenerationException
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
name|support
operator|.
name|replication
operator|.
name|ShardReplicationOperationRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Required
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|Unicode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
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
name|util
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
name|util
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|BinaryXContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|builder
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
name|Arrays
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|Actions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Index request to index a typed JSON document into a specific index and make it searchable. Best  * created using {@link org.elasticsearch.client.Requests#indexRequest(String)}.  *  *<p>The index requires the {@link #index()}, {@link #type(String)}, {@link #id(String)} and  * {@link #source(byte[])} to be set.  *  *<p>The source (content to index) can be set in its bytes form using ({@link #source(byte[])}),  * its string form ({@link #source(String)}) or using a {@link org.elasticsearch.util.xcontent.builder.XContentBuilder}  * ({@link #source(org.elasticsearch.util.xcontent.builder.XContentBuilder)}).  *  *<p>If the {@link #id(String)} is not set, it will be automatically generated.  *  * @author kimchy (shay.banon)  * @see IndexResponse  * @see org.elasticsearch.client.Requests#indexRequest(String)  * @see org.elasticsearch.client.Client#index(IndexRequest)  */
end_comment

begin_class
DECL|class|IndexRequest
specifier|public
class|class
name|IndexRequest
extends|extends
name|ShardReplicationOperationRequest
block|{
comment|/**      * Operation type controls if the type of the index operation.      */
DECL|enum|OpType
specifier|public
specifier|static
enum|enum
name|OpType
block|{
comment|/**          * Index the source. If there an existing document with the id, it will          * be replaced.          */
DECL|enum constant|INDEX
name|INDEX
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
comment|/**          * Creates the resource. Simply adds it to the index, if there is an existing          * document with the id, then it won't be removed.          */
DECL|enum constant|CREATE
name|CREATE
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|;
DECL|field|id
specifier|private
name|byte
name|id
decl_stmt|;
DECL|method|OpType
name|OpType
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**          * The internal representation of the operation type.          */
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**          * Constructs the operation type from its internal representation.          */
DECL|method|fromId
specifier|public
specifier|static
name|OpType
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|INDEX
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|CREATE
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No type match for ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|source
specifier|private
name|byte
index|[]
name|source
decl_stmt|;
DECL|field|sourceOffset
specifier|private
name|int
name|sourceOffset
decl_stmt|;
DECL|field|sourceLength
specifier|private
name|int
name|sourceLength
decl_stmt|;
DECL|field|sourceUnsafe
specifier|private
name|boolean
name|sourceUnsafe
decl_stmt|;
DECL|field|opType
specifier|private
name|OpType
name|opType
init|=
name|OpType
operator|.
name|INDEX
decl_stmt|;
DECL|method|IndexRequest
specifier|public
name|IndexRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new index request against the specific index. The {@link #type(String)},      * {@link #id(String)} and {@link #source(byte[])} must be set.      */
DECL|method|IndexRequest
specifier|public
name|IndexRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|/**      * Constructs a new index request against the index, type, id and using the source.      *      * @param index The index to index into      * @param type  The type to index into      * @param id    The id of document      */
DECL|method|IndexRequest
specifier|public
name|IndexRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"type is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"source is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * Before we fork on a local thread, make sure we copy over the bytes if they are unsafe      */
DECL|method|beforeLocalFork
annotation|@
name|Override
specifier|protected
name|void
name|beforeLocalFork
parameter_list|()
block|{
name|source
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the index the index operation will happen on.      */
DECL|method|index
annotation|@
name|Override
specifier|public
name|IndexRequest
name|index
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|super
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
comment|/**      * Should the listener be called on a separate thread if needed.      */
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|IndexRequest
name|listenerThreaded
parameter_list|(
name|boolean
name|threadedListener
parameter_list|)
block|{
name|super
operator|.
name|listenerThreaded
argument_list|(
name|threadedListener
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls if the operation will be executed on a separate thread when executed locally. Defaults      * to<tt>true</tt> when running in embedded mode.      */
DECL|method|operationThreaded
annotation|@
name|Override
specifier|public
name|IndexRequest
name|operationThreaded
parameter_list|(
name|boolean
name|threadedOperation
parameter_list|)
block|{
name|super
operator|.
name|operationThreaded
argument_list|(
name|threadedOperation
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The type of the indexed document.      */
DECL|method|type
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Sets the type of the indexed document.      */
DECL|method|type
annotation|@
name|Required
specifier|public
name|IndexRequest
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The id of the indexed document. If not set, will be automatically generated.      */
DECL|method|id
name|String
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Sets the id of the indexed document. If not set, will be automatically generated.      */
DECL|method|id
specifier|public
name|IndexRequest
name|id
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source of the JSON document to index.      */
DECL|method|source
name|byte
index|[]
name|source
parameter_list|()
block|{
if|if
condition|(
name|sourceUnsafe
operator|||
name|sourceOffset
operator|>
literal|0
condition|)
block|{
name|source
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|source
argument_list|,
name|sourceOffset
argument_list|,
name|sourceLength
argument_list|)
expr_stmt|;
name|sourceOffset
operator|=
literal|0
expr_stmt|;
name|sourceUnsafe
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|source
return|;
block|}
comment|/**      * Writes the Map as a JSON.      *      * @param source The map to index      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
parameter_list|(
name|Map
name|source
parameter_list|)
throws|throws
name|ElasticSearchGenerationException
block|{
return|return
name|source
argument_list|(
name|source
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
return|;
block|}
comment|/**      * Writes the Map as the provided content type.      *      * @param source The map to index      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
parameter_list|(
name|Map
name|source
parameter_list|,
name|XContentType
name|contentType
parameter_list|)
throws|throws
name|ElasticSearchGenerationException
block|{
try|try
block|{
name|BinaryXContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBinaryBuilder
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|source
argument_list|(
name|builder
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchGenerationException
argument_list|(
literal|"Failed to generate ["
operator|+
name|source
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sets the document source to index.      *      *<p>Note, its preferable to either set it using {@link #source(org.elasticsearch.util.xcontent.builder.XContentBuilder)}      * or using the {@link #source(byte[])}.      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|UnicodeUtil
operator|.
name|UTF8Result
name|result
init|=
name|Unicode
operator|.
name|fromStringAsUtf8
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|this
operator|.
name|source
operator|=
name|result
operator|.
name|result
expr_stmt|;
name|this
operator|.
name|sourceOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|sourceLength
operator|=
name|result
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|sourceUnsafe
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the content source to index.      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
parameter_list|(
name|XContentBuilder
name|sourceBuilder
parameter_list|)
block|{
try|try
block|{
name|source
operator|=
name|sourceBuilder
operator|.
name|unsafeBytes
argument_list|()
expr_stmt|;
name|sourceOffset
operator|=
literal|0
expr_stmt|;
name|sourceLength
operator|=
name|sourceBuilder
operator|.
name|unsafeBytesLength
argument_list|()
expr_stmt|;
name|sourceUnsafe
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchGenerationException
argument_list|(
literal|"Failed to generate ["
operator|+
name|sourceBuilder
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Sets the document to index in bytes form.      */
DECL|method|source
specifier|public
name|IndexRequest
name|source
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
return|return
name|source
argument_list|(
name|source
argument_list|,
literal|0
argument_list|,
name|source
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**      * Sets the document to index in bytes form (assumed to be safe to be used from different      * threads).      *      * @param source The source to index      * @param offset The offset in the byte array      * @param length The length of the data      * @return      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
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
return|return
name|source
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Sets the document to index in bytes form.      *      * @param source The source to index      * @param offset The offset in the byte array      * @param length The length of the data      * @param unsafe Is the byte array safe to be used form a different thread      * @return      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
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
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sourceOffset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|sourceLength
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|sourceUnsafe
operator|=
name|unsafe
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A timeout to wait if the index operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
DECL|method|timeout
specifier|public
name|IndexRequest
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type of operation to perform.      */
DECL|method|opType
specifier|public
name|IndexRequest
name|opType
parameter_list|(
name|OpType
name|opType
parameter_list|)
block|{
name|this
operator|.
name|opType
operator|=
name|opType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets a string representation of the {@link #opType(org.elasticsearch.action.index.IndexRequest.OpType)}. Can      * be either "index" or "create".      */
DECL|method|opType
specifier|public
name|IndexRequest
name|opType
parameter_list|(
name|String
name|opType
parameter_list|)
throws|throws
name|ElasticSearchIllegalArgumentException
block|{
if|if
condition|(
literal|"create"
operator|.
name|equals
argument_list|(
name|opType
argument_list|)
condition|)
block|{
return|return
name|opType
argument_list|(
name|OpType
operator|.
name|CREATE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|opType
argument_list|)
condition|)
block|{
return|return
name|opType
argument_list|(
name|OpType
operator|.
name|INDEX
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No index opType matching ["
operator|+
name|opType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Set the replication type for this operation.      */
DECL|method|replicationType
annotation|@
name|Override
specifier|public
name|IndexRequest
name|replicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|super
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
comment|/**      * Set to<tt>true</tt> to force this index to use {@link OpType#CREATE}.      */
DECL|method|create
specifier|public
name|IndexRequest
name|create
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
if|if
condition|(
name|create
condition|)
block|{
return|return
name|opType
argument_list|(
name|OpType
operator|.
name|CREATE
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|opType
argument_list|(
name|OpType
operator|.
name|INDEX
argument_list|)
return|;
block|}
block|}
comment|/**      * The type of operation to perform.      */
DECL|method|opType
specifier|public
name|OpType
name|opType
parameter_list|()
block|{
return|return
name|this
operator|.
name|opType
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|type
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|id
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
name|sourceUnsafe
operator|=
literal|false
expr_stmt|;
name|sourceOffset
operator|=
literal|0
expr_stmt|;
name|sourceLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|byte
index|[
name|sourceLength
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|opType
operator|=
name|OpType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|writeUTF
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|sourceLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|source
argument_list|,
name|sourceOffset
argument_list|,
name|sourceLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|opType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|index
operator|+
literal|"]["
operator|+
name|type
operator|+
literal|"]["
operator|+
name|id
operator|+
literal|"], source["
operator|+
name|Unicode
operator|.
name|fromBytes
argument_list|(
name|source
argument_list|,
name|sourceOffset
argument_list|,
name|sourceLength
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

