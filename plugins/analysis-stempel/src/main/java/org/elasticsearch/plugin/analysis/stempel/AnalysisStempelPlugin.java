begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.analysis.stempel
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|analysis
operator|.
name|stempel
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
name|index
operator|.
name|analysis
operator|.
name|AnalyzerProvider
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
name|analysis
operator|.
name|TokenFilterFactory
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
name|analysis
operator|.
name|pl
operator|.
name|PolishAnalyzerProvider
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
name|analysis
operator|.
name|pl
operator|.
name|PolishStemTokenFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|AnalysisModule
operator|.
name|AnalysisProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|AnalysisPlugin
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_class
DECL|class|AnalysisStempelPlugin
specifier|public
class|class
name|AnalysisStempelPlugin
extends|extends
name|Plugin
implements|implements
name|AnalysisPlugin
block|{
annotation|@
name|Override
DECL|method|getTokenFilters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|>
name|getTokenFilters
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"polish_stem"
argument_list|,
name|PolishStemTokenFilterFactory
operator|::
operator|new
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAnalyzers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|AnalyzerProvider
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
argument_list|>
argument_list|>
name|getAnalyzers
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"polish"
argument_list|,
name|PolishAnalyzerProvider
operator|::
operator|new
argument_list|)
return|;
block|}
block|}
end_class

end_unit

