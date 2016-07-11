begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|xcontent
operator|.
name|ToXContent
import|;
end_import

begin_comment
comment|/**  * The hits of a search request.  *  *  */
end_comment

begin_interface
DECL|interface|SearchHits
specifier|public
interface|interface
name|SearchHits
extends|extends
name|Streamable
extends|,
name|ToXContent
extends|,
name|Iterable
argument_list|<
name|SearchHit
argument_list|>
block|{
comment|/**      * The total number of hits that matches the search request.      */
DECL|method|totalHits
name|long
name|totalHits
parameter_list|()
function_decl|;
comment|/**      * The total number of hits that matches the search request.      */
DECL|method|getTotalHits
name|long
name|getTotalHits
parameter_list|()
function_decl|;
comment|/**      * The maximum score of this query.      */
DECL|method|maxScore
name|float
name|maxScore
parameter_list|()
function_decl|;
comment|/**      * The maximum score of this query.      */
DECL|method|getMaxScore
name|float
name|getMaxScore
parameter_list|()
function_decl|;
comment|/**      * The hits of the search request (based on the search type, and from / size provided).      */
DECL|method|hits
name|SearchHit
index|[]
name|hits
parameter_list|()
function_decl|;
comment|/**      * Return the hit as the provided position.      */
DECL|method|getAt
name|SearchHit
name|getAt
parameter_list|(
name|int
name|position
parameter_list|)
function_decl|;
comment|/**      * The hits of the search request (based on the search type, and from / size provided).      */
DECL|method|getHits
name|SearchHit
index|[]
name|getHits
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

