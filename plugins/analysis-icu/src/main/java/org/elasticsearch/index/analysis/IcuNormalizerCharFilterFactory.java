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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Normalizer2
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
name|icu
operator|.
name|ICUNormalizer2CharFilter
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|IndexSettings
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

begin_comment
comment|/**  * Uses the {@link org.apache.lucene.analysis.icu.ICUNormalizer2CharFilter} to normalize character.  *<p>The<tt>name</tt> can be used to provide the type of normalization to perform.</p>  *<p>The<tt>mode</tt> can be used to provide 'compose' or 'decompose'. Default is compose.</p>  *<p>The<tt>unicodeSetFilter</tt> attribute can be used to provide the UniCodeSet for filtering.</p>  */
end_comment

begin_class
DECL|class|IcuNormalizerCharFilterFactory
specifier|public
class|class
name|IcuNormalizerCharFilterFactory
extends|extends
name|AbstractCharFilterFactory
implements|implements
name|MultiTermAwareComponent
block|{
DECL|field|normalizer
specifier|private
specifier|final
name|Normalizer2
name|normalizer
decl_stmt|;
DECL|method|IcuNormalizerCharFilterFactory
specifier|public
name|IcuNormalizerCharFilterFactory
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|Environment
name|environment
parameter_list|,
name|String
name|name
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|String
name|method
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|,
literal|"nfkc_cf"
argument_list|)
decl_stmt|;
name|String
name|mode
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
literal|"compose"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
operator|&&
operator|!
literal|"decompose"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|mode
operator|=
literal|"compose"
expr_stmt|;
block|}
name|Normalizer2
name|normalizer
init|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
name|method
argument_list|,
literal|"compose"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|?
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
else|:
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
decl_stmt|;
name|this
operator|.
name|normalizer
operator|=
name|IcuNormalizerTokenFilterFactory
operator|.
name|wrapWithUnicodeSetFilter
argument_list|(
name|normalizer
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Reader
name|create
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
name|reader
argument_list|,
name|normalizer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMultiTermComponent
specifier|public
name|Object
name|getMultiTermComponent
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

