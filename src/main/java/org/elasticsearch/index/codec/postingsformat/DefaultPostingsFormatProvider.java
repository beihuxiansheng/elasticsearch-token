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
name|blocktree
operator|.
name|BlockTreeTermsWriter
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
name|lucene41
operator|.
name|Lucene41PostingsFormat
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
comment|/**  * The default postingsformat, maps to {@link Lucene41PostingsFormat}.  *<ul>  *<li><tt>min_block_size</tt>: the minimum block size the default Lucene term  * dictionary uses to encode on-disk blocks.</li>  *   *<li><tt>max_block_size</tt>: the maximum block size the default Lucene term  * dictionary uses to encode on-disk blocks.</li>  *</ul>  */
end_comment

begin_comment
comment|// LUCENE UPGRADE: Check if type of field postingsFormat needs to be updated!
end_comment

begin_class
DECL|class|DefaultPostingsFormatProvider
specifier|public
class|class
name|DefaultPostingsFormatProvider
extends|extends
name|AbstractPostingsFormatProvider
block|{
DECL|field|minBlockSize
specifier|private
specifier|final
name|int
name|minBlockSize
decl_stmt|;
DECL|field|maxBlockSize
specifier|private
specifier|final
name|int
name|maxBlockSize
decl_stmt|;
DECL|field|postingsFormat
specifier|private
specifier|final
name|Lucene41PostingsFormat
name|postingsFormat
decl_stmt|;
annotation|@
name|Inject
DECL|method|DefaultPostingsFormatProvider
specifier|public
name|DefaultPostingsFormatProvider
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
name|minBlockSize
operator|=
name|postingsFormatSettings
operator|.
name|getAsInt
argument_list|(
literal|"min_block_size"
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxBlockSize
operator|=
name|postingsFormatSettings
operator|.
name|getAsInt
argument_list|(
literal|"max_block_size"
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|postingsFormat
operator|=
operator|new
name|Lucene41PostingsFormat
argument_list|(
name|minBlockSize
argument_list|,
name|maxBlockSize
argument_list|)
expr_stmt|;
block|}
DECL|method|minBlockSize
specifier|public
name|int
name|minBlockSize
parameter_list|()
block|{
return|return
name|minBlockSize
return|;
block|}
DECL|method|maxBlockSize
specifier|public
name|int
name|maxBlockSize
parameter_list|()
block|{
return|return
name|maxBlockSize
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

