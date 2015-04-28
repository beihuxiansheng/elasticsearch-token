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
name|pattern
operator|.
name|PatternReplaceCharFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
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
name|Strings
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
annotation|@
name|AnalysisSettingsRequired
DECL|class|PatternReplaceCharFilterFactory
specifier|public
class|class
name|PatternReplaceCharFilterFactory
extends|extends
name|AbstractCharFilterFactory
block|{
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|replacement
specifier|private
specifier|final
name|String
name|replacement
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatternReplaceCharFilterFactory
specifier|public
name|PatternReplaceCharFilterFactory
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
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasLength
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"pattern"
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"pattern is missing for ["
operator|+
name|name
operator|+
literal|"] char filter of type 'pattern_replace'"
argument_list|)
throw|;
block|}
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"pattern"
argument_list|)
argument_list|)
expr_stmt|;
name|replacement
operator|=
name|settings
operator|.
name|get
argument_list|(
literal|"replacement"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// when not set or set to "", use "".
block|}
DECL|method|getPattern
specifier|public
name|Pattern
name|getPattern
parameter_list|()
block|{
return|return
name|pattern
return|;
block|}
DECL|method|getReplacement
specifier|public
name|String
name|getReplacement
parameter_list|()
block|{
return|return
name|replacement
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Reader
name|create
parameter_list|(
name|Reader
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|,
name|replacement
argument_list|,
name|tokenStream
argument_list|)
return|;
block|}
block|}
end_class

end_unit

