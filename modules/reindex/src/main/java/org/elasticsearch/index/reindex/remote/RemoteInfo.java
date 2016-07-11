begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex.remote
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
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
name|Nullable
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
name|bytes
operator|.
name|BytesReference
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
name|Writeable
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_class
DECL|class|RemoteInfo
specifier|public
class|class
name|RemoteInfo
implements|implements
name|Writeable
block|{
DECL|field|scheme
specifier|private
specifier|final
name|String
name|scheme
decl_stmt|;
DECL|field|host
specifier|private
specifier|final
name|String
name|host
decl_stmt|;
DECL|field|port
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|BytesReference
name|query
decl_stmt|;
DECL|field|username
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
DECL|field|password
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
DECL|method|RemoteInfo
specifier|public
name|RemoteInfo
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|BytesReference
name|query
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|scheme
operator|=
name|requireNonNull
argument_list|(
name|scheme
argument_list|,
literal|"[scheme] must be specified to reindex from a remote cluster"
argument_list|)
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|requireNonNull
argument_list|(
name|host
argument_list|,
literal|"[host] must be specified to reindex from a remote cluster"
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|requireNonNull
argument_list|(
name|query
argument_list|,
literal|"[query] must be specified to reindex from a remote cluster"
argument_list|)
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|RemoteInfo
specifier|public
name|RemoteInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|scheme
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|host
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|port
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|query
operator|=
name|in
operator|.
name|readBytesReference
argument_list|()
expr_stmt|;
name|username
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|password
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
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
name|writeString
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
DECL|method|getScheme
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|scheme
return|;
block|}
DECL|method|getHost
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
DECL|method|getPort
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
DECL|method|getQuery
specifier|public
name|BytesReference
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
annotation|@
name|Nullable
DECL|method|getUsername
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
annotation|@
name|Nullable
DECL|method|getPassword
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
literal|false
operator|==
literal|"http"
operator|.
name|equals
argument_list|(
name|scheme
argument_list|)
condition|)
block|{
comment|// http is the default so it isn't worth taking up space if it is the scheme
name|b
operator|.
name|append
argument_list|(
literal|"scheme="
argument_list|)
operator|.
name|append
argument_list|(
name|scheme
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"host="
argument_list|)
operator|.
name|append
argument_list|(
name|host
argument_list|)
operator|.
name|append
argument_list|(
literal|" port="
argument_list|)
operator|.
name|append
argument_list|(
name|port
argument_list|)
operator|.
name|append
argument_list|(
literal|" query="
argument_list|)
operator|.
name|append
argument_list|(
name|query
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" username="
argument_list|)
operator|.
name|append
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" password=<<>>"
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

