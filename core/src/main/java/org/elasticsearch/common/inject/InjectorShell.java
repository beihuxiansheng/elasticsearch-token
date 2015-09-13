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
name|*
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|Preconditions
operator|.
name|checkState
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
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
import|;
end_import

begin_comment
comment|/**  * A partially-initialized injector. See {@link InjectorBuilder}, which uses this to build a tree  * of injectors in batch.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|InjectorShell
class|class
name|InjectorShell
block|{
DECL|field|elements
specifier|private
specifier|final
name|List
argument_list|<
name|Element
argument_list|>
name|elements
decl_stmt|;
DECL|field|injector
specifier|private
specifier|final
name|InjectorImpl
name|injector
decl_stmt|;
DECL|field|privateElements
specifier|private
specifier|final
name|PrivateElements
name|privateElements
decl_stmt|;
DECL|method|InjectorShell
specifier|private
name|InjectorShell
parameter_list|(
name|Builder
name|builder
parameter_list|,
name|List
argument_list|<
name|Element
argument_list|>
name|elements
parameter_list|,
name|InjectorImpl
name|injector
parameter_list|)
block|{
name|this
operator|.
name|privateElements
operator|=
name|builder
operator|.
name|privateElements
expr_stmt|;
name|this
operator|.
name|elements
operator|=
name|elements
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
block|}
DECL|method|getPrivateElements
name|PrivateElements
name|getPrivateElements
parameter_list|()
block|{
return|return
name|privateElements
return|;
block|}
DECL|method|getInjector
name|InjectorImpl
name|getInjector
parameter_list|()
block|{
return|return
name|injector
return|;
block|}
DECL|method|getElements
name|List
argument_list|<
name|Element
argument_list|>
name|getElements
parameter_list|()
block|{
return|return
name|elements
return|;
block|}
DECL|class|Builder
specifier|static
class|class
name|Builder
block|{
DECL|field|elements
specifier|private
specifier|final
name|List
argument_list|<
name|Element
argument_list|>
name|elements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|modules
specifier|private
specifier|final
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**          * lazily constructed          */
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|parent
specifier|private
name|InjectorImpl
name|parent
decl_stmt|;
DECL|field|stage
specifier|private
name|Stage
name|stage
decl_stmt|;
comment|/**          * null unless this exists in a {@link Binder#newPrivateBinder private environment}          */
DECL|field|privateElements
specifier|private
name|PrivateElementsImpl
name|privateElements
decl_stmt|;
DECL|method|parent
name|Builder
name|parent
parameter_list|(
name|InjectorImpl
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|state
operator|=
operator|new
name|InheritingState
argument_list|(
name|parent
operator|.
name|state
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|stage
name|Builder
name|stage
parameter_list|(
name|Stage
name|stage
parameter_list|)
block|{
name|this
operator|.
name|stage
operator|=
name|stage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|privateElements
name|Builder
name|privateElements
parameter_list|(
name|PrivateElements
name|privateElements
parameter_list|)
block|{
name|this
operator|.
name|privateElements
operator|=
operator|(
name|PrivateElementsImpl
operator|)
name|privateElements
expr_stmt|;
name|this
operator|.
name|elements
operator|.
name|addAll
argument_list|(
name|privateElements
operator|.
name|getElements
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addModules
name|void
name|addModules
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|modules
parameter_list|)
block|{
for|for
control|(
name|Module
name|module
range|:
name|modules
control|)
block|{
name|this
operator|.
name|modules
operator|.
name|add
argument_list|(
name|module
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Synchronize on this before calling {@link #build}.          */
DECL|method|lock
name|Object
name|lock
parameter_list|()
block|{
return|return
name|getState
argument_list|()
operator|.
name|lock
argument_list|()
return|;
block|}
comment|/**          * Creates and returns the injector shells for the current modules. Multiple shells will be          * returned if any modules contain {@link Binder#newPrivateBinder private environments}. The          * primary injector will be first in the returned list.          */
DECL|method|build
name|List
argument_list|<
name|InjectorShell
argument_list|>
name|build
parameter_list|(
name|Initializer
name|initializer
parameter_list|,
name|BindingProcessor
name|bindingProcessor
parameter_list|,
name|Stopwatch
name|stopwatch
parameter_list|,
name|Errors
name|errors
parameter_list|)
block|{
name|checkState
argument_list|(
name|stage
operator|!=
literal|null
argument_list|,
literal|"Stage not initialized"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|privateElements
operator|==
literal|null
operator|||
name|parent
operator|!=
literal|null
argument_list|,
literal|"PrivateElements with no parent"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|state
operator|!=
literal|null
argument_list|,
literal|"no state. Did you remember to lock() ?"
argument_list|)
expr_stmt|;
name|InjectorImpl
name|injector
init|=
operator|new
name|InjectorImpl
argument_list|(
name|parent
argument_list|,
name|state
argument_list|,
name|initializer
argument_list|)
decl_stmt|;
if|if
condition|(
name|privateElements
operator|!=
literal|null
condition|)
block|{
name|privateElements
operator|.
name|initInjector
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
comment|// bind Stage and Singleton if this is a top-level injector
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|modules
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|RootModule
argument_list|(
name|stage
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|TypeConverterBindingProcessor
argument_list|(
name|errors
argument_list|)
operator|.
name|prepareBuiltInConverters
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
name|elements
operator|.
name|addAll
argument_list|(
name|Elements
operator|.
name|getElements
argument_list|(
name|stage
argument_list|,
name|modules
argument_list|)
argument_list|)
expr_stmt|;
name|stopwatch
operator|.
name|resetAndLog
argument_list|(
literal|"Module execution"
argument_list|)
expr_stmt|;
operator|new
name|MessageProcessor
argument_list|(
name|errors
argument_list|)
operator|.
name|process
argument_list|(
name|injector
argument_list|,
name|elements
argument_list|)
expr_stmt|;
operator|new
name|TypeListenerBindingProcessor
argument_list|(
name|errors
argument_list|)
operator|.
name|process
argument_list|(
name|injector
argument_list|,
name|elements
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TypeListenerBinding
argument_list|>
name|listenerBindings
init|=
name|injector
operator|.
name|state
operator|.
name|getTypeListenerBindings
argument_list|()
decl_stmt|;
name|injector
operator|.
name|membersInjectorStore
operator|=
operator|new
name|MembersInjectorStore
argument_list|(
name|injector
argument_list|,
name|listenerBindings
argument_list|)
expr_stmt|;
name|stopwatch
operator|.
name|resetAndLog
argument_list|(
literal|"TypeListeners creation"
argument_list|)
expr_stmt|;
operator|new
name|ScopeBindingProcessor
argument_list|(
name|errors
argument_list|)
operator|.
name|process
argument_list|(
name|injector
argument_list|,
name|elements
argument_list|)
expr_stmt|;
name|stopwatch
operator|.
name|resetAndLog
argument_list|(
literal|"Scopes creation"
argument_list|)
expr_stmt|;
operator|new
name|TypeConverterBindingProcessor
argument_list|(
name|errors
argument_list|)
operator|.
name|process
argument_list|(
name|injector
argument_list|,
name|elements
argument_list|)
expr_stmt|;
name|stopwatch
operator|.
name|resetAndLog
argument_list|(
literal|"Converters creation"
argument_list|)
expr_stmt|;
name|bindInjector
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|bindLogger
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|bindingProcessor
operator|.
name|process
argument_list|(
name|injector
argument_list|,
name|elements
argument_list|)
expr_stmt|;
name|stopwatch
operator|.
name|resetAndLog
argument_list|(
literal|"Binding creation"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InjectorShell
argument_list|>
name|injectorShells
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|injectorShells
operator|.
name|add
argument_list|(
operator|new
name|InjectorShell
argument_list|(
name|this
argument_list|,
name|elements
argument_list|,
name|injector
argument_list|)
argument_list|)
expr_stmt|;
comment|// recursively build child shells
name|PrivateElementProcessor
name|processor
init|=
operator|new
name|PrivateElementProcessor
argument_list|(
name|errors
argument_list|,
name|stage
argument_list|)
decl_stmt|;
name|processor
operator|.
name|process
argument_list|(
name|injector
argument_list|,
name|elements
argument_list|)
expr_stmt|;
for|for
control|(
name|Builder
name|builder
range|:
name|processor
operator|.
name|getInjectorShellBuilders
argument_list|()
control|)
block|{
name|injectorShells
operator|.
name|addAll
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|initializer
argument_list|,
name|bindingProcessor
argument_list|,
name|stopwatch
argument_list|,
name|errors
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stopwatch
operator|.
name|resetAndLog
argument_list|(
literal|"Private environment creation"
argument_list|)
expr_stmt|;
return|return
name|injectorShells
return|;
block|}
DECL|method|getState
specifier|private
name|State
name|getState
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|state
operator|=
operator|new
name|InheritingState
argument_list|(
name|State
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
block|}
comment|/**      * The Injector is a special case because we allow both parent and child injectors to both have      * a binding for that key.      */
DECL|method|bindInjector
specifier|private
specifier|static
name|void
name|bindInjector
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|)
block|{
name|Key
argument_list|<
name|Injector
argument_list|>
name|key
init|=
name|Key
operator|.
name|get
argument_list|(
name|Injector
operator|.
name|class
argument_list|)
decl_stmt|;
name|InjectorFactory
name|injectorFactory
init|=
operator|new
name|InjectorFactory
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|injector
operator|.
name|state
operator|.
name|putBinding
argument_list|(
name|key
argument_list|,
operator|new
name|ProviderInstanceBindingImpl
argument_list|<>
argument_list|(
name|injector
argument_list|,
name|key
argument_list|,
name|SourceProvider
operator|.
name|UNKNOWN_SOURCE
argument_list|,
name|injectorFactory
argument_list|,
name|Scoping
operator|.
name|UNSCOPED
argument_list|,
name|injectorFactory
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|InjectionPoint
operator|>
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|InjectorFactory
specifier|private
specifier|static
class|class
name|InjectorFactory
implements|implements
name|InternalFactory
argument_list|<
name|Injector
argument_list|>
implements|,
name|Provider
argument_list|<
name|Injector
argument_list|>
block|{
DECL|field|injector
specifier|private
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|method|InjectorFactory
specifier|private
name|InjectorFactory
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Injector
name|get
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|InternalContext
name|context
parameter_list|,
name|Dependency
argument_list|<
name|?
argument_list|>
name|dependency
parameter_list|)
throws|throws
name|ErrorsException
block|{
return|return
name|injector
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Injector
name|get
parameter_list|()
block|{
return|return
name|injector
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
return|return
literal|"Provider<Injector>"
return|;
block|}
block|}
comment|/**      * The Logger is a special case because it knows the injection point of the injected member. It's      * the only binding that does this.      */
DECL|method|bindLogger
specifier|private
specifier|static
name|void
name|bindLogger
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|)
block|{
name|Key
argument_list|<
name|Logger
argument_list|>
name|key
init|=
name|Key
operator|.
name|get
argument_list|(
name|Logger
operator|.
name|class
argument_list|)
decl_stmt|;
name|LoggerFactory
name|loggerFactory
init|=
operator|new
name|LoggerFactory
argument_list|()
decl_stmt|;
name|injector
operator|.
name|state
operator|.
name|putBinding
argument_list|(
name|key
argument_list|,
operator|new
name|ProviderInstanceBindingImpl
argument_list|<>
argument_list|(
name|injector
argument_list|,
name|key
argument_list|,
name|SourceProvider
operator|.
name|UNKNOWN_SOURCE
argument_list|,
name|loggerFactory
argument_list|,
name|Scoping
operator|.
name|UNSCOPED
argument_list|,
name|loggerFactory
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|InjectionPoint
operator|>
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|LoggerFactory
specifier|private
specifier|static
class|class
name|LoggerFactory
implements|implements
name|InternalFactory
argument_list|<
name|Logger
argument_list|>
implements|,
name|Provider
argument_list|<
name|Logger
argument_list|>
block|{
annotation|@
name|Override
DECL|method|get
specifier|public
name|Logger
name|get
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|InternalContext
name|context
parameter_list|,
name|Dependency
argument_list|<
name|?
argument_list|>
name|dependency
parameter_list|)
block|{
name|InjectionPoint
name|injectionPoint
init|=
name|dependency
operator|.
name|getInjectionPoint
argument_list|()
decl_stmt|;
return|return
name|injectionPoint
operator|==
literal|null
condition|?
name|Logger
operator|.
name|getAnonymousLogger
argument_list|()
else|:
name|Logger
operator|.
name|getLogger
argument_list|(
name|injectionPoint
operator|.
name|getMember
argument_list|()
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Logger
name|get
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getAnonymousLogger
argument_list|()
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
return|return
literal|"Provider<Logger>"
return|;
block|}
block|}
DECL|class|RootModule
specifier|private
specifier|static
class|class
name|RootModule
implements|implements
name|Module
block|{
DECL|field|stage
specifier|final
name|Stage
name|stage
decl_stmt|;
DECL|method|RootModule
specifier|private
name|RootModule
parameter_list|(
name|Stage
name|stage
parameter_list|)
block|{
name|this
operator|.
name|stage
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|stage
argument_list|,
literal|"stage"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|binder
operator|=
name|binder
operator|.
name|withSource
argument_list|(
name|SourceProvider
operator|.
name|UNKNOWN_SOURCE
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|Stage
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|stage
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bindScope
argument_list|(
name|Singleton
operator|.
name|class
argument_list|,
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

