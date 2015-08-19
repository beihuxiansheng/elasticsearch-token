begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.assistedinject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|assistedinject
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
name|Iterables
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
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
name|Message
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
name|util
operator|.
name|Providers
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
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
name|internal
operator|.
name|Annotations
operator|.
name|getKey
import|;
end_import

begin_comment
comment|/**  * The newer implementation of factory provider. This implementation uses a child injector to  * create values.  *  * @author jessewilson@google.com (Jesse Wilson)  * @author dtm@google.com (Daniel Martin)  */
end_comment

begin_class
DECL|class|FactoryProvider2
specifier|final
class|class
name|FactoryProvider2
parameter_list|<
name|F
parameter_list|>
implements|implements
name|InvocationHandler
implements|,
name|Provider
argument_list|<
name|F
argument_list|>
block|{
comment|/**      * if a factory method parameter isn't annotated, it gets this annotation.      */
DECL|field|DEFAULT_ANNOTATION
specifier|static
specifier|final
name|Assisted
name|DEFAULT_ANNOTATION
init|=
operator|new
name|Assisted
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|()
block|{
return|return
name|Assisted
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|Assisted
operator|&&
operator|(
operator|(
name|Assisted
operator|)
name|o
operator|)
operator|.
name|value
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|127
operator|*
literal|"value"
operator|.
name|hashCode
argument_list|()
operator|^
literal|""
operator|.
name|hashCode
argument_list|()
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
literal|"@"
operator|+
name|Assisted
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"(value=)"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * the produced type, or null if all methods return concrete types      */
DECL|field|producedType
specifier|private
specifier|final
name|Key
argument_list|<
name|?
argument_list|>
name|producedType
decl_stmt|;
DECL|field|returnTypesByMethod
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Method
argument_list|,
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
name|returnTypesByMethod
decl_stmt|;
DECL|field|paramTypes
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Method
argument_list|,
name|List
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|>
name|paramTypes
decl_stmt|;
comment|/**      * the hosting injector, or null if we haven't been initialized yet      */
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
comment|/**      * the factory interface, implemented and provided      */
DECL|field|factory
specifier|private
specifier|final
name|F
name|factory
decl_stmt|;
comment|/**      * @param factoryType  a Java interface that defines one or more create methods.      * @param producedType a concrete type that is assignable to the return types of all factory      *                     methods.      */
DECL|method|FactoryProvider2
name|FactoryProvider2
parameter_list|(
name|TypeLiteral
argument_list|<
name|F
argument_list|>
name|factoryType
parameter_list|,
name|Key
argument_list|<
name|?
argument_list|>
name|producedType
parameter_list|)
block|{
name|this
operator|.
name|producedType
operator|=
name|producedType
expr_stmt|;
name|Errors
name|errors
init|=
operator|new
name|Errors
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// we imprecisely treat the class literal of T as a Class<T>
name|Class
argument_list|<
name|F
argument_list|>
name|factoryRawType
init|=
operator|(
name|Class
operator|)
name|factoryType
operator|.
name|getRawType
argument_list|()
decl_stmt|;
try|try
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Method
argument_list|,
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
name|returnTypesBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Method
argument_list|,
name|List
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|>
name|paramTypesBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// TODO: also grab methods from superinterfaces
for|for
control|(
name|Method
name|method
range|:
name|factoryRawType
operator|.
name|getMethods
argument_list|()
control|)
block|{
name|Key
argument_list|<
name|?
argument_list|>
name|returnType
init|=
name|getKey
argument_list|(
name|factoryType
operator|.
name|getReturnType
argument_list|(
name|method
argument_list|)
argument_list|,
name|method
argument_list|,
name|method
operator|.
name|getAnnotations
argument_list|()
argument_list|,
name|errors
argument_list|)
decl_stmt|;
name|returnTypesBuilder
operator|.
name|put
argument_list|(
name|method
argument_list|,
name|returnType
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|>
name|params
init|=
name|factoryType
operator|.
name|getParameterTypes
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|Annotation
index|[]
index|[]
name|paramAnnotations
init|=
name|method
operator|.
name|getParameterAnnotations
argument_list|()
decl_stmt|;
name|int
name|p
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
name|keys
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|param
range|:
name|params
control|)
block|{
name|Key
argument_list|<
name|?
argument_list|>
name|paramKey
init|=
name|getKey
argument_list|(
name|param
argument_list|,
name|method
argument_list|,
name|paramAnnotations
index|[
name|p
operator|++
index|]
argument_list|,
name|errors
argument_list|)
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|assistKey
argument_list|(
name|method
argument_list|,
name|paramKey
argument_list|,
name|errors
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|paramTypesBuilder
operator|.
name|put
argument_list|(
name|method
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|returnTypesByMethod
operator|=
name|returnTypesBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|paramTypes
operator|=
name|paramTypesBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ErrorsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
name|e
operator|.
name|getErrors
argument_list|()
operator|.
name|getMessages
argument_list|()
argument_list|)
throw|;
block|}
name|factory
operator|=
name|factoryRawType
operator|.
name|cast
argument_list|(
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|factoryRawType
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|factoryRawType
block|}
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|F
name|get
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
comment|/**      * Returns a key similar to {@code key}, but with an {@literal @}Assisted binding annotation.      * This fails if another binding annotation is clobbered in the process. If the key already has      * the {@literal @}Assisted annotation, it is returned as-is to preserve any String value.      */
DECL|method|assistKey
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|assistKey
parameter_list|(
name|Method
name|method
parameter_list|,
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
if|if
condition|(
name|key
operator|.
name|getAnnotationType
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|Key
operator|.
name|get
argument_list|(
name|key
operator|.
name|getTypeLiteral
argument_list|()
argument_list|,
name|DEFAULT_ANNOTATION
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|getAnnotationType
argument_list|()
operator|==
name|Assisted
operator|.
name|class
condition|)
block|{
return|return
name|key
return|;
block|}
else|else
block|{
name|errors
operator|.
name|withSource
argument_list|(
name|method
argument_list|)
operator|.
name|addMessage
argument_list|(
literal|"Only @Assisted is allowed for factory parameters, but found @%s"
argument_list|,
name|key
operator|.
name|getAnnotationType
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|errors
operator|.
name|toException
argument_list|()
throw|;
block|}
block|}
comment|/**      * At injector-creation time, we initialize the invocation handler. At this time we make sure      * all factory methods will be able to build the target types.      */
annotation|@
name|Inject
DECL|method|initialize
name|void
name|initialize
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|injector
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|Message
argument_list|(
name|FactoryProvider2
operator|.
name|class
argument_list|,
literal|"Factories.create() factories may only be used in one Injector!"
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|returnTypesByMethod
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
index|[]
name|args
init|=
operator|new
name|Object
index|[
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|args
argument_list|,
literal|"dummy object for validating Factories"
argument_list|)
expr_stmt|;
name|getBindingFromNewInjector
argument_list|(
name|method
argument_list|,
name|args
argument_list|)
expr_stmt|;
comment|// throws if the binding isn't properly configured
block|}
block|}
comment|/**      * Creates a child injector that binds the args, and returns the binding for the method's result.      */
DECL|method|getBindingFromNewInjector
specifier|public
name|Binding
argument_list|<
name|?
argument_list|>
name|getBindingFromNewInjector
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|,
specifier|final
name|Object
index|[]
name|args
parameter_list|)
block|{
name|checkState
argument_list|(
name|injector
operator|!=
literal|null
argument_list|,
literal|"Factories.create() factories cannot be used until they're initialized by Guice."
argument_list|)
expr_stmt|;
specifier|final
name|Key
argument_list|<
name|?
argument_list|>
name|returnType
init|=
name|returnTypesByMethod
operator|.
name|get
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|Module
name|assistedModule
init|=
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// raw keys are necessary for the args array and return value
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|Binder
name|binder
init|=
name|binder
argument_list|()
operator|.
name|withSource
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|int
name|p
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Key
argument_list|<
name|?
argument_list|>
name|paramKey
range|:
name|paramTypes
operator|.
name|get
argument_list|(
name|method
argument_list|)
control|)
block|{
comment|// Wrap in a Provider to cover null, and to prevent Guice from injecting the parameter
name|binder
operator|.
name|bind
argument_list|(
operator|(
name|Key
operator|)
name|paramKey
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
name|args
index|[
name|p
operator|++
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|producedType
operator|!=
literal|null
operator|&&
operator|!
name|returnType
operator|.
name|equals
argument_list|(
name|producedType
argument_list|)
condition|)
block|{
name|binder
operator|.
name|bind
argument_list|(
name|returnType
argument_list|)
operator|.
name|to
argument_list|(
operator|(
name|Key
operator|)
name|producedType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|binder
operator|.
name|bind
argument_list|(
name|returnType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Injector
name|forCreate
init|=
name|injector
operator|.
name|createChildInjector
argument_list|(
name|assistedModule
argument_list|)
decl_stmt|;
return|return
name|forCreate
operator|.
name|getBinding
argument_list|(
name|returnType
argument_list|)
return|;
block|}
comment|/**      * When a factory method is invoked, we create a child injector that binds all parameters, then      * use that to get an instance of the return type.      */
annotation|@
name|Override
DECL|method|invoke
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
specifier|final
name|Method
name|method
parameter_list|,
specifier|final
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|Object
operator|.
name|class
condition|)
block|{
return|return
name|method
operator|.
name|invoke
argument_list|(
name|this
argument_list|,
name|args
argument_list|)
return|;
block|}
name|Provider
argument_list|<
name|?
argument_list|>
name|provider
init|=
name|getBindingFromNewInjector
argument_list|(
name|method
argument_list|,
name|args
argument_list|)
operator|.
name|getProvider
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|provider
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ProvisionException
name|e
parameter_list|)
block|{
comment|// if this is an exception declared by the factory method, throw it as-is
if|if
condition|(
name|e
operator|.
name|getErrorMessages
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Message
name|onlyError
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|e
operator|.
name|getErrorMessages
argument_list|()
argument_list|)
decl_stmt|;
name|Throwable
name|cause
init|=
name|onlyError
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
operator|&&
name|canRethrow
argument_list|(
name|method
argument_list|,
name|cause
argument_list|)
condition|)
block|{
throw|throw
name|cause
throw|;
block|}
block|}
throw|throw
name|e
throw|;
block|}
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
name|factory
operator|.
name|getClass
argument_list|()
operator|.
name|getInterfaces
argument_list|()
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" for "
operator|+
name|producedType
operator|.
name|getTypeLiteral
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|==
name|factory
return|;
block|}
comment|/**      * Returns true if {@code thrown} can be thrown by {@code invoked} without wrapping.      */
DECL|method|canRethrow
specifier|static
name|boolean
name|canRethrow
parameter_list|(
name|Method
name|invoked
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
if|if
condition|(
name|thrown
operator|instanceof
name|Error
operator|||
name|thrown
operator|instanceof
name|RuntimeException
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|declared
range|:
name|invoked
operator|.
name|getExceptionTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|declared
operator|.
name|isInstance
argument_list|(
name|thrown
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

