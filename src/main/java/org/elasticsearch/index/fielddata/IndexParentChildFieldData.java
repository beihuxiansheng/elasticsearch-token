begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_comment
comment|/**  * Soecialization of {@link IndexFieldData} for parent/child mappings.  */
end_comment

begin_interface
DECL|interface|IndexParentChildFieldData
specifier|public
interface|interface
name|IndexParentChildFieldData
extends|extends
name|IndexFieldData
operator|.
name|Global
argument_list|<
name|AtomicParentChildFieldData
argument_list|>
block|{
comment|/**      * Load a global view of the ordinals for the given {@link IndexReader},      * potentially from a cache.      */
annotation|@
name|Override
DECL|method|loadGlobal
name|IndexParentChildFieldData
name|loadGlobal
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
function_decl|;
comment|/**      * Load a global view of the ordinals for the given {@link IndexReader}.      */
annotation|@
name|Override
DECL|method|localGlobalDirect
name|IndexParentChildFieldData
name|localGlobalDirect
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

