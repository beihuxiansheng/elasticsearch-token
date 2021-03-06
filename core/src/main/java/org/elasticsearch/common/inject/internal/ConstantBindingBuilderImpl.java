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
name|ConstantBindingBuilder
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
import|;
end_import

begin_comment
comment|/**  * Bind a constant.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|ConstantBindingBuilderImpl
specifier|public
specifier|final
class|class
name|ConstantBindingBuilderImpl
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractBindingBuilder
argument_list|<
name|T
argument_list|>
implements|implements
name|AnnotatedConstantBindingBuilder
implements|,
name|ConstantBindingBuilder
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// constant bindings start out with T unknown
DECL|method|ConstantBindingBuilderImpl
specifier|public
name|ConstantBindingBuilderImpl
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
parameter_list|)
block|{
name|super
argument_list|(
name|binder
argument_list|,
name|elements
argument_list|,
name|source
argument_list|,
operator|(
name|Key
argument_list|<
name|T
argument_list|>
operator|)
name|NULL_KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|annotatedWith
specifier|public
name|ConstantBindingBuilder
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
block|{
name|annotatedWithInternal
argument_list|(
name|annotationType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|annotatedWith
specifier|public
name|ConstantBindingBuilder
name|annotatedWith
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{
name|annotatedWithInternal
argument_list|(
name|annotation
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Long
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Boolean
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Double
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Float
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Short
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Character
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
name|void
name|to
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|Class
operator|.
name|class
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|to
specifier|public
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|void
name|to
parameter_list|(
specifier|final
name|E
name|value
parameter_list|)
block|{
name|toConstant
argument_list|(
name|value
operator|.
name|getDeclaringClass
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|toConstant
specifier|private
name|void
name|toConstant
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Object
name|instance
parameter_list|)
block|{
comment|// this type will define T, so these assignments are safe
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|T
argument_list|>
name|typeAsClassT
init|=
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|type
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|instanceAsT
init|=
operator|(
name|T
operator|)
name|instance
decl_stmt|;
if|if
condition|(
name|keyTypeIsSet
argument_list|()
condition|)
block|{
name|binder
operator|.
name|addError
argument_list|(
name|CONSTANT_VALUE_ALREADY_SET
argument_list|)
expr_stmt|;
return|return;
block|}
name|BindingImpl
argument_list|<
name|T
argument_list|>
name|base
init|=
name|getBinding
argument_list|()
decl_stmt|;
name|Key
argument_list|<
name|T
argument_list|>
name|key
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|getKey
argument_list|()
operator|.
name|getAnnotation
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|key
operator|=
name|Key
operator|.
name|get
argument_list|(
name|typeAsClassT
argument_list|,
name|base
operator|.
name|getKey
argument_list|()
operator|.
name|getAnnotation
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|base
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
name|key
operator|=
name|Key
operator|.
name|get
argument_list|(
name|typeAsClassT
argument_list|,
name|base
operator|.
name|getKey
argument_list|()
operator|.
name|getAnnotationType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
name|Key
operator|.
name|get
argument_list|(
name|typeAsClassT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|instanceAsT
operator|==
literal|null
condition|)
block|{
name|binder
operator|.
name|addError
argument_list|(
name|BINDING_TO_NULL
argument_list|)
expr_stmt|;
block|}
name|setBinding
argument_list|(
operator|new
name|InstanceBindingImpl
argument_list|<>
argument_list|(
name|base
operator|.
name|getSource
argument_list|()
argument_list|,
name|key
argument_list|,
name|base
operator|.
name|getScoping
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|instanceAsT
argument_list|)
argument_list|)
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
literal|"ConstantBindingBuilder"
return|;
block|}
block|}
end_class

end_unit

