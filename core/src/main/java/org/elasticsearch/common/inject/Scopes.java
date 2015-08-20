begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|InternalFactory
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
name|Scoping
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * Built-in scope implementations.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|Scopes
specifier|public
class|class
name|Scopes
block|{
DECL|method|Scopes
specifier|private
name|Scopes
parameter_list|()
block|{     }
comment|/**      * One instance per {@link Injector}. Also see {@code @}{@link Singleton}.      */
DECL|field|SINGLETON
specifier|public
specifier|static
specifier|final
name|Scope
name|SINGLETON
init|=
operator|new
name|Scope
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|scope
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|T
argument_list|>
name|creator
parameter_list|)
block|{
return|return
operator|new
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|private
specifier|volatile
name|T
name|instance
decl_stmt|;
comment|// DCL on a volatile is safe as of Java 5, which we obviously require.
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"DoubleCheckedLocking"
argument_list|)
specifier|public
name|T
name|get
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
comment|/*                         * Use a pretty coarse lock. We don't want to run into deadlocks                         * when two threads try to load circularly-dependent objects.                         * Maybe one of these days we will identify independent graphs of                         * objects and offer to load them in parallel.                         */
synchronized|synchronized
init|(
name|InjectorImpl
operator|.
name|class
init|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
name|creator
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|instance
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s[%s]"
argument_list|,
name|creator
argument_list|,
name|SINGLETON
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Scopes.SINGLETON"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * No scope; the same as not applying any scope at all.  Each time the      * Injector obtains an instance of an object with "no scope", it injects this      * instance then immediately forgets it.  When the next request for the same      * binding arrives it will need to obtain the instance over again.      *<p/>      *<p>This exists only in case a class has been annotated with a scope      * annotation such as {@link Singleton @Singleton}, and you need to override      * this to "no scope" in your binding.      *      * @since 2.0      */
DECL|field|NO_SCOPE
specifier|public
specifier|static
specifier|final
name|Scope
name|NO_SCOPE
init|=
operator|new
name|Scope
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|scope
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|Provider
argument_list|<
name|T
argument_list|>
name|unscoped
parameter_list|)
block|{
return|return
name|unscoped
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Scopes.NO_SCOPE"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Scopes an internal factory.      */
DECL|method|scope
specifier|static
parameter_list|<
name|T
parameter_list|>
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|scope
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|InjectorImpl
name|injector
parameter_list|,
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|creator
parameter_list|,
name|Scoping
name|scoping
parameter_list|)
block|{
if|if
condition|(
name|scoping
operator|.
name|isNoScope
argument_list|()
condition|)
block|{
return|return
name|creator
return|;
block|}
name|Scope
name|scope
init|=
name|scoping
operator|.
name|getScopeInstance
argument_list|()
decl_stmt|;
comment|// TODO: use diamond operator once JI-9019884 is fixed
name|Provider
argument_list|<
name|T
argument_list|>
name|scoped
init|=
name|scope
operator|.
name|scope
argument_list|(
name|key
argument_list|,
operator|new
name|ProviderToInternalFactoryAdapter
argument_list|<
name|T
argument_list|>
argument_list|(
name|injector
argument_list|,
name|creator
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|InternalFactoryToProviderAdapter
argument_list|<>
argument_list|(
name|Initializables
operator|.
expr|<
name|Provider
argument_list|<
name|?
extends|extends
name|T
argument_list|>
operator|>
name|of
argument_list|(
name|scoped
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Replaces annotation scopes with instance scopes using the Injector's annotation-to-instance      * map. If the scope annotation has no corresponding instance, an error will be added and unscoped      * will be retuned.      */
DECL|method|makeInjectable
specifier|static
name|Scoping
name|makeInjectable
parameter_list|(
name|Scoping
name|scoping
parameter_list|,
name|InjectorImpl
name|injector
parameter_list|,
name|Errors
name|errors
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|scopeAnnotation
init|=
name|scoping
operator|.
name|getScopeAnnotation
argument_list|()
decl_stmt|;
if|if
condition|(
name|scopeAnnotation
operator|==
literal|null
condition|)
block|{
return|return
name|scoping
return|;
block|}
name|Scope
name|scope
init|=
name|injector
operator|.
name|state
operator|.
name|getScope
argument_list|(
name|scopeAnnotation
argument_list|)
decl_stmt|;
if|if
condition|(
name|scope
operator|!=
literal|null
condition|)
block|{
return|return
name|Scoping
operator|.
name|forInstance
argument_list|(
name|scope
argument_list|)
return|;
block|}
name|errors
operator|.
name|scopeNotFound
argument_list|(
name|scopeAnnotation
argument_list|)
expr_stmt|;
return|return
name|Scoping
operator|.
name|UNSCOPED
return|;
block|}
block|}
end_class

end_unit
