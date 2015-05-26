begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.create
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|create
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchGenerationException
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
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|MasterNodeRequest
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
name|XContentType
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
name|ValidateActions
operator|.
name|addValidationError
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
name|Strings
operator|.
name|EMPTY_ARRAY
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
name|Strings
operator|.
name|hasLength
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
name|settings
operator|.
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
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
name|settings
operator|.
name|Settings
operator|.
name|readSettingsFromStream
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
name|settings
operator|.
name|Settings
operator|.
name|writeSettingsToStream
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
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeBooleanValue
import|;
end_import

begin_comment
comment|/**  * Create snapshot request  *<p/>  * The only mandatory parameter is repository name. The repository name has to satisfy the following requirements  *<ul>  *<li>be a non-empty string</li>  *<li>must not contain whitespace (tabs or spaces)</li>  *<li>must not contain comma (',')</li>  *<li>must not contain hash sign ('#')</li>  *<li>must not start with underscore ('-')</li>  *<li>must be lowercase</li>  *<li>must not contain invalid file name characters {@link org.elasticsearch.common.Strings#INVALID_FILENAME_CHARS}</li>  *</ul>  */
end_comment

begin_class
DECL|class|CreateSnapshotRequest
specifier|public
class|class
name|CreateSnapshotRequest
extends|extends
name|MasterNodeRequest
argument_list|<
name|CreateSnapshotRequest
argument_list|>
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
DECL|field|snapshot
specifier|private
name|String
name|snapshot
decl_stmt|;
DECL|field|repository
specifier|private
name|String
name|repository
decl_stmt|;
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
init|=
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|indicesOptions
specifier|private
name|IndicesOptions
name|indicesOptions
init|=
name|IndicesOptions
operator|.
name|strictExpandOpen
argument_list|()
decl_stmt|;
DECL|field|partial
specifier|private
name|boolean
name|partial
init|=
literal|false
decl_stmt|;
DECL|field|settings
specifier|private
name|Settings
name|settings
init|=
name|EMPTY_SETTINGS
decl_stmt|;
DECL|field|includeGlobalState
specifier|private
name|boolean
name|includeGlobalState
init|=
literal|true
decl_stmt|;
DECL|field|waitForCompletion
specifier|private
name|boolean
name|waitForCompletion
decl_stmt|;
DECL|method|CreateSnapshotRequest
name|CreateSnapshotRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new put repository request with the provided snapshot and repository names      *      * @param repository repository name      * @param snapshot   snapshot name      */
DECL|method|CreateSnapshotRequest
specifier|public
name|CreateSnapshotRequest
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|snapshot
operator|=
name|snapshot
expr_stmt|;
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
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
name|snapshot
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"snapshot is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"repository is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indices
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"indices is null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"index is null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|indicesOptions
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"indicesOptions is null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"settings is null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * Sets the snapshot name      *      * @param snapshot snapshot name      */
DECL|method|snapshot
specifier|public
name|CreateSnapshotRequest
name|snapshot
parameter_list|(
name|String
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|snapshot
operator|=
name|snapshot
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The snapshot name      *      * @return snapshot name      */
DECL|method|snapshot
specifier|public
name|String
name|snapshot
parameter_list|()
block|{
return|return
name|this
operator|.
name|snapshot
return|;
block|}
comment|/**      * Sets repository name      *      * @param repository name      * @return this request      */
DECL|method|repository
specifier|public
name|CreateSnapshotRequest
name|repository
parameter_list|(
name|String
name|repository
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns repository name      *      * @return repository name      */
DECL|method|repository
specifier|public
name|String
name|repository
parameter_list|()
block|{
return|return
name|this
operator|.
name|repository
return|;
block|}
comment|/**      * Sets a list of indices that should be included into the snapshot      *<p/>      * The list of indices supports multi-index syntax. For example: "+test*" ,"-test42" will index all indices with      * prefix "test" except index "test42". Aliases are supported. An empty list or {"_all"} will snapshot all open      * indices in the cluster.      *      * @param indices      * @return this request      */
annotation|@
name|Override
DECL|method|indices
specifier|public
name|CreateSnapshotRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets a list of indices that should be included into the snapshot      *<p/>      * The list of indices supports multi-index syntax. For example: "+test*" ,"-test42" will index all indices with      * prefix "test" except index "test42". Aliases are supported. An empty list or {"_all"} will snapshot all open      * indices in the cluster.      *      * @param indices      * @return this request      */
DECL|method|indices
specifier|public
name|CreateSnapshotRequest
name|indices
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indices
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns a list of indices that should be included into the snapshot      *      * @return list of indices      */
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
comment|/**      * Specifies the indices options. Like what type of requested indices to ignore. For example indices that don't exist.      *      * @return the desired behaviour regarding indices options      */
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|indicesOptions
return|;
block|}
comment|/**      * Specifies the indices options. Like what type of requested indices to ignore. For example indices that don't exist.      *      * @param indicesOptions the desired behaviour regarding indices options      * @return this request      */
DECL|method|indicesOptions
specifier|public
name|CreateSnapshotRequest
name|indicesOptions
parameter_list|(
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
name|this
operator|.
name|indicesOptions
operator|=
name|indicesOptions
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns true if indices with unavailable shards should be be partially snapshotted.      *      * @return the desired behaviour regarding indices options      */
DECL|method|partial
specifier|public
name|boolean
name|partial
parameter_list|()
block|{
return|return
name|partial
return|;
block|}
comment|/**      * Set to true to allow indices with unavailable shards to be partially snapshotted.      *      * @param partial true if indices with unavailable shards should be be partially snapshotted.      * @return this request      */
DECL|method|partial
specifier|public
name|CreateSnapshotRequest
name|partial
parameter_list|(
name|boolean
name|partial
parameter_list|)
block|{
name|this
operator|.
name|partial
operator|=
name|partial
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If set to true the operation should wait for the snapshot completion before returning.      *      * By default, the operation will return as soon as snapshot is initialized. It can be changed by setting this      * flag to true.      *      * @param waitForCompletion true if operation should wait for the snapshot completion      * @return this request      */
DECL|method|waitForCompletion
specifier|public
name|CreateSnapshotRequest
name|waitForCompletion
parameter_list|(
name|boolean
name|waitForCompletion
parameter_list|)
block|{
name|this
operator|.
name|waitForCompletion
operator|=
name|waitForCompletion
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns true if the request should wait for the snapshot completion before returning      *      * @return true if the request should wait for completion      */
DECL|method|waitForCompletion
specifier|public
name|boolean
name|waitForCompletion
parameter_list|()
block|{
return|return
name|waitForCompletion
return|;
block|}
comment|/**      * Sets repository-specific snapshot settings.      *<p/>      * See repository documentation for more information.      *      * @param settings repository-specific snapshot settings      * @return this request      */
DECL|method|settings
specifier|public
name|CreateSnapshotRequest
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets repository-specific snapshot settings.      *<p/>      * See repository documentation for more information.      *      * @param settings repository-specific snapshot settings      * @return this request      */
DECL|method|settings
specifier|public
name|CreateSnapshotRequest
name|settings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets repository-specific snapshot settings in JSON, YAML or properties format      *<p/>      * See repository documentation for more information.      *      * @param source repository-specific snapshot settings      * @return this request      */
DECL|method|settings
specifier|public
name|CreateSnapshotRequest
name|settings
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|loadFromSource
argument_list|(
name|source
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets repository-specific snapshot settings.      *<p/>      * See repository documentation for more information.      *      * @param source repository-specific snapshot settings      * @return this request      */
DECL|method|settings
specifier|public
name|CreateSnapshotRequest
name|settings
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
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|settings
argument_list|(
name|builder
operator|.
name|string
argument_list|()
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
name|ElasticsearchGenerationException
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
return|return
name|this
return|;
block|}
comment|/**      * Returns repository-specific snapshot settings      *      * @return repository-specific snapshot settings      */
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
return|;
block|}
comment|/**      * Set to true if global state should be stored as part of the snapshot      *      * @param includeGlobalState true if global state should be stored      * @return this request      */
DECL|method|includeGlobalState
specifier|public
name|CreateSnapshotRequest
name|includeGlobalState
parameter_list|(
name|boolean
name|includeGlobalState
parameter_list|)
block|{
name|this
operator|.
name|includeGlobalState
operator|=
name|includeGlobalState
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns true if global state should be stored as part of the snapshot      *      * @return true if global state should be stored as part of the snapshot      */
DECL|method|includeGlobalState
specifier|public
name|boolean
name|includeGlobalState
parameter_list|()
block|{
return|return
name|includeGlobalState
return|;
block|}
comment|/**      * Parses snapshot definition.      *      * @param source snapshot definition      * @return this request      */
DECL|method|source
specifier|public
name|CreateSnapshotRequest
name|source
parameter_list|(
name|XContentBuilder
name|source
parameter_list|)
block|{
return|return
name|source
argument_list|(
name|source
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Parses snapshot definition.      *      * @param source snapshot definition      * @return this request      */
DECL|method|source
specifier|public
name|CreateSnapshotRequest
name|source
parameter_list|(
name|Map
name|source
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|source
operator|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"indices"
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|String
condition|)
block|{
name|indices
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|ArrayList
condition|)
block|{
name|indices
argument_list|(
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"malformed indices section, should be an array of strings"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"partial"
argument_list|)
condition|)
block|{
name|partial
argument_list|(
name|nodeBooleanValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"settings"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"malformed settings section, should indices an inner object"
argument_list|)
throw|;
block|}
name|settings
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"include_global_state"
argument_list|)
condition|)
block|{
name|includeGlobalState
operator|=
name|nodeBooleanValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|indicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromMap
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|source
argument_list|,
name|IndicesOptions
operator|.
name|lenientExpandOpen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Parses snapshot definition. JSON, YAML and properties formats are supported      *      * @param source snapshot definition      * @return this request      */
DECL|method|source
specifier|public
name|CreateSnapshotRequest
name|source
parameter_list|(
name|String
name|source
parameter_list|)
block|{
if|if
condition|(
name|hasLength
argument_list|(
name|source
argument_list|)
condition|)
block|{
try|try
block|{
return|return
name|source
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
operator|.
name|mapOrderedAndClose
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse repository source ["
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
return|return
name|this
return|;
block|}
comment|/**      * Parses snapshot definition. JSON, YAML and properties formats are supported      *      * @param source snapshot definition      * @return this request      */
DECL|method|source
specifier|public
name|CreateSnapshotRequest
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
comment|/**      * Parses snapshot definition. JSON, YAML and properties formats are supported      *      * @param source snapshot definition      * @param offset offset      * @param length length      * @return this request      */
DECL|method|source
specifier|public
name|CreateSnapshotRequest
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
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
try|try
block|{
return|return
name|source
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
operator|.
name|mapOrderedAndClose
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
name|IllegalArgumentException
argument_list|(
literal|"failed to parse repository source"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|this
return|;
block|}
comment|/**      * Parses snapshot definition. JSON, YAML and properties formats are supported      *      * @param source snapshot definition      * @return this request      */
DECL|method|source
specifier|public
name|CreateSnapshotRequest
name|source
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
try|try
block|{
return|return
name|source
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
operator|.
name|mapOrderedAndClose
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
name|IllegalArgumentException
argument_list|(
literal|"failed to parse snapshot source"
argument_list|,
name|e
argument_list|)
throw|;
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
name|snapshot
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|repository
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|indicesOptions
operator|=
name|IndicesOptions
operator|.
name|readIndicesOptions
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|settings
operator|=
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|includeGlobalState
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|waitForCompletion
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|partial
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
name|writeString
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|indicesOptions
operator|.
name|writeIndicesOptions
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|writeSettingsToStream
argument_list|(
name|settings
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|includeGlobalState
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|waitForCompletion
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|partial
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

