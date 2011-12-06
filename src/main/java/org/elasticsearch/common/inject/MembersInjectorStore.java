begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2009 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ImmutableList
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
name|internal
operator|.
name|FailableCache
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
name|TypeListenerBinding
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
name|Field
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
comment|/**  * Members injectors by type.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|MembersInjectorStore
class|class
name|MembersInjectorStore
block|{
DECL|field|injector
specifier|private
specifier|final
name|InjectorImpl
name|injector
decl_stmt|;
DECL|field|typeListenerBindings
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|TypeListenerBinding
argument_list|>
name|typeListenerBindings
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|FailableCache
argument_list|<
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|,
name|MembersInjectorImpl
argument_list|<
name|?
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|FailableCache
argument_list|<
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|,
name|MembersInjectorImpl
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|MembersInjectorImpl
argument_list|<
name|?
argument_list|>
name|create
parameter_list|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
return|return
name|createWithListeners
argument_list|(
name|type
argument_list|,
name|errors
argument_list|)
return|;
block|}
block|}
empty_stmt|;
DECL|method|MembersInjectorStore
name|MembersInjectorStore
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|List
argument_list|<
name|TypeListenerBinding
argument_list|>
name|typeListenerBindings
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
name|typeListenerBindings
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|typeListenerBindings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns true if any type listeners are installed. Other code may take shortcuts when there      * aren't any type listeners.      */
DECL|method|hasTypeListeners
specifier|public
name|boolean
name|hasTypeListeners
parameter_list|()
block|{
return|return
operator|!
name|typeListenerBindings
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Returns a new complete members injector with injection listeners registered.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// the MembersInjector type always agrees with the passed type
DECL|method|get
specifier|public
parameter_list|<
name|T
parameter_list|>
name|MembersInjectorImpl
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|TypeLiteral
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
return|return
operator|(
name|MembersInjectorImpl
argument_list|<
name|T
argument_list|>
operator|)
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|errors
argument_list|)
return|;
block|}
comment|/**      * Creates a new members injector and attaches both injection listeners and method aspects.      */
DECL|method|createWithListeners
specifier|private
parameter_list|<
name|T
parameter_list|>
name|MembersInjectorImpl
argument_list|<
name|T
argument_list|>
name|createWithListeners
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
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
name|Set
argument_list|<
name|InjectionPoint
argument_list|>
name|injectionPoints
decl_stmt|;
try|try
block|{
name|injectionPoints
operator|=
name|InjectionPoint
operator|.
name|forInstanceMethodsAndFields
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
name|errors
operator|.
name|merge
argument_list|(
name|e
operator|.
name|getErrorMessages
argument_list|()
argument_list|)
expr_stmt|;
name|injectionPoints
operator|=
name|e
operator|.
name|getPartialValue
argument_list|()
expr_stmt|;
block|}
name|ImmutableList
argument_list|<
name|SingleMemberInjector
argument_list|>
name|injectors
init|=
name|getInjectors
argument_list|(
name|injectionPoints
argument_list|,
name|errors
argument_list|)
decl_stmt|;
name|errors
operator|.
name|throwIfNewErrors
argument_list|(
name|numErrorsBefore
argument_list|)
expr_stmt|;
name|EncounterImpl
argument_list|<
name|T
argument_list|>
name|encounter
init|=
operator|new
name|EncounterImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|errors
argument_list|,
name|injector
operator|.
name|lookups
argument_list|)
decl_stmt|;
for|for
control|(
name|TypeListenerBinding
name|typeListener
range|:
name|typeListenerBindings
control|)
block|{
if|if
condition|(
name|typeListener
operator|.
name|getTypeMatcher
argument_list|()
operator|.
name|matches
argument_list|(
name|type
argument_list|)
condition|)
block|{
try|try
block|{
name|typeListener
operator|.
name|getListener
argument_list|()
operator|.
name|hear
argument_list|(
name|type
argument_list|,
name|encounter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|errors
operator|.
name|errorNotifyingTypeListener
argument_list|(
name|typeListener
argument_list|,
name|type
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|encounter
operator|.
name|invalidate
argument_list|()
expr_stmt|;
name|errors
operator|.
name|throwIfNewErrors
argument_list|(
name|numErrorsBefore
argument_list|)
expr_stmt|;
return|return
operator|new
name|MembersInjectorImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|injector
argument_list|,
name|type
argument_list|,
name|encounter
argument_list|,
name|injectors
argument_list|)
return|;
block|}
comment|/**      * Returns the injectors for the specified injection points.      */
DECL|method|getInjectors
name|ImmutableList
argument_list|<
name|SingleMemberInjector
argument_list|>
name|getInjectors
parameter_list|(
name|Set
argument_list|<
name|InjectionPoint
argument_list|>
name|injectionPoints
parameter_list|,
name|Errors
name|errors
parameter_list|)
block|{
name|List
argument_list|<
name|SingleMemberInjector
argument_list|>
name|injectors
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|InjectionPoint
name|injectionPoint
range|:
name|injectionPoints
control|)
block|{
try|try
block|{
name|Errors
name|errorsForMember
init|=
name|injectionPoint
operator|.
name|isOptional
argument_list|()
condition|?
operator|new
name|Errors
argument_list|(
name|injectionPoint
argument_list|)
else|:
name|errors
operator|.
name|withSource
argument_list|(
name|injectionPoint
argument_list|)
decl_stmt|;
name|SingleMemberInjector
name|injector
init|=
name|injectionPoint
operator|.
name|getMember
argument_list|()
operator|instanceof
name|Field
condition|?
operator|new
name|SingleFieldInjector
argument_list|(
name|this
operator|.
name|injector
argument_list|,
name|injectionPoint
argument_list|,
name|errorsForMember
argument_list|)
else|:
operator|new
name|SingleMethodInjector
argument_list|(
name|this
operator|.
name|injector
argument_list|,
name|injectionPoint
argument_list|,
name|errorsForMember
argument_list|)
decl_stmt|;
name|injectors
operator|.
name|add
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ErrorsException
name|ignoredForNow
parameter_list|)
block|{
comment|// ignored for now
block|}
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|injectors
argument_list|)
return|;
block|}
block|}
end_class

end_unit

