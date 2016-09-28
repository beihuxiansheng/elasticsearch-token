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
name|elasticsearch
operator|.
name|AnalysisFactoryTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
DECL|class|AnalysisICUFactoryTests
specifier|public
class|class
name|AnalysisICUFactoryTests
extends|extends
name|AnalysisFactoryTestCase
block|{
annotation|@
name|Override
DECL|method|getTokenizers
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTokenizers
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokenizers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getTokenizers
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizers
operator|.
name|put
argument_list|(
literal|"icu"
argument_list|,
name|IcuTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|tokenizers
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTokenFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getTokenFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"icufolding"
argument_list|,
name|IcuFoldingTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"icunormalizer2"
argument_list|,
name|IcuNormalizerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"icutransform"
argument_list|,
name|IcuTransformTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
annotation|@
name|Override
DECL|method|getCharFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getCharFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getCharFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"icunormalizer2"
argument_list|,
name|IcuNormalizerCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
block|}
end_class

end_unit
