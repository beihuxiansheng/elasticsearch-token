begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
package|;
end_package

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
name|search
operator|.
name|internal
operator|.
name|SearchContext
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|FetchPhaseExecutionException
specifier|public
class|class
name|FetchPhaseExecutionException
extends|extends
name|ElasticSearchException
block|{
DECL|method|FetchPhaseExecutionException
specifier|public
name|FetchPhaseExecutionException
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchPhaseExecutionException
specifier|public
name|FetchPhaseExecutionException
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|super
argument_list|(
literal|"Failed to fetch query ["
operator|+
name|context
operator|.
name|query
argument_list|()
operator|+
literal|"], sort ["
operator|+
name|context
operator|.
name|sort
argument_list|()
operator|+
literal|"], from ["
operator|+
name|context
operator|.
name|from
argument_list|()
operator|+
literal|"], size ["
operator|+
name|context
operator|.
name|size
argument_list|()
operator|+
literal|"], reason ["
operator|+
name|msg
operator|+
literal|"]"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

