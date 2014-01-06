begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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

begin_interface
DECL|interface|PayloadProcessor
interface|interface
name|PayloadProcessor
block|{
DECL|method|buildPayload
name|BytesRef
name|buildPayload
parameter_list|(
name|BytesRef
name|surfaceForm
parameter_list|,
name|long
name|weight
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|parsePayload
name|void
name|parsePayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|,
name|SuggestPayload
name|ref
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|class|SuggestPayload
specifier|static
class|class
name|SuggestPayload
block|{
DECL|field|payload
specifier|final
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|weight
name|long
name|weight
init|=
literal|0
decl_stmt|;
DECL|field|surfaceForm
specifier|final
name|BytesRef
name|surfaceForm
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
block|}
block|}
end_interface

end_unit

