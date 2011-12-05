begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|xcontent
operator|.
name|ToXContent
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SortBuilder
specifier|public
specifier|abstract
class|class
name|SortBuilder
implements|implements
name|ToXContent
block|{
comment|/**      * The order of sorting. Defaults to {@link SortOrder#ASC}.      */
DECL|method|order
specifier|public
specifier|abstract
name|SortBuilder
name|order
parameter_list|(
name|SortOrder
name|order
parameter_list|)
function_decl|;
comment|/**      * Sets the value when a field is missing in a doc. Can also be set to<tt>_last</tt> or      *<tt>_first</tt> to sort missing last or first respectively.      */
DECL|method|missing
specifier|public
specifier|abstract
name|SortBuilder
name|missing
parameter_list|(
name|Object
name|missing
parameter_list|)
function_decl|;
block|}
end_class

end_unit

