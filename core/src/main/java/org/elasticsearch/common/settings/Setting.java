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
name|ElasticsearchParseException
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
name|ToXContentToBytes
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
name|Booleans
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
name|ESLogger
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
name|unit
operator|.
name|ByteSizeValue
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
name|MemorySizeValue
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
name|*
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|Predicate
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|Setting
specifier|public
class|class
name|Setting
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ToXContentToBytes
block|{
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|defaultValue
specifier|private
specifier|final
name|Function
argument_list|<
name|Settings
argument_list|,
name|String
argument_list|>
name|defaultValue
decl_stmt|;
DECL|field|parser
specifier|private
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|parser
decl_stmt|;
DECL|field|dynamic
specifier|private
specifier|final
name|boolean
name|dynamic
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|Scope
name|scope
decl_stmt|;
DECL|method|Setting
specifier|public
name|Setting
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|description
parameter_list|,
name|Function
argument_list|<
name|Settings
argument_list|,
name|String
argument_list|>
name|defaultValue
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|parser
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|dynamic
operator|=
name|dynamic
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
comment|/**      * Returns the settings key or a prefix if this setting is a group setting      * @see #isGroupSetting()      */
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/**      * Returns a human readable description of this setting      */
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**      * Returns<code>true</code> iff this setting is dynamically updateable, otherwise<code>false</code>      */
DECL|method|isDynamic
specifier|public
name|boolean
name|isDynamic
parameter_list|()
block|{
return|return
name|dynamic
return|;
block|}
comment|/**      * Returns the settings scope      */
DECL|method|getScope
specifier|public
name|Scope
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
comment|/**      * Returns<code>true</code> iff this setting is a group setting. Group settings represent a set of settings      * rather than a single value. The key, see {@link #getKey()}, in contrast to non-group settings is a prefix like<tt>cluster.store.</tt>      * that matches all settings with this prefix.      */
DECL|method|isGroupSetting
specifier|public
name|boolean
name|isGroupSetting
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns the default values string representation for this setting.      * @param settings a settings object for settings that has a default value depending on another setting if available      */
DECL|method|getDefault
specifier|public
name|String
name|getDefault
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|defaultValue
operator|.
name|apply
argument_list|(
name|settings
argument_list|)
return|;
block|}
comment|/**      * Returns<code>true</code> iff this setting is present in the given settings object. Otherwise<code>false</code>      */
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|settings
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the settings value. If the setting is not present in the given settings object the default value is returned      * instead.      */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|value
init|=
name|getRaw
argument_list|(
name|settings
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|parser
operator|.
name|apply
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse value ["
operator|+
name|value
operator|+
literal|"] for setting ["
operator|+
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the raw (string) settings value. If the setting is not present in the given settings object the default value is returned      * instead. This is useful if the value can't be parsed due to an invalid value to access the actual value.      */
DECL|method|getRaw
specifier|public
name|String
name|getRaw
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|settings
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|defaultValue
operator|.
name|apply
argument_list|(
name|settings
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns<code>true</code> iff the given key matches the settings key or if this setting is a group setting if the      * given key is part of the settings group.      * @see #isGroupSetting()      */
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|toTest
parameter_list|)
block|{
return|return
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|key
argument_list|,
name|toTest
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"key"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"description"
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|scope
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"dynamic"
argument_list|,
name|dynamic
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"default"
argument_list|,
name|defaultValue
operator|.
name|apply
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * The settings scope - settings can either be cluster settings or per index settings.      */
DECL|enum|Scope
specifier|public
enum|enum
name|Scope
block|{
DECL|enum constant|Cluster
name|Cluster
block|,
DECL|enum constant|Index
name|Index
block|;     }
DECL|method|newUpdater
name|SettingsService
operator|.
name|SettingUpdater
name|newUpdater
parameter_list|(
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|newUpdater
argument_list|(
name|consumer
argument_list|,
name|logger
argument_list|,
name|settings
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
literal|true
argument_list|)
return|;
block|}
DECL|method|newUpdater
name|SettingsService
operator|.
name|SettingUpdater
name|newUpdater
parameter_list|(
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Predicate
argument_list|<
name|T
argument_list|>
name|accept
parameter_list|)
block|{
if|if
condition|(
name|isDynamic
argument_list|()
condition|)
block|{
return|return
operator|new
name|Updater
argument_list|(
name|consumer
argument_list|,
name|logger
argument_list|,
name|settings
argument_list|,
name|accept
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"setting ["
operator|+
name|getKey
argument_list|()
operator|+
literal|"] is not dynamic"
argument_list|)
throw|;
block|}
block|}
DECL|method|compoundUpdater
specifier|static
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
name|SettingsService
operator|.
name|SettingUpdater
name|compoundUpdater
parameter_list|(
specifier|final
name|BiConsumer
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|consumer
parameter_list|,
specifier|final
name|Setting
argument_list|<
name|A
argument_list|>
name|aSettting
parameter_list|,
specifier|final
name|Setting
argument_list|<
name|B
argument_list|>
name|bSetting
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|AtomicReference
argument_list|<
name|A
argument_list|>
name|aRef
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|B
argument_list|>
name|bRef
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SettingsService
operator|.
name|SettingUpdater
name|aSettingUpdater
init|=
name|aSettting
operator|.
name|newUpdater
argument_list|(
name|aRef
operator|::
name|set
argument_list|,
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|SettingsService
operator|.
name|SettingUpdater
name|bSettingUpdater
init|=
name|bSetting
operator|.
name|newUpdater
argument_list|(
name|bRef
operator|::
name|set
argument_list|,
name|logger
argument_list|,
name|settings
argument_list|)
decl_stmt|;
return|return
operator|new
name|SettingsService
operator|.
name|SettingUpdater
argument_list|()
block|{
name|boolean
name|aHasChanged
init|=
literal|false
decl_stmt|;
name|boolean
name|bHasChanged
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|prepareApply
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|aHasChanged
operator|=
name|aSettingUpdater
operator|.
name|prepareApply
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|bHasChanged
operator|=
name|bSettingUpdater
operator|.
name|prepareApply
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|aHasChanged
operator|||
name|bHasChanged
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|()
block|{
name|aSettingUpdater
operator|.
name|apply
argument_list|()
expr_stmt|;
name|bSettingUpdater
operator|.
name|apply
argument_list|()
expr_stmt|;
if|if
condition|(
name|aHasChanged
operator|||
name|bHasChanged
condition|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|aRef
operator|.
name|get
argument_list|()
argument_list|,
name|bRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
block|{
try|try
block|{
name|aRef
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|aSettingUpdater
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|bRef
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|bSettingUpdater
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CompoundUpdater for: "
operator|+
name|aSettingUpdater
operator|+
literal|" and "
operator|+
name|bSettingUpdater
return|;
block|}
block|}
return|;
block|}
DECL|class|Updater
specifier|private
class|class
name|Updater
implements|implements
name|SettingsService
operator|.
name|SettingUpdater
block|{
DECL|field|consumer
specifier|private
specifier|final
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|accept
specifier|private
specifier|final
name|Predicate
argument_list|<
name|T
argument_list|>
name|accept
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|field|commitPending
specifier|private
name|boolean
name|commitPending
decl_stmt|;
DECL|field|pendingValue
specifier|private
name|String
name|pendingValue
decl_stmt|;
DECL|field|valueInstance
specifier|private
name|T
name|valueInstance
decl_stmt|;
DECL|method|Updater
specifier|public
name|Updater
parameter_list|(
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Predicate
argument_list|<
name|T
argument_list|>
name|accept
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|value
operator|=
name|getRaw
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|accept
operator|=
name|accept
expr_stmt|;
block|}
DECL|method|prepareApply
specifier|public
name|boolean
name|prepareApply
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|newValue
init|=
name|settings
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|newValue
operator|==
literal|null
condition|)
block|{
name|newValue
operator|=
name|getRaw
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|newValue
argument_list|)
operator|==
literal|false
condition|)
block|{
name|T
name|inst
init|=
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|accept
operator|.
name|test
argument_list|(
name|inst
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal value can't update ["
operator|+
name|key
operator|+
literal|"] from ["
operator|+
name|value
operator|+
literal|"] to ["
operator|+
name|getRaw
argument_list|(
name|settings
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|pendingValue
operator|=
name|newValue
expr_stmt|;
name|valueInstance
operator|=
name|inst
expr_stmt|;
name|commitPending
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|commitPending
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|commitPending
return|;
block|}
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|()
block|{
if|if
condition|(
name|commitPending
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"update [{}] from [{}] to [{}]"
argument_list|,
name|key
argument_list|,
name|value
argument_list|,
name|pendingValue
argument_list|)
expr_stmt|;
name|value
operator|=
name|pendingValue
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|valueInstance
argument_list|)
expr_stmt|;
block|}
name|commitPending
operator|=
literal|false
expr_stmt|;
name|valueInstance
operator|=
literal|null
expr_stmt|;
name|pendingValue
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
block|{
name|commitPending
operator|=
literal|false
expr_stmt|;
name|valueInstance
operator|=
literal|null
expr_stmt|;
name|pendingValue
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Updater for: "
operator|+
name|Setting
operator|.
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|Setting
specifier|public
name|Setting
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|description
parameter_list|,
name|String
name|defaultValue
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|parser
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
name|description
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|defaultValue
argument_list|,
name|parser
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
DECL|method|floatSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|Float
argument_list|>
name|floatSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|float
name|defaultValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|Float
operator|.
name|toString
argument_list|(
name|defaultValue
argument_list|)
argument_list|,
name|Float
operator|::
name|parseFloat
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
DECL|method|floatSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|Float
argument_list|>
name|floatSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|float
name|defaultValue
parameter_list|,
name|float
name|minValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|Float
operator|.
name|toString
argument_list|(
name|defaultValue
argument_list|)
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{
name|float
name|value
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|s
argument_list|)
argument_list|;             if
operator|(
name|value
operator|<
name|minValue
operator|)
block|{
throw|throw
argument_list|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse value ["
operator|+
name|s
operator|+
literal|"] for setting ["
operator|+
name|key
operator|+
literal|"] must be>= "
operator|+
name|minValue
argument_list|)
block|;             }
return|return
name|value
return|;
block|}
operator|,
name|dynamic
operator|,
name|scope
block|)
class|;
end_class

begin_function
unit|}      public
DECL|method|intSetting
specifier|static
name|Setting
argument_list|<
name|Integer
argument_list|>
name|intSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|defaultValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|Integer
operator|.
name|toString
argument_list|(
name|defaultValue
argument_list|)
argument_list|,
name|Integer
operator|::
name|parseInt
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|boolSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|boolSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|Boolean
operator|.
name|toString
argument_list|(
name|defaultValue
argument_list|)
argument_list|,
name|Booleans
operator|::
name|parseBooleanExact
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|byteSizeSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|byteSizeSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|percentage
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|percentage
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|MemorySizeValue
operator|.
name|parseBytesSizeValueOrHeapRatio
argument_list|(
name|s
argument_list|,
name|key
argument_list|)
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|byteSizeSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|byteSizeSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteSizeValue
name|value
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|value
operator|.
name|toString
argument_list|()
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
name|s
argument_list|,
name|key
argument_list|)
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|positiveTimeSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|positiveTimeSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
name|timeSetting
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|groupSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|Settings
argument_list|>
name|groupSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|endsWith
argument_list|(
literal|"."
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key must end with a '.'"
argument_list|)
throw|;
block|}
return|return
operator|new
name|Setting
argument_list|<
name|Settings
argument_list|>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
literal|""
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
literal|null
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
block|{              @
name|Override
specifier|public
name|boolean
name|isGroupSetting
argument_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Settings
name|get
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|settings
operator|.
name|getByPrefix
argument_list|(
name|key
argument_list|)
return|;
block|}
expr|@
name|Override
specifier|public
name|boolean
name|match
argument_list|(
name|String
name|toTest
argument_list|)
block|{
return|return
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|key
operator|+
literal|"*"
argument_list|,
name|toTest
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SettingsService
operator|.
name|SettingUpdater
name|newUpdater
parameter_list|(
name|Consumer
argument_list|<
name|Settings
argument_list|>
name|consumer
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Predicate
argument_list|<
name|Settings
argument_list|>
name|accept
parameter_list|)
block|{
if|if
condition|(
name|isDynamic
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"setting ["
operator|+
name|getKey
argument_list|()
operator|+
literal|"] is not dynamic"
argument_list|)
throw|;
block|}
specifier|final
name|Setting
argument_list|<
name|?
argument_list|>
name|setting
init|=
name|this
decl_stmt|;
return|return
operator|new
name|SettingsService
operator|.
name|SettingUpdater
argument_list|()
block|{
specifier|private
name|Settings
name|pendingSettings
decl_stmt|;
specifier|private
name|Settings
name|committedSettings
init|=
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|prepareApply
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Settings
name|currentSettings
init|=
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentSettings
operator|.
name|equals
argument_list|(
name|committedSettings
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|accept
operator|.
name|test
argument_list|(
name|currentSettings
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal value can't update ["
operator|+
name|key
operator|+
literal|"] from ["
operator|+
name|committedSettings
operator|.
name|getAsMap
argument_list|()
operator|+
literal|"] to ["
operator|+
name|currentSettings
operator|.
name|getAsMap
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|pendingSettings
operator|=
name|currentSettings
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|()
block|{
if|if
condition|(
name|pendingSettings
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|pendingSettings
argument_list|)
expr_stmt|;
name|committedSettings
operator|=
name|pendingSettings
expr_stmt|;
block|}
name|pendingSettings
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
block|{
name|pendingSettings
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Updater for: "
operator|+
name|setting
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_function

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_function
unit|}      public
DECL|method|timeSetting
specifier|static
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|timeSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|Function
argument_list|<
name|Settings
argument_list|,
name|String
argument_list|>
name|defaultValue
parameter_list|,
name|TimeValue
name|minValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
name|defaultValue
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{
name|TimeValue
name|timeValue
init|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|s
argument_list|,
literal|null
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeValue
operator|.
name|millis
argument_list|()
operator|<
name|minValue
operator|.
name|millis
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse value ["
operator|+
name|s
operator|+
literal|"] for setting ["
operator|+
name|key
operator|+
literal|"] must be>= "
operator|+
name|minValue
argument_list|)
throw|;
block|}
return|return
name|timeValue
return|;
block|}
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|timeSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|timeSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|,
name|TimeValue
name|minValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
name|timeSetting
argument_list|(
name|key
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|defaultValue
operator|.
name|getStringRep
argument_list|()
argument_list|,
name|minValue
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|timeSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|timeSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|defaultValue
operator|.
name|toString
argument_list|()
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|s
argument_list|,
name|defaultValue
argument_list|,
name|key
argument_list|)
argument_list|,
name|dynamic
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|doubleSetting
specifier|public
specifier|static
name|Setting
argument_list|<
name|Double
argument_list|>
name|doubleSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|defaultValue
parameter_list|,
name|double
name|minValue
parameter_list|,
name|boolean
name|dynamic
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<>
argument_list|(
name|key
argument_list|,
literal|"_na_"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|Double
operator|.
name|toString
argument_list|(
name|defaultValue
argument_list|)
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{
name|final
name|double
name|d
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|s
argument_list|)
argument_list|;             if
operator|(
name|d
operator|<
name|minValue
operator|)
block|{
throw|throw
argument_list|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse value ["
operator|+
name|s
operator|+
literal|"] for setting ["
operator|+
name|key
operator|+
literal|"] must be>= "
operator|+
name|minValue
argument_list|)
block|;             }
return|return
name|d
return|;
block|}
end_function

begin_operator
operator|,
end_operator

begin_expr_stmt
name|dynamic
operator|,
name|scope
end_expr_stmt

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

unit|}  }
end_unit

