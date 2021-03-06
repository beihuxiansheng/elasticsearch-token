begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Annotations
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
name|MoreTypes
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
name|ToStringBuilder
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
name|Type
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
comment|/**  * Binding key consisting of an injection type and an optional annotation.  * Matches the type and annotation at a point of injection.  *<p>  * For example, {@code Key.get(Service.class, Transactional.class)} will  * match:  *<pre>  *   {@literal @}Inject  *   public void setService({@literal @}Transactional Service service) {  *     ...  *   }  *</pre>  *<p>  * {@code Key} supports generic types via subclassing just like {@link  * TypeLiteral}.  *<p>  * Keys do not differentiate between primitive types (int, char, etc.) and  * their corresponding wrapper types (Integer, Character, etc.). Primitive  * types will be replaced with their wrapper types when keys are created.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|Key
specifier|public
class|class
name|Key
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|annotationStrategy
specifier|private
specifier|final
name|AnnotationStrategy
name|annotationStrategy
decl_stmt|;
DECL|field|typeLiteral
specifier|private
specifier|final
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
comment|/**      * Constructs a new key. Derives the type from this class's type parameter.      *<p>      * Clients create an empty anonymous subclass. Doing so embeds the type      * parameter in the anonymous class's type hierarchy so we can reconstitute it      * at runtime despite erasure.      *<p>      * Example usage for a binding of type {@code Foo} annotated with      * {@code @Bar}:      *<p>      * {@code new Key<Foo>(Bar.class) {}}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|Key
specifier|protected
name|Key
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
name|this
operator|.
name|annotationStrategy
operator|=
name|strategyFor
argument_list|(
name|annotationType
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeLiteral
operator|=
operator|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
operator|)
name|TypeLiteral
operator|.
name|fromSuperclassTypeParameter
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new key. Derives the type from this class's type parameter.      *<p>      * Clients create an empty anonymous subclass. Doing so embeds the type      * parameter in the anonymous class's type hierarchy so we can reconstitute it      * at runtime despite erasure.      *<p>      * Example usage for a binding of type {@code Foo} annotated with      * {@code @Bar}:      *<p>      * {@code new Key<Foo>(new Bar()) {}}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|Key
specifier|protected
name|Key
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{
comment|// no usages, not test-covered
name|this
operator|.
name|annotationStrategy
operator|=
name|strategyFor
argument_list|(
name|annotation
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeLiteral
operator|=
operator|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
operator|)
name|TypeLiteral
operator|.
name|fromSuperclassTypeParameter
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new key. Derives the type from this class's type parameter.      *<p>      * Clients create an empty anonymous subclass. Doing so embeds the type      * parameter in the anonymous class's type hierarchy so we can reconstitute it      * at runtime despite erasure.      *<p>      * Example usage for a binding of type {@code Foo}:      *<p>      * {@code new Key<Foo>() {}}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|Key
specifier|protected
name|Key
parameter_list|()
block|{
name|this
operator|.
name|annotationStrategy
operator|=
name|NullAnnotationStrategy
operator|.
name|INSTANCE
expr_stmt|;
name|this
operator|.
name|typeLiteral
operator|=
operator|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
operator|)
name|TypeLiteral
operator|.
name|fromSuperclassTypeParameter
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unsafe. Constructs a key from a manually specified type.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|Key
specifier|private
name|Key
parameter_list|(
name|Type
name|type
parameter_list|,
name|AnnotationStrategy
name|annotationStrategy
parameter_list|)
block|{
name|this
operator|.
name|annotationStrategy
operator|=
name|annotationStrategy
expr_stmt|;
name|this
operator|.
name|typeLiteral
operator|=
name|MoreTypes
operator|.
name|makeKeySafe
argument_list|(
operator|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
operator|)
name|TypeLiteral
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a key from a manually specified type.      */
DECL|method|Key
specifier|private
name|Key
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|,
name|AnnotationStrategy
name|annotationStrategy
parameter_list|)
block|{
name|this
operator|.
name|annotationStrategy
operator|=
name|annotationStrategy
expr_stmt|;
name|this
operator|.
name|typeLiteral
operator|=
name|MoreTypes
operator|.
name|makeKeySafe
argument_list|(
name|typeLiteral
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
DECL|method|computeHashCode
specifier|private
name|int
name|computeHashCode
parameter_list|()
block|{
return|return
name|typeLiteral
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|annotationStrategy
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Gets the key type.      */
DECL|method|getTypeLiteral
specifier|public
specifier|final
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|getTypeLiteral
parameter_list|()
block|{
return|return
name|typeLiteral
return|;
block|}
comment|/**      * Gets the annotation type.      */
DECL|method|getAnnotationType
specifier|public
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|getAnnotationType
parameter_list|()
block|{
return|return
name|annotationStrategy
operator|.
name|getAnnotationType
argument_list|()
return|;
block|}
comment|/**      * Gets the annotation.      */
DECL|method|getAnnotation
specifier|public
specifier|final
name|Annotation
name|getAnnotation
parameter_list|()
block|{
return|return
name|annotationStrategy
operator|.
name|getAnnotation
argument_list|()
return|;
block|}
DECL|method|hasAnnotationType
name|boolean
name|hasAnnotationType
parameter_list|()
block|{
return|return
name|annotationStrategy
operator|.
name|getAnnotationType
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|getAnnotationName
name|String
name|getAnnotationName
parameter_list|()
block|{
name|Annotation
name|annotation
init|=
name|annotationStrategy
operator|.
name|getAnnotation
argument_list|()
decl_stmt|;
if|if
condition|(
name|annotation
operator|!=
literal|null
condition|)
block|{
return|return
name|annotation
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// not test-covered
return|return
name|annotationStrategy
operator|.
name|getAnnotationType
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getRawType
name|Class
argument_list|<
name|?
super|super
name|T
argument_list|>
name|getRawType
parameter_list|()
block|{
return|return
name|typeLiteral
operator|.
name|getRawType
argument_list|()
return|;
block|}
comment|/**      * Gets the key of this key's provider.      */
DECL|method|providerKey
name|Key
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
name|providerKey
parameter_list|()
block|{
return|return
name|ofType
argument_list|(
name|typeLiteral
operator|.
name|providerType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Key
argument_list|<
name|?
argument_list|>
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Key
argument_list|<
name|?
argument_list|>
name|other
init|=
operator|(
name|Key
argument_list|<
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
return|return
name|annotationStrategy
operator|.
name|equals
argument_list|(
name|other
operator|.
name|annotationStrategy
argument_list|)
operator|&&
name|typeLiteral
operator|.
name|equals
argument_list|(
name|other
operator|.
name|typeLiteral
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|ToStringBuilder
argument_list|(
name|Key
operator|.
name|class
argument_list|)
operator|.
name|add
argument_list|(
literal|"type"
argument_list|,
name|typeLiteral
argument_list|)
operator|.
name|add
argument_list|(
literal|"annotation"
argument_list|,
name|annotationStrategy
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation strategy.      */
DECL|method|get
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|AnnotationStrategy
name|annotationStrategy
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|type
argument_list|,
name|annotationStrategy
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type.      */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|type
argument_list|,
name|NullAnnotationStrategy
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation type.      */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
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
operator|new
name|Key
argument_list|<>
argument_list|(
name|type
argument_list|,
name|strategyFor
argument_list|(
name|annotationType
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation.      */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|Annotation
name|annotation
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|type
argument_list|,
name|strategyFor
argument_list|(
name|annotation
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type.      */
DECL|method|get
specifier|public
specifier|static
name|Key
argument_list|<
name|?
argument_list|>
name|get
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<
name|Object
argument_list|>
argument_list|(
name|type
argument_list|,
name|NullAnnotationStrategy
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation type.      */
DECL|method|get
specifier|public
specifier|static
name|Key
argument_list|<
name|?
argument_list|>
name|get
parameter_list|(
name|Type
name|type
parameter_list|,
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
operator|new
name|Key
argument_list|<
name|Object
argument_list|>
argument_list|(
name|type
argument_list|,
name|strategyFor
argument_list|(
name|annotationType
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation.      */
DECL|method|get
specifier|public
specifier|static
name|Key
argument_list|<
name|?
argument_list|>
name|get
parameter_list|(
name|Type
name|type
parameter_list|,
name|Annotation
name|annotation
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<
name|Object
argument_list|>
argument_list|(
name|type
argument_list|,
name|strategyFor
argument_list|(
name|annotation
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type.      */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|typeLiteral
argument_list|,
name|NullAnnotationStrategy
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation type.      */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|,
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
operator|new
name|Key
argument_list|<>
argument_list|(
name|typeLiteral
argument_list|,
name|strategyFor
argument_list|(
name|annotationType
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Gets a key for an injection type and an annotation.      */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|,
name|Annotation
name|annotation
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|typeLiteral
argument_list|,
name|strategyFor
argument_list|(
name|annotation
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns a new key of the specified type with the same annotation as this      * key.      */
DECL|method|ofType
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|ofType
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|type
argument_list|,
name|annotationStrategy
argument_list|)
return|;
block|}
comment|/**      * Returns a new key of the specified type with the same annotation as this      * key.      */
DECL|method|ofType
name|Key
argument_list|<
name|?
argument_list|>
name|ofType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<
name|Object
argument_list|>
argument_list|(
name|type
argument_list|,
name|annotationStrategy
argument_list|)
return|;
block|}
comment|/**      * Returns a new key of the specified type with the same annotation as this      * key.      */
DECL|method|ofType
parameter_list|<
name|T
parameter_list|>
name|Key
argument_list|<
name|T
argument_list|>
name|ofType
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|type
argument_list|,
name|annotationStrategy
argument_list|)
return|;
block|}
comment|/**      * Returns true if this key has annotation attributes.      */
DECL|method|hasAttributes
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
name|annotationStrategy
operator|.
name|hasAttributes
argument_list|()
return|;
block|}
comment|/**      * Returns this key without annotation attributes, i.e. with only the      * annotation type.      */
DECL|method|withoutAttributes
name|Key
argument_list|<
name|T
argument_list|>
name|withoutAttributes
parameter_list|()
block|{
return|return
operator|new
name|Key
argument_list|<>
argument_list|(
name|typeLiteral
argument_list|,
name|annotationStrategy
operator|.
name|withoutAttributes
argument_list|()
argument_list|)
return|;
block|}
DECL|interface|AnnotationStrategy
interface|interface
name|AnnotationStrategy
block|{
DECL|method|getAnnotation
name|Annotation
name|getAnnotation
parameter_list|()
function_decl|;
DECL|method|getAnnotationType
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|getAnnotationType
parameter_list|()
function_decl|;
DECL|method|hasAttributes
name|boolean
name|hasAttributes
parameter_list|()
function_decl|;
DECL|method|withoutAttributes
name|AnnotationStrategy
name|withoutAttributes
parameter_list|()
function_decl|;
block|}
comment|/**      * Returns {@code true} if the given annotation type has no attributes.      */
DECL|method|isMarker
specifier|static
name|boolean
name|isMarker
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
name|getMethods
argument_list|()
operator|.
name|length
operator|==
literal|0
return|;
block|}
comment|/**      * Gets the strategy for an annotation.      */
DECL|method|strategyFor
specifier|static
name|AnnotationStrategy
name|strategyFor
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
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
init|=
name|annotation
operator|.
name|annotationType
argument_list|()
decl_stmt|;
name|ensureRetainedAtRuntime
argument_list|(
name|annotationType
argument_list|)
expr_stmt|;
name|ensureIsBindingAnnotation
argument_list|(
name|annotationType
argument_list|)
expr_stmt|;
if|if
condition|(
name|annotationType
operator|.
name|getMethods
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|AnnotationTypeStrategy
argument_list|(
name|annotationType
argument_list|,
name|annotation
argument_list|)
return|;
block|}
return|return
operator|new
name|AnnotationInstanceStrategy
argument_list|(
name|annotation
argument_list|)
return|;
block|}
comment|/**      * Gets the strategy for an annotation type.      */
DECL|method|strategyFor
specifier|static
name|AnnotationStrategy
name|strategyFor
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
literal|"annotation type"
argument_list|)
expr_stmt|;
name|ensureRetainedAtRuntime
argument_list|(
name|annotationType
argument_list|)
expr_stmt|;
name|ensureIsBindingAnnotation
argument_list|(
name|annotationType
argument_list|)
expr_stmt|;
return|return
operator|new
name|AnnotationTypeStrategy
argument_list|(
name|annotationType
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|ensureRetainedAtRuntime
specifier|private
specifier|static
name|void
name|ensureRetainedAtRuntime
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
if|if
condition|(
operator|!
name|Annotations
operator|.
name|isRetainedAtRuntime
argument_list|(
name|annotationType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|annotationType
operator|.
name|getName
argument_list|()
operator|+
literal|" is not retained at runtime. Please annotate it with @Retention(RUNTIME)."
argument_list|)
throw|;
block|}
block|}
DECL|method|ensureIsBindingAnnotation
specifier|private
specifier|static
name|void
name|ensureIsBindingAnnotation
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
if|if
condition|(
operator|!
name|isBindingAnnotation
argument_list|(
name|annotationType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|annotationType
operator|.
name|getName
argument_list|()
operator|+
literal|" is not a binding annotation. Please annotate it with @BindingAnnotation."
argument_list|)
throw|;
block|}
block|}
DECL|enum|NullAnnotationStrategy
enum|enum
name|NullAnnotationStrategy
implements|implements
name|AnnotationStrategy
block|{
DECL|enum constant|INSTANCE
name|INSTANCE
block|;
annotation|@
name|Override
DECL|method|hasAttributes
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|withoutAttributes
specifier|public
name|AnnotationStrategy
name|withoutAttributes
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Key already has no attributes."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAnnotation
specifier|public
name|Annotation
name|getAnnotation
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getAnnotationType
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|getAnnotationType
parameter_list|()
block|{
return|return
literal|null
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
literal|"[none]"
return|;
block|}
block|}
comment|// this class not test-covered
DECL|class|AnnotationInstanceStrategy
specifier|static
class|class
name|AnnotationInstanceStrategy
implements|implements
name|AnnotationStrategy
block|{
DECL|field|annotation
specifier|final
name|Annotation
name|annotation
decl_stmt|;
DECL|method|AnnotationInstanceStrategy
name|AnnotationInstanceStrategy
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{
name|this
operator|.
name|annotation
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|annotation
argument_list|,
literal|"annotation"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasAttributes
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|withoutAttributes
specifier|public
name|AnnotationStrategy
name|withoutAttributes
parameter_list|()
block|{
return|return
operator|new
name|AnnotationTypeStrategy
argument_list|(
name|getAnnotationType
argument_list|()
argument_list|,
name|annotation
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAnnotation
specifier|public
name|Annotation
name|getAnnotation
parameter_list|()
block|{
return|return
name|annotation
return|;
block|}
annotation|@
name|Override
DECL|method|getAnnotationType
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|getAnnotationType
parameter_list|()
block|{
return|return
name|annotation
operator|.
name|annotationType
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
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|AnnotationInstanceStrategy
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AnnotationInstanceStrategy
name|other
init|=
operator|(
name|AnnotationInstanceStrategy
operator|)
name|o
decl_stmt|;
return|return
name|annotation
operator|.
name|equals
argument_list|(
name|other
operator|.
name|annotation
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|annotation
operator|.
name|hashCode
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
name|annotation
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|AnnotationTypeStrategy
specifier|static
class|class
name|AnnotationTypeStrategy
implements|implements
name|AnnotationStrategy
block|{
DECL|field|annotationType
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
decl_stmt|;
comment|// Keep the instance around if we have it so the client can request it.
DECL|field|annotation
specifier|final
name|Annotation
name|annotation
decl_stmt|;
DECL|method|AnnotationTypeStrategy
name|AnnotationTypeStrategy
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|,
name|Annotation
name|annotation
parameter_list|)
block|{
name|this
operator|.
name|annotationType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|annotationType
argument_list|,
literal|"annotation type"
argument_list|)
expr_stmt|;
name|this
operator|.
name|annotation
operator|=
name|annotation
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasAttributes
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|withoutAttributes
specifier|public
name|AnnotationStrategy
name|withoutAttributes
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Key already has no attributes."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAnnotation
specifier|public
name|Annotation
name|getAnnotation
parameter_list|()
block|{
return|return
name|annotation
return|;
block|}
annotation|@
name|Override
DECL|method|getAnnotationType
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|getAnnotationType
parameter_list|()
block|{
return|return
name|annotationType
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
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|AnnotationTypeStrategy
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AnnotationTypeStrategy
name|other
init|=
operator|(
name|AnnotationTypeStrategy
operator|)
name|o
decl_stmt|;
return|return
name|annotationType
operator|.
name|equals
argument_list|(
name|other
operator|.
name|annotationType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|annotationType
operator|.
name|hashCode
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
literal|"@"
operator|+
name|annotationType
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
DECL|method|isBindingAnnotation
specifier|static
name|boolean
name|isBindingAnnotation
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{
return|return
name|isBindingAnnotation
argument_list|(
name|annotation
operator|.
name|annotationType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isBindingAnnotation
specifier|static
name|boolean
name|isBindingAnnotation
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
name|BindingAnnotation
operator|.
name|class
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

