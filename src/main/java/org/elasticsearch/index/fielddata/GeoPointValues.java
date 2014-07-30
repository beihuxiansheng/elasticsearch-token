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
comment|/**  * Per-document geo-point values.  */
end_comment

begin_class
DECL|class|GeoPointValues
specifier|public
specifier|abstract
class|class
name|GeoPointValues
block|{
comment|/**      * Get the {@link GeoPoint} associated with<code>docID</code>.      * The returned {@link GeoPoint} might be reused across calls.      * If the given<code>docID</code> does not have a value then the returned      * geo point mught have both latitude and longitude set to 0.      */
DECL|method|get
specifier|public
specifier|abstract
name|GeoPoint
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
block|}
end_class

end_unit

