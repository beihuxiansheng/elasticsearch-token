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
name|ElasticsearchIllegalArgumentException
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
name|collect
operator|.
name|Tuple
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
name|component
operator|.
name|AbstractComponent
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
name|inject
operator|.
name|Inject
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
name|Streamable
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
name|settings
operator|.
name|Settings
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
name|XContentHelper
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
name|index
operator|.
name|engine
operator|.
name|DocumentMissingException
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
name|engine
operator|.
name|DocumentSourceMissingException
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
name|get
operator|.
name|GetField
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
name|get
operator|.
name|GetResult
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
name|mapper
operator|.
name|internal
operator|.
name|ParentFieldMapper
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
name|mapper
operator|.
name|internal
operator|.
name|RoutingFieldMapper
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
name|mapper
operator|.
name|internal
operator|.
name|TTLFieldMapper
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
name|shard
operator|.
name|ShardId
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
name|shard
operator|.
name|service
operator|.
name|IndexShard
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
name|ExecutableScript
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
name|ScriptService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|source
operator|.
name|FetchSourceContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SourceLookup
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
name|HashMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMapWithExpectedSize
import|;
end_import

begin_comment
comment|/**  * Helper for translating an update request to an index, delete request or update response.  */
end_comment

begin_class
DECL|class|UpdateHelper
specifier|public
class|class
name|UpdateHelper
extends|extends
name|AbstractComponent
block|{
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
annotation|@
name|Inject
DECL|method|UpdateHelper
specifier|public
name|UpdateHelper
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
block|}
comment|/**      * Prepares an update request by converting it into an index or delete request or an update response (no action).      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|prepare
specifier|public
name|Result
name|prepare
parameter_list|(
name|UpdateRequest
name|request
parameter_list|,
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|long
name|getDate
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|GetResult
name|getResult
init|=
name|indexShard
operator|.
name|getService
argument_list|()
operator|.
name|get
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|RoutingFieldMapper
operator|.
name|NAME
block|,
name|ParentFieldMapper
operator|.
name|NAME
block|,
name|TTLFieldMapper
operator|.
name|NAME
block|}
argument_list|,
literal|true
argument_list|,
name|request
operator|.
name|version
argument_list|()
argument_list|,
name|request
operator|.
name|versionType
argument_list|()
argument_list|,
name|FetchSourceContext
operator|.
name|FETCH_SOURCE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|getResult
operator|.
name|isExists
argument_list|()
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|upsertRequest
argument_list|()
operator|==
literal|null
operator|&&
operator|!
name|request
operator|.
name|docAsUpsert
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DocumentMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|indexShard
operator|.
name|indexService
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|)
throw|;
block|}
name|Long
name|ttl
init|=
literal|null
decl_stmt|;
name|IndexRequest
name|indexRequest
init|=
name|request
operator|.
name|docAsUpsert
argument_list|()
condition|?
name|request
operator|.
name|doc
argument_list|()
else|:
name|request
operator|.
name|upsertRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|scriptedUpsert
argument_list|()
operator|&&
operator|(
name|request
operator|.
name|script
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
comment|// Run the script to perform the create logic
name|IndexRequest
name|upsert
init|=
name|request
operator|.
name|upsertRequest
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|upsertDoc
init|=
name|upsert
operator|.
name|sourceAsMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// Tell the script that this is a create and not an update
name|ctx
operator|.
name|put
argument_list|(
literal|"op"
argument_list|,
literal|"create"
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"_source"
argument_list|,
name|upsertDoc
argument_list|)
expr_stmt|;
try|try
block|{
name|ExecutableScript
name|script
init|=
name|scriptService
operator|.
name|executable
argument_list|(
name|request
operator|.
name|scriptLang
argument_list|,
name|request
operator|.
name|script
argument_list|,
name|request
operator|.
name|scriptType
argument_list|,
name|request
operator|.
name|scriptParams
argument_list|)
decl_stmt|;
name|script
operator|.
name|setNextVar
argument_list|(
literal|"ctx"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// we need to unwrap the ctx...
name|ctx
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|script
operator|.
name|unwrap
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"failed to execute script"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|//Allow the script to set TTL using ctx._ttl
name|ttl
operator|=
name|getTTLFromScriptContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
comment|//Allow the script to abort the create by setting "op" to "none"
name|String
name|scriptOpChoice
init|=
operator|(
name|String
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"op"
argument_list|)
decl_stmt|;
comment|// Only valid options for an upsert script are "create"
comment|// (the default) or "none", meaning abort upsert
if|if
condition|(
operator|!
literal|"create"
operator|.
name|equals
argument_list|(
name|scriptOpChoice
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
literal|"none"
operator|.
name|equals
argument_list|(
name|scriptOpChoice
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Used upsert operation [{}] for script [{}], doing nothing..."
argument_list|,
name|scriptOpChoice
argument_list|,
name|request
operator|.
name|script
argument_list|)
expr_stmt|;
block|}
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|getResult
operator|.
name|getIndex
argument_list|()
argument_list|,
name|getResult
operator|.
name|getType
argument_list|()
argument_list|,
name|getResult
operator|.
name|getId
argument_list|()
argument_list|,
name|getResult
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|update
operator|.
name|setGetResult
argument_list|(
name|getResult
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|update
argument_list|,
name|Operation
operator|.
name|NONE
argument_list|,
name|upsertDoc
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
return|;
block|}
name|indexRequest
operator|.
name|source
argument_list|(
operator|(
name|Map
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"_source"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRequest
operator|.
name|index
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
comment|// it has to be a "create!"
operator|.
name|create
argument_list|(
literal|true
argument_list|)
operator|.
name|routing
argument_list|(
name|request
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|ttl
argument_list|(
name|ttl
argument_list|)
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|)
operator|.
name|replicationType
argument_list|(
name|request
operator|.
name|replicationType
argument_list|()
argument_list|)
operator|.
name|consistencyLevel
argument_list|(
name|request
operator|.
name|consistencyLevel
argument_list|()
argument_list|)
expr_stmt|;
name|indexRequest
operator|.
name|operationThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|versionType
argument_list|()
operator|!=
name|VersionType
operator|.
name|INTERNAL
condition|)
block|{
comment|// in all but the internal versioning mode, we want to create the new document using the given version.
name|indexRequest
operator|.
name|version
argument_list|(
name|request
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|versionType
argument_list|(
name|request
operator|.
name|versionType
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Result
argument_list|(
name|indexRequest
argument_list|,
name|Operation
operator|.
name|UPSERT
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|long
name|updateVersion
init|=
name|getResult
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|versionType
argument_list|()
operator|!=
name|VersionType
operator|.
name|INTERNAL
condition|)
block|{
assert|assert
name|request
operator|.
name|versionType
argument_list|()
operator|==
name|VersionType
operator|.
name|FORCE
assert|;
name|updateVersion
operator|=
name|request
operator|.
name|version
argument_list|()
expr_stmt|;
comment|// remember, match_any is excluded by the conflict test
block|}
if|if
condition|(
name|getResult
operator|.
name|internalSourceRef
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// no source, we can't do nothing, through a failure...
throw|throw
operator|new
name|DocumentSourceMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|indexShard
operator|.
name|indexService
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|)
throw|;
block|}
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|sourceAndContent
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|getResult
operator|.
name|internalSourceRef
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|operation
init|=
literal|null
decl_stmt|;
name|String
name|timestamp
decl_stmt|;
name|Long
name|ttl
init|=
literal|null
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|updatedSourceAsMap
decl_stmt|;
specifier|final
name|XContentType
name|updateSourceContentType
init|=
name|sourceAndContent
operator|.
name|v1
argument_list|()
decl_stmt|;
name|String
name|routing
init|=
name|getResult
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|RoutingFieldMapper
operator|.
name|NAME
argument_list|)
condition|?
name|getResult
operator|.
name|field
argument_list|(
name|RoutingFieldMapper
operator|.
name|NAME
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|null
decl_stmt|;
name|String
name|parent
init|=
name|getResult
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|)
condition|?
name|getResult
operator|.
name|field
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|script
argument_list|()
operator|==
literal|null
operator|&&
name|request
operator|.
name|doc
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
name|request
operator|.
name|doc
argument_list|()
decl_stmt|;
name|updatedSourceAsMap
operator|=
name|sourceAndContent
operator|.
name|v2
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexRequest
operator|.
name|ttl
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ttl
operator|=
name|indexRequest
operator|.
name|ttl
argument_list|()
expr_stmt|;
block|}
name|timestamp
operator|=
name|indexRequest
operator|.
name|timestamp
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexRequest
operator|.
name|routing
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|routing
operator|=
name|indexRequest
operator|.
name|routing
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|indexRequest
operator|.
name|parent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|parent
operator|=
name|indexRequest
operator|.
name|parent
argument_list|()
expr_stmt|;
block|}
name|boolean
name|noop
init|=
operator|!
name|XContentHelper
operator|.
name|update
argument_list|(
name|updatedSourceAsMap
argument_list|,
name|indexRequest
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|request
operator|.
name|detectNoop
argument_list|()
argument_list|)
decl_stmt|;
comment|// noop could still be true even if detectNoop isn't because update detects empty maps as noops.  BUT we can only
comment|// actually turn the update into a noop if detectNoop is true to preserve backwards compatibility and to handle
comment|// cases where users repopulating multi-fields or adding synonyms, etc.
if|if
condition|(
name|request
operator|.
name|detectNoop
argument_list|()
operator|&&
name|noop
condition|)
block|{
name|operation
operator|=
literal|"none"
expr_stmt|;
block|}
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"_source"
argument_list|,
name|sourceAndContent
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ExecutableScript
name|script
init|=
name|scriptService
operator|.
name|executable
argument_list|(
name|request
operator|.
name|scriptLang
argument_list|,
name|request
operator|.
name|script
argument_list|,
name|request
operator|.
name|scriptType
argument_list|,
name|request
operator|.
name|scriptParams
argument_list|)
decl_stmt|;
name|script
operator|.
name|setNextVar
argument_list|(
literal|"ctx"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// we need to unwrap the ctx...
name|ctx
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|script
operator|.
name|unwrap
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"failed to execute script"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|operation
operator|=
operator|(
name|String
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"op"
argument_list|)
expr_stmt|;
name|timestamp
operator|=
operator|(
name|String
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"_timestamp"
argument_list|)
expr_stmt|;
name|ttl
operator|=
name|getTTLFromScriptContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|updatedSourceAsMap
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"_source"
argument_list|)
expr_stmt|;
block|}
comment|// apply script to update the source
comment|// No TTL has been given in the update script so we keep previous TTL value if there is one
if|if
condition|(
name|ttl
operator|==
literal|null
condition|)
block|{
name|ttl
operator|=
name|getResult
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|TTLFieldMapper
operator|.
name|NAME
argument_list|)
condition|?
operator|(
name|Long
operator|)
name|getResult
operator|.
name|field
argument_list|(
name|TTLFieldMapper
operator|.
name|NAME
argument_list|)
operator|.
name|getValue
argument_list|()
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|ttl
operator|!=
literal|null
condition|)
block|{
name|ttl
operator|=
name|ttl
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|getDate
operator|)
expr_stmt|;
comment|// It is an approximation of exact TTL value, could be improved
block|}
block|}
if|if
condition|(
name|operation
operator|==
literal|null
operator|||
literal|"index"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
specifier|final
name|IndexRequest
name|indexRequest
init|=
name|Requests
operator|.
name|indexRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|request
operator|.
name|id
argument_list|()
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
name|source
argument_list|(
name|updatedSourceAsMap
argument_list|,
name|updateSourceContentType
argument_list|)
operator|.
name|version
argument_list|(
name|updateVersion
argument_list|)
operator|.
name|versionType
argument_list|(
name|request
operator|.
name|versionType
argument_list|()
argument_list|)
operator|.
name|replicationType
argument_list|(
name|request
operator|.
name|replicationType
argument_list|()
argument_list|)
operator|.
name|consistencyLevel
argument_list|(
name|request
operator|.
name|consistencyLevel
argument_list|()
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
name|refresh
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|)
decl_stmt|;
name|indexRequest
operator|.
name|operationThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|indexRequest
argument_list|,
name|Operation
operator|.
name|INDEX
argument_list|,
name|updatedSourceAsMap
argument_list|,
name|updateSourceContentType
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"delete"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
name|DeleteRequest
name|deleteRequest
init|=
name|Requests
operator|.
name|deleteRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|request
operator|.
name|id
argument_list|()
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
name|version
argument_list|(
name|updateVersion
argument_list|)
operator|.
name|versionType
argument_list|(
name|request
operator|.
name|versionType
argument_list|()
argument_list|)
operator|.
name|replicationType
argument_list|(
name|request
operator|.
name|replicationType
argument_list|()
argument_list|)
operator|.
name|consistencyLevel
argument_list|(
name|request
operator|.
name|consistencyLevel
argument_list|()
argument_list|)
decl_stmt|;
name|deleteRequest
operator|.
name|operationThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|deleteRequest
argument_list|,
name|Operation
operator|.
name|DELETE
argument_list|,
name|updatedSourceAsMap
argument_list|,
name|updateSourceContentType
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|getResult
operator|.
name|getIndex
argument_list|()
argument_list|,
name|getResult
operator|.
name|getType
argument_list|()
argument_list|,
name|getResult
operator|.
name|getId
argument_list|()
argument_list|,
name|getResult
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|update
operator|.
name|setGetResult
argument_list|(
name|extractGetResult
argument_list|(
name|request
argument_list|,
name|indexShard
operator|.
name|indexService
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|getResult
operator|.
name|getVersion
argument_list|()
argument_list|,
name|updatedSourceAsMap
argument_list|,
name|updateSourceContentType
argument_list|,
name|getResult
operator|.
name|internalSourceRef
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|update
argument_list|,
name|Operation
operator|.
name|NONE
argument_list|,
name|updatedSourceAsMap
argument_list|,
name|updateSourceContentType
argument_list|)
return|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Used update operation [{}] for script [{}], doing nothing..."
argument_list|,
name|operation
argument_list|,
name|request
operator|.
name|script
argument_list|)
expr_stmt|;
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|getResult
operator|.
name|getIndex
argument_list|()
argument_list|,
name|getResult
operator|.
name|getType
argument_list|()
argument_list|,
name|getResult
operator|.
name|getId
argument_list|()
argument_list|,
name|getResult
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|update
argument_list|,
name|Operation
operator|.
name|NONE
argument_list|,
name|updatedSourceAsMap
argument_list|,
name|updateSourceContentType
argument_list|)
return|;
block|}
block|}
DECL|method|getTTLFromScriptContext
specifier|private
name|Long
name|getTTLFromScriptContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ctx
parameter_list|)
block|{
name|Long
name|ttl
init|=
literal|null
decl_stmt|;
name|Object
name|fetchedTTL
init|=
name|ctx
operator|.
name|get
argument_list|(
literal|"_ttl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetchedTTL
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fetchedTTL
operator|instanceof
name|Number
condition|)
block|{
name|ttl
operator|=
operator|(
operator|(
name|Number
operator|)
name|fetchedTTL
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ttl
operator|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
operator|(
name|String
operator|)
name|fetchedTTL
argument_list|,
literal|null
argument_list|)
operator|.
name|millis
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ttl
return|;
block|}
comment|/**      * Extracts the fields from the updated document to be returned in a update response      */
DECL|method|extractGetResult
specifier|public
name|GetResult
name|extractGetResult
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
name|String
name|concreteIndex
parameter_list|,
name|long
name|version
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|,
name|XContentType
name|sourceContentType
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|BytesReference
name|sourceAsBytes
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|==
literal|null
operator|||
name|request
operator|.
name|fields
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|boolean
name|sourceRequested
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|GetField
argument_list|>
name|fields
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|fields
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|SourceLookup
name|sourceLookup
init|=
operator|new
name|SourceLookup
argument_list|()
decl_stmt|;
name|sourceLookup
operator|.
name|setNextSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|request
operator|.
name|fields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"_source"
argument_list|)
condition|)
block|{
name|sourceRequested
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
name|Object
name|value
init|=
name|sourceLookup
operator|.
name|extractValue
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|newHashMapWithExpectedSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|GetField
name|getField
init|=
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|getField
operator|==
literal|null
condition|)
block|{
name|getField
operator|=
operator|new
name|GetField
argument_list|(
name|field
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|getField
argument_list|)
expr_stmt|;
block|}
name|getField
operator|.
name|getValues
argument_list|()
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// TODO when using delete/none, we can still return the source as bytes by generating it (using the sourceContentType)
return|return
operator|new
name|GetResult
argument_list|(
name|concreteIndex
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|version
argument_list|,
literal|true
argument_list|,
name|sourceRequested
condition|?
name|sourceAsBytes
else|:
literal|null
argument_list|,
name|fields
argument_list|)
return|;
block|}
DECL|class|Result
specifier|public
specifier|static
class|class
name|Result
block|{
DECL|field|action
specifier|private
specifier|final
name|Streamable
name|action
decl_stmt|;
DECL|field|operation
specifier|private
specifier|final
name|Operation
name|operation
decl_stmt|;
DECL|field|updatedSourceAsMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|updatedSourceAsMap
decl_stmt|;
DECL|field|updateSourceContentType
specifier|private
specifier|final
name|XContentType
name|updateSourceContentType
decl_stmt|;
DECL|method|Result
specifier|public
name|Result
parameter_list|(
name|Streamable
name|action
parameter_list|,
name|Operation
name|operation
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|updatedSourceAsMap
parameter_list|,
name|XContentType
name|updateSourceContentType
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|updatedSourceAsMap
operator|=
name|updatedSourceAsMap
expr_stmt|;
name|this
operator|.
name|updateSourceContentType
operator|=
name|updateSourceContentType
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|action
specifier|public
parameter_list|<
name|T
extends|extends
name|Streamable
parameter_list|>
name|T
name|action
parameter_list|()
block|{
return|return
operator|(
name|T
operator|)
name|action
return|;
block|}
DECL|method|operation
specifier|public
name|Operation
name|operation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
DECL|method|updatedSourceAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|updatedSourceAsMap
parameter_list|()
block|{
return|return
name|updatedSourceAsMap
return|;
block|}
DECL|method|updateSourceContentType
specifier|public
name|XContentType
name|updateSourceContentType
parameter_list|()
block|{
return|return
name|updateSourceContentType
return|;
block|}
block|}
DECL|enum|Operation
specifier|public
specifier|static
enum|enum
name|Operation
block|{
DECL|enum constant|UPSERT
name|UPSERT
block|,
DECL|enum constant|INDEX
name|INDEX
block|,
DECL|enum constant|DELETE
name|DELETE
block|,
DECL|enum constant|NONE
name|NONE
block|}
block|}
end_class

end_unit

