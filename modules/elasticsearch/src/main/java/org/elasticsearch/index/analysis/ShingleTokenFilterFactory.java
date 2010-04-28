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
name|shingle
operator|.
name|ShingleFilter
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
name|util
operator|.
name|guice
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
name|util
operator|.
name|guice
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
name|util
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|ShingleTokenFilterFactory
specifier|public
class|class
name|ShingleTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|maxShingleSize
specifier|private
specifier|final
name|int
name|maxShingleSize
decl_stmt|;
DECL|field|outputUnigrams
specifier|private
specifier|final
name|boolean
name|outputUnigrams
decl_stmt|;
DECL|method|ShingleTokenFilterFactory
annotation|@
name|Inject
specifier|public
name|ShingleTokenFilterFactory
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
name|maxShingleSize
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"max_shingle_size"
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|)
expr_stmt|;
name|outputUnigrams
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"output_unigrams"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|create
annotation|@
name|Override
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
name|ShingleFilter
name|filter
init|=
operator|new
name|ShingleFilter
argument_list|(
name|tokenStream
argument_list|,
name|maxShingleSize
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setOutputUnigrams
argument_list|(
name|outputUnigrams
argument_list|)
expr_stmt|;
return|return
name|filter
return|;
block|}
block|}
end_class

end_unit

