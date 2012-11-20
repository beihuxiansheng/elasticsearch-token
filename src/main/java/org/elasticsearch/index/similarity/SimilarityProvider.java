begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.similarity
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|similarity
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|Provider
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexComponent
import|;
end_import

begin_comment
comment|/**  * Provider for {@link Similarity} instances  */
end_comment

begin_interface
DECL|interface|SimilarityProvider
specifier|public
interface|interface
name|SimilarityProvider
block|{
comment|/**      * Returns the name associated with the Provider      *      * @return Name of the Provider      */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Similarity} the Provider is for      *      * @return Provided {@link Similarity}      */
DECL|method|get
name|Similarity
name|get
parameter_list|()
function_decl|;
comment|/**      * Factory for creating {@link SimilarityProvider} instances      */
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
comment|/**          * Creates a new {@link SimilarityProvider} instance          *          * @param name Name of the provider          * @param settings Settings to be used by the Provider          * @return {@link SimilarityProvider} instance created by the Factory          */
DECL|method|create
name|SimilarityProvider
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|Settings
name|settings
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

