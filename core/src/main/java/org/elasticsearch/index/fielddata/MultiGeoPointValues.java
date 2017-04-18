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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A stateful lightweight per document set of {@link GeoPoint} values.  * To iterate over values in a document use the following pattern:  *<pre>  *   GeoPointValues values = ..;  *   values.setDocId(docId);  *   final int numValues = values.count();  *   for (int i = 0; i&lt; numValues; i++) {  *       GeoPoint value = values.valueAt(i);  *       // process value  *   }  *</pre>  * The set of values associated with a document might contain duplicates and  * comes in a non-specified order.  */
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
comment|/**      * Advance this instance to the given document id      * @return true if there is a value for this document      */
DECL|method|advanceExact
specifier|public
specifier|abstract
name|boolean
name|advanceExact
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Return the number of geo points the current document has.      */
DECL|method|docValueCount
specifier|public
specifier|abstract
name|int
name|docValueCount
parameter_list|()
function_decl|;
comment|/**      * Return the next value associated with the current document. This must not be      * called more than {@link #docValueCount()} times.      *      * Note: the returned {@link GeoPoint} might be shared across invocations.      *      * @return the next value for the current docID set to {@link #advanceExact(int)}.      */
DECL|method|nextValue
specifier|public
specifier|abstract
name|GeoPoint
name|nextValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

