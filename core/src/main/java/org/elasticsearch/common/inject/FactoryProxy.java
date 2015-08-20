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
name|Dependency
import|;
end_import

begin_comment
comment|/**  * A placeholder which enables us to swap in the real factory once the injector is created.  */
end_comment

begin_class
DECL|class|FactoryProxy
class|class
name|FactoryProxy
parameter_list|<
name|T
parameter_list|>
implements|implements
name|InternalFactory
argument_list|<
name|T
argument_list|>
implements|,
name|BindingProcessor
operator|.
name|CreationListener
block|{
DECL|field|injector
specifier|private
specifier|final
name|InjectorImpl
name|injector
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|Key
argument_list|<
name|T
argument_list|>
name|key
decl_stmt|;
DECL|field|targetKey
specifier|private
specifier|final
name|Key
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|targetKey
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|field|targetFactory
specifier|private
name|InternalFactory
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|targetFactory
decl_stmt|;
DECL|method|FactoryProxy
name|FactoryProxy
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
name|Key
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|targetKey
parameter_list|,
name|Object
name|source
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
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|targetKey
operator|=
name|targetKey
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|notify
specifier|public
name|void
name|notify
parameter_list|(
specifier|final
name|Errors
name|errors
parameter_list|)
block|{
try|try
block|{
name|targetFactory
operator|=
name|injector
operator|.
name|getInternalFactory
argument_list|(
name|targetKey
argument_list|,
name|errors
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ErrorsException
name|e
parameter_list|)
block|{
name|errors
operator|.
name|merge
argument_list|(
name|e
operator|.
name|getErrors
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
return|return
name|targetFactory
operator|.
name|get
argument_list|(
name|errors
operator|.
name|withSource
argument_list|(
name|targetKey
argument_list|)
argument_list|,
name|context
argument_list|,
name|dependency
argument_list|)
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
operator|new
name|ToStringBuilder
argument_list|(
name|FactoryProxy
operator|.
name|class
argument_list|)
operator|.
name|add
argument_list|(
literal|"key"
argument_list|,
name|key
argument_list|)
operator|.
name|add
argument_list|(
literal|"provider"
argument_list|,
name|targetFactory
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
