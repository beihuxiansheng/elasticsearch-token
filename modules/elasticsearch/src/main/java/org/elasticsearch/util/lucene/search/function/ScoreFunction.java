begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.lucene.search.function
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|ScoreFunction
specifier|public
interface|interface
name|ScoreFunction
block|{
DECL|method|setNextReader
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
function_decl|;
DECL|method|score
name|float
name|score
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|subQueryScore
parameter_list|)
function_decl|;
DECL|method|explain
name|Explanation
name|explain
parameter_list|(
name|int
name|docId
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

