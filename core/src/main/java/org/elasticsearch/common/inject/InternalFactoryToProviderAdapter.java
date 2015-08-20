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
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|InternalFactoryToProviderAdapter
class|class
name|InternalFactoryToProviderAdapter
parameter_list|<
name|T
parameter_list|>
implements|implements
name|InternalFactory
argument_list|<
name|T
argument_list|>
block|{
DECL|field|initializable
specifier|private
specifier|final
name|Initializable
argument_list|<
name|Provider
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|initializable
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|method|InternalFactoryToProviderAdapter
specifier|public
name|InternalFactoryToProviderAdapter
parameter_list|(
name|Initializable
argument_list|<
name|Provider
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|initializable
parameter_list|)
block|{
name|this
argument_list|(
name|initializable
argument_list|,
name|SourceProvider
operator|.
name|UNKNOWN_SOURCE
argument_list|)
expr_stmt|;
block|}
DECL|method|InternalFactoryToProviderAdapter
specifier|public
name|InternalFactoryToProviderAdapter
parameter_list|(
name|Initializable
argument_list|<
name|Provider
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|initializable
parameter_list|,
name|Object
name|source
parameter_list|)
block|{
name|this
operator|.
name|initializable
operator|=
name|checkNotNull
argument_list|(
name|initializable
argument_list|,
literal|"provider"
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|checkNotNull
argument_list|(
name|source
argument_list|,
literal|"source"
argument_list|)
expr_stmt|;
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
try|try
block|{
return|return
name|errors
operator|.
name|checkForNull
argument_list|(
name|initializable
operator|.
name|get
argument_list|(
name|errors
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|source
argument_list|,
name|dependency
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|userException
parameter_list|)
block|{
throw|throw
name|errors
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
operator|.
name|errorInProvider
argument_list|(
name|userException
argument_list|)
operator|.
name|toException
argument_list|()
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
name|initializable
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
