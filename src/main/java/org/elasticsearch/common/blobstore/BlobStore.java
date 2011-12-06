begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.common.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|BlobStore
specifier|public
interface|interface
name|BlobStore
block|{
DECL|method|immutableBlobContainer
name|ImmutableBlobContainer
name|immutableBlobContainer
parameter_list|(
name|BlobPath
name|path
parameter_list|)
function_decl|;
DECL|method|delete
name|void
name|delete
parameter_list|(
name|BlobPath
name|path
parameter_list|)
function_decl|;
DECL|method|close
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

