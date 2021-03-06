begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|AbstractModule
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
name|Binding
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
name|Key
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
name|MembersInjector
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
name|Module
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
name|PrivateBinder
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
name|PrivateModule
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
name|Provider
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
name|Scope
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
name|Stage
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
name|TypeLiteral
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
name|binder
operator|.
name|AnnotatedBindingBuilder
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
name|binder
operator|.
name|AnnotatedConstantBindingBuilder
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
name|binder
operator|.
name|AnnotatedElementBuilder
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
name|AbstractBindingBuilder
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
name|BindingBuilder
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
name|ConstantBindingBuilderImpl
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
name|ExposureBuilder
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
name|PrivateElementsImpl
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
name|ProviderMethodsModule
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
name|SourceProvider
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
name|matcher
operator|.
name|Matcher
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
name|Loggers
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Exposes elements of a module so they can be inspected, validated or {@link  * Element#applyTo(Binder) rewritten}.  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_class
DECL|class|Elements
specifier|public
specifier|final
class|class
name|Elements
block|{
DECL|field|GET_INSTANCE_VISITOR
specifier|private
specifier|static
specifier|final
name|BindingTargetVisitor
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|GET_INSTANCE_VISITOR
init|=
operator|new
name|DefaultBindingTargetVisitor
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|visit
parameter_list|(
name|InstanceBinding
argument_list|<
name|?
argument_list|>
name|binding
parameter_list|)
block|{
return|return
name|binding
operator|.
name|getInstance
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|visitOther
parameter_list|(
name|Binding
argument_list|<
name|?
argument_list|>
name|binding
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
comment|/**      * Records the elements executed by {@code modules}.      */
DECL|method|getElements
specifier|public
specifier|static
name|List
argument_list|<
name|Element
argument_list|>
name|getElements
parameter_list|(
name|Module
modifier|...
name|modules
parameter_list|)
block|{
return|return
name|getElements
argument_list|(
name|Stage
operator|.
name|DEVELOPMENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|modules
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Records the elements executed by {@code modules}.      */
DECL|method|getElements
specifier|public
specifier|static
name|List
argument_list|<
name|Element
argument_list|>
name|getElements
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
return|return
name|getElements
argument_list|(
name|Stage
operator|.
name|DEVELOPMENT
argument_list|,
name|modules
argument_list|)
return|;
block|}
comment|/**      * Records the elements executed by {@code modules}.      */
DECL|method|getElements
specifier|public
specifier|static
name|List
argument_list|<
name|Element
argument_list|>
name|getElements
parameter_list|(
name|Stage
name|stage
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|modules
parameter_list|)
block|{
name|RecordingBinder
name|binder
init|=
operator|new
name|RecordingBinder
argument_list|(
name|stage
argument_list|)
decl_stmt|;
for|for
control|(
name|Module
name|module
range|:
name|modules
control|)
block|{
name|binder
operator|.
name|install
argument_list|(
name|module
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|binder
operator|.
name|elements
argument_list|)
return|;
block|}
comment|/**      * Returns the module composed of {@code elements}.      */
DECL|method|getModule
specifier|public
specifier|static
name|Module
name|getModule
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|elements
parameter_list|)
block|{
return|return
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
for|for
control|(
name|Element
name|element
range|:
name|elements
control|)
block|{
name|element
operator|.
name|applyTo
argument_list|(
name|binder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|class|RecordingBinder
specifier|private
specifier|static
class|class
name|RecordingBinder
implements|implements
name|Binder
implements|,
name|PrivateBinder
block|{
DECL|field|stage
specifier|private
specifier|final
name|Stage
name|stage
decl_stmt|;
DECL|field|modules
specifier|private
specifier|final
name|Set
argument_list|<
name|Module
argument_list|>
name|modules
decl_stmt|;
DECL|field|elements
specifier|private
specifier|final
name|List
argument_list|<
name|Element
argument_list|>
name|elements
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|field|sourceProvider
specifier|private
specifier|final
name|SourceProvider
name|sourceProvider
decl_stmt|;
comment|/**          * The binder where exposed bindings will be created          */
DECL|field|parent
specifier|private
specifier|final
name|RecordingBinder
name|parent
decl_stmt|;
DECL|field|privateElements
specifier|private
specifier|final
name|PrivateElementsImpl
name|privateElements
decl_stmt|;
DECL|method|RecordingBinder
specifier|private
name|RecordingBinder
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
name|this
operator|.
name|modules
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|elements
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|source
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|sourceProvider
operator|=
operator|new
name|SourceProvider
argument_list|()
operator|.
name|plusSkippedClasses
argument_list|(
name|Elements
operator|.
name|class
argument_list|,
name|RecordingBinder
operator|.
name|class
argument_list|,
name|AbstractModule
operator|.
name|class
argument_list|,
name|ConstantBindingBuilderImpl
operator|.
name|class
argument_list|,
name|AbstractBindingBuilder
operator|.
name|class
argument_list|,
name|BindingBuilder
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|privateElements
operator|=
literal|null
expr_stmt|;
block|}
comment|/**          * Creates a recording binder that's backed by {@code prototype}.          */
DECL|method|RecordingBinder
specifier|private
name|RecordingBinder
parameter_list|(
name|RecordingBinder
name|prototype
parameter_list|,
name|Object
name|source
parameter_list|,
name|SourceProvider
name|sourceProvider
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|source
operator|==
literal|null
operator|^
name|sourceProvider
operator|==
literal|null
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|this
operator|.
name|stage
operator|=
name|prototype
operator|.
name|stage
expr_stmt|;
name|this
operator|.
name|modules
operator|=
name|prototype
operator|.
name|modules
expr_stmt|;
name|this
operator|.
name|elements
operator|=
name|prototype
operator|.
name|elements
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sourceProvider
operator|=
name|sourceProvider
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|prototype
operator|.
name|parent
expr_stmt|;
name|this
operator|.
name|privateElements
operator|=
name|prototype
operator|.
name|privateElements
expr_stmt|;
block|}
comment|/**          * Creates a private recording binder.          */
DECL|method|RecordingBinder
specifier|private
name|RecordingBinder
parameter_list|(
name|RecordingBinder
name|parent
parameter_list|,
name|PrivateElementsImpl
name|privateElements
parameter_list|)
block|{
name|this
operator|.
name|stage
operator|=
name|parent
operator|.
name|stage
expr_stmt|;
name|this
operator|.
name|modules
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|elements
operator|=
name|privateElements
operator|.
name|getElementsMutable
argument_list|()
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|parent
operator|.
name|source
expr_stmt|;
name|this
operator|.
name|sourceProvider
operator|=
name|parent
operator|.
name|sourceProvider
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|privateElements
operator|=
name|privateElements
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bindScope
specifier|public
name|void
name|bindScope
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
name|elements
operator|.
name|add
argument_list|(
operator|new
name|ScopeBinding
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|annotationType
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// it is safe to use the type literal for the raw type
DECL|method|requestInjection
specifier|public
name|void
name|requestInjection
parameter_list|(
name|Object
name|instance
parameter_list|)
block|{
name|requestInjection
argument_list|(
operator|(
name|TypeLiteral
operator|)
name|TypeLiteral
operator|.
name|get
argument_list|(
name|instance
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requestInjection
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|requestInjection
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
name|instance
parameter_list|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|new
name|InjectionRequest
argument_list|<>
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|type
argument_list|,
name|instance
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMembersInjector
specifier|public
parameter_list|<
name|T
parameter_list|>
name|MembersInjector
argument_list|<
name|T
argument_list|>
name|getMembersInjector
parameter_list|(
specifier|final
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|)
block|{
specifier|final
name|MembersInjectorLookup
argument_list|<
name|T
argument_list|>
name|element
init|=
operator|new
name|MembersInjectorLookup
argument_list|<>
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|typeLiteral
argument_list|)
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
name|element
operator|.
name|getMembersInjector
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMembersInjector
specifier|public
parameter_list|<
name|T
parameter_list|>
name|MembersInjector
argument_list|<
name|T
argument_list|>
name|getMembersInjector
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|getMembersInjector
argument_list|(
name|TypeLiteral
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bindListener
specifier|public
name|void
name|bindListener
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|>
name|typeMatcher
parameter_list|,
name|TypeListener
name|listener
parameter_list|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|new
name|TypeListenerBinding
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|listener
argument_list|,
name|typeMatcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requestStaticInjection
specifier|public
name|void
name|requestStaticInjection
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|types
parameter_list|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
range|:
name|types
control|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|new
name|StaticInjectionRequest
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|install
specifier|public
name|void
name|install
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|modules
operator|.
name|add
argument_list|(
name|module
argument_list|)
condition|)
block|{
name|Binder
name|binder
init|=
name|this
decl_stmt|;
if|if
condition|(
name|module
operator|instanceof
name|PrivateModule
condition|)
block|{
name|binder
operator|=
name|binder
operator|.
name|newPrivateBinder
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|module
operator|.
name|configure
argument_list|(
name|binder
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// NOTE: This is not in the original guice. We rethrow here to expose any explicit errors in configure()
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|Collection
argument_list|<
name|Message
argument_list|>
name|messages
init|=
name|Errors
operator|.
name|getMessagesFromThrowable
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|messages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|elements
operator|.
name|addAll
argument_list|(
name|messages
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|binder
operator|.
name|install
argument_list|(
name|ProviderMethodsModule
operator|.
name|forModule
argument_list|(
name|module
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|currentStage
specifier|public
name|Stage
name|currentStage
parameter_list|()
block|{
return|return
name|stage
return|;
block|}
annotation|@
name|Override
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|new
name|Message
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|Errors
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|message
init|=
literal|"An exception was caught and reported. Message: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
operator|new
name|Message
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|getSource
argument_list|()
argument_list|)
argument_list|,
name|message
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|elements
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bind
specifier|public
parameter_list|<
name|T
parameter_list|>
name|AnnotatedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
return|return
operator|new
name|BindingBuilder
argument_list|<>
argument_list|(
name|this
argument_list|,
name|elements
argument_list|,
name|getSource
argument_list|()
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bind
specifier|public
parameter_list|<
name|T
parameter_list|>
name|AnnotatedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|)
block|{
return|return
name|bind
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|typeLiteral
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bind
specifier|public
parameter_list|<
name|T
parameter_list|>
name|AnnotatedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|bind
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bindConstant
specifier|public
name|AnnotatedConstantBindingBuilder
name|bindConstant
parameter_list|()
block|{
return|return
operator|new
name|ConstantBindingBuilderImpl
argument_list|<
name|Void
argument_list|>
argument_list|(
name|this
argument_list|,
name|elements
argument_list|,
name|getSource
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProvider
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|getProvider
parameter_list|(
specifier|final
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
specifier|final
name|ProviderLookup
argument_list|<
name|T
argument_list|>
name|element
init|=
operator|new
name|ProviderLookup
argument_list|<>
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
name|element
operator|.
name|getProvider
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProvider
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|getProvider
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|getProvider
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|convertToTypes
specifier|public
name|void
name|convertToTypes
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|>
name|typeMatcher
parameter_list|,
name|TypeConverter
name|converter
parameter_list|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|new
name|TypeConverterBinding
argument_list|(
name|getSource
argument_list|()
argument_list|,
name|typeMatcher
argument_list|,
name|converter
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|withSource
specifier|public
name|RecordingBinder
name|withSource
parameter_list|(
specifier|final
name|Object
name|source
parameter_list|)
block|{
return|return
operator|new
name|RecordingBinder
argument_list|(
name|this
argument_list|,
name|source
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|skipSources
specifier|public
name|RecordingBinder
name|skipSources
parameter_list|(
name|Class
modifier|...
name|classesToSkip
parameter_list|)
block|{
comment|// if a source is specified explicitly, we don't need to skip sources
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
name|SourceProvider
name|newSourceProvider
init|=
name|sourceProvider
operator|.
name|plusSkippedClasses
argument_list|(
name|classesToSkip
argument_list|)
decl_stmt|;
return|return
operator|new
name|RecordingBinder
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
name|newSourceProvider
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newPrivateBinder
specifier|public
name|PrivateBinder
name|newPrivateBinder
parameter_list|()
block|{
name|PrivateElementsImpl
name|privateElements
init|=
operator|new
name|PrivateElementsImpl
argument_list|(
name|getSource
argument_list|()
argument_list|)
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|privateElements
argument_list|)
expr_stmt|;
return|return
operator|new
name|RecordingBinder
argument_list|(
name|this
argument_list|,
name|privateElements
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|expose
specifier|public
name|void
name|expose
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
block|{
name|exposeInternal
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expose
specifier|public
name|AnnotatedElementBuilder
name|expose
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|exposeInternal
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|expose
specifier|public
name|AnnotatedElementBuilder
name|expose
parameter_list|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|exposeInternal
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
DECL|method|exposeInternal
specifier|private
parameter_list|<
name|T
parameter_list|>
name|AnnotatedElementBuilder
name|exposeInternal
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
if|if
condition|(
name|privateElements
operator|==
literal|null
condition|)
block|{
name|addError
argument_list|(
literal|"Cannot expose %s on a standard binder. "
operator|+
literal|"Exposed bindings are only applicable to private binders."
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
operator|new
name|AnnotatedElementBuilder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|annotatedWith
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|)
block|{                     }
annotation|@
name|Override
specifier|public
name|void
name|annotatedWith
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{                     }
block|}
return|;
block|}
name|ExposureBuilder
argument_list|<
name|T
argument_list|>
name|builder
init|=
operator|new
name|ExposureBuilder
argument_list|<>
argument_list|(
name|this
argument_list|,
name|getSource
argument_list|()
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|privateElements
operator|.
name|addExposureBuilder
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|field|logger
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Elements
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getSource
specifier|protected
name|Object
name|getSource
parameter_list|()
block|{
name|Object
name|ret
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|ret
operator|=
name|sourceProvider
operator|!=
literal|null
condition|?
name|sourceProvider
operator|.
name|get
argument_list|()
else|:
name|source
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|source
expr_stmt|;
block|}
return|return
name|ret
operator|==
literal|null
condition|?
literal|"_unknown_"
else|:
name|ret
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
literal|"Binder"
return|;
block|}
block|}
block|}
end_class

end_unit

