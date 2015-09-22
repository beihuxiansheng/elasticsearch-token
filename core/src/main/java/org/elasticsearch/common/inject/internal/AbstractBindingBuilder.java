begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
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
name|spi
operator|.
name|Element
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
name|InstanceBinding
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

begin_comment
comment|/**  * Bind a value or constant.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|AbstractBindingBuilder
specifier|public
specifier|abstract
class|class
name|AbstractBindingBuilder
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|IMPLEMENTATION_ALREADY_SET
specifier|public
specifier|static
specifier|final
name|String
name|IMPLEMENTATION_ALREADY_SET
init|=
literal|"Implementation is set more than once."
decl_stmt|;
DECL|field|SINGLE_INSTANCE_AND_SCOPE
specifier|public
specifier|static
specifier|final
name|String
name|SINGLE_INSTANCE_AND_SCOPE
init|=
literal|"Setting the scope is not permitted when binding to a single instance."
decl_stmt|;
DECL|field|SCOPE_ALREADY_SET
specifier|public
specifier|static
specifier|final
name|String
name|SCOPE_ALREADY_SET
init|=
literal|"Scope is set more than once."
decl_stmt|;
DECL|field|BINDING_TO_NULL
specifier|public
specifier|static
specifier|final
name|String
name|BINDING_TO_NULL
init|=
literal|"Binding to null instances is not allowed. "
operator|+
literal|"Use toProvider(Providers.of(null)) if this is your intended behaviour."
decl_stmt|;
DECL|field|CONSTANT_VALUE_ALREADY_SET
specifier|public
specifier|static
specifier|final
name|String
name|CONSTANT_VALUE_ALREADY_SET
init|=
literal|"Constant value is set more than once."
decl_stmt|;
DECL|field|ANNOTATION_ALREADY_SPECIFIED
specifier|public
specifier|static
specifier|final
name|String
name|ANNOTATION_ALREADY_SPECIFIED
init|=
literal|"More than one annotation is specified for this binding."
decl_stmt|;
DECL|field|NULL_KEY
specifier|protected
specifier|static
specifier|final
name|Key
argument_list|<
name|?
argument_list|>
name|NULL_KEY
init|=
name|Key
operator|.
name|get
argument_list|(
name|Void
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|elements
specifier|protected
name|List
argument_list|<
name|Element
argument_list|>
name|elements
decl_stmt|;
DECL|field|position
specifier|protected
name|int
name|position
decl_stmt|;
DECL|field|binder
specifier|protected
specifier|final
name|Binder
name|binder
decl_stmt|;
DECL|field|binding
specifier|private
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|binding
decl_stmt|;
DECL|method|AbstractBindingBuilder
specifier|public
name|AbstractBindingBuilder
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|List
argument_list|<
name|Element
argument_list|>
name|elements
parameter_list|,
name|Object
name|source
parameter_list|,
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
name|this
operator|.
name|binder
operator|=
name|binder
expr_stmt|;
name|this
operator|.
name|elements
operator|=
name|elements
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|elements
operator|.
name|size
argument_list|()
expr_stmt|;
name|this
operator|.
name|binding
operator|=
operator|new
name|UntargettedBindingImpl
argument_list|<>
argument_list|(
name|source
argument_list|,
name|key
argument_list|,
name|Scoping
operator|.
name|UNSCOPED
argument_list|)
expr_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|position
argument_list|,
name|this
operator|.
name|binding
argument_list|)
expr_stmt|;
block|}
DECL|method|getBinding
specifier|protected
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|getBinding
parameter_list|()
block|{
return|return
name|binding
return|;
block|}
DECL|method|setBinding
specifier|protected
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|setBinding
parameter_list|(
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|binding
parameter_list|)
block|{
name|this
operator|.
name|binding
operator|=
name|binding
expr_stmt|;
name|elements
operator|.
name|set
argument_list|(
name|position
argument_list|,
name|binding
argument_list|)
expr_stmt|;
return|return
name|binding
return|;
block|}
comment|/**      * Sets the binding to a copy with the specified annotation on the bound key      */
DECL|method|annotatedWithInternal
specifier|protected
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|annotatedWithInternal
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|annotationType
argument_list|,
literal|"annotationType"
argument_list|)
expr_stmt|;
name|checkNotAnnotated
argument_list|()
expr_stmt|;
return|return
name|setBinding
argument_list|(
name|binding
operator|.
name|withKey
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|this
operator|.
name|binding
operator|.
name|getKey
argument_list|()
operator|.
name|getTypeLiteral
argument_list|()
argument_list|,
name|annotationType
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Sets the binding to a copy with the specified annotation on the bound key      */
DECL|method|annotatedWithInternal
specifier|protected
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|annotatedWithInternal
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|annotation
argument_list|,
literal|"annotation"
argument_list|)
expr_stmt|;
name|checkNotAnnotated
argument_list|()
expr_stmt|;
return|return
name|setBinding
argument_list|(
name|binding
operator|.
name|withKey
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|this
operator|.
name|binding
operator|.
name|getKey
argument_list|()
operator|.
name|getTypeLiteral
argument_list|()
argument_list|,
name|annotation
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|in
specifier|public
name|void
name|in
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|scopeAnnotation
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scopeAnnotation
argument_list|,
literal|"scopeAnnotation"
argument_list|)
expr_stmt|;
name|checkNotScoped
argument_list|()
expr_stmt|;
name|setBinding
argument_list|(
name|getBinding
argument_list|()
operator|.
name|withScoping
argument_list|(
name|Scoping
operator|.
name|forAnnotation
argument_list|(
name|scopeAnnotation
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|in
specifier|public
name|void
name|in
parameter_list|(
specifier|final
name|Scope
name|scope
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scope
argument_list|,
literal|"scope"
argument_list|)
expr_stmt|;
name|checkNotScoped
argument_list|()
expr_stmt|;
name|setBinding
argument_list|(
name|getBinding
argument_list|()
operator|.
name|withScoping
argument_list|(
name|Scoping
operator|.
name|forInstance
argument_list|(
name|scope
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|asEagerSingleton
specifier|public
name|void
name|asEagerSingleton
parameter_list|()
block|{
name|checkNotScoped
argument_list|()
expr_stmt|;
name|setBinding
argument_list|(
name|getBinding
argument_list|()
operator|.
name|withScoping
argument_list|(
name|Scoping
operator|.
name|EAGER_SINGLETON
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|keyTypeIsSet
specifier|protected
name|boolean
name|keyTypeIsSet
parameter_list|()
block|{
return|return
operator|!
name|Void
operator|.
name|class
operator|.
name|equals
argument_list|(
name|binding
operator|.
name|getKey
argument_list|()
operator|.
name|getTypeLiteral
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|checkNotTargetted
specifier|protected
name|void
name|checkNotTargetted
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|binding
operator|instanceof
name|UntargettedBindingImpl
operator|)
condition|)
block|{
name|binder
operator|.
name|addError
argument_list|(
name|IMPLEMENTATION_ALREADY_SET
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkNotAnnotated
specifier|protected
name|void
name|checkNotAnnotated
parameter_list|()
block|{
if|if
condition|(
name|binding
operator|.
name|getKey
argument_list|()
operator|.
name|getAnnotationType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|binder
operator|.
name|addError
argument_list|(
name|ANNOTATION_ALREADY_SPECIFIED
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkNotScoped
specifier|protected
name|void
name|checkNotScoped
parameter_list|()
block|{
comment|// Scoping isn't allowed when we have only one instance.
if|if
condition|(
name|binding
operator|instanceof
name|InstanceBinding
condition|)
block|{
name|binder
operator|.
name|addError
argument_list|(
name|SINGLE_INSTANCE_AND_SCOPE
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|binding
operator|.
name|getScoping
argument_list|()
operator|.
name|isExplicitlyScoped
argument_list|()
condition|)
block|{
name|binder
operator|.
name|addError
argument_list|(
name|SCOPE_ALREADY_SET
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

