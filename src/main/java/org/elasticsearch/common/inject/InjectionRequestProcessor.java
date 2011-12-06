begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|InternalContext
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
name|InjectionRequest
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
name|StaticInjectionRequest
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
comment|/**  * Handles {@link Binder#requestInjection} and {@link Binder#requestStaticInjection} commands.  *  * @author crazybob@google.com (Bob Lee)  * @author jessewilson@google.com (Jesse Wilson)  * @author mikeward@google.com (Mike Ward)  */
end_comment

begin_class
DECL|class|InjectionRequestProcessor
class|class
name|InjectionRequestProcessor
extends|extends
name|AbstractProcessor
block|{
DECL|field|staticInjections
specifier|private
specifier|final
name|List
argument_list|<
name|StaticInjection
argument_list|>
name|staticInjections
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|initializer
specifier|private
specifier|final
name|Initializer
name|initializer
decl_stmt|;
DECL|method|InjectionRequestProcessor
name|InjectionRequestProcessor
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|Initializer
name|initializer
parameter_list|)
block|{
name|super
argument_list|(
name|errors
argument_list|)
expr_stmt|;
name|this
operator|.
name|initializer
operator|=
name|initializer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|Boolean
name|visit
parameter_list|(
name|StaticInjectionRequest
name|request
parameter_list|)
block|{
name|staticInjections
operator|.
name|add
argument_list|(
operator|new
name|StaticInjection
argument_list|(
name|injector
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|Boolean
name|visit
parameter_list|(
name|InjectionRequest
name|request
parameter_list|)
block|{
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
name|request
operator|.
name|getInjectionPoints
argument_list|()
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
name|initializer
operator|.
name|requestInjection
argument_list|(
name|injector
argument_list|,
name|request
operator|.
name|getInstance
argument_list|()
argument_list|,
name|request
operator|.
name|getSource
argument_list|()
argument_list|,
name|injectionPoints
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
block|{
for|for
control|(
name|StaticInjection
name|staticInjection
range|:
name|staticInjections
control|)
block|{
name|staticInjection
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|injectMembers
specifier|public
name|void
name|injectMembers
parameter_list|()
block|{
for|for
control|(
name|StaticInjection
name|staticInjection
range|:
name|staticInjections
control|)
block|{
name|staticInjection
operator|.
name|injectMembers
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * A requested static injection.      */
DECL|class|StaticInjection
specifier|private
class|class
name|StaticInjection
block|{
DECL|field|injector
specifier|final
name|InjectorImpl
name|injector
decl_stmt|;
DECL|field|source
specifier|final
name|Object
name|source
decl_stmt|;
DECL|field|request
specifier|final
name|StaticInjectionRequest
name|request
decl_stmt|;
DECL|field|memberInjectors
name|ImmutableList
argument_list|<
name|SingleMemberInjector
argument_list|>
name|memberInjectors
decl_stmt|;
DECL|method|StaticInjection
specifier|public
name|StaticInjection
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|StaticInjectionRequest
name|request
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
name|source
operator|=
name|request
operator|.
name|getSource
argument_list|()
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
DECL|method|validate
name|void
name|validate
parameter_list|()
block|{
name|Errors
name|errorsForMember
init|=
name|errors
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
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
name|request
operator|.
name|getInjectionPoints
argument_list|()
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
name|memberInjectors
operator|=
name|injector
operator|.
name|membersInjectorStore
operator|.
name|getInjectors
argument_list|(
name|injectionPoints
argument_list|,
name|errorsForMember
argument_list|)
expr_stmt|;
block|}
DECL|method|injectMembers
name|void
name|injectMembers
parameter_list|()
block|{
try|try
block|{
name|injector
operator|.
name|callInContext
argument_list|(
operator|new
name|ContextualCallable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|call
parameter_list|(
name|InternalContext
name|context
parameter_list|)
block|{
for|for
control|(
name|SingleMemberInjector
name|injector
range|:
name|memberInjectors
control|)
block|{
name|injector
operator|.
name|inject
argument_list|(
name|errors
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
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
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

