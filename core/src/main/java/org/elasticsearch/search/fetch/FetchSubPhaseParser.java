begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
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
name|XContentParser
import|;
end_import

begin_comment
comment|/**  * Parser for the ext section of a search request, which can hold custom fetch sub phase  */
end_comment

begin_interface
DECL|interface|FetchSubPhaseParser
specifier|public
interface|interface
name|FetchSubPhaseParser
parameter_list|<
name|Context
extends|extends
name|FetchSubPhaseContext
parameter_list|>
block|{
DECL|method|parse
name|Context
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

