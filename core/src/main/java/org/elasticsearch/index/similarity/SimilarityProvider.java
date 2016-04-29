begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
block|}
end_interface

end_unit

