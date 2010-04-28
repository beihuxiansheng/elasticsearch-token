begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|ImmutableMap
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|FieldNameAnalyzer
specifier|public
class|class
name|FieldNameAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|analyzers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzers
decl_stmt|;
DECL|field|defaultAnalyzer
specifier|private
specifier|final
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
DECL|method|FieldNameAnalyzer
specifier|public
name|FieldNameAnalyzer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzers
parameter_list|,
name|Analyzer
name|defaultAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|analyzers
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultAnalyzer
operator|=
name|defaultAnalyzer
expr_stmt|;
block|}
DECL|method|tokenStream
annotation|@
name|Override
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|getAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|method|reusableTokenStream
annotation|@
name|Override
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|reusableTokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|method|getPositionIncrementGap
annotation|@
name|Override
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|getAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|getAnalyzer
specifier|private
name|Analyzer
name|getAnalyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Analyzer
name|analyzer
init|=
name|analyzers
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
return|return
name|analyzer
return|;
block|}
return|return
name|defaultAnalyzer
return|;
block|}
block|}
end_class

end_unit

