begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.id
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|id
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
name|AtomicReader
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|service
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|IdCache
specifier|public
interface|interface
name|IdCache
extends|extends
name|IndexComponent
extends|,
name|CloseableComponent
extends|,
name|Iterable
argument_list|<
name|IdReaderCache
argument_list|>
block|{
comment|// we need to "inject" the index service to not create cyclic dep
DECL|method|setIndexService
name|void
name|setIndexService
parameter_list|(
name|IndexService
name|indexService
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
DECL|method|refresh
name|void
name|refresh
parameter_list|(
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|readers
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|reader
name|IdReaderCache
name|reader
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

