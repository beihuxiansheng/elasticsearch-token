begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
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
name|ImmutableSet
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
name|Lists
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
operator|.
name|*
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
name|spi
operator|.
name|InjectionPoint
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
name|spi
operator|.
name|TypeListenerBinding
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|InheritingState
class|class
name|InheritingState
implements|implements
name|State
block|{
DECL|field|parent
specifier|private
specifier|final
name|State
name|parent
decl_stmt|;
comment|// Must be a linked hashmap in order to preserve order of bindings in Modules.
DECL|field|explicitBindingsMutable
specifier|private
specifier|final
name|Map
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Binding
argument_list|<
name|?
argument_list|>
argument_list|>
name|explicitBindingsMutable
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
DECL|field|explicitBindings
specifier|private
specifier|final
name|Map
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Binding
argument_list|<
name|?
argument_list|>
argument_list|>
name|explicitBindings
init|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|explicitBindingsMutable
argument_list|)
decl_stmt|;
DECL|field|scopes
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
argument_list|,
name|Scope
argument_list|>
name|scopes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|converters
specifier|private
specifier|final
name|List
argument_list|<
name|MatcherAndConverter
argument_list|>
name|converters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|listenerBindings
specifier|private
specifier|final
name|List
argument_list|<
name|TypeListenerBinding
argument_list|>
name|listenerBindings
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|blacklistedKeys
specifier|private
name|WeakKeySet
name|blacklistedKeys
init|=
operator|new
name|WeakKeySet
argument_list|()
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
decl_stmt|;
DECL|method|InheritingState
name|InheritingState
parameter_list|(
name|State
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|checkNotNull
argument_list|(
name|parent
argument_list|,
literal|"parent"
argument_list|)
expr_stmt|;
name|this
operator|.
name|lock
operator|=
operator|(
name|parent
operator|==
name|State
operator|.
name|NONE
operator|)
condition|?
name|this
else|:
name|parent
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
DECL|method|parent
specifier|public
name|State
name|parent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// we only put in BindingImpls that match their key types
DECL|method|getExplicitBinding
specifier|public
parameter_list|<
name|T
parameter_list|>
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|getExplicitBinding
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
name|Binding
argument_list|<
name|?
argument_list|>
name|binding
init|=
name|explicitBindings
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|binding
operator|!=
literal|null
condition|?
operator|(
name|BindingImpl
argument_list|<
name|T
argument_list|>
operator|)
name|binding
else|:
name|parent
operator|.
name|getExplicitBinding
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getExplicitBindingsThisLevel
specifier|public
name|Map
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Binding
argument_list|<
name|?
argument_list|>
argument_list|>
name|getExplicitBindingsThisLevel
parameter_list|()
block|{
return|return
name|explicitBindings
return|;
block|}
DECL|method|putBinding
specifier|public
name|void
name|putBinding
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|BindingImpl
argument_list|<
name|?
argument_list|>
name|binding
parameter_list|)
block|{
name|explicitBindingsMutable
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|binding
argument_list|)
expr_stmt|;
block|}
DECL|method|getScope
specifier|public
name|Scope
name|getScope
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|)
block|{
name|Scope
name|scope
init|=
name|scopes
operator|.
name|get
argument_list|(
name|annotationType
argument_list|)
decl_stmt|;
return|return
name|scope
operator|!=
literal|null
condition|?
name|scope
else|:
name|parent
operator|.
name|getScope
argument_list|(
name|annotationType
argument_list|)
return|;
block|}
DECL|method|putAnnotation
specifier|public
name|void
name|putAnnotation
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|,
name|Scope
name|scope
parameter_list|)
block|{
name|scopes
operator|.
name|put
argument_list|(
name|annotationType
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
DECL|method|getConvertersThisLevel
specifier|public
name|Iterable
argument_list|<
name|MatcherAndConverter
argument_list|>
name|getConvertersThisLevel
parameter_list|()
block|{
return|return
name|converters
return|;
block|}
DECL|method|addConverter
specifier|public
name|void
name|addConverter
parameter_list|(
name|MatcherAndConverter
name|matcherAndConverter
parameter_list|)
block|{
name|converters
operator|.
name|add
argument_list|(
name|matcherAndConverter
argument_list|)
expr_stmt|;
block|}
DECL|method|getConverter
specifier|public
name|MatcherAndConverter
name|getConverter
parameter_list|(
name|String
name|stringValue
parameter_list|,
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Errors
name|errors
parameter_list|,
name|Object
name|source
parameter_list|)
block|{
name|MatcherAndConverter
name|matchingConverter
init|=
literal|null
decl_stmt|;
for|for
control|(
name|State
name|s
init|=
name|this
init|;
name|s
operator|!=
name|State
operator|.
name|NONE
condition|;
name|s
operator|=
name|s
operator|.
name|parent
argument_list|()
control|)
block|{
for|for
control|(
name|MatcherAndConverter
name|converter
range|:
name|s
operator|.
name|getConvertersThisLevel
argument_list|()
control|)
block|{
if|if
condition|(
name|converter
operator|.
name|getTypeMatcher
argument_list|()
operator|.
name|matches
argument_list|(
name|type
argument_list|)
condition|)
block|{
if|if
condition|(
name|matchingConverter
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|ambiguousTypeConversion
argument_list|(
name|stringValue
argument_list|,
name|source
argument_list|,
name|type
argument_list|,
name|matchingConverter
argument_list|,
name|converter
argument_list|)
expr_stmt|;
block|}
name|matchingConverter
operator|=
name|converter
expr_stmt|;
block|}
block|}
block|}
return|return
name|matchingConverter
return|;
block|}
DECL|method|addTypeListener
specifier|public
name|void
name|addTypeListener
parameter_list|(
name|TypeListenerBinding
name|listenerBinding
parameter_list|)
block|{
name|listenerBindings
operator|.
name|add
argument_list|(
name|listenerBinding
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeListenerBindings
specifier|public
name|List
argument_list|<
name|TypeListenerBinding
argument_list|>
name|getTypeListenerBindings
parameter_list|()
block|{
name|List
argument_list|<
name|TypeListenerBinding
argument_list|>
name|parentBindings
init|=
name|parent
operator|.
name|getTypeListenerBindings
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|TypeListenerBinding
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|TypeListenerBinding
argument_list|>
argument_list|(
name|parentBindings
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|parentBindings
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|listenerBindings
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|blacklist
specifier|public
name|void
name|blacklist
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
block|{
name|parent
operator|.
name|blacklist
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|blacklistedKeys
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|isBlacklisted
specifier|public
name|boolean
name|isBlacklisted
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
block|{
return|return
name|blacklistedKeys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearBlacklisted
specifier|public
name|void
name|clearBlacklisted
parameter_list|()
block|{
name|blacklistedKeys
operator|=
operator|new
name|WeakKeySet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeAllBindingsToEagerSingletons
specifier|public
name|void
name|makeAllBindingsToEagerSingletons
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
name|Map
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Binding
argument_list|<
name|?
argument_list|>
argument_list|>
name|x
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Binding
argument_list|<
name|?
argument_list|>
argument_list|>
name|entry
range|:
name|this
operator|.
name|explicitBindingsMutable
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Key
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|BindingImpl
argument_list|<
name|?
argument_list|>
name|binding
init|=
operator|(
name|BindingImpl
argument_list|<
name|?
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|binding
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|x
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|InstanceBindingImpl
argument_list|<
name|Object
argument_list|>
argument_list|(
name|injector
argument_list|,
name|key
argument_list|,
name|SourceProvider
operator|.
name|UNKNOWN_SOURCE
argument_list|,
operator|new
name|InternalFactory
operator|.
name|Instance
argument_list|(
name|value
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|InjectionPoint
operator|>
name|of
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|explicitBindingsMutable
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|explicitBindingsMutable
operator|.
name|putAll
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
DECL|method|lock
specifier|public
name|Object
name|lock
parameter_list|()
block|{
return|return
name|lock
return|;
block|}
block|}
end_class

end_unit

