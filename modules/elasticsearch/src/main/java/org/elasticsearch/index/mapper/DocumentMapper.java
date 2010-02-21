begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
annotation|@
name|ThreadSafe
DECL|interface|DocumentMapper
specifier|public
interface|interface
name|DocumentMapper
block|{
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * When constructed by parsing a mapping definition, will return it. Otherwise,      * returns<tt>null</tt>.      */
DECL|method|mappingSource
name|String
name|mappingSource
parameter_list|()
function_decl|;
comment|/**      * Generates the source of the mapper based on the current mappings.      */
DECL|method|buildSource
name|String
name|buildSource
parameter_list|()
throws|throws
name|FailedToGenerateSourceMapperException
function_decl|;
DECL|method|uidMapper
name|UidFieldMapper
name|uidMapper
parameter_list|()
function_decl|;
DECL|method|idMapper
name|IdFieldMapper
name|idMapper
parameter_list|()
function_decl|;
DECL|method|typeMapper
name|TypeFieldMapper
name|typeMapper
parameter_list|()
function_decl|;
DECL|method|sourceMapper
name|SourceFieldMapper
name|sourceMapper
parameter_list|()
function_decl|;
DECL|method|boostMapper
name|BoostFieldMapper
name|boostMapper
parameter_list|()
function_decl|;
DECL|method|mappers
name|DocumentFieldMappers
name|mappers
parameter_list|()
function_decl|;
comment|/**      * The default index analyzer to be used. Note, the {@link DocumentFieldMappers#indexAnalyzer()} should      * probably be used instead.      */
DECL|method|indexAnalyzer
name|Analyzer
name|indexAnalyzer
parameter_list|()
function_decl|;
comment|/**      * The default search analyzer to be used. Note, the {@link DocumentFieldMappers#searchAnalyzer()} should      * probably be used instead.      */
DECL|method|searchAnalyzer
name|Analyzer
name|searchAnalyzer
parameter_list|()
function_decl|;
comment|/**      * Parses the source into a parsed document.      *<p/>      *<p>Validates that the source has the provided id and type. Note, most times      * we will already have the id and the type even though they exist in the source as well.      */
DECL|method|parse
name|ParsedDocument
name|parse
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
annotation|@
name|Nullable
name|String
name|id
parameter_list|,
name|String
name|source
parameter_list|)
throws|throws
name|MapperParsingException
function_decl|;
comment|/**      * Parses the source into the parsed document.      */
DECL|method|parse
name|ParsedDocument
name|parse
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|MapperParsingException
function_decl|;
DECL|method|addFieldMapperListener
name|void
name|addFieldMapperListener
parameter_list|(
name|FieldMapperListener
name|fieldMapperListener
parameter_list|,
name|boolean
name|includeExisting
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

