begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|pulsing
operator|.
name|Pulsing40PostingsFormat
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
comment|/**  */
end_comment

begin_class
DECL|class|Pulsing40PostingsFormatProvider
specifier|public
class|class
name|Pulsing40PostingsFormatProvider
extends|extends
name|AbstractPostingsFormatProvider
block|{
DECL|field|freqCutOff
specifier|private
specifier|final
name|int
name|freqCutOff
decl_stmt|;
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
name|Pulsing40PostingsFormat
name|postingsFormat
decl_stmt|;
annotation|@
name|Inject
DECL|method|Pulsing40PostingsFormatProvider
specifier|public
name|Pulsing40PostingsFormatProvider
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
name|freqCutOff
operator|=
name|postingsFormatSettings
operator|.
name|getAsInt
argument_list|(
literal|"freq_cut_off"
argument_list|,
literal|1
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
name|Pulsing40PostingsFormat
argument_list|(
name|freqCutOff
argument_list|,
name|minBlockSize
argument_list|,
name|maxBlockSize
argument_list|)
expr_stmt|;
block|}
DECL|method|freqCutOff
specifier|public
name|int
name|freqCutOff
parameter_list|()
block|{
return|return
name|freqCutOff
return|;
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

