begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|regex
operator|.
name|Regex
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
name|util
operator|.
name|set
operator|.
name|Sets
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
name|Collections
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
name|HashSet
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
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * A basic setting service that can be used for per-index and per-cluster settings.  * This service offers transactional application of updates settings.  */
end_comment

begin_class
DECL|class|AbstractScopedSettings
specifier|public
specifier|abstract
class|class
name|AbstractScopedSettings
extends|extends
name|AbstractComponent
block|{
DECL|field|lastSettingsApplied
specifier|private
name|Settings
name|lastSettingsApplied
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
DECL|field|settingUpdaters
specifier|private
specifier|final
name|List
argument_list|<
name|SettingUpdater
argument_list|>
name|settingUpdaters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|complexMatchers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|complexMatchers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|keySettings
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|keySettings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|Setting
operator|.
name|Scope
name|scope
decl_stmt|;
DECL|method|AbstractScopedSettings
specifier|protected
name|AbstractScopedSettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Set
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|settingsSet
parameter_list|,
name|Setting
operator|.
name|Scope
name|scope
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastSettingsApplied
operator|=
name|Settings
operator|.
name|EMPTY
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
for|for
control|(
name|Setting
argument_list|<
name|?
argument_list|>
name|entry
range|:
name|settingsSet
control|)
block|{
name|addSetting
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|AbstractScopedSettings
specifier|protected
name|AbstractScopedSettings
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|,
name|Settings
name|scopeSettings
parameter_list|,
name|AbstractScopedSettings
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastSettingsApplied
operator|=
name|scopeSettings
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|other
operator|.
name|scope
expr_stmt|;
name|complexMatchers
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|complexMatchers
argument_list|)
expr_stmt|;
name|keySettings
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|keySettings
argument_list|)
expr_stmt|;
name|settingUpdaters
operator|.
name|addAll
argument_list|(
name|other
operator|.
name|settingUpdaters
argument_list|)
expr_stmt|;
block|}
DECL|method|addSetting
specifier|protected
specifier|final
name|void
name|addSetting
parameter_list|(
name|Setting
argument_list|<
name|?
argument_list|>
name|setting
parameter_list|)
block|{
if|if
condition|(
name|setting
operator|.
name|getScope
argument_list|()
operator|!=
name|scope
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Setting must be a "
operator|+
name|scope
operator|+
literal|" setting but was: "
operator|+
name|setting
operator|.
name|getScope
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|setting
operator|.
name|hasComplexMatcher
argument_list|()
condition|)
block|{
name|complexMatchers
operator|.
name|putIfAbsent
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|,
name|setting
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|keySettings
operator|.
name|putIfAbsent
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|,
name|setting
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getScope
specifier|public
name|Setting
operator|.
name|Scope
name|getScope
parameter_list|()
block|{
return|return
name|this
operator|.
name|scope
return|;
block|}
comment|/**      * Applies the given settings to all listeners and rolls back the result after application. This      * method will not change any settings but will fail if any of the settings can't be applied.      */
DECL|method|dryRun
specifier|public
specifier|synchronized
name|Settings
name|dryRun
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|Settings
name|current
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|previous
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|this
operator|.
name|lastSettingsApplied
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RuntimeException
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SettingUpdater
name|settingUpdater
range|:
name|settingUpdaters
control|)
block|{
try|try
block|{
if|if
condition|(
name|settingUpdater
operator|.
name|hasChanged
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
condition|)
block|{
name|settingUpdater
operator|.
name|getValue
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to prepareCommit settings for [{}]"
argument_list|,
name|ex
argument_list|,
name|settingUpdater
argument_list|)
expr_stmt|;
block|}
block|}
comment|// here we are exhaustive and record all settings that failed.
name|ExceptionsHelper
operator|.
name|rethrowAndSuppress
argument_list|(
name|exceptions
argument_list|)
expr_stmt|;
return|return
name|current
return|;
block|}
comment|/**      * Applies the given settings to all the settings consumers or to none of them. The settings      * will be merged with the node settings before they are applied while given settings override existing node      * settings.      * @param newSettings the settings to apply      * @return the unmerged applied settings     */
DECL|method|applySettings
specifier|public
specifier|synchronized
name|Settings
name|applySettings
parameter_list|(
name|Settings
name|newSettings
parameter_list|)
block|{
if|if
condition|(
name|lastSettingsApplied
operator|!=
literal|null
operator|&&
name|newSettings
operator|.
name|equals
argument_list|(
name|lastSettingsApplied
argument_list|)
condition|)
block|{
comment|// nothing changed in the settings, ignore
return|return
name|newSettings
return|;
block|}
specifier|final
name|Settings
name|current
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|newSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|previous
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|this
operator|.
name|lastSettingsApplied
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|Runnable
argument_list|>
name|applyRunnables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SettingUpdater
name|settingUpdater
range|:
name|settingUpdaters
control|)
block|{
try|try
block|{
name|applyRunnables
operator|.
name|add
argument_list|(
name|settingUpdater
operator|.
name|updater
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to prepareCommit settings for [{}]"
argument_list|,
name|ex
argument_list|,
name|settingUpdater
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
for|for
control|(
name|Runnable
name|settingUpdater
range|:
name|applyRunnables
control|)
block|{
name|settingUpdater
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to apply settings"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{         }
return|return
name|lastSettingsApplied
operator|=
name|newSettings
return|;
block|}
comment|/**      * Adds a settings consumer with a predicate that is only evaluated at update time.      *<p>      * Note: Only settings registered in {@link SettingsModule} can be changed dynamically.      *</p>      * @param validator an additional validator that is only applied to updates of this setting.      *                  This is useful to add additional validation to settings at runtime compared to at startup time.      */
DECL|method|addSettingsUpdateConsumer
specifier|public
specifier|synchronized
parameter_list|<
name|T
parameter_list|>
name|void
name|addSettingsUpdateConsumer
parameter_list|(
name|Setting
argument_list|<
name|T
argument_list|>
name|setting
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|validator
parameter_list|)
block|{
if|if
condition|(
name|setting
operator|!=
name|get
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Setting is not registered for key ["
operator|+
name|setting
operator|.
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|settingUpdaters
operator|.
name|add
argument_list|(
name|setting
operator|.
name|newUpdater
argument_list|(
name|consumer
argument_list|,
name|logger
argument_list|,
name|validator
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a settings consumer that accepts the values for two settings. The consumer if only notified if one or both settings change.      *<p>      * Note: Only settings registered in {@link SettingsModule} can be changed dynamically.      *</p>      * This method registers a compound updater that is useful if two settings are depending on each other. The consumer is always provided      * with both values even if only one of the two changes.      */
DECL|method|addSettingsUpdateConsumer
specifier|public
specifier|synchronized
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
name|void
name|addSettingsUpdateConsumer
parameter_list|(
name|Setting
argument_list|<
name|A
argument_list|>
name|a
parameter_list|,
name|Setting
argument_list|<
name|B
argument_list|>
name|b
parameter_list|,
name|BiConsumer
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|consumer
parameter_list|)
block|{
if|if
condition|(
name|a
operator|!=
name|get
argument_list|(
name|a
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Setting is not registered for key ["
operator|+
name|a
operator|.
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|b
operator|!=
name|get
argument_list|(
name|b
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Setting is not registered for key ["
operator|+
name|b
operator|.
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|settingUpdaters
operator|.
name|add
argument_list|(
name|Setting
operator|.
name|compoundUpdater
argument_list|(
name|consumer
argument_list|,
name|a
argument_list|,
name|b
argument_list|,
name|logger
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a settings consumer.      *<p>      * Note: Only settings registered in {@link org.elasticsearch.cluster.ClusterModule} can be changed dynamically.      *</p>      */
DECL|method|addSettingsUpdateConsumer
specifier|public
specifier|synchronized
parameter_list|<
name|T
parameter_list|>
name|void
name|addSettingsUpdateConsumer
parameter_list|(
name|Setting
argument_list|<
name|T
argument_list|>
name|setting
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|)
block|{
name|addSettingsUpdateConsumer
argument_list|(
name|setting
argument_list|,
name|consumer
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Validates that all settings in the builder are registered and valid      */
DECL|method|validate
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
name|Settings
operator|.
name|Builder
name|settingsBuilder
parameter_list|)
block|{
name|validate
argument_list|(
name|settingsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * * Validates that all given settings are registered and valid      */
DECL|method|validate
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
name|Settings
name|settings
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
name|String
argument_list|>
name|entry
range|:
name|settings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|validate
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Validates that the setting is valid      */
DECL|method|validate
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
name|String
name|key
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|Setting
name|setting
init|=
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|setting
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown setting ["
operator|+
name|key
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|setting
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Transactional interface to update settings.      * @see Setting      */
DECL|interface|SettingUpdater
specifier|public
interface|interface
name|SettingUpdater
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**          * Returns true if this updaters setting has changed with the current update          * @param current the current settings          * @param previous the previous setting          * @return true if this updaters setting has changed with the current update          */
DECL|method|hasChanged
name|boolean
name|hasChanged
parameter_list|(
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
function_decl|;
comment|/**          * Returns the instance value for the current settings. This method is stateless and idempotent.          * This method will throw an exception if the source of this value is invalid.          */
DECL|method|getValue
name|T
name|getValue
parameter_list|(
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
function_decl|;
comment|/**          * Applies the given value to the updater. This methods will actually run the update.          */
DECL|method|apply
name|void
name|apply
parameter_list|(
name|T
name|value
parameter_list|,
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
function_decl|;
comment|/**          * Updates this updaters value if it has changed.          * @return<code>true</code> iff the value has been updated.          */
DECL|method|apply
specifier|default
name|boolean
name|apply
parameter_list|(
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
block|{
if|if
condition|(
name|hasChanged
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
condition|)
block|{
name|T
name|value
init|=
name|getValue
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|value
argument_list|,
name|current
argument_list|,
name|previous
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**          * Returns a callable runnable that calls {@link #apply(Object, Settings, Settings)} if the settings          * actually changed. This allows to defer the update to a later point in time while keeping type safety.          * If the value didn't change the returned runnable is a noop.          */
DECL|method|updater
specifier|default
name|Runnable
name|updater
parameter_list|(
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
block|{
if|if
condition|(
name|hasChanged
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
condition|)
block|{
name|T
name|value
init|=
name|getValue
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
decl_stmt|;
return|return
parameter_list|()
lambda|->
block|{
name|apply
argument_list|(
name|value
argument_list|,
name|current
argument_list|,
name|previous
argument_list|)
expr_stmt|;
block|}
return|;
block|}
return|return
parameter_list|()
lambda|->
block|{}
return|;
block|}
block|}
comment|/**      * Returns the {@link Setting} for the given key or<code>null</code> if the setting can not be found.      */
DECL|method|get
specifier|public
name|Setting
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Setting
argument_list|<
name|?
argument_list|>
name|setting
init|=
name|keySettings
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|setting
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|entry
range|:
name|complexMatchers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|match
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
block|}
else|else
block|{
return|return
name|setting
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns<code>true</code> if the setting for the given key is dynamically updateable. Otherwise<code>false</code>.      */
DECL|method|hasDynamicSetting
specifier|public
name|boolean
name|hasDynamicSetting
parameter_list|(
name|String
name|key
parameter_list|)
block|{
specifier|final
name|Setting
name|setting
init|=
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|isDynamic
argument_list|()
return|;
block|}
comment|/**      * Returns a settings object that contains all settings that are not      * already set in the given source. The diff contains either the default value for each      * setting or the settings value in the given default settings.      */
DECL|method|diff
specifier|public
name|Settings
name|diff
parameter_list|(
name|Settings
name|source
parameter_list|,
name|Settings
name|defaultSettings
parameter_list|)
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Setting
argument_list|<
name|?
argument_list|>
name|setting
range|:
name|keySettings
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|setting
operator|.
name|exists
argument_list|(
name|source
argument_list|)
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|,
name|setting
operator|.
name|getRaw
argument_list|(
name|defaultSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Returns the value for the given setting.      */
DECL|method|get
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|Setting
argument_list|<
name|T
argument_list|>
name|setting
parameter_list|)
block|{
if|if
condition|(
name|setting
operator|.
name|getScope
argument_list|()
operator|!=
name|scope
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"settings scope doesn't match the setting scope ["
operator|+
name|this
operator|.
name|scope
operator|+
literal|"] != ["
operator|+
name|setting
operator|.
name|getScope
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|get
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"setting "
operator|+
name|setting
operator|.
name|getKey
argument_list|()
operator|+
literal|" has not been registered"
argument_list|)
throw|;
block|}
return|return
name|setting
operator|.
name|get
argument_list|(
name|this
operator|.
name|lastSettingsApplied
argument_list|,
name|settings
argument_list|)
return|;
block|}
comment|/**      * Updates a target settings builder with new, updated or deleted settings from a given settings builder.      *<p>      * Note: This method will only allow updates to dynamic settings. if a non-dynamic setting is updated an {@link IllegalArgumentException} is thrown instead.      *</p>      * @param toApply the new settings to apply      * @param target the target settings builder that the updates are applied to. All keys that have explicit null value in toApply will be removed from this builder      * @param updates a settings builder that holds all updates applied to target      * @param type a free text string to allow better exceptions messages      * @return<code>true</code> if the target has changed otherwise<code>false</code>      */
DECL|method|updateDynamicSettings
specifier|public
name|boolean
name|updateDynamicSettings
parameter_list|(
name|Settings
name|toApply
parameter_list|,
name|Settings
operator|.
name|Builder
name|target
parameter_list|,
name|Settings
operator|.
name|Builder
name|updates
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
name|updateSettings
argument_list|(
name|toApply
argument_list|,
name|target
argument_list|,
name|updates
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Updates a target settings builder with new, updated or deleted settings from a given settings builder.      * @param toApply the new settings to apply      * @param target the target settings builder that the updates are applied to. All keys that have explicit null value in toApply will be removed from this builder      * @param updates a settings builder that holds all updates applied to target      * @param type a free text string to allow better exceptions messages      * @return<code>true</code> if the target has changed otherwise<code>false</code>      */
DECL|method|updateSettings
specifier|public
name|boolean
name|updateSettings
parameter_list|(
name|Settings
name|toApply
parameter_list|,
name|Settings
operator|.
name|Builder
name|target
parameter_list|,
name|Settings
operator|.
name|Builder
name|updates
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
name|updateSettings
argument_list|(
name|toApply
argument_list|,
name|target
argument_list|,
name|updates
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Updates a target settings builder with new, updated or deleted settings from a given settings builder.      * @param toApply the new settings to apply      * @param target the target settings builder that the updates are applied to. All keys that have explicit null value in toApply will be removed from this builder      * @param updates a settings builder that holds all updates applied to target      * @param type a free text string to allow better exceptions messages      * @param onlyDynamic  if<code>false</code> all settings are updated otherwise only dynamic settings are updated. if set to<code>true</code> and a non-dynamic setting is updated an exception is thrown.      * @return<code>true</code> if the target has changed otherwise<code>false</code>      */
DECL|method|updateSettings
specifier|private
name|boolean
name|updateSettings
parameter_list|(
name|Settings
name|toApply
parameter_list|,
name|Settings
operator|.
name|Builder
name|target
parameter_list|,
name|Settings
operator|.
name|Builder
name|updates
parameter_list|,
name|String
name|type
parameter_list|,
name|boolean
name|onlyDynamic
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|toRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsBuilder
init|=
name|Settings
operator|.
name|settingsBuilder
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
name|String
argument_list|>
name|entry
range|:
name|toApply
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|toRemove
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|onlyDynamic
operator|==
literal|false
operator|&&
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|!=
literal|null
operator|)
operator|||
name|hasDynamicSetting
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|validate
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|toApply
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|updates
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|type
operator|+
literal|" setting ["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"], not dynamically updateable"
argument_list|)
throw|;
block|}
block|}
name|changed
operator||=
name|applyDeletes
argument_list|(
name|toRemove
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|target
operator|.
name|put
argument_list|(
name|settingsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|changed
return|;
block|}
DECL|method|applyDeletes
specifier|private
specifier|static
specifier|final
name|boolean
name|applyDeletes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|deletes
parameter_list|,
name|Settings
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|deletes
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keysToRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
init|=
name|builder
operator|.
name|internalMap
argument_list|()
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keySet
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|entry
argument_list|,
name|key
argument_list|)
condition|)
block|{
name|keysToRemove
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|key
range|:
name|keysToRemove
control|)
block|{
name|builder
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|changed
return|;
block|}
block|}
end_class

end_unit

