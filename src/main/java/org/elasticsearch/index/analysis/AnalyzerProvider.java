begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
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
name|analysis
operator|.
name|Analyzer
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
name|inject
operator|.
name|Provider
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|AnalyzerProvider
specifier|public
interface|interface
name|AnalyzerProvider
parameter_list|<
name|T
extends|extends
name|Analyzer
parameter_list|>
extends|extends
name|Provider
argument_list|<
name|T
argument_list|>
block|{
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
DECL|method|scope
name|AnalyzerScope
name|scope
parameter_list|()
function_decl|;
DECL|method|get
name|T
name|get
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

