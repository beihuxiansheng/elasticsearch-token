begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BindingTargetVisitor
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
name|ConstructorBinding
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
name|Dependency
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
name|Set
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

begin_class
DECL|class|ConstructorBindingImpl
class|class
name|ConstructorBindingImpl
parameter_list|<
name|T
parameter_list|>
extends|extends
name|BindingImpl
argument_list|<
name|T
argument_list|>
implements|implements
name|ConstructorBinding
argument_list|<
name|T
argument_list|>
block|{
DECL|field|factory
specifier|private
specifier|final
name|Factory
argument_list|<
name|T
argument_list|>
name|factory
decl_stmt|;
DECL|method|ConstructorBindingImpl
specifier|private
name|ConstructorBindingImpl
parameter_list|(
name|Injector
name|injector
parameter_list|,
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|Object
name|source
parameter_list|,
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|scopedFactory
parameter_list|,
name|Scoping
name|scoping
parameter_list|,
name|Factory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|injector
argument_list|,
name|key
argument_list|,
name|source
argument_list|,
name|scopedFactory
argument_list|,
name|scoping
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|create
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ConstructorBindingImpl
argument_list|<
name|T
argument_list|>
name|create
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|Object
name|source
parameter_list|,
name|Scoping
name|scoping
parameter_list|)
block|{
name|Factory
argument_list|<
name|T
argument_list|>
name|factoryFactory
init|=
operator|new
name|Factory
argument_list|<>
argument_list|()
decl_stmt|;
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|scopedFactory
init|=
name|Scopes
operator|.
name|scope
argument_list|(
name|key
argument_list|,
name|injector
argument_list|,
name|factoryFactory
argument_list|,
name|scoping
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstructorBindingImpl
argument_list|<>
argument_list|(
name|injector
argument_list|,
name|key
argument_list|,
name|source
argument_list|,
name|scopedFactory
argument_list|,
name|scoping
argument_list|,
name|factoryFactory
argument_list|)
return|;
block|}
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
name|factory
operator|.
name|constructorInjector
operator|=
name|injector
operator|.
name|constructors
operator|.
name|get
argument_list|(
name|getKey
argument_list|()
operator|.
name|getTypeLiteral
argument_list|()
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptTargetVisitor
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|acceptTargetVisitor
parameter_list|(
name|BindingTargetVisitor
argument_list|<
name|?
super|super
name|T
argument_list|,
name|V
argument_list|>
name|visitor
parameter_list|)
block|{
name|checkState
argument_list|(
name|factory
operator|.
name|constructorInjector
operator|!=
literal|null
argument_list|,
literal|"not initialized"
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getConstructor
specifier|public
name|InjectionPoint
name|getConstructor
parameter_list|()
block|{
name|checkState
argument_list|(
name|factory
operator|.
name|constructorInjector
operator|!=
literal|null
argument_list|,
literal|"Binding is not ready"
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|constructorInjector
operator|.
name|getConstructionProxy
argument_list|()
operator|.
name|getInjectionPoint
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getInjectableMembers
specifier|public
name|Set
argument_list|<
name|InjectionPoint
argument_list|>
name|getInjectableMembers
parameter_list|()
block|{
name|checkState
argument_list|(
name|factory
operator|.
name|constructorInjector
operator|!=
literal|null
argument_list|,
literal|"Binding is not ready"
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|constructorInjector
operator|.
name|getInjectableMembers
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDependencies
specifier|public
name|Set
argument_list|<
name|Dependency
argument_list|<
name|?
argument_list|>
argument_list|>
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|forInjectionPoints
argument_list|(
operator|new
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|InjectionPoint
argument_list|>
argument_list|()
operator|.
name|add
argument_list|(
name|getConstructor
argument_list|()
argument_list|)
operator|.
name|addAll
argument_list|(
name|getInjectableMembers
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|applyTo
specifier|public
name|void
name|applyTo
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This element represents a synthetic binding."
argument_list|)
throw|;
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
operator|new
name|ToStringBuilder
argument_list|(
name|ConstructorBinding
operator|.
name|class
argument_list|)
operator|.
name|add
argument_list|(
literal|"key"
argument_list|,
name|getKey
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"source"
argument_list|,
name|getSource
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"scope"
argument_list|,
name|getScoping
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|Factory
specifier|private
specifier|static
class|class
name|Factory
parameter_list|<
name|T
parameter_list|>
implements|implements
name|InternalFactory
argument_list|<
name|T
argument_list|>
block|{
DECL|field|constructorInjector
specifier|private
name|ConstructorInjector
argument_list|<
name|T
argument_list|>
name|constructorInjector
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|get
specifier|public
name|T
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
name|checkState
argument_list|(
name|constructorInjector
operator|!=
literal|null
argument_list|,
literal|"Constructor not ready"
argument_list|)
expr_stmt|;
comment|// This may not actually be safe because it could return a super type of T (if that's all the
comment|// client needs), but it should be OK in practice thanks to the wonders of erasure.
return|return
operator|(
name|T
operator|)
name|constructorInjector
operator|.
name|construct
argument_list|(
name|errors
argument_list|,
name|context
argument_list|,
name|dependency
operator|.
name|getKey
argument_list|()
operator|.
name|getRawType
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

