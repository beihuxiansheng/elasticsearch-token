begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TypeLiteral
import|;
end_import

begin_comment
comment|/**  * Converts constant string values to a different type.  *  * @author crazybob@google.com (Bob Lee)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|TypeConverter
specifier|public
interface|interface
name|TypeConverter
block|{
comment|/**      * Converts a string value. Throws an exception if a conversion error occurs.      */
DECL|method|convert
name|Object
name|convert
parameter_list|(
name|String
name|value
parameter_list|,
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|toType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

