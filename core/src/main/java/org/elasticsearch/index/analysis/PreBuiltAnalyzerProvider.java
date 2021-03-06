begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_class
DECL|class|PreBuiltAnalyzerProvider
specifier|public
class|class
name|PreBuiltAnalyzerProvider
implements|implements
name|AnalyzerProvider
argument_list|<
name|NamedAnalyzer
argument_list|>
block|{
DECL|field|analyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|analyzer
decl_stmt|;
DECL|method|PreBuiltAnalyzerProvider
specifier|public
name|PreBuiltAnalyzerProvider
parameter_list|(
name|String
name|name
parameter_list|,
name|AnalyzerScope
name|scope
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
comment|// we create the named analyzer here so the resources associated with it will be shared
comment|// and we won't wrap a shared analyzer with named analyzer each time causing the resources
comment|// to not be shared...
name|this
operator|.
name|analyzer
operator|=
operator|new
name|NamedAnalyzer
argument_list|(
name|name
argument_list|,
name|scope
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|analyzer
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scope
specifier|public
name|AnalyzerScope
name|scope
parameter_list|()
block|{
return|return
name|analyzer
operator|.
name|scope
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|NamedAnalyzer
name|get
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
block|}
end_class

end_unit

