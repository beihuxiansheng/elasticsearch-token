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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|AbstractPostingsFormatProvider
specifier|public
specifier|abstract
class|class
name|AbstractPostingsFormatProvider
implements|implements
name|PostingsFormatProvider
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|AbstractPostingsFormatProvider
specifier|protected
name|AbstractPostingsFormatProvider
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

