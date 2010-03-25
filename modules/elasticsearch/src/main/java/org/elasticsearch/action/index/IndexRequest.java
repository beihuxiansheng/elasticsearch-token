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
name|FastByteArrayOutputStream
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
name|json
operator|.
name|JsonBuilder
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|Jackson
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Index request to index a typed JSON document into a specific index and make it searchable. Best  * created using {@link org.elasticsearch.client.Requests#indexRequest(String)}.  *  *<p>The index requires the {@link #index()}, {@link #type(String)}, {@link #id(String)} and  * {@link #source(byte[])} to be set.  *  *<p>The source (JSON to index) can be set in its bytes form using ({@link #source(byte[])}),  * its string form ({@link #source(String)}) or using a {@link org.elasticsearch.util.json.JsonBuilder}  * ({@link #source(org.elasticsearch.util.json.JsonBuilder)}).  *  *<p>If the {@link #id(String)} is not set, it will be automatically generated.  *  * @author kimchy (shay.banon)  * @see IndexResponse  * @see org.elasticsearch.client.Requests#indexRequest(String)  * @see org.elasticsearch.client.Client#index(IndexRequest)  */
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
DECL|field|opType
specifier|private
name|OpType
name|opType
init|=
name|OpType
operator|.
name|INDEX
decl_stmt|;
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
comment|/**      * Constructs a new index request against the index, type, id and using the source.      *      * @param index  The index to index into      * @param type   The type to index into      * @param id     The id of document      * @param source The JSON source document      */
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
parameter_list|,
name|byte
index|[]
name|source
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
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
DECL|method|IndexRequest
name|IndexRequest
parameter_list|()
block|{     }
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
return|return
name|source
return|;
block|}
comment|/**      * Writes the JSON as a {@link Map}.      *      * @param source The map to index      */
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
name|FastByteArrayOutputStream
name|os
init|=
name|FastByteArrayOutputStream
operator|.
name|Cached
operator|.
name|cached
argument_list|()
decl_stmt|;
try|try
block|{
name|defaultObjectMapper
argument_list|()
operator|.
name|writeValue
argument_list|(
name|os
argument_list|,
name|source
argument_list|)
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
name|source
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|os
operator|.
name|copiedByteArray
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the JSON source to index.      *      *<p>Note, its preferable to either set it using {@link #source(org.elasticsearch.util.json.JsonBuilder)}      * or using the {@link #source(byte[])}.      */
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
name|this
operator|.
name|source
operator|=
name|Unicode
operator|.
name|fromStringAsBytes
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the JSON source to index.      */
DECL|method|source
annotation|@
name|Required
specifier|public
name|IndexRequest
name|source
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|)
block|{
try|try
block|{
return|return
name|source
argument_list|(
name|jsonBuilder
operator|.
name|copiedBytes
argument_list|()
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
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Failed to build json for index request"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sets the JSON source to index.      */
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
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
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
name|source
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|readVInt
argument_list|()
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
name|source
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|source
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
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

