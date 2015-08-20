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
name|Errors
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
name|ErrorsException
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
name|concurrent
operator|.
name|CountDownLatch
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
comment|/**  * Manages and injects instances at injector-creation time. This is made more complicated by  * instances that request other instances while they're being injected. We overcome this by using  * {@link Initializable}, which attempts to perform injection before use.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|Initializer
class|class
name|Initializer
block|{
comment|/**      * the only thread that we'll use to inject members.      */
DECL|field|creatingThread
specifier|private
specifier|final
name|Thread
name|creatingThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
comment|/**      * zero means everything is injected.      */
DECL|field|ready
specifier|private
specifier|final
name|CountDownLatch
name|ready
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**      * Maps instances that need injection to a source that registered them      */
DECL|field|pendingInjection
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|InjectableReference
argument_list|<
name|?
argument_list|>
argument_list|>
name|pendingInjection
init|=
name|Maps
operator|.
name|newIdentityHashMap
argument_list|()
decl_stmt|;
comment|/**      * Registers an instance for member injection when that step is performed.      *      * @param instance an instance that optionally has members to be injected (each annotated with      * @param source   the source location that this injection was requested      * @Inject).      */
DECL|method|requestInjection
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Initializable
argument_list|<
name|T
argument_list|>
name|requestInjection
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|T
name|instance
parameter_list|,
name|Object
name|source
parameter_list|,
name|Set
argument_list|<
name|InjectionPoint
argument_list|>
name|injectionPoints
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|source
argument_list|)
expr_stmt|;
comment|// short circuit if the object has no injections
if|if
condition|(
name|instance
operator|==
literal|null
operator|||
operator|(
name|injectionPoints
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|injector
operator|.
name|membersInjectorStore
operator|.
name|hasTypeListeners
argument_list|()
operator|)
condition|)
block|{
return|return
name|Initializables
operator|.
name|of
argument_list|(
name|instance
argument_list|)
return|;
block|}
name|InjectableReference
argument_list|<
name|T
argument_list|>
name|initializable
init|=
operator|new
name|InjectableReference
argument_list|<>
argument_list|(
name|injector
argument_list|,
name|instance
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|pendingInjection
operator|.
name|put
argument_list|(
name|instance
argument_list|,
name|initializable
argument_list|)
expr_stmt|;
return|return
name|initializable
return|;
block|}
comment|/**      * Prepares member injectors for all injected instances. This prompts Guice to do static analysis      * on the injected instances.      */
DECL|method|validateOustandingInjections
name|void
name|validateOustandingInjections
parameter_list|(
name|Errors
name|errors
parameter_list|)
block|{
for|for
control|(
name|InjectableReference
argument_list|<
name|?
argument_list|>
name|reference
range|:
name|pendingInjection
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|reference
operator|.
name|validate
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ErrorsException
name|e
parameter_list|)
block|{
name|errors
operator|.
name|merge
argument_list|(
name|e
operator|.
name|getErrors
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Performs creation-time injections on all objects that require it. Whenever fulfilling an      * injection depends on another object that requires injection, we inject it first. If the two      * instances are codependent (directly or transitively), ordering of injection is arbitrary.      */
DECL|method|injectAll
name|void
name|injectAll
parameter_list|(
specifier|final
name|Errors
name|errors
parameter_list|)
block|{
comment|// loop over a defensive copy since ensureInjected() mutates the set. Unfortunately, that copy
comment|// is made complicated by a bug in IBM's JDK, wherein entrySet().toArray(Object[]) doesn't work
for|for
control|(
name|InjectableReference
argument_list|<
name|?
argument_list|>
name|reference
range|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pendingInjection
operator|.
name|values
argument_list|()
argument_list|)
control|)
block|{
try|try
block|{
name|reference
operator|.
name|get
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ErrorsException
name|e
parameter_list|)
block|{
name|errors
operator|.
name|merge
argument_list|(
name|e
operator|.
name|getErrors
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|pendingInjection
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Failed to satisfy "
operator|+
name|pendingInjection
argument_list|)
throw|;
block|}
name|ready
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
DECL|class|InjectableReference
specifier|private
class|class
name|InjectableReference
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Initializable
argument_list|<
name|T
argument_list|>
block|{
DECL|field|injector
specifier|private
specifier|final
name|InjectorImpl
name|injector
decl_stmt|;
DECL|field|instance
specifier|private
specifier|final
name|T
name|instance
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|field|membersInjector
specifier|private
name|MembersInjectorImpl
argument_list|<
name|T
argument_list|>
name|membersInjector
decl_stmt|;
DECL|method|InjectableReference
specifier|public
name|InjectableReference
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|T
name|instance
parameter_list|,
name|Object
name|source
parameter_list|)
block|{
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|this
operator|.
name|instance
operator|=
name|checkNotNull
argument_list|(
name|instance
argument_list|,
literal|"instance"
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|checkNotNull
argument_list|(
name|source
argument_list|,
literal|"source"
argument_list|)
expr_stmt|;
block|}
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|(
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// the type of 'T' is a TypeLiteral<T>
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
init|=
name|TypeLiteral
operator|.
name|get
argument_list|(
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|instance
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|membersInjector
operator|=
name|injector
operator|.
name|membersInjectorStore
operator|.
name|get
argument_list|(
name|type
argument_list|,
name|errors
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**          * Reentrant. If {@code instance} was registered for injection at injector-creation time, this          * method will ensure that all its members have been injected before returning.          */
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
if|if
condition|(
name|ready
operator|.
name|getCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|instance
return|;
block|}
comment|// just wait for everything to be injected by another thread
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|!=
name|creatingThread
condition|)
block|{
try|try
block|{
name|ready
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
name|instance
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Give up, since we don't know if our injection is ready
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// toInject needs injection, do it right away. we only do this once, even if it fails
if|if
condition|(
name|pendingInjection
operator|.
name|remove
argument_list|(
name|instance
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|membersInjector
operator|.
name|injectAndNotify
argument_list|(
name|instance
argument_list|,
name|errors
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
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
name|instance
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
