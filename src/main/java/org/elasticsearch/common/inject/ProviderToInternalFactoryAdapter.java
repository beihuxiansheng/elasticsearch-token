begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|internal
operator|.
name|InternalFactory
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

begin_comment
comment|/**  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|ProviderToInternalFactoryAdapter
class|class
name|ProviderToInternalFactoryAdapter
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Provider
argument_list|<
name|T
argument_list|>
block|{
DECL|field|injector
specifier|private
specifier|final
name|InjectorImpl
name|injector
decl_stmt|;
DECL|field|internalFactory
specifier|private
specifier|final
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|internalFactory
decl_stmt|;
DECL|method|ProviderToInternalFactoryAdapter
specifier|public
name|ProviderToInternalFactoryAdapter
parameter_list|(
name|InjectorImpl
name|injector
parameter_list|,
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|internalFactory
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
name|internalFactory
operator|=
name|internalFactory
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|T
name|get
parameter_list|()
block|{
specifier|final
name|Errors
name|errors
init|=
operator|new
name|Errors
argument_list|()
decl_stmt|;
try|try
block|{
name|T
name|t
init|=
name|injector
operator|.
name|callInContext
argument_list|(
operator|new
name|ContextualCallable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|T
name|call
parameter_list|(
name|InternalContext
name|context
parameter_list|)
throws|throws
name|ErrorsException
block|{
name|Dependency
name|dependency
init|=
name|context
operator|.
name|getDependency
argument_list|()
decl_stmt|;
return|return
name|internalFactory
operator|.
name|get
argument_list|(
name|errors
argument_list|,
name|context
argument_list|,
name|dependency
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|errors
operator|.
name|throwIfNewErrors
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
catch|catch
parameter_list|(
name|ErrorsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|errors
operator|.
name|merge
argument_list|(
name|e
operator|.
name|getErrors
argument_list|()
argument_list|)
operator|.
name|getMessages
argument_list|()
argument_list|)
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
name|internalFactory
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

