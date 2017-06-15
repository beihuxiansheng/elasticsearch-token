begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
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
name|xcontent
operator|.
name|NamedXContentRegistry
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
comment|/**  * Provides named XContent parsers.  */
end_comment

begin_interface
DECL|interface|NamedXContentProvider
specifier|public
interface|interface
name|NamedXContentProvider
block|{
comment|/**      * @return a list of {@link NamedXContentRegistry.Entry} that this plugin provides.      */
DECL|method|getNamedXContentParsers
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|getNamedXContentParsers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

