begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
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
name|Injector
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
comment|/**  * A private collection of elements that are hidden from the enclosing injector or module by  * default. See {@link org.elasticsearch.common.inject.PrivateModule PrivateModule} for details.  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|PrivateElements
specifier|public
interface|interface
name|PrivateElements
extends|extends
name|Element
block|{
comment|/**      * Returns the configuration information in this private environment.      */
DECL|method|getElements
name|List
argument_list|<
name|Element
argument_list|>
name|getElements
parameter_list|()
function_decl|;
comment|/**      * Returns the child injector that hosts these private elements, or null if the elements haven't      * been used to create an injector.      */
DECL|method|getInjector
name|Injector
name|getInjector
parameter_list|()
function_decl|;
comment|/**      * Returns the unique exposed keys for these private elements.      */
DECL|method|getExposedKeys
name|Set
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
name|getExposedKeys
parameter_list|()
function_decl|;
comment|/**      * Returns an arbitrary object containing information about the "place" where this key was      * exposed. Used by Guice in the production of descriptive error messages.      *<p/>      *<p>Tools might specially handle types they know about; {@code StackTraceElement} is a good      * example. Tools should simply call {@code toString()} on the source object if the type is      * unfamiliar.      *      * @param key one of the keys exposed by this module.      */
DECL|method|getExposedSource
name|Object
name|getExposedSource
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

