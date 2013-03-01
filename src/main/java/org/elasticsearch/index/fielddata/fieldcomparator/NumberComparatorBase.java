begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.fieldcomparator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
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
name|search
operator|.
name|FieldComparator
import|;
end_import

begin_comment
comment|/**  * Base FieldComparator class for number fields.  */
end_comment

begin_comment
comment|// This is right now only used for sorting number based fields inside nested objects
end_comment

begin_class
DECL|class|NumberComparatorBase
specifier|public
specifier|abstract
class|class
name|NumberComparatorBase
parameter_list|<
name|T
parameter_list|>
extends|extends
name|FieldComparator
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Adds numeric value at the specified doc to the specified slot.      *      * @param slot  The specified slot      * @param doc   The specified doc      */
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**      * Divides the value at the specified slot with the specified divisor.      *      * @param slot      The specified slot      * @param divisor   The specified divisor      */
DECL|method|divide
specifier|public
specifier|abstract
name|void
name|divide
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|divisor
parameter_list|)
function_decl|;
block|}
end_class

end_unit

