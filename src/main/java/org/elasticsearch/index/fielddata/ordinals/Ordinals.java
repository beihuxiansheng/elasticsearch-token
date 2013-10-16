begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.ordinals
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
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
name|util
operator|.
name|LongsRef
import|;
end_import

begin_comment
comment|/**  * A thread safe ordinals abstraction. Ordinals can only be positive integers.  */
end_comment

begin_interface
DECL|interface|Ordinals
specifier|public
interface|interface
name|Ordinals
block|{
DECL|field|MISSING_ORDINAL
specifier|static
specifier|final
name|long
name|MISSING_ORDINAL
init|=
literal|0
decl_stmt|;
DECL|field|MIN_ORDINAL
specifier|static
specifier|final
name|long
name|MIN_ORDINAL
init|=
literal|1
decl_stmt|;
comment|/**      * The memory size this ordinals take.      */
DECL|method|getMemorySizeInBytes
name|long
name|getMemorySizeInBytes
parameter_list|()
function_decl|;
comment|/**      * Is one of the docs maps to more than one ordinal?      */
DECL|method|isMultiValued
name|boolean
name|isMultiValued
parameter_list|()
function_decl|;
comment|/**      * The number of docs in this ordinals.      */
DECL|method|getNumDocs
name|int
name|getNumDocs
parameter_list|()
function_decl|;
comment|/**      * The number of ordinals, excluding the {@link #MISSING_ORDINAL} ordinal indicating a missing value.      */
DECL|method|getNumOrds
name|long
name|getNumOrds
parameter_list|()
function_decl|;
comment|/**      * Returns total unique ord count; this includes +1 for      * the  {@link #MISSING_ORDINAL}  ord (always  {@value #MISSING_ORDINAL} ).      */
DECL|method|getMaxOrd
name|long
name|getMaxOrd
parameter_list|()
function_decl|;
comment|/**      * Returns a lightweight (non thread safe) view iterator of the ordinals.      */
DECL|method|ordinals
name|Docs
name|ordinals
parameter_list|()
function_decl|;
comment|/**      * A non thread safe ordinals abstraction, yet very lightweight to create. The idea      * is that this gets created for each "iteration" over ordinals.      *<p/>      *<p>A value of 0 ordinal when iterating indicated "no" value.</p>      * To iterate of a set of ordinals for a given document use {@link #setDocument(int)} and {@link #nextOrd()} as      * show in the example below:      *<pre>      *   Ordinals.Docs docs = ...;      *   final int len = docs.setDocId(docId);      *   for (int i = 0; i< len; i++) {      *       final long ord = docs.nextOrd();      *       // process ord      *   }      *</pre>      */
DECL|interface|Docs
interface|interface
name|Docs
block|{
comment|/**          * Returns the original ordinals used to generate this Docs "itereator".          */
DECL|method|ordinals
name|Ordinals
name|ordinals
parameter_list|()
function_decl|;
comment|/**          * The number of docs in this ordinals.          */
DECL|method|getNumDocs
name|int
name|getNumDocs
parameter_list|()
function_decl|;
comment|/**          * The number of ordinals, excluding the "0" ordinal (indicating a missing value).          */
DECL|method|getNumOrds
name|long
name|getNumOrds
parameter_list|()
function_decl|;
comment|/**          * Returns total unique ord count; this includes +1 for          * the null ord (always 0).          */
DECL|method|getMaxOrd
name|long
name|getMaxOrd
parameter_list|()
function_decl|;
comment|/**          * Is one of the docs maps to more than one ordinal?          */
DECL|method|isMultiValued
name|boolean
name|isMultiValued
parameter_list|()
function_decl|;
comment|/**          * The ordinal that maps to the relevant docId. If it has no value, returns          *<tt>0</tt>.          */
DECL|method|getOrd
name|long
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**          * Returns an array of ordinals matching the docIds, with 0 length one          * for a doc with no ordinals.          */
DECL|method|getOrds
name|LongsRef
name|getOrds
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**          * Returns the next ordinal for the current docID set to {@link #setDocument(int)}.          * This method should only be called<tt>N</tt> times where<tt>N</tt> is the number          * returned from {@link #setDocument(int)}. If called more than<tt>N</tt> times the behavior          * is undefined.          *          * Note: This method will never return<tt>0</tt>.          *          * @return the next ordinal for the current docID set to {@link #setDocument(int)}.          */
DECL|method|nextOrd
name|long
name|nextOrd
parameter_list|()
function_decl|;
comment|/**          * Sets iteration to the specified docID and returns the number of          * ordinals for this document ID,          * @param docId document ID          *          * @see #nextOrd()          */
DECL|method|setDocument
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**          * Returns the current ordinal in the iteration          * @return the current ordinal in the iteration          */
DECL|method|currentOrd
name|long
name|currentOrd
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

