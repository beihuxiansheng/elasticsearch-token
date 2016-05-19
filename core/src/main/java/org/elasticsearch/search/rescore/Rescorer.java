begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.rescore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|rescore
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
name|Term
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
name|Explanation
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
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchType
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A query rescorer interface used to re-rank the Top-K results of a previously  * executed search.  */
end_comment

begin_interface
DECL|interface|Rescorer
specifier|public
interface|interface
name|Rescorer
block|{
comment|/**      * Returns the name of this rescorer      */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * Modifies the result of the previously executed search ({@link TopDocs})      * in place based on the given {@link RescoreSearchContext}.      *      * @param topDocs        the result of the previously executed search      * @param context        the current {@link SearchContext}. This will never be<code>null</code>.      * @param rescoreContext the {@link RescoreSearchContext}. This will never be<code>null</code>      * @throws IOException if an {@link IOException} occurs during rescoring      */
DECL|method|rescore
specifier|public
name|TopDocs
name|rescore
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|RescoreSearchContext
name|rescoreContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Executes an {@link Explanation} phase on the rescorer.      *      * @param topLevelDocId the global / top-level document ID to explain      * @param context the explanation for the results being fed to this rescorer      * @param rescoreContext context for this rescorer      * @param sourceExplanation explanation of the source of the documents being fed into this rescore      * @return the explain for the given top level document ID.      * @throws IOException if an {@link IOException} occurs      */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|topLevelDocId
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|RescoreSearchContext
name|rescoreContext
parameter_list|,
name|Explanation
name|sourceExplanation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Extracts all terms needed to execute this {@link Rescorer}. This method      * is executed in a distributed frequency collection roundtrip for      * {@link SearchType#DFS_QUERY_AND_FETCH} and      * {@link SearchType#DFS_QUERY_THEN_FETCH}      */
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|RescoreSearchContext
name|rescoreContext
parameter_list|,
name|Set
argument_list|<
name|Term
argument_list|>
name|termsSet
parameter_list|)
function_decl|;
comment|/*      * TODO: At this point we only have one implementation which modifies the      * TopDocs given. Future implementations might return actual results that      * contain information about the rescore context. For example a pair wise      * reranker might return the feature vector for the top N window in order to      * merge results on the callers side. For now we don't have a return type at      * all since something like this requires a more general refactoring how      * documents are merged since in such a case we don't really have a score      * per document rather a "X is more relevant than Y" relation      */
block|}
end_interface

end_unit

