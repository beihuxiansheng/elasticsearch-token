begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BindingAnnotation
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
name|ScopeAnnotation
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
name|annotation
operator|.
name|Retention
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
name|RetentionPolicy
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
name|Member
import|;
end_import

begin_comment
comment|/**  * Annotation utilities.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|Annotations
specifier|public
class|class
name|Annotations
block|{
comment|/**      * Returns true if the given annotation is retained at runtime.      */
DECL|method|isRetainedAtRuntime
specifier|public
specifier|static
name|boolean
name|isRetainedAtRuntime
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
name|Retention
name|retention
init|=
name|annotationType
operator|.
name|getAnnotation
argument_list|(
name|Retention
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|retention
operator|!=
literal|null
operator|&&
name|retention
operator|.
name|value
argument_list|()
operator|==
name|RetentionPolicy
operator|.
name|RUNTIME
return|;
block|}
comment|/**      * Returns the scope annotation on {@code type}, or null if none is specified.      */
DECL|method|findScopeAnnotation
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|findScopeAnnotation
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|implementation
parameter_list|)
block|{
return|return
name|findScopeAnnotation
argument_list|(
name|errors
argument_list|,
name|implementation
operator|.
name|getAnnotations
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the scoping annotation, or null if there isn't one.      */
DECL|method|findScopeAnnotation
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|findScopeAnnotation
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|Annotation
index|[]
name|annotations
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|found
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Annotation
name|annotation
range|:
name|annotations
control|)
block|{
if|if
condition|(
name|annotation
operator|.
name|annotationType
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|ScopeAnnotation
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|found
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|duplicateScopeAnnotations
argument_list|(
name|found
argument_list|,
name|annotation
operator|.
name|annotationType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|found
operator|=
name|annotation
operator|.
name|annotationType
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|found
return|;
block|}
DECL|method|isScopeAnnotation
specifier|public
specifier|static
name|boolean
name|isScopeAnnotation
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
return|return
name|annotationType
operator|.
name|getAnnotation
argument_list|(
name|ScopeAnnotation
operator|.
name|class
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Adds an error if there is a misplaced annotations on {@code type}. Scoping      * annotations are not allowed on abstract classes or interfaces.      */
DECL|method|checkForMisplacedScopeAnnotations
specifier|public
specifier|static
name|void
name|checkForMisplacedScopeAnnotations
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Object
name|source
parameter_list|,
name|Errors
name|errors
parameter_list|)
block|{
if|if
condition|(
name|Classes
operator|.
name|isConcrete
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|scopeAnnotation
init|=
name|findScopeAnnotation
argument_list|(
name|errors
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|scopeAnnotation
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|withSource
argument_list|(
name|type
argument_list|)
operator|.
name|scopeAnnotationOnAbstractType
argument_list|(
name|scopeAnnotation
argument_list|,
name|type
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Gets a key for the given type, member and annotations.      */
DECL|method|getKey
specifier|public
specifier|static
name|Key
argument_list|<
name|?
argument_list|>
name|getKey
parameter_list|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Member
name|member
parameter_list|,
name|Annotation
index|[]
name|annotations
parameter_list|,
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
name|int
name|numErrorsBefore
init|=
name|errors
operator|.
name|size
argument_list|()
decl_stmt|;
name|Annotation
name|found
init|=
name|findBindingAnnotation
argument_list|(
name|errors
argument_list|,
name|member
argument_list|,
name|annotations
argument_list|)
decl_stmt|;
name|errors
operator|.
name|throwIfNewErrors
argument_list|(
name|numErrorsBefore
argument_list|)
expr_stmt|;
return|return
name|found
operator|==
literal|null
condition|?
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
else|:
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|,
name|found
argument_list|)
return|;
block|}
comment|/**      * Returns the binding annotation on {@code member}, or null if there isn't one.      */
DECL|method|findBindingAnnotation
specifier|public
specifier|static
name|Annotation
name|findBindingAnnotation
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|Member
name|member
parameter_list|,
name|Annotation
index|[]
name|annotations
parameter_list|)
block|{
name|Annotation
name|found
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Annotation
name|annotation
range|:
name|annotations
control|)
block|{
if|if
condition|(
name|annotation
operator|.
name|annotationType
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|BindingAnnotation
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|found
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|duplicateBindingAnnotations
argument_list|(
name|member
argument_list|,
name|found
operator|.
name|annotationType
argument_list|()
argument_list|,
name|annotation
operator|.
name|annotationType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|found
operator|=
name|annotation
expr_stmt|;
block|}
block|}
block|}
return|return
name|found
return|;
block|}
block|}
end_class

end_unit

