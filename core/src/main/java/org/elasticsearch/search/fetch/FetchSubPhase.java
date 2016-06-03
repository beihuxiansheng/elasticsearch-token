begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
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
name|LeafReaderContext
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchHit
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|readerContext
specifier|private
name|LeafReaderContext
name|readerContext
decl_stmt|;
DECL|field|docId
specifier|private
name|int
name|docId
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
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|docId
parameter_list|,
name|IndexSearcher
name|searcher
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
name|searcher
operator|=
name|searcher
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
name|LeafReader
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
name|LeafReaderContext
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
name|searcher
operator|.
name|getIndexReader
argument_list|()
return|;
block|}
DECL|method|topLevelSearcher
specifier|public
name|IndexSearcher
name|topLevelSearcher
parameter_list|()
block|{
return|return
name|searcher
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
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
return|return
name|cache
return|;
block|}
block|}
DECL|method|parseElements
specifier|default
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
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
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
function_decl|;
comment|/**      * This interface is in the fetch phase plugin mechanism.      * Whenever a new search is executed we create a new {@link SearchContext} that holds individual contexts for each {@link org.elasticsearch.search.fetch.FetchSubPhase}.      * Fetch phases that use the plugin mechanism must provide a ContextFactory to the SearchContext that creates the fetch phase context and also associates them with a name.      * See {@link SearchContext#getFetchSubPhaseContext(FetchSubPhase.ContextFactory)}      */
DECL|interface|ContextFactory
specifier|public
interface|interface
name|ContextFactory
parameter_list|<
name|SubPhaseContext
extends|extends
name|FetchSubPhaseContext
parameter_list|>
block|{
comment|/**          * The name of the context.          */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**          * Creates a new instance of a FetchSubPhaseContext that holds all information a FetchSubPhase needs to execute on hits.          */
DECL|method|newContextInstance
specifier|public
name|SubPhaseContext
name|newContextInstance
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

