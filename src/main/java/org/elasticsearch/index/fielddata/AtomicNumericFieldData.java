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
name|SortedNumericDocValues
import|;
end_import

begin_comment
comment|/**  * Specialization of {@link AtomicFieldData} for numeric data.  */
end_comment

begin_interface
DECL|interface|AtomicNumericFieldData
specifier|public
interface|interface
name|AtomicNumericFieldData
extends|extends
name|AtomicFieldData
block|{
comment|/**      * Get an integer view of the values of this segment. If the implementation      * stores floating-point numbers then these values will return the same      * values but casted to longs.      */
DECL|method|getLongValues
name|SortedNumericDocValues
name|getLongValues
parameter_list|()
function_decl|;
comment|/**      * Return a floating-point view of the values in this segment. If the      * implementation stored integers then the returned doubles would be the      * same ones as you would get from casting to a double.      */
DECL|method|getDoubleValues
name|SortedNumericDoubleValues
name|getDoubleValues
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

