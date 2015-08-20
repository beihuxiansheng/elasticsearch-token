begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

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
name|Binder
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
name|multibindings
operator|.
name|MapBinder
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
name|multibindings
operator|.
name|Multibinder
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This class defines an official elasticsearch extension point. It registers  * all extensions by a single name and ensures that extensions are not registered  * more than once.  */
end_comment

begin_class
DECL|class|ExtensionPoint
specifier|public
specifier|abstract
class|class
name|ExtensionPoint
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|singletons
specifier|protected
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|singletons
decl_stmt|;
comment|/**      * Creates a new extension point      *      * @param name           the human readable underscore case name of the extension point. This is used in error messages etc.      * @param singletons     a list of singletons to bind with this extension point - these are bound in {@link #bind(Binder)}      */
DECL|method|ExtensionPoint
specifier|public
name|ExtensionPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|singletons
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|singletons
operator|=
name|singletons
expr_stmt|;
block|}
comment|/**      * Binds the extension as well as the singletons to the given guice binder.      *      * @param binder the binder to use      */
DECL|method|bind
specifier|public
specifier|final
name|void
name|bind
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
range|:
name|singletons
control|)
block|{
name|binder
operator|.
name|bind
argument_list|(
name|c
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
name|bindExtensions
argument_list|(
name|binder
argument_list|)
expr_stmt|;
block|}
comment|/**      * Subclasses can bind their type, map or set extensions here.      */
DECL|method|bindExtensions
specifier|protected
specifier|abstract
name|void
name|bindExtensions
parameter_list|(
name|Binder
name|binder
parameter_list|)
function_decl|;
comment|/**      * A map based extension point which allows to register keyed implementations ie. parsers or some kind of strategies.      */
DECL|class|ClassMap
specifier|public
specifier|static
class|class
name|ClassMap
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ExtensionPoint
block|{
DECL|field|extensionClass
specifier|protected
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|extensionClass
decl_stmt|;
DECL|field|extensions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|extensions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reservedKeys
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|reservedKeys
decl_stmt|;
comment|/**          * Creates a new {@link ClassMap}          *          * @param name           the human readable underscore case name of the extension poing. This is used in error messages etc.          * @param extensionClass the base class that should be extended          * @param singletons     a list of singletons to bind with this extension point - these are bound in {@link #bind(Binder)}          * @param reservedKeys   a set of reserved keys by internal implementations          */
DECL|method|ClassMap
specifier|public
name|ClassMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|extensionClass
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|reservedKeys
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|singletons
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|singletons
argument_list|)
expr_stmt|;
name|this
operator|.
name|extensionClass
operator|=
name|extensionClass
expr_stmt|;
name|this
operator|.
name|reservedKeys
operator|=
name|reservedKeys
expr_stmt|;
block|}
comment|/**          * Returns the extension for the given key or<code>null</code>          */
DECL|method|getExtension
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|getExtension
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|extensions
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**          * Registers an extension class for a given key. This method will thr          *          * @param key       the extensions key          * @param extension the extension          * @throws IllegalArgumentException iff the key is already registered or if the key is a reserved key for an internal implementation          */
DECL|method|registerExtension
specifier|public
specifier|final
name|void
name|registerExtension
parameter_list|(
name|String
name|key
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|extension
parameter_list|)
block|{
if|if
condition|(
name|extensions
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|||
name|reservedKeys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't register the same ["
operator|+
name|this
operator|.
name|name
operator|+
literal|"] more than once for ["
operator|+
name|key
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|extensions
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|extension
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bindExtensions
specifier|protected
specifier|final
name|void
name|bindExtensions
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|MapBinder
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|parserMapBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|extensionClass
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
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|clazz
range|:
name|extensions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|parserMapBinder
operator|.
name|addBinding
argument_list|(
name|clazz
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|to
argument_list|(
name|clazz
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * A Type extension point which basically allows to registerd keyed extensions like {@link ClassMap}      * but doesn't instantiate and bind all the registered key value pairs but instead replace a singleton based on a given setting via {@link #bindType(Binder, Settings, String, String)}      * Note: {@link #bind(Binder)} is not supported by this class      */
DECL|class|SelectedType
specifier|public
specifier|static
specifier|final
class|class
name|SelectedType
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ClassMap
argument_list|<
name|T
argument_list|>
block|{
DECL|method|SelectedType
specifier|public
name|SelectedType
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|extensionClass
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|extensionClass
argument_list|,
name|Collections
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
block|}
comment|/**          * Binds the extension class to the class that is registered for the give configured for the settings key in          * the settings object.          *          * @param binder       the binder to use          * @param settings     the settings to look up the key to find the implementation to bind          * @param settingsKey  the key to use with the settings          * @param defaultValue the default value if the settings do not contain the key, or null if there is no default          * @return the actual bound type key          */
DECL|method|bindType
specifier|public
name|String
name|bindType
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|settingsKey
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
specifier|final
name|String
name|type
init|=
name|settings
operator|.
name|get
argument_list|(
name|settingsKey
argument_list|,
name|defaultValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing setting ["
operator|+
name|settingsKey
operator|+
literal|"]"
argument_list|)
throw|;
block|}
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|instance
init|=
name|getExtension
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown ["
operator|+
name|this
operator|.
name|name
operator|+
literal|"] type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|extensionClass
operator|==
name|instance
condition|)
block|{
name|binder
operator|.
name|bind
argument_list|(
name|extensionClass
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|binder
operator|.
name|bind
argument_list|(
name|extensionClass
argument_list|)
operator|.
name|to
argument_list|(
name|instance
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
block|}
comment|/**      * A set based extension point which allows to register extended classes that might be used to chain additional functionality etc.      */
DECL|class|ClassSet
specifier|public
specifier|final
specifier|static
class|class
name|ClassSet
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ExtensionPoint
block|{
DECL|field|extensionClass
specifier|protected
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|extensionClass
decl_stmt|;
DECL|field|extensions
specifier|private
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|extensions
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**          * Creates a new {@link ClassSet}          *          * @param name           the human readable underscore case name of the extension poing. This is used in error messages etc.          * @param extensionClass the base class that should be extended          * @param singletons     a list of singletons to bind with this extension point - these are bound in {@link #bind(Binder)}          */
DECL|method|ClassSet
specifier|public
name|ClassSet
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|extensionClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|singletons
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|singletons
argument_list|)
expr_stmt|;
name|this
operator|.
name|extensionClass
operator|=
name|extensionClass
expr_stmt|;
block|}
comment|/**          * Registers a new extension          *          * @param extension the extension to register          * @throws IllegalArgumentException iff the class is already registered          */
DECL|method|registerExtension
specifier|public
specifier|final
name|void
name|registerExtension
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|extension
parameter_list|)
block|{
if|if
condition|(
name|extensions
operator|.
name|contains
argument_list|(
name|extension
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't register the same ["
operator|+
name|this
operator|.
name|name
operator|+
literal|"] more than once for ["
operator|+
name|extension
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|extensions
operator|.
name|add
argument_list|(
name|extension
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bindExtensions
specifier|protected
specifier|final
name|void
name|bindExtensions
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|Multibinder
argument_list|<
name|T
argument_list|>
name|allocationMultibinder
init|=
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|,
name|extensionClass
argument_list|)
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
range|:
name|extensions
control|)
block|{
name|allocationMultibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|clazz
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * A an instance of a map, mapping one instance value to another. Both key and value are instances, not classes      * like with other extension points.      */
DECL|class|InstanceMap
specifier|public
specifier|final
specifier|static
class|class
name|InstanceMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|ExtensionPoint
block|{
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|keyType
specifier|private
specifier|final
name|Class
argument_list|<
name|K
argument_list|>
name|keyType
decl_stmt|;
DECL|field|valueType
specifier|private
specifier|final
name|Class
argument_list|<
name|V
argument_list|>
name|valueType
decl_stmt|;
comment|/**          * Creates a new {@link ClassSet}          *          * @param name           the human readable underscore case name of the extension point. This is used in error messages.          * @param singletons     a list of singletons to bind with this extension point - these are bound in {@link #bind(Binder)}          */
DECL|method|InstanceMap
specifier|public
name|InstanceMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|K
argument_list|>
name|keyType
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueType
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|singletons
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|singletons
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyType
operator|=
name|keyType
expr_stmt|;
name|this
operator|.
name|valueType
operator|=
name|valueType
expr_stmt|;
block|}
comment|/**          * Registers a mapping from {@param key} to {@param value}          *          * @throws IllegalArgumentException iff the key is already registered          */
DECL|method|registerExtension
specifier|public
specifier|final
name|void
name|registerExtension
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|V
name|old
init|=
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot register ["
operator|+
name|this
operator|.
name|name
operator|+
literal|"] with key ["
operator|+
name|key
operator|+
literal|"] to ["
operator|+
name|value
operator|+
literal|"], already registered to ["
operator|+
name|old
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|bindExtensions
specifier|protected
name|void
name|bindExtensions
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|MapBinder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|mapBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|,
name|keyType
argument_list|,
name|valueType
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|mapBinder
operator|.
name|addBinding
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|toInstance
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
