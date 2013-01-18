begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|AtomicReaderContext
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
name|FieldComparatorSource
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
name|SortField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|IndexFieldData
specifier|public
interface|interface
name|IndexFieldData
parameter_list|<
name|FD
extends|extends
name|AtomicFieldData
parameter_list|>
extends|extends
name|IndexComponent
block|{
comment|/**      * The field name.      */
DECL|method|getFieldName
name|String
name|getFieldName
parameter_list|()
function_decl|;
comment|/**      * Are the values ordered? (in ascending manner).      */
DECL|method|valuesOrdered
name|boolean
name|valuesOrdered
parameter_list|()
function_decl|;
comment|/**      * Loads the atomic field data for the reader, possibly cached.      */
DECL|method|load
name|FD
name|load
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
function_decl|;
comment|/**      * Loads directly the atomic field data for the reader, ignoring any caching involved.      */
DECL|method|loadDirect
name|FD
name|loadDirect
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Comparator used for sorting.      */
DECL|method|comparatorSource
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|)
function_decl|;
comment|/**      * Clears any resources associated with this field data.      */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
comment|// we need this extended source we we have custom comparators to reuse our field data
comment|// in this case, we need to reduce type that will be used when search results are reduced
comment|// on another node (we don't have the custom source them...)
DECL|class|XFieldComparatorSource
specifier|public
specifier|abstract
class|class
name|XFieldComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|method|reducedType
specifier|public
specifier|abstract
name|SortField
operator|.
name|Type
name|reducedType
parameter_list|()
function_decl|;
block|}
DECL|interface|Builder
interface|interface
name|Builder
block|{
DECL|method|build
name|IndexFieldData
name|build
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|FieldDataType
name|type
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

