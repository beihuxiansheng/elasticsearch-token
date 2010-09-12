begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|util
operator|.
name|UnicodeUtil
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
name|thread
operator|.
name|ThreadLocals
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
name|xcontent
operator|.
name|XContentBuilder
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
DECL|class|XContentRestResponse
specifier|public
class|class
name|XContentRestResponse
extends|extends
name|AbstractRestResponse
block|{
DECL|field|END_JSONP
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|END_JSONP
decl_stmt|;
static|static
block|{
name|UnicodeUtil
operator|.
name|UTF8Result
name|U_END_JSONP
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
literal|");"
argument_list|,
literal|0
argument_list|,
literal|");"
operator|.
name|length
argument_list|()
argument_list|,
name|U_END_JSONP
argument_list|)
expr_stmt|;
name|END_JSONP
operator|=
operator|new
name|byte
index|[
name|U_END_JSONP
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|U_END_JSONP
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|END_JSONP
argument_list|,
literal|0
argument_list|,
name|U_END_JSONP
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|field|prefixCache
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|>
argument_list|>
name|prefixCache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|>
argument_list|(
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|prefixUtf8Result
specifier|private
specifier|final
name|UnicodeUtil
operator|.
name|UTF8Result
name|prefixUtf8Result
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|Status
name|status
decl_stmt|;
DECL|field|builder
specifier|private
specifier|final
name|XContentBuilder
name|builder
decl_stmt|;
DECL|method|XContentRestResponse
specifier|public
name|XContentRestResponse
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|Status
name|status
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|prefixUtf8Result
operator|=
name|startJsonp
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|XContentRestResponse
specifier|public
name|XContentRestResponse
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|Status
name|status
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|prefixUtf8Result
operator|=
name|startJsonp
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|contentType
annotation|@
name|Override
specifier|public
name|String
name|contentType
parameter_list|()
block|{
return|return
literal|"application/json; charset=UTF-8"
return|;
block|}
DECL|method|contentThreadSafe
annotation|@
name|Override
specifier|public
name|boolean
name|contentThreadSafe
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|content
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|content
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|unsafeBytes
argument_list|()
return|;
block|}
DECL|method|contentLength
annotation|@
name|Override
specifier|public
name|int
name|contentLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|unsafeBytesLength
argument_list|()
return|;
block|}
DECL|method|status
annotation|@
name|Override
specifier|public
name|Status
name|status
parameter_list|()
block|{
return|return
name|this
operator|.
name|status
return|;
block|}
DECL|method|prefixContent
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|prefixContent
parameter_list|()
block|{
if|if
condition|(
name|prefixUtf8Result
operator|!=
literal|null
condition|)
block|{
return|return
name|prefixUtf8Result
operator|.
name|result
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|prefixContentLength
annotation|@
name|Override
specifier|public
name|int
name|prefixContentLength
parameter_list|()
block|{
if|if
condition|(
name|prefixUtf8Result
operator|!=
literal|null
condition|)
block|{
return|return
name|prefixUtf8Result
operator|.
name|length
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|suffixContent
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|suffixContent
parameter_list|()
block|{
if|if
condition|(
name|prefixUtf8Result
operator|!=
literal|null
condition|)
block|{
return|return
name|END_JSONP
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|suffixContentLength
annotation|@
name|Override
specifier|public
name|int
name|suffixContentLength
parameter_list|()
block|{
if|if
condition|(
name|prefixUtf8Result
operator|!=
literal|null
condition|)
block|{
return|return
name|END_JSONP
operator|.
name|length
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|startJsonp
specifier|private
specifier|static
name|UnicodeUtil
operator|.
name|UTF8Result
name|startJsonp
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|String
name|callback
init|=
name|request
operator|.
name|param
argument_list|(
literal|"callback"
argument_list|)
decl_stmt|;
if|if
condition|(
name|callback
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|UnicodeUtil
operator|.
name|UTF8Result
name|result
init|=
name|prefixCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|callback
argument_list|,
literal|0
argument_list|,
name|callback
operator|.
name|length
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|.
name|result
index|[
name|result
operator|.
name|length
index|]
operator|=
literal|'('
expr_stmt|;
name|result
operator|.
name|length
operator|++
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

