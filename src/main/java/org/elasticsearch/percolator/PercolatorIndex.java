begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
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
name|mapper
operator|.
name|ParsedDocument
import|;
end_import

begin_comment
comment|/**  * Abstraction on how to index the percolator document.  */
end_comment

begin_interface
DECL|interface|PercolatorIndex
interface|interface
name|PercolatorIndex
block|{
comment|/**      * Indexes the document(s) and initializes the PercolateContext      *      * @param context  Initialized with document related properties for fetch phase.      * @param document Document that is percolated. Can contain several documents.      * */
DECL|method|prepare
name|void
name|prepare
parameter_list|(
name|PercolateContext
name|context
parameter_list|,
name|ParsedDocument
name|document
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

