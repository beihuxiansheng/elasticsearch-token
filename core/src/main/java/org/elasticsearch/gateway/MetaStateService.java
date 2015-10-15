begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|XContentParser
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
name|env
operator|.
name|NodeEnvironment
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
name|Index
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Handles writing and loading both {@link MetaData} and {@link IndexMetaData}  */
end_comment

begin_class
DECL|class|MetaStateService
specifier|public
class|class
name|MetaStateService
extends|extends
name|AbstractComponent
block|{
DECL|field|FORMAT_SETTING
specifier|static
specifier|final
name|String
name|FORMAT_SETTING
init|=
literal|"gateway.format"
decl_stmt|;
DECL|field|GLOBAL_STATE_FILE_PREFIX
specifier|static
specifier|final
name|String
name|GLOBAL_STATE_FILE_PREFIX
init|=
literal|"global-"
decl_stmt|;
DECL|field|INDEX_STATE_FILE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_STATE_FILE_PREFIX
init|=
literal|"state-"
decl_stmt|;
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|XContentType
name|format
decl_stmt|;
DECL|field|formatParams
specifier|private
specifier|final
name|ToXContent
operator|.
name|Params
name|formatParams
decl_stmt|;
DECL|field|gatewayModeFormatParams
specifier|private
specifier|final
name|ToXContent
operator|.
name|Params
name|gatewayModeFormatParams
decl_stmt|;
DECL|field|indexStateFormat
specifier|private
specifier|final
name|MetaDataStateFormat
argument_list|<
name|IndexMetaData
argument_list|>
name|indexStateFormat
decl_stmt|;
DECL|field|globalStateFormat
specifier|private
specifier|final
name|MetaDataStateFormat
argument_list|<
name|MetaData
argument_list|>
name|globalStateFormat
decl_stmt|;
annotation|@
name|Inject
DECL|method|MetaStateService
specifier|public
name|MetaStateService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|XContentType
operator|.
name|fromRestContentType
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|FORMAT_SETTING
argument_list|,
literal|"smile"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|format
operator|==
name|XContentType
operator|.
name|SMILE
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"binary"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|formatParams
operator|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|gatewayModeParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|gatewayModeParams
operator|.
name|put
argument_list|(
literal|"binary"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|gatewayModeParams
operator|.
name|put
argument_list|(
name|MetaData
operator|.
name|CONTEXT_MODE_PARAM
argument_list|,
name|MetaData
operator|.
name|CONTEXT_MODE_GATEWAY
argument_list|)
expr_stmt|;
name|gatewayModeFormatParams
operator|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|gatewayModeParams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|formatParams
operator|=
name|ToXContent
operator|.
name|EMPTY_PARAMS
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|gatewayModeParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|gatewayModeParams
operator|.
name|put
argument_list|(
name|MetaData
operator|.
name|CONTEXT_MODE_PARAM
argument_list|,
name|MetaData
operator|.
name|CONTEXT_MODE_GATEWAY
argument_list|)
expr_stmt|;
name|gatewayModeFormatParams
operator|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|gatewayModeParams
argument_list|)
expr_stmt|;
block|}
name|indexStateFormat
operator|=
name|indexStateFormat
argument_list|(
name|format
argument_list|,
name|formatParams
argument_list|)
expr_stmt|;
name|globalStateFormat
operator|=
name|globalStateFormat
argument_list|(
name|format
argument_list|,
name|gatewayModeFormatParams
argument_list|)
expr_stmt|;
block|}
comment|/**      * Loads the full state, which includes both the global state and all the indices      * meta state.      */
DECL|method|loadFullState
name|MetaData
name|loadFullState
parameter_list|()
throws|throws
name|Exception
block|{
name|MetaData
name|globalMetaData
init|=
name|loadGlobalState
argument_list|()
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaDataBuilder
decl_stmt|;
if|if
condition|(
name|globalMetaData
operator|!=
literal|null
condition|)
block|{
name|metaDataBuilder
operator|=
name|MetaData
operator|.
name|builder
argument_list|(
name|globalMetaData
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metaDataBuilder
operator|=
name|MetaData
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|nodeEnv
operator|.
name|findAllIndices
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|loadIndexState
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] failed to find metadata for existing index location"
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|metaDataBuilder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Loads the index state for the provided index name, returning null if doesn't exists.      */
annotation|@
name|Nullable
DECL|method|loadIndexState
name|IndexMetaData
name|loadIndexState
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|indexStateFormat
operator|.
name|loadLatestState
argument_list|(
name|logger
argument_list|,
name|nodeEnv
operator|.
name|indexPaths
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Loads the global state, *without* index state, see {@link #loadFullState()} for that.      */
DECL|method|loadGlobalState
name|MetaData
name|loadGlobalState
parameter_list|()
throws|throws
name|IOException
block|{
name|MetaData
name|globalState
init|=
name|globalStateFormat
operator|.
name|loadLatestState
argument_list|(
name|logger
argument_list|,
name|nodeEnv
operator|.
name|nodeDataPaths
argument_list|()
argument_list|)
decl_stmt|;
comment|// ES 2.0 now requires units for all time and byte-sized settings, so we add the default unit if it's missing
comment|// TODO: can we somehow only do this for pre-2.0 cluster state?
if|if
condition|(
name|globalState
operator|!=
literal|null
condition|)
block|{
return|return
name|MetaData
operator|.
name|addDefaultUnitsIfNeeded
argument_list|(
name|logger
argument_list|,
name|globalState
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Writes the index state.      */
DECL|method|writeIndex
name|void
name|writeIndex
parameter_list|(
name|String
name|reason
parameter_list|,
name|IndexMetaData
name|indexMetaData
parameter_list|,
annotation|@
name|Nullable
name|IndexMetaData
name|previousIndexMetaData
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] writing state, reason [{}]"
argument_list|,
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|,
name|reason
argument_list|)
expr_stmt|;
try|try
block|{
name|indexStateFormat
operator|.
name|write
argument_list|(
name|indexMetaData
argument_list|,
name|indexMetaData
operator|.
name|getVersion
argument_list|()
argument_list|,
name|nodeEnv
operator|.
name|indexPaths
argument_list|(
operator|new
name|Index
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}]: failed to write index state"
argument_list|,
name|ex
argument_list|,
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to write state for ["
operator|+
name|indexMetaData
operator|.
name|getIndex
argument_list|()
operator|+
literal|"]"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Writes the global state, *without* the indices states.      */
DECL|method|writeGlobalState
name|void
name|writeGlobalState
parameter_list|(
name|String
name|reason
parameter_list|,
name|MetaData
name|metaData
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[_global] writing state, reason [{}]"
argument_list|,
name|reason
argument_list|)
expr_stmt|;
try|try
block|{
name|globalStateFormat
operator|.
name|write
argument_list|(
name|metaData
argument_list|,
name|metaData
operator|.
name|version
argument_list|()
argument_list|,
name|nodeEnv
operator|.
name|nodeDataPaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[_global]: failed to write global state"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to write global state"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a StateFormat that can read and write {@link MetaData}      */
DECL|method|globalStateFormat
specifier|static
name|MetaDataStateFormat
argument_list|<
name|MetaData
argument_list|>
name|globalStateFormat
parameter_list|(
name|XContentType
name|format
parameter_list|,
specifier|final
name|ToXContent
operator|.
name|Params
name|formatParams
parameter_list|)
block|{
return|return
operator|new
name|MetaDataStateFormat
argument_list|<
name|MetaData
argument_list|>
argument_list|(
name|format
argument_list|,
name|GLOBAL_STATE_FILE_PREFIX
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|MetaData
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|MetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|state
argument_list|,
name|builder
argument_list|,
name|formatParams
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MetaData
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns a StateFormat that can read and write {@link IndexMetaData}      */
DECL|method|indexStateFormat
specifier|static
name|MetaDataStateFormat
argument_list|<
name|IndexMetaData
argument_list|>
name|indexStateFormat
parameter_list|(
name|XContentType
name|format
parameter_list|,
specifier|final
name|ToXContent
operator|.
name|Params
name|formatParams
parameter_list|)
block|{
return|return
operator|new
name|MetaDataStateFormat
argument_list|<
name|IndexMetaData
argument_list|>
argument_list|(
name|format
argument_list|,
name|INDEX_STATE_FILE_PREFIX
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|IndexMetaData
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexMetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|state
argument_list|,
name|builder
argument_list|,
name|formatParams
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexMetaData
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexMetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

