begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
comment|/**  * A facet parser parses the relevant matching "type" of facet into a {@link FacetExecutor}.  *<p/>  * The parser also suggest the default {@link FacetExecutor.Mode} both for global and main executions.  */
end_comment

begin_interface
DECL|interface|FacetParser
specifier|public
interface|interface
name|FacetParser
block|{
comment|/**      * The type of the facet, for example, terms.      */
DECL|method|types
name|String
index|[]
name|types
parameter_list|()
function_decl|;
comment|/**      * The default mode to use when executed as a "main" (query level) facet.      */
DECL|method|defaultMainMode
name|FacetExecutor
operator|.
name|Mode
name|defaultMainMode
parameter_list|()
function_decl|;
comment|/**      * The default mode to use when executed as a "global" (all docs) facet.      */
DECL|method|defaultGlobalMode
name|FacetExecutor
operator|.
name|Mode
name|defaultGlobalMode
parameter_list|()
function_decl|;
comment|/**      * Parses the facet into a {@link FacetExecutor}.      */
DECL|method|parse
name|FacetExecutor
name|parse
parameter_list|(
name|String
name|facetName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

