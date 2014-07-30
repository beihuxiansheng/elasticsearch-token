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
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoPoint
import|;
end_import

begin_comment
comment|/**  * A stateful lightweight per document set of {@link GeoPoint} values.  * To iterate over values in a document use the following pattern:  *<pre>  *   GeoPointValues values = ..;  *   values.setDocId(docId);  *   final int numValues = values.count();  *   for (int i = 0; i< numValues; i++) {  *       GeoPoint value = values.valueAt(i);  *       // process value  *   }  *</pre>  * The set of values associated with a document might contain duplicates and  * comes in a non-specified order.  */
end_comment

begin_class
DECL|class|MultiGeoPointValues
specifier|public
specifier|abstract
class|class
name|MultiGeoPointValues
block|{
comment|/**      * Creates a new {@link MultiGeoPointValues} instance      */
DECL|method|MultiGeoPointValues
specifier|protected
name|MultiGeoPointValues
parameter_list|()
block|{     }
comment|/**      * Sets iteration to the specified docID.      * @param docId document ID      *      * @see #valueAt(int)      * @see #count()      */
DECL|method|setDocument
specifier|public
specifier|abstract
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * Return the number of geo points the current document has.      */
DECL|method|count
specifier|public
specifier|abstract
name|int
name|count
parameter_list|()
function_decl|;
comment|/**      * Return the<code>i-th</code> value associated with the current document.      * Behavior is undefined when<code>i</code> is undefined or greater than      * or equal to {@link #count()}.      *      * Note: the returned {@link GeoPoint} might be shared across invocations.      *      * @return the next value for the current docID set to {@link #setDocument(int)}.      */
DECL|method|valueAt
specifier|public
specifier|abstract
name|GeoPoint
name|valueAt
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
block|}
end_class

end_unit

