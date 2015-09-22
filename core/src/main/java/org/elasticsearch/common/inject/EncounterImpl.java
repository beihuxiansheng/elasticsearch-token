begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2009 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|InjectionListener
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
name|spi
operator|.
name|TypeEncounter
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

begin_comment
comment|/**  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|EncounterImpl
specifier|final
class|class
name|EncounterImpl
parameter_list|<
name|T
parameter_list|>
implements|implements
name|TypeEncounter
argument_list|<
name|T
argument_list|>
block|{
DECL|field|errors
specifier|private
specifier|final
name|Errors
name|errors
decl_stmt|;
DECL|field|lookups
specifier|private
specifier|final
name|Lookups
name|lookups
decl_stmt|;
DECL|field|membersInjectors
specifier|private
name|List
argument_list|<
name|MembersInjector
argument_list|<
name|?
super|super
name|T
argument_list|>
argument_list|>
name|membersInjectors
decl_stmt|;
comment|// lazy
DECL|field|injectionListeners
specifier|private
name|List
argument_list|<
name|InjectionListener
argument_list|<
name|?
super|super
name|T
argument_list|>
argument_list|>
name|injectionListeners
decl_stmt|;
comment|// lazy
DECL|field|valid
specifier|private
name|boolean
name|valid
init|=
literal|true
decl_stmt|;
DECL|method|EncounterImpl
specifier|public
name|EncounterImpl
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|Lookups
name|lookups
parameter_list|)
block|{
name|this
operator|.
name|errors
operator|=
name|errors
expr_stmt|;
name|this
operator|.
name|lookups
operator|=
name|lookups
expr_stmt|;
block|}
DECL|method|invalidate
specifier|public
name|void
name|invalidate
parameter_list|()
block|{
name|valid
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|getMembersInjectors
specifier|public
name|List
argument_list|<
name|MembersInjector
argument_list|<
name|?
super|super
name|T
argument_list|>
argument_list|>
name|getMembersInjectors
parameter_list|()
block|{
return|return
name|membersInjectors
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|MembersInjector
argument_list|<
name|?
super|super
name|T
argument_list|>
operator|>
name|emptyList
argument_list|()
else|:
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|membersInjectors
argument_list|)
return|;
block|}
DECL|method|getInjectionListeners
specifier|public
name|List
argument_list|<
name|InjectionListener
argument_list|<
name|?
super|super
name|T
argument_list|>
argument_list|>
name|getInjectionListeners
parameter_list|()
block|{
return|return
name|injectionListeners
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|InjectionListener
argument_list|<
name|?
super|super
name|T
argument_list|>
operator|>
name|emptyList
argument_list|()
else|:
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|injectionListeners
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|public
name|void
name|register
parameter_list|(
name|MembersInjector
argument_list|<
name|?
super|super
name|T
argument_list|>
name|membersInjector
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
if|if
condition|(
name|membersInjectors
operator|==
literal|null
condition|)
block|{
name|membersInjectors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|membersInjectors
operator|.
name|add
argument_list|(
name|membersInjector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|public
name|void
name|register
parameter_list|(
name|InjectionListener
argument_list|<
name|?
super|super
name|T
argument_list|>
name|injectionListener
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
if|if
condition|(
name|injectionListeners
operator|==
literal|null
condition|)
block|{
name|injectionListeners
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|injectionListeners
operator|.
name|add
argument_list|(
name|injectionListener
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
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
name|errors
operator|.
name|addMessage
argument_list|(
name|message
argument_list|,
name|arguments
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
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
name|errors
operator|.
name|errorInUserCode
argument_list|(
name|t
argument_list|,
literal|"An exception was caught and reported. Message: %s"
argument_list|,
name|t
operator|.
name|getMessage
argument_list|()
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
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
name|errors
operator|.
name|addMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
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
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
return|return
name|lookups
operator|.
name|getProvider
argument_list|(
name|key
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
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Encounters may not be used after hear() returns."
argument_list|)
throw|;
block|}
return|return
name|lookups
operator|.
name|getMembersInjector
argument_list|(
name|typeLiteral
argument_list|)
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
block|}
end_class

end_unit

