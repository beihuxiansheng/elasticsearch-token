begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|DirectoryReader
import|;
end_import

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
name|IndexSearcher
import|;
end_import

begin_comment
comment|/**  * Extension point to add custom functionality at request time to the {@link DirectoryReader}  * and {@link IndexSearcher} managed by the {@link Engine}.  */
end_comment

begin_interface
DECL|interface|IndexSearcherWrapper
specifier|public
interface|interface
name|IndexSearcherWrapper
block|{
comment|/**      * @param reader The provided directory reader to be wrapped to add custom functionality      * @return a new directory reader wrapping the provided directory reader or if no wrapping was performed      *         the provided directory reader      */
DECL|method|wrap
name|DirectoryReader
name|wrap
parameter_list|(
name|DirectoryReader
name|reader
parameter_list|)
function_decl|;
comment|/**      * @param searcher The provided index searcher to be wrapped to add custom functionality      * @return a new index searcher wrapping the provided index searcher or if no wrapping was performed      *         the provided index searcher      */
DECL|method|wrap
name|IndexSearcher
name|wrap
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|EngineException
function_decl|;
block|}
end_interface

end_unit

