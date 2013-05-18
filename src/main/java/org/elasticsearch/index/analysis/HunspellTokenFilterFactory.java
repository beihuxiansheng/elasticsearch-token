begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
operator|.
name|HunspellDictionary
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
name|hunspell
operator|.
name|HunspellStemFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|inject
operator|.
name|Inject
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
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|index
operator|.
name|Index
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
name|settings
operator|.
name|IndexSettings
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
name|HunspellService
import|;
end_import

begin_class
annotation|@
name|AnalysisSettingsRequired
DECL|class|HunspellTokenFilterFactory
specifier|public
class|class
name|HunspellTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|dictionary
specifier|private
specifier|final
name|HunspellDictionary
name|dictionary
decl_stmt|;
DECL|field|dedup
specifier|private
specifier|final
name|boolean
name|dedup
decl_stmt|;
annotation|@
name|Inject
DECL|method|HunspellTokenFilterFactory
specifier|public
name|HunspellTokenFilterFactory
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|settings
parameter_list|,
name|HunspellService
name|hunspellService
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|String
name|locale
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"locale"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"language"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"lang"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|locale
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"missing [locale | language | lang] configuration for hunspell token filter"
argument_list|)
throw|;
block|}
name|dictionary
operator|=
name|hunspellService
operator|.
name|getDictionary
argument_list|(
name|locale
argument_list|)
expr_stmt|;
if|if
condition|(
name|dictionary
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Unknown hunspell dictionary for locale [%s]"
argument_list|,
name|locale
argument_list|)
argument_list|)
throw|;
block|}
name|dedup
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"dedup"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|HunspellStemFilter
argument_list|(
name|tokenStream
argument_list|,
name|dictionary
argument_list|,
name|dedup
argument_list|)
return|;
block|}
DECL|method|dedup
specifier|public
name|boolean
name|dedup
parameter_list|()
block|{
return|return
name|dedup
return|;
block|}
block|}
end_class

end_unit

