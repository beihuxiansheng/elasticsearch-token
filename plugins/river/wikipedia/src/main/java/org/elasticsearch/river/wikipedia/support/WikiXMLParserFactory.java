begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.river.wikipedia.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|wikipedia
operator|.
name|support
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * @author Delip Rao  */
end_comment

begin_class
DECL|class|WikiXMLParserFactory
specifier|public
class|class
name|WikiXMLParserFactory
block|{
DECL|method|getSAXParser
specifier|public
specifier|static
name|WikiXMLParser
name|getSAXParser
parameter_list|(
name|URL
name|fileName
parameter_list|)
block|{
return|return
operator|new
name|WikiXMLSAXParser
argument_list|(
name|fileName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

