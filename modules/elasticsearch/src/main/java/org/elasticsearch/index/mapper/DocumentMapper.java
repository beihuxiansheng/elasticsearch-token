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
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
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
comment|/**  * @author kimchy (shay.banon)  */
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
comment|/**      * Merges this document mapper with the provided document mapper.      */
DECL|method|merge
name|void
name|merge
parameter_list|(
name|DocumentMapper
name|mergeWith
parameter_list|,
name|MergeFlags
name|mergeFlags
parameter_list|)
throws|throws
name|MergeMappingException
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
comment|/**      * Parses the source into a parsed document.      *      *<p>Validates that the source has the provided id and type. Note, most times      * we will already have the id and the type even though they exist in the source as well.      */
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
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|MapperParsingException
function_decl|;
comment|/**      * Parses the source into a parsed document.      *      *<p>Validates that the source has the provided id and type. Note, most times      * we will already have the id and the type even though they exist in the source as well.      */
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
name|byte
index|[]
name|source
parameter_list|,
annotation|@
name|Nullable
name|ParseListener
name|listener
parameter_list|)
throws|throws
name|MapperParsingException
function_decl|;
comment|/**      * Parses the source into the parsed document.      */
DECL|method|parse
name|ParsedDocument
name|parse
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|MapperParsingException
function_decl|;
comment|/**      * Adds a field mapper listener.      */
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
DECL|class|MergeFlags
specifier|public
specifier|static
class|class
name|MergeFlags
block|{
DECL|method|mergeFlags
specifier|public
specifier|static
name|MergeFlags
name|mergeFlags
parameter_list|()
block|{
return|return
operator|new
name|MergeFlags
argument_list|()
return|;
block|}
DECL|field|simulate
specifier|private
name|boolean
name|simulate
init|=
literal|true
decl_stmt|;
DECL|field|ignoreDuplicates
specifier|private
name|boolean
name|ignoreDuplicates
init|=
literal|false
decl_stmt|;
DECL|method|MergeFlags
specifier|public
name|MergeFlags
parameter_list|()
block|{         }
comment|/**          * A simulation run, don't perform actual modifications to the mapping.          */
DECL|method|simulate
specifier|public
name|boolean
name|simulate
parameter_list|()
block|{
return|return
name|simulate
return|;
block|}
DECL|method|simulate
specifier|public
name|MergeFlags
name|simulate
parameter_list|(
name|boolean
name|simulate
parameter_list|)
block|{
name|this
operator|.
name|simulate
operator|=
name|simulate
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ignoreDuplicates
specifier|public
name|boolean
name|ignoreDuplicates
parameter_list|()
block|{
return|return
name|ignoreDuplicates
return|;
block|}
DECL|method|ignoreDuplicates
specifier|public
name|MergeFlags
name|ignoreDuplicates
parameter_list|(
name|boolean
name|ignoreDuplicates
parameter_list|)
block|{
name|this
operator|.
name|ignoreDuplicates
operator|=
name|ignoreDuplicates
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/**      * A listener to be called during the parse process.      */
DECL|interface|ParseListener
specifier|public
specifier|static
interface|interface
name|ParseListener
parameter_list|<
name|ParseContext
parameter_list|>
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|ParseListener
name|EMPTY
init|=
operator|new
name|ParseListenerAdapter
argument_list|()
decl_stmt|;
comment|/**          * Called before a field is added to the document. Return<tt>true</tt> to include          * it in the document.          */
DECL|method|beforeFieldAdded
name|boolean
name|beforeFieldAdded
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|,
name|Fieldable
name|fieldable
parameter_list|,
name|ParseContext
name|parseContent
parameter_list|)
function_decl|;
block|}
DECL|class|ParseListenerAdapter
specifier|public
specifier|static
class|class
name|ParseListenerAdapter
implements|implements
name|ParseListener
block|{
DECL|method|beforeFieldAdded
annotation|@
name|Override
specifier|public
name|boolean
name|beforeFieldAdded
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|,
name|Fieldable
name|fieldable
parameter_list|,
name|Object
name|parseContext
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_interface

end_unit

