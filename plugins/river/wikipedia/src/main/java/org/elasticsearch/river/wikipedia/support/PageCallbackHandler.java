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

begin_comment
comment|/**  * Interface to allow streamed processing of pages.  * This allows a SAX style processing of Wikipedia XML files.  * The registered callback is executed on each page  * element in the XML file.  *<p>  * Using callbacks will consume lesser memory, an useful feature for large  * dumps like English and German.  *  * @author Delip Rao  * @see WikiXMLDOMParser  * @see WikiPage  */
end_comment

begin_interface
DECL|interface|PageCallbackHandler
specifier|public
interface|interface
name|PageCallbackHandler
block|{
comment|/**      * This is the callback method that should be implemented before      * registering with<code>WikiXMLDOMParser</code>      *      * @param page a wikipedia page object      * @see WikiPage      */
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|WikiPage
name|page
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

