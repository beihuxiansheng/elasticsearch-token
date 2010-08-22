begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|StoreFileMetaData
specifier|public
class|class
name|StoreFileMetaData
implements|implements
name|Streamable
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|lastModified
specifier|private
name|long
name|lastModified
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|method|StoreFileMetaData
name|StoreFileMetaData
parameter_list|()
block|{     }
DECL|method|StoreFileMetaData
specifier|public
name|StoreFileMetaData
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
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
DECL|method|lastModified
specifier|public
name|long
name|lastModified
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastModified
return|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|readStoreFileMetaData
specifier|public
specifier|static
name|StoreFileMetaData
name|readStoreFileMetaData
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|StoreFileMetaData
name|md
init|=
operator|new
name|StoreFileMetaData
argument_list|()
decl_stmt|;
name|md
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|md
return|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"name ["
operator|+
name|name
operator|+
literal|"], length ["
operator|+
name|length
operator|+
literal|"]"
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|name
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|length
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

