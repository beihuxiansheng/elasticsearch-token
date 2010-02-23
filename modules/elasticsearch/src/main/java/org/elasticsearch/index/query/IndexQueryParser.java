begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|IndexQueryParser
specifier|public
interface|interface
name|IndexQueryParser
extends|extends
name|IndexComponent
block|{
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
DECL|method|parse
name|Query
name|parse
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|parse
name|Query
name|parse
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|parse
name|Query
name|parse
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
block|}
end_interface

end_unit

