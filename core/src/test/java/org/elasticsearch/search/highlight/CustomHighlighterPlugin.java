begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|SearchModule
import|;
end_import

begin_class
DECL|class|CustomHighlighterPlugin
specifier|public
class|class
name|CustomHighlighterPlugin
extends|extends
name|Plugin
block|{
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|SearchModule
name|highlightModule
parameter_list|)
block|{
name|highlightModule
operator|.
name|registerHighlighter
argument_list|(
literal|"test-custom"
argument_list|,
operator|new
name|CustomHighlighter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

