begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.bloom
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|bloom
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
name|IndexReader
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
name|bloom
operator|.
name|BloomFilter
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
name|component
operator|.
name|CloseableComponent
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|BloomCache
specifier|public
interface|interface
name|BloomCache
extends|extends
name|IndexComponent
extends|,
name|CloseableComponent
block|{
comment|/**      * *Async* loads a bloom filter for the field name. Note, this one only supports      * for fields that have a single term per doc.      */
DECL|method|filter
name|BloomFilter
name|filter
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|asyncLoad
parameter_list|)
function_decl|;
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
DECL|method|clear
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
function_decl|;
DECL|method|sizeInBytes
name|long
name|sizeInBytes
parameter_list|()
function_decl|;
DECL|method|sizeInBytes
name|long
name|sizeInBytes
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

