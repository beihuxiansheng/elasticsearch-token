begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Facets of search action.  *  *  */
end_comment

begin_interface
DECL|interface|Facets
specifier|public
interface|interface
name|Facets
extends|extends
name|Iterable
argument_list|<
name|Facet
argument_list|>
block|{
comment|/**      * The list of {@link Facet}s.      */
DECL|method|facets
name|List
argument_list|<
name|Facet
argument_list|>
name|facets
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Facet}s keyed by facet name.      */
DECL|method|getFacets
name|Map
argument_list|<
name|String
argument_list|,
name|Facet
argument_list|>
name|getFacets
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Facet}s keyed by facet name.      */
DECL|method|facetsAsMap
name|Map
argument_list|<
name|String
argument_list|,
name|Facet
argument_list|>
name|facetsAsMap
parameter_list|()
function_decl|;
comment|/**      * Returns the facet by name already casted to the specified type.      */
DECL|method|facet
parameter_list|<
name|T
extends|extends
name|Facet
parameter_list|>
name|T
name|facet
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|facetType
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * A facet of the specified name.      */
DECL|method|facet
parameter_list|<
name|T
extends|extends
name|Facet
parameter_list|>
name|T
name|facet
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

