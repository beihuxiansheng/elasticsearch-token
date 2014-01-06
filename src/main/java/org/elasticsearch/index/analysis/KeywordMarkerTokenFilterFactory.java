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
name|java
operator|.
name|util
operator|.
name|Set
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
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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

begin_class
annotation|@
name|AnalysisSettingsRequired
DECL|class|KeywordMarkerTokenFilterFactory
specifier|public
class|class
name|KeywordMarkerTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|keywordLookup
specifier|private
specifier|final
name|CharArraySet
name|keywordLookup
decl_stmt|;
annotation|@
name|Inject
DECL|method|KeywordMarkerTokenFilterFactory
specifier|public
name|KeywordMarkerTokenFilterFactory
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Environment
name|env
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
name|boolean
name|ignoreCase
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"ignore_case"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|rules
init|=
name|Analysis
operator|.
name|getWordSet
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"keywords"
argument_list|,
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|rules
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"keyword filter requires either `keywords` or `keywords_path` to be configured"
argument_list|)
throw|;
block|}
name|keywordLookup
operator|=
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|rules
argument_list|,
name|ignoreCase
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
name|SetKeywordMarkerFilter
argument_list|(
name|tokenStream
argument_list|,
name|keywordLookup
argument_list|)
return|;
block|}
block|}
end_class

end_unit

