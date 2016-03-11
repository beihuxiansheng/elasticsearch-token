begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|// TODO once sort refactoring is done this needs to be merged into SortBuilder
end_comment

begin_interface
DECL|interface|SortElementParserTemp
specifier|public
interface|interface
name|SortElementParserTemp
parameter_list|<
name|T
extends|extends
name|SortBuilder
parameter_list|>
block|{
comment|/**      * Creates a new SortBuilder from the json held by the {@link SortElementParserTemp}      * in {@link org.elasticsearch.common.xcontent.XContent} format      *      * @param context      *            the input parse context. The state on the parser contained in      *            this context will be changed as a side effect of this method      *            call      * @return the new item      */
DECL|method|fromXContent
name|T
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|String
name|elementName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

