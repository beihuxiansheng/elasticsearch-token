begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.update
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|update
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
name|ActiveShardCount
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
name|WriteRequestBuilder
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
name|ReplicationRequest
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
name|single
operator|.
name|instance
operator|.
name|InstanceShardOperationRequestBuilder
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
name|ElasticsearchClient
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
name|logging
operator|.
name|DeprecationLogger
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
name|logging
operator|.
name|Loggers
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|document
operator|.
name|RestUpdateAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
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

begin_class
DECL|class|UpdateRequestBuilder
specifier|public
class|class
name|UpdateRequestBuilder
extends|extends
name|InstanceShardOperationRequestBuilder
argument_list|<
name|UpdateRequest
argument_list|,
name|UpdateResponse
argument_list|,
name|UpdateRequestBuilder
argument_list|>
implements|implements
name|WriteRequestBuilder
argument_list|<
name|UpdateRequestBuilder
argument_list|>
block|{
DECL|field|DEPRECATION_LOGGER
specifier|private
specifier|static
specifier|final
name|DeprecationLogger
name|DEPRECATION_LOGGER
init|=
operator|new
name|DeprecationLogger
argument_list|(
name|Loggers
operator|.
name|getLogger
argument_list|(
name|RestUpdateAction
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|UpdateRequestBuilder
specifier|public
name|UpdateRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|UpdateAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|UpdateRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|UpdateRequestBuilder
specifier|public
name|UpdateRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|UpdateAction
name|action
parameter_list|,
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
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|UpdateRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the type of the indexed document.      */
DECL|method|setType
specifier|public
name|UpdateRequestBuilder
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
comment|/**      * Sets the id of the indexed document.      */
DECL|method|setId
specifier|public
name|UpdateRequestBuilder
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
name|UpdateRequestBuilder
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
DECL|method|setParent
specifier|public
name|UpdateRequestBuilder
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
comment|/**      * The script to execute. Note, make sure not to send different script each times and instead      * use script params if possible with the same (automatically compiled) script.      *<p>      * The script works with the variable<code>ctx</code>, which is bound to the entry,      * e.g.<code>ctx._source.mycounter += 1</code>.      *      */
DECL|method|setScript
specifier|public
name|UpdateRequestBuilder
name|setScript
parameter_list|(
name|Script
name|script
parameter_list|)
block|{
name|request
operator|.
name|script
argument_list|(
name|script
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly specify the fields that will be returned. By default, nothing is returned.      * @deprecated Use {@link UpdateRequestBuilder#setFetchSource(String[], String[])} instead      */
annotation|@
name|Deprecated
DECL|method|setFields
specifier|public
name|UpdateRequestBuilder
name|setFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|DEPRECATION_LOGGER
operator|.
name|deprecated
argument_list|(
literal|"Deprecated field [fields] used, expected [_source] instead"
argument_list|)
expr_stmt|;
name|request
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Indicate that _source should be returned with every hit, with an      * "include" and/or "exclude" set which can include simple wildcard      * elements.      *      * @param include      *            An optional include (optionally wildcarded) pattern to filter      *            the returned _source      * @param exclude      *            An optional exclude (optionally wildcarded) pattern to filter      *            the returned _source      */
DECL|method|setFetchSource
specifier|public
name|UpdateRequestBuilder
name|setFetchSource
parameter_list|(
annotation|@
name|Nullable
name|String
name|include
parameter_list|,
annotation|@
name|Nullable
name|String
name|exclude
parameter_list|)
block|{
name|request
operator|.
name|fetchSource
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Indicate that _source should be returned, with an      * "include" and/or "exclude" set which can include simple wildcard      * elements.      *      * @param includes      *            An optional list of include (optionally wildcarded) pattern to      *            filter the returned _source      * @param excludes      *            An optional list of exclude (optionally wildcarded) pattern to      *            filter the returned _source      */
DECL|method|setFetchSource
specifier|public
name|UpdateRequestBuilder
name|setFetchSource
parameter_list|(
annotation|@
name|Nullable
name|String
index|[]
name|includes
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|excludes
parameter_list|)
block|{
name|request
operator|.
name|fetchSource
argument_list|(
name|includes
argument_list|,
name|excludes
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Indicates whether the response should contain the updated _source.      */
DECL|method|setFetchSource
specifier|public
name|UpdateRequestBuilder
name|setFetchSource
parameter_list|(
name|boolean
name|fetchSource
parameter_list|)
block|{
name|request
operator|.
name|fetchSource
argument_list|(
name|fetchSource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the number of retries of a version conflict occurs because the document was updated between      * getting it and updating it. Defaults to 0.      */
DECL|method|setRetryOnConflict
specifier|public
name|UpdateRequestBuilder
name|setRetryOnConflict
parameter_list|(
name|int
name|retryOnConflict
parameter_list|)
block|{
name|request
operator|.
name|retryOnConflict
argument_list|(
name|retryOnConflict
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the version, which will cause the index operation to only be performed if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|setVersion
specifier|public
name|UpdateRequestBuilder
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
comment|/**      * Sets the versioning type. Defaults to {@link org.elasticsearch.index.VersionType#INTERNAL}.      */
DECL|method|setVersionType
specifier|public
name|UpdateRequestBuilder
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
comment|/**      * Sets the number of shard copies that must be active before proceeding with the write.      * See {@link ReplicationRequest#waitForActiveShards(ActiveShardCount)} for details.      */
DECL|method|setWaitForActiveShards
specifier|public
name|UpdateRequestBuilder
name|setWaitForActiveShards
parameter_list|(
name|ActiveShardCount
name|waitForActiveShards
parameter_list|)
block|{
name|request
operator|.
name|waitForActiveShards
argument_list|(
name|waitForActiveShards
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A shortcut for {@link #setWaitForActiveShards(ActiveShardCount)} where the numerical      * shard count is passed in, instead of having to first call {@link ActiveShardCount#from(int)}      * to get the ActiveShardCount.      */
DECL|method|setWaitForActiveShards
specifier|public
name|UpdateRequestBuilder
name|setWaitForActiveShards
parameter_list|(
specifier|final
name|int
name|waitForActiveShards
parameter_list|)
block|{
return|return
name|setWaitForActiveShards
argument_list|(
name|ActiveShardCount
operator|.
name|from
argument_list|(
name|waitForActiveShards
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|IndexRequest
name|indexRequest
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|indexRequest
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|XContentBuilder
name|source
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|Map
name|source
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|Map
name|source
parameter_list|,
name|XContentType
name|contentType
parameter_list|)
block|{
name|request
operator|.
name|doc
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
comment|/**      * Sets the doc to use for updates when a script is not specified.      * @deprecated use {@link #setDoc(String, XContentType)}      */
annotation|@
name|Deprecated
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|String
name|source
parameter_list|,
name|XContentType
name|xContentType
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      * @deprecated use {@link #setDoc(byte[], XContentType)}      */
annotation|@
name|Deprecated
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|XContentType
name|xContentType
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified.      * @deprecated use {@link #setDoc(byte[], int, int, XContentType)}      */
annotation|@
name|Deprecated
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
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
name|doc
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
comment|/**      * Sets the doc to use for updates when a script is not specified.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
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
name|XContentType
name|xContentType
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified, the doc provided      * is a field and value pairs.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|Object
modifier|...
name|source
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc to use for updates when a script is not specified, the doc provided      * is a field and value pairs.      */
DECL|method|setDoc
specifier|public
name|UpdateRequestBuilder
name|setDoc
parameter_list|(
name|XContentType
name|xContentType
parameter_list|,
name|Object
modifier|...
name|source
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|xContentType
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the index request to be used if the document does not exists. Otherwise, a {@link org.elasticsearch.index.engine.DocumentMissingException}      * is thrown.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|IndexRequest
name|indexRequest
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|indexRequest
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|XContentBuilder
name|source
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|Map
name|source
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|Map
name|source
parameter_list|,
name|XContentType
name|contentType
parameter_list|)
block|{
name|request
operator|.
name|upsert
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
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      * @deprecated use {@link #setUpsert(String, XContentType)}      */
annotation|@
name|Deprecated
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|String
name|source
parameter_list|,
name|XContentType
name|xContentType
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      * @deprecated use {@link #setDoc(byte[], XContentType)}      */
annotation|@
name|Deprecated
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|XContentType
name|xContentType
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      * @deprecated use {@link #setUpsert(byte[], int, int, XContentType)}      */
annotation|@
name|Deprecated
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
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
name|upsert
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
comment|/**      * Sets the doc source of the update request to be used when the document does not exists.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
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
name|XContentType
name|xContentType
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists. The doc      * includes field and value pairs.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|Object
modifier|...
name|source
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc source of the update request to be used when the document does not exists. The doc      * includes field and value pairs.      */
DECL|method|setUpsert
specifier|public
name|UpdateRequestBuilder
name|setUpsert
parameter_list|(
name|XContentType
name|xContentType
parameter_list|,
name|Object
modifier|...
name|source
parameter_list|)
block|{
name|request
operator|.
name|upsert
argument_list|(
name|xContentType
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether the specified doc parameter should be used as upsert document.      */
DECL|method|setDocAsUpsert
specifier|public
name|UpdateRequestBuilder
name|setDocAsUpsert
parameter_list|(
name|boolean
name|shouldUpsertDoc
parameter_list|)
block|{
name|request
operator|.
name|docAsUpsert
argument_list|(
name|shouldUpsertDoc
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to perform extra effort to detect noop updates via docAsUpsert.      * Defaults to true.      */
DECL|method|setDetectNoop
specifier|public
name|UpdateRequestBuilder
name|setDetectNoop
parameter_list|(
name|boolean
name|detectNoop
parameter_list|)
block|{
name|request
operator|.
name|detectNoop
argument_list|(
name|detectNoop
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether the script should be run in the case of an insert      */
DECL|method|setScriptedUpsert
specifier|public
name|UpdateRequestBuilder
name|setScriptedUpsert
parameter_list|(
name|boolean
name|scriptedUpsert
parameter_list|)
block|{
name|request
operator|.
name|scriptedUpsert
argument_list|(
name|scriptedUpsert
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

