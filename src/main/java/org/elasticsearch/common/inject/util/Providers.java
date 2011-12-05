begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|util
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
name|Provider
import|;
end_import

begin_comment
comment|/**  * Static utility methods for creating and working with instances of  * {@link Provider}.  *  * @author Kevin Bourrillion (kevinb9n@gmail.com)  * @since 2.0  */
end_comment

begin_class
DECL|class|Providers
specifier|public
specifier|final
class|class
name|Providers
block|{
DECL|method|Providers
specifier|private
name|Providers
parameter_list|()
block|{     }
comment|/**      * Returns a provider which always provides {@code instance}.  This should not      * be necessary to use in your application, but is helpful for several types      * of unit tests.      *      * @param instance the instance that should always be provided.  This is also      *                 permitted to be null, to enable aggressive testing, although in real      *                 life a Guice-supplied Provider will never return null.      */
DECL|method|of
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
specifier|final
name|T
name|instance
parameter_list|)
block|{
return|return
operator|new
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|T
name|get
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"of("
operator|+
name|instance
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

