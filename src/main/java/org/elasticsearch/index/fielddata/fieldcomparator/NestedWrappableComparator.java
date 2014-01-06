begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
comment|/** Base comparator which allows for nested sorting. */
end_comment

begin_class
DECL|class|NestedWrappableComparator
specifier|public
specifier|abstract
class|class
name|NestedWrappableComparator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|FieldComparator
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Assigns the underlying missing value to the specified slot, if the actual implementation supports missing value.      *      * @param slot The slot to assign the the missing value to.      */
DECL|method|missing
specifier|public
specifier|abstract
name|void
name|missing
parameter_list|(
name|int
name|slot
parameter_list|)
function_decl|;
comment|/**      * Compares the missing value to the bottom.      *      * @return any N< 0 if the bottom value is not competitive with the missing value, any N> 0 if the      * bottom value is competitive with the missing value and 0 if they are equal.      */
DECL|method|compareBottomMissing
specifier|public
specifier|abstract
name|int
name|compareBottomMissing
parameter_list|()
function_decl|;
block|}
end_class

end_unit

