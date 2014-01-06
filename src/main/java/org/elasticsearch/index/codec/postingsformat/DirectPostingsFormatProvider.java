begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.codec.postingsformat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|postingsformat
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|memory
operator|.
name|DirectPostingsFormat
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

begin_comment
comment|/**  * A {@link PostingsFormatProvider} for {@link DirectPostingsFormat}. This  * postings format uses an on-disk storage for its terms and posting lists and  * streams its data during segment merges but loads its entire postings, terms  * and positions into memory for faster search performance. This format has a  * significant memory footprint and should be used with care.<b> This postings  * format offers the following parameters:  *<ul>  *<li><tt>min_skip_count</tt>: the minimum number terms with a shared prefix to  * allow a skip pointer to be written. the default is<tt>8</tt></li>  *   *<li><tt>low_freq_cutoff</tt>: terms with a lower document frequency use a  * single array object representation for postings and positions.</li>  *</ul>  *   * @see DirectPostingsFormat  *   */
end_comment

begin_class
DECL|class|DirectPostingsFormatProvider
specifier|public
class|class
name|DirectPostingsFormatProvider
extends|extends
name|AbstractPostingsFormatProvider
block|{
DECL|field|minSkipCount
specifier|private
specifier|final
name|int
name|minSkipCount
decl_stmt|;
DECL|field|lowFreqCutoff
specifier|private
specifier|final
name|int
name|lowFreqCutoff
decl_stmt|;
DECL|field|postingsFormat
specifier|private
specifier|final
name|DirectPostingsFormat
name|postingsFormat
decl_stmt|;
annotation|@
name|Inject
DECL|method|DirectPostingsFormatProvider
specifier|public
name|DirectPostingsFormatProvider
parameter_list|(
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|postingsFormatSettings
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|minSkipCount
operator|=
name|postingsFormatSettings
operator|.
name|getAsInt
argument_list|(
literal|"min_skip_count"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
comment|// See DirectPostingsFormat#DEFAULT_MIN_SKIP_COUNT
name|this
operator|.
name|lowFreqCutoff
operator|=
name|postingsFormatSettings
operator|.
name|getAsInt
argument_list|(
literal|"low_freq_cutoff"
argument_list|,
literal|32
argument_list|)
expr_stmt|;
comment|// See DirectPostingsFormat#DEFAULT_LOW_FREQ_CUTOFF
name|this
operator|.
name|postingsFormat
operator|=
operator|new
name|DirectPostingsFormat
argument_list|(
name|minSkipCount
argument_list|,
name|lowFreqCutoff
argument_list|)
expr_stmt|;
block|}
DECL|method|minSkipCount
specifier|public
name|int
name|minSkipCount
parameter_list|()
block|{
return|return
name|minSkipCount
return|;
block|}
DECL|method|lowFreqCutoff
specifier|public
name|int
name|lowFreqCutoff
parameter_list|()
block|{
return|return
name|lowFreqCutoff
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|PostingsFormat
name|get
parameter_list|()
block|{
return|return
name|postingsFormat
return|;
block|}
block|}
end_class

end_unit

