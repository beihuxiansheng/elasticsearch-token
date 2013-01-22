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

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|IndexOrdinalFieldData
specifier|public
interface|interface
name|IndexOrdinalFieldData
parameter_list|<
name|FD
extends|extends
name|AtomicOrdinalFieldData
parameter_list|>
extends|extends
name|IndexFieldData
argument_list|<
name|FD
argument_list|>
block|{
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
block|}
end_interface

end_unit

