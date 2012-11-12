begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|Document
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
name|index
operator|.
name|AtomicReader
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
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|SearchParseElement
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
name|InternalSearchHit
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|FetchSubPhase
specifier|public
interface|interface
name|FetchSubPhase
block|{
DECL|class|HitContext
specifier|public
specifier|static
class|class
name|HitContext
block|{
DECL|field|hit
specifier|private
name|InternalSearchHit
name|hit
decl_stmt|;
DECL|field|topLevelReader
specifier|private
name|IndexReader
name|topLevelReader
decl_stmt|;
DECL|field|topLevelDocId
specifier|private
name|int
name|topLevelDocId
decl_stmt|;
DECL|field|readerContext
specifier|private
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|docId
specifier|private
name|int
name|docId
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|cache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cache
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|InternalSearchHit
name|hit
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|docId
parameter_list|,
name|IndexReader
name|topLevelReader
parameter_list|,
name|int
name|topLevelDocId
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|this
operator|.
name|hit
operator|=
name|hit
expr_stmt|;
name|this
operator|.
name|readerContext
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|topLevelReader
operator|=
name|topLevelReader
expr_stmt|;
name|this
operator|.
name|topLevelDocId
operator|=
name|topLevelDocId
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
DECL|method|hit
specifier|public
name|InternalSearchHit
name|hit
parameter_list|()
block|{
return|return
name|hit
return|;
block|}
DECL|method|reader
specifier|public
name|AtomicReader
name|reader
parameter_list|()
block|{
return|return
name|readerContext
operator|.
name|reader
argument_list|()
return|;
block|}
DECL|method|readerContext
specifier|public
name|AtomicReaderContext
name|readerContext
parameter_list|()
block|{
return|return
name|readerContext
return|;
block|}
DECL|method|docId
specifier|public
name|int
name|docId
parameter_list|()
block|{
return|return
name|docId
return|;
block|}
DECL|method|topLevelReader
specifier|public
name|IndexReader
name|topLevelReader
parameter_list|()
block|{
return|return
name|topLevelReader
return|;
block|}
DECL|method|topLevelDocId
specifier|public
name|int
name|topLevelDocId
parameter_list|()
block|{
return|return
name|topLevelDocId
return|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|cache
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cache
parameter_list|()
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
return|return
name|cache
return|;
block|}
block|}
DECL|method|parseElements
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
function_decl|;
DECL|method|hitExecutionNeeded
name|boolean
name|hitExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
function_decl|;
comment|/**      * Executes the hit level phase, with a reader and doc id (note, its a low level reader, and the matching doc).      */
DECL|method|hitExecute
name|void
name|hitExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|HitContext
name|hitContext
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|hitsExecutionNeeded
name|boolean
name|hitsExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
function_decl|;
DECL|method|hitsExecute
name|void
name|hitsExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|InternalSearchHit
index|[]
name|hits
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
block|}
end_interface

end_unit

