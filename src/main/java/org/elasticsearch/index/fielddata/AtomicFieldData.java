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
name|TermsEnum
import|;
end_import

begin_comment
comment|/**  * The thread safe {@link org.apache.lucene.index.AtomicReader} level cache of the data.  */
end_comment

begin_interface
DECL|interface|AtomicFieldData
specifier|public
interface|interface
name|AtomicFieldData
parameter_list|<
name|Script
extends|extends
name|ScriptDocValues
parameter_list|>
extends|extends
name|RamUsage
block|{
comment|/**      * If this method returns false, this means that no document has multiple values. However this method may return true even if all      * documents are single-valued. So this method is useful for performing optimizations when the single-value case makes the problem      * simpler but cannot be used to actually check whether this instance is multi-valued.      */
DECL|method|isMultiValued
name|boolean
name|isMultiValued
parameter_list|()
function_decl|;
comment|/**      * An upper limit of the number of unique values in this atomic field data.      */
DECL|method|getNumberUniqueValues
name|long
name|getNumberUniqueValues
parameter_list|()
function_decl|;
comment|/**      * Use a non thread safe (lightweight) view of the values as bytes.      */
DECL|method|getBytesValues
name|BytesValues
name|getBytesValues
parameter_list|()
function_decl|;
comment|/**      * Returns a "scripting" based values.      */
DECL|method|getScriptValues
name|Script
name|getScriptValues
parameter_list|()
function_decl|;
comment|/**      * Close the field data.      */
DECL|method|close
name|void
name|close
parameter_list|()
function_decl|;
DECL|interface|WithOrdinals
interface|interface
name|WithOrdinals
parameter_list|<
name|Script
extends|extends
name|ScriptDocValues
parameter_list|>
extends|extends
name|AtomicFieldData
argument_list|<
name|Script
argument_list|>
block|{
comment|/**          * Use a non thread safe (lightweight) view of the values as bytes.          * @param needsHashes          */
DECL|method|getBytesValues
name|BytesValues
operator|.
name|WithOrdinals
name|getBytesValues
parameter_list|()
function_decl|;
comment|/**          * Returns a terms enum to iterate over all the underlying values.          */
DECL|method|getTermsEnum
name|TermsEnum
name|getTermsEnum
parameter_list|()
function_decl|;
block|}
comment|/**      * This enum provides information about the order of the values for      * a given document. For instance {@link BytesValues} by default      * return values in {@link #BYTES} order but if the interface      * wraps a numeric variant the sort order might change to {@link #NUMERIC}.      * In that case the values might not be returned in byte sort order but in numeric      * order instead while maintaining the property of<tt>N< N+1</tt> during the      * value iterations.      *      * @see org.elasticsearch.index.fielddata.BytesValues#getOrder()      * @see org.elasticsearch.index.fielddata.DoubleValues#getOrder()      * @see org.elasticsearch.index.fielddata.LongValues#getOrder()      */
DECL|enum|Order
specifier|public
enum|enum
name|Order
block|{
comment|/**          * Donates Byte sort order          */
DECL|enum constant|BYTES
name|BYTES
block|,
comment|/**          * Donates Numeric sort order          */
DECL|enum constant|NUMERIC
name|NUMERIC
block|,
comment|/**          * Donates custom sort order          */
DECL|enum constant|CUSTOM
name|CUSTOM
block|,
comment|/**          * Donates no sort order          */
DECL|enum constant|NONE
name|NONE
block|}
block|}
end_interface

end_unit

