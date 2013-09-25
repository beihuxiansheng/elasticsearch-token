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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|CustomAnalyzerWrapper
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
name|util
operator|.
name|Version
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
name|lucene
operator|.
name|Lucene
import|;
end_import

begin_comment
comment|/**  * Named analyzer is an analyzer wrapper around an actual analyzer ({@link #analyzer} that is associated  * with a name ({@link #name()}.  */
end_comment

begin_class
DECL|class|NamedAnalyzer
specifier|public
class|class
name|NamedAnalyzer
extends|extends
name|CustomAnalyzerWrapper
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|AnalyzerScope
name|scope
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|positionOffsetGap
specifier|private
specifier|final
name|int
name|positionOffsetGap
decl_stmt|;
DECL|method|NamedAnalyzer
specifier|public
name|NamedAnalyzer
parameter_list|(
name|NamedAnalyzer
name|analyzer
parameter_list|,
name|int
name|positionOffsetGap
parameter_list|)
block|{
name|this
argument_list|(
name|analyzer
operator|.
name|name
argument_list|()
argument_list|,
name|analyzer
operator|.
name|scope
argument_list|()
argument_list|,
name|analyzer
operator|.
name|analyzer
argument_list|()
argument_list|,
name|positionOffsetGap
argument_list|)
expr_stmt|;
block|}
DECL|method|NamedAnalyzer
specifier|public
name|NamedAnalyzer
parameter_list|(
name|String
name|name
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
DECL|method|NamedAnalyzer
specifier|public
name|NamedAnalyzer
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
name|this
argument_list|(
name|name
argument_list|,
name|scope
argument_list|,
name|analyzer
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
static|static
block|{
comment|// LUCENE MONITOR: this should be in Lucene 4.5.
assert|assert
name|Lucene
operator|.
name|VERSION
operator|==
name|Version
operator|.
name|LUCENE_44
operator|:
literal|"when upgrading to 4.5, we should use call analyzer#getReuseStrategy(), see https://issues.apache.org/jira/browse/LUCENE-5170"
assert|;
block|}
DECL|method|NamedAnalyzer
specifier|public
name|NamedAnalyzer
parameter_list|(
name|String
name|name
parameter_list|,
name|AnalyzerScope
name|scope
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|int
name|positionOffsetGap
parameter_list|)
block|{
comment|// our named analyzer always wrap a non per field analyzer, so no need to have per field analyzer
name|super
argument_list|(
operator|new
name|GlobalReuseStrategy
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|positionOffsetGap
operator|=
name|positionOffsetGap
expr_stmt|;
block|}
comment|/**      * The name of the analyzer.      */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/**      * The scope of the analyzer.      */
DECL|method|scope
specifier|public
name|AnalyzerScope
name|scope
parameter_list|()
block|{
return|return
name|this
operator|.
name|scope
return|;
block|}
comment|/**      * The actual analyzer.      */
DECL|method|analyzer
specifier|public
name|Analyzer
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
annotation|@
name|Override
DECL|method|getWrappedAnalyzer
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
annotation|@
name|Override
DECL|method|wrapComponents
specifier|protected
name|TokenStreamComponents
name|wrapComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
return|return
name|components
return|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|positionOffsetGap
operator|!=
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
return|return
name|positionOffsetGap
return|;
block|}
return|return
name|super
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"analyzer name["
operator|+
name|name
operator|+
literal|"], analyzer ["
operator|+
name|analyzer
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

