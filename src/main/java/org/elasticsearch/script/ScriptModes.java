begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
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
name|ImmutableMap
import|;
end_import

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
name|Maps
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
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
name|script
operator|.
name|ScriptService
operator|.
name|ScriptType
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * Holds the {@link org.elasticsearch.script.ScriptMode}s for each of the different scripting languages available,  * each script source and each scripted operation.  */
end_comment

begin_class
DECL|class|ScriptModes
specifier|public
class|class
name|ScriptModes
block|{
DECL|field|SCRIPT_SETTINGS_PREFIX
specifier|static
specifier|final
name|String
name|SCRIPT_SETTINGS_PREFIX
init|=
literal|"script."
decl_stmt|;
DECL|field|ENGINE_SETTINGS_PREFIX
specifier|static
specifier|final
name|String
name|ENGINE_SETTINGS_PREFIX
init|=
literal|"script.engine"
decl_stmt|;
DECL|field|scriptModes
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
decl_stmt|;
DECL|method|ScriptModes
name|ScriptModes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|,
name|ScriptContextRegistry
name|scriptContextRegistry
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
comment|//filter out the native engine as we don't want to apply fine grained settings to it.
comment|//native scripts are always on as they are static by definition.
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|filteredEngines
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|scriptEngines
argument_list|)
decl_stmt|;
name|filteredEngines
operator|.
name|remove
argument_list|(
name|NativeScriptEngineService
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|scriptModes
operator|=
name|buildScriptModeSettingsMap
argument_list|(
name|settings
argument_list|,
name|filteredEngines
argument_list|,
name|scriptContextRegistry
argument_list|)
expr_stmt|;
block|}
DECL|method|buildScriptModeSettingsMap
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|buildScriptModeSettingsMap
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|,
name|ScriptContextRegistry
name|scriptContextRegistry
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModesMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|//file scripts are enabled by default, for any language
name|addGlobalScriptTypeModes
argument_list|(
name|scriptEngines
operator|.
name|keySet
argument_list|()
argument_list|,
name|scriptContextRegistry
argument_list|,
name|ScriptType
operator|.
name|FILE
argument_list|,
name|ScriptMode
operator|.
name|ON
argument_list|,
name|scriptModesMap
argument_list|)
expr_stmt|;
comment|//indexed scripts are enabled by default only for sandboxed languages
name|addGlobalScriptTypeModes
argument_list|(
name|scriptEngines
operator|.
name|keySet
argument_list|()
argument_list|,
name|scriptContextRegistry
argument_list|,
name|ScriptType
operator|.
name|INDEXED
argument_list|,
name|ScriptMode
operator|.
name|SANDBOX
argument_list|,
name|scriptModesMap
argument_list|)
expr_stmt|;
comment|//dynamic scripts are enabled by default only for sandboxed languages
name|addGlobalScriptTypeModes
argument_list|(
name|scriptEngines
operator|.
name|keySet
argument_list|()
argument_list|,
name|scriptContextRegistry
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|ScriptMode
operator|.
name|SANDBOX
argument_list|,
name|scriptModesMap
argument_list|)
expr_stmt|;
name|processSourceBasedGlobalSettings
argument_list|(
name|settings
argument_list|,
name|scriptEngines
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptModesMap
argument_list|)
expr_stmt|;
name|processOperationBasedGlobalSettings
argument_list|(
name|settings
argument_list|,
name|scriptEngines
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptModesMap
argument_list|)
expr_stmt|;
name|processEngineSpecificSettings
argument_list|(
name|settings
argument_list|,
name|scriptEngines
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptModesMap
argument_list|)
expr_stmt|;
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|scriptModesMap
argument_list|)
return|;
block|}
DECL|method|processSourceBasedGlobalSettings
specifier|private
specifier|static
name|void
name|processSourceBasedGlobalSettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|,
name|ScriptContextRegistry
name|scriptContextRegistry
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
comment|//read custom source based settings for all operations (e.g. script.indexed: on)
for|for
control|(
name|ScriptType
name|scriptType
range|:
name|ScriptType
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|scriptTypeSetting
init|=
name|settings
operator|.
name|get
argument_list|(
name|SCRIPT_SETTINGS_PREFIX
operator|+
name|scriptType
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|scriptTypeSetting
argument_list|)
condition|)
block|{
name|ScriptMode
name|scriptTypeMode
init|=
name|ScriptMode
operator|.
name|parse
argument_list|(
name|scriptTypeSetting
argument_list|)
decl_stmt|;
name|addGlobalScriptTypeModes
argument_list|(
name|scriptEngines
operator|.
name|keySet
argument_list|()
argument_list|,
name|scriptContextRegistry
argument_list|,
name|scriptType
argument_list|,
name|scriptTypeMode
argument_list|,
name|scriptModes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|processOperationBasedGlobalSettings
specifier|private
specifier|static
name|void
name|processOperationBasedGlobalSettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|,
name|ScriptContextRegistry
name|scriptContextRegistry
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
comment|//read custom op based settings for all sources (e.g. script.aggs: off)
comment|//op based settings take precedence over source based settings, hence they get expanded later
for|for
control|(
name|ScriptContext
name|scriptContext
range|:
name|scriptContextRegistry
operator|.
name|scriptContexts
argument_list|()
control|)
block|{
name|ScriptMode
name|scriptMode
init|=
name|getScriptContextMode
argument_list|(
name|settings
argument_list|,
name|SCRIPT_SETTINGS_PREFIX
argument_list|,
name|scriptContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptMode
operator|!=
literal|null
condition|)
block|{
name|addGlobalScriptContextModes
argument_list|(
name|scriptEngines
operator|.
name|keySet
argument_list|()
argument_list|,
name|scriptContext
argument_list|,
name|scriptMode
argument_list|,
name|scriptModes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|processEngineSpecificSettings
specifier|private
specifier|static
name|void
name|processEngineSpecificSettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|,
name|ScriptContextRegistry
name|scriptContextRegistry
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|langGroupedSettings
init|=
name|settings
operator|.
name|getGroups
argument_list|(
name|ENGINE_SETTINGS_PREFIX
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|langSettings
range|:
name|langGroupedSettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//read engine specific settings that refer to a non existing script lang will be ignored
name|ScriptEngineService
name|scriptEngineService
init|=
name|scriptEngines
operator|.
name|get
argument_list|(
name|langSettings
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptEngineService
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ScriptType
name|scriptType
range|:
name|ScriptType
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|scriptTypePrefix
init|=
name|scriptType
operator|+
literal|"."
decl_stmt|;
for|for
control|(
name|ScriptContext
name|scriptContext
range|:
name|scriptContextRegistry
operator|.
name|scriptContexts
argument_list|()
control|)
block|{
name|ScriptMode
name|scriptMode
init|=
name|getScriptContextMode
argument_list|(
name|langSettings
operator|.
name|getValue
argument_list|()
argument_list|,
name|scriptTypePrefix
argument_list|,
name|scriptContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptMode
operator|!=
literal|null
condition|)
block|{
name|addScriptMode
argument_list|(
name|scriptEngineService
argument_list|,
name|scriptType
argument_list|,
name|scriptContext
argument_list|,
name|scriptMode
argument_list|,
name|scriptModes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|getScriptContextMode
specifier|private
specifier|static
name|ScriptMode
name|getScriptContextMode
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|prefix
parameter_list|,
name|ScriptContext
name|scriptContext
parameter_list|)
block|{
name|String
name|settingValue
init|=
name|settings
operator|.
name|get
argument_list|(
name|prefix
operator|+
name|scriptContext
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|settingValue
argument_list|)
condition|)
block|{
return|return
name|ScriptMode
operator|.
name|parse
argument_list|(
name|settingValue
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|addGlobalScriptTypeModes
specifier|private
specifier|static
name|void
name|addGlobalScriptTypeModes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|langs
parameter_list|,
name|ScriptContextRegistry
name|scriptContextRegistry
parameter_list|,
name|ScriptType
name|scriptType
parameter_list|,
name|ScriptMode
name|scriptMode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
for|for
control|(
name|String
name|lang
range|:
name|langs
control|)
block|{
for|for
control|(
name|ScriptContext
name|scriptContext
range|:
name|scriptContextRegistry
operator|.
name|scriptContexts
argument_list|()
control|)
block|{
name|addScriptMode
argument_list|(
name|lang
argument_list|,
name|scriptType
argument_list|,
name|scriptContext
argument_list|,
name|scriptMode
argument_list|,
name|scriptModes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addGlobalScriptContextModes
specifier|private
specifier|static
name|void
name|addGlobalScriptContextModes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|langs
parameter_list|,
name|ScriptContext
name|scriptContext
parameter_list|,
name|ScriptMode
name|scriptMode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
for|for
control|(
name|String
name|lang
range|:
name|langs
control|)
block|{
for|for
control|(
name|ScriptType
name|scriptType
range|:
name|ScriptType
operator|.
name|values
argument_list|()
control|)
block|{
name|addScriptMode
argument_list|(
name|lang
argument_list|,
name|scriptType
argument_list|,
name|scriptContext
argument_list|,
name|scriptMode
argument_list|,
name|scriptModes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addScriptMode
specifier|private
specifier|static
name|void
name|addScriptMode
parameter_list|(
name|ScriptEngineService
name|scriptEngineService
parameter_list|,
name|ScriptType
name|scriptType
parameter_list|,
name|ScriptContext
name|scriptContext
parameter_list|,
name|ScriptMode
name|scriptMode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
comment|//expand the lang specific settings to all of the different names given to each scripting language
for|for
control|(
name|String
name|scriptEngineName
range|:
name|scriptEngineService
operator|.
name|types
argument_list|()
control|)
block|{
name|addScriptMode
argument_list|(
name|scriptEngineName
argument_list|,
name|scriptType
argument_list|,
name|scriptContext
argument_list|,
name|scriptMode
argument_list|,
name|scriptModes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addScriptMode
specifier|private
specifier|static
name|void
name|addScriptMode
parameter_list|(
name|String
name|lang
parameter_list|,
name|ScriptType
name|scriptType
parameter_list|,
name|ScriptContext
name|scriptContext
parameter_list|,
name|ScriptMode
name|scriptMode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModes
parameter_list|)
block|{
name|scriptModes
operator|.
name|put
argument_list|(
name|ENGINE_SETTINGS_PREFIX
operator|+
literal|"."
operator|+
name|lang
operator|+
literal|"."
operator|+
name|scriptType
operator|+
literal|"."
operator|+
name|scriptContext
operator|.
name|getKey
argument_list|()
argument_list|,
name|scriptMode
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the script mode for a script of a certain written in a certain language,      * of a certain type and executing as part of a specific operation/api.      *      * @param lang the language that the script is written in      * @param scriptType the type of the script      * @param scriptContext the operation that requires the execution of the script      * @return whether scripts are on, off, or enabled only for sandboxed languages      */
DECL|method|getScriptMode
specifier|public
name|ScriptMode
name|getScriptMode
parameter_list|(
name|String
name|lang
parameter_list|,
name|ScriptType
name|scriptType
parameter_list|,
name|ScriptContext
name|scriptContext
parameter_list|)
block|{
comment|//native scripts are always on as they are static by definition
if|if
condition|(
name|NativeScriptEngineService
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|lang
argument_list|)
condition|)
block|{
return|return
name|ScriptMode
operator|.
name|ON
return|;
block|}
name|ScriptMode
name|scriptMode
init|=
name|scriptModes
operator|.
name|get
argument_list|(
name|ENGINE_SETTINGS_PREFIX
operator|+
literal|"."
operator|+
name|lang
operator|+
literal|"."
operator|+
name|scriptType
operator|+
literal|"."
operator|+
name|scriptContext
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptMode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"script mode not found for lang ["
operator|+
name|lang
operator|+
literal|"], script_type ["
operator|+
name|scriptType
operator|+
literal|"], operation ["
operator|+
name|scriptContext
operator|.
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|scriptMode
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
comment|//order settings by key before printing them out, for readability
name|TreeMap
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|scriptModesTreeMap
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|scriptModesTreeMap
operator|.
name|putAll
argument_list|(
name|scriptModes
argument_list|)
expr_stmt|;
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ScriptMode
argument_list|>
name|stringScriptModeEntry
range|:
name|scriptModesTreeMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|stringBuilder
operator|.
name|append
argument_list|(
name|stringScriptModeEntry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|stringScriptModeEntry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

